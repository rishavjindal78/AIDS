package org.shunya.server.services;

import org.shunya.server.TaskExecutionPlan;
import org.shunya.server.TelegramStatusObserver;
import org.shunya.server.model.Agent;
import org.shunya.server.model.TaskRun;
import org.shunya.server.model.TaskStep;
import org.shunya.server.model.TaskStepRun;
import org.shunya.shared.FieldPropertiesMap;
import org.shunya.shared.TaskContext;
import org.shunya.shared.TaskStepDTO;
import org.shunya.shared.TaskStepRunDTO;
import org.shunya.shared.model.*;
import org.shunya.shared.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import javax.xml.bind.JAXBException;
import java.net.ConnectException;
import java.net.Inet4Address;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class TaskService {
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    Map<TaskRun, TaskExecutionPlan> taskExecutionPlanMap = new ConcurrentHashMap<>(100);
    Map<TaskRun, List<TaskStepRun>> currentlyRunningTaskSteps = new ConcurrentHashMap<>(100);

    @Autowired
    private RestClient restClient;

    @Autowired
    private DBService DBService;

    @Autowired
    @Qualifier("myExecutor")
    private TaskExecutor executor;

    @Autowired
    private TelegramStatusObserver statusObserver;

    // if task is not started at all - create execution plan and start it
    // if task has start but not completed - then execute rest of steps
    // if all task steps has completed, then do the cleanup
    @Async
    public void execute(TaskRun taskRun) {
        statusObserver.notifyStatus(taskRun.getTeam().getTelegramId(), taskRun.isNotifyStatus(), "starting execution for - " + taskRun.getName());
        logger.info("starting execution for TaskRun {}", taskRun.getName());
        DBService.save(taskRun);
        TaskExecutionPlan executionPlan = taskExecutionPlanMap.computeIfAbsent(taskRun, k -> new TaskExecutionPlan(taskRun.getTask()));
        Map.Entry<Integer, List<TaskStep>> next = executionPlan.next();
        if (next != null) {
            taskRun.setRunState(RunState.RUNNING);
            taskRun.setRunStatus(RunStatus.RUNNING);
            DBService.save(taskRun);
            delegateStepToAgents(next.getValue(), taskRun);
        } else {
            taskRun.setRunState(RunState.COMPLETED);
            taskRun.setFinishTime(new Date());
            taskRun.setRunStatus(RunStatus.NOT_RUN);
            DBService.save(taskRun);
            taskExecutionPlanMap.remove(taskRun);
            logger.info("Task has no steps, completing it now");
        }
    }

    @Async
    public void consumeStepResult(TaskContext taskContext) {
        logger.info("Execution Completed for Step - " + taskContext.getStepDTO().getSequence() + ", Status = " + taskContext.getTaskStepRunDTO().getRunStatus());
        TaskStepRun taskStepRun = DBService.getTaskStepRun(taskContext.getTaskStepRunDTO().getId());
        taskStepRun.setStartTime(taskContext.getTaskStepRunDTO().getStartTime());
        taskStepRun.setFinishTime(taskContext.getTaskStepRunDTO().getFinishTime());
        taskStepRun.setLogs(taskContext.getTaskStepRunDTO().getLogs());
        taskStepRun.setStatus(taskContext.getTaskStepRunDTO().isStatus());
        taskStepRun.setRunStatus(taskContext.getTaskStepRunDTO().getRunStatus());
        taskStepRun.setRunState(taskContext.getTaskStepRunDTO().getRunState());
        DBService.save(taskStepRun);
        TaskRun taskRun = DBService.getTaskRun(taskStepRun);
        statusObserver.notifyStatus(taskRun.getTeam().getTelegramId(), taskRun.isNotifyStatus(), "Execution Completed for Step# " + taskContext.getStepDTO().getSequence() + ", Status = " + taskContext.getTaskStepRunDTO().getRunStatus());
        currentlyRunningTaskSteps.get(taskRun).remove(taskStepRun);
        TaskExecutionPlan taskExecutionPlan = taskExecutionPlanMap.get(taskRun);
        taskExecutionPlan.getSessionMap().putAll(taskContext.getSessionMap());
        taskExecutionPlan.setTaskStatus(taskExecutionPlan.isTaskStatus() & taskStepRun.isStatus());
        if (currentlyRunningTaskSteps.get(taskRun).isEmpty()) {
            if (!taskExecutionPlan.isTaskStatus() && taskExecutionPlan.isAbortOnFirstFailure()) {
                logger.info("Aborting Task Execution after first failure");
                saveTaskRun(taskRun, taskExecutionPlan, RunStatus.FAILURE);
                statusObserver.notifyStatus(taskRun.getTeam().getTelegramId(), taskRun.isNotifyStatus(), "Aborting Task Execution after first failure");
                return;
            }
            processNextStep(taskRun, taskExecutionPlan);
        } else {
            logger.info("Waiting for other parallel steps to complete for sequence - " + taskStepRun.getTaskStep().getSequence());
        }
    }

    private void processNextStep(TaskRun taskRun, TaskExecutionPlan taskExecutionPlan) {
        Map.Entry<Integer, List<TaskStep>> next = taskExecutionPlan.next();
        if (next != null) {
            delegateStepToAgents(next.getValue(), taskRun);
        } else {
            currentlyRunningTaskSteps.remove(taskRun);
            saveTaskRun(taskRun, taskExecutionPlan, RunStatus.SUCCESS);
            logger.info("Task has no further steps, completing it now");
            statusObserver.notifyStatus(taskRun.getTeam().getTelegramId(), taskRun.isNotifyStatus(), "Task has no further steps, completing it now");
        }
    }

    private void saveTaskRun(TaskRun taskRun, TaskExecutionPlan taskExecutionPlan, RunStatus success) {
        synchronized (taskRun) {
            taskRun.setRunState(RunState.COMPLETED);
            taskRun.setFinishTime(new Date());
            taskRun.setStatus(taskExecutionPlan.isTaskStatus());
            taskRun.setRunStatus(success);
            DBService.save(taskRun);
        }
    }

    @PreDestroy
    public void dispose() {
        logger.info("Destroying the Task Service context");
    }

    private void delegateStepToAgents(List<TaskStep> taskStepDataList, TaskRun taskRun) {
        taskStepDataList.parallelStream().forEach(stepData -> {
            List<Agent> agentList = stepData.getAgentList().size() > 0 ? stepData.getAgentList() : taskExecutionPlanMap.get(taskRun).getTask().getAgentList();
            agentList.stream().forEach(agent -> {
                TaskStepRun taskStepRun = new TaskStepRun();
                taskStepRun.setSequence(stepData.getSequence());
                taskStepRun.setTaskStep(stepData);
                taskStepRun.setTaskRun(taskRun);
                taskStepRun.setAgent(agent);
                taskStepRun.setRunState(RunState.RUNNING);
                taskStepRun.setRunStatus(RunStatus.RUNNING);
                DBService.save(taskStepRun);
                currentlyRunningTaskSteps.computeIfAbsent(taskRun, tsr -> new Vector<>()).add(taskStepRun);
            });
        });
        if (currentlyRunningTaskSteps.get(taskRun) != null && currentlyRunningTaskSteps.get(taskRun).size() > 0) {
            logger.info("execution started for task step - " + taskStepDataList.get(0).getSequence());
            new CopyOnWriteArrayList<>(currentlyRunningTaskSteps.get(taskRun)).parallelStream().forEach(taskStepRun -> {
                TaskContext executionContext = new TaskContext();
                try {
                    String hostAddress = Inet4Address.getLocalHost().getHostAddress();
                    executionContext.setCallbackURL("http://" + hostAddress + ":9290/rest/server/submitTaskStepResults");
                    executionContext.setBaseUrl("http://" + hostAddress + ":9290/rest");
                    executionContext.setUsername("agent");
                    executionContext.setPassword("agent");
                    executionContext.setSessionMap(taskExecutionPlanMap.get(taskRun).getSessionMap());
                    executionContext.setTaskStepRunDTO(convertToDTO(taskStepRun));
                    TaskStep taskStep = taskStepRun.getTaskStep();
                    executionContext.setStepDTO(convertToDTO(taskStep));
                    restClient.submitTaskToAgent(executionContext, taskStepRun.getAgent());
                    logger.info("task submitted - " + taskStep.getDescription());
                } catch (Exception e) {
                    logger.error("Task Submission Failed", e);
                    taskExecutionPlanMap.get(taskRun).setTaskStatus(false);
                    executionContext.getTaskStepRunDTO().setStatus(false);
                    if (e.getCause() != null && e.getCause() instanceof ConnectException) {
                        statusObserver.notifyStatus(taskRun.getTeam().getTelegramId(), taskRun.isNotifyStatus(), "Task Submission Failed, Agent not reachable - " + taskStepRun.getAgent().getName() + e.getMessage());
                        executionContext.getTaskStepRunDTO().setLogs("Task Submission Failed, Agent not reachable - " + taskStepRun.getAgent().getName() + "\r\n" + e);
                    } else {
                        executionContext.getTaskStepRunDTO().setLogs("Task Submission Failed - " + Utils.getStackTrace(e));
                    }
                    executionContext.getTaskStepRunDTO().setFinishTime(new Date());
                    executionContext.getTaskStepRunDTO().setRunStatus(RunStatus.FAILURE);
                    executionContext.getTaskStepRunDTO().setRunState(RunState.COMPLETED);
                    consumeStepResult(executionContext);
                }
            });
            logger.info("execution command sent for task step - " + taskStepDataList.get(0).getSequence());
        } else {
            logger.warn("execution skipped for task step as there was no agent configured - " + taskStepDataList.get(0).getSequence());
            processNextStep(taskRun, taskExecutionPlanMap.get(taskRun));
        }
    }

    private TaskStepDTO convertToDTO(TaskStep taskStep) throws JAXBException {
        TaskStepDTO taskStepDTO = new TaskStepDTO();
        taskStepDTO.setName(taskStep.getName());
        taskStepDTO.setDescription(taskStep.getDescription());
        taskStepDTO.setInputParamsMap(FieldPropertiesMap.convertStringToMap(taskStep.getInputParams()));
        taskStepDTO.setOutputParamsMap(FieldPropertiesMap.convertStringToMap(taskStep.getOutputParams()));
        taskStepDTO.setSequence(taskStep.getSequence());
        //TODO - Handle taskName and TaskClass using taskRegistryService
        taskStepDTO.setTaskClass("org.shunya.shared.taskSteps." + taskStep.getTaskClass());
        taskStepDTO.setTaskId(taskStep.getTask().getId());
        return taskStepDTO;
    }

    private TaskStepRunDTO convertToDTO(TaskStepRun taskStepRun) {
        TaskStepRunDTO taskStepRunDTO = new TaskStepRunDTO();
        taskStepRunDTO.setId(taskStepRun.getId());
        return taskStepRunDTO;
    }
}