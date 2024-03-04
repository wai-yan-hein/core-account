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
 * @author pann
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Entity
@Table(name = "pur_order_his_detail")
public class PurOrderHisDetail {
    @EmbeddedId
    private PurOrderHisDetailKey key;
    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "stock_code")
    private String stockCode;
    @Column(name = "wet")
    private double wet;
    @Column(name = "bag")
    private double bag;
    @Column(name = "qty")
    private double qty;
    @Column(name = "weight")
    private double weight;
    @Column(name = "rice")
    private double rice;
    @Column(name = "price")
    private double price;
    @Column(name = "amount")
    private double amount;
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
