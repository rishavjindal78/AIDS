package org.shunya.server.dao;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.shunya.server.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DBDaoImpl implements DBDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<Agent> list() {
        return sessionFactory.getCurrentSession().createCriteria(Agent.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    @Override
    public void saveOrUpdate(Agent agent) {
        sessionFactory.getCurrentSession().saveOrUpdate(agent);
    }

    @Override
    public void saveOrUpdate(TaskRun taskRun) {
        sessionFactory.getCurrentSession().saveOrUpdate(taskRun);
    }

    @Override
    public void saveOrUpdate(Task task) {
        sessionFactory.getCurrentSession().saveOrUpdate(task);
    }

    @Override
    public void saveOrUpdate(TaskStep taskStep) {
        sessionFactory.getCurrentSession().saveOrUpdate(taskStep);
    }

    @Override
    public void saveOrUpdate(TaskStepRun taskStepRun) {
        sessionFactory.getCurrentSession().saveOrUpdate(taskStepRun);
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
