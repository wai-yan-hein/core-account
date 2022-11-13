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
public class ChartOfAccount {

    private COAKey key;
    private String coaNameEng;
    private String coaNameMya;
    private boolean active;
    private Integer sortId;
    private Date createdDate;
    private Date modifiedDate;
    private String createdBy;
    private String modifiedBy;
    private String coaParent;
    private String option;
    private Integer coaLevel;
    private String coaCodeUsr;
    private String parentUsrCode;
    private String appShortName;
    private String migCode;
    private Integer macId;
    private boolean marked;
    private String curCode;
    private String groupCode;
    private String groupUsrCode;
    private String groupName;
    private String headCode;
    private String headUsrCode;
    private String headName;
    private List<ChartOfAccount> child;

    public ChartOfAccount(COAKey key, String coaNameEng) {
        this.key = key;
        this.coaNameEng = coaNameEng;
    }

    public ChartOfAccount() {
    }

    @Override
    public String toString() {
        return coaNameEng;
    }

}
