package com.inventory.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Data
public class StockPayment {

    private String vouNo;
    private String compCode;
    private Integer deptId;
    private LocalDateTime vouDate;
    private String traderCode;
    private String locCode;
    private String remark;
    private boolean deleted;
    private boolean calculate;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String createdBy;
    private String updatedBy;
    private Integer macId;
    private String tranOption;
    private double payQty;
    private List<StockPaymentDetail> listDetail;
    private List<StockPaymentDetailKey> listDelete;
    private boolean vouLock;
    private ZonedDateTime vouDateTime;
    private String traderName;
    private String projectNo;
}
