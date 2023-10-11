package com.inventory.model;


import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "stock_formula_qty")
public class StockFormulaQty {
    @EmbeddedId
    private StockFormulaQtyKey key;
    @Column(name = "criteria_code")
    private String criteriaCode;
    @Column(name = "percent")
    private double percent;
    @Column(name = "qty")
    private double qty;
    @Column(name = "unit")
    private String unit;
    @Column(name = "percent_allow")
    private double percentAllow;
    private transient String criteriaName;
    private transient String userCode;
}
