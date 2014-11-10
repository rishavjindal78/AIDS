package org.shunya.agent;

import org.shunya.agent.services.ServerWorker;
import org.shunya.shared.TaskContext;
import org.shunya.shared.TaskStep;
import org.shunya.shared.model.RunState;
import org.shunya.shared.model.RunStatus;
import org.shunya.shared.model.TaskStepData;
import org.shunya.shared.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TaskProcessor {
    static final Logger logger = LoggerFactory.getLogger(TaskProcessor.class);
    @Autowired
    private ServerWorker serverWorker;
    @Autowired
    private SystemSupport systemSupport;
    private AtomicBoolean shutdown = new AtomicBoolean(false);
    private AtomicInteger runningJobCount = new AtomicInteger(0);
    private ConcurrentMap<Long, TaskStep> cache = new ConcurrentHashMap<>();

    @Autowired
    private TaskExecutor myExecutor;

    public void executeTask(TaskContext taskContext) throws InterruptedException {
        try {
            preProcess();
            myExecutor.execute(() -> {
                TaskStepData taskStepData = taskContext.getTaskStepRun().getTaskStepData();
                taskContext.getTaskStepRun().setStartTime(new Date());
                TaskStep task = TaskStep.getTask(taskStepData);
                task.setTaskStepData(taskStepData);
                task.setSessionMap(taskContext.getSessionMap());
                cache.putIfAbsent(taskContext.getTaskStepRun().getId(), task);
                try {
                    task.beforeTaskStart();
                    boolean status = task.execute();
                    taskContext.getTaskStepRun().setStatus(status);
                    taskContext.getTaskStepRun().setFinishTime(new Date());
                    taskContext.getTaskStepRun().setLogs(task.getMemoryLogs());
                    if (status)
                        taskContext.getTaskStepRun().setRunStatus(RunStatus.SUCCESS);
                    else
                        taskContext.getTaskStepRun().setRunStatus(RunStatus.FAILURE);
                } catch (Exception e) {
                    taskContext.getTaskStepRun().setRunStatus(RunStatus.FAILURE);
                    taskContext.getTaskStepRun().setLogs(Utils.getStackTrace(e) + "\n");
                    logger.warn("Error executing the task : " + taskStepData.getId(), e);
                } finally {
                    taskContext.getTaskStepRun().setRunState(RunState.COMPLETED);
                    task.afterTaskFinish();
                    serverWorker.postResultToServer(taskContext.getCallbackURL(), taskContext);
                }
            });
        } catch (Throwable t) {
            logger.error("Error executing the task - ", t);
        } finally {
            postProcess();
            cache.remove(taskContext.getTaskStepRun().getId());
        }
    }

    public String getMemoryLogs(long id, long start){
        TaskStep taskStep = cache.get(id);
        if(taskStep !=null){
            return taskStep.getMemoryLogs().substring((int) Math.min(taskStep.getMemoryLogs().length(), start));
        }
        return "";
    }

    private void postProcess() {
        runningJobCount.decrementAndGet();
        updateSystemTray();
    }

    private void preProcess() {
        runningJobCount.incrementAndGet();
        updateSystemTray();
    }

    private void updateSystemTray() {
        if (runningJobCount.get() == 0)
            systemSupport.update(AgentState.Idle);
        else
            systemSupport.update(AgentState.Busy);
    }

    @PostConstruct
    public void start() {

    }

    @PreDestroy
    public void shutdown() throws InterruptedException {
        shutdown.set(true);
    }
}
