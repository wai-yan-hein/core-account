/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup.dialog;

import com.common.Global;
import com.common.StartWithRowFilter;
import com.common.TableCellRender;
import com.common.Util1;
import com.inventory.model.MessageType;
import com.inventory.model.StockUnit;
import com.inventory.model.StockUnitKey;
import com.repo.InventoryRepo;
import com.inventory.ui.setup.dialog.common.StockUnitTableModel;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.time.LocalDateTime;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lenovo
 */
public class StockUnitSetupDailog extends javax.swing.JDialog implements KeyListener {

    private static final Logger log = LoggerFactory.getLogger(StockUnitSetupDailog.class);

    private int selectRow = - 1;
    private StockUnit stockUnit = new StockUnit();
    private final StockUnitTableModel stockUnitTableModel = new StockUnitTableModel();
    private InventoryRepo inventoryRepo;
    private TableRowSorter<TableModel> sorter;
    private StartWithRowFilter swrf;
    private List<StockUnit> listStockUnit;

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public List<StockUnit> getListStockUnit() {
        return listStockUnit;
    }

    public void setListStockUnit(List<StockUnit> listStockUnit) {
        this.listStockUnit = listStockUnit;
        stockUnitTableModel.setListUnit(this.listStockUnit);
        txtUnitShort.requestFocus();

    }

    /**
     * Creates new form ItemTypeSetupDialog
     *
     * @param frame
     */
    public StockUnitSetupDailog(JFrame frame) {
        super(frame, true);
        initComponents();
        initKeyListener();
        lblStatus.setForeground(Color.green);
        swrf = new StartWithRowFilter(txtFilter);
    }

    public void initMain() {
        initTable();
    }

    private void initKeyListener() {
        txtUnitShort.addKeyListener(this);
        txtUnitDesp.addKeyListener(this);
        btnClear.addKeyListener(this);
        btnSave.addKeyListener(this);
        tblUnit.addKeyListener(this);
    }

    private void initTable() {
        tblUnit.setModel(stockUnitTableModel);
        sorter = new TableRowSorter<>(tblUnit.getModel());
        tblUnit.setRowSorter(sorter);
        tblUnit.getTableHeader().setFont(Global.lableFont);
        tblUnit.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblUnit.setRowHeight(Global.tblRowHeight);
        tblUnit.setDefaultRenderer(Object.class, new TableCellRender());
        tblUnit.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting()) {
                if (tblUnit.getSelectedRow() >= 0) {
                    selectRow = tblUnit.convertRowIndexToModel(tblUnit.getSelectedRow());
                    setItemUnit(stockUnitTableModel.getStockUnit(selectRow));
                }
            }
        });

    }

    private void setItemUnit(StockUnit unit) {
        stockUnit = unit;
        txtUnitShort.setEditable(false);
        txtUnitShort.setText(stockUnit.getKey().getUnitCode());
        txtUnitDesp.setText(stockUnit.getUnitName());
        lblStatus.setText("EDIT");
        lblStatus.setForeground(Color.blue);
        txtUnitShort.requestFocus();
    }

    private void save() {
        if (isValidEntry()) {
            progress.setIndeterminate(true);
            btnSave.setEnabled(false);
            inventoryRepo.saveStockUnit(stockUnit).subscribe((t) -> {
                if (lblStatus.getText().equals("EDIT")) {
                    listStockUnit.set(selectRow, t);
                } else {
                    listStockUnit.add(t);
                }
                clear();
                sendMessage(t.getUnitName());
            }, (e) -> {
                progress.setIndeterminate(false);
                btnSave.setEnabled(true);
            });

        }
    }

    private void sendMessage(String mes) {
        inventoryRepo.sendDownloadMessage(MessageType.UNIT, mes)
                .doOnSuccess((t) -> {
                    log.info(t);
                }).subscribe();
    }

    private void clear() {
        progress.setIndeterminate(false);
        btnSave.setEnabled(true);
        txtFilter.setText(null);
        txtUnitShort.setText(null);
        txtUnitDesp.setText(null);
        lblStatus.setText("NEW");
        lblStatus.setForeground(Color.green);
        stockUnit = new StockUnit();
        txtUnitShort.setEditable(true);
        stockUnitTableModel.refresh();
        tblUnit.requestFocus();
        txtUnitShort.requestFocus();
    }

    private void delete() {

    }

    private boolean isValidEntry() {
        boolean status = true;
        if (Util1.isNull(txtUnitShort.getText())) {
            status = false;
            JOptionPane.showMessageDialog(this, "Invalid Unit Short.");
            txtUnitShort.requestFocus();
        } else if (Util1.isNull(txtUnitDesp.getText())) {
            status = false;
            JOptionPane.showMessageDialog(this, "Invalid Unit Description.");
            txtUnitDesp.requestFocus();
        } else {
            if (lblStatus.getText().equals("NEW")) {
                stockUnit.setCreatedBy(Global.loginUser.getUserCode());
                stockUnit.setCreatedDate(LocalDateTime.now());
                stockUnit.setMacId(Global.macId);
                stockUnit.setUserCode(Global.loginUser.getUserCode());
            } else {
                stockUnit.setUpdatedBy(Global.loginUser.getUserCode());
            }
            StockUnitKey key = new StockUnitKey();
            key.setCompCode(Global.compCode);
            key.setUnitCode(txtUnitShort.getText());
            stockUnit.setKey(key);
            stockUnit.setDeptId(Global.deptId);
            stockUnit.setUnitName(txtUnitDesp.getText());
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
        tblUnit = new javax.swing.JTable();
        txtFilter = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtUnitShort = new javax.swing.JTextField();
        btnSave = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtUnitDesp = new javax.swing.JTextField();
        progress = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Stock Unit Setup");

        tblUnit.setFont(Global.textFont);
        tblUnit.setModel(new javax.swing.table.DefaultTableModel(
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
        tblUnit.setName("tblUnit"); // NOI18N
        jScrollPane1.setViewportView(tblUnit);

        txtFilter.setName("txtFilter"); // NOI18N
        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Unit Short");

        txtUnitShort.setFont(Global.textFont);
        txtUnitShort.setName("txtUnitShort"); // NOI18N
        txtUnitShort.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtUnitShortFocusGained(evt);
            }
        });
        txtUnitShort.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUnitShortActionPerformed(evt);
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

        btnClear.setFont(Global.lableFont);
        btnClear.setText("Clear");
        btnClear.setName("btnClear"); // NOI18N
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        lblStatus.setFont(Global.lableFont);
        lblStatus.setText("NEW");

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Unit Desp");

        txtUnitDesp.setFont(Global.textFont);
        txtUnitDesp.setName("txtUnitDesp"); // NOI18N
        txtUnitDesp.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtUnitDespFocusGained(evt);
            }
        });
        txtUnitDesp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUnitDespActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtUnitShort, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 79, Short.MAX_VALUE)
                        .addComponent(btnSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnClear))
                    .addComponent(txtUnitDesp, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtUnitShort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtUnitDesp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblStatus)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnClear)
                        .addComponent(btnSave)))
                .addContainerGap(273, Short.MAX_VALUE))
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
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtFilter, javax.swing.GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(progress, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 337, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtUnitShortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUnitShortActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUnitShortActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        try {
            save();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            log.error("Save StockUnit :" + e.getMessage());
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        // TODO add your handling code here:
        clear();
    }//GEN-LAST:event_btnClearActionPerformed

    private void txtFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyReleased
        // TODO add your handling code here:
        if (txtFilter.getText().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(swrf);
        }
    }//GEN-LAST:event_txtFilterKeyReleased

    private void txtUnitDespActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUnitDespActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUnitDespActionPerformed

    private void txtUnitShortFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtUnitShortFocusGained
        // TODO add your handling code here:
        txtUnitShort.selectAll();
    }//GEN-LAST:event_txtUnitShortFocusGained

    private void txtUnitDespFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtUnitDespFocusGained
        // TODO add your handling code here:
        txtUnitDesp.selectAll();
    }//GEN-LAST:event_txtUnitDespFocusGained

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnSave;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JProgressBar progress;
    private javax.swing.JTable tblUnit;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JTextField txtUnitDesp;
    private javax.swing.JTextField txtUnitShort;
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

        if (sourceObj instanceof JTable jTable) {
            ctrlName = jTable.getName();
        } else if (sourceObj instanceof JTextField jTextField) {
            ctrlName = jTextField.getName();
        } else if (sourceObj instanceof JButton jButton) {
            ctrlName = jButton.getName();
        }
        switch (ctrlName) {
            case "txtUnitShort" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtUnitDesp.requestFocus();
                }
            }
            case "txtUnitDesp" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnSave.requestFocus();
                }
            }
            case "btnSave" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtUnitShort.requestFocus();
                }
            }
            case "btnDelete" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnClear.requestFocus();
                }
            }
            case "btnClear" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtUnitShort.requestFocus();
                }
            }
        }
    }
}
