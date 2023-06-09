/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.acc.model;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *
 * @author Lenovo
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
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
