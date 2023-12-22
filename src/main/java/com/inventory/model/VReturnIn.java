/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.model;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.ZonedDateTime;

/**
 *
 * @author Lenovo
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VReturnIn {

    private String vouNo;
    private String traderCode;
    private String vouDate;
    private String curCode;
    private String remark;
    private double vouTotal;
    private double discount;
    private double discountPrice;
    private String createdBy;
    private boolean deleted;
    private double paid;
    private double vouBalance;
    private String compCode;
    private Integer macId;
    private String stockCode;
    private double qty;
    private double wt;
    private String unit;
    private double price;
    private double amount;
    private String locCode;
    private int uniqueId;
    private String traderName;
    private String stockName;
    private String locationName;
    private Integer deptId;
    private boolean local;
    private ZonedDateTime vouDateTime;
}
