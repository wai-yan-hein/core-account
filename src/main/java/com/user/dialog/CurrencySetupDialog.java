/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.user.dialog;

import com.common.Global;
import com.common.TableCellRender;
import com.common.Util1;
import com.inventory.entity.MessageType;
import com.user.model.Currency;
import com.user.common.CurrencyTabelModel;
import com.repo.UserRepo;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class CurrencySetupDialog extends javax.swing.JDialog implements KeyListener {

    private int selectRow = -1;
    private Currency currency = new Currency();
    private final CurrencyTabelModel currencyTabelModel = new CurrencyTabelModel();
    private UserRepo userRepo;

    public UserRepo getUserRepo() {
        return userRepo;
    }

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Creates new form CurrencySetup
     *
     * @param frame
     */
    public CurrencySetupDialog(JFrame frame) {
        super(frame, true);
        initComponents();
        initKeyListener();
        initFocusAdapter();
    }

    public void initMain() {
        initTable();
    }

    private void initFocusAdapter() {
        txtCurrCode.addFocusListener(fa);
        txtCurrName.addFocusListener(fa);
        txtCurrSymbol.addFocusListener(fa);
    }
    private FocusAdapter fa = new FocusAdapter() {
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

    public void searchCurrency() {
        userRepo.getCurrency().subscribe((t) -> {
            currencyTabelModel.setListCurrency(t);
            txtCurrCode.requestFocus();
        }, (e) -> {
            JOptionPane.showMessageDialog(this, e.getMessage());
        });
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
    }

    private void saveCurrency() {
        if (isValidCurrency()) {
            progress.setIndeterminate(true);
            btnSave.setEnabled(false);
            userRepo.saveCurrency(currency).subscribe((t) -> {
                if (lblStatus.getText().equals("NEW")) {
                    currencyTabelModel.addCurrency(t);
                } else {
                    currencyTabelModel.setCurrency(selectRow, currency);
                }
                clear();
                sendMessage(t.getCurrencyName());
            }, (e) -> {
                progress.setIndeterminate(false);
                btnSave.setEnabled(true);
                JOptionPane.showMessageDialog(this, e.getMessage());
            });
        }
    }

    private void sendMessage(String mes) {
        userRepo.sendDownloadMessage(MessageType.CURRENCY, mes)
                .doOnSuccess((t) -> {
                    log.info(t);
                }).subscribe();
    }

    private void setCurrency(Currency currency) {
        txtCurrCode.setText(currency.getCurCode());
        txtCurrName.setText(currency.getCurrencyName());
        txtCurrSymbol.setText(currency.getCurrencySymbol());
        chkActive.setSelected(currency.isActive());
        lblStatus.setText("EDIT");
    }

    public void clear() {
        progress.setIndeterminate(false);
        btnSave.setEnabled(true);
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

    private boolean isValidCurrency() {
        String curCode = txtCurrCode.getText();
        String curName = txtCurrName.getText();
        if (Util1.isNullOrEmpty(curCode)) {
            JOptionPane.showMessageDialog(this, "Invalid currency code.");
            return false;
        } else if (Util1.isNullOrEmpty(curName)) {
            JOptionPane.showMessageDialog(this, "Invalid currency name.");
            return false;
        } else {
            currency.setCurCode(curCode);
            currency.setCurrencyName(curName);
            currency.setActive(chkActive.isSelected());
            currency.setCurrencySymbol(txtCurrSymbol.getText());
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
        progress = new javax.swing.JProgressBar();

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

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
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
        chkActive.setSelected(true);
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
                        .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(110, 110, 110)
                                .addComponent(btnSave)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnClear))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(chkActive)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE))
                        .addGap(12, 12, 12)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCurrName)
                            .addComponent(txtCurrCode)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtCurrSymbol)))
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(progress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE)
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
        saveCurrency();
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
    private javax.swing.JProgressBar progress;
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
