/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.model;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *
 * @author Lenovo
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class General {

    private Float amount;
    private Float qty;
    private Float smallQty;
    private String stockCode;
    private String stockName;
    private String sysCode;
    private String stockTypeName;
    private String brandName;
    private String categoryName;
    private String traderCode;
    private String traderName;
    private Float totalQty;
    private String saleManName;
    private String saleManCode;
    private String qtyRel;
    private String message;
    private String address;
    private String unit;
}
