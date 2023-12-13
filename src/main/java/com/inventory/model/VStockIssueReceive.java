/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.model;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 *
 * @author pann
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VStockIssueReceive {
    private String vouNo;
    private String vouDate;
    private String createdBy;
    private LocalDateTime createdDate;
    private boolean deleted;    
    private String location;
    private String remark;
    private String description;
    private String updatedBy;
    private LocalDateTime updatedDate;
    private Integer macId;
    private Integer deptId;
    private String compCode;
    private String traderName;
    private String labourGroupName;
    private String stockCode;
    private String stockName;
    private Integer uniqueId;
    private Double wet;   
    private Double bag;
    private Double qty;
    private Double weight;
    private Double price;
    private Double amount;
    private boolean local;
    private ZonedDateTime vouDateTime;
}
