package org.shunya.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Service
public class SystemSupport implements ServerStateListener {
    static final Logger logger = LoggerFactory.getLogger(SystemSupport.class);

    private final TrayIcon trayIcon;
    private AgentState agentCurrentState = AgentState.Idle;
    private BufferedImage currentImage;
    private final BufferedImage idle, busy;

    public SystemSupport() throws HeadlessException, IOException, AWTException {
        idle = ImageIO.read(SystemSupport.class.getClassLoader().getResourceAsStream("AgentDisconnect.png"));
        busy = ImageIO.read(SystemSupport.class.getClassLoader().getResourceAsStream("AgentBusy.png"));
        trayIcon = new TrayIcon(idle);
        trayIcon.setToolTip("AIDS Agent Started");
        trayIcon.setImageAutoSize(true);
        startSystemTray();
    }

    public static void main(String[] args) throws IOException, AWTException {
        SystemSupport systemSupport = new SystemSupport();
        systemSupport.startSystemTray();
        System.in.read();
        systemSupport.shutdown();
    }

    public synchronized void updateIconImage(final AgentState agentState) {
        SwingUtilities.invokeLater(() -> {
            updateState(agentState);
        });
    }

    private synchronized void updateState(AgentState agentState) {
        trayIcon.setImage(currentImage);
        displayMsg(agentState.getDisplayMessage(), TrayIcon.MessageType.INFO);
    }

    @Override
    public void displayMsg(String msg, TrayIcon.MessageType msgType) {
        if (trayIcon != null) {
            trayIcon.displayMessage("Agent", msg, msgType);
        }
    }

    private void startSystemTray() throws AWTException {
        if (SystemTray.isSupported()) {
            update(AgentState.Idle);
            Dimension trayIconSize = SystemTray.getSystemTray().getTrayIconSize();
            System.out.println("trayIconSize = " + trayIconSize);
            SystemTray.getSystemTray().add(trayIcon);
        }
    }

    public void shutdown() {
        SystemTray.getSystemTray().remove(trayIcon);
        System.exit(0);
    }

    @Override
    public synchronized void update(AgentState state) {
        if (agentCurrentState == state)
            return;
        this.agentCurrentState = state;
        switch (agentCurrentState) {
            case Idle:
                currentImage = idle;
                break;
            case Busy:
                currentImage = busy;
                break;
        }
        logger.info("updating state to {} ", state.getDisplayMessage());
        updateIconImage(state);
    }
}