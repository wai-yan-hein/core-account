/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.report;

import com.acc.common.AccountRepo;
import com.acc.common.CrAmtTableModel;
import com.acc.common.DateAutoCompleter;
import com.acc.common.DrAmtTableModel;
import com.acc.editor.DepartmentAutoCompleter;
import com.acc.model.ReportFilter;
import com.acc.model.Gl;
import com.common.Global;
import com.common.ProUtil;
import com.common.ReturnObject;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.common.Util1;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class TrialBalanceDetailDialog extends javax.swing.JDialog implements SelectionObserver {

    private final CrAmtTableModel crAmtTableModel = new CrAmtTableModel();
    private final DrAmtTableModel drAmtTableModel = new DrAmtTableModel();
    private TableRowSorter<TableModel> sorter;
    private String desp;
    private Double openingAmt = 0.0;
    private String coaCode;
    private String traderCode;
    private String stDate;
    private String endDate;
    private String curCode;
    private List<String> department;
    private WebClient accountApi;
    private AccountRepo accountRepo;
    private DateAutoCompleter dateAutoCompleter;
    private DepartmentAutoCompleter departmentAutoCompleter;
    private final Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();

    public AccountRepo getAccountRepo() {
        return accountRepo;
    }

    public void setAccountRepo(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }

    public WebClient getAccountApi() {
        return accountApi;
    }

    public void setAccountApi(WebClient accountApi) {
        this.accountApi = accountApi;
    }

    public List<String> getDepartment() {
        return department;
    }

    public void setDepartment(List<String> department) {
        this.department = department;
    }

    public String getStDate() {
        return stDate;
    }

    public void setStDate(String stDate) {
        this.stDate = stDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCurCode() {
        return curCode;
    }

    public void setCurCode(String curCode) {
        this.curCode = curCode;
        this.txtCur.setText(curCode);
    }

    public String getTraderCode() {
        return traderCode;
    }

    public void setTraderCode(String traderCode) {
        this.traderCode = traderCode;
    }

    public String getCoaCode() {
        return coaCode;
    }

    public void setCoaCode(String coaCode) {
        this.coaCode = coaCode;
    }

    public Double getOpeningAmt() {
        return openingAmt;
    }

    public void setOpeningAmt(Double openingAmt) {
        this.openingAmt = openingAmt;
    }

    public void setDesp(String desp) {
        this.desp = desp;
        lblName.setText(this.desp);
    }

    /**
     * Creates new form TrialBalanceDetailDialog
     */
    public TrialBalanceDetailDialog() {
        super(Global.parentForm, true);
        initComponents();
        initFormat();
    }

    private void initCombo() {
        dateAutoCompleter = new DateAutoCompleter(txtDate, Global.listDate);
        dateAutoCompleter.setSelectionObserver(this);
        departmentAutoCompleter = new DepartmentAutoCompleter(txtDep, accountRepo.getDepartment(), null, true, true);
        departmentAutoCompleter.setObserver(this);
    }

    private void initFormat() {
        txtOpening.setFormatterFactory(Util1.getDecimalFormat());
        txtClosing.setFormatterFactory(Util1.getDecimalFormat());
        txtDrAmt.setFormatterFactory(Util1.getDecimalFormat());
        txtCrAmt.setFormatterFactory(Util1.getDecimalFormat());
    }

    private void searchTriBalDetail() {
        progress.setIndeterminate(true);
        ReportFilter filter = new ReportFilter(Global.compCode, Global.macId);
        filter.setFromDate(stDate);
        filter.setToDate(endDate);
        filter.setSrcAcc(coaCode);
        filter.setCurCode(curCode);
        filter.setListDepartment(department);
        filter.setTraderCode(traderCode);
        Mono<ReturnObject> result = accountApi.post()
                .uri("/account/search-gl")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .bodyToMono(ReturnObject.class);
        result.subscribe((t) -> {
            try {
                String path = "temp/Ledger" + Global.macId;
                Util1.extractZipToJson(t.getFile(), path);
                Reader reader = Files.newBufferedReader(Paths.get(path.concat(".json")));
                List<Gl> listVGl = gson.fromJson(reader, new TypeToken<ArrayList<Gl>>() {
                }.getType());
                calculateOpeningClosing(listVGl);
                progress.setIndeterminate(false);
            } catch (JsonIOException | JsonSyntaxException | IOException e) {
                log.error("searchTriBalDetail : " + e.getMessage());
                progress.setIndeterminate(false);
            }
        }, (e) -> {
            JOptionPane.showMessageDialog(this, e.getMessage());
            progress.setIndeterminate(false);

        });
    }

    private void calculateOpeningClosing(List<Gl> listVGl) {
        double opAmt = Util1.getDouble(txtOpening.getValue());
        double ttlDrAmt = 0.0;
        double ttlCrAmt = 0.0;
        if (!listVGl.isEmpty()) {
            for (Gl vgl : listVGl) {
                double drAmt = Util1.getDouble(vgl.getDrAmt());
                double crAmt = Util1.getDouble(vgl.getCrAmt());
                if (drAmt > 0) {
                    ttlDrAmt += drAmt;
                    drAmtTableModel.addVGl(vgl);
                }
                if (crAmt > 0) {
                    ttlCrAmt += crAmt;
                    crAmtTableModel.addVGl(vgl);
                }
            }
            txtDrCount.setValue(drAmtTableModel.getListVGl().size());
            txtCrCount.setValue(crAmtTableModel.getListVGl().size());
            txtCrAmt.setValue(ttlCrAmt);
            txtDrAmt.setValue(ttlDrAmt);
        }
        txtOpening.setValue(opAmt);
        double closingAmt = opAmt + ttlDrAmt - ttlCrAmt;
        txtClosing.setValue(closingAmt);

    }

    public void initMain() {
        initTable();
        initCombo();
        initData();
        searchTriBalDetail();
    }

    private void initData() {
        String d1 = Util1.toDateStr(stDate, "yyyy-MM-dd", Global.dateFormat);
        String d2 = Util1.toDateStr(endDate, "yyyy-MM-dd", Global.dateFormat);
        txtDate.setText(String.format("%s to %s", d1, d2));
    }

    private void initTable() {
        tblCR();
        tblDR();
        clear();
    }

    private void clear() {
        txtDrAmt.setValue(0);
        txtCrAmt.setValue(0);
        txtOpening.setValue(0);
        txtClosing.setValue(0);
    }

    private void tblCR() {
        crAmtTableModel.clear();
        tblCr.setModel(crAmtTableModel);
        tblCr.getTableHeader().setFont(Global.lableFont);
        tblCr.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCr.setRowHeight(Global.tblRowHeight);
        tblCr.getColumnModel().getColumn(0).setPreferredWidth(40);
        tblCr.getColumnModel().getColumn(1).setPreferredWidth(5);
        tblCr.getColumnModel().getColumn(2).setPreferredWidth(170);
        tblCr.getColumnModel().getColumn(3).setPreferredWidth(170);
        tblCr.getColumnModel().getColumn(4).setPreferredWidth(100);
        tblCr.getColumnModel().getColumn(5).setPreferredWidth(50);
        tblCr.setDefaultRenderer(Double.class, new TableCellRender());
        tblCr.setDefaultRenderer(Object.class, new TableCellRender());
        sorter = new TableRowSorter<>(tblCr.getModel());
        tblCr.setRowSorter(sorter);

    }

    private void tblDR() {
        drAmtTableModel.clear();
        tblDr.setModel(drAmtTableModel);
        tblDr.setRowHeight(Global.tblRowHeight);
        tblDr.getTableHeader().setFont(Global.lableFont);
        tblDr.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblDr.getColumnModel().getColumn(0).setPreferredWidth(40);
        tblDr.getColumnModel().getColumn(1).setPreferredWidth(5);
        tblDr.getColumnModel().getColumn(2).setPreferredWidth(170);
        tblDr.getColumnModel().getColumn(3).setPreferredWidth(170);
        tblDr.getColumnModel().getColumn(4).setPreferredWidth(100);
        tblDr.getColumnModel().getColumn(5).setPreferredWidth(50);
        tblDr.setDefaultRenderer(Double.class, new TableCellRender());
        tblDr.setDefaultRenderer(Object.class, new TableCellRender());
        sorter = new TableRowSorter<>(tblDr.getModel());
        tblDr.setRowSorter(sorter);

    }

    private void print() {
        try {
            this.dispose();
            String compName = Global.companyName;
            String reportPath = ProUtil.getReportPath();
            String filePath = reportPath + File.separator + "TriBalanceDetail";
            Map<String, Object> p = new HashMap();
            p.put("p_company_name", compName);
            p.put("p_mac_id", Global.macId);
            p.put("p_comp_code", Global.compCode);
            p.put("p_report_info", lblName.getText());
            p.put("p_from_date", stDate);
            p.put("p_to_date", endDate);
            p.put("p_account_code", coaCode);
            p.put("p_cur_code", curCode);
            p.put("p_trader_code", Util1.isNull(traderCode, "-"));
            p.put("p_opening", txtOpening.getValue());
            p.put("p_closing", txtClosing.getValue());
        } catch (Exception ex) {
            log.error("print: " + ex.getMessage());
        }
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
        jPanel1 = new javax.swing.JPanel();
        lblName = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblDr = new javax.swing.JTable();
        txtClosing = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        txtOpening = new javax.swing.JFormattedTextField();
        txtDrAmt = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        txtDrCount = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblCr = new javax.swing.JTable();
        txtCrAmt = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtCrCount = new javax.swing.JFormattedTextField();
        jPanel3 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        txtDate = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtDep = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtCur = new javax.swing.JTextField();
        progress = new javax.swing.JProgressBar();

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Trial Balance");
        setBackground(new java.awt.Color(255, 255, 255));
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                formFocusLost(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblName.setFont(Global.menuFont);
        lblName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblName.setText("Name");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblName, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Closing Amt");

        tblDr.setFont(Global.textFont);
        tblDr.setModel(new javax.swing.table.DefaultTableModel(
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
        tblDr.setName("Debit"); // NOI18N
        tblDr.setRowHeight(Global.tblRowHeight);
        jScrollPane1.setViewportView(tblDr);

        txtClosing.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtClosing.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtClosing.setEnabled(false);
        txtClosing.setFont(Global.amtFont);

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Opening Amt");

        txtOpening.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtOpening.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtOpening.setEnabled(false);
        txtOpening.setFont(Global.amtFont);
        txtOpening.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtOpeningActionPerformed(evt);
            }
        });

        txtDrAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDrAmt.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtDrAmt.setEnabled(false);
        txtDrAmt.setFont(Global.amtFont);
        txtDrAmt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDrAmtActionPerformed(evt);
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Total Dr-Amt");

        txtDrCount.setEditable(false);
        txtDrCount.setBorder(null);
        txtDrCount.setFont(Global.lableFont);

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Records : ");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtOpening, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtClosing)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDrAmt, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
                            .addComponent(txtDrCount))))
                .addContainerGap())
        );

        jPanel4Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel5, jLabel6});

        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtOpening, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(txtDrAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(0, 1, Short.MAX_VALUE)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(txtClosing, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(txtDrCount, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        tblCr.setFont(Global.textFont);
        tblCr.setModel(new javax.swing.table.DefaultTableModel(
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
        tblCr.setName("Credit"); // NOI18N
        tblCr.setRowHeight(Global.tblRowHeight);
        jScrollPane2.setViewportView(tblCr);

        txtCrAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtCrAmt.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCrAmt.setEnabled(false);
        txtCrAmt.setFont(Global.amtFont);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Total Cr-Amt");

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Records : ");

        txtCrCount.setEditable(false);
        txtCrCount.setBorder(null);
        txtCrCount.setFont(Global.lableFont);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtCrCount)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCrAmt)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(txtCrAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtCrCount)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(37, 37, 37))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jButton1.setFont(Global.lableFont);
        jButton1.setText("Print");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Date");

        txtDate.setFont(Global.textFont);

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Department");

        txtDep.setFont(Global.textFont);

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Currency");

        txtCur.setEditable(false);
        txtCur.setFont(Global.textFont);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtDate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtDep)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtCur)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jLabel8)
                    .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(txtDep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(txtCur, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(progress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(progress, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
    }//GEN-LAST:event_formComponentShown

    private void formFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_formFocusLost

    private void txtOpeningActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtOpeningActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtOpeningActionPerformed

    private void txtDrAmtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDrAmtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDrAmtActionPerformed

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        // TODO add your handling code here:

    }//GEN-LAST:event_formKeyPressed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        print();
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
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
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblName;
    private javax.swing.JProgressBar progress;
    private javax.swing.JTable tblCr;
    private javax.swing.JTable tblDr;
    private javax.swing.JFormattedTextField txtClosing;
    private javax.swing.JFormattedTextField txtCrAmt;
    private javax.swing.JFormattedTextField txtCrCount;
    private javax.swing.JTextField txtCur;
    private javax.swing.JTextField txtDate;
    private javax.swing.JTextField txtDep;
    private javax.swing.JFormattedTextField txtDrAmt;
    private javax.swing.JFormattedTextField txtDrCount;
    private javax.swing.JFormattedTextField txtOpening;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {
    }

}
