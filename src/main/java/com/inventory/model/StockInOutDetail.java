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

    private StockInOutKey key;
    private String stockCode;
    private String userCode;
    private String stockName;
    private String groupName;
    private String brandName;
    private String catName;
    private String relation;
    private String purUnitCode;
    private String locCode;
    private String locName;
    private Float inQty;
    private String inUnitCode;
    private Float outQty;
    private String outUnitCode;
    private String description;
    private String remark;
    private Integer uniqueId;
    private Float costPrice;
}
