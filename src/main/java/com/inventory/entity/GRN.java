/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.Data;

/**
 *
 * @author DELL
 */
@Data
@Entity
@Table(name = "grn")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GRN {

    @EmbeddedId
    private GRNKey key;
    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "batch_no")
    private String batchNo;
    @Column(name = "vou_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime vouDate;
    @Column(name = "trader_code")
    private String traderCode;
    @Column(name = "closed")
    private boolean closed;
    @Column(name = "deleted")
    private boolean deleted;
    @Column(name = "created_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdDate;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "remark")
    private String remark;
    @Column(name = "loc_code")
    private String locCode;
    @Transient
    private List<GRNDetail> listDetail;
    @Transient
    private List<GRNDetailKey> listDel;
    @Transient
    private String traderName;
    @Transient
    private String traderUserCode;
    @Transient
    private ZonedDateTime vouDateTime;

    public GRN() {
    }

    public GRN(String batchNo) {
        this.batchNo = batchNo;
    }

}
