/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.entity;

import com.inventory.dto.SaleNote;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    @Column(name = "credit_term", columnDefinition = "DATE")
    private LocalDate creditTerm;
    @Column(name = "cur_code")
    private String curCode;
    @Column(name = "remark")
    private String remark;
    @Column(name = "reference")
    private String reference;
    @Column(name = "vou_total")
    private double vouTotal;
    @Column(name = "grand_total")
    private double grandTotal;
    @Column(name = "discount")
    private double discount;
    @Column(name = "disc_p")
    private double discP;
    @Column(name = "tax_amt")
    private double taxAmt;
    @Column(name = "tax_p")
    private double taxPercent;
    @Column(name = "deleted")
    private boolean deleted;
    @Column(name = "paid")
    private double paid;
    @Column(name = "vou_balance")
    private double balance;
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
    private double expense;
    @Column(name = "account")
    private String saleAcc;
    @Column(name = "labour_group_code")
    private String labourGroupCode;
    @Column(name = "print_count")
    private Integer printCount;
    @Column(name = "debtor_acc")
    private String debtorAcc;
    @Column(name = "cash_acc")
    private String cashAcc;
    @Column(name = "dept_code")
    private String deptCode;
    @Column(name = "weight_vou_no")
    private String weightVouNo;
    @Column(name = "post")
    private boolean post;
    @Column(name = "s_pay")
    private boolean sPay;
    @Column(name = "tran_source")
    private int tranSource;
    @Column(name = "total_payment")
    private double totalPayment;
    @Column(name = "opening")
    private double opening;
    private double outstanding;
    @Column(name = "total_balance")
    private double totalBalance;
    @Transient
    private String localVouNo;
    @Transient
    private List<String> listOrder;
    private transient String status = "STATUS";
    private transient List<SaleHisDetail> listSH;
    private transient boolean backup;
    private transient List<String> location;
    private transient boolean local = false;
    private transient List<SaleExpense> listExpense;
    private transient List<SaleExpenseKey> listDelExpense;

    @Transient
    private List<VouDiscount> listVouDiscount;
    @Transient
    private List<VouDiscountKey> listDelVouDiscount;
    @Transient
    private List<SaleNote> listSaleNote;
    @Transient
    private Trader trader;
    @Transient
    private String vouDateStr;

    public SaleHis() {
    }
}
