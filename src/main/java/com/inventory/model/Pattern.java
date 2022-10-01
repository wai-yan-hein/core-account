/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.model;

import java.util.Date;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
public class Pattern {

    private Stock stock;
    private Float qty;
    private Float price;
    private StockUnit unit;
    private Location location;
    private String stockCode;
    private Integer uniqueId;
    private String compCode;
}
