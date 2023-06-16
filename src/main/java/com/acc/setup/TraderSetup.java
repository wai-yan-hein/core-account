/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.setup;

import com.acc.common.AccountRepo;
import com.acc.common.TraderATableModel;
import com.acc.editor.COA3AutoCompleter;
import com.acc.model.TraderA;
import com.acc.model.TraderAKey;
import com.common.Global;
import com.common.PanelControl;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.common.Util1;
import com.inventory.ui.setup.dialog.CustomerImportDialog;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.time.LocalDateTime;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.JTextComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Slf4j
@Component
public class TraderSetup extends javax.swing.JPanel implements KeyListener, PanelControl {

    private int selectRow = -1;
    private TraderA trader = new TraderA();
    private final TraderATableModel traderATableModel = new TraderATableModel();
    private COA3AutoCompleter cOAAutoCompleter;
    @Autowired
    private AccountRepo accountRepo;
    private SelectionObserver observer;
    private JProgressBar progress;
    private TableRowSorter<TableModel> sorter;

    public JProgressBar getProgress() {
        return progress;
    }

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }

    public SelectionObserver getObserver() {
        return observer;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    /**
     * Creates new form CustomerSetup
     */
    public TraderSetup() {
        initComponents();
        initKeyListener();
    }
    private final FocusAdapter fa = new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            if (e.getSource() instanceof JTextField txt) {
                txt.selectAll();
            }
        }

    };

    private void batchLock(boolean lock) {
        txtSysCode.setEnabled(lock);
        txtCusCode.setEnabled(lock);
        txtCusName.setEnabled(lock);
        txtCusEmail.setEnabled(lock);
        txtCusPhone.setEnabled(lock);
        txtCusAddress.setEnabled(lock);
        txtAccount.setEnabled(lock);
        chkActive.setEnabled(lock);
        observer.selected("save", lock);
        observer.selected("delete", lock);
    }

    public void initMain() {
        batchLock(!Global.batchLock);
        initCombo();
        initTable();
        searchTrader();
    }

    private void initCombo() {
        cOAAutoCompleter = new COA3AutoCompleter(txtAccount, accountRepo, null, false, 3);
        cOAAutoCompleter.setCoa(null);
    }

    private void initTable() {
        tblCustomer.setModel(traderATableModel);
        tblCustomer.getTableHeader().setFont(Global.textFont);
        tblCustomer.getColumnModel().getColumn(0).setPreferredWidth(1);// Code
        tblCustomer.getColumnModel().getColumn(1).setPreferredWidth(10);// Code
        tblCustomer.getColumnModel().getColumn(2).setPreferredWidth(400);// Name
        tblCustomer.setDefaultRenderer(Boolean.class, new TableCellRender());
        tblCustomer.setDefaultRenderer(Object.class, new TableCellRender());
        tblCustomer.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting()) {
                if (tblCustomer.getSelectedRow() >= 0) {
                    selectRow = tblCustomer.convertRowIndexToModel(tblCustomer.getSelectedRow());
                    setTrader(traderATableModel.getTrader(selectRow));
                }
            }
        });
        sorter = new TableRowSorter(tblCustomer.getModel());
        tblCustomer.setRowSorter(sorter);
    }

    private void searchTrader() {
        progress.setIndeterminate(true);
        traderATableModel.clear();
        accountRepo.getTrader()
                .collectList()
                .subscribe((t) -> {
                    traderATableModel.setListTrader(t);
                    lblRecord.setText(String.valueOf(traderATableModel.getListTrader().size() + ""));
                    progress.setIndeterminate(false);
                }, (e) -> {
                    JOptionPane.showMessageDialog(this, e.getMessage());
                    progress.setIndeterminate(false);
                });

    }

    private void setTrader(TraderA cus) {
        trader = cus;
        txtSysCode.setText(trader.getKey().getCode());
        txtCusCode.setText(trader.getUserCode());
        txtCusName.setText(trader.getTraderName());
        txtCusEmail.setText(trader.getEmail());
        txtCusPhone.setText(trader.getPhone());
        txtCusAddress.setText(trader.getAddress());
        chkActive.setSelected(trader.isActive());
        txtCusName.requestFocus();
        lblStatus.setText("EDIT");
        accountRepo.findCOA(trader.getAccount()).subscribe((coa) -> {
            cOAAutoCompleter.setCoa(coa);
        });
    }

    private boolean isValidEntry() {
        boolean status = true;
        if (txtCusName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(Global.parentForm, "Trader Name can't be empty");
            txtCusName.requestFocus();
            status = false;
        } else if (cOAAutoCompleter.getCOA() == null) {
            JOptionPane.showMessageDialog(Global.parentForm, "Chart of account can't be empty");
            txtAccount.requestFocus();
            status = false;
        } else {
            trader.setUserCode(txtCusCode.getText());
            trader.setTraderName(txtCusName.getText());
            trader.setPhone(txtCusPhone.getText());
            trader.setEmail(txtCusEmail.getText());
            trader.setAddress(txtCusAddress.getText());
            trader.setActive(chkActive.isSelected());
            trader.setTraderType("T");
            trader.setAccount(cOAAutoCompleter.getCOA().getKey().getCoaCode());
            if (lblStatus.getText().equals("NEW")) {
                trader.setMacId(Global.macId);
                trader.setCreatedBy(Global.loginUser.getUserCode());
                trader.setCreatedDate(LocalDateTime.now());
                TraderAKey key = new TraderAKey();
                key.setCompCode(Global.compCode);
                key.setCode(null);
                trader.setKey(key);
            } else {
                trader.setUpdatedUser(Global.loginUser.getUserCode());//**pann edit upadated by
            }
        }
        return status;
    }

    private void saveTrader() {
        if (isValidEntry()) {
            accountRepo.saveTrader(trader).subscribe((t) -> {
                if (!Util1.isNull(t.getKey().getCode())) {
                    if (lblStatus.getText().equals("EDIT")) {
                        traderATableModel.setTrader(selectRow, t);
                    } else {
                        traderATableModel.addTrader(t);
                    }
                    clear();
                }
            });

        }
    }

    public void clear() {
        trader = new TraderA();
        txtSysCode.setText(null);
        txtCusCode.setText(null);
        txtCusName.setText(null);
        txtCusEmail.setText(null);
        txtCusPhone.setText(null);
        txtCusAddress.setText(null);
        chkActive.setSelected(true);
        lblStatus.setText("NEW");
        txtCusCode.requestFocus();
        lblRecord.setText(String.valueOf(traderATableModel.getListTrader().size()));
        cOAAutoCompleter.setCoa(null);
    }
    private final RowFilter<Object, Object> startsWithFilter = new RowFilter<Object, Object>() {
        @Override
        public boolean include(RowFilter.Entry<? extends Object, ? extends Object> entry) {
            String tmp1 = entry.getStringValue(0).toUpperCase().replace(" ", "");
            String tmp2 = entry.getStringValue(1).toUpperCase().replace(" ", "");
            String text = txtFilter.getText().toUpperCase().replace(" ", "");
            return tmp1.startsWith(text) || tmp2.startsWith(text);
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

        panelEntry = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtCusCode = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtCusName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtCusPhone = new javax.swing.JTextField();
        txtCusEmail = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtCusAddress = new javax.swing.JTextField();
        chkActive = new javax.swing.JCheckBox();
        lblStatus = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        txtSysCode = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        lblGroup1 = new javax.swing.JLabel();
        txtAccount = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblCustomer = new javax.swing.JTable();
        jLabel6 = new javax.swing.JLabel();
        lblRecord = new javax.swing.JLabel();
        txtFilter = new javax.swing.JTextField();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        panelEntry.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("User Code");

        txtCusCode.setFont(Global.textFont);
        txtCusCode.setName("txtCusCode"); // NOI18N
        txtCusCode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCusCodeKeyReleased(evt);
            }
        });

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Name");

        txtCusName.setFont(Global.textFont);
        txtCusName.setName("txtCusName"); // NOI18N

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Phone");

        txtCusPhone.setFont(Global.textFont);
        txtCusPhone.setName("txtCusPhone"); // NOI18N

        txtCusEmail.setFont(Global.textFont);
        txtCusEmail.setName("txtCusEmail"); // NOI18N

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Email");

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Address");

        txtCusAddress.setFont(Global.textFont);
        txtCusAddress.setName("txtCusAddress"); // NOI18N
        txtCusAddress.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCusAddressKeyReleased(evt);
            }
        });

        chkActive.setFont(Global.lableFont);
        chkActive.setSelected(true);
        chkActive.setText("Active");
        chkActive.setName("chkActive"); // NOI18N

        lblStatus.setFont(Global.lableFont);
        lblStatus.setText("NEW");

        jButton1.setBackground(Global.selectionColor);
        jButton1.setFont(Global.lableFont);
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Import");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Sys Code");

        txtSysCode.setEditable(false);
        txtSysCode.setFont(Global.textFont);
        txtSysCode.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtSysCode.setName("txtCusCode"); // NOI18N
        txtSysCode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSysCodeKeyReleased(evt);
            }
        });

        lblGroup1.setFont(Global.lableFont);
        lblGroup1.setText("Account");

        txtAccount.setFont(Global.textFont);
        txtAccount.setName("txtCreditTerm"); // NOI18N

        javax.swing.GroupLayout panelEntryLayout = new javax.swing.GroupLayout(panelEntry);
        panelEntry.setLayout(panelEntryLayout);
        panelEntryLayout.setHorizontalGroup(
            panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelEntryLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator1)
                    .addGroup(panelEntryLayout.createSequentialGroup()
                        .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lblStatus, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblGroup1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCusEmail)
                            .addComponent(txtCusPhone)
                            .addComponent(txtCusName)
                            .addComponent(txtCusCode, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtCusAddress)
                            .addComponent(txtSysCode, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(chkActive, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtAccount)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelEntryLayout.createSequentialGroup()
                                .addGap(0, 191, Short.MAX_VALUE)
                                .addComponent(jButton1)))))
                .addContainerGap())
        );
        panelEntryLayout.setVerticalGroup(
            panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEntryLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtSysCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtCusCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtCusName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtCusPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtCusEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtCusAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblGroup1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtAccount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkActive)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStatus)
                    .addComponent(jButton1))
                .addContainerGap(300, Short.MAX_VALUE))
        );

        panelEntryLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtCusAddress, txtCusCode, txtCusEmail, txtCusName, txtCusPhone});

        panelEntryLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jLabel10, jLabel2, jLabel3, jLabel4, jLabel5, lblGroup1});

        tblCustomer.setAutoCreateRowSorter(true);
        tblCustomer.setFont(Global.textFont);
        tblCustomer.setModel(new javax.swing.table.DefaultTableModel(
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
        tblCustomer.setName("tblCustomer"); // NOI18N
        tblCustomer.setRowHeight(Global.tblRowHeight);
        tblCustomer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblCustomerKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(tblCustomer);

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Record :");

        lblRecord.setFont(Global.lableFont);
        lblRecord.setText("0");

        txtFilter.setFont(Global.textFont);
        txtFilter.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtFilterFocusGained(evt);
            }
        });
        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblRecord, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(txtFilter))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelEntry, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelEntry, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(lblRecord))
                        .addGap(1, 1, 1)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtCusCodeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCusCodeKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCusCodeKeyReleased

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observer.selected("control", this);
    }//GEN-LAST:event_formComponentShown

    private void tblCustomerKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblCustomerKeyReleased
        // TODO add your handling code here:

    }//GEN-LAST:event_tblCustomerKeyReleased

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        CustomerImportDialog dialog = new CustomerImportDialog(Global.parentForm);
        dialog.setAccountRepo(accountRepo);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);

    }//GEN-LAST:event_jButton1ActionPerformed

    private void txtCusAddressKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCusAddressKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCusAddressKeyReleased

    private void txtSysCodeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSysCodeKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSysCodeKeyReleased

    private void txtFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyReleased
        // TODO add your handling code here:
        String filter = txtFilter.getText();
        if (filter.length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(startsWithFilter);
        }
    }//GEN-LAST:event_txtFilterKeyReleased

    private void txtFilterFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFilterFocusGained
        // TODO add your handling code here:
        txtFilter.selectAll();
    }//GEN-LAST:event_txtFilterFocusGained

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkActive;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblGroup1;
    private javax.swing.JLabel lblRecord;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPanel panelEntry;
    private javax.swing.JTable tblCustomer;
    private javax.swing.JTextField txtAccount;
    private javax.swing.JTextField txtCusAddress;
    private javax.swing.JTextField txtCusCode;
    private javax.swing.JTextField txtCusEmail;
    private javax.swing.JTextField txtCusName;
    private javax.swing.JTextField txtCusPhone;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JTextField txtSysCode;
    // End of variables declaration//GEN-END:variables

    private void initKeyListener() {
        txtCusCode.addKeyListener(this);
        txtCusName.addKeyListener(this);
        txtCusPhone.addKeyListener(this);
        txtCusAddress.addKeyListener(this);
        txtCusEmail.addKeyListener(this);
        chkActive.addKeyListener(this);
        tblCustomer.addKeyListener(this);
        txtCusCode.addFocusListener(fa);
        txtCusName.addFocusListener(fa);
        txtCusPhone.addFocusListener(fa);
        txtCusAddress.addFocusListener(fa);
        txtCusEmail.addFocusListener(fa);
        chkActive.addFocusListener(fa);
        tblCustomer.addFocusListener(fa);

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

    }

    @Override
    public void keyReleased(KeyEvent e) {
        Object sourceObj = e.getSource();
        String ctrlName = "-";

        if (sourceObj instanceof JComboBox) {
            ctrlName = ((JComboBox) sourceObj).getName();
        } else if (sourceObj instanceof JFormattedTextField) {
            ctrlName = ((JFormattedTextField) sourceObj).getName();
        } else if (sourceObj instanceof JTextField) {
            ctrlName = ((JTextField) sourceObj).getName();
        } else if (sourceObj instanceof JCheckBox) {
            ctrlName = ((JCheckBox) sourceObj).getName();
        } else if (sourceObj instanceof JButton) {
            ctrlName = ((JButton) sourceObj).getName();
        } else if (sourceObj instanceof JTable) {
            ctrlName = ((JTable) sourceObj).getName();
        } else if (sourceObj instanceof JTable) {
            ctrlName = ((JTable) sourceObj).getName();
        } else if (sourceObj instanceof JTextComponent) {
            ctrlName = ((JTextComponent) sourceObj).getName();
        }
        switch (ctrlName) {
            case "txtCusCode":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtCusName.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                }
                break;
            case "txtConPerson":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtCusPhone.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtCusName.requestFocus();

                }
                tabToTable(e);
                break;
            case "cboRegion":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtCusAddress.requestFocus();
                        break;
                    /*case KeyEvent.VK_UP:
                        txtCusEmail.requestFocus();
                        break;
                     */
                    case KeyEvent.VK_RIGHT:
                        txtCusAddress.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtCusEmail.requestFocus();
                        break;
                }
                tabToTable(e);

                break;
            case "cboAccount":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        break;
                    /*case KeyEvent.VK_UP:
                        txtCusAddress.requestFocus();
                        break;
                     */
                    case KeyEvent.VK_RIGHT:
                        break;
                    case KeyEvent.VK_LEFT:
                        txtCusAddress.requestFocus();
                        break;
                }
                tabToTable(e);

                break;
            case "btnSave":
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    chkActive.requestFocus();
                }
                tabToTable(e);

                break;
            case "btnClear":
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtCusName.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                }
                tabToTable(e);

                break;
            case "tblCustomer":
                if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    selectRow = tblCustomer.convertRowIndexToModel(tblCustomer.getSelectedRow());
                    setTrader(traderATableModel.getTrader(selectRow));
                }

                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtCusName.requestFocus();
                }
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtCusName.requestFocus();
                }
                break;
            default:
                break;
        }
    }

    private void tabToTable(KeyEvent e) {
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
            tblCustomer.requestFocus();
            if (tblCustomer.getRowCount() >= 0) {
                tblCustomer.setRowSelectionInterval(0, 0);
            }
        }
    }

    @Override
    public void save() {
        saveTrader();
    }

    @Override
    public void delete() {
        if (selectRow >= 0) {
            TraderA t = traderATableModel.getTrader(selectRow);
            List<String> str = accountRepo.deleteTrader(t.getKey());
            if (str.isEmpty()) {
                traderATableModel.deleteTrader(selectRow);
                clear();
                JOptionPane.showMessageDialog(this, "Deleted.");
            } else {
                JOptionPane.showMessageDialog(this, str);
            }
        }
    }

    @Override
    public void newForm() {
        clear();
    }

    @Override
    public void history() {
    }

    @Override
    public void print() {
    }

    @Override
    public void refresh() {
        searchTrader();
    }

    @Override
    public void filter() {
    }

    @Override
    public String panelName() {
        return this.getName();
    }

}
