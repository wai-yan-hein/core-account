/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.h2.service;

import com.inventory.model.VRoleMenu;
import com.user.model.Menu;
import java.util.List;

/**
 *
 * @author Athu Sint
 */
public interface MenuService {

    Menu save(Menu menu);

    String getMaxDate();

    List<Menu> getMenuTree(String compCode);

    List<Menu> getMenuDynamic(String compCode);
}
