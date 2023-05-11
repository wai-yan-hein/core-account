/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.acc.model;

import java.util.Date;
import lombok.Data;

/**
 *
 * @author htut
 */
@Data
public class OpeningBalance {

    private OpeningKey key;
    private Date opDate;
    private String sourceAccId;
    private String srcAccName;
    private String curCode;
    private Double crAmt;
    private Double drAmt;
    private String userCode;
    private Date createdDate;
    private String deptCode;
    private String traderCode;
    private String traderName;
    private String tranSource;
    private String deptUsrCode;
    private String traderUsrCode;
    private String traderType;
    private String coaUsrCode;
    private String coaParent;
    private String regCode;
    private String projectNo;

    public OpeningBalance() {

    }
}
