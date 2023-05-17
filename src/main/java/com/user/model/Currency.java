/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.user.model;

import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
public class Currency {

    private String curCode;
    private String currencyName;
    private String currencySymbol;
    private boolean active;
    private String curGainAcc;
    private String curLostAcc;

    public Currency(String curCode, String currencyName) {
        this.curCode = curCode;
        this.currencyName = currencyName;
    }

    public Currency() {
    }

    @Override
    public String toString() {
        return curCode;
    }

}
