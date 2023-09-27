/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.h2.dao.RolePropertyDao;
import com.user.model.RoleProperty;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Athu Sint
 */
@Lazy
@Service
@Transactional
public class RolePropertyServiceImpl implements RolePropertyService {

    @Autowired
    private RolePropertyDao dao;

    @Override
    public RoleProperty save(RoleProperty pc) {
        return dao.save(pc);
    }

    @Override
    public String getMaxDate() {
        return dao.getMaxDate();
    }

    @Override
    public List<RoleProperty> getRoleProperty(String roleCode, String compCode) {
        return dao.getRoleProperty(roleCode, compCode);
    }

}
