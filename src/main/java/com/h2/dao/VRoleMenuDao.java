package com.h2.dao;

import com.inventory.entity.VRoleMenu;
import java.util.List;

public interface VRoleMenuDao {
    List<VRoleMenu> getMenu(String roleCode, String parentCode, String compCode,boolean privilege);
    List<VRoleMenu> getReport(String roleCode, String menuClass, String compCode);
}
