/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.entity;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *
 * @author Lenovo
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class General {

    private Double amount;
    private Double qty;
    private Double smallQty;
    private String stockCode;
    private String stockName;
    private String sysCode;
    private String stockTypeName;
    private String brandName;
    private String categoryName;
    private String traderCode;
    private String traderName;
    private Double totalQty;
    private String saleManName;
    private String saleManCode;
    private String qtyRel;
    private String message;
    private String address;
    private String relation;
}
