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
public class VouStatus {

    private VouStatusKey key;
    private String description;
    private Date updatedDate;
    private String updatedBy;
    private Date createdDate;
    private String createdBy;
    private Integer macId;
    private String userCode;

    public VouStatus() {
    }

    public VouStatus(String code, String description) {
        this.key = new VouStatusKey();
        this.key.setCode(code);
        this.description = description;
    }

}
