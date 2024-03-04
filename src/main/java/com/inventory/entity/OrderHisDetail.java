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
@Table(name = "order_his_detail")
public class OrderHisDetail {

    @EmbeddedId
    private OrderDetailKey key;
    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "stock_code")
    private String stockCode;
    @Column(name = "order_qty", nullable = false)
    private Double orderQty;
    @Column(name = "qty", nullable = false)
    private Double qty;
    @Column(name = "unit")
    private String unitCode;
    @Column(name = "price", nullable = false)
    private Double price;
    @Column(name = "amt", nullable = false)
    private Double amount;
    @Column(name = "loc_code")
    private String locCode;
    @Column(name = "weight")
    private Double weight;
    @Column(name = "weight_unit")
    private String weightUnit;
    @Column(name = "design")
    private String design;
    @Column(name = "size")
    private String size;
    @Transient
    private Stock stock;
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

}
