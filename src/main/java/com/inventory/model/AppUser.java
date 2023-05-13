/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.model;

import lombok.Data;

/**
 *
 * @author wai yan
 */
@Data
public class AppUser implements java.io.Serializable {

    private String userCode;
    private String userName;
    private String userShortName;
    private String email;
    private String password;
    private boolean active;
    private String roleCode;

    public AppUser(String userCode, String userName) {
        this.userCode = userCode;
        this.userName = userName;
    }

    public AppUser() {
    }

}
