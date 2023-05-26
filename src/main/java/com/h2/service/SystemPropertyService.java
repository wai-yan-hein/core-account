/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.service;

import com.user.model.SysProperty;

/**
 *
 * @author Lenovo
 */
public interface SystemPropertyService {

    SysProperty save(SysProperty pc);

    String getMaxDate();
}
