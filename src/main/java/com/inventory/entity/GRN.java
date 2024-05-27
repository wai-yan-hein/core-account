/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class GRN {

    private GRNKey key;
    private Integer deptId;
    private String batchNo;
    private LocalDateTime vouDate;
    private String traderCode;
    private boolean closed;
    private boolean deleted;
    private LocalDateTime createdDate;
    private String createdBy;
    private LocalDateTime updatedDate;
    private String updatedBy;
    private Integer macId;
    private String remark;
    private String locCode;
    private List<GRNDetail> listDetail;
    private List<GRNDetailKey> listDel;
    private String traderName;
    private String traderUserCode;
    private ZonedDateTime vouDateTime;

    public GRN() {
    }

    public GRN(String batchNo) {
        this.batchNo = batchNo;
    }

}
