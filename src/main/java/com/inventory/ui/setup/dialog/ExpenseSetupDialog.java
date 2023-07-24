/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup.dialog;

import com.repo.AccountRepo;
import com.acc.common.COAComboBoxModel;
import com.acc.model.ChartOfAccount;
import com.common.Global;
import com.common.ProUtil;
import com.common.StartWithRowFilter;
import com.common.TableCellRender;
import com.common.Util1;
import com.inventory.model.Expense;
import com.inventory.model.ExpenseKey;
import com.inventory.ui.common.ExpenseTableModel;
import com.repo.InventoryRepo;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class ExpenseSetupDialog extends javax.swing.JDialog implements KeyListener {

    private int selectRow = - 1;
    private Expense exp = new Expense();
    private ExpenseTableModel expenseTableModel = new ExpenseTableModel();
    private InventoryRepo inventoryRepo;
    private AccountRepo accountRepo;
    private TableRowSorter<TableModel> sorter;
    private StartWithRowFilter swrf;
    private COAComboBoxModel coaComboModel = new COAComboBoxModel();
    private boolean needAccount;

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
     * Creates new form ItemTypeSetupDialog
     *
     * @param frame
     * @param needAccount
     */
    public ExpenseSetupDialog(JFrame frame, boolean needAccount) {
        super(frame, false);
        this.needAccount = needAccount;
        cboAccount.setEnabled(needAccount);
        initComponents();
        initKeyListener();
        initSpinner();
        lblStatus.setForeground(Color.green);
    }

    private void initSpinner() {
        SpinnerModel spinnerModel = new SpinnerNumberModel(0, 0, 100, 1);
        spPercent.setModel(spinnerModel);
    }

    public void initMain() {
        swrf = new StartWithRowFilter(txtFilter);
        initTable();
        initCombo();
        searchCategory();
        txtName.requestFocus();
    }

    private void initCombo() {
        Mono<List<ChartOfAccount>> m1 = accountRepo.getCOAByHead(ProUtil.getProperty(ProUtil.INCOME));
        Mono<List<ChartOfAccount>> m2 = accountRepo.getCOAByHead(ProUtil.getProperty(ProUtil.EXPENSE));
        Mono.zip(m1, m2).flatMap((t) -> Flux.fromIterable(t.getT1())
                .concatWith(Flux.fromIterable(t.getT2()))
                .collectList())
                .subscribe((t) -> {
                    coaComboModel.setData(t);
                    cboAccount.setModel(coaComboModel);
                });
    }

    private void initKeyListener() {
        txtName.addKeyListener(this);
        btnClear.addKeyListener(this);
        btnSave.addKeyListener(this);
        tblCategory.addKeyListener(this);
    }

    private void searchCategory() {
        inventoryRepo.getExpense().subscribe((t) -> {
            expenseTableModel.setListDetail(t);
        });
    }

    private void initTable() {
        tblCategory.setModel(expenseTableModel);
        sorter = new TableRowSorter<>(tblCategory.getModel());
        tblCategory.setRowSorter(sorter);
        tblCategory.getTableHeader().setFont(Global.lableFont);
        tblCategory.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCategory.setDefaultRenderer(Object.class, new TableCellRender());
        tblCategory.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting()) {
                if (tblCategory.getSelectedRow() >= 0) {
                    selectRow = tblCategory.convertRowIndexToModel(tblCategory.getSelectedRow());
                    setExpense(expenseTableModel.getExpense(selectRow));
                }
            }
        });
        tblCategory.setRowHeight(Global.tblRowHeight);
        tblCategory.setDefaultRenderer(Object.class, new TableCellRender());

    }

    private void setExpense(Expense cat) {
        exp = cat;
        txtUserCode.setText(exp.getUserCode());
        txtName.setText(exp.getExpenseName());
        spPercent.setValue(exp.getPercent());
        cboAccount.setSelectedIndex(getIndex(cat.getAccountCode()));
        cboAccount.repaint();
        lblStatus.setText("EDIT");
        lblStatus.setForeground(Color.blue);

    }

    private int getIndex(String code) {
        List<ChartOfAccount> list = coaComboModel.getData();
        for (int i = 0; i < list.size(); i++) {
            ChartOfAccount coa = list.get(i);
            if (coa.getKey().getCoaCode().equals(code)) {
                return i;
            }
        }
        return -1;
    }

    private void save() {
        if (isValidEntry()) {
            inventoryRepo.saveExpense(exp).subscribe((t) -> {
                if (lblStatus.getText().equals("EDIT")) {
                    expenseTableModel.setObject(t, selectRow);
                } else {
                    expenseTableModel.addObject(t);
                }
                clear();
            });

        }
    }

    private void clear() {
        txtName.setText(null);
        txtUserCode.setText(null);
        txtFilter.setText(null);
        cboAccount.setSelectedItem(null);
        cboAccount.repaint();
        spPercent.setValue(0);
        lblStatus.setText("NEW");
        lblStatus.setForeground(Color.green);
        exp = new Expense();
        tblCategory.requestFocus();
        txtName.requestFocus();
    }

    private boolean isValidEntry() {
        boolean status = true;
        if (needAccount) {
            if (coaComboModel.getSelectedItem() == null) {
                status = false;
                JOptionPane.showMessageDialog(this, "Invalid Name");
                cboAccount.requestFocus();
            }
        } else {
            if (lblStatus.getText().equals("NEW")) {
                ExpenseKey key = new ExpenseKey();
                key.setExpenseCode(null);
                key.setCompCode(Global.compCode);
                exp.setKey(key);
            }
            ChartOfAccount c = (ChartOfAccount) cboAccount.getSelectedItem();
            exp.setUserCode(txtUserCode.getText());
            exp.setAccountCode(c.getKey().getCoaCode());
            exp.setExpenseName(txtName.getText());
            exp.setPercent(Util1.getFloat(spPercent.getValue()));
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

        jScrollPane1 = new javax.swing.JScrollPane();
        tblCategory = new javax.swing.JTable();
        txtFilter = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        btnSave = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        cboAccount = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        spPercent = new javax.swing.JSpinner();
        jLabel5 = new javax.swing.JLabel();
        txtUserCode = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Expense Setup");
        setModalityType(java.awt.Dialog.ModalityType.TOOLKIT_MODAL);

        tblCategory.setFont(Global.textFont);
        tblCategory.setModel(new javax.swing.table.DefaultTableModel(
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
        tblCategory.setName("tblCategory"); // NOI18N
        jScrollPane1.setViewportView(tblCategory);

        txtFilter.setName("txtFilter"); // NOI18N
        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Account");

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

        btnClear.setFont(Global.lableFont);
        btnClear.setText("Clear");
        btnClear.setName("btnClear"); // NOI18N
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        lblStatus.setFont(Global.lableFont);
        lblStatus.setText("NEW");

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Name");

        txtName.setFont(Global.textFont);
        txtName.setName("txtName"); // NOI18N
        txtName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtNameFocusGained(evt);
            }
        });
        txtName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNameActionPerformed(evt);
            }
        });

        cboAccount.setFont(Global.textFont);
        cboAccount.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboAccountItemStateChanged(evt);
            }
        });
        cboAccount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboAccountActionPerformed(evt);
            }
        });

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Percent (%)");

        spPercent.setFont(Global.textFont);

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("User Code");

        txtUserCode.setFont(Global.textFont);
        txtUserCode.setName("txtName"); // NOI18N
        txtUserCode.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtUserCodeFocusGained(evt);
            }
        });
        txtUserCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUserCodeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtName)
                            .addComponent(cboAccount, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(spPercent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(txtUserCode)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                        .addGap(26, 26, 26)
                        .addComponent(btnSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnClear, btnSave});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(cboAccount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtUserCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(spPercent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnClear)
                    .addComponent(btnSave)
                    .addComponent(lblStatus))
                .addContainerGap(209, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFilter, javax.swing.GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        try {
            save();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Save Expense", JOptionPane.ERROR_MESSAGE);
            log.error("Save Categor :" + e.getMessage());
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        // TODO add your handling code here:
        clear();
    }//GEN-LAST:event_btnClearActionPerformed

    private void txtFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyReleased
        // TODO add your handling code here:
        if (txtFilter.getText().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(swrf);
        }
    }//GEN-LAST:event_txtFilterKeyReleased

    private void txtNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNameFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNameFocusGained

    private void txtNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNameActionPerformed

    private void cboAccountItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboAccountItemStateChanged
        // TODO add your handling code here:

    }//GEN-LAST:event_cboAccountItemStateChanged

    private void cboAccountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboAccountActionPerformed
        // TODO add your handling code here:
        if (cboAccount.getSelectedItem() instanceof ChartOfAccount coa) {
            txtName.setText(coa.getCoaNameEng());
        }
    }//GEN-LAST:event_cboAccountActionPerformed

    private void txtUserCodeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtUserCodeFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUserCodeFocusGained

    private void txtUserCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUserCodeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUserCodeActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox<ChartOfAccount> cboAccount;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JSpinner spPercent;
    private javax.swing.JTable tblCategory;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtUserCode;
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

        if (sourceObj instanceof JTable jTable) {
            ctrlName = jTable.getName();
        } else if (sourceObj instanceof JTextField jTextField) {
            ctrlName = jTextField.getName();
        } else if (sourceObj instanceof JButton jButton) {
            ctrlName = jButton.getName();
        }
        switch (ctrlName) {
            case "txtName" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnSave.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    btnClear.requestFocus();
                }
            }

            case "btnDelete" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnClear.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    btnSave.requestFocus();
                }
            }
            case "btnClear" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtName.requestFocus();
                }
            }
            case "txtPercent" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnSave.requestFocus();
                }
            }

        }
    }
}
