package org.shunya.server.controller;

import org.shunya.server.TaskService;
import org.shunya.server.services.DBService;
import org.shunya.server.services.MyJobScheduler;
import org.shunya.shared.FieldPropertiesMap;
import org.shunya.shared.TaskContext;
import org.shunya.shared.TaskStep;
import org.shunya.shared.TaskStepDTO;
import org.shunya.shared.model.*;
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
    private DBService DBService;

    @Autowired
    private MyJobScheduler myJobScheduler;

//    final String[] taskClasses = {"EchoTaskStep", "DiscSpaceTaskStep", "SystemCommandTask"};

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
        TaskData taskData = DBService.getTaskData(id);
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
        TaskData taskData = DBService.getTaskData(taskId);
        taskData.getAgentList().add(DBService.getAgent(agent.getId()));
        DBService.save(taskData);
        return "redirect:../index";
    }

    @RequestMapping(value = "removeAgent/{taskId}/{agentId}", method = RequestMethod.POST)
    public String removeAgent(@ModelAttribute("agent") Agent agent, @PathVariable("taskId") long taskId, @PathVariable("agentId") long agentId) throws Exception {
        TaskData taskData = DBService.getTaskData(taskId);
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
        TaskStepData taskStepData = DBService.getTaskStepData(taskStepId);
        taskStepData.getAgentList().add(DBService.getAgent(agent.getId()));
        DBService.save(taskStepData);
        return "redirect:../../view/" + taskStepData.getTaskData().getId();
    }

    @RequestMapping(value = "taskStep/removeAgent/{taskStepId}/{agentId}", method = RequestMethod.POST)
    public String removeTaskStepAgent(@ModelAttribute("agent") Agent agent, @PathVariable("taskStepId") long taskStepId, @PathVariable("agentId") long agentId) throws Exception {
        TaskStepData taskStepData = DBService.getTaskStepData(taskStepId);
        taskStepData.getAgentList().remove(DBService.getAgent(agentId));
        DBService.save(taskStepData);
        final String referer = request.getHeader("referer");
        System.out.println("referer = " + referer);
        return "redirect:" + referer;
    }

    @RequestMapping(value = "/addTask/{id}", method = RequestMethod.POST)
    public String addTask(@PathVariable("id") long id, @ModelAttribute("user") TaskData taskData, final HttpServletRequest request) {
        logger.info("Message received for Add : " + taskData);
        if (id != 0) {
            final TaskData dbTaskData = DBService.getTaskData(id);
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
    public void updateTaskStep(@ModelAttribute("taskStepData") TaskStepData taskStepData) throws IOException {
        TaskStepData dbTaskStepData = DBService.getTaskStepData(taskStepData.getId());
        dbTaskStepData.setActive(taskStepData.isActive());
        DBService.save(dbTaskStepData);
    }

    @RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
    public String viewTaskDetails(@ModelAttribute("model") ModelMap model, @PathVariable("id") long id, final HttpServletRequest request) {
        TaskData taskData = DBService.getTaskData(id);
//        taskData.setStepDataList(taskData.getStepDataList().stream().sorted(Comparator.comparing(TaskStepData::getSequence)).collect(toList()));
        final String referer = request.getHeader("referer");
        model.addAttribute("taskData", taskData);
        model.addAttribute("referer", referer);
        return "taskSteps";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String editTaskStep(@ModelAttribute("model") ModelMap model, @PathVariable("id") long id) throws Exception {
        TaskStepData stepData = DBService.getTaskStepData(id);
        model.addAttribute("stepData", stepData);
        List<String> taskClasses = DBService.lisTaskMetadata().stream().map(TaskMetadata::getName).collect(toList());
        model.addAttribute("taskClasses", taskClasses);
//        FieldPropertiesMap inPropertiesMap = TaskStep.listInputParams(Class.forName(stepData.getClassName()), parseStringMap(stepData.getInputParams()));
        FieldPropertiesMap inPropertiesMap = FieldPropertiesMap.convertXmlToObject(stepData.getInputParams());
        FieldPropertiesMap outPropertiesMap = FieldPropertiesMap.convertXmlToObject(stepData.getOutputParams());
        model.addAttribute("inputParams", inPropertiesMap.values());
        model.addAttribute("outputParams", outPropertiesMap.values());
        return "editTaskStep";
    }

    @RequestMapping(value = "/addTaskStep/{taskId}", method = RequestMethod.GET)
    public String viewTaskSteps(@ModelAttribute("model") ModelMap model, @PathVariable("taskId") long taskId, @RequestParam(value = "taskClassId", required = false, defaultValue = "0") long taskClassId) throws Exception {
        List<TaskMetadata> taskMetadatas = DBService.lisTaskMetadata();
        long firstId = taskMetadatas.stream().findFirst().get().getId();
        taskClassId = (taskClassId==0?firstId:taskClassId);
        model.addAttribute("taskId", taskId);
        FieldPropertiesMap inPropertiesMap = TaskStep.listInputParams(Class.forName(DBService.getTaskMetadata(taskClassId).getClassName()), Collections.<String, String>emptyMap());
        FieldPropertiesMap outPropertiesMap = TaskStep.listOutputParams(Class.forName(DBService.getTaskMetadata(taskClassId).getClassName()), Collections.<String, String>emptyMap());
        model.addAttribute("selectedClassId", taskClassId);
        model.addAttribute("taskClasses", taskMetadatas);
        model.addAttribute("inputParams", inPropertiesMap.values());
        model.addAttribute("outputParams", outPropertiesMap.values());
        model.addAttribute("referer", request.getHeader("referer"));
        return "addTaskStep";
    }

    @RequestMapping(value = "/addTaskStep/{id}", method = RequestMethod.POST)
    public String addTaskStep(@PathVariable("id") long id, @ModelAttribute("taskStepDTO") TaskStepDTO taskStepDTO) throws Exception {
        if (id != 0) {
            TaskStepData taskStepData = DBService.getTaskStepData(id);
            taskStepData.setId(id);
            taskStepData.setSequence(taskStepDTO.getSequence());
            taskStepData.setDescription(taskStepDTO.getDescription());
            taskStepData.setInputParams(convertObjectToXml(TaskStep.listInputParams(Class.forName(taskStepData.getTaskMetadata().getClassName()),taskStepDTO.getInputParamsMap())));
            taskStepData.setOutputParams(convertObjectToXml(TaskStep.listOutputParams(Class.forName(taskStepData.getTaskMetadata().getClassName()), taskStepDTO.getOutputParamsMap())));
            DBService.save(taskStepData);
        } else {
            TaskStepData taskStepData = new TaskStepData();
            taskStepData.setSequence(taskStepDTO.getSequence());
            taskStepData.setDescription(taskStepDTO.getDescription());
            taskStepData.setTaskMetadata(DBService.getTaskMetadata(taskStepDTO.getClassNameId()));
            taskStepData.setInputParams(convertObjectToXml(TaskStep.listInputParams(Class.forName(taskStepData.getTaskMetadata().getClassName()),taskStepDTO.getInputParamsMap())));
            taskStepData.setOutputParams(convertObjectToXml(TaskStep.listOutputParams(Class.forName(taskStepData.getTaskMetadata().getClassName()), taskStepDTO.getOutputParamsMap())));
            taskStepData.setTaskData(taskStepDTO.getTaskData());
            DBService.save(taskStepData);
        }
        return "redirect:../view/" + taskStepDTO.getTaskData().getId();
    }

    @RequestMapping(value = "/deleteStep/{id}", method = RequestMethod.GET)
    public String deleteStep(@PathVariable("id") long id, @ModelAttribute("stepData") TaskStepData stepData) throws IOException {
        if (id != 0) {
            DBService.deleteTaskStep(id);
        }
        final String referer = request.getHeader("referer");
        System.out.println("referer = " + referer);
        return "redirect:" + referer;
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public String deleteTask(@PathVariable("id") long id, @ModelAttribute("taskData") TaskData taskData) throws IOException {
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
    public void runTaskData(@PathVariable("taskId") Long taskId, @RequestParam String comment, Principal principal) {
        logger.info("Run request for {}, user comments ", taskId, comment);
        TaskData taskData = DBService.getTaskData(taskId);
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
        TaskRun taskRun = DBService.getTaskRun(taskContext.getTaskStepRun());
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