package org.shunya.shared.taskSteps;


import org.shunya.shared.StringUtils;
import org.shunya.shared.TaskStep;
import org.shunya.shared.annotation.InputParam;
import org.shunya.shared.annotation.PunterTask;
import org.shunya.shared.utils.RestClient;

import java.util.logging.Level;

@PunterTask(author = "munishc", name = "FileUploadTask", description = "Transfers file from one agent to another", documentation = "src/main/resources/docs/TextSamplerDemoHelp.html")
public class FileUploadTaskStep extends TaskStep {
    @InputParam(required = true, displayName = "Server Address", description = "enter your httpUrl here http://localhost:8080/upload/")
    private String server;
    @InputParam(required = false, displayName = "Local File Path", description = "Path of the file to upload e:/test.mp3")
    private String filePath;
    @InputParam(required = false, displayName = "Target FIle Name", description = "Name of the file")
    private String name;
    @InputParam(required = false, displayName = "Target Folder", description = "Remote Path for the file")
    private String remotePath;
    private RestClient restClient = new RestClient();

    @Override
    public boolean run() {
        boolean status = false;
        LOGGER.get().log(Level.INFO, "Uploading File :" + filePath + " To Location : " + server);
        try {
            restClient.fileUpload(server, filePath, name, remotePath);
            status = true;
        } catch (Exception e) {
            LOGGER.get().log(Level.SEVERE, StringUtils.getExceptionStackTrace(e));
        }
        return status;
    }
}