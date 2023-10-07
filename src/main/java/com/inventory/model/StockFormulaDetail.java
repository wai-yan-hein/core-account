package com.inventory.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.Data;

@Data
@Entity
@Table(name = "stock_formula_detail")
public class StockFormulaDetail implements Serializable{

    @EmbeddedId
    private StockFormulaDetailKey key;
    @Column(name = "criteria_code")
    private String criteriaCode;
    @Column(name = "percent")
    private double percent;
    @Column(name = "price")
    private double price;
    private transient String criteriaName;
    private transient String userCode;
}
