/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.h2.dao.PrivilegeMenuDao;
import com.user.model.PrivilegeMenu;
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
public class PrivilegeMenuServiceImpl implements PrivilegeMenuService {

    @Autowired
    private PrivilegeMenuDao dao;

    @Override
    public PrivilegeMenu save(PrivilegeMenu pc) {
        return dao.save(pc);
    }

    @Override
    public String getMaxDate() {
        return dao.getMaxDate();
    }

}
