package org.shunya.agent;

public enum AgentState {
    Idle("Agent is Idle"), Busy("Agent started processing"), Disconnect("Agent disconnected");
    private String displayMessage;

    AgentState(String displayMessage) {
        this.displayMessage = displayMessage;
    }

    public String getDisplayMessage() {
        return displayMessage;
    }
}
