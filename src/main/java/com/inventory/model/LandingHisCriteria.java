package com.inventory.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "landing_detail_criteria")
public class LandingHisCriteria {

    @EmbeddedId
    private LandingHisCriteriaKey key;
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
    private transient String criteriaUserCode;
    private transient String criteriaName;
}
