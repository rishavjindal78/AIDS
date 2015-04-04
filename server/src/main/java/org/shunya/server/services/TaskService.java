package org.shunya.server.services;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.util.Arrays.asList;

@Service
public class TaskService {
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    Map<TaskStepRun, TaskContext> currentlyRunningStepContext = new ConcurrentHashMap<>(10);
    Map<TaskRun, TaskExecutionContext> taskRunExecutionContext = new ConcurrentHashMap<>();
    Set<Long> currentlyRunningTasks = new HashSet<>();

    Map<TaskRun, List<DeferredResult<String>>> taskRunStatusSubscribers = new ConcurrentHashMap<>();
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

//    @Scheduled(cron = "20 0/10 * * * ?")
    @Scheduled(cron = "${task.timeout.cron}")
    public void checkTimeoutSystemFailures() {
        logger.info("Running Timeout System Failures");
        taskRunExecutionContext.forEach((taskRun, taskExecutionContext) -> {
            if (LocalDateTime.ofInstant(taskRun.getStartTime().toInstant(), ZoneId.systemDefault()).isBefore(LocalDateTime.now().minusHours(maxSystemFailureTimeInHours))) {
                logger.warn("System Failures Detected for TaskRun due to timeout - " + taskRun.getName());
                handleCompletion(taskRun, taskExecutionContext, RunStatus.FAILURE);
                taskExecutionContext.getCurrentlyRunningTaskStepRuns().remove(taskRun);
                logger.warn("Task Kicked due to System Failures, TaskRun - " + taskRun.getName());
            }
        });
    }

//    @Scheduled(cron = "0 */3 * * * ?")
    @Scheduled(cron = "${agent.failure.cron}")
    public void checkAgentDiedSystemFailures() {
        logger.info("Running System Failures of Agents");
        taskRunExecutionContext.forEach((taskRun, taskExecutionContext) -> {
            List<TaskStepRun> currentlyRunningTaskStepRuns = taskExecutionContext.getCurrentlyRunningTaskStepRuns();
            if (currentlyRunningTaskStepRuns != null)
                new CopyOnWriteArrayList<>(currentlyRunningTaskStepRuns).stream().filter(taskStepRun -> currentlyRunningStepContext.containsKey(taskStepRun)).forEach(taskStepRun -> {
                    Boolean stepRunning = false;
                    Boolean agentRunning = false;
                    try {
                        for (int i = 0; i < 2; i++) {
                            stepRunning = restClient.checkStepRunning(taskStepRun.getId(), taskStepRun.getAgent());
                            Thread.sleep(2000);
                            if (stepRunning)
                                break;
                            logger.warn("Step is not running on the agent, " + taskStepRun + " Agent - " + taskStepRun.getAgent());
                        }
                        agentRunning = true;
                    } catch (Exception e) {
                        agentRunning = false;
                        stepRunning = false;
                        logger.warn("Task probably died due to Agent failure, synchronizing it at server.", e);
                    }
                    if (!stepRunning && currentlyRunningStepContext.containsKey(taskStepRun)) {
                        TaskContext executionContext = currentlyRunningStepContext.get(taskStepRun);
                        taskRunExecutionContext.get(taskRun).setTaskStatus(false);
                        executionContext.getTaskStepRunDTO().setStatus(false);
                        if (!agentRunning) {
                            logger.warn("TaskStep failed due to unreachable Agent, agent probably died." + taskStepRun);
                            executionContext.getTaskStepRunDTO().setLogs("TaskStep failed due to unreachable Agent, agent probably died.");
                        } else {
                            logger.warn("TaskStep failed due to unknown reason, Agent is not running this step." + taskStepRun);
                            executionContext.getTaskStepRunDTO().setLogs("TaskStep failed due to unknown reason, Agent is not running this step.");
                        }
                        executionContext.getTaskStepRunDTO().setFinishTime(new Date());
                        executionContext.getTaskStepRunDTO().setRunStatus(RunStatus.FAILURE);
                        executionContext.getTaskStepRunDTO().setRunState(RunState.COMPLETED);
                        consumeStepResult(executionContext);
                    }
                });
        });
        logger.info("Finished Running System Failures of Agents");
    }

    public TaskRun createTaskRun(String comment, boolean notifyStatus, Principal principal, Task task, Agent agent, boolean singleton, String properties) {
        HashMap<String, String> propertiesOverride = new HashMap<>();
        if (properties != null && !properties.isEmpty())
            propertiesOverride.putAll(Utils.splitToMap(properties, ",", "="));
        TaskRun taskRun = new TaskRun();
        taskRun.setTask(task);
        taskRun.setName(task.getName());
        taskRun.setStartTime(new Date());
        taskRun.setComments(comment);
        taskRun.setNotifyStatus(notifyStatus);
        if(principal!=null)
        taskRun.setRunBy(dbService.findUserByUsername(principal.getName()));
        taskRun.setTeam(task.getTeam());
        taskRun.setAgent(agent);
        dbService.save(taskRun);
        execute(taskRun, propertiesOverride, singleton);
        return taskRun;
    }

    public boolean isTaskRunning(TaskRun taskRun) {
        return taskRunExecutionContext.containsKey(taskRun);
    }

    // if task is not started at all - create execution plan and start it
    // if task has start but not completed - then execute rest of steps
    // if all task steps has completed, then do the cleanup
    @Async
    public void execute(TaskRun taskRun, Map<String, String> propertiesOverride, boolean singleton) {
        if (singleton && currentlyRunningTasks.contains(taskRun.getTask().getId())) {
            statusObserver.notifyStatus(taskRun.getTeam().getTelegramId(), taskRun.isNotifyStatus(), "Another instance of this Task is already running, cancelling execution - " + taskRun.getName());
            logger.info("Another instance of this Task is already running, cancelling execution - ", taskRun.getName());
            return;
        }
        onStart(taskRun);
        statusObserver.notifyStatus(taskRun.getTeam().getTelegramId(), taskRun.isNotifyStatus(), "starting execution for - " + taskRun.getName());
        logger.info("Starting execution for TaskRun {}", taskRun.getName());
        dbService.save(taskRun);
        //Build execution map
        TaskExecutionContext executionContext = taskRunExecutionContext.computeIfAbsent(taskRun, k -> new TaskExecutionContext(taskRun.getTask()));
        TaskExecutionPlan executionPlan = new TaskExecutionPlan(taskRun.getTask());
        while (executionPlan.hasNext()) {
            Map.Entry<Integer, List<TaskStep>> next = executionPlan.next();
            if (next != null) {
                executionContext.addTaskStepRun(next.getKey(), prepareAndSaveTaskStepRuns(next.getValue(), taskRun));
            }
        }
        //Start execution now
        Map.Entry<Integer, List<TaskStepRun>> entry = executionContext.pollNextTaskStepRunList();
        if (entry != null) {
            taskRun.setRunState(RunState.RUNNING);
            taskRun.setRunStatus(RunStatus.RUNNING);
            dbService.save(taskRun);
            executionContext.getSessionMap().putAll(loadAgentNames(taskRun.getTeam().getId()));
            executionContext.getSessionMap().putAll(loadProperties(taskRun.getTeam().getTeamProperties()));
            if (taskRun.getRunBy() != null)
                executionContext.getSessionMap().putAll(loadProperties(taskRun.getRunBy().getUserProperties()));
            executionContext.getSessionMap().putAll(loadProperties(taskRun.getTask().getTaskProperties()));
            if (propertiesOverride != null) {
                executionContext.getPropertiesOverride().putAll(propertiesOverride);
            }
            delegateStepToAgents(entry.getValue(), taskRun, entry.getKey());
        } else {
            handleCompletion(taskRun, executionContext, RunStatus.NOT_RUN);
            logger.info("Task has no steps, completing it now");
        }
    }

    protected void onStart(TaskRun taskRun) {
        currentlyRunningTasks.add(taskRun.getTask().getId());
    }

    protected void handleCompletion(TaskRun taskRun, TaskExecutionContext taskExecutionPlan, RunStatus runStatus) {
        saveTaskRun(taskRun, taskExecutionPlan, runStatus);
        taskRunExecutionContext.remove(taskRun);
        currentlyRunningTasks.remove(taskRun.getTask().getId());
    }

    public boolean cancelTaskRun(TaskRun taskRun) {
        TaskExecutionContext taskExecutionPlan = taskRunExecutionContext.get(taskRun);
        if (taskExecutionPlan != null) {
            taskExecutionPlan.setCancelled(true);
            List<TaskStepRun> taskStepRuns = taskExecutionPlan.getCurrentlyRunningTaskStepRuns();
            new ArrayList<>(taskStepRuns).forEach(taskStepRun -> restClient.interruptTaskStep(taskStepRun.getId(), taskStepRun.getAgent()));
            return true;
        }
        return false;
    }

    //    @Async
//    Making this method async may cause race condition when agent submits results
    public void consumeStepResult(TaskContext taskContext) {
        TaskStepRun taskStepRun = dbService.getTaskStepRun(taskContext.getTaskStepRunDTO().getId());
        currentlyRunningStepContext.remove(taskStepRun);
        taskStepRun.setStartTime(taskContext.getTaskStepRunDTO().getStartTime());
        taskStepRun.setFinishTime(taskContext.getTaskStepRunDTO().getFinishTime());
        taskStepRun.setLogs(taskContext.getTaskStepRunDTO().getLogs());
        taskStepRun.setStatus(taskContext.getTaskStepRunDTO().isStatus());
        taskStepRun.setRunStatus(taskContext.getTaskStepRunDTO().getRunStatus());
        taskStepRun.setRunState(taskContext.getTaskStepRunDTO().getRunState());
        dbService.save(taskStepRun);
        TaskRun taskRun = dbService.getTaskRun(taskStepRun);
        publishTaskRunStatus(taskRun);
        logger.info(taskContext.getStepDTO().getSequence() + ". " + taskStepRun.getAgent().getName() + " - " + taskContext.getStepDTO().getDescription() + " - " + taskContext.getTaskStepRunDTO().getRunStatus());
        statusObserver.notifyStatus(taskRun.getTeam().getTelegramId(), taskRun.isNotifyStatus(), taskContext.getStepDTO().getSequence() + ". " + taskStepRun.getAgent().getName() + " - " + taskContext.getStepDTO().getDescription() + " - " + taskContext.getTaskStepRunDTO().getRunStatus());
        TaskExecutionContext executionContext = taskRunExecutionContext.get(taskRun);
        executionContext.getCurrentlyRunningTaskStepRuns().remove(taskStepRun);
        executionContext.getSessionMap().putAll(taskContext.getSessionMap());
        executionContext.setTaskStatus(executionContext.isTaskStatus() & (taskStepRun.isStatus() || taskStepRun.getTaskStep().isIgnoreFailure()));
        if (taskStepRun.getTaskStep().isIgnoreFailure() && !taskStepRun.isStatus()) {
            logger.info("Ignoring Step failure, as it is set Optional");
        }
        if (executionContext.getCurrentlyRunningTaskStepRuns().isEmpty()) {
            if (!executionContext.isTaskStatus() && executionContext.isAbortOnFirstFailure()) {
                logger.info("Aborting Task Execution after first failure, State = Complete, step failed = " + taskStepRun.getTaskStep().getName());
                handleCompletion(taskRun, executionContext, RunStatus.FAILURE);
                statusObserver.notifyStatus(taskRun.getTeam().getTelegramId(), taskRun.isNotifyStatus(), "Aborting Task Execution after first failure, State = Complete");
                publishTaskRunStatus(taskRun);
                return;
            } else if (executionContext.isCancelled()) {
                logger.info("Aborting Task Execution due to User Cancellation, State = Cancelled");
                handleCompletion(taskRun, executionContext, RunStatus.CANCELLED);
                statusObserver.notifyStatus(taskRun.getTeam().getTelegramId(), taskRun.isNotifyStatus(), "Aborting Task Execution due to User Cancellation, State = Cancelled");
                publishTaskRunStatus(taskRun);
                return;
            }
            processNextStep(taskRun, executionContext);
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

    private Map<String, String> loadProperties(CustomProperties customProperties) {
        try {
            if (customProperties != null && customProperties.getProperties() != null) {
                InputStream is = new ByteArrayInputStream(customProperties.getProperties().getBytes());
                Properties prop = new Properties();
                prop.load(is);
                return (Map) prop;
            }
        } catch (Exception e) {
            logger.error("Error loading Agent properties while execution", e);
        }
        return Collections.emptyMap();
    }

    private void processNextStep(TaskRun taskRun, TaskExecutionContext executionContext) {
        Map.Entry<Integer, List<TaskStepRun>> entry = executionContext.pollNextTaskStepRunList();
        if (entry != null) {
            delegateStepToAgents(entry.getValue(), taskRun, entry.getKey());
        } else {
            executionContext.getCurrentlyRunningTaskStepRuns().remove(taskRun);
            handleCompletion(taskRun, executionContext, RunStatus.SUCCESS);
            logger.info("Task has no further steps, " + taskRun.getName() + " Completed with status - " + taskRun.getRunStatus());
            statusObserver.notifyStatus(taskRun.getTeam().getTelegramId(), true, "Task - " + taskRun.getName() + ", " + taskRun.getComments() + " Completed, Status - " + taskRun.getRunStatus());
            publishTaskRunStatus(taskRun);
        }
    }

    private void saveTaskRun(TaskRun taskRun, TaskExecutionContext taskExecutionPlan, RunStatus success) {
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

    private void delegateStepToAgents(List<TaskStepRun> taskStepRunList, TaskRun taskRun, Integer sequence) {
        if (taskRunExecutionContext.get(taskRun).getCurrentlyRunningTaskStepRuns() != null && taskRunExecutionContext.get(taskRun).getCurrentlyRunningTaskStepRuns().size() > 0) {
            logger.info("execution started for task step - " + sequence);
            new CopyOnWriteArrayList<>(taskStepRunList).parallelStream().forEach(taskStepRun -> {
                TaskContext executionContext = new TaskContext();
                try {
                    String hostAddress = Inet4Address.getLocalHost().getHostAddress();
                    executionContext.setCallbackURL("http://" + hostAddress + ":" + serverPort + contextPath + "/server/submitTaskStepResults");
                    executionContext.setBaseUrl("http://" + hostAddress + ":" + serverPort + contextPath);
                    executionContext.setUsername("agent");
                    executionContext.setPassword("agent");
                    executionContext.setSessionMap(new HashMap(taskRunExecutionContext.get(taskRun).getSessionMap()));
                    executionContext.setTaskStepRunDTO(convertToDTO(taskStepRun));
                    TaskStep taskStep = taskStepRun.getTaskStep();
                    executionContext.setStepDTO(convertToDTO(taskStep));
                    if (taskRun.getRunBy() != null)
                        executionContext.getSessionMap().putAll(loadProperties(taskRun.getRunBy().getUserProperties()));
                    executionContext.getSessionMap().putAll(loadProperties(taskRun.getTeam().getTeamProperties()));
                    executionContext.getSessionMap().putAll(loadProperties(taskRun.getTask().getTaskProperties()));
                    executionContext.getSessionMap().putAll(loadProperties(taskStepRun.getAgent().getAgentProperties()));
                    executionContext.getSessionMap().putAll(taskRunExecutionContext.get(taskRun).getPropertiesOverride());
                    logger.info("executionContext.getSessionMap() = " + executionContext.getSessionMap());
                    taskStepRun.setRunState(RunState.RUNNING);
                    taskStepRun.setRunStatus(RunStatus.RUNNING);
                    taskStepRun.setStartTime(new Date());
                    dbService.save(taskStepRun);
                    restClient.submitTaskToAgent(executionContext, taskStepRun.getAgent());
                    currentlyRunningStepContext.put(taskStepRun, executionContext);
                    logger.info("task submitted - " + taskStep.getDescription());
                } catch (Exception e) {
                    logger.error("Task Submission Failed", e.getMessage());
                    taskRunExecutionContext.get(taskRun).setTaskStatus(false);
                    executionContext.getTaskStepRunDTO().setStatus(false);
                    if (e.getCause() != null && e.getCause() instanceof IOException) {
                        statusObserver.notifyStatus(taskRun.getTeam().getTelegramId(), taskRun.isNotifyStatus(), "Task Submission Failed, Agent not reachable - " + taskStepRun.getAgent().getName() + e.getMessage());
                        executionContext.getTaskStepRunDTO().setLogs("Task Submission Failed, Agent not reachable - " + taskStepRun.getAgent().getName() + "\r\n" + e);
                    } else {
                        executionContext.getTaskStepRunDTO().setLogs("Task Submission Failed - " + Utils.getStackTrace(e));
                    }
                    executionContext.getTaskStepRunDTO().setStartTime(new Date());
                    executionContext.getTaskStepRunDTO().setFinishTime(new Date());
                    executionContext.getTaskStepRunDTO().setRunStatus(RunStatus.FAILURE);
                    executionContext.getTaskStepRunDTO().setRunState(RunState.COMPLETED);
                    consumeStepResult(executionContext);
                }
            });
            publishTaskRunStatus(taskRun);
            logger.info("execution command sent for task step - " + sequence);
        } else {
            logger.warn("execution skipped for task step as there was no agent configured - " + sequence);
            processNextStep(taskRun, taskRunExecutionContext.get(taskRun));
        }
    }

    private List<TaskStepRun> prepareAndSaveTaskStepRuns(List<TaskStep> taskStepDataList, TaskRun taskRun) {
        List<TaskStepRun> taskStepRuns = new Vector<>();
        taskStepDataList.parallelStream().forEach(stepData -> {
            Set<Agent> agentList = stepData.getAgentList().size() > 0 ? stepData.getAgentList() : new HashSet<>(asList(taskRun.getAgent()));
            agentList.stream().filter(agent1 -> agent1 != null).forEach(agent -> {
                TaskStepRun taskStepRun = new TaskStepRun();
                taskStepRun.setSequence(stepData.getSequence());
                taskStepRun.setTaskStep(stepData);
                taskStepRun.setTaskRun(taskRun);
                taskStepRun.setAgent(agent);
                taskStepRun.setRunState(RunState.NOT_RUN);
                taskStepRun.setRunStatus(RunStatus.NOT_RUN);
                dbService.save(taskStepRun);
                taskStepRuns.add(taskStepRun);
//                prepareTaskSteps.computeIfAbsent(taskRun, tsr -> new Vector<>()).add(taskStepRun);
            });
        });
        return taskStepRuns;
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

    private void publishTaskRunStatus(TaskRun taskRun) {
        if (taskRunStatusSubscribers.get(taskRun) != null && taskRunStatusSubscribers.get(taskRun).size() > 0) {
            try {
                taskRun = dbService.getTaskRun(taskRun.getId());
                String jsonResults = getTaskRunAsJson(taskRun);
                List<DeferredResult> processed = new ArrayList<>();
                synchronized (taskRunStatusSubscribers.get(taskRun)) {
                    taskRunStatusSubscribers.get(taskRun).forEach(stringDeferredResult -> {
                        stringDeferredResult.setResult(jsonResults);
                        processed.add(stringDeferredResult);
                    });
                    taskRunStatusSubscribers.get(taskRun).removeAll(processed);
                    logger.info("Published TaskRun updates to all registered clients");
                }
            } catch (IOException e) {
                logger.error("Exception publishing TaskRun status updates", e);
            }
        }
    }

    private String getTaskRunAsJson(TaskRun taskRun) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
//      mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mapper.writeValue(baos, taskRun);
        return baos.toString();
    }

    @Scheduled(cron = "0/30 * * * * ?")
    public void processStalePublishRequest() {
        taskRunStatusSubscribers.forEach((taskRun, deferredResults) -> {
            if (!deferredResults.isEmpty()) {
                synchronized (taskRunStatusSubscribers.get(taskRun)) {
                    taskRun = dbService.getTaskRun(taskRun.getId());
                    if (taskRun.getRunState() == RunState.COMPLETED) {
                        try {
                            String jsonResult = getTaskRunAsJson(taskRun);
                            List<DeferredResult> processed = new ArrayList<>();
                            taskRunStatusSubscribers.get(taskRun).forEach(stringDeferredResult -> {
                                stringDeferredResult.setResult(jsonResult);
                                processed.add(stringDeferredResult);
                            });
                            taskRunStatusSubscribers.get(taskRun).removeAll(processed);
                            logger.info("Published TaskRun stale updates to all registered clients");
                        } catch (IOException e) {
                            logger.error("Exception publishing TaskRun status updates", e);
                        }
                    }
                }
            }
        });
    }

    public void registerForTaskRunStatus(TaskRun taskRun, DeferredResult<String> deferredResult) {
        taskRunStatusSubscribers.computeIfAbsent(taskRun, taskRun1 -> new Vector<>()).add(deferredResult);
    }
}