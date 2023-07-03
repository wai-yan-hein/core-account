/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.model;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

/**
 *
 * @author wai yan
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Region implements java.io.Serializable {

    private RegionKey key;
    private String regionName;
    private String regionType;
    private String parentRegion;
    
    private String updatedBy;
    private LocalDateTime createdDate;
    private String createdBy;
    private Integer macId;
    private String userCode;

    public Region(String regCode, String regionName) {
        this.key = new RegionKey();
        this.key.setRegCode(regCode);
        this.regionName = regionName;
    }

    public Region() {
    }

}
