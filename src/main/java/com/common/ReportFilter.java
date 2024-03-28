/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.common;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReportFilter {

    private List<String> listTrader;
    private List<String> listSaleMan;
    private String curCode;
    private String opDate;
    private String fromDate;
    private String toDate;
    private String vouNo;
    private List<String> listLocation;
    private List<String> listStock;
    private List<String> listBrand;
    private List<String> listRegion;
    private List<String> listCategory;
    private List<String> listStockType;
    private String reportName;
    private String locCode;
    private String stockCode;
    private String brandCode;
    private String regCode;
    private String catCode;
    private String stockTypeCode;
    private String traderCode;
    private String saleManCode;
    private String vouTypeCode;
    private String batchNo;
    private boolean calSale;
    private boolean calPur;
    private boolean calRI;
    private boolean calRO;
    private boolean calMill;
    private Integer macId;
    private String compCode;
    private Integer deptId;
    private String status;
    private String projectNo;
    private boolean orderFavorite;
    private float creditAmt;
    private String fromDueDate;
    private String toDueDate;
    private boolean deleted;
    private boolean active;
    private String labourGroupCode;
    private String warehouseCode;
    private int reportType;
    private String mode;
    private boolean summary;
    private String remark;
    private String userCode;
    private String tranSource;
    private String description;
    private String vouStatus;
    private String jobNo;
    private String tranOption;
    private String saleVouNo;
    private String account;
    private String orderNo;
    private String orderName;
    private String reference;
    private boolean nullBatch;
    private boolean finished;
    private boolean close;
    private boolean orderByBatch;
    private String processNo;
    private String orderStatus;
    private String refNo;
    private boolean draft;
    private boolean local;
    //account

    private String openingDate;
    private String closingDate;
    private String desp;
    private String srcAcc;
    private String acc;
    private String glVouNo;
    private String coaCode;
    private String traderType;
    private String coaLv2;
    private String coaLv1;
    private String coaLv3;
    private String invGroup;
    private boolean netChange;
    private List<String> listDepartment;
    private String deptCode;
    private String cashGroup;
    private String bankGroup;
    private String fixedAcc;
    private String currentAcc;
    private String capitalAcc;
    private String liaAcc;
    private String incomeAcc;
    private String otherIncomeAcc;
    private String purchaseAcc;
    private String expenseAcc;
    private String plAcc;
    private String reAcc;
    private List<String> listCOAGroup;
    private List<String> listPl;
    private List<String> listBs;
    private List<String> listIe;
    private boolean pl;
    private boolean ie;
    private boolean bs;

    public ReportFilter(Integer macId, String compCode, Integer deptId) {
        this.macId = macId;
        this.compCode = compCode;
        this.deptId = deptId;
    }

}
