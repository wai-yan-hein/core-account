/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 * @author wai yan
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "milling_raw")
public class MillingRawDetail {

    @EmbeddedId
    private MillingRawDetailKey key;
    @Column(name = "stock_code")
    private String stockCode;
    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "qty", nullable = false)
    private double qty;
    @Column(name = "unit")
    private String unitCode;
    @Column(name = "price", nullable = false)
    private double price;
    @Column(name = "amt", nullable = false)
    private double amount;
    @Column(name = "loc_code")
    private String locCode;
    @Column(name = "weight")
    private double weight;
    @Column(name = "weight_unit")
    private String weightUnit;
    @Column(name = "tot_weight")
    private double totalWeight;
    @Transient
    private String unitName;
    @Transient
    private String weightUnitName;
    @Transient
    private double totalAmount;
    @Transient
    private String userCode;
    @Transient
    private String stockName;
    @Transient
    private String groupName;
    @Transient
    private String brandName;
    @Transient
    private String catName;
    @Transient
    private String relName;
    @Transient
    private String locName;
    @Transient
    private String traderName;
    @Transient
    private Stock stock;
    @Transient
    private String qtyStr;
}
