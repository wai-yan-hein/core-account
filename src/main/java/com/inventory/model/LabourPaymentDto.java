package com.inventory.model;

import java.time.LocalDate;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Data
public class LabourPaymentDto {

    private String vouNo;
    private String compCode;
    private int deptId;
    private String deptCode;
    private LocalDateTime vouDate;
    private String labourGroupCode;
    private String curCode;
    private String remark;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String createdBy;
    private String updatedBy;
    private boolean deleted;
    private int macId;
    private List<LabourPaymentDetail> listDetail;
    private Integer memberCount;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String sourceAcc;
    private String expenseAcc;
    private double payTotal;
    private ZonedDateTime vouDateTime;
    private String labourName;

}
