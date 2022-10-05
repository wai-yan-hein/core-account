/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.model;

import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
public class Pattern {

    private String stockCode;
    private String userCode;
    private String stockName;
    private String groupName;
    private String brandName;
    private String catName;
    private String relation;
    private Float qty;
    private Float price;
    private String unitCode;
    private String locCode;
    private String locName;
    private String mapStockCode;
    private Integer uniqueId;
    private String compCode;
    private Integer deptId;
}
