/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.common;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

/**
 *
 * @author Lenovo
 */
public class ProUtil {

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
    public static final String EMP_ACC = "employee.account";
    public static final String CONVERSION_ACC = "conversion.account";
    public static final String CREDITOR_GROUP = "creditor.group";
    public static final String CREDITOR_ACC = "creditor.account";
    public static final String P_SHOW_EXPENSE = "purchase.show.expense";
    public static final String P_SHOW_STOCKINFO = "purchase.show.stockinfo";
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
    public static final String SALE_STOCKINFO_SHOW = "sale.stockI.info.show";
    public static final String BATCH_SALE = "batch.sale";
    public static final String BATCH_GRN = "batch.grn";
    public static final String P_GRN_REPORT = "purchase.grn.report";
    public static final String PAYMENT_EDIT = "payment.edit";
    public static final String AUTO_UPDATE = "auto.update";
    public static final String WEIGHT_POINT = "weight.point";
    public static final String CHECK_SALE_A4 = "check.sale.A4";
    public static final String CHECK_SALE_A5 = "check.sale.A5";
    public static final String CHECK_SALE_VOUCHER = "check.sale.voucher";
    public static final String SALE_LAST_PRICE = "sale.last.price";
    public static final String STOCK_NAME_WITH_BRAND = "stock.name.with.brand";
    public static final String SALE_EDIT = "sale.voucher.edit";
    public static final String TRANSFER_EDIT = "transfer.voucher.edit";
    public static final String TRANSFER_DELETE = "transfer.voucher.delete";
    public static final String DEPARTMENT_LOCK = "department.lock";
    public static final String MILLING_STOCK_USAGE = "milling.stock.usage";
    public static final String DRCR_REPORT = "drcr.report";
    public static final String ROUND_POINT = "round.point";
    public static final String DISABLE_MILL = "disable.calculate.milling.stock";
    public static final String DISABLE_SALE = "disable.calculate.sale.stock";
    public static final String DISABLE_PUR = "disable.calculate.purchase.stock";
    public static final String DISABLE_RETIN = "disable.calculate.returnin.stock";
    public static final String DISABLE_RETOUT = "disable.calculate.returnout.stock";
    public static final String DISABLE_PATTERN_IO = "disable.pattern.stockio";
    public static final String STOCK_IO_A5 = "stockio.report.A5";
    public static final String SALE_PRICE_OPTION = "sale.price.option";
    public static final String DISABLE_DR_VOUCHER = "disable.dr.voucher";
    public static final String DISABLE_CR_VOUCHER = "disable.cr.voucher";
    public static final String STOCK_IO_VOUCHER = "stockio.voucher";
    public static final String TRANSFER_VOUCHER = "transfer.voucher";
    public static final String PURCHASE_VOUCHER = "report.purchase.voucher";
    public static final String SALE_PRINT_COUNT = "sale.print.count";
    public static final String PURCHASE_PRINT_COUNT = "purchase.print.count";
    public static final String RETURNIN_PRINT_COUNT = "returnin.print.count";
    public static final String RETURNOUT_PRINT_COUNT = "returnout.print.count";
    public static final String LANDING_PRINT_COUNT = "landing.print.count";
    public static final String TRANSFER_PRINT_COUNT = "transfer.print.count";
    public static final String STOCKIO_PRINT_COUNT = "stockio.print.count";
    public static final String MILLING_PRINT_COUNT = "milling.print.count";
    public static final String STOCK_NO_UNIT = "stock.no.unit";
    public static final String MAX_STOCK_WEIGHT = "max.stock.weight";
    public static final String PUR_RD_DIS = "purchase.round.down.discount";
    public static final String DECIMAL_PLACE = "decimal.palace";
    public static final String STOCK_IR_EDIT = "stockir.voucher";
    public static final String SALE_VOU = "report.sale.voucher";
    public static final String SALE_RICE = "report.sale.rice";
    public static final String SALE_PADDY = "report.sale.paddy";
    public static final String SALE_VOU_A5 = "report.sale.A5";
    public static final String SALE_VOU_A4 = "report.sale.A4";
    public static final String SALE_VOU_NOTE = "report.sale.note";
    public static final String ORDER_VOU = "report.order.voucher";
    public static final String STOCK_USE_WEIHGT = "stock.use.weight";
    public static final String DEFAULT_STOCK_REC = "default.stock.receivable";
    public static final String DEFAULT_STOCK_PAY = "default.stock.payable";
    public static final String DEFAULT_STOCK_RI_REC = "default.stock.ri.receivable";
    public static final String DEFAULT_STOCK_RO_PAY = "default.stock.ro.payable";
    public static final String DEFAULT_STOCK_SKIP_INV = "default.stock.skip.inventory";
    public static final String TRADER_BAL_ACC = "trader.balance.account";
    public static final String PRINTER_NAME = "printer.name";
    public static final String PRINTER_POS_NAME = "printer.pos.name";
    public static final String PRINTER_PAGE = "printer.pages";
    public static final String BARCODE = "use.barcode";
    public static final String DARK_MODE = "dark.mode";
    public static final String ENABLE_TRADER_COA = "enable.trader.coa";
    public static final String PURCHASE_VOUCHER_EDIT = "purchase.voucher.edit";
    public static final String SALE_CLOSED = "sale.closed";
    public static final String LOGO = "logo.name";
    public static final String WATERMARK = "wartermark.name";
    public static final String SS_CONTAIN = "stock.search.contain";
    public static int getDecimalPalace() {
        return Util1.getInteger(ProUtil.getProperty(ProUtil.DECIMAL_PLACE));
    }

    public static boolean isPurRDDis() {
        return Util1.getBoolean(Global.hmRoleProperty.get(PUR_RD_DIS));
    }

    public static boolean isDisableDrVoucher() {
        return Util1.getBoolean(Global.hmRoleProperty.get(DISABLE_DR_VOUCHER));
    }

    public static boolean isDisableCrVoucher() {
        return Util1.getBoolean(Global.hmRoleProperty.get(DISABLE_CR_VOUCHER));
    }

    public static String getDrCrReport() {
        return Global.hmRoleProperty.get(DRCR_REPORT);
    }

    public static String getFontPath() {
        return Global.hmRoleProperty.get("font.path");
    }

    public static boolean isDepartmentLock() {
        return Util1.getBoolean(Global.hmRoleProperty.get(DEPARTMENT_LOCK));
    }

    public static boolean isSalePaid() {
        return Util1.getBoolean(Global.hmRoleProperty.get("default.sale.paid"));
    }

    public static boolean isSaleEdit() {
        return Util1.getBoolean(Global.hmRoleProperty.get(SALE_EDIT));
    }

    public static boolean isTransferEdit() {
        return Util1.getBoolean(Global.hmRoleProperty.get(TRANSFER_EDIT));
    }

    public static boolean isPaymentEdit() {
        return Util1.getBoolean(Global.hmRoleProperty.get(ProUtil.PAYMENT_EDIT));
    }

    public static boolean isPurchaseEdit() {
        return Util1.getBoolean(Global.hmRoleProperty.get(PURCHASE_VOUCHER_EDIT));
    }

    public static boolean isCalStock() {
        return Util1.getBoolean(Global.hmRoleProperty.get("calculate.stock"));
    }
    public static boolean isSSContain() {
        return Util1.getBoolean(Global.hmRoleProperty.get(SS_CONTAIN));
    }

    public static boolean isUnitRelation() {
        return Util1.getBoolean(Global.hmRoleProperty.get("unit.relation"));
    }

    public static String getProperty(String key) {
        return Global.hmRoleProperty.get(key);
    }

    public static boolean isTraderBalAcc() {
        return Util1.getBoolean(Global.hmRoleProperty.get(TRADER_BAL_ACC));
    }

    public static boolean isStockNoUnit() {
        return Util1.getBoolean(Global.hmRoleProperty.get(STOCK_NO_UNIT));
    }

    public static boolean isDisableMill() {
        return Util1.getBoolean(Global.hmRoleProperty.get(DISABLE_MILL));
    }

    public static boolean isDisableSale() {
        return Util1.getBoolean(Global.hmRoleProperty.get(DISABLE_SALE));
    }

    public static boolean isDisablePur() {
        return Util1.getBoolean(Global.hmRoleProperty.get(DISABLE_PUR));
    }

    public static boolean isDisableRetIn() {
        return Util1.getBoolean(Global.hmRoleProperty.get(DISABLE_RETIN));
    }

    public static boolean isDisableRetOut() {
        return Util1.getBoolean(Global.hmRoleProperty.get(DISABLE_RETOUT));
    }

    public static boolean isPriceOption() {
        return Util1.getBoolean(Global.hmRoleProperty.get(SALE_PRICE_OPTION));
    }

    public static boolean isPricePopup() {
        return Util1.getBoolean(Global.hmRoleProperty.get("sale.price.popup"));
    }

    public static boolean isSalePriceChange() {
        return Util1.getBoolean(Global.hmRoleProperty.get("sale.price.change"));
    }

    public static boolean isUseWeight() {
        return Util1.getBoolean(Global.hmRoleProperty.get(STOCK_USE_WEIHGT));
    }

    public static boolean isUseWeightPoint() {
        return Util1.getBoolean(Global.hmRoleProperty.get(WEIGHT_POINT));
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

    public static boolean isMillingStockUsage() {
        return Util1.getBoolean(Global.hmRoleProperty.get(MILLING_STOCK_USAGE));
    }

    public static boolean isPrint() {
        return Util1.getBoolean(Global.hmRoleProperty.get("printer.print"));
    }

    public static boolean isTraderCOA() {
        return Util1.getBoolean(Global.hmRoleProperty.get(ENABLE_TRADER_COA));
    }

    public static boolean isStockIREdit() {
        return Util1.getBoolean(Global.hmRoleProperty.get(STOCK_IR_EDIT));
    }

    public static boolean useBarCode() {
        return Util1.getBoolean(Global.hmRoleProperty.get(BARCODE));
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


    public static double getMaxSW() {
        double wt = Util1.getDouble(Global.hmRoleProperty.get(MAX_STOCK_WEIGHT));
        return wt == 0 ? 500 : wt;
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

    public static boolean isShowAcc(String tranSource) {
        return switch (tranSource) {
            case "SALE", "PURCHASE", "RETURN_IN", "RETURN_OUT", "DR", "CR" ->
                true;
            default ->
                false;
        };
    }

    public static String logoPath() {
        return String.format("images%s%s", File.separator, ProUtil.getProperty(LOGO));
    }

    public static String waterMark() {
        return String.format("images%s%s", File.separator, ProUtil.getProperty(WATERMARK));
    }

    public static DefaultFormatterFactory getDecimalFormatter() {
        int place = ProUtil.getDecimalPalace();
        return switch (place) {
            case 1 ->
                new DefaultFormatterFactory(new NumberFormatter(new DecimalFormat(Util1.DECIMAL_FORMAT1)));
            case 2 ->
                new DefaultFormatterFactory(new NumberFormatter(new DecimalFormat(Util1.DECIMAL_FORMAT2)));
            default ->
                new DefaultFormatterFactory(new NumberFormatter(new DecimalFormat(Util1.DECIMAL_FORMAT)));
        };
    }

    public static List<String> getListPl() {
        List<String> list = new ArrayList<>();
        list.add(getProperty(INCOME));
        list.add(getProperty(OTHER_INCOME));
        list.add(getProperty(PURCHASE));
        list.add(getProperty(EXPENSE));
        return list;
    }

    public static List<String> getListBS() {
        List<String> list = new ArrayList<>();
        list.add(getProperty(FIXED));
        list.add(getProperty(CURRENT));
        list.add(getProperty(LIA));
        list.add(getProperty(CAPITAL));
        return list;
    }

    public static DecimalFormat getDecimalFormat() {
        int place = ProUtil.getDecimalPalace();
        return switch (place) {
            case 1 ->
                new DecimalFormat(Util1.DECIMAL_FORMAT1);
            case 2 ->
                new DecimalFormat(Util1.DECIMAL_FORMAT2);
            case 3 ->
                new DecimalFormat(Util1.DECIMAL_FORMAT3);
            case 4 ->
                new DecimalFormat(Util1.DECIMAL_FORMAT4);
            default ->
                new DecimalFormat(Util1.DECIMAL_FORMAT);
        };
    }
}
