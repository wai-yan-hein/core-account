/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.acc.model;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 *
 * @author htut
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpeningBalance {

    private OpeningKey key;
    private LocalDate opDate;
    private String sourceAccId;
    private String srcAccName;
    private String curCode;
    private double crAmt;
    private double drAmt;
    private String userCode;
    private LocalDateTime createdDate;
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
