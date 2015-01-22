package org.shunya.server;

import org.shunya.server.model.Task;
import org.shunya.server.model.TaskRun;
import org.shunya.server.services.DBService;
import org.shunya.server.services.TaskService;

import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;

public class ScheduledTaskRunner implements Runnable{
    private static final Logger logger = Logger.getLogger(ScheduledTaskRunner.class.getName());

    private final long taskId;
    private final DBService dbService;
    private final TaskService taskService;

    public ScheduledTaskRunner(long taskId, DBService dbService, TaskService taskService) {
        this.taskId = taskId;
        this.dbService = dbService;
        this.taskService = taskService;
    }

    @Override
    public void run() {
        logger.info("Executing scheduled Task - " + taskId);
        Task task = dbService.getTask(taskId);
        TaskRun taskRun = new TaskRun();
        taskRun.setTask(task);
        taskRun.setName(task.getName());
        taskRun.setStartTime(new Date());
        taskRun.setComments("Scheduled execution");
        taskRun.setNotifyStatus(false);
//        taskRun.setRunBy(dbService.findUserByUsername(principal.getName()));
        taskRun.setTeam(task.getTeam());
        taskService.execute(taskRun, new HashMap<>());
    }
}
