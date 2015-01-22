package org.shunya.server.engine;

import org.shunya.server.model.Task;
import org.shunya.server.model.TaskRun;
import org.shunya.server.model.Team;
import org.shunya.server.model.User;
import org.shunya.server.services.DBService;
import org.shunya.server.services.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class TelegramTaskRunner {
    private static final Logger logger = LoggerFactory.getLogger(TelegramTaskRunner.class);

    private long taskId;
    private long taskRunId;

    TelegramUserState inputState;
    TelegramUserState propertiesState;
    TelegramUserState confirmState;

    TelegramUserState state;

    final DBService dbService;
    final TaskService taskService;
    final PeerState peerState;
    final int fromId;
    int randomNumber;
    private Map<String, String> valuesToOverride;

    public TelegramTaskRunner(DBService dbService, TaskService taskService, PeerState peerState, int fromId) {
        this.dbService = dbService;
        this.taskService = taskService;
        this.peerState = peerState;
        this.fromId = fromId;

        inputState = new UserTaskSelectionState(this);
        propertiesState = new OverrideInputState(this);
        confirmState = new UserConfirmState(this);

        state = inputState;
    }

    public String process(String input) {
        return state.process(input);
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public String help() {
        User user = null;
        try {
            user = dbService.findUserByTelegramId(fromId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int teamChatId = peerState.getId();
        List<Team> teams = dbService.findTeamByChatId(teamChatId);
        if (teams == null || teams.isEmpty()) {
            return "No Team bound to this Chat Channel";
        } else {
            List<Task> availableTasks = new ArrayList<>();
            for (Team team : teams) {
                availableTasks.addAll(dbService.listTasksByTeam(team.getId()));
            }
            StringBuilder helpMessage = new StringBuilder();
            if (user != null) {
                helpMessage.append("hi " + user.getName() + ", ");
            }
            helpMessage.append("Type one bot command :\n");
            for (Task task : availableTasks) {
                long id = task.getId();
                String name = task.getName();
                helpMessage.append("bot " + id + " <" + name + ">\n");
            }
            return helpMessage.toString();
        }
    }

    public String execute() {
        try {
            String comments = "process run by ChatId - " + fromId;
            Task task = dbService.getTask(taskId);
            if (task.getTeam().getTelegramId() == peerState.getId()) {
                TaskRun taskRun = new TaskRun();
                taskRun.setTask(task);
                taskRun.setName(task.getName());
                taskRun.setStartTime(new Date());
                taskRun.setComments(comments);
                taskRun.setNotifyStatus(true);
                taskRun.setTeam(task.getTeam());
                try {
                    User user = dbService.findUserByTelegramId(fromId);
                    taskRun.setRunBy(user);
                    dbService.save(taskRun);
                    setTaskRunId(taskRun.getId());
                } catch (Exception e) {
                    logger.warn("exception getting user info", e);
                }
                logger.info("Properties to override for - " + valuesToOverride);
                taskService.execute(taskRun, valuesToOverride);
                return "Command Sent to Server - " + task.getName();
            } else {
                return "Chat Channel is not part of Team - " + task.getTeam().getName();
            }
        } catch (Exception e) {
            return "Unknown error - " + e.getMessage();
        }
    }

    public String abort() {
        if (getTaskRunId() != 0) {
            TaskRun taskRun = dbService.getTaskRun(getTaskRunId());
            if (taskService.cancelTaskRun(taskRun))
                return "Command submitted to cancel Task Execution";
        }
        return "There is no task running at the moment";
    }

    public void setState(TelegramUserState state) {
        this.state = state;
    }

    public TelegramUserState getInputState() {
        return inputState;
    }

    public TelegramUserState getPropertiesState() {
        return propertiesState;
    }

    public TelegramUserState getConfirmState() {
        return confirmState;
    }

    public int getRandomNumber() {
        return randomNumber;
    }

    public void setRandomNumber(int randomNumber) {
        this.randomNumber = randomNumber;
    }

    public String reset() {
        setState(getInputState());
        return "Trx has been reset";
    }

    public long getTaskRunId() {
        return taskRunId;
    }

    public void setTaskRunId(long taskRunId) {
        this.taskRunId = taskRunId;
    }

    public void setValuesToOverride(Map<String, String> valuesToOverride) {
        this.valuesToOverride = valuesToOverride;
    }

    public Map<String, String> getValuesToOverride() {
        return valuesToOverride;
    }
}
