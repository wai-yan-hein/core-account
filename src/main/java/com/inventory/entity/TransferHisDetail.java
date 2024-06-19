/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransferHisDetail {

    private THDetailKey key;
    private Integer deptId;
    private String stockCode;
    private double qty;
    private String unitCode;
    private double weight;
    private String weightUnit;
    private double totalWeight;
    private double wet;
    private double rice;
    private double bag;
    private double price;
    private double amount;
    private String userCode;
    private String stockName;
    private String groupName;
    private String brandName;
    private String catName;
    private String relCode;
    private String relName;
    private String locName;
}
