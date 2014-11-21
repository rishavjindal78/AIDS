package org.shunya.server;

import org.shunya.server.services.DBService;
import org.shunya.server.services.AgentWorker;
import org.shunya.shared.TaskContext;
import org.shunya.shared.model.*;
import org.shunya.shared.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.net.ConnectException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class TaskService {
    private static final Logger logger = Logger.getLogger(TaskService.class.getName());

    Map<TaskRun, TaskExecutionPlan> taskExecutionPlanMap = new ConcurrentHashMap<>(100);
    Map<TaskRun, List<TaskStepRun>> currentlyRunningTaskSteps = new ConcurrentHashMap<>(100);

    @Autowired
    private AgentWorker agentWorker;

    @Autowired
    private DBService DBService;

    @Autowired
    @Qualifier("myExecutor")
    private TaskExecutor executor;

    // if task is not started at all - create execution plan and start it
    // if task has start but not completed - then execute rest of steps
    // if all task steps has completed, then do the cleanup
    public void createTaskRun(TaskRun taskRun) {
        DBService.save(taskRun);
        TaskExecutionPlan executionPlan = taskExecutionPlanMap.computeIfAbsent(taskRun, k -> new TaskExecutionPlan(taskRun.getTaskData()));
        Map.Entry<Integer, List<TaskStepData>> next = executionPlan.next();
        if (next != null) {
            taskRun.setRunState(RunState.RUNNING);
            taskRun.setRunStatus(RunStatus.RUNNING);
            DBService.save(taskRun);
            delegateTaskSteps(next.getValue(), taskRun);
        } else {
            taskRun.setRunState(RunState.COMPLETED);
            taskRun.setFinishTime(new Date());
            taskRun.setRunStatus(RunStatus.NOT_RUN);
            DBService.save(taskRun);
            taskExecutionPlanMap.remove(taskRun);
            logger.info(() -> "Task has no steps, completing it now");
        }
    }

    public void processNextSteps(TaskRun taskRun, TaskContext taskContext) {
        executor.execute(() -> {
            TaskStepRun taskStepRun = taskContext.getTaskStepRun();
            taskStepRun.setTaskRun(taskRun);
            DBService.save(taskStepRun);
            currentlyRunningTaskSteps.get(taskRun).remove(taskStepRun);
            TaskExecutionPlan taskExecutionPlan = taskExecutionPlanMap.get(taskRun);
            taskExecutionPlan.getSessionMap().putAll(taskContext.getSessionMap());
            taskExecutionPlan.setTaskStatus(taskExecutionPlan.isTaskStatus() & taskStepRun.isStatus());
            if (currentlyRunningTaskSteps.get(taskRun).isEmpty()) {
                if (!taskExecutionPlan.isTaskStatus() && taskExecutionPlan.isAbortOnFirstFailure()) {
                    logger.info(() -> "Aborting Task Execution after first failure");
                    saveTaskRun(taskRun, taskExecutionPlan, RunStatus.FAILURE);
                    return;
                }
                Map.Entry<Integer, List<TaskStepData>> next = taskExecutionPlan.next();
                if (next != null) {
                    delegateTaskSteps(next.getValue(), taskRun);
                } else {
                    currentlyRunningTaskSteps.remove(taskRun);
                    saveTaskRun(taskRun, taskExecutionPlan, RunStatus.SUCCESS);
                    logger.info(() -> "Task has no further steps, completing it now");
                }
            } else {
                logger.info(() -> "Waiting for other parallel steps to complete for sequence - " + taskStepRun.getTaskStepData().getSequence());
            }
        });
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
        logger.info(() -> "Destroying the Task Service context");
    }

    private void delegateTaskSteps(List<TaskStepData> taskStepDataList, TaskRun taskRun) {
        taskStepDataList.parallelStream().forEach(stepData -> {
            List<Agent> agentList = stepData.getAgentList().size() > 0 ? stepData.getAgentList() : taskExecutionPlanMap.get(taskRun).getTaskData().getAgentList();
            agentList.stream().forEach(agent -> {
                TaskStepRun taskStepRun = new TaskStepRun();
                taskStepRun.setSequence(stepData.getSequence());
                taskStepRun.setTaskStepData(stepData);
                taskStepRun.setTaskRun(taskRun);
                taskStepRun.setAgent(agent);
                DBService.save(taskStepRun);
                currentlyRunningTaskSteps.computeIfAbsent(taskRun, tsr -> new Vector<>()).add(taskStepRun);
            });
        });
        logger.info(() -> "execution started for task steps - " + taskStepDataList.get(0).getSequence());
        new CopyOnWriteArrayList<>(currentlyRunningTaskSteps.get(taskRun)).parallelStream().forEach(taskStepRun -> {
            TaskContext executionContext = new TaskContext();
            try {
                String hostAddress = Inet4Address.getLocalHost().getHostAddress();
                String callbackUrl = "http://" + hostAddress + ":9290/rest/server/submitTaskStepResults";
                executionContext.setCallbackURL(callbackUrl);
                executionContext.setSessionMap(taskExecutionPlanMap.get(taskRun).getSessionMap());
                executionContext.setTaskStepRun(taskStepRun);
                agentWorker.submitTaskToAgent(executionContext);
                logger.info(() -> "task submitted - " + taskStepRun.getTaskStepData().getDescription());
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Task Submission Failed", e);
                taskExecutionPlanMap.get(taskRun).setTaskStatus(false);
                executionContext.getTaskStepRun().setStatus(false);
                if (e.getCause() != null && e.getCause() instanceof ConnectException) {
                    executionContext.getTaskStepRun().setLogs("Task Submission Failed, Agent not reachable - " + executionContext.getTaskStepRun().getAgent().getName() + "\r\n" + e);
                } else {
                    executionContext.getTaskStepRun().setLogs("Task Submission Failed - " + Utils.getStackTrace(e));
                }
                executionContext.getTaskStepRun().setFinishTime(new Date());
                executionContext.getTaskStepRun().setRunStatus(RunStatus.FAILURE);
                executionContext.getTaskStepRun().setRunState(RunState.COMPLETED);
                processNextSteps(taskRun, executionContext);
            }
        });
        logger.info(() -> "execution completed for task steps - " + taskStepDataList.get(0).getSequence());
    }
}
