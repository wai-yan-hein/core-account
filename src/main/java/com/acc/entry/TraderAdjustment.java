/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.entry;

import com.acc.common.AccountRepo;
import com.acc.editor.COA3CellEditor;
import com.acc.editor.CurrencyAAutoCompleter;
import com.acc.editor.CurrencyAEditor;
import com.acc.editor.DepartmentAutoCompleter;
import com.acc.editor.DepartmentCellEditor;
import com.acc.editor.DespAutoCompleter;
import com.acc.editor.DespEditor;
import com.acc.editor.RefAutoCompleter;
import com.acc.editor.RefCellEditor;
import com.acc.editor.TraderAAutoCompleter;
import com.acc.common.CashInOutTableModel;
import com.acc.common.CashOpeningTableModel;
import com.acc.common.DateAutoCompleter;
import com.acc.common.DateTableDecorator;
import com.acc.common.OpeningCellRender;
import com.acc.common.TraderAdjustmentTableModel;
import com.acc.editor.BatchCellEditor;
import com.acc.editor.BatchNoAutoCompeter;
import com.acc.editor.COA3AutoCompleter;
import com.acc.model.ChartOfAccount;
import com.acc.model.DeleteObj;
import com.user.model.Currency;
import com.acc.model.Department;
import com.acc.model.ReportFilter;
import com.acc.model.TmpOpening;
import com.acc.model.Gl;
import com.acc.model.TraderA;
import com.common.Global;
import com.common.PanelControl;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.user.common.UserRepo;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import lombok.extern.slf4j.Slf4j;
import net.coderazzi.filters.gui.AutoChoices;
import net.coderazzi.filters.gui.TableFilterHeader;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.view.JasperViewer;
import org.springframework.core.task.TaskExecutor;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class TraderAdjustment extends javax.swing.JPanel implements SelectionObserver,
        PanelControl {

    private TaskExecutor taskExecutor;
    private WebClient accountApi;
    private DateAutoCompleter dateAutoCompleter;
    private TraderAAutoCompleter traderAutoCompleter;
    private DepartmentAutoCompleter departmentAutoCompleter;
    private COA3AutoCompleter coaAutoCompleter;
    private CurrencyAAutoCompleter currencyAutoCompleter;
    private SelectionObserver selectionObserver;
    private DespAutoCompleter despAutoCompleter;
    private RefAutoCompleter refAutoCompleter;
    private BatchNoAutoCompeter batAutoCompleter;
    private final CashInOutTableModel inOutTableModel = new CashInOutTableModel();
    private final CashOpeningTableModel opTableModel = new CashOpeningTableModel();
    private SelectionObserver observer;
    private final JPopupMenu popupmenu = new JPopupMenu();
    private final JLabel lblMessage = new JLabel();
    private TableFilterHeader filterHeader;
    private final HashMap<String, Gl> hmOpening = new HashMap<>();
    private JProgressBar progress;
    private AccountRepo accountRepo;
    private UserRepo userRepo;
    private Mono<List<Department>> monoDep;
    private Mono<List<Currency>> monoCur;
    private final String path = String.format("%s%s%s", "temp", File.separator, "Ledger" + Global.macId);
    private DateTableDecorator decorator;
    private TraderAdjustmentTableModel adjustmentTableModel = new TraderAdjustmentTableModel();

    public UserRepo getUserRepo() {
        return userRepo;
    }

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public AccountRepo getAccounRepo() {
        return accountRepo;
    }

    public void setAccounRepo(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }

    public TaskExecutor getTaskExecutor() {
        return taskExecutor;
    }

    public void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public WebClient getAccountApi() {
        return accountApi;
    }

    public void setAccountApi(WebClient accountApi) {
        this.accountApi = accountApi;
    }

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
     * Creates new form CashBook
     *
     */
    public TraderAdjustment() {
        initComponents();
        initPopup();
        initTableCashInOut();
        initTableCashOP();
        initDateDecorator();
        actionMapping();
    }

    private void batchLock(boolean lock) {
        tblCash.setEnabled(lock);
        observer.selected("save", lock);
        observer.selected("delete", lock);
    }

    private void initTableModel() {
        accountRepo.getDefaultDepartment().subscribe((t) -> {
            adjustmentTableModel.setDepartment(t);
        });
        userRepo.getDefaultCurrency().subscribe((t) -> {
            adjustmentTableModel.setCurrency(t);
        });
        adjustmentTableModel.setTraderAAutoCompleter(traderAutoCompleter);
        tblCash.setModel(adjustmentTableModel);
        adjustmentTableModel.setParent(tblCash);
        adjustmentTableModel.setAccountRepo(accountRepo);
        adjustmentTableModel.setDateAutoCompleter(dateAutoCompleter);
        adjustmentTableModel.setObserver(this);
        adjustmentTableModel.addNewRow();
    }

    private void initDateDecorator() {
        decorator = DateTableDecorator.decorate(panelDate);
        decorator.setObserver(this);
    }

    private void createDateFilter() {
        HashMap<Integer, String> hmDate = new HashMap<>();
        HashMap<String, Integer> hmPage = new HashMap<>();
        List<Date> date = Util1.getDaysBetweenDates(Util1.toDate(Global.startDate, "dd/MM/yyyy"), Util1.getTodayDate());
        for (int i = 0; i < date.size(); i++) {
            String str = Util1.toDateStr(date.get(i), "yyyy-MM-dd");
            int z = i + 1;
            hmDate.put(z, str);
            hmPage.put(str, z);
        }
        decorator.setHmPage(hmPage);
        decorator.setHmData(hmDate);
        decorator.refreshButton(Util1.toDateStrMYSQL(dateAutoCompleter.getEndDate(), Global.dateFormat));
    }

    private void initFilter() {
        monoCur = accountRepo.getCurrency();
        monoDep = accountRepo.getDepartment();
        traderAutoCompleter = new TraderAAutoCompleter(txtPerson, accountApi, null, true);
        traderAutoCompleter.setSelectionObserver(this);
        monoDep.subscribe((t) -> {
            departmentAutoCompleter = new DepartmentAutoCompleter(txtDepartment, t, null, true, true);
            departmentAutoCompleter.setObserver(this);
        });

        monoCur.subscribe((t) -> {
            currencyAutoCompleter = new CurrencyAAutoCompleter(txtCurrency, t, null, true);
            currencyAutoCompleter.setSelectionObserver(this);
        });
        coaAutoCompleter = new COA3AutoCompleter(txtAccount, accountApi, null, true, 0);
        coaAutoCompleter.setSelectionObserver(this);
        dateAutoCompleter = new DateAutoCompleter(txtDate);
        dateAutoCompleter.setSelectionObserver(this);
        despAutoCompleter = new DespAutoCompleter(txtDesp, accountApi, null, true);
        despAutoCompleter.setSelectionObserver(this);
        refAutoCompleter = new RefAutoCompleter(txtRefrence, accountApi, null, true);
        refAutoCompleter.setSelectionObserver(this);
        batAutoCompleter = new BatchNoAutoCompeter(txtBatch, accountApi, null, true);
        batAutoCompleter.setSelectionObserver(this);
//model
    }

    public void initMain() {
        batchLock(!Global.batchLock);
        initFilter();
        initTableModel();
        initTableCB();
        createDateFilter();
    }

    private void requestFoucsTable() {
        int rc = tblCash.getRowCount();
        if (rc >= 1) {
            tblCash.setRowSelectionInterval(rc - 1, rc - 1);
        } else {
            tblCash.setRowSelectionInterval(0, 0);
        }
        tblCash.setColumnSelectionInterval(0, 0);
        tblCash.requestFocus();
    }

    private void initTableCashInOut() {
        tblCIO.setModel(inOutTableModel);
        tblCIO.getTableHeader().setFont(Global.tblHeaderFont);
        tblCIO.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCIO.setFont(Global.amtFont);
        tblCIO.setDefaultRenderer(Double.class, new OpeningCellRender());
        tblCIO.setDefaultRenderer(Object.class, new OpeningCellRender());
        tblCIO.getColumnModel().getColumn(0).setPreferredWidth(10);
        tblCIO.getColumnModel().getColumn(1).setPreferredWidth(100);
        tblCIO.getColumnModel().getColumn(2).setPreferredWidth(100);
    }

    private void initTableCashOP() {
        tblCashOP.setModel(opTableModel);
        tblCashOP.getTableHeader().setFont(Global.tblHeaderFont);
        tblCashOP.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCashOP.setFont(Global.amtFont);
        tblCashOP.setDefaultRenderer(Double.class, new OpeningCellRender());
        tblCashOP.setDefaultRenderer(Object.class, new OpeningCellRender());
        tblCashOP.getColumnModel().getColumn(0).setPreferredWidth(10);
        tblCashOP.getColumnModel().getColumn(1).setPreferredWidth(100);
        tblCashOP.getColumnModel().getColumn(2).setPreferredWidth(100);
    }

    private void initTableCB() {

        tblCash.getTableHeader().setFont(Global.tblHeaderFont);
        tblCash.getTableHeader().setPreferredSize(new Dimension(25, 25));
        tblCash.setCellSelectionEnabled(true);
        tblCash.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCash.getColumnModel().getColumn(0).setPreferredWidth(20);// Date
        tblCash.getColumnModel().getColumn(1).setPreferredWidth(13);// Department
        tblCash.getColumnModel().getColumn(2).setPreferredWidth(180);// Description      
        tblCash.getColumnModel().getColumn(3).setPreferredWidth(180);// Ref  
        tblCash.getColumnModel().getColumn(4).setPreferredWidth(90);// Ref  
        tblCash.getColumnModel().getColumn(5).setPreferredWidth(90);// Person
        tblCash.getColumnModel().getColumn(6).setPreferredWidth(150);// Account
        tblCash.getColumnModel().getColumn(7).setPreferredWidth(1);// Curr      
        tblCash.getColumnModel().getColumn(8).setPreferredWidth(90);// Dr-Amt   
        tblCash.getColumnModel().getColumn(9).setPreferredWidth(90);// Cr-Amt  
        tblCash.getColumnModel().getColumn(0).setCellEditor(new AutoClearEditor());
        monoDep.subscribe((t) -> {
            tblCash.getColumnModel().getColumn(1).setCellEditor(new DepartmentCellEditor(t));
        });
        tblCash.getColumnModel().getColumn(2).setCellEditor(new DespEditor(accountApi));
        tblCash.getColumnModel().getColumn(3).setCellEditor(new RefCellEditor(accountApi));
        tblCash.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());
        tblCash.getColumnModel().getColumn(5).setCellEditor(new BatchCellEditor(accountApi));
        tblCash.getColumnModel().getColumn(6).setCellEditor(new COA3CellEditor(accountApi, 3));
        monoCur.subscribe((t) -> {
            tblCash.getColumnModel().getColumn(7).setCellEditor(new CurrencyAEditor(t));
        });
        tblCash.getColumnModel().getColumn(8).setCellEditor(new AutoClearEditor());
        tblCash.getColumnModel().getColumn(9).setCellEditor(new AutoClearEditor());
        tblCash.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        filterHeader = new TableFilterHeader(tblCash, AutoChoices.ENABLED);
        filterHeader.setPosition(TableFilterHeader.Position.TOP);
        filterHeader.setFont(Global.textFont);
        filterHeader.setVisible(false);
    }

    private void actionMapping() {
        tblCash.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");
        tblCash.getActionMap().put("delete", actionDelete);
        tblCash.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, InputEvent.SHIFT_DOWN_MASK), "force-delete");
        tblCash.getActionMap().put("force-delete", forceDelete);
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

    private void closeCellEditor() {
        if (tblCash.getCellEditor() != null) {
            tblCash.getCellEditor().stopCellEditing();
        }
    }

    private void deleteVoucher(boolean force) {
        closeCellEditor();
        int selectRow = tblCash.convertRowIndexToModel(tblCash.getSelectedRow());
        int yes_no;
        if (tblCash.getSelectedRow() >= 0) {
            Gl vgl = adjustmentTableModel.getVGl(selectRow);
            if (vgl.getTranSource().equals("Report")) {
                return;
            }
            if (!force) {
                if (!vgl.getTranSource().equals("CB")) {
                    JOptionPane.showMessageDialog(Global.parentForm, "delete in original voucher.");
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
                        "Delete", JOptionPane.YES_NO_OPTION);
                if (yes_no == 0) {
                    accountRepo.delete(obj).subscribe((t) -> {
                        if (t) {
                            adjustmentTableModel.deleteVGl(selectRow);
                        }
                    });

                    calDebitCredit();
                }
            }
        }
    }

    public void printVoucher() {
        String currency = currencyAutoCompleter.getCurrency().getCurCode();
        String stDate = dateAutoCompleter.getStDate();
        String endDate = dateAutoCompleter.getEndDate();
        if (!currency.equals("-") || !ProUtil.isMultiCur()) {
            progress.setIndeterminate(true);
            taskExecutor.execute(() -> {
                try {
                    Map<String, Object> p = new HashMap();
                    p.put("p_report_name", traderAutoCompleter.getTrader().getTraderName());
                    p.put("p_date", String.format("Between %s and %s", stDate, endDate));
                    p.put("p_print_date", Util1.getTodayDateTime());
                    p.put("p_comp_name", Global.companyName);
                    p.put("p_comp_address", Global.companyAddress);
                    p.put("p_comp_phone", Global.companyPhone);
                    p.put("p_currency", currencyAutoCompleter.getCurrency().getCurCode());
                    p.put("p_batch_no", txtBatch.getText());
                    Gl vGl = opTableModel.getVGl(0);
                    double opening = vGl.getDrAmt();
                    double closing = vGl.getCrAmt();
                    p.put("p_opening", opening);
                    p.put("p_closing", closing);
                    String rpName = chkSummary.isSelected() ? "IndividualLedgerSummary.jasper" : "TraderLedger.jasper";
                    String filePath = String.format(Global.accountRP + rpName);
                    InputStream input = new FileInputStream(new File(path.concat(".json")));
                    JsonDataSource ds = new JsonDataSource(input);
                    JasperPrint js = JasperFillManager.fillReport(filePath, p, ds);
                    JasperViewer.viewReport(js, false);
                    progress.setIndeterminate(false);
                } catch (JRException ex) {
                    progress.setIndeterminate(false);
                    JOptionPane.showMessageDialog(Global.parentForm, ex.getMessage());
                    log.error("printVoucher : " + ex.getMessage());

                } catch (FileNotFoundException ex) {
                    log.error(ex.getMessage());
                }
            });
        } else {
            JOptionPane.showMessageDialog(this, "Select Currency.");
        }

    }

    private void searchCash() {
        TraderA trader = traderAutoCompleter.getTrader();
        String traderCode = trader.getKey().getCode();
        if (traderCode.equals("-")) {
            JOptionPane.showMessageDialog(this, "Select Trader.");
            return;
        } else if (trader.getAccount().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selected trader does not have account.");
            return;
        }
        progress.setIndeterminate(true);
        ReportFilter filter = new ReportFilter(Global.compCode, Global.macId);
        filter.setFromDate(Util1.toDateStrMYSQL(dateAutoCompleter.getStDate(), Global.dateFormat));
        filter.setToDate(Util1.toDateStrMYSQL(dateAutoCompleter.getEndDate(), Global.dateFormat));
        filter.setDesp(despAutoCompleter.getAutoText().getDescription().equals("All") ? "-" : despAutoCompleter.getAutoText().getDescription());
        filter.setReference(refAutoCompleter.getAutoText().getDescription().equals("All") ? "-"
                : refAutoCompleter.getAutoText().getDescription());
        filter.setCurCode(currencyAutoCompleter.getCurrency().getCurCode());
        filter.setListDepartment(departmentAutoCompleter.getListOption());
        filter.setBatchNo(batAutoCompleter.getAutoText().getDescription().equals("All") ? "-"
                : batAutoCompleter.getAutoText().getDescription());
        filter.setTraderCode(traderCode);
        ChartOfAccount coa = coaAutoCompleter.getCOA();
        String coaLv1 = Util1.getInteger(coa.getCoaLevel()) == 1 ? coa.getKey().getCoaCode() : "-";
        String coaLv2 = Util1.getInteger(coa.getCoaLevel()) == 2 ? coa.getKey().getCoaCode() : "-";
        String accCode = Util1.getInteger(coa.getCoaLevel()) == 3 ? coa.getKey().getCoaCode() : "-";
        filter.setCoaLv1(coaLv1);
        filter.setCoaLv2(coaLv2);
        filter.setAcc(accCode);
        filter.setSrcAcc(trader.getAccount());
        filter.setSummary(chkSummary.isSelected());
        adjustmentTableModel.clear();
        Flux<Gl> result = accountApi.post()
                .uri("/account/search-gl")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .bodyToFlux(Gl.class);
        result.subscribe((t) -> {
            adjustmentTableModel.addVGl(t);
        }, (e) -> {
            JOptionPane.showMessageDialog(this, e.getMessage());
            progress.setIndeterminate(false);
        }, () -> {
            adjustmentTableModel.setGlDate(filter.getFromDate());
            adjustmentTableModel.fireTableDataChanged();
            adjustmentTableModel.addNewRow();
            decorator.refreshButton(filter.getFromDate());
            progress.setIndeterminate(false);
            calOpeningClosing(traderCode, trader.getAccount());
            requestFoucsTable();
        });
    }

    public void clearFilter() {
        searchCash();

    }

    /*  private void getLatestCurrency() {
    if (ProUtil.isMultiCur()) {
    initCurrency();
    } else {
    tblCurrency.setVisible(false);
    jScrollPane4.setBorder(null);
    }
    }
    
    private void initCurrency() {
    tblCurrency.setModel(curExchangeRateTableModel);
    tblCurrency.getTableHeader().setFont(Global.tblHeaderFont);
    tblCurrency.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    tblCurrency.setFont(Global.shortCutFont);
    tblCurrency.setCellSelectionEnabled(true);
    tblCurrency.setDefaultRenderer(Object.class, new TableCellRender());
    tblCurrency.setDefaultRenderer(Double.class, new TableCellRender());
    //curExchangeRateTableModel.setListEx(exchangeService.getLatestExchange(Global.compCode, Util1.toDateStr(Util1.getTodayDate(), "yyyy-MM-dd")));
    }*/
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuItem1 = new javax.swing.JMenuItem();
        tblScrollPane = new javax.swing.JScrollPane();
        tblCash = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCIO = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblCashOP = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        txtDate = new javax.swing.JFormattedTextField();
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
        txtBatch = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        panelDate = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        chkSummary = new javax.swing.JCheckBox();
        txtRecord = new javax.swing.JFormattedTextField();

        jMenuItem1.setText("jMenuItem1");

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        tblCash.setAutoCreateRowSorter(true);
        tblCash.setFont(Global.textFont);
        tblCash.setToolTipText("");
        tblCash.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tblCash.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tblCash.setGridColor(new java.awt.Color(204, 204, 204));
        tblCash.setRowHeight(Global.tblRowHeight);
        tblCash.setShowHorizontalLines(true);
        tblCash.setShowVerticalLines(true);
        tblScrollPane.setViewportView(tblCash);

        tblCIO.setFont(Global.shortCutFont);
        tblCIO.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblCIO.setRowHeight(Global.tblRowHeight);
        jScrollPane1.setViewportView(tblCIO);

        tblCashOP.setFont(Global.shortCutFont);
        tblCashOP.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblCashOP.setRowHeight(Global.tblRowHeight);
        jScrollPane2.setViewportView(tblCashOP);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        txtDate.setFont(Global.textFont);
        txtDate.setName("txtDate"); // NOI18N
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
        txtCurrency.setEnabled(false);
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

        txtBatch.setFont(Global.textFont);
        txtBatch.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtBatch.setName("txtCurrency"); // NOI18N
        txtBatch.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtBatchFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtBatchFocusLost(evt);
            }
        });
        txtBatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBatchActionPerformed(evt);
            }
        });

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Batch No");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPerson, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
                    .addComponent(txtDepartment, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtAccount, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDesp, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtRefrence, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtCurrency, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtBatch, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtDate, txtPerson});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel3))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtDesp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtRefrence, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtPerson, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtAccount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtBatch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtDepartment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
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

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel9.setText("Records : ");

        chkSummary.setText("Summary Mode");
        chkSummary.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSummaryActionPerformed(evt);
            }
        });

        txtRecord.setEditable(false);
        txtRecord.setBorder(null);
        txtRecord.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txtRecord.setHorizontalAlignment(javax.swing.JTextField.LEFT);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkSummary)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtRecord, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(240, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkSummary)
                    .addComponent(jLabel9)
                    .addComponent(txtRecord, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tblScrollPane)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 685, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(panelDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 685, Short.MAX_VALUE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tblScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observer.selected("control", this);
    }//GEN-LAST:event_formComponentShown

    private void txtDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDateFocusGained
        // TODO add your handling code here:
        txtDate.selectAll();
    }//GEN-LAST:event_txtDateFocusGained

    private void txtDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDateFocusLost
        // TODO add your handling code here:
        //dateAutoCompleter.closePopup();
        //messageBean.setValue(txtDate.getText());
    }//GEN-LAST:event_txtDateFocusLost

    private void txtDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDateActionPerformed

    private void txtDepartmentFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDepartmentFocusGained
        // TODO add your handling code here:
        txtDepartment.selectAll();
        //departmentAutoCompleter.showPopup();
    }//GEN-LAST:event_txtDepartmentFocusGained

    private void txtDepartmentFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDepartmentFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDepartmentFocusLost

    private void txtDepartmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDepartmentActionPerformed
        // TODO add your handling code here:
        /*if (txtDepartment.getText().isEmpty()) {
            selectionObserver.selected("Department", "-");
        }*/
    }//GEN-LAST:event_txtDepartmentActionPerformed

    private void txtPersonFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPersonFocusGained
        // TODO add your handling code here:
        txtPerson.selectAll();
        //traderAutoCompleter.showPopup();
    }//GEN-LAST:event_txtPersonFocusGained

    private void txtPersonFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPersonFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPersonFocusLost

    private void txtPersonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPersonActionPerformed
        // TODO add your handling code here:
        /*if (txtPerson.getText().isEmpty()) {
            selectionObserver.selected("Trader", "-");
        }*/
    }//GEN-LAST:event_txtPersonActionPerformed

    private void txtAccountFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAccountFocusGained
        // TODO add your handling code here:

        txtAccount.selectAll();
        //coaAutoCompleter.showPopup();
    }//GEN-LAST:event_txtAccountFocusGained

    private void txtAccountFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAccountFocusLost
        // TODO add your handling code here:
        //coaAutoCompleter.closePopup();
    }//GEN-LAST:event_txtAccountFocusLost

    private void txtAccountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAccountActionPerformed
        // TODO add your handling code here:
        /*if (txtAccount.getText().isEmpty()) {
            selectionObserver.selected("COA", "-");
        }*/
    }//GEN-LAST:event_txtAccountActionPerformed

    private void txtDespFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDespFocusGained
        // TODO add your handling code here:
        txtDesp.selectAll();
    }//GEN-LAST:event_txtDespFocusGained

    private void txtDespFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDespFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDespFocusLost

    private void txtDespActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDespActionPerformed
        // TODO add your handling code here:
        selectionObserver.selected("Description", Util1.isNull(txtDesp.getText(), "-"));
    }//GEN-LAST:event_txtDespActionPerformed

    private void txtRefrenceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRefrenceFocusGained
        // TODO add your handling code here:
        txtRefrence.selectAll();
    }//GEN-LAST:event_txtRefrenceFocusGained

    private void txtRefrenceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRefrenceFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRefrenceFocusLost

    private void txtRefrenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRefrenceActionPerformed
        // TODO add your handling code here:
        selectionObserver.selected("Ref", Util1.isNull(txtRefrence.getText(), "-"));
    }//GEN-LAST:event_txtRefrenceActionPerformed

    private void txtCurrencyFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCurrencyFocusGained
        // TODO add your handling code here:

        txtCurrency.selectAll();
        // currencyAutoCompleter.showPopup();
    }//GEN-LAST:event_txtCurrencyFocusGained

    private void txtCurrencyFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCurrencyFocusLost
        // TODO add your handling code here:
        //currencyAutoCompleter.closePopup();
    }//GEN-LAST:event_txtCurrencyFocusLost

    private void txtCurrencyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCurrencyActionPerformed
        // TODO add your handling code here:
        /* if (txtCurrency.getText().isEmpty()) {
            selectionObserver.selected("Currency", "-");
        }*/
    }//GEN-LAST:event_txtCurrencyActionPerformed

    private void txtBatchFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBatchFocusGained
        // TODO add your handling code here:
        txtBatch.selectAll();
    }//GEN-LAST:event_txtBatchFocusGained

    private void txtBatchFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBatchFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBatchFocusLost

    private void txtBatchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBatchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBatchActionPerformed

    private void chkSummaryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSummaryActionPerformed
        // TODO add your handling code here:
        chkSummary.setText(chkSummary.isSelected() ? "Summary Mode " : "Detail Mode");
        searchCash();
    }//GEN-LAST:event_chkSummaryActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkSummary;
    private javax.swing.JLabel jLabel1;
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel panelDate;
    private javax.swing.JTable tblCIO;
    private javax.swing.JTable tblCash;
    private javax.swing.JTable tblCashOP;
    private javax.swing.JScrollPane tblScrollPane;
    private javax.swing.JTextField txtAccount;
    private javax.swing.JTextField txtBatch;
    private javax.swing.JTextField txtCurrency;
    private javax.swing.JFormattedTextField txtDate;
    private javax.swing.JTextField txtDepartment;
    private javax.swing.JTextField txtDesp;
    private javax.swing.JTextField txtPerson;
    private javax.swing.JFormattedTextField txtRecord;
    private javax.swing.JTextField txtRefrence;
    // End of variables declaration//GEN-END:variables

    private void calDebitCredit() {
        opTableModel.clear();
        inOutTableModel.clear();
        List<Gl> listVGl = adjustmentTableModel.getListVGl();
        //lblTotalCount.setText(listVGl.size() - 1 + "");
        Map<String, List<Gl>> hmGroup = listVGl.stream().collect(Collectors.groupingBy(w -> w.getCurCode()));
        hmGroup.forEach((t, u) -> {
            double drAmt = 0.0;
            double crAmt = 0.0;
            for (Gl gl : u) {
                drAmt += Util1.getDouble(gl.getDrAmt());
                crAmt += Util1.getDouble(gl.getCrAmt());
            }
            double closing;
            Gl vgl = hmOpening.get(t);
            if (vgl != null) {
                closing = Util1.getDouble(vgl.getDrAmt()) + drAmt - crAmt;
                vgl.setCrAmt(closing);
            } else {
                closing = drAmt - crAmt;
                vgl = new Gl(t, 0.0, closing);
            }
            opTableModel.addVGl(vgl);
            inOutTableModel.addVGl(new Gl(t, drAmt, crAmt));
        });
    }

    private void calOpeningClosing(String traderCode, String account) {
        String stDate = Util1.toDateStrMYSQL(dateAutoCompleter.getStDate(), Global.dateFormat);
        String opDate = Util1.toDateStrMYSQL(Global.startDate, "dd/MM/yyyy");
        String clDate = Util1.toDateStrMYSQL(stDate, "dd/MM/yyyy");
        ReportFilter filter = new ReportFilter(Global.compCode, Global.macId);
        filter.setOpeningDate(opDate);
        filter.setFromDate(clDate);
        filter.setCurCode(currencyAutoCompleter.getCurrency().getCurCode());
        filter.setListDepartment(departmentAutoCompleter.getListOption());
        filter.setTraderCode(traderCode);
        filter.setCoaCode(account);
        Mono<TmpOpening> result = accountRepo.getOpening(filter);
        hmOpening.clear();
        result.subscribe((t) -> {
            hmOpening.put(t.getKey().getCurCode(), new Gl(t.getKey().getCurCode(), t.getOpening(), t.getClosing()));
        }, (e) -> {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }, () -> {
            calDebitCredit();
        });
    }

    @Override
    public void selected(Object source, Object selectObj) {
        if (selectObj != null) {
            if (source.equals("Date")) {
                searchCash();
            } else if (source.equals("Date-Search")) {
                String date = Util1.toDateStr(selectObj.toString(), "yyyy-MM-dd", Global.dateFormat);
                dateAutoCompleter.setStDate(date);
                dateAutoCompleter.setEndDate(date);
                txtDate.setText(date);
                searchCash();
            } else if (source.equals("CAL-TOTAL")) {
                calDebitCredit();
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
        clearFilter();
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
        chkSummary.setSelected(false);
        chkSummary.setText("Detail Mode");
        searchCash();
    }

    @Override
    public void filter() {
        filterHeader.setVisible(!filterHeader.isVisible());
    }

    @Override
    public String panelName() {
        return this.getName();
    }

}
