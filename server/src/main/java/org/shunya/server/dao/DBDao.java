package org.shunya.server.dao;

import org.hibernate.SessionFactory;
import org.shunya.server.model.*;

import java.util.List;

public interface DBDao {
    List<Agent> list();
    void saveOrUpdate(Agent agent);
    void saveOrUpdate(TaskRun taskRun);
    void saveOrUpdate(Task task);
    void saveOrUpdate(TaskStep taskStep);
    void saveOrUpdate(TaskStepRun taskStepRun);

    void saveOrUpdate(Authority authority);

    SessionFactory getSessionFactory();

    void saveOrUpdate(User user);
}
