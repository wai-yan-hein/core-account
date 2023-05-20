/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.report;

import com.acc.dialog.TrialBalanceDetailDialog;
import com.acc.common.AccountRepo;
import com.acc.common.DateAutoCompleter;
import com.acc.common.DateTableDecorator;
import com.acc.common.GLListingTableModel;
import com.acc.common.GLTableCellRender;
import com.acc.editor.COA3AutoCompleter;
import com.acc.editor.DepartmentAutoCompleter;
import com.acc.model.ChartOfAccount;
import com.acc.model.ReportFilter;
import com.acc.model.VTriBalance;
import com.common.Global;
import com.common.PanelControl;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.common.Util1;
import com.user.common.UserRepo;
import com.user.editor.CurrencyAutoCompleter;
import com.user.editor.ProjectAutoCompleter;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import net.coderazzi.filters.gui.AutoChoices;
import net.coderazzi.filters.gui.TableFilterHeader;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.view.JasperViewer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Component
public class GLReport extends javax.swing.JPanel implements SelectionObserver,
        PanelControl, KeyListener {

    private int selectRow = -1;
    private static final Logger log = LoggerFactory.getLogger(GLReport.class);
    private DateAutoCompleter dateAutoCompleter;
    private ProjectAutoCompleter projectAutoCompleter;
    private String stDate, endDate;
    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private UserRepo userRepo;
    private TrialBalanceDetailDialog dialog;
    private DateTableDecorator decorator;

    /**
     * Creates new form AparGlReport
     */
    private final GLListingTableModel glListingTableModel = new GLListingTableModel();

    private COA3AutoCompleter cOAAutoCompleter;
    private SelectionObserver observer;
    private DepartmentAutoCompleter departmentAutoCompleter;
    private CurrencyAutoCompleter currencyAutoCompleter;
    private TableFilterHeader filterHeader;
    private boolean isGLCal = false;
    private JProgressBar progress;

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

    public GLReport() {
        initComponents();
        initKeyListener();
        initTextBox();
        initDateDecorator();
    }

    private final FocusAdapter fa = new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            if (e.getSource() instanceof JTextField txt) {
                txt.selectAll();
            }
        }
    };

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

    private void initTextBox() {
        txtOB.setFormatterFactory(Util1.getDecimalFormat());
    }

    private void initKeyListener() {
        txtDep.addKeyListener(this);
        txtCOA.addKeyListener(this);
        txtProjectNo.addKeyListener(this);
        txtDep.addFocusListener(fa);
        txtCOA.addFocusListener(fa);
        txtProjectNo.addFocusListener(fa);
    }

    public void initMain() {
        initCombo();
        initTableModel();
        initTable();
        assingDefaultValue();
        createDateFilter();
        searchGLListing();
    }

    private void assingDefaultValue() {
        txtOB.setFormatterFactory(Util1.getDecimalFormat());
        progress.setIndeterminate(false);
        txtCurrency.setEnabled(ProUtil.isMultiCur());
    }

    private void initTableModel() {
        tblGL.setModel(glListingTableModel);
        tblGL.setDefaultRenderer(Double.class, new GLTableCellRender(3, 4));
        tblGL.setDefaultRenderer(Object.class, new GLTableCellRender(3, 4));
    }

    private void initTable() {
        tblGL.getTableHeader().setFont(Global.lableFont);
        tblGL.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblGL.getColumnModel().getColumn(0).setPreferredWidth(20);
        tblGL.getColumnModel().getColumn(1).setPreferredWidth(400);
        tblGL.getColumnModel().getColumn(2).setPreferredWidth(1);
        tblGL.getColumnModel().getColumn(3).setPreferredWidth(50);
        tblGL.getColumnModel().getColumn(4).setPreferredWidth(50);
        tblGL.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                }
                if (e.getClickCount() == 2) {
                    if (tblGL.getSelectedRow() >= 0) {
                        selectRow = tblGL.convertRowIndexToModel(tblGL.getSelectedRow());
                        if (chkDetail.isSelected()) {
                            VTriBalance vtb = glListingTableModel.getTBAL(selectRow);
                            String coaCode = vtb.getCoaCode();
                            String coaName = vtb.getCoaName();
                            String curCode = vtb.getCurCode();
                            openTBDDialog(coaCode, curCode, coaName);
                        } else {
                            VTriBalance vtb = glListingTableModel.getTBAL(selectRow);
                            String coaCode = vtb.getCoaCode();
                            String coaName = vtb.getCoaName();
                            String curCode = vtb.getCurCode();
                            openTBDDialog(coaCode, curCode, coaName);
                        }
                    }
                }
            }
        });
        filterHeader = new TableFilterHeader(tblGL, AutoChoices.ENABLED);
        filterHeader.setPosition(TableFilterHeader.Position.TOP);
        filterHeader.setFont(Global.textFont);
        filterHeader.setVisible(false);
    }

    private String getCurCode() {
        if (currencyAutoCompleter == null || currencyAutoCompleter.getCurrency() == null) {
            return Global.currency;
        }
        return currencyAutoCompleter.getCurrency().getCurCode();
    }

    private List<String> getListDep() {
        return departmentAutoCompleter == null ? new ArrayList<>() : departmentAutoCompleter.getListOption();
    }

    private void searchGLListing() {
        if (!isGLCal) {
            long start = new GregorianCalendar().getTimeInMillis();
            progress.setIndeterminate(true);
            isGLCal = true;
            initTableModel();
            glListingTableModel.clear();
            String opDate = Util1.toDateStrMYSQL(Global.startDate, "dd/MM/yyyy");
            stDate = Util1.toDateStrMYSQL(dateAutoCompleter.getStDate(), "dd/MM/yyyy");
            endDate = Util1.toDateStrMYSQL(dateAutoCompleter.getEndDate(), "dd/MM/yyyy");
            ChartOfAccount coa = cOAAutoCompleter.getCOA();
            String coaLv1 = Util1.getInteger(coa.getCoaLevel()) == 1 ? coa.getKey().getCoaCode() : "-";
            String coaLv2 = Util1.getInteger(coa.getCoaLevel()) == 2 ? coa.getKey().getCoaCode() : "-";
            String coaLv3 = Util1.getInteger(coa.getCoaLevel()) == 3 ? coa.getKey().getCoaCode() : "-";
            ReportFilter filter = new ReportFilter(Global.compCode, Global.macId);
            filter.setCoaCode(coaLv3);
            filter.setCoaLv1(coaLv1);
            filter.setCoaLv2(coaLv2);
            filter.setFromDate(stDate);
            filter.setToDate(endDate);
            filter.setOpeningDate(opDate);
            filter.setClosing(chkClosing.isSelected());
            filter.setCurCode(getCurCode());
            filter.setListDepartment(getListDep());
            filter.setProjectNo(projectAutoCompleter.getProject().getKey().getProjectNo());
            decorator.refreshButton(filter.getFromDate());
            accountRepo.getTri(filter).subscribe((t) -> {
                glListingTableModel.setListTBAL(t);
                calGLTotlaAmount();
                if (chkActive.isSelected()) {
                    removeZero();
                }
                isGLCal = false;
                progress.setIndeterminate(false);
                long end = new GregorianCalendar().getTimeInMillis();
                long pt = end - start;
                lblCalTime.setText(pt / 1000 + " s");
                tblGL.requestFocus();
            }, (e) -> {
                tblGL.requestFocus();
                JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
                isGLCal = false;
                progress.setIndeterminate(false);
            });

        }

    }

    private void openTBDDialog(String coaCode, String curCode, String coaName) {
        if (dialog == null) {
            dialog = new TrialBalanceDetailDialog(Global.parentForm);
            dialog.setAccountRepo(accountRepo);
            dialog.setUserRepo(userRepo);
            dialog.initMain();
            dialog.setSize(Global.width - 50, Global.height - 50);
            dialog.setLocationRelativeTo(null);
        }
        dialog.setCoaCode(coaCode);
        dialog.setStDate(dateAutoCompleter.getStDate());
        dialog.setEndDate(dateAutoCompleter.getEndDate());
        dialog.setCurCode(curCode);
        dialog.setDesp(coaName);
        dialog.setTraderCode(null);
        dialog.setDepartment(departmentAutoCompleter.getListOption());
        dialog.initData();
        dialog.searchTriBalDetail();
        dialog.setVisible(true);
    }

    private void calGLTotlaAmount() {
        List<VTriBalance> listTB = glListingTableModel.getListTBAL();
        double ttlDrAmt = 0.0;
        double ttlCrAmt = 0.0;
        double ttlNet = 0.0;
        double outBal;
        for (VTriBalance tb : listTB) {
            ttlDrAmt += Util1.getDouble(tb.getDrAmt());
            ttlCrAmt += Util1.getDouble(tb.getCrAmt());
        }
        txtFTotalCrAmt.setValue(Util1.toFormatPattern(ttlCrAmt));
        txtFTotalDrAmt.setValue(Util1.toFormatPattern(ttlDrAmt));
        txtFNetChange.setValue(Util1.toFormatPattern(ttlNet));
        if (ttlDrAmt > ttlCrAmt) {
            outBal = ttlDrAmt - ttlCrAmt;
        } else {
            outBal = ttlCrAmt - ttlDrAmt;
        }
        txtOB.setValue(outBal);
    }

    private void initCombo() {
        dateAutoCompleter = new DateAutoCompleter(txtDate);
        dateAutoCompleter.setSelectionObserver(this);
        cOAAutoCompleter = new COA3AutoCompleter(txtCOA, accountRepo, null, true, 0);
        cOAAutoCompleter.setSelectionObserver(this);
        projectAutoCompleter = new ProjectAutoCompleter(txtProjectNo, userRepo, null, true);
        projectAutoCompleter.setObserver(this);
        accountRepo.getDepartment().subscribe((t) -> {
            departmentAutoCompleter = new DepartmentAutoCompleter(txtDep, t, null, true, true);
            departmentAutoCompleter.setObserver(this);
        });
        userRepo.getCurrency().subscribe((t) -> {
            currencyAutoCompleter = new CurrencyAutoCompleter(txtCurrency, t, null);
            currencyAutoCompleter.setObserver(this);
            userRepo.findCurrency(Global.currency).subscribe((c) -> {
                currencyAutoCompleter.setCurrency(c);
            });
        });
    }

    private void printGLListing() {
        try {
            progress.setIndeterminate(true);
            String path = "temp/Tri" + Global.macId + ".json";
            Util1.writeJsonFile(glListingTableModel.getListTBAL(), path);
            Map<String, Object> p = new HashMap();
            p.put("p_report_name", "Trial Balance");
            p.put("p_date", String.format("Between %s and %s", dateAutoCompleter.getStDate(), dateAutoCompleter.getEndDate()));
            p.put("p_print_date", Util1.getTodayDateTime());
            p.put("p_comp_name", Global.companyName);
            p.put("p_comp_address", Global.companyAddress);
            p.put("p_comp_phone", Global.companyPhone);
            p.put("p_currency", currencyAutoCompleter.getCurrency().getCurCode());
            p.put("p_department", txtDep.getText());
            Util1.initJasperContext();
            JsonDataSource ds = new JsonDataSource(new File(path)) {
            };
            JasperPrint js = JasperFillManager.fillReport(Global.accountRP + "TriBalance.jasper", p, ds);
            JasperViewer.viewReport(js, false);
            progress.setIndeterminate(false);
        } catch (FileNotFoundException | JRException ex) {
            progress.setIndeterminate(false);
            JOptionPane.showMessageDialog(Global.parentForm, "Report", ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            log.error("printGLListing : " + ex.getMessage());
        } catch (IOException ex) {
            log.error("printGLListing : " + ex.getMessage());
        }
    }

    public void clear() {
        txtCurrency.setText(null);
        txtDate.setText(null);
        txtDep.setText(null);
        txtCOA.setText(null);
    }

    private void removeZero() {
        List<VTriBalance> listTBAL = glListingTableModel.getListTBAL();
        if (!listTBAL.isEmpty()) {
            listTBAL.removeIf((e) -> Util1.getDouble(e.getDrAmt()) + Util1.getDouble(e.getCrAmt()) == 0);
            glListingTableModel.setListTBAL(listTBAL);
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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtDate = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtDep = new javax.swing.JTextField();
        txtCOA = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtCurrency = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtProjectNo = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblGL = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        txtFTotalDrAmt = new javax.swing.JFormattedTextField();
        txtFTotalCrAmt = new javax.swing.JFormattedTextField();
        txtFNetChange = new javax.swing.JFormattedTextField();
        txtOB = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lblCalTime = new javax.swing.JLabel();
        panelDate = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        chkClosing = new javax.swing.JCheckBox();
        chkActive = new javax.swing.JCheckBox();
        chkDetail = new javax.swing.JCheckBox();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Date");

        txtDate.setFont(Global.lableFont);
        txtDate.setName("txtDate"); // NOI18N

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Department");

        txtDep.setFont(Global.textFont);
        txtDep.setName("txtDep"); // NOI18N
        txtDep.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDepFocusGained(evt);
            }
        });
        txtDep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDepActionPerformed(evt);
            }
        });

        txtCOA.setFont(Global.textFont);
        txtCOA.setName("txtCOA"); // NOI18N
        txtCOA.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCOAFocusGained(evt);
            }
        });
        txtCOA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCOAActionPerformed(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("COA");

        txtCurrency.setFont(Global.lableFont);
        txtCurrency.setToolTipText("");
        txtCurrency.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCurrency.setEnabled(false);
        txtCurrency.setName("txtCurrency"); // NOI18N
        txtCurrency.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCurrencyActionPerformed(evt);
            }
        });

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Currency");

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Project No");

        txtProjectNo.setFont(Global.textFont);
        txtProjectNo.setName("txtCOA"); // NOI18N
        txtProjectNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtProjectNoFocusGained(evt);
            }
        });
        txtProjectNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtProjectNoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(txtDate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(txtDep)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(txtCOA)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addComponent(txtProjectNo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(txtCurrency)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtDep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCOA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel7)
                    .addComponent(txtProjectNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tblGL.setFont(Global.textFont);
        tblGL.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblGL.setRowHeight(Global.tblRowHeight);
        tblGL.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblGLKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblGL);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        txtFTotalDrAmt.setEditable(false);
        txtFTotalDrAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFTotalDrAmt.setFont(Global.amtFont);

        txtFTotalCrAmt.setEditable(false);
        txtFTotalCrAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFTotalCrAmt.setFont(Global.amtFont);

        txtFNetChange.setEditable(false);
        txtFNetChange.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFNetChange.setFont(Global.amtFont);

        txtOB.setEditable(false);
        txtOB.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtOB.setFont(Global.amtFont);

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Out Of Balance");

        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Cal Time : ");

        lblCalTime.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lblCalTime.setText("0");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblCalTime, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 367, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(txtFTotalDrAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtFTotalCrAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtFNetChange, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(txtOB))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFTotalDrAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFTotalCrAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFNetChange, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(lblCalTime))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtOB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
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
            .addGap(0, 34, Short.MAX_VALUE)
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        chkClosing.setFont(Global.lableFont);
        chkClosing.setSelected(true);
        chkClosing.setText("Net Change");
        chkClosing.setBorderPaintedFlat(true);
        chkClosing.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkClosingActionPerformed(evt);
            }
        });

        chkActive.setFont(Global.lableFont);
        chkActive.setSelected(true);
        chkActive.setText("Zero");
        chkActive.setBorderPaintedFlat(true);
        chkActive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkActiveActionPerformed(evt);
            }
        });

        chkDetail.setFont(Global.lableFont);
        chkDetail.setText("Currency Convert");
        chkDetail.setBorderPaintedFlat(true);
        chkDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkDetailActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkClosing)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkActive)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkDetail)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(chkClosing, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(chkActive, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkDetail, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jPanel3, panelDate});

    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observer.selected("control", this);
    }//GEN-LAST:event_formComponentShown

    private void txtDepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDepActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDepActionPerformed

    private void txtCOAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCOAActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCOAActionPerformed

    private void txtCurrencyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCurrencyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCurrencyActionPerformed

    private void txtDepFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDepFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDepFocusGained

    private void txtCOAFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCOAFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCOAFocusGained

    private void tblGLKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblGLKeyReleased

    }//GEN-LAST:event_tblGLKeyReleased

    private void chkClosingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkClosingActionPerformed
        // TODO add your handling code here:
        searchGLListing();
    }//GEN-LAST:event_chkClosingActionPerformed

    private void chkActiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkActiveActionPerformed
        // TODO add your handling code here:
        if (chkActive.isSelected()) {
            removeZero();
        } else {
            searchGLListing();
        }
    }//GEN-LAST:event_chkActiveActionPerformed

    private void chkDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkDetailActionPerformed
        // TODO add your handling code here:
        searchGLListing();
    }//GEN-LAST:event_chkDetailActionPerformed

    private void txtProjectNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtProjectNoFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtProjectNoFocusGained

    private void txtProjectNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProjectNoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtProjectNoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkActive;
    private javax.swing.JCheckBox chkClosing;
    private javax.swing.JCheckBox chkDetail;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCalTime;
    private javax.swing.JPanel panelDate;
    private javax.swing.JTable tblGL;
    private javax.swing.JTextField txtCOA;
    private javax.swing.JTextField txtCurrency;
    private javax.swing.JTextField txtDate;
    private javax.swing.JTextField txtDep;
    private javax.swing.JFormattedTextField txtFNetChange;
    private javax.swing.JFormattedTextField txtFTotalCrAmt;
    private javax.swing.JFormattedTextField txtFTotalDrAmt;
    private javax.swing.JFormattedTextField txtOB;
    private javax.swing.JTextField txtProjectNo;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {
        if (source != null) {
            if (source.equals("Date-Search")) {
                String date = Util1.toDateStr(selectObj.toString(), "yyyy-MM-dd", Global.dateFormat);
                dateAutoCompleter.setStDate(date);
                dateAutoCompleter.setEndDate(date);
                txtDate.setText(date);
            }
            searchGLListing();
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
        printGLListing();
    }

    @Override
    public void refresh() {
        searchGLListing();
    }

    @Override
    public void filter() {
        filterHeader.setVisible(!filterHeader.isVisible());
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
    }

    @Override
    public void keyReleased(KeyEvent e) {
        Object source = e.getSource();
        String ctrlName = "-";
        if (source instanceof JTextField) {
            ctrlName = ((JTextField) source).getName();
        }
    }
}
