/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.user.setup;

import com.acc.dialog.ShootTriMessageDialog;
import com.common.ComponentUtil;
import com.repo.AccountRepo;
import com.common.EncryptUtil;
import com.common.Global;
import com.common.OfflineOptionPane;
import com.common.PanelControl;
import com.common.QRUtil;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.common.Util1;
import com.inventory.entity.Message;
import com.inventory.entity.MessageType;
import com.user.common.CompanyTableModel;
import com.repo.UserRepo;
import com.ui.SecurityDialog;
import com.user.dialog.DateLockDialog;
import com.user.dialog.DepartmentSetupDialog;
import com.user.dialog.MachineInfoDialog;
import com.user.dialog.QRDialog;
import com.user.dialog.YearEndFixDialog;
import com.user.dialog.YearEndProcessingDailog;
import com.user.editor.BusinessTypeAutoCompleter;
import com.user.editor.CurrencyAutoCompleter;
import com.user.model.CompanyInfo;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.time.LocalDateTime;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.web.reactive.function.client.WebClientRequestException;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class CompanySetup extends javax.swing.JPanel implements KeyListener, PanelControl {

    @Setter
    private UserRepo userRepo;
    @Setter
    private AccountRepo accountRepo;
    @Setter
    private Environment environment;
    @Setter
    private String token;
    @Setter
    private JProgressBar progress;
    @Setter
    private SelectionObserver observer;

    private int selectRow = -1;
    private CompanyInfo company = new CompanyInfo();
    private CurrencyAutoCompleter currencyAutoCompleter;
    private BusinessTypeAutoCompleter businessTypeAutoCompleter;
    private final CompanyTableModel tableModel = new CompanyTableModel();
    private DateLockDialog dateLockDialog;
    private MachineInfoDialog machineInfoDialog;
    private YearEndFixDialog yeFixDialog;
    private DepartmentSetupDialog depDialog;

    /**
     * Creates new form Company
     */
    public CompanySetup() {
        initComponents();
    }

    public void initMain() {
        initCombo();
        initKeyListener();
        initTable();
        assignDefault();
        ComponentUtil.addFocusListener(panelEntry);
    }

    private void initTable() {
        tblCompany.setModel(tableModel);
        tblCompany.getTableHeader().setFont(Global.tblHeaderFont);
        tblCompany.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCompany.setDefaultRenderer(Object.class, new TableCellRender());
        tblCompany.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting()) {
                if (tblCompany.getSelectedRow() >= 0) {
                    selectRow = tblCompany.convertRowIndexToModel(tblCompany.getSelectedRow());
                    CompanyInfo com = tableModel.getCompany(selectRow);
                    setCompanyInfo(com);
                }

            }
        });
        searchCompany();
    }

    private void searchCompany() {
        userRepo.getCompany(false).doOnSuccess((t) -> {
            tableModel.setListCompany(t);
        }).doOnTerminate(() -> {
            txtCode.requestFocus();
        }).subscribe();
    }

    private void initCombo() {
        currencyAutoCompleter = new CurrencyAutoCompleter(txtCurrency, null);
        userRepo.getCurrency().doOnSuccess((t) -> {
            currencyAutoCompleter.setListCurrency(t);
        }).subscribe();
        userRepo.getDefaultCurrency().doOnSuccess((c) -> {
            currencyAutoCompleter.setCurrency(c);
        }).subscribe();
        userRepo.getBusinessType().doOnSuccess((t) -> {
            businessTypeAutoCompleter = new BusinessTypeAutoCompleter(txtBusType, t, null, false);
        }).subscribe();
    }

    private void assignDefault() {
        txtFromDate.setDate(Util1.getTodayDate());
        txtToDate.setDate(Util1.getTodayDate());
        panelAdv.setVisible(chkSync.isSelected());
        btnReset.setEnabled(false);
    }

    private void setCompanyInfo(CompanyInfo cInfo) {
        company = cInfo;
        company.setToken(token);
        userRepo.findCurrency(company.getCurCode()).doOnSuccess((t) -> {
            currencyAutoCompleter.setCurrency(t);
        }).subscribe();
        userRepo.find(company.getBusId()).doOnSuccess((t) -> {
            businessTypeAutoCompleter.setObject(t);
        }).subscribe();
        txtCode.setText(company.getCompCode());
        txtUserCode.setText(company.getUserCode());
        txtName.setText(company.getCompName());
        txtPhone.setText(company.getCompPhone());
        txtEmail.setText(company.getCompEmail());
        txtAddress.setText(company.getCompAddress());
        txtFromDate.setDate(Util1.convertToDate(company.getStartDate()));
        txtToDate.setDate(Util1.convertToDate(company.getEndDate()));
        txtSecurity.setText(company.getSecurityCode());
        chkActive.setSelected(company.isActive());
        chkSync.setSelected(company.isSync());
        txtReportCompany.setText(company.getReportCompany());
        txtReportUrl.setText(company.getReportUrl());
        lblStatus.setText("EDIT");
        txtBusType.setEditable(company.getBusId() == null);
        btnReset.setEnabled(true);
    }

    private void saveCompany() {
        if (isValidEntry()) {
            progress.setIndeterminate(true);
            observer.selected("save", false);
            String status = lblStatus.getText();
            userRepo.saveCompany(company).doOnSuccess((t) -> {
                if (status.equals("NEW")) {
                    tableModel.addCompany(t);
                } else {
                    tableModel.setCompany(selectRow, t);
                }
                updateCompany(t);
                clear();
                sendMessage(t.getCompName());
            }).doOnError((e) -> {
                log.error("saveCompany : " + e.getMessage());
                progress.setIndeterminate(false);
                observer.selected("save", true);
                if (e instanceof WebClientRequestException) {
                    OfflineOptionPane pane = new OfflineOptionPane();
                    JDialog dialog = pane.createDialog("Offline");
                    dialog.setVisible(true);
                    int yn = (int) pane.getValue();
                    if (yn == JOptionPane.YES_OPTION) {
                        saveCompany();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Error : " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }).subscribe();
        }
    }

    private void sendMessage(String mes) {
        userRepo.sendDownloadMessage(MessageType.COMPANY, mes)
                .doOnSuccess((t) -> {
                    log.info(t);
                }).subscribe();
    }

    private boolean isValidEntry() {
        boolean status = true;
        if (txtName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(Global.parentForm, "Invalid Name");
            txtName.requestFocus();
            status = false;
        } else if (txtFromDate.getDate() == null || txtToDate.getDate() == null) {
            status = false;
            JOptionPane.showMessageDialog(Global.parentForm, "Invalid Companay Date");
            txtFromDate.requestFocus();
        } else if (currencyAutoCompleter.getCurrency() == null) {
            status = false;
            JOptionPane.showMessageDialog(Global.parentForm, "Invalid Currency.");
            txtCurrency.requestFocus();
        } else if (businessTypeAutoCompleter.getObject() == null) {
            status = false;
            JOptionPane.showMessageDialog(Global.parentForm, "Invalid Business Type.");
            txtBusType.requestFocus();
        } else {
            company.setBusId(businessTypeAutoCompleter.getObject().getBusId());
            company.setCreatedBy(Global.loginUser.getUserCode());
            company.setCreatedDate(LocalDateTime.now());
            company.setCompCode(txtCode.getText());
            company.setUserCode(txtUserCode.getText());
            company.setCompName(txtName.getText());
            company.setCompPhone(txtPhone.getText());
            company.setCompEmail(txtEmail.getText());
            company.setCompAddress(txtAddress.getText());
            company.setSecurityCode(String.valueOf(txtSecurity.getPassword()));
            company.setStartDate(Util1.toLocalDate(txtFromDate.getDate()));
            company.setEndDate(Util1.toLocalDate(txtToDate.getDate()));
            company.setActive(chkActive.isSelected());
            company.setCurCode(currencyAutoCompleter.getCurrency().getCurCode());
            company.setUpdateMenu(chkMenuUpdate.isSelected());
            company.setToken(token);
            company.setReportCompany(txtReportCompany.getText());
            company.setReportUrl(txtReportUrl.getText());
            company.setSync(chkSync.isSelected());
        }
        return status;

    }

    private void clear() {
        txtCode.setText(null);
        txtUserCode.setText(null);
        txtName.setText(null);
        txtPhone.setText(null);
        txtEmail.setText(null);
        txtSecurity.setText(null);
        txtAddress.setText(null);
        txtFromDate.setDate(null);
        txtToDate.setDate(null);
        txtReportCompany.setText(null);
        txtReportUrl.setText(null);
        chkSync.setSelected(false);
        chkActive.setSelected(true);
        lblStatus.setText("NEW");
        company = new CompanyInfo();
        txtUserCode.requestFocus();
        txtBusType.setEditable(true);
        txtSecurity.setText(null);
        currencyAutoCompleter.setCurrency(null);
        businessTypeAutoCompleter.setObject(null);
        progress.setIndeterminate(false);
        assignDefault();
        observeMain();
    }

    private void updateCompany(CompanyInfo info) {
        if (Global.compCode.equals(info.getCompCode())) {
            Global.companyAddress = info.getCompAddress();
            Global.companyPhone = info.getCompPhone();
            Global.companyName = info.getCompName();
            Global.startDate = Util1.toDateStr(info.getStartDate(), "dd/MM/yyyy");
            Global.endate = Util1.toDateStr(info.getEndDate(), "dd/MM/yyyy");
            observer.selected("change-name", "change-name");
        }
    }

    private void initKeyListener() {
        txtCode.addKeyListener(this);
        txtUserCode.addKeyListener(this);
        txtName.addKeyListener(this);
        txtPhone.addKeyListener(this);
        txtEmail.addKeyListener(this);
        txtSecurity.addKeyListener(this);
        txtAddress.addKeyListener(this);
        txtFromDate.addKeyListener(this);
        txtToDate.addKeyListener(this);
        chkActive.addKeyListener(this);
        tblCompany.addKeyListener(this);
    }

    private void yearEndDialog() {
        YearEndProcessingDailog d = new YearEndProcessingDailog(Global.parentForm);
        d.setUserRepo(userRepo);
        d.setToken(token);
        d.initMain();
        d.setLocationRelativeTo(null);
        d.setVisible(true);
    }

    private void yearEndFixDialog() {
        if (yeFixDialog == null) {
            yeFixDialog = new YearEndFixDialog(Global.parentForm);
            yeFixDialog.setUserRepo(userRepo);
            yeFixDialog.setToken(token);
            yeFixDialog.initMain();
            yeFixDialog.setLocationRelativeTo(null);
        }
        yeFixDialog.searchCompany();
    }

    private void departmentDialog() {
        if (depDialog == null) {
            depDialog = new DepartmentSetupDialog(Global.parentForm);
            depDialog.setLocationRelativeTo(null);
            depDialog.setSize(Global.width - 200, Global.height - 200);
            depDialog.setUserRepo(userRepo);
            depDialog.setAccountRepo(accountRepo);
            depDialog.initMain();
        }
        depDialog.searchDepartment();
    }

    private void qrDialog() {
        String uPort = environment.getProperty("user.port");
        String iPort = environment.getProperty("inventory.port");
        String aPort = environment.getProperty("account.port");
        String hostIp = Util1.getServerIp(environment.getProperty("host.name"));
        String text = String.format("%s,%s,%s,%s", hostIp, uPort, iPort, aPort);
        String encodeStr = EncryptUtil.encode(text);
        QRDialog d = new QRDialog(Global.parentForm);
        d.setLocationRelativeTo(null);
        d.setImage(QRUtil.createdQRImage(encodeStr));
        d.setVisible(true);
    }

    private void showShootTriDialog() {
        ShootTriMessageDialog s = new ShootTriMessageDialog(Global.parentForm, true);
        s.setAccountRepo(accountRepo);
        s.initMain();
        s.setSize(Global.width - 200, Global.height - 200);
        s.setLocationRelativeTo(null);
        s.setVisible(true);
    }

    private void observeMain() {
        observer.selected("control", this);
        observer.selected("save", true);
        observer.selected("print", false);
        observer.selected("history", false);
        observer.selected("delete", false);
        observer.selected("refresh", true);
    }

    private void updateClient() {
        int yn = JOptionPane.showConfirmDialog(Global.parentForm, "Do you want release program update?", "Program Update.", JOptionPane.WARNING_MESSAGE);
        if (yn == JOptionPane.YES_OPTION) {
            Message message = new Message();
            message.setHeader("PROGRAM_UPDATE");
            userRepo.sendMessage(message).doOnSuccess((t) -> {
                JOptionPane.showMessageDialog(this, t);
            }).subscribe();
        }
    }

    private void dateLockDialog() {
        if (dateLockDialog == null) {
            dateLockDialog = new DateLockDialog(Global.parentForm);
            dateLockDialog.setUserRepo(userRepo);
            dateLockDialog.initMain();
            dateLockDialog.setSize(Global.width - 400, Global.height - 200);
            dateLockDialog.setLocationRelativeTo(null);
        }
        dateLockDialog.search();
    }

    private void machineDialog() {
        if (machineInfoDialog == null) {
            machineInfoDialog = new MachineInfoDialog(Global.parentForm);
            machineInfoDialog.setSize(Global.width - 300, Global.height - 300);
            machineInfoDialog.setLocationRelativeTo(null);
            machineInfoDialog.setUserRepo(userRepo);
            machineInfoDialog.initMain();
        }
        machineInfoDialog.search();
    }

    private void showSecurityDialog() {
        SecurityDialog dialog = new SecurityDialog(Global.parentForm);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        if (dialog.isCorrect()) {
            confirmToReset();
        }
    }

    private void confirmToReset() {
        int yn = JOptionPane.showConfirmDialog(Global.parentForm, "Do you want to reset program?", "Reset Program.", JOptionPane.WARNING_MESSAGE);
        if (yn == JOptionPane.YES_OPTION) {
            Message message = new Message();
            message.setHeader("RESET_PROGRAM");
            userRepo.cleanData(company).doOnSuccess((t) -> {
                JOptionPane.showMessageDialog(this, "Reset Success.");
            }).doOnError((e) -> {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }).subscribe();
        }
    }

    private void openSync() {
        SecurityDialog d = new SecurityDialog(Global.parentForm);
        d.setLocationRelativeTo(null);
        d.setVisible(true);
        panelAdv.setVisible(d.isCorrect());
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
        tblCompany = new javax.swing.JTable();
        panelEntry = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtCode = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtUserCode = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        txtPhone = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtAddress = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        chkActive = new javax.swing.JCheckBox();
        lblStatus = new javax.swing.JLabel();
        txtFromDate = new com.toedter.calendar.JDateChooser();
        txtToDate = new com.toedter.calendar.JDateChooser();
        jLabel11 = new javax.swing.JLabel();
        txtCurrency = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtBusType = new javax.swing.JTextField();
        txtSecurity = new javax.swing.JPasswordField();
        chkMenuUpdate = new javax.swing.JCheckBox();
        lblMessage = new javax.swing.JLabel();
        panelAdv = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        txtReportUrl = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txtReportCompany = new javax.swing.JTextField();
        chkSync = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        tblCompany.setFont(Global.textFont);
        tblCompany.setModel(new javax.swing.table.DefaultTableModel(
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
        tblCompany.setName("tblCompany"); // NOI18N
        tblCompany.setRowHeight(Global.tblRowHeight);
        jScrollPane1.setViewportView(tblCompany);

        panelEntry.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("System Code");

        txtCode.setEditable(false);
        txtCode.setFont(Global.textFont);
        txtCode.setName("txtCode"); // NOI18N

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("User Code");

        txtUserCode.setFont(Global.textFont);
        txtUserCode.setName("txtUserCode"); // NOI18N

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Name");

        txtName.setFont(Global.textFont);
        txtName.setName("txtName"); // NOI18N

        txtPhone.setFont(Global.textFont);
        txtPhone.setName("txtPhone"); // NOI18N

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Phone");

        txtEmail.setFont(Global.textFont);
        txtEmail.setName("txtEmail"); // NOI18N

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Email");

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Security Code");

        txtAddress.setFont(Global.textFont);
        txtAddress.setName("txtAddress"); // NOI18N

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Address");

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("From Date");

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("To Date");

        chkActive.setFont(Global.lableFont);
        chkActive.setSelected(true);
        chkActive.setText("Active");
        chkActive.setName("chkActive"); // NOI18N

        lblStatus.setFont(Global.amtFont);
        lblStatus.setText("NEW");

        txtFromDate.setDateFormatString("dd/MM/yyyy");
        txtFromDate.setFont(Global.textFont);

        txtToDate.setDateFormatString("dd/MM/yyyy");
        txtToDate.setFont(Global.textFont);

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Currency");

        txtCurrency.setFont(Global.textFont);
        txtCurrency.setName("txtAddress"); // NOI18N
        txtCurrency.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCurrencyActionPerformed(evt);
            }
        });

        jLabel13.setFont(Global.lableFont);
        jLabel13.setText("Buiness Type");

        txtBusType.setFont(Global.textFont);
        txtBusType.setName("txtAddress"); // NOI18N
        txtBusType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBusTypeActionPerformed(evt);
            }
        });

        txtSecurity.setFont(Global.textFont);

        chkMenuUpdate.setFont(Global.lableFont);
        chkMenuUpdate.setText("Update Menu From Template (Notice)");

        lblMessage.setFont(Global.lableFont);
        lblMessage.setText("-");

        panelAdv.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Report Url");

        txtReportUrl.setFont(Global.textFont);

        jLabel12.setFont(Global.lableFont);
        jLabel12.setText("Report Company");

        txtReportCompany.setFont(Global.textFont);

        javax.swing.GroupLayout panelAdvLayout = new javax.swing.GroupLayout(panelAdv);
        panelAdv.setLayout(panelAdvLayout);
        panelAdvLayout.setHorizontalGroup(
            panelAdvLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAdvLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelAdvLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelAdvLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtReportUrl)
                    .addComponent(txtReportCompany, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelAdvLayout.setVerticalGroup(
            panelAdvLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAdvLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelAdvLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtReportUrl)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelAdvLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtReportCompany)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        chkSync.setFont(Global.lableFont);
        chkSync.setText("Sync");
        chkSync.setName("chkActive"); // NOI18N
        chkSync.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSyncActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelEntryLayout = new javax.swing.GroupLayout(panelEntry);
        panelEntry.setLayout(panelEntryLayout);
        panelEntryLayout.setHorizontalGroup(
            panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEntryLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelEntryLayout.createSequentialGroup()
                        .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCode)
                            .addComponent(txtUserCode)))
                    .addGroup(panelEntryLayout.createSequentialGroup()
                        .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtEmail, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
                            .addComponent(txtPhone, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
                            .addComponent(txtName, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
                            .addComponent(txtAddress, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
                            .addComponent(txtSecurity)))
                    .addGroup(panelEntryLayout.createSequentialGroup()
                        .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelEntryLayout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(txtCurrency))
                            .addGroup(panelEntryLayout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtToDate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtFromDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(panelEntryLayout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(txtBusType))
                            .addGroup(panelEntryLayout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(chkActive)
                                .addGap(12, 12, 12)
                                .addComponent(chkSync)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblStatus))))
                    .addComponent(lblMessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelEntryLayout.createSequentialGroup()
                        .addComponent(chkMenuUpdate)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(panelAdv, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelEntryLayout.setVerticalGroup(
            panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEntryLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtUserCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtSecurity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtFromDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtToDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(txtBusType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkActive, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkSync, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblMessage)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelAdv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addComponent(chkMenuUpdate)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jButton1.setFont(Global.lableFont);
        jButton1.setText("Year End");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setFont(Global.lableFont);
        jButton2.setText("Department");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setFont(Global.lableFont);
        jButton3.setText("QR");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setFont(Global.lableFont);
        jButton4.setText("Update");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setFont(Global.lableFont);
        jButton5.setText("Shoot Tri");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setFont(Global.lableFont);
        jButton6.setText("Date Lock");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setFont(Global.lableFont);
        jButton7.setText("Machine");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        btnReset.setFont(Global.lableFont);
        btnReset.setText("Reset");
        btnReset.setEnabled(false);
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });

        jButton8.setFont(Global.lableFont);
        jButton8.setText("Year End Fix");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
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
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton7))
                    .addComponent(jButton2)
                    .addComponent(jButton3)
                    .addComponent(jButton4)
                    .addComponent(jButton5)
                    .addComponent(jButton6)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jButton8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnReset, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnReset, jButton1, jButton2, jButton3, jButton4, jButton5, jButton6, jButton7});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnReset))
                    .addComponent(jButton7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton8)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 432, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelEntry, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelEntry, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observeMain();
    }//GEN-LAST:event_formComponentShown

    private void txtCurrencyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCurrencyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCurrencyActionPerformed

    private void txtBusTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBusTypeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBusTypeActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        yearEndDialog();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        departmentDialog();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        qrDialog();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        updateClient();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        showShootTriDialog();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        dateLockDialog();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        machineDialog();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void chkSyncActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSyncActionPerformed
        // TODO add your handling code here:
        openSync();
    }//GEN-LAST:event_chkSyncActionPerformed

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        showSecurityDialog();        // TODO add your handling code here:
    }//GEN-LAST:event_btnResetActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
        yearEndFixDialog();
    }//GEN-LAST:event_jButton8ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnReset;
    private javax.swing.JCheckBox chkActive;
    private javax.swing.JCheckBox chkMenuUpdate;
    private javax.swing.JCheckBox chkSync;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblMessage;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPanel panelAdv;
    private javax.swing.JPanel panelEntry;
    private javax.swing.JTable tblCompany;
    private javax.swing.JTextField txtAddress;
    private javax.swing.JTextField txtBusType;
    private javax.swing.JTextField txtCode;
    private javax.swing.JTextField txtCurrency;
    private javax.swing.JTextField txtEmail;
    private com.toedter.calendar.JDateChooser txtFromDate;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtPhone;
    private javax.swing.JTextField txtReportCompany;
    private javax.swing.JTextField txtReportUrl;
    private javax.swing.JPasswordField txtSecurity;
    private com.toedter.calendar.JDateChooser txtToDate;
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

    }

    @Override
    public void delete() {
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
    public void save() {
        saveCompany();
    }

    @Override
    public void refresh() {
        searchCompany();
        initCombo();
    }

    @Override
    public void filter() {
    }

    @Override
    public String panelName() {
        return this.getName();
    }

}
