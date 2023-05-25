/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.user.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import java.util.Date;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
@Entity
@Table(name = "menu")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Menu {

    @EmbeddedId
    private MenuKey key;
    @Column(name = "user_code")
    private String userCode;
    @Column(name = "menu_class")
    private String menuClass;
    @Column(name = "menu_name")
    private String menuName;
    @Column(name = "menu_url")
    private String menuUrl;
    @Column(name = "parent_menu_code")
    private String parentMenuCode;
    @Column(name = "menu_type")
    private String menuType;
    @Column(name = "account")
    private String account;
    @Column(name = "order_by")
    private Integer orderBy;
    @Column(name = "updated_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;
    @Transient
    private List<Menu> child;

    @Override
    public String toString() {
        return menuName;
    }

}
