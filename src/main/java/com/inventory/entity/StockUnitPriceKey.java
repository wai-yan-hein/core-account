package com.inventory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.Data;

@Data
@Embeddable
public class StockUnitPriceKey implements Serializable {

    @Column(name = "stock_code")
    private String stockCode;
    @Column(name = "comp_code")
    private String compCode;
    @Column(name = "unit")
    private String unit;

}
