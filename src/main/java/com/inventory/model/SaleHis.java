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
@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "sale_his")
public class SaleHis {

    @EmbeddedId
    private SaleHisKey key;
    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "trader_code")
    private String traderCode;
    @Column(name = "saleman_code")
    private String saleManCode;
    @Column(name = "vou_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime vouDate;
    @Column(name = "credit_term", columnDefinition = "TIMESTAMP")
    private LocalDateTime creditTerm;
    @Column(name = "cur_code")
    private String curCode;
    @Column(name = "remark")
    private String remark;
    @Column(name = "reference")
    private String reference;
    @Column(name = "vou_total")
    private Double vouTotal;
    @Column(name = "grand_total")
    private Double grandTotal;
    @Column(name = "discount")
    private Double discount;
    @Column(name = "disc_p")
    private Double discP;
    @Column(name = "tax_amt")
    private Double taxAmt;
    @Column(name = "tax_p")
    private Double taxPercent;
    @Column(name = "deleted")
    private boolean deleted;
    @Column(name = "paid")
    private Double paid;
    @Column(name = "vou_balance")
    private Double balance;
    @Column(name = "created_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdDate;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "session_id")
    private Integer session;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;
    @Column(name = "address")
    private String address;
    @Column(name = "order_code")
    private String orderCode;
    @Column(name = "reg_code")
    private String regionCode;
    @Column(name = "loc_code")
    private String locCode;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "intg_upd_status")
    private String intgUpdStatus;
    @Column(name = "vou_lock")
    private boolean vouLock;
    @Column(name = "order_no")
    private String orderNo;
    @Column(name = "project_no")
    private String projectNo;
    @Column(name = "tmp_vou_no")
    private String tmpVouNo;
    @Column(name = "car_no")
    private String carNo;
    @Column(name = "grn_vou_no")
    private String grnVouNo;
    @Column(name = "expense")
    private Double expense;
    @Column(name = "account")
    private String account;
    @Transient
    private String localVouNo;
    private transient String status = "STATUS";
    private transient List<SaleHisDetail> listSH;
    private transient List<SaleDetailKey> listDel;
    private transient boolean backup;
    private transient List<String> location;
    private transient boolean local = false;
    private transient List<SaleExpense> listExpense;
    private transient List<SaleExpenseKey> listDelExpense;
    @Transient
    private List<VouDiscount> listVouDiscount;
    @Transient
    private List<VouDiscountKey> listDelVouDiscount;

    public SaleHis() {
    }
}
