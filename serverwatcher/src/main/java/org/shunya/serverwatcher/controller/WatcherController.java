package org.shunya.serverwatcher.controller;

import org.apache.commons.io.IOUtils;
import org.shunya.serverwatcher.JAXBHelper;
import org.shunya.serverwatcher.ServerApp;
import org.shunya.serverwatcher.services.ServerHealthService;
import org.shunya.shared.AbstractStep;
import org.shunya.shared.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;
import sun.management.Agent;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileOutputStream;
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
                             @RequestParam(required = false, defaultValue = "") String filename,
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

    @RequestMapping(value = "upload", method = RequestMethod.POST)
    @ResponseBody
    public String handleFileUpload(@RequestParam(value = "name", required = false) String name, @RequestParam(value = "path", required = false) String path, @RequestParam("file") MultipartFile file/*, @RequestParam("session") Map<String, Object> sessionMap*/) throws IOException {
        try {
            ServerApp serverApp = JAXBHelper.loadServerAppConfig(file.getInputStream(), ServerApp.class);
            file.getInputStream().close();
            serverHealthService.addServerApp(serverApp);
            serverHealthService.scheduleJob(serverApp);
            serverHealthService.saveServerApp(serverApp);
//            logger.info(() -> "File saved at location : " + absolutePath);
            return "File Saved Successfully";
        } catch (Exception e) {
            e.printStackTrace();
//            logger.severe(() -> "Error while saving the file - " + StringUtils.getExceptionStackTrace(e));
        }
        return "Failed to save file";
    }

}
