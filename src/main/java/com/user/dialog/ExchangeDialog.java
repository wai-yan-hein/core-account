/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.user.dialog;

import com.common.Global;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.entity.MessageType;
import com.user.common.CurrencyComboBoxModel;
import com.repo.UserRepo;
import com.user.model.Currency;
import com.user.model.ExchangeKey;
import com.user.model.ExchangeRate;
import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class ExchangeDialog extends javax.swing.JDialog {

    private String status;
    private ExchangeRate exchange = new ExchangeRate();
    private SelectionObserver observer;
    private CurrencyComboBoxModel homeComboBoxModel = new CurrencyComboBoxModel();
    private CurrencyComboBoxModel targetComboBoxModel = new CurrencyComboBoxModel();
    private UserRepo userRepo;

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public void setExchange(ExchangeRate exchange) {
        this.exchange = exchange;
        if (exchange == null) {
            clear();
        } else {
            setData(exchange);
        }
    }

    public SelectionObserver getObserver() {
        return observer;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        lblStatus.setText(status);
        lblStatus.setForeground(status.equals("NEW") ? Color.GREEN : Color.BLUE);
    }

    /**
     * Creates new form ExchangeDialog
     *
     * @param frame
     */
    public ExchangeDialog(JFrame frame) {
        super(frame, true);
        initComponents();
        initTextFormat();
        initFocusAdapter();
    }

    private void initTextFormat() {
        txtHome.setFormatterFactory(Util1.getDecimalFormat());
        txtTarget.setFormatterFactory(Util1.getDecimalFormat());
        txtHome.setHorizontalAlignment(JTextField.RIGHT);
        txtTarget.setHorizontalAlignment(JTextField.RIGHT);
    }

    private void clear() {
        initDate();
        txtExId.setText("");
        txtHome.setValue(1);
        txtTarget.setValue(1);
        lblStatus.setText("NEW");
        txtTarget.requestFocus();
    }

    private void setData(ExchangeRate e) {
        txtExId.setText(e.getKey().getExCode());
        txtDate.setDate(Util1.convertToDate(e.getExDate()));
        txtHome.setValue(e.getHomeFactor());
        txtTarget.setValue(e.getTargetFactor());
        userRepo.findCurrency(e.getHomeCur()).subscribe((t) -> {
            cboHC.setSelectedItem(t);
            cboHC.setModel(homeComboBoxModel);
        });
        userRepo.findCurrency(e.getTargetCur()).subscribe((t) -> {
            cboTC.setSelectedItem(t);
            cboTC.setModel(targetComboBoxModel);
        });
    }

    private void initFocusAdapter() {
        txtHome.addFocusListener(fa);
        txtTarget.addFocusListener(fa);
    }
    private final FocusAdapter fa = new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            Object obj = e.getSource();
            if (obj instanceof JTextField txt) {
                txt.selectAll();
            } else if (obj instanceof JFormattedTextField txt) {
                txt.selectAll();
            }
        }
    };

    private void initDate() {
        txtDate.setDate(Util1.getTodayDate());
    }

    private void initCombo() {
        userRepo.getCurrency().subscribe((t) -> {
            homeComboBoxModel.setData(t);
            List<Currency> updatedList = new ArrayList<>(t);
            updatedList.removeIf(c -> c.getCurCode().equals(Global.currency));
            targetComboBoxModel.setData(t);
            cboHC.setModel(homeComboBoxModel);
            cboTC.setModel(targetComboBoxModel);
            cboTC.setSelectedIndex(0);
            cboHC.setEnabled(false);
            userRepo.findCurrency(Global.currency).doOnSuccess((c) -> {
                homeComboBoxModel.setSelectedItem(c);
            }).subscribe();
        }, (e) -> {
            JOptionPane.showMessageDialog(this, e.getMessage());
        });
    }

    public void initMain() {
        initDate();
        initCombo();
    }

    private void saveCurrency() {
        if (isValidEntry()) {
            userRepo.save(exchange).subscribe((t) -> {
                observer.selected("exchange", t);
                clear();
                sendMessage(t.getTargetCur());
            }, (er) -> {
                JOptionPane.showMessageDialog(this, er.getMessage());
            });
        }
    }

    private void sendMessage(String mes) {
        userRepo.sendDownloadMessage(MessageType.EXRATE, mes)
                .doOnSuccess((t) -> {
                    log.info(t);
                }).subscribe();
    }

    private boolean isValidEntry() {
        if (!ProUtil.isValidDate(txtDate.getDate())) {
            return false;
        } else if (cboHC.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Invalid Home Currency");
            return false;
        } else if (cboTC.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Invalid Target Currency");
            return false;
        } else {
            if (lblStatus.getText().equals("NEW")) {
                ExchangeKey key = new ExchangeKey();
                exchange = new ExchangeRate();
                key.setCompCode(Global.compCode);
                exchange.setKey(key);
                exchange.setCreatedBy(Global.loginUser.getUserCode());
                exchange.setCreatedDate(Util1.getTodayLocalDateTime());
                exchange.setDeleted(false);
            } else {
                exchange.setUpdatedBy(Global.loginUser.getUserCode());
                exchange.setUpdatedDate(Util1.getTodayLocalDateTime());
            }
            if (cboHC.getSelectedItem() instanceof Currency cur) {
                exchange.setHomeCur(cur.getCurCode());
            }
            if (cboTC.getSelectedItem() instanceof Currency cur) {
                exchange.setTargetCur(cur.getCurCode());
            }
            exchange.setExDate(Util1.convertToLocalDateTime(txtDate.getDate()));
            exchange.setHomeFactor(Util1.getDouble(txtHome.getValue()));
            exchange.setTargetFactor(Util1.getDouble(txtTarget.getValue()));

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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btnUpdate = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtDate = new com.toedter.calendar.JDateChooser();
        cboHC = new javax.swing.JComboBox<>();
        cboTC = new javax.swing.JComboBox<>();
        txtHome = new javax.swing.JFormattedTextField();
        txtTarget = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        txtExId = new javax.swing.JTextField();
        btnUpdate1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Currency Exchange");
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel1.setText("Exchange Rate Setup");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE)
                .addContainerGap())
        );

        btnUpdate.setFont(Global.lableFont);
        btnUpdate.setText("Save");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        lblStatus.setFont(Global.amtFont);
        lblStatus.setText("NEW");

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Target Currency");

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Exchange Date");

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Home Currency");

        txtDate.setDateFormatString("dd/MM/yyyy");
        txtDate.setFont(Global.textFont);
        txtDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDateKeyReleased(evt);
            }
        });

        cboHC.setFont(Global.textFont);

        cboTC.setFont(Global.textFont);

        txtHome.setFont(Global.textFont);

        txtTarget.setFont(Global.textFont);

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Exchange Id");

        txtExId.setEditable(false);
        txtExId.setFont(Global.textFont);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDate, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtHome)
                            .addComponent(txtTarget))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboTC, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboHC, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(txtExId))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtExId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cboHC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtHome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cboTC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTarget, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        btnUpdate1.setFont(Global.lableFont);
        btnUpdate1.setText("Clear");
        btnUpdate1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdate1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator3)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator1)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnUpdate1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnUpdate))
                    .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnUpdate))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(277, 277, 277)
                        .addComponent(btnUpdate1)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:

    }//GEN-LAST:event_formComponentShown

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // TODO add your handling code here:
        saveCurrency();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void txtDateKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDateKeyReleased
        // TODO add your handling code here:

    }//GEN-LAST:event_txtDateKeyReleased

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
//        observer.selected("S", "S");
    }//GEN-LAST:event_formWindowClosed

    private void btnUpdate1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdate1ActionPerformed
        // TODO add your handling code here:
        clear();
    }//GEN-LAST:event_btnUpdate1ActionPerformed

    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnUpdate;
    private javax.swing.JButton btnUpdate1;
    private javax.swing.JComboBox<Currency> cboHC;
    private javax.swing.JComboBox<Currency> cboTC;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel lblStatus;
    private com.toedter.calendar.JDateChooser txtDate;
    private javax.swing.JTextField txtExId;
    private javax.swing.JFormattedTextField txtHome;
    private javax.swing.JFormattedTextField txtTarget;
    // End of variables declaration//GEN-END:variables
}
