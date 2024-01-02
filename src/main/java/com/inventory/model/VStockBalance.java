/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
public class VStockBalance {

    private String stockCode;
    private String stockName;
    private String locCode;
    private String locationName;
    private double totalQty;
    private double weight;
    private String unitName;
    private Double salePrice;
    private Double saleQty;
    private Double transferQty;
    private Double opQty;
}
