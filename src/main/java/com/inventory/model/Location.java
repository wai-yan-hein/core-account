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
 * @author Lenovo
 */
@Data
public class Location implements java.io.Serializable {

    private LocationKey key;
    private String locName;
    private String parentCode;
    private boolean calcStock;
    private Date updatedDate;
    private String updatedBy;
    private Date createdDate;
    private String createdBy;
    private Integer macId;
    private String userCode;

    public Location(String locCode, String locName) {
        this.key = new LocationKey();
        this.key.setLocCode(locCode);
        this.locName = locName;
    }

    public Location() {
    }

}
