package com.inventory.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Entity
@Table(name = "labour_group")
public class LabourGroup {

    @EmbeddedId
    private LabourGroupKey key;
    @Column(name = "labour_name")
    private String labourName;
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
    @Column(name = "member_count")
    private Integer memberCount;
    @Column(name = "qty")
    private Double qty;
    @Column(name = "price")
    private Double price;

    public LabourGroup() {
    }

    public LabourGroup(String code, String name) {
        this.key = new LabourGroupKey();
        this.key.setCode(code);
        this.labourName = name;
    }

    @Override
    public String toString() {
        return labourName;
    }
}
