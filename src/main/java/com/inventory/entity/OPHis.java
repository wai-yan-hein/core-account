/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.ToString;

/**
 *
 * @author Lenovo
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)

public class OPHis {

    private OPHisKey key;
    private Integer deptId;
    private LocalDate vouDate;
    private String remark;
    private String createdBy;
    private LocalDateTime createdDate;
    private String updatedBy;
    private String curCode;
    private String locCode;
    private double opAmt;
    private LocalDateTime updatedDate;
    private boolean deleted;
    private Integer macId;
    private String intgUpdStatus;
    private String traderCode;
    private Integer tranSource;
    private List<OPHisDetail> detailList;
    private String status;
    private String locName;
    private String vouDateStr;
    private double qty;
    private double bag;

}
