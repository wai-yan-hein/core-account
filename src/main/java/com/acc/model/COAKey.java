/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.acc.model;

import lombok.Data;

/**
 *
 * @author DELL
 */
@Data
public class COAKey {

    private String coaCode;
    private String compCode;

    public COAKey(String coaCode, String compCode) {
        this.coaCode = coaCode;
        this.compCode = compCode;
    }

    public COAKey() {
    }
    
}
