package org.shunya.server.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.shunya.server.Role;
import org.shunya.server.model.*;
import org.shunya.server.services.*;
import org.shunya.server.vo.AgentVO;
import org.shunya.server.vo.AgentVOBuilder;
import org.shunya.server.vo.LogsVO;
import org.shunya.server.vo.MultiAgentVO;
import org.shunya.shared.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.shunya.shared.FieldPropertiesMap.convertObjectToXml;

@Controller
@RequestMapping("/server")
public class ServerController {
    private static final Logger logger = LoggerFactory.getLogger(ServerController.class);

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private TaskService taskService;

    @Autowired
    private DBService dbService;

    @Autowired
    private AgentStatusService agentStatusService;

    @Autowired
    private RestClient restClient;

    @Autowired
    private AidsJobScheduler aidsJobScheduler;
    final String[] taskClasses = {"EchoStep", "DiscSpaceStep", "SystemCommandStep", "FileUploadStep", "HttpDownloadStep", "MultiHttpDownloadStep", "DeclareVariableStep", "UpdateDBStep", "EmailStep", "UnZipStep", "MultiUnZipStep", "ZipStep", "TokenReplaceStep"};

    @RequestMapping(value = "test", method = RequestMethod.GET)
    public String test(@ModelAttribute("model") ModelMap model) {
        return "test";
    }

    @RequestMapping(value = {"", "index"}, method = RequestMethod.GET)
    public String index(@ModelAttribute("model") ModelMap model) {
        Team team = (Team) request.getSession().getAttribute("SELECTED_TEAM");
        if (team != null) {
            return "redirect:/server/team/" + team.getId() + "/tasks";
        } else {
            return "redirect:/admin/team/index";
        }
    }

    @RequestMapping(value = "team/{teamId}/agents", method = RequestMethod.GET)
    public String agents(@ModelAttribute("model") ModelMap model, Principal principal, @PathVariable("teamId") long teamId) {
        Team team = dbService.findTeamById(teamId);
        request.getSession().setAttribute("SELECTED_TEAM", team);
//        System.out.println("principal = " + principal.getName());
        model.addAttribute("username", principal.getName());
        List<Agent> agentList = dbService.listAgentsByTeam(teamId);
        List<AgentVO> agentVOList = new ArrayList<>(agentList.size());
        agentList.forEach(agent -> {
            agentVOList.add(AgentVOBuilder.anAgentVO().withName(agent.getName())
                    .withId(agent.getId())
                    .withDescription(agent.getDescription())
                    .withBaseUrl(agent.getBaseUrl())
                    .withStatus(agentStatusService.getStatus(agent))
                    .build());
        });
        model.addAttribute("agents", agentVOList);
        return "agents";
    }

    @RequestMapping(value = "agent/checkStatus", method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void forceCheckAgentStatus() {
        logger.info("Triggering Agent Status Check on Demand");
        agentStatusService.checkStatus();
    }

    @RequestMapping(value = "agent/status/{agentId}", method = RequestMethod.GET)
    @ResponseBody
    public String agentStatus(@ModelAttribute("model") ModelMap model, @PathVariable("agentId") long agentId) {
        Agent agent = new Agent();
        agent.setId(agentId);
        return agentStatusService.getStatus(agent).toString();
    }

    @RequestMapping(value = "team/{teamId}/tasks", method = RequestMethod.GET)
    public String index(@ModelAttribute("model") ModelMap model, @PathVariable("teamId") long teamId) {
        model.addAttribute("message", "Hello world!");
        model.addAttribute("tasks", dbService.listTasksByTeam(teamId));
        model.addAttribute("team", dbService.findTeamById(teamId));
        return "tasks";
    }

    @RequestMapping(value = "team/{teamId}/register", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public String registerAgent(@ModelAttribute("agent") Agent agent, Principal principal, @PathVariable("teamId") long teamId) {
        agent.setCreatedBy(dbService.findUserByUsername(principal.getName()));
        agent.setTeam(dbService.findTeamById(teamId));
        dbService.save(agent);
        final String referer = request.getHeader("referer");
        System.out.println("referer = " + referer);
        return "redirect:" + referer;
    }

    @RequestMapping(value = "ping", method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void ping() {
        logger.debug("ping success");
    }

    @RequestMapping(value = "/editAgent/{id}", method = RequestMethod.GET)
    public String editAgent(@ModelAttribute("model") ModelMap model, @PathVariable("id") long id) throws Exception {
        Agent agent = dbService.getAgent(id);
        model.addAttribute("agent", agent);
        return "editAgent";
    }

    @RequestMapping(value = "/editAgent/{id}", method = RequestMethod.POST)
    public String editAgent(@PathVariable("id") long id, @ModelAttribute("agent") Agent agentDTO, final HttpServletRequest request) {
        logger.info("Message received for update Agent : " + agentDTO);
        if (id != 0) {
            final Agent dbAgent = dbService.getAgent(id);
            dbAgent.setName(agentDTO.getName());
            dbAgent.setDescription(agentDTO.getDescription());
            dbAgent.setBaseUrl(agentDTO.getBaseUrl());
            dbAgent.setAgentProperties(agentDTO.getAgentProperties());
            dbService.save(dbAgent);
        }
        final String referer = request.getHeader("referer");
        System.out.println("referer = " + referer);
        return "redirect:" + referer;
    }

    @RequestMapping(value = "agent/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void deleteAgent(@PathVariable("id") long id, @ModelAttribute("model") ModelMap model) throws IOException {
        if (id != 0) {
            dbService.deleteAgent(id);
        }
    }

    @RequestMapping(value = "team/{teamId}/editTask/{id}", method = RequestMethod.GET)
    public String editTask(@ModelAttribute("model") ModelMap model, @PathVariable("id") long id, @PathVariable("teamId") long teamId) throws Exception {
        model.addAttribute("task", dbService.getTask(id));
        model.addAttribute("team", dbService.findTeamById(teamId));
        return "editTask";
    }

    @RequestMapping(value = "/viewLogs/{taskStepId}", method = RequestMethod.GET)
    @ResponseBody
    public String viewTaskStepLogs(@ModelAttribute("model") ModelMap model, @PathVariable("taskStepId") long taskStepId) throws Exception {
        TaskStepRun taskStepRun = dbService.getTaskStepRun(taskStepId);
        return taskStepRun.getLogs();
    }

    @RequestMapping(value = "team/{teamId}/addAgent/{id}", method = RequestMethod.GET)
    public String addAgent(@ModelAttribute("model") ModelMap model, @PathVariable("id") long id, @PathVariable("teamId") long teamId) throws Exception {
        model.addAttribute("agents", dbService.listAgentsByTeam(teamId));
        model.addAttribute("task.id", id);
        model.addAttribute("selectedAgents", dbService.getTask(id).getAgentList());
        return "addAgent";
    }

    @RequestMapping(value = "addAgent/{taskId}", method = RequestMethod.POST)
    public String addAgentPOST(@ModelAttribute("multiAgentVO") MultiAgentVO multiAgentVO, @PathVariable("taskId") long taskId) throws Exception {
        Task task = dbService.getTask(taskId);
        multiAgentVO.getAgents().forEach(agentId -> task.getAgentList().add(dbService.getAgent(agentId)));
        dbService.save(task);
        final String referer = request.getHeader("referer");
        System.out.println("referer = " + referer);
        return "redirect:" + referer;
    }

    @RequestMapping(value = "removeAgent/{taskId}/{agentId}", method = RequestMethod.POST)
    public String removeAgent(@ModelAttribute("agent") Agent agent, @PathVariable("taskId") long taskId, @PathVariable("agentId") long agentId) throws Exception {
        Task task = dbService.getTask(taskId);
        task.getAgentList().remove(dbService.getAgent(agentId));
        dbService.save(task);
        final String referer = request.getHeader("referer");
        System.out.println("referer = " + referer);
        return "redirect:" + referer;
    }

    @RequestMapping(value = "team/{teamId}/taskStep/addAgent/{taskStepId}", method = RequestMethod.GET)
    public String addTaskStepAgent(@ModelAttribute("model") ModelMap model, @PathVariable("taskStepId") long taskStepId, @PathVariable("teamId") long teamId) throws Exception {
        model.addAttribute("agents", dbService.listAgentsByTeam(teamId));
        model.addAttribute("taskStepId", taskStepId);
        TaskStep taskStep = dbService.getTaskStep(taskStepId);
        model.addAttribute("taskStep", taskStep);
        model.addAttribute("selectedAgents", taskStep.getAgentList());
        return "addTaskStepAgent";
    }

    @RequestMapping(value = "taskStep/addAgent/{taskStepId}", method = RequestMethod.POST)
    public String addTaskStepAgentPOST(@ModelAttribute("multiAgentVO") MultiAgentVO multiAgentVO, @PathVariable("taskStepId") long taskStepId) throws Exception {
        TaskStep taskStep = dbService.getTaskStep(taskStepId);
        multiAgentVO.getAgents().forEach(agent -> taskStep.getAgentList().add(dbService.getAgent(agent)));
        dbService.save(taskStep);
        return "redirect:../../task/" + taskStep.getTask().getId();
    }

    @RequestMapping(value = "taskStep/removeAgent/{taskStepId}/{agentId}", method = RequestMethod.POST)
    public String removeTaskStepAgent(@ModelAttribute("agent") Agent agent, @PathVariable("taskStepId") long taskStepId, @PathVariable("agentId") long agentId) throws Exception {
        TaskStep taskStepData = dbService.getTaskStep(taskStepId);
        taskStepData.getAgentList().remove(dbService.getAgent(agentId));
        dbService.save(taskStepData);
        final String referer = request.getHeader("referer");
        System.out.println("referer = " + referer);
        return "redirect:" + referer;
    }

    @RequestMapping(value = "team/{teamId}/addTask/{id}", method = RequestMethod.POST)
    public String addTask(@PathVariable("id") long id, @ModelAttribute("user") Task task, final HttpServletRequest request, Principal principal, @PathVariable("teamId") long teamId) {
        logger.info("Message received for Add : " + task);
        if (id != 0) {
            final Task existingTask = dbService.getTask(id);
            existingTask.setName(task.getName());
            existingTask.setDescription(task.getDescription());
            existingTask.setSchedule(task.getSchedule());
            existingTask.setTags(task.getTags());
            existingTask.setTaskProperties(task.getTaskProperties());
            dbService.save(existingTask);
        } else if (null != task && null != task.getName() && !task.getDescription().isEmpty()) {
            task.setAuthor(dbService.findUserByUsername(principal.getName()));
            task.setTeam(dbService.findTeamById(teamId));
//          taskData.setTradeDate(new Date());
            dbService.save(task);
        }
        final String referer = request.getHeader("referer");
        System.out.println("referer = " + referer);
        return "redirect:" + referer;
    }

    @RequestMapping(value = "cloneTask/{taskId}", method = RequestMethod.POST)
    public String cloneTask(@PathVariable("taskId") long taskId,
                            final HttpServletRequest request,
                            @RequestParam(value = "taskName", defaultValue = "clonedTask", required = false) String taskName,
                            Principal principal) {
        logger.info("Message received for cloning an existing task");
        if (taskId != 0) {
            dbService.cloneTask(taskId, taskName, principal.getName());
        }
        final String referer = request.getHeader("referer");
        System.out.println("referer = " + referer);
        return "redirect:" + referer;
    }

    @RequestMapping(value = "updateTaskStep", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void updateTaskStep(@ModelAttribute("taskStepData") TaskStep taskStepData) throws IOException {
        TaskStep dbTaskStepData = dbService.getTaskStep(taskStepData.getId());
        dbTaskStepData.setActive(taskStepData.isActive());
        dbService.save(dbTaskStepData);
    }

    @RequestMapping(value = "updateTaskStep2", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void updateTaskStep2(@ModelAttribute("taskStepData") TaskStep taskStepData) throws IOException {
        TaskStep dbTaskStepData = dbService.getTaskStep(taskStepData.getId());
        dbTaskStepData.setIgnoreFailure(taskStepData.isIgnoreFailure());
        dbService.save(dbTaskStepData);
    }

    @RequestMapping(value = "task/{id}", method = RequestMethod.GET)
    public String viewTaskDetails(@ModelAttribute("model") ModelMap model, @PathVariable("id") long id, final HttpServletRequest request, Principal principal) {
        Task task = dbService.getTask(id);
        final User user = dbService.findUserByUsername(principal.getName());
        final List<Long> authorisedTeams = user.getTeamList().stream().map(Team::getId).collect(toList());
        if (!authorisedTeams.contains(task.getTeam().getId())) {
            throw new AccessDeniedException("User " + user.getName() + " is not authorised for this content");
        }
//        task.setStepDataList(task.getStepDataList().stream().sorted(Comparator.comparing(TaskStepData::getSequence)).collect(toList()));
        final String referer = request.getHeader("referer");
        model.addAttribute("task", task);
        model.addAttribute("taskClasses", taskClasses);
        model.addAttribute("referer", referer);
        return "taskSteps";
    }

    @RequestMapping(value = "export/task/{id}", method = RequestMethod.GET)
    public void downloadTaskAsJson(@ModelAttribute("model") ModelMap model, @PathVariable("id") long id, HttpServletResponse response) {
        try {
            Task task = dbService.getTask(id);
            ObjectMapper mapper = new ObjectMapper();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mapper.writeValue(baos, asList(task));
//            response.setContentType("application/json");
            response.setContentType("application/force-download");
            response.setContentLength(baos.size());
            //response.setContentLength(-1);
            response.setHeader("Content-Transfer-Encoding", "binary");
            String headerKey = "Content-Disposition";
            String headerValue = String.format("attachment; filename=\"%s\"", task.getName() + ".json");
            response.setHeader(headerKey, headerValue);
            IOUtils.copy(new ByteArrayInputStream(baos.toByteArray()), response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "export/agents", method = RequestMethod.GET)
    public void downloadAgentsAsJson(@ModelAttribute("model") ModelMap model, HttpServletResponse response) {
        try {
            final List<Agent> agents = dbService.listAgents();
            ObjectMapper mapper = new ObjectMapper();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mapper.writeValue(baos, agents);
//            response.setContentType("application/json");
            response.setContentType("application/force-download");
            response.setContentLength(baos.size());
            //response.setContentLength(-1);
            response.setHeader("Content-Transfer-Encoding", "binary");
            String headerKey = "Content-Disposition";
            String headerValue = String.format("attachment; filename=\"%s\"", "aids-agents" + ".json");
            response.setHeader(headerKey, headerValue);
            IOUtils.copy(new ByteArrayInputStream(baos.toByteArray()), response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "export/tasks", method = RequestMethod.GET)
    public void downloadTasksAsJson(@ModelAttribute("model") ModelMap model, HttpServletResponse response) {
        try {
            final List<Task> tasks = dbService.listAllTasks();
            ObjectMapper mapper = new ObjectMapper();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mapper.writeValue(baos, tasks);
//            response.setContentType("application/json");
            response.setContentType("application/force-download");
            response.setContentLength(baos.size());
            //response.setContentLength(-1);
            response.setHeader("Content-Transfer-Encoding", "binary");
            String headerKey = "Content-Disposition";
            String headerValue = String.format("attachment; filename=\"%s\"", "aids-tasks" + ".json");
            response.setHeader(headerKey, headerValue);
            IOUtils.copy(new ByteArrayInputStream(baos.toByteArray()), response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "view/task/{id}", method = RequestMethod.GET)
    public void viewTaskAsJson(@ModelAttribute("model") ModelMap model, @PathVariable("id") long id, HttpServletResponse response) {
        try {
            Task task = dbService.getTask(id);
            ObjectMapper mapper = new ObjectMapper();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mapper.writeValue(baos, task);
            response.setContentType("application/json");
            // set headers for the response
            String headerKey = "Content-Disposition";
            String headerValue = String.format("inline; filename=\"%s\"", task.getName() + ".json");
            response.setHeader(headerKey, headerValue);
            IOUtils.copy(new ByteArrayInputStream(baos.toByteArray()), response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "team/{teamId}/taskUpload", method = RequestMethod.POST)
    public String importTaskAsJson(@RequestParam MultipartFile file, ModelMap model, @PathVariable("teamId") long teamId, Principal principal) throws IOException {
        model.addAttribute("message", "File '" + file.getOriginalFilename() + "' uploaded successfully");
        //2. Convert JSON to Java object
        ObjectMapper mapper = new ObjectMapper();
        List<Task> tasks = mapper.readValue(file.getInputStream(), new TypeReference<List<Task>>() {
        });
        tasks.forEach(task -> {
            task.setAuthor(dbService.findUserByUsername(principal.getName()));
            task.setTeam(dbService.findTeamById(teamId));
            List<TaskStep> stepDataList = task.getStepDataList();
            for (TaskStep taskStep : stepDataList) {
                taskStep.setTask(task);
            }
            dbService.save(task);
            System.out.println("task = " + task);
        });
        return "redirect:tasks";
    }

    @RequestMapping(value = "team/{teamId}/agentsUpload", method = RequestMethod.POST)
    public String importAgentsAsJson(@RequestParam MultipartFile file, ModelMap model, @PathVariable("teamId") long teamId, Principal principal) throws IOException {
        model.addAttribute("message", "File '" + file.getOriginalFilename() + "' uploaded successfully");
        ObjectMapper mapper = new ObjectMapper();
        List<Agent> agents = mapper.readValue(file.getInputStream(), new TypeReference<List<Agent>>() {
        });
        agents.forEach(agent -> {
            agent.setCreatedBy(dbService.findUserByUsername(principal.getName()));
            agent.setTeam(dbService.findTeamById(teamId));
            dbService.save(agent);
            logger.info("agent imported successfully = " + agent.getName());
        });
        return "redirect:agents";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String editTaskStep(@ModelAttribute("model") ModelMap model, @PathVariable("id") long id) throws Exception {
        TaskStep stepData = dbService.getTaskStep(id);
        model.addAttribute("stepData", stepData);
        model.addAttribute("taskClasses", asList(taskClasses));
//        FieldPropertiesMap inPropertiesMap = TaskStep.listInputParams(Class.forName(stepData.getClassName()), parseStringMap(stepData.getInputParams()));
        FieldPropertiesMap inPropertiesMap = FieldPropertiesMap.convertXmlToObject(stepData.getInputParams());
        FieldPropertiesMap outPropertiesMap = FieldPropertiesMap.convertXmlToObject(stepData.getOutputParams());
        model.addAttribute("inputParams", inPropertiesMap.values());
        model.addAttribute("outputParams", outPropertiesMap.values());
        Class<?> task = Class.forName("org.shunya.shared.taskSteps." + stepData.getTaskClass());
        model.addAttribute("taskMetadata", AbstractStep.getTaksStepMetaData(task));
        return "editTaskStep";
    }

    @RequestMapping(value = "/addTaskStep/{taskId}", method = RequestMethod.GET)
    public String addTaskStep(@ModelAttribute("model") ModelMap model, @PathVariable("taskId") long taskId, @RequestParam(value = "taskClass", required = false, defaultValue = "EchoStep") String taskClass) throws Exception {
        model.addAttribute("taskId", taskId);
        if (taskClass == null || taskClass.isEmpty())
            taskClass = taskClasses[0];
        //TODO - Add Task Registry for managing task classes
        Class<?> task = Class.forName("org.shunya.shared.taskSteps." + taskClass);
        FieldPropertiesMap inPropertiesMap = AbstractStep.listInputParams(task, task.newInstance());
        FieldPropertiesMap outPropertiesMap = AbstractStep.listOutputParams(task, Collections.<String, String>emptyMap());
        model.addAttribute("selectedClass", taskClass);
        model.addAttribute("taskClasses", taskClasses);
        model.addAttribute("inputParams", inPropertiesMap.values());
        model.addAttribute("outputParams", outPropertiesMap.values());
        model.addAttribute("taskMetadata", AbstractStep.getTaksStepMetaData(task));
        model.addAttribute("referer", request.getHeader("referer"));
        return "addTaskStep";
    }

    @RequestMapping(value = "/addTaskStep/{id}", method = RequestMethod.POST)
    public String addTaskStep(@PathVariable("id") long id, @ModelAttribute("taskStepDTO") TaskStepDTO taskStepDTO) throws Exception {
        if (id != 0) {
            TaskStep existingTaskStep = dbService.getTaskStep(id);
            existingTaskStep.setId(id);
            existingTaskStep.setSequence(taskStepDTO.getSequence());
            existingTaskStep.setDescription(taskStepDTO.getDescription());
            existingTaskStep.setInputParams(convertObjectToXml(AbstractStep.listInputParams(Class.forName("org.shunya.shared.taskSteps." + existingTaskStep.getTaskClass()), taskStepDTO.getInputParamsMap())));
            existingTaskStep.setOutputParams(convertObjectToXml(AbstractStep.listOutputParams(Class.forName("org.shunya.shared.taskSteps." + existingTaskStep.getTaskClass()), taskStepDTO.getOutputParamsMap())));
            dbService.save(existingTaskStep);
        } else {
            TaskStep newTaskStep = new TaskStep();
            newTaskStep.setSequence(taskStepDTO.getSequence());
            newTaskStep.setDescription(taskStepDTO.getDescription());
            newTaskStep.setTaskClass(taskStepDTO.getTaskClass());
            newTaskStep.setInputParams(convertObjectToXml(AbstractStep.listInputParams(Class.forName("org.shunya.shared.taskSteps." + taskStepDTO.getTaskClass()), taskStepDTO.getInputParamsMap())));
            newTaskStep.setOutputParams(convertObjectToXml(AbstractStep.listOutputParams(Class.forName("org.shunya.shared.taskSteps." + taskStepDTO.getTaskClass()), taskStepDTO.getOutputParamsMap())));
            newTaskStep.setTask(dbService.getTask(taskStepDTO.getTaskId()));
            dbService.save(newTaskStep);
        }
        return "redirect:../task/" + taskStepDTO.getTaskId();
    }

    @RequestMapping(value = "/deleteStep/{taskStepId}", method = RequestMethod.POST)
    public String deleteStep(@PathVariable("taskStepId") long id, @ModelAttribute("stepData") TaskStep stepData) throws IOException {
        if (id != 0) {
            dbService.deleteTaskStep(id);
        }
        final String referer = request.getHeader("referer");
        System.out.println("referer = " + referer);
        return "redirect:" + referer;
    }

    @RequestMapping(value = "delete/{taskId}", method = RequestMethod.POST)
    public String deleteTask(@PathVariable("taskId") long id, @ModelAttribute("model") ModelMap model) throws IOException {
        if (id != 0) {
            dbService.deleteTask(id);
        }
        final String referer = request.getHeader("referer");
        System.out.println("referer = " + referer);
        return "redirect:" + referer;
    }

    @RequestMapping(value = "deleteStepRun/{id}", method = RequestMethod.POST)
    public String deleteStepRun(@PathVariable("id") long id, @ModelAttribute("model") ModelMap model) throws IOException {
        if (id != 0) {
            dbService.deleteTaskStepRun(id);
        }
        final String referer = request.getHeader("referer");
        System.out.println("referer = " + referer);
        return "redirect:" + referer;
    }

    @RequestMapping(value = "deleteTaskRun/{id}", method = RequestMethod.POST)
    public String deleteTaskRun(@PathVariable("id") long id, @ModelAttribute("model") ModelMap model) throws IOException {
        if (id != 0) {
            dbService.deleteTaskRun(id);
        }
        final String referer = request.getHeader("referer");
        System.out.println("referer = " + referer);
        return "redirect:" + referer;
    }

    @RequestMapping(value = "taskHistory/{taskId}", method = RequestMethod.GET)
    public String viewTaskHistoryForTask(@ModelAttribute("model") ModelMap model, @PathVariable("taskId") long taskId) {
        List<TaskRun> all = dbService.findTaskHistoryForTaskId(taskId);
        model.addAttribute("taskHistoryList", all);
        model.addAttribute("task", dbService.getTask(taskId));
        model.addAttribute("taskId", taskId);
        return "taskRun";
    }

    @RequestMapping(value = "team/{teamId}/taskHistory", method = RequestMethod.GET)
    public String viewTaskHistory(@ModelAttribute("model") ModelMap model, @PathVariable("teamId") long teamId) {
        List<TaskRun> all = dbService.findTaskHistoryByTeam(teamId);
        model.addAttribute("taskHistoryList", all);
        return "taskRunAll";
    }

    @RequestMapping(value = "/taskStepHistory/{taskHistoryId}", method = RequestMethod.GET)
    public String viewTaskStepHistory(@ModelAttribute("model") ModelMap model, @PathVariable("taskHistoryId") long taskHistoryId) {
        TaskRun taskHistory = dbService.getTaskRun(taskHistoryId);
        model.addAttribute("taskHistory", taskHistory);
        return "taskStepRun";
    }

    @RequestMapping(value = "run/{taskId}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    public List<TaskRun> runTask(@PathVariable("taskId") Long taskId,
                                 @RequestParam(defaultValue = "test", required = false) String comment,
                                 @RequestParam(defaultValue = "", required = false) String properties,
                                 @RequestParam(defaultValue = "3", required = false) int loggingLevel,
                                 @RequestParam(defaultValue = "false", required = false) boolean notifyStatus,
                                 Principal principal) {

        logger.info("Run request for {}, user comments {}", taskId, comment);
        logger.info("Custom Properties {}", properties);
        Task task = dbService.getTask(taskId);
        final User user = dbService.findUserByUsername(principal.getName());
        final List<Long> authorisedTeams = user.getTeamList().stream().map(Team::getId).collect(toList());
        if (!authorisedTeams.contains(task.getTeam().getId())) {
            throw new AccessDeniedException("User " + user.getName() + " is not allowed to run task from Team : " + task.getTeam().getName());
        }
        List<TaskRun> taskRuns = new ArrayList<>();
        if (!task.getAgentList().isEmpty())
            task.getAgentList().forEach(agent -> taskRuns.add(taskService.createTaskRun(comment, notifyStatus, principal, task, agent, false, properties, loggingLevel)));
        else
            taskRuns.add(taskService.createTaskRun(comment, notifyStatus, principal, task, null, true, properties, loggingLevel));
        return taskRuns;
    }


    @RequestMapping(value = "runSingleStep/{taskId}/{taskStepId}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    public TaskRun runTaskWithSingleStep(@PathVariable("taskId") Long taskId,
                                         @RequestParam(defaultValue = "test", required = false) String comment,
                                         @RequestParam(defaultValue = "false", required = false) boolean notifyStatus,
                                         Principal principal, @PathVariable("taskStepId") long taskStepId) {
        logger.info("Run request for {}, user comments ", taskId, comment);
        //TODO implement logging level taken from UI
        Task task = dbService.getTask(taskId);
        task.getStepDataList().forEach(taskStep -> {
            if (taskStep.getId() != taskStepId)
                taskStep.setActive(false);
            else
                taskStep.setActive(true);
        });
        TaskRun taskRun = new TaskRun();
        taskRun.setTask(task);
        taskRun.setName(task.getName());
        taskRun.setStartTime(new Date());
        taskRun.setComments(comment);
        taskRun.setNotifyStatus(notifyStatus);
        taskRun.setRunBy(dbService.findUserByUsername(principal.getName()));
        taskRun.setTeam(task.getTeam());
        dbService.save(taskRun);
        taskService.execute(taskRun, new HashMap<>(), true, 4);
        return taskRun;
    }

    @RequestMapping(value = "cancel/{taskRunId}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    public String cancelTaskRun(@PathVariable("taskRunId") Long taskRunId,
                                @RequestParam(defaultValue = "test", required = false) String comment,
                                Principal principal) {
        logger.info("Cancel request for {}, user comments ", taskRunId, comment);
        String name = principal.getName();
        taskService.cancelTaskRun(dbService.getTaskRun(taskRunId));
        return "Request Submitted for Task cancellation";
    }

    @RequestMapping(value = "team", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String changeTeam(@RequestParam("teamId") Long teamId, Principal principal) {
        logger.info("Current Team changed to {}", teamId);
        Team team = dbService.findTeamById(teamId);
        request.getSession().setAttribute("SELECTED_TEAM", team);
        final String referer = request.getHeader("referer");
        System.out.println("referer = " + referer);
        return "redirect:/user/profile/" + principal.getName();
    }

    @RequestMapping(value = "team/{teamId}/settings", method = RequestMethod.GET)
    public String teamSettings(@ModelAttribute("model") ModelMap model, @PathVariable("teamId") long teamId, Principal principal) {
        model.addAttribute("team", dbService.findTeamById(teamId));
        model.addAttribute("user", dbService.findUserByUsername(principal.getName()));
        return "teamSettings";
    }

    @RequestMapping(value = "team/{teamId}/settings", method = RequestMethod.POST)
    public String updateTeamSettings(@ModelAttribute("team") Team team, @PathVariable("teamId") long teamId, Principal principal) {
        Team teamById = dbService.findTeamById(teamId);
        teamById.setName(team.getName());
        teamById.setDescription(team.getDescription());
        teamById.setTelegramId(team.getTelegramId());
        teamById.setTeamProperties(team.getTeamProperties());
        dbService.save(teamById);
        return "redirect:settings";
    }

    @RequestMapping(value = "submitTaskStepResults", method = RequestMethod.POST)
    @ResponseBody
    @Secured({Role.ROLE_AGENT})
    public String submitTaskStepResults(@RequestBody TaskContext taskContext) {
        logger.info("Successfully received Task results {} ", taskContext.getStepDTO().getSequence() + ", Status - " + taskContext.getTaskStepRunDTO().getRunStatus() + " baseUrl - " + taskContext.getBaseUrl());
        taskService.consumeStepResult(taskContext);
        return "success";
    }

    @RequestMapping(value = "getMemoryLogs/{stepRunId}", method = RequestMethod.GET)
    @ResponseBody
    public LogsVO getStepLogs(@ModelAttribute("model") ModelMap model, @PathVariable("stepRunId") long taskRunId, @RequestParam("start") long start) throws Exception {
        TaskStepRun taskStepRun = dbService.getTaskStepRun(taskRunId);
        String logs = "";
        LogsVO logsVO = new LogsVO();
        if (taskStepRun.getRunState() == RunState.RUNNING) {
            logs = restClient.getMemoryLogs(taskStepRun.getId(), taskStepRun.getAgent(), start);
            if (logs == null)
                logs = "";
            if (logs.equalsIgnoreCase("finished")) {
                logs = taskStepRun.getLogs();
                logsVO.setStatus("FINISHED");
            }
        } else {
            logs = taskStepRun.getLogs().substring((int) Math.min(taskStepRun.getLogs().length(), start));
            logsVO.setStatus("FINISHED");
        }
        logsVO.setLogs(logs);
        return logsVO;
    }

    @RequestMapping(value = "getMemoryLogs/view/{stepRunId}", method = RequestMethod.GET)
    public String getStepLogView(@ModelAttribute("model") ModelMap model, @PathVariable("stepRunId") long stepRunId) throws Exception {
        TaskStepRun taskStepRun = dbService.getTaskStepRun(stepRunId);
        model.addAttribute("taskStepRun", taskStepRun);
        return "tailLogs";
    }

    @RequestMapping(value = "taskRunMonitor", method = RequestMethod.GET)
    @ResponseBody
    public DeferredResult<String> taskRunMonitor(@RequestParam(value = "taskRunId", required = false, defaultValue = "0") long taskRunId,
                                                 @RequestParam(value = "cacheId", required = false, defaultValue = "0") long cacheId,
                                                 HttpServletResponse response) throws IOException, InterruptedException {
        DeferredResult<String> deferredResult = new DeferredResult<>();
        TaskRun taskRun = dbService.getTaskRun(taskRunId);
        int serverCacheId = taskService.getCacheId(taskRun);
        if (taskRun != null && (taskRun.getRunState() == RunState.COMPLETED || cacheId == 0 || cacheId < serverCacheId)) {
            taskRun.setCacheId(serverCacheId);
            ObjectMapper mapper = new ObjectMapper();
//            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mapper.writeValue(baos, taskRun);
            deferredResult.setResult(baos.toString());
        } else {
            taskService.registerForTaskRunStatus(taskRun, cacheId, deferredResult);
        }
        return deferredResult;
    }

    @RequestMapping(value = "taskRun/view/{taskRunId}", method = RequestMethod.GET)
    public String getTaskRunView(@ModelAttribute("model") ModelMap model, @PathVariable("taskRunId") long taskRunId) throws Exception {
        model.addAttribute("taskRun", dbService.getTaskRun(taskRunId));
        return "tailTaskRun";
    }

    @RequestMapping(value = "schedule", method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<String> schedulePrediction(@ModelAttribute("model") ModelMap model, @RequestParam("cronString") String cronString) {
        return aidsJobScheduler.predict(cronString, 10);
    }

    @RequestMapping(value = "shutdown", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void shutdownCompact(@ModelAttribute("model") ModelMap model) {
        dbService.shutdownCompact();
    }
}