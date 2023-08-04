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
 * @author Lenovo
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "op_his_detail")
public class OPHisDetail {

    @EmbeddedId
    private OPHisDetailKey key;
    @Column(name = "stock_code")
    private String stockCode;
    @Column(name = "qty")
    private Float qty;
    @Column(name = "price")
    private Float price;
    @Column(name = "amount")
    private Float amount;
    @Column(name = "loc_code")
    private String locCode;
    @Column(name = "unit")
    private String unitCode;    
    @Column(name = "weight")
    private Float weight;    
    @Column(name = "weight_unit")
    private String weightUnit;
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
}
