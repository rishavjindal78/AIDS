package org.shunya.server;

import org.shunya.shared.model.TaskData;
import org.shunya.shared.model.TaskStepData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TaskExecutionPlan {
    private final TaskData taskData;
    private final TreeMap<Integer, List<TaskStepData>> treeMap;
    private Map<String, Object> sessionMap = new HashMap<>();
    private boolean taskStatus = true;
    private boolean abortOnFirstFailure = true;

    public TaskExecutionPlan(TaskData taskData) {
        this.taskData = taskData;
        this.treeMap = new TreeMap(taskData.getStepDataList()
                .stream()
                .filter(taskStepData -> taskStepData.isActive())
                .collect(Collectors.groupingBy(td -> td.getSequence())));
        this.abortOnFirstFailure = taskData.isAbortOnFirstFailure();
    }

    public Map.Entry<Integer, List<TaskStepData>> next() {
        return treeMap.pollFirstEntry();
    }

    public boolean hasNext(){
        return treeMap.firstEntry()!=null;
    }

    public TaskData getTaskData() {
        return taskData;
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
