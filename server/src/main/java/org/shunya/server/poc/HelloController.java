package org.shunya.server.poc;

import org.shunya.server.poc.AgentContextListner;
import org.shunya.shared.model.Agent;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping("/")
public class HelloController {
    private Map<String, DeferredResult> agentMap = new ConcurrentHashMap<>(10);

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String printWelcome(ModelMap model) {
        model.addAttribute("message", "Hello world!");
        return "hello";
    }

    @RequestMapping(value = "ping", method = RequestMethod.GET)
    @ResponseBody
    public String ping() {
        return "success";
    }

    @RequestMapping(value = "register", method = RequestMethod.POST)
    @ResponseBody
    public String register(@RequestBody Agent agent) {
        System.out.println("agent = " + agent);
        return "success";
    }

    @RequestMapping(value = "submit", method = RequestMethod.GET)
    @ResponseBody
    public String submit(HttpServletRequest request) {
        Agent agent = new Agent();
        agent.setName("First");
//        agent.setId("1000");
        ((AgentContextListner) request.getServletContext().getAttribute("listner")).runTaskOnAgent(null, agent);
        return "submitted";
    }

    @RequestMapping(value = "getTask/{id}", method = RequestMethod.GET)
    @ResponseBody
    public DeferredResult<Agent> getTask(@PathVariable("id") String id){
        DeferredResult<Agent> deferredResult = new DeferredResult<>();
        agentMap.put(id, deferredResult);
        return deferredResult;
    }

    @RequestMapping(value = "execute/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String execute(@PathVariable("id") String id){
        DeferredResult deferredResult = agentMap.get(id);
        Agent agent = new Agent();
//        agent.setId("100");
        agent.setName("test name");
        deferredResult.setResult(agent);
        return "success";
    }
}