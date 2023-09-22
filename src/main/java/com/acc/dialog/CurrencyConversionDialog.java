/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.acc.dialog;

import com.acc.common.COAComboBoxModel;
import com.acc.common.DepartmentAccComboBoxModel;
import com.acc.model.ChartOfAccount;
import com.acc.model.DepartmentA;
import com.acc.model.Gl;
import com.acc.model.GlKey;
import com.common.Global;
import com.common.ProUtil;
import com.common.Util1;
import com.repo.AccountRepo;
import com.repo.UserRepo;
import com.user.common.CurrencyComboBoxModel;
import com.user.common.ExchangeRateComoboModel;
import com.user.model.Currency;
import com.user.model.ExchangeRate;
import java.awt.Color;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class CurrencyConversionDialog extends javax.swing.JDialog {

    private COAComboBoxModel srcComboBoxModel = new COAComboBoxModel();
    private COAComboBoxModel targetComboBoxModel = new COAComboBoxModel();
    private COAComboBoxModel conComboBoxModel = new COAComboBoxModel();
    private CurrencyComboBoxModel srcCurrencyComboBoxModel = new CurrencyComboBoxModel();
    private CurrencyComboBoxModel targetCurrencyComboBoxModel = new CurrencyComboBoxModel();
    private AccountRepo accountRepo;
    private ExchangeRateComoboModel exchangeRateComoboModel = new ExchangeRateComoboModel();
    private DepartmentAccComboBoxModel departmentAccComboBoxModel = new DepartmentAccComboBoxModel();
    private final String transSource = "EX";
    private UserRepo userRepo;
    private List<Gl> listGl;

    public void setAccountRepo(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Creates new form CurrencyConversionDialog
     *
     * @param frame
     */
    public CurrencyConversionDialog(JFrame frame) {
        super(frame, true);
        initComponents();
    }

    public void initMain() {
        assignDefaultValue();
        initData();
    }

    public void search(String vouNo) {
        if (!Util1.isNullOrEmpty(vouNo)) {
            progress.setIndeterminate(true);
            accountRepo.getJournal(vouNo).doOnSuccess((list) -> {
                listGl = list;
                Gl gl1 = list.get(0);
                Gl gl2 = list.get(1);
                userRepo.findExchange(gl1.getExCode()).doOnSuccess((t) -> {
                    exchangeRateComoboModel.setSelectedItem(t);
                    cboRate.repaint();
                }).subscribe();
                accountRepo.findCOA(gl1.getSrcAccCode()).doOnSuccess((t) -> {
                    srcComboBoxModel.setSelectedItem(t);
                    cboSource.repaint();
                }).subscribe();
                userRepo.findCurrency(gl1.getCurCode()).doOnSuccess((t) -> {
                    srcCurrencyComboBoxModel.setSelectedItem(t);
                    cboSourceCur.repaint();
                }).subscribe();
                accountRepo.findCOA(gl2.getSrcAccCode()).doOnSuccess((t) -> {
                    targetComboBoxModel.setSelectedItem(t);
                    cboTarget.repaint();
                }).subscribe();
                userRepo.findCurrency(gl2.getCurCode()).doOnSuccess((t) -> {
                    targetCurrencyComboBoxModel.setSelectedItem(t);
                    cboTargetCur.repaint();
                }).subscribe();
                accountRepo.findCOA(gl1.getAccCode()).doOnSuccess((t) -> {
                    conComboBoxModel.setSelectedItem(t);
                    cboConversion.repaint();
                }).subscribe();
                accountRepo.findDepartment(gl1.getDeptCode()).doOnSuccess((t) -> {
                    departmentAccComboBoxModel.setSelectedItem(t);
                    cboDepartment.repaint();
                });
                lblStatus.setForeground(Color.blue);
                lblStatus.setText("EDIT");
                txtDate.setDate(Util1.convertToDate(gl1.getGlDate()));
                txtDesp.setText(gl1.getDescription());
                txtRef.setText(gl1.getReference());
                txtVouNo.setText(gl1.getGlVouNo());
                txtTranAmt.setValue(gl1.getCrAmt());
                txtExAmt.setValue(gl2.getDrAmt());
                enableForm(true);
                progress.setIndeterminate(false);
            }).subscribe();
        } else {
            clear();
        }
    }

    private void initData() {
        cboSourceCur.setModel(srcCurrencyComboBoxModel);
        cboTargetCur.setModel(targetCurrencyComboBoxModel);
        cboDepartment.setModel(departmentAccComboBoxModel);
        userRepo.getExchangeRate().doOnSuccess((t) -> {
            exchangeRateComoboModel.setData(t);
            cboRate.setModel(exchangeRateComoboModel);
        }).subscribe();
        accountRepo.findCOA(Global.hmRoleProperty.get(ProUtil.CONVERSION_ACC)).doOnSuccess((t) -> {
            List<ChartOfAccount> list = new ArrayList<>();
            list.add(t);
            conComboBoxModel.setData(list);
            conComboBoxModel.setSelectedItem(t);
            cboConversion.setModel(conComboBoxModel);
        }).subscribe();
        accountRepo.getCashBank().doOnSuccess((t) -> {
            t.add(new ChartOfAccount());
            srcComboBoxModel.setData(t);
            targetComboBoxModel.setData(t);
            cboSource.setModel(srcComboBoxModel);
            cboTarget.setModel(targetComboBoxModel);
        }).subscribe();
        accountRepo.getDefaultDepartment().doOnSuccess((t) -> {
            departmentAccComboBoxModel.setSelectedItem(t);
            cboDepartment.repaint();
        }).subscribe();
        accountRepo.getDepartment().doOnSuccess((t) -> {
            departmentAccComboBoxModel.setData(t);
        }).subscribe();

    }

    private void enableForm(boolean status) {
        cboSource.setEnabled(status);
        cboSourceCur.setEnabled(status);
        cboTarget.setEnabled(status);
        cboConversion.setEnabled(status);
        cboDepartment.setEnabled(status);
        txtTranAmt.setEditable(status);
    }

    private void assignDefaultValue() {
        lblStatus.setForeground(Color.green);
        txtDate.setDateFormatString(Global.dateFormat);
        txtDate.setDate(Util1.getTodayDate());
        txtTranAmt.setHorizontalAlignment(JTextField.RIGHT);
        txtTranAmt.setFormatterFactory(Util1.getDecimalFormat());
        txtTranAmt.setFont(Global.textFont);
        txtExAmt.setHorizontalAlignment(JTextField.RIGHT);
        txtExAmt.setFormatterFactory(Util1.getDecimalFormat());
        txtExAmt.setFont(Global.textFont);
    }

    private boolean isValidEntry() {
        if (cboConversion.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select Conversion A/C.");
            cboConversion.requestFocus();
            return false;
        } else if (cboSource.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select Source A/C.");
            cboSource.requestFocus();
            return false;
        } else if (cboTarget.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select Target A/C.");
            cboTarget.requestFocus();
            return false;
        } else if (cboSourceCur.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select Source Currency.");
            cboSourceCur.requestFocus();
            return false;
        } else if (cboTargetCur.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select Target Currency.");
            cboTargetCur.requestFocus();
            return false;
        } else if (txtDate.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Please select Date.");
            txtDate.requestFocus();
            return false;
        } else if (cboConversion.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select Exchange.");
            cboConversion.requestFocus();
            return false;
        } else if (cboDepartment.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select Department.");
            cboDepartment.requestFocus();
            return false;
        }
        return true;
    }

    private void prepareData() {
        if (cboRate.getSelectedItem() instanceof ExchangeRate rate) {
            enableForm(true);
            String homeCur = rate.getHomeCur();
            String exCur = rate.getTargetCur();
            userRepo.getCurrency(homeCur, exCur).doOnSuccess((t) -> {
                srcCurrencyComboBoxModel.setData(t);
                targetCurrencyComboBoxModel.setData(t);
                srcCurrencyComboBoxModel.setSelectedItem(t.get(0));
                targetCurrencyComboBoxModel.setSelectedItem(t.get(1));
                cboSourceCur.repaint();
                cboTargetCur.repaint();
            }).subscribe();
        }
    }

    private void calculate() {
        if (cboRate.getSelectedItem() instanceof ExchangeRate rate) {
            double tranAmt = Util1.getDouble(txtTranAmt.getValue());
            double exRate = rate.getHomeFactor() / rate.getTargetFactor();
            if (cboSourceCur.getSelectedItem() instanceof Currency c1
                    && cboTargetCur.getSelectedItem() instanceof Currency) {
                String srcCur = c1.getCurCode();
                double exAmt;
                if (rate.getHomeCur().equals(srcCur)) {
                    exAmt = tranAmt / exRate;
                } else {
                    exAmt = tranAmt * exRate;
                }
                txtExAmt.setValue(exAmt);
                txtExAmt.requestFocus();
            }
        }
    }

    private void clear() {
        lblStatus.setForeground(Color.green);
        lblStatus.setText("NEW");
        cboRate.setSelectedItem(null);
        txtTranAmt.setValue(null);
        txtExAmt.setValue(null);
        cboSource.setSelectedItem(null);
        cboSourceCur.setSelectedItem(null);
        cboTarget.setSelectedItem(null);
        cboTargetCur.setSelectedItem(null);
        cboConversion.setSelectedItem(null);
        txtDesp.setText(null);
        txtRef.setText(null);
        txtVouNo.setText(null);
        enableForm(false);
        progress.setIndeterminate(false);
        btnSave.setEnabled(true);

    }

    private void swapCurrency() {
        int sIndex = cboSourceCur.getSelectedIndex();
        int tIndex = cboTargetCur.getSelectedIndex();
        if (sIndex > 0 && tIndex > 0) {
            if (sIndex == 1) {
                cboTargetCur.setSelectedIndex(0);
            } else {
                cboTargetCur.setSelectedIndex(1);
            }
            cboTargetCur.repaint();
            calculate();
        }
    }

    private void save() {
        if (isValidEntry()) {
            progress.setIndeterminate(true);
            btnSave.setEnabled(false);
            List<Gl> list = new ArrayList<>();
            list.add(getGl(1));
            list.add(getGl(2));
            accountRepo.saveGl(list).doOnSuccess((t) -> {
                clear();
            }).doOnError((t) -> {
                progress.setIndeterminate(false);
                btnSave.setEnabled(true);
                JOptionPane.showMessageDialog(this, t.getMessage());
            }).subscribe();
        }
    }

    private Gl getGl(int status) {
        Gl gl;
        if (lblStatus.getText().equals("NEW")) {
            gl = new Gl();
            GlKey key = new GlKey();
            key.setCompCode(Global.compCode);
            key.setDeptId(Global.deptId);
            gl.setKey(key);
            gl.setCreatedDate(LocalDateTime.now());
            gl.setCreatedBy(Global.loginUser.getUserCode());
        } else {
            gl = listGl.get(status - 1);
            gl.setModifyBy(Global.loginUser.getUserCode());
            gl.setModifyDate(LocalDateTime.now());
        }
        gl.setGlDate(Util1.convertToLocalDateTime(txtDate.getDate()));
        gl.setDescription(txtDesp.getText());
        gl.setReference(txtRef.getText());
        gl.setVouNo(txtVouNo.getText());
        gl.setTranSource(transSource);
        gl.setMacId(Global.macId);
        gl.setOrderId(status);
        if (cboRate.getSelectedItem() instanceof ExchangeRate rate) {
            gl.setExCode(rate.getKey().getExCode());
        }
        if (cboDepartment.getSelectedItem() instanceof DepartmentA dep) {
            gl.setDeptCode(dep.getKey().getDeptCode());
        }
        //src

        //convserion
        if (cboConversion.getSelectedItem() instanceof ChartOfAccount coa) {
            gl.setAccCode(coa.getKey().getCoaCode());
        }
        if (status == 1) {
            if (cboSource.getSelectedItem() instanceof ChartOfAccount coa) {
                gl.setSrcAccCode(coa.getKey().getCoaCode());
            }
            //currency
            if (cboSourceCur.getSelectedItem() instanceof Currency cur) {
                gl.setCurCode(cur.getCurCode());
            }
            gl.setCrAmt(Util1.getDouble(txtTranAmt.getValue()));
        } else {
            if (cboTarget.getSelectedItem() instanceof ChartOfAccount coa) {
                gl.setSrcAccCode(coa.getKey().getCoaCode());
            }
            //currency
            if (cboTargetCur.getSelectedItem() instanceof Currency cur) {
                gl.setCurCode(cur.getCurCode());
            }
            gl.setDrAmt(Util1.getDouble(txtExAmt.getValue()));
        }
        return gl;
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
        jLabel1 = new javax.swing.JLabel();
        cboConversion = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        cboSource = new javax.swing.JComboBox<>();
        cboTarget = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        cboSourceCur = new javax.swing.JComboBox<>();
        cboTargetCur = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        cboRate = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        txtTranAmt = new javax.swing.JFormattedTextField();
        btnSave = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtDate = new com.toedter.calendar.JDateChooser();
        txtDesp = new javax.swing.JTextField();
        txtRef = new javax.swing.JTextField();
        txtExAmt = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtVouNo = new javax.swing.JTextField();
        cboDepartment = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        progress = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Currency Conversion Dialog");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setText("Conversion A/C ");

        cboConversion.setFont(Global.textFont);
        cboConversion.setEnabled(false);

        jLabel2.setText("Source  A/C ");

        cboSource.setFont(Global.textFont);
        cboSource.setEnabled(false);

        cboTarget.setFont(Global.textFont);
        cboTarget.setEnabled(false);

        jLabel3.setText("Target A/C");

        cboSourceCur.setFont(Global.textFont);
        cboSourceCur.setEnabled(false);
        cboSourceCur.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboSourceCurItemStateChanged(evt);
            }
        });
        cboSourceCur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboSourceCurActionPerformed(evt);
            }
        });

        cboTargetCur.setFont(Global.textFont);
        cboTargetCur.setEnabled(false);
        cboTargetCur.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboTargetCurItemStateChanged(evt);
            }
        });
        cboTargetCur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTargetCurActionPerformed(evt);
            }
        });

        jLabel4.setText("Exchange Rate");

        cboRate.setFont(Global.textFont);
        cboRate.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboRateItemStateChanged(evt);
            }
        });
        cboRate.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                cboRateAncestorAdded(evt);
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });

        jLabel5.setText("Transfer Amt");

        txtTranAmt.setEditable(false);
        txtTranAmt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTranAmtActionPerformed(evt);
            }
        });

        btnSave.setFont(Global.lableFont);
        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        jButton2.setFont(Global.lableFont);
        jButton2.setText("Clear");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel6.setText("Reference");

        jLabel7.setText("Description");

        jLabel8.setText("Date");

        txtDate.setDateFormatString("dd/MM/yyyy");
        txtDate.setFont(Global.textFont);

        txtDesp.setFont(Global.textFont);

        txtRef.setFont(Global.textFont);

        txtExAmt.setEditable(false);

        jLabel9.setText("Exchange Amt");

        lblStatus.setFont(Global.menuFont);
        lblStatus.setText("NEW");

        jLabel10.setText("Vou No");

        txtVouNo.setEditable(false);
        txtVouNo.setFont(Global.textFont);

        cboDepartment.setFont(Global.textFont);
        cboDepartment.setEnabled(false);
        cboDepartment.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboDepartmentItemStateChanged(evt);
            }
        });
        cboDepartment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboDepartmentActionPerformed(evt);
            }
        });

        jLabel11.setText("Dep :");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboRate, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtTranAmt)
                                    .addComponent(txtExAmt))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cboSourceCur, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cboTargetCur, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(cboSource, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cboTarget, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSave))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboConversion, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtDesp)
                            .addComponent(txtRef)
                            .addComponent(txtVouNo)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cboDepartment, 0, 112, Short.MAX_VALUE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cboRate)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtTranAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboSourceCur, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtExAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboTargetCur, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(cboSource, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(cboTarget, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cboConversion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(txtDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cboDepartment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtDesp)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtRef)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtVouNo)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblStatus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 41, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSave, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton2, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(progress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(progress, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cboRateAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_cboRateAncestorAdded
        // TODO add your handling code here:
    }//GEN-LAST:event_cboRateAncestorAdded

    private void cboRateItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboRateItemStateChanged
        // TODO add your handling code here:
        prepareData();
    }//GEN-LAST:event_cboRateItemStateChanged

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        clear();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void cboSourceCurItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboSourceCurItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_cboSourceCurItemStateChanged

    private void cboTargetCurItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboTargetCurItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_cboTargetCurItemStateChanged

    private void cboSourceCurActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboSourceCurActionPerformed
        // TODO add your handling code here:
        swapCurrency();

    }//GEN-LAST:event_cboSourceCurActionPerformed

    private void cboTargetCurActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTargetCurActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboTargetCurActionPerformed

    private void txtTranAmtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTranAmtActionPerformed
        // TODO add your handling code here:
        calculate();
    }//GEN-LAST:event_txtTranAmtActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        save();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void cboDepartmentItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboDepartmentItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_cboDepartmentItemStateChanged

    private void cboDepartmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboDepartmentActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboDepartmentActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox<ChartOfAccount> cboConversion;
    private javax.swing.JComboBox<DepartmentA> cboDepartment;
    private javax.swing.JComboBox<ExchangeRate> cboRate;
    private javax.swing.JComboBox<ChartOfAccount> cboSource;
    private javax.swing.JComboBox<Currency> cboSourceCur;
    private javax.swing.JComboBox<ChartOfAccount> cboTarget;
    private javax.swing.JComboBox<Currency> cboTargetCur;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JProgressBar progress;
    private com.toedter.calendar.JDateChooser txtDate;
    private javax.swing.JTextField txtDesp;
    private javax.swing.JFormattedTextField txtExAmt;
    private javax.swing.JTextField txtRef;
    private javax.swing.JFormattedTextField txtTranAmt;
    private javax.swing.JTextField txtVouNo;
    // End of variables declaration//GEN-END:variables
}
