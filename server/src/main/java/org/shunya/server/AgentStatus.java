package org.shunya.server;

public enum AgentStatus {
    UP("Agent is Running"), DOWN("Agent is Down");

    private String desc;

    AgentStatus(String desc) {
        this.desc = desc;
    }
}
