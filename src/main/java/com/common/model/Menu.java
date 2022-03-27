/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.common.model;

import java.util.Date;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
public class Menu {

    private String menuCode;
    private String userCode;
    private String menuClass;
    private String menuName;
    private String menuUrl;
    private String parentMenuCode;
    private String menuType;
    private String account;
    private Integer orderBy;
    private List<Menu> child;

    @Override
    public String toString() {
        return menuName;
    }

}
