/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup.dialog;

import com.common.Global;
import com.common.StartWithRowFilter;
import com.common.TableCellRender;
import com.inventory.model.MessageType;
import com.inventory.model.StockBrand;
import com.inventory.model.StockBrandKey;
import com.repo.InventoryRepo;
import com.inventory.ui.setup.dialog.common.StockBrandTableModel;
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
public class StockBrandSetupDialog extends javax.swing.JDialog implements KeyListener {

    private static final Logger log = LoggerFactory.getLogger(StockBrandSetupDialog.class);

    private int selectRow = - 1;
    private StockBrand brand = new StockBrand();
    private final StockBrandTableModel itemBrandTableModel = new StockBrandTableModel();
    private InventoryRepo inventoryRepo;
    private List<StockBrand> listStockBrand;
    private TableRowSorter<TableModel> sorter;
    private StartWithRowFilter swrf;

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public void setListStockBrand(List<StockBrand> listStockBrand) {
        this.listStockBrand = listStockBrand;
        itemBrandTableModel.setListItemBrand(this.listStockBrand);
        txtCode.requestFocus();

    }

    /**
     * Creates new form ItemTypeSetupDialog
     *
     * @param frame
     */
    public StockBrandSetupDialog(JFrame frame) {
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
        txtCode.addKeyListener(this);
        txtName.addKeyListener(this);
        btnClear.addKeyListener(this);
        btnSave.addKeyListener(this);
        tblBrand.addKeyListener(this);
    }

    private void initTable() {
        tblBrand.setModel(itemBrandTableModel);
        sorter = new TableRowSorter<>(tblBrand.getModel());
        tblBrand.setRowSorter(sorter);
        tblBrand.getTableHeader().setFont(Global.lableFont);
        tblBrand.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblBrand.setDefaultRenderer(Object.class, new TableCellRender());
        tblBrand.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting()) {
                if (tblBrand.getSelectedRow() >= 0) {
                    selectRow = tblBrand.convertRowIndexToModel(tblBrand.getSelectedRow());
                    setCategory(itemBrandTableModel.getItemBrand(selectRow));
                }
            }
        });
        tblBrand.setRowHeight(Global.tblRowHeight);

    }

    private void setCategory(StockBrand b) {
        brand = b;
        txtCode.setText(brand.getUserCode());
        txtName.setText(brand.getBrandName());
        lblStatus.setText("EDIT");
        lblStatus.setForeground(Color.blue);
        txtCode.requestFocus();

    }

    private void save() {
        if (isValidEntry()) {
            progress.setIndeterminate(true);
            btnSave.setEnabled(false);
            inventoryRepo.saveBrand(brand).subscribe((t) -> {
                if (lblStatus.getText().equals("EDIT")) {
                    listStockBrand.set(selectRow, t);
                } else {
                    listStockBrand.add(t);
                }
                clear();
                sendMessage(t.getBrandName());
            }, (e) -> {
                progress.setIndeterminate(false);
                btnSave.setEnabled(true);
                JOptionPane.showMessageDialog(this, e.getMessage());
            });

        }
    }

    private void sendMessage(String mes) {
        inventoryRepo.sendDownloadMessage(MessageType.BRAND, mes)
                .doOnSuccess((t) -> {
                    log.info(t);
                }).subscribe();
    }

    private void clear() {
        progress.setIndeterminate(false);
        btnSave.setEnabled(true);
        txtCode.setText(null);
        txtFilter.setText(null);
        txtName.setText(null);
        lblStatus.setText("NEW");
        lblStatus.setForeground(Color.green);
        brand = new StockBrand();
        itemBrandTableModel.refresh();
        tblBrand.requestFocus();
        txtCode.requestFocus();
    }

    private boolean isValidEntry() {
        boolean status = true;

        if (txtName.getText().isEmpty()) {
            status = false;
            JOptionPane.showMessageDialog(this, "Name is empty.",
                    "Invalid Name", JOptionPane.ERROR_MESSAGE);
            txtName.requestFocusInWindow();
        } else {
            brand.setUserCode(txtCode.getText());
            brand.setBrandName(txtName.getText());
            if (lblStatus.getText().equals("NEW")) {
                StockBrandKey key = new StockBrandKey();
                key.setBrandCode(null);
                key.setCompCode(Global.compCode);
                brand.setKey(key);
                brand.setDeptId(Global.deptId);
                brand.setCreatedBy(Global.loginUser.getUserCode());
                brand.setCreatedDate(LocalDateTime.now());
                brand.setMacId(Global.macId);
            } else {
                brand.setUpdatedBy(Global.loginUser.getUserCode());
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
        tblBrand = new javax.swing.JTable();
        txtFilter = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        btnSave = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtCode = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        progress = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Stock Brand Setup");

        tblBrand.setFont(Global.textFont);
        tblBrand.setModel(new javax.swing.table.DefaultTableModel(
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
        tblBrand.setName("tblBrand"); // NOI18N
        jScrollPane1.setViewportView(tblBrand);

        txtFilter.setName("txtFilter"); // NOI18N
        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Name");

        txtName.setFont(Global.textFont);
        txtName.setName("txtName"); // NOI18N
        txtName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtNameFocusGained(evt);
            }
        });
        txtName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNameActionPerformed(evt);
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
        jLabel3.setText("Code");

        txtCode.setFont(Global.textFont);
        txtCode.setName("txtCode"); // NOI18N
        txtCode.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCodeFocusGained(evt);
            }
        });
        txtCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 57, Short.MAX_VALUE)
                            .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 57, Short.MAX_VALUE))
                        .addGap(12, 12, 12)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtName, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(0, 79, Short.MAX_VALUE)
                                .addComponent(btnSave)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnClear))
                            .addComponent(txtCode, javax.swing.GroupLayout.Alignment.LEADING))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblStatus)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnClear)
                        .addComponent(btnSave)))
                .addContainerGap(263, Short.MAX_VALUE))
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
                            .addComponent(txtFilter, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE))
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
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNameActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        try {
            save();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            log.error("Save StockBrand :" + e.getMessage());
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

    private void txtNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNameFocusGained
        // TODO add your handling code here:
        txtName.selectAll();
    }//GEN-LAST:event_txtNameFocusGained

    private void txtCodeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCodeFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCodeFocusGained

    private void txtCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCodeActionPerformed

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
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JProgressBar progress;
    private javax.swing.JTable tblBrand;
    private javax.swing.JTextField txtCode;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JTextField txtName;
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

            case "txtName" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    btnSave.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    btnClear.requestFocus();
                }
                tabToTable(e);
            }
            case "txtCode" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtName.requestFocus();
                }
            }

            case "btnSave" -> {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtName.requestFocus();
                }
                tabToTable(e);
            }
            case "btnDelete" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    btnClear.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    btnSave.requestFocus();
                }
                tabToTable(e);
            }
            case "btnClear" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtName.requestFocus();
                }
                tabToTable(e);
            }
        }
    }

    private void tabToTable(KeyEvent e) {
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
            tblBrand.requestFocus();
            if (tblBrand.getRowCount() >= 0) {
                tblBrand.setRowSelectionInterval(0, 0);
            }
        }
    }
}
