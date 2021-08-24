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
public class StockUnit implements java.io.Serializable {

    private String itemUnitCode;
    private String itemUnitName;
    private Date updatedDate;
    private AppUser updatedBy;
    private Date createdDate;
    private AppUser createdBy;
    private Integer macId;
    private String userCode;
    private String compCode;

    @Override
    public String toString() {
        return itemUnitCode;
    }

}
