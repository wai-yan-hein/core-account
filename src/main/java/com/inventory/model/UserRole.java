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
public class UserRole implements java.io.Serializable {

    private String roleCode;
    private String roleName;
    private String compCode;
    private Date updatedDate;
    private AppUser updatedBy;
    private Date createdDate;
    private AppUser createdBy;
    private Integer macId;
    private String userCode;

    @Override
    public String toString() {
        return roleName;
    }

}
