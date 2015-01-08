package org.shunya.server;

public enum AgentStatus {
    UP("Agent is Running"), DOWN("Agent is Down");

    private String desc;
    private String version;

    AgentStatus(String desc) {
        this.desc = desc;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }
}
