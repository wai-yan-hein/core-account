/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Data;

/**
 * @author pann
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Entity
@Table(name = "iss_rec_his_detail")
public class StockIssRecDetail {

    @EmbeddedId
    private StockIssRecDetailKey key;
    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "stock_code")
    private String stockCode;
    @Column(name = "loc_code")
    private String locCode;
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
    @Transient
    private boolean local;
}
