/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.Data;

/**
 *
 * @author DELL
 */
@Data
@Entity
@Table(name = "landing_his")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LandingHis {

    @EmbeddedId
    private LandingHisKey key;
    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "vou_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime vouDate;
    @Column(name = "trader_code")
    private String traderCode;
    @Column(name = "deleted")
    private boolean deleted;
    @Column(name = "created_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdDate;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "remark")
    private String remark;
    @Column(name = "loc_code")
    private String locCode;
    @Column(name = "stock_code")
    private String stockCode;
    @Column(name = "gross_qty")
    private double grossQty;
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
    @Column(name = "criteria_amt")
    private double criteriaAmt;
    @Column(name = "pur_amount")
    private double purAmt;
    @Column(name = "pur_price")
    private double purPrice;
    @Column(name = "cargo")
    private String cargo;
    @Column(name = "vou_paid")
    private double vouPaid;
    @Column(name = "vou_balance")
    private double vouBalance;
    @Column(name = "vou_discount")
    private double vouDiscount;
    @Column(name = "grand_total")
    private double grandTotal;
    @Column(name = "purchase")
    private boolean purchase;
    @Transient
    private List<LandingHisPrice> listPrice;
    @Transient
    private List<LandingHisPriceKey> listDelPrice;
    @Transient
    private List<LandingHisQty> listQty;
    @Transient
    private List<LandingHisQtyKey> listDelQty;
    @Transient
    private List<LandingHisGrade> listGrade;
    @Transient
    private String traderName;
    @Transient
    private String traderUserCode;
    @Transient
    private ZonedDateTime vouDateTime;
    @Transient
    private String stockName;

    public LandingHis() {
    }

}
