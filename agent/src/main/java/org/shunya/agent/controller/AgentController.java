package org.shunya.agent.controller;

import org.shunya.agent.TaskProcessor;
import org.shunya.shared.TaskContext;
import org.shunya.shared.model.Agent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/agent")
public class AgentController {
    @Autowired
    private TaskProcessor taskProcessor;

    @RequestMapping(value = "submitTaskStep", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public void submitTaskStep(@RequestBody TaskContext taskContext) throws InterruptedException, ExecutionException {
        System.out.println("TaskStep Received for Execution = " + taskContext);
        taskProcessor.executeTask(taskContext);
    }

    @RequestMapping(value = "ping", method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public void ping() {

    }

    @RequestMapping(value = "getMemoryLogs/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String editAgent(@ModelAttribute("model") ModelMap model, @PathVariable("id") long id, @RequestParam("start")long start) throws Exception {
        return taskProcessor.getMemoryLogs(id, start);
    }

}
