package com.inventory.model;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;
@Data
@Embeddable
public class StockCriteriaKey implements Serializable {
    @Column(name = "criteria_code")
    private String criteriaCode;
    @Column(name = "comp_code")
    private String compCode;
}
