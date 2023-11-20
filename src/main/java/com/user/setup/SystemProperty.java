/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.user.setup;

import com.repo.AccountRepo;
import com.acc.editor.COA3AutoCompleter;
import com.acc.editor.DepartmentAutoCompleter;
import com.acc.model.DepartmentA;
import com.common.Global;
import com.common.PanelControl;
import com.common.ProUtil;
import com.user.model.RoleProperty;
import com.user.model.RolePropertyKey;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.editor.StockAutoCompleter;
import com.inventory.editor.TraderAutoCompleter;
import com.inventory.model.Location;
import com.inventory.model.MessageType;
import com.inventory.model.Stock;
import com.user.model.SysProperty;
import com.inventory.model.Trader;
import com.repo.InventoryRepo;
import com.repo.UserRepo;
import com.user.dialog.PrintCountDialog;
import com.user.dialog.ReportNameDialog;
import com.user.dialog.ValidationDialog;
import com.user.editor.MacAutoCompleter;
import com.user.editor.TextAutoCompleter;
import com.user.model.MachineProperty;
import com.user.model.MachinePropertyKey;
import com.user.model.PropertyKey;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class SystemProperty extends javax.swing.JPanel implements SelectionObserver, PanelControl {

    private UserRepo userRepo;
    private InventoryRepo inventoryRepo;
    private AccountRepo accountRepo;
    private SelectionObserver observer;
    private JProgressBar progress;
    private TextAutoCompleter printerAutoCompleter;
    private TraderAutoCompleter cusCompleter;
    private TraderAutoCompleter supCompleter;
    private StockAutoCompleter stockAutoCompleter;
    private LocationAutoCompleter locCompleter;
    private DepartmentAutoCompleter departmentAutoCompleter;
    private COA3AutoCompleter cashAutoCompleter;
    private COA3AutoCompleter plAutoCompleter;
    private COA3AutoCompleter reAutoCompleter;
    private COA3AutoCompleter inventoryAutoCompleter;
    private COA3AutoCompleter cashGroupAutoCompleter;
    private COA3AutoCompleter bankGroupAutoCompleter;
    private COA3AutoCompleter fixedAutoCompleter;
    private COA3AutoCompleter currentAutoCompleter;
    private COA3AutoCompleter capitalAutoCompleter;
    private COA3AutoCompleter liaAutoCompleter;
    private COA3AutoCompleter incomeAutoCompleter;
    private COA3AutoCompleter otherIncomeAutoCompleter;
    private COA3AutoCompleter purchaseAutoCompleter;
    private COA3AutoCompleter expenseAutoCompleter;
    private COA3AutoCompleter debtorGroupAutoCompleter;
    private COA3AutoCompleter debtorAccAutoCompleter;
    private COA3AutoCompleter creditorGroupAutoCompleter;
    private COA3AutoCompleter creditorAccAutoCompleter;
    private COA3AutoCompleter conversionAccAutoCompleter;

    private MacAutoCompleter macAutoCompleter;
    private HashMap<String, String> hmProperty;
    private String properyType = "System";
    private String roleCode;
    private Integer macId;
    private ReportNameDialog reportNameDialog;
    private PrintCountDialog printCountDialog;
    private ValidationDialog validationDialog;
    private HashMap<JTextField, COA3AutoCompleter> hmCOA3 = new HashMap<>();

    public void setAccountRepo(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public void setMacId(Integer macId) {
        this.macId = macId;
    }

    public void setProperyType(String properyType) {
        this.properyType = properyType;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }

    private final ActionListener action = (ActionEvent e) -> {
        if (e.getSource() instanceof JCheckBox chk) {
            String key = chk.getName();
            String value = Util1.getString(chk.isSelected());
            save(key, value);
        }
        if (e.getSource() instanceof JTextField txt) {
            String key = txt.getName();
            String value = txt.getText();
            save(key, value);
        }
    };

    /**
     * Creates new form SystemProperty
     */
    public SystemProperty() {
        initComponents();
        initAction();
        initKey();
    }

    private void initKey() {
        txtDep.setName("default.department");
        chkDepFilter.setName("department.filter");
        chkDepOption.setName("department.option");
        chkPriceOption.setName(ProUtil.SALE_PRICE_OPTION);
        txtStock.setName(ProUtil.DEFAULT_STOCK);
        txtCash.setName(ProUtil.DEFAULT_CASH);
        txtPages.setName("printer.pages");
        txtInvGroup.setName("inventory.group");
        txtCashGroup.setName(ProUtil.CASH_GROUP);
        txtBankGroup.setName(ProUtil.BANK_GROUP);
        txtComP.setName("purchase.commission");
        txtCustomer.setName(ProUtil.DEFAULT_CUSTOMER);
        txtSupplier.setName(ProUtil.DEFAULT_SUPPLIER);
        txtLocation.setName(ProUtil.DEFAULT_LOCATION);
        chkPrint.setName("printer.print");
        chkDisableDep.setName("disable.department");
        chkSaleEdit.setName(ProUtil.SALE_EDIT);
        chkSWB.setName(ProUtil.STOCK_NAME_WITH_BRAND);
        chkSVou.setName(ProUtil.CHECK_SALE_VOUCHER);
        chkSLP.setName(ProUtil.SALE_LAST_PRICE);
        chkSA4.setName(ProUtil.CHECK_SALE_A4);
        chkSA5.setName(ProUtil.CHECK_SALE_A5);
        chkShowExpense.setName(ProUtil.P_SHOW_EXPENSE);
        chkShowStockInfo.setName(ProUtil.P_SHOW_STOCKINFO);
        chkShowGRN.setName(ProUtil.P_SHOW_GRN);
        chkPurByBatchDetail.setName(ProUtil.P_BATCH_DETAIL);
        chkMulti.setName(ProUtil.MULTI_CUR);
        chkDisableAll.setName(ProUtil.DISABLE_ALL_FILTER);
        chkSaleExpenseShown.setName(ProUtil.SALE_EXPENSE_SHOW);
        chkSaleStockInfo.setName(ProUtil.SALE_STOCKINFO_SHOW);
        chkBatchSale.setName(ProUtil.BATCH_SALE);
        chkBatchGRN.setName(ProUtil.BATCH_GRN);
        chkPurGRNReport.setName(ProUtil.P_GRN_REPORT);
        chkPaymentEdit.setName(ProUtil.PAYMENT_EDIT);
        chkAutoUpdate.setName(ProUtil.AUTO_UPDATE);
        chkWeightPoint.setName(ProUtil.WEIGHT_POINT);
        chkTransferEdit.setName(ProUtil.TRANSFER_EDIT);
        chkTransferDelete.setName(ProUtil.TRANSFER_DELETE);
        chkDepLock.setName(ProUtil.DEPARTMENT_LOCK);
        chkUsage.setName(ProUtil.MILLING_STOCK_USAGE);
        chkDisableMill.setName(ProUtil.DISABLE_MILL);
        chkDisableDrVoucher.setName(ProUtil.DISABLE_DR_VOUCHER);
        chkDisableCrVoucher.setName(ProUtil.DISABLE_CR_VOUCHER);
        chkNoUnit.setName(ProUtil.STOCK_NO_UNIT);
        txtPlAcc.setName(ProUtil.PL);
        txtREAcc.setName(ProUtil.RE);
        txtFixed.setName(ProUtil.FIXED);
        txtCurrent.setName(ProUtil.CURRENT);
        txtLiability.setName(ProUtil.LIA);
        txtCapital.setName(ProUtil.CAPITAL);
        txtIncome.setName(ProUtil.INCOME);
        txtOtherIncome.setName(ProUtil.OTHER_INCOME);
        txtPurchase.setName(ProUtil.PURCHASE);
        txtExpense.setName(ProUtil.EXPENSE);
        txtDebtor.setName(ProUtil.DEBTOR_GROUP);
        txtCreditor.setName(ProUtil.CREDITOR_GROUP);
        txtCus.setName(ProUtil.DEBTOR_ACC);
        txtSup.setName(ProUtil.CREDITOR_ACC);
        txtComAmt.setName(ProUtil.P_COM_AMT);
        txtDivider.setName(ProUtil.DIVIDER);
        txtConversionAcc.setName(ProUtil.CONVERSION_ACC);
        txtDrCr.setName(ProUtil.DRCR_REPORT);

    }

    private void initProperty() {
        switch (properyType) {
            case "System" -> {
                hmProperty = new HashMap<>();
                userRepo.getSystemProperty().block().forEach((t) -> {
                    hmProperty.put(t.getKey().getPropKey(), t.getPropValue());
                });
            }
            case "Role" ->
                hmProperty = userRepo.getRoleProperty(roleCode);
            case "Machine" ->
                hmProperty = userRepo.getMachineProperty(macId);
        }
    }

    private void initMac() {
        if (properyType.equals("Machine")) {
            panelMac.setVisible(true);
            userRepo.getMacList().subscribe((t) -> {
                macAutoCompleter = new MacAutoCompleter(txtMac, t, null, false);
                macAutoCompleter.setObserver(this);
            });

        } else {
            panelMac.setVisible(false);
        }
    }

    public void initMain() {
        progress.setIndeterminate(true);
        initMac();
        initProperty();
        initCheckBox();
        initTextBox();
        initCombo();
        initHashCOA();
    }

    private void initAction() {
        chkSA4.addActionListener(action);
        chkSA5.addActionListener(action);
        chkSVou.addActionListener(action);
        chkSLP.addActionListener(action);
        chkPrint.addActionListener(action);
        chkSWB.addActionListener(action);
        chkPricePopup.addActionListener(action);
        chkCalStock.addActionListener(action);
        chkSalePaid.addActionListener(action);
        chkSaleEdit.addActionListener(action);
        chkPurVouEdit.addActionListener(action);
        chkBalance.addActionListener(action);
        chkWeight.addActionListener(action);
        chkWeightPoint.addActionListener(action);
        chkDisableSale.addActionListener(action);
        chkPriceChange.addActionListener(action);
        chkDisablePur.addActionListener(action);
        chkDisableRI.addActionListener(action);
        chkDisableRO.addActionListener(action);
        chkDisableStockIO.addActionListener(action);
        chkDepFilter.addActionListener(action);
        chkDepOption.addActionListener(action);
        chkPriceOption.addActionListener(action);
        chkDisableDep.addActionListener(action);
        chkShowExpense.addActionListener(action);
        chkShowStockInfo.addActionListener(action);
        chkShowGRN.addActionListener(action);
        chkPurByBatchDetail.addActionListener(action);
        chkMulti.addActionListener(action);
        chkDisableAll.addActionListener(action);
        chkSaleExpenseShown.addActionListener(action);
        chkSaleStockInfo.addActionListener(action);
        chkBatchGRN.addActionListener(action);
        chkBatchSale.addActionListener(action);
        chkPurGRNReport.addActionListener(action);
        chkPaymentEdit.addActionListener(action);
        chkAutoUpdate.addActionListener(action);
        chkTransferEdit.addActionListener(action);
        chkTransferDelete.addActionListener(action);
        chkDepLock.addActionListener(action);
        chkUsage.addActionListener(action);
        chkDisableMill.addActionListener(action);
        chkDisableDrVoucher.addActionListener(action);
        chkDisableCrVoucher.addActionListener(action);
        chkNoUnit.addActionListener(action);
        //txt
        txtA4Report.addActionListener(action);
        txtA5Report.addActionListener(action);
        txtVouReport.addActionListener(action);
        txtLogoName.addActionListener(action);
        txtDebtor.addActionListener(action);
        txtCreditor.addActionListener(action);
        txtCus.addActionListener(action);
        txtSup.addActionListener(action);
        txtCash.addActionListener(action);
        txtPages.addActionListener(action);
        txtComP.addActionListener(action);
        txtComAmt.addActionListener(action);
        txtCreditAmt.addActionListener(action);
        txtDivider.addActionListener(action);
        txtDrCr.addActionListener(action);
    }

    private void initCheckBox() {
        chkSA4.setSelected(Util1.getBoolean(hmProperty.get(chkSA4.getName())));
        chkSA5.setSelected(Util1.getBoolean(hmProperty.get(chkSA5.getName())));
        chkSVou.setSelected(Util1.getBoolean(hmProperty.get(chkSVou.getName())));
        chkSLP.setSelected(Util1.getBoolean(hmProperty.get(chkSLP.getName())));
        chkPrint.setSelected(Util1.getBoolean(hmProperty.get(chkPrint.getName())));
        chkSWB.setSelected(Util1.getBoolean(hmProperty.get(chkSWB.getName())));
        chkPricePopup.setName("sale.price.popup");
        chkPricePopup.setSelected(Util1.getBoolean(hmProperty.get("sale.price.popup")));
        chkCalStock.setName("calculate.stock");
        chkCalStock.setSelected(Util1.getBoolean(hmProperty.get("calculate.stock")));
        chkSalePaid.setName("default.sale.paid");
        chkSalePaid.setSelected(Util1.getBoolean(hmProperty.get("default.sale.paid")));
        chkSaleEdit.setSelected(Util1.getBoolean(chkSaleEdit.getName()));
        chkPriceChange.setName("sale.price.change");
        chkPriceChange.setSelected(Util1.getBoolean(hmProperty.get("sale.price.change")));
        chkPurVouEdit.setName("purchase.voucher.edit");
        chkPurVouEdit.setSelected(Util1.getBoolean(hmProperty.get("purchase.voucher.edit")));
        chkBalance.setName("trader.balance");
        chkBalance.setSelected(Util1.getBoolean(hmProperty.get("trader.balance")));
        chkWeight.setName("stock.use.weight");
        chkWeight.setSelected(Util1.getBoolean(hmProperty.get("stock.use.weight")));
        chkDisableSale.setName("disable.calculate.sale.stock");
        chkDisableSale.setSelected(Util1.getBoolean(hmProperty.get("disable.calculate.sale.stock")));
        chkDisablePur.setName("disable.calculate.purchase.stock");
        chkDisablePur.setSelected(Util1.getBoolean(hmProperty.get("disable.calculate.purchase.stock")));
        chkDisableRI.setName("disable.calculate.returin.stock");
        chkDisableRI.setSelected(Util1.getBoolean(hmProperty.get("disable.calculate.returin.stock")));
        chkDisableRO.setName("disable.calculate.returnout.stock");
        chkDisableRO.setSelected(Util1.getBoolean(hmProperty.get("disable.calculate.returnout.stock")));
        chkDisableStockIO.setName("disable.pattern.stockio");
        chkDisableStockIO.setSelected(Util1.getBoolean(hmProperty.get("disable.pattern.stockio")));
        chkDepFilter.setSelected(Util1.getBoolean(hmProperty.get(chkDepFilter.getName())));
        chkDepOption.setSelected(Util1.getBoolean(hmProperty.get(chkDepOption.getName())));
        chkPriceOption.setSelected(Util1.getBoolean(hmProperty.get(chkPriceOption.getName())));
        chkDisableDep.setSelected(Util1.getBoolean(hmProperty.get(chkDisableDep.getName())));
        chkShowExpense.setSelected(Util1.getBoolean(hmProperty.get(chkShowExpense.getName())));
        chkShowStockInfo.setSelected(Util1.getBoolean(hmProperty.get(chkShowStockInfo.getName())));
        chkShowGRN.setSelected(Util1.getBoolean(hmProperty.get(chkShowGRN.getName())));
        chkPurByBatchDetail.setSelected(Util1.getBoolean(hmProperty.get(chkPurByBatchDetail.getName())));
        chkMulti.setSelected(Util1.getBoolean(hmProperty.get(chkMulti.getName())));
        chkDisableAll.setSelected(Util1.getBoolean(hmProperty.get(chkDisableAll.getName())));
        chkSaleExpenseShown.setSelected(Util1.getBoolean(hmProperty.get(chkSaleExpenseShown.getName())));
        chkSaleStockInfo.setSelected(Util1.getBoolean(hmProperty.get(chkSaleStockInfo.getName())));
        chkPaymentEdit.setSelected(Util1.getBoolean(hmProperty.get(chkPaymentEdit.getName())));
        chkAutoUpdate.setSelected(Util1.getBoolean(hmProperty.get(chkAutoUpdate.getName())));
        chkWeightPoint.setSelected(Util1.getBoolean(hmProperty.get(chkWeightPoint.getName())));
        chkTransferEdit.setSelected(Util1.getBoolean(hmProperty.get(chkTransferEdit.getName())));
        chkTransferDelete.setSelected(Util1.getBoolean(hmProperty.get(chkTransferDelete.getName())));
        chkDepLock.setSelected(Util1.getBoolean(hmProperty.get(chkDepLock.getName())));
        chkUsage.setSelected(Util1.getBoolean(hmProperty.get(chkUsage.getName())));
        chkDisableMill.setSelected(Util1.getBoolean(hmProperty.get(chkDisableMill.getName())));
        chkDisableDrVoucher.setSelected(Util1.getBoolean(hmProperty.get(chkDisableDrVoucher.getName())));
        chkDisableCrVoucher.setSelected(Util1.getBoolean(hmProperty.get(chkDisableCrVoucher.getName())));
        chkNoUnit.setSelected(Util1.getBoolean(hmProperty.get(chkNoUnit.getName())));
    }

    private void initTextBox() {
        txtVouReport.setName("report.sale.voucher");
        txtVouReport.setText(hmProperty.get("report.sale.voucher"));
        txtCreditAmt.setName(ProUtil.C_CREDIT_AMT);
        txtCreditAmt.setText(hmProperty.get(txtCreditAmt.getName()));
        txtA5Report.setName("report.sale.A5");
        txtA5Report.setText(hmProperty.get("report.sale.A5"));
        txtA4Report.setName("report.sale.A4");
        txtA4Report.setText(hmProperty.get("report.sale.A4"));
        txtLogoName.setName("logo.name");
        txtLogoName.setText(hmProperty.get("logo.name"));
        txtPages.setText(hmProperty.get(txtPages.getName()));
        txtComP.setText(hmProperty.get(txtComP.getName()));
        txtComAmt.setText(hmProperty.get(txtComAmt.getName()));
        txtDivider.setText(hmProperty.get(txtDivider.getName()));
        txtDrCr.setText(hmProperty.get(txtDrCr.getName()));
    }

    private void initCombo() {
        printerAutoCompleter = new TextAutoCompleter(txtPrinter, getPrinter(), null);
        printerAutoCompleter.setObserver(this);
        printerAutoCompleter.setText(hmProperty.get("printer.name"));
        cusCompleter = new TraderAutoCompleter(txtCustomer, inventoryRepo, null, false, "CUS");
        cusCompleter.setObserver(this);
        inventoryRepo.findTrader(hmProperty.get(txtCustomer.getName())).doOnSuccess((t) -> {
            cusCompleter.setTrader(t);
        }).subscribe();
        supCompleter = new TraderAutoCompleter(txtSupplier, inventoryRepo, null, false, "SUP");
        supCompleter.setObserver(this);
        inventoryRepo.findTrader(hmProperty.get(txtSupplier.getName())).doOnSuccess((t) -> {
            supCompleter.setTrader(t);
        }).subscribe();
        locCompleter = new LocationAutoCompleter(txtLocation, null, false, false);
        locCompleter.setObserver(this);
        inventoryRepo.getLocation().doOnSuccess((t) -> {
            locCompleter.setListLocation(t);
        }).subscribe();
        inventoryRepo.findLocation(hmProperty.get(txtLocation.getName())).doOnSuccess((t) -> {
            locCompleter.setLocation(t);
        }).subscribe();
        stockAutoCompleter = new StockAutoCompleter(txtStock, inventoryRepo, null, false);
        stockAutoCompleter.setObserver(this);
        inventoryRepo.findStock(hmProperty.get(txtStock.getName())).doOnSuccess((t) -> {
            stockAutoCompleter.setStock(t);
        }).subscribe();
        departmentAutoCompleter = new DepartmentAutoCompleter(txtDep, null, false, false);
        departmentAutoCompleter.setObserver(this);
        accountRepo.findDepartment(hmProperty.get(txtDep.getName())).doOnSuccess((t) -> {
            departmentAutoCompleter.setDepartment(t);
        }).subscribe();
        accountRepo.getDepartment().doOnSuccess((t) -> {
            departmentAutoCompleter.setListDepartment(t);
        }).subscribe();
        cashAutoCompleter = new COA3AutoCompleter(txtCash, accountRepo, null, false, 3);
        cashAutoCompleter.setObserver(this);
        accountRepo.findCOA(hmProperty.get(txtCash.getName())).doOnSuccess((t) -> {
            cashAutoCompleter.setCoa(t);
        }).subscribe();
        plAutoCompleter = new COA3AutoCompleter(txtPlAcc, accountRepo, null, false, 3);
        plAutoCompleter.setObserver(this);
        accountRepo.findCOA(hmProperty.get(txtPlAcc.getName())).doOnSuccess((t) -> {
            plAutoCompleter.setCoa(t);
        }).subscribe();
        reAutoCompleter = new COA3AutoCompleter(txtREAcc, accountRepo, null, false, 3);
        reAutoCompleter.setObserver(this);
        accountRepo.findCOA(hmProperty.get(txtREAcc.getName())).doOnSuccess((t) -> {
            reAutoCompleter.setCoa(t);
        }).subscribe();

        inventoryAutoCompleter = new COA3AutoCompleter(txtInvGroup, accountRepo, null, false, 2);
        inventoryAutoCompleter.setObserver(this);
        accountRepo.findCOA(hmProperty.get(txtInvGroup.getName())).doOnSuccess((t) -> {
            inventoryAutoCompleter.setCoa(t);
        }).subscribe();
        cashGroupAutoCompleter = new COA3AutoCompleter(txtCashGroup, accountRepo, null, false, 2);
        cashGroupAutoCompleter.setObserver(this);
        accountRepo.findCOA(hmProperty.get(txtCashGroup.getName())).doOnSuccess((t) -> {
            cashGroupAutoCompleter.setCoa(t);
        }).subscribe();
        bankGroupAutoCompleter = new COA3AutoCompleter(txtBankGroup, accountRepo, null, false, 2);
        bankGroupAutoCompleter.setObserver(this);
        accountRepo.findCOA(hmProperty.get(txtBankGroup.getName())).doOnSuccess((t) -> {
            bankGroupAutoCompleter.setCoa(t);
        }).subscribe();
        fixedAutoCompleter = new COA3AutoCompleter(txtFixed, accountRepo, null, false, 1);
        fixedAutoCompleter.setObserver(this);
        accountRepo.findCOA(hmProperty.get(txtFixed.getName())).doOnSuccess((t) -> {
            fixedAutoCompleter.setCoa(t);
        }).subscribe();
        currentAutoCompleter = new COA3AutoCompleter(txtCurrent, accountRepo, null, false, 1);
        currentAutoCompleter.setObserver(this);
        accountRepo.findCOA(hmProperty.get(txtCurrent.getName())).doOnSuccess((t) -> {
            currentAutoCompleter.setCoa(t);
        }).subscribe();
        liaAutoCompleter = new COA3AutoCompleter(txtLiability, accountRepo, null, false, 1);
        liaAutoCompleter.setObserver(this);
        accountRepo.findCOA(hmProperty.get(txtLiability.getName())).doOnSuccess((t) -> {
            liaAutoCompleter.setCoa(t);
        }).subscribe();
        capitalAutoCompleter = new COA3AutoCompleter(txtCapital, accountRepo, null, false, 1);
        capitalAutoCompleter.setObserver(this);
        accountRepo.findCOA(hmProperty.get(txtCapital.getName())).doOnSuccess((t) -> {
            capitalAutoCompleter.setCoa(t);
        }).subscribe();
        incomeAutoCompleter = new COA3AutoCompleter(txtIncome, accountRepo, null, false, 1);
        incomeAutoCompleter.setObserver(this);
        accountRepo.findCOA(hmProperty.get(txtIncome.getName())).doOnSuccess((t) -> {
            incomeAutoCompleter.setCoa(t);
        }).subscribe();
        otherIncomeAutoCompleter = new COA3AutoCompleter(txtOtherIncome, accountRepo, null, false, 1);
        otherIncomeAutoCompleter.setObserver(this);
        accountRepo.findCOA(hmProperty.get(txtOtherIncome.getName())).doOnSuccess((t) -> {
            otherIncomeAutoCompleter.setCoa(t);
        }).subscribe();
        purchaseAutoCompleter = new COA3AutoCompleter(txtPurchase, accountRepo, null, false, 1);
        purchaseAutoCompleter.setObserver(this);
        accountRepo.findCOA(hmProperty.get(txtPurchase.getName())).doOnSuccess((t) -> {
            purchaseAutoCompleter.setCoa(t);
        }).subscribe();
        expenseAutoCompleter = new COA3AutoCompleter(txtExpense, accountRepo, null, false, 1);
        expenseAutoCompleter.setObserver(this);
        accountRepo.findCOA(hmProperty.get(txtExpense.getName())).doOnSuccess((t) -> {
            expenseAutoCompleter.setCoa(t);
        }).subscribe();
        debtorGroupAutoCompleter = new COA3AutoCompleter(txtDebtor, accountRepo, null, false, 2);
        debtorGroupAutoCompleter.setObserver(this);
        accountRepo.findCOA(hmProperty.get(txtDebtor.getName())).doOnSuccess((t) -> {
            debtorGroupAutoCompleter.setCoa(t);
        }).subscribe();
        debtorAccAutoCompleter = new COA3AutoCompleter(txtCus, accountRepo, null, false, 3);
        debtorAccAutoCompleter.setObserver(this);
        accountRepo.findCOA(hmProperty.get(txtCus.getName())).doOnSuccess((t) -> {
            debtorAccAutoCompleter.setCoa(t);
        }).subscribe();
        creditorGroupAutoCompleter = new COA3AutoCompleter(txtCreditor, accountRepo, null, false, 2);
        creditorGroupAutoCompleter.setObserver(this);
        accountRepo.findCOA(hmProperty.get(txtCreditor.getName())).doOnSuccess((t) -> {
            creditorGroupAutoCompleter.setCoa(t);
        }).subscribe();
        creditorAccAutoCompleter = new COA3AutoCompleter(txtSup, accountRepo, null, false, 3);
        creditorAccAutoCompleter.setObserver(this);
        accountRepo.findCOA(hmProperty.get(txtSup.getName())).doOnSuccess((t) -> {
            creditorAccAutoCompleter.setCoa(t);
        }).subscribe();
        conversionAccAutoCompleter = new COA3AutoCompleter(txtConversionAcc, accountRepo, null, false, 3);
        conversionAccAutoCompleter.setObserver(this);
        accountRepo.findCOA(hmProperty.get(txtConversionAcc.getName())).doOnSuccess((t) -> {
            conversionAccAutoCompleter.setCoa(t);
            progress.setIndeterminate(false);
        }).subscribe();
    }

    private void initHashCOA() {
        hmCOA3.put(txtCash, cashAutoCompleter);
        hmCOA3.put(txtInvGroup, inventoryAutoCompleter);
        hmCOA3.put(txtPlAcc, plAutoCompleter);
        hmCOA3.put(txtREAcc, reAutoCompleter);
        hmCOA3.put(txtCashGroup, cashGroupAutoCompleter);
        hmCOA3.put(txtBankGroup, bankGroupAutoCompleter);
        hmCOA3.put(txtFixed, fixedAutoCompleter);
        hmCOA3.put(txtCurrent, currentAutoCompleter);
        hmCOA3.put(txtCapital, capitalAutoCompleter);
        hmCOA3.put(txtLiability, liaAutoCompleter);
        hmCOA3.put(txtIncome, incomeAutoCompleter);
        hmCOA3.put(txtOtherIncome, otherIncomeAutoCompleter);
        hmCOA3.put(txtPurchase, purchaseAutoCompleter);
        hmCOA3.put(txtExpense, expenseAutoCompleter);
        hmCOA3.put(txtCreditor, creditorGroupAutoCompleter);
        hmCOA3.put(txtSup, creditorAccAutoCompleter);
        hmCOA3.put(txtDebtor, debtorGroupAutoCompleter);
        hmCOA3.put(txtCus, debtorAccAutoCompleter);
        hmCOA3.put(txtConversionAcc, conversionAccAutoCompleter);
    }

    private void save(String key, String value) {
        int yn = JOptionPane.showConfirmDialog(this, "Are you sure to change setting?", "Setting", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (yn == JOptionPane.YES_OPTION) {
            switch (properyType) {
                case "Role" ->
                    saveRoleProp(key, value);
                case "Machine" ->
                    saveMacProp(key, value);
                default ->
                    saveSysProp(key, value);
            }
        }
    }

    private void save1(String key, String value) {
        log.info("save1");
        switch (properyType) {
            case "Role" ->
                saveRoleProp(key, value);
            case "Machine" ->
                saveMacProp(key, value);
            default ->
                saveSysProp(key, value);
        }
    }

    private void saveSysProp(String key, String value) {
        SysProperty p = new SysProperty();
        PropertyKey pKey = new PropertyKey();
        pKey.setPropKey(key);
        pKey.setCompCode(Global.compCode);
        p.setKey(pKey);
        p.setPropValue(value);
        p.setUpdatedDate(LocalDateTime.now());
        userRepo.saveSys(p).doOnSuccess((t) -> {
            Global.hmRoleProperty.put(key, value);
            sendMessage(MessageType.SYSTEM_PROPERTY, t.getKey().getPropKey());
        }).subscribe();
    }

    private void saveRoleProp(String key, String value) {
        RoleProperty p = new RoleProperty();
        RolePropertyKey pKey = new RolePropertyKey();
        pKey.setPropKey(key);
        pKey.setRoleCode(roleCode);
        pKey.setCompCode(Global.compCode);
        p.setKey(pKey);
        p.setPropValue(value);
        p.setUpdatedDate(LocalDateTime.now());
        userRepo.saveRoleProperty(p).doOnSuccess((t) -> {
            Global.hmRoleProperty.put(key, value);
            sendMessage(MessageType.ROLE_PROPERTY, t.getKey().getPropKey());
        }).subscribe();
    }

    private void saveMacProp(String key, String value) {
        MachineProperty p = new MachineProperty();
        MachinePropertyKey mKey = new MachinePropertyKey();
        mKey.setMacId(macId);
        mKey.setPropKey(key);
        p.setKey(mKey);
        p.setPropValue(value);
        p.setUpdatedDate(LocalDateTime.now());
        userRepo.saveMacProp(p).doOnSuccess((t) -> {
            Global.hmRoleProperty.put(key, value);
            sendMessage(MessageType.MACHINE_PROERTY, key);
        }).subscribe();

    }

    private void sendMessage(String messageType, String mes) {
        userRepo.sendDownloadMessage(messageType, mes)
                .doOnSuccess((t) -> {
                    log.info(t);
                }).subscribe();
    }

    private List<String> getPrinter() {
        List<String> str = new ArrayList<>();
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printer : printServices) {
            str.add(printer.getName());
        }
        str.add(hmProperty.get("printer.name"));
        return str;
    }

    private void reportNameDialog() {
        if (reportNameDialog == null) {
            reportNameDialog = new ReportNameDialog(Global.parentForm);
            reportNameDialog.setObserver(this);
            reportNameDialog.setLocationRelativeTo(null);
        }
        reportNameDialog.setData(hmProperty);
        reportNameDialog.setVisible(true);
    }

    private void printCountDialog() {
        if (printCountDialog == null) {
            printCountDialog = new PrintCountDialog(Global.parentForm);
            printCountDialog.setObserver(this);
            printCountDialog.setLocationRelativeTo(null);
        }
        printCountDialog.setData(hmProperty);
        printCountDialog.setVisible(true);
    }

    private void validationDialog() {
        if (validationDialog == null) {
            validationDialog = new ValidationDialog(Global.parentForm);
            validationDialog.setObserver(this);
            validationDialog.setLocationRelativeTo(null);
        }
        validationDialog.setData(hmProperty);
        validationDialog.setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtSupplier = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtCustomer = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtLocation = new javax.swing.JTextField();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel19 = new javax.swing.JLabel();
        txtDep = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        txtStock = new javax.swing.JTextField();
        jlablel = new javax.swing.JLabel();
        txtFixed = new javax.swing.JTextField();
        jlablee = new javax.swing.JLabel();
        txtCurrent = new javax.swing.JTextField();
        txtLiability = new javax.swing.JTextField();
        lablel = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        txtCapital = new javax.swing.JTextField();
        txtIncome = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        txtOtherIncome = new javax.swing.JTextField();
        txtPurchase = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        txtExpense = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        chkPrint = new javax.swing.JCheckBox();
        chkSWB = new javax.swing.JCheckBox();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        txtPrinter = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtLogoName = new javax.swing.JTextField();
        chkWeight = new javax.swing.JCheckBox();
        chkDisableSale = new javax.swing.JCheckBox();
        chkDisablePur = new javax.swing.JCheckBox();
        chkDisableRI = new javax.swing.JCheckBox();
        chkDisableRO = new javax.swing.JCheckBox();
        chkDisableStockIO = new javax.swing.JCheckBox();
        chkDepFilter = new javax.swing.JCheckBox();
        chkDepOption = new javax.swing.JCheckBox();
        jLabel21 = new javax.swing.JLabel();
        txtPages = new javax.swing.JTextField();
        chkMulti = new javax.swing.JCheckBox();
        chkAutoUpdate = new javax.swing.JCheckBox();
        chkWeightPoint = new javax.swing.JCheckBox();
        chkDepLock = new javax.swing.JCheckBox();
        chkDisableMill = new javax.swing.JCheckBox();
        chkNoUnit = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        chkSA4 = new javax.swing.JCheckBox();
        chkSA5 = new javax.swing.JCheckBox();
        chkSVou = new javax.swing.JCheckBox();
        chkSLP = new javax.swing.JCheckBox();
        jSeparator1 = new javax.swing.JSeparator();
        chkCalStock = new javax.swing.JCheckBox();
        chkPricePopup = new javax.swing.JCheckBox();
        chkSalePaid = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        txtVouReport = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtA5Report = new javax.swing.JTextField();
        txtA4Report = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        chkSaleEdit = new javax.swing.JCheckBox();
        chkPriceChange = new javax.swing.JCheckBox();
        chkBalance = new javax.swing.JCheckBox();
        chkPriceOption = new javax.swing.JCheckBox();
        jLabel18 = new javax.swing.JLabel();
        txtCreditAmt = new javax.swing.JTextField();
        jSeparator7 = new javax.swing.JSeparator();
        chkSaleExpenseShown = new javax.swing.JCheckBox();
        chkSaleStockInfo = new javax.swing.JCheckBox();
        jPanel3 = new javax.swing.JPanel();
        chkPurVouEdit = new javax.swing.JCheckBox();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel15 = new javax.swing.JLabel();
        txtComP = new javax.swing.JTextField();
        chkShowExpense = new javax.swing.JCheckBox();
        chkShowGRN = new javax.swing.JCheckBox();
        chkPurByBatchDetail = new javax.swing.JCheckBox();
        jLabel16 = new javax.swing.JLabel();
        txtComAmt = new javax.swing.JTextField();
        chkPurGRNReport = new javax.swing.JCheckBox();
        chkShowStockInfo = new javax.swing.JCheckBox();
        panelMac = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        txtMac = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        txtDebtor = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txtCreditor = new javax.swing.JTextField();
        jSeparator5 = new javax.swing.JSeparator();
        txtCus = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtSup = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txtInvGroup = new javax.swing.JTextField();
        jSeparator6 = new javax.swing.JSeparator();
        chkDisableDep = new javax.swing.JCheckBox();
        jLabel23 = new javax.swing.JLabel();
        txtPlAcc = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        txtREAcc = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        txtCashGroup = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        txtCash = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        txtBankGroup = new javax.swing.JTextField();
        chkDisableAll = new javax.swing.JCheckBox();
        jLabel28 = new javax.swing.JLabel();
        txtConversionAcc = new javax.swing.JTextField();
        chkDisableDrVoucher = new javax.swing.JCheckBox();
        chkDisableCrVoucher = new javax.swing.JCheckBox();
        jPanel6 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        txtDivider = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        chkBatchSale = new javax.swing.JCheckBox();
        chkBatchGRN = new javax.swing.JCheckBox();
        jPanel8 = new javax.swing.JPanel();
        chkPaymentEdit = new javax.swing.JCheckBox();
        jPanel10 = new javax.swing.JPanel();
        chkTransferEdit = new javax.swing.JCheckBox();
        chkTransferDelete = new javax.swing.JCheckBox();
        jPanel9 = new javax.swing.JPanel();
        chkUsage = new javax.swing.JCheckBox();
        jPanel11 = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        txtDrCr = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Default", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, Global.menuFont));

        jLabel2.setText("Suppplier");

        txtSupplier.setFont(Global.textFont);

        jLabel3.setText("Customer");

        txtCustomer.setFont(Global.textFont);

        jLabel4.setText("Location");

        txtLocation.setFont(Global.textFont);

        jLabel19.setText("Department");

        txtDep.setFont(Global.textFont);

        jLabel22.setText("Stock");

        txtStock.setFont(Global.textFont);

        jlablel.setText("Fixed");

        txtFixed.setFont(Global.textFont);

        jlablee.setText("Current");

        txtCurrent.setFont(Global.textFont);

        txtLiability.setFont(Global.textFont);

        lablel.setText("Liability");

        jLabel29.setText("Capital");

        txtCapital.setFont(Global.textFont);

        txtIncome.setFont(Global.textFont);

        jLabel30.setText("Income");

        jLabel31.setText("Other Income");

        txtOtherIncome.setFont(Global.textFont);

        txtPurchase.setFont(Global.textFont);

        jLabel32.setText("Purchase");

        jLabel33.setText("Expense \n");

        txtExpense.setFont(Global.textFont);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator3)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCustomer, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)
                            .addComponent(txtSupplier)
                            .addComponent(txtLocation)
                            .addComponent(txtDep)
                            .addComponent(txtStock)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jlablel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jlablee, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lablel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel31, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel33, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtFixed)
                            .addComponent(txtCurrent)
                            .addComponent(txtLiability)
                            .addComponent(txtCapital)
                            .addComponent(txtIncome)
                            .addComponent(txtOtherIncome)
                            .addComponent(txtPurchase)
                            .addComponent(txtExpense))))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtSupplier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(txtStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(txtDep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlablel)
                    .addComponent(txtFixed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlablee)
                    .addComponent(txtCurrent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lablel)
                    .addComponent(txtLiability, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29)
                    .addComponent(txtCapital, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(txtIncome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31)
                    .addComponent(txtOtherIncome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel32)
                    .addComponent(txtPurchase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33)
                    .addComponent(txtExpense, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "System", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, Global.menuFont));

        chkPrint.setText("Auto Print");

        chkSWB.setText("Stock Name With Brand");

        jLabel1.setText("Printer");

        txtPrinter.setFont(Global.textFont);

        jLabel8.setText("Logo Name");

        txtLogoName.setFont(Global.textFont);

        chkWeight.setText("Weight");

        chkDisableSale.setText("Disable Calculate Stock in Sale");

        chkDisablePur.setText("Disable Calculate Stock in Purchase");

        chkDisableRI.setText("Disable Calculate Stock in Return In");

        chkDisableRO.setText("Disable Calculate Stock in Return Out");

        chkDisableStockIO.setText("Disable Pattern in Stock I/O");

        chkDepFilter.setText("Department Filter");

        chkDepOption.setText("Department Option");

        jLabel21.setText("Pages");

        txtPages.setFont(Global.textFont);

        chkMulti.setText("Multi Currency");

        chkAutoUpdate.setText("Auto Update");

        chkWeightPoint.setText("Weight Point");

        chkDepLock.setText("Department Lock");

        chkDisableMill.setText("Disable Calculate Stock in Milling");

        chkNoUnit.setText("No Unit");
        chkNoUnit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkNoUnitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator2)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPrinter)
                            .addComponent(txtLogoName)
                            .addComponent(txtPages)))
                    .addComponent(chkDisableSale, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkDisablePur, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkDisableRI, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkDisableRO, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkDisableStockIO, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkDepOption, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkDisableMill, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(chkSWB)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkNoUnit))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(chkPrint)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(chkAutoUpdate))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(chkWeight)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkWeightPoint))
                            .addComponent(chkMulti)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(chkDepFilter)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkDepLock)))
                        .addGap(0, 3, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkPrint)
                    .addComponent(chkAutoUpdate))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkSWB)
                    .addComponent(chkNoUnit))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkWeight)
                    .addComponent(chkWeightPoint))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkDisableSale)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkDisablePur)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkDisableRI)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkDisableRO)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkDisableMill)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkDisableStockIO)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkDepFilter)
                    .addComponent(chkDepLock))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkDepOption)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkMulti)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtPrinter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(txtPages, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtLogoName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Sale", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, Global.menuFont));

        chkSA4.setText("A4");
        chkSA4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSA4ActionPerformed(evt);
            }
        });

        chkSA5.setText("A5");

        chkSVou.setText("Vou Printer");

        chkSLP.setText("Last Price");

        chkCalStock.setText("Calculate Stock");

        chkPricePopup.setText("Price Popup");

        chkSalePaid.setText("Paid");

        jLabel5.setText("Vou Report");

        txtVouReport.setFont(Global.textFont);

        jLabel6.setText("A5 Report");

        txtA5Report.setFont(Global.textFont);

        txtA4Report.setFont(Global.textFont);

        jLabel7.setText("A4 Report");

        chkSaleEdit.setText(" Edit");

        chkPriceChange.setText("Price Change");

        chkBalance.setText("Trader Balance");

        chkPriceOption.setText("Price Option");

        jLabel18.setText("Credit Amt");

        txtCreditAmt.setFont(Global.textFont);

        chkSaleExpenseShown.setText("Expense Show");
        chkSaleExpenseShown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSaleExpenseShownActionPerformed(evt);
            }
        });

        chkSaleStockInfo.setText("Show Stock Info");
        chkSaleStockInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSaleStockInfoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator7)
                    .addComponent(jSeparator1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtA5Report)
                            .addComponent(txtVouReport)
                            .addComponent(txtA4Report)
                            .addComponent(txtCreditAmt)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(chkPriceOption, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(chkCalStock, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkPriceChange, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE)
                            .addComponent(chkPricePopup, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(chkSA5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(chkSA4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(chkSaleExpenseShown)
                                    .addComponent(chkSaleEdit)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(chkSVou)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkSalePaid))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(chkSLP, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(chkBalance))
                            .addComponent(chkSaleStockInfo))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(chkSA4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chkSA5)
                            .addComponent(chkSaleEdit)))
                    .addComponent(chkSaleExpenseShown))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(chkSaleStockInfo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkSVou)
                    .addComponent(chkSalePaid))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkSLP)
                    .addComponent(chkBalance))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkCalStock)
                    .addComponent(chkPriceChange))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkPriceOption)
                    .addComponent(chkPricePopup))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(txtCreditAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtVouReport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtA5Report, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtA4Report, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Purchase", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, Global.menuFont));

        chkPurVouEdit.setText("Voucher Edit");

        jLabel15.setText("Comm %");

        txtComP.setFont(Global.textFont);

        chkShowExpense.setText("Expense Show");

        chkShowGRN.setText("GRN Show");

        chkPurByBatchDetail.setText("By Batch Detail");

        jLabel16.setText("Comm Amt");

        txtComAmt.setFont(Global.textFont);

        chkPurGRNReport.setText("GRN");
        chkPurGRNReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkPurGRNReportActionPerformed(evt);
            }
        });

        chkShowStockInfo.setText("Show Stock Info");
        chkShowStockInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowStockInfoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(chkShowStockInfo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkPurGRNReport)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jSeparator4)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtComAmt)
                            .addComponent(txtComP)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(chkShowGRN)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkPurByBatchDetail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(chkPurVouEdit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkShowExpense, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkPurVouEdit)
                    .addComponent(chkShowExpense))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkShowGRN)
                    .addComponent(chkPurByBatchDetail))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkShowStockInfo)
                    .addComponent(chkPurGRNReport))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(txtComP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(txtComAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelMac.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel10.setText("Machine");

        javax.swing.GroupLayout panelMacLayout = new javax.swing.GroupLayout(panelMac);
        panelMac.setLayout(panelMacLayout);
        panelMacLayout.setHorizontalGroup(
            panelMacLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMacLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtMac, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelMacLayout.setVerticalGroup(
            panelMacLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMacLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMacLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(panelMacLayout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(4, 4, 4))
                    .addComponent(txtMac, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(10, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Account", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, Global.menuFont));

        jLabel11.setText("Debotor Acc Group");

        txtDebtor.setFont(Global.textFont);

        jLabel12.setText("Creditor Acc Group");

        txtCreditor.setFont(Global.textFont);

        txtCus.setFont(Global.textFont);

        jLabel13.setText("Customer Acc");

        txtSup.setFont(Global.textFont);

        jLabel14.setText("Supplier Acc");

        jLabel17.setText("Inv Group");

        txtInvGroup.setFont(Global.textFont);

        chkDisableDep.setText("Disable Department");

        jLabel23.setText("PL Acc");

        txtPlAcc.setFont(Global.textFont);

        jLabel24.setText("RE Acc");

        txtREAcc.setFont(Global.textFont);

        jLabel25.setText("Cash Group");

        txtCashGroup.setFont(Global.textFont);

        jLabel20.setText("Cash");

        txtCash.setFont(Global.textFont);

        jLabel26.setText("Bank Group");

        txtBankGroup.setFont(Global.textFont);

        chkDisableAll.setText("Disable All Filter");
        chkDisableAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkDisableAllActionPerformed(evt);
            }
        });

        jLabel28.setText("Conversion A/C");

        txtConversionAcc.setFont(Global.textFont);

        chkDisableDrVoucher.setText("Disable Debit Voucher");
        chkDisableDrVoucher.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkDisableDrVoucherActionPerformed(evt);
            }
        });

        chkDisableCrVoucher.setText("Disable Credit Voucher");
        chkDisableCrVoucher.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkDisableCrVoucherActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator6)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator5)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtCreditor)
                                    .addComponent(txtDebtor, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                                    .addComponent(txtCus, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                                    .addComponent(txtSup, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel28, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel26, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel25, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txtInvGroup, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtPlAcc, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtREAcc, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtCashGroup, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtBankGroup, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtConversionAcc)
                                    .addComponent(txtCash)))))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkDisableAll)
                            .addComponent(chkDisableDep))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkDisableDrVoucher, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(chkDisableCrVoucher, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(txtDebtor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(txtCreditor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(txtCus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(txtSup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(txtCashGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(txtBankGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(txtInvGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(txtPlAcc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(txtREAcc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(txtCash, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(txtConversionAcc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkDisableAll)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chkDisableDep)
                            .addComponent(chkDisableCrVoucher)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(chkDisableDrVoucher)))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Report"));

        jLabel27.setText("Divider");

        txtDivider.setFont(Global.textFont);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel27)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDivider)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(txtDivider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Batch"));

        chkBatchSale.setText("Sale");

        chkBatchGRN.setText("GRN");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkBatchSale)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkBatchGRN)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkBatchSale)
                    .addComponent(chkBatchGRN))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("Payment"));

        chkPaymentEdit.setText("Edit");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkPaymentEdit)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(chkPaymentEdit))
        );

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder("Transfer"));

        chkTransferEdit.setText("Edit");

        chkTransferDelete.setText("Delete");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkTransferEdit)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkTransferDelete)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkTransferEdit, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(chkTransferDelete, javax.swing.GroupLayout.Alignment.TRAILING)))
        );

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder("Milling"));

        chkUsage.setText("Stock Usage");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkUsage)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(chkUsage))
        );

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder("Report"));

        jLabel34.setText("Dr/Cr Report");

        txtDrCr.setFont(Global.textFont);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel34)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDrCr)
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34)
                    .addComponent(txtDrCr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton1.setFont(Global.lableFont);
        jButton1.setText("Report");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setFont(Global.lableFont);
        jButton2.setText("Print Count");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setFont(Global.lableFont);
        jButton3.setText("Validation");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelMac, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jButton1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton3)
                                        .addGap(0, 0, Short.MAX_VALUE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButton2)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelMac, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton1)
                            .addComponent(jButton3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void chkSA4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSA4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkSA4ActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        if (observer != null) {
            observer.selected("control", this);
        }
    }//GEN-LAST:event_formComponentShown

    private void chkDisableAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkDisableAllActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkDisableAllActionPerformed

    private void chkSaleExpenseShownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSaleExpenseShownActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkSaleExpenseShownActionPerformed

    private void chkPurGRNReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkPurGRNReportActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkPurGRNReportActionPerformed

    private void chkSaleStockInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSaleStockInfoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkSaleStockInfoActionPerformed

    private void chkShowStockInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShowStockInfoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkShowStockInfoActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        reportNameDialog();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void chkDisableDrVoucherActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkDisableDrVoucherActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkDisableDrVoucherActionPerformed

    private void chkDisableCrVoucherActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkDisableCrVoucherActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkDisableCrVoucherActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        printCountDialog();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void chkNoUnitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkNoUnitActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkNoUnitActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        validationDialog();
    }//GEN-LAST:event_jButton3ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkAutoUpdate;
    private javax.swing.JCheckBox chkBalance;
    private javax.swing.JCheckBox chkBatchGRN;
    private javax.swing.JCheckBox chkBatchSale;
    private javax.swing.JCheckBox chkCalStock;
    private javax.swing.JCheckBox chkDepFilter;
    private javax.swing.JCheckBox chkDepLock;
    private javax.swing.JCheckBox chkDepOption;
    private javax.swing.JCheckBox chkDisableAll;
    private javax.swing.JCheckBox chkDisableCrVoucher;
    private javax.swing.JCheckBox chkDisableDep;
    private javax.swing.JCheckBox chkDisableDrVoucher;
    private javax.swing.JCheckBox chkDisableMill;
    private javax.swing.JCheckBox chkDisablePur;
    private javax.swing.JCheckBox chkDisableRI;
    private javax.swing.JCheckBox chkDisableRO;
    private javax.swing.JCheckBox chkDisableSale;
    private javax.swing.JCheckBox chkDisableStockIO;
    private javax.swing.JCheckBox chkMulti;
    private javax.swing.JCheckBox chkNoUnit;
    private javax.swing.JCheckBox chkPaymentEdit;
    private javax.swing.JCheckBox chkPriceChange;
    private javax.swing.JCheckBox chkPriceOption;
    private javax.swing.JCheckBox chkPricePopup;
    private javax.swing.JCheckBox chkPrint;
    private javax.swing.JCheckBox chkPurByBatchDetail;
    private javax.swing.JCheckBox chkPurGRNReport;
    private javax.swing.JCheckBox chkPurVouEdit;
    private javax.swing.JCheckBox chkSA4;
    private javax.swing.JCheckBox chkSA5;
    private javax.swing.JCheckBox chkSLP;
    private javax.swing.JCheckBox chkSVou;
    private javax.swing.JCheckBox chkSWB;
    private javax.swing.JCheckBox chkSaleEdit;
    private javax.swing.JCheckBox chkSaleExpenseShown;
    private javax.swing.JCheckBox chkSalePaid;
    private javax.swing.JCheckBox chkSaleStockInfo;
    private javax.swing.JCheckBox chkShowExpense;
    private javax.swing.JCheckBox chkShowGRN;
    private javax.swing.JCheckBox chkShowStockInfo;
    private javax.swing.JCheckBox chkTransferDelete;
    private javax.swing.JCheckBox chkTransferEdit;
    private javax.swing.JCheckBox chkUsage;
    private javax.swing.JCheckBox chkWeight;
    private javax.swing.JCheckBox chkWeightPoint;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JLabel jlablee;
    private javax.swing.JLabel jlablel;
    private javax.swing.JLabel lablel;
    private javax.swing.JPanel panelMac;
    private javax.swing.JTextField txtA4Report;
    private javax.swing.JTextField txtA5Report;
    private javax.swing.JTextField txtBankGroup;
    private javax.swing.JTextField txtCapital;
    private javax.swing.JTextField txtCash;
    private javax.swing.JTextField txtCashGroup;
    private javax.swing.JTextField txtComAmt;
    private javax.swing.JTextField txtComP;
    private javax.swing.JTextField txtConversionAcc;
    private javax.swing.JTextField txtCreditAmt;
    private javax.swing.JTextField txtCreditor;
    private javax.swing.JTextField txtCurrent;
    private javax.swing.JTextField txtCus;
    private javax.swing.JTextField txtCustomer;
    private javax.swing.JTextField txtDebtor;
    private javax.swing.JTextField txtDep;
    private javax.swing.JTextField txtDivider;
    private javax.swing.JTextField txtDrCr;
    private javax.swing.JTextField txtExpense;
    private javax.swing.JTextField txtFixed;
    private javax.swing.JTextField txtIncome;
    private javax.swing.JTextField txtInvGroup;
    private javax.swing.JTextField txtLiability;
    private javax.swing.JTextField txtLocation;
    private javax.swing.JTextField txtLogoName;
    private javax.swing.JTextField txtMac;
    private javax.swing.JTextField txtOtherIncome;
    private javax.swing.JTextField txtPages;
    private javax.swing.JTextField txtPlAcc;
    private javax.swing.JTextField txtPrinter;
    private javax.swing.JTextField txtPurchase;
    private javax.swing.JTextField txtREAcc;
    private javax.swing.JTextField txtStock;
    private javax.swing.JTextField txtSup;
    private javax.swing.JTextField txtSupplier;
    private javax.swing.JTextField txtVouReport;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {
        if (source.equals("TEXT")) {
            String key = "printer.name";
            String value = printerAutoCompleter.getText();
            save(key, value);
        } else if (source.equals("save")) {
            SysProperty p = (SysProperty) selectObj;
            save1(p.getKey().getPropKey(), p.getPropValue());
        } else if (source.equals("TRADER")) {
            String type = selectObj.toString();
            String key;
            String value = "-";
            if (type.equals("CUS")) {
                key = txtCustomer.getName();
                Trader t = cusCompleter.getTrader();
                if (t != null) {
                    value = t.getKey().getCode();
                }
            } else {
                key = txtSupplier.getName();
                Trader t = supCompleter.getTrader();
                if (t != null) {
                    value = t.getKey().getCode();
                }
            }
            save(key, value);
        } else if (source.equals("Location")) {
            String key = txtLocation.getName();
            Location loc = locCompleter.getLocation();
            if (loc != null) {
                String value = loc.getKey().getLocCode();
                save(key, value);
            }
        } else if (source.equals("Mac")) {
            macId = macAutoCompleter.getInfo().getMacId();
            initMain();
        } else if (source.equals("Department")) {
            DepartmentA d = departmentAutoCompleter.getDepartment();
            if (d != null) {
                save(txtDep.getName(), d.getKey().getDeptCode());
            }
        } else if (source.equals("COA_TF")) {
            if (selectObj instanceof JTextField txt) {
                COA3AutoCompleter completer = hmCOA3.get(txt);
                if (completer != null) {
                    save(txt.getName(), completer.getCOA().getKey().getCoaCode());
                }
            }
            /*ChartOfAccount cash = cashAutoCompleter.getCOA();
            if (cash != null) {
            save(txtCash.getName(), cash.getKey().getCoaCode());
            }
            ChartOfAccount inv = inventoryAutoCompleter.getCOA();
            if (inv != null) {
            save(txtInvGroup.getName(), inv.getKey().getCoaCode());
            }
            ChartOfAccount pl = plAutoCompleter.getCOA();
            if (pl != null) {
            save(txtPlAcc.getName(), pl.getKey().getCoaCode());
            }
            ChartOfAccount re = reAutoCompleter.getCOA();
            if (re != null) {
            save(txtREAcc.getName(), re.getKey().getCoaCode());
            }
            ChartOfAccount cg = cashGroupAutoCompleter.getCOA();
            if (cg != null) {
            save(txtCashGroup.getName(), cg.getKey().getCoaCode());
            }
            ChartOfAccount bg = bankGroupAutoCompleter.getCOA();
            if (bg != null) {
            save(txtBankGroup.getName(), bg.getKey().getCoaCode());
            }
            
            ChartOfAccount f = fixedAutoCompleter.getCOA();
            if (f != null) {
            save(txtFixed.getName(), f.getKey().getCoaCode());
            }
            ChartOfAccount c = currentAutoCompleter.getCOA();
            if (c != null) {
            save(txtCurrent.getName(), c.getKey().getCoaCode());
            }
            ChartOfAccount ca = capitalAutoCompleter.getCOA();
            if (ca != null) {
            save(txtCapital.getName(), ca.getKey().getCoaCode());
            }
            ChartOfAccount l = liaAutoCompleter.getCOA();
            if (l != null) {
            save(txtLiability.getName(), l.getKey().getCoaCode());
            }
            ChartOfAccount i = incomeAutoCompleter.getCOA();
            if (i != null) {
            save(txtIncome.getName(), i.getKey().getCoaCode());
            }
            
            ChartOfAccount o = otherIncomeAutoCompleter.getCOA();
            if (o != null) {
            save(txtOtherIncome.getName(), o.getKey().getCoaCode());
            }
            
            ChartOfAccount p = purchaseAutoCompleter.getCOA();
            if (p != null) {
            save(txtPurchase.getName(), p.getKey().getCoaCode());
            }
            
            ChartOfAccount g = expenseAutoCompleter.getCOA();
            if (g != null) {
            save(txtExpense.getName(), g.getKey().getCoaCode());
            }
            ChartOfAccount ct = creditorGroupAutoCompleter.getCOA();
            if (ct != null) {
            save(txtCreditor.getName(), ct.getKey().getCoaCode());
            }
            ChartOfAccount cta = creditorAccAutoCompleter.getCOA();
            if (cta != null) {
            save(txtSup.getName(), cta.getKey().getCoaCode());
            }
            ChartOfAccount dbg = debtorGroupAutoCompleter.getCOA();
            if (dbg != null) {
            save(txtDebtor.getName(), dbg.getKey().getCoaCode());
            }
            ChartOfAccount dba = debtorAccAutoCompleter.getCOA();
            if (dba != null) {
            save(txtCus.getName(), dba.getKey().getCoaCode());
            }
            ChartOfAccount cona = conversionAccAutoCompleter.getCOA();
            if (cona != null) {
            save(txtConversionAcc.getName(), cona.getKey().getCoaCode());
            }*/

        } else if (source.equals("STOCK")) {
            Stock s = stockAutoCompleter.getStock();
            if (s != null) {
                save(txtStock.getName(), s.getKey().getStockCode());
            }
        }
    }

    @Override
    public void save() {
    }

    @Override
    public void delete() {
    }

    @Override
    public void newForm() {
    }

    @Override
    public void history() {
    }

    @Override
    public void print() {
    }

    @Override
    public void refresh() {
        initMain();
    }

    @Override
    public void filter() {
    }

    @Override
    public String panelName() {
        return this.getName();
    }
}
