/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup.dialog;

import com.repo.AccountRepo;
import com.acc.editor.COAAutoCompleter;
import com.acc.editor.DepartmentAutoCompleter;
import com.acc.model.ChartOfAccount;
import com.acc.model.DepartmentA;
import com.common.Global;
import com.common.IconUtil;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.common.StartWithRowFilter;
import com.common.TableCellRender;
import com.common.Util1;
import com.formdev.flatlaf.FlatClientProperties;
import com.inventory.editor.WareHouseAutoCompleter;
import com.inventory.entity.Location;
import com.inventory.entity.LocationKey;
import com.inventory.entity.MessageType;
import com.inventory.entity.WareHouse;
import com.repo.InventoryRepo;
import com.inventory.ui.setup.dialog.common.LocationTableModel;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.time.LocalDateTime;
import java.util.ArrayList;
import javax.swing.JButton;
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

/**
 *
 * @author Lenovo
 */
@Slf4j
public class LocationSetupDialog extends javax.swing.JDialog implements KeyListener, SelectionObserver {

    private int selectRow = - 1;
    private Location location = new Location();
    private final LocationTableModel locationTableModel = new LocationTableModel();
    private WareHouseAutoCompleter wareHouseAutoCompleter;
    private WareHouseAutoCompleter wareHouseAutoCompleterF;
    private DepartmentAutoCompleter departmentAutoCompleter;
    private COAAutoCompleter coaAutoCompleter;
    @Setter
    private InventoryRepo inventoryRepo;
    @Setter
    private AccountRepo accountRepo;
    private TableRowSorter<TableModel> sorter;
    private StartWithRowFilter swrf;

    /**
     * Creates new form ItemTypeSetupDialog
     *
     * @param frame
     */
    public LocationSetupDialog(JFrame frame) {
        super(frame, true);
        initComponents();
        initKeyListener();
        initClientProperty();
    }

    public void initMain() {
        swrf = new StartWithRowFilter(txtSearch);
        initTable();
        initModel();
        txtUserCode.requestFocus();
    }

    private void initClientProperty() {
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search Here");
        txtSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        txtSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, IconUtil.getIcon(IconUtil.SEARCH_ICON));
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
        departmentAutoCompleter = new DepartmentAutoCompleter(txtDep, null, false, false);
        wareHouseAutoCompleter = new WareHouseAutoCompleter(txtWH, null, false);
        wareHouseAutoCompleterF = new WareHouseAutoCompleter(txtWHF, null, true);
        wareHouseAutoCompleterF.setObserver(this);
        coaAutoCompleter = new COAAutoCompleter(txtCoa, null, false);
        accountRepo.getDepartment().doOnSuccess((t) -> {
            t.add(new DepartmentA());
            departmentAutoCompleter.setListDepartment(t);
        }).subscribe();
        inventoryRepo.getWareHouse().doOnSuccess((t) -> {
            wareHouseAutoCompleter.setListObject(t);
            wareHouseAutoCompleterF.setListObject(new ArrayList<>(t));
        }).subscribe();
        accountRepo.getCOAByGroup(ProUtil.getProperty(ProUtil.CASH_GROUP)).doOnSuccess((t) -> {
            t.add(new ChartOfAccount());
            coaAutoCompleter.setListCOA(t);
        }).subscribe();

    }

    public void search() {
        progress.setIndeterminate(true);
        String whCode = wareHouseAutoCompleterF.getObject().getKey().getCode();
        inventoryRepo.getLocation(whCode).doOnSuccess((t) -> {
            locationTableModel.setListLocation(t);
        }).doOnTerminate(() -> {
            calSize();
            progress.setIndeterminate(false);
        }).subscribe();
        setVisible(true);
    }

    private void calSize() {
        lblRec.setText(String.valueOf(locationTableModel.getListLocation().size()));
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
        inventoryRepo.findWareHouse(whCode).doOnSuccess((t) -> {
            wareHouseAutoCompleter.setObject(t);
        }).subscribe();
    }

    private void setCash(String cashAcc) {
        accountRepo.findCOA(cashAcc).doOnSuccess((t) -> {
            coaAutoCompleter.setCoa(t);
        }).subscribe();
    }

    private void setDepartment(String deptCode) {
        accountRepo.findDepartment(deptCode).doOnSuccess((t) -> {
            departmentAutoCompleter.setDepartment(t);
        }).subscribe();
    }

    private void save() {
        if (isValidEntry()) {
            progress.setIndeterminate(true);
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
        calSize();
        progress.setIndeterminate(false);
        txtUserCode.setText(null);
        txtSearch.setText(null);
        txtName.setText(null);
        chkActive.setSelected(true);
        departmentAutoCompleter.setDepartment(null);
        coaAutoCompleter.setCoa(null);
        wareHouseAutoCompleter.setObject(null);
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
            DepartmentA d = departmentAutoCompleter.getDepartment();
            if (d != null) {
                location.setDeptCode(d.getKey() == null ? null : d.getKey().getDeptCode());
            }
            ChartOfAccount coa = coaAutoCompleter.getCOA();
            if (coa != null) {
                location.setCashAcc(coa.getKey() == null ? null : coa.getKey().getCoaCode());
            }

            WareHouse w = wareHouseAutoCompleter.getObject();
            if (w != null) {
                location.setWareHouseCode(w.getKey() == null ? null : w.getKey().getCode());
                location.setWareHouseName(w.getDescription());
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

        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        btnSave = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtUserCode = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        btnSave1 = new javax.swing.JButton();
        chkActive = new javax.swing.JCheckBox();
        jLabel6 = new javax.swing.JLabel();
        txtWH = new javax.swing.JTextField();
        txtDep = new javax.swing.JTextField();
        txtCoa = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        lblRec = new javax.swing.JLabel();
        progress = new javax.swing.JProgressBar();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblLocation = new javax.swing.JTable();
        txtWHF = new javax.swing.JTextField();
        txtSearch = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Location Setup");
        setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL);

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

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Cash");

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

        txtWH.setFont(Global.textFont);
        txtWH.setName("txtName"); // NOI18N
        txtWH.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtWHFocusGained(evt);
            }
        });
        txtWH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtWHActionPerformed(evt);
            }
        });

        txtDep.setFont(Global.textFont);
        txtDep.setName("txtName"); // NOI18N
        txtDep.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDepFocusGained(evt);
            }
        });
        txtDep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDepActionPerformed(evt);
            }
        });

        txtCoa.setFont(Global.textFont);
        txtCoa.setName("txtName"); // NOI18N
        txtCoa.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCoaFocusGained(evt);
            }
        });
        txtCoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCoaActionPerformed(evt);
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Records :");

        lblRec.setFont(Global.lableFont);
        lblRec.setText("0");

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
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(btnSave1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSave)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnClear))
                            .addComponent(txtUserCode)
                            .addComponent(chkActive, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtWH)
                            .addComponent(txtDep)
                            .addComponent(txtCoa)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblRec, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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
                    .addComponent(txtWH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtDep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtCoa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(chkActive)
                        .addGap(9, 9, 9)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnClear)
                            .addComponent(btnSave)
                            .addComponent(btnSave1)))
                    .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(lblRec))
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

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

        txtWHF.setFont(Global.textFont);
        txtWHF.setName("txtSearch"); // NOI18N
        txtWHF.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtWHFKeyReleased(evt);
            }
        });

        txtSearch.setFont(Global.textFont);
        txtSearch.setName("txtSearch"); // NOI18N
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtSearch, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtWHF, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtWHF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1)
                .addContainerGap())
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
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        // TODO add your handling code here:
        if (txtSearch.getText().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(swrf);
        }
    }//GEN-LAST:event_txtSearchKeyReleased

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

    private void txtWHFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtWHFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtWHFocusGained

    private void txtWHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtWHActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtWHActionPerformed

    private void txtDepFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDepFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDepFocusGained

    private void txtDepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDepActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDepActionPerformed

    private void txtCoaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCoaFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCoaFocusGained

    private void txtCoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCoaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCoaActionPerformed

    private void txtWHFKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtWHFKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtWHFKeyReleased

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSave1;
    private javax.swing.JCheckBox chkActive;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblRec;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JProgressBar progress;
    private javax.swing.JTable tblLocation;
    private javax.swing.JTextField txtCoa;
    private javax.swing.JTextField txtDep;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtUserCode;
    private javax.swing.JTextField txtWH;
    private javax.swing.JTextField txtWHF;
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

    @Override
    public void selected(Object source, Object selectObj) {
        search();
    }
}
