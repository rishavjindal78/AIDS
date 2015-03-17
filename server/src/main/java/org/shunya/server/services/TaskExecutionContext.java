package org.shunya.server.services;

import org.shunya.server.model.Task;
import org.shunya.server.model.TaskStepRun;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TaskExecutionContext {
    private final Task task;
    //    private TreeMap<Integer, List<TaskStep>> sequenceTaskSteps = new TreeMap<>();
    private List<TaskStepRun> currentlyRunningTaskStepRuns;
    private TreeMap<Integer, List<TaskStepRun>> sequenceTaskStepRuns = new TreeMap<>();
    private Map<String, Object> sessionMap = new HashMap<>();
    private Map<String, String> propertiesOverride = new HashMap<>();
    private boolean taskStatus = true;
    private boolean abortOnFirstFailure = true;
    private boolean cancelled = false;

    public TaskExecutionContext(Task task) {
        this.task = task;
        this.abortOnFirstFailure = task.isAbortOnFirstFailure();
    }

    public Map.Entry<Integer, List<TaskStepRun>> pollNextTaskStepRunList() {
        Map.Entry<Integer, List<TaskStepRun>> firstEntry = sequenceTaskStepRuns.pollFirstEntry();
        if (firstEntry != null)
            currentlyRunningTaskStepRuns = firstEntry.getValue();
        return firstEntry;
    }

    public void addTaskStepRun(Integer sequence, List<TaskStepRun> values){
        sequenceTaskStepRuns.put(sequence, values);
    }

    public Task getTask() {
        return task;
    }

    public Map<String, Object> getSessionMap() {
        return sessionMap;
    }

    public boolean isTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(boolean taskStatus) {
        this.taskStatus = taskStatus;
    }

    public boolean isAbortOnFirstFailure() {
        return abortOnFirstFailure;
    }

    public void setAbortOnFirstFailure(boolean abortOnFirstFailure) {
        this.abortOnFirstFailure = abortOnFirstFailure;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public Map<String, String> getPropertiesOverride() {
        return propertiesOverride;
    }

    public List<TaskStepRun> getCurrentlyRunningTaskStepRuns() {
        return currentlyRunningTaskStepRuns;
    }
}
