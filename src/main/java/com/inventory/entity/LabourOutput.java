package com.inventory.entity;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
public class LabourOutput {
    private String vouNo;
    private String compCode;
    private Integer deptId;
    private LocalDateTime vouDate;
    private String remark;
    private LocalDateTime createdDate;
    private String createdBy;
    private LocalDateTime updatedDate;
    private String updatedBy;
    private Integer macId;
    private boolean deleted;
    private boolean vouLock;
    private double outputQty;
    private double rejectQty;
    private double amount;
    private List<LabourOutputDetail> listDetail;
    private ZonedDateTime vouDateTime;
}
