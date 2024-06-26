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
import com.inventory.editor.CountryAutoCompleter;
import com.inventory.editor.RegionAutoCompleter;
import com.inventory.editor.TraderGroupAutoCompleter;
import com.inventory.entity.Country;
import com.inventory.entity.General;
import com.inventory.entity.MessageType;
import com.inventory.entity.Region;
import com.inventory.entity.Trader;
import com.inventory.entity.TraderGroup;
import com.inventory.entity.TraderKey;
import com.repo.InventoryRepo;
import com.inventory.ui.setup.common.CustomerTabelModel;
import com.inventory.ui.setup.dialog.CustomerImportDialog;
import com.inventory.ui.setup.dialog.RegionSetup;
import com.inventory.ui.setup.dialog.TraderGroupDialog;
import com.repo.UserRepo;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileWriter;
import java.io.IOException;
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
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.core.task.TaskExecutor;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class CustomerSetup extends javax.swing.JPanel implements KeyListener, PanelControl, SelectionObserver {

    private int row = 0;
    private Trader customer = new Trader();
    private final CustomerTabelModel customerTabelModel = new CustomerTabelModel();
    @Setter
    private TaskExecutor taskExecutor;
    private TraderGroupAutoCompleter traderGroupAutoCompleter;
    private COAAutoCompleter cOAAutoCompleter;
    @Setter
    private InventoryRepo inventoryRepo;
    @Setter
    private AccountRepo accountRepo;
    @Setter
    private UserRepo userRepo;
    private RegionAutoCompleter regionAutoCompleter;
    private CountryAutoCompleter countryAutoCompleter;
    @Setter
    private SelectionObserver observer;
    @Setter
    private JProgressBar progress;
    private RegionSetup regionSetup;
    @Setter
    private FindDialog findDialog;
    private CustomerImportDialog customerImportDialog;

    enum Header {
        UserCode,
        Name,
        Address,
        PhoneNo,
        Email,
        ContactPerson,
        Region,
        Remark,
        Nrc,
        Group,
        Department
    }

    /**
     * Creates new form CustomerSetup
     */
    public CustomerSetup() {
        initComponents();
        initKeyListener();
        initFormat();
        initSpinner();
    }

    private void initFormat() {
        txtCreditAmt.setFormatterFactory(Util1.getDecimalFormat());
    }

    public void initMain() {
        initCombo();
        initTable();
        initRowHeader();
        initFind();
        batchLock();
        searchCustomer();
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
        scroll.setRowHeaderView(list);
    }

    private void initSpinner() {
        SpinnerModel spinnerModel = new SpinnerNumberModel(0, 0, 100, 1);
        spPercent.setModel(spinnerModel);
    }

    private void initCombo() {
        regionAutoCompleter = new RegionAutoCompleter(txtRegion, null, false);
        inventoryRepo.getRegion().doOnSuccess((t) -> {
            regionAutoCompleter.setListRegion(t);
        }).subscribe();
        countryAutoCompleter = new CountryAutoCompleter(txtCountry, null, false);
        userRepo.getCountry().doOnSuccess((t) -> {
            countryAutoCompleter.setListCountry(t);
        }).subscribe();
        inventoryRepo.getTraderGroup().doOnSuccess((t) -> {
            traderGroupAutoCompleter = new TraderGroupAutoCompleter(txtGroup, t, null, false);
            traderGroupAutoCompleter.setGroup(null);
        }).subscribe();
        cOAAutoCompleter = new COAAutoCompleter(txtAccount, null, false);
        accountRepo.getCOAByGroup(ProUtil.getProperty(ProUtil.DEBTOR_GROUP)).doOnSuccess((t) -> {
            cOAAutoCompleter.setListCOA(t);
        }).subscribe();
        assignDefault();
    }

    private void assignDefault() {
        accountRepo.findCOA(ProUtil.getProperty(ProUtil.DEBTOR_ACC)).doOnSuccess((tt) -> {
            cOAAutoCompleter.setCoa(tt);
        }).subscribe();
    }

    private void initTable() {
        tblCustomer.setModel(customerTabelModel);
        tblCustomer.getTableHeader().setFont(Global.tblHeaderFont);
        tblCustomer.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCustomer.getColumnModel().getColumn(0).setPreferredWidth(60);// Code
        tblCustomer.getColumnModel().getColumn(1).setPreferredWidth(300);// Name
        tblCustomer.getColumnModel().getColumn(2).setPreferredWidth(5);// Active   
        tblCustomer.setDefaultRenderer(Boolean.class, new TableCellRender());
        tblCustomer.setDefaultRenderer(Object.class, new TableCellRender());
    }

    private void searchCustomer() {
        progress.setIndeterminate(true);
        inventoryRepo.getCustomer()
                .doOnSuccess((t) -> {
                    customerTabelModel.setListCustomer(t);
                    lblRecord.setText(String.valueOf(t.size()));
                })
                .doOnTerminate(() -> {
                    progress.setIndeterminate(false);
                    ComponentUtil.scrollTable(tblCustomer, row, 0);
                }).subscribe();

    }

    private void setCustomer(Trader cus) {
        customer = cus;
        txtSysCode.setText(customer.getKey().getCode());
        txtCusCode.setText(customer.getUserCode());
        txtConPerson.setText(customer.getContactPerson());
        txtCusName.setText(customer.getTraderName());
        txtCusEmail.setText(customer.getEmail());
        txtNRC.setText(customer.getNrc());
        txtRFID.setText(customer.getRfId());
        txtRemark.setText(txtRemark.getText());
        txtCusPhone.setText(customer.getPhone());
        txtCusAddress.setText(customer.getAddress());
        chkActive.setSelected(customer.isActive());
        txtCreditLimit.setText(Util1.getString(cus.getCreditLimit()));
        spPercent.setValue(Util1.getInteger(cus.getCreditDays()));
        txtCreditAmt.setValue(customer.getCreditAmt());
        chkMulti.setSelected(customer.isMulti());
        txtPrice.setText(customer.getPriceType());
        txtCusName.requestFocus();
        lblStatus.setText("EDIT");
        inventoryRepo.findRegion(customer.getRegCode()).doOnSuccess((t) -> {
            regionAutoCompleter.setRegion(t);
        }).subscribe();
        userRepo.findCountry(customer.getCountryCode()).doOnSuccess((t) -> {
            countryAutoCompleter.setCountry(t);
        }).subscribe();
        inventoryRepo.findTraderGroup(customer.getGroupCode(), Global.deptId).doOnSuccess((t) -> {
            traderGroupAutoCompleter.setGroup(t);
        }).subscribe();
        accountRepo.findCOA(customer.getAccount()).doOnSuccess((t) -> {
            cOAAutoCompleter.setCoa(t);
        }).subscribe();

    }

    private boolean isValidEntry() {
        boolean status = true;
        if (txtCusName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(Global.parentForm, "Trader Name can't be empty");
            txtCusName.requestFocus();
            status = false;
        } else {
            customer.setUserCode(txtCusCode.getText());
            customer.setTraderName(txtCusName.getText());
            customer.setContactPerson(txtConPerson.getText());
            customer.setPhone(txtCusPhone.getText());
            customer.setEmail(txtCusEmail.getText());
            customer.setAddress(txtCusAddress.getText());
            customer.setActive(chkActive.isSelected());
            customer.setCreditLimit(Util1.getInteger(txtCreditLimit.getText()));
            customer.setCreditDays(Util1.getInteger(spPercent.getValue()));
            customer.setNrc(txtNRC.getText());
            customer.setRfId(txtRFID.getText());
            customer.setRemark(txtRemark.getText());
            customer.setCreditAmt(Util1.getFloat(txtCreditAmt.getValue()));
            Region r = regionAutoCompleter.getRegion();
            if (r != null) {
                customer.setRegCode(r.getKey().getRegCode());
            }
            Country c = countryAutoCompleter.getCountry();
            if (c != null) {
                customer.setCountryCode(c.getCode());
            }
            customer.setType("CUS");
            customer.setCashDown(false);
            customer.setMulti(chkMulti.isSelected());
            customer.setPriceType(Util1.isNull(txtPrice.getText(), "N"));
            TraderGroup t = traderGroupAutoCompleter.getGroup();
            if (t != null) {
                customer.setGroupCode(t.getKey().getGroupCode());
            }
            if (cOAAutoCompleter != null) {
                ChartOfAccount coa = cOAAutoCompleter.getCOA();
                if (coa != null) {
                    customer.setAccount(coa.getKey().getCoaCode());
                } else {
                    customer.setAccount(ProUtil.getProperty(ProUtil.DEBTOR_ACC));
                }
            }
            if (lblStatus.getText().equals("NEW")) {
                customer.setMacId(Global.macId);
                customer.setCreatedBy(Global.loginUser.getUserCode());
                customer.setCreatedDate(LocalDateTime.now());
                customer.setDeptId(Global.deptId);
                TraderKey key = new TraderKey();
                key.setCompCode(Global.compCode);
                key.setCode(null);
                customer.setKey(key);
            } else {
                customer.setUpdatedBy(Global.loginUser.getUserCode());
            }
        }
        return status;
    }

    private void saveCustomer() {
        if (isValidEntry()) {
            progress.setIndeterminate(true);
            observer.selected("save", false);
            inventoryRepo.saveTrader(customer).doOnSuccess((t) -> {
                if (lblStatus.getText().equals("EDIT")) {
                    customerTabelModel.setCustomer(row, customer);
                } else {
                    customerTabelModel.addCustomer(customer);
                }
                clear();
                sendMessage(t.getTraderName());
            }).doOnError((e) -> {
                observer.selected("save", true);
                progress.setIndeterminate(false);
                JOptionPane.showMessageDialog(this, e.getMessage());
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
        customer = new Trader();
        txtSysCode.setText(null);
        txtCusCode.setText(null);
        txtCusName.setText(null);
        txtCusEmail.setText(null);
        txtNRC.setText(null);
        txtRFID.setText(null);
        txtRemark.setText(null);
        txtCusPhone.setText(null);
        regionAutoCompleter.setRegion(null);
        countryAutoCompleter.setCountry(null);
        txtCusAddress.setText(null);
        chkActive.setSelected(true);
        chkMulti.setSelected(false);
        txtCreditLimit.setText(null);
        lblStatus.setText("NEW");
        txtConPerson.setText(null);
        //txtCreditTerm.setText(null);
        txtCreditAmt.setValue(null);
        txtPrice.setText("N");
        customerTabelModel.refresh();
        txtCusCode.requestFocus();
        lblRecord.setText(String.valueOf(customerTabelModel.getListCustomer().size()));
        traderGroupAutoCompleter.setGroup(null);
        if (cOAAutoCompleter != null) {
            cOAAutoCompleter.setCoa(null);
        }
        spPercent.setValue(0);
        assignDefault();
    }

    private void setCustomer() {
        row = tblCustomer.convertRowIndexToModel(tblCustomer.getSelectedRow());
        if (row >= 0) {
            setCustomer(customerTabelModel.getCustomer(row));
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

    private void customerImportDialog() {
        customerImportDialog = new CustomerImportDialog(Global.parentForm);
        customerImportDialog.setTaskExecutor(taskExecutor);
        customerImportDialog.setInventoryRepo(inventoryRepo);
        customerImportDialog.setUserRepo(userRepo);
        customerImportDialog.setObserver(this);
        customerImportDialog.setLocationRelativeTo(null);
        customerImportDialog.setVisible(true);
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
        txtCreditLimit = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtConPerson = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        chkActive = new javax.swing.JCheckBox();
        lblStatus = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        txtRegion = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        txtSysCode = new javax.swing.JTextField();
        chkMulti = new javax.swing.JCheckBox();
        jLabel12 = new javax.swing.JLabel();
        txtPrice = new javax.swing.JTextField();
        lblGroup = new javax.swing.JLabel();
        txtGroup = new javax.swing.JTextField();
        btnGroup = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        lblGroup1 = new javax.swing.JLabel();
        txtAccount = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtNRC = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txtRFID = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        txtCreditAmt = new javax.swing.JFormattedTextField();
        spPercent = new javax.swing.JSpinner();
        btnDownload = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        txtCountry = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        scroll = new javax.swing.JScrollPane();
        tblCustomer = new javax.swing.JTable();
        jLabel6 = new javax.swing.JLabel();
        lblRecord = new javax.swing.JLabel();

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

        txtCreditLimit.setFont(Global.textFont);
        txtCreditLimit.setName("txtCreditLimit"); // NOI18N

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Credit Limit");

        txtConPerson.setFont(Global.textFont);
        txtConPerson.setName("txtConPerson"); // NOI18N
        txtConPerson.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtConPersonActionPerformed(evt);
            }
        });

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Contact Person");

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Region");

        chkActive.setFont(Global.lableFont);
        chkActive.setSelected(true);
        chkActive.setText("Active");
        chkActive.setName("chkActive"); // NOI18N

        lblStatus.setFont(Global.lableFont);
        lblStatus.setText("NEW");

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Credit Day");

        jButton1.setFont(Global.lableFont);
        jButton1.setText("Import");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

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

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Sys Code");

        txtSysCode.setEditable(false);
        txtSysCode.setFont(Global.textFont);
        txtSysCode.setName("txtCusCode"); // NOI18N
        txtSysCode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSysCodeKeyReleased(evt);
            }
        });

        chkMulti.setFont(Global.lableFont);
        chkMulti.setText("Multi Use");
        chkMulti.setName("chkActive"); // NOI18N
        chkMulti.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkMultiActionPerformed(evt);
            }
        });

        jLabel12.setFont(Global.lableFont);
        jLabel12.setText("Price");

        txtPrice.setFont(Global.textFont);
        txtPrice.setName("txtCreditTerm"); // NOI18N
        txtPrice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPriceActionPerformed(evt);
            }
        });

        lblGroup.setFont(Global.lableFont);
        lblGroup.setText("Group");

        txtGroup.setFont(Global.textFont);
        txtGroup.setName("txtCreditTerm"); // NOI18N

        btnGroup.setBackground(Global.selectionColor);
        btnGroup.setFont(Global.lableFont);
        btnGroup.setForeground(new java.awt.Color(255, 255, 255));
        btnGroup.setText("...");
        btnGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGroupActionPerformed(evt);
            }
        });

        lblGroup1.setFont(Global.lableFont);
        lblGroup1.setText("Account");

        txtAccount.setFont(Global.textFont);
        txtAccount.setName("txtCreditTerm"); // NOI18N

        jLabel13.setFont(Global.lableFont);
        jLabel13.setText("NRC");

        txtNRC.setFont(Global.textFont);
        txtNRC.setName("txtCusEmail"); // NOI18N

        jLabel14.setFont(Global.lableFont);
        jLabel14.setText("Remark");

        txtRemark.setFont(Global.textFont);
        txtRemark.setName("txtCusEmail"); // NOI18N

        jLabel15.setFont(Global.lableFont);
        jLabel15.setText("RFID");

        txtRFID.setFont(Global.textFont);
        txtRFID.setName("txtCusEmail"); // NOI18N

        jLabel16.setFont(Global.lableFont);
        jLabel16.setText("Credit Amt");

        txtCreditAmt.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtCreditAmt.setFont(Global.textFont);

        spPercent.setFont(Global.textFont);

        btnDownload.setFont(Global.lableFont);
        btnDownload.setText("Download");
        btnDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDownloadActionPerformed(evt);
            }
        });

        jLabel17.setFont(Global.lableFont);
        jLabel17.setText("Country");

        txtCountry.setFont(Global.textFont);
        txtCountry.setName("txtCusEmail"); // NOI18N

        jButton3.setBackground(Global.selectionColor);
        jButton3.setFont(Global.lableFont);
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("...");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelEntryLayout = new javax.swing.GroupLayout(panelEntry);
        panelEntry.setLayout(panelEntryLayout);
        panelEntryLayout.setHorizontalGroup(
            panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelEntryLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator1)
                    .addGroup(panelEntryLayout.createSequentialGroup()
                        .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(lblStatus))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelEntryLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelEntryLayout.createSequentialGroup()
                                        .addComponent(chkMulti)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(chkActive))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelEntryLayout.createSequentialGroup()
                                        .addComponent(btnDownload)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton1))))
                            .addComponent(txtCusAddress)
                            .addGroup(panelEntryLayout.createSequentialGroup()
                                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelEntryLayout.createSequentialGroup()
                                        .addComponent(txtRegion)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(txtRFID)
                                    .addComponent(txtCusEmail, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txtConPerson)
                                    .addComponent(txtSysCode)
                                    .addComponent(txtCusName)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelEntryLayout.createSequentialGroup()
                                        .addComponent(txtCountry)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(txtPrice)
                                    .addComponent(txtCreditLimit))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(panelEntryLayout.createSequentialGroup()
                                        .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel1)
                                            .addComponent(lblGroup1, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lblGroup, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtRemark, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(panelEntryLayout.createSequentialGroup()
                                                .addComponent(txtNRC)
                                                .addGap(1, 1, 1))
                                            .addComponent(txtCusPhone, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(txtAccount, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(txtCusCode, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(panelEntryLayout.createSequentialGroup()
                                                .addComponent(txtGroup)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnGroup, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addGroup(panelEntryLayout.createSequentialGroup()
                                        .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtCreditAmt)
                                            .addComponent(spPercent))))))))
                .addContainerGap())
        );

        panelEntryLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel11, jLabel13, jLabel14, jLabel16, jLabel3, lblGroup, lblGroup1});

        panelEntryLayout.setVerticalGroup(
            panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEntryLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelEntryLayout.createSequentialGroup()
                        .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(txtSysCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)
                            .addComponent(txtCusCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel2)
                                .addComponent(txtCusName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(lblGroup1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtAccount, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(txtConPerson, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(txtCusPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(txtCusEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13)
                            .addComponent(txtNRC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(txtRFID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14)
                            .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtRegion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9)
                            .addComponent(jButton2)
                            .addComponent(lblGroup, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnGroup))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtCountry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17)
                            .addComponent(jButton3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(txtCreditLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panelEntryLayout.createSequentialGroup()
                        .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(spPercent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtCreditAmt)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12)
                    .addComponent(txtPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtCusAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkMulti)
                    .addComponent(chkActive)
                    .addComponent(lblStatus))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(btnDownload))
                .addContainerGap(78, Short.MAX_VALUE))
        );

        panelEntryLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtConPerson, txtCreditLimit, txtCusAddress, txtCusCode, txtCusEmail, txtCusName, txtCusPhone});

        panelEntryLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtAccount, txtGroup});

        panelEntryLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jLabel10, jLabel2, jLabel3, jLabel4, jLabel5, jLabel7, jLabel8, jLabel9, lblGroup, lblGroup1});

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
        tblCustomer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCustomerMouseClicked(evt);
            }
        });
        tblCustomer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblCustomerKeyReleased(evt);
            }
        });
        scroll.setViewportView(tblCustomer);

        jLabel6.setText("Record :");

        lblRecord.setText("0");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblRecord, javax.swing.GroupLayout.DEFAULT_SIZE, 501, Short.MAX_VALUE)
                        .addGap(561, 561, 561))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(scroll)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelEntry, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelEntry, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scroll))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(lblRecord))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtCusCodeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCusCodeKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCusCodeKeyReleased

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observeMain();
    }//GEN-LAST:event_formComponentShown

    private void tblCustomerKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblCustomerKeyReleased
        // TODO add your handling code here:

    }//GEN-LAST:event_tblCustomerKeyReleased

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        customerImportDialog();

    }//GEN-LAST:event_jButton1ActionPerformed

    private void txtConPersonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtConPersonActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_txtConPersonActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        regionSetup();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void txtCusAddressKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCusAddressKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCusAddressKeyReleased

    private void txtSysCodeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSysCodeKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSysCodeKeyReleased

    private void chkMultiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkMultiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkMultiActionPerformed

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

    private void txtPriceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPriceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPriceActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDownloadActionPerformed
        printFile();
    }//GEN-LAST:event_btnDownloadActionPerformed

    private void btnDownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        printFile();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void tblCustomerMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCustomerMouseClicked
        // TODO add your handling code here:
        setCustomer();
    }//GEN-LAST:event_tblCustomerMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDownload;
    private javax.swing.JButton btnGroup;
    private javax.swing.JCheckBox chkActive;
    private javax.swing.JCheckBox chkMulti;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblGroup;
    private javax.swing.JLabel lblGroup1;
    private javax.swing.JLabel lblRecord;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPanel panelEntry;
    private javax.swing.JScrollPane scroll;
    private javax.swing.JSpinner spPercent;
    private javax.swing.JTable tblCustomer;
    private javax.swing.JTextField txtAccount;
    private javax.swing.JTextField txtConPerson;
    private javax.swing.JTextField txtCountry;
    private javax.swing.JFormattedTextField txtCreditAmt;
    private javax.swing.JTextField txtCreditLimit;
    private javax.swing.JTextField txtCusAddress;
    private javax.swing.JTextField txtCusCode;
    private javax.swing.JTextField txtCusEmail;
    private javax.swing.JTextField txtCusName;
    private javax.swing.JTextField txtCusPhone;
    private javax.swing.JTextField txtGroup;
    private javax.swing.JTextField txtNRC;
    private javax.swing.JTextField txtPrice;
    private javax.swing.JTextField txtRFID;
    private javax.swing.JTextField txtRegion;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JTextField txtSysCode;
    // End of variables declaration//GEN-END:variables

    private void initKeyListener() {
        txtCusCode.addKeyListener(this);
        txtCusName.addKeyListener(this);
        txtCusPhone.addKeyListener(this);
        txtCusAddress.addKeyListener(this);
        txtCusEmail.addKeyListener(this);
        txtCreditLimit.addKeyListener(this);
        txtConPerson.addKeyListener(this);
        chkActive.addKeyListener(this);
        tblCustomer.addKeyListener(this);

    }

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
            case "txtCusName":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtConPerson.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {

                }
                tabToTable(e);
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
            case "txtCusPhone":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtCusEmail.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtConPerson.requestFocus();

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
            case "cboPriceType":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtCreditLimit.requestFocus();
                        break;
                    /*case KeyEvent.VK_UP:
                        cboAccount.requestFocus();
                        break;
                     */
                    case KeyEvent.VK_RIGHT:
                        txtCreditLimit.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        break;
                }
                tabToTable(e);

                break;

            case "txtCreditTerm":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    chkActive.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtCreditLimit.requestFocus();
                }
                tabToTable(e);

                break;
            case "txtCreditLimit":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                }
                tabToTable(e);

                break;
            case "chkActive":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtConPerson.requestFocus();
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
                    row = tblCustomer.convertRowIndexToModel(tblCustomer.getSelectedRow());
                    setCustomer(customerTabelModel.getCustomer(row));
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
        if (row >= 0) {
            Trader t = customerTabelModel.getCustomer(row);
            progress.setIndeterminate(true);
            int yn = JOptionPane.showConfirmDialog(this, "Are you sure to deleted?", "Customer Delete", JOptionPane.YES_OPTION);
            if (yn == JOptionPane.YES_OPTION) {
                inventoryRepo.deleteTrader(t.getKey()).doOnSuccess((list) -> {
                    if (list.isEmpty()) {
                        customerTabelModel.deleteCustomer(row);
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
        searchCustomer();
    }

    @Override
    public void filter() {
        findDialog.setVisible(!findDialog.isVisible());
    }

    @Override
    public String panelName() {
        return this.getName();
    }

    @Override
    public void selected(Object source, Object selectObj) {
        if (source.equals("Trader")) {
            if (selectObj instanceof Trader t) {
                customerTabelModel.addCustomer(t);
            }
        }
    }

    private void printFile() {
        try {
            progress.setIndeterminate(true);
            String CSV_FILE_PATH = "customers.csv";
            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                    .setHeader(Header.class)
                    .setSkipHeaderRecord(false)
                    .build();
            FileWriter out = new FileWriter(CSV_FILE_PATH);
            try (CSVPrinter printer = csvFormat.print(out)) {
                printer.flush();
            }
            progress.setIndeterminate(false);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

}
