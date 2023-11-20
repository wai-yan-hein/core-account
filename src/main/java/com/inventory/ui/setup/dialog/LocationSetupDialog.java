/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup.dialog;

import com.repo.AccountRepo;
import com.acc.common.COAComboBoxModel;
import com.acc.common.DepartmentAccComboBoxModel;
import com.acc.model.COAKey;
import com.acc.model.ChartOfAccount;
import com.acc.model.DepartmentA;
import com.acc.model.DepartmentAKey;
import com.common.Global;
import com.common.ProUtil;
import com.common.StartWithRowFilter;
import com.common.TableCellRender;
import com.common.Util1;
import com.inventory.model.Location;
import com.inventory.model.LocationKey;
import com.inventory.model.MessageType;
import com.inventory.model.WareHouse;
import com.inventory.model.WareHouseKey;
import com.inventory.ui.common.WareHouseComboModel;
import com.repo.InventoryRepo;
import com.inventory.ui.setup.dialog.common.LocationTableModel;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.time.LocalDateTime;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class LocationSetupDialog extends javax.swing.JDialog implements KeyListener {

    private int selectRow = - 1;
    private Location location = new Location();
    private final LocationTableModel locationTableModel = new LocationTableModel();
    private final DepartmentAccComboBoxModel departmentComboBoxModel = new DepartmentAccComboBoxModel();
    private final COAComboBoxModel cOAComboBoxModel = new COAComboBoxModel();
    private final WareHouseComboModel wareHouseComboModel = new WareHouseComboModel();
    private InventoryRepo inventoryRepo;
    private AccountRepo accountRepo;
    private TableRowSorter<TableModel> sorter;
    private StartWithRowFilter swrf;

    public void setAccountRepo(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    /**
     * Creates new form ItemTypeSetupDialog
     *
     * @param frame
     */
    public LocationSetupDialog(JFrame frame) {
        super(frame, true);
        initComponents();
        initKeyListener();
    }

    public void initMain() {
        swrf = new StartWithRowFilter(txtFilter);
        initTable();
        initModel();
        txtUserCode.requestFocus();
    }

    private void initKeyListener() {
        txtUserCode.addKeyListener(this);
        txtName.addKeyListener(this);
        btnClear.addKeyListener(this);
        btnSave.addKeyListener(this);
        tblLocation.addKeyListener(this);
        lblStatus.setForeground(Color.green);
    }

    private void initModel() {
        cboDepartment.setModel(departmentComboBoxModel);
        cboCash.setModel(cOAComboBoxModel);
        cboWH.setModel(wareHouseComboModel);
        inventoryRepo.getWareHouse().doOnSuccess((t) -> {
            wareHouseComboModel.setData(t);
        }).subscribe();
        accountRepo.getDepartment().doOnSuccess((t) -> {
            t.add(new DepartmentA());
            departmentComboBoxModel.setData(t);
        }).subscribe();
        accountRepo.getCOAByGroup(ProUtil.getProperty(ProUtil.CASH_GROUP)).doOnSuccess((t) -> {
            t.add(new ChartOfAccount());
            cOAComboBoxModel.setData(t);
        }).subscribe();

    }

    public void search() {
        inventoryRepo.getLocation().doOnSuccess((t) -> {
            locationTableModel.setListLocation(t);
        }).doOnTerminate(() -> {
            setVisible(true);
        }).subscribe();

    }

    private void initTable() {
        tblLocation.setModel(locationTableModel);
        sorter = new TableRowSorter<>(tblLocation.getModel());
        tblLocation.setRowSorter(sorter);
        tblLocation.getTableHeader().setFont(Global.tblHeaderFont);
        tblLocation.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblLocation.setRowHeight(Global.tblRowHeight);
        tblLocation.getColumnModel().getColumn(0).setPreferredWidth(20);
        tblLocation.getColumnModel().getColumn(1).setPreferredWidth(200);
        tblLocation.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblLocation.setDefaultRenderer(Object.class, new TableCellRender());
        tblLocation.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting()) {
                if (tblLocation.getSelectedRow() >= 0) {
                    selectRow = tblLocation.convertRowIndexToModel(tblLocation.getSelectedRow());
                    setLocation(locationTableModel.getLocation(selectRow));
                }
            }
        });
    }

    private void setLocation(Location loc) {
        location = loc;
        txtName.setText(location.getLocName());
        txtUserCode.setText(location.getUserCode());
        chkActive.setSelected(loc.isActive());
        txtName.requestFocus();
        lblStatus.setText("EDIT");
        lblStatus.setForeground(Color.BLUE);
        setDepartment(loc.getDeptCode());
        setCash(loc.getCashAcc());
        setWareHouse(loc.getWareHouseCode());
    }

    private void setWareHouse(String whCode) {
        if (!Util1.isNullOrEmpty(whCode)) {
            inventoryRepo.findWareHouse(whCode).doOnSuccess((t) -> {
                wareHouseComboModel.setSelectedItem(t);
                cboWH.repaint();
            }).subscribe();
        } else {
            wareHouseComboModel.setSelectedItem(null);
            cboWH.repaint();
        }
    }

    private void setCash(String cashAcc) {
        if (!Util1.isNullOrEmpty(cashAcc)) {
            accountRepo.findCOA(cashAcc).doOnSuccess((t) -> {
                cOAComboBoxModel.setSelectedItem(t);
                cboCash.repaint();
            }).subscribe();
        } else {
            cOAComboBoxModel.setSelectedItem(null);
            cboCash.repaint();
        }
    }

    private void setDepartment(String deptCode) {
        if (!Util1.isNullOrEmpty(deptCode)) {
            accountRepo.findDepartment(deptCode).doOnSuccess((t) -> {
                departmentComboBoxModel.setSelectedItem(t);
                cboDepartment.repaint();
            }).subscribe();
        } else {
            departmentComboBoxModel.setSelectedItem(null);
            cboDepartment.repaint();
        }
    }

    private void save() {
        if (isValidEntry()) {
            inventoryRepo.saveLocation(location).doOnSuccess((t) -> {
                if (lblStatus.getText().equals("EDIT")) {
                    locationTableModel.setLocation(t, selectRow);
                } else {
                    locationTableModel.addLocation(t);
                }
            }).doOnTerminate(() -> {
                sendMessage(location.getLocName());
                clear();
            }).subscribe();
        }
    }

    private void sendMessage(String mes) {
        inventoryRepo.sendDownloadMessage(MessageType.LOCATION, mes)
                .doOnSuccess((t) -> {
                    log.info(t);
                }).subscribe();
    }

    private void clear() {
        txtUserCode.setText(null);
        txtFilter.setText(null);
        txtName.setText(null);
        chkActive.setSelected(true);
        departmentComboBoxModel.setSelectedItem(null);
        cboDepartment.repaint();
        cOAComboBoxModel.setSelectedItem(null);
        cboCash.repaint();
        wareHouseComboModel.setSelectedItem(null);
        cboWH.repaint();
        lblStatus.setText("NEW");
        lblStatus.setForeground(Color.GREEN);
        location = new Location();
        locationTableModel.refresh();
        txtUserCode.requestFocus();
    }

    private boolean isValidEntry() {
        boolean status = true;
        if (txtName.getText().isEmpty()) {
            status = false;
            JOptionPane.showMessageDialog(this, "Invalid Name");
            txtName.requestFocus();
        } else {
            if (cboDepartment.getSelectedItem() instanceof DepartmentA d) {
                DepartmentAKey key = d.getKey();
                location.setDeptCode(key == null ? null : key.getDeptCode());
            }
            if (cboCash.getSelectedItem() instanceof ChartOfAccount coa) {
                COAKey key = coa.getKey();
                location.setCashAcc(key == null ? null : key.getCoaCode());
            }
            if (cboWH.getSelectedItem() instanceof WareHouse wh) {
                WareHouseKey key = wh.getKey();
                location.setWareHouseCode(key == null ? null : key.getCode());
                location.setWareHouseName(wh.getDescription());

            }
            location.setUserCode(txtUserCode.getText());
            location.setLocName(txtName.getText());
            location.setDeptId(Global.deptId);
            location.setActive(chkActive.isSelected());
            if (lblStatus.getText().equals("NEW")) {
                LocationKey key = new LocationKey();
                key.setCompCode(Global.compCode);
                key.setLocCode(null);
                location.setKey(key);
                location.setCreatedBy(Global.loginUser.getUserCode());
                location.setCreatedDate(LocalDateTime.now());
                location.setMacId(Global.macId);
            } else {
                location.setUpdatedBy(Global.loginUser.getUserCode());
            }
        }
        return status;
    }

    private void export() {
        Util1.writeJsonFile(locationTableModel.getListLocation(), "location.json");
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
        tblLocation = new javax.swing.JTable();
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
        cboDepartment = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        cboCash = new javax.swing.JComboBox<>();
        jSeparator1 = new javax.swing.JSeparator();
        btnSave1 = new javax.swing.JButton();
        chkActive = new javax.swing.JCheckBox();
        jLabel6 = new javax.swing.JLabel();
        cboWH = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Location Setup");
        setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL);

        tblLocation.setFont(Global.textFont);
        tblLocation.setModel(new javax.swing.table.DefaultTableModel(
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
        tblLocation.setName("tblLocation"); // NOI18N
        jScrollPane1.setViewportView(tblLocation);

        txtFilter.setFont(Global.textFont);
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
        jLabel4.setText("Department");

        cboDepartment.setFont(Global.textFont);

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Cash");

        cboCash.setFont(Global.textFont);

        btnSave1.setBackground(Global.selectionColor);
        btnSave1.setFont(Global.lableFont);
        btnSave1.setForeground(new java.awt.Color(255, 255, 255));
        btnSave1.setText("Export");
        btnSave1.setName("btnSave"); // NOI18N
        btnSave1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSave1ActionPerformed(evt);
            }
        });

        chkActive.setFont(Global.lableFont);
        chkActive.setSelected(true);
        chkActive.setText("Active");

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Ware House");

        cboWH.setFont(Global.textFont);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(12, 12, 12)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtName)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(0, 81, Short.MAX_VALUE)
                                .addComponent(btnSave1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSave)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnClear))
                            .addComponent(txtUserCode)
                            .addComponent(cboDepartment, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cboCash, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(chkActive, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cboWH, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtUserCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(cboWH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(cboDepartment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(cboCash, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(chkActive)
                        .addGap(9, 9, 9)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnClear)
                            .addComponent(btnSave)
                            .addComponent(btnSave1)))
                    .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(202, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFilter, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE))
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
            JOptionPane.showMessageDialog(this, e.getMessage(), "Save Location", JOptionPane.ERROR_MESSAGE);
            log.error("Save Categor :" + e.getMessage());
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

    private void btnSave1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSave1ActionPerformed
        // TODO add your handling code here:
        export();
    }//GEN-LAST:event_btnSave1ActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSave1;
    private javax.swing.JComboBox<ChartOfAccount> cboCash;
    private javax.swing.JComboBox<DepartmentA> cboDepartment;
    private javax.swing.JComboBox<WareHouse> cboWH;
    private javax.swing.JCheckBox chkActive;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblLocation;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JTextField txtName;
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
            case "txtUserCode" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtName.requestFocus();
                }
            }
            case "txtName" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnSave.requestFocus();
                }
            }

            case "btnSave" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnClear.requestFocus();
                }
            }
            case "btnClear" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtName.requestFocus();
                }
            }
        }
    }
}
