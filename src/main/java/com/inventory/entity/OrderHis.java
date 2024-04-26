/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.Data;

/**
 *
 * @author DELL
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderHis {

    private OrderHisKey key;
    private Integer deptId;
    private String traderCode;
    private String saleManCode;
    private LocalDateTime vouDate;
    private LocalDateTime creditTerm;
    private String curCode;
    private String remark;
    private String reference;
    private double vouTotal;
    private double vouBalance;
    private boolean deleted;
    private LocalDateTime createdDate;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime updatedDate;
    private String locCode;
    private Integer macId;
    private String intgUpdStatus;
    private boolean vouLock;
    private String projectNo;
    private String orderStatus;
    private boolean post;
    private List<OrderHisDetail> listSH;
    private List<OrderDetailKey> listDel;
    private boolean backup;
    private List<String> location;
    private ZonedDateTime vouDateTime;
    private String traderName;
    private String userCode;
    private String orderStatusName;
    private boolean select;

}
