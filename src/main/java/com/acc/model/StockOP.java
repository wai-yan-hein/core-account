/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.acc.model;

import java.util.Date;
import lombok.Data;

/**
 *
 * @author DELL
 */
@Data
public class StockOP {

    private StockOPKey key;
    private Date tranDate;
    private String coaCode;
    private String deptCode;
    private String curCode;
    private String remark;
    private String createdBy;
    private Date createdDate;
    private String updatedBy;
    private boolean deleted;
    private double clAmt;
    private String coaCodeUser;
    private String coaNameEng;
    private String deptUsrCode;
    private String projectNo;
}
