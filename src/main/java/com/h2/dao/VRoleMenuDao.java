package com.h2.dao;

import com.inventory.model.VRoleMenu;
import java.util.List;

public interface VRoleMenuDao {
    List<VRoleMenu> getMenu(String roleCode, String parentCode, String compCode);
    List<VRoleMenu> getMenuChild(String roleCode, String parentCode, String compCode);
    List<VRoleMenu> getReport(String roleCode, String menuClass, String compCode);
}