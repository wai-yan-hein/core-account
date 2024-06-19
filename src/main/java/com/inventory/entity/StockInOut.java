/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
public class StockInOut {

    private StockIOKey key;
    private Integer deptId;
    private String remark;
    private String description;
    private LocalDateTime updatedDate;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdDate;
    private String vouStatusCode;
    private Integer macId;
    private LocalDateTime vouDate;
    private Boolean deleted;
    private String intgUpdStatus;
    private boolean vouLock;
    private String labourGroupCode;
    private String jobCode;
    private String receivedName;
    private String receivedPhoneNo;
    private String carNo;
    private String traderCode;
    private Integer printCount;
    private Boolean post;
    private String status = "STATUS";
    private List<StockInOutDetail> listSH;
    private List<StockInOutKey> listDel;
    private List<LocationKey> keys;
}
