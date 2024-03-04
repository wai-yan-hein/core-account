/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.ZonedDateTime;
import lombok.Data;

/**
 * @author wai yan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class VOrder {

    private String vouNo;
    private String traderCode;
    private String rfId;
    private String saleManCode;
    private String vouDate;
    private ZonedDateTime vouDateTime;
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
    private int uniqueId;
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
    private float lastBalance;
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
    private boolean local;
    private String orderStatus;
    private String orderStatusName;
    private boolean select;
    private boolean post;
}
