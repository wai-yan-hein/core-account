/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.user.dialog;

import com.common.ComponentUtil;
import com.common.Global;
import com.common.IconUtil;
import com.common.StartWithRowFilter;
import com.common.TableCellRender;
import com.common.Util1;
import com.common.YNOptionPane;
import com.formdev.flatlaf.FlatClientProperties;
import com.inventory.entity.MessageType;
import com.user.model.Currency;
import com.user.common.CurrencyTabelModel;
import com.repo.UserRepo;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClientRequestException;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class CurrencySetupDialog extends javax.swing.JDialog implements KeyListener {

    private int selectRow = -1;
    private Currency currency = new Currency();
    private final CurrencyTabelModel tableModel = new CurrencyTabelModel();
    @Setter
    private UserRepo userRepo;
    private TableRowSorter<TableModel> sorter;
    private StartWithRowFilter tblFilter;

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
        initClientProperty();
    }

    public void initMain() {
        initTable();
    }

    private void initClientProperty() {
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search Here");
        txtSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        txtSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, IconUtil.getIcon(IconUtil.SEARCH_ICON));
    }

    private void initFocusAdapter() {
        ComponentUtil.addFocusListener(this);
    }

    public void searchCurrency() {
        progress.setIndeterminate(true);
        userRepo.getCurrency().doOnSuccess((t) -> {
            tableModel.setListCurrency(t);
        }).doOnTerminate(() -> {
            calRecord();
            progress.setIndeterminate(false);
            txtCurrCode.requestFocus();
        }).subscribe();
        setVisible(true);
    }

    private void initTable() {
        tblCurrency.setModel(tableModel);
        tblCurrency.getTableHeader().setFont(Global.tblHeaderFont);
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
                        Currency c = tableModel.getCurrency(selectRow);
                        setCurrency(c);
                    }
                }

            }
        });
        tblCurrency.setRowHeight(Global.tblRowHeight);
        tblCurrency.setDefaultRenderer(Object.class, new TableCellRender());
        sorter = new TableRowSorter<>(tblCurrency.getModel());
        tblFilter = new StartWithRowFilter(txtSearch);
        tblCurrency.setRowSorter(sorter);
    }

    private void calRecord() {
        lblRecord.setText(String.valueOf(tableModel.getListCurrency().size()));
    }

    private void saveCurrency() {
        if (isValidCurrency()) {
            progress.setIndeterminate(true);
            btnSave.setEnabled(false);
            userRepo.saveCurrency(currency).doOnSuccess((t) -> {
                if (lblStatus.getText().equals("NEW")) {
                    tableModel.addCurrency(t);
                } else {
                    tableModel.setCurrency(selectRow, t);
                }
                clear();
                sendMessage(t.getCurrencyName());
            }).doOnError((e) -> {
                progress.setIndeterminate(false);
                btnSave.setEnabled(true);
                if (e instanceof WebClientRequestException) {
                    int yn = JOptionPane.showConfirmDialog(this, "Internet Offline. Try Again?", "Offline", JOptionPane.YES_OPTION, JOptionPane.ERROR_MESSAGE);
                    if (yn == JOptionPane.YES_OPTION) {
                        saveCurrency();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Error : " + e.getMessage(), "Server Error", JOptionPane.ERROR_MESSAGE);
                }
            }).subscribe();
        }
    }

    private void sendMessage(String mes) {
        userRepo.sendDownloadMessage(MessageType.CURRENCY, mes)
                .doOnSuccess((t) -> {
                    log.info(t);
                }).subscribe();
    }

    private void setCurrency(Currency currency) {
        txtCurrCode.setEditable(false);
        txtCurrCode.setText(currency.getCurCode());
        txtCurrName.setText(currency.getCurrencyName());
        txtCurrSymbol.setText(currency.getCurrencySymbol());
        chkActive.setSelected(currency.isActive());
        lblStatus.setText("EDIT");
    }

    public void clear() {
        calRecord();
        progress.setIndeterminate(false);
        btnSave.setEnabled(true);
        txtCurrCode.setEditable(true);
        txtCurrCode.setText(null);
        txtCurrName.setText(null);
        txtCurrSymbol.setText(null);
        chkActive.setSelected(Boolean.TRUE);
        lblStatus.setText("NEW");
        txtCurrCode.requestFocus();
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

    private void importCurrency() {
        YNOptionPane pane = new YNOptionPane("Are you sure to import currency?", JOptionPane.ERROR_MESSAGE);
        JDialog dialog = pane.createDialog("Edit");
        dialog.setVisible(true);
        int yn = (int) pane.getValue();
        if (yn == JOptionPane.YES_OPTION) {
            userRepo.importCurrency().doOnSuccess((t) -> {
                if (t) {
                    JOptionPane.showMessageDialog(this, "Import Success.");
                    searchCurrency();
                }
            }).subscribe();
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
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        lblRecord = new javax.swing.JLabel();
        progress = new javax.swing.JProgressBar();
        txtSearch = new javax.swing.JTextField();

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

        jButton1.setText("Import");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Records :");

        lblRecord.setFont(Global.lableFont);
        lblRecord.setText("0");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE))
                        .addGap(12, 12, 12)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCurrName)
                            .addComponent(txtCurrCode)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblRecord, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnClear))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(chkActive)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(txtCurrSymbol))))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkActive)
                    .addComponent(lblStatus))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnClear)
                        .addComponent(btnSave)
                        .addComponent(lblRecord, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 176, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addContainerGap())
        );

        txtSearch.setFont(Global.textFont);
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(progress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE)
                            .addComponent(txtSearch))
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
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

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        importCurrency();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        // TODO add your handling code here:
        if (txtSearch.getText().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(tblFilter);
        }
    }//GEN-LAST:event_txtSearchKeyReleased

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox chkActive;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblRecord;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JProgressBar progress;
    private javax.swing.JTable tblCurrency;
    private javax.swing.JTextField txtCurrCode;
    private javax.swing.JTextField txtCurrName;
    private javax.swing.JTextField txtCurrSymbol;
    private javax.swing.JTextField txtSearch;
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
                    Currency curr = tableModel.getCurrency(selectRow);
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
