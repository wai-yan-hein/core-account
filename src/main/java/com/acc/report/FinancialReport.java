/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.report;

import com.acc.dialog.COAOptionDialog;
import com.acc.dialog.FindDialog;
import com.repo.AccountRepo;
import com.acc.editor.DateAutoCompleter;
import com.acc.editor.COA3AutoCompleter;
import com.acc.editor.DepartmentAutoCompleter;
import com.acc.editor.TraderAAutoCompleter;
import com.acc.model.TraderA;
import com.common.ComponentUtil;
import com.common.Global;
import com.common.PanelControl;
import com.common.ProUtil;
import com.common.ReportFilter;
import com.common.RowHeader;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.common.Util1;
import com.inventory.ui.common.ReportTableModel;
import com.repo.UserRepo;
import com.user.editor.ProjectAutoCompleter;
import com.user.model.Menu;
import java.awt.Dimension;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.ListSelectionModel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.swing.JRViewer;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class FinancialReport extends javax.swing.JPanel implements PanelControl, SelectionObserver {

    private final ReportTableModel tableModel = new ReportTableModel("Financial Report");
    @Setter
    private AccountRepo accountRepo;
    @Setter
    private UserRepo userRepo;
    @Setter
    private SelectionObserver observer;
    @Setter
    private JProgressBar progress;
    private boolean isReport = false;
    private TraderAAutoCompleter traderAutoCompleter;
    private DateAutoCompleter dateAutoCompleter;
    private DepartmentAutoCompleter departmentAutoCompleter;
    private COA3AutoCompleter cOA3AutoCompleter;
    private ProjectAutoCompleter projectAutoCompleter;
    private ReportFilter filter;
    private COAOptionDialog dialog;
    private FindDialog findDialog;
    private int row = 0;

    /**
     * Creates new form Reports
     */
    public FinancialReport() {
        initComponents();
        ComponentUtil.addFocusListener(panelFilter);
    }

    public void initMain() {
        initTableReport();
        initRowHeader();
        initCombo();
        initFind();
        getReport();
    }

    private void initFind() {
        findDialog = new FindDialog(Global.parentForm, tblReport);
    }

    private void initRowHeader() {
        RowHeader header = new RowHeader();
        JList list = header.createRowHeader(tblReport, 30);
        scroll.setRowHeaderView(list);
    }

    private void initTableReport() {
        tblReport.setModel(tableModel);
        tblReport.getTableHeader().setFont(Global.tblHeaderFont);
        tblReport.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblReport.setDefaultRenderer(Object.class, new TableCellRender());
        tblReport.setDefaultRenderer(Boolean.class, new TableCellRender());
        tblReport.getColumnModel().getColumn(0).setPreferredWidth(400);
        tblReport.getColumnModel().getColumn(1).setPreferredWidth(50);
        tblReport.getSelectionModel().addListSelectionListener((e) -> {
            int selectRow = tblReport.getSelectedRow();
            if (selectRow >= 0) {
                row = tblReport.convertRowIndexToModel(selectRow);
            }
        });
    }

    private void getReport() {
        progress.setIndeterminate(true);
        userRepo.getReport("Account").doOnSuccess((t) -> {
            tableModel.setListReport(t);
            lblRecord.setText(String.valueOf(tableModel.getListReport().size()));
        }).doOnTerminate(() -> {
            progress.setIndeterminate(false);
            ComponentUtil.scrollTable(tblReport, row, 0);
        }).subscribe();

    }

    private void initCombo() {
        dateAutoCompleter = new DateAutoCompleter(txtDate);
        dateAutoCompleter.setObserver(this);
        traderAutoCompleter = new TraderAAutoCompleter(txtTrader, accountRepo, null, true);
        traderAutoCompleter.setObserver(this);
        departmentAutoCompleter = new DepartmentAutoCompleter(txtDep, null, true, true);
        departmentAutoCompleter.setObserver(this);
        accountRepo.getDepartment().doOnSuccess((t) -> {
            departmentAutoCompleter.setListDepartment(t);
        }).subscribe();
        cOA3AutoCompleter = new COA3AutoCompleter(txtCOA, accountRepo, null, true, 3);
        cOA3AutoCompleter.setObserver(this);
        projectAutoCompleter = new ProjectAutoCompleter(txtProjectNo, null, true);
        projectAutoCompleter.setObserver(this);
        userRepo.searchProject().doOnSuccess((t) -> {
            projectAutoCompleter.setListProject(t);
        }).subscribe();
    }

    private List<String> getDepartment() {
        return departmentAutoCompleter.getDepartment() == null ? null : departmentAutoCompleter.getListOption();
    }

    private void report() {
        if (row >= 0) {
            int selectRow = tblReport.convertRowIndexToModel(row);
            Menu report = tableModel.getReport(selectRow);
            String reportName = report.getMenuName();
            String reportUrl = report.getMenuUrl();
            if (isValidReport(reportUrl)) {
                if (!isReport) {
                    progress.setIndeterminate(true);
                    isReport = true;
                    String stDate = dateAutoCompleter.getDateModel().getStartDate();
                    String enDate = dateAutoCompleter.getDateModel().getEndDate();
                    filter = new ReportFilter(Global.macId, Global.compCode, Global.deptId);
                    filter.setFromDate(stDate);
                    filter.setToDate(enDate);
                    filter.setTraderCode(traderAutoCompleter.getTrader().getKey().getCode());
                    filter.setListDepartment(getDepartment());
                    filter.setCurCode(Global.currency);
                    filter.setFixedAcc(ProUtil.getProperty(ProUtil.FIXED));
                    filter.setCurrentAcc(ProUtil.getProperty(ProUtil.CURRENT));
                    filter.setCapitalAcc(ProUtil.getProperty(ProUtil.CAPITAL));
                    filter.setLiaAcc(ProUtil.getProperty(ProUtil.LIA));
                    filter.setIncomeAcc(ProUtil.getProperty(ProUtil.INCOME));
                    filter.setOtherIncomeAcc(ProUtil.getProperty(ProUtil.OTHER_INCOME));
                    filter.setPurchaseAcc(ProUtil.getProperty(ProUtil.PURCHASE));
                    filter.setExpenseAcc(ProUtil.getProperty(ProUtil.EXPENSE));
                    filter.setPlAcc(ProUtil.getProperty(ProUtil.PL));
                    filter.setReAcc(ProUtil.getProperty(ProUtil.RE));
                    filter.setInvGroup(ProUtil.getInvGroup());
                    filter.setSrcAcc(cOA3AutoCompleter.getCOA().getKey().getCoaCode());
                    filter.setCashGroup(ProUtil.getProperty(ProUtil.CASH_GROUP));
                    filter.setBankGroup(ProUtil.getProperty(ProUtil.BANK_GROUP));
                    filter.setListCOAGroup(dialog == null ? null : dialog.getSelectCOA());
                    log.info("Report Date : " + stDate + " - " + enDate);
                    Map<String, Object> param = new HashMap<>();
                    param.put("p_report_name", reportName);
                    param.put("p_report_title", cOA3AutoCompleter.getCOA().getCoaNameEng());
                    param.put("p_date", String.format("Between %s and %s", Util1.toDateStr(stDate, "yyyy-MM-dd", Global.dateFormat), Util1.toDateStr(enDate, "yyyy-MM-dd", Global.dateFormat)));
                    param.put("p_print_date", Util1.getTodayDateTime());
                    param.put("p_comp_name", Global.companyName);
                    param.put("p_comp_address", Global.companyAddress);
                    param.put("p_comp_phone", Global.companyPhone);
                    param.put("p_currency", Global.currency);
                    param.put("p_department", txtDep.getText());
                    param.put("p_logo_path", getLogoPath());
                    initTraderParameter(param);
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

    private String getLogoPath() {
        return String.format("images%s%s", File.separator, ProUtil.getProperty("logo.name"));
    }

    private boolean isValidReport(String url) {
        switch (url) {
            case "CreditDetail", "SharerHolderStatement" -> {
                if (traderAutoCompleter.getTrader().getKey().getCode().equals("-")) {
                    JOptionPane.showMessageDialog(this, "Please select Trader.", "Report Validation", JOptionPane.INFORMATION_MESSAGE);
                    txtTrader.requestFocus();
                    return false;
                }
            }
            case "IndividualStatement" -> {
                if (cOA3AutoCompleter.getCOA().getKey().getCoaCode().equals("-")) {
                    JOptionPane.showMessageDialog(this, "Please select COA.", "Report Validation", JOptionPane.INFORMATION_MESSAGE);
                    txtCOA.requestFocus();
                    return false;
                }
            }
        }
        return true;
    }

    private void printReport(String reportUrl, String reportName, Map<String, Object> param) {
        filter.setReportName(reportName);
        long start = new GregorianCalendar().getTimeInMillis();
        accountRepo.getReport(filter)
                .doOnSuccess((t) -> {
                    try {
                        if (t != null) {
                            String filePath = String.format("%s%s%s", Global.accountRP,
                                    File.separator, reportUrl.concat(".jasper"));
                            if (t.getFile() != null) {
                                long end = new GregorianCalendar().getTimeInMillis();
                                long pt = end - start;
                                lblTime.setText(pt / 1000 + " s");
                                InputStream input = new ByteArrayInputStream(t.getFile());
                                JsonDataSource ds = new JsonDataSource(input);
                                int dataCount = ds.recordCount();
                                if (dataCount > 0) {
                                    param.put("p_gross_profit", t.getGrossProfit());
                                    param.put("p_profit", t.getNetProfit());
                                    param.put("p_cos_pc", t.getCosPercent());
                                    param.put("p_gp_pc", t.getGpPercent());
                                    param.put("p_np_pc", t.getNpPercent());
                                    param.put("p_opening", t.getOpAmt());
                                    param.put("p_closing", t.getClAmt());
                                    param.put("p_op_date", t.getOpDate());
                                    JasperPrint js = JasperFillManager.fillReport(filePath, param, ds);
                                    JRViewer viwer = new JRViewer(js);
                                    JFrame frame = new JFrame("Core Value Report");
                                    frame.setIconImage(Global.parentForm.getIconImage());
                                    frame.getContentPane().add(viwer);
                                    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                                    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                                    frame.setMinimumSize(new Dimension(800, 600)); // Set minimum size here
                                    frame.setVisible(true);
                                } else {
                                    JOptionPane.showMessageDialog(this, "Sorry, there is no data available.");
                                }
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
                }).doOnError((e) -> {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
            progress.setIndeterminate(false);
        }).subscribe();

    }

    private void initTraderParameter(Map<String, Object> param) {
        TraderA t = traderAutoCompleter.getTrader();
        t = accountRepo.findTrader(t.getKey().getCode()).block();
        if (t != null) {
            param.put("p_trader_name", t.getTraderName());
            param.put("p_trader_type", t.getTraderType());
            param.put("p_remark", t.getRemark());
            param.put("p_nrc", t.getNrc());
            param.put("p_address", t.getAddress());
        }

    }

    private void coaGroup() {
        if (dialog == null) {
            dialog = new COAOptionDialog(Global.parentForm);
            dialog.setLocationRelativeTo(null);
            dialog.setAccountRepo(accountRepo);
            dialog.setObserver(this);
            dialog.initMain();
        }
        dialog.searchCOA();
    }

    private void clearFilter() {
        lblMessage.setText("");
        dateAutoCompleter.clear();
        departmentAutoCompleter.clear();
        traderAutoCompleter.clear();
        cOA3AutoCompleter.clear();
        projectAutoCompleter.clear();
    }

    private void setTraderCOA() {
        TraderA trader = traderAutoCompleter.getTrader();
        if (trader != null) {
            accountRepo.findCOA(trader.getAccount()).doOnSuccess((t) -> {
                cOA3AutoCompleter.setCoa(t);
            }).subscribe();
        }
    }

    private void observeMain() {
        observer.selected("control", this);
        observer.selected("save", false);
        observer.selected("print", true);
        observer.selected("history", false);
        observer.selected("delete", false);
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
        tblReport = new javax.swing.JTable();
        jLabel12 = new javax.swing.JLabel();
        lblRecord = new javax.swing.JLabel();
        panelFilter = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        txtDate = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtTrader = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txtDep = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtCOA = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtProjectNo = new javax.swing.JTextField();
        btnGroup = new javax.swing.JButton();
        lable2 = new javax.swing.JLabel();
        lblTime = new javax.swing.JLabel();
        lblMessage = new javax.swing.JLabel();

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
        tblReport.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblReportMouseClicked(evt);
            }
        });
        scroll.setViewportView(tblReport);

        jLabel12.setFont(Global.lableFont);
        jLabel12.setText("Record :");

        lblRecord.setFont(Global.lableFont);
        lblRecord.setText("0");

        panelFilter.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

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

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("COA");

        txtCOA.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCOAFocusGained(evt);
            }
        });
        txtCOA.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCOAKeyReleased(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Project");

        txtProjectNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtProjectNoFocusGained(evt);
            }
        });
        txtProjectNo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtProjectNoKeyReleased(evt);
            }
        });

        btnGroup.setBackground(Global.selectionColor);
        btnGroup.setFont(Global.lableFont);
        btnGroup.setForeground(new java.awt.Color(255, 255, 255));
        btnGroup.setText("COA Group");
        btnGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGroupActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelFilterLayout = new javax.swing.GroupLayout(panelFilter);
        panelFilter.setLayout(panelFilterLayout);
        panelFilterLayout.setHorizontalGroup(
            panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFilterLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDate, javax.swing.GroupLayout.DEFAULT_SIZE, 406, Short.MAX_VALUE)
                    .addComponent(txtTrader, javax.swing.GroupLayout.DEFAULT_SIZE, 406, Short.MAX_VALUE)
                    .addComponent(txtDep, javax.swing.GroupLayout.DEFAULT_SIZE, 406, Short.MAX_VALUE)
                    .addComponent(txtProjectNo, javax.swing.GroupLayout.DEFAULT_SIZE, 406, Short.MAX_VALUE)
                    .addGroup(panelFilterLayout.createSequentialGroup()
                        .addComponent(txtCOA, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnGroup)))
                .addContainerGap())
        );
        panelFilterLayout.setVerticalGroup(
            panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFilterLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtDate)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtDep)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTrader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCOA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGroup))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtProjectNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lable2.setFont(Global.lableFont);
        lable2.setText("Report Time");

        lblTime.setFont(Global.lableFont);
        lblTime.setText("0");

        lblMessage.setFont(Global.lableFont);
        lblMessage.setText("-");

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
                    .addComponent(scroll, javax.swing.GroupLayout.DEFAULT_SIZE, 468, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelFilter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblMessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblMessage)
                        .addGap(0, 248, Short.MAX_VALUE))
                    .addComponent(scroll))
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
        observeMain();
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

    private void txtDepFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDepFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDepFocusGained

    private void txtDepKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDepKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDepKeyReleased

    private void txtCOAFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCOAFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCOAFocusGained

    private void txtCOAKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCOAKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCOAKeyReleased

    private void txtProjectNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtProjectNoFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtProjectNoFocusGained

    private void txtProjectNoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtProjectNoKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtProjectNoKeyReleased

    private void btnGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGroupActionPerformed
        // TODO add your handling code here:
        coaGroup();
    }//GEN-LAST:event_btnGroupActionPerformed

    private void tblReportMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblReportMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tblReportMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGroup;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel lable2;
    private javax.swing.JLabel lblMessage;
    private javax.swing.JLabel lblRecord;
    private javax.swing.JLabel lblTime;
    private javax.swing.JPanel panelFilter;
    private javax.swing.JScrollPane scroll;
    private javax.swing.JTable tblReport;
    private javax.swing.JTextField txtCOA;
    private javax.swing.JTextField txtDate;
    private javax.swing.JTextField txtDep;
    private javax.swing.JTextField txtProjectNo;
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
        clearFilter();
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
        findDialog.setVisible(!findDialog.isVisible());
    }

    @Override
    public String panelName() {
        return this.getName();
    }

    @Override
    public void selected(Object source, Object selectObj) {
        if (source.equals("SELECT_GROUP")) {
            List<String> list = dialog.getSelectCOA();
            lblMessage.setText("Selected Group : " + list.size());
        } else if (source.equals("TRADER")) {
            setTraderCOA();
        }
    }

}
