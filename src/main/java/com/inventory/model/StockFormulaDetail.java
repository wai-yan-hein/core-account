package com.inventory.model;


import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "stock_formula_detail")
public class StockFormulaDetail {
    @EmbeddedId
    private StockFormulaDetailKey key;
    @Column(name = "description")
    private String description;
    @Column(name = "percent")
    private double percent;
    @Column(name = "price")
    private double price;
}
