package org.shunya.server.services;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.shunya.server.dao.AgentDao;
import org.shunya.shared.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AgentDBService implements AgentService {
    @Autowired
    private AgentDao agentDao;

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<Agent> list() {
        return agentDao.list();
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<TaskData> listTasks() {
        return agentDao.getSessionFactory().getCurrentSession().createCriteria(TaskData.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).addOrder(Order.desc("id")).list();
    }

    @Override
    public void deleteTaskStep(long id) {
        TaskStepData taskStepData = getTaskStepData(id);
        taskStepData.getTaskData().getStepDataList().remove(taskStepData);
        agentDao.getSessionFactory().getCurrentSession().delete(taskStepData);
    }

    @Transactional(readOnly = false)
    public void save(Agent agent) {
        agentDao.saveOrUpdate(agent);
    }

    @Transactional(readOnly = false)
    public void save(TaskData taskData) {
        agentDao.saveOrUpdate(taskData);
    }

    @Transactional(readOnly = false)
    public void save(TaskStepData taskStepData) {
        agentDao.saveOrUpdate(taskStepData);
    }

    @Transactional(readOnly = false)
    public void save(TaskStepRun taskStepRun) {
        agentDao.saveOrUpdate(taskStepRun);
    }

    @Transactional(readOnly = false)
    public void save(TaskRun taskRun) {
        agentDao.saveOrUpdate(taskRun);
    }

    @Transactional(readOnly = true)
    public TaskRun getTaskRun(TaskStepRun taskStepRun) {
        TaskStepRun taskStepRunDb = (TaskStepRun) agentDao.getSessionFactory().getCurrentSession().get(TaskStepRun.class, taskStepRun.getId());
        TaskRun taskRun = taskStepRunDb.getTaskRun();
        return taskRun;
    }

    @Transactional(readOnly = true)
    public TaskRun getTaskRun(long id) {
        TaskRun taskRun = (TaskRun) agentDao.getSessionFactory().getCurrentSession().get(TaskRun.class, id);
        if (taskRun.getTaskStepRuns() != null)
            Hibernate.initialize(taskRun.getTaskStepRuns());
        return taskRun;
    }

    @Transactional(readOnly = true)
    public TaskData getTaskData(long id) {
//        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return (TaskData) agentDao.getSessionFactory().getCurrentSession().get(TaskData.class, id);
    }

    @Override
    public Agent getAgent(long id) {
        return (Agent) agentDao.getSessionFactory().getCurrentSession().get(Agent.class, id);
    }

    @Override
    public TaskStepData getTaskStepData(long id) {
        return (TaskStepData) agentDao.getSessionFactory().getCurrentSession().get(TaskStepData.class, id);
    }

    @Override
    public List<TaskRun> findTaskHistoryForTaskId(long taskId) {
        Criteria criteria = agentDao.getSessionFactory().getCurrentSession().createCriteria(TaskRun.class);
        criteria.setFetchSize(30);
        criteria.add(Restrictions.eq("taskData.id", taskId));
        criteria.addOrder(Order.desc("id"));
        criteria.setMaxResults(30);
        criteria.setCacheable(true);
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return criteria.list();
    }
}