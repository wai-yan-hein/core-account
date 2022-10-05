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
 * @author Lenovo
 */
@Data
public class Stock implements java.io.Serializable {

    private StockKey key;
    private boolean active;
    private String brandCode;
    private String stockName;
    private String catCode;
    private String typeCode;
    private String createdBy;
    private String updatedBy;
    private String barcode;
    private String shortName;
    private Float purWeight;
    private Float purPrice;
    private String purUnitCode;
    private Float saleWeight;
    private String saleUnitCode;
    private Date expireDate;
    private String remark;
    private Float salePriceN;
    private Float salePriceA;
    private Float salePriceB;
    private Float salePriceC;
    private Float salePriceD;
    private Float salePriceE;
    private Date updatedDate;
    private Date createdDate;
    private String migCode;
    private String compCode;
    private String userCode;
    private String relCode;
    private Integer macId;
    private boolean calculate;
    private String relName;
    private String groupName;
    private String catName;
    private String brandName;

    public Stock() {
    }

    public Stock(String stockCode, String stockName) {
        this.key = new StockKey();
        this.key.setStockCode(stockCode);
        this.stockName = stockName;
    }

}
