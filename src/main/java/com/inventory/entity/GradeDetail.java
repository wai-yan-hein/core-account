package com.inventory.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
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
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;
    @Transient
    private String stockName;
}
