/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.h2.service;

import com.user.model.RoleProperty;
import java.util.List;

/**
 *
 * @author Athu Sint
 */
public interface RolePropertyService {

    RoleProperty save(RoleProperty pc);

    String getMaxDate();

    List<RoleProperty> getRoleProperty(String roleCode,String compCode);
}
