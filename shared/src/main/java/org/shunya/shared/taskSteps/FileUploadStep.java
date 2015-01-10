package org.shunya.shared.taskSteps;


import org.shunya.shared.StringUtils;
import org.shunya.shared.AbstractStep;
import org.shunya.shared.annotation.InputParam;
import org.shunya.shared.annotation.PunterTask;
import org.shunya.shared.utils.RestClient;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Arrays.asList;

@PunterTask(author = "munishc", name = "FileUploadTask", description = "Transfers file from one agent to another", documentation = "src/main/resources/docs/TextSamplerDemoHelp.html")
public class FileUploadStep extends AbstractStep {
    @InputParam(required = true, displayName = "Server Address", description = "enter your httpUrl here http://localhost:8080/upload/")
    private String server;
    @InputParam(required = false, displayName = "Local File Path", description = "Path of the file to upload e:/test.mp3")
    private String filePath;
    @InputParam(required = false, displayName = "Target File Name", description = "Target Name of the file")
    private String name;
    @InputParam(required = false, displayName = "Target Folder", description = "Target Path for the file", substitute = false)
    private String remotePath;
    private RestClient restClient = new RestClient();

    @Override
    public boolean run() {
        final AtomicBoolean status = new AtomicBoolean(true);
        try {
            String[] split = server.split("[,;]");
            final Logger logger = LOGGER.get();
            asList(split).parallelStream().filter(s -> s != null && !s.isEmpty()).forEach(singleServer -> {
                try {
                    logger.log(Level.INFO, "Uploading File :" + filePath + " To Location : " + singleServer);
                    String targetLocation = restClient.fileUpload(singleServer, filePath, name, remotePath);
                    logger.log(Level.INFO, filePath + " Uploaded on Agent: " + singleServer + " at Location - " + targetLocation);
                    status.set(true & status.get());
                } catch (Exception e) {
                    status.set(false);
                    e.printStackTrace();
                    logger.log(Level.SEVERE, "Exception uploading file to server - " + singleServer + "\n" + StringUtils.getExceptionStackTrace(e));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.get().log(Level.SEVERE, StringUtils.getExceptionStackTrace(e));
        }
        return status.get();
    }
}