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
public class VCOALv3 {

    private String coaCode;
    private String coaUsrCode;
    private String coaNameEng;
    private String coaCodeParent2;
    private String coaUsrCodeParent2;
    private String coaNameEngParent2;
    private String coaCodeParent3;
    private String coaUsrCodeParent3;
    private String coaNameEngParent3;
    private String compCode;
    private String curCode;

    public VCOALv3(String coaCode, String coaNameEng) {
        this.coaCode = coaCode;
        this.coaNameEng = coaNameEng;
    }
    public VCOALv3() {
    }
}
