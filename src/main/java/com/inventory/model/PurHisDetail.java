/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.model;

import java.io.Serializable;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
public class PurHisDetail implements Serializable {
    private PurDetailKey pdKey;
    private Stock stock;
    private Float qty;
    private Float stdWeight;
    private StockUnit purUnit;
    private Float avgWeight;
    private Float avgPrice;
    private Float price;
    private Float amount;
    private Location location;
    private Integer uniqueId;
}
