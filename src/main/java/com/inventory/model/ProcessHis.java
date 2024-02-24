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
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "process_his")
public class ProcessHis {

    @EmbeddedId
    private ProcessHisKey key;
    @Column(name = "stock_code")
    private String stockCode;
    @Column(name = "vou_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime vouDate;
    @Column(name = "loc_code")
    private String locCode;
    @Column(name = "end_date")
    private LocalDateTime endDate;
    @Column(name = "pt_code")
    private String ptCode;
    @Column(name = "remark")
    private String remark;
    @Column(name = "process_no")
    private String processNo;
    @Column(name = "qty")
    private Float qty;
    @Column(name = "unit")
    private String unit;
    @Column(name = "price")
    private Float price;
    @Column(name = "finished")
    private boolean finished;
    @Column(name = "deleted")
    private boolean deleted;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "intg_upd_status")
    private String intgUpdStatus;
    @Transient
    private List<ProcessHisDetail> listDetail;
    @Transient
    private String stockUsrCode;
    @Transient
    private String stockName;
    @Transient
    private String ptName;
    @Transient
    private String locName;
    @Transient
    private boolean local;
    @Transient
    private ZonedDateTime vouDateTime;
}
