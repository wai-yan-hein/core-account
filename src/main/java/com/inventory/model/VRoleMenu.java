/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.model;

import java.util.List;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
public class VRoleMenu {

    private String menuCode;
    private String roleCode;
    private boolean allow;
    private String menuName;
    private String menuType;
    private String menuUrl;
    private String menuClass;
    private String account;
    private String parentMenuCode;
    private String compCode;
    private Integer orderBy;
    private List<VRoleMenu> child;

    public VRoleMenu() {
    }

    public VRoleMenu(String menuType, String menuName, boolean allow, List<VRoleMenu> child) {
        this.allow = allow;
        this.menuName = menuName;
        this.menuType = menuType;
        this.child = child;
    }

    @Override
    public String toString() {
        return menuName;
    }

}
