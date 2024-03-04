package com.inventory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Data
@Entity
@Table(name = "landing_his_price")
public class LandingHisPrice {

    @EmbeddedId
    private LandingHisPriceKey key;
    @Column(name = "criteria_code")
    private String criteriaCode;
    @Column(name = "percent")
    private double percent;
    @Column(name = "price")
    private double price;
    @Column(name = "amount")
    private double amount;
    @Column(name = "percent_allow")
    private double percentAllow;
    @Transient
    private String criteriaUserCode;
    @Transient
    private String criteriaName;
}
