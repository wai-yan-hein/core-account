/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.model;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *
 * @author Lenovo
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VSale {

    private String sdCode;
    private String vouNo;
    private String traderCode;
    private String saleManCode;
    private String vouDate;
    private String creditTerm;
    private String curCode;
    private String remark;
    private Float vouTotal;
    private Float grandTotal;
    private Float discount;
    private Float discountPrice;
    private Float taxAmt;
    private Float taxPrice;
    private String createdDate;
    private String createdBy;
    private boolean deleted;
    private Float paid;
    private Float vouBalance;
    private String updatedBy;
    private String updatedDate;
    private String cusPhoneNo;
    private String cusAddress;
    private String orderCode;
    private String regionCode;
    private Integer macId;
    private Integer sessionId;
    private String stockUserCode;
    private String stockCode;
    private String expiredDate;
    private Float qty;
    private Float saleWt;
    private String saleUnit;
    private Float salePrice;
    private Float saleAmount;
    private String locCode;
    private Integer uniqueId;
    private String traderName;
    private String saleManName;
    private String stockName;
    private String stockTypeCode;
    private String brandCode;
    private String brandName;
    private String catCode;
    private String categoryName;
    private String locationName;
    private String compName;
    private String compPhone;
    private String compAddress;
    private String regionName;
    private String stockTypeName;
    private String refNo;
    private Integer deptId;
    private String reference;
}
