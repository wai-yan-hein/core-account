/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.report;

import com.acc.common.AccountRepo;
import com.acc.common.DateAutoCompleter;
import com.acc.editor.DepartmentAutoCompleter;
import com.acc.editor.TraderAAutoCompleter;
import com.acc.model.Department;
import com.common.FilterObject;
import com.common.FontCellRender;
import com.common.Global;
import com.common.PanelControl;
import com.common.ProUtil;
import com.common.ReportFilter;
import com.common.ReturnObject;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.common.Util1;
import com.inventory.model.VRoleMenu;
import com.inventory.ui.common.ReportTableModel;
import com.user.common.UserRepo;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.swing.JRViewer;
import org.jfree.ui.NumberCellRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 *
 * @author Lenovo
 */
@Component
@Slf4j
public class FinancialReport extends javax.swing.JPanel implements PanelControl, SelectionObserver {

    private final ReportTableModel tableModel = new ReportTableModel("Fanancial Report");
    @Autowired
    private WebClient accountApi;
    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private UserRepo userRepo;
    private boolean isReport = false;
    private TraderAAutoCompleter traderAutoCompleter;
    private DateAutoCompleter dateAutoCompleter;
    private DepartmentAutoCompleter departmentAutoCompleter;
    private ReportFilter filter;
    private SelectionObserver observer;
    private JProgressBar progress;
    private TableRowSorter<TableModel> sorter;

    public SelectionObserver getObserver() {
        return observer;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public JProgressBar getProgress() {
        return progress;
    }

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }

    /**
     * Creates new form Reports
     */
    public FinancialReport() {
        initComponents();
    }

    public void initMain() {
        initTableReport();
        initCombo();
    }

    private void initTableReport() {
        tblReport.setModel(tableModel);
        tblReport.getTableHeader().setFont(Global.tblHeaderFont);
        tblReport.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblReport.setDefaultRenderer(Object.class, new TableCellRender());
        tblReport.getColumnModel().getColumn(0).setPreferredWidth(30);
        tblReport.getColumnModel().getColumn(1).setPreferredWidth(900);
        tblReport.getColumnModel().getColumn(0).setCellRenderer(new FontCellRender());
        sorter = new TableRowSorter(tblReport.getModel());
        tblReport.setRowSorter(sorter);
        getReport();
    }

    private void getReport() {
        tableModel.setListReport(userRepo.getReport("Account"));
        lblRecord.setText(String.valueOf(tableModel.getListReport().size()));
    }

    private void initCombo() {
        dateAutoCompleter = new DateAutoCompleter(txtDate, Global.listDate);
        dateAutoCompleter.setSelectionObserver(this);
        traderAutoCompleter = new TraderAAutoCompleter(txtTrader, accountRepo, null, true);
        traderAutoCompleter.setSelectionObserver(this);
        departmentAutoCompleter = new DepartmentAutoCompleter(txtDep, accountRepo.getDepartment(), null, true, true);
        departmentAutoCompleter.setObserver(this);
    }

    private void report() {
        int row = tblReport.getSelectedRow();
        if (row >= 0) {
            int selectRow = tblReport.convertRowIndexToModel(row);
            VRoleMenu report = tableModel.getReport(selectRow);
            String reportName = report.getMenuName();
            String reportUrl = report.getMenuUrl();
            if (isValidReport(reportUrl)) {
                if (!isReport) {
                    progress.setIndeterminate(true);
                    isReport = true;
                    String stDate = dateAutoCompleter.getStDate();
                    String enDate = dateAutoCompleter.getEndDate();
                    filter = new ReportFilter(Global.macId, Global.compCode, Global.deptId);
                    filter.setOpDate(Util1.toDateStr(Global.startDate, "dd/MM/yyyy", "yyyy-MM-dd"));
                    filter.setFromDate(Util1.toDateStr(stDate, "dd/MM/yyyy", "yyyy-MM-dd"));
                    filter.setToDate(Util1.toDateStr(enDate, "dd/MM/yyyy", "yyyy-MM-dd"));
                    filter.setTraderCode(traderAutoCompleter.getTrader().getKey().getCode());
                    filter.setListDepartment(departmentAutoCompleter.getListOption());
                    filter.setIncomeExpenseProcess(ProUtil.getIncomeExpenseProcess());
                    filter.setPlProcess(ProUtil.getPLProcess());
                    filter.setBsProcess(ProUtil.getBalanceSheetProcess());
                    filter.setInvGroup(ProUtil.getInvGroup());
                    filter.setCoaCode(traderAutoCompleter.getTrader().getAccCode());
                    log.info("Report Date : " + stDate + " - " + enDate);
                    Map<String, Object> param = new HashMap<>();
                    param.put("p_report_name", reportName);
                    param.put("p_date", String.format("Between %s and %s", stDate, enDate));
                    param.put("p_print_date", Util1.getTodayDateTime());
                    param.put("p_comp_name", Global.companyName);
                    param.put("p_comp_address", Global.companyAddress);
                    param.put("p_comp_phone", Global.companyPhone);
                    param.put("p_currency", Global.currency);
                    printReport(reportUrl, reportUrl, param);
                }
                isReport = false;
            }
        } else {
            isReport = false;
            progress.setIndeterminate(false);
            JOptionPane.showMessageDialog(Global.parentForm, "Choose Report.");
        }
    }

    private boolean isValidReport(String url) {
        if (url.equals("CreditDetail")) {
            if (traderAutoCompleter.getTrader().getKey().getCode().equals("-")) {
                JOptionPane.showMessageDialog(this, "Please select Trader.", "Report Validation", JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
        }
        return true;
    }

    private void printReport(String reportUrl, String reportName, Map<String, Object> param) {
        filter.setReportName(reportName);
        long start = new GregorianCalendar().getTimeInMillis();

        Mono<ReturnObject> result = accountApi
                .post()
                .uri("/report/get-report")
                .body(Mono.just(filter), FilterObject.class
                )
                .retrieve()
                .bodyToMono(ReturnObject.class
                );
        result.subscribe((t) -> {
            try {
                if (t != null) {
                    log.info(String.format("printReport %s", t.getMessage()));
                    String filePath = String.format("%s%s%s", Global.accountRP, File.separator, reportUrl.concat(".jasper"));
                    if (t.getFile().length > 0) {
                        long end = new GregorianCalendar().getTimeInMillis();
                        long pt = end - start;
                        lblTime.setText(pt / 1000 + " s");
                        JasperReportsContext jc = DefaultJasperReportsContext.getInstance();
                        jc.setProperty("net.sf.jasperreports.default.font.name", Global.fontName);
                        jc.setProperty("net.sf.jasperreports.default.pdf.font.name", Global.fontName);
                        jc.setProperty("net.sf.jasperreports.default.pdf.encoding", "Identity-H");
                        jc.setProperty("net.sf.jasperreports.default.pdf.embedded", "true");
                        jc.setProperty("net.sf.jasperreports.viewer.zoom", "1");
                        InputStream input = new ByteArrayInputStream(t.getFile());
                        JsonDataSource ds = new JsonDataSource(input);
                        param.put("p_gross_profit", t.getGrossProfit());
                        param.put("p_profit", t.getNetProfit());
                        param.put("p_cos_pc", t.getCosPercent());
                        param.put("p_gp_pc", t.getGpPercent());
                        param.put("p_np_pc", t.getNpPercent());
                        param.put("p_opening", t.getOpAmt());
                        param.put("p_closing", t.getClAmt());
                        param.put("p_trader_name", txtTrader.getText());
                        param.put("p_trader_type", traderAutoCompleter.getTrader().getTraderType());
                        JasperPrint js = JasperFillManager.fillReport(filePath, param, ds);
                        JRViewer viwer = new JRViewer(js);
                        JFrame frame = new JFrame("Core Value Report");
                        frame.getContentPane().add(viwer);
                        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        frame.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(this, "Report Does Not Exists.");
                    }
                }
                progress.setIndeterminate(false);
            } catch (JRException ex) {
                log.error("printVoucher : " + ex.getMessage());
                progress.setIndeterminate(false);
                JOptionPane.showMessageDialog(Global.parentForm, ex.getMessage());
            }
        }, (e) -> {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
            progress.setIndeterminate(false);
        });

    }
    private final RowFilter<Object, Object> startsWithFilter = new RowFilter<Object, Object>() {
        @Override
        public boolean include(RowFilter.Entry<? extends Object, ? extends Object> entry) {
            String tmp1 = entry.getStringValue(0).toUpperCase().replace(" ", "");
            String text = txtFilter.getText().toUpperCase().replace(" ", "");
            return tmp1.startsWith(text);
        }
    };

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblReport = new javax.swing.JTable();
        jLabel12 = new javax.swing.JLabel();
        lblRecord = new javax.swing.JLabel();
        txtFilter = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        txtDate = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtTrader = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txtDep = new javax.swing.JTextField();
        lable2 = new javax.swing.JLabel();
        lblTime = new javax.swing.JLabel();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        tblReport.setFont(Global.textFont);
        tblReport.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblReport.setRowHeight(26);
        jScrollPane1.setViewportView(tblReport);

        jLabel12.setFont(Global.lableFont);
        jLabel12.setText("Record :");

        lblRecord.setFont(Global.lableFont);
        lblRecord.setText("0");

        txtFilter.setFont(Global.textFont);
        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel14.setFont(Global.lableFont);
        jLabel14.setText("Date");

        txtDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDateFocusGained(evt);
            }
        });
        txtDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDateKeyReleased(evt);
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Trader");

        txtTrader.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtTraderFocusGained(evt);
            }
        });
        txtTrader.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTraderKeyReleased(evt);
            }
        });

        jLabel15.setFont(Global.lableFont);
        jLabel15.setText("Department");

        txtDep.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDepFocusGained(evt);
            }
        });
        txtDep.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDepKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDate, javax.swing.GroupLayout.DEFAULT_SIZE, 406, Short.MAX_VALUE)
                    .addComponent(txtTrader, javax.swing.GroupLayout.DEFAULT_SIZE, 406, Short.MAX_VALUE)
                    .addComponent(txtDep, javax.swing.GroupLayout.DEFAULT_SIZE, 406, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtDate)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtDep)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTrader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lable2.setFont(Global.lableFont);
        lable2.setText("Report Time");

        lblTime.setFont(Global.lableFont);
        lblTime.setText("0");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblRecord, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lable2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblTime, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 468, Short.MAX_VALUE)
                    .addComponent(txtFilter))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 327, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(lblRecord)
                    .addComponent(lable2)
                    .addComponent(lblTime))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observer.selected("control", this);
    }//GEN-LAST:event_formComponentShown

    private void txtTraderKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTraderKeyReleased
        // TODO add your handling code here:

    }//GEN-LAST:event_txtTraderKeyReleased

    private void txtTraderFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTraderFocusGained
        // TODO add your handling code here:
        txtTrader.selectAll();
    }//GEN-LAST:event_txtTraderFocusGained

    private void txtDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDateFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDateFocusGained

    private void txtDateKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDateKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDateKeyReleased

    private void txtFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyReleased
        // TODO add your handling code here:
        String f = txtFilter.getText();
        if (f.length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(startsWithFilter);
        }
    }//GEN-LAST:event_txtFilterKeyReleased

    private void txtDepFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDepFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDepFocusGained

    private void txtDepKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDepKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDepKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lable2;
    private javax.swing.JLabel lblRecord;
    private javax.swing.JLabel lblTime;
    private javax.swing.JTable tblReport;
    private javax.swing.JTextField txtDate;
    private javax.swing.JTextField txtDep;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JTextField txtTrader;
    // End of variables declaration//GEN-END:variables

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
        report();
    }

    @Override
    public void refresh() {
        getReport();
    }

    @Override
    public void filter() {
    }

    @Override
    public String panelName() {
        return this.getName();
    }

    @Override
    public void selected(Object source, Object selectObj) {

    }

}