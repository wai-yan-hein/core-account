/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.acc.model;

import java.util.Date;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
public class Gl {

    private GlKey key;
    private Date glDate;
    private String glDateStr;
    private String description;
    private String srcAccCode;
    private String accCode;
    private String curCode;
    private Double drAmt;
    private Double crAmt;
    private String reference;
    private String deptCode;
    private String vouNo;
    private String traderCode;
    private Date createdDate;
    private Date modifyDate;
    private String modifyBy;
    private String createdBy;
    private String tranSource;
    private String glVouNo;
    private Integer splitId;
    private String intgUpdStatus; //For integration update status
    private String remark;
    private String refNo;
    private Integer macId;
    private Integer exchangeId;
    private List<GlKey> delList;
    private String deptUsrCode;
    private String traderName;
    private String srcAccName;
    private String accName;
    private boolean edit;
    private double amount;
    private String fromDes;
    private String forDes;
    private String narration;
    private String batchNo;
    private String vouDate;

    public Gl(String curCode, Double drAmt, Double crAmt) {
        this.curCode = curCode;
        this.drAmt = drAmt;
        this.crAmt = crAmt;
    }

    public Gl() {
    }

    public Gl(String tranSource) {
        this.tranSource = tranSource;
    }

}
