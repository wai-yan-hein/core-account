/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.acc.setup;

import com.acc.common.AccountRepo;
import com.acc.common.OpeningBalanceTableModel;
import com.acc.common.DateAutoCompleter;
import com.acc.model.OpeningBalance;
import com.acc.model.Department;
import com.user.model.Currency;
import com.acc.model.ChartOfAccount;
import com.acc.model.ReportFilter;
import com.acc.model.TraderA;
import com.acc.editor.COA3CellEditor;
import com.acc.editor.TraderCellEditor;
import com.acc.editor.DepartmentCellEditor;
import com.acc.editor.CurrencyAEditor;
import com.acc.editor.DepartmentAutoCompleter;
import com.acc.editor.COAAutoCompleter;
import com.acc.editor.TraderAAutoCompleter;
import com.acc.editor.CurrencyAAutoCompleter;
import com.user.common.UserRepo;
import com.common.SelectionObserver;
import com.common.PanelControl;
import com.common.KeyPropagate;
import com.common.Global;
import com.common.Util1;
import com.common.DecimalFormatRender;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
//import com.cv.accountswing.util.Util1;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

import lombok.extern.slf4j.Slf4j;
import net.coderazzi.filters.gui.AutoChoices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.ResponseEntity;

import net.coderazzi.filters.gui.TableFilterHeader;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.view.JasperViewer;
import reactor.core.publisher.Mono;

/**
 *
 * @author htut
 */
@Slf4j
@Component
public class COAOpeningBalanceSetup extends javax.swing.JPanel
        implements SelectionObserver, KeyListener, KeyPropagate, PanelControl {

    private final OpeningBalanceTableModel openingTableModel = new OpeningBalanceTableModel();
    private final String path = String.format("%s%s%s", "temp", File.separator, "Ledger" + Global.macId);

    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private WebClient accountApi;

    private DepartmentAutoCompleter departmenttAutoCompleter;
    private CurrencyAAutoCompleter currencyAutoCompleter;
    private COAAutoCompleter coaAutoCompleter;
    private TraderAAutoCompleter tradeAutoCompleter;
    private DateAutoCompleter dateAutoCompleter;

    private List<Department> listDept = new ArrayList<>();
    private List<String> department = new ArrayList<>();
    private List<TraderA> listTrader = new ArrayList<>();
    private List<Currency> listCurrency = new ArrayList<>();

    private SelectionObserver observer;
    private TableFilterHeader filterHeader;
    private JProgressBar progress;
    private TaskExecutor taskExecutor;

    private boolean isSearch = false;
    private String curCode, depCode, coaParent;
    private String startDate, endDate, dept, accCode, ref, traderCode, currency, traderType, tranSource, coaLv2, coaLv1;

    ButtonGroup g = new ButtonGroup();
    /**
     * Creates new form COAOpeningBalanceSetup
     */
    // Constructor for opening balance
    public COAOpeningBalanceSetup() {
        initComponents();
        chkGroup(); 
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

    public void setObservaer(SelectionObserver observer) {
        this.observer = observer;
    }

    public void actionMapping() {
        String solve = "delete";
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        tblOpening.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, solve);
        tblOpening.getActionMap().put(solve, new DeleteAction());
    }

    public class DeleteAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            // deleteTran();
        }
    }

    public void initMain() {
        txtDate.setDate(Util1.toDate(Global.startDate,"dd/MM/yyyy"));
        initKeyListener();
        initComboBox();
        initTable();
        searchOpening();
    }

    private void initKeyListener() {
        txtDate.getDateEditor().getUiComponent().setName("txtDate");
        txtDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtCOA.addKeyListener(this);
        txtDept.addKeyListener(this);
        txtCurrency.addKeyListener(this);
        tblOpening.addKeyListener(this);

    }

    // grouping check box
    private void chkGroup() {
      
       
        g.add(chkCus);
        g.add(chkSup);
    }

    // initialize data for combo box
    private void initComboBox() {
        listDept = accountRepo.getDepartment();
        listCurrency = accountRepo.getCurrency();
        listTrader = accountRepo.getTrader();
        departmenttAutoCompleter = new DepartmentAutoCompleter(txtDept, listDept, null, true, true);
        departmenttAutoCompleter.setObserver(this);
        currencyAutoCompleter = new CurrencyAAutoCompleter(txtCurrency, listCurrency, null, true);
        currencyAutoCompleter.setSelectionObserver(this);
        coaAutoCompleter = new COAAutoCompleter(txtCOA, accountRepo.getChartOfAccount(), null, true);
        coaAutoCompleter.setSelectionObserver(this);
        // dateAutoCompleter = new DateAutoCompleter(txtDate, true);
        // dateAutoCompleter.setSelectionObserver(this);
    }

    private final FocusAdapter fa = new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            ((JTextFieldDateEditor) e.getSource()).selectAll();
        }
    };

    // initialize data for table:: tblOpening
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
        openingTableModel.setOpdate(txtDate);
        openingTableModel.setTradeAutoCompleter(tradeAutoCompleter);
        openingTableModel.setParent(tblOpening);
        openingTableModel.setObserver(this);
        openingTableModel.setAccountRepo(accountRepo);
        openingTableModel.setDateAutoCompleter(dateAutoCompleter);
        openingTableModel.setObserver(this);
        openingTableModel.setDepartment(accountRepo.getDefaultDepartment());
        openingTableModel.setCurrency(userRepo.getDefaultCurrency());
        openingTableModel.setDeptAutoCompleter(departmenttAutoCompleter);
        openingTableModel.addNewRow();
        tblOpening.getColumnModel().getColumn(0).setPreferredWidth(10);
        tblOpening.getColumnModel().getColumn(1).setPreferredWidth(250);
        tblOpening.getColumnModel().getColumn(2).setPreferredWidth(20);
        tblOpening.getColumnModel().getColumn(3).setPreferredWidth(250);
        tblOpening.getColumnModel().getColumn(4).setPreferredWidth(5);
        tblOpening.getColumnModel().getColumn(5).setPreferredWidth(10);
        tblOpening.getColumnModel().getColumn(6).setPreferredWidth(20);
        tblOpening.getColumnModel().getColumn(7).setPreferredWidth(20);
        tblOpening.getColumnModel().getColumn(6).setCellRenderer(new DecimalFormatRender());
        tblOpening.getColumnModel().getColumn(7).setCellRenderer(new DecimalFormatRender());
        tblOpening.getColumnModel().getColumn(0).setCellEditor(new COA3CellEditor(accountRepo, false));
        tblOpening.getColumnModel().getColumn(1).setCellEditor(new COA3CellEditor(accountRepo, false));
        tblOpening.getColumnModel().getColumn(2).setCellEditor(new TraderCellEditor(listTrader, false, 2));
        tblOpening.getColumnModel().getColumn(3).setCellEditor(new TraderCellEditor(listTrader, false, 2));
        tblOpening.getColumnModel().getColumn(4).setCellEditor(new DepartmentCellEditor(false, listDept));
        tblOpening.getColumnModel().getColumn(5).setCellEditor(new CurrencyAEditor(listCurrency, false));
        tblOpening.getColumnModel().getColumn(6).setCellEditor(new AutoClearEditor());
        tblOpening.getColumnModel().getColumn(7).setCellEditor(new AutoClearEditor());
        tblOpening.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblOpening.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");

        filterHeader = new TableFilterHeader(tblOpening, AutoChoices.ENABLED);
        filterHeader.setPosition(TableFilterHeader.Position.TOP);
        filterHeader.setFont(Global.textFont);
        filterHeader.setVisible(false);
    }

    // allo cell editable for tabl e
    public boolean isCellEditable(int row, int column) {
        return tblOpening.getModel().isCellEditable(row, column);
    }

    private void initialDefault() {
        // startDate = txtDate.setDate(Util1.toDate(Global.startDate));
        endDate = startDate;
        dept = "-";
        accCode = "-";
        currency = "-";
        ref = "-";
        tranSource = "-";
        traderCode = "-";
    }

    private void initializeparameter() {
        // startDate = Util1.toDate(Global.startDate);
        currency = currencyAutoCompleter.getCurrency().getCurCode();
        department = departmenttAutoCompleter.getListOption();
        ChartOfAccount coa = coaAutoCompleter.getCOA();
        coaLv1 = Util1.getInteger(coa.getCoaLevel()) == 1 ? coa.getKey().getCoaCode() : "-";
        coaLv2 = Util1.getInteger(coa.getCoaLevel()) == 2 ? coa.getKey().getCoaCode() : "-";
        accCode = Util1.getInteger(coa.getCoaLevel()) == 3 ? coa.getKey().getCoaCode() : "-";
    }

    // search openig balance with filtered data for table
    private void searchOpening() {
        progress.setIndeterminate(true);
        initializeparameter();
        ReportFilter filter = new ReportFilter(Global.compCode, Global.macId);
        // filter.setOpeningDate(Util1.toDateStr(txtDate.getDate(), "yyyy-MM-dd"));
        filter.setCurCode(currency);
        filter.setDeptCode(departmenttAutoCompleter.getDepartment().getKey().getDeptCode());
        filter.setCoaLv1(coaLv1);
        filter.setCoaLv2(coaLv2);
        filter.setAcc(accCode);

        if (chkCus.isSelected()) {
            filter.setTraderType("CUS");
        } else if (chkSup.isSelected()) {
            filter.setTraderType("SUP");
        } else {
            filter.setTraderType("-");
        }

        Mono<ResponseEntity<List<OpeningBalance>>> result = accountApi.post()
                .uri("/account/get-opening")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .toEntityList(OpeningBalance.class);
        result.subscribe((t) -> {

            List<OpeningBalance> listop = t.getBody();
            openingTableModel.setListOpening(listop);
            btnGenerateZero.setEnabled(listop.isEmpty());
            openingTableModel.addNewRow();
            calTotalAmt(listop);
            progress.setIndeterminate(false);
        });
    }

    // calculate dr-amt and cr-amt and total
    private void calTotalAmt(List<OpeningBalance> listOpening) {
        double drAmt = 0.0;
        double crAmt = 0.0;
        for (OpeningBalance opening : listOpening) {
            drAmt += Util1.getDouble(opening.getDrAmt());
            crAmt += Util1.getDouble(opening.getCrAmt());
        }
        txtFDrAmt.setValue(drAmt);
        txtFCrAmt.setValue(crAmt);
        double op = drAmt - crAmt;
        txtFOB.setValue(op);
        lblCount.setText(listOpening.size() - 1 + "");
    }

    private void printOpening() {
        progress.setIndeterminate(true);
        taskExecutor.execute(() -> {
            try {
                String reportOpDate = Util1.toDateStr(txtDate.getDate(), "yyyy-MM-dd");
                Map<String, Object> parameters = new HashMap();
                parameters.put("p_report_name", this.getName());
                parameters.put("p_report_info","Opening Date - " + Util1.toDateStr(reportOpDate, "yyyy-MM-dd", "dd/MM/yyyy"));
                parameters.put("p_op_date", reportOpDate);
                parameters.put("p_dept_code", departmenttAutoCompleter.getListOption());
                parameters.put("p_dept_name", "Dept : " + departmenttAutoCompleter.getDepartment().getDeptName());
                parameters.put("p_print_date", Util1.getTodayDateTime());
                String filePath = String.format(Global.accountRP + "Opening.jasper");
                InputStream input = new FileInputStream(new File(path.concat(".json")));
                JsonDataSource ds = new JsonDataSource(input);
                JasperPrint js = JasperFillManager.fillReport(filePath, parameters, ds);
                JasperViewer.viewReport(js, false);
                progress.setIndeterminate(false);

            } catch (Exception e) {
                progress.setIndeterminate(false);
                log.error("printVoucher : " + e.getMessage());
            }
        });
        String reportPath = "report";
        // String filePath=reportPath
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblOpening = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        btnGenerateZero = new javax.swing.JButton();
        txtDept = new javax.swing.JTextField();
        txtCOA = new javax.swing.JTextField();
        chkCus = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        txtCurrency = new javax.swing.JTextField();
        chkSup = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();
        txtDate = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        lblCount = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtFDrAmt = new javax.swing.JFormattedTextField();
        txtFCrAmt = new javax.swing.JFormattedTextField();
        txtFOB = new javax.swing.JFormattedTextField();

        jScrollPane2.setViewportView(jEditorPane1);

        tblOpening.setAutoCreateRowSorter(true);
        tblOpening.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {

                },
                new String[] {

                }));
        tblOpening.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tblOpening.setGridColor(new java.awt.Color(204, 204, 204));
        tblOpening.setName("tblOpening"); // NOI18N
        jScrollPane1.setViewportView(tblOpening);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Date");

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("COA");

        btnGenerateZero.setText("Generate Zero");

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
        txtDate.setFont(Global.textFont);
        txtDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDateFocusGained(evt);
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
                                .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, 108,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtDept, javax.swing.GroupLayout.PREFERRED_SIZE, 152,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, 152,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCOA, javax.swing.GroupLayout.PREFERRED_SIZE, 150,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(chkCus)
                                .addGap(18, 18, 18)
                                .addComponent(chkSup)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnGenerateZero)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(txtDate, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGroup(jPanel2Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(txtDept, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(txtCurrency,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(txtCOA, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jLabel1)
                                                        .addComponent(jLabel2)
                                                        .addComponent(jLabel3)
                                                        .addComponent(jLabel5)
                                                        .addComponent(chkSup)
                                                        .addComponent(chkCus)
                                                        .addComponent(btnGenerateZero))
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                .addContainerGap()));

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL,
                new java.awt.Component[] { txtCOA, txtCurrency, txtDept });

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Total Count :");

        lblCount.setFont(Global.lableFont);
        lblCount.setText("0");

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Dr-Amt");

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Cr-Amt");

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Out Of Balance");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane1)
                                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(6, 6, 6)
                                                .addComponent(jLabel4)
                                                .addGap(84, 84, 84)
                                                .addComponent(lblCount)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabel7)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(txtFDrAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 157,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addGroup(layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING,
                                                                false)
                                                        .addComponent(jLabel9)
                                                        .addComponent(jLabel8))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(txtFOB, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                157, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(txtFCrAmt,
                                                                javax.swing.GroupLayout.Alignment.TRAILING,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE, 157,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addContainerGap()));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 317,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel4)
                                        .addComponent(lblCount)
                                        .addComponent(jLabel7)
                                        .addComponent(jLabel8)
                                        .addComponent(txtFDrAmt, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtFCrAmt, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel9)
                                        .addComponent(txtFOB, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
    }// </editor-fold>//GEN-END:initComponents

    private void chkCusActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_chkCusActionPerformed
         //   evt.set
        searchOpening();
    }// GEN-LAST:event_chkCusActionPerformed

    private void chkSupActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_chkSupActionPerformed
        
        searchOpening();
    }// GEN-LAST:event_chkSupActionPerformed

    private void txtDateFocusGained(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_txtDateFocusGained
        // TODO add your handling code here:
    }// GEN-LAST:event_txtDateFocusGained

    // Variables declaration - do not modify
    private javax.swing.JButton btnGenerateZero;
    private javax.swing.JCheckBox chkCus;
    private javax.swing.JCheckBox chkSup;
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblCount;
    private javax.swing.JTable tblOpening;
    private javax.swing.JTextField txtCOA;
    private javax.swing.JTextField txtCurrency;
    private com.toedter.calendar.JDateChooser txtDate;
    private javax.swing.JTextField txtDept;
    private javax.swing.JFormattedTextField txtFCrAmt;
    private javax.swing.JFormattedTextField txtFDrAmt;
    private javax.swing.JFormattedTextField txtFOB;
    // End of variables declaration

    // setting selected object for data call
    @Override
    public void selected(Object source, Object selectObj) {   
        if (selectObj != null) {
            searchOpening();
        }
    }

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
    public void keyEvent(KeyEvent e) {

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

    }

    @Override
    public String panelName() {
        return this.getName();
    }

}
