/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.model;

import java.util.List;
import lombok.Data;

/**
 *
 * @author winswe
 */
@Data
public class VRoleMenu implements java.io.Serializable {

    private VRoleMenuKey key;
    private String menuClass;
    private String menuName;
    private String menuNameMM;
    private String menuUrl;
    private String parent;
    private String menuType;
    private Integer orderBy;
    private String soureAccCode;
    private Boolean isAllow;
    private String apiUrl;
    private List<VRoleMenu> child;
}
