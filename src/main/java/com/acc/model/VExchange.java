/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.model;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Lenovo
 */
public class VExchange implements Serializable {

    private ExchangeKey key;
    private Date exchangeDate;
    private String remark;
    private String compCode;
    private Double exRate;

    public VExchange() {
    }

    public ExchangeKey getKey() {
        return key;
    }

    public void setKey(ExchangeKey key) {
        this.key = key;
    }

    public Date getExchangeDate() {
        return exchangeDate;
    }

    public void setExchangeDate(Date exchangeDate) {
        this.exchangeDate = exchangeDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCompCode() {
        return compCode;
    }

    public void setCompCode(String compCode) {
        this.compCode = compCode;
    }

    public Double getExRate() {
        return exRate;
    }

    public void setExRate(Double exRate) {
        this.exRate = exRate;
    }

}
