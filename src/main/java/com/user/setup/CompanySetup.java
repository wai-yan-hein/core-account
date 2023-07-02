/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.user.setup;

import com.repo.AccountRepo;
import com.common.EncryptUtil;
import com.common.Global;
import com.common.PanelControl;
import com.common.QRUtil;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.common.Util1;
import com.user.common.CompanyTableModel;
import com.repo.UserRepo;
import com.user.dialog.DepartmentSetupDialog;
import com.user.dialog.QRDialog;
import com.user.dialog.YearEndProcessingDailog;
import com.user.editor.BusinessTypeAutoCompleter;
import com.user.editor.CurrencyAutoCompleter;
import com.user.model.CompanyInfo;
import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.time.LocalDateTime;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 *
 * @author Lenovo
 */
@Slf4j
@Component
public class CompanySetup extends javax.swing.JPanel implements KeyListener, PanelControl {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private Environment environment;
    private int selectRow = -1;
    private CompanyInfo companyInfo = new CompanyInfo();
    private CurrencyAutoCompleter currencyAutoCompleter;
    private BusinessTypeAutoCompleter businessTypeAutoCompleter;
    private final CompanyTableModel tableModel = new CompanyTableModel();
    private SelectionObserver observer;
    private JProgressBar progress;

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
     * Creates new form Company
     */
    public CompanySetup() {
        initComponents();
    }

    public void initMain() {
        initCombo();
        initKeyListener();
        initTable();
    }

    private void initTable() {
        tblCompany.setModel(tableModel);
        tblCompany.getTableHeader().setFont(Global.textFont);
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
        txtCode.requestFocus();
    }

    private void searchCompany() {
        userRepo.getCompany(false).subscribe((t) -> {
            tableModel.setListCompany(t);
        });
    }

    private void initCombo() {
        currencyAutoCompleter = new CurrencyAutoCompleter(txtCurrency, null);
        userRepo.getCurrency().subscribe((t) -> {
            currencyAutoCompleter.setListCurrency(t);
        });
        userRepo.getDefaultCurrency().subscribe((c) -> {
            currencyAutoCompleter.setCurrency(c);
        });
        userRepo.getBusinessType().subscribe((t) -> {
            businessTypeAutoCompleter = new BusinessTypeAutoCompleter(txtBusType, t, null, false);
        });
    }

    private void setCompanyInfo(CompanyInfo cInfo) {
        companyInfo = cInfo;
        userRepo.findCurrency(companyInfo.getCurCode()).subscribe((t) -> {
            currencyAutoCompleter.setCurrency(t);
        });
        Integer busId = companyInfo.getBusId();
        if (busId != null) {
            userRepo.find(busId).subscribe((t) -> {
                businessTypeAutoCompleter.setObject(t);
            });
        }
        txtCode.setText(companyInfo.getCompCode());
        txtUserCode.setText(companyInfo.getUserCode());
        txtName.setText(companyInfo.getCompName());
        txtPhone.setText(companyInfo.getCompPhone());
        txtEmail.setText(companyInfo.getCompEmail());
        txtAddress.setText(companyInfo.getCompAddress());
        txtFromDate.setDate(companyInfo.getStartDate());
        txtToDate.setDate(companyInfo.getEndDate());
        chkActive.setSelected(companyInfo.isActive());
        lblStatus.setText("EDIT");
        txtBusType.setEditable(companyInfo.getBusId() == null);
    }

    private void saveCompany() {
        if (isValidEntry()) {
            try {
                progress.setIndeterminate(true);
                String status = lblStatus.getText();
                userRepo.saveCompany(companyInfo)
                        .flatMap(c -> {
                            if (c == null) {
                                return Mono.just(Boolean.FALSE);
                            } else {
                                if (status.equals("NEW")) {
                                    tableModel.addCompany(c);
                                } else {
                                    tableModel.setCompany(selectRow, c);
                                }
                                updateCompany(c);
                                return accountRepo.saveCOAFromTemplate(companyInfo.getBusId(), c.getCompCode())
                                        .doOnNext(t -> {
                                            clear();
                                            progress.setIndeterminate(false);
                                        })
                                        .thenReturn(Boolean.TRUE);
                            }
                        }).subscribe(sta -> {
                }, err -> {
                    JOptionPane.showMessageDialog(this, err.getMessage());
                    progress.setIndeterminate(false);
                });
            } catch (HeadlessException e) {
                log.error("Save Company :" + e.getMessage());
                JOptionPane.showMessageDialog(Global.parentForm, "Could'nt saved.");
            }
        }
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
            companyInfo.setBusId(businessTypeAutoCompleter.getObject().getBusId());
            companyInfo.setCreatedBy(Global.loginUser.getUserCode());
            companyInfo.setCreatedDate(LocalDateTime.now());
            companyInfo.setCompCode(txtCode.getText());
            companyInfo.setUserCode(txtUserCode.getText());
            companyInfo.setCompName(txtName.getText());
            companyInfo.setCompPhone(txtPhone.getText());
            companyInfo.setCompEmail(txtEmail.getText());
            companyInfo.setCompAddress(txtAddress.getText());
            companyInfo.setStartDate(txtFromDate.getDate());
            companyInfo.setEndDate(txtToDate.getDate());
            companyInfo.setActive(chkActive.isSelected());
            companyInfo.setCurCode(currencyAutoCompleter.getCurrency().getCurCode());
        }
        return status;

    }

    private void clear() {
        txtCode.setText(null);
        txtUserCode.setText(null);
        txtName.setText(null);
        txtPhone.setText(null);
        txtEmail.setText(null);
        txtSecurityCode.setText(null);
        txtAddress.setText(null);
        txtFromDate.setDate(null);
        txtToDate.setDate(null);
        chkActive.setSelected(Boolean.TRUE);
        lblStatus.setText("NEW");
        companyInfo = new CompanyInfo();
        txtUserCode.requestFocus();
        txtBusType.setEditable(true);
        currencyAutoCompleter.setCurrency(null);
        businessTypeAutoCompleter.setObject(null);
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
        txtSecurityCode.addKeyListener(this);
        txtAddress.addKeyListener(this);
        txtFromDate.addKeyListener(this);
        txtToDate.addKeyListener(this);
        chkActive.addKeyListener(this);
        tblCompany.addKeyListener(this);
    }

    private void yearEndDialog() {
        YearEndProcessingDailog d = new YearEndProcessingDailog(Global.parentForm);
        d.setUserRepo(userRepo);
        d.setAccountRepo(accountRepo);
        d.initMain();
        d.setLocationRelativeTo(null);
        d.setVisible(true);
    }

    private void departmentDialog() {
        DepartmentSetupDialog d = new DepartmentSetupDialog(Global.parentForm);
        d.setUserRepo(userRepo);
        d.initMain();
        d.setLocationRelativeTo(null);
        d.setVisible(true);
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
        jPanel1 = new javax.swing.JPanel();
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
        txtSecurityCode = new javax.swing.JTextField();
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
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

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

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

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

        txtSecurityCode.setFont(Global.textFont);
        txtSecurityCode.setName("txtSecurityCode"); // NOI18N

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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCode)
                            .addComponent(txtUserCode)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtSecurityCode, javax.swing.GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE)
                            .addComponent(txtEmail, javax.swing.GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE)
                            .addComponent(txtPhone, javax.swing.GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE)
                            .addComponent(txtName, javax.swing.GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE)
                            .addComponent(txtAddress, javax.swing.GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(txtCurrency))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtToDate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtFromDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(txtBusType))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(chkActive)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblStatus)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtUserCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtSecurityCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtFromDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtToDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(txtBusType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkActive, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(34, Short.MAX_VALUE))
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

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(jButton3))
                .addContainerGap(94, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 397, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jPanel1, jScrollPane1});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observer.selected("control", this);
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkActive;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblCompany;
    private javax.swing.JTextField txtAddress;
    private javax.swing.JTextField txtBusType;
    private javax.swing.JTextField txtCode;
    private javax.swing.JTextField txtCurrency;
    private javax.swing.JTextField txtEmail;
    private com.toedter.calendar.JDateChooser txtFromDate;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtPhone;
    private javax.swing.JTextField txtSecurityCode;
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
    }

    @Override
    public void filter() {
    }

    @Override
    public String panelName() {
        return this.getName();
    }

}
