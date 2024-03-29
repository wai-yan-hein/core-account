/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.ToString;

/**
 *
 * @author Lenovo
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@Entity
@Table(name = "op_his")
public class OPHis {

    @EmbeddedId
    private OPHisKey key;
    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "op_date")
    @Temporal(TemporalType.DATE)
    private Date vouDate;
    @Column(name = "remark")
    private String remark;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "created_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdDate;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "cur_code")
    private String curCode;
    @Column(name = "loc_code")
    private String locCode;
    @Column(name = "op_amt")
    private double opAmt;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;
    @Column(name = "deleted")
    private boolean deleted;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "intg_upd_status")
    private String intgUpdStatus;
    @Column(name = "trader_code")
    private String traderCode;
    @Column(name = "tran_source")
    private int tranSource;
    private double qty;
    private double bag;
    @Transient
    private List<OPHisDetail> detailList;
    @Transient
    private List<OPHisDetailKey> listDel;
    @Transient
    private String status = "STATUS";
    @Transient
    private List<LocationKey> keys;
    @Transient
    private String locName;
    @Transient
    private String vouDateStr;

}
