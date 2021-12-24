/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.model;

import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
public class OPHisDetail {

    private String opCode;
    private Stock stock;
    private Float qty;
    private Float stdWt;
    private Float price;
    private Float amount;
    private Location location;
    private StockUnit stockUnit;
    private String vouNo;
    private Integer uniqueId;
}
