package com.inventory.model;

import lombok.Data;

import java.util.List;

@Data
public class VLanding {

    private String tranType;
    private String criteriaName;
    private double percent;
    private double percentAllow;
    private double price;
    private double amount;
    private double purPrice;
    private double purQty;
    private double purAmt;
    private double purWeight;
    private double purWeightTotal;
    private String purUnit;

    private String vouNo;
    private String vouDate;
    private String grossQty;
    private double qty;
    private String unit;
    private double weight;
    private double totalWeight;
    private String remark;
    private String cargo;
    private String traderName;
    private String locName;
    private String stockName;
    private String gradeStockName;
    List<VLanding> listDetail;
}
