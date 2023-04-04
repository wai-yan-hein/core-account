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
public class COATemplate {

    private COATemplateKey key;
    private String coaNameEng;
    private String coaNameMya;
    private boolean active;
    private String coaParent;
    private Integer coaLevel;
    private String coaCodeUsr;
    private String curCode;
    private String deptCode;
    private boolean credit;
}
