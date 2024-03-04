/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.h2.dao.MachineInfoDao;
import com.user.model.MachineInfo;
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
public class MachineInfoServiceImpl implements MachineInfoService {

    @Autowired
    private MachineInfoDao dao;

    @Override
    public MachineInfo save(MachineInfo mInfo) {
        return dao.save(mInfo);
    }

    @Override
    public String getMaxDate() {
        return dao.getMaxDate();
    }

    @Override
    public MachineInfo getMachineInfo(String machineName) {
        return dao.getMachineInfo(machineName);
    }

    @Override
    public List<MachineInfo> findAll() {
        return dao.findAll();
    }

    @Override
    public MachineInfo find(Integer macId) {
        return dao.find(macId);
    }

}
