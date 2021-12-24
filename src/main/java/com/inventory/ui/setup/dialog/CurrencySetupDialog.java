/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup.dialog;

import com.inventory.common.Global;
import com.inventory.common.TableCellRender;
import com.inventory.common.Util1;
import com.inventory.model.Currency;
import com.inventory.ui.setup.common.CurrencyTabelModel;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 *
 * @author Lenovo
 */
@Component
@Slf4j
public class CurrencySetupDialog extends javax.swing.JDialog implements KeyListener {

    private int selectRow = -1;
    private Currency currency;
    @Autowired
    private CurrencyTabelModel currencyTabelModel;
    @Autowired
    private WebClient webClient;
    boolean status = true;

    /**
     * Creates new form CurrencySetup
     */
    public CurrencySetupDialog() {
        initComponents();
        initKeyListener();
    }

    public void initMain() {
        initTable();
        txtCurrCode.requestFocus();
    }

    private void initTable() {
        tblCurrency.setModel(currencyTabelModel);
        tblCurrency.getTableHeader().setFont(Global.textFont);
        tblCurrency.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCurrency.getColumnModel().getColumn(0).setPreferredWidth(20);// Code
        tblCurrency.getColumnModel().getColumn(1).setPreferredWidth(100);// Name
        tblCurrency.getColumnModel().getColumn(2).setPreferredWidth(15);// Symbol      
        tblCurrency.getColumnModel().getColumn(3).setPreferredWidth(10);// Symbol  
        tblCurrency.setDefaultRenderer(Boolean.class, new TableCellRender());
        tblCurrency.setDefaultRenderer(Object.class, new TableCellRender());
        tblCurrency.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting()) {
                if (tblCurrency.getSelectedRow() >= 0) {
                    selectRow = tblCurrency.convertRowIndexToModel(tblCurrency.getSelectedRow());

                    if (selectRow >= 0) {
                        Currency c = currencyTabelModel.getCurrency(selectRow);
                        setCurrency(c);
                    }
                }

            }
        });
        tblCurrency.setRowHeight(Global.tblRowHeight);
        tblCurrency.setDefaultRenderer(Object.class, new TableCellRender());
        currencyTabelModel.setListCurrency(Global.listCurrency);
    }

    private void saveCurrency() {
        currency = new Currency();
        currency.setCurCode(txtCurrCode.getText());
        currency.setActive(chkActive.isSelected());
        currency.setCurrencyName(txtCurrName.getText());
        currency.setCurrencySymbol(txtCurrSymbol.getText());
        if (isValidCurrency(currency, lblStatus.getText())) {
            Mono<Currency> result = webClient.post()
                    .uri("/setup/save-currency")
                    .body(Mono.just(currency), Currency.class)
                    .retrieve()
                    .bodyToMono(Currency.class);
            result.subscribe((t) -> {
                if (t != null) {
                    if (lblStatus.getText().equals("EDIT")) {
                        Global.listCurrency.set(selectRow, t);
                    } else {
                        Global.listCurrency.add(t);
                    }
                    clear();
                    JOptionPane.showMessageDialog(this, "Saved");
                }
            }, (e) -> {
                JOptionPane.showMessageDialog(this, e.getMessage());
            });

        }
    }

    private void setCurrency(Currency currency) {
        txtCurrCode.setText(currency.getCurCode());
        txtCurrName.setText(currency.getCurrencyName());
        txtCurrSymbol.setText(currency.getCurrencySymbol());
        chkActive.setSelected(currency.getActive());
        lblStatus.setText("EDIT");
    }

    public void clear() {
        txtCurrCode.setText(null);
        txtCurrName.setText(null);
        txtCurrSymbol.setText(null);
        chkActive.setSelected(Boolean.TRUE);
        lblStatus.setText("NEW");
        txtCurrCode.requestFocus();
        currencyTabelModel.fireTableDataChanged();
    }

    private void initKeyListener() {
        txtCurrCode.addKeyListener(this);
        txtCurrName.addKeyListener(this);
        txtCurrSymbol.addKeyListener(this);
        chkActive.addKeyListener(this);
        btnSave.addKeyListener(this);
        btnClear.addKeyListener(this);
        tblCurrency.addKeyListener(this);
    }

    private boolean isValidCurrency(Currency cur, String editStatus) {
        if (Util1.isNull(cur.getCurCode())) {
            status = false;
            JOptionPane.showMessageDialog(this, "Invalid currency code.");
        } else if (Util1.isNull(cur.getCurrencyName(), "-").equals("-")) {
            status = false;
            JOptionPane.showMessageDialog(this, "Invalid currency name.");
        }
        if (status) {
            if (editStatus.equals("NEW")) {
                cur.setCreatedBy(Global.loginUser.getAppUserCode());
                cur.setCreatedDt(Util1.getTodayDate());
            } else {
                cur.setUpdatedBy(Global.loginUser.getAppUserCode());
            }
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
        tblCurrency = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtCurrCode = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtCurrName = new javax.swing.JTextField();
        txtCurrSymbol = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        chkActive = new javax.swing.JCheckBox();
        btnClear = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Currency Setup");

        tblCurrency.setFont(Global.textFont);
        tblCurrency.setModel(new javax.swing.table.DefaultTableModel(
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
        tblCurrency.setName("tblCurrency"); // NOI18N
        tblCurrency.setRowHeight(Global.tblRowHeight);
        jScrollPane1.setViewportView(tblCurrency);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jPanel1.setFont(Global.textFont);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Code");

        txtCurrCode.setFont(Global.textFont);
        txtCurrCode.setName("txtCurrCode"); // NOI18N

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Name");

        txtCurrName.setFont(Global.textFont);
        txtCurrName.setName("txtCurrName"); // NOI18N

        txtCurrSymbol.setFont(Global.textFont);
        txtCurrSymbol.setName("txtCurrSymbol"); // NOI18N

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Symbol");

        chkActive.setFont(Global.lableFont);
        chkActive.setText("Active");
        chkActive.setName("chkActive"); // NOI18N

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
        btnSave.setText("Save");
        btnSave.setName("btnSave"); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        lblStatus.setFont(Global.lableFont);
        lblStatus.setText("NEW");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtCurrCode))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtCurrName))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE)
                            .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(110, 110, 110)
                                .addComponent(btnSave)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnClear))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtCurrSymbol)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(chkActive)
                                        .addGap(0, 0, Short.MAX_VALUE))))))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtCurrCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtCurrName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtCurrSymbol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkActive)
                    .addComponent(lblStatus))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnClear)
                    .addComponent(btnSave))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE))
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
        try {
            saveCurrency();
        } catch (Exception e) {
            log.error("Save Currency :" + e.getMessage());
            JOptionPane.showMessageDialog(this, e.getMessage(), "Save Currency", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox chkActive;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblCurrency;
    private javax.swing.JTextField txtCurrCode;
    private javax.swing.JTextField txtCurrName;
    private javax.swing.JTextField txtCurrSymbol;
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

        if (sourceObj instanceof JComboBox) {
            ctrlName = ((JComboBox) sourceObj).getName();
        } else if (sourceObj instanceof JCheckBox) {
            ctrlName = ((JCheckBox) sourceObj).getName();
        } else if (sourceObj instanceof JTextField) {
            ctrlName = ((JTextField) sourceObj).getName();
        } else if (sourceObj instanceof JButton) {
            ctrlName = ((JButton) sourceObj).getName();
        } else if (sourceObj instanceof JTable) {
            ctrlName = ((JTable) sourceObj).getName();
        }
        switch (ctrlName) {
            case "txtCurrCode":
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtCurrName.requestFocus();
                }
                break;
            case "txtCurrName":
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtCurrSymbol.requestFocus();
                }
                break;
            case "txtCurrSymbol":
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    chkActive.requestFocus();
                }
                break;
            case "chkActive":
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnSave.requestFocus();
                }
                break;
            case "btnSave":
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnClear.requestFocus();
                }
                break;
            case "btnClear":
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtCurrCode.requestFocus();
                }
                break;
            case "tblCurrency":
                if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP) {
                    selectRow = tblCurrency.convertRowIndexToModel(tblCurrency.getSelectedRow());
                    Currency curr = currencyTabelModel.getCurrency(selectRow);
                    setCurrency(curr);
                }
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtCurrCode.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtCurrCode.requestFocus();
                }

                break;
        }
    }
}
