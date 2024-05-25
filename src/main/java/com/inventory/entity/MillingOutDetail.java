/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.entity;

import lombok.Data;

/**
 *
 * @author wai yan
 */
@Data
public class MillingOutDetail {

    private MillingOutDetailKey key;
    private String stockCode;
    private Integer deptId;
    private double qty;
    private String unitCode;
    private double recentPrice;
    private double price;
    private double amount;
    private String locCode;
    private double weight;
    private String weightUnit;
    private double percent;
    private double percentQty;
    private double totalWeight;
    private String unitName;
    private String weightUnitName;
    private String userCode;
    private String stockName;
    private String groupName;
    private String brandName;
    private String catName;
    private String relName;
    private String locName;
    private String traderName;
    private Stock stock;
    private String qtyStr;
    private boolean calculate;
}
