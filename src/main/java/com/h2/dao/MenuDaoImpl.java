/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.h2.dao;

import com.common.Util1;
import com.inventory.model.VRoleMenu;
import com.user.model.Menu;
import com.user.model.MenuKey;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Athu Sint
 */
@Repository
public class MenuDaoImpl extends AbstractDao<MenuKey, Menu> implements MenuDao {

    @Override
    public Menu save(Menu menu) {
        saveOrUpdate(menu, menu.getKey());
        return menu;
    }

    @Override
    public String getMaxDate() {
        String sql = "select max(o.updatedDate) from Menu o";
        Date date = getDate(sql);
        return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
    }
    
    @Override
    public List<Menu> getMenuTree(String compCode) {
        List<Menu> menus = getMenuChild("1", compCode);
        if (!menus.isEmpty()) {
            for (Menu m : menus) {
                getMenu(m);
            }
        }
        return menus;
    }

    private void getMenu(Menu parent) {
        List<Menu> menus = getMenuChild(parent.getKey().getMenuCode(), parent.getKey().getCompCode());
        parent.setChild(menus);
        if (!menus.isEmpty()) {
            for (Menu m : menus) {
                getMenu(m);
            }
        }
    }

    private List<Menu> getMenuChild(String parentCode, String compCode) {
        String sql = "select o from Menu o where o.parentMenuCode = '" + parentCode
                + "' and o.key.compCode = '" + compCode + "' order by o.orderBy";
        return findHSQL(sql);
    }

}
