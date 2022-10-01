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

    private Stock stock;
    private Float minQty;
    private StockUnit minUnit;
    private Float maxQty;
    private StockUnit maxUnit;
    private Float balQty;
    private String balUnit;
    private String compCode;
    private Float orderQty;
    private StockUnit orderUnit;
    private float minSmallQty;
    private float maxSmallQty;
    private float balSmallQty;
    private String status;
}
