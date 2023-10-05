/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.h2.dao;

import com.user.model.Menu;
import java.util.List;

/**
 *
 * @author Athu Sint
 */
public interface MenuDao {

    Menu save(Menu menu);

    String getMaxDate();

    List<Menu> getMenuTree(String compCode);

    List<Menu> getMenuDynamic(String compCode);

    boolean delete(Menu obj);

}
