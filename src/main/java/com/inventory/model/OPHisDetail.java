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
    private String stockCode;
    private String userCode;
    private String stockName;
    private String groupName;
    private String brandName;
    private String catName;
    private String relation;
    private Float qty;
    private Float price;
    private Float amount;
    private String locCode;
    private String unitCode;
    private String vouNo;
    private Integer uniqueId;
}
