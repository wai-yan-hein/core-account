/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.acc.setup;

import com.repo.AccountRepo;
import com.acc.common.OpeningBalanceTableModel;
import com.acc.dialog.FindDialog;
import com.acc.editor.COA3AutoCompleter;
import com.acc.model.OpeningBalance;
import com.acc.model.ChartOfAccount;
import com.acc.editor.COA3CellEditor;
import com.acc.editor.TraderCellEditor;
import com.acc.editor.DepartmentCellEditor;
import com.acc.editor.DepartmentAutoCompleter;
import com.acc.editor.TraderAAutoCompleter;
import com.user.editor.CurrencyAutoCompleter;
import com.acc.model.OpeningKey;
import com.common.ComponentUtil;
import com.common.DateLockUtil;
import com.common.SelectionObserver;
import com.common.PanelControl;
import com.common.Global;
import com.common.Util1;
import com.common.DecimalFormatRender;
import com.common.IconUtil;
import com.common.ReportFilter;
import com.user.editor.AutoClearEditor;
import com.repo.UserRepo;
import com.user.editor.CurrencyEditor;
import com.user.editor.ProjectAutoCompleter;
import com.user.editor.ProjectCellEditor;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractAction;
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
 * @author myoht
 */
@Slf4j
public class COAOpening extends javax.swing.JPanel implements SelectionObserver, PanelControl {

    private final OpeningBalanceTableModel openingTableModel = new OpeningBalanceTableModel();

    private AccountRepo accountRepo;
    private UserRepo userRepo;

    private DepartmentAutoCompleter departmentAutoCompleter;
    private CurrencyAutoCompleter currencyAutoCompleter;
    private COA3AutoCompleter coaAutoCompleter;
    private TraderAAutoCompleter tradeAutoCompleter;
    private ProjectAutoCompleter projectAutoCompleter;
    private SelectionObserver observer;
    private JProgressBar progress;
    private int row = 0;
    private int column = 0;
    private FindDialog findDialog;

    public void setAccountRepo(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Creates new form COAOpening
     */
    public COAOpening() {
        initComponents();
        actionMapping();
    }

    private void batchLock(boolean lock) {
        tblOpening.setEnabled(lock);
        observer.selected("save", lock);
        observer.selected("delete", lock);
    }

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }

    public SelectionObserver getObserver() {
        return observer;
    }

    public void setObservaer(SelectionObserver observer) {
        this.observer = observer;
    }

    private void actionMapping() {
        tblOpening.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");
        tblOpening.getActionMap().put("delete", new DeleteAction());
    }

    public class DeleteAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            deleteTran();
        }
    }

    private void deleteTran() {
        if (row >= 0) {
            OpeningBalance b = openingTableModel.getOpening(row);
            OpeningKey key = b.getKey();
            if (key != null) {
                int yn = JOptionPane.showConfirmDialog(this, "Are you sure to delete?");
                if (yn == JOptionPane.YES_OPTION) {
                    accountRepo.delete(key).doOnSuccess((sucess) -> {
                        openingTableModel.deleteOpening(row);
                    }).doOnTerminate(() -> {
                        calDrCr();
                    }).subscribe();
                }
            } else {
                openingTableModel.deleteOpening(row);
                calDrCr();
            }
        }
    }

    public void initMain() {
        initProperty();
        batchLock(!Global.batchLock);
        txtDate.setDate(Util1.parseDate(Global.startDate, "dd/MM/yyyy"));
        initComboBox();
        initTable();
        initFindDialog();
        searchOpening();
    }

    private void initFindDialog() {
        findDialog = new FindDialog(Global.parentForm, tblOpening);
    }

    private void initComboBox() {
        departmentAutoCompleter = new DepartmentAutoCompleter(txtDept, null, true, true);
        departmentAutoCompleter.setObserver(this);
        accountRepo.getDepartment().doOnSuccess((t) -> {
            departmentAutoCompleter.setListDepartment(t);
        }).subscribe();
        currencyAutoCompleter = new CurrencyAutoCompleter(txtCurrency, null);
        currencyAutoCompleter.setObserver(this);
        userRepo.getCurrency().subscribe((t) -> {
            currencyAutoCompleter.setListCurrency(t);
        });
        userRepo.getDefaultCurrency().subscribe((c) -> {
            currencyAutoCompleter.setCurrency(c);
        });
        coaAutoCompleter = new COA3AutoCompleter(txtCOA, accountRepo, null, true, 0);
        coaAutoCompleter.setObserver(this);
        projectAutoCompleter = new ProjectAutoCompleter(txtProjectNo, userRepo, null, true);
        projectAutoCompleter.setObserver(this);
    }

    private void initProperty() {
        ComponentUtil.addFocusListener(this);
        ComponentUtil.setTextProperty(this);
        txtDrAmt.setFont(Global.menuFont);
        txtCrAmt.setFont(Global.menuFont);
        txtOFB.setFont(Global.menuFont);
    }

    private void initTable() {
        tblOpening.setModel(openingTableModel);
        tblOpening.getTableHeader().setFont(Global.tblHeaderFont);
        tblOpening.getTableHeader().setPreferredSize(new Dimension(25, 25));
        tblOpening.setCellSelectionEnabled(true);
        tblOpening.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblOpening.setFont(Global.textFont);
        tblOpening.setShowGrid(true);
        tblOpening.setRowHeight(Global.tblRowHeight);
        tblOpening.setCellSelectionEnabled(true);
        tblOpening.setAutoCreateRowSorter(false);
        openingTableModel.setOpdate(txtDate);
        openingTableModel.setTradeAutoCompleter(tradeAutoCompleter);
        openingTableModel.setParent(tblOpening);
        openingTableModel.setObserver(this);
        openingTableModel.setAccountRepo(accountRepo);
        openingTableModel.setObserver(this);
        openingTableModel.setProgress(progress);
        tblOpening.getColumnModel().getColumn(0).setPreferredWidth(10);//dep
        tblOpening.getColumnModel().getColumn(1).setPreferredWidth(80);//code
        tblOpening.getColumnModel().getColumn(2).setPreferredWidth(200);//name
        tblOpening.getColumnModel().getColumn(3).setPreferredWidth(80);//trader code
        tblOpening.getColumnModel().getColumn(4).setPreferredWidth(200);//trader name
        tblOpening.getColumnModel().getColumn(5).setPreferredWidth(50);//project no
        tblOpening.getColumnModel().getColumn(6).setPreferredWidth(20);//cur
        tblOpening.getColumnModel().getColumn(7).setPreferredWidth(80);//dr
        tblOpening.getColumnModel().getColumn(8).setPreferredWidth(80);//dr
        accountRepo.getDepartment().doOnSuccess((t) -> {
            tblOpening.getColumnModel().getColumn(0).setCellEditor(new DepartmentCellEditor(t));
        }).subscribe();
        tblOpening.getColumnModel().getColumn(1).setCellEditor(new COA3CellEditor(accountRepo, 3));
        tblOpening.getColumnModel().getColumn(2).setCellEditor(new COA3CellEditor(accountRepo, 3));
        tblOpening.getColumnModel().getColumn(3).setCellEditor(new TraderCellEditor(accountRepo));
        tblOpening.getColumnModel().getColumn(4).setCellEditor(new TraderCellEditor(accountRepo));
        tblOpening.getColumnModel().getColumn(5).setCellEditor(new ProjectCellEditor(userRepo));
        userRepo.getCurrency().doOnSuccess((t) -> {
            tblOpening.getColumnModel().getColumn(6).setCellEditor(new CurrencyEditor(t));
        }).subscribe();
        tblOpening.getColumnModel().getColumn(7).setCellEditor(new AutoClearEditor());
        tblOpening.getColumnModel().getColumn(8).setCellEditor(new AutoClearEditor());
        tblOpening.getColumnModel().getColumn(7).setCellRenderer(new DecimalFormatRender());
        tblOpening.getColumnModel().getColumn(8).setCellRenderer(new DecimalFormatRender());
        tblOpening.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblOpening.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
    }

    // allo cell editable for table
    public boolean isCellEditable(int row, int column) {
        return tblOpening.getModel().isCellEditable(row, column);
    }

    private String getCurCode() {
        if (currencyAutoCompleter == null || currencyAutoCompleter.getCurrency() == null) {
            return Global.currency;
        }
        return currencyAutoCompleter.getCurrency().getCurCode();
    }

    private String getDepCode() {
        return departmentAutoCompleter.getDepartment() == null ? "-" : departmentAutoCompleter.getDepartment().getKey().getDeptCode();
    }

    // search openig balance with filtered data for table
    private void searchOpening() {
        progress.setIndeterminate(true);
        ReportFilter filter = new ReportFilter(Global.macId, Global.compCode, Global.deptId);
        filter.setOpeningDate(Util1.toDateStr(txtDate.getDate(), "yyyy-MM-dd"));
        filter.setCurCode(getCurCode());
        filter.setDeptCode(getDepCode());
        ChartOfAccount coa = coaAutoCompleter.getCOA();
        filter.setCoaLv1(Util1.getInteger(coa.getCoaLevel()) == 1 ? coa.getKey().getCoaCode() : "-");
        filter.setCoaLv2(Util1.getInteger(coa.getCoaLevel()) == 2 ? coa.getKey().getCoaCode() : "-");
        filter.setCoaLv3(Util1.getInteger(coa.getCoaLevel()) == 3 ? coa.getKey().getCoaCode() : "-");
        filter.setProjectNo(projectAutoCompleter.getProject().getKey().getProjectNo());
        if (chkCus.isSelected()) {
            filter.setTraderType("C");
        } else if (chkSup.isSelected()) {
            filter.setTraderType("S");
        } else {
            filter.setTraderType("-");
        }
        if (DateLockUtil.isLockDate(txtDate.getDate())) {
            enableForm(false);
            lblMessage.setText(DateLockUtil.MESSAGE);
        } else {
            enableForm(true);
            lblMessage.setText("");
        }
        openingTableModel.clear();
        accountRepo.getOpeningBalance(filter)
                .doOnNext(openingTableModel::addObject)
                .doOnNext(obj -> calTotal())
                .doOnError((e) -> {
                    JOptionPane.showMessageDialog(this, e.getMessage());
                })
                .doOnTerminate(() -> {
                    calOB();
                    openingTableModel.addNewRow();
                    focusOnTable();
                    progress.setIndeterminate(false);
                    ComponentUtil.scrollTable(tblOpening, row, column);
                }).subscribe();
    }

    private void enableForm(boolean status) {
        tblOpening.setEnabled(status);
    }

    private void focusOnTable() {
        int rc = tblOpening.getRowCount();
        if (rc > 1) {
            tblOpening.setRowSelectionInterval(rc - 1, rc - 1);
            tblOpening.setColumnSelectionInterval(0, 0);
            tblOpening.requestFocus();
        }
    }

    // calculate dr-amt and cr-amt and total
    private void calTotal() {
        double drAmt = openingTableModel.getDrAmt();
        double crAmt = openingTableModel.getCrAmt();
        int size = openingTableModel.getSize();
        txtDrAmt.setValue(drAmt);
        txtCrAmt.setValue(crAmt);
        txtRecord.setValue(size);

    }

    private void calDrCr() {
        double drAmt = openingTableModel.getListOpening().stream().mapToDouble((t) -> t.getDrAmt()).sum();
        double crAmt = openingTableModel.getListOpening().stream().mapToDouble((t) -> t.getCrAmt()).sum();
        txtDrAmt.setValue(drAmt);
        txtCrAmt.setValue(crAmt);
        calOB();
    }

    private void calOB() {
        double ob = Util1.getDouble(txtDrAmt.getValue()) - Util1.getDouble(txtCrAmt.getValue());
        txtOFB.setValue(ob);
        txtOFB.setForeground(ob == 0 ? Color.black : Color.red);
    }

    private void printOpening() {
        progress.setIndeterminate(true);
        ReportFilter filter = new ReportFilter(Global.macId, Global.compCode, Global.deptId);
        filter.setReportName("OpeningTri");
        filter.setOpeningDate(Util1.toDateStr(txtDate.getDate(), "yyyy-MM-dd"));
        filter.setDeptCode(departmentAutoCompleter.getDepartment().getKey().getDeptCode());
        filter.setCurCode(currencyAutoCompleter.getCurrency().getCurCode());
        accountRepo.getReport(filter).subscribe((t) -> {
            try {
                Map<String, Object> p = new HashMap();
                p.put("p_report_name", "Opening Tri Balance");
                p.put("p_date", "Opening Date : " + Util1.toDateStr(txtDate.getDate(), "dd/MM/yyyy"));
                p.put("p_print_date", Util1.getTodayDateTime());
                p.put("p_comp_name", Global.companyName);
                p.put("p_comp_address", Global.companyAddress);
                p.put("p_comp_phone", Global.companyPhone);
                p.put("p_currency", currencyAutoCompleter.getCurrency().getCurCode());
                String filePath = String.format(Global.accountRP + "OpeningTri.jasper");
                InputStream input = new ByteArrayInputStream(t.getFile());
                JsonDataSource ds = new JsonDataSource(input);
                JasperPrint js = JasperFillManager.fillReport(filePath, p, ds);
                JasperViewer.viewReport(js, false);
                progress.setIndeterminate(false);
            } catch (JRException e) {
                progress.setIndeterminate(false);
                log.error("printVoucher : " + e.getMessage());
            }
        }, (e) -> {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
            progress.setIndeterminate(false);
        });

    }

    @Override
    public void selected(Object source, Object selectObj) {
        if (selectObj != null) {
            if (source.equals("CAL-TOTAL")) {
                calDrCr();
            } else {
                searchOpening();
            }
        }
    }

    @Override
    public void save() {
    }

    @Override
    public void delete() {
    }

    @Override
    public void newForm() {
    }

    @Override
    public void history() {
    }

    @Override
    public void print() {
        printOpening();
    }

    @Override
    public void refresh() {
        searchOpening();
    }

    @Override
    public void filter() {
        findDialog.setVisible(!findDialog.isVisible());
    }

    @Override
    public String panelName() {
        return this.getName();
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

        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtDept = new javax.swing.JTextField();
        txtCOA = new javax.swing.JTextField();
        chkCus = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        txtCurrency = new javax.swing.JTextField();
        chkSup = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();
        txtDate = new com.toedter.calendar.JDateChooser();
        jLabel6 = new javax.swing.JLabel();
        txtProjectNo = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblOpening = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtCrAmt = new javax.swing.JFormattedTextField();
        jLabel7 = new javax.swing.JLabel();
        txtOFB = new javax.swing.JFormattedTextField();
        txtDrAmt = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        lblMessage = new javax.swing.JLabel();
        txtRecord = new javax.swing.JFormattedTextField();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Date");

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("COA");

        txtDept.setFont(Global.textFont);

        txtCOA.setFont(Global.textFont);

        chkCus.setFont(Global.lableFont);
        chkCus.setText("Customer");
        chkCus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCusActionPerformed(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Currency");

        txtCurrency.setFont(Global.textFont);

        chkSup.setFont(Global.lableFont);
        chkSup.setText("Supplier");
        chkSup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSupActionPerformed(evt);
            }
        });

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Department");

        txtDate.setDateFormatString("dd/MM/yyyy");
        txtDate.setEnabled(false);
        txtDate.setFont(Global.textFont);
        txtDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDateFocusGained(evt);
            }
        });

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Project No");

        txtProjectNo.setFont(Global.textFont);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtDate, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDept, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCurrency, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCOA, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtProjectNo, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkCus)
                .addGap(18, 18, 18)
                .addComponent(chkSup)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtDept, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCOA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5)
                            .addComponent(chkSup)
                            .addComponent(chkCus)
                            .addComponent(jLabel6)
                            .addComponent(txtProjectNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 1, Short.MAX_VALUE)))
                .addContainerGap())
        );

        tblOpening.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblOpening.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tblOpening.setGridColor(new java.awt.Color(204, 204, 204));
        tblOpening.setName("tblOpening"); // NOI18N
        tblOpening.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblOpeningMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblOpening);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel8.setFont(Global.menuFont);
        jLabel8.setText("Credit Total :");

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Records");

        txtCrAmt.setEditable(false);
        txtCrAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtCrAmt.setFont(Global.amtFont);

        jLabel7.setFont(Global.menuFont);
        jLabel7.setText("Debit Total :");

        txtOFB.setEditable(false);
        txtOFB.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtOFB.setFont(Global.amtFont);

        txtDrAmt.setEditable(false);
        txtDrAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDrAmt.setFont(Global.amtFont);

        jLabel9.setFont(Global.menuFont);
        jLabel9.setText("Out Of Balance :");

        lblMessage.setFont(Global.lableFont);
        lblMessage.setForeground(Color.red);
        lblMessage.setText(".");

        txtRecord.setEditable(false);
        txtRecord.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtRecord.setFont(Global.amtFont);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtRecord, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDrAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblMessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtCrAmt, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(txtOFB))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(txtDrAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCrAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtRecord, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtOFB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblMessage))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void chkCusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCusActionPerformed
        // TODO add your handling code here:
        searchOpening();
    }//GEN-LAST:event_chkCusActionPerformed

    private void chkSupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSupActionPerformed
        // TODO add your handling code here:
        searchOpening();
    }//GEN-LAST:event_chkSupActionPerformed

    private void txtDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDateFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDateFocusGained

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observeMain();
    }//GEN-LAST:event_formComponentShown

    private void tblOpeningMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblOpeningMouseClicked
        // TODO add your handling code here:
        row = tblOpening.getSelectedRow();
        column = tblOpening.getSelectedColumn();
    }//GEN-LAST:event_tblOpeningMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkCus;
    private javax.swing.JCheckBox chkSup;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblMessage;
    private javax.swing.JTable tblOpening;
    private javax.swing.JTextField txtCOA;
    private javax.swing.JFormattedTextField txtCrAmt;
    private javax.swing.JTextField txtCurrency;
    private com.toedter.calendar.JDateChooser txtDate;
    private javax.swing.JTextField txtDept;
    private javax.swing.JFormattedTextField txtDrAmt;
    private javax.swing.JFormattedTextField txtOFB;
    private javax.swing.JTextField txtProjectNo;
    private javax.swing.JFormattedTextField txtRecord;
    // End of variables declaration//GEN-END:variables

}
