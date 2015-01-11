package org.shunya.server.services;

import org.shunya.server.model.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DBService {
    List<Agent> listAgents();
    List<Agent> listAgentsByTeam(long teamId);
    List<Task> listTasksByTeam(long teamId);

    List<TaskRun> findRunningTasks();

    List<Task> listTasks();
    List<Team> listTeams();
    List<User> listUser();
    List<Authority> listAuthorities();
    void deleteTaskStep(long id);
    void deleteTask(long id);
    void deleteTaskRun(long id);
    void deleteTaskStepRun(long id);
    void save(Agent agent);
    void save(Task task);
    void save(TaskStep taskStep);
    void save(TaskStepRun taskStepRun);
    void save(TaskRun taskRun);
    void save(Authority authority);
    void save(User user);
    void update(User user);
    @Transactional(readOnly = false)
    void save(Team team);

    @Transactional(readOnly = false)
    void update(Team team);

    Authority findAuthorityByName(String role);
    User findUserByUsername(String username);
    User findUserByTelegramId(int telegramId);
    TaskRun getTaskRun(TaskStepRun taskStepRun);
    TaskRun getTaskRun(long id);
    TaskStepRun getTaskStepRun(long id);
    Task getTask(long id);
    Agent getAgent(long id);
    Team findTeamById(long id);
    List<Team> findTeamByChatId(int telegramId);
    User getUser(long id);
    TaskStep getTaskStep(long id);
    List<TaskRun> findTaskHistoryForTaskId(long taskId);
    List<TaskRun> findTaskHistoryByTeam(long teamId);
    List<TaskRun> findTaskHistoryByAge(int ageInDays);
}
