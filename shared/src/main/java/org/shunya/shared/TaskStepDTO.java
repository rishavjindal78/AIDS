package org.shunya.shared;

import org.shunya.shared.model.TaskData;

import java.util.HashMap;
import java.util.Map;

public class TaskStepDTO {
    private int sequence;
    private int classNameId;
    private String description;
    private boolean active = true;
    private Map<String,String> inputParamsMap = new HashMap<>();
    private Map<String,String> outputParamsMap = new HashMap<>();

    private TaskData taskData;

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Map<String, String> getInputParamsMap() {
        return inputParamsMap;
    }

    public void setInputParamsMap(Map<String, String> inputParamsMap) {
        this.inputParamsMap = inputParamsMap;
    }

    public Map<String, String> getOutputParamsMap() {
        return outputParamsMap;
    }

    public void setOutputParamsMap(Map<String, String> outputParamsMap) {
        this.outputParamsMap = outputParamsMap;
    }

    public int getClassNameId() {
        return classNameId;
    }

    public void setClassNameId(int classNameId) {
        this.classNameId = classNameId;
    }

    public TaskData getTaskData() {
        return taskData;
    }

    public void setTaskData(TaskData taskData) {
        this.taskData = taskData;
    }
}
