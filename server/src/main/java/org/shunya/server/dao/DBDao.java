package org.shunya.server.dao;

import org.hibernate.SessionFactory;
import org.shunya.server.model.*;

import java.util.List;

public interface DBDao {
    public List<Agent> list();
    public void saveOrUpdate(Agent agent);
    public void saveOrUpdate(TaskRun taskRun);
    public void saveOrUpdate(Task task);
    public void saveOrUpdate(TaskStep taskStep);
    public void saveOrUpdate(TaskStepRun taskStepRun);

    SessionFactory getSessionFactory();
}
