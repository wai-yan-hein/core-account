/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.model;

import lombok.Data;

/**
 *
 * @author lenovo
 */
@Data
public class RetInKey {

    private String rdCode;
    private String vouNo;
    private Integer depId;

    public RetInKey() {
    }

    public RetInKey(String rdCode, String vouNo, Integer depId) {
        this.rdCode = rdCode;
        this.vouNo = vouNo;
        this.depId = depId;
    }

}
