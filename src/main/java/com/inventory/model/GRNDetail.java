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
public class GRNDetail {

    private GRNDetailKey key;
    private String stockCode;
    private Float qty;
    private String unit;
    private String locCode;
    private String userCode;
    private String stockName;
    private String relName;
    private String locName;
}
