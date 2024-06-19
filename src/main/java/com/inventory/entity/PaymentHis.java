package com.inventory.entity;

import lombok.Data;

import java.util.List;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
public class PaymentHis {

    private String vouNo;
    private String compCode;
    private Integer deptId;
    private LocalDateTime vouDate;
    private String traderCode;
    private String remark;
    private boolean deleted;
    private double amount;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String createdBy;
    private String updatedBy;
    private Integer macId;
    private String deptCode;
    private String account;
    private String projectNo;
    private String curCode;
    private String intgUpdStatus;
    private String tranOption;
    private List<PaymentHisDetail> listDetail;
    private String traderName;
    private ZonedDateTime vouDateTime;
    private boolean vouLock;
}
