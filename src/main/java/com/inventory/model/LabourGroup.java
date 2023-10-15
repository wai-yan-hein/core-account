package com.inventory.model;

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
    @Column(name = "name")
    private String name;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "created_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdDate;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "user_code")
    private String userCode;
    @Column(name = "intg_upd_status")
    private String intgUpdStatus;
    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "active")
    private boolean active;
    @Column(name = "member_count")
    private Integer memberCount;

    public LabourGroup() {
    }

    public LabourGroup(String code, String name) {
        this.key = new LabourGroupKey();
        this.key.setCode(code);
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
