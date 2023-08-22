/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.h2.dao.VouStatusDao;
import com.inventory.model.VouStatus;
import com.inventory.model.VouStatusKey;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Lenovo
 */
@Lazy
@Service
@Transactional
public class VouStatusServiceImpl implements VouStatusService {

    @Autowired
    private VouStatusDao dao;

    @Override
    public VouStatus save(VouStatus cat) {
        return dao.save(cat);
    }

    @Override
    public List<VouStatus> findAll(String compCode) {
        return dao.findAll(compCode);
    }

    @Override
    public String getMaxDate() {
        return dao.getMaxDate();
    }

    @Override
    public VouStatus find(VouStatusKey key) {
        return dao.find(key);
    }

}
