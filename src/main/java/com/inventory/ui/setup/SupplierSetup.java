/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup;

import com.acc.dialog.FindDialog;
import com.repo.AccountRepo;
import com.acc.editor.COAAutoCompleter;
import com.acc.model.ChartOfAccount;
import com.common.ComponentUtil;
import com.common.Global;
import com.common.PanelControl;
import com.common.ProUtil;
import com.common.RowHeader;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.common.Util1;
import com.inventory.editor.RegionAutoCompleter;
import com.inventory.editor.TraderGroupAutoCompleter;
import com.inventory.entity.General;
import com.inventory.entity.MessageType;
import com.inventory.entity.Region;
import com.inventory.entity.Trader;
import com.inventory.entity.TraderGroup;
import com.inventory.entity.TraderKey;
import com.repo.InventoryRepo;
import com.inventory.ui.setup.common.SupplierTabelModel;
import com.inventory.ui.setup.dialog.RegionSetup;
import com.inventory.ui.setup.dialog.TraderGroupDialog;
import com.repo.UserRepo;
import com.user.editor.DepartmentUserAutoCompleter;
import com.user.model.DepartmentUser;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.JTextComponent;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class SupplierSetup extends javax.swing.JPanel implements KeyListener, PanelControl {

    private int selectRow = -1;
    private Trader supplier = new Trader();
    private SupplierTabelModel supplierTabelModel = new SupplierTabelModel();
    private InventoryRepo inventoryRepo;
    private AccountRepo accountRepo;
    private UserRepo userRepo;
    private RegionAutoCompleter regionAutoCompleter;
    private TraderGroupAutoCompleter traderGroupAutoCompleter;
    private COAAutoCompleter cOAAutoCompleter;

    private SelectionObserver observer;
    private JProgressBar progress;
    private DepartmentUserAutoCompleter departmentUserAutoCompleter;
    private RegionSetup regionSetup;
    private FindDialog findDialog;

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public void setAccountRepo(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public SelectionObserver getObserver() {
        return observer;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public JProgressBar getProgress() {
        return progress;
    }

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }

    /**
     * Creates new form CustomerSetup
     */
    public SupplierSetup() {
        initComponents();
        initKeyListener();
        initSpinner();
    }

    public void initMain() {
        ComponentUtil.addFocusListener(this);
        initCombo();
        initTable();
        initRowHeader();
        batchLock();
        initFind();
    }

    private void batchLock() {
        ComponentUtil.enableForm(this, !Global.batchLock);
    }

    private void initFind() {
        findDialog = new FindDialog(Global.parentForm, tblCustomer);
    }

    private void initRowHeader() {
        RowHeader header = new RowHeader();
        JList list = header.createRowHeader(tblCustomer, 30);
        s1.setRowHeaderView(list);
    }

    private void initSpinner() {
        SpinnerModel spinnerModel = new SpinnerNumberModel(0, 0, 100, 1);
        spPercent.setModel(spinnerModel);
    }

    private void initCombo() {
        regionAutoCompleter = new RegionAutoCompleter(txtRegion, null, false);
        departmentUserAutoCompleter = new DepartmentUserAutoCompleter(txtDep, null, false);
        inventoryRepo.getRegion().subscribe((t) -> {
            regionAutoCompleter.setListRegion(t);
        });
        inventoryRepo.getTraderGroup().subscribe((t) -> {
            traderGroupAutoCompleter = new TraderGroupAutoCompleter(txtGroup, t, null, false);
            traderGroupAutoCompleter.setGroup(null);
        });
        cOAAutoCompleter = new COAAutoCompleter(txtAccount, null, false);
        accountRepo.getCOAByGroup(ProUtil.getProperty(ProUtil.CREDITOR_GROUP)).doOnSuccess((t) -> {
            cOAAutoCompleter.setListCOA(t);
        }).subscribe();
        userRepo.getDeparment(true).doOnSuccess((t) -> {
            departmentUserAutoCompleter.setListDepartment(t);
        }).subscribe();
        assignDefault();
    }

    private void assignDefault() {
        accountRepo.findCOA(ProUtil.getProperty(ProUtil.CREDITOR_ACC)).doOnSuccess((tt) -> {
            cOAAutoCompleter.setCoa(tt);
        }).subscribe();
        userRepo.findDepartment(Global.deptId).doOnSuccess(t -> {
            departmentUserAutoCompleter.setDepartment(t);
        }).subscribe();
    }

    private void initTable() {
        tblCustomer.setModel(supplierTabelModel);
        tblCustomer.getTableHeader().setFont(Global.textFont);
        tblCustomer.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCustomer.getColumnModel().getColumn(0).setPreferredWidth(40);// Code
        tblCustomer.getColumnModel().getColumn(1).setPreferredWidth(300);// Name
        tblCustomer.getColumnModel().getColumn(1).setPreferredWidth(300);// Name
        tblCustomer.getColumnModel().getColumn(3).setPreferredWidth(40);// Active 
        tblCustomer.setDefaultRenderer(Boolean.class, new TableCellRender());
        tblCustomer.setDefaultRenderer(Object.class, new TableCellRender());
        tblCustomer.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        searchSupplier();
    }

    private void searchSupplier() {
        progress.setIndeterminate(true);
        inventoryRepo.getSupplier()
                .doOnSuccess((t) -> {
                    supplierTabelModel.setListCustomer(t);
                    lblRecord.setText(String.valueOf(t.size()));
                })
                .doOnError((e) -> {
                    JOptionPane.showMessageDialog(this, e.getMessage());
                    progress.setIndeterminate(false);
                })
                .doOnTerminate(() -> {
                    progress.setIndeterminate(false);
                })
                .subscribe();

    }

    private void setCustomer(Trader sup) {
        supplier = sup;
        txtSysCode.setText(supplier.getKey().getCode());
        txtCusCode.setText(supplier.getUserCode());
        txtCusName.setText(supplier.getTraderName());
        txtCusEmail.setText(supplier.getEmail());
        txtCusPhone.setText(supplier.getPhone());
        txtCusAddress.setText(supplier.getAddress());
        chkActive.setSelected(supplier.isActive());
        chkCD.setSelected(supplier.isCashDown());
        chkMulti.setSelected(supplier.isMulti());
        spPercent.setValue(Util1.getInteger(supplier.getCreditDays()));
        txtCusName.requestFocus();
        lblStatus.setText("EDIT");
        inventoryRepo.findRegion(supplier.getRegCode()).doOnSuccess((t) -> {
            regionAutoCompleter.setRegion(t);
        }).subscribe();
        inventoryRepo.findTraderGroup(supplier.getGroupCode(), Global.deptId).doOnSuccess((t) -> {
            traderGroupAutoCompleter.setGroup(t);
        }).subscribe();
        accountRepo.findCOA(supplier.getAccount()).doOnSuccess((t) -> {
            cOAAutoCompleter.setCoa(t);
        }).subscribe();
        Integer deptId = supplier.getDeptId();
        userRepo.findDepartment(deptId).doOnSuccess(t -> {
            departmentUserAutoCompleter.setDepartment(t);
        }).subscribe();

    }

    private boolean isValidEntry() {
        boolean status;
        DepartmentUser department = departmentUserAutoCompleter.getDepartment();
        Region region = regionAutoCompleter.getRegion();
        ChartOfAccount coa = cOAAutoCompleter.getCOA();
        TraderGroup traderGroup = traderGroupAutoCompleter.getGroup();

        if (txtCusName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(Global.parentForm, "Customer Name can't be empty");
            status = false;
        } else if (department == null) {
            JOptionPane.showMessageDialog(this, "You must choose department.", "Department", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtDep.requestFocus();
        } else {
            supplier.setUserCode(txtCusCode.getText());
            supplier.setTraderName(txtCusName.getText());
            supplier.setPhone(txtCusPhone.getText());
            supplier.setEmail(txtCusEmail.getText());
            supplier.setAddress(txtCusAddress.getText());
            supplier.setActive(chkActive.isSelected());
            supplier.setCreditDays(Util1.getInteger(spPercent.getValue()));
            supplier.setDeptId(department.getKey().getDeptId());
            supplier.setRegCode(region == null ? null : region.getKey().getRegCode());
            supplier.setAccount(coa == null ? ProUtil.getProperty(ProUtil.CREDITOR_ACC) : coa.getKey().getCoaCode());
            supplier.setType("SUP");
            supplier.setCashDown(chkCD.isSelected());
            supplier.setMulti(chkMulti.isSelected());
            supplier.setGroupCode(traderGroup == null ? null : traderGroup.getKey().getGroupCode());

            if (lblStatus.getText().equals("NEW")) {
                supplier.setMacId(Global.macId);
                supplier.setCreatedBy(Global.loginUser.getUserCode());
                supplier.setCreatedDate(LocalDateTime.now());
                TraderKey key = new TraderKey();
                key.setCompCode(Global.compCode);
                key.setCode(null);
                supplier.setKey(key);
            } else {
                supplier.setUpdatedBy(Global.loginUser.getUserCode());
            }
            status = true;
        }
        return status;
    }

    private void saveCustomer() {
        if (isValidEntry()) {
            observer.selected("save", false);
            progress.setIndeterminate(true);
            inventoryRepo.saveTrader(supplier).doOnSuccess((t) -> {
                if (lblStatus.getText().equals("EDIT")) {
                    supplierTabelModel.setCustomer(selectRow, t);
                } else {
                    supplierTabelModel.addCustomer(t);
                }
            }).doOnError((e) -> {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }).doOnTerminate(() -> {
                sendMessage(supplier.getTraderName());
                clear();
            }).subscribe();

        }
    }

    private void sendMessage(String mes) {
        inventoryRepo.sendDownloadMessage(MessageType.TRADER_INV, mes)
                .doOnSuccess((t) -> {
                    log.info(t);
                }).subscribe();
        accountRepo.sendDownloadMessage(MessageType.TRADER_ACC, mes)
                .doOnSuccess((t) -> {
                    log.info(t);
                }).subscribe();
    }

    public void clear() {
        observer.selected("save", true);
        progress.setIndeterminate(false);
        txtSysCode.setText(null);
        txtCusCode.setText(null);
        txtCusName.setText(null);
        txtCusEmail.setText(null);
        txtCusPhone.setText(null);
        regionAutoCompleter.setRegion(null);
        txtCusAddress.setText(null);
        chkActive.setSelected(true);
        chkCD.setSelected(false);
        chkMulti.setSelected(false);
        lblStatus.setText("NEW");
        txtCusCode.requestFocus();
        supplier = new Trader();
        supplierTabelModel.refresh();
        lblRecord.setText(String.valueOf(supplierTabelModel.getListCustomer().size()));
        traderGroupAutoCompleter.setGroup(null);
        if (cOAAutoCompleter != null) {
            cOAAutoCompleter.setCoa(null);
        }
        spPercent.setValue(0);
        assignDefault();
    }

    private void initKeyListener() {
        txtCusCode.addKeyListener(this);
        txtCusName.addKeyListener(this);
        txtCusPhone.addKeyListener(this);
        txtCusAddress.addKeyListener(this);
        txtCusEmail.addKeyListener(this);
        txtRemark.addKeyListener(this);
        chkActive.addKeyListener(this);
        tblCustomer.addKeyListener(this);

    }

    private void setSupplier() {
        if (tblCustomer.getSelectedRow() >= 0) {
            selectRow = tblCustomer.convertRowIndexToModel(tblCustomer.getSelectedRow());
            setCustomer(supplierTabelModel.getCustomer(selectRow));

        }
    }

    private void observeMain() {
        observer.selected("control", this);
        observer.selected("save", true);
        observer.selected("print", false);
        observer.selected("history", false);
        observer.selected("delete", true);
        observer.selected("refresh", true);
    }

    private void regionSetup() {
        if (regionSetup == null) {
            regionSetup = new RegionSetup(Global.parentForm);
            regionSetup.setInventoryRepo(inventoryRepo);
            regionSetup.initMain();
            regionSetup.setSize(Global.width / 2, Global.height / 2);
            regionSetup.setLocationRelativeTo(null);
        }
        regionSetup.search();
        regionAutoCompleter.setListRegion(regionSetup.getListRegion());
    }

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
        txtRemark = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        chkActive = new javax.swing.JCheckBox();
        lblStatus = new javax.swing.JLabel();
        txtRegion = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        txtSysCode = new javax.swing.JTextField();
        chkCD = new javax.swing.JCheckBox();
        chkMulti = new javax.swing.JCheckBox();
        jLabel10 = new javax.swing.JLabel();
        txtGroup = new javax.swing.JTextField();
        btnGroup = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        txtAccount = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        spPercent = new javax.swing.JSpinner();
        jLabel30 = new javax.swing.JLabel();
        txtDep = new javax.swing.JTextField();
        s1 = new javax.swing.JScrollPane();
        tblCustomer = new javax.swing.JTable();
        jLabel6 = new javax.swing.JLabel();
        lblRecord = new javax.swing.JLabel();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        panelEntry.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        panelEntry.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                panelEntryComponentShown(evt);
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("User Code");

        txtCusCode.setFont(Global.textFont);
        txtCusCode.setName("txtCusCode"); // NOI18N

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

        txtRemark.setFont(Global.textFont);
        txtRemark.setName("txtRemark"); // NOI18N

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Remark");

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Region");

        chkActive.setFont(Global.lableFont);
        chkActive.setSelected(true);
        chkActive.setText("Active");
        chkActive.setName("chkActive"); // NOI18N

        lblStatus.setFont(Global.lableFont);
        lblStatus.setText("NEW");

        txtRegion.setFont(Global.textFont);
        txtRegion.setName("txtCusEmail"); // NOI18N

        jButton2.setBackground(Global.selectionColor);
        jButton2.setFont(Global.lableFont);
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("...");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Sys Code");

        txtSysCode.setEditable(false);
        txtSysCode.setFont(Global.textFont);
        txtSysCode.setName("txtCusCode"); // NOI18N

        chkCD.setFont(Global.lableFont);
        chkCD.setText("Cash Down");
        chkCD.setName("chkActive"); // NOI18N

        chkMulti.setFont(Global.lableFont);
        chkMulti.setText("Multi Use");
        chkMulti.setName("chkActive"); // NOI18N

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Group");

        txtGroup.setFont(Global.textFont);
        txtGroup.setName("txtRemark"); // NOI18N

        btnGroup.setBackground(Global.selectionColor);
        btnGroup.setFont(Global.lableFont);
        btnGroup.setForeground(new java.awt.Color(255, 255, 255));
        btnGroup.setText("...");
        btnGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGroupActionPerformed(evt);
            }
        });

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Account");

        txtAccount.setFont(Global.textFont);
        txtAccount.setName("txtRemark"); // NOI18N

        jLabel12.setFont(Global.lableFont);
        jLabel12.setText("Credit Day");

        spPercent.setFont(Global.textFont);

        jLabel30.setFont(Global.lableFont);
        jLabel30.setText("Department");

        txtDep.setFont(Global.textFont);
        txtDep.setName("txtCusName"); // NOI18N

        javax.swing.GroupLayout panelEntryLayout = new javax.swing.GroupLayout(panelEntry);
        panelEntry.setLayout(panelEntryLayout);
        panelEntryLayout.setHorizontalGroup(
            panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEntryLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkActive, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkCD, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkMulti, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelEntryLayout.createSequentialGroup()
                        .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtSysCode, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelEntryLayout.createSequentialGroup()
                                .addComponent(txtRegion, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 8, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtCusPhone, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtCusCode, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtRemark, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtAccount, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelEntryLayout.createSequentialGroup()
                                .addComponent(txtGroup, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnGroup, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtCusAddress, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtCusName, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtCusEmail)
                            .addComponent(spPercent)
                            .addComponent(txtDep))
                        .addContainerGap())))
        );

        panelEntryLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnGroup, jButton2});

        panelEntryLayout.setVerticalGroup(
            panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEntryLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtSysCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30)
                    .addComponent(txtDep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtCusCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtCusName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtCusPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4)
                        .addComponent(txtCusEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addGroup(panelEntryLayout.createSequentialGroup()
                        .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtRegion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton2)
                            .addComponent(jLabel5)
                            .addComponent(txtCusAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnGroup))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtAccount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(spPercent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkMulti)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkCD)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkActive)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblStatus)
                .addContainerGap(175, Short.MAX_VALUE))
        );

        panelEntryLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtCusAddress, txtCusCode, txtCusEmail, txtCusName, txtCusPhone, txtRemark});

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
        tblCustomer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCustomerMouseClicked(evt);
            }
        });
        tblCustomer.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                tblCustomerComponentShown(evt);
            }
        });
        s1.setViewportView(tblCustomer);

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Record :");

        lblRecord.setFont(Global.lableFont);
        lblRecord.setText("0");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(s1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelEntry, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblRecord, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelEntry, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(s1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(lblRecord))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void tblCustomerComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_tblCustomerComponentShown
        // TODO add your handling code here:
    }//GEN-LAST:event_tblCustomerComponentShown

    private void panelEntryComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_panelEntryComponentShown
        // TODO add your handling code here:
    }//GEN-LAST:event_panelEntryComponentShown

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observeMain();
    }//GEN-LAST:event_formComponentShown

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        regionSetup();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void btnGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGroupActionPerformed
        // TODO add your handling code here:
        inventoryRepo.getTraderGroup().subscribe((t) -> {
            TraderGroupDialog dialog = new TraderGroupDialog(Global.parentForm);
            dialog.setListGroup(t);
            dialog.setInventoryRepo(inventoryRepo);
            dialog.initMain();
            dialog.setSize(Global.width / 2, Global.height / 2);
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        });
    }//GEN-LAST:event_btnGroupActionPerformed

    private void tblCustomerMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCustomerMouseClicked
        // TODO add your handling code here:
        setSupplier();
    }//GEN-LAST:event_tblCustomerMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGroup;
    private javax.swing.JCheckBox chkActive;
    private javax.swing.JCheckBox chkCD;
    private javax.swing.JCheckBox chkMulti;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel lblRecord;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPanel panelEntry;
    private javax.swing.JScrollPane s1;
    private javax.swing.JSpinner spPercent;
    private javax.swing.JTable tblCustomer;
    private javax.swing.JTextField txtAccount;
    private javax.swing.JTextField txtCusAddress;
    private javax.swing.JTextField txtCusCode;
    private javax.swing.JTextField txtCusEmail;
    private javax.swing.JTextField txtCusName;
    private javax.swing.JTextField txtCusPhone;
    private javax.swing.JTextField txtDep;
    private javax.swing.JTextField txtGroup;
    private javax.swing.JTextField txtRegion;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JTextField txtSysCode;
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

        if (sourceObj instanceof JComboBox jComboBox) {
            ctrlName = jComboBox.getName();
        } else if (sourceObj instanceof JFormattedTextField jFormattedTextField) {
            ctrlName = jFormattedTextField.getName();
        } else if (sourceObj instanceof JTextField jTextField) {
            ctrlName = jTextField.getName();
        } else if (sourceObj instanceof JCheckBox jCheckBox) {
            ctrlName = jCheckBox.getName();
        } else if (sourceObj instanceof JButton jButton) {
            ctrlName = jButton.getName();
        } else if (sourceObj instanceof JTable jTable) {
            ctrlName = jTable.getName();
        } else if (sourceObj instanceof JTextComponent jTextComponent) {
            ctrlName = jTextComponent.getName();
        }
        switch (ctrlName) {
            case "txtCusCode":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtCusName.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                }
                break;
            case "txtCusName":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtCusPhone.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {

                }
                tabToTable(e);
                break;
            case "txtCusPhone":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtCusEmail.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtCusName.requestFocus();

                }
                tabToTable(e);

                break;
            case "txtCusEmail":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtRegion.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtCusPhone.requestFocus();

                }
                tabToTable(e);

                break;
            case "txtRegion":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtCusAddress.requestFocus();
                    case KeyEvent.VK_UP ->
                        txtCusEmail.requestFocus();
                    case KeyEvent.VK_RIGHT ->
                        txtCusAddress.requestFocus();
                    case KeyEvent.VK_LEFT ->
                        txtCusEmail.requestFocus();
                }
                tabToTable(e);
                break;

            case "txtCusAddress":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtRegion.requestFocus();
                }
                tabToTable(e);

                break;
            case "cboAccount":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER -> {
                    }
                    case KeyEvent.VK_UP ->
                        txtCusAddress.requestFocus();
                    case KeyEvent.VK_RIGHT -> {
                    }
                    case KeyEvent.VK_LEFT ->
                        txtCusAddress.requestFocus();
                }
                tabToTable(e);
                break;

            case "cboPriceType":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtRemark.requestFocus();
                    case KeyEvent.VK_UP -> {
                    }
                    case KeyEvent.VK_RIGHT ->
                        txtRemark.requestFocus();
                    case KeyEvent.VK_LEFT -> {
                    }
                }
                tabToTable(e);
                break;

            case "txtRemark":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    chkActive.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                }
                tabToTable(e);

                break;
            case "chkActive":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtRemark.requestFocus();
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
                    setCustomer(supplierTabelModel.getCustomer(selectRow));
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
        saveCustomer();
    }

    @Override
    public void delete() {
        if (selectRow >= 0) {
            Trader t = supplierTabelModel.getCustomer(selectRow);
            int yn = JOptionPane.showConfirmDialog(this, "Are you sure to deleted?", "Supplier Delete", JOptionPane.YES_OPTION);
            if (yn == JOptionPane.YES_OPTION) {
                inventoryRepo.deleteTrader(t.getKey()).doOnSuccess((list) -> {
                    if (list.isEmpty()) {
                        supplierTabelModel.deleteCustomer(selectRow);
                        JOptionPane.showMessageDialog(this, "Deleted.");
                    } else {
                        String str = list.stream()
                                .map(General::getMessage) // Extract the message field from each General object
                                .collect(Collectors.joining()); // Concatenate the messages
                        JOptionPane.showMessageDialog(this, str);
                    }
                }).doOnTerminate(() -> {
                    sendMessage(t.getTraderName() + " : Deleted.");
                    clear();
                }).subscribe();
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
        searchSupplier();
    }

    @Override
    public void filter() {
        findDialog.setVisible(!findDialog.isVisible());
    }

    @Override
    public String panelName() {
        return this.getName();
    }

}
