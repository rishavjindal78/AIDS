package org.shunya.server.services;

import org.shunya.server.model.*;

import java.util.List;

public interface DBService {
    public List<Agent> list();
    public List<Task> listTasks();
    public void deleteTaskStep(long id);
    public void deleteTask(long id);
    public void save(Agent agent);
    public void save(Task taskData);
    public void save(TaskStep taskStepData);
    public void save(TaskStepRun taskStepRun);
    public void save(TaskRun taskRun);
    public TaskRun getTaskRun(TaskStepRun taskStepRun);
    public TaskRun getTaskRun(long id);
    public TaskStepRun getTaskStepRun(long id);
    public Task getTaskData(long id);
    public Agent getAgent(long id);
    public TaskStep getTaskStep(long id);
    List<TaskRun> findTaskHistoryForTaskId(long taskId);
}
