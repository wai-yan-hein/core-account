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

    public PurDetailKey() {
    }

    public PurDetailKey(String vouNo, String pdCode) {
        this.vouNo = vouNo;
        this.pdCode = pdCode;
    }

}
