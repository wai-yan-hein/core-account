/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.model;

import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
public class TraderKey {

    private String code;
    private String compCode;
    private Integer deptId;

    public TraderKey() {
    }

    public TraderKey(String code, String compCode, Integer deptId) {
        this.code = code;
        this.compCode = compCode;
        this.deptId = deptId;
    }
    

}
