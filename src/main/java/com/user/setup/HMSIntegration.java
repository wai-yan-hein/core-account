/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.user.setup;

import com.common.Global;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.common.Util1;
import com.model.VoucherInfo;
import com.model.VoucherInfoEnum;
import com.repo.AccountRepo;
import com.repo.HMSRepo;
import com.user.common.HMSIntegrationTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.JProgressBar;
import javax.swing.ListSelectionModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 *
 * @author DELL
 */
@Component
@Slf4j
public class HMSIntegration extends javax.swing.JPanel {

    @Autowired
    private HMSRepo hmsRepo;
    @Autowired
    private AccountRepo accountRepo;
    private final HMSIntegrationTableModel tableModel = new HMSIntegrationTableModel();
    private JProgressBar progress;
    private SelectionObserver observer;

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }

    /**
     * Creates new form HMSIntegration
     */
    public HMSIntegration() {
        initComponents();
    }

    public void initMain() {
        initTable();
        initDate();
    }

    private void initDate() {
        txtFromDate.setDate(Util1.getTodayDate());
        txtToDate.setDate(Util1.getTodayDate());
    }

    private void initTable() {
        tblAudit.setModel(tableModel);
        tblAudit.setFont(Global.textFont);
        tblAudit.setRowHeight(Global.tblRowHeight);
        tblAudit.getTableHeader().setFont(Global.textFont);
        tblAudit.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblAudit.setDefaultRenderer(Object.class, new TableCellRender());
        tblAudit.setDefaultRenderer(Double.class, new TableCellRender());
    }

    private void check() {
        enableForm(false);
        String fromDate = Util1.toDateStr(txtFromDate.getDate(), "yyyy-MM-dd");
        String toDate = Util1.toDateStr(txtToDate.getDate(), "yyyy-MM-dd");
        Mono<List<VoucherInfo>> m1 = null;
        Mono<List<VoucherInfo>> m2 = null;
        List<VoucherInfo> joinList = new ArrayList<>();
        if (cboType.getSelectedItem() instanceof VoucherInfoEnum c) {
            String type = c.name();
            switch (type) {
                case "SALE" -> {
                    m1 = hmsRepo.getSaleList(fromDate, toDate);
                    m2 = accountRepo.getIntegrationVoucher(fromDate, toDate, type);
                }
                case "PURCHASE" -> {
                    m1 = hmsRepo.getPurchaseList(fromDate, toDate);
                    m2 = accountRepo.getIntegrationVoucher(fromDate, toDate, type);
                }
                case "RETURN_IN" -> {
                    m1 = hmsRepo.getReturnInList(fromDate, toDate);
                    m2 = accountRepo.getIntegrationVoucher(fromDate, toDate, type);
                }
                case "RETURN_OUT" -> {
                    m1 = hmsRepo.getReturnOutList(fromDate, toDate);
                    m2 = accountRepo.getIntegrationVoucher(fromDate, toDate, type);
                }
                case "OPD" -> {
                    m1 = hmsRepo.getOPDList(fromDate, toDate);
                    m2 = accountRepo.getIntegrationVoucher(fromDate, toDate, type);
                }
                case "OT" -> {
                    m1 = hmsRepo.getOTList(fromDate, toDate);
                    m2 = accountRepo.getIntegrationVoucher(fromDate, toDate, type);
                }
                case "DC" -> {
                    m1 = hmsRepo.getDCList(fromDate, toDate);
                    m2 = accountRepo.getIntegrationVoucher(fromDate, toDate, type);
                }
                case "PAYMENT" -> {
                    m1 = hmsRepo.getPaymentList(fromDate, toDate);
                    m2 = accountRepo.getIntegrationVoucher(fromDate, toDate, type);
                }
            }
        }
        m1.zipWith(m2).subscribe((zip) -> {
            List<VoucherInfo> list1 = zip.getT1();
            List<VoucherInfo> list2 = zip.getT2();
            list1.forEach((l1) -> {
                list2.forEach((l2) -> {
                    if (l1.getVouNo().equals(l2.getVouNo())) {
                        if (!Objects.equals(Util1.getDouble(l1.getVouTotal()), Util1.getDouble(l2.getVouTotal()))) {
                            VoucherInfo obj = VoucherInfo.builder()
                                    .option("Different")
                                    .vouNo(l1.getVouNo())
                                    .hmsVouTotal(l1.getVouTotal())
                                    .accVouTotal(l2.getVouTotal())
                                    .diffAmt(l1.getVouTotal() - l2.getVouTotal())
                                    .build();
                            joinList.add(obj);
                        }
                    }
                });
            });
            tableModel.setListVoucher(joinList);
            txtRecord.setValue(joinList.size());
            progress.setIndeterminate(false);
            btnCheck.setEnabled(true);
            btnSync.setEnabled(!joinList.isEmpty());
        }, (e) -> {
            enableForm(true);
        });

    }

    private void sync() {
        if (cboType.getSelectedItem() instanceof VoucherInfoEnum c) {
            enableForm(false);
            List<VoucherInfo> list = tableModel.getListVoucher();
            list.forEach((t) -> {
                String vouNo = t.getVouNo();
                hmsRepo.syncToAccount(c.name(), vouNo).subscribe((res) -> {
                    tableModel.delete(vouNo);
                    lblLog.setText(String.format("%s - %s", vouNo, res));
                    int size = tableModel.getListVoucher().size();
                    txtRecord.setValue(size);
                    if (size == 0) {
                        enableForm(true);
                        lblLog.setText("Done.");
                    }
                }, (e) -> {
                    enableForm(true);
                });
            });

        }
    }

    private void enableForm(boolean status) {
        progress.setIndeterminate(!status);
        btnSync.setEnabled(status);
        btnCheck.setEnabled(status);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        txtFromDate = new com.toedter.calendar.JDateChooser();
        jLabel1 = new javax.swing.JLabel();
        txtToDate = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        cboType = new javax.swing.JComboBox<>(VoucherInfoEnum.values());
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblAudit = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        btnSync = new javax.swing.JButton();
        btnCheck = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        txtRecord = new javax.swing.JFormattedTextField();
        lblLog = new javax.swing.JLabel();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        txtFromDate.setDateFormatString("dd/MM/yyyy");
        txtFromDate.setFont(Global.textFont);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("From Date");

        txtToDate.setDateFormatString("dd/MM/yyyy");
        txtToDate.setFont(Global.textFont);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("To Date");

        cboType.setFont(Global.textFont);

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Type");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cboType, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cboType, txtFromDate, txtToDate});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cboType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtToDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtFromDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );

        tblAudit.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblAudit);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        btnSync.setFont(Global.lableFont);
        btnSync.setText("Sync");
        btnSync.setEnabled(false);
        btnSync.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSyncActionPerformed(evt);
            }
        });

        btnCheck.setFont(Global.lableFont);
        btnCheck.setText("Check");
        btnCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCheckActionPerformed(evt);
            }
        });

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Record :");

        txtRecord.setEditable(false);
        txtRecord.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtRecord.setFont(Global.lableFont);

        lblLog.setText("-");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtRecord, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblLog, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCheck)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSync)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSync)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnCheck)
                        .addComponent(jLabel4)
                        .addComponent(txtRecord, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblLog)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCheckActionPerformed
        // TODO add your handling code here:
        check();
    }//GEN-LAST:event_btnCheckActionPerformed

    private void btnSyncActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSyncActionPerformed
        // TODO add your handling code here:
        sync();
    }//GEN-LAST:event_btnSyncActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCheck;
    private javax.swing.JButton btnSync;
    private javax.swing.JComboBox<VoucherInfoEnum> cboType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblLog;
    private javax.swing.JTable tblAudit;
    private com.toedter.calendar.JDateChooser txtFromDate;
    private javax.swing.JFormattedTextField txtRecord;
    private com.toedter.calendar.JDateChooser txtToDate;
    // End of variables declaration//GEN-END:variables
}
