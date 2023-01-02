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
public class StockBrand implements java.io.Serializable {

    private StockBrandKey key;
    private String brandName;
    private Integer migId;
    
    private String updatedBy;
    private Date createdDate;
    private String createdBy;
    private Integer macId;
    private String userCode;

    public StockBrand() {
    }

    public StockBrand(String brandCode, String brandName) {
        this.key = new StockBrandKey();
        this.key.setBrandCode(brandCode);
        this.brandName = brandName;
    }

}
