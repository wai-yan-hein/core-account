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
public class StockInOutKey implements Serializable {

    private String sdCode;
    private String vouNo;

    public StockInOutKey() {
    }

    public StockInOutKey(String sdCode, String vouNo) {
        this.sdCode = sdCode;
        this.vouNo = vouNo;
    }
}
