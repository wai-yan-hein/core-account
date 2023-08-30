/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.common.Util1;
import com.user.model.DepartmentUser;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Athu Sint
 */
@Slf4j
@Repository
public class DepartmentUserDaoImpl extends AbstractDao<Integer, DepartmentUser> implements DepartmentUserDao {

    @Override
    public DepartmentUser save(DepartmentUser dept) {
        saveOrUpdate(dept, dept.getKey().getDeptId());
        return dept;
    }

    @Override
    public String getMaxDate() {
        String jpql = "select max(o.updatedDate) from DepartmentUser o";
        LocalDateTime date = getDate(jpql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }

    @Override
    public DepartmentUser findById(Integer id) {
        return getByKey(id);
    }

    @Override
    public List<DepartmentUser> findAll(Boolean active) {
        String sql;
        if (active) {
            sql = "select o from DepartmentUser o where o.deleted =false and o.active =true";
        } else {
            sql = "select o from DepartmentUser o where o.deleted =false";
        }
        return findHSQL(sql);
    }

}
