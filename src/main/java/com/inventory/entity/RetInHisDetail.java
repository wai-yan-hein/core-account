/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RetInHisDetail {

    
    private RetInKey key;
    private Integer deptId;
    private String stockCode;
    private double qty;
    private String unitCode;
    private double price;
    private double amount;
    private String locCode;
    private double weight;
    private String weightUnit;
    private double totalWeight;
    private double wet;
    private double rice;
    private double bag;
    private String userCode;
    private String stockName;
    private String groupName;
    private String brandName;
    private String catName;
    private String relCode;
    private String relName;
    private String locName;
    private String unit;
    private String remark;
    private double vouTotal;
    private double paid;
    private String vouDate;
    private double vouBalance;
    private String traderName;
    private String vouNo;

}
