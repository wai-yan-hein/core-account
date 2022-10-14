/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.user.setup;

import com.common.Global;
import com.common.RoleProperty;
import com.common.RolePropertyKey;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.editor.TraderAutoCompleter;
import com.inventory.model.Location;
import com.inventory.model.MachineInfo;
import com.inventory.model.SysProperty;
import com.inventory.model.Trader;
import com.inventory.ui.common.InventoryRepo;
import com.user.common.UserRepo;
import com.user.editor.MacAutoCompleter;
import com.user.editor.TextAutoCompleter;
import com.user.model.MachineProperty;
import com.user.model.MachinePropertyKey;
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

/**
 *
 * @author Lenovo
 */
@Slf4j
public class SystemProperty extends javax.swing.JPanel implements SelectionObserver {

    private UserRepo userRepo;
    private InventoryRepo inventoryRepo;
    private SelectionObserver observer;
    private JProgressBar progress;
    private TextAutoCompleter printerAutoCompleter;
    private TraderAutoCompleter cusCompleter;
    private TraderAutoCompleter supCompleter;
    private LocationAutoCompleter locCompleter;
    private MacAutoCompleter macAutoCompleter;
    private HashMap<String, String> hmProperty;
    private String properyType = "System";
    private String roleCode;
    private Integer macId;

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
        //txt
        txtA4Report.addActionListener(action);
        txtA5Report.addActionListener(action);
        txtVouReport.addActionListener(action);
        txtLogoName.addActionListener(action);
        txtPurReport.addActionListener(action);
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
        chkPrint.setName("printer.print");
        chkPrint.setSelected(Util1.getBoolean(hmProperty.get("printer.print")));
        chkSWB.setName("stock.name.with.brand");
        chkSWB.setSelected(Util1.getBoolean(hmProperty.get("stock.name.with.brand")));
        chkPricePopup.setName("sale.price.option");
        chkPricePopup.setSelected(Util1.getBoolean(hmProperty.get("sale.price.option")));
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
        chkDisableSale.setName("disable.calcuate.sale.stock");
        chkDisableSale.setSelected(Util1.getBoolean(hmProperty.get("disable.calcuate.sale.stock")));
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
    }

    private void initCombo() {
        printerAutoCompleter = new TextAutoCompleter(txtPrinter, getPrinter(), null);
        printerAutoCompleter.setObserver(this);
        printerAutoCompleter.setText(hmProperty.get("printer.name"));
        cusCompleter = new TraderAutoCompleter(txtCustomer, inventoryRepo, null, false, "CUS");
        cusCompleter.setObserver(this);
        cusCompleter.setTrader(inventoryRepo.findTrader(hmProperty.get("default.customer")));
        supCompleter = new TraderAutoCompleter(txtSupplier, inventoryRepo, null, false, "SUP");
        supCompleter.setObserver(this);
        supCompleter.setTrader(inventoryRepo.findTrader(hmProperty.get("default.supplier")));
        locCompleter = new LocationAutoCompleter(txtLocation, inventoryRepo.getLocation(), null, false, false);
        locCompleter.setObserver(this);
        locCompleter.setLocation(inventoryRepo.findLocation(hmProperty.get("default.location")));
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
        p.setPropKey(key);
        p.setPropValue(value);
        p.setCompCode(Global.compCode);
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
        jPanel3 = new javax.swing.JPanel();
        chkPurVouEdit = new javax.swing.JCheckBox();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel9 = new javax.swing.JLabel();
        txtPurReport = new javax.swing.JTextField();
        panelMac = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        txtMac = new javax.swing.JTextField();

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Default", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, Global.menuFont));

        jLabel2.setText("Suppplier");

        txtSupplier.setFont(Global.textFont);

        jLabel3.setText("Customer");

        txtCustomer.setFont(Global.textFont);

        jLabel4.setText("Location");

        txtLocation.setFont(Global.textFont);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator3)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCustomer)
                            .addComponent(txtSupplier)
                            .addComponent(txtLocation, javax.swing.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE))))
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
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        chkDisableSale.setText("Disable Calcuate Stock in Sale");

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
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPrinter)
                            .addComponent(txtLogoName)))
                    .addComponent(chkWeight, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkDisableSale, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtPrinter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtLogoName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(165, Short.MAX_VALUE))
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
                            .addComponent(txtA5Report)
                            .addComponent(txtVouReport)
                            .addComponent(txtA4Report)))
                    .addComponent(chkVouEdit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkPriceChange, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkBalance, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                        .addComponent(txtPurReport, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)))
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelMac, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 364, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void chkSA4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSA4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkSA4ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkBalance;
    private javax.swing.JCheckBox chkCalStock;
    private javax.swing.JCheckBox chkDisableSale;
    private javax.swing.JCheckBox chkPriceChange;
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
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
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
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JPanel panelMac;
    private javax.swing.JTextField txtA4Report;
    private javax.swing.JTextField txtA5Report;
    private javax.swing.JTextField txtCustomer;
    private javax.swing.JTextField txtLocation;
    private javax.swing.JTextField txtLogoName;
    private javax.swing.JTextField txtMac;
    private javax.swing.JTextField txtPrinter;
    private javax.swing.JTextField txtPurReport;
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
        }
    }
}
