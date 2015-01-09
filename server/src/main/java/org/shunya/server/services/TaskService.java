package org.shunya.server.services;

import org.shunya.server.TaskExecutionPlan;
import org.shunya.server.model.*;
import org.shunya.shared.*;
import org.shunya.shared.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.Inet4Address;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class TaskService {
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    Map<TaskRun, TaskExecutionPlan> taskExecutionPlanMap = new ConcurrentHashMap<>(100);
    Map<TaskRun, List<TaskStepRun>> currentlyRunningTaskSteps = new ConcurrentHashMap<>(100);
    Set<Long> currentlyRunningTasks = new HashSet<>();

    @Autowired
    private RestClient restClient;

    @Autowired
    private DBService dbService;

    @Autowired
    @Qualifier("myExecutor")
    private TaskExecutor executor;

    @Autowired
    private TelegramStatusObserver statusObserver;

    @Value("#{servletContext.contextPath}")
    private String contextPath;

    @Value("${server.port}")
    private String serverPort;

    private int maxSystemFailureTimeInHours = 3;

    @Scheduled(cron = "0 0/2 * * * ?")
    public void checkSystemFailures() {
        logger.info("Running System Failures of Agents");
        taskExecutionPlanMap.forEach((taskRun, taskExecutionPlan) -> {
            if (LocalDateTime.ofInstant(taskRun.getStartTime().toInstant(), ZoneId.systemDefault()).isBefore(LocalDateTime.now().minusHours(maxSystemFailureTimeInHours))) {
                logger.warn("System Failures Detected for TaskRun - " + taskRun.getName());
                handleCompletion(taskRun, taskExecutionPlan, RunStatus.FAILURE);
                currentlyRunningTaskSteps.remove(taskRun);
                logger.warn("Task Kicked due to System Failures, TaskRun - " + taskRun.getName());
            }
        });
    }

    // if task is not started at all - create execution plan and start it
    // if task has start but not completed - then execute rest of steps
    // if all task steps has completed, then do the cleanup
    @Async
    public void execute(TaskRun taskRun) {
        if (currentlyRunningTasks.contains(taskRun.getTask().getId())) {
            statusObserver.notifyStatus(taskRun.getTeam().getTelegramId(), taskRun.isNotifyStatus(), "Another instance of this Task is already running, cancelling execution - " + taskRun.getName());
            logger.info("Another instance of this Task is already running, cancelling execution - ", taskRun.getName());
            return;
        }
        onStart(taskRun);
        statusObserver.notifyStatus(taskRun.getTeam().getTelegramId(), taskRun.isNotifyStatus(), "starting execution for - " + taskRun.getName());
        logger.info("starting execution for TaskRun {}", taskRun.getName());
        dbService.save(taskRun);
        TaskExecutionPlan executionPlan = taskExecutionPlanMap.computeIfAbsent(taskRun, k -> new TaskExecutionPlan(taskRun.getTask()));
        Map.Entry<Integer, List<TaskStep>> next = executionPlan.next();
        if (next != null) {
            taskRun.setRunState(RunState.RUNNING);
            taskRun.setRunStatus(RunStatus.RUNNING);
            dbService.save(taskRun);
            executionPlan.getSessionMap().putAll(loadAgentNames(taskRun.getTeam().getId()));
            executionPlan.getSessionMap().putAll(loadTeamProperties(taskRun.getTeam().getTeamProperties()));
            executionPlan.getSessionMap().putAll(loadTaskProperties(taskRun.getTask().getTaskProperties()));
            delegateStepToAgents(next.getValue(), taskRun);
        } else {
            handleCompletion(taskRun, executionPlan, RunStatus.NOT_RUN);
            logger.info("Task has no steps, completing it now");
        }
    }

    protected void onStart(TaskRun taskRun) {
        currentlyRunningTasks.add(taskRun.getTask().getId());
    }

    protected void handleCompletion(TaskRun taskRun, TaskExecutionPlan taskExecutionPlan, RunStatus runStatus) {
        saveTaskRun(taskRun, taskExecutionPlan, runStatus);
        taskExecutionPlanMap.remove(taskRun);
        currentlyRunningTasks.remove(taskRun.getTask().getId());
    }

    public boolean cancelTaskRun(TaskRun taskRun) {
        TaskExecutionPlan taskExecutionPlan = taskExecutionPlanMap.get(taskRun);
        if (taskExecutionPlan != null) {
            taskExecutionPlan.setCancelled(true);
            return true;
        }
        return false;
    }

    @Async
    public void consumeStepResult(TaskContext taskContext) {
        TaskStepRun taskStepRun = dbService.getTaskStepRun(taskContext.getTaskStepRunDTO().getId());
        taskStepRun.setStartTime(taskContext.getTaskStepRunDTO().getStartTime());
        taskStepRun.setFinishTime(taskContext.getTaskStepRunDTO().getFinishTime());
        taskStepRun.setLogs(taskContext.getTaskStepRunDTO().getLogs());
        taskStepRun.setStatus(taskContext.getTaskStepRunDTO().isStatus());
        taskStepRun.setRunStatus(taskContext.getTaskStepRunDTO().getRunStatus());
        taskStepRun.setRunState(taskContext.getTaskStepRunDTO().getRunState());
        dbService.save(taskStepRun);
        TaskRun taskRun = dbService.getTaskRun(taskStepRun);
        logger.info(taskContext.getStepDTO().getSequence() + ". " + taskStepRun.getAgent().getName() + " - " + taskContext.getStepDTO().getDescription() + " - " + taskContext.getTaskStepRunDTO().getRunStatus());
        statusObserver.notifyStatus(taskRun.getTeam().getTelegramId(), taskRun.isNotifyStatus(), taskContext.getStepDTO().getSequence() + ". " + taskStepRun.getAgent().getName() + " - " + taskContext.getStepDTO().getDescription() + " - " + taskContext.getTaskStepRunDTO().getRunStatus());
        currentlyRunningTaskSteps.get(taskRun).remove(taskStepRun);
        TaskExecutionPlan taskExecutionPlan = taskExecutionPlanMap.get(taskRun);
        taskExecutionPlan.getSessionMap().putAll(taskContext.getSessionMap());
        taskExecutionPlan.setTaskStatus(taskExecutionPlan.isTaskStatus() & (taskStepRun.isStatus() || taskStepRun.getTaskStep().isIgnoreFailure()));
        if (taskStepRun.getTaskStep().isIgnoreFailure() && !taskStepRun.isStatus()) {
            logger.info("Ignoring Step failure, as it is set Optional");
        }
        if (currentlyRunningTaskSteps.get(taskRun).isEmpty()) {
            if (!taskExecutionPlan.isTaskStatus() && taskExecutionPlan.isAbortOnFirstFailure()) {
                logger.info("Aborting Task Execution after first failure, State = Complete");
                handleCompletion(taskRun, taskExecutionPlan, RunStatus.FAILURE);
                statusObserver.notifyStatus(taskRun.getTeam().getTelegramId(), taskRun.isNotifyStatus(), "Aborting Task Execution after first failure, State = Complete");
                return;
            } else if (taskExecutionPlan.isCancelled()) {
                logger.info("Aborting Task Execution due to User Cancellation, State = Cancelled");
                handleCompletion(taskRun, taskExecutionPlan, RunStatus.CANCELLED);
                statusObserver.notifyStatus(taskRun.getTeam().getTelegramId(), taskRun.isNotifyStatus(), "Aborting Task Execution due to User Cancellation, State = Cancelled");
                return;
            }
            processNextStep(taskRun, taskExecutionPlan);
        } else {
            logger.info("Waiting for other parallel steps to complete for sequence - " + taskStepRun.getTaskStep().getSequence());
        }
    }

    private Map<String, String> loadAgentNames(long teamId) {
        List<Agent> agents = dbService.listAgentsByTeam(teamId);
        Map<String, String> variables = new HashMap<>();
        agents.forEach(agent -> variables.put(agent.getName(), agent.getBaseUrl()));
        return variables;
    }

    private Map<String, String> loadAgentProperties(AgentProperties agentProperties) {
        try {
            if (agentProperties != null && agentProperties.getProperties() != null) {
                InputStream is = new ByteArrayInputStream(agentProperties.getProperties().getBytes());
                Properties prop = new Properties();
                prop.load(is);
                return (Map) prop;
            }
        } catch (Exception e) {
            logger.error("Error loading Agent properties while execution", e);
        }
        return Collections.emptyMap();
    }

    private Map<String, String> loadTaskProperties(TaskProperties taskProperties) {
        try {
            if (taskProperties != null && taskProperties.getProperties() != null) {
                InputStream is = new ByteArrayInputStream(taskProperties.getProperties().getBytes());
                Properties prop = new Properties();
                prop.load(is);
                return (Map) prop;
            }
        } catch (Exception e) {
            logger.error("Error loading Agent properties while execution", e);
        }
        return Collections.emptyMap();
    }

    private Map<String, String> loadTeamProperties(TeamProperties teamProperties) {
        try {
            if (teamProperties != null && teamProperties.getProperties() != null) {
                InputStream is = new ByteArrayInputStream(teamProperties.getProperties().getBytes());
                Properties prop = new Properties();
                prop.load(is);
                return (Map) prop;
            }
        } catch (Exception e) {
            logger.error("Error loading Agent properties while execution", e);
        }
        return Collections.emptyMap();
    }

    private void processNextStep(TaskRun taskRun, TaskExecutionPlan taskExecutionPlan) {
        Map.Entry<Integer, List<TaskStep>> next = taskExecutionPlan.next();
        if (next != null) {
            delegateStepToAgents(next.getValue(), taskRun);
        } else {
            currentlyRunningTaskSteps.remove(taskRun);
            handleCompletion(taskRun, taskExecutionPlan, RunStatus.SUCCESS);
            logger.info("Task has no further steps, " + taskRun.getName() + " Completed with status - " + taskRun.getRunStatus());
            statusObserver.notifyStatus(taskRun.getTeam().getTelegramId(), true, "Task - " + taskRun.getName() + " Completed with status - " + taskRun.getRunStatus());
        }
    }

    private void saveTaskRun(TaskRun taskRun, TaskExecutionPlan taskExecutionPlan, RunStatus success) {
        synchronized (taskRun) {
            taskRun.setRunState(RunState.COMPLETED);
            taskRun.setFinishTime(new Date());
            taskRun.setStatus(taskExecutionPlan.isTaskStatus());
            taskRun.setRunStatus(success);
            dbService.save(taskRun);
        }
    }

    @PostConstruct
    public void start() {
        System.out.println("serverPort = " + serverPort);
    }

    @PreDestroy
    public void dispose() {
        logger.info("Destroying the Task Service context");
    }

    private void delegateStepToAgents(List<TaskStep> taskStepDataList, TaskRun taskRun) {
        taskStepDataList.parallelStream().forEach(stepData -> {
            Set<Agent> agentList = stepData.getAgentList().size() > 0 ? stepData.getAgentList() : taskExecutionPlanMap.get(taskRun).getTask().getAgentList();
            agentList.stream().forEach(agent -> {
                TaskStepRun taskStepRun = new TaskStepRun();
                taskStepRun.setSequence(stepData.getSequence());
                taskStepRun.setStartTime(new Date());
                taskStepRun.setTaskStep(stepData);
                taskStepRun.setTaskRun(taskRun);
                taskStepRun.setAgent(agent);
                taskStepRun.setRunState(RunState.RUNNING);
                taskStepRun.setRunStatus(RunStatus.RUNNING);
                dbService.save(taskStepRun);
                currentlyRunningTaskSteps.computeIfAbsent(taskRun, tsr -> new Vector<>()).add(taskStepRun);
            });
        });
        if (currentlyRunningTaskSteps.get(taskRun) != null && currentlyRunningTaskSteps.get(taskRun).size() > 0) {
            logger.info("execution started for task step - " + taskStepDataList.get(0).getSequence());
            new CopyOnWriteArrayList<>(currentlyRunningTaskSteps.get(taskRun)).parallelStream().forEach(taskStepRun -> {
                TaskContext executionContext = new TaskContext();
                try {
                    String hostAddress = Inet4Address.getLocalHost().getHostAddress();
                    executionContext.setCallbackURL("http://" + hostAddress + ":" + serverPort + contextPath + "/server/submitTaskStepResults");
                    executionContext.setBaseUrl("http://" + hostAddress + ":" + serverPort + contextPath);
                    executionContext.setUsername("agent");
                    executionContext.setPassword("agent");
                    executionContext.setSessionMap(new HashMap(taskExecutionPlanMap.get(taskRun).getSessionMap()));
                    executionContext.setTaskStepRunDTO(convertToDTO(taskStepRun));
                    TaskStep taskStep = taskStepRun.getTaskStep();
                    executionContext.setStepDTO(convertToDTO(taskStep));
                    executionContext.getSessionMap().putAll(loadTeamProperties(taskRun.getTeam().getTeamProperties()));
                    executionContext.getSessionMap().putAll(loadTaskProperties(taskRun.getTask().getTaskProperties()));
                    executionContext.getSessionMap().putAll(loadAgentProperties(taskStepRun.getAgent().getAgentProperties()));
                    logger.info("executionContext.getSessionMap() = " + executionContext.getSessionMap());
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