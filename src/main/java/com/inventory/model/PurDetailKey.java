/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.model;

import java.io.Serializable;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
public class PurDetailKey implements Serializable {

    private String vouNo;
    private String pdCode;
    private Integer deptId;

    public PurDetailKey() {
    }

    public PurDetailKey(String vouNo, String pdCode, Integer deptId) {
        this.vouNo = vouNo;
        this.pdCode = pdCode;
        this.deptId = deptId;
    }

   

}
