package org.shunya.server.dao;

import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.io.Serializable;
import java.util.List;

public interface GenericDao<E, I extends Serializable> {

    Session getCurrentSession();

    E findById(I id);

    void saveOrUpdate(E e);

    void delete(E e);

    List<E> findByCriteria(Order order, Criterion... criterions);

    List<E> findAll();

    List<E> findAllPaginated(int page, int size, Order order, Criterion... criterions);

    List<E> findByExample(E example) throws Exception;

    List<E> findByCriteria(Criterion... criterions);

    long countRows(Criterion... criterion);
}
