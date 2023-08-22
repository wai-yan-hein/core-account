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
import java.io.Serializable;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "pur_his_detail")
public class PurHisDetail implements Serializable {

    @EmbeddedId
    private PurDetailKey key;
    @Column(name = "stock_code")
    private String stockCode;
    @Column(name = "qty")
    private Double qty;
    @Column(name = "avg_qty")
    private Double avgQty;
    @Column(name = "pur_unit")
    private String unitCode;
    @Column(name = "org_price")
    private Double orgPrice;
    @Column(name = "pur_price")
    private Double price;
    @Column(name = "pur_amt")
    private Double amount;
    @Column(name = "loc_code")
    private String locCode;
    @Column(name = "weight")
    private Double weight;
    @Column(name = "weight_unit")
    private String weightUnit;
    @Column(name = "std_weight")
    private Double stdWeight;
    @Column(name = "length")
    private Double length;
    @Column(name = "width")
    private Double width;
    @Column(name = "total_weight")
    private Double totalWeight;
    @Column(name = "m_percent")
    private String mPercent;
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
}
