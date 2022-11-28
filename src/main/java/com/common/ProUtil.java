/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.text.DateFormat;
import java.util.Date;
import javax.swing.JOptionPane;

/**
 *
 * @author Lenovo
 */
public class ProUtil {

    public static final Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();

    public static String getFontPath() {
        return Global.hmRoleProperty.get("font.path");
    }

    public static boolean isSalePaid() {
        return Util1.getBoolean(Global.hmRoleProperty.get("default.sale.paid"));
    }

    public static boolean isSaleEdit() {
        return Util1.getBoolean(Global.hmRoleProperty.get("sale.voucher.edit"));
    }

    public static boolean isPurchaseEdit() {
        return Util1.getBoolean(Global.hmRoleProperty.get("purchase.voucher.edit"));
    }

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

    public static boolean isPricePopup() {
        return Util1.getBoolean(Global.hmRoleProperty.get("sale.price.popup"));
    }

    public static boolean isSalePriceChange() {
        return Util1.getBoolean(Global.hmRoleProperty.get("sale.price.change"));
    }

    public static boolean isWeightOption() {
        return Util1.getBoolean(Global.hmRoleProperty.get("stock.use.weight"));
    }

    public static boolean isStockNameWithCategory() {
        return Util1.getBoolean(Global.hmRoleProperty.get("stock.name.with.category"));
    }

    public static boolean isSaleLastPrice() {
        return Util1.getBoolean(Global.hmRoleProperty.get("sale.last.price"));
    }

    public static boolean isMultiCur() {
        return Util1.getBoolean(Global.hmRoleProperty.get("system.multi.currency.flag"));
    }

    public static boolean isPrint() {
        return Util1.getBoolean(Global.hmRoleProperty.get("printer.print"));
    }

    public static String getPLProcess() {
        return Global.hmRoleProperty.get("pl.process");
    }

    public static String getInvGroup() {
        return Global.hmRoleProperty.get("inventory.group");
    }

    public static String getIEProcess() {
        return Global.hmRoleProperty.get("system.income.expense.process");
    }

    public static String getCashProcess() {
        return Global.hmRoleProperty.get("system.cash.all.process");
    }

    public static String getBalanceSheetProcess() {
        return Global.hmRoleProperty.get("balancesheet.process");
    }

    public static String getConversionAcc() {
        return Global.hmRoleProperty.get("conversion.account");
    }

    public static String getCashGroup() {
        return Global.hmRoleProperty.get("cash.group");
    }

    public static String getIncomeExpenseProcess() {
        String ig = Global.hmRoleProperty.get("income.expense.process");
        return ig;
    }

    public static String getReportPath() {
        return "report/";
    }

    public static Date lockDate() {
        String value = ProUtil.getProperty("data.lock.date");
        int day = lockDay();
        if (day != 0) {
            String today = Util1.toDateStr(Util1.getTodayDate(), "yyyy-MM-dd");
            value = Util1.toDateStr(Util1.minusDay(today, day), "yyyy-MM-dd", "dd/MM/yyyy");
        }
        return value == null ? Util1.toDate(Global.startDate) : Util1.toDate(value, "dd/MM/yyyy");
    }

    public static int lockDay() {
        String value = ProUtil.getProperty("data.lock.day");
        return value == null ? 0 : Util1.getInteger(value);
    }

    public static boolean isValidDate(Date date) {
        if (date.after(Util1.getTodayDate())) {
            JOptionPane.showMessageDialog(Global.parentForm, "Date is due.", "Invalid Date", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (date.before(Util1.toDate(Global.startDate, Global.dateFormat))) {
            JOptionPane.showMessageDialog(Global.parentForm, "Date is not in finacial period.", "Invalid Date", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (date.before(lockDate())) {
            JOptionPane.showMessageDialog(Global.parentForm, "Date is locked.", "Invalid Date", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
}
