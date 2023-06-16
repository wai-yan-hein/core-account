package com.inventory.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import lombok.Data;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "payment_his")
public class PaymentHis {

    @EmbeddedId
    private PaymentHisKey key;
    @Column(name = "vou_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime vouDate;
    @Column(name = "trader_code")
    private String traderCode;
    @Column(name = "remark")
    private String remark;
    @Column(name = "deleted")
    private boolean deleted;
    @Column(name = "amount")
    private Float amount;
    @Column(name = "created_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdDate;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "account")
    private String account;
    @Column(name = "project_no")
    private String projectNo;
    @Column(name = "cur_code")
    private String curCode;
    @Transient
    private List<PaymentHisDetail> listDetail;
    @Transient
    private List<PaymentHisDetailKey> listDelete;
    @Transient
    private String traderName;
}
