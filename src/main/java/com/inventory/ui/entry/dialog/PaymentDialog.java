/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.inventory.ui.entry.dialog;

import com.common.ComponentUtil;
import com.common.Global;
import com.common.IconUtil;
import com.common.Util1;
import com.inventory.entity.SaleHis;
import com.repo.AccountRepo;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class PaymentDialog extends javax.swing.JDialog implements KeyListener {

    @Getter
    @Setter
    private boolean confirm;
    @Setter
    private AccountRepo accountRepo;
    @Setter
    private SaleHis sh;
    @Setter
    private boolean newState;

    /**
     * Creates new form PaymentDialog
     *
     * @param frame
     */
    public PaymentDialog(JFrame frame) {
        super(frame, true);
        initComponents();
        initTextFormat();

    }

    private void initTextFormat() {
        ComponentUtil.addFocusListener(this);
        ComponentUtil.setTextProperty(this);
        txtPayment.getDocument().addDocumentListener(dl);
        txtOpening.getDocument().addDocumentListener(dl);
    }

    private void refresh() {
        setData(sh, newState);
    }

    public void setData(SaleHis sh, boolean newState) {
        this.sh = sh;
        this.newState = newState;
        clear();
        if (newState) {
            progress.setIndeterminate(true);
            String traderCode = sh.getTraderCode();
            String curCode = sh.getCurCode();
            String vouDate = Util1.addDay(Util1.toDateStr(sh.getVouDate(), "yyyy-MM-dd"), 1);
            accountRepo.getTraderBalance(vouDate, traderCode, curCode).doOnSuccess((opening) -> {
                SwingUtilities.invokeLater(() -> {
                    double vouTotal = sh.getVouTotal();
                    double balance = opening + vouTotal;
                    log.info("balance" + balance);
                    txtOpening.setForeground(opening < 0 ? Color.red : Color.green);
                    txtOpening.setValue(opening);
                    txtVouTotal.setValue(vouTotal);
                    txtBalance.setValue(balance);
                    txtPayment.setEditable(true);
                    txtOpening.setEditable(true);
                });
            }).doOnError((e) -> {
                JOptionPane.showMessageDialog(this, e.getMessage(), "ERROR", JOptionPane.ERROR);
            }).doOnTerminate(() -> {
                txtPayment.requestFocus();
                progress.setIndeterminate(false);
            }).subscribe();
        } else {
            initData(sh);
        }
        setVisible(true);
    }

    private void initData(SaleHis sh) {
        txtOpening.setValue(sh.getOpening());
        txtOpening.setForeground(sh.getOpening() < 0 ? Color.red : Color.green);
        txtVouTotal.setValue(sh.getVouTotal());
        txtBalance.setValue(sh.getTotalBalance());
        txtPayment.setValue(sh.getTotalPayment());
        txtClosing.setValue(sh.getOutstanding());
        txtPayment.setEditable(true);
        txtOpening.setEditable(true);
    }

    private void calPayment() {
        SwingUtilities.invokeLater(() -> {
            double opening = Util1.getDouble(txtOpening.getText());
            double vouTotal = Util1.getDouble(txtVouTotal.getText());
            double balance = opening + vouTotal;
            double payment = Util1.getDouble(txtPayment.getText());
            double outstanding = balance - payment;
            txtBalance.setValue(balance);
            txtClosing.setValue(outstanding);
            txtClosing.setForeground(outstanding < 0 ? Color.red : Color.green);
        });
    }

    private void confirm() {
        double opening = Util1.getDouble(txtOpening.getValue());
        double payment = Util1.getDouble(txtPayment.getValue());
        double balance = Util1.getDouble(txtBalance.getValue());
        double outstanding = Util1.getDouble(txtClosing.getValue());
        sh.setOpening(opening);
        sh.setTotalPayment(payment);
        sh.setTotalBalance(balance);
        sh.setOutstanding(outstanding);
        setConfirm(true);
        setVisible(false);
    }

    private void clear() {
        txtOpening.setValue(0);
        txtVouTotal.setValue(0);
        txtBalance.setValue(0);
        txtPayment.setValue(0);
        txtClosing.setValue(0);
    }
    private final DocumentListener dl = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            calPayment();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            calPayment();

        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            calPayment();
        }
    };

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        btnConfirm = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        chkFullPaid = new javax.swing.JCheckBox();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtPayment = new javax.swing.JFormattedTextField();
        txtClosing = new javax.swing.JFormattedTextField();
        txtBalance = new javax.swing.JFormattedTextField();
        txtVouTotal = new javax.swing.JFormattedTextField();
        jLabel8 = new javax.swing.JLabel();
        txtOpening = new javax.swing.JFormattedTextField();
        jButton1 = new javax.swing.JButton();
        progress = new javax.swing.JProgressBar();

        setTitle("Payment Dialog");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel4.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel4.setForeground(Global.selectionColor);
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText(" Payment");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jButton2.setFont(Global.lableFont);
        jButton2.setText("Cancel");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        btnConfirm.setBackground(Global.selectionColor);
        btnConfirm.setFont(Global.lableFont);
        btnConfirm.setForeground(new java.awt.Color(255, 255, 255));
        btnConfirm.setText("Confirm");
        btnConfirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmActionPerformed(evt);
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Vou Total");

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Closing");

        chkFullPaid.setFont(Global.lableFont);
        chkFullPaid.setText("Full Paid");
        chkFullPaid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFullPaidActionPerformed(evt);
            }
        });

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Balance");

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Payment");

        txtPayment.setEditable(false);
        txtPayment.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPayment.setFont(Global.amtFont);
        txtPayment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPaymentActionPerformed(evt);
            }
        });

        txtClosing.setEditable(false);
        txtClosing.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtClosing.setFont(Global.amtFont);

        txtBalance.setEditable(false);
        txtBalance.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtBalance.setFont(Global.amtFont);

        txtVouTotal.setEditable(false);
        txtVouTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVouTotal.setFont(Global.amtFont);

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Opening");

        txtOpening.setEditable(false);
        txtOpening.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtOpening.setFont(Global.amtFont);
        txtOpening.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtOpeningActionPerformed(evt);
            }
        });

        jButton1.setBackground(Util1.DARK_MODE?new Color(50, 50, 50):Color.white);
        jButton1.setIcon(IconUtil.getIcon("refresh.svg"));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 106, Short.MAX_VALUE)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnConfirm))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkFullPaid, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtPayment)
                            .addComponent(txtClosing, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtBalance)
                            .addComponent(txtVouTotal)
                            .addComponent(txtOpening))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtOpening, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtVouTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBalance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPayment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtClosing, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkFullPaid)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnConfirm, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(progress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(progress, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void chkFullPaidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFullPaidActionPerformed
        // TODO add your handling code here:
        if (chkFullPaid.isSelected()) {
            txtPayment.setValue(Util1.getDouble(txtBalance.getValue()));
        } else {
            txtPayment.setValue(0);
        }
        calPayment();
    }//GEN-LAST:event_chkFullPaidActionPerformed

    private void btnConfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmActionPerformed
        // TODO add your handling code here:
        confirm();
    }//GEN-LAST:event_btnConfirmActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        setVisible(false);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void txtPaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPaymentActionPerformed
        // TODO add your handling code here:
        calPayment();
        btnConfirm.requestFocus();
    }//GEN-LAST:event_txtPaymentActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        refresh();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void txtOpeningActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtOpeningActionPerformed
        // TODO add your handling code here:
       
    }//GEN-LAST:event_txtOpeningActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConfirm;
    private javax.swing.JCheckBox chkFullPaid;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JProgressBar progress;
    private javax.swing.JFormattedTextField txtBalance;
    private javax.swing.JFormattedTextField txtClosing;
    private javax.swing.JFormattedTextField txtOpening;
    private javax.swing.JFormattedTextField txtPayment;
    private javax.swing.JFormattedTextField txtVouTotal;
    // End of variables declaration//GEN-END:variables

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
