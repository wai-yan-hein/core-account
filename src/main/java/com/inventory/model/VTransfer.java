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
public class VTransfer {

    private String vouNo;
    private String vouDate;
    private ZonedDateTime vouDateTime;
    private String remark;
    private String fromLocationName;
    private String toLocationName;
    private String refNo;
    private String stockCode;
    private String stockUserCode;
    private String stockName;
    private String unit;
    private String unitName;
    private Double qty;
    private Double price;
    private Double amount;
    private String stockTypeName;
    private String createdBy;
    private boolean deleted;
    private Integer deptId;
    private Double weight;
    private String weightUnit;
    private String weightUnitName;
    private String labourGroupName;
    private String traderName;
    private boolean local;
    private Double saleAmt;
}
