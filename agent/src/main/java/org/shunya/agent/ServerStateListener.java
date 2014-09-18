package org.shunya.agent;

import java.awt.*;

public interface ServerStateListener {
    void displayMsg(String msg, TrayIcon.MessageType msgType);

    void update(AgentState state);

}
