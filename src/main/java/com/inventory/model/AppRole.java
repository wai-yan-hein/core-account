/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.model;

import lombok.Data;

/**
 *
 * @author winswe
 */
@Data
public class AppRole implements java.io.Serializable {

    private String roleCode;
    private String roleName;
    private String exampleRole;

    public AppRole() {
    }

   

}
