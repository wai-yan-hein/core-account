/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.Data;

/**
 *
 * @author wai yan
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurHis implements Cloneable {

    private PurHisKey key;
    private Integer deptId;
    private String traderCode;
    private LocalDateTime vouDate;
    private LocalDate dueDate;
    private String locCode;
    private boolean deleted;
    private double vouTotal;
    private double paid;
    private double discount;
    private double balance;
    private String createdBy;
    private LocalDateTime createdDate;
    private String updatedBy;
    private LocalDateTime updatedDate;
    private String remark;
    private Integer session;
    private String curCode;
    private double discP;
    private double taxP;
    private double taxAmt;
    private String reference;
    private String intgUpdStatus;
    private Integer macId;
    private boolean vouLock;
    private String batchNo;
    private double commP;
    private double commAmt;
    private double expense;
    private String projectNo;
    private String carNo;
    private String labourGroupCode;
    private String landVouNo;
    private Integer printCount;
    private String weightVouNo;
    private String payableAcc;
    private String commAcc;
    private String disAcc;
    private String taxAcc;
    private String cashAcc;
    private String purchaseAcc;
    private String deptCode;
    private double grandTotal;
    private boolean sRec;
    private Integer tranSource;
    private double outstanding;
    private String grnVouNo;
    private String refNo;
    private List<PurHisDetail> listPD;
    private List<PurExpense> listExpense;
    private ZonedDateTime vouDateTime;

    @Override
    public PurHis clone() throws CloneNotSupportedException {
        PurHis clonedGl = (PurHis) super.clone();
        return clonedGl;
    }
}
