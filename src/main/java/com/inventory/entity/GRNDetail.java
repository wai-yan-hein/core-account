/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.entity;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *
 * @author DELL
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GRNDetail {

    private GRNDetailKey key;
    private Integer deptId;
    private String stockCode;
    private double qty;
    private String unit;
    private String locCode;
    private double weight;
    private String weightUnit;
    private double totalWeight;
    private String userCode;
    private String stockName;
    private String relName;
    private String locName;
    private double stdWeight;
    private Stock stock;
}
