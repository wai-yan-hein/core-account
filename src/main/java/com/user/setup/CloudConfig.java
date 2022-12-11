/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.user.setup;

import com.common.Global;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.user.common.DepartmentConfigTableModel;
import com.user.common.UserRepo;
import com.user.model.PropertyKey;
import com.user.model.SysProperty;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Component
public class CloudConfig extends javax.swing.JPanel {

    private final DepartmentConfigTableModel tableModel = new DepartmentConfigTableModel();
    private SelectionObserver observer;

    public SelectionObserver getObserver() {
        return observer;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    @Autowired
    private UserRepo userRepo;
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
     * Creates new form CloudConfig
     */
    public CloudConfig() {
        initComponents();
        initKeyName();
        initKeyListener();

    }

    private void initKeyName() {
        txtSvrQ.setName("cloud.activemq.inventory.server.queue");
        txtAccSvrQ.setName("cloud.activemq.account.server.queue");
        txtMQUrl.setName("cloud.activemq.url");
        chkUpServer.setName("cloud.upload.server");
    }

    private void initKeyListener() {
        txtSvrQ.addActionListener(action);
        txtAccSvrQ.addActionListener(action);
        txtMQUrl.addActionListener(action);
        chkUpServer.addActionListener(action);

    }

    public void initMain() {
        initTable();
        search();
    }

    private void save(String key, String value) {
        SysProperty p = new SysProperty();
        PropertyKey pKey = new PropertyKey();
        pKey.setPropKey(key);
        pKey.setCompCode(Global.compCode);
        p.setKey(pKey);
        p.setPropValue(value);
        userRepo.saveSys(p);
        Global.hmRoleProperty.put(key, value);
    }

    private void initTable() {
        tableModel.setUserRepo(userRepo);
        tableModel.setTable(tblDepartment);
        tblDepartment.setModel(tableModel);
        tblDepartment.getTableHeader().setFont(Global.tblHeaderFont);
        tblDepartment.setRowHeight(Global.tblRowHeight);
        tblDepartment.setFont(Global.textFont);
        tblDepartment.setShowGrid(true);
        tblDepartment.getColumnModel().getColumn(0).setCellEditor(new AutoClearEditor());
        tblDepartment.getColumnModel().getColumn(1).setCellEditor(new AutoClearEditor());
        tblDepartment.getColumnModel().getColumn(2).setCellEditor(new AutoClearEditor());
        tblDepartment.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());

        tblDepartment.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblDepartment.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblDepartment.setCellSelectionEnabled(true);
    }

    private void search() {
        tableModel.setListDepartment(userRepo.getDeparment());
        tableModel.addNewRow();
        txtSvrQ.setText(Global.hmRoleProperty.get(txtSvrQ.getName()));
        txtAccSvrQ.setText(Global.hmRoleProperty.get(txtAccSvrQ.getName()));
        txtMQUrl.setText(Global.hmRoleProperty.get(txtMQUrl.getName()));
        chkUpServer.setSelected(Util1.getBoolean(Global.hmRoleProperty.get(chkUpServer.getName())));
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
        tblDepartment = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        txtSvrQ = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtMQUrl = new javax.swing.JTextField();
        chkUpServer = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        txtAccSvrQ = new javax.swing.JTextField();

        tblDepartment.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblDepartment);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        txtSvrQ.setFont(Global.textFont);
        txtSvrQ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSvrQActionPerformed(evt);
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Inventory Server Q");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Active MQ Url");

        txtMQUrl.setFont(Global.textFont);
        txtMQUrl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMQUrlActionPerformed(evt);
            }
        });

        chkUpServer.setText("Upload Server");

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Account Server Q");

        txtAccSvrQ.setFont(Global.textFont);
        txtAccSvrQ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAccSvrQActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(8, 8, 8)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtMQUrl)
                    .addComponent(txtSvrQ)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(chkUpServer)
                        .addGap(0, 334, Short.MAX_VALUE))
                    .addComponent(txtAccSvrQ))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSvrQ, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtAccSvrQ, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtMQUrl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkUpServer)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 545, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 494, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtSvrQActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSvrQActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSvrQActionPerformed

    private void txtMQUrlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMQUrlActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMQUrlActionPerformed

    private void txtAccSvrQActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAccSvrQActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAccSvrQActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkUpServer;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblDepartment;
    private javax.swing.JTextField txtAccSvrQ;
    private javax.swing.JTextField txtMQUrl;
    private javax.swing.JTextField txtSvrQ;
    // End of variables declaration//GEN-END:variables
}
