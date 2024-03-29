/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.h2.dao;

import com.user.model.SysProperty;
import java.util.List;

/**
 *
 * @author Athu Sint
 */
public interface SystemPropertyDao {

    SysProperty save(SysProperty systemProperty);

    String getMaxDate();

    List<SysProperty> getSystemProperty(String compCode);
}
