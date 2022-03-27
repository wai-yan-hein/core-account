/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.acc.model;

import java.util.Date;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
public class VGl {

    private String glCode;
    private Date glDate;
    private String description;
    private String sourceAcId;
    private String accCode;
    private String curCode;
    private Double drAmt;
    private Double crAmt;
    private String reference;
    private String deptCode;
    private String vouNo;
    private String traderCode;
    private String compCode;
    private Date createdDate;
    private Date modifyDate;
    private String modifyBy;
    private String createdBy;
    private String tranSource;
    private String srcAccName;
    private String accName;
    private String curName;
    private String deptName;
    private String deptUsrCode;
    private String traderName;
    private String traderType;
    private String glVouNo;
    private String srcAccCode;
    private Integer splitId;
    private String sourceAccParent;
    private String accParent;
    private String remark;
    private Integer macId;
    private String refNo;

    public VGl(String curCode, Double drAmt, Double crAmt) {
        this.curCode = curCode;
        this.drAmt = drAmt;
        this.crAmt = crAmt;
    }

    public VGl() {
    }

}
