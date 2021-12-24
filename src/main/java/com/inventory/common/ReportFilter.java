/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.common;

import java.util.List;
import lombok.Data;
import lombok.NonNull;

/**
 *
 * @author Lenovo
 */
@Data
public class ReportFilter {

    private List<String> listTrader;
    private List<String> listSaleMan;
    private String curCode;
    private String fromDate;
    private String toDate;
    private List<String> listLocation;
    private List<String> listStock;
    private List<String> listBrand;
    private List<String> listRegion;
    private List<String> listCategory;
    private List<String> listStockType;
    private String reportName;
    @NonNull
    private Integer macId;
    @NonNull
    private String compCode;

    public ReportFilter(Integer macId, String compCode) {
        this.macId = macId;
        this.compCode = compCode;
    }

}
