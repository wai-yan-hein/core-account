package com.h2.service;

import com.user.model.Menu;
import java.util.List;

public interface VRoleMenuService {
    List<Menu> getMenu(String roleCode, String parentCode, String compCode,boolean privilege);
    List<Menu> getReport(String roleCode, String menuClass, String compCode);
}
