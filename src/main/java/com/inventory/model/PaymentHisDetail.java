package com.inventory.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "payment_his_detail")
public class PaymentHisDetail {

    @EmbeddedId
    private PaymentHisDetailKey key;
    @Column(name = "sale_vou_no")
    private String saleVouNo;
    @Column(name = "pay_amt")
    private double payAmt;
    @Column(name = "dis_amt")
    private double disAmt;
    @Column(name = "dis_percent")
    private double disPercent;
    @Column(name = "full_paid")
    private boolean fullPaid;
    @Column(name = "cur_code")
    private String curCode;
    @Column(name = "remark")
    private String remark;
    @Column(name = "reference")
    private String reference;
    @Temporal(TemporalType.DATE)
    @Column(name = "sale_vou_date")
    private LocalDate saleDate;
    @Column(name = "vou_total")
    private double vouTotal;
    @Column(name = "vou_balance")
    private double vouBalance;


}
