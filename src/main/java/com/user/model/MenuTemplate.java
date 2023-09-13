/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.user.model;

import java.util.List;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *
 * @author Lenovo
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MenuTemplate {

    private MenuTemplateKey key;
    private String menuClass;
    private String menuName;
    private String menuUrl;
    private String parentMenuId;
    private String menuType;
    private String account;
    private Integer orderBy;
    private List<MenuTemplate> child;

    @Override
    public String toString() {
        return menuName;
    }

}
