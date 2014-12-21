package org.shunya.server.services;

import org.shunya.server.dao.GenericDao;

import java.io.Serializable;
import java.util.List;

public interface GenericService<T, I extends Serializable> {
    T findById(I id);
    List<T> findAll();
    List<T> findPaginated(int page, int size);
    int countRows();
    void save(T obj);
    void delete(T obj);
    GenericDao getDao();
}
