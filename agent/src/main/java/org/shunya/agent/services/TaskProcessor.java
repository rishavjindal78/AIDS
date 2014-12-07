package org.shunya.agent.services;

import org.shunya.shared.AbstractStep;
import org.shunya.shared.TaskContext;
import org.shunya.shared.TaskStepDTO;
import org.shunya.shared.RunState;
import org.shunya.shared.RunStatus;
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
            myExecutor.execute(() -> {
                preProcess();
                TaskStepDTO taskStepDTO = taskContext.getStepDTO();
                taskContext.getTaskStepRunDTO().setStartTime(new Date());
                AbstractStep task = AbstractStep.getTask(taskStepDTO);
                task.setTaskStepData(taskStepDTO);
                task.setSessionMap(taskContext.getSessionMap());
                cache.putIfAbsent(taskContext.getTaskStepRunDTO().getId(), task);
                try {
                    task.beforeTaskStart();
                    boolean status = task.execute();
                    taskContext.getTaskStepRunDTO().setStatus(status);
                    taskContext.getTaskStepRunDTO().setFinishTime(new Date());
                    taskContext.getTaskStepRunDTO().setLogs(task.getMemoryLogs());
                    if (status)
                        taskContext.getTaskStepRunDTO().setRunStatus(RunStatus.SUCCESS);
                    else
                        taskContext.getTaskStepRunDTO().setRunStatus(RunStatus.FAILURE);
                } catch (Exception e) {
                    taskContext.getTaskStepRunDTO().setRunStatus(RunStatus.FAILURE);
                    taskContext.getTaskStepRunDTO().setLogs(Utils.getStackTrace(e) + "\n");
                    logger.warn("Error executing the task : " + taskContext.getTaskStepRunDTO().getId(), e);
                } finally {
                    taskContext.getTaskStepRunDTO().setRunState(RunState.COMPLETED);
                    task.afterTaskFinish();
                    postProcess();
                    restClient.postResultToServer(taskContext);
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
        return "";
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
