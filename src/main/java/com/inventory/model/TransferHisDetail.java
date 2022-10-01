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
public class TransferHisDetail {

    private String tdCode;
    private String vouNo;
    private Stock stock;
    private Float qty;
    private Float wt;
    private StockUnit unit;
    private Integer uniqueId;
}
