/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDate;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)

public class PurHisDetail {

    private PurDetailKey key;
    private Integer deptId;
    private String stockCode;
    private double qty;
    private double weightLoss;
    private String unitCode;
    private double orgPrice;
    private double price;
    private double amount;
    private String locCode;
    private double weight;
    private String weightUnit;
    private double stdWeight;
    private double length;
    private double width;
    private double totalWeight;
    private String mPercent;
    private double wet;
    private double rice;
    private double bag;
    private double avgQty;
    private double avgPrice;
    private LocalDate expDate;
    private String userCode;
    private String stockName;
    private boolean calculate;
    private String groupName;
    private String brandName;
    private String catName;
    private String relName;
    private String relCode;
    private String locName;
    private String landVouNo;
    private double purQty;
    
}
