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
public class VStockIO {

    private String vouNo;
    private String vouDate;
    private String remark;
    private String description;
    private String vouTypeUserCode;
    private String vouTypeName;
    private String stockUsrCode;
    private String stockName;
    private String locName;
    private String curCode;
    private Float inQty;
    private String inUnit;
    private Float outQty;
    private String outUnit;
    private Float costPrice;
    private Float amount;
    private String createdBy;
    private boolean deleted;
    private Integer deptId;
}
