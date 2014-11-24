package org.shunya.server;

import org.shunya.server.model.Task;
import org.shunya.server.model.TaskStep;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class TaskExecutionPlan {
    private final Task task;
    private final TreeMap<Integer, List<TaskStep>> treeMap;
    private Map<String, Object> sessionMap = new HashMap<>();
    private boolean taskStatus = true;
    private boolean abortOnFirstFailure = true;

    public TaskExecutionPlan(Task task) {
        this.task = task;
        this.treeMap = new TreeMap(task.getStepDataList()
                .stream()
                .filter(taskStepData -> taskStepData.isActive())
                .collect(Collectors.groupingBy(td -> td.getSequence())));
        this.abortOnFirstFailure = task.isAbortOnFirstFailure();
    }

    public Map.Entry<Integer, List<TaskStep>> next() {
        return treeMap.pollFirstEntry();
    }

    public boolean hasNext(){
        return treeMap.firstEntry()!=null;
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
}
