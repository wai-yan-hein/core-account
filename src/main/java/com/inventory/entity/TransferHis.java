/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)

public class TransferHis {

    private TransferHisKey key;
    private Integer deptId;
    private String createdBy;
    private LocalDateTime createdDate;
    private boolean deleted;
    private LocalDateTime vouDate;
    private String refNo;
    private String remark;
    private LocalDateTime updatedDate;
    private String updatedBy;
    private String locCodeFrom;
    private String locCodeTo;
    private Integer macId;
    private String intgUpdStatus;
    private boolean vouLock;
    private String traderCode;
    private String labourGroupCode;
    private String jobCode;
    private Integer printCount;
    private Boolean skipInv;
    private List<TransferHisDetail> listTD;
    private List<String> location;

}
