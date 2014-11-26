package org.shunya.shared;

import java.util.Map;

public class TaskContext {
    private TaskStepDTO stepDTO;
    private TaskStepRunDTO taskStepRunDTO;
    private Map<String, Object> sessionMap;
    private boolean doVariableSubstitution = true;
    private String callbackURL;
    private String baseUrl;
    private String username;
    private String password;

    public TaskStepRunDTO getTaskStepRunDTO() {
        return taskStepRunDTO;
    }

    public void setTaskStepRunDTO(TaskStepRunDTO taskStepRunDTO) {
        this.taskStepRunDTO = taskStepRunDTO;
    }

    public Map<String, Object> getSessionMap() {
        return sessionMap;
    }

    public void setSessionMap(Map<String, Object> sessionMap) {
        this.sessionMap = sessionMap;
    }

    public boolean isDoVariableSubstitution() {
        return doVariableSubstitution;
    }

    public void setDoVariableSubstitution(boolean doVariableSubstitution) {
        this.doVariableSubstitution = doVariableSubstitution;
    }

    public String getCallbackURL() {
        return callbackURL;
    }

    public void setCallbackURL(String callbackURL) {
        this.callbackURL = callbackURL;
    }

    public TaskStepDTO getStepDTO() {
        return stepDTO;
    }

    public void setStepDTO(TaskStepDTO stepDTO) {
        this.stepDTO = stepDTO;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
