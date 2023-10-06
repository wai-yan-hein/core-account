/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.model;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 *
 * @author DELL
 */
@Data
@Entity
@Table(name = "grade_his_detail")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LandingHisDetail {

    @EmbeddedId
    private LandingHisDetailKey key;
    @Column(name = "dept_id")
    private int deptId;
    @Column(name = "stock_code")
    private String stockCode;
    @Column(name = "qty")
    private double qty;
    @Column(name = "unit")
    private String unit;
    @Column(name = "weight")
    private double weight;
    @Column(name = "weight_unit")
    private String weightUnit;
    @Column(name = "total_weight")
    private double totalWeight;
    @Column(name = "price")
    private double price;
    @Column(name = "amount")
    private double amount;
    @Transient
    private String userCode;
    @Transient
    private String stockName;
    @Transient
    private String relName;
    @Transient
    private String locName;
    @Transient
    private Stock stock;
}
