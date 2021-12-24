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
public class StockInOutDetail implements Serializable {

    private StockInOutKey ioKey;
    private Stock stock;
    private Location location;
    private Float inQty;
    private Float inWt;
    private StockUnit inUnit;
    private Float outQty;
    private Float outWt;
    private StockUnit outUnit;
    private String description;
    private String remark;
    private Integer uniqueId;
    private Float costPrice;
}
