/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.model;

import lombok.Data;

/**
 *
 * @author DELL
 */
@Data
public class OrderHisDetail {
    private OrderDetailKey key;
    private String stockCode;
    private String userCode;
    private String stockName;
    private String groupName;
    private String brandName;
    private String catName;
    private String relName;
    private Float qty;
    private String unitCode;
    private Float price;
    private Float amount;
    private String locCode;
    private String locName;
    private Stock stock;
    private String batchNo;
    private String traderName;
    private Float weight;
    private String weightUnit;
    private Float stdWeight;
    
}
