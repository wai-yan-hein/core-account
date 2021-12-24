/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.model;

import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
public class PatternDetail {

    private String ptCode;
    private String patternCode;
    private Stock stock;
    private Location location;
    private Float inQty;
    private Float inWt;
    private StockUnit inUnit;
    private Float outQty;
    private Float outWt;
    private StockUnit outUnit;
    private Integer uniqueId;
    private Float costPrice;

}
