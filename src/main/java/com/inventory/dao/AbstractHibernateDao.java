/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.dao;

/**
 *
 * @author Lenovo
 */
import com.google.common.base.Preconditions;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.Serializable;
import java.util.List;
import org.hibernate.query.NativeQuery;

@SuppressWarnings("unchecked")
public abstract class AbstractHibernateDao<T extends Serializable> {

    private Class<T> clazz;

    @Autowired
    protected SessionFactory sessionFactory;

    protected final void setClazz(final Class<T> clazzToSet) {
        clazz = Preconditions.checkNotNull(clazzToSet);
    }

    // API
    public T findOne(final long id) {
        return (T) getCurrentSession().get(clazz, id);
    }

    public List<T> findAll() {
        return getCurrentSession().createQuery("from " + clazz.getName()).list();
    }

    public T create(final T entity) {
        Preconditions.checkNotNull(entity);
        getCurrentSession().saveOrUpdate(entity);
        return entity;
    }

    public T update(final T entity) {
        Preconditions.checkNotNull(entity);
        return (T) getCurrentSession().merge(entity);
    }

    public void delete(final T entity) {
        Preconditions.checkNotNull(entity);
        getCurrentSession().delete(entity);
    }

    public void deleteById(final long entityId) {
        final T entity = findOne(entityId);
        Preconditions.checkState(entity != null);
        delete(entity);
    }

    public List getList(String sqlQuery) {
        NativeQuery query = getCurrentSession().createSQLQuery(sqlQuery);
        return query.list();
    }

    public int execQuery(String sqlQuery) {
        NativeQuery query = getCurrentSession().createSQLQuery(sqlQuery);
        return query.executeUpdate();
    }

    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
}
