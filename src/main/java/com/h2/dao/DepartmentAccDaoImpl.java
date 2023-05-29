/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.acc.model.DepartmentA;
import com.acc.model.DepartmentAKey;
import com.common.Util1;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public class DepartmentAccDaoImpl extends AbstractDao<DepartmentAKey, DepartmentA> implements DepartmentAccDao {

    @Override
    public DepartmentA save(DepartmentA dep) {
        saveOrUpdate(dep, dep.getKey());
        return dep;
    }

    @Override
    public String getMaxDate() {
        String jpql = "select max(o.updatedDt) from DepartmentA o";
        Date date = getDate(jpql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

    @Override
    public List<DepartmentA> findAll(String compCode) {
        String hsql = "select o from DepartmentA o where o.key.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public DepartmentA find(DepartmentAKey key) {
        return getByKey(key);
    }

}
