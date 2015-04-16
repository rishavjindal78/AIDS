package org.shunya.agent.controller;

import org.shunya.agent.services.TaskProcessor;
import org.shunya.shared.AbstractStep;
import org.shunya.shared.TaskContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/agent")
public class AgentController implements HandlerExceptionResolver {
    private static final Logger logger = LoggerFactory.getLogger(AgentController.class.getName());
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private TaskProcessor taskProcessor;

    @Value("${app.version}")
    private String appVersion;

    @RequestMapping(value = "submitTaskStep", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public void submitTaskStep(@RequestBody TaskContext taskContext) throws InterruptedException, ExecutionException {
        logger.info("TaskStep Received for Execution = " + taskContext.getStepDTO().getDescription() + " ,ID - " + taskContext.getTaskStepRunDTO().getId());
        taskProcessor.executeTask(taskContext);
    }

    @RequestMapping(value = "ping", method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public void ping() {
        logger.debug("ping success");
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
    public String handleFileUpload(@RequestParam(value = "name", required = false) String name, @RequestParam(value = "path", required = false) String path, @RequestParam("file") MultipartFile file/*, @RequestParam("session") Map<String, Object> sessionMap*/) throws IOException {
        try {
            name = AbstractStep.substituteEnvVariables(name);
            path = AbstractStep.substituteEnvVariables(path);
            if (path == null)
                path = "uploads";
            File dir = new File(path);
            dir.mkdirs();
            File targetFile = new File(dir, name);
            file.transferTo(targetFile);
            String absolutePath = targetFile.getAbsolutePath();
            logger.info("File saved at location : " + absolutePath);
            return absolutePath;
        } catch (Exception e) {
            logger.error("Error while saving the file - ", e);
            throw e;
        }
    }

    @RequestMapping(value = "getMemoryLogs/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String getMemoryLogs(@ModelAttribute("model") ModelMap model, @PathVariable("id") long id, @RequestParam("start") long start) throws Exception {
        return taskProcessor.getMemoryLogs(id, start);
    }

    @RequestMapping(value = "isStepRunning/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Boolean checkIfTaskStepRunning(@ModelAttribute("model") ModelMap model, @PathVariable("id") long id) throws Exception {
        return taskProcessor.isRunning(id);
    }

    @RequestMapping(value = "interrupt/{id}", method = RequestMethod.GET)
    @ResponseBody
    public void interruptStep(@ModelAttribute("model") ModelMap model, @PathVariable("id") long id) throws Exception {
        taskProcessor.interrupt(id);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> rulesForCustomerNotFound(HttpServletRequest req, Exception e, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
        return null;
    }

    public ModelAndView resolveException(HttpServletRequest request,
                                         HttpServletResponse response, Object handler, Exception exception) {
        Map<String, Object> model = new HashMap<>();
        if (exception instanceof MaxUploadSizeExceededException) {
            model.put("errors", exception.getMessage());
        } else {
            model.put("errors", "Unexpected error: " + exception.getMessage());
        }
//        model.put("uploadedFile", new UploadedFile());
//        return new ModelAndView("/upload", model);
        logger.error("Exception Occurred - ", exception);
        try {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.getLocalizedMessage());
            return null;
        } catch (IOException e) {
            logger.error("Exception Occurred - ", e);
        }
        return new ModelAndView(new MappingJackson2JsonView(), model);
    }

}