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
@Table(name = "order_status")
public class OrderStatus {

    @EmbeddedId
    private OrderStatusKey key;
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
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "user_code")
    private String userCode;
    @Column(name = "intg_upd_status")
    private String intgUpdStatus;
    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "deleted")
    private boolean deleted;
    @Column(name = "active")
    private boolean active;
    @Column(name = "order_by")
    private Integer orderBy;

    public OrderStatus() {
    }

    public OrderStatus(String code, String description) {
        this.key = new OrderStatusKey();
        this.key.setCode(code);
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
