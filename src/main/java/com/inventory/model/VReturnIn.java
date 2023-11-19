/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.model;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.ZonedDateTime;

/**
 *
 * @author Lenovo
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VReturnIn {

    private String vouNo;
    private String traderCode;
    private String vouDate;
    private String curCode;
    private String remark;
    private Double vouTotal;
    private Double discount;
    private Double discountPrice;
    private String createdBy;
    private boolean deleted;
    private Double paid;
    private Double vouBalance;
    private String compCode;
    private Integer macId;
    private String stockCode;
    private Double qty;
    private Double wt;
    private String unit;
    private Double price;
    private Double amount;
    private String locCode;
    private int uniqueId;
    private String traderName;
    private String stockName;
    private String locationName;
    private Integer deptId;
    private boolean local;
    private ZonedDateTime vouDateTime;
}
