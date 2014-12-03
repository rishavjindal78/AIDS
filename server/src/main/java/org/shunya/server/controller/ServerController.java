package org.shunya.server.controller;

import org.shunya.server.Role;
import org.shunya.server.model.*;
import org.shunya.server.services.AgentStatusService;
import org.shunya.server.services.TaskService;
import org.shunya.server.services.DBService;
import org.shunya.server.services.MyJobScheduler;
import org.shunya.server.vo.AgentVO;
import org.shunya.server.vo.AgentVOBuilder;
import org.shunya.shared.AbstractStep;
import org.shunya.shared.FieldPropertiesMap;
import org.shunya.shared.TaskContext;
import org.shunya.shared.TaskStepDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;
import java.util.*;

import static java.util.Arrays.asList;
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
    private MyJobScheduler myJobScheduler;
    final String[] taskClasses = {"EchoStep", "DiscSpaceStep", "SystemCommandStep", "FileUploadStep", "HttpDownloadStep", "DeclareVariableStep", "UpdateDBStep", "EmailStep"};

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
        System.out.println("principal = " + principal.getName());
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
            dbService.save(dbAgent);
        }
        final String referer = request.getHeader("referer");
        System.out.println("referer = " + referer);
        return "redirect:" + referer;
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
        return "addAgent";
    }

    @RequestMapping(value = "addAgent/{taskId}", method = RequestMethod.POST)
    public String addAgentPOST(@ModelAttribute("agent") Agent agent, @PathVariable("taskId") long taskId) throws Exception {
        Task task = dbService.getTask(taskId);
        task.getAgentList().add(dbService.getAgent(agent.getId()));
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
        return "addTaskStepAgent";
    }

    @RequestMapping(value = "taskStep/addAgent/{taskStepId}", method = RequestMethod.POST)
    public String addTaskStepAgentPOST(@ModelAttribute("agent") Agent agent, @PathVariable("taskStepId") long taskStepId) throws Exception {
        TaskStep taskStep = dbService.getTaskStep(taskStepId);
        taskStep.getAgentList().add(dbService.getAgent(agent.getId()));
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
            existingTask.setTags(task.getTags());
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

    @RequestMapping(value = "updateTaskStep", method = RequestMethod.POST)
    @ResponseBody
    //we can also use this @RequestParam("active") String valueOne  ,  @ModelAttribute("active") String valueOne
    public void updateTaskStep(@ModelAttribute("taskStepData") TaskStep taskStepData) throws IOException {
        TaskStep dbTaskStepData = dbService.getTaskStep(taskStepData.getId());
        dbTaskStepData.setActive(taskStepData.isActive());
        dbService.save(dbTaskStepData);
    }

    @RequestMapping(value = "task/{id}", method = RequestMethod.GET)
    public String viewTaskDetails(@ModelAttribute("model") ModelMap model, @PathVariable("id") long id, final HttpServletRequest request) {
        Task task = dbService.getTask(id);
//        task.setStepDataList(task.getStepDataList().stream().sorted(Comparator.comparing(TaskStepData::getSequence)).collect(toList()));
        final String referer = request.getHeader("referer");
        model.addAttribute("task", task);
        model.addAttribute("referer", referer);
        return "taskSteps";
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
        return "editTaskStep";
    }

    @RequestMapping(value = "/addTaskStep/{taskId}", method = RequestMethod.GET)
    public String viewTaskSteps(@ModelAttribute("model") ModelMap model, @PathVariable("taskId") long taskId, @RequestParam(value = "taskClass", required = false, defaultValue = "EchoStep") String taskClass) throws Exception {
        model.addAttribute("taskId", taskId);
        if (taskClass == null || taskClass.isEmpty())
            taskClass = taskClasses[0];
        //TODO - Add Task Registry for managing task classes
        FieldPropertiesMap inPropertiesMap = AbstractStep.listInputParams(Class.forName("org.shunya.shared.taskSteps." + taskClass), Collections.<String, String>emptyMap());
        FieldPropertiesMap outPropertiesMap = AbstractStep.listOutputParams(Class.forName("org.shunya.shared.taskSteps." + taskClass), Collections.<String, String>emptyMap());
        model.addAttribute("selectedClass", taskClass);
        model.addAttribute("taskClasses", taskClasses);
        model.addAttribute("inputParams", inPropertiesMap.values());
        model.addAttribute("outputParams", outPropertiesMap.values());
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

    @RequestMapping(value = "/deleteStep/{id}", method = RequestMethod.GET)
    public String deleteStep(@PathVariable("id") long id, @ModelAttribute("stepData") TaskStep stepData) throws IOException {
        if (id != 0) {
            dbService.deleteTaskStep(id);
        }
        final String referer = request.getHeader("referer");
        System.out.println("referer = " + referer);
        return "redirect:" + referer;
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.GET)
    public String deleteTask(@PathVariable("id") long id, @ModelAttribute("taskData") Task taskData) throws IOException {
        if (id != 0) {
            dbService.deleteTask(id);
        }
        final String referer = request.getHeader("referer");
        System.out.println("referer = " + referer);
        return "redirect:" + referer;
    }

    @RequestMapping(value = "taskHistory/{taskId}", method = RequestMethod.GET)
    public String viewTaskHistoryForTask(@ModelAttribute("model") ModelMap model, @PathVariable("taskId") long taskId) {
        List<TaskRun> all = dbService.findTaskHistoryForTaskId(taskId);
        model.addAttribute("taskHistoryList", all);
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
    public TaskRun runTask(@PathVariable("taskId") Long taskId,
                           @RequestParam(defaultValue = "test", required = false) String comment,
                           @RequestParam(defaultValue = "false", required = false) boolean notifyStatus,
                           Principal principal) {
        logger.info("Run request for {}, user comments ", taskId, comment);
        Task task = dbService.getTask(taskId);
        TaskRun taskRun = new TaskRun();
        taskRun.setTask(task);
        taskRun.setName(task.getName());
        taskRun.setStartTime(new Date());
        taskRun.setComments(comment);
        taskRun.setNotifyStatus(notifyStatus);
        taskRun.setRunBy(dbService.findUserByUsername(principal.getName()));
        taskRun.setTeam(task.getTeam());
        taskService.execute(taskRun);
        return taskRun;
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

    @RequestMapping(value = "submitTaskStepResults", method = RequestMethod.POST)
    @ResponseBody
    @Secured({Role.ROLE_AGENT})
    public String submitTaskStepResults(@RequestBody TaskContext taskContext) {
        logger.info("Successfully received Task results {} ", taskContext.getStepDTO().getSequence());
        taskService.consumeStepResult(taskContext);
        return "success";
    }

    @RequestMapping(value = "schedule", method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void scheduleDynamic() {
        myJobScheduler.schedule("5 * * * * ?");
    }
}