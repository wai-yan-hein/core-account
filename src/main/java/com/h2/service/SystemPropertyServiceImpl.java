/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.h2.dao.SystemPropertyDao;
import com.user.model.SysProperty;
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
public class SystemPropertyServiceImpl implements SystemPropertyService {

    @Autowired
    private SystemPropertyDao dao;

    @Override
    public SysProperty save(SysProperty pc) {
        return dao.save(pc);
    }

    @Override
    public String getMaxDate() {
        return dao.getMaxDate();
    }

    @Override
    public List<SysProperty> getSystemProperty(String compCode) {
        return dao.getSystemProperty(compCode);
    }

}
