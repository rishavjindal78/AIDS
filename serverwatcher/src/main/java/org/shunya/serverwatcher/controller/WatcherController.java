package org.shunya.serverwatcher.controller;

import org.shunya.serverwatcher.JAXBHelper;
import org.shunya.serverwatcher.ServerApp;
import org.shunya.serverwatcher.services.ServerHealthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import sun.management.Agent;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import java.io.IOException;

@Controller
@RequestMapping("/dashboard")
public class WatcherController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ServerHealthService serverHealthService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String dashboard() {
        return "dashboard";
    }

    @RequestMapping(value = "healthMonitor", method = RequestMethod.GET)
    @ResponseBody
    public DeferredResult<String> healthMonitor(@RequestParam(required = false, defaultValue = "0") long id,
                                                @RequestParam(required = false, defaultValue = "0") long cacheId,
                                                HttpServletResponse response) throws IOException, InterruptedException {
        DeferredResult<String> deferredResult = new DeferredResult<>();
        serverHealthService.getStatusForServerId(id, cacheId, deferredResult);
        return deferredResult;
    }

    @RequestMapping(value = "fileDownload", method = RequestMethod.GET)
    public void registerUser(@RequestParam(required = true) long id,
                             @RequestParam(required = false, defaultValue = "")String filename,
                             HttpServletResponse response) throws Exception {
        response.setContentType("application/xml");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + id + ".xml\"");
        ServletOutputStream op = response.getOutputStream();
        try {
            byte[] bytes = JAXBHelper.convertServerAppToByteArray(ServerApp.class, serverHealthService.getServerApp(id));
            response.setHeader("Content-Length", String.valueOf(bytes.length));
            op.write(bytes, 0, bytes.length);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "refresh", method = RequestMethod.POST)
    @ResponseBody
    public void refresh(@RequestParam(required = false, defaultValue = "0") long id) throws Exception {
        if (id != 0)
            serverHealthService.refresh(id);
        else
            serverHealthService.refreshAll();
    }
/*
    @RequestMapping(value = "profile/{username}", method = RequestMethod.GET)
    public String profile(@ModelAttribute("model") ModelMap model, @PathVariable("username") String username) {
        User user = dbService.findUserByUsername(username);
        model.addAttribute("user", user);
        return "profile";
    }*/
}
