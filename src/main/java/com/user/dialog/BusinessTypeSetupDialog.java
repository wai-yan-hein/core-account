/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.user.dialog;

import com.acc.common.AccountRepo;
import com.acc.model.BusinessType;
import com.common.Global;
import com.common.SelectionObserver;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.user.common.BusinessTypeTableModel;
import com.user.common.UserRepo;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

/**
 *
 * @author DELL
 */
public class BusinessTypeSetupDialog extends javax.swing.JDialog {

    private final BusinessTypeTableModel businessTypeTableModel = new BusinessTypeTableModel(true);
    private SelectionObserver observer;
    private BusinessType businessType;
    private UserRepo userRepo;
    private AccountRepo accountRepo;

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

    public BusinessType getBusinessType() {
        return businessType;
    }

    public void setBusinessType(BusinessType businessType) {
        this.businessType = businessType;
    }

    public SelectionObserver getObserver() {
        return observer;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    /**
     * Creates new form RoleSetupDialog
     *
     * @param parent
     */
    public BusinessTypeSetupDialog(JFrame parent) {
        super(parent, true);
        initComponents();
        actionMapping();
        initMain();
    }

    private void actionMapping() {
        String solve = "enter";
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        table.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, solve);
        table.getActionMap().put(solve, new EnterAction());

    }

    private class EnterAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            select();
        }
    }

    public void search() {
        userRepo.getBusinessType().subscribe((t) -> {
            businessTypeTableModel.setAccountRepo(accountRepo);
            businessTypeTableModel.setUserRepo(userRepo);
            businessTypeTableModel.setList(t);
            businessTypeTableModel.addNewRow();
        });
    }

    private void initMain() {
        businessTypeTableModel.setTable(table);
        table.setModel(businessTypeTableModel);
        table.getTableHeader().setFont(Global.tblHeaderFont);
        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(Global.tblRowHeight);
        table.setCellSelectionEnabled(true);
        table.setShowGrid(true);
        table.getColumnModel().getColumn(1).setCellEditor(new AutoClearEditor());
        table.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");

    }

    private void select() {
        int row = table.convertRowIndexToModel(table.getSelectedRow());
        if (row >= 0) {
            setBusinessType(businessTypeTableModel.getObject(row));
            this.dispose();
        }
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
        table = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Business Type Setup Dialog");

        table.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(table);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 421, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 397, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}
