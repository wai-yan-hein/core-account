/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.acc.model;

import java.util.List;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
public class ReportFilter {

    private String reportName;
    private String fromDate;
    private String toDate;
    private String openingDate;
    private String closingDate;
    private String desp;
    private String srcAcc;
    private String acc;
    private String curCode;
    private String reference;
    private String deptCode;
    private String refNo;
    private String compCode;
    private String tranSource;
    private String glVouNo;
    private String traderCode;
    private String coaCode;
    private String traderType;
    private String coaLv2;
    private String coaLv1;
    private Integer macId;
    private boolean closing;
    private List<String> listDepartment;
    private boolean summary;

    public ReportFilter(String compCode, Integer macId) {
        this.compCode = compCode;
        this.macId = macId;
    }

}
