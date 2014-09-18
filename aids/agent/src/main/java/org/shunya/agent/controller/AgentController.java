package org.shunya.agent.controller;

import org.shunya.agent.TaskProcessor;
import org.shunya.shared.TaskContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
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
        System.out.println("taskStep received for execution = " + taskContext);
        taskProcessor.executeTask(taskContext);
    }

    @RequestMapping(value = "ping", method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public void ping() {

    }

}
