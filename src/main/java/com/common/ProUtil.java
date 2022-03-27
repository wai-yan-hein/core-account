/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.text.DateFormat;

/**
 *
 * @author Lenovo
 */
public class ProUtil {

    public static final Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();

    public static boolean isCalStock() {
        return Util1.getBoolean(Global.hmRoleProperty.get("calculate.stock"));
    }

    public static boolean isUnitRelation() {
        return Util1.getBoolean(Global.hmRoleProperty.get("unit.relation"));
    }

    public static String getProperty(String key) {
        return Global.hmRoleProperty.get(key);
    }

    public static boolean isPriceOption() {
        return Util1.getBoolean(Global.hmRoleProperty.get("sale.price.option"));
    }

    public static boolean isMultiCur() {
        return Util1.getBoolean(Global.hmRoleProperty.get("system.multi.currency.flag"));
    }

    public static String getPLProcess() {
        return Global.hmRoleProperty.get("system.profitlost.process");
    }

    public static String getInventoryAcc() {
        return Global.hmRoleProperty.get("system.inventory.coa");
    }

    public static String getIEProcess() {
        return Global.hmRoleProperty.get("system.income.expense.process");
    }

    public static String getCashProcess() {
        return Global.hmRoleProperty.get("system.cash.all.process");
    }

    public static String getBalanceSheetProcess() {
        return Global.hmRoleProperty.get("system.balancesheet.process");
    }

    public static String getConversionAcc() {
        return Global.hmRoleProperty.get("conversion.account");
    }

    public static String getCashGroup() {
        return Global.hmRoleProperty.get("cash.group");
    }

    public static String getReportPath() {
        return "report";
    }

}
