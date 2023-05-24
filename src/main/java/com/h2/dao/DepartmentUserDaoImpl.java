/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.user.model.DepartmentUser;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Athu Sint
 */
@Repository
public class DepartmentUserDaoImpl extends AbstractDao<Integer, DepartmentUser> implements DepartmentUserDao {

    @Override
    public DepartmentUser save(DepartmentUser dept) {
        saveOrUpdate(dept, dept.getDeptId());
        return dept;
    }

}
