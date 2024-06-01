/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StockInOutDetail  {

    private StockInOutKey key;
    private Integer deptId;
    private String stockCode;
    private String locCode;
    private double inQty;
    private String inUnitCode;
    private double outQty;
    private String outUnitCode;
    private double costPrice;
    private double weight;
    private String weightUnit;
    private double totalWeight;
    private double wet;
    private double rice;
    private double inBag;
    private double outBag;
    private double amount;
    private String userCode;
    private String stockName;
    private String groupName;
    private String brandName;
    private String catName;
    private String relName;
    private String locName;
}
