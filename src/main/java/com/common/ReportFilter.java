/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Data;
import lombok.NonNull;

/**
 *
 * @author Lenovo
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class ReportFilter {

    private String reportName;
    @NonNull
    private Integer macId;
    @NonNull
    private String compCode;
    @NonNull
    private Integer deptId;
    private String vouNo;
    private String curCode;
    private String opDate;
    private String fromDate;
    private String toDate;
    private List<String> listLocation;
    private List<String> listStock;
    private List<String> listBrand;
    private List<String> listRegion;
    private List<String> listCategory;
    private List<String> listStockType;
    private List<String> listTrader;
    private List<String> listSaleMan;
    private List<String> listDepartment;
    private String locCode;
    private String stockCode;
    private String brandCode;
    private String regCode;
    private String catCode;
    private String stockTypeCode;
    private String traderCode;
    private String saleManCode;
    private String vouTypeCode;
    private boolean calSale;
    private boolean calPur;
    private boolean calRI;
    private boolean calRO;
    private String status;
    private String incomeExpenseProcess;
    private String plProcess;
    private String bsProcess;
    private String invGroup;
    private String coaCode;
    private String srcAcc;

    public ReportFilter(Integer macId, String compCode, Integer deptId) {
        this.macId = macId;
        this.compCode = compCode;
        this.deptId = deptId;
    }

}
