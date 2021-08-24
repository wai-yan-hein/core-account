/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.model;

import java.util.Date;
import lombok.Data;

/**
 *
 * @author winswe
 */
@Data
public class AppUser implements java.io.Serializable {

    private String appUserCode;
    private Boolean active;
    private String email;
    private String password;
    private String phone;
    private String userName;
    private String userShort;
    private String createStatus;
    private Date updatedDate;
    private AppUser updatedBy;
    private Date createdDate;
    private AppUser createdBy;
    private Integer macId;
    private String userCode;
    private String compCode;

    public AppUser(String appUserCode, String userName) {
        this.appUserCode = appUserCode;
        this.userName = userName;
    }

    public AppUser() {
    }

}
