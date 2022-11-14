/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.acc.model;

import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
public class TraderAKey {

    private String code;
    private String compCode;

    public TraderAKey() {
    }

    public TraderAKey(String code, String compCode) {
        this.code = code;
        this.compCode = compCode;
    }
    
}
