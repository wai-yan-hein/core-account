/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.inventory.ui.entry;

import com.acc.editor.COA3AutoCompleter;
import com.acc.editor.COA3CellEditor;
import com.repo.AccountRepo;
import com.acc.editor.COAAutoCompleter;
import com.acc.editor.DepartmentAutoCompleter;
import com.acc.model.ChartOfAccount;
import com.acc.model.DepartmentA;
import com.common.ComponentUtil;
import com.common.DateLockUtil;
import com.common.Global;
import com.common.PanelControl;
import com.common.ProUtil;
import com.common.ReportFilter;
import com.common.RowHeader;
import com.common.SelectionObserver;
import com.common.Util1;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.editor.LabourGroupAutoCompleter;
import com.inventory.model.LabourGroup;
import com.inventory.model.LabourPaymentDetail;
import com.inventory.model.LabourPaymentDto;
import com.inventory.ui.common.LabourPaymentTableModel;
import com.inventory.ui.entry.dialog.LabourPaymentHistory;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.repo.InventoryRepo;
import com.repo.UserRepo;
import com.user.editor.CurrencyAutoCompleter;
import com.user.model.Currency;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class LabourPaymentEntry extends javax.swing.JPanel implements SelectionObserver, PanelControl {

    private final LabourPaymentTableModel tableModel = new LabourPaymentTableModel();
    private SelectionObserver observer;
    private JProgressBar progress;
    private UserRepo userRepo;
    private InventoryRepo inventoryRepo;
    private AccountRepo accountRepo;
    private LabourGroupAutoCompleter labourGroupAutoCompleter;
    private CurrencyAutoCompleter currencyAutoCompleter;
    private COAAutoCompleter cOAAutoCompleter;
    private COA3AutoCompleter expenseAutoCompleter;
    private DepartmentAutoCompleter departmentAutoCompleter;
    private LabourPaymentDto ph = new LabourPaymentDto();
    private LabourPaymentHistory dialog;

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
     */
    public LabourPaymentEntry() {
        initComponents();
        initFocusAdapter();
        initFormat();
        actionMapping();
    }

    private void initFormat() {
        txtRecord.setFormatterFactory(Util1.getDecimalFormat());
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
                    calculatePayment();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Can't delete in payment mode.");
        }
    }

    private void initCombo() {
        currencyAutoCompleter = new CurrencyAutoCompleter(txtCurrency, null);
        labourGroupAutoCompleter = new LabourGroupAutoCompleter(txtLabourGroup, null, false);
        labourGroupAutoCompleter.setObserver(this);
        cOAAutoCompleter = new COAAutoCompleter(txtAccount, null, false);
        expenseAutoCompleter = new COA3AutoCompleter(txtExpense, accountRepo, null, false, 3);
        departmentAutoCompleter = new DepartmentAutoCompleter(txtDep, null, false, false);
    }

    private void initData() {
        userRepo.getCurrency().doOnSuccess((t) -> {
            currencyAutoCompleter.setListCurrency(t);
        }).subscribe();
        userRepo.getDefaultCurrency().doOnSuccess((c) -> {
            currencyAutoCompleter.setCurrency(c);
        }).subscribe();
        accountRepo.getDefaultDepartment().doOnSuccess((t) -> {
            departmentAutoCompleter.setDepartment(t);
        }).subscribe();
        accountRepo.getCashBank().doOnSuccess((t) -> {
            t.add(new ChartOfAccount());
            cOAAutoCompleter.setListCOA(t);
        }).subscribe();
        inventoryRepo.getLabourGroup().doOnSuccess((t) -> {
            labourGroupAutoCompleter.setListObject(t);
        }).subscribe();
        accountRepo.getDepartment().doOnSuccess((t) -> {
            departmentAutoCompleter.setListDepartment(t);
        }).subscribe();
    }

    private void initFocusAdapter() {
        ComponentUtil.addFocusListener(this);
    }

    public void initMain() {
        initDate();
        initCombo();
        initData();
        initTable();
        initRowHeader();
    }

    private void initDate() {
        txtVouDate.setDate(Util1.getTodayDate());
        txtFromDate.setDate(Util1.getTodayDate());
        txtToDate.setDate(Util1.getTodayDate());
    }

    private void initRowHeader() {
        RowHeader header = new RowHeader();
        JList list = header.createRowHeader(tblPayment, 30);
        scroll.setRowHeaderView(list);
    }

    private void initTable() {
        tableModel.setObserver(this);
        tableModel.setAccountRepo(accountRepo);
        tableModel.setTable(tblPayment);
        tblPayment.setModel(tableModel);
        tblPayment.getTableHeader().setFont(Global.tblHeaderFont);
        tblPayment.setCellSelectionEnabled(true);
        tblPayment.setRowHeight(Global.tblRowHeight);
        tblPayment.setShowGrid(true);
        tblPayment.setFont(Global.textFont);
        tblPayment.getColumnModel().getColumn(0).setPreferredWidth(200);//Desp
        tblPayment.getColumnModel().getColumn(1).setPreferredWidth(50);//qty
        tblPayment.getColumnModel().getColumn(2).setPreferredWidth(70);//price
        tblPayment.getColumnModel().getColumn(3).setPreferredWidth(80);//Amount
        tblPayment.getColumnModel().getColumn(4).setPreferredWidth(100);//account
        tblPayment.getColumnModel().getColumn(0).setCellEditor(new AutoClearEditor());
        tblPayment.getColumnModel().getColumn(1).setCellEditor(new AutoClearEditor());
        tblPayment.getColumnModel().getColumn(2).setCellEditor(new AutoClearEditor());
        tblPayment.getColumnModel().getColumn(4).setCellEditor(new COA3CellEditor(accountRepo, 3));
        tblPayment.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblPayment.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void savePayment(boolean print) {
        progress.setIndeterminate(true);
        if (isValidEntry() && tableModel.isValidEntry()) {
            if (DateLockUtil.isLockDate(txtVouDate.getDate())) {
                DateLockUtil.showMessage(this);
                txtVouDate.requestFocus();
                return;
            }
            observer.selected("save", false);
            progress.setIndeterminate(true);
            ph.setListDetail(tableModel.getPaymentList());
            inventoryRepo.saveLabourPayment(ph).doOnSuccess((saved) -> {
                if (saved != null) {
                    ph.setVouDateTime(saved.getVouDateTime());
                    progress.setIndeterminate(false);
                }
            }).doOnError((e) -> {
                JOptionPane.showMessageDialog(this, e.getMessage());
                progress.setIndeterminate(false);
                observer.selected("save", true);
            }).doOnTerminate(() -> {
                if (print) {
                    printVoucher(ph);
                }
                clear();
            }).subscribe();
        }
    }

    private void enableToolBar(boolean status) {
        progress.setIndeterminate(!status);
        observer.selected("refresh", status);
        observer.selected("print", status);
        observer.selected("save", false);
        observer.selected("history", true);
    }

    private void printVoucher(LabourPaymentDto dto) {
        try {
            enableToolBar(false);
            double labourPrice = dto.getMemberCount() == 0 ? 0 : dto.getPayTotal() / dto.getMemberCount();
            String fromDate = Util1.toDateStr(dto.getFromDate(), Global.dateFormat);
            String toDate = Util1.toDateStr(dto.getToDate(), Global.dateFormat);
            String payDate = fromDate.equals(toDate) ? fromDate : String.format("%s : %s", fromDate, toDate);
            String reportName = "LabourPaymentA5";
            String reportPath = String.format("report%s%s", File.separator, reportName.concat(".jasper"));
            Map<String, Object> param = new HashMap<>();
            param.put("p_print_date", Util1.getTodayDateTime());
            param.put("p_comp_name", Global.companyName);
            param.put("p_comp_address", Global.companyAddress);
            param.put("p_comp_phone", Global.companyPhone);
            param.put("p_logo_path", ProUtil.logoPath());
            param.put("p_vou_no", dto.getVouNo());
            param.put("p_vou_date", Util1.getDate(dto.getVouDateTime()));
            param.put("p_vou_time", Util1.getTime(dto.getVouDateTime()));
            param.put("p_pay_date", payDate);
            param.put("p_labour_name", dto.getLabourName());
            param.put("p_labour_count", dto.getMemberCount());
            param.put("p_labour_price", labourPrice);
            param.put("p_sub_report_dir", "report/");
            ObjectMapper mapper = new ObjectMapper();
            JsonNode n = mapper.readTree(Util1.gson.toJson(tableModel.getListDetail()));
            JsonDataSource d = new JsonDataSource(n, null) {
            };
            JasperPrint main = JasperFillManager.fillReport(reportPath, param, d);
            JasperViewer.viewReport(main, false);
        } catch (JsonProcessingException | JRException e) {
            log.error("printVoucher : " + e.getMessage());
        }

    }

    private boolean isValidEntry() {
        Date fromDate = txtFromDate.getDate();
        Date toDate = txtToDate.getDate();
        LabourGroup group = labourGroupAutoCompleter.getObject();
        Currency currency = currencyAutoCompleter.getCurrency();
        ChartOfAccount srcAcc = cOAAutoCompleter.getCOA();
        DepartmentA department = departmentAutoCompleter.getDepartment();
        if (fromDate == null || toDate == null) {
            JOptionPane.showMessageDialog(this, "Invalid Date Range.");
            txtFromDate.requestFocus();
            return false;
        } else if (group == null) {
            JOptionPane.showMessageDialog(this, "Choose Labour Group.");
            txtLabourGroup.requestFocus();
            return false;
        } else if (currency == null) {
            JOptionPane.showMessageDialog(this, "Invalid Currency.");
            txtCurrency.requestFocus();
            return false;
        } else if (txtVouDate == null) {
            JOptionPane.showMessageDialog(this, "Invalid Pay Date.");
            txtVouDate.requestFocus();
            return false;
        } else if (Util1.getDouble(txtPayTotal.getValue()) == 0) {
            JOptionPane.showMessageDialog(this, "Invalid Pay Amount.");
            txtVouDate.requestFocus();
            return false;
        } else {
            if (srcAcc != null) {
                if (department == null) {
                    JOptionPane.showConfirmDialog(this, "Invalid Department");
                    return false;
                }
                ph.setDeptCode(department.getKey().getDeptCode());
            }
            if (lblStatus.getText().equals("NEW")) {
                ph.setCreatedBy(Global.loginUser.getUserCode());
            } else {
                ph.setUpdatedBy(Global.loginUser.getUserCode());
            }
            ph.setDeptId(Global.deptId);
            ph.setMacId(Global.macId);
            ph.setCompCode(Global.compCode);
            ph.setFromDate(Util1.toLocalDate(txtFromDate.getDate()));
            ph.setToDate(Util1.toLocalDate(txtToDate.getDate()));
            ph.setLabourGroupCode(group.getKey().getCode());
            ph.setLabourName(group.getLabourName());
            ph.setCurCode(currency.getCurCode());
            ph.setMemberCount(Util1.getInteger(txtMember.getText()));
            ph.setVouDate(Util1.convertToLocalDateTime(txtVouDate.getDate()));
            ph.setRemark(txtRemark.getText());
            ph.setPayTotal(Util1.getDouble(txtPayTotal.getValue()));
            ChartOfAccount payAcc = cOAAutoCompleter.getCOA();
            ChartOfAccount expAcc = expenseAutoCompleter.getCOA();
            ph.setSourceAcc(payAcc == null ? null : payAcc.getKey().getCoaCode());
            ph.setExpenseAcc(expAcc == null ? null : expAcc.getKey().getCoaCode());
        }
        return true;
    }

    private void clear() {
        progress.setIndeterminate(false);
        labourGroupAutoCompleter.setObject(null);
        cOAAutoCompleter.setCoa(null);
        expenseAutoCompleter.setCoa(null);
        txtRemark.setText(null);
        txtRecord.setText("0");
        txtMember.setText(null);
        txtVouNo.setText(null);
        txtPayTotal.setValue(0);
        tableModel.clear();
        lblStatus.setText("NEW");
        lblStatus.setForeground(Color.green);
        ph = new LabourPaymentDto();
        enableForm(true);
        observeMain();

    }

    private void historyPayment() {
        if (dialog == null) {
            dialog = new LabourPaymentHistory(Global.parentForm);
            dialog.setObserver(this);
            dialog.setUserRepo(userRepo);
            dialog.setInventoryRepo(inventoryRepo);
            dialog.initMain();
            dialog.setSize(Global.width - 20, Global.height - 20);
            dialog.setLocationRelativeTo(null);
        }
        dialog.search();
    }

    private void setVoucherDetail(LabourPaymentDto ph) {
        this.ph = ph;
        String vouNo = ph.getVouNo();
        userRepo.findCurrency(ph.getCurCode()).doOnSuccess((t) -> {
            currencyAutoCompleter.setCurrency(t);
        }).subscribe();
        inventoryRepo.findLabourGroup(ph.getLabourGroupCode()).doOnSuccess((t) -> {
            labourGroupAutoCompleter.setObject(t);
        }).subscribe();
        accountRepo.findCOA(ph.getSourceAcc()).doOnSuccess((t) -> {
            cOAAutoCompleter.setCoa(t);
        }).subscribe();
        accountRepo.findCOA(ph.getExpenseAcc()).doOnSuccess((t) -> {
            expenseAutoCompleter.setCoa(t);
        }).subscribe();
        accountRepo.findDepartment(ph.getDeptCode()).doOnSuccess((t) -> {
            departmentAutoCompleter.setDepartment(t);
        }).subscribe();
        txtMember.setText(Util1.getString(ph.getMemberCount()));
        txtVouNo.setText(vouNo);
        txtVouDate.setDate(Util1.convertToDate(ph.getVouDate()));
        txtRemark.setText(ph.getRemark());
        txtPayTotal.setValue(ph.getPayTotal());
        txtFromDate.setDate(Util1.toDate(ph.getFromDate()));
        txtToDate.setDate(Util1.toDate(ph.getToDate()));
        if (ph.isDeleted()) {
            lblStatus.setText("DELETED");
            lblStatus.setForeground(Color.red);
            enableForm(false);
        } else if (DateLockUtil.isLockDate(ph.getVouDate())) {
            lblStatus.setText(DateLockUtil.MESSAGE);
            lblStatus.setForeground(Color.RED);
            enableForm(false);
        } else {
            lblStatus.setText("EDIT");
            lblStatus.setForeground(Color.blue);
            enableForm(false);
        }
        inventoryRepo.getLabourPaymentDetail(vouNo).doOnSuccess((t) -> {
            if (t != null) {
                tableModel.setListDetail(t);
                tblPayment.requestFocus();
            }
        }).subscribe();
    }

    private void enableForm(boolean status) {
        ComponentUtil.enableForm(this, status);
        observer.selected("save", status);
    }

    private void deletePayment() {
        String status = lblStatus.getText();
        switch (status) {
            case "EDIT" -> {
                int yes_no = JOptionPane.showConfirmDialog(this,
                        "Are you sure to delete?", "Payment Voucher Delete.", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                if (yes_no == 0) {
                    inventoryRepo.deleteLabourPayment(ph.getVouNo()).doOnSuccess((delete) -> {
                        if (delete) {
                            clear();
                        }
                    }).subscribe();
                }
            }
            case "DELETED" -> {
                int yes_no = JOptionPane.showConfirmDialog(this,
                        "Are you sure to restore?", "Payment Voucher Restore.", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (yes_no == 0) {
                    ph.setDeleted(false);
                    inventoryRepo.restoreLabourPayment(ph.getVouNo()).doOnSuccess((restore) -> {
                        if (restore) {
                            lblStatus.setText("EDIT");
                            lblStatus.setForeground(Color.blue);
                            enableForm(true);
                        }
                    }).subscribe();
                }
            }
            default ->
                JOptionPane.showMessageDialog(this, "Voucher can't delete.");
        }
    }

    private void calculatePayment() {
        Date fromDate = txtFromDate.getDate();
        Date toDate = txtToDate.getDate();
        LabourGroup group = labourGroupAutoCompleter.getObject();
        Currency currency = currencyAutoCompleter.getCurrency();
        if (fromDate == null || toDate == null) {
            JOptionPane.showMessageDialog(this, "Invalid Date Range.");
        } else if (group == null) {
            JOptionPane.showMessageDialog(this, "Choose Labour Group.");
        } else if (currency == null) {
            JOptionPane.showMessageDialog(this, "Invalid Currency.");
        } else {
            progress.setIndeterminate(true);
            ReportFilter filter = new ReportFilter(Global.macId, Global.compCode, Global.deptId);
            filter.setFromDate(Util1.toDateStr(fromDate, "yyyy-MM-dd"));
            filter.setToDate(Util1.toDateStr(toDate, "yyyy-MM-dd"));
            filter.setLabourGroupCode(group.getKey().getCode());
            filter.setCurCode(currency.getCurCode());
            inventoryRepo.calulateLabourPayment(filter).doOnSuccess((t) -> {
                if (t != null) {
                    tableModel.setListDetail(t);
                }
            }).doOnTerminate(() -> {
                calTotal();
                tableModel.addNewRow();
                progress.setIndeterminate(false);
            }).subscribe();
        }
    }

    private void calTotal() {
        List<LabourPaymentDetail> list = tableModel.getListDetail();
        double total = list.stream().mapToDouble((t) -> t.getAmount()).sum();
        txtPayTotal.setValue(total);
        txtRecord.setValue(list.size());

    }

    private void setMemberCount() {
        LabourGroup group = labourGroupAutoCompleter.getObject();
        if (group != null) {
            txtMember.setText(Util1.getString(group.getMemberCount()));
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
        txtAccount = new javax.swing.JTextField();
        txtExpense = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        txtPayTotal = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        txtDep = new javax.swing.JTextField();
        scroll = new javax.swing.JScrollPane();
        tblPayment = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        lblStatus = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtFromDate = new com.toedter.calendar.JDateChooser();
        jLabel7 = new javax.swing.JLabel();
        txtToDate = new com.toedter.calendar.JDateChooser();
        jLabel9 = new javax.swing.JLabel();
        txtCurrency = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txtLabourGroup = new javax.swing.JTextField();
        btnCalculate = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        txtMember = new javax.swing.JTextField();
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
        jLabel2.setText("Pay Date");

        txtVouDate.setDateFormatString("dd/MM/yyyy");
        txtVouDate.setFont(Global.textFont);
        txtVouDate.setMaxSelectableDate(new java.util.Date(253370745073000L));

        txtAccount.setFont(Global.textFont);

        txtExpense.setFont(Global.textFont);

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Expense A/C");

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Cash / Bank");

        jLabel13.setFont(Global.lableFont);
        jLabel13.setText("Remark");

        txtRemark.setFont(Global.textFont);

        txtPayTotal.setEditable(false);
        txtPayTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPayTotal.setFont(Global.amtFont);

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Payment Total");

        jLabel15.setFont(Global.lableFont);
        jLabel15.setText("Department");

        txtDep.setFont(Global.textFont);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtVouDate, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtExpense, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtAccount, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(txtRemark))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDep, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 131, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPayTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtVouDate, txtVouNo});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtDep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtAccount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtPayTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel3)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtExpense, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtVouDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
        scroll.setViewportView(tblPayment);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblStatus.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        lblStatus.setText("NEW");

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("From Date");

        txtFromDate.setDateFormatString("dd/MM/yyyy");
        txtFromDate.setFont(Global.textFont);
        txtFromDate.setMaxSelectableDate(new java.util.Date(253370745073000L));

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("To Date");

        txtToDate.setDateFormatString("dd/MM/yyyy");
        txtToDate.setFont(Global.textFont);
        txtToDate.setMaxSelectableDate(new java.util.Date(253370745073000L));

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Currency");

        txtCurrency.setFont(Global.textFont);

        jLabel12.setFont(Global.lableFont);
        jLabel12.setText("Labour Group");

        txtLabourGroup.setFont(Global.textFont);

        btnCalculate.setBackground(Global.selectionColor);
        btnCalculate.setFont(Global.lableFont);
        btnCalculate.setForeground(new java.awt.Color(255, 255, 255));
        btnCalculate.setText("Calculate");
        btnCalculate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCalculateActionPerformed(evt);
            }
        });

        jLabel14.setFont(Global.lableFont);
        jLabel14.setText("Labour Member");

        txtMember.setFont(Global.textFont);
        txtMember.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(txtCurrency)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCalculate))
                    .addComponent(txtLabourGroup, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtMember, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblStatus)
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtFromDate, txtToDate});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtMember, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtLabourGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblStatus))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnCalculate)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scroll, javax.swing.GroupLayout.Alignment.LEADING)
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
                .addComponent(scroll, javax.swing.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)
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

    private void btnCalculateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalculateActionPerformed
        // TODO add your handling code here:
        calculatePayment();
    }//GEN-LAST:event_btnCalculateActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCalculate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JScrollPane scroll;
    private javax.swing.JTable tblPayment;
    private javax.swing.JTextField txtAccount;
    private javax.swing.JTextField txtCurrency;
    private javax.swing.JTextField txtDep;
    private javax.swing.JTextField txtExpense;
    private com.toedter.calendar.JDateChooser txtFromDate;
    private javax.swing.JTextField txtLabourGroup;
    private javax.swing.JTextField txtMember;
    private javax.swing.JFormattedTextField txtPayTotal;
    private javax.swing.JFormattedTextField txtRecord;
    private javax.swing.JTextField txtRemark;
    private com.toedter.calendar.JDateChooser txtToDate;
    private com.toedter.calendar.JDateChooser txtVouDate;
    private javax.swing.JTextField txtVouNo;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {
        if (source.equals("LabourGroup")) {
            setMemberCount();
        } else if (source.equals("PAYMENT_HISTORY")) {
            if (selectObj instanceof LabourPaymentDto p) {
                setVoucherDetail(p);
            }
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
