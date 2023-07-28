/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.LocalDateTime;
import java.util.Date;
import javax.swing.JOptionPane;

/**
 *
 * @author Lenovo
 */
public class ProUtil {

    public static final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
    public static final String FIXED = "fixed.account";
    public static final String CURRENT = "current.account";
    public static final String CAPITAL = "capital.account";
    public static final String LIA = "liability.account";
    public static final String INCOME = "income.account";
    public static final String OTHER_INCOME = "otherincome.account";
    public static final String PURCHASE = "purchase.account";
    public static final String EXPENSE = "expense.account";
    public static final String PL = "pl.account";
    public static final String RE = "re.account";
    public static final String DEBTOR_GROUP = "debtor.group";
    public static final String DEBTOR_ACC = "debtor.account";
    public static final String CREDITOR_GROUP = "creditor.group";
    public static final String CREDITOR_ACC = "creditor.account";
    public static final String P_SHOW_EXPENSE = "purchase.show.expense";
    public static final String P_SHOW_GRN = "purchase.show.grn";
    public static final String P_BATCH_DETAIL = "purchase.batch.detail";
    public static final String P_COM_AMT = "purchase.commission.amount";
    public static final String MULTI_CUR = "multi.currency";
    public static final String CASH_GROUP = "cash.group";
    public static final String BANK_GROUP = "bank.group";
    public static final String C_CREDIT_AMT = "customer.credit.amount";
    public static final String DISABLE_ALL_FILTER = "disable.all.filter";
    public static final String DEFAULT_LOCATION = "default.location";
    public static final String DEFAULT_CURRENCY = "default.currency";
    public static final String DEFAULT_CASH = "default.cash";
    public static final String DEFAULT_STOCK = "default.stock";
    public static final String DEFAULT_CUSTOMER = "default.customer";
    public static final String DEFAULT_SUPPLIER = "default.supplier";
    public static final String DEFAULT_SALEMAN = "default.saleman";
    public static final String DIVIDER = "divider";
    public static final String SALE_EXPENSE_SHOW = "sale.expense.show";
    public static final String BATCH_SALE = "batch.sale";
    public static final String BATCH_GRN = "batch.grn";
    public static final String P_GRN_REPORT = "purchase.grn.report";

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
        return Util1.getBoolean(Global.hmRoleProperty.get(MULTI_CUR));
    }

    public static boolean isDisableDep() {
        return Util1.getBoolean(Global.hmRoleProperty.get("disable.department"));
    }

    public static boolean isPrint() {
        return Util1.getBoolean(Global.hmRoleProperty.get("printer.print"));
    }

    public static String getInvGroup() {
        return Global.hmRoleProperty.get("inventory.group");
    }

    public static String getIEProcess() {
        return Global.hmRoleProperty.get("system.income.expense.process");
    }

    public static String getConversionAcc() {
        return Global.hmRoleProperty.get("conversion.account");
    }

    public static String getCashGroup() {
        return Global.hmRoleProperty.get("cash.group");
    }

    public static String getReportPath() {
        return "report/";
    }

    public static Integer getDepId() {
        return Util1.getBoolean(Global.hmRoleProperty.get("department.filter")) ? Global.deptId : 0;
    }

    public static Date lockDate() {
        String value = ProUtil.getProperty("data.lock.date");
        int day = lockDay();
        if (day != 0) {
            String today = Util1.toDateStr(Util1.getTodayDate(), "yyyy-MM-dd");
            value = Util1.toDateStr(Util1.minusDay(today, day), "yyyy-MM-dd", Global.dateFormat);
        }
        return value == null ? Util1.parseDate(Global.startDate, Global.dateFormat) : Util1.parseDate(value, Global.dateFormat);
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
        if (date.before(Util1.parseDate(Global.startDate, Global.dateFormat))) {
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
