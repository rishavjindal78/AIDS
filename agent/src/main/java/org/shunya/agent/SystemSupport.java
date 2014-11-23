package org.shunya.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Service
public class SystemSupport {
    static final Logger logger = LoggerFactory.getLogger(SystemSupport.class);

    private TrayIcon trayIcon;
    private AgentState agentCurrentState = AgentState.Idle;
    private BufferedImage currentImage;
    private BufferedImage idle, busy;

    @PostConstruct
    public void init() throws HeadlessException, IOException, AWTException {
        logger.info("Starting SystemSupport");
        idle = ImageIO.read(SystemSupport.class.getClassLoader().getResourceAsStream("AgentIdle.png"));
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

    public void displayMsg(String msg, TrayIcon.MessageType msgType) {
        if (trayIcon != null) {
            trayIcon.displayMessage("Agent", msg, msgType);
        }
    }

    private void startSystemTray() throws AWTException {
        if (SystemTray.isSupported()) {
            logger.warn("SystemTray is supported in this system");
            update(AgentState.Idle);
            Dimension trayIconSize = SystemTray.getSystemTray().getTrayIconSize();
            System.out.println("trayIconSize = " + trayIconSize);
            SystemTray.getSystemTray().add(trayIcon);
        } else {
            logger.warn("SystemTray is not supported in this system");
        }
    }

    @PreDestroy
    public void shutdown() {
        SystemTray.getSystemTray().remove(trayIcon);
        System.exit(0);
    }

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