/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.Data;

/**
 *
 * @author WSwe
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)

public class RetOutHis {

    private RetOutHisKey key;
    private Integer deptId;
    private String traderCode;
    private LocalDateTime vouDate;
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
    private Integer sessionId;
    private String refNo;
    private String curCode;
    private double discP;
    private String intgUpdStatus;
    private Integer macId;
    private String status;
    private boolean vouLock;
    private String projectNo;
    private Integer printCount;
    private double grandTotal;
    private String deptCode;
    private String srcAcc;
    private String cashAcc;
    private String payableAcc;
    private String disAcc;
    private String taxAcc;
    private double taxAmt;
    private double taxP;
    private List<RetOutHisDetail> listRD;
    private ZonedDateTime vouDateTime;
    private String traderName;
}
