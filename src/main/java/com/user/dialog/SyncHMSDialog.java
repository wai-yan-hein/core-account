/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.user.dialog;

import com.common.ComponentUtil;
import com.common.Global;
import com.common.TableCellRender;
import com.common.Util1;
import com.model.VoucherInfoEnum;
import com.repo.HMSRepo;
import com.user.common.HMSVoucherTableModel;
import com.user.model.SyncModel;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class SyncHMSDialog extends javax.swing.JDialog {

    private final HMSVoucherTableModel hmsVoucherTableModel = new HMSVoucherTableModel();
    @Setter
    private HMSRepo hmsRepo;

    /**
     * Creates new form CurrencySetup
     *
     * @param frame
     */
    public SyncHMSDialog(JFrame frame) {
        super(frame, true);
        initComponents();
        initFocusAdapter();
    }

    public void initMain() {
        initTable();
        initModel();
    }

    private void initFocusAdapter() {
        ComponentUtil.addFocusListener(this);
    }

    private void initModel() {
        hmsVoucherTableModel.clear();
        VoucherInfoEnum[] list = VoucherInfoEnum.values();
        for (VoucherInfoEnum e : list) {
            SyncModel m = new SyncModel();
            m.setSync(false);
            m.setTranSource(e.name());
            hmsVoucherTableModel.addObject(m);
        }
        txtStartDate.setDate(Util1.getTodayDate());
        txtEndDate.setDate(Util1.getTodayDate());

    }

    private void initTable() {
        tblVoucher.setModel(hmsVoucherTableModel);
        tblVoucher.getTableHeader().setFont(Global.textFont);
        tblVoucher.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblVoucher.getColumnModel().getColumn(0).setPreferredWidth(100);// Code
        tblVoucher.getColumnModel().getColumn(1).setPreferredWidth(10);// Name  
        tblVoucher.setDefaultRenderer(Boolean.class, new TableCellRender());
        tblVoucher.setDefaultRenderer(Object.class, new TableCellRender());
        tblVoucher.setRowHeight(Global.tblRowHeight);
        tblVoucher.setDefaultRenderer(Object.class, new TableCellRender());
    }

    private void syncVoucher() {
        progress.setIndeterminate(true);
        btnSave.setEnabled(false);
        List<SyncModel> list = hmsVoucherTableModel.getFilterList();
        list.forEach((t) -> {
            t.setFromDate(Util1.toDateStr(txtStartDate.getDate(), "yyyy-MM-dd"));
            t.setToDate(Util1.toDateStr(txtEndDate.getDate(), "yyyy-MM-dd"));
            t.setAck(chkAck.isSelected());
        });
        hmsRepo.syncVoucher(list).doOnSuccess((t) -> {
            JOptionPane.showMessageDialog(this, "Sync Complete.");
            progress.setIndeterminate(false);
            btnSave.setEnabled(true);
        }).doOnError((e) -> {
            progress.setIndeterminate(false);
            btnSave.setEnabled(true);
            JOptionPane.showMessageDialog(this, e.getMessage());
        }).subscribe();

    }

    public void clear() {
        progress.setIndeterminate(false);
        btnSave.setEnabled(true);
        initModel();
    }

    private void selectAll() {
        List<SyncModel> list = hmsVoucherTableModel.getListSync();
        list.forEach((t) -> {
            t.setSync(chkSelect.isSelected());
        });
        hmsVoucherTableModel.setListSync(list);
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
        tblVoucher = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btnClear = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        txtStartDate = new com.toedter.calendar.JDateChooser();
        txtEndDate = new com.toedter.calendar.JDateChooser();
        chkSelect = new javax.swing.JCheckBox();
        chkAck = new javax.swing.JCheckBox();
        progress = new javax.swing.JProgressBar();

        setTitle("Sync HMS Dialog");

        tblVoucher.setFont(Global.textFont);
        tblVoucher.setModel(new javax.swing.table.DefaultTableModel(
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
        tblVoucher.setName("tblVoucher"); // NOI18N
        tblVoucher.setRowHeight(Global.tblRowHeight);
        jScrollPane1.setViewportView(tblVoucher);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel1.setFont(Global.textFont);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("From Date");

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("To Date");

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
        btnSave.setText("Sync");
        btnSave.setName("btnSave"); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        txtStartDate.setDateFormatString("dd/MM/yyyy");
        txtStartDate.setFont(Global.textFont);

        txtEndDate.setDateFormatString("dd/MM/yyyy");
        txtEndDate.setFont(Global.textFont);

        chkSelect.setFont(Global.lableFont);
        chkSelect.setText("Select All");
        chkSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSelectActionPerformed(evt);
            }
        });

        chkAck.setFont(Global.lableFont);
        chkAck.setText("ACK");
        chkAck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkAckActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtStartDate, javax.swing.GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE)
                            .addComponent(txtEndDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(chkAck)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnClear))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(chkSelect, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(txtStartDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(txtEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnClear)
                    .addComponent(btnSave)
                    .addComponent(chkAck))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 241, Short.MAX_VALUE)
                .addComponent(chkSelect)
                .addContainerGap())
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
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE)
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE))
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
        syncVoucher();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void chkSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSelectActionPerformed
        // TODO add your handling code here:
        selectAll();
    }//GEN-LAST:event_chkSelectActionPerformed

    private void chkAckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkAckActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkAckActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox chkAck;
    private javax.swing.JCheckBox chkSelect;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JProgressBar progress;
    private javax.swing.JTable tblVoucher;
    private com.toedter.calendar.JDateChooser txtEndDate;
    private com.toedter.calendar.JDateChooser txtStartDate;
    // End of variables declaration//GEN-END:variables

}
