/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.inventory.ui.entry;

import com.repo.AccountRepo;
import com.acc.common.COAComboBoxModel;
import com.acc.model.ChartOfAccount;
import com.common.Global;
import com.common.PanelControl;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.editor.TraderAutoCompleter;
import com.inventory.model.PaymentHis;
import com.inventory.model.PaymentHisDetail;
import com.inventory.model.PaymentHisKey;
import com.inventory.model.Trader;
import com.repo.InventoryRepo;
import com.inventory.ui.common.PaymentTableModel;
import com.inventory.ui.entry.dialog.PaymentHistoryDialog;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.toedter.calendar.JTextFieldDateEditor;
import com.repo.UserRepo;
import com.user.editor.CurrencyAutoCompleter;
import com.user.editor.ProjectAutoCompleter;
import com.user.model.Project;
import com.user.model.ProjectKey;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.view.JasperViewer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class PaymentEntry extends javax.swing.JPanel implements SelectionObserver, PanelControl {

    private final PaymentTableModel tableModel = new PaymentTableModel();
    private SelectionObserver observer;
    private JProgressBar progress;
    private UserRepo userRepo;
    private InventoryRepo inventoryRepo;
    private AccountRepo accountRepo;
    private TraderAutoCompleter traderAutoCompleter;
    private ProjectAutoCompleter projectAutoCompleter;
    private CurrencyAutoCompleter currencyAutoCompleter;
    private COAComboBoxModel coaComboModel = new COAComboBoxModel();
    private PaymentHis ph = new PaymentHis();
    private PaymentHistoryDialog dialog;
    private String tranOption;

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public void setAccountRepo(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }

    /**
     * Creates new form ReceiveEntry
     *
     * @param tranOption
     */
    public PaymentEntry(String tranOption) {
        this.tranOption = tranOption;
        initComponents();
        initFocusAdapter();
        initFormat();
        configureOption();
        actionMapping();
    }

    private void configureOption() {
        lblTrader.setText(tranOption.equals("C") ? "Customer" : "Supplier");
    }

    private void initFormat() {
        txtRecord.setFormatterFactory(Util1.getDecimalFormat());
        txtAmount.setFormatterFactory(Util1.getDecimalFormat());
        txtOutstanding.setFormatterFactory(Util1.getDecimalFormat());
        txtCreditAmt.setFormatterFactory(Util1.getDecimalFormat());
        txtDifAmt.setFormatterFactory(Util1.getDecimalFormat());
    }

    private void actionMapping() {
        String solve = "delete";
        KeyStroke delete = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        tblPayment.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(delete, solve);
        tblPayment.getActionMap().put(solve, new DeleteAction());
    }

    private class DeleteAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            deleteTran();
        }
    }

    private void deleteTran() {
        if (lblStatus.getText().equals("EDIT")) {
            int row = tblPayment.convertRowIndexToModel(tblPayment.getSelectedRow());
            if (row >= 0) {
                if (tblPayment.getCellEditor() != null) {
                    tblPayment.getCellEditor().stopCellEditing();
                }
                int yes_no = JOptionPane.showConfirmDialog(this,
                        "Are you sure to delete?", "Payment Transaction delete.", JOptionPane.YES_NO_OPTION);
                if (yes_no == 0) {
                    tableModel.delete(row);
                    calTotalPayment();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Can't delete in payment mode.");
        }
    }

    private void initCombo() {
        traderAutoCompleter = new TraderAutoCompleter(txtTrader, inventoryRepo, null, false, "-");
        traderAutoCompleter.setObserver(this);
        projectAutoCompleter = new ProjectAutoCompleter(txtProjectNo, userRepo, null, false);
        currencyAutoCompleter = new CurrencyAutoCompleter(txtCurrency, null);
        userRepo.getCurrency().subscribe((t) -> {
            currencyAutoCompleter.setListCurrency(t);
        });
        userRepo.getDefaultCurrency().subscribe((c) -> {
            currencyAutoCompleter.setCurrency(c);
        });
        accountRepo.getCashBank().doOnSuccess((t) -> {
            t.add(new ChartOfAccount());
            coaComboModel.setData(t);
            cboCash.setModel(coaComboModel);
        }).subscribe();
    }

    private void initFocusAdapter() {
        txtAmount.addFocusListener(fa);
        txtProjectNo.addFocusListener(fa);
        txtRemark.addFocusListener(fa);
        txtTrader.addFocusListener(fa);
        txtVouDate.addFocusListener(fa);
    }

    public void initMain() {
        initDate();
        initCombo();
        initTable();
    }
    private final FocusAdapter fa = new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            if (e.getSource() instanceof JTextFieldDateEditor txt) {
                txt.selectAll();
            } else if (e.getSource() instanceof JFormattedTextField txt) {
                txt.selectAll();
            } else if (e.getSource() instanceof JTextField txt) {
                txt.selectAll();
            }
        }
    };

    private void initDate() {
        txtVouDate.setDate(Util1.getTodayDate());
    }

    private void initTable() {
        tableModel.setObserver(this);
        tableModel.setTable(tblPayment);
        tblPayment.setModel(tableModel);
        tblPayment.getTableHeader().setFont(Global.tblHeaderFont);
        tblPayment.setCellSelectionEnabled(true);
        tblPayment.setRowHeight(Global.tblRowHeight);
        tblPayment.setShowGrid(true);
        tblPayment.setFont(Global.textFont);
        tblPayment.getColumnModel().getColumn(0).setPreferredWidth(50);//Date
        tblPayment.getColumnModel().getColumn(1).setPreferredWidth(100);//VouNo
        tblPayment.getColumnModel().getColumn(2).setPreferredWidth(100);//Remark
        tblPayment.getColumnModel().getColumn(3).setPreferredWidth(100);//Ref
        tblPayment.getColumnModel().getColumn(4).setPreferredWidth(20);//VouNo
        tblPayment.getColumnModel().getColumn(5).setPreferredWidth(60);//Total
        tblPayment.getColumnModel().getColumn(6).setPreferredWidth(60);//Oustanting
        tblPayment.getColumnModel().getColumn(7).setPreferredWidth(60);//Payment
        tblPayment.getColumnModel().getColumn(8).setPreferredWidth(1);//paid
        tblPayment.getColumnModel().getColumn(7).setCellEditor(new AutoClearEditor());
        tblPayment.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblPayment.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void searchTraderBalance() {
        Trader t = traderAutoCompleter.getTrader();
        if (t != null) {
            if (lblStatus.getText().equals("NEW")) {
                progress.setIndeterminate(true);
                Float creditAmt = Util1.getFloat(txtCreditAmt.getValue());
                Float tmp = Util1.getFloat(ProUtil.getProperty(ProUtil.C_CREDIT_AMT));
                txtCreditAmt.setValue(creditAmt == 0 ? tmp : creditAmt);
                inventoryRepo.getTraderBalance(t.getKey().getCode(), tranOption)
                        .subscribe((payment) -> {
                            lblMessage.setText(payment.isEmpty() ? "No Record." : "");
                            tableModel.setListDetail(payment);
                            progress.setIndeterminate(false);
                            calTotalPayment();
                        }, (e) -> {
                            JOptionPane.showMessageDialog(this, e.getMessage());
                            progress.setIndeterminate(false);
                        });
            } else {
                JOptionPane.showMessageDialog(this, "Create New Payment Voucher.");
            }
        }
    }

    private void calTotalPayment() {
        double creditAmt = Util1.getDouble(txtCreditAmt.getValue());
        double payment = tableModel.getListDetail().stream().mapToDouble((obj) -> Util1.getDouble(obj.getPayAmt())).sum();
        double outstanding = tableModel.getListDetail().stream().mapToDouble((obj) -> Util1.getDouble(obj.getVouBalance())).sum();
        txtAmount.setValue(payment);
        txtOutstanding.setValue(outstanding - payment);
        txtDifAmt.setValue(outstanding - creditAmt);
        txtRecord.setValue(tableModel.getListDetail().size());
    }

    private void savePayment(boolean print) {
        if (isValidEntry() && tableModel.isValidEntry()) {
            observer.selected("save", false);
            progress.setIndeterminate(true);
            ph.setListDetail(tableModel.getPaymentList());
            ph.setListDelete(tableModel.getListDelete());
            ph.setTranOption(tranOption);
            inventoryRepo.savePayment(ph).subscribe((t) -> {
                clear();
                if (print) {
                    printVoucher(t.getKey());
                }
            }, (e) -> {
                JOptionPane.showMessageDialog(this, e.getMessage());
                progress.setIndeterminate(false);
                observer.selected("save", true);
            });
        }
    }

    private void enableToolBar(boolean status) {
        progress.setIndeterminate(!status);
        observer.selected("refresh", status);
        observer.selected("print", status);
        observer.selected("save", false);
        observer.selected("history", true);
    }

    private void printVoucher(PaymentHisKey key) {
        enableToolBar(false);
        inventoryRepo.paymentReport(key).subscribe((t) -> {
            try {
                byte[] data = Util1.listToByteArray(t);
                String reportName = "PaymentVoucher";
                String logoPath = String.format("images%s%s", File.separator, ProUtil.getProperty("logo.name"));
                Map<String, Object> param = new HashMap<>();
                param.put("p_print_date", Util1.getTodayDateTime());
                param.put("p_comp_name", Global.companyName);
                param.put("p_comp_address", Global.companyAddress);
                param.put("p_comp_phone", Global.companyPhone);
                param.put("p_logo_path", logoPath);
                param.put("p_tran_option", tranOption);
                String reportPath = ProUtil.getReportPath() + reportName.concat(".jasper");
                ByteArrayInputStream stream = new ByteArrayInputStream(data);
                JsonDataSource ds = new JsonDataSource(stream);
                JasperPrint jp = JasperFillManager.fillReport(reportPath, param, ds);
                JasperViewer.viewReport(jp, false);
                enableToolBar(true);
            } catch (JRException ex) {
                enableToolBar(true);
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });
    }

    private boolean isValidEntry() {
        Trader t = traderAutoCompleter.getTrader();
        if (t == null) {
            JOptionPane.showMessageDialog(this, "Invalid Trader.");
            txtTrader.requestFocus();
            return false;
        } else if (txtVouDate.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Invalid Date.");
            txtVouDate.requestFocus();
            return false;
        } else if (Util1.getFloat(txtAmount.getValue()) <= 0) {
            JOptionPane.showMessageDialog(this, "Invalid Pay Amt.");
            txtAmount.requestFocus();
            return false;
        } else if (currencyAutoCompleter == null || currencyAutoCompleter.getCurrency() == null) {
            JOptionPane.showMessageDialog(this, "Invalid Currency.");
            txtCurrency.requestFocus();
            return false;
        } else {
            if (cboCash.getSelectedItem() instanceof ChartOfAccount coa) {
                if (coa.getKey() != null) {
                    ph.setAccount(coa.getKey().getCoaCode());
                }
            }
            Project p = projectAutoCompleter.getProject();
            if (p != null) {
                ph.setProjectNo(p.getKey().getProjectNo());
            }
            ph.setCurCode(currencyAutoCompleter.getCurrency().getCurCode());
            ph.setTraderCode(t.getKey().getCode());
            ph.setVouDate(Util1.convertToLocalDateTime(txtVouDate.getDate()));
            ph.setAmount(Util1.getFloatOne(txtAmount.getValue()));
            ph.setDeleted(false);
            ph.setRemark(txtRemark.getText());
            ph.setMacId(Global.macId);

            if (lblStatus.getText().equals("NEW")) {
                PaymentHisKey key = new PaymentHisKey();
                key.setCompCode(Global.compCode);
                ph.setDeptId(Global.deptId);
                ph.setKey(key);
                ph.setCreatedBy(Global.loginUser.getUserCode());
                ph.setCreatedDate(LocalDateTime.now());
            } else {
                ph.setUpdatedBy(Global.loginUser.getUserCode());
            }
        }
        return true;
    }

    private void clear() {
        traderAutoCompleter.setTrader(null);
        projectAutoCompleter.setProject(null);
        coaComboModel.setSelectedItem(null);
        progress.setIndeterminate(false);
        cboCash.repaint();
        txtVouNo.setText(null);
        txtRemark.setText(null);
        txtAmount.setValue(null);
        txtCreditAmt.setValue(0);
        txtOutstanding.setValue(0);
        txtDifAmt.setValue(0);
        txtRecord.setValue(0);
        tableModel.clear();
        lblStatus.setForeground(Color.green);
        lblStatus.setText("NEW");
        lblMessage.setText("");
        enableForm(true);
        ph = new PaymentHis();
    }

    private void historyPayment() {
        if (dialog == null) {
            dialog = new PaymentHistoryDialog(Global.parentForm, tranOption);
            dialog.setObserver(this);
            dialog.setUserRepo(userRepo);
            dialog.setInventoryRepo(inventoryRepo);
            dialog.setAccountRepo(accountRepo);
            dialog.setTitle(String.format("%s Payment History Dialog", tranOption.equals("C") ? "Customer" : "Supplier"));
            dialog.initMain();
            dialog.setSize(Global.width - 100, Global.height - 100);
            dialog.setLocationRelativeTo(null);
        }
        dialog.search();
    }

    private void setVoucherDetail(PaymentHis ph) {
        this.ph = ph;
        int deptId = ph.getDeptId();
        String compCode = ph.getKey().getCompCode();
        String vouNo = ph.getKey().getVouNo();
        inventoryRepo.findTrader(ph.getTraderCode()).subscribe((t) -> {
            traderAutoCompleter.setTrader(t);
        });
        accountRepo.findCOA(ph.getAccount()).subscribe((t) -> {
            coaComboModel.setSelectedItem(t);
        });
        userRepo.find(new ProjectKey(ph.getProjectNo(), compCode)).subscribe((t) -> {
            projectAutoCompleter.setProject(t);
        });
        userRepo.findCurrency(ph.getCurCode()).subscribe((t) -> {
            currencyAutoCompleter.setCurrency(t);
        });
        txtVouNo.setText(vouNo);
        txtVouDate.setDate(Util1.convertToDate(ph.getVouDate()));
        txtRemark.setText(ph.getRemark());
        txtAmount.setValue(Util1.getFloat(ph.getAmount()));
        if (ph.isDeleted()) {
            lblStatus.setText("DELETED");
            lblStatus.setForeground(Color.red);
            enableForm(false);
        } else if (!ProUtil.isPaymentEdit()) {
            lblStatus.setText("No Permission.");
            lblStatus.setForeground(Color.RED);
            enableForm(false);
            observer.selected("print", true);
        } else {
            lblStatus.setText("EDIT");
            lblStatus.setForeground(Color.blue);
            enableForm(true);
        }
        inventoryRepo.getPaymentDetail(vouNo, deptId).subscribe((t) -> {
            tableModel.setListDetail(t);
            calTotalPayment();
            tblPayment.requestFocus();
        });
    }

    private void enableForm(boolean status) {
        txtAmount.setEnabled(status);
        txtRemark.setEnabled(status);
        txtOutstanding.setEnabled(status);
        txtProjectNo.setEnabled(status);
        txtTrader.setEnabled(status);
        txtVouNo.setEnabled(status);
        txtVouDate.setEnabled(status);
        cboCash.setEnabled(status);
        tblPayment.setEnabled(status);
        txtCurrency.setEnabled(status);
        observer.selected("save", status);
        observer.selected("delete", status);
        observer.selected("print", status);
    }

    private void deletePayment() {
        String status = lblStatus.getText();
        switch (status) {
            case "EDIT" -> {
                int yes_no = JOptionPane.showConfirmDialog(this,
                        "Are you sure to delete?", "Payment Voucher Delete.", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                if (yes_no == 0) {
                    inventoryRepo.delete(ph.getKey()).subscribe((t) -> {
                        if (t) {
                            calTotalPayment();
                            clear();
                        }
                    });
                }
            }
            case "DELETED" -> {
                int yes_no = JOptionPane.showConfirmDialog(this,
                        "Are you sure to restore?", "Payment Voucher Restore.", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (yes_no == 0) {
                    ph.setDeleted(false);
                    inventoryRepo.restore(ph.getKey()).subscribe((t) -> {
                        if (t) {
                            lblStatus.setText("EDIT");
                            lblStatus.setForeground(Color.blue);
                            enableForm(true);
                        }
                    });
                }
            }
            default ->
                JOptionPane.showMessageDialog(this, "Voucher can't delete.");
        }
    }

    private void generateFIFOPayment() {
        List<PaymentHisDetail> list = tableModel.getListDetail();
        list.stream().forEach((p) -> {
            p.setPayAmt(0.0f);
            p.setFullPaid(false);
        });
        float payment = Util1.getFloat(txtAmount.getValue());
        double ttlBalance = Util1.getDouble(txtOutstanding.getValue());
        if (payment <= ttlBalance) {
            for (PaymentHisDetail p : list) {
                if (payment > 0) {
                    float balance = p.getVouBalance();
                    if (payment >= balance) {
                        p.setPayAmt(balance);
                        p.setFullPaid(true);
                    } else {
                        p.setPayAmt(payment);
                        p.setFullPaid(false);
                    }
                    payment -= balance;
                }
            }
            tableModel.fireTableDataChanged();
        } else {
            JOptionPane.showMessageDialog(this, "Payment Amount is greater than outstanding.");
            txtAmount.setValue(ttlBalance);
            txtAmount.requestFocus();
        }
    }

    private void observeMain() {
        observer.selected("control", this);
        observer.selected("save", true);
        observer.selected("print", true);
        observer.selected("history", true);
        observer.selected("delete", true);
        observer.selected("refresh", false);
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
        txtVouNo = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtVouDate = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtAmount = new javax.swing.JFormattedTextField();
        txtOutstanding = new javax.swing.JFormattedTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtCreditAmt = new javax.swing.JFormattedTextField();
        jLabel12 = new javax.swing.JLabel();
        txtDifAmt = new javax.swing.JFormattedTextField();
        lblMessage = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPayment = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        lblTrader = new javax.swing.JLabel();
        txtTrader = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtProjectNo = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        cboCash = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        txtCurrency = new javax.swing.JTextField();
        lblStatus = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        txtRecord = new javax.swing.JFormattedTextField();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel1.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jPanel1ComponentShown(evt);
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Vou No");

        txtVouNo.setEditable(false);
        txtVouNo.setFont(Global.textFont);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Vou Date");

        txtVouDate.setDateFormatString("dd/MM/yyyy");
        txtVouDate.setFont(Global.textFont);
        txtVouDate.setMaxSelectableDate(new java.util.Date(253370745073000L));

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Remark");

        txtRemark.setFont(Global.textFont);

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Multiple Payment");

        txtAmount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAmount.setFont(Global.textFont);
        txtAmount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAmountActionPerformed(evt);
            }
        });

        txtOutstanding.setEditable(false);
        txtOutstanding.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtOutstanding.setFont(Global.amtFont);

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Total Outstanding");

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Credit Amount");

        txtCreditAmt.setEditable(false);
        txtCreditAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtCreditAmt.setFont(Global.amtFont);

        jLabel12.setFont(Global.lableFont);
        jLabel12.setText("Max / (Min)");

        txtDifAmt.setEditable(false);
        txtDifAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDifAmt.setFont(Global.amtFont);

        lblMessage.setFont(Global.lableFont);
        lblMessage.setForeground(Global.selectionColor);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtRemark)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtVouDate, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addGap(110, 110, 110)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(lblMessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtAmount, javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDifAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCreditAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtOutstanding, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtRemark, txtVouDate, txtVouNo});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtOutstanding, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7))
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtCreditAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel11)
                                .addComponent(lblMessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtDifAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel12)
                                .addComponent(txtAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtRemark))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtVouDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        tblPayment.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblPayment);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblTrader.setFont(Global.lableFont);
        lblTrader.setText("Customer");

        txtTrader.setFont(Global.textFont);

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Project No");

        txtProjectNo.setFont(Global.textFont);

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Account");

        cboCash.setFont(Global.textFont);

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Currency");

        txtCurrency.setFont(Global.textFont);

        lblStatus.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        lblStatus.setText("NEW");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(lblTrader))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtProjectNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTrader, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(cboCash, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblStatus))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtProjectNo, txtTrader});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTrader, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTrader)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboCash, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblStatus))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtCurrency))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtProjectNo)))
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Record :");

        txtRecord.setEditable(false);
        txtRecord.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtRecord.setFont(Global.textFont);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtRecord, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtRecord)
                    .addComponent(jLabel10))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jPanel1ComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jPanel1ComponentShown
        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel1ComponentShown

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observeMain();
    }//GEN-LAST:event_formComponentShown

    private void txtAmountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAmountActionPerformed
        // TODO add your handling code here:
        generateFIFOPayment();
    }//GEN-LAST:event_txtAmountActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<ChartOfAccount> cboCash;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblMessage;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblTrader;
    private javax.swing.JTable tblPayment;
    private javax.swing.JFormattedTextField txtAmount;
    private javax.swing.JFormattedTextField txtCreditAmt;
    private javax.swing.JTextField txtCurrency;
    private javax.swing.JFormattedTextField txtDifAmt;
    private javax.swing.JFormattedTextField txtOutstanding;
    private javax.swing.JTextField txtProjectNo;
    private javax.swing.JFormattedTextField txtRecord;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JTextField txtTrader;
    private com.toedter.calendar.JDateChooser txtVouDate;
    private javax.swing.JTextField txtVouNo;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {
        if (source.equals("CAL_PAYMENT")) {
            calTotalPayment();
        } else if (source.equals("PAYMENT_HISTORY")) {
            if (selectObj instanceof PaymentHis p) {
                setVoucherDetail(p);
            }
        } else if (source != null) {
            searchTraderBalance();
        }
    }

    @Override
    public void save() {
        savePayment(false);
    }

    @Override
    public void delete() {
        deletePayment();
    }

    @Override
    public void newForm() {
        clear();
    }

    @Override
    public void history() {
        historyPayment();
    }

    @Override
    public void print() {
        savePayment(true);
    }

    @Override
    public void refresh() {
    }

    @Override
    public void filter() {
    }

    @Override
    public String panelName() {
        return this.getName();
    }
}
