/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.inventory.ui.entry;

import com.repo.AccountRepo;
import com.acc.editor.COA3AutoCompleter;
import com.acc.editor.DepartmentAutoCompleter;
import com.common.Global;
import com.common.StartWithRowFilter;
import com.common.TableCellRender;
import com.inventory.model.AccKey;
import com.inventory.model.AccSetting;
import com.inventory.model.AccType;
import com.repo.InventoryRepo;
import com.inventory.ui.setup.dialog.common.AccountSettingTableModel;
import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Lenovo
 */
public class AccountSettingEntry extends javax.swing.JPanel {
    
    private TableRowSorter<TableModel> sorter;
    private StartWithRowFilter swrf;
    private AccSetting setting = new AccSetting();
    private final AccountSettingTableModel settingTableModel = new AccountSettingTableModel();
    private InventoryRepo inventoryRepo;
    private AccountRepo accountRepo;
    private COA3AutoCompleter sourceCompleter;
    private COA3AutoCompleter cashCompleter;
    private COA3AutoCompleter disCompleter;
    private COA3AutoCompleter taxCompleter;
    private COA3AutoCompleter balCompleter;
    private DepartmentAutoCompleter deptCompleter;
    
    public AccountRepo getAccountRepo() {
        return accountRepo;
    }
    
    public void setAccountRepo(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }
    
    public InventoryRepo getInventoryRepo() {
        return inventoryRepo;
    }
    
    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    /**
     * Creates new form AccountSetting
     */
    public AccountSettingEntry() {
        initComponents();
        initFoucsAdapter();
    }
    
    public void initMain() {
        initAccTable();
        initComobo();
    }
    
    private void initFoucsAdapter() {
        txtSrc.addFocusListener(fa);
        txtCash.addFocusListener(fa);
        txtDiscount.addFocusListener(fa);
        txtTax.addFocusListener(fa);
        txtBal.addFocusListener(fa);
        txtDep.addFocusListener(fa);
    }
    private final FocusAdapter fa = new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            Object s = e.getSource();
            if (s instanceof JTextField txt) {
                txt.selectAll();
            }
        }
    };
    
    private void initComobo() {
        sourceCompleter = new COA3AutoCompleter(txtSrc, accountRepo, null, false, 3);
        cashCompleter = new COA3AutoCompleter(txtCash, accountRepo, null, false, 3);
        disCompleter = new COA3AutoCompleter(txtDiscount, accountRepo, null, false, 3);
        taxCompleter = new COA3AutoCompleter(txtTax, accountRepo, null, false, 3);
        balCompleter = new COA3AutoCompleter(txtBal, accountRepo, null, false, 3);
        accountRepo.getDepartment().subscribe((t) -> {
            deptCompleter = new DepartmentAutoCompleter(txtDep, t, null, false, true);
        }, (e) -> {
            JOptionPane.showMessageDialog(this, e.getMessage());
        });
    }
    
    private void initAccTable() {
        swrf = new StartWithRowFilter(txtFilter);
        tblSetting.setModel(settingTableModel);
        sorter = new TableRowSorter<>(tblSetting.getModel());
        tblSetting.setRowSorter(sorter);
        tblSetting.getTableHeader().setFont(Global.lableFont);
        tblSetting.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblSetting.setDefaultRenderer(Object.class, new TableCellRender());
        tblSetting.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting()) {
                if (tblSetting.getSelectedRow() >= 0) {
                    int row = tblSetting.convertRowIndexToModel(tblSetting.getSelectedRow());
                    setAccSetting(settingTableModel.getSetting(row));
                }
            }
        });
        tblSetting.setRowHeight(Global.tblRowHeight);
        tblSetting.setDefaultRenderer(Object.class, new TableCellRender());
    }
    
    public void searchAccSetting() {
        inventoryRepo.getAccSetting().subscribe((t) -> {
            settingTableModel.setListSetting(t);
        });
    }
    
    private void setAccSetting(AccSetting cat) {
        setting = cat;
        txtSrc.requestFocus();
        lblStatus.setText("EDIT");
        lblStatus.setForeground(Color.blue);
        cboType.setEnabled(false);
        cboType.setSelectedIndex(AccType.valueOf(setting.getKey().getType()).ordinal());
        txtCash.setText(setting.getPayAcc());
        txtDiscount.setText(setting.getDiscountAcc());
        txtTax.setText(setting.getTaxAcc());
        txtBal.setText(setting.getBalanceAcc());
        txtDep.setText(setting.getDeptCode());
        accountRepo.findCOA(cat.getSourceAcc()).subscribe((t) -> {
            sourceCompleter.setCoa(t);
        }, (e) -> {
            JOptionPane.showMessageDialog(this, e.getMessage());
        });
        accountRepo.findCOA(cat.getPayAcc()).subscribe((t) -> {
            cashCompleter.setCoa(t);
        }, (e) -> {
            JOptionPane.showMessageDialog(this, e.getMessage());
        });
        accountRepo.findCOA(cat.getDiscountAcc()).subscribe((t) -> {
            disCompleter.setCoa(t);
        }, (e) -> {
            JOptionPane.showMessageDialog(this, e.getMessage());
        });
        accountRepo.findCOA(cat.getTaxAcc()).subscribe((t) -> {
            taxCompleter.setCoa(t);
        }, (e) -> {
            JOptionPane.showMessageDialog(this, e.getMessage());
        });
        accountRepo.findCOA(cat.getBalanceAcc()).subscribe((t) -> {
            balCompleter.setCoa(t);
        }, (e) -> {
            JOptionPane.showMessageDialog(this, e.getMessage());
        });
        accountRepo.findDepartment(cat.getDeptCode()).subscribe((t) -> {
            deptCompleter.setDepartment(t);
        }, (e) -> {
            JOptionPane.showMessageDialog(this, e.getMessage());
        });
    }
    
    private void saveSetting() {
        if (isValidEntry()) {
            inventoryRepo.save(setting).subscribe((t) -> {
                if (lblStatus.getText().equals("EDIT")) {
                    int row = tblSetting.convertRowIndexToModel(tblSetting.getSelectedRow());
                    settingTableModel.setSetting(t, row);
                } else {
                    settingTableModel.addSetting(t);
                }
                clearSetting();
            });
        }
    }
    
    private void clearSetting() {
//        txtType.setText(null);
//        txtType.setEnabled(true);
        cboType.setEnabled(true);
        txtFilter.setText(null);
        sourceCompleter.setCoa(null);
        cashCompleter.setCoa(null);
        disCompleter.setCoa(null);
        taxCompleter.setCoa(null);
        balCompleter.setCoa(null);
        deptCompleter.setDepartment(null);
        lblStatus.setText("NEW");
        lblStatus.setForeground(Color.green);
        setting = new AccSetting();
        tblSetting.requestFocus();
//        txtType.requestFocus();
    }
    
    private boolean isValidEntry() {
        boolean status = true;
        if (txtSrc.getText().isEmpty()) {
            status = false;
            JOptionPane.showMessageDialog(this, "Invalid Name");
            txtSrc.requestFocus();
        }
        if (txtCash.getText().isEmpty()) {
            status = false;
            JOptionPane.showMessageDialog(this, "Invalid Cash");
            txtCash.requestFocus();
        }
        if (txtDiscount.getText().isEmpty()) {
            status = false;
            JOptionPane.showMessageDialog(this, "Invalid Discount");
            txtDiscount.requestFocus();
        }
        if (txtTax.getText().isEmpty()) {
            status = false;
            JOptionPane.showMessageDialog(this, "Invalid Tax");
            txtTax.requestFocus();
        }
        if (txtBal.getText().isEmpty()) {
            status = false;
            JOptionPane.showMessageDialog(this, "Invalid Balance");
            txtBal.requestFocus();
        }
        if (txtDep.getText().isEmpty()) {
            status = false;
            JOptionPane.showMessageDialog(this, "Invalid Department");
            txtDep.requestFocus();
        } else {
            AccKey key = new AccKey();
            AccType s = (AccType) cboType.getSelectedItem();
            key.setType(s.name());
            key.setCompCode(Global.compCode);
            setting.setKey(key);
            setting.setSourceAcc(sourceCompleter.getCOA().getKey().getCoaCode());
            setting.setPayAcc(cashCompleter.getCOA().getKey().getCoaCode());
            setting.setDiscountAcc(disCompleter.getCOA().getKey().getCoaCode());
            setting.setTaxAcc(taxCompleter.getCOA().getKey().getCoaCode());
            setting.setBalanceAcc(balCompleter.getCOA().getKey().getCoaCode());
            setting.setDeptCode(deptCompleter.getDepartment().getKey().getDeptCode());
        }
        return status;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSetting = new javax.swing.JTable();
        txtFilter = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtSrc = new javax.swing.JTextField();
        lblStatus = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel4 = new javax.swing.JLabel();
        txtCash = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtDiscount = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtTax = new javax.swing.JTextField();
        txtBal = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        btnSave = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        txtDep = new javax.swing.JTextField();
        cboType = new javax.swing.JComboBox<>(AccType.values());

        tblSetting.setFont(Global.textFont);
        tblSetting.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblSetting.setName("tblSetting"); // NOI18N
        jScrollPane1.setViewportView(tblSetting);

        txtFilter.setName("txtFilter"); // NOI18N
        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Source");

        txtSrc.setFont(Global.textFont);
        txtSrc.setName("txtSrc"); // NOI18N
        txtSrc.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSrcFocusGained(evt);
            }
        });
        txtSrc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSrcActionPerformed(evt);
            }
        });

        lblStatus.setFont(Global.lableFont);
        lblStatus.setText("NEW");

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Type");

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Cash");

        txtCash.setFont(Global.textFont);
        txtCash.setName("txtName"); // NOI18N
        txtCash.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCashFocusGained(evt);
            }
        });
        txtCash.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCashActionPerformed(evt);
            }
        });

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Discount");

        txtDiscount.setFont(Global.textFont);
        txtDiscount.setName("txtName"); // NOI18N
        txtDiscount.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDiscountFocusGained(evt);
            }
        });
        txtDiscount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDiscountActionPerformed(evt);
            }
        });

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Tax");

        txtTax.setFont(Global.textFont);
        txtTax.setName("txtName"); // NOI18N
        txtTax.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtTaxFocusGained(evt);
            }
        });
        txtTax.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTaxActionPerformed(evt);
            }
        });

        txtBal.setFont(Global.textFont);
        txtBal.setName("txtName"); // NOI18N
        txtBal.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtBalFocusGained(evt);
            }
        });
        txtBal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBalActionPerformed(evt);
            }
        });

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Balance");

        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnClear.setText("Clear");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Department");

        txtDep.setFont(Global.textFont);
        txtDep.setName("txtName"); // NOI18N
        txtDep.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDepFocusGained(evt);
            }
        });
        txtDep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDepActionPerformed(evt);
            }
        });

        cboType.setFont(Global.textFont);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator1)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtSrc)
                            .addComponent(txtCash)
                            .addComponent(txtDiscount)
                            .addComponent(txtTax)
                            .addComponent(txtBal)
                            .addComponent(txtDep)
                            .addComponent(cboType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 46, Short.MAX_VALUE)
                        .addGap(111, 111, 111)
                        .addComponent(btnClear)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSave)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(cboType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtSrc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtCash, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtTax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtBal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtDep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStatus)
                    .addComponent(btnSave)
                    .addComponent(btnClear))
                .addContainerGap(133, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(txtFilter, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 558, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 381, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyReleased
        // TODO add your handling code here:
        if (txtFilter.getText().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(swrf);
        }
    }//GEN-LAST:event_txtFilterKeyReleased

    private void txtSrcFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSrcFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSrcFocusGained

    private void txtSrcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSrcActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSrcActionPerformed

    private void txtCashFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCashFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCashFocusGained

    private void txtCashActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCashActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCashActionPerformed

    private void txtDiscountFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDiscountFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDiscountFocusGained

    private void txtDiscountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDiscountActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDiscountActionPerformed

    private void txtTaxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTaxFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTaxFocusGained

    private void txtTaxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTaxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTaxActionPerformed

    private void txtBalFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBalFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBalFocusGained

    private void txtBalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBalActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        saveSetting();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void txtDepFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDepFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDepFocusGained

    private void txtDepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDepActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDepActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        clearSetting();
    }//GEN-LAST:event_btnClearActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox<AccType> cboType;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblSetting;
    private javax.swing.JTextField txtBal;
    private javax.swing.JTextField txtCash;
    private javax.swing.JTextField txtDep;
    private javax.swing.JTextField txtDiscount;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JTextField txtSrc;
    private javax.swing.JTextField txtTax;
    // End of variables declaration//GEN-END:variables
}
