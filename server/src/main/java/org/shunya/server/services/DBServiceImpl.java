package org.shunya.server.services;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.shunya.server.dao.DBDao;
import org.shunya.server.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DBServiceImpl implements DBService {
    @Autowired
    private DBDao DBDao;

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<Agent> listAgents() {
        return DBDao.list();
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<Task> listTasks() {
        return DBDao.getSessionFactory().getCurrentSession().createCriteria(Task.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).addOrder(Order.desc("id")).list();
    }

    @Override
    public List<Team> listTeams() {
        return DBDao.getSessionFactory().getCurrentSession().createCriteria(Team.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).addOrder(Order.desc("id")).list();
    }

    @Override
    public List<User> listUser() {
        return DBDao.getSessionFactory().getCurrentSession().createCriteria(User.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).addOrder(Order.desc("id")).list();
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<Authority> listAuthorities() {
        return DBDao.getSessionFactory().getCurrentSession().createCriteria(Authority.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).addOrder(Order.desc("id")).list();
    }

    @Override
    public void deleteTaskStep(long id) {
        TaskStep taskStepData = getTaskStep(id);
        taskStepData.getTask().getStepDataList().remove(taskStepData);
        DBDao.getSessionFactory().getCurrentSession().delete(taskStepData);
    }

    @Override
    public void deleteTask(long id) {
        Task task = getTask(id);
        List taskData1 = DBDao.getSessionFactory().getCurrentSession().createQuery("select id from TaskRun tr where tr.task = :task").setEntity("task", task).list();
        DBDao.getSessionFactory().getCurrentSession().createQuery("delete from TaskStepRun tr where tr.taskRun.id in (:taskRunIds)").setParameterList("taskRunIds", taskData1).executeUpdate();
        DBDao.getSessionFactory().getCurrentSession().createQuery("delete from TaskRun tr where tr.task = :task").setEntity("task", task).executeUpdate();
        DBDao.getSessionFactory().getCurrentSession().delete(task);
    }

    @Transactional(readOnly = false)
    public void save(Agent agent) {
        DBDao.saveOrUpdate(agent);
    }

    @Transactional(readOnly = false)
    public void save(Task task) {
        DBDao.saveOrUpdate(task);
    }

    @Transactional(readOnly = false)
    public void save(TaskStep taskStepData) {
        DBDao.saveOrUpdate(taskStepData);
    }

    @Transactional(readOnly = false)
    public void save(TaskStepRun taskStepRun) {
        DBDao.saveOrUpdate(taskStepRun);
    }

    @Transactional(readOnly = false)
    public void save(TaskRun taskRun) {
        DBDao.saveOrUpdate(taskRun);
    }

    @Transactional(readOnly = false)
    public void save(Authority authority) {
        DBDao.saveOrUpdate(authority);
    }

    @Transactional(readOnly = false)
    public void save(User user) {
        DBDao.saveOrUpdate(user);
    }

    @Override
    @Transactional(readOnly = false)
    public void save(Team team) {
        DBDao.saveOrUpdate(team);
    }

    @Transactional(readOnly = true)
    public Authority findAuthorityByName(String role) {
        Criteria criteria = DBDao.getSessionFactory().getCurrentSession().createCriteria(Authority.class);
        criteria.add(Restrictions.eq("role", role));
        criteria.setCacheable(true);
        return (Authority) criteria.uniqueResult();
    }

    @Override
    public User findByUsername(String username) {
        return (User) DBDao.getSessionFactory().getCurrentSession()
                .createCriteria(User.class)
                .add(Restrictions.eq("username", username))
                .setCacheable(true)
                .uniqueResult();
    }

    @Transactional(readOnly = true)
    public TaskRun getTaskRun(TaskStepRun taskStepRun) {
        TaskStepRun taskStepRunDb = (TaskStepRun) DBDao.getSessionFactory().getCurrentSession().get(TaskStepRun.class, taskStepRun.getId());
        TaskRun taskRun = taskStepRunDb.getTaskRun();
        return taskRun;
    }

    @Transactional(readOnly = true)
    public TaskRun getTaskRun(long id) {
        TaskRun taskRun = (TaskRun) DBDao.getSessionFactory().getCurrentSession().get(TaskRun.class, id);
        if (taskRun.getTaskStepRuns() != null)
            Hibernate.initialize(taskRun.getTaskStepRuns());
        return taskRun;
    }

    @Override
    public TaskStepRun getTaskStepRun(long id) {
        return (TaskStepRun) DBDao.getSessionFactory().getCurrentSession().get(TaskStepRun.class, id);
    }

    @Transactional(readOnly = true)
    public Task getTask(long id) {
//        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return (Task) DBDao.getSessionFactory().getCurrentSession().get(Task.class, id);
    }

    @Override
    public Agent getAgent(long id) {
        return (Agent) DBDao.getSessionFactory().getCurrentSession().get(Agent.class, id);
    }

    @Override
    public Team getTeam(long id) {
        return (Team) DBDao.getSessionFactory().getCurrentSession().get(Team.class, id);
    }

    @Override
    public User getUser(long id) {
        return (User) DBDao.getSessionFactory().getCurrentSession().get(User.class, id);
    }

    @Override
    public TaskStep getTaskStep(long id) {
        return (TaskStep) DBDao.getSessionFactory().getCurrentSession().get(TaskStep.class, id);
    }

    @Override
    public List<TaskRun> findTaskHistoryForTaskId(long taskId) {
        Criteria criteria = DBDao.getSessionFactory().getCurrentSession().createCriteria(TaskRun.class);
        criteria.setFetchSize(30);
        criteria.add(Restrictions.eq("task.id", taskId));
        criteria.addOrder(Order.desc("id"));
        criteria.setMaxResults(30);
        criteria.setCacheable(true);
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return criteria.list();
    }

    @Override
    public List<TaskRun> findTaskHistory() {
        Criteria criteria = DBDao.getSessionFactory().getCurrentSession().createCriteria(TaskRun.class);
        criteria.setFetchSize(30);
        criteria.addOrder(Order.desc("id"));
        criteria.setMaxResults(30);
        criteria.setCacheable(true);
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return criteria.list();
    }
}