package com.inventory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "warehouse")
public class WareHouse {
    @EmbeddedId
    private WareHouseKey key;
    @Column(name = "description")
    private String description;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "created_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdDate;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "user_code")
    private String userCode;
    @Column(name = "active")
    private boolean active;
    @Column(name = "deleted")
    private boolean deleted;

    public WareHouse() {
    }

    public WareHouse(String wareHouseCode, String description) {
        this.key = new WareHouseKey();
        this.key.setCode(wareHouseCode);
        this.description = description;
    }
    
    
    
}
