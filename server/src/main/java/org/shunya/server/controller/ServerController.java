package org.shunya.server.controller;

import org.shunya.server.TaskService;
import org.shunya.server.services.AgentService;
import org.shunya.server.services.MyJobScheduler;
import org.shunya.shared.FieldPropertiesMap;
import org.shunya.shared.TaskContext;
import org.shunya.shared.TaskStep;
import org.shunya.shared.TaskStepDTO;
import org.shunya.shared.model.Agent;
import org.shunya.shared.model.TaskData;
import org.shunya.shared.model.TaskRun;
import org.shunya.shared.model.TaskStepData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.security.Principal;
import java.util.*;

import static org.shunya.shared.FieldPropertiesMap.convertObjectToXml;
import static org.shunya.shared.FieldPropertiesMap.parseStringMap;

@Controller
@RequestMapping("/server")
public class ServerController {
    private static final Logger logger = LoggerFactory.getLogger(ServerController.class);

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private TaskService taskService;

    @Autowired
    private AgentService agentService;

    @Autowired
    private MyJobScheduler myJobScheduler;

    final String[] taskClasses = {"EchoTaskStep", "DiscSpaceTaskStep", "SystemCommandTask"};

    @RequestMapping(value = "agents", method = RequestMethod.GET)
    public String agents(@ModelAttribute("model") ModelMap model) {
        model.addAttribute("message", "Hello world!");
        model.addAttribute("agents", agentService.list());
        return "agents";
    }

    @RequestMapping(value = {"index", ""}, method = RequestMethod.GET)
    public String index(@ModelAttribute("model") ModelMap model) {
        model.addAttribute("message", "Hello world!");
        model.addAttribute("taskDatas", agentService.listTasks());
        return "tasks";
    }

    @RequestMapping(value = "register", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public String registerAgent(@ModelAttribute("agent") Agent agent) {
        agentService.save(agent);
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
        Agent agent = agentService.getAgent(id);
        model.addAttribute("agent", agent);
        return "editAgent";
    }

    @RequestMapping(value = "/editAgent/{id}", method = RequestMethod.POST)
    public String editAgent(@PathVariable("id") long id, @ModelAttribute("agent") Agent agentDTO, final HttpServletRequest request) {
        logger.info("Message received for update Agent : " + agentDTO);
        if (id != 0) {
            final Agent dbAgent = agentService.getAgent(id);
            dbAgent.setName(agentDTO.getName());
            dbAgent.setDescription(agentDTO.getDescription());
            dbAgent.setBaseUrl(agentDTO.getBaseUrl());
            agentService.save(dbAgent);
        }
        final String referer = request.getHeader("referer");
        System.out.println("referer = " + referer);
        return "redirect:" + referer;
    }

    @RequestMapping(value = "/editTask/{id}", method = RequestMethod.GET)
    public String editTask(@ModelAttribute("model") ModelMap model, @PathVariable("id") long id) throws Exception {
        TaskData taskData = agentService.getTaskData(id);
        model.addAttribute("task", taskData);
        return "editTask";
    }

    @RequestMapping(value = "addAgent/{id}", method = RequestMethod.GET)
    public String addAgent(@ModelAttribute("model") ModelMap model, @PathVariable("id") long id) throws Exception {
        model.addAttribute("agents", agentService.list());
        model.addAttribute("task.id", id);
        return "addAgent";
    }

    @RequestMapping(value = "addAgent/{taskId}", method = RequestMethod.POST)
    public String addAgentPOST(@ModelAttribute("agent") Agent agent, @PathVariable("taskId") long taskId) throws Exception {
        TaskData taskData = agentService.getTaskData(taskId);
        taskData.getAgentList().add(agentService.getAgent(agent.getId()));
        agentService.save(taskData);
        return "redirect:../index";
    }

    @RequestMapping(value = "removeAgent/{taskId}/{agentId}", method = RequestMethod.POST)
    public String removeAgent(@ModelAttribute("agent") Agent agent, @PathVariable("taskId") long taskId, @PathVariable("agentId") long agentId) throws Exception {
        TaskData taskData = agentService.getTaskData(taskId);
        taskData.getAgentList().remove(agentService.getAgent(agentId));
        agentService.save(taskData);
        return "redirect:../../index";
    }

    @RequestMapping(value = "taskStep/addAgent/{taskStepId}", method = RequestMethod.GET)
    public String addTaskStepAgent(@ModelAttribute("model") ModelMap model, @PathVariable("taskStepId") long taskStepId) throws Exception {
        model.addAttribute("agents", agentService.list());
        model.addAttribute("taskStepId", taskStepId);
        return "addTaskStepAgent";
    }

    @RequestMapping(value = "taskStep/addAgent/{taskStepId}", method = RequestMethod.POST)
    public String addTaskStepAgentPOST(@ModelAttribute("agent") Agent agent, @PathVariable("taskStepId") long taskStepId) throws Exception {
        TaskStepData taskStepData = agentService.getTaskStepData(taskStepId);
        taskStepData.getAgentList().add(agentService.getAgent(agent.getId()));
        agentService.save(taskStepData);
        return "redirect:../../view/" + taskStepData.getTaskData().getId();
    }

    @RequestMapping(value = "taskStep/removeAgent/{taskStepId}/{agentId}", method = RequestMethod.POST)
    public String removeTaskStepAgent(@ModelAttribute("agent") Agent agent, @PathVariable("taskStepId") long taskStepId, @PathVariable("agentId") long agentId) throws Exception {
        TaskStepData taskStepData = agentService.getTaskStepData(taskStepId);
        taskStepData.getAgentList().remove(agentService.getAgent(agentId));
        agentService.save(taskStepData);
        final String referer = request.getHeader("referer");
        System.out.println("referer = " + referer);
        return "redirect:" + referer;
    }

    @RequestMapping(value = "/addTask/{id}", method = RequestMethod.POST)
    public String addTask(@PathVariable("id") long id, @ModelAttribute("user") TaskData taskData, final HttpServletRequest request) {
        logger.info("Message received for Add : " + taskData);
        if (id != 0) {
            final TaskData dbTaskData = agentService.getTaskData(id);
            dbTaskData.setName(taskData.getName());
            dbTaskData.setDescription(taskData.getDescription());
            dbTaskData.setTags(taskData.getTags());
            agentService.save(dbTaskData);
        } else if (null != taskData && null != taskData.getName() && !taskData.getDescription().isEmpty()) {
//          taskData.setTradeDate(new Date());
            agentService.save(taskData);
        }
        final String referer = request.getHeader("referer");
        System.out.println("referer = " + referer);
        return "redirect:" + referer;
    }

    @RequestMapping(value = "updateTaskStep", method = RequestMethod.POST)
    @ResponseBody
    //we can also use this @RequestParam("active") String valueOne  ,  @ModelAttribute("active") String valueOne
    public void updateTaskStep(@ModelAttribute("taskStepData") TaskStepData taskStepData) throws IOException {
        TaskStepData dbTaskStepData = agentService.getTaskStepData(taskStepData.getId());
        dbTaskStepData.setActive(taskStepData.isActive());
        agentService.save(dbTaskStepData);
    }

    @RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
    public String viewTaskDetails(@ModelAttribute("model") ModelMap model, @PathVariable("id") long id, final HttpServletRequest request) {
        TaskData taskData = agentService.getTaskData(id);
//        taskData.setStepDataList(taskData.getStepDataList().stream().sorted(Comparator.comparing(TaskStepData::getSequence)).collect(toList()));
        final String referer = request.getHeader("referer");
        model.addAttribute("taskData", taskData);
        model.addAttribute("referer", referer);
        return "taskSteps";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String editTaskStep(@ModelAttribute("model") ModelMap model, @PathVariable("id") long id) throws Exception {
        TaskStepData stepData = agentService.getTaskStepData(id);
        model.addAttribute("stepData", stepData);
        model.addAttribute("taskClasses", taskClasses);
//        FieldPropertiesMap inPropertiesMap = TaskStep.listInputParams(Class.forName(stepData.getClassName()), parseStringMap(stepData.getInputParams()));
        FieldPropertiesMap inPropertiesMap = FieldPropertiesMap.convertXmlToObject(stepData.getInputParams());
        FieldPropertiesMap outPropertiesMap = FieldPropertiesMap.convertXmlToObject(stepData.getOutputParams());
        model.addAttribute("inputParams", inPropertiesMap.values());
        model.addAttribute("outputParams", outPropertiesMap.values());
        return "editTaskStep";
    }

    @RequestMapping(value = "/addTaskStep/{taskId}", method = RequestMethod.GET)
    public String viewTaskSteps(@ModelAttribute("model") ModelMap model, @PathVariable("taskId") long taskId, @RequestParam(value = "taskClassId", required = false, defaultValue = "0") int taskClassId) throws Exception {
        model.addAttribute("taskId", taskId);
        FieldPropertiesMap inPropertiesMap = TaskStep.listInputParams(Class.forName("org.shunya.shared.taskSteps." + taskClasses[taskClassId]), Collections.<String, String>emptyMap());
        FieldPropertiesMap outPropertiesMap = TaskStep.listOutputParams(Class.forName("org.shunya.shared.taskSteps." + taskClasses[taskClassId]), Collections.<String, String>emptyMap());
        model.addAttribute("selectedClassId", taskClassId);
        model.addAttribute("taskClasses", taskClasses);
        model.addAttribute("inputParams", inPropertiesMap.values());
        model.addAttribute("outputParams", outPropertiesMap.values());
        model.addAttribute("referer", request.getHeader("referer"));
        return "addTaskStep";
    }

    @RequestMapping(value = "/addTaskStep/{id}", method = RequestMethod.POST)
    public String addTaskStep(@PathVariable("id") long id, @ModelAttribute("taskStepDTO") TaskStepDTO taskStepDTO) throws Exception {
        if (id != 0) {
            TaskStepData taskStepData = agentService.getTaskStepData(id);
            taskStepData.setId(id);
            taskStepData.setSequence(taskStepDTO.getSequence());
            taskStepData.setDescription(taskStepDTO.getDescription());
            taskStepData.setInputParams(convertObjectToXml(TaskStep.listInputParams(Class.forName(taskStepData.getClassName()),taskStepDTO.getInputParamsMap())));
            taskStepData.setOutputParams(convertObjectToXml(TaskStep.listOutputParams(Class.forName(taskStepData.getClassName()), taskStepDTO.getOutputParamsMap())));
            agentService.save(taskStepData);
        } else {
            TaskStepData taskStepData = new TaskStepData();
            taskStepData.setSequence(taskStepDTO.getSequence());
            taskStepData.setDescription(taskStepDTO.getDescription());
            taskStepData.setClassName("org.shunya.shared.taskSteps." + taskClasses[taskStepDTO.getClassNameId()]);
            taskStepData.setInputParams(convertObjectToXml(TaskStep.listInputParams(Class.forName(taskStepData.getClassName()),taskStepDTO.getInputParamsMap())));
            taskStepData.setOutputParams(convertObjectToXml(TaskStep.listOutputParams(Class.forName(taskStepData.getClassName()), taskStepDTO.getOutputParamsMap())));
            taskStepData.setTaskData(taskStepDTO.getTaskData());
            agentService.save(taskStepData);
        }
        return "redirect:../view/" + taskStepDTO.getTaskData().getId();
    }

    @RequestMapping(value = "/deleteStep/{id}", method = RequestMethod.GET)
    public String deleteStep(@PathVariable("id") long id, @ModelAttribute("stepData") TaskStepData stepData) throws IOException {
        if (id != 0) {
            agentService.deleteTaskStep(id);
        }
        final String referer = request.getHeader("referer");
        System.out.println("referer = " + referer);
        return "redirect:" + referer;
    }

    @RequestMapping(value = "/taskHistory/{taskId}", method = RequestMethod.GET)
    public String viewTaskHistory(@ModelAttribute("model") ModelMap model, @PathVariable("taskId") long taskId) {
        List<TaskRun> all = agentService.findTaskHistoryForTaskId(taskId);
        model.addAttribute("taskHistoryList", all);
        model.addAttribute("taskId", taskId);
        return "taskHistory";
    }

    @RequestMapping(value = "/taskStepHistory/{taskHistoryId}", method = RequestMethod.GET)
    public String viewTaskStepHistory(@ModelAttribute("model") ModelMap model, @PathVariable("taskHistoryId") long taskHistoryId) {
        TaskRun taskHistory = agentService.getTaskRun(taskHistoryId);
        model.addAttribute("taskHistory", taskHistory);
        return "taskStepHistory";
    }

    @RequestMapping(value = "run/{taskId}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    public void runTaskData(@PathVariable("taskId") Long taskId, @RequestParam String comment, Principal principal) {
        logger.info("Run request for {}, user comments ", taskId, comment);
        TaskData taskData = agentService.getTaskData(taskId);
        TaskRun taskRun = new TaskRun();
        taskRun.setTaskData(taskData);
        taskRun.setName(taskData.getName());
        taskRun.setStartTime(new Date());
        taskRun.setComments(comment);
        taskService.createTaskRun(taskRun);
    }

    @RequestMapping(value = "submitTaskStepResults", method = RequestMethod.POST)
    @ResponseBody
    public String submitTaskStepResults(@RequestBody TaskContext taskContext) {
        logger.info("Successfully received Task results {} ", taskContext);
        TaskRun taskRun = agentService.getTaskRun(taskContext.getTaskStepRun());
        taskService.processNextSteps(taskRun, taskContext);
        return "success";
    }

    @RequestMapping(value = "schedule", method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void scheduleDynamic() {
        myJobScheduler.schedule("5 * * * * ?");
    }
}