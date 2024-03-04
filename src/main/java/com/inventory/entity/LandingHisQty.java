package com.inventory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "landing_his_qty")
public class LandingHisQty {

    @EmbeddedId
    private LandingHisQtyKey key;
    @Column(name = "criteria_code")
    private String criteriaCode;
    @Column(name = "percent")
    private double percent;
    @Column(name = "qty")
    private double qty;
    @Column(name = "total_qty")
    private double totalQty;
    @Column(name = "percent_allow")
    private double percentAllow;
    @Column(name = "unit")
    private String unit;
    private transient String criteriaUserCode;
    private transient String criteriaName;
}
