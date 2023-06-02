/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReturnObject {

    private Double openInv;
    private Double clInv;
    private Double cos;
    private String cosPercent;
    private Double ttlIncome;
    private Double ttlPurchase;
    private Double ttlExpense;
    private Double ttlOtherIncome;
    private Double grossProfit;
    private String gpPercent;
    private Double netProfit;
    private String npPercent;
    private Double ttlFixAss;
    private Double ttlCurAss;
    private Double ttlCapital;
    private Double ttlLia;
    private String status;
    private String message;
    private String errorMessage;
    private List<Object> list;
    private Object data;
    private byte[] file;
    private double opAmt;
    private double clAmt;
    private String vouNo;
    private String glVouNo;
    private String tranSource;
    private String opDate;
    private String compCode;
}
