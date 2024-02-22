/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.acc.entry;

import com.repo.AccountRepo;
import com.acc.editor.DateAutoCompleter;
import com.acc.common.JournalTableModel;
import com.acc.dialog.CurrencyConversionDialog;
import com.acc.dialog.JournalEntryDialog;
import com.acc.editor.COA3AutoCompleter;
import com.acc.editor.DepartmentAutoCompleter;
import com.acc.editor.TraderAAutoCompleter;
import com.acc.model.DeleteObj;
import com.acc.model.Gl;
import com.common.ComponentUtil;
import com.common.DateLockUtil;
import com.common.Global;
import com.common.PanelControl;
import com.common.ReportFilter;
import com.common.RowHeader;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.common.Util1;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.repo.UserRepo;
import com.user.editor.ProjectAutoCompleter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.ListSelectionModel;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author DELL
 */
@Slf4j
public class Journal extends javax.swing.JPanel implements SelectionObserver, PanelControl {

    private final JournalTableModel tableModel = new JournalTableModel();
    private DateAutoCompleter dateAutoCompleter;
    private DepartmentAutoCompleter departmentAutoCompleter;
    private ProjectAutoCompleter projectAutoCompleter;
    private COA3AutoCompleter cOA3AutoCompleter;
    private TraderAAutoCompleter traderAAutoCompleter;
    private JProgressBar progress;
    private SelectionObserver observer;
    private JournalEntryDialog dialog;
    private CurrencyConversionDialog conversionDialog;
    private int selectRow = 0;
    private int selectCol =0;
    private AccountRepo accountRepo;
    private UserRepo userRepo;

    public void setAccountRepo(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    /**
     * Creates new form Journal
     */
    public Journal() {
        initComponents();
        initListener();
        initFormat();
    }

    public void initMain() {
        initCompleter();
        initTable();
        initRowHeader();
        searchJournal();
    }

    private void initFormat() {
        txtRecord.setFormatterFactory(Util1.getDecimalFormat());
    }

    private void initRowHeader() {
        RowHeader header = new RowHeader();
        JList list = header.createRowHeader(tblJournal, 30);
        scroll.setRowHeaderView(list);
    }

    private void deleteVoucher() {
        selectRow = tblJournal.convertRowIndexToModel(tblJournal.getSelectedRow());
        if (selectRow >= 0) {
            Gl gl = tableModel.getGl(selectRow);
            if (gl.isTranLock()) {
                DateLockUtil.showMessage(this);
                return;
            }
            String glVouNo = gl.getGlVouNo();
            if (glVouNo != null) {
                int yes_no = JOptionPane.showConfirmDialog(Global.parentForm, "Are you sure to delete journal?",
                        "Delete", JOptionPane.YES_NO_OPTION);
                if (yes_no == 0) {
                    DeleteObj obj = new DeleteObj();
                    obj.setGlVouNo(glVouNo);
                    obj.setCompCode(Global.compCode);
                    obj.setModifyBy(Global.loginUser.getUserCode());
                    accountRepo.deleteVoucher(obj).subscribe((t) -> {
                        if (t) {
                            tableModel.delete(selectRow);
                            focusOnTable();
                        }
                    });
                }
            }
        }
    }

    private void focusOnTable() {
        int rc = tblJournal.getRowCount();
        if (rc > 1) {
            tblJournal.setRowSelectionInterval(rc - 1, rc - 1);
            tblJournal.setColumnSelectionInterval(0, 0);
            tblJournal.requestFocus();
        } else {
            txtDate.requestFocusInWindow();
        }
    }

    private void initListener() {
        ComponentUtil.addFocusListener(this);
        txtRefrence.addActionListener(action);
        txtDesp.addActionListener(action);
    }
    private final ActionListener action = (ActionEvent e) -> {
        searchJournal();
    };

    private List<String> getListDep() {
        return departmentAutoCompleter.getDepartment() == null ? new ArrayList<>() : departmentAutoCompleter.getListOption();
    }

    private String getProjectNo() {
        return projectAutoCompleter == null ? "-" : projectAutoCompleter.getProject().getKey().getProjectNo();
    }

    private void searchJournal() {
        progress.setIndeterminate(true);
        ReportFilter filter = new ReportFilter(Global.macId, Global.compCode, Global.deptId);
        filter.setFromDate(dateAutoCompleter.getDateModel().getStartDate());
        filter.setToDate(dateAutoCompleter.getDateModel().getEndDate());
        filter.setListDepartment(getListDep());
        filter.setDesp(txtDesp.getText());
        filter.setReference(txtRefrence.getText());
        filter.setGlVouNo(txtVouNo.getText());
        filter.setProjectNo(getProjectNo());
        filter.setCoaCode(cOA3AutoCompleter.getCOA().getKey().getCoaCode());
        filter.setTraderCode(traderAAutoCompleter.getTrader().getKey().getCode());
        tableModel.clear();
        accountRepo.searchJournal(filter)
                .doOnNext(tableModel::addObject)
                .doOnNext((obj) -> calTotal())
                .doOnNext(obj -> checkDateLock(obj))
                .doOnError((e) -> {
                    JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }).doOnTerminate(() -> {
            ComponentUtil.scrollTable(tblJournal, selectRow,selectCol);
            progress.setIndeterminate(false);
        }).subscribe();
    }

    private void calTotal() {
        txtRecord.setValue(tableModel.getSize());
    }

    private void checkDateLock(Gl t) {
        if (DateLockUtil.isLockDate(t.getGlDate())) {
            t.setTranLock(true);
        }
    }

    public void openJournalEntryDialog(String glVou, String status) {
        if (dialog == null) {
            dialog = new JournalEntryDialog(Global.parentForm);
            dialog.setAccountRepo(accountRepo);
            dialog.setUserRepo(userRepo);
            dialog.setObserver(this);
            dialog.initMain();
            dialog.setSize(Global.width - 30, Global.height - 30);
            dialog.setIconImage(Global.parentForm.getIconImage());
            dialog.setLocationRelativeTo(null);
        }
        dialog.setStatus(status);
        dialog.searchJournalByVouId(glVou);
    }

    private void initCompleter() {
        dateAutoCompleter = new DateAutoCompleter(txtDate);
        dateAutoCompleter.setObserver(this);
        projectAutoCompleter = new ProjectAutoCompleter(txtProjectNo, userRepo, null, true);
        projectAutoCompleter.setObserver(this);
        cOA3AutoCompleter = new COA3AutoCompleter(txtAccount, accountRepo, null, true, 3);
        cOA3AutoCompleter.setObserver(this);
        departmentAutoCompleter = new DepartmentAutoCompleter(txtDep, null, true, true);
        departmentAutoCompleter.setObserver(this);
        traderAAutoCompleter = new TraderAAutoCompleter(txtTrader, accountRepo, null, true);
        traderAAutoCompleter.setObserver(this);
        accountRepo.getDepartment().doOnSuccess((t) -> {
            departmentAutoCompleter.setListDepartment(t);
        }).subscribe();
    }

    private void initTable() {
        tblJournal.setModel(tableModel);
        tblJournal.getTableHeader().setFont(Global.tblHeaderFont);
        tblJournal.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblJournal.getColumnModel().getColumn(0).setPreferredWidth(5);//Date
        tblJournal.getColumnModel().getColumn(1).setPreferredWidth(50);//Vou
        tblJournal.getColumnModel().getColumn(2).setPreferredWidth(400);//Ref
        tblJournal.getColumnModel().getColumn(3).setPreferredWidth(400);//Ref
        tblJournal.getColumnModel().getColumn(4).setPreferredWidth(100);//Ref
        tblJournal.getColumnModel().getColumn(5).setPreferredWidth(50);//amt
        tblJournal.setDefaultRenderer(Object.class, new TableCellRender());
        tblJournal.setDefaultRenderer(Double.class, new TableCellRender());
        tblJournal.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectRow = tblJournal.convertRowIndexToModel(tblJournal.getSelectedRow());
                selectCol = tblJournal.getSelectedColumn();
                if (e.getClickCount() == 2) {
                    if (tblJournal.getSelectedRow() >= 0) {
                        Gl vGl = tableModel.getGl(selectRow);
                        String tranSource = vGl.getTranSource();
                        String vouNo = vGl.getGlVouNo();
                        switch (tranSource) {
                            case "GV" ->
                                openJournalEntryDialog(vouNo, "EDIT");
                            case "EX" ->
                                conversionDialog(vouNo);
                        }
                    }
                }
            }
        });

    }

    private void observeMain() {
        observer.selected("control", this);
        observer.selected("save", false);
        observer.selected("print", true);
        observer.selected("history", false);
        observer.selected("delete", true);
        observer.selected("refresh", true);
    }

    private void conversionDialog(String vouNo) {
        if (conversionDialog == null) {
            conversionDialog = new CurrencyConversionDialog(Global.parentForm);
            conversionDialog.setUserRepo(userRepo);
            conversionDialog.setAccountRepo(accountRepo);
            conversionDialog.setLocationRelativeTo(null);
            conversionDialog.initMain();
        }
        conversionDialog.search(vouNo);
        conversionDialog.setVisible(true);
    }

    private void printVoucher(Gl gl) {
        progress.setIndeterminate(true);
        String glVouNo = gl.getGlVouNo();
        accountRepo.getJournal(glVouNo).doOnSuccess((list) -> {
            try {
                String name = chkPl.isSelected() ? "Profit&LossAppropriation.jasper" : "JournalVoucher.jasper";
                String rpPath = Global.accountRP + name;
                Map<String, Object> p = new HashMap();
                p.put("p_report_name", "Journal Voucher");
                p.put("p_date", String.format("Between %s and %s", dateAutoCompleter.getDateModel().getStartDate(), dateAutoCompleter.getDateModel().getEndDate()));
                p.put("p_print_date", Util1.getTodayDateTime());
                p.put("p_comp_name", Global.companyName);
                p.put("p_comp_address", Global.companyAddress);
                p.put("p_comp_phone", Global.companyPhone);
                p.put("p_vou_type", gl.getTranSource());
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        chkPl = new javax.swing.JCheckBox();
        txtRecord = new javax.swing.JFormattedTextField();
        scroll = new javax.swing.JScrollPane();
        tblJournal = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtVouNo = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtRefrence = new javax.swing.JTextField();
        btnEntry = new javax.swing.JButton();
        txtDate = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtDep = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtDesp = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtProjectNo = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtAccount = new javax.swing.JTextField();
        btnEntry1 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        txtTrader = new javax.swing.JTextField();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Records :");

        chkPl.setFont(Global.lableFont);
        chkPl.setText("P/L Acc");

        txtRecord.setEditable(false);
        txtRecord.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtRecord.setFont(Global.lableFont);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtRecord, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(chkPl)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(txtRecord, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(chkPl, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tblJournal.setFont(Global.textFont);
        tblJournal.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblJournal.setName("tblJournal"); // NOI18N
        tblJournal.setRowHeight(Global.tblRowHeight);
        scroll.setViewportView(tblJournal);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setFont(Global.lableFont);
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Date");

        txtVouNo.setFont(Global.shortCutFont);
        txtVouNo.setName("txtVouNo"); // NOI18N

        jLabel3.setFont(Global.lableFont);
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Voucher No");

        jLabel4.setFont(Global.lableFont);
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Refrence");

        txtRefrence.setFont(Global.shortCutFont);
        txtRefrence.setName("txtRefrence"); // NOI18N
        txtRefrence.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRefrenceActionPerformed(evt);
            }
        });

        btnEntry.setBackground(Global.selectionColor);
        btnEntry.setFont(Global.lableFont);
        btnEntry.setForeground(new java.awt.Color(255, 255, 255));
        btnEntry.setText("New Journal");
        btnEntry.setToolTipText("New Journal");
        btnEntry.setName("btnEntry"); // NOI18N
        btnEntry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEntryActionPerformed(evt);
            }
        });

        txtDate.setFont(Global.textFont);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Department");

        txtDep.setFont(Global.textFont);

        jLabel6.setFont(Global.lableFont);
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Description");

        txtDesp.setFont(Global.shortCutFont);
        txtDesp.setName("txtRefrence"); // NOI18N
        txtDesp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDespActionPerformed(evt);
            }
        });

        jLabel7.setFont(Global.lableFont);
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Project No");

        txtProjectNo.setFont(Global.shortCutFont);
        txtProjectNo.setName("txtRefrence"); // NOI18N
        txtProjectNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtProjectNoActionPerformed(evt);
            }
        });

        jLabel8.setFont(Global.lableFont);
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Account");

        txtAccount.setFont(Global.shortCutFont);
        txtAccount.setName("txtRefrence"); // NOI18N
        txtAccount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAccountActionPerformed(evt);
            }
        });

        btnEntry1.setFont(Global.lableFont);
        btnEntry1.setText("Currency Conversion");
        btnEntry1.setToolTipText("New Journal");
        btnEntry1.setName("btnEntry"); // NOI18N
        btnEntry1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEntry1ActionPerformed(evt);
            }
        });

        jLabel9.setFont(Global.lableFont);
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Trader");

        txtTrader.setFont(Global.shortCutFont);
        txtTrader.setName("txtRefrence"); // NOI18N
        txtTrader.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTraderActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtDate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtDep)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtVouNo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtRefrence)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtDesp)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtAccount)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtTrader)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtProjectNo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEntry1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEntry)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)
                        .addComponent(jLabel4)
                        .addComponent(txtRefrence, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnEntry, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel6)
                        .addComponent(txtDesp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7)
                        .addComponent(txtProjectNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8)
                        .addComponent(txtAccount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnEntry1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel9)
                        .addComponent(txtTrader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtDep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scroll, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scroll, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observeMain();
    }//GEN-LAST:event_formComponentShown

    private void txtRefrenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRefrenceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRefrenceActionPerformed

    private void btnEntryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEntryActionPerformed
        // TODO add your handling code here:
        openJournalEntryDialog("-", "NEW");
    }//GEN-LAST:event_btnEntryActionPerformed

    private void txtDespActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDespActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDespActionPerformed

    private void txtProjectNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProjectNoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtProjectNoActionPerformed

    private void txtAccountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAccountActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAccountActionPerformed

    private void btnEntry1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEntry1ActionPerformed
        // TODO add your handling code here:
        conversionDialog(null);
    }//GEN-LAST:event_btnEntry1ActionPerformed

    private void txtTraderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTraderActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTraderActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEntry;
    private javax.swing.JButton btnEntry1;
    private javax.swing.JCheckBox chkPl;
    private javax.swing.JLabel jLabel1;
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
    private javax.swing.JScrollPane scroll;
    private javax.swing.JTable tblJournal;
    private javax.swing.JTextField txtAccount;
    private javax.swing.JTextField txtDate;
    private javax.swing.JTextField txtDep;
    private javax.swing.JTextField txtDesp;
    private javax.swing.JTextField txtProjectNo;
    private javax.swing.JFormattedTextField txtRecord;
    private javax.swing.JTextField txtRefrence;
    private javax.swing.JTextField txtTrader;
    private javax.swing.JTextField txtVouNo;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {
        if (source != null) {
            if (source.equals("print")) {
                if (selectObj instanceof Gl gl) {
                    printVoucher(gl);
                }
            } else {
                searchJournal();
            }
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
        int row = tblJournal.convertRowIndexToModel(tblJournal.getSelectedRow());
        if (row >= 0) {
            printVoucher(tableModel.getGl(row));
        }
    }

    @Override
    public void refresh() {
        searchJournal();
    }

    @Override
    public void filter() {
    }

    @Override
    public String panelName() {
        return this.getName();
    }
}
