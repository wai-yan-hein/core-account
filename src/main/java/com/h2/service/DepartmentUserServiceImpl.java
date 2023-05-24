/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.h2.dao.DepartmentUserDao;
import com.user.model.DepartmentUser;

/**
 *
 * @author Athu Sint
 */
@Service
@Transactional
public class DepartmentUserServiceImpl implements DepartmentUserService {

    @Autowired
    private DepartmentUserDao deptDao;

    @Override
    public DepartmentUser save(DepartmentUser dept) {
        return deptDao.save(dept);
    }

}
