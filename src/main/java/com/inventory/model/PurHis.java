/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import lombok.Data;

/**
 *
 * @author wai yan
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "pur_his")
public class PurHis implements java.io.Serializable {

    @EmbeddedId
    private PurHisKey key;
    @Column(name = "trader_code")
    private String traderCode;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "vou_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime vouDate;
    @Temporal(TemporalType.DATE)
    @Column(name = "due_date")
    private Date dueDate;
    @Column(name = "loc_code")
    private String locCode;
    @Column(name = "deleted")
    private boolean deleted;
    @Column(name = "vou_total")
    private Double vouTotal;
    @Column(name = "paid")
    private Double paid;
    @Column(name = "discount")
    private Double discount;
    @Column(name = "balance")
    private Double balance;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "created_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdDate;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;
    @Column(name = "remark")
    private String remark;
    @Column(name = "session_id")
    private Integer session;
    @Column(name = "cur_code")
    private String curCode;
    @Column(name = "disc_p")
    private Double discP;
    @Column(name = "tax_p")
    private Double taxP;
    @Column(name = "tax_amt")
    private Double taxAmt;
    @Column(name = "reference")
    private String reference;
    @Column(name = "intg_upd_status")
    private String intgUpdStatus;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "vou_lock")
    private boolean vouLock;
    @Column(name = "batch_no")
    private String batchNo;
    @Column(name = "comm_p")
    private Double commP;
    @Column(name = "comm_amt")
    private Double commAmt;
    @Column(name = "expense")
    private Double expense;
    @Column(name = "project_no")
    private String projectNo;
    @Column(name = "car_no")
    private String carNo;
    @Transient
    private String status = "STATUS";
    @Transient
    private List<PurHisDetail> listPD;
    @Transient
    private List<PurDetailKey> listDel;
    @Transient
    private List<String> location;
    @Transient
    private List<PurExpense> listExpense;

    public PurHis() {
    }

}
