/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.user.setup;

import com.acc.common.AccountRepo;
import com.acc.editor.COA3AutoCompleter;
import com.acc.editor.DepartmentAutoCompleter;
import com.acc.model.ChartOfAccount;
import com.acc.model.Department;
import com.common.Global;
import com.common.PanelControl;
import com.common.ProUtil;
import com.common.RoleProperty;
import com.common.RolePropertyKey;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.editor.StockAutoCompleter;
import com.inventory.editor.TraderAutoCompleter;
import com.inventory.model.Location;
import com.inventory.model.MachineInfo;
import com.inventory.model.Stock;
import com.user.model.SysProperty;
import com.inventory.model.Trader;
import com.inventory.ui.common.InventoryRepo;
import com.user.common.UserRepo;
import com.user.editor.MacAutoCompleter;
import com.user.editor.TextAutoCompleter;
import com.user.model.MachineProperty;
import com.user.model.MachinePropertyKey;
import com.user.model.PropertyKey;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.JCheckBox;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

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

    private MacAutoCompleter macAutoCompleter;
    private HashMap<String, String> hmProperty;
    private String properyType = "System";
    private String roleCode;
    private Integer macId;
    private WebClient accountApi;

    public AccountRepo getAccountRepo() {
        return accountRepo;
    }

    public void setAccountRepo(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }

    public UserRepo getUserRepo() {
        return userRepo;
    }

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public InventoryRepo getInventoryRepo() {
        return inventoryRepo;
    }

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public Integer getMacId() {
        return macId;
    }

    public void setMacId(Integer macId) {
        this.macId = macId;
    }

    public String getProperyType() {
        return properyType;
    }

    public void setProperyType(String properyType) {
        this.properyType = properyType;
    }

    public SelectionObserver getObserver() {
        return observer;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public JProgressBar getProgress() {
        return progress;
    }

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }

    public WebClient getAccountApi() {
        return accountApi;
    }

    public void setAccountApi(WebClient accountApi) {
        this.accountApi = accountApi;
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
        txtDep.setName("default.department");
        chkDepFilter.setName("department.filter");
        chkDepOption.setName("department.option");
        chkPriceOption.setName("sale.price.option");
        txtStock.setName("default.stock");
        txtCash.setName("default.cash");
        txtPages.setName("printer.pages");
        txtInvGroup.setName("inventory.group");
        txtCashGroup.setName("cash.group");
        txtComP.setName("purchase.commission");
        chkPrint.setName("printer.print");
        chkDisableDep.setName("disable.department");

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
    }

    private void initProperty() {
        switch (properyType) {
            case "System" ->
                hmProperty = userRepo.getSystProperty();
            case "Role" ->
                hmProperty = userRepo.getRoleProperty(roleCode);
            case "Machine" ->
                hmProperty = userRepo.getMachineProperty(macId);
        }
    }

    private void initMac() {
        if (properyType.equals("Machine")) {
            panelMac.setVisible(true);
            List<MachineInfo> listMac = userRepo.getMacList();
            macAutoCompleter = new MacAutoCompleter(txtMac, listMac, null, false);
            macAutoCompleter.setObserver(this);
        } else {
            panelMac.setVisible(false);
        }
    }

    public void initMain() {
        initMac();
        initProperty();
        initTextBox();
        initCheckBox();
        initCombo();
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
        chkVouEdit.addActionListener(action);
        chkPurVouEdit.addActionListener(action);
        chkBalance.addActionListener(action);
        chkWeight.addActionListener(action);
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
        //txt
        txtA4Report.addActionListener(action);
        txtA5Report.addActionListener(action);
        txtVouReport.addActionListener(action);
        txtLogoName.addActionListener(action);
        txtPurReport.addActionListener(action);
        txtDebtor.addActionListener(action);
        txtCreditor.addActionListener(action);
        txtCus.addActionListener(action);
        txtSup.addActionListener(action);
        txtCash.addActionListener(action);
        txtPages.addActionListener(action);
        txtComP.addActionListener(action);
    }

    private void initCheckBox() {
        chkSA4.setName("check.sale.A4");
        chkSA4.setSelected(Util1.getBoolean(hmProperty.get("check.sale.A4")));
        chkSA5.setName("check.sale.A5");
        chkSA5.setSelected(Util1.getBoolean(hmProperty.get("check.sale.A5")));
        chkSVou.setName("check.sale.voucher");
        chkSVou.setSelected(Util1.getBoolean(hmProperty.get("check.sale.voucher")));
        chkSLP.setName("sale.last.price");
        chkSLP.setSelected(Util1.getBoolean(hmProperty.get("sale.last.price")));
        chkPrint.setSelected(Util1.getBoolean(hmProperty.get(chkPrint.getName())));
        chkSWB.setName("stock.name.with.brand");
        chkSWB.setSelected(Util1.getBoolean(hmProperty.get("stock.name.with.brand")));
        chkPricePopup.setName("sale.price.popup");
        chkPricePopup.setSelected(Util1.getBoolean(hmProperty.get("sale.price.popup")));
        chkCalStock.setName("calculate.stock");
        chkCalStock.setSelected(Util1.getBoolean(hmProperty.get("calculate.stock")));
        chkSalePaid.setName("default.sale.paid");
        chkSalePaid.setSelected(Util1.getBoolean(hmProperty.get("default.sale.paid")));
        chkVouEdit.setName("sale.voucher.edit");
        chkVouEdit.setSelected(Util1.getBoolean(hmProperty.get("sale.voucher.edit")));
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
    }

    private void initTextBox() {
        txtVouReport.setName("report.sale.voucher");
        txtVouReport.setText(hmProperty.get("report.sale.voucher"));
        txtA5Report.setName("report.sale.A5");
        txtA5Report.setText(hmProperty.get("report.sale.A5"));
        txtA4Report.setName("report.sale.A4");
        txtA4Report.setText(hmProperty.get("report.sale.A4"));
        txtLogoName.setName("logo.name");
        txtLogoName.setText(hmProperty.get("logo.name"));
        txtPurReport.setName("report.purchase.voucher");
        txtPurReport.setText(hmProperty.get("report.purchase.voucher"));
        txtPages.setText(hmProperty.get(txtPages.getName()));
        txtComP.setText(hmProperty.get(txtComP.getName()));
    }

    private void initCombo() {
        printerAutoCompleter = new TextAutoCompleter(txtPrinter, getPrinter(), null);
        printerAutoCompleter.setObserver(this);
        printerAutoCompleter.setText(hmProperty.get("printer.name"));
        cusCompleter = new TraderAutoCompleter(txtCustomer, inventoryRepo, null, false, "CUS");
        cusCompleter.setObserver(this);
        cusCompleter.setTrader(inventoryRepo.getDefaultCustomer());
        supCompleter = new TraderAutoCompleter(txtSupplier, inventoryRepo, null, false, "SUP");
        supCompleter.setObserver(this);
        supCompleter.setTrader(inventoryRepo.getDefaultSupplier());
        locCompleter = new LocationAutoCompleter(txtLocation, inventoryRepo.getLocation(), null, false, false);
        locCompleter.setObserver(this);
        locCompleter.setLocation(inventoryRepo.getDefaultLocation());
        stockAutoCompleter = new StockAutoCompleter(txtStock, inventoryRepo, null, false);
        stockAutoCompleter.setObserver(this);
        stockAutoCompleter.setStock(inventoryRepo.getDefaultStock());
        departmentAutoCompleter = new DepartmentAutoCompleter(txtDep, accountRepo.getDepartment(), null, false, false);
        departmentAutoCompleter.setObserver(this);
        departmentAutoCompleter.setDepartment(accountRepo.getDefaultDepartment());
        cashAutoCompleter = new COA3AutoCompleter(txtCash, accountApi, null, false, 3);
        cashAutoCompleter.setSelectionObserver(this);
        cashAutoCompleter.setCoa(accountRepo.getDefaultCash());
        plAutoCompleter = new COA3AutoCompleter(txtPlAcc, accountApi, null, false, 3);
        plAutoCompleter.setSelectionObserver(this);
        plAutoCompleter.setCoa(accountRepo.findCOA(hmProperty.get(txtPlAcc.getName())));
        reAutoCompleter = new COA3AutoCompleter(txtREAcc, accountApi, null, false, 3);
        reAutoCompleter.setSelectionObserver(this);
        reAutoCompleter.setCoa(accountRepo.findCOA(hmProperty.get(txtREAcc.getName())));
        inventoryAutoCompleter = new COA3AutoCompleter(txtInvGroup, accountApi, null, false, 2);
        inventoryAutoCompleter.setSelectionObserver(this);
        inventoryAutoCompleter.setCoa(accountRepo.findCOA(hmProperty.get(txtInvGroup.getName())));
        cashGroupAutoCompleter = new COA3AutoCompleter(txtCashGroup, accountApi, null, false, 2);
        cashGroupAutoCompleter.setSelectionObserver(this);
        cashGroupAutoCompleter.setCoa(accountRepo.findCOA(hmProperty.get(txtCashGroup.getName())));

        fixedAutoCompleter = new COA3AutoCompleter(txtFixed, accountApi, null, false, 1);
        fixedAutoCompleter.setSelectionObserver(this);
        fixedAutoCompleter.setCoa(accountRepo.findCOA(hmProperty.get(txtFixed.getName())));

        currentAutoCompleter = new COA3AutoCompleter(txtCurrent, accountApi, null, false, 1);
        currentAutoCompleter.setSelectionObserver(this);
        currentAutoCompleter.setCoa(accountRepo.findCOA(hmProperty.get(txtCurrent.getName())));

        liaAutoCompleter = new COA3AutoCompleter(txtLiability, accountApi, null, false, 1);
        liaAutoCompleter.setSelectionObserver(this);
        liaAutoCompleter.setCoa(accountRepo.findCOA(hmProperty.get(txtLiability.getName())));

        capitalAutoCompleter = new COA3AutoCompleter(txtCapital, accountApi, null, false, 1);
        capitalAutoCompleter.setSelectionObserver(this);
        capitalAutoCompleter.setCoa(accountRepo.findCOA(hmProperty.get(txtCapital.getName())));

        incomeAutoCompleter = new COA3AutoCompleter(txtIncome, accountApi, null, false, 1);
        incomeAutoCompleter.setSelectionObserver(this);
        incomeAutoCompleter.setCoa(accountRepo.findCOA(hmProperty.get(txtIncome.getName())));

        otherIncomeAutoCompleter = new COA3AutoCompleter(txtOtherIncome, accountApi, null, false, 1);
        otherIncomeAutoCompleter.setSelectionObserver(this);
        otherIncomeAutoCompleter.setCoa(accountRepo.findCOA(hmProperty.get(txtOtherIncome.getName())));

        purchaseAutoCompleter = new COA3AutoCompleter(txtPurchase, accountApi, null, false, 1);
        purchaseAutoCompleter.setSelectionObserver(this);
        purchaseAutoCompleter.setCoa(accountRepo.findCOA(hmProperty.get(txtPurchase.getName())));

        expenseAutoCompleter = new COA3AutoCompleter(txtExpense, accountApi, null, false, 1);
        expenseAutoCompleter.setSelectionObserver(this);
        expenseAutoCompleter.setCoa(accountRepo.findCOA(hmProperty.get(txtExpense.getName())));

        debtorGroupAutoCompleter = new COA3AutoCompleter(txtDebtor, accountApi, null, false, 2);
        debtorGroupAutoCompleter.setSelectionObserver(this);
        debtorGroupAutoCompleter.setCoa(accountRepo.findCOA(hmProperty.get(txtDebtor.getName())));

        debtorAccAutoCompleter = new COA3AutoCompleter(txtCus, accountApi, null, false, 3);
        debtorAccAutoCompleter.setSelectionObserver(this);
        debtorAccAutoCompleter.setCoa(accountRepo.findCOA(hmProperty.get(txtCus.getName())));

        creditorGroupAutoCompleter = new COA3AutoCompleter(txtCreditor, accountApi, null, false, 2);
        creditorGroupAutoCompleter.setSelectionObserver(this);
        creditorGroupAutoCompleter.setCoa(accountRepo.findCOA(hmProperty.get(txtCreditor.getName())));

        creditorAccAutoCompleter = new COA3AutoCompleter(txtSup, accountApi, null, false, 3);
        creditorAccAutoCompleter.setSelectionObserver(this);
        creditorAccAutoCompleter.setCoa(accountRepo.findCOA(hmProperty.get(txtSup.getName())));
    }

    private void save(String key, String value) {
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
        userRepo.saveSys(p);
        Global.hmRoleProperty.put(key, value);
        log.info("save.");
    }

    private void saveRoleProp(String key, String value) {
        RoleProperty p = new RoleProperty();
        RolePropertyKey pKey = new RolePropertyKey();
        pKey.setPropKey(key);
        pKey.setRoleCode(roleCode);
        p.setKey(pKey);
        p.setPropValue(value);
        p.setCompCode(Global.compCode);
        userRepo.saveRoleProperty(p);
        Global.hmRoleProperty.put(key, value);
    }

    private void saveMacProp(String key, String value) {
        MachineProperty p = new MachineProperty();
        MachinePropertyKey mKey = new MachinePropertyKey();
        mKey.setMacId(macId);
        mKey.setPropKey(key);
        p.setKey(mKey);
        p.setPropValue(value);
        userRepo.saveMacProp(p);
        Global.hmRoleProperty.put(key, value);
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
        chkVouEdit = new javax.swing.JCheckBox();
        chkPriceChange = new javax.swing.JCheckBox();
        chkBalance = new javax.swing.JCheckBox();
        chkPriceOption = new javax.swing.JCheckBox();
        jPanel3 = new javax.swing.JPanel();
        chkPurVouEdit = new javax.swing.JCheckBox();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel9 = new javax.swing.JLabel();
        txtPurReport = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txtComP = new javax.swing.JTextField();
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
                            .addComponent(txtCustomer, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
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

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkPrint, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkSWB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                    .addComponent(chkWeight, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkDisableSale, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkDisablePur, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkDisableRI, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkDisableRO, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
                    .addComponent(chkDisableStockIO, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkDepFilter, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkDepOption, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkPrint)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkSWB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkWeight)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkDisableSale)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkDisablePur)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkDisableRI)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkDisableRO)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkDisableStockIO)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkDepFilter)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkDepOption)
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

        chkVouEdit.setText("Voucher Edit");

        chkPriceChange.setText("Price Change");

        chkBalance.setText("Trader Balance");

        chkPriceOption.setText("Price Option");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkSA4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkSA5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkSVou, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkSLP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator1)
                    .addComponent(chkCalStock, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkPricePopup, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkSalePaid, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtA5Report, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                            .addComponent(txtVouReport)
                            .addComponent(txtA4Report)))
                    .addComponent(chkVouEdit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkPriceChange, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkBalance, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkPriceOption, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(chkSA4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkSA5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkSVou)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkSLP)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkCalStock)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkPriceOption)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkPricePopup)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkPriceChange)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkSalePaid)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkVouEdit)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkBalance)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        jLabel9.setText("Report");

        txtPurReport.setFont(Global.textFont);

        jLabel15.setText("Comm %");

        txtComP.setFont(Global.textFont);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkPurVouEdit, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator4)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtPurReport, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtComP, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkPurVouEdit)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtPurReport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(txtComP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                                    .addComponent(txtCreditor, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                                    .addComponent(txtDebtor)
                                    .addComponent(txtCus)
                                    .addComponent(txtSup)))
                            .addComponent(chkDisableDep, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                                    .addComponent(jLabel25, javax.swing.GroupLayout.Alignment.LEADING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtInvGroup)
                                    .addComponent(txtPlAcc)
                                    .addComponent(txtREAcc)
                                    .addComponent(txtCashGroup)
                                    .addComponent(txtCash))))))
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
                .addGap(261, 261, 261)
                .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkDisableDep)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelMac, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelMac, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void chkSA4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSA4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkSA4ActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observer.selected("control", this);
    }//GEN-LAST:event_formComponentShown


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkBalance;
    private javax.swing.JCheckBox chkCalStock;
    private javax.swing.JCheckBox chkDepFilter;
    private javax.swing.JCheckBox chkDepOption;
    private javax.swing.JCheckBox chkDisableDep;
    private javax.swing.JCheckBox chkDisablePur;
    private javax.swing.JCheckBox chkDisableRI;
    private javax.swing.JCheckBox chkDisableRO;
    private javax.swing.JCheckBox chkDisableSale;
    private javax.swing.JCheckBox chkDisableStockIO;
    private javax.swing.JCheckBox chkPriceChange;
    private javax.swing.JCheckBox chkPriceOption;
    private javax.swing.JCheckBox chkPricePopup;
    private javax.swing.JCheckBox chkPrint;
    private javax.swing.JCheckBox chkPurVouEdit;
    private javax.swing.JCheckBox chkSA4;
    private javax.swing.JCheckBox chkSA5;
    private javax.swing.JCheckBox chkSLP;
    private javax.swing.JCheckBox chkSVou;
    private javax.swing.JCheckBox chkSWB;
    private javax.swing.JCheckBox chkSalePaid;
    private javax.swing.JCheckBox chkVouEdit;
    private javax.swing.JCheckBox chkWeight;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JLabel jlablee;
    private javax.swing.JLabel jlablel;
    private javax.swing.JLabel lablel;
    private javax.swing.JPanel panelMac;
    private javax.swing.JTextField txtA4Report;
    private javax.swing.JTextField txtA5Report;
    private javax.swing.JTextField txtCapital;
    private javax.swing.JTextField txtCash;
    private javax.swing.JTextField txtCashGroup;
    private javax.swing.JTextField txtComP;
    private javax.swing.JTextField txtCreditor;
    private javax.swing.JTextField txtCurrent;
    private javax.swing.JTextField txtCus;
    private javax.swing.JTextField txtCustomer;
    private javax.swing.JTextField txtDebtor;
    private javax.swing.JTextField txtDep;
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
    private javax.swing.JTextField txtPurReport;
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
        } else if (source.equals("CUS") || source.equals("SUP")) {
            String key;
            String value = "-";
            if (source.equals("CUS")) {
                key = "default.customer";
                Trader t = cusCompleter.getTrader();
                if (t != null) {
                    value = t.getKey().getCode();
                }
            } else {
                key = "default.supplier";
                Trader t = supCompleter.getTrader();
                if (t != null) {
                    value = t.getKey().getCode();
                }
            }
            save(key, value);
        } else if (source.equals("Location")) {
            String key = "default.location";
            Location loc = locCompleter.getLocation();
            if (loc != null) {
                String value = loc.getKey().getLocCode();
                save(key, value);
            }
        } else if (source.equals("Mac")) {
            macId = macAutoCompleter.getInfo().getMacId();
            initMain();
        } else if (source.equals("Department")) {
            Department d = departmentAutoCompleter.getDepartment();
            if (d != null) {
                save(txtDep.getName(), d.getKey().getDeptCode());
            }
        } else if (source.equals("COA")) {
            ChartOfAccount cash = cashAutoCompleter.getCOA();
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
