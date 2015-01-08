package org.shunya.agent.controller;

import org.apache.commons.io.IOUtils;
import org.shunya.agent.services.TaskProcessor;
import org.shunya.shared.StringUtils;
import org.shunya.shared.TaskContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

@Controller
@RequestMapping("/agent")
public class AgentController {
    private static final Logger logger = Logger.getLogger(AgentController.class.getName());
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private TaskProcessor taskProcessor;

    @Value("${app.version}")
    private String appVersion;

    @RequestMapping(value = "submitTaskStep", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public void submitTaskStep(@RequestBody TaskContext taskContext) throws InterruptedException, ExecutionException {
        System.out.println("TaskStep Received for Execution = " + taskContext.getStepDTO().getDescription() + " ID - " + taskContext.getTaskStepRunDTO().getId());
        taskProcessor.executeTask(taskContext);
    }

    @RequestMapping(value = "ping", method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public void ping() {
        logger.fine(() -> "ping success");
    }

    @RequestMapping(value = "version", method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public String version() {
        return appVersion;
    }

    @RequestMapping(value = "download/{name}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    @ResponseBody
    public FileSystemResource downloadFile(@PathVariable("name") String name, HttpServletResponse response) throws IOException {
//        response.setContentType(file.getContentType());
//        response.setContentLength((new Long(file.getLength()).intValue()));
        response.setHeader("Content-Disposition", "attachment;filename=" + name);
        return new FileSystemResource(new File("uploads", name));
    }

    @RequestMapping(value = "upload", method = RequestMethod.POST)
    @ResponseBody
    public String handleFileUpload(@RequestParam(value = "name", required = false) String name, @RequestParam(value = "path", required = false) String path, @RequestParam("file") MultipartFile file) throws IOException {
        try {
            if (path == null)
                path = "uploads";
            File dir = new File(path);
            dir.mkdirs();
            FileOutputStream fileOutputStream = new FileOutputStream(new File(dir, name));
            IOUtils.copyLarge(file.getInputStream(), fileOutputStream);
            fileOutputStream.close();
            String absolutePath = new File(dir, name).getAbsolutePath();
            logger.info(() -> "File saved at location : " + absolutePath);
            return absolutePath;
        } catch (IOException e) {
            logger.severe(() -> "Error while saving the file - " + StringUtils.getExceptionStackTrace(e));
            throw e;
        }
    }

    @RequestMapping(value = "getMemoryLogs/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String getMemoryLogs(@ModelAttribute("model") ModelMap model, @PathVariable("id") long id, @RequestParam("start") long start) throws Exception {
        return taskProcessor.getMemoryLogs(id, start);
    }

}
