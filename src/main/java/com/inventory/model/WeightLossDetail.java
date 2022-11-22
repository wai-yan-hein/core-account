/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.model;

import lombok.Data;

/**
 *
 * @author DELL
 */
@Data
public class WeightLossDetail {

    private WeightLossDetailKey key;
    private String stockCode;
    private String locCode;
    private Float qty;
    private String unit;
    private Float price;
    private Float lossQty;
    private String lossUnit;
    private Float lossPrice;
    private String stockUserCode;
    private String stockName;
    private String locName;
    private String relName;
}
