package org.shunya.server.services;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.shunya.server.dao.DBDao;
import org.shunya.shared.model.*;
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
    public List<Agent> list() {
        return DBDao.list();
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<TaskData> listTasks() {
        return DBDao.getSessionFactory().getCurrentSession().createCriteria(TaskData.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).addOrder(Order.desc("id")).list();
    }

    @Override
    public List<TaskMetadata> lisTaskMetadata() {
        return DBDao.getSessionFactory().getCurrentSession().createCriteria(TaskMetadata.class)
                .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).setMaxResults(20).setCacheable(true).addOrder(Order.asc("name")).list();
    }

    @Override
    public void deleteTaskStep(long id) {
        TaskStepData taskStepData = getTaskStepData(id);
        taskStepData.getTaskData().getStepDataList().remove(taskStepData);
        DBDao.getSessionFactory().getCurrentSession().delete(taskStepData);
    }

    @Override
    public void deleteTask(long id) {
        TaskData taskData = getTaskData(id);
        List taskData1 = DBDao.getSessionFactory().getCurrentSession().createQuery("select id from TaskRun tr where tr.taskData = :taskData").setEntity("taskData", taskData).list();
        DBDao.getSessionFactory().getCurrentSession().createQuery("delete from TaskStepRun tr where tr.taskRun.id in (:taskRunIds)").setParameterList("taskRunIds", taskData1).executeUpdate();
        DBDao.getSessionFactory().getCurrentSession().createQuery("delete from TaskRun tr where tr.taskData = :taskData").setEntity("taskData", taskData).executeUpdate();
        DBDao.getSessionFactory().getCurrentSession().delete(taskData);
    }

    @Transactional(readOnly = false)
    public void save(Agent agent) {
        DBDao.saveOrUpdate(agent);
    }

    @Transactional(readOnly = false)
    public void save(TaskData taskData) {
        DBDao.saveOrUpdate(taskData);
    }

    @Transactional(readOnly = false)
    public void save(TaskStepData taskStepData) {
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
    public TaskData getTaskData(long id) {
//        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return (TaskData) DBDao.getSessionFactory().getCurrentSession().get(TaskData.class, id);
    }

    @Override
    public Agent getAgent(long id) {
        return (Agent) DBDao.getSessionFactory().getCurrentSession().get(Agent.class, id);
    }

    @Override
    public TaskStepData getTaskStepData(long id) {
        return (TaskStepData) DBDao.getSessionFactory().getCurrentSession().get(TaskStepData.class, id);
    }

    @Override
    public TaskMetadata getTaskMetadata(long id) {
        return (TaskMetadata) DBDao.getSessionFactory().getCurrentSession().get(TaskMetadata.class, id);
    }

    @Override
    public List<TaskRun> findTaskHistoryForTaskId(long taskId) {
        Criteria criteria = DBDao.getSessionFactory().getCurrentSession().createCriteria(TaskRun.class);
        criteria.setFetchSize(30);
        criteria.add(Restrictions.eq("taskData.id", taskId));
        criteria.addOrder(Order.desc("id"));
        criteria.setMaxResults(30);
        criteria.setCacheable(true);
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return criteria.list();
    }
}