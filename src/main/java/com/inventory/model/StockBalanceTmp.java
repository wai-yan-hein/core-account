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
public class StockBalanceTmp implements Serializable {

    private Integer macId;
    private Stock stock;
    private Location location;
    private Float changeWt;
    private String changeUnit;
    private Float changeWt2;
    private String changeUnit2;
    private Float qty;
    private Float stdWt;
    private Float totalWt;
    private String unit;

}
