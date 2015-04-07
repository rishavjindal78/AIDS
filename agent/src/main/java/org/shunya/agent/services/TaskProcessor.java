package org.shunya.agent.services;

import org.shunya.shared.*;
import org.shunya.shared.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TaskProcessor {
    static final Logger logger = LoggerFactory.getLogger(TaskProcessor.class);
    @Autowired
    private RestClient restClient;
    private AtomicInteger runningJobCount = new AtomicInteger(0);
    private ConcurrentMap<Long, AbstractStep> cache = new ConcurrentHashMap<>();

    @Autowired
    private TaskExecutor myExecutor;

    @Value("#{servletContext.contextPath?:'/'}")
    private String contextPath;

    @Value("${app.name}")
    private String appName;

    public void executeTask(TaskContext taskContext) throws InterruptedException {
        try {
            TaskStepDTO taskStepDTO = taskContext.getStepDTO();
            taskContext.getTaskStepRunDTO().setStartTime(new Date());
            AbstractStep taskStep = AbstractStep.getTask(taskStepDTO);
            taskStep.setLoggingLevel(taskContext.getLoggingLevel());
            taskStep.setTaskStepData(taskStepDTO);
            taskStep.setSessionMap(taskContext.getSessionMap());
            cache.put(taskContext.getTaskStepRunDTO().getId(), taskStep);
            myExecutor.execute(() -> {
                preProcess();
                try {
                    taskStep.beforeTaskStart();
                    boolean status = taskStep.execute();
                    taskContext.getTaskStepRunDTO().setStatus(status);
                    taskContext.getTaskStepRunDTO().setLogs(taskStep.getMemoryLogs());
                    if (status)
                        taskContext.getTaskStepRunDTO().setRunStatus(RunStatus.SUCCESS);
                    else
                        taskContext.getTaskStepRunDTO().setRunStatus(RunStatus.FAILURE);
                } catch (Exception e) {
                    taskContext.getTaskStepRunDTO().setRunStatus(RunStatus.FAILURE);
                    taskContext.getTaskStepRunDTO().setLogs(Utils.getStackTrace(e) + "\n");
                    logger.warn("Error executing the taskStep : " + taskContext.getTaskStepRunDTO().getId(), e);
                } finally {
                    taskContext.getTaskStepRunDTO().setFinishTime(new Date());
                    taskContext.getTaskStepRunDTO().setRunState(RunState.COMPLETED);
                    taskStep.afterTaskFinish();
                    postProcess();
                    try{restClient.postResultToServer(taskContext);}catch (Exception e){logger.error("Severe error connecting to the server", e);}
                    cache.remove(taskContext.getTaskStepRunDTO().getId());
                }
            });
        } catch (Throwable t) {
            logger.error("Error executing the task - ", t);
        }
    }

    public String getMemoryLogs(long id, long start) {
        AbstractStep abstractStep = cache.get(id);
        if (abstractStep != null) {
            return abstractStep.getMemoryLogs().substring((int) Math.min(abstractStep.getMemoryLogs().length(), start));
        }
        return "FINISHED";
    }

    public boolean isRunning(long taskStepRunId) {
        boolean running = cache.get(taskStepRunId) != null;
        if (!running) {
            logger.warn("TaskStep is not running - " + taskStepRunId);
        }
        return running;
    }

    public void interrupt(long taskStepRunId) {
        if (cache.get(taskStepRunId) != null) {
            cache.get(taskStepRunId).interrupt();
        }
    }

    private void postProcess() {
        runningJobCount.decrementAndGet();
    }

    private void preProcess() {
        runningJobCount.incrementAndGet();
    }

    @PostConstruct
    public void start() {
        System.out.println("Context Path is - " + contextPath);
        System.out.println("App Name is - " + appName);
    }

    @PreDestroy
    public void shutdown() throws InterruptedException {
        logger.warn("Shutting down the Agent on this machine");
    }
}
