/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 *
 * @author DELL
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderHisDetail {

    private OrderDetailKey key;
    private Integer deptId;
    private String stockCode;
    private double orderQty;
    private double qty;
    private String unitCode;
    private double price;
    private double amount;
    private String locCode;
    private double weight;
    private String weightUnit;
    private String design;
    private String size;
    private String userCode;
    private String stockName;
    private String groupName;
    private String brandName;
    private String catName;
    private String relName;
    private String locName;
    private String traderName;
    private String phoneNo;
    private String address;
    private String rfId;
    private String vouDateStr;
    private String traderCode;
    private String remark;
    private double salePrice;
    private double saleAmount;
    private String saleUnit;
    private String locationName;
    private String createdBy;
    private String orderStatusName;
    private Stock stock;
    private String saleManName;
    private String vouNo;
    private double heatPressQty;

}
