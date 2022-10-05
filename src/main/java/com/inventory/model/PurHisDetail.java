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
    private String stockCode;
    private String userCode;
    private String stockName;
    private String groupName;
    private String brandName;
    private String catName;
    private String relName;
    private Float avgQty;
    private Float qty;
    private String unitCode;
    private Float price;
    private Float amount;
    private String locCode;
    private String locName;
    private Integer uniqueId;
}
