/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.entry;

import com.repo.AccountRepo;
import com.acc.editor.COA3CellEditor;
import com.user.editor.CurrencyAutoCompleter;
import com.acc.editor.DepartmentAutoCompleter;
import com.acc.editor.DepartmentCellEditor;
import com.acc.editor.DespAutoCompleter;
import com.acc.editor.DespEditor;
import com.acc.editor.RefAutoCompleter;
import com.acc.editor.RefCellEditor;
import com.acc.editor.TraderAAutoCompleter;
import com.acc.editor.TraderCellEditor;
import com.acc.editor.TranSourceAutoCompleter;
import com.acc.common.AllCashTableModel;
import com.acc.common.ColumnHeaderListener;
import com.acc.editor.DateAutoCompleter;
import com.acc.common.DateTableDecorator;
import com.acc.common.DayBookTableModel;
import com.acc.dialog.FindDialog;
import com.acc.dialog.ReportModeDialog;
import com.acc.editor.BatchCellEditor;
import com.acc.editor.BatchNoAutoCompeter;
import com.acc.editor.COA3AutoCompleter;
import com.acc.model.ChartOfAccount;
import com.acc.model.DateModel;
import com.acc.model.DeleteObj;
import com.acc.model.Gl;
import com.common.ComponentUtil;
import com.common.DateLockUtil;
import com.common.DecimalFormatRender;
import com.common.Global;
import com.common.IconUtil;
import com.common.PanelControl;
import com.common.ProUtil;
import com.common.ReportFilter;
import com.common.RowHeader;
import com.common.SelectionObserver;
import com.common.Util1;
import com.user.editor.AutoClearEditor;
import com.repo.UserRepo;
import com.user.editor.CurrencyEditor;
import com.user.editor.ProjectAutoCompleter;
import com.user.editor.ProjectCellEditor;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.view.JasperViewer;
import org.springframework.core.task.TaskExecutor;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class AllCash extends javax.swing.JPanel implements SelectionObserver, PanelControl {

    @Setter
    private TaskExecutor taskExecutor;
    @Setter
    private SelectionObserver observer;
    @Setter
    private String sourceAccId;
    @Setter
    private JProgressBar progress;
    @Setter
    private AccountRepo accountRepo;
    @Setter
    private UserRepo userRepo;
    private DateAutoCompleter dateAutoCompleter;
    private TraderAAutoCompleter traderAutoCompleter;
    private DepartmentAutoCompleter departmentAutoCompleter;
    private COA3AutoCompleter coaAutoCompleter;
    private CurrencyAutoCompleter currencyAutoCompleter;
    private DespAutoCompleter despAutoCompleter;
    private RefAutoCompleter refAutoCompleter;
    private TranSourceAutoCompleter tranSourceAutoCompleter;
    private BatchNoAutoCompeter batchNoAutoCompeter;
    private ProjectAutoCompleter projectAutoCompleter;
    private final JPopupMenu popupmenu = new JPopupMenu();
    private final JLabel lblMessage = new JLabel();
    private DateTableDecorator decorator;
    private final boolean single;
    private final AllCashTableModel allCashTableModel = new AllCashTableModel();
    private final DayBookTableModel dayBookTableModel = new DayBookTableModel();
    private ColumnHeaderListener listener;
    private int selectRow = -1;
    private int selectCol = 0;
    private ReportModeDialog reportModeDialog;
    private FindDialog findDialog;

    /**
     * Creates new form CashBook
     *
     * @param single
     */
    public AllCash(boolean single) {
        this.single = single;
        initComponents();
        initProperty();
        initPopup();
        initDateDecorator();
        actionMapping();
    }

    private void initRowHeader() {
        RowHeader header = new RowHeader();
        JList list = header.createRowHeader(tblCash, 40);
        scroll.setRowHeaderView(list);
    }

    private void initProperty() {
        ComponentUtil.addFocusListener(this);
        ComponentUtil.setTextProperty(this);
        txtDr.setForeground(Global.GREEN);
        txtCr.setForeground(Color.RED);
        txtDr.setFont(Global.menuFont);
        txtCr.setFont(Global.menuFont);
        txtOpening.setFont(Global.menuFont);
        txtClosing.setFont(Global.menuFont);

    }

    private void tableListener(boolean status) {
        if (status) {
            listener = new ColumnHeaderListener(tblCash);
            tblCash.addMouseListener(listener);
        } else {
            tblCash.removeMouseListener(listener);
        }
    }

    private void batchLock(boolean lock) {
        tblCash.setEnabled(lock);
        observer.selected("save", lock);
        observer.selected("delete", lock);
    }

    private void initTableModel() {
        if (single) {
            tblCash.setModel(dayBookTableModel);
            dayBookTableModel.setParent(tblCash);
            dayBookTableModel.setAccountRepo(accountRepo);
            dayBookTableModel.setObserver(this);
            dayBookTableModel.setSourceAccId(sourceAccId);
            dayBookTableModel.setProgress(progress);
            accountRepo.findCOA(sourceAccId).doOnSuccess((coa) -> {
                if (coa == null) {
                    JOptionPane.showMessageDialog(this, "mapping coa does not exists.");
                    ComponentUtil.enableForm(this, false);
                    return;
                }
                dayBookTableModel.setCredit(coa.isCredit());
                accountRepo.getDefaultDepartment().doOnSuccess((t) -> {
                    dayBookTableModel.setDepartment(t);
                }).subscribe();
                userRepo.findCurrency(Util1.isNull(coa.getCurCode(), Global.currency)).doOnSuccess((c) -> {
                    currencyAutoCompleter.setCurrency(c);
                }).subscribe();

            }).subscribe();
        } else {
            tblCash.setModel(allCashTableModel);
            accountRepo.findCOA(sourceAccId).doOnSuccess((coa) -> {
                if (coa == null) {
                    JOptionPane.showMessageDialog(this, "mapping coa does not exists.");
                    ComponentUtil.enableForm(this, false);
                    return;
                }
                allCashTableModel.setParent(tblCash);
                allCashTableModel.setAccountRepo(accountRepo);
                allCashTableModel.setObserver(this);
                allCashTableModel.setSourceAccId(sourceAccId);
                allCashTableModel.setProgress(progress);
                userRepo.findCurrency(Util1.isNull(coa.getCurCode(), Global.currency)).doOnSuccess((c) -> {
                    currencyAutoCompleter.setCurrency(c);
                }).subscribe();
            }).subscribe();

        }
    }

    private void initDateDecorator() {
        decorator = DateTableDecorator.decorate(panelDate);
        decorator.setObserver(this);
    }

    private void createDateFilter() {
        HashMap<Integer, String> hmDate = new HashMap<>();
        HashMap<String, Integer> hmPage = new HashMap<>();
        List<LocalDate> date = Util1.getDaysBetweenDates(Util1.parseLocalDate(Global.startDate, "dd/MM/yyyy"), LocalDate.now());
        for (int i = 0; i < date.size(); i++) {
            String str = Util1.toDateStr(date.get(i), "yyyy-MM-dd");
            int z = i + 1;
            hmDate.put(z, str);
            hmPage.put(str, z);
        }
        decorator.setHmPage(hmPage);
        decorator.setHmData(hmDate);
        decorator.refreshButton(dateAutoCompleter.getDateModel().getEndDate());
    }

    private void initFilter() {
        departmentAutoCompleter = new DepartmentAutoCompleter(txtDepartment, null, true, true);
        departmentAutoCompleter.setObserver(this);
        accountRepo.getDepartment().doOnSuccess((t) -> {
            departmentAutoCompleter.setListDepartment(t);
        }).subscribe();
        currencyAutoCompleter = new CurrencyAutoCompleter(txtCurrency, null);
        currencyAutoCompleter.setObserver(this);
        userRepo.getCurrency().doOnSuccess((t) -> {
            currencyAutoCompleter.setListCurrency(t);
        }).subscribe();
        userRepo.getDefaultCurrency().doOnSuccess((c) -> {
            currencyAutoCompleter.setCurrency(c);
        }).subscribe();
        tranSourceAutoCompleter = new TranSourceAutoCompleter(txtOption, null, true);
        tranSourceAutoCompleter.setObserver(this);
        accountRepo.getTranSource().doOnSuccess((t) -> {
            tranSourceAutoCompleter.setListGl(t);
        }).subscribe();
        dateAutoCompleter = new DateAutoCompleter(txtDate);
        dateAutoCompleter.setObserver(this);
        despAutoCompleter = new DespAutoCompleter(txtDesp, accountRepo, null, true);
        despAutoCompleter.setObserver(this);
        refAutoCompleter = new RefAutoCompleter(txtRefrence, accountRepo, null, true);
        refAutoCompleter.setObserver(this);
        coaAutoCompleter = new COA3AutoCompleter(txtAccount, accountRepo, null, true, 0);
        coaAutoCompleter.setObserver(this);
        traderAutoCompleter = new TraderAAutoCompleter(txtPerson, accountRepo, null, true);
        traderAutoCompleter.setObserver(this);
        batchNoAutoCompeter = new BatchNoAutoCompeter(txtBatchNo, accountRepo, null, true);
        batchNoAutoCompeter.setObserver(this);
        projectAutoCompleter = new ProjectAutoCompleter(txtProjectNo, null, true);
        projectAutoCompleter.setObserver(this);
        userRepo.searchProject().doOnSuccess((t) -> {
            projectAutoCompleter.setListProject(t);
        }).subscribe();

    }

    public void initMain() {
        batchLock(!Global.batchLock);
        txtCurrency.setEnabled(ProUtil.isMultiCur());
        initFilter();
        initTableModel();
        initFindDialog();
        initTableCB();
        initRowHeader();
        createDateFilter();
        searchCash();
    }

    private void requestFoucsTable() {
        ComponentUtil.scrollTable(tblCash, selectRow, selectCol);
    }

    private void initTableCB() {
        tblCash.setDefaultRenderer(Object.class, new DecimalFormatRender());
        tblCash.setDefaultRenderer(Double.class, new DecimalFormatRender());
        tblCash.getTableHeader().setFont(Global.tblHeaderFont);
        tblCash.setCellSelectionEnabled(true);
        tblCash.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCash.getColumnModel().getColumn(0).setPreferredWidth(20);// Date
        tblCash.getColumnModel().getColumn(1).setPreferredWidth(13);// Department
        tblCash.getColumnModel().getColumn(2).setPreferredWidth(180);// Description      
        tblCash.getColumnModel().getColumn(3).setPreferredWidth(180);// Ref  
        tblCash.getColumnModel().getColumn(4).setPreferredWidth(90);// Ref  
        tblCash.getColumnModel().getColumn(5).setPreferredWidth(50);// batch  
        tblCash.getColumnModel().getColumn(6).setPreferredWidth(50);// project  
        tblCash.getColumnModel().getColumn(7).setPreferredWidth(90);// Person
        tblCash.getColumnModel().getColumn(8).setPreferredWidth(150);// Account
        tblCash.getColumnModel().getColumn(9).setPreferredWidth(1);// Curr      
        tblCash.getColumnModel().getColumn(10).setPreferredWidth(60);// Dr-Amt   
        tblCash.getColumnModel().getColumn(0).setCellEditor(new AutoClearEditor());
        accountRepo.getDepartment().doOnSuccess((t) -> {
            tblCash.getColumnModel().getColumn(1).setCellEditor(new DepartmentCellEditor(t));
        }).subscribe();
        tblCash.getColumnModel().getColumn(2).setCellEditor(new DespEditor(accountRepo));
        tblCash.getColumnModel().getColumn(3).setCellEditor(new RefCellEditor(accountRepo));
        tblCash.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());
        tblCash.getColumnModel().getColumn(5).setCellEditor(new BatchCellEditor(accountRepo));
        userRepo.searchProject().doOnSuccess((t) -> {
            tblCash.getColumnModel().getColumn(6).setCellEditor(new ProjectCellEditor(t));
        }).subscribe();
        tblCash.getColumnModel().getColumn(7).setCellEditor(new TraderCellEditor(accountRepo));
        tblCash.getColumnModel().getColumn(8).setCellEditor(new COA3CellEditor(accountRepo, 3));
        userRepo.getCurrency().doOnSuccess((t) -> {
            tblCash.getColumnModel().getColumn(9).setCellEditor(new CurrencyEditor(t));
        }).subscribe();
        tblCash.getColumnModel().getColumn(10).setCellEditor(new AutoClearEditor());
        if (!single) {
            tblCash.getColumnModel().getColumn(11).setPreferredWidth(60);// Cr-Amt  
            tblCash.getColumnModel().getColumn(11).setCellEditor(new AutoClearEditor());
        }
        tblCash.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
    }

    private void setGl(Gl gl) {
        if (gl.isTranLock()) {
            lblInfo.setText(DateLockUtil.MESSAGE);
        } else {
            lblInfo.setText("");
        }
    }

    private void actionMapping() {
        tblCash.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");
        tblCash.getActionMap().put("delete", actionDelete);
        tblCash.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, InputEvent.SHIFT_DOWN_MASK), "force-delete");
        tblCash.getActionMap().put("force-delete", forceDelete);
        tblCash.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK), "copy-row");
        tblCash.getActionMap().put("copy-row", copyRow);
    }

    private void initPopup() {
        lblMessage.setFont(Global.textFont);
        lblMessage.setHorizontalAlignment(JLabel.CENTER);
        popupmenu.setBorder(BorderFactory.createLineBorder(Color.black));
        popupmenu.setFocusable(false);
        popupmenu.add(lblMessage);
    }

    public boolean isCellEditable(int row, int column) {
        return tblCash.getModel().isCellEditable(row, column);
    }

    private final Action actionDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            deleteVoucher(false);
        }
    };
    private final Action forceDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            deleteVoucher(true);
        }
    };
    private final Action copyRow = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int row = tblCash.convertRowIndexToModel(tblCash.getSelectedRow());
            int column = tblCash.getSelectedColumn();
            if (row > 0) {
                Gl copy = getGl(row - 1);
                Gl cur = getGl(row);
                switch (column) {
                    case 0 -> {
                        cur.setGlDate(copy.getGlDate());
                    }
                    case 1 -> {
                        cur.setDeptCode(copy.getDeptCode());
                        cur.setDeptUsrCode(copy.getDeptUsrCode());
                    }
                    case 2 -> {
                        cur.setDescription(copy.getDescription());
                    }
                    case 3 -> {
                        cur.setReference(copy.getReference());
                    }
                    case 4 -> {
                        cur.setRefNo(copy.getRefNo());
                    }
                    //batch no
                    case 5 -> {
                        cur.setBatchNo(copy.getBatchNo());
                    }
                    //project
                    case 6 -> {
                        cur.setProjectNo(copy.getProjectNo());
                    }
                    case 7 -> {
                        cur.setTraderCode(copy.getTraderCode());
                        cur.setTraderName(copy.getTraderName());
                        cur.setAccCode(copy.getAccCode());
                        cur.setAccName(copy.getAccName());
                    }
                    case 8 -> {
                        cur.setAccCode(copy.getAccCode());
                        cur.setAccName(copy.getAccName());
                    }
                    case 9 -> {
                        cur.setCurCode(copy.getCurCode());
                    }
                    case 10 -> {
                        cur.setDrAmt(copy.getDrAmt());
                    }
                    case 11 -> {
                        cur.setCrAmt(copy.getCrAmt());
                    }
                }
                allCashTableModel.setVGl(row, cur);
                tblCash.setColumnSelectionInterval(column + 1, column + 1);
                tblCash.setRowSelectionInterval(row, row);
                tblCash.requestFocus();
            }

        }
    };

    private void closeCellEditor() {
        if (tblCash.getCellEditor() != null) {
            tblCash.getCellEditor().stopCellEditing();
        }
    }

    private Gl getGl(int row) {
        if (single) {
            return dayBookTableModel.getVGl(row);
        } else {
            return allCashTableModel.getVGl(row);
        }
    }

    private void deleteVoucher(boolean force) {
        closeCellEditor();
        int row = tblCash.convertRowIndexToModel(tblCash.getSelectedRow());
        int yes_no;
        if (row >= 0) {
            Gl vgl = getGl(row);
            if (vgl.getTranSource().equals("Report")) {
                return;
            }
            if (!force) {
                if (vgl.isTranLock()) {
                    DateLockUtil.showMessage(this);
                    return;
                }
            }
            String glCode = vgl.getKey().getGlCode();
            if (glCode != null) {
                DeleteObj obj = new DeleteObj();
                obj.setGlCode(glCode);
                obj.setCompCode(vgl.getKey().getCompCode());
                obj.setDeptId(vgl.getKey().getDeptId());
                obj.setModifyBy(Global.loginUser.getUserCode());
                yes_no = JOptionPane.showConfirmDialog(Global.parentForm, "Are you sure to delete?",
                        "Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (yes_no == 0) {
                    accountRepo.delete(obj).doOnSuccess((t) -> {
                        deleteTable(row);
                        calculateClosing();
                    }).subscribe();

                }
            }
        }
    }

    private void deleteTable(int row) {
        if (single) {
            dayBookTableModel.deleteVGl(row);
        } else {
            allCashTableModel.deleteVGl(row);
        }
    }

    public void printVoucher() {
        String currency = getCurCode();
        String stDate = dateAutoCompleter.getDateModel().getStartDate();
        String endDate = dateAutoCompleter.getDateModel().getEndDate();
        if (!currency.equals("-") || !ProUtil.isMultiCur()) {
            enableToolBar(false);
            taskExecutor.execute(() -> {
                try {
                    String path = "temp/Ledger" + Global.macId + ".json";
                    List<Gl> list = getListDetail();
                    Util1.writeJsonFile(list, path);
                    Map<String, Object> p = new HashMap();
                    p.put("p_report_name", this.getName());
                    p.put("p_date", String.format("Between %s and %s",
                            Util1.toDateStr(stDate, "yyyy-MM-dd", "dd/MM/yyyy"),
                            Util1.toDateStr(endDate, "yyyy-MM-dd", "dd/MM/yyyy")));
                    p.put("p_print_date", Util1.getTodayDateTime());
                    p.put("p_comp_name", Global.companyName);
                    p.put("p_comp_address", Global.companyAddress);
                    p.put("p_comp_phone", Global.companyPhone);
                    p.put("p_currency", currency);
                    p.put("p_logo_path", ProUtil.logoPath());
                    double op = Util1.getDouble(txtOpening.getValue());
                    double closing = Util1.getDouble(txtClosing.getValue());
                    p.put("p_opening", op);
                    p.put("p_closing", closing);
                    String filePath = String.format(Global.accountRP + getReportName());
                    InputStream input = new FileInputStream(path);
                    JsonDataSource ds = new JsonDataSource(input);
                    JasperPrint js = JasperFillManager.fillReport(filePath, p, ds);
                    JasperViewer.viewReport(js, false);
                    enableToolBar(true);
                } catch (JRException ex) {
                    enableToolBar(true);
                    JOptionPane.showMessageDialog(Global.parentForm, ex.getMessage());
                    log.error("printVoucher : " + ex.getMessage());
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(AllCash.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        } else {
            JOptionPane.showMessageDialog(this, "Select Currency.");
        }
    }

    private String getReportName() {
        String reportName = "IndividualLedger.jasper";
        if (reportModeDialog != null) {
            String name = reportModeDialog.getSelectName();
            switch (name) {
                case "Detail" ->
                    reportName = "IndividualLedger.jasper";
                case "Summary" ->
                    reportName = "IndividualLedgerSummary.jasper";
            }
        }
        return reportName;
    }

    private String getCurCode() {
        if (currencyAutoCompleter == null || currencyAutoCompleter.getCurrency() == null) {
            return Global.currency;
        }
        return currencyAutoCompleter.getCurrency().getCurCode();
    }

    private List<String> getListDep() {
        return departmentAutoCompleter.getDepartment() == null ? new ArrayList<>() : departmentAutoCompleter.getListOption();
    }

    private String getTranSource() {
        if (tranSourceAutoCompleter == null || tranSourceAutoCompleter.getAutoText() == null) {
            return "-";
        }
        return tranSourceAutoCompleter.getAutoText().getTranSource().equals("All") ? "-"
                : tranSourceAutoCompleter.getAutoText().getTranSource();
    }

    private void searchCash() {
        enableToolBar(false);
        clearModel();
        ReportFilter filter = getFilter();
        accountRepo.searchGlFlux(filter).doOnNext((gl) -> {
            addObject(gl);
        }).doOnNext((t) -> {
            calDrCrStream();
        }).doOnError((e) -> {
            log.error("searchCash: " + e.getMessage());
            enableToolBar(true);
        }).doOnTerminate(() -> {
            decorator.refreshButton(filter.getFromDate());
            addNewRow(filter.getFromDate());
            enableToolBar(true);
            requestFoucsTable();
            accountRepo.getOpening(getOPFilter()).doOnSuccess((t) -> {
                txtOpening.setValue(t == null ? 0 : t.getOpening());
            }).doOnTerminate(() -> {
                calculateClosing();
            }).subscribe();
        }).subscribe();
    }

    private void calDrCrStream() {
        double drAmt;
        double crAmt;
        int size;
        if (single) {
            drAmt = dayBookTableModel.getDrAmt();
            crAmt = dayBookTableModel.getCrAmt();
            size = dayBookTableModel.getSize();
        } else {
            drAmt = allCashTableModel.getDrAmt();
            crAmt = allCashTableModel.getCrAmt();
            size = allCashTableModel.getSize();
        }
        txtDr.setValue(drAmt);
        txtCr.setValue(crAmt);
        lblRecord.setValue(size);
    }

    private void calDrCr() {
        List<Gl> list = getListDetail();
        double drAmt = list.stream().mapToDouble((t) -> t.getDrAmt()).sum();
        double crAmt = list.stream().mapToDouble((t) -> t.getCrAmt()).sum();
        txtDr.setValue(drAmt);
        txtCr.setValue(crAmt);
    }

    private List<Gl> getListDetail() {
        return single ? dayBookTableModel.getListVGl() : allCashTableModel.getListVGl();
    }

    private void enableToolBar(boolean status) {
        progress.setIndeterminate(!status);
        observer.selected("refresh", status);
        observer.selected("print", status);
        observer.selected("save", false);
        observer.selected("history", false);
        ComponentUtil.setComponentHierarchyEnabled(panelDate, status);
        ComponentUtil.setComponentHierarchyEnabled(panelOption, status);
    }

    private String getProjectNo() {
        return projectAutoCompleter == null ? "-" : projectAutoCompleter.getProject().getKey().getProjectNo();
    }

    private ReportFilter getFilter() {
        ReportFilter filter = new ReportFilter(Global.macId, Global.compCode, Global.deptId);
        filter.setFromDate(dateAutoCompleter.getDateModel().getStartDate());
        filter.setToDate(dateAutoCompleter.getDateModel().getEndDate());
        filter.setDesp(despAutoCompleter.getAutoText().getDescription().equals("All") ? "-" : despAutoCompleter.getAutoText().getDescription());
        filter.setSrcAcc(sourceAccId);
        filter.setReference(refAutoCompleter.getAutoText().getDescription().equals("All") ? "-"
                : refAutoCompleter.getAutoText().getDescription());
        filter.setBatchNo(batchNoAutoCompeter.getAutoText().getDescription().equals("All") ? "-"
                : batchNoAutoCompeter.getAutoText().getDescription());
        filter.setProjectNo(getProjectNo());
        filter.setCurCode(getCurCode());
        filter.setListDepartment(getListDep());
        filter.setTranSource(getTranSource());
        filter.setTraderCode(traderAutoCompleter.getTrader().getKey().getCode());
        ChartOfAccount coa = coaAutoCompleter.getCOA();
        String coaLv1 = Util1.getInteger(coa.getCoaLevel()) == 1 ? coa.getKey().getCoaCode() : "-";
        String coaLv2 = Util1.getInteger(coa.getCoaLevel()) == 2 ? coa.getKey().getCoaCode() : "-";
        String accCode = Util1.getInteger(coa.getCoaLevel()) == 3 ? coa.getKey().getCoaCode() : "-";
        filter.setCoaLv1(coaLv1);
        filter.setCoaLv2(coaLv2);
        filter.setAcc(accCode);
        filter.setMode(getMode());
        log.info("start date : " + filter.getFromDate());
        log.info("end date : " + filter.getToDate());
        return filter;
    }

    private String getMode() {
        String mode = "Detail";
        if (reportModeDialog != null) {
            return reportModeDialog.getSelectName();
        }
        return mode;
    }

    private ReportFilter getOPFilter() {
        String clDate = dateAutoCompleter.getDateModel().getStartDate();
        String endDate = dateAutoCompleter.getDateModel().getEndDate();
        ReportFilter filter = new ReportFilter(Global.macId, Global.compCode, Global.deptId);
        filter.setFromDate(clDate);
        filter.setToDate(endDate);
        filter.setCurCode(getCurCode());
        filter.setListDepartment(getListDep());
        filter.setTraderCode(traderAutoCompleter.getTrader().getKey().getCode());
        filter.setCoaCode(sourceAccId);
        return filter;
    }

    private void calculateClosing() {
        double opening = Util1.getDouble(txtOpening.getValue());
        double drAmt = Util1.getDouble(txtDr.getValue());
        double crAmt = Util1.getDouble(txtCr.getValue());
        double closing = opening + drAmt - crAmt;
        closing = closing == -0 ? 0 : closing;
        txtOpening.setForeground(opening >= 0 ? Global.GREEN : Color.RED);
        txtClosing.setForeground(closing >= 0 ? Global.GREEN : Color.RED);
        txtClosing.setValue(closing);
        txtDr.setValue(drAmt);
        txtCr.setValue(crAmt);

    }

    private void clearModel() {
        if (single) {
            dayBookTableModel.clear();
        } else {
            allCashTableModel.clear();
        }
        lblRecord.setValue(0);
        txtOpening.setValue(0);
        txtClosing.setValue(0);
        txtDr.setValue(0);
        txtCr.setValue(0);
    }

    private void addNewRow(String fromDate) {
        if (single) {
            dayBookTableModel.setGlDate(fromDate);
            dayBookTableModel.setCurCode(getCurCode());
            dayBookTableModel.addNewRow();
        } else {
            allCashTableModel.setGlDate(fromDate);
            allCashTableModel.setCurCode(getCurCode());
            allCashTableModel.addNewRow();
        }
    }

    private void addObject(Gl gl) {
        if (DateLockUtil.isLockDate(gl.getGlDate())) {
            gl.setTranLock(true);
        } else if (!Util1.isNull(gl.getTranSource(), "CB").equals("CB")) {
            gl.setTranLock(true);
        }
        if (single) {
            dayBookTableModel.addObject(gl);
        } else {
            allCashTableModel.addObject(gl);
        }
    }

    private void checkLock() {
        selectRow = tblCash.convertRowIndexToModel(tblCash.getSelectedRow());
        selectCol = tblCash.getSelectedColumn();
        if (selectRow >= 0) {
            Gl gl = getGl(selectRow);
            setGl(gl);
        }
    }

    private void reportModeDialog() {
        if (reportModeDialog == null) {
            reportModeDialog = new ReportModeDialog(Global.parentForm);
            reportModeDialog.setLocationRelativeTo(null);
        }
        reportModeDialog.setVisible(true);
        btnMode.setText(reportModeDialog.getSelectName());
        searchCash();
    }

    private void initFindDialog() {
        findDialog = new FindDialog(Global.parentForm, tblCash);
    }

    private void defaultFilter() {
        dateAutoCompleter.setTodayDate();
        traderAutoCompleter.clear();
        coaAutoCompleter.clear();
        despAutoCompleter.clear();
        refAutoCompleter.clear();
        batchNoAutoCompeter.clear();
        projectAutoCompleter.clear();
        tranSourceAutoCompleter.clear();
        accountRepo.getDefaultDepartment().doOnSuccess((t) -> {
            if (t != null) {
                departmentAutoCompleter.setDepartment(t);
            } else {
                departmentAutoCompleter.clear();
            }
        });
        tblCash.setRowSorter(null);
        selectRow = -1;
        selectCol = 1;
    }

    private void observeMain() {
        observer.selected("control", this);
        observer.selected("save", false);
        observer.selected("print", true);
        observer.selected("history", false);
        observer.selected("delete", true);
        observer.selected("refresh", true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuItem1 = new javax.swing.JMenuItem();
        scroll = new javax.swing.JScrollPane();
        tblCash = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtDepartment = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtPerson = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtAccount = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtDesp = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtRefrence = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtCurrency = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtOption = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtBatchNo = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtProjectNo = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtDate = new javax.swing.JTextField();
        panelDate = new javax.swing.JPanel();
        panelOption = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        lblRecord = new javax.swing.JFormattedTextField();
        chkAdjust = new javax.swing.JCheckBox();
        lblInfo = new javax.swing.JLabel();
        btnMode = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        txtCr = new javax.swing.JFormattedTextField();
        jLabel13 = new javax.swing.JLabel();
        txtDr = new javax.swing.JFormattedTextField();
        jPanel3 = new javax.swing.JPanel();
        txtClosing = new javax.swing.JFormattedTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txtOpening = new javax.swing.JFormattedTextField();

        jMenuItem1.setText("jMenuItem1");

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        tblCash.setFont(Global.textFont);
        tblCash.setToolTipText("");
        tblCash.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tblCash.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tblCash.setGridColor(new java.awt.Color(204, 204, 204));
        tblCash.setRowHeight(Global.tblRowHeight);
        tblCash.setShowHorizontalLines(true);
        tblCash.setShowVerticalLines(true);
        tblCash.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCashMouseClicked(evt);
            }
        });
        scroll.setViewportView(tblCash);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Date");

        txtDepartment.setFont(Global.textFont);
        txtDepartment.setName("txtDepartment"); // NOI18N
        txtDepartment.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDepartmentFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDepartmentFocusLost(evt);
            }
        });
        txtDepartment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDepartmentActionPerformed(evt);
            }
        });

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Department");

        txtPerson.setFont(Global.textFont);
        txtPerson.setName("txtPerson"); // NOI18N
        txtPerson.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPersonFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPersonFocusLost(evt);
            }
        });
        txtPerson.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPersonActionPerformed(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Trader");

        txtAccount.setFont(Global.textFont);
        txtAccount.setName("txtAccount"); // NOI18N
        txtAccount.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtAccountFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtAccountFocusLost(evt);
            }
        });
        txtAccount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAccountActionPerformed(evt);
            }
        });

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Account Group");

        txtDesp.setFont(Global.textFont);
        txtDesp.setName("txtDesp"); // NOI18N
        txtDesp.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDespFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDespFocusLost(evt);
            }
        });
        txtDesp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDespActionPerformed(evt);
            }
        });

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Description");

        txtRefrence.setFont(Global.textFont);
        txtRefrence.setName("txtRefrence"); // NOI18N
        txtRefrence.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtRefrenceFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtRefrenceFocusLost(evt);
            }
        });
        txtRefrence.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRefrenceActionPerformed(evt);
            }
        });

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Refrence");

        txtCurrency.setFont(Global.textFont);
        txtCurrency.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCurrency.setName("txtCurrency"); // NOI18N
        txtCurrency.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCurrencyFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtCurrencyFocusLost(evt);
            }
        });
        txtCurrency.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCurrencyActionPerformed(evt);
            }
        });

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Currency");

        txtOption.setFont(Global.textFont);
        txtOption.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtOption.setName("txtCurrency"); // NOI18N
        txtOption.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtOptionFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtOptionFocusLost(evt);
            }
        });
        txtOption.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtOptionActionPerformed(evt);
            }
        });

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Option");

        txtBatchNo.setFont(Global.textFont);
        txtBatchNo.setName("txtRefrence"); // NOI18N
        txtBatchNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtBatchNoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtBatchNoFocusLost(evt);
            }
        });
        txtBatchNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBatchNoActionPerformed(evt);
            }
        });

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Batch No");

        txtProjectNo.setFont(Global.textFont);
        txtProjectNo.setName("txtRefrence"); // NOI18N
        txtProjectNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtProjectNoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtProjectNoFocusLost(evt);
            }
        });
        txtProjectNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtProjectNoActionPerformed(evt);
            }
        });

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Project No");

        txtDate.setFont(Global.textFont);
        txtDate.setName("txtDepartment"); // NOI18N
        txtDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDateFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDateFocusLost(evt);
            }
        });
        txtDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtDate, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtDepartment, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPerson, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtAccount)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDesp, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtRefrence, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtBatchNo, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtProjectNo, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtCurrency, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtOption, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel3)
                            .addComponent(jLabel10)
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtDepartment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDesp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtRefrence, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPerson, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtAccount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtBatchNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtProjectNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtOption, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelDate.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        javax.swing.GroupLayout panelDateLayout = new javax.swing.GroupLayout(panelDate);
        panelDate.setLayout(panelDateLayout);
        panelDateLayout.setHorizontalGroup(
            panelDateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelDateLayout.setVerticalGroup(
            panelDateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 33, Short.MAX_VALUE)
        );

        panelOption.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel9.setText("Records : ");

        lblRecord.setEditable(false);
        lblRecord.setBorder(null);
        lblRecord.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        lblRecord.setHorizontalAlignment(javax.swing.JTextField.LEFT);

        chkAdjust.setText("Adjust Column");
        chkAdjust.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkAdjustActionPerformed(evt);
            }
        });

        lblInfo.setFont(Global.lableFont);
        lblInfo.setForeground(Color.RED);
        lblInfo.setText("-");

        btnMode.setBackground(Global.selectionColor);
        btnMode.setFont(Global.lableFont);
        btnMode.setForeground(new java.awt.Color(255, 255, 255));
        btnMode.setIcon(IconUtil.getIcon(IconUtil.LIST_ICON)
        );
        btnMode.setText("Mode");
        btnMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelOptionLayout = new javax.swing.GroupLayout(panelOption);
        panelOption.setLayout(panelOptionLayout);
        panelOptionLayout.setHorizontalGroup(
            panelOptionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOptionLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnMode)
                .addGap(38, 38, 38)
                .addComponent(chkAdjust)
                .addGap(114, 114, 114)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblRecord, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 18, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelOptionLayout.setVerticalGroup(
            panelOptionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOptionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelOptionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(lblRecord, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkAdjust)
                    .addComponent(lblInfo)
                    .addComponent(btnMode))
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel12.setFont(Global.menuFont);
        jLabel12.setText("Total Cash Out / Credit");

        txtCr.setEditable(false);
        txtCr.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel13.setFont(Global.menuFont);
        jLabel13.setText("Total Cash In / Debit");

        txtDr.setEditable(false);
        txtDr.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtDr, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                    .addComponent(txtCr))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDr))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCr)))
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        txtClosing.setEditable(false);
        txtClosing.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel15.setFont(Global.menuFont);
        jLabel15.setText("Closing");

        jLabel14.setFont(Global.menuFont);
        jLabel14.setText("Opening");

        txtOpening.setEditable(false);
        txtOpening.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtOpening, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtClosing, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtOpening))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtClosing)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(panelDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(panelOption, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(scroll)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scroll, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(panelDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelOption, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observeMain();
    }//GEN-LAST:event_formComponentShown

    private void txtDepartmentFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDepartmentFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDepartmentFocusGained

    private void txtDepartmentFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDepartmentFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDepartmentFocusLost

    private void txtDepartmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDepartmentActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDepartmentActionPerformed

    private void txtPersonFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPersonFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPersonFocusGained

    private void txtPersonFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPersonFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPersonFocusLost

    private void txtPersonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPersonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPersonActionPerformed

    private void txtAccountFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAccountFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAccountFocusGained

    private void txtAccountFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAccountFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAccountFocusLost

    private void txtAccountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAccountActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_txtAccountActionPerformed

    private void txtDespFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDespFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDespFocusGained

    private void txtDespFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDespFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDespFocusLost

    private void txtDespActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDespActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDespActionPerformed

    private void txtRefrenceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRefrenceFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRefrenceFocusGained

    private void txtRefrenceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRefrenceFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRefrenceFocusLost

    private void txtRefrenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRefrenceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRefrenceActionPerformed

    private void txtCurrencyFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCurrencyFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCurrencyFocusGained

    private void txtCurrencyFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCurrencyFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCurrencyFocusLost

    private void txtCurrencyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCurrencyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCurrencyActionPerformed

    private void txtOptionFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtOptionFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtOptionFocusGained

    private void txtOptionFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtOptionFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtOptionFocusLost

    private void txtOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtOptionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtOptionActionPerformed

    private void txtBatchNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBatchNoFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBatchNoFocusGained

    private void txtBatchNoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBatchNoFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBatchNoFocusLost

    private void txtBatchNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBatchNoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBatchNoActionPerformed

    private void txtProjectNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtProjectNoFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtProjectNoFocusGained

    private void txtProjectNoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtProjectNoFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtProjectNoFocusLost

    private void txtProjectNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProjectNoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtProjectNoActionPerformed

    private void chkAdjustActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkAdjustActionPerformed
        // TODO add your handling code here:
        tableListener(chkAdjust.isSelected());
    }//GEN-LAST:event_chkAdjustActionPerformed

    private void tblCashMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCashMouseClicked
        // TODO add your handling code here:
        checkLock();
    }//GEN-LAST:event_tblCashMouseClicked

    private void txtDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDateFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDateFocusGained

    private void txtDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDateFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDateFocusLost

    private void txtDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDateActionPerformed

    private void btnModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModeActionPerformed
        // TODO add your handling code here:
        reportModeDialog();
    }//GEN-LAST:event_btnModeActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnMode;
    private javax.swing.JCheckBox chkAdjust;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JFormattedTextField lblRecord;
    private javax.swing.JPanel panelDate;
    private javax.swing.JPanel panelOption;
    private javax.swing.JScrollPane scroll;
    private javax.swing.JTable tblCash;
    private javax.swing.JTextField txtAccount;
    private javax.swing.JTextField txtBatchNo;
    private javax.swing.JFormattedTextField txtClosing;
    private javax.swing.JFormattedTextField txtCr;
    private javax.swing.JTextField txtCurrency;
    private javax.swing.JTextField txtDate;
    private javax.swing.JTextField txtDepartment;
    private javax.swing.JTextField txtDesp;
    private javax.swing.JFormattedTextField txtDr;
    private javax.swing.JFormattedTextField txtOpening;
    private javax.swing.JTextField txtOption;
    private javax.swing.JTextField txtPerson;
    private javax.swing.JTextField txtProjectNo;
    private javax.swing.JTextField txtRefrence;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {
        if (selectObj != null) {
            if (source.equals("Date")) {
                searchCash();
            } else if (source.equals("Date-Search")) {
                if (source.equals("Date-Search")) {
                    String date = selectObj.toString();
                    DateModel model = new DateModel();
                    model.setStartDate(date);
                    model.setEndDate(date);
                    model.setDescription(Util1.toDateStr(date, "yyyy-MM-dd", "dd/MM/yyyy"));
                    dateAutoCompleter.setDateModel(model);
                }
                searchCash();
            } else if (source.equals("CAL-TOTAL")) {
                calDrCr();
                calculateClosing();
            } else {
                searchCash();
            }
        }
    }

    @Override
    public void save() {

    }

    @Override
    public void delete() {
        deleteVoucher(false);
    }

    @Override
    public void newForm() {
        defaultFilter();
        searchCash();
    }

    @Override
    public void history() {
    }

    @Override
    public void print() {
        printVoucher();
    }

    @Override
    public void refresh() {
        searchCash();
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
