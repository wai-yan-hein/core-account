package com.h2.service;

import com.inventory.model.VRoleMenu;
import java.util.List;

public interface VRoleMenuService {
    List<VRoleMenu> getMenu(String roleCode, String parentCode, String compCode);
    List<VRoleMenu> getMenuChild(String roleCode, String parentCode, String compCode);
    List<VRoleMenu> getReport(String roleCode, String menuClass, String compCode);
}
