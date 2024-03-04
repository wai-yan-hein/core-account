package com.inventory.entity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "output_cost")
public class OutputCost {

    @EmbeddedId
    private OutputCostKey key;
    @Column(name = "user_code")
    private String userCode;
    @Column(name = "name")
    private String name;
    @Column(name = "price")
    private double price;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "created_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdDate;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "intg_upd_status")
    private String intgUpdStatus;
    @Column(name = "active")
    private boolean active;
}
