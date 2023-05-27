/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.h2.service;

import com.inventory.model.AppRole;
import java.util.List;

/**
 *
 * @author Athu Sint
 */
public interface RoleService {

    AppRole save(AppRole pc);

    String getMaxDate();
    
    List<AppRole> findAll(String compCode);
}
