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
@Table(name = "grn_detail")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GRNDetail {

    @EmbeddedId
    private GRNDetailKey key;
    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "stock_code")
    private String stockCode;
    @Column(name = "qty")
    private Float qty;
    @Column(name = "unit")
    private String unit;
    @Column(name = "loc_code")
    private String locCode;
    @Column(name = "weight")
    private Float weight;
    @Column(name = "weight_unit")
    private String weightUnit;
    @Transient
    private String userCode;
    @Transient
    private String stockName;
    @Transient
    private String relName;
    @Transient
    private String locName;
    @Transient
    private Float stdWeight;
    @Transient
    private Stock stock;
}
