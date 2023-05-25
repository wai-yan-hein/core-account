/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
@Embeddable
public class PMKey implements Serializable{

   @Column(name = "role_code")
    private String roleCode;
    @Column(name = "menu_code")
    private String menuCode;
    @Column(name = "comp_code")
    private String compCode;
}
