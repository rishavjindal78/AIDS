package org.shunya.server.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public abstract class GenericDaoImpl<E, I extends Serializable> implements GenericDao<E, I> {
    protected Class<E> entityClass;

    public GenericDaoImpl() {
        Type t = getClass().getGenericSuperclass();
        ParameterizedType pt = (ParameterizedType) t;
        entityClass = (Class<E>) pt.getActualTypeArguments()[0];
    }

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public E findById(I id) {
        E entity = (E) getCurrentSession().get(entityClass, id);
//        getCurrentSession().refresh(entity);    if required
        return entity;
    }

    @Override
    public void saveOrUpdate(E e) {
        getCurrentSession().saveOrUpdate(e);
    }

    @Override
    public void delete(E e) {
        getCurrentSession().delete(e);
    }

    @Override
    public List<E> findByCriteria(Criterion... criterions) {
        Criteria criteria = getCurrentSession().createCriteria(entityClass);
        for (Criterion criterion : criterions) {
            criteria.add(criterion);
        }
        criteria.setCacheable(true);
        return criteria.list();
    }

    @Override
    public List<E> findByCriteria(Order order, Criterion... criterions) {
        Criteria criteria = getCurrentSession().createCriteria(entityClass);
        criteria.setFetchSize(100);
        for (Criterion criterion : criterions) {
            criteria.add(criterion);
        }
        criteria.addOrder(order);
        criteria.setMaxResults(100);
        criteria.setCacheable(true);
        return criteria.list();
    }

    @Override
    public List<E> findAll() {
        Criteria criteria = getCurrentSession().createCriteria(entityClass);
//        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return criteria.list();
    }

    @Override
    public List<E> findAllPaginated(int page, int size, Order order, Criterion... criterions) {
        Criteria criteria = getCurrentSession().createCriteria(entityClass);
        criteria.setFetchSize(50);
        criteria.addOrder(order);
        for (Criterion criterion : criterions) {
            if (criterion != null) {
                criteria.add(criterion);
            }
        }
        criteria.setFirstResult(page * size);
        criteria.setMaxResults(size);
        criteria.setCacheable(true);
        return criteria.list();
    }

    @Override
    public List<E> findByExample(E example) throws Exception {
        return getCurrentSession().createCriteria(entityClass).add(Example.create(example)).list();
    }

    @Override
    public long countRows(Criterion... criterion) {
        final Criteria criteria = getCurrentSession()
                .createCriteria(entityClass);
        for (Criterion tmp : criterion) {
            criteria.add(tmp);
        }
        return (long) criteria.setProjection(Projections.rowCount()).uniqueResult();
    }
}
