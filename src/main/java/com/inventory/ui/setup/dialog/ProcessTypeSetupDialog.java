/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup.dialog;

import com.common.Global;
import com.common.StartWithRowFilter;
import com.common.TableCellRender;
import com.inventory.model.ProcessType;
import com.inventory.model.ProcessTypeKey;
import com.inventory.ui.common.InventoryRepo;
import com.inventory.ui.setup.dialog.common.ProcessTypeTableModel;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
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

/**
 *
 * @author Lenovo
 */
public class ProcessTypeSetupDialog extends javax.swing.JDialog implements KeyListener {

    private static final Logger log = LoggerFactory.getLogger(ProcessTypeSetupDialog.class);

    private int selectRow = - 1;
    private ProcessType vou = new ProcessType();
    private final ProcessTypeTableModel processTypeTableModel = new ProcessTypeTableModel();
    private InventoryRepo inventoryRepo;

    private TableRowSorter<TableModel> sorter;
    private StartWithRowFilter swrf;
    private List<ProcessType> listType = new ArrayList<>();

    public List<ProcessType> getListType() {
        return listType;
    }

    public void setListType(List<ProcessType> listType) {
        this.listType = listType;
    }

    public InventoryRepo getInventoryRepo() {
        return inventoryRepo;
    }

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    /**
     * Creates new form ItemTypeSetupDialog
     */
    public ProcessTypeSetupDialog() {
        super(Global.parentForm, false);
        initComponents();
        initKeyListener();
        lblStatus.setForeground(Color.green);
    }

    public void initMain() {
        swrf = new StartWithRowFilter(txtFilter);
        initTable();
        searchCategory();
        txtUserCode.requestFocus();
    }

    private void initKeyListener() {
        txtUserCode.addKeyListener(this);
        txtName.addKeyListener(this);
        btnClear.addKeyListener(this);
        btnSave.addKeyListener(this);
        tblVou.addKeyListener(this);
    }

    private void searchCategory() {
        processTypeTableModel.setListVou(listType);
    }

    private void initTable() {
        tblVou.setModel(processTypeTableModel);
        sorter = new TableRowSorter<>(tblVou.getModel());
        tblVou.setRowSorter(sorter);
        tblVou.getTableHeader().setFont(Global.lableFont);
        tblVou.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblVou.setRowHeight(Global.tblRowHeight);
        tblVou.setDefaultRenderer(Object.class, new TableCellRender());
        tblVou.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting()) {
                if (tblVou.getSelectedRow() >= 0) {
                    selectRow = tblVou.convertRowIndexToModel(tblVou.getSelectedRow());
                    setType(processTypeTableModel.getVouStatus(selectRow));
                }
            }
        });
    }

    private void setType(ProcessType cat) {
        vou = cat;
        txtName.setText(vou.getProName());
        txtUserCode.setText(vou.getUserCode());
        chkCalulate.setSelected(vou.isCalculate());
        chkActive.setSelected(vou.isActive());
        txtName.requestFocus();
        lblStatus.setText("EDIT");
        lblStatus.setForeground(Color.blue);

    }

    private void save() {
        if (isValidEntry()) {
            vou = inventoryRepo.saveProcessType(vou);
            if (lblStatus.getText().equals("EDIT")) {
                listType.set(selectRow, vou);
            } else {
                listType.add(vou);
            }
            clear();
        }
    }

    private void clear() {
        chkActive.setSelected(true);
        chkCalulate.setSelected(true);
        txtUserCode.setText(null);
        txtFilter.setText(null);
        txtName.setText(null);
        lblStatus.setText("NEW");
        lblStatus.setForeground(Color.green);
        vou = new ProcessType();
        processTypeTableModel.refresh();
        tblVou.requestFocus();
        txtUserCode.requestFocus();
    }

    private boolean isValidEntry() {
        boolean status = true;
        if (txtName.getText().isEmpty()) {
            status = false;
            JOptionPane.showMessageDialog(this, "Invalid Name");
            txtName.requestFocus();
        } else {
            vou.setUserCode(txtUserCode.getText());
            vou.setProName(txtName.getText());
            vou.setActive(chkActive.isSelected());
            vou.setCalculate(chkCalulate.isSelected());
            if (lblStatus.getText().equals("NEW")) {
                ProcessTypeKey key = new ProcessTypeKey();
                key.setCompCode(Global.compCode);
                vou.setKey(key);
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
        tblVou = new javax.swing.JTable();
        txtFilter = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        btnSave = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtUserCode = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtSysCode = new javax.swing.JTextField();
        chkCalulate = new javax.swing.JCheckBox();
        chkActive = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Process Type Setup");
        setModalityType(java.awt.Dialog.ModalityType.TOOLKIT_MODAL);

        tblVou.setFont(Global.textFont);
        tblVou.setModel(new javax.swing.table.DefaultTableModel(
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
        tblVou.setName("tblVou"); // NOI18N
        jScrollPane1.setViewportView(tblVou);

        txtFilter.setName("txtFilter"); // NOI18N
        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Description");

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
        jLabel3.setText("User Code");

        txtUserCode.setFont(Global.textFont);
        txtUserCode.setName("txtUserCode"); // NOI18N
        txtUserCode.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtUserCodeFocusGained(evt);
            }
        });
        txtUserCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUserCodeActionPerformed(evt);
            }
        });

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Sys Code");

        txtSysCode.setEditable(false);
        txtSysCode.setFont(Global.textFont);
        txtSysCode.setName("txtUserCode"); // NOI18N
        txtSysCode.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSysCodeFocusGained(evt);
            }
        });
        txtSysCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSysCodeActionPerformed(evt);
            }
        });

        chkCalulate.setSelected(true);
        chkCalulate.setText("Stock Calculate");
        chkCalulate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCalulateActionPerformed(evt);
            }
        });

        chkActive.setSelected(true);
        chkActive.setText("Active");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(12, 12, 12))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtName, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtUserCode, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtSysCode, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 75, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnSave)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(chkCalulate)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkActive)))))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnClear, btnSave});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtSysCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtUserCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkCalulate)
                    .addComponent(lblStatus)
                    .addComponent(chkActive))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnClear)
                    .addComponent(btnSave))
                .addContainerGap(196, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFilter, javax.swing.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE))
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
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        getAccessibleContext().setAccessibleDescription("");

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
            JOptionPane.showMessageDialog(this, e.getMessage(), "Save Voucher Status", JOptionPane.ERROR_MESSAGE);
            log.error("Save Voucher Status :" + e.getMessage());
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

    private void txtUserCodeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtUserCodeFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUserCodeFocusGained

    private void txtUserCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUserCodeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUserCodeActionPerformed

    private void txtSysCodeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSysCodeFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSysCodeFocusGained

    private void txtSysCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSysCodeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSysCodeActionPerformed

    private void chkCalulateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCalulateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkCalulateActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox chkActive;
    private javax.swing.JCheckBox chkCalulate;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblVou;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtSysCode;
    private javax.swing.JTextField txtUserCode;
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
            case "txtName" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnSave.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    btnClear.requestFocus();
                }
            }
            case "txtUserCode" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtName.requestFocus();
                }
            }
            case "btnSave" -> {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtName.requestFocus();
                }
            }
            case "btnDelete" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnClear.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    btnSave.requestFocus();
                }
            }
            case "btnClear" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtUserCode.requestFocus();
                }
            }
        }
    }
}
