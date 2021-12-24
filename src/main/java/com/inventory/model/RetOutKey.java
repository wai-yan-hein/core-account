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
 * @author lenovo
 */
@Data
public class RetOutKey implements Serializable {

    private String rdCode;
    private String vouNo;

    public RetOutKey() {
    }

    public RetOutKey(String rdCode, String vouNo) {
        this.rdCode = rdCode;
        this.vouNo = vouNo;
    }
}
