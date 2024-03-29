package com.inventory.entity;

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
    private double overPayment;
    private double balance;
    private double grandTotal;
    private double discount;
    private double paid;

    private String vouNo;
    private String vouDate;
    private double grossQty;
    private double totalQty;
    private double qty;
    private String unit;
    private String unitName;
    private double weight;
    private double totalWeight;
    private String remark;
    private String cargo;
    private String traderName;
    private String locName;
    private String stockName;
    private String gradeStockName;
    private String regionName;
    private String traderPhoneNo;
    private double wetPercent;
    private String purUnitName;
    List<VLanding> listPrice;
    List<VLanding> listQty;
}
