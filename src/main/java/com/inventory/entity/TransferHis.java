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
import java.util.List;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "transfer_his")
public class TransferHis {

    @EmbeddedId
    private TransferHisKey key;
    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "created_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdDate;
    @Column(name = "deleted")
    private boolean deleted;
    @Column(name = "vou_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime vouDate;
    @Column(name = "ref_no")
    private String refNo;
    @Column(name = "remark")
    private String remark;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "loc_code_from")
    private String locCodeFrom;
    @Column(name = "loc_code_to")
    private String locCodeTo;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "intg_upd_status")
    private String intgUpdStatus;
    @Column(name = "vou_lock")
    private boolean vouLock;
    @Column(name = "trader_code")
    private String traderCode;
    @Column(name = "labour_group_code")
    private String labourGroupCode;
    @Column(name = "job_code")
    private String jobCode;
    @Column(name = "print_count")
    private Integer printCount;
    @Column(name = "skip_inv")
    private Boolean skipInv;
    @Transient
    private List<TransferHisDetail> listTD;
    @Transient
    private List<THDetailKey> delList;
    @Transient
    private String status = "STATUS";
    @Transient
    private List<String> location;

}
