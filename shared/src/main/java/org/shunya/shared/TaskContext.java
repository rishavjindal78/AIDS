package org.shunya.shared;

import java.util.Map;

public class TaskContext {
    private TaskStepDTO stepDTO;
    private TaskStepRunDTO taskStepRunDTO;
    private Map<String, Object> sessionMap;
    private boolean doVariableSubstitution = true;
    private String callbackURL = "http://localhost:9290/rest/server/submitTaskStepResults";

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
}
