/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package com.inventory.ui.entry.dialog;

import com.common.Global;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.editor.ExpenseEditor;
import com.inventory.model.SaleExpense;
import com.inventory.model.SaleExpenseKey;
import com.inventory.ui.common.SaleExpenseTableModel;
import com.inventory.ui.setup.dialog.ExpenseSetupDialog;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.repo.AccountRepo;
import com.repo.InventoryRepo;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

/**
 *
 * @author Lenovo
 */
public class SaleExpenseFrame extends javax.swing.JInternalFrame implements SelectionObserver {

    private InventoryRepo inventoryRepo;
    private AccountRepo accountRepo;
    private SelectionObserver observer;
    private final SaleExpenseTableModel expenseTableModel = new SaleExpenseTableModel();

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public void setAccountRepo(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    /**
     * Creates new form SaleExpenseFrame
     */
    public SaleExpenseFrame() {
        initComponents();
    }

    public void initMain() {
        initTable();
    }

    private void expenseDialog() {
        ExpenseSetupDialog d = new ExpenseSetupDialog(Global.parentForm, true);
        d.setInventoryRepo(inventoryRepo);
        d.setAccountRepo(accountRepo);
        d.initMain();
        d.setLocationRelativeTo(null);
        d.setVisible(true);
    }

    private void initTable() {
        expenseTableModel.addNewRow();
        expenseTableModel.setObserver(this);
        expenseTableModel.setTable(tblExpense);
        tblExpense.setModel(expenseTableModel);
        tblExpense.getTableHeader().setFont(Global.tblHeaderFont);
        tblExpense.setFont(Global.textFont);
        tblExpense.setRowHeight(Global.tblRowHeight);
        tblExpense.setCellSelectionEnabled(true);
        tblExpense.setShowGrid(true);
        tblExpense.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblExpense.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblExpense.getColumnModel().getColumn(0).setPreferredWidth(100);
        tblExpense.getColumnModel().getColumn(1).setPreferredWidth(40);
        inventoryRepo.getExpense().doOnSuccess((t) -> {
            tblExpense.getColumnModel().getColumn(0).setCellEditor(new ExpenseEditor(t));
        }).subscribe();
        tblExpense.getColumnModel().getColumn(1).setCellEditor(new AutoClearEditor());
        txtExpense.setFormatterFactory(Util1.getDecimalFormat());
        txtExpense.setFont(Global.amtFont);
        txtExpense.setHorizontalAlignment(JTextField.RIGHT);
        String solve = "delete";
        KeyStroke delete = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        tblExpense.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(delete, solve);
        tblExpense.getActionMap().put(solve, new DeleteExpense());
    }

    public List<SaleExpense> getListDetail() {
        return expenseTableModel.getExpenseList();
    }

    public List<SaleExpenseKey> getDeleteList() {
        return expenseTableModel.getDeleteList();
    }

    public double getExpense() {
        return Util1.getDouble(txtExpense.getValue());
    }

    public void setExpense(Double expense) {
        txtExpense.setValue(expense);
    }

    private class DeleteExpense extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            deleteTranExpense();
        }
    }

    private void deleteTranExpense() {
        int row = tblExpense.convertRowIndexToModel(tblExpense.getSelectedRow());
        if (row >= 0) {
            int yes_no = JOptionPane.showConfirmDialog(this,
                    "Are you sure to delete?", "Expense Transaction delete.", JOptionPane.YES_NO_OPTION);
            if (yes_no == 0) {
                expenseTableModel.delete(row);
            }
        }
    }

    public void searchExpense(String vouNo) {
        expProgress.setIndeterminate(true);
        inventoryRepo.getSaleExpense(vouNo).doOnSuccess((t) -> {
            expenseTableModel.setListDetail(t);
            expenseTableModel.addNewRow();
            expProgress.setIndeterminate(false);
        }).doOnError((e) -> {
            JOptionPane.showMessageDialog(this, e.getMessage());
            expProgress.setIndeterminate(false);
        }).subscribe();
    }

    private void calExpense() {
        double ttlExp = expenseTableModel.getListDetail().stream()
                .mapToDouble(e -> Util1.getDouble(e.getAmount()))
                .sum();
        txtExpense.setValue(ttlExp);
        observer.selected("CAL-TOTAL", "CAL-TOTAL");
    }

    public void clear() {
        txtExpense.setValue(0);
        expenseTableModel.clear();
        expenseTableModel.addNewRow();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        txtExpense = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblExpense = new javax.swing.JTable();
        expProgress = new javax.swing.JProgressBar();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Sale Expense");
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/images/spring-icon.png"))); // NOI18N

        jButton1.setFont(Global.amtFont);
        jButton1.setText("+");
        jButton1.setAlignmentY(0.0F);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        txtExpense.setEditable(false);

        jLabel1.setText("Total : ");

        tblExpense.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane4.setViewportView(tblExpense);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(expProgress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButton1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 104, Short.MAX_VALUE)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtExpense, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(expProgress, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtExpense, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jButton1))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        expenseDialog();
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar expProgress;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable tblExpense;
    private javax.swing.JFormattedTextField txtExpense;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {
        String src = source.toString();
        if (src.equals("CAL-TOTAL")) {
            calExpense();
        }
    }
}
