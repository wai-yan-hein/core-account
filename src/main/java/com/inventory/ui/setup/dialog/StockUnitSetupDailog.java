/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup.dialog;

import com.inventory.common.Global;
import com.inventory.common.StartWithRowFilter;
import com.inventory.common.TableCellRender;
import com.inventory.common.Util1;
import com.inventory.model.StockUnit;
import com.inventory.ui.setup.dialog.common.StockUnitTableModel;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 *
 * @author Lenovo
 */
@Component
public class StockUnitSetupDailog extends javax.swing.JDialog implements KeyListener {

    private static final Logger log = LoggerFactory.getLogger(StockUnitSetupDailog.class);

    private int selectRow = - 1;
    private StockUnit stockUnit = new StockUnit();

    @Autowired
    private StockUnitTableModel stockUnitTableModel;
    @Autowired
    private WebClient webClient;
    private TableRowSorter<TableModel> sorter;
    private StartWithRowFilter swrf;

    /**
     * Creates new form ItemTypeSetupDialog
     */
    public StockUnitSetupDailog() {
        super(Global.parentForm, true);
        initComponents();
        lblStatus.setForeground(Color.green);
    }

    public void initMain() {
        swrf = new StartWithRowFilter(txtFilter);
        initTable();
        initKeyListener();
        searchItemUnit();

    }

    private void initKeyListener() {
        txtUnitShort.addKeyListener(this);
        btnClear.addKeyListener(this);
        btnDelete.addKeyListener(this);
        btnSave.addKeyListener(this);
        tblUnit.addKeyListener(this);
        txtUnitShort.requestFocus();
    }

    private void searchItemUnit() {
        stockUnitTableModel.setListUnit(Global.listStockUnit);
    }

    private void initTable() {
        tblUnit.setModel(stockUnitTableModel);
        sorter = new TableRowSorter<>(tblUnit.getModel());
        tblUnit.setRowSorter(sorter);
        tblUnit.getTableHeader().setFont(Global.lableFont);
        tblUnit.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
        txtUnitShort.setText(stockUnit.getItemUnitCode());
        txtUnitDesp.setText(stockUnit.getItemUnitName());
        lblStatus.setText("EDIT");
        lblStatus.setForeground(Color.blue);
        txtUnitShort.requestFocus();
    }

    private void save() {
        if (isValidEntry()) {
            Mono<StockUnit> result = webClient.post()
                    .uri("/setup/save-unit")
                    .body(Mono.just(stockUnit), StockUnit.class)
                    .retrieve()
                    .bodyToMono(StockUnit.class);
            result.subscribe((t) -> {
                if (t != null) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Saved");
                    if (lblStatus.getText().equals("EDIT")) {
                        Global.listStockUnit.set(selectRow, t);
                    } else {
                        Global.listStockUnit.add(t);
                    }
                    clear();
                }
            }, (e) -> {
                JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
            });
        }
    }

    private void clear() {
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
            JOptionPane.showMessageDialog(Global.parentForm, "Invalid Unit Short.");
            txtUnitShort.requestFocus();
        } else if (Util1.isNull(txtUnitDesp.getText())) {
            status = false;
            JOptionPane.showMessageDialog(Global.parentForm, "Invalid Unit Description.");
            txtUnitDesp.requestFocus();
        } else {
            stockUnit.setItemUnitCode(txtUnitShort.getText());
            stockUnit.setItemUnitName(txtUnitDesp.getText());
            if (lblStatus.getText().equals("NEW")) {
                stockUnit.setCreatedBy(Global.loginUser);
                stockUnit.setCreatedDate(Util1.getTodayDate());
                stockUnit.setMacId(Global.machineId);
                stockUnit.setCompCode(Global.compCode);
                stockUnit.setUserCode(Global.loginUser.getAppUserCode());
            } else {
                stockUnit.setUpdatedBy(Global.loginUser);
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
        tblUnit = new javax.swing.JTable();
        txtFilter = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtUnitShort = new javax.swing.JTextField();
        btnSave = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtUnitDesp = new javax.swing.JTextField();

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

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

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

        btnSave.setFont(Global.lableFont);
        btnSave.setText("Save");
        btnSave.setName("btnSave"); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnDelete.setFont(Global.lableFont);
        btnDelete.setText("Delete");
        btnDelete.setName("btnDelete"); // NOI18N
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
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
        txtUnitDesp.setName("txtName"); // NOI18N
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
                        .addComponent(btnSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDelete)
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
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnSave)
                        .addComponent(lblStatus))
                    .addComponent(btnDelete)
                    .addComponent(btnClear))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFilter)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
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
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
            log.error("Save StockUnit :" + e.getMessage());
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        try {
            delete();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
            log.error("Delete StockUnit :" + e.getMessage());
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

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
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnSave;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
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

        if (sourceObj instanceof JTable) {
            ctrlName = ((JTable) sourceObj).getName();
        } else if (sourceObj instanceof JTextField) {
            ctrlName = ((JTextField) sourceObj).getName();
        } else if (sourceObj instanceof JButton) {
            ctrlName = ((JButton) sourceObj).getName();
        }
        switch (ctrlName) {

            case "txtUnitShort":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtUnitDesp.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    btnClear.requestFocus();
                }
                tabToTable(e);

                break;
            case "txtUnitDesp":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    btnSave.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    btnClear.requestFocus();
                }
                tabToTable(e);

                break;

            case "btnSave":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    btnDelete.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtUnitShort.requestFocus();
                }
                tabToTable(e);

                break;
            case "btnDelete":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    btnClear.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    btnSave.requestFocus();
                }
                tabToTable(e);

                break;
            case "btnClear":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtUnitShort.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    btnDelete.requestFocus();
                }
                tabToTable(e);

                break;
        }
    }

    private void tabToTable(KeyEvent e) {
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
            tblUnit.requestFocus();
            if (tblUnit.getRowCount() >= 0) {
                tblUnit.setRowSelectionInterval(0, 0);
            }
        }
    }
}
