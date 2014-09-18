package org.shunya.server.services;

import org.shunya.shared.model.*;

import java.util.List;

public interface AgentService {
    public List<Agent> list();
    public List<TaskData> listTasks();
    public void deleteTaskStep(long id);
    public void save(Agent agent);
    public void save(TaskData taskData);
    public void save(TaskStepData taskStepData);
    public void save(TaskStepRun taskStepRun);
    public void save(TaskRun taskRun);
    public TaskRun getTaskRun(TaskStepRun taskStepRun);
    public TaskRun getTaskRun(long id);
    public TaskData getTaskData(long id);
    public Agent getAgent(long id);
    public TaskStepData getTaskStepData(long id);

    List<TaskRun> findTaskHistoryForTaskId(long taskId);
}
