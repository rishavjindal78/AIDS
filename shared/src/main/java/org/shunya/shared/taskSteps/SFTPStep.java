package org.shunya.shared.taskSteps;

import com.jcraft.jsch.*;
import org.shunya.shared.AbstractStep;
import org.shunya.shared.annotation.InputParam;
import org.shunya.shared.annotation.OutputParam;
import org.shunya.shared.annotation.PunterTask;

import java.util.logging.Level;

@PunterTask(author = "munishc", name = "EchoTaskStep", description = "Echo's the input data to SOP", documentation = "docs/TextSamplerDemoHelp.html")
public class SFTPStep extends AbstractStep {
    @InputParam(required = true, displayName = "Name", type = "input", description = "enter your name here")
    private String name;

    @OutputParam(displayName = "Out Name")
    private String outName;

    @Override
    public boolean run() {
        outName = "Hello " + name;
        LOGGER.get().log(Level.INFO, outName);
        return true;
    }

    public void putFile(String username, String host, String password, String remotefile,     String localfile){
        JSch jsch = new JSch();
        Session session = null;
        try {
            session = jsch.getSession(username, host, 22);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(password);
            session.connect();
            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp) channel;
            sftpChannel.put(localfile, remotefile);
            sftpChannel.exit();
            session.disconnect();
        } catch (JSchException e) {
            e.printStackTrace();
        } catch (SftpException e) {
            e.printStackTrace();
        }
    }
}