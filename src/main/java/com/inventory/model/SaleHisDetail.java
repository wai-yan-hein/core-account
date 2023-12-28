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
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import java.util.Date;
import lombok.Data;

/**
 *
 * @author wai yan
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "sale_his_detail")
public class SaleHisDetail {

    @EmbeddedId
    private SaleDetailKey key;
    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "stock_code")
    private String stockCode;
    @Temporal(TemporalType.DATE)
    @Column(name = "expire_date")
    private Date expDate;
    @Column(name = "qty", nullable = false)
    private double qty;
    @Column(name = "sale_unit")
    private String unitCode;
    @Column(name = "sale_price", nullable = false)
    private double price;
    @Column(name = "sale_amt", nullable = false)
    private double amount;
    @Column(name = "loc_code")
    private String locCode;
    @Column(name = "batch_no")
    private String batchNo;
    @Column(name = "weight")
    private double weight;
    @Column(name = "weight_unit")
    private String weightUnit;
    @Column(name = "std_weight")
    private double stdWeight;
    @Column(name = "total_weight")
    private double totalWeight;
    @Column(name = "org_price")
    private double orgPrice;
    @Column(name = "weight_loss")
    private double weightLoss;
    private double wet;
    private double rice;
    private double bag;
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
}
