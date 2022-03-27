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
public class StockType implements java.io.Serializable {

    private String stockTypeCode;
    private String stockTypeName;
    private String accountId;
    private Date updatedDate;
    private String updatedBy;
    private Date createdDate;
    private String createdBy;
    private Integer macId;
    private String userCode;
    private String compCode;

    public StockType() {
    }

    public StockType(String stockTypeCode, String stockTypeName) {
        this.stockTypeCode = stockTypeCode;
        this.stockTypeName = stockTypeName;
    }

}
