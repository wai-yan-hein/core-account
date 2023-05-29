/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.user.setup;

import com.common.Global;
import com.user.common.RoleCompanyTableModel;
import com.user.common.UserRepo;
import com.user.model.PrivilegeCompany;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 *
 * @author Lenovo
 */
@Component
public class RoleCompany extends javax.swing.JPanel {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private WebClient userApi;
    private final RoleCompanyTableModel tableModel = new RoleCompanyTableModel();
    private JProgressBar progress;

    public JProgressBar getProgress() {
        return progress;
    }

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }

    /**
     * Creates new form SystemProperty
     */
    public RoleCompany() {
        initComponents();
    }

    private void focusTable() {
        int row = tblSystem.getRowCount();
        if (row >= 1) {
            tblSystem.setColumnSelectionInterval(0, 0);
            tblSystem.setRowSelectionInterval(row - 1, row - 1);
        }
        tblSystem.requestFocusInWindow();
    }

    public void initTable() {
        tableModel.setUserRepo(userRepo);
        tblSystem.setModel(tableModel);
        tblSystem.getTableHeader().setFont(Global.tblHeaderFont);
        tblSystem.setRowHeight(Global.tblRowHeight);
        tblSystem.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblSystem.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblSystem.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0), "F8-Action");
        tblSystem.getActionMap().put("F8-Action", actionDelete);
    }
    private final Action actionDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            //delete();
        }
    };

    public void searchCompany(String roleCode) {
        tableModel.clear();
        userRepo.searchCompany(roleCode)
                .subscribe((t) -> {
                    tableModel.setRoleCode(roleCode);
                    tableModel.setListProperty(t);
                    tableModel.addNewRow();
                    tblSystem.requestFocus();
                }, (e) -> {
                    JOptionPane.showMessageDialog(this, e.getMessage());
                });
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
        tblSystem = new javax.swing.JTable();
        txtFilter = new javax.swing.JTextField();

        tblSystem.setFont(Global.textFont);
        tblSystem.setModel(new javax.swing.table.DefaultTableModel(
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
        tblSystem.setRowHeight(Global.tblRowHeight);
        tblSystem.setShowHorizontalLines(true);
        tblSystem.setShowVerticalLines(true);
        jScrollPane1.setViewportView(tblSystem);

        txtFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFilterActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                    .addComponent(txtFilter))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFilterActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFilterActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblSystem;
    private javax.swing.JTextField txtFilter;
    // End of variables declaration//GEN-END:variables
}
