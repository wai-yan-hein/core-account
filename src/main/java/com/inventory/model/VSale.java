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

    private String groupName;
    private String vouNo;
    private String traderCode;
    private String rfId;
    private String saleManCode;
    private String vouDate;
    private String creditTerm;
    private String curCode;
    private String remark;
    private Double vouTotal;
    private Double grandTotal;
    private Double discount;
    private Double discountPrice;
    private Double taxAmt;
    private Double taxPrice;
    private String createdDate;
    private String createdBy;
    private boolean deleted;
    private Double paid;
    private Double vouBalance;
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
    private Double qty;
    private String saleUnit;
    private Double salePrice;
    private Double saleAmount;
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
    private Double lastBalance;
    private String compCode;
    private String relName;
    private String qtyStr;
    private Integer deptId;
    private String batchNo;
    private String reference;
    private String supplierName;
    private Double weight;
    private String weightUnit;
    private String phoneNo;
    private String address;
    private String projectNo;
    private String userCode;
    private Double creditAmt;
    private Double diffAmt;
    private String tranOption;
    private String saleVouNo;
    private String payDate;
    private Integer vouCount;
    private Double totalQty;
    private boolean local;
}
