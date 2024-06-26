/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.report;

import com.acc.dialog.TrialBalanceDetailDialog;
import com.acc.common.APARTableModel;
import com.repo.AccountRepo;
import com.acc.editor.DateAutoCompleter;
import com.acc.common.DateTableDecorator;
import com.acc.common.GLTableCellRender;
import com.acc.dialog.FindDialog;
import com.acc.editor.COAAutoCompleter;
import com.acc.editor.DepartmentAutoCompleter;
import com.acc.editor.TraderAAutoCompleter;
import com.acc.model.TraderA;
import com.acc.model.VApar;
import com.common.ComponentUtil;
import com.common.ExcelExporter;
import com.common.Global;
import com.common.PanelControl;
import com.common.ProUtil;
import com.common.ReportFilter;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.editor.RegionAutoCompleter;
import com.inventory.entity.Region;
import com.repo.InventoryRepo;
import com.repo.UserRepo;
import com.user.editor.CurrencyAutoCompleter;
import com.user.editor.ProjectAutoCompleter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
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
public class AparReport extends javax.swing.JPanel implements SelectionObserver,
        PanelControl, KeyListener {

    private int row = 0;
    /**
     * Creates new form AparReport
     */
    @Setter
    private AccountRepo accountRepo;
    @Setter
    private UserRepo userRepo;
    @Setter
    private InventoryRepo inventoryRepo;
    private final APARTableModel aPARTableModel = new APARTableModel();
    private DateAutoCompleter dateAutoCompleter;
    private CurrencyAutoCompleter currencyAutoCompleter;
    private DepartmentAutoCompleter departmentAutoCompleter;
    private TraderAAutoCompleter traderAutoCompleter;
    private COAAutoCompleter cOAAutoCompleter;
    private ProjectAutoCompleter projectAutoCompleter;
    private RegionAutoCompleter regionAutoCompleter;
    private boolean isApPrCal = false;
    @Setter
    private SelectionObserver observer;
    @Setter
    private JProgressBar progress;
    @Setter
    private TaskExecutor taskExecutor;
    private TrialBalanceDetailDialog dialog;
    private DateTableDecorator decorator;
    private final ExcelExporter exporter = new ExcelExporter();
    private FindDialog findDialog;

    public AparReport() {
        initComponents();
        initKeyListener();
        initTextBoxFormat();
        initDateDecorator();
    }

    private void assingDefaultValue() {
        ComponentUtil.setTextProperty(this);
        progress.setIndeterminate(false);
        txtCurrency.setEnabled(ProUtil.isMultiCur());
    }

    private void initDateDecorator() {
        decorator = DateTableDecorator.decorate(panelDate);
        decorator.setObserver(this);
    }

    private void initKeyListener() {
        txtDep.addKeyListener(this);
        txtPerson.addKeyListener(this);
        txtAccount.addKeyListener(this);
        txtProjectNo.addKeyListener(this);
        ComponentUtil.addFocusListener(this);
    }

    public void initMain() {
        assingDefaultValue();
        initCombo();
        initTable();
        initFind();
        createDateFilter();
        searchAPAR();
    }

    private void initFind() {
        findDialog = new FindDialog(Global.parentForm, tblAPAR);
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
        decorator.refreshButton(dateAutoCompleter.getDateModel().getStartDate());
    }

    private void initTextBoxFormat() {
        ComponentUtil.setTextProperty(this);
        txtDrAmt.setFont(Global.menuFont);
        txtCrAmt.setFont(Global.menuFont);
    }

    private void initTable() {
        tblAPAR.setModel(aPARTableModel);
        tblAPAR.getTableHeader().setFont(Global.tblHeaderFont);
        tblAPAR.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblAPAR.getColumnModel().getColumn(0).setPreferredWidth(20);
        tblAPAR.getColumnModel().getColumn(1).setPreferredWidth(400);
        tblAPAR.getColumnModel().getColumn(2).setPreferredWidth(200);
        tblAPAR.getColumnModel().getColumn(3).setPreferredWidth(1);
        tblAPAR.getColumnModel().getColumn(4).setPreferredWidth(100);
        tblAPAR.getColumnModel().getColumn(5).setPreferredWidth(100);
        tblAPAR.setDefaultRenderer(Double.class, new GLTableCellRender(4, 5));
        tblAPAR.setDefaultRenderer(Object.class, new GLTableCellRender(4, 5));

        tblAPAR.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                row = tblAPAR.convertRowIndexToModel(tblAPAR.getSelectedRow());
                if (e.getClickCount() == 2) {
                    if (tblAPAR.getSelectedRow() >= 0) {
                        VApar apar = aPARTableModel.getAPAR(row);
                        String traderCode = apar.getTraderCode();
                        String traderName = apar.getTraderName();
                        String coaCode = apar.getCoaCode();
                        openTBDDialog(coaCode, traderCode, traderName);
                    }
                }
            }
        });
    }

    private void openTBDDialog(String coaCode, String traderCode, String traderName) {
        if (dialog == null) {
            dialog = new TrialBalanceDetailDialog(Global.parentForm);
            dialog.setAccountRepo(accountRepo);
            dialog.setUserRepo(userRepo);
            dialog.initMain();
            dialog.setSize(Global.width - 20, Global.height - 20);
            dialog.setLocationRelativeTo(null);
        }
        dialog.setCoaCode(coaCode);
        dialog.setCurrency(currencyAutoCompleter == null ? null : currencyAutoCompleter.getCurrency());
        dialog.setDesp(traderName);
        dialog.setTraderCode(traderCode);
        dialog.setDepartment(departmentAutoCompleter.getListOption());
        dialog.setDeptName(txtDep.getText());
        dialog.setDateModel(dateAutoCompleter.getDateModel());
        dialog.searchTriBalDetail();
    }

    private List<String> getListDep() {
        return departmentAutoCompleter.getDepartment() == null ? new ArrayList<>() : departmentAutoCompleter.getListOption();
    }

    private String getCoaCode() {
        return cOAAutoCompleter.getCOA() == null ? "-"
                : cOAAutoCompleter.getCOA().getKey().getCoaCode();
    }

    private String getCurCode() {
        return currencyAutoCompleter.getCurrency() == null ? Global.currency : currencyAutoCompleter.getCurrency().getCurCode();
    }

    private void searchAPAR() {
        if (!isApPrCal) {
            long start = new GregorianCalendar().getTimeInMillis();
            log.info("AP/PR Calculating Start.");
            progress.setIndeterminate(true);
            isApPrCal = true;
            aPARTableModel.clear();
            String stDate = dateAutoCompleter.getDateModel().getStartDate();
            String endDate = dateAutoCompleter.getDateModel().getEndDate();
            TraderA trader = traderAutoCompleter.getTrader();
            String traderType = trader.getTraderType();
            String traderCode = trader.getKey().getCode();
            ReportFilter filter = new ReportFilter(Global.macId, Global.compCode, Global.deptId);
            filter.setFromDate(stDate);
            filter.setToDate(endDate);
            filter.setTraderCode(traderCode);
            filter.setCurCode(getCurCode());
            filter.setTraderType(traderType);
            filter.setListDepartment(getListDep());
            filter.setCoaCode(getCoaCode());
            filter.setProjectNo(projectAutoCompleter.getProject().getKey().getProjectNo());
            decorator.refreshButton(filter.getFromDate());
            accountRepo.getArAp(filter).doOnSuccess((t) -> {
                aPARTableModel.setListOrg(t);
                aPARTableModel.setListAPAR(t);
            }).doOnError((e) -> {
                isApPrCal = false;
                progress.setIndeterminate(false);
                JOptionPane.showMessageDialog(this, e.getMessage());
            }).doOnTerminate(() -> {
                calTotal();
                filterRegion();
                removeZero();
                isApPrCal = false;
                progress.setIndeterminate(false);
                long end = new GregorianCalendar().getTimeInMillis();
                long pt = (end - start) / 1000;
                lblCalTime.setText(pt + " s");
                ComponentUtil.scrollTable(tblAPAR, row, 0);
            }).subscribe();
        }
    }

    private void removeZero() {
        if (chkZero.isSelected()) {
            List<VApar> mutableList = new ArrayList<>(aPARTableModel.getListAPAR());
            List<VApar> listFilter = mutableList.stream().filter(t -> t.getDrAmt() + t.getCrAmt() != 0).toList();
            aPARTableModel.setListAPAR(listFilter);

        } else {
            aPARTableModel.setListAPAR(aPARTableModel.getListOrg());
        }
    }

    private void filterRegion() {
        Region r = regionAutoCompleter.getRegion();
        String regCode = r.getKey().getRegCode();
        if (!regCode.equals("-")) {
            List<VApar> mutableList = new ArrayList<>(aPARTableModel.getListAPAR());
            List<VApar> listFilter = mutableList.stream().filter(t -> Util1.isNull(t.getRegCode(), "-").equals(regCode)).toList();
            aPARTableModel.setListAPAR(listFilter);
        }

    }

    private void calTotal() {
        List<VApar> list = aPARTableModel.getListAPAR();
        double drAmt = list.stream().mapToDouble((value) -> Util1.getDouble(value.getDrAmt())).sum();
        double crAmt = list.stream().mapToDouble((value) -> Util1.getDouble(value.getCrAmt())).sum();
        txtDrAmt.setValue(drAmt);
        txtCrAmt.setValue(crAmt);
        txtFOFB.setValue(drAmt - crAmt);
    }

    private void initCombo() {
        dateAutoCompleter = new DateAutoCompleter(txtDate);
        dateAutoCompleter.setObserver(this);
        departmentAutoCompleter = new DepartmentAutoCompleter(txtDep, null, true, true);
        departmentAutoCompleter.setObserver(this);
        accountRepo.getDepartment().doOnSuccess((t) -> {
            departmentAutoCompleter.setListDepartment(t);
        }).subscribe();
        cOAAutoCompleter = new COAAutoCompleter(txtAccount, null, true);
        cOAAutoCompleter.setObserver(this);
        accountRepo.getTraderAccount().collectList().doOnSuccess((t) -> {
            cOAAutoCompleter.setListCOA(t);
        }).subscribe();
        currencyAutoCompleter = new CurrencyAutoCompleter(txtCurrency, null);
        currencyAutoCompleter.setObserver(this);
        userRepo.getCurrency().doOnSuccess((t) -> {
            currencyAutoCompleter.setListCurrency(t);
        }).subscribe();
        userRepo.getDefaultCurrency().doOnSuccess((c) -> {
            currencyAutoCompleter.setCurrency(c);
        }).subscribe();
        regionAutoCompleter = new RegionAutoCompleter(txtRegion, null, true);
        regionAutoCompleter.setObserver(this);
        inventoryRepo.getRegion().doOnSuccess((t) -> {
            regionAutoCompleter.setListRegion(t);
        }).subscribe();
        traderAutoCompleter = new TraderAAutoCompleter(txtPerson, accountRepo, null, true);
        traderAutoCompleter.setObserver(this);
        projectAutoCompleter = new ProjectAutoCompleter(txtProjectNo, null, true);
        projectAutoCompleter.setObserver(this);
        userRepo.searchProject().doOnSuccess((t) -> {
            projectAutoCompleter.setListProject(t);
        }).subscribe();

    }

    private void printARAP() {
        try {
            progress.setIndeterminate(true);
            String path = "temp/Ledger" + Global.macId + ".json";
            Util1.writeJsonFile(aPARTableModel.getListAPAR(), path);
            Map<String, Object> p = new HashMap();
            p.put("p_report_name", "Account Receivable & Payable");
            p.put("p_date", String.format("Between %s and %s", dateAutoCompleter.getDateModel().getStartDate(), dateAutoCompleter.getDateModel().getEndDate()));
            p.put("p_print_date", Util1.getTodayDateTime());
            p.put("p_comp_name", Global.companyName);
            p.put("p_comp_address", Global.companyAddress);
            p.put("p_comp_phone", Global.companyPhone);
            p.put("p_currency", currencyAutoCompleter.getCurrency().getCurCode());
            p.put("p_department", txtDep.getText());
            Util1.initJasperContext();
            JsonDataSource ds = new JsonDataSource(new File(path));
            JasperPrint js = JasperFillManager.fillReport(Global.accountRP + "ARAP.jasper", p, ds);
            JasperViewer.viewReport(js, false);
            progress.setIndeterminate(false);
        } catch (FileNotFoundException | JRException ex) {
            progress.setIndeterminate(false);
            JOptionPane.showMessageDialog(Global.parentForm, "Report", ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            log.error("printARAP : " + ex.getMessage());
        }

    }

    private void exportExcel() {
        exporter.setObserver(this);
        exporter.setTaskExecutor(taskExecutor);
        exporter.exportArAp(aPARTableModel.getListAPAR(), "AR-AP");
    }

    public void clear() {
        txtCurrency.setText(null);
        txtDate.setText(null);
        txtDep.setText(null);
        txtPerson.setText(null);
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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtDate = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtDep = new javax.swing.JTextField();
        txtPerson = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtCurrency = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtAccount = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtProjectNo = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtRegion = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblAPAR = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        txtDrAmt = new javax.swing.JFormattedTextField();
        txtCrAmt = new javax.swing.JFormattedTextField();
        txtFOFB = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lblCalTime = new javax.swing.JLabel();
        btnExcel = new javax.swing.JButton();
        lblMessage = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        chkZero = new javax.swing.JCheckBox();
        panelDate = new javax.swing.JPanel();

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

        txtPerson.setFont(Global.textFont);
        txtPerson.setName("txtPerson"); // NOI18N
        txtPerson.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPersonFocusGained(evt);
            }
        });
        txtPerson.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPersonActionPerformed(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Trader");

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
        jLabel7.setText("Account");

        txtAccount.setFont(Global.textFont);
        txtAccount.setName("txtPerson"); // NOI18N
        txtAccount.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtAccountFocusGained(evt);
            }
        });
        txtAccount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAccountActionPerformed(evt);
            }
        });

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Project No");

        txtProjectNo.setFont(Global.textFont);
        txtProjectNo.setName("txtPerson"); // NOI18N
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

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Region");

        txtRegion.setFont(Global.textFont);
        txtRegion.setName("txtPerson"); // NOI18N
        txtRegion.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtRegionFocusGained(evt);
            }
        });
        txtRegion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRegionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDep)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPerson)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAccount)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtRegion)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtProjectNo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
                    .addComponent(txtPerson, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel7)
                    .addComponent(txtAccount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(txtProjectNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(txtRegion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tblAPAR.setAutoCreateRowSorter(true);
        tblAPAR.setFont(Global.textFont);
        tblAPAR.setModel(new javax.swing.table.DefaultTableModel(
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
        tblAPAR.setRowHeight(Global.tblRowHeight);
        tblAPAR.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblAPARKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblAPAR);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        txtDrAmt.setEditable(false);
        txtDrAmt.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0"))));
        txtDrAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDrAmt.setFont(Global.amtFont);

        txtCrAmt.setEditable(false);
        txtCrAmt.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0"))));
        txtCrAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtCrAmt.setFont(Global.amtFont);

        txtFOFB.setEditable(false);
        txtFOFB.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0"))));
        txtFOFB.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFOFB.setFont(Global.amtFont);

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Out Of Balance");

        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Cal Time : ");

        lblCalTime.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lblCalTime.setText("0");

        btnExcel.setBackground(Global.selectionColor);
        btnExcel.setFont(Global.lableFont);
        btnExcel.setForeground(new java.awt.Color(255, 255, 255));
        btnExcel.setText("Export Excel");
        btnExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcelActionPerformed(evt);
            }
        });

        lblMessage.setText(".");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(btnExcel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblMessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblCalTime, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtDrAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFOFB, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCrAmt, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCrAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(lblCalTime))
                        .addComponent(txtDrAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFOFB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(btnExcel)
                    .addComponent(lblMessage))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        chkZero.setSelected(true);
        chkZero.setText("Zero");
        chkZero.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkZeroActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkZero)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(8, Short.MAX_VALUE)
                .addComponent(chkZero)
                .addContainerGap())
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observeMain();
    }//GEN-LAST:event_formComponentShown

    private void txtDepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDepActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDepActionPerformed

    private void txtPersonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPersonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPersonActionPerformed

    private void txtCurrencyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCurrencyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCurrencyActionPerformed

    private void txtDepFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDepFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDepFocusGained

    private void txtPersonFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPersonFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPersonFocusGained

    private void tblAPARKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblAPARKeyReleased

    }//GEN-LAST:event_tblAPARKeyReleased

    private void txtAccountFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAccountFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAccountFocusGained

    private void txtAccountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAccountActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAccountActionPerformed

    private void txtProjectNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtProjectNoFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtProjectNoFocusGained

    private void txtProjectNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProjectNoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtProjectNoActionPerformed

    private void chkZeroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkZeroActionPerformed
        // TODO add your handling code here:
        removeZero();
    }//GEN-LAST:event_chkZeroActionPerformed

    private void btnExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcelActionPerformed
        // TODO add your handling code here:
        exportExcel();
    }//GEN-LAST:event_btnExcelActionPerformed

    private void txtRegionFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRegionFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRegionFocusGained

    private void txtRegionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRegionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRegionActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExcel;
    private javax.swing.JCheckBox chkZero;
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
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCalTime;
    private javax.swing.JLabel lblMessage;
    private javax.swing.JPanel panelDate;
    private javax.swing.JTable tblAPAR;
    private javax.swing.JTextField txtAccount;
    private javax.swing.JFormattedTextField txtCrAmt;
    private javax.swing.JTextField txtCurrency;
    private javax.swing.JTextField txtDate;
    private javax.swing.JTextField txtDep;
    private javax.swing.JFormattedTextField txtDrAmt;
    private javax.swing.JFormattedTextField txtFOFB;
    private javax.swing.JTextField txtPerson;
    private javax.swing.JTextField txtProjectNo;
    private javax.swing.JTextField txtRegion;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {
        if (source != null) {
            if (source.equals("Date-Search")) {
                String date = selectObj.toString();
                dateAutoCompleter.getDateModel().setStartDate(date);
                dateAutoCompleter.getDateModel().setEndDate(date);
                txtDate.setText(Util1.toDateStr(date, "yyyy-MM-dd", "dd/MM/yyyy"));
            } else if (source.equals(ExcelExporter.MESSAGE)) {
                lblMessage.setText(selectObj.toString());
            } else if (source.equals(ExcelExporter.FINISH)) {
                btnExcel.setEnabled(true);
                lblMessage.setText(selectObj.toString());
            } else if (source.equals(ExcelExporter.ERROR)) {
                btnExcel.setEnabled(true);
                lblMessage.setText(selectObj.toString());
            } else {
                searchAPAR();
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
        searchAPAR();
    }

    @Override
    public void history() {
    }

    @Override
    public void print() {
        printARAP();
    }

    @Override
    public void refresh() {
        searchAPAR();
    }

    @Override
    public String panelName() {
        return this.getName();
    }

    @Override
    public void filter() {
        findDialog.setVisible(!findDialog.isVisible());
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
}
