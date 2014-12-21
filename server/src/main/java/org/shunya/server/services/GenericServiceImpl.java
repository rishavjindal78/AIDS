package org.shunya.server.services;

import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

@Transactional(readOnly = true)
public abstract class GenericServiceImpl<T, I extends Serializable> implements GenericService<T, I> {
    @Override
    @Transactional(readOnly = false)
    public void save(T obj) {
        getDao().saveOrUpdate(obj);
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(T obj) {
        getDao().delete(obj);
    }

    @Override
    public int countRows() {
        return (int) getDao().countRows();
    }

    @Override
    public List<T> findAll() {
        return getDao().findAll();
    }

    @Override
    public T findById(I id) {
        return (T) getDao().findById(id);
    }

}
