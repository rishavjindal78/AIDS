package org.shunya.shared;

import org.shunya.shared.model.TaskStepRun;

import java.util.Map;

public class TaskContext {
    private TaskStepRun taskStepRun;
    private Map<String, Object> sessionMap;
    private boolean doVariableSubstitution = true;
    private String callbackURL = "http://localhost:9290/rest/server/submitTaskStepResults";

    public TaskStepRun getTaskStepRun() {
        return taskStepRun;
    }

    public void setTaskStepRun(TaskStepRun taskStepRun) {
        this.taskStepRun = taskStepRun;
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
}
