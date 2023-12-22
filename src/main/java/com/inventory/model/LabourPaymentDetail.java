package com.inventory.model;


import lombok.Data;

@Data
public class LabourPaymentDetail {
    private String vouNo;
    private String compCode;
    private int uniqueId;
    private String description;
    private double qty;
    private double price;
    private double amount;
    private String account;
    private String accountName;


}
