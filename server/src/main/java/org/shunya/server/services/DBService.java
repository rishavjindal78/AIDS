package org.shunya.server.services;

import org.shunya.server.model.*;

import java.util.List;

public interface DBService {
    List<Agent> list();
    List<Task> listTasks();
    List<Authority> listAuthorities();
    void deleteTaskStep(long id);
    void deleteTask(long id);
    void save(Agent agent);
    void save(Task task);
    void save(TaskStep taskStep);
    void save(TaskStepRun taskStepRun);
    void save(TaskRun taskRun);
    void save(Authority authority);
    void save(User user);
    TaskRun getTaskRun(TaskStepRun taskStepRun);
    TaskRun getTaskRun(long id);
    TaskStepRun getTaskStepRun(long id);
    Task getTask(long id);
    Agent getAgent(long id);
    TaskStep getTaskStep(long id);
    List<TaskRun> findTaskHistoryForTaskId(long taskId);
}
