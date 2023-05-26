/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;

/**
 *
 * @author Lenovo
 * @param <PK>
 * @param <T>
 */
@Slf4j
public abstract class AbstractDao<PK extends Serializable, T> {

    private final Class<T> persistentClass;

    @SuppressWarnings("unchecked")
    public AbstractDao() {
        this.persistentClass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private DataSource dataSource;
    @PersistenceContext
    private EntityManager entityManager;

    public T getByKey(PK key) {
        return entityManager.find(persistentClass, key);
    }
    
    public void persist(T entity) {
        entityManager.persist(entity);
    }

    public void saveOrUpdate(T entity, PK pk) {
        T t = entityManager.find(persistentClass, pk);
        if (t == null) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }

    }

    public void update(T entity) {
        entityManager.merge(entity);
    }

    public List<T> findHSQL(String hsql) {
        return entityManager.createQuery(hsql, persistentClass).getResultList();
    }

    public void execSql(String... sql) {
        for (String s : sql) {
            jdbcTemplate.execute(s);
        }
    }

    public void remove(PK pk) {
        T byKey = getByKey(pk);
        if (byKey != null) {
            entityManager.remove(byKey);
        }
    }

    public List<Map<String, Object>> getList(String sql) {
        return jdbcTemplate.queryForList(sql);
    }

    public Date getDate(String jpql) {
        return entityManager.createQuery(jpql, Date.class).getSingleResult();
    }

   

    public ResultSet getResult(String sql) {
        return jdbcTemplate.execute((ConnectionCallback<ResultSet>) con -> {
            Statement stmt = con.createStatement();
            return stmt.executeQuery(sql);
        });
    }
}
