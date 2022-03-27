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
    private String glVouNo;
    private Integer splitId;
    private String intgUpdStatus; //For integration update status
    private String remark;
    private String narration;
    private String refNo;
    private Integer macId;
    private Integer exchangeId;
    private List<String> delList;
}
