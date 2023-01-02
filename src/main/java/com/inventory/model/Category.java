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
public class Category implements java.io.Serializable {

    private CategoryKey key;
    private String catName;
    private Integer migId;
    
    private String updatedBy;
    private Date createdDate;
    private String createdBy;
    private Integer macId;
    private String userCode;

    public Category() {
    }

    public Category(String catCode, String catName) {
        this.key = new CategoryKey();
        this.key.setCatCode(catCode);
        this.catName = catName;
    }

}
