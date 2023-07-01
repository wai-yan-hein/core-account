/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry;

import com.acc.common.DateAutoCompleter;
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
import com.inventory.editor.BatchAutoCompeter;
import com.inventory.editor.BrandAutoCompleter;
import com.inventory.editor.CategoryAutoCompleter;
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.editor.RegionAutoCompleter;
import com.inventory.editor.SaleManAutoCompleter;
import com.inventory.editor.StockAutoCompleter;
import com.inventory.editor.StockTypeAutoCompleter;
import com.inventory.editor.TraderAutoCompleter;
import com.inventory.editor.VouStatusAutoCompleter;
import com.inventory.model.VRoleMenu;
import com.inventory.ui.common.InventoryRepo;
import com.inventory.ui.common.ReportTableModel;
import com.toedter.calendar.JTextFieldDateEditor;
import com.user.common.UserRepo;
import com.user.editor.CurrencyAutoCompleter;
import com.user.editor.ProjectAutoCompleter;
import com.user.model.Project;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
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
public class Reports extends javax.swing.JPanel implements PanelControl, SelectionObserver {

    private final ReportTableModel tableModel = new ReportTableModel("Inventory Report");
    @Autowired
    private WebClient inventoryApi;
    @Autowired
    private InventoryRepo inventoryRepo;
    @Autowired
    private UserRepo userRepo;
    private boolean isReport = false;
    private String stDate;
    private String enDate;
    private TraderAutoCompleter traderAutoCompleter;
    private SaleManAutoCompleter saleManAutoCompleter;
    private LocationAutoCompleter locationAutoCompleter;
    private StockTypeAutoCompleter stockTypeAutoCompleter;
    private BrandAutoCompleter brandAutoCompleter;
    private CategoryAutoCompleter categoryAutoCompleter;
    private RegionAutoCompleter regionAutoCompleter;
    private CurrencyAutoCompleter currencyAutoCompleter;
    private StockAutoCompleter stockAutoCompleter;
    private VouStatusAutoCompleter vouStatusAutoCompleter;
    private DateAutoCompleter dateAutoCompleter;
    private BatchAutoCompeter batchAutoCompeter;
    private ProjectAutoCompleter projectAutoCompleter;
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
    private final FocusAdapter fa = new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            if (e.getSource() instanceof JTextField txt) {
                txt.selectAll();
            } else if (e.getSource() instanceof JTextFieldDateEditor txt) {
                txt.selectAll();
            }
        }
    };

    /**
     * Creates new form Reports
     */
    public Reports() {
        initComponents();
        initFocusAdapter();
    }

    private void initFocusAdapter() {
        txtDate.addFocusListener(fa);
        txtFromDate.addFocusListener(fa);
        txtToDate.addFocusListener(fa);
        txtTrader.addFocusListener(fa);
        txtSaleMan.addFocusListener(fa);
        txtLocation.addFocusListener(fa);
        txtStock.addFocusListener(fa);
        txtStockType.addFocusListener(fa);
        txtCategory.addFocusListener(fa);
        txtBrand.addFocusListener(fa);
        txtVouType.addFocusListener(fa);
        txtRegion.addFocusListener(fa);
        txtBatchNo.addFocusListener(fa);
        txtProjectNo.addFocusListener(fa);
    }

    public void initMain() {
        initTableReport();
        initCombo();
        initDate();
    }

    private void initDate() {
        txtFromDate.setDate(Util1.getTodayDate());
        txtToDate.setDate(Util1.getTodayDate());
    }

    private void initTableReport() {
        tblReport.setModel(tableModel);
        tblReport.getTableHeader().setFont(Global.tblHeaderFont);
        tblReport.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblReport.setDefaultRenderer(Object.class, new TableCellRender());
        tblReport.getColumnModel().getColumn(0).setCellRenderer(new FontCellRender());
        tblReport.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblReport.getColumnModel().getColumn(1).setPreferredWidth(900);
        sorter = new TableRowSorter(tblReport.getModel());
        tblReport.setRowSorter(sorter);
        getReport();
    }

    private void getReport() {
        progress.setIndeterminate(true);
        userRepo.getReport("Inventory")
                .subscribe((t) -> {
                    tableModel.setListReport(t);
                    lblRecord.setText(String.valueOf(t.size()));
                    progress.setIndeterminate(false);
                }, (e) -> {
                    progress.setIndeterminate(false);
                    JOptionPane.showConfirmDialog(Global.parentForm, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                });
    }

    private void initCombo() {
        locationAutoCompleter = new LocationAutoCompleter(txtLocation, null, true, true);
        locationAutoCompleter.setObserver(this);
        inventoryRepo.getLocation().subscribe((t) -> {
            locationAutoCompleter.setListLocation(t);
        });
        traderAutoCompleter = new TraderAutoCompleter(txtTrader, inventoryRepo, null, true, "-");
        saleManAutoCompleter = new SaleManAutoCompleter(txtSaleMan, null, true);
        inventoryRepo.getSaleMan().subscribe((t) -> {
            saleManAutoCompleter.setListSaleMan(t);
        });
        stockTypeAutoCompleter = new StockTypeAutoCompleter(txtStockType, null, true);
        inventoryRepo.getStockType().subscribe((t) -> {
            stockTypeAutoCompleter.setListStockType(t);
        });
        categoryAutoCompleter = new CategoryAutoCompleter(txtCategory, null, true);
        inventoryRepo.getCategory().subscribe((t) -> {
            categoryAutoCompleter.setListCategory(t);
        });
        brandAutoCompleter = new BrandAutoCompleter(txtBrand, null, true);
        inventoryRepo.getStockBrand().subscribe((t) -> {
            brandAutoCompleter.setListStockBrand(t);
        });
        inventoryRepo.getRegion().subscribe((t) -> {
            regionAutoCompleter = new RegionAutoCompleter(txtRegion, t, null, true, false);
        });
        currencyAutoCompleter = new CurrencyAutoCompleter(txtCurrency, null);
        userRepo.getCurrency().subscribe((t) -> {
            currencyAutoCompleter.setListCurrency(t);
        });
        userRepo.getDefaultCurrency().subscribe((c) -> {
            currencyAutoCompleter.setCurrency(c);
        });
        stockAutoCompleter = new StockAutoCompleter(txtStock, inventoryRepo, null, true);
        vouStatusAutoCompleter = new VouStatusAutoCompleter(txtVouType, inventoryRepo, null, true);
        dateAutoCompleter = new DateAutoCompleter(txtDate);
        dateAutoCompleter.setSelectionObserver(this);
        batchAutoCompeter = new BatchAutoCompeter(txtBatchNo, inventoryRepo, null, true);
        batchAutoCompeter.setObserver(this);
        projectAutoCompleter = new ProjectAutoCompleter(txtProjectNo, userRepo, null, true);
        projectAutoCompleter.setObserver(this);
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
                    observer.selected("save", false);
                    progress.setIndeterminate(true);
                    isReport = true;
                    stDate = Util1.toDateStr(txtFromDate.getDate(), "yyyy-MM-dd");
                    enDate = Util1.toDateStr(txtToDate.getDate(), "yyyy-MM-dd");
                    filter = new ReportFilter(Global.macId, Global.compCode, Global.deptId);
                    filter.setOpDate(Util1.toDateStr(Global.startDate, "dd/MM/yyyy", "yyyy-MM-dd"));
                    filter.setFromDate(stDate);
                    filter.setToDate(enDate);
                    filter.setCurCode(getCurCode());
                    filter.setTraderCode(traderAutoCompleter.getTrader().getKey().getCode());
                    filter.setSaleManCode(saleManAutoCompleter.getSaleMan().getKey().getSaleManCode());
                    filter.setListLocation(locationAutoCompleter.getListOption());
                    filter.setStockTypeCode(stockTypeAutoCompleter.getStockType().getKey().getStockTypeCode());
                    filter.setBrandCode(brandAutoCompleter.getBrand().getKey().getBrandCode());
                    filter.setRegCode(regionAutoCompleter.getRegion().getKey().getRegCode());
                    filter.setCatCode(categoryAutoCompleter.getCategory().getKey().getCatCode());
                    filter.setStockCode(stockAutoCompleter.getStock().getKey().getStockCode());
                    filter.setVouTypeCode(vouStatusAutoCompleter.getVouStatus().getKey().getCode());
                    filter.setLocCode(locationAutoCompleter.getLocation().getKey().getLocCode());
                    filter.setCalSale(Util1.getBoolean(ProUtil.getProperty("disable.calculate.sale.stock")));
                    filter.setCalPur(Util1.getBoolean(ProUtil.getProperty("disable.calculate.purchase.stock")));
                    filter.setCalRI(Util1.getBoolean(ProUtil.getProperty("disable.calculate.returin.stock")));
                    filter.setCalRO(Util1.getBoolean(ProUtil.getProperty("disable.calculate.retunout.stock")));
                    filter.setCreditAmt(Util1.getFloat(ProUtil.getProperty(ProUtil.C_CREDIT_AMT)));
                    String batchNo = batchAutoCompeter.getBatch().getBatchNo();
                    filter.setBatchNo(batchNo.equals("All") ? "-" : batchNo);
                    Project p = projectAutoCompleter.getProject();
                    String projectNo = p == null ? null : p.getKey().getProjectNo();
                    filter.setProjectNo(projectNo);
                    Map<String, Object> param = new HashMap<>();
                    param.put("p_report_name", reportName);
                    param.put("p_date", String.format("Between %s and %s",
                            Util1.toDateStr(stDate, "yyyy-MM-dd", "dd/MM/yyyy"),
                            Util1.toDateStr(enDate, "yyyy-MM-dd", "dd/MM/yyyy")));
                    param.put("p_print_date", Util1.getTodayDateTime());
                    param.put("p_comp_name", Global.companyName);
                    param.put("p_comp_address", Global.companyAddress);
                    param.put("p_comp_phone", Global.companyPhone);
                    param.put("p_currency", getCurCode());
                    param.put("p_stock_type", stockTypeAutoCompleter.getStockType().getStockTypeName());
                    param.put("p_location", txtLocation.getText());
                    printReport(reportUrl, reportUrl, param);
                }
                isReport = false;
            }
        } else {
            isReport = false;
            progress.setIndeterminate(false);
            observer.selected("save", false);
            JOptionPane.showMessageDialog(Global.parentForm, "Choose Report.");
        }
    }

    private String getCurCode() {
        if (currencyAutoCompleter == null || currencyAutoCompleter.getCurrency() == null) {
            return Global.currency;
        }
        return currencyAutoCompleter.getCurrency().getCurCode();
    }

    private boolean isValidReport(String url) {
        if (url.equals("StockInOutDetail")) {
            if (stockAutoCompleter.getStock().getKey().getStockCode().equals("-")) {
                JOptionPane.showMessageDialog(this, "Please select stock code.", "Report Validation", JOptionPane.INFORMATION_MESSAGE);
                txtStock.requestFocus();
                return false;
            }
        }
        return true;
    }

    private void printReport(String reportUrl, String reportName, Map<String, Object> param) {
        filter.setReportName(reportName);
        inventoryApi
                .post()
                .uri("/report/get-report")
                .body(Mono.just(filter), FilterObject.class)
                .retrieve()
                .bodyToMono(ReturnObject.class)
                .subscribe((t) -> {
                    try {
                        observer.selected("save", true);
                        if (t != null) {
                            String filePath = String.format("%s%s%s", Global.reportPath, File.separator, reportUrl.concat(".jasper"));
                            if (t.getFile().length > 0) {
                                JasperReportsContext jc = DefaultJasperReportsContext.getInstance();
                                jc.setProperty("net.sf.jasperreports.default.font.name", Global.fontName.concat(".ttf"));
                                jc.setProperty("net.sf.jasperreports.default.pdf.font.name", Global.fontName.concat(".ttf"));
                                jc.setProperty("net.sf.jasperreports.default.pdf.encoding", "Identity-H");
                                jc.setProperty("net.sf.jasperreports.default.pdf.embedded", "true");
                                jc.setProperty("net.sf.jasperreports.viewer.zoom", "1");
                                jc.setProperty("net.sf.jasperreports.export.xlsx.detect.cell.type", "true");
                                jc.setProperty("net.sf.jasperreports.export.xlsx.white.page.background", "false");
                                jc.setProperty("net.sf.jasperreports.export.xlsx.auto.fit.page.width", "true");
                                jc.setProperty("net.sf.jasperreports.export.xlsx.ignore.graphics", "false");
                                InputStream input = new ByteArrayInputStream(t.getFile());
                                JsonDataSource ds = new JsonDataSource(input);
                                JasperPrint js = JasperFillManager.fillReport(filePath, param, ds);
                                JRViewer viwer = new JRViewer(js);
                                JFrame frame = new JFrame("Core Value Report");
                                frame.setIconImage(Global.parentForm.getIconImage());
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
        jPanel1 = new javax.swing.JPanel();
        txtFromDate = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtToDate = new com.toedter.calendar.JDateChooser();
        jLabel1 = new javax.swing.JLabel();
        txtTrader = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtLocation = new javax.swing.JTextField();
        txtStockType = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtCategory = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtBrand = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtRegion = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtSaleMan = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtStock = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtVouType = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txtBatchNo = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        txtProjectNo = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        lblRecord = new javax.swing.JLabel();
        txtFilter = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        txtDate = new javax.swing.JTextField();
        txtCurrency = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();

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

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        txtFromDate.setDateFormatString("dd/MM/yyyy");
        txtFromDate.setFont(Global.lableFont);

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("From Date");

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("To Date");

        txtToDate.setDateFormatString("dd/MM/yyyy");
        txtToDate.setFont(Global.lableFont);

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

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Location");

        txtLocation.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtLocationFocusGained(evt);
            }
        });
        txtLocation.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtLocationKeyReleased(evt);
            }
        });

        txtStockType.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtStockTypeFocusGained(evt);
            }
        });
        txtStockType.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtStockTypeKeyReleased(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Stock Group");

        txtCategory.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCategoryFocusGained(evt);
            }
        });
        txtCategory.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCategoryKeyReleased(evt);
            }
        });

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Stock Category");

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Stock Brand");

        txtBrand.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtBrandFocusGained(evt);
            }
        });
        txtBrand.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBrandKeyReleased(evt);
            }
        });

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Region");

        txtRegion.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtRegionFocusGained(evt);
            }
        });

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Sale Man");

        txtSaleMan.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSaleManFocusGained(evt);
            }
        });
        txtSaleMan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSaleManKeyReleased(evt);
            }
        });

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Stock ");

        txtStock.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtStockFocusGained(evt);
            }
        });
        txtStock.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtStockKeyReleased(evt);
            }
        });

        jLabel13.setFont(Global.lableFont);
        jLabel13.setText("Vou Type");

        txtVouType.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtVouTypeFocusGained(evt);
            }
        });
        txtVouType.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtVouTypeKeyReleased(evt);
            }
        });

        jLabel15.setFont(Global.lableFont);
        jLabel15.setText("Batch No");

        txtBatchNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtBatchNoFocusGained(evt);
            }
        });

        jLabel16.setFont(Global.lableFont);
        jLabel16.setText("Project No");

        txtProjectNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtProjectNoFocusGained(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtFromDate, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtToDate, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE))
                    .addComponent(txtTrader)
                    .addComponent(txtLocation)
                    .addComponent(txtStockType)
                    .addComponent(txtCategory)
                    .addComponent(txtBrand)
                    .addComponent(txtRegion)
                    .addComponent(txtSaleMan)
                    .addComponent(txtStock)
                    .addComponent(txtVouType)
                    .addComponent(txtBatchNo)
                    .addComponent(txtProjectNo))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtFromDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtTrader)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtSaleMan)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtLocation)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtStock)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtStockType)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtCategory)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtBrand)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtVouType)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtRegion)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtBatchNo)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtProjectNo))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel16.getAccessibleContext().setAccessibleName("Project No");

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

        txtCurrency.setEditable(false);
        txtCurrency.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCurrencyFocusGained(evt);
            }
        });

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Currency");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCurrency)
                    .addComponent(txtDate))
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
                    .addComponent(txtCurrency)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 484, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblRecord, javax.swing.GroupLayout.PREFERRED_SIZE, 409, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(txtFilter))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(lblRecord)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observer.selected("control", this);
    }//GEN-LAST:event_formComponentShown

    private void txtTraderKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTraderKeyReleased
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtSaleMan.requestFocus();
        }
    }//GEN-LAST:event_txtTraderKeyReleased

    private void txtSaleManKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSaleManKeyReleased
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtLocation.requestFocus();
        }
    }//GEN-LAST:event_txtSaleManKeyReleased

    private void txtLocationKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtLocationKeyReleased
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtStockType.requestFocus();
        }
    }//GEN-LAST:event_txtLocationKeyReleased

    private void txtStockTypeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtStockTypeKeyReleased
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtCategory.requestFocus();
        }
    }//GEN-LAST:event_txtStockTypeKeyReleased

    private void txtCategoryKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCategoryKeyReleased
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtBrand.requestFocus();
        }
    }//GEN-LAST:event_txtCategoryKeyReleased

    private void txtBrandKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBrandKeyReleased
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtRegion.requestFocus();
        }
    }//GEN-LAST:event_txtBrandKeyReleased

    private void txtStockKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtStockKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtStockKeyReleased

    private void txtTraderFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTraderFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTraderFocusGained

    private void txtSaleManFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSaleManFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSaleManFocusGained

    private void txtLocationFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtLocationFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtLocationFocusGained

    private void txtStockFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtStockFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtStockFocusGained

    private void txtStockTypeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtStockTypeFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtStockTypeFocusGained

    private void txtCategoryFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCategoryFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCategoryFocusGained

    private void txtBrandFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBrandFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBrandFocusGained

    private void txtRegionFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRegionFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRegionFocusGained

    private void txtCurrencyFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCurrencyFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCurrencyFocusGained

    private void txtVouTypeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVouTypeFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVouTypeFocusGained

    private void txtVouTypeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtVouTypeKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVouTypeKeyReleased

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

    private void txtBatchNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBatchNoFocusGained
        txtBatchNo.selectAll();        // TODO add your handling code here:
    }//GEN-LAST:event_txtBatchNoFocusGained

    private void txtProjectNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtProjectNoFocusGained
        txtProjectNo.selectAll();        // TODO add your handling code here:
    }//GEN-LAST:event_txtProjectNoFocusGained


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblRecord;
    private javax.swing.JTable tblReport;
    private javax.swing.JTextField txtBatchNo;
    private javax.swing.JTextField txtBrand;
    private javax.swing.JTextField txtCategory;
    private javax.swing.JTextField txtCurrency;
    private javax.swing.JTextField txtDate;
    private javax.swing.JTextField txtFilter;
    private com.toedter.calendar.JDateChooser txtFromDate;
    private javax.swing.JTextField txtLocation;
    private javax.swing.JTextField txtProjectNo;
    private javax.swing.JTextField txtRegion;
    private javax.swing.JTextField txtSaleMan;
    private javax.swing.JTextField txtStock;
    private javax.swing.JTextField txtStockType;
    private com.toedter.calendar.JDateChooser txtToDate;
    private javax.swing.JTextField txtTrader;
    private javax.swing.JTextField txtVouType;
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
        if (source.equals("Date")) {
            txtFromDate.setDate(Util1.toDate(dateAutoCompleter.getDateModel().getStartDate(), "yyyy-MM-dd"));
            txtToDate.setDate(Util1.toDate(dateAutoCompleter.getDateModel().getEndDate(), "yyyy-MM-dd"));
        }
    }

}
