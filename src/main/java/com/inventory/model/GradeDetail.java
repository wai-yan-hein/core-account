package com.inventory.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "grade_detail")
public class GradeDetail {

    @EmbeddedId
    private GradeDetailKey key;
    @Column(name = "min_percent")
    private double minPercent;
    @Column(name = "max_percent")
    private double maxPercent;
    @Column(name = "grade_stock_code")
    private String gradeStockCode;
    @Transient
    private String stockName;
}
