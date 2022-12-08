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
public class VReturnIn {

    private String rdCode;
    private String vouNo;
    private String traderCode;
    private String vouDate;
    private String curCode;
    private String remark;
    private Float vouTotal;
    private Float discount;
    private Float discountPrice;
    private String createdBy;
    private boolean deleted;
    private Float paid;
    private Float vouBalance;
    private String compCode;
    private Integer macId;
    private String stockCode;
    private Float qty;
    private Float wt;
    private String unit;
    private Float price;
    private Float amount;
    private String locCode;
    private Integer uniqueId;
    private String traderName;
    private String stockName;
    private String locationName;
    private Integer deptId;
}
