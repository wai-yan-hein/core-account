/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.h2.service;

import com.user.model.MachineProperty;
import java.util.List;

/**
 *
 * @author Athu Sint
 */
public interface MacPropertyService {

    MachineProperty save(MachineProperty mProp);

    String getMaxDate();

    List<MachineProperty> getMacProperty(Integer macId);
}
