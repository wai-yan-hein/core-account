/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.model;

import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
public class RetInHisDetail implements java.io.Serializable {

    private RetInKey riKey;
    private Stock stock;
    private Float qty;
    private StockUnit unit;
    private Float costPrice;
    private Float price;
    private Float amount;
    private Location location;
    private Integer uniqueId;
    private Float wt;

}
