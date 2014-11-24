package org.shunya.server.controller;

import org.shunya.server.model.*;
import org.shunya.server.services.TaskService;
import org.shunya.server.services.DBService;
import org.shunya.server.services.MyJobScheduler;
import org.shunya.shared.AbstractStep;
import org.shunya.shared.FieldPropertiesMap;
import org.shunya.shared.TaskContext;
import org.shunya.shared.TaskStepDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    private DBService DBService;

    @Autowired
    private MyJobScheduler myJobScheduler;
    final String[] taskClasses = {"EchoStep", "DiscSpaceStep", "SystemCommandStep"};

    @RequestMapping(value = "test", method = RequestMethod.GET)
    public String test(@ModelAttribute("model") ModelMap model) {
        return "test";
    }

    @RequestMapping(value = "agents", method = RequestMethod.GET)
    public String agents(@ModelAttribute("model") ModelMap model) {
        model.addAttribute("message", "Hello world!");
        model.addAttribute("agents", DBService.list());
        return "agents";
    }

    @RequestMapping(value = {"index", ""}, method = RequestMethod.GET)
    public String index(@ModelAttribute("model") ModelMap model) {
        model.addAttribute("message", "Hello world!");
        model.addAttribute("taskDatas", DBService.listTasks());
        return "tasks";
    }

    @RequestMapping(value = "register", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public String registerAgent(@ModelAttribute("agent") Agent agent) {
        DBService.save(agent);
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
        Agent agent = DBService.getAgent(id);
        model.addAttribute("agent", agent);
        return "editAgent";
    }

    @RequestMapping(value = "/editAgent/{id}", method = RequestMethod.POST)
    public String editAgent(@PathVariable("id") long id, @ModelAttribute("agent") Agent agentDTO, final HttpServletRequest request) {
        logger.info("Message received for update Agent : " + agentDTO);
        if (id != 0) {
            final Agent dbAgent = DBService.getAgent(id);
            dbAgent.setName(agentDTO.getName());
            dbAgent.setDescription(agentDTO.getDescription());
            dbAgent.setBaseUrl(agentDTO.getBaseUrl());
            DBService.save(dbAgent);
        }
        final String referer = request.getHeader("referer");
        System.out.println("referer = " + referer);
        return "redirect:" + referer;
    }

    @RequestMapping(value = "/editTask/{id}", method = RequestMethod.GET)
    public String editTask(@ModelAttribute("model") ModelMap model, @PathVariable("id") long id) throws Exception {
        Task taskData = DBService.getTaskData(id);
        model.addAttribute("task", taskData);
        return "editTask";
    }

    @RequestMapping(value = "/viewLogs/{taskStepId}", method = RequestMethod.GET)
    @ResponseBody
    public String viewTaskStepLogs(@ModelAttribute("model") ModelMap model, @PathVariable("taskStepId") long taskStepId) throws Exception {
        TaskStepRun taskStepRun = DBService.getTaskStepRun(taskStepId);
        return taskStepRun.getLogs();
    }

    @RequestMapping(value = "addAgent/{id}", method = RequestMethod.GET)
    public String addAgent(@ModelAttribute("model") ModelMap model, @PathVariable("id") long id) throws Exception {
        model.addAttribute("agents", DBService.list());
        model.addAttribute("task.id", id);
        return "addAgent";
    }

    @RequestMapping(value = "addAgent/{taskId}", method = RequestMethod.POST)
    public String addAgentPOST(@ModelAttribute("agent") Agent agent, @PathVariable("taskId") long taskId) throws Exception {
        Task taskData = DBService.getTaskData(taskId);
        taskData.getAgentList().add(DBService.getAgent(agent.getId()));
        DBService.save(taskData);
        return "redirect:../index";
    }

    @RequestMapping(value = "removeAgent/{taskId}/{agentId}", method = RequestMethod.POST)
    public String removeAgent(@ModelAttribute("agent") Agent agent, @PathVariable("taskId") long taskId, @PathVariable("agentId") long agentId) throws Exception {
        Task taskData = DBService.getTaskData(taskId);
        taskData.getAgentList().remove(DBService.getAgent(agentId));
        DBService.save(taskData);
        return "redirect:../../index";
    }

    @RequestMapping(value = "taskStep/addAgent/{taskStepId}", method = RequestMethod.GET)
    public String addTaskStepAgent(@ModelAttribute("model") ModelMap model, @PathVariable("taskStepId") long taskStepId) throws Exception {
        model.addAttribute("agents", DBService.list());
        model.addAttribute("taskStepId", taskStepId);
        return "addTaskStepAgent";
    }

    @RequestMapping(value = "taskStep/addAgent/{taskStepId}", method = RequestMethod.POST)
    public String addTaskStepAgentPOST(@ModelAttribute("agent") Agent agent, @PathVariable("taskStepId") long taskStepId) throws Exception {
        TaskStep taskStep = DBService.getTaskStep(taskStepId);
        taskStep.getAgentList().add(DBService.getAgent(agent.getId()));
        DBService.save(taskStep);
        return "redirect:../../view/" + taskStep.getTask().getId();
    }

    @RequestMapping(value = "taskStep/removeAgent/{taskStepId}/{agentId}", method = RequestMethod.POST)
    public String removeTaskStepAgent(@ModelAttribute("agent") Agent agent, @PathVariable("taskStepId") long taskStepId, @PathVariable("agentId") long agentId) throws Exception {
        TaskStep taskStepData = DBService.getTaskStep(taskStepId);
        taskStepData.getAgentList().remove(DBService.getAgent(agentId));
        DBService.save(taskStepData);
        final String referer = request.getHeader("referer");
        System.out.println("referer = " + referer);
        return "redirect:" + referer;
    }

    @RequestMapping(value = "/addTask/{id}", method = RequestMethod.POST)
    public String addTask(@PathVariable("id") long id, @ModelAttribute("user") Task taskData, final HttpServletRequest request) {
        logger.info("Message received for Add : " + taskData);
        if (id != 0) {
            final Task dbTaskData = DBService.getTaskData(id);
            dbTaskData.setName(taskData.getName());
            dbTaskData.setDescription(taskData.getDescription());
            dbTaskData.setTags(taskData.getTags());
            DBService.save(dbTaskData);
        } else if (null != taskData && null != taskData.getName() && !taskData.getDescription().isEmpty()) {
//          taskData.setTradeDate(new Date());
            DBService.save(taskData);
        }
        final String referer = request.getHeader("referer");
        System.out.println("referer = " + referer);
        return "redirect:" + referer;
    }

    @RequestMapping(value = "updateTaskStep", method = RequestMethod.POST)
    @ResponseBody
    //we can also use this @RequestParam("active") String valueOne  ,  @ModelAttribute("active") String valueOne
    public void updateTaskStep(@ModelAttribute("taskStepData") TaskStep taskStepData) throws IOException {
        TaskStep dbTaskStepData = DBService.getTaskStep(taskStepData.getId());
        dbTaskStepData.setActive(taskStepData.isActive());
        DBService.save(dbTaskStepData);
    }

    @RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
    public String viewTaskDetails(@ModelAttribute("model") ModelMap model, @PathVariable("id") long id, final HttpServletRequest request) {
        Task task = DBService.getTaskData(id);
//        task.setStepDataList(task.getStepDataList().stream().sorted(Comparator.comparing(TaskStepData::getSequence)).collect(toList()));
        final String referer = request.getHeader("referer");
        model.addAttribute("task", task);
        model.addAttribute("referer", referer);
        return "taskSteps";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String editTaskStep(@ModelAttribute("model") ModelMap model, @PathVariable("id") long id) throws Exception {
        TaskStep stepData = DBService.getTaskStep(id);
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
            TaskStep existingTaskStep = DBService.getTaskStep(id);
            existingTaskStep.setId(id);
            existingTaskStep.setSequence(taskStepDTO.getSequence());
            existingTaskStep.setDescription(taskStepDTO.getDescription());
            existingTaskStep.setInputParams(convertObjectToXml(AbstractStep.listInputParams(Class.forName("org.shunya.shared.taskSteps." + existingTaskStep.getTaskClass()), taskStepDTO.getInputParamsMap())));
            existingTaskStep.setOutputParams(convertObjectToXml(AbstractStep.listOutputParams(Class.forName("org.shunya.shared.taskSteps." + existingTaskStep.getTaskClass()), taskStepDTO.getOutputParamsMap())));
            DBService.save(existingTaskStep);
        } else {
            TaskStep newTaskStep = new TaskStep();
            newTaskStep.setSequence(taskStepDTO.getSequence());
            newTaskStep.setDescription(taskStepDTO.getDescription());
            newTaskStep.setTaskClass(taskStepDTO.getTaskClass());
            newTaskStep.setInputParams(convertObjectToXml(AbstractStep.listInputParams(Class.forName("org.shunya.shared.taskSteps." + taskStepDTO.getTaskClass()), taskStepDTO.getInputParamsMap())));
            newTaskStep.setOutputParams(convertObjectToXml(AbstractStep.listOutputParams(Class.forName("org.shunya.shared.taskSteps." + taskStepDTO.getTaskClass()), taskStepDTO.getOutputParamsMap())));
            newTaskStep.setTask(DBService.getTaskData(taskStepDTO.getTaskId()));
            DBService.save(newTaskStep);
        }
        return "redirect:../view/" + taskStepDTO.getTaskId();
    }

    @RequestMapping(value = "/deleteStep/{id}", method = RequestMethod.GET)
    public String deleteStep(@PathVariable("id") long id, @ModelAttribute("stepData") TaskStep stepData) throws IOException {
        if (id != 0) {
            DBService.deleteTaskStep(id);
        }
        final String referer = request.getHeader("referer");
        System.out.println("referer = " + referer);
        return "redirect:" + referer;
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public String deleteTask(@PathVariable("id") long id, @ModelAttribute("taskData") Task taskData) throws IOException {
        if (id != 0) {
            DBService.deleteTask(id);
        }
        final String referer = request.getHeader("referer");
        System.out.println("referer = " + referer);
        return "redirect:" + referer;
    }

    @RequestMapping(value = "/taskHistory/{taskId}", method = RequestMethod.GET)
    public String viewTaskHistory(@ModelAttribute("model") ModelMap model, @PathVariable("taskId") long taskId) {
        List<TaskRun> all = DBService.findTaskHistoryForTaskId(taskId);
        model.addAttribute("taskHistoryList", all);
        model.addAttribute("taskId", taskId);
        return "taskRun";
    }

    @RequestMapping(value = "/taskStepHistory/{taskHistoryId}", method = RequestMethod.GET)
    public String viewTaskStepHistory(@ModelAttribute("model") ModelMap model, @PathVariable("taskHistoryId") long taskHistoryId) {
        TaskRun taskHistory = DBService.getTaskRun(taskHistoryId);
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
        Task taskData = DBService.getTaskData(taskId);
        TaskRun taskRun = new TaskRun();
        taskRun.setTask(taskData);
        taskRun.setName(taskData.getName());
        taskRun.setStartTime(new Date());
        taskRun.setComments(comment);
        taskService.execute(taskRun);
        return taskRun;
    }

    @RequestMapping(value = "submitTaskStepResults", method = RequestMethod.POST)
    @ResponseBody
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