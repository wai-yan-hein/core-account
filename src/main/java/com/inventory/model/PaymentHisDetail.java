package com.inventory.model;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PaymentHisDetail {

    private String vouNo;
    private String compCode;
    private Integer uniqueId;
    private Integer deptId;
    private String saleVouNo;
    private Double payAmt;
    private Double disAmt;
    private Double disPercent;
    private boolean fullPaid;
    private String curCode;
    private String remark;
    private String reference;
    private LocalDate saleDate;
    private Double vouTotal;
    private Double vouBalance;

}
