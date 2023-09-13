/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.model;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *
 * @author Lenovo
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProcessDetail {

    private String pdCode;
    private Stock stock;
    private Location location;
    private Float qty;
    private Float price;
    private StockUnit unit;
    private Float amount;
    private String vouNo;
    private int uniqueId;
}
