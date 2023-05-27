/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.h2.service;

import com.inventory.model.MachineInfo;
import java.util.List;

/**
 *
 * @author Athu Sint
 */
public interface MachineInfoService {

    MachineInfo save(MachineInfo mInfo);

    String getMaxDate();
    
    MachineInfo getMachineInfo(String machineName);
    
    List<MachineInfo> findAll();
}
