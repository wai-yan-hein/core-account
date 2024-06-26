/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.user.dialog;

import com.common.Global;
import com.common.Util1;
import com.user.common.CompanyComboModel;
import com.repo.UserRepo;
import com.user.model.CompanyInfo;
import com.user.model.YearEnd;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import lombok.Setter;

/**
 *
 * @author Lenovo
 */
public class YearEndProcessingDailog extends javax.swing.JDialog {

    @Setter
    private UserRepo userRepo;
    @Setter
    private String token;

    /**
     * Creates new form YearEndProcessingDailog
     *
     * @param parent
     */
    public YearEndProcessingDailog(java.awt.Frame parent) {
        super(parent, true);
        initComponents();
    }

    public void initMain() {
        txtYearEnd.setDate(Util1.getTodayDate());
        txtStartDate.setDate(Util1.getTodayDate());
        LocalDate now = LocalDate.now();
        LocalDate endDate = now.plusYears(2);
        txtEndDate.setDate(Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        userRepo.getCompany(true).doOnSuccess((t) -> {
            CompanyComboModel model = new CompanyComboModel(t);
            cboCompany.setModel(model);
            cboCompany.setSelectedIndex(0);
        }).subscribe();
    }

    private void confirm() {
        if (isValidEntry()) {
            int yn = JOptionPane.showConfirmDialog(this, "Are you sure year end processing?"
                    + "", "Confirm Dialog", JOptionPane.YES_OPTION, JOptionPane.WARNING_MESSAGE);
            if (yn == JOptionPane.YES_OPTION) {
                progress.setIndeterminate(true);
                lblLog.setText("Please wait this make a few minute...");
                btnConfirm.setEnabled(false);
                btnExit.setEnabled(false);
                CompanyInfo company = (CompanyInfo) cboCompany.getSelectedItem();
                YearEnd end = new YearEnd();
                end.setYeCompCode(company.getCompCode());
                end.setYearEndDate(Util1.toLocalDate(txtYearEnd.getDate()));
                end.setStartDate(Util1.toLocalDate(txtStartDate.getDate()));
                end.setEndDate(Util1.toLocalDate(txtEndDate.getDate()));
                end.setOpening(chkOpening.isSelected());
                end.setBatchLock(chkLock.isSelected());
                end.setCreatedDate(LocalDateTime.now());
                end.setCreateBy(Global.loginUser.getUserCode());
                end.setToken(token);
                userRepo.yearEnd(end).doOnSuccess((t) -> {
                    lblLog.setText(t.getMessage());
                    String compCode = t.getCompCode();
                    if (compCode != null) {
                        lblLog.setText(t.getMessage());
                        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                        btnConfirm.setEnabled(true);
                        btnExit.setEnabled(true);
                        progress.setIndeterminate(false);
                        dispose();
                    }
                }).doOnError((e) -> {
                    error(e);
                }).subscribe();
            }
        }
    }

    private void error(Throwable e) {
        JOptionPane.showMessageDialog(this, e.getMessage());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        progress.setIndeterminate(false);
        btnConfirm.setEnabled(true);
        btnExit.setEnabled(true);
    }

    private boolean isValidEntry() {
        if (txtYearEnd.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Invalid Year End Date.");
            txtYearEnd.requestFocus();
            return false;
        } else if (txtStartDate.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Invalid Start Date.");
            txtStartDate.requestFocus();
            return false;
        } else if (txtEndDate.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Invalid Start Date.");
            txtEndDate.requestFocus();
            return false;
        } else if (cboCompany.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Invalid Company.");
            cboCompany.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        cboCompany = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        txtStartDate = new com.toedter.calendar.JDateChooser();
        chkOpening = new javax.swing.JCheckBox();
        chkLock = new javax.swing.JCheckBox();
        btnConfirm = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        progress = new javax.swing.JProgressBar();
        lblLog = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtEndDate = new com.toedter.calendar.JDateChooser();
        jLabel5 = new javax.swing.JLabel();
        txtYearEnd = new com.toedter.calendar.JDateChooser();
        btnExit = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Year End Dialog");

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Company");

        cboCompany.setFont(Global.textFont);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Start Date");

        txtStartDate.setDateFormatString("dd/MM/yyyy");
        txtStartDate.setFont(Global.textFont);

        chkOpening.setFont(Global.lableFont);
        chkOpening.setText("Zero Opening");

        chkLock.setFont(Global.lableFont);
        chkLock.setSelected(true);
        chkLock.setText("Lock");

        btnConfirm.setFont(Global.lableFont);
        btnConfirm.setText("Confirm");
        btnConfirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel3.setFont(Global.menuFont);
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Year End Processing");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 53, Short.MAX_VALUE)
                .addContainerGap())
        );

        lblLog.setFont(Global.lableFont);
        lblLog.setText("...");

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("End Date");

        txtEndDate.setDateFormatString("dd/MM/yyyy");
        txtEndDate.setFont(Global.textFont);

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Year End Date");

        txtYearEnd.setDateFormatString("dd/MM/yyyy");
        txtYearEnd.setFont(Global.textFont);

        btnExit.setFont(Global.lableFont);
        btnExit.setText("Exit");
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnExit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnConfirm))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cboCompany, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtStartDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(chkLock, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(chkOpening))
                                    .addComponent(txtEndDate, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE)
                                    .addComponent(txtYearEnd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(progress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblLog, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(progress, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cboCompany)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(txtYearEnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(txtStartDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(txtEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkOpening)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkLock)
                        .addGap(7, 7, 7)
                        .addComponent(lblLog)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnConfirm))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(174, 174, 174)
                        .addComponent(btnExit)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnConfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmActionPerformed
        // TODO add your handling code here:
        confirm();
    }//GEN-LAST:event_btnConfirmActionPerformed

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        // TODO add your handling code here:
        dispose();
    }//GEN-LAST:event_btnExitActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConfirm;
    private javax.swing.JButton btnExit;
    private javax.swing.JComboBox<CompanyInfo> cboCompany;
    private javax.swing.JCheckBox chkLock;
    private javax.swing.JCheckBox chkOpening;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblLog;
    private javax.swing.JProgressBar progress;
    private com.toedter.calendar.JDateChooser txtEndDate;
    private com.toedter.calendar.JDateChooser txtStartDate;
    private com.toedter.calendar.JDateChooser txtYearEnd;
    // End of variables declaration//GEN-END:variables

}
