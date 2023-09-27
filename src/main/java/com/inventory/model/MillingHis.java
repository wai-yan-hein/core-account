/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 * @author wai yan
 */
@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "milling_his")
public class MillingHis {

    @EmbeddedId
    private MillingHisKey key;
    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "trader_code")
    private String traderCode;
    @Column(name = "vou_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime vouDate;
    @Column(name = "cur_code")
    private String curCode;
    @Column(name = "remark")
    private String remark;
    @Column(name = "reference")
    private String reference;
    @Column(name = "deleted")
    private boolean deleted;
    @Column(name = "created_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdDate;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;
    @Column(name = "loc_code")
    private String locCode;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "intg_upd_status")
    private String intgUpdStatus;
    @Column(name = "vou_lock")
    private boolean vouLock;
    @Column(name = "project_no")
    private String projectNo;
    @Column(name = "car_no")
    private String carNo;
    @Column(name = "vou_status_id")
    private String vouStatusId;
    @Column(name = "load_qty")
    private double loadQty;
    @Column(name = "load_weight")
    private double loadWeight;
    @Column(name = "load_amount")
    private double loadAmount;
    @Column(name = "load_expense")
    private double loadExpense;
    @Column(name = "load_cost")
    private double loadCost;
    @Column(name = "output_qty")
    private double outputQty;
    @Column(name = "output_weight")
    private double outputWeight;
    @Column(name = "output_amount")
    private double outputAmount;
    @Column(name = "diff_weight")
    private double diffWeight;
    @Column(name = "diff_qty")
    private double diffQty;
    @Column(name = "percent_weight")
    private double percentWeight;
    @Column(name = "percent_qty")
    private double percentQty;
    @Transient
    private String localVouNo;
    private transient String status = "STATUS";
    private transient List<MillingRawDetail> listRaw;
    private transient List<MillingRawDetailKey> listRawDel;
    private transient List<MillingOutDetail> listOutput;
    private transient List<MillingOutDetailKey> listOutputDel;
    private transient List<MillingExpense> listExpense;
    private transient List<MillingExpenseKey> listExpenseDel;
    private transient boolean local = false;
    private transient String traderName = "";
    private transient String processType = "";
    private transient String vouDateStr = "";
    @Transient
    private ZonedDateTime vouDateTime;

    public MillingHis() {
    }
}
