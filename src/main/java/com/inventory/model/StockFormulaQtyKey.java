package com.inventory.model;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class StockFormulaQtyKey implements Serializable {
    @Column(name = "formula_code")
    private String formulaCode;
    @Column(name = "comp_code")
    private String compCode;
    @Column(name = "unique_id")
    private int uniqueId;
}
