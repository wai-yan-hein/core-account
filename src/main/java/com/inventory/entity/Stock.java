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
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "stock")
public class Stock {

    @EmbeddedId
    private StockKey key;
    @Column(name = "active")
    private boolean active;
    @Column(name = "stock_type_code")
    private String typeCode;
    @Column(name = "brand_code")
    private String brandCode;
    @Column(name = "stock_name")
    private String stockName;
    @Column(name = "category_code")
    private String catCode;
    @Column(name = "pur_unit")
    private String purUnitCode;
    @Column(name = "sale_unit")
    private String saleUnitCode;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "barcode")
    private String barcode;
    @Column(name = "short_name")
    private String shortName;
    @Column(name = "pur_price")
    private Double purPrice;
    @Temporal(TemporalType.DATE)
    @Column(name = "licence_exp_date")
    private Date expireDate;
    @Column(name = "remark")
    private String remark;
    @Column(name = "sale_price_n")
    private Double salePriceN;
    @Column(name = "sale_price_a")
    private Double salePriceA;
    @Column(name = "sale_price_b")
    private Double salePriceB;
    @Column(name = "sale_price_c")
    private Double salePriceC;
    @Column(name = "sale_price_d")
    private Double salePriceD;
    @Column(name = "sale_price_e")
    private Double salePriceE;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;
    @Column(name = "created_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdDate;
    @Column(name = "mig_code")
    private String migCode;
    @Column(name = "user_code")
    private String userCode;
    @Column(name = "rel_code")
    private String relCode;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "calculate")
    private boolean calculate;
    @Column(name = "intg_upd_status")
    private String intgUpdStatus;
    @Column(name = "explode")
    private boolean explode;
    @Column(name = "weight_unit")
    private String weightUnit;
    @Column(name = "weight")
    private Double weight;
    @Column(name = "favorite")
    private boolean favorite;
    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "sale_closed")
    private boolean saleClosed;
    @Column(name = "deleted")
    private boolean deleted;
    @Column(name = "formula_code")
    private String formulaCode;
    @Column(name = "pur_amt")
    private Double purAmt;
    @Column(name = "pur_qty")
    private Double purQty;
    @Column(name = "sale_amt")
    private Double saleAmt;
    @Column(name = "sale_qty")
    private Double saleQty;
    @Transient
    private String relName;
    @Transient
    private String groupName;
    @Transient
    private String brandName;
    @Transient
    private String catName;

    public Stock() {
    }

    public Stock(String stockCode, String stockName) {
        this.key = new StockKey();
        this.key.setStockCode(stockCode);
        this.stockName = stockName;
    }

}
