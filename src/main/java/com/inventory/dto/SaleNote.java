package com.inventory.dto;

import lombok.Data;

@Data
public class SaleNote {
    private String vouNo;
    private String compCode;
    private int uniqueId;
    private String description;
    private double saleQty;
    private double qty;
    private String unitName;
}
