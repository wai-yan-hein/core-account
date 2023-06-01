/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import java.util.Date;
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
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "created_date")
    private Date createdDate;
    @Column(name = "deleted")
    private boolean deleted;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "vou_date")
    private Date vouDate;
    @Column(name = "ref_no")
    private String refNo;
    @Column(name = "remark")
    private String remark;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    private Date updatedDate;
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
    @Transient
    private List<TransferHisDetail> listTD;
    @Transient
    private List<THDetailKey> delList;
    @Transient
    private String status = "STATUS";
    @Transient
    private List<String> location;

}
