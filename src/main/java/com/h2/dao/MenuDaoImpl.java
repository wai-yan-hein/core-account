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
    public List<VRoleMenu> getMenuTree(String roleCode, String compCode) {
        List<VRoleMenu> roles = getMenuChild(roleCode, "1", compCode);
        if (!roles.isEmpty()) {
            for (VRoleMenu role : roles) {
                getRoleMenuChild(role);
            }
        }
        return roles;
    }

    private void getRoleMenuChild(VRoleMenu parent) {
        List<VRoleMenu> roles = getMenuChild(parent.getRoleCode(), parent.getMenuCode(), parent.getCompCode());
        parent.setChild(roles);
        if (!roles.isEmpty()) {
            for (VRoleMenu role : roles) {
                getRoleMenuChild(role);
            }
        }
    }

    private List<VRoleMenu> getMenuChild(String roleCode, String parent, String compCode) {
        String sql = """
                     SELECT o.menu_code AS menu_code, o.role_code AS role_code, o.comp_code AS comp_code, o.allow AS allow, 
                     o.menu_name AS menu_name, o.menu_url AS menu_url, o.menu_type AS menu_type, o.menu_class AS menu_class, 
                     o.account AS account, o.parent_menu_code AS parent_menu_code, o.order_by AS order_by
                     FROM (
                       SELECT p.menu_code AS menu_code, p.role_code AS role_code, p.comp_code AS comp_code, p.allow AS allow, 
                     m.menu_name AS menu_name, m.menu_url AS menu_url, m.menu_type AS menu_type, m.menu_class AS menu_class, 
                     m.account AS account, m.parent_menu_code AS parent_menu_code, m.order_by AS order_by
                       FROM privilege_menu p
                       JOIN menu m ON p.menu_code = m.menu_code AND p.comp_code = m.comp_code
                     ) o
                     where o.role_code = '""" + roleCode + "' and o.parent_menu_code = '" + parent + "' and o.menu_type = 'Menu'\n"
                + "and o.comp_code = '" + compCode + "' order by o.order_by";
        List<VRoleMenu> vList = new ArrayList<>();
        VRoleMenu vMenu;
        ResultSet rs = getResult(sql);
        if (rs != null) {
            try {
                while (rs.next()) {
                    vMenu = new VRoleMenu();
                    vMenu.setMenuCode(rs.getString("menu_code"));
                    vMenu.setRoleCode(rs.getString("role_code"));
                    vMenu.setCompCode(rs.getString("comp_code"));
                    vMenu.setAllow(rs.getBoolean("allow"));
                    vMenu.setMenuName(rs.getString("menu_name"));
                    vMenu.setMenuUrl(rs.getString("menu_url"));
                    vMenu.setMenuType(rs.getString("menu_type"));
                    vMenu.setMenuClass(rs.getString("menu_class"));
                    vMenu.setAccount(rs.getString("account"));
                    vMenu.setParentMenuCode(rs.getString("parent_menu_code"));
                    vMenu.setOrderBy(rs.getInt("order_by"));
                    vList.add(vMenu);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return vList;
    }

}
