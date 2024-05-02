package com.inventory.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LabourOutputDetail {

    private String vouNo;
    private String compCode;
    private Integer uniqueId;
    private String jobNo;
    private String traderCode;
    private String labourCode;
    private String description;
    private String orderVouNo;
    private String refNo;
    private String remark;
    private String vouStatusCode;
    private double printQty;
    private double outputQty;
    private double rejectQty;
    private double price;
    private double amount;
    private String traderName;
    private String jobName;
    private String vouStatusName;
    private String labourName;
}
