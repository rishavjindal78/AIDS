package org.shunya.server.dao;

import org.hibernate.SessionFactory;
import org.shunya.shared.model.*;

import java.util.List;

public interface DBDao {
    public List<Agent> list();
    public void saveOrUpdate(Agent agent);
    public void saveOrUpdate(TaskRun taskRun);
    public void saveOrUpdate(TaskData taskData);
    public void saveOrUpdate(TaskStepData taskStepData);
    public void saveOrUpdate(TaskStepRun taskStepRun);

    SessionFactory getSessionFactory();
}
