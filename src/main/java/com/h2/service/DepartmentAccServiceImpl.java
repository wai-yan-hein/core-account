/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.acc.model.DepartmentA;
import com.acc.model.DepartmentAKey;
import com.h2.dao.DepartmentAccDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Lenovo
 */
@Service
@Transactional
public class DepartmentAccServiceImpl implements DepartmentAccService {

    @Autowired
    private DepartmentAccDao dao;

    @Override
    public DepartmentA save(DepartmentA dep) {
        return dao.save(dep);
    }

    @Override
    public List<DepartmentA> findAll(String compCode) {
        return dao.findAll(compCode);
    }

    @Override
    public String getMaxDate() {
        return dao.getMaxDate();
    }

    @Override
    public DepartmentA find(DepartmentAKey key) {
        return dao.find(key);
    }

}
