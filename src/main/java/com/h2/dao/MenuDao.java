/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.h2.dao;

import com.inventory.model.VRoleMenu;
import com.user.model.Menu;
import java.util.List;

/**
 *
 * @author Athu Sint
 */
public interface MenuDao {

    Menu save(Menu menu);

    String getMaxDate();
    
    List<VRoleMenu> getMenuTree(String roleCode, String compCode);
}
