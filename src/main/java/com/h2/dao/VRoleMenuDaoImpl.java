package com.h2.dao;

import com.inventory.model.VRoleMenu;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class VRoleMenuDaoImpl extends AbstractDao<String, VRoleMenu> implements VRoleMenuDao {

    @Override
    public List<VRoleMenu> getMenu(String roleCode, String parentCode, String compCode, boolean privilege) {
        String sql = """
                select o.menu_code,o.role_code,o.comp_code,o.allow,
                o.menu_name,o.menu_name_mm,o.menu_url,o.menu_type,o.menu_class,
                o.account,o.parent_menu_code,o.order_by,o.menu_version
                from(
                select p.menu_code,p.role_code,p.comp_code,p.allow,
                m.menu_name,m.menu_name_mm,m.menu_url,m.menu_type,m.menu_class,
                m.account,m.parent_menu_code,m.order_by,m.menu_version
                from privilege_menu p
                join menu m on p.menu_code=m.menu_code
                and p.comp_code=m.comp_code
                )o
                where o.role_code=?
                and o.comp_code=?
                and o.parent_menu_code=?
                and o.menu_type='Menu'
                and (allow = ? or false = ?)
                order by o.order_by""";
        List<VRoleMenu> vList = new ArrayList<>();
        VRoleMenu vMenu;
        ResultSet rs = getResult(sql, roleCode, compCode, parentCode, privilege, privilege);
        if (rs != null) {
            try {
                while (rs.next()) {
                    vMenu = new VRoleMenu();
                    vMenu.setMenuCode(rs.getString("menu_code"));
                    vMenu.setRoleCode(rs.getString("role_code"));
                    vMenu.setCompCode(rs.getString("comp_code"));
                    vMenu.setAllow(rs.getBoolean("allow"));
                    vMenu.setMenuName(rs.getString("menu_name"));
                    vMenu.setMenuNameMM(rs.getString("menu_name_mm"));
                    vMenu.setMenuUrl(rs.getString("menu_url"));
                    vMenu.setMenuType(rs.getString("menu_type"));
                    vMenu.setMenuClass(rs.getString("menu_class"));
                    vMenu.setAccount(rs.getString("account"));
                    vMenu.setParentMenuCode(rs.getString("parent_menu_code"));
                    vMenu.setOrderBy(rs.getInt("order_by"));
                    vMenu.setMenuVersion(rs.getInt("menu_version"));
                    vList.add(vMenu);
                }
            } catch (SQLException e) {
                log.error("getMenu: " + e.getMessage());
            }
        }
        return vList;
    }

    @Override
    public List<VRoleMenu> getReport(String roleCode, String menuClass, String compCode) {
        String sql = """
                select o.menu_code,o.role_code,o.comp_code,o.allow,
                o.menu_name,o.menu_url,o.menu_type,o.menu_class,
                o.account,o.parent_menu_code,o.order_by
                from(
                select p.menu_code,p.role_code,p.comp_code,p.allow,
                m.menu_name,m.menu_url,m.menu_type,m.menu_class,
                m.account,m.parent_menu_code,m.order_by
                from privilege_menu p
                join menu m on p.menu_code=m.menu_code
                and p.comp_code=m.comp_code
                )o
                where o.role_code=? and o.menu_type='Report'
                and o.comp_code=? and(o.menu_class=? or'-'=?)
                and allow = true
                order by o.order_by""";
        List<VRoleMenu> vList = new ArrayList<>();
        VRoleMenu vMenu;
        ResultSet rs = getResult(sql, roleCode, compCode, menuClass, menuClass);
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
                log.error("getReport : " + e.getMessage());
            }
        }
        return vList;
    }
}
