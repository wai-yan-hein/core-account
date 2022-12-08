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
public class VOpening {

    private String vouNo;
    private String vouDate;
    private String remark;
    private String locationName;
    private String stockCode;
    private String stockUserCode;
    private String stockName;
    private String unit;
    private Float qty;
    private Float price;
    private Float amount;
    private String stockTypeName;
    private String createdBy;
    private boolean deleted;
    private Integer deptId;
}
