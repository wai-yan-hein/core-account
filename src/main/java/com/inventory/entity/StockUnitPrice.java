package com.inventory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "stock_unit_price")
public class StockUnitPrice {

    @EmbeddedId
    private StockUnitPriceKey key;
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
    @Column(name = "unique_id")
    private Integer uniqueId;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;

}
