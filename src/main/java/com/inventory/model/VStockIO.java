/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.model;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Transient;
import java.time.ZonedDateTime;

/**
 *
 * @author Lenovo
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
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
    private Float inAmt;
    private Float outAmt;
    private String createdBy;
    private boolean deleted;
    private String unit;
    private Float price;
    private Float qty;
    private String stockCode;
    private String processNo;
    private Integer deptId;
    private String relName;
    private Float smallPrice;
    private ZonedDateTime vouDateTime;
    private String jobName;
    private String labourGroupName;
    private String traderName;
    private String receivedName;
    private String receivedPhone;
    private String carNo;
    private String phoneNo;
    private String regionName;
    private String inUnitName;
    private String outUnitName;
    private String weightUnitName;
    private Double weight;
    private Double inBag;
    private Double outBag;

}
