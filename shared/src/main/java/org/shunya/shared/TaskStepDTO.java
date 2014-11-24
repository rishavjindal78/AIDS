package org.shunya.shared;

import com.sun.javafx.tk.Toolkit;

import java.util.HashMap;
import java.util.Map;

public class TaskStepDTO {
    private long taskId;
    private int sequence;
    private String name;
    private String taskClass;
    private String description;
    private Map<String, String> inputParamsMap = new HashMap<>();
    private Map<String, String> outputParamsMap = new HashMap<>();

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
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


    public String getTaskClass() {
        return taskClass;
    }

    public void setTaskClass(String taskClass) {
        this.taskClass = taskClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }
}
