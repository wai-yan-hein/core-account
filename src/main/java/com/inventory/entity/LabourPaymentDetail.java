package com.inventory.entity;

import lombok.Data;

@Data
public class LabourPaymentDetail {

    private String vouNo;
    private String compCode;
    private int uniqueId;
    private String description;
    private double price;
    private double qty;
    private double amount;
    private String account;
    private String accountName;
    private String deptCode;
    private String deptUserCode;

}
