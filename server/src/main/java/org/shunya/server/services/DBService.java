package org.shunya.server.services;

import org.shunya.server.model.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DBService {
    List<Agent> listAgents();
    List<Task> listTasks();
    List<Team> listTeams();
    List<User> listUser();
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
    @Transactional(readOnly = false)
    void save(Team team);
    Authority findAuthorityByName(String role);
    User findByUsername(String username);
    TaskRun getTaskRun(TaskStepRun taskStepRun);
    TaskRun getTaskRun(long id);
    TaskStepRun getTaskStepRun(long id);
    Task getTask(long id);
    Agent getAgent(long id);
    Team getTeam(long id);
    User getUser(long id);
    TaskStep getTaskStep(long id);
    List<TaskRun> findTaskHistoryForTaskId(long taskId);
    List<TaskRun> findTaskHistory();
}
