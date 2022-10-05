/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.model;

import lombok.Data;

/**
 *
 * @author lenovo
 */
@Data
public class RetOutHisDetail implements java.io.Serializable {

    private RetOutKey roKey;
    private Stock stock;
    private Float qty;
    private StockUnit unit;
    private Float price;
    private Float amount;
    private Location location;
    private Integer uniqueId;

}
