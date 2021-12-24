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
public class Region implements java.io.Serializable {

    private String regCode;
    private String regionName;
    private String regionType;
    private String parentRegion;
    private String compCode;
    private Date updatedDate;
    private AppUser updatedBy;
    private Date createdDate;
    private AppUser createdBy;
    private Integer macId;
    private String userCode;

    public Region(String regCode, String regionName) {
        this.regCode = regCode;
        this.regionName = regionName;
    }

    public Region() {
    }

}
