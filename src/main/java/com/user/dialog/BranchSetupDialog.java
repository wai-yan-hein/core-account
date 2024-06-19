/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.user.dialog;

import com.acc.editor.COAAutoCompleter;
import com.acc.editor.DepartmentAutoCompleter;
import com.acc.model.ChartOfAccount;
import com.acc.model.DepartmentA;
import com.common.ComponentUtil;
import com.common.Global;
import com.common.ProUtil;
import com.common.TableCellRender;
import com.common.Util1;
import com.inventory.entity.MessageType;
import com.repo.AccountRepo;
import com.user.common.BranchTableModel;
import com.repo.UserRepo;
import com.user.model.DepartmentKey;
import com.user.model.Branch;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.time.LocalDateTime;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class BranchSetupDialog extends javax.swing.JDialog implements KeyListener {

    private int selectRow = -1;
    private Branch department = new Branch();
    private final BranchTableModel departmentTableModel = new BranchTableModel();
    private DepartmentAutoCompleter departmentAutoCompleter;
    private COAAutoCompleter coaAutoCompleter;
    @Setter
    private UserRepo userRepo;
    @Setter
    private AccountRepo accountRepo;

    /**
     * Creates new form CurrencySetup
     *
     * @param frame
     */
    public BranchSetupDialog(JFrame frame) {
        super(frame, true);
        initComponents();
        initKeyListener();
        initFocusAdapter();
    }

    public void initMain() {
        initTable();
        initCompleter();
    }

    private void initCompleter() {
        departmentAutoCompleter = new DepartmentAutoCompleter(txtDep, null, false, false);
        accountRepo.getDepartment().doOnSuccess((t) -> {
            t.add(new DepartmentA());
            departmentAutoCompleter.setListDepartment(t);
        }).subscribe();
        coaAutoCompleter = new COAAutoCompleter(txtCoa, null, false);
        accountRepo.getCOAByGroup(ProUtil.getProperty(ProUtil.CASH_GROUP)).doOnSuccess((t) -> {
            t.add(new ChartOfAccount());
            coaAutoCompleter.setListCOA(t);
        }).subscribe();
    }

    private void initFocusAdapter() {
        ComponentUtil.addFocusListener(this);
    }

    public void searchDepartment() {
        progress.setIndeterminate(true);
        userRepo.getDeparment(false).doOnSuccess((t) -> {
            departmentTableModel.setListDepartment(t);
        }).doOnTerminate(() -> {
            progress.setIndeterminate(false);
            txtCode.requestFocus();
        }).subscribe();
        setVisible(true);
    }

    private void initTable() {
        tblDep.setModel(departmentTableModel);
        tblDep.getTableHeader().setFont(Global.tblHeaderFont);
        tblDep.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblDep.getColumnModel().getColumn(0).setPreferredWidth(20);// Code
        tblDep.getColumnModel().getColumn(1).setPreferredWidth(100);// Name
        tblDep.setDefaultRenderer(Boolean.class, new TableCellRender());
        tblDep.setDefaultRenderer(Object.class, new TableCellRender());
        tblDep.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting()) {
                if (tblDep.getSelectedRow() >= 0) {
                    selectRow = tblDep.convertRowIndexToModel(tblDep.getSelectedRow());
                    Branch c = departmentTableModel.getDepartment(selectRow);
                    setDepartment(c);
                }

            }
        });
        tblDep.setRowHeight(Global.tblRowHeight);
        tblDep.setDefaultRenderer(Object.class, new TableCellRender());
    }

    private void saveDepartment() {
        if (isValidEntry()) {
            progress.setIndeterminate(true);
            btnSave.setEnabled(false);
            userRepo.saveDepartment(department).doOnSuccess((t) -> {
                if (lblStatus.getText().equals("NEW")) {
                    departmentTableModel.addDepartment(t);
                } else {
                    departmentTableModel.setDepartment(selectRow, t);
                }
                if (t.getKey().getDeptId().equals(Global.deptId)) {
                    Global.department = t;
                }
            }).doOnError((e) -> {
                progress.setIndeterminate(false);
                btnSave.setEnabled(true);
                JOptionPane.showMessageDialog(this, e.getMessage());
            }).doOnTerminate(() -> {
                sendMessage(department.getDeptName());
                clear();
            }).subscribe();
        }
    }

    private void sendMessage(String mes) {
        userRepo.sendDownloadMessage(MessageType.DEPARTMENT_USER, mes)
                .doOnSuccess((t) -> {
                    log.info(t);
                }).subscribe();
    }

    private void setDepartment(Branch d) {
        this.department = d;
        setDepartment(d.getDeptCode());
        setCash(d.getCashAcc());
        txtCode.setText(d.getUserCode());
        txtName.setText(d.getDeptName());
        txtPhone.setText(d.getPhoneNo());
        txtAddress.setText(d.getAddress());
        txtEmail.setText(d.getEmail());
        chkActive.setSelected(d.isActive());
        txtTitle.setText(d.getTitle());
        lblStatus.setText("EDIT");
    }

    public void clear() {
        department = new Branch();
        txtCode.setText(null);
        txtName.setText(null);
        txtPhone.setText(null);
        txtEmail.setText(null);
        txtAddress.setText(null);
        txtTitle.setText(null);
        departmentAutoCompleter.setDepartment(null);
        coaAutoCompleter.setCoa(null);
        chkActive.setSelected(Boolean.TRUE);
        lblStatus.setText("NEW");
        progress.setIndeterminate(false);
        btnSave.setEnabled(true);
        txtCode.requestFocus();
    }

    private void initKeyListener() {
        txtCode.addKeyListener(this);
        txtName.addKeyListener(this);
        txtPhone.addKeyListener(this);
        txtEmail.addKeyListener(this);
        chkActive.addKeyListener(this);
        btnSave.addKeyListener(this);
        btnClear.addKeyListener(this);
        tblDep.addKeyListener(this);
    }

    private boolean isValidEntry() {
        String code = txtCode.getText();
        String name = txtName.getText();
        if (Util1.isNullOrEmpty(code)) {
            JOptionPane.showMessageDialog(this, "Invalid department code.");
            return false;
        } else if (Util1.isNullOrEmpty(name)) {
            JOptionPane.showMessageDialog(this, "Invalid department name.");
            return false;
        } else {
            if (lblStatus.getText().equals("NEW")) {
                DepartmentKey key = new DepartmentKey();
                key.setCompCode(Global.compCode);
                department.setKey(key);
            }
            DepartmentA d = departmentAutoCompleter.getDepartment();
            if (d != null) {
                department.setDeptCode(d.getKey() == null ? null : d.getKey().getDeptCode());
            }
            ChartOfAccount coa = coaAutoCompleter.getCOA();
            if (coa != null) {
                department.setCashAcc(coa.getKey() == null ? null : coa.getKey().getCoaCode());
            }
            department.setUserCode(code);
            department.setDeptName(name);
            department.setTitle(txtTitle.getText());
            department.setActive(chkActive.isSelected());
            department.setPhoneNo(txtPhone.getText());
            department.setEmail(txtEmail.getText());
            department.setAddress(txtAddress.getText());
            department.setUpdatedDate(LocalDateTime.now());
        }
        return true;
    }

    private void setCash(String cashAcc) {
        accountRepo.findCOA(cashAcc).doOnSuccess((t) -> {
            coaAutoCompleter.setCoa(t);
        }).subscribe();
    }

    private void setDepartment(String deptCode) {
        accountRepo.findDepartment(deptCode).doOnSuccess((t) -> {
            departmentAutoCompleter.setDepartment(t);
        }).subscribe();
    }

    private void export() {
        Util1.writeJsonFile(departmentTableModel.getListDepartment(), "department.json");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblDep = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtCode = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        txtPhone = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        chkActive = new javax.swing.JCheckBox();
        btnClear = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel5 = new javax.swing.JLabel();
        txtAddress = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        btnSave1 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        txtDep = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtCoa = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtTitle = new javax.swing.JTextField();
        progress = new javax.swing.JProgressBar();

        setTitle("Branch Setup");

        tblDep.setFont(Global.textFont);
        tblDep.setModel(new javax.swing.table.DefaultTableModel(
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
        tblDep.setName("tblDep"); // NOI18N
        tblDep.setRowHeight(Global.tblRowHeight);
        jScrollPane1.setViewportView(tblDep);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel1.setFont(Global.textFont);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Code");

        txtCode.setFont(Global.textFont);
        txtCode.setName("txtCode"); // NOI18N

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Name");

        txtName.setFont(Global.textFont);
        txtName.setName("txtName"); // NOI18N

        txtPhone.setFont(Global.textFont);
        txtPhone.setName("txtPhone"); // NOI18N

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Phone");

        chkActive.setFont(Global.lableFont);
        chkActive.setSelected(true);
        chkActive.setText("Active");
        chkActive.setName("chkActive"); // NOI18N

        btnClear.setFont(Global.lableFont);
        btnClear.setText("Clear");
        btnClear.setName("btnClear"); // NOI18N
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        btnSave.setBackground(Global.selectionColor);
        btnSave.setFont(Global.lableFont);
        btnSave.setForeground(new java.awt.Color(255, 255, 255));
        btnSave.setText("Save");
        btnSave.setName("btnSave"); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        lblStatus.setFont(Global.lableFont);
        lblStatus.setText("NEW");

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Address");

        txtAddress.setFont(Global.textFont);
        txtAddress.setName("txtCurrSymbol"); // NOI18N

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Email");

        txtEmail.setFont(Global.textFont);
        txtEmail.setName("txtCurrSymbol"); // NOI18N

        btnSave1.setBackground(Global.selectionColor);
        btnSave1.setFont(Global.lableFont);
        btnSave1.setForeground(new java.awt.Color(255, 255, 255));
        btnSave1.setText("Export");
        btnSave1.setName("btnSave"); // NOI18N
        btnSave1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSave1ActionPerformed(evt);
            }
        });

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Dep");

        txtDep.setFont(Global.textFont);
        txtDep.setName("txtCurrSymbol"); // NOI18N

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Cash");

        txtCoa.setFont(Global.textFont);
        txtCoa.setName("txtCurrSymbol"); // NOI18N

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Title");

        txtTitle.setFont(Global.textFont);
        txtTitle.setName("txtName"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtPhone)
                                .addComponent(txtTitle))
                            .addComponent(txtCode)
                            .addComponent(txtName)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtAddress)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(chkActive)
                                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtDep, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtCoa, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnSave1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnClear)))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel2, jLabel3, jLabel4, jLabel5, jLabel6});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtDep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtCoa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStatus)
                    .addComponent(chkActive))
                .addGap(8, 8, 8)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnClear)
                    .addComponent(btnSave)
                    .addComponent(btnSave1))
                .addContainerGap(92, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(progress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 363, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(progress, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        // TODO add your handling code here:
        clear();
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        saveDepartment();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnSave1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSave1ActionPerformed
        // TODO add your handling code here:
        export();
    }//GEN-LAST:event_btnSave1ActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSave1;
    private javax.swing.JCheckBox chkActive;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JProgressBar progress;
    private javax.swing.JTable tblDep;
    private javax.swing.JTextField txtAddress;
    private javax.swing.JTextField txtCoa;
    private javax.swing.JTextField txtCode;
    private javax.swing.JTextField txtDep;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtPhone;
    private javax.swing.JTextField txtTitle;
    // End of variables declaration//GEN-END:variables
 @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        Object sourceObj = e.getSource();
        String ctrlName = "-";

        if (sourceObj instanceof JComboBox) {
            ctrlName = ((JComboBox) sourceObj).getName();
        } else if (sourceObj instanceof JCheckBox) {
            ctrlName = ((JCheckBox) sourceObj).getName();
        } else if (sourceObj instanceof JTextField) {
            ctrlName = ((JTextField) sourceObj).getName();
        } else if (sourceObj instanceof JButton) {
            ctrlName = ((JButton) sourceObj).getName();
        } else if (sourceObj instanceof JTable) {
            ctrlName = ((JTable) sourceObj).getName();
        }
        switch (ctrlName) {
            case "txtCurrCode" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtName.requestFocus();
                }
            }
            case "txtCurrName" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtPhone.requestFocus();
                }
            }
            case "txtCurrSymbol" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    chkActive.requestFocus();
                }
            }
            case "chkActive" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnSave.requestFocus();
                }
            }
            case "btnSave" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnClear.requestFocus();
                }
            }
            case "btnClear" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtCode.requestFocus();
                }
            }
            case "tblCurrency" -> {
                if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP) {
                    selectRow = tblDep.convertRowIndexToModel(tblDep.getSelectedRow());
                    Branch dep = departmentTableModel.getDepartment(selectRow);
                    setDepartment(dep);
                }
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtCode.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtCode.requestFocus();
                }
            }

        }
    }
}
