/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.model;

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
@Table(name = "milling_output")
public class MillingOutDetail {

    @EmbeddedId
    private MillingOutDetailKey key;
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
    @Column(name = "percent")
    private double percent;
    @Column(name = "percent_qty")
    private double percentQty;
    @Column(name = "tot_weight")
    private double totalWeight;
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
    @Transient
    private boolean calculate;
}
