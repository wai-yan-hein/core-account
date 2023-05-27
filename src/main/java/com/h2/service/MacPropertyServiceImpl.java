/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.h2.dao.MacPropertyDao;
import com.user.model.MachineProperty;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Athu Sint
 */
@Service
@Transactional
public class MacPropertyServiceImpl implements MacPropertyService {

    @Autowired
    private MacPropertyDao dao;

    @Override
    public MachineProperty save(MachineProperty mProp) {
        return dao.save(mProp);
    }

    @Override
    public String getMaxDate() {
        return dao.getMaxDate();
    }

    @Override
    public List<MachineProperty> getMacProperty(Integer macId) {
        return dao.getMacProperty(macId);
    }

}
