/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.common;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *
 * @author Lenovo
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FilterObject {

    private String fromDate;
    private String toDate;
    private String vouNo;
    private String userCode;
    private String description;
    private String remark;
    private String vouStatus;
    private String stockCode;
    private String saleManCode;
    private String reference;
    private String locCode;
    private String locCodeTo;
    private String refNo;
    private boolean deleted;
    private String compCode;
    private Integer deptId;
    private String processNo;
    private Boolean finished;
    private boolean close;
    private String traderCode;
    private boolean nullBatch;
    private String batchNo;
    private boolean orderByBatch;
    private String projectNo;
    private String curCode;
    private String account;
    private boolean local;
    private String saleVouNo;
    private String tranOption;
    private String orderStatus;
    private String tranSource;
    private boolean draft;
    private String jobNo;
    private String labourGroupCode;
    private String orderNo;
    private String orderName;

    public FilterObject(String compCode, Integer deptId) {
        this.compCode = compCode;
        this.deptId = deptId;
    }

}
