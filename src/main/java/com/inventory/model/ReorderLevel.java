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
public class ReorderLevel {

    private ReorderKey key;
    private String userCode;
    private String stockName;
    private String groupName;
    private String brandName;
    private String catName;
    private String relName;
    private Float minQty;
    private String minUnitCode;
    private Float maxQty;
    private String maxUnitCode;
    private Float balQty;
    private String balUnit;
    private Float orderQty;
    private String orderUnitCode;
    private float minSmallQty;
    private float maxSmallQty;
    private float balSmallQty;
    private String status;
}
