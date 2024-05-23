/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.acc.entry;

import com.repo.AccountRepo;
import com.acc.editor.DateAutoCompleter;
import com.acc.common.VoucherTableModel;
import com.acc.dialog.FindDialog;
import com.acc.dialog.VoucherEntryDailog;
import com.acc.editor.COAAutoCompleter;
import com.acc.model.Gl;
import com.common.Global;
import com.common.PanelControl;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.acc.editor.DepartmentAutoCompleter;
import com.acc.editor.DespAutoCompleter;
import com.acc.editor.RefAutoCompleter;
import com.acc.model.ChartOfAccount;
import com.acc.model.DeleteObj;
import com.common.ComponentUtil;
import com.common.DateLockUtil;
import com.common.ProUtil;
import com.common.ReportFilter;
import com.common.RowHeader;
import com.common.Util1;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.repo.UserRepo;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import lombok.Setter;
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
public class DrCrVoucher extends javax.swing.JPanel implements SelectionObserver, PanelControl, KeyListener {

    private int selectRow = 0;
    private int selectCol = 0;
    private DateAutoCompleter dateAutoCompleter;
    private DepartmentAutoCompleter departmentAutoCompleter;
    private DespAutoCompleter despAutoCompleter;
    private RefAutoCompleter refAutoCompleter;
    private COAAutoCompleter cOAAutoCompleter;
    @Setter
    private SelectionObserver observer;
    @Setter
    private JProgressBar progress;
    private final VoucherTableModel voucherTableModel = new VoucherTableModel();
    @Setter
    private AccountRepo accountRepo;
    @Setter
    private UserRepo userRepo;
    private VoucherEntryDailog dialog;
    private FindDialog findDialog;

    /**
     * Creates new form CrDrVoucher1
     */
    public DrCrVoucher() {
        initComponents();
        initKeyListener();
    }

    private void initDisable() {
        btnDr.setEnabled(!ProUtil.isDisableDrVoucher());
        btnCr.setEnabled(!ProUtil.isDisableCrVoucher());
    }

    private void initProperty() {
        ComponentUtil.addFocusListener(this);
        ComponentUtil.setTextProperty(this);
        txtDr.setForeground(Color.green);
        txtCr.setForeground(Color.red);
        txtDr.setFont(Global.menuFont);
        txtCr.setFont(Global.menuFont);
        txtOpening.setFont(Global.menuFont);
        txtClosing.setFont(Global.menuFont);

    }

    private void initTable() {
        tblVoucher.setModel(voucherTableModel);
        tblVoucher.getTableHeader().setFont(Global.tblHeaderFont);
        tblVoucher.setDefaultRenderer(Object.class, new TableCellRender());
        tblVoucher.setDefaultRenderer(Double.class, new TableCellRender());
        tblVoucher.getTableHeader().setFont(Global.tblHeaderFont);
        tblVoucher.getColumnModel().getColumn(0).setPreferredWidth(40);//date
        tblVoucher.getColumnModel().getColumn(1).setPreferredWidth(100);//acc
        tblVoucher.getColumnModel().getColumn(2).setPreferredWidth(50);//vou
        tblVoucher.getColumnModel().getColumn(3).setPreferredWidth(150);//desp
        tblVoucher.getColumnModel().getColumn(4).setPreferredWidth(50);//ref
        tblVoucher.getColumnModel().getColumn(5).setPreferredWidth(50);//narration
        tblVoucher.getColumnModel().getColumn(6).setPreferredWidth(100);//from
        tblVoucher.getColumnModel().getColumn(7).setPreferredWidth(100);//for
        tblVoucher.getColumnModel().getColumn(8).setPreferredWidth(40);//type
        tblVoucher.getColumnModel().getColumn(9).setPreferredWidth(50);//dr
        tblVoucher.getColumnModel().getColumn(10).setPreferredWidth(50);//cr

        tblVoucher.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectRow = tblVoucher.convertRowIndexToModel(tblVoucher.getSelectedRow());
                selectCol = tblVoucher.getSelectedColumn();
                if (e.getClickCount() == 2) {
                    if (tblVoucher.getSelectedRow() >= 0) {
                        Gl gl = voucherTableModel.getVGl(selectRow);
                        openVoucherDialog(gl.getTranSource(), gl.getGlVouNo());
                    }
                }
            }

        });
    }

    private void initCombo() {
        dateAutoCompleter = new DateAutoCompleter(txtDate);
        dateAutoCompleter.setObserver(this);
        departmentAutoCompleter = new DepartmentAutoCompleter(txtDept, null, true, true);
        departmentAutoCompleter.setObserver(this);
        accountRepo.getDefaultDepartment().doOnSuccess((t) -> {
            departmentAutoCompleter.setDepartment(t);
        }).subscribe();
        accountRepo.getDepartment().doOnSuccess((t) -> {
            departmentAutoCompleter.setListDepartment(t);
        }).subscribe();
        despAutoCompleter = new DespAutoCompleter(txtDesp, accountRepo, null, true);
        despAutoCompleter.setObserver(this);
        refAutoCompleter = new RefAutoCompleter(txtRef, accountRepo, null, true);
        refAutoCompleter.setObserver(this);
        cOAAutoCompleter = new COAAutoCompleter(txtCash, null, true);
        cOAAutoCompleter.setObserver(this);
        accountRepo.getCashBank().doOnSuccess((t) -> {
            cOAAutoCompleter.setListCOA(t);
        }).doOnTerminate(() -> {
            accountRepo.getDefaultCash().doOnSuccess((t) -> {
                if (t != null) {
                    cOAAutoCompleter.setCoa(t);
                }
            }).subscribe();
        }).subscribe();
    }

    public void initMain() {
        initProperty();
        initDisable();
        initCombo();
        initTable();
        initRowHeader();
        initFindDialog();
        search();
    }

    private void initFindDialog() {
        findDialog = new FindDialog(Global.parentForm, tblVoucher);
    }

    private void initRowHeader() {
        RowHeader header = new RowHeader();
        JList list = header.createRowHeader(tblVoucher, 30);
        scroll.setRowHeaderView(list);
    }

    private void initKeyListener() {
        txtDate.addKeyListener(this);
        txtDept.addKeyListener(this);
        txtDesp.addKeyListener(this);
        txtRef.addKeyListener(this);
        txtRefNo.addKeyListener(this);
        txtVouNo.addKeyListener(this);
    }

    private List<String> getListDep() {
        return departmentAutoCompleter.getDepartment() == null ? new ArrayList<>() : departmentAutoCompleter.getListOption();
    }

    private void search() {
        if (progress != null) {
            progress.setIndeterminate(true);
            ChartOfAccount coa = cOAAutoCompleter.getCOA();
            ReportFilter filter = new ReportFilter(Global.macId, Global.compCode, Global.deptId);
            filter.setFromDate(dateAutoCompleter.getDateModel().getStartDate());
            filter.setToDate(dateAutoCompleter.getDateModel().getEndDate());
            filter.setListDepartment(getListDep());
            filter.setDesp(txtDesp.getText());
            filter.setGlVouNo(txtVouNo.getText());
            filter.setReference(txtRef.getText());
            filter.setSrcAcc(coa == null ? "-" : coa.getKey().getCoaCode());
            voucherTableModel.clear();
            txtDr.setValue(0);
            txtCr.setValue(0);
            txtOpening.setValue(0);
            txtClosing.setValue(0);
            txtRecord.setValue(0);
            accountRepo.searchVoucher(filter)
                    .doOnNext(voucherTableModel::addObject)
                    .doOnNext((obj) -> calTotal())
                    .doOnNext(obj -> checkDateLock(obj))
                    .doOnError((e) -> {
                        JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }).doOnTerminate(() -> {
            }).doOnTerminate(() -> {
                calOpening();
                ComponentUtil.scrollTable(tblVoucher, selectRow, selectCol);
            }).subscribe();
        }

    }

    private void calTotal() {
        txtRecord.setValue(voucherTableModel.getSize());
        txtDr.setValue(voucherTableModel.getDrAmt());
        txtCr.setValue(voucherTableModel.getCrAmt());
    }

    private void checkDateLock(Gl t) {
        if (DateLockUtil.isLockDate(t.getGlDate())) {
            t.setTranLock(true);
        }
    }

    private void calculateClosing() {
        double opening = Util1.getDouble(txtOpening.getValue());
        double closing = Util1.getDouble(txtOpening.getValue());
        txtOpening.setForeground(opening >= 0 ? Color.GREEN : Color.RED);
        txtClosing.setForeground(closing >= 0 ? Color.GREEN : Color.RED);
    }

    private void calOpening() {
        ChartOfAccount coa = cOAAutoCompleter.getCOA();
        if (coa != null) {
            if (!coa.getKey().getCoaCode().equals("-")) {
                String startDate = dateAutoCompleter.getDateModel().getStartDate();
                String endDate = dateAutoCompleter.getDateModel().getEndDate();
                ReportFilter filter = new ReportFilter(Global.macId, Global.compCode, Global.deptId);
                filter.setToDate(endDate);
                filter.setFromDate(startDate);
                filter.setCurCode(Global.currency);
                filter.setListDepartment(getListDep());
                filter.setCoaCode(coa.getKey().getCoaCode());
                accountRepo.getOpeningClosing(filter).doOnSuccess((t) -> {
                    txtOpening.setValue(t.getOpening());
                    txtDr.setValue(t.getDrAmt());
                    txtCr.setValue(t.getCrAmt());
                    txtClosing.setValue(t.getClosing());
                }).doOnTerminate(() -> {
                    calculateClosing();
                    progress.setIndeterminate(false);
                }).subscribe();
            } else {
                progress.setIndeterminate(false);
            }
        } else {
            progress.setIndeterminate(false);
        }
    }

    public void openVoucherDialog(String type, String glVouNo) {
        progress.setIndeterminate(true);
        if (dialog == null) {
            dialog = new VoucherEntryDailog(Global.parentForm);
            dialog.setSize(Global.width - 20, Global.height - 20);
            dialog.setLocationRelativeTo(null);
            dialog.setAccountRepo(accountRepo);
            dialog.setUserRepo(userRepo);
            dialog.setObserver(this);
            dialog.initMain();
        }
        dialog.setVouType(type);
        dialog.searchDetail(glVouNo);
    }

    private void deleteVoucher() {
        int row = tblVoucher.convertRowIndexToModel(tblVoucher.getSelectedRow());
        if (row >= 0) {
            Gl gl = voucherTableModel.getVGl(row);
            if (gl.isTranLock()) {
                DateLockUtil.showMessage(this);
                return;
            }
            String glVouNo = gl.getGlVouNo();
            if (glVouNo != null) {
                int yes_no = JOptionPane.showConfirmDialog(Global.parentForm, "Are you sure to delete voucher?",
                        "Delete", JOptionPane.YES_NO_OPTION);
                if (yes_no == 0) {
                    DeleteObj obj = new DeleteObj();
                    obj.setGlVouNo(glVouNo);
                    obj.setCompCode(Global.compCode);
                    obj.setModifyBy(Global.loginUser.getUserCode());
                    accountRepo.deleteVoucher(obj).doOnSuccess((t) -> {
                        if (t) {
                            voucherTableModel.remove(selectRow);
                            focusOnTable();
                        }
                    }).subscribe();

                }
            }
        }
    }

    private void focusOnTable() {
        int rc = tblVoucher.getRowCount();
        if (rc >= 1) {
            tblVoucher.setRowSelectionInterval(rc - 1, rc - 1);
            tblVoucher.setColumnSelectionInterval(0, 0);
            tblVoucher.requestFocus();
        } else {
            txtDate.requestFocusInWindow();
        }
    }

    private void printVoucher(Gl gl) {
        progress.setIndeterminate(true);
        String glVouNo = gl.getGlVouNo();
        accountRepo.getVoucher(glVouNo).doOnSuccess((list) -> {
            try {
                String rpName = gl.getTranSource().equals("DR") ? "Payment / Debit Voucher" : "Receipt / Credit Voucher";
                String reportName = Util1.isNull(ProUtil.getDrCrReport(), "DrCrVoucherA5");
                String rpPath = Global.accountRP + reportName + ".jasper";
                Map<String, Object> p = new HashMap();
                p.put("p_report_name", rpName);
                p.put("p_date", String.format("Between %s and %s", dateAutoCompleter.getDateModel().getStartDate(), dateAutoCompleter.getDateModel().getEndDate()));
                p.put("p_print_date", Util1.getTodayDateTime());
                p.put("p_comp_name", Global.companyName);
                p.put("p_comp_address", Global.companyAddress);
                p.put("p_comp_phone", Global.companyPhone);
                p.put("p_vou_type", gl.getTranSource());
                p.put("p_logo_path", ProUtil.logoPath());
                Util1.initJasperContext();
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(Util1.gson.toJson(list));
                JsonDataSource ds = new JsonDataSource(node, null) {
                };
                JasperPrint js = JasperFillManager.fillReport(rpPath, p, ds);
                JasperViewer.viewReport(js, false);
                progress.setIndeterminate(false);
            } catch (JsonProcessingException | JRException ex) {
                progress.setIndeterminate(false);
                log.error("printVoucher : " + ex.getMessage());
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }).doOnError((e) -> {
            progress.setIndeterminate(false);
            JOptionPane.showMessageDialog(this, e.getMessage());
        }).subscribe();

    }

    private void observeMain() {
        observer.selected("control", this);
        observer.selected("save", false);
        observer.selected("print", true);
        observer.selected("history", false);
        observer.selected("delete", true);
        observer.selected("refresh", true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scroll = new javax.swing.JScrollPane();
        tblVoucher = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        btnDr = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        txtDesp = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtRefNo = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtRef = new javax.swing.JTextField();
        btnCr = new javax.swing.JButton();
        txtDate = new javax.swing.JTextField();
        txtDept = new javax.swing.JTextField();
        txtVouNo = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtCash = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        txtOpening = new javax.swing.JFormattedTextField();
        jLabel14 = new javax.swing.JLabel();
        txtClosing = new javax.swing.JFormattedTextField();
        jLabel15 = new javax.swing.JLabel();
        txtDr = new javax.swing.JFormattedTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        txtCr = new javax.swing.JFormattedTextField();
        jPanel3 = new javax.swing.JPanel();
        txtRecord = new javax.swing.JFormattedTextField();
        jLabel7 = new javax.swing.JLabel();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        tblVoucher.setFont(Global.textFont);
        tblVoucher.setModel(new javax.swing.table.DefaultTableModel(
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
        tblVoucher.setRowHeight(Global.tblRowHeight);
        tblVoucher.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblVoucherMouseClicked(evt);
            }
        });
        scroll.setViewportView(tblVoucher);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Dep :");

        btnDr.setBackground(Color.green);
        btnDr.setFont(Global.lableFont);
        btnDr.setForeground(Color.white);
        btnDr.setText("Payment / Debit");
        btnDr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDrActionPerformed(evt);
            }
        });

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Ref No");

        txtDesp.setFont(Global.textFont);
        txtDesp.setName("txtDesp"); // NOI18N

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Reference");

        txtRefNo.setFont(Global.textFont);
        txtRefNo.setName("txtRefNo"); // NOI18N

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Date");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Vou No");

        txtRef.setFont(Global.textFont);
        txtRef.setName("txtRef"); // NOI18N

        btnCr.setBackground(Color.red);
        btnCr.setFont(Global.lableFont);
        btnCr.setForeground(Color.white);
        btnCr.setText("Receipt / Credit");
        btnCr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCrActionPerformed(evt);
            }
        });

        txtDate.setFont(Global.textFont);
        txtDate.setName("txtDate"); // NOI18N
        txtDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDateActionPerformed(evt);
            }
        });

        txtDept.setFont(Global.textFont);
        txtDept.setName("txtDept"); // NOI18N
        txtDept.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDeptActionPerformed(evt);
            }
        });

        txtVouNo.setFont(Global.textFont);
        txtVouNo.setName("txtVouNo"); // NOI18N

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Description");

        jLabel12.setFont(Global.lableFont);
        jLabel12.setText("Cash");

        txtCash.setFont(Global.textFont);
        txtCash.setName("txtDate"); // NOI18N
        txtCash.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCashActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtDate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtCash)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtDept)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtVouNo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtDesp)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtRef)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtRefNo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDr)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCr)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtDate)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtVouNo)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtDesp)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtRef)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtRefNo)
                        .addComponent(btnDr)
                        .addComponent(btnCr))
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtDept, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtCash))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        txtOpening.setEditable(false);
        txtOpening.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel14.setFont(Global.menuFont);
        jLabel14.setText("Opening");

        txtClosing.setEditable(false);
        txtClosing.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel15.setFont(Global.menuFont);
        jLabel15.setText("Closing");

        txtDr.setEditable(false);
        txtDr.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel13.setFont(Global.menuFont);
        jLabel13.setText("Total Cash In / Debit");

        jLabel16.setFont(Global.menuFont);
        jLabel16.setText("Total Cash Out / Credit");

        txtCr.setEditable(false);
        txtCr.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        txtRecord.setEditable(false);

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Record :");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtRecord, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtRecord, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtOpening, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtClosing, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtDr, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtCr, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(jLabel16))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCr)
                            .addComponent(txtDr)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14)
                            .addComponent(jLabel15))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtOpening, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                            .addComponent(txtClosing)))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scroll)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scroll, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnCrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCrActionPerformed
        // TODO add your handling code here:
        openVoucherDialog("CR", null);
    }//GEN-LAST:event_btnCrActionPerformed

    private void btnDrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDrActionPerformed
        // TODO add your handling code here:
        openVoucherDialog("DR", null);
    }//GEN-LAST:event_btnDrActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observeMain();
    }//GEN-LAST:event_formComponentShown

    private void tblVoucherMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVoucherMouseClicked
        // TODO add your handling code here:

    }//GEN-LAST:event_tblVoucherMouseClicked

    private void txtDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDateActionPerformed

    private void txtDeptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDeptActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDeptActionPerformed

    private void txtCashActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCashActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCashActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCr;
    private javax.swing.JButton btnDr;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane scroll;
    private javax.swing.JTable tblVoucher;
    private javax.swing.JTextField txtCash;
    private javax.swing.JFormattedTextField txtClosing;
    private javax.swing.JFormattedTextField txtCr;
    private javax.swing.JTextField txtDate;
    private javax.swing.JTextField txtDept;
    private javax.swing.JTextField txtDesp;
    private javax.swing.JFormattedTextField txtDr;
    private javax.swing.JFormattedTextField txtOpening;
    private javax.swing.JFormattedTextField txtRecord;
    private javax.swing.JTextField txtRef;
    private javax.swing.JTextField txtRefNo;
    private javax.swing.JTextField txtVouNo;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {
        if (source.equals("print")) {
            if (selectObj instanceof Gl gl) {
                printVoucher(gl);
            }
        } else if (source.equals("progress")) {
            if (selectObj instanceof Boolean status) {
                progress.setIndeterminate(status);
            }
        } else if (source.equals("refresh")) {
            search();
        } else {
            search();
        }
    }

    @Override
    public void save() {
    }

    @Override
    public void delete() {
        deleteVoucher();
    }

    @Override
    public void newForm() {
    }

    @Override
    public void history() {
    }

    @Override
    public void print() {
        int row = tblVoucher.convertRowIndexToModel(tblVoucher.getSelectedRow());
        if (row >= 0) {
            printVoucher(voucherTableModel.getVGl(row));
        }
    }

    @Override
    public void refresh() {
        search();
    }

    @Override
    public String panelName() {
        return this.getName();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        Object sourceObj = e.getSource();
        String ctrlName = "-";
        if (sourceObj instanceof JTextField txt) {
            ctrlName = txt.getName();
        }
        switch (ctrlName) {
            case "txtDate" -> {
                log.info(e.getKeyCode() + "");
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtDept.requestFocus();
                }
            }

            case "txtDept" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtVouNo.requestFocus();
                }
            }

            case "txtVouNo" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtDesp.requestFocus();
                    search();
                }
            }

            case "txtDesp" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtRef.requestFocus();
                    search();
                }
            }

            case "txtRef" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtRefNo.requestFocus();
                    search();
                }
            }

            case "txtRefNo" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    tblVoucher.requestFocus();
                    search();
                }
            }

        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void filter() {
        findDialog.setVisible(!findDialog.isVisible());
    }
}
