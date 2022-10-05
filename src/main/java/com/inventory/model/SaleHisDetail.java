/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.model;

import java.util.Date;
import lombok.Data;

/**
 *
 * @author wai yan
 */
@Data
public class SaleHisDetail implements java.io.Serializable {

    private SaleDetailKey sdKey;
    private Stock stock;
    private Date expDate;
    private Float qty;
    private StockUnit saleUnit;
    private Float price;
    private Float amount;
    private Location location;
    private Integer uniqueId;
}
