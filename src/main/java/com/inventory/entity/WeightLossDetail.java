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
 * @author DELL
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "weight_loss_his_detail")
public class WeightLossDetail {

    @EmbeddedId
    private WeightLossDetailKey key;
    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "stock_code")
    private String stockCode;
    @Column(name = "loc_code")
    private String locCode;
    @Column(name = "qty")
    private Double qty;
    @Column(name = "unit")
    private String unit;
    @Column(name = "price")
    private Double price;
    @Column(name = "loss_qty")
    private Double lossQty;
    @Column(name = "loss_unit")
    private String lossUnit;
    @Column(name = "loss_price")
    private Double lossPrice;
    @Transient
    private String stockUserCode;
    @Transient
    private String stockName;
    @Transient
    private String locName;
    @Transient
    private String relName;
}
