package org.shunya.shared.taskSteps;

import org.shunya.shared.StringUtils;
import org.shunya.shared.AbstractStep;
import org.shunya.shared.annotation.InputParam;
import org.shunya.shared.annotation.PunterTask;
import org.shunya.shared.utils.DevEmailService;
import org.shunya.shared.utils.EmailService;

import java.io.File;
import java.util.Collections;
import java.util.logging.Level;

@PunterTask(author = "munishc", name = "EmailTask", description = "Email Task", documentation = "src/main/resources/docs/EmailTask.html")
public class EmailStep extends AbstractStep {
    @InputParam(required = true, displayName = "To Address", description = "comma separated to addresses")
    private String toAddress;
    @InputParam(required = true, displayName = "CC Address", description = "comma separated CC addresses")
    private String ccAddress;
    @InputParam(required = true, displayName = "Email Subject", description = "Subject of Email")
    private String subject;
    @InputParam(required = true, displayName = "From Address", description = "from Address")
    private String fromAddress;
    @InputParam(required = true, displayName = "Email Body", type = "textarea", description = "html body")
    private String body;
    @InputParam(required = true, displayName = "Comma Separated File Paths", description = "Comma Separated File Names")
    private String attachments;
    @InputParam(required = false, displayName = "Username", description = "Username if Auth is required")
    private String username;
    @InputParam(required = false, displayName = "Display Name", description = "Password if Auth is required")
    private String password;
    @InputParam(required = false, displayName = "Input String for matching", description = "Input String for matching")
    private String inputString;
    @InputParam(required = false, displayName = "Line Separated Expected Messages", description = "Line separated expected messages")
    private String expectedMessages;

    @Override
    public boolean run() {
        boolean status = false;
        try {
            String outName = "";
            if (inputString != null && expectedMessages != null && !expectedMessages.isEmpty()) {
                String[] messages = expectedMessages.split("\n");
                for (String message : messages) {
                    if (!inputString.contains(message)) {
                        outName += message + " not true," + System.getProperty("line.separator");
                    }
                }
                if (outName.length() >= 1) {
                    LOGGER.get().log(Level.SEVERE, outName);
                } else {
                    LOGGER.get().log(Level.INFO, "No Email was sent since Condition did not meet!");
                    return true;
                }
            }
            attachments = attachments == null ? "" : attachments;
            String[] fileNames = attachments.split("[,;]");
            if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                //Authentication Based Email
//				EmailServiceWithAuth.getInstance(username,password).sendEMail(subject, toAddress, body+outName, fileNames, fromAddress, ccAddress);
                DevEmailService.getInstance().sendEmail(subject, toAddress, body, Collections.<File>emptyList());
            } else {
                //Non-Auth Based Email
                EmailService.getInstance().sendEMail(subject, toAddress, body + outName, fileNames, fromAddress, ccAddress);
            }
            status = true;
            LOGGER.get().log(Level.INFO, "Email sent successfully To Addresses: " + toAddress);
        } catch (Exception e) {
            status = false;
            LOGGER.get().log(Level.SEVERE, StringUtils.getExceptionStackTrace(e));
        }
        return status;
    }
}