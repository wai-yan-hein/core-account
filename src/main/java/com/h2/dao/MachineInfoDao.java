/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.h2.dao;

import com.user.model.MachineInfo;
import java.util.List;

/**
 *
 * @author Athu Sint
 */
public interface MachineInfoDao {

    MachineInfo save(MachineInfo mInfo);

    MachineInfo find(Integer macId);

    String getMaxDate();

    MachineInfo getMachineInfo(String machineName);

    List<MachineInfo> findAll();
}
