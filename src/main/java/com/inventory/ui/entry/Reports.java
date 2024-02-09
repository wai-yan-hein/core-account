/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry;

import com.acc.editor.DateAutoCompleter;
import com.common.ComponentUtil;
import com.common.ExcelExporter;
import com.common.Global;
import com.common.PanelControl;
import com.common.ProUtil;
import com.common.ReportFilter;
import com.common.RowHeader;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.common.Util1;
import com.inventory.editor.BatchAutoCompeter;
import com.inventory.editor.BrandAutoCompleter;
import com.inventory.editor.CategoryAutoCompleter;
import com.inventory.editor.LabourGroupAutoCompleter;
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.editor.RegionAutoCompleter;
import com.inventory.editor.SaleManAutoCompleter;
import com.inventory.editor.StockAutoCompleter;
import com.inventory.editor.StockTypeAutoCompleter;
import com.inventory.editor.TraderAutoCompleter;
import com.inventory.editor.VouStatusAutoCompleter;
import com.inventory.editor.WareHouseAutoCompleter;
import com.inventory.model.General;
import com.inventory.model.VOpening;
import com.inventory.model.VPurchase;
import com.inventory.model.VRoleMenu;
import com.inventory.model.VSale;
import com.inventory.model.VStockIO;
import com.repo.InventoryRepo;
import com.inventory.ui.common.ReportTableModel;
import com.repo.UserRepo;
import com.ui.management.model.ClosingBalance;
import com.user.editor.CurrencyAutoCompleter;
import com.user.editor.DepartmentUserAutoCompleter;
import com.user.editor.ProjectAutoCompleter;
import com.user.model.Project;
import cv.api.common.StockValue;
import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.JList;
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
import org.springframework.core.task.TaskExecutor;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class Reports extends javax.swing.JPanel implements PanelControl, SelectionObserver {

    private final ReportTableModel tableModel = new ReportTableModel("Inventory Report");
    private InventoryRepo inventoryRepo;
    private UserRepo userRepo;
    private TaskExecutor taskExecutor;
    private boolean isReport = false;
    private String stDate;
    private String enDate;
    private String stDueDate;
    private String enDueDate;
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
    private LabourGroupAutoCompleter labourGroupAutoCompleter;
    private WareHouseAutoCompleter wareHouseAutoCompleter;
    private DepartmentUserAutoCompleter departmentUserAutoCompleter;
    private ReportFilter filter;
    private SelectionObserver observer;
    private JProgressBar progress;
    private TableRowSorter<TableModel> sorter;
    private final ExcelExporter exporter = new ExcelExporter();
    private Set<String> excelReport = new HashSet<>();

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
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
    public Reports() {
        initComponents();
    }

    public void initMain() {
        ComponentUtil.addFocusListener(this);
        ComponentUtil.setTextProperty(this);
        initExcel();
        initTableReport();
        initRowHeader();
        initRowSorter();
        initCombo();
        initData();
        initDate();
        getReport();
    }

    private void initRowHeader() {
        RowHeader header = new RowHeader();
        JList list = header.createRowHeader(tblReport, 30);
        scroll.setRowHeaderView(list);
    }

    private void initExcel() {
        exporter.setObserver(this);
        exporter.setTaskExecutor(taskExecutor);
        //add report url 
        excelReport.add("StockInOutDetail");
        excelReport.add("StockInOutSummary");
        excelReport.add("StockListByGroup");
        excelReport.add("StockInOutSummaryByWeight");
        excelReport.add("StockInOutDetailByWeight");
        excelReport.add("SaleByStockDetail");
        excelReport.add("SaleByCustomerDetail");
        excelReport.add("PurchaseBySupplierDetail");
        excelReport.add("PurchaseByStockDetail");
        excelReport.add("TopSaleByCustomer");
        excelReport.add("TopSaleBySaleMan");
        excelReport.add("TopSaleByStock");
        excelReport.add("SaleByStockSummary");
        excelReport.add("SaleByCustomerSummary");
        excelReport.add("PurchaseBySupplierSummary");
        excelReport.add("PurchaseByStockSummary");
        excelReport.add("OpeningByGroup");
        excelReport.add("OpeningByLocation");
        excelReport.add("StockOutByVoucherTypeDetail");
        excelReport.add("StockInOutPriceCalender");
        excelReport.add("SaleBySaleManSummary");
        excelReport.add("SaleBySaleManDetail");
        excelReport.add("SalePriceCalender");
        excelReport.add("PurchasePriceCalender");
        excelReport.add("StockValue");
    }

    private void initDate() {
        txtFromDate.setDate(Util1.getTodayDate());
        txtToDate.setDate(Util1.getTodayDate());
        txtFromDueDate.setDate(Util1.getTodayDate());
        txtToDueDate.setDate(Util1.getTodayDate());
    }

    private void initTableReport() {
        tableModel.setExcelReport(excelReport);
        tblReport.setModel(tableModel);
        tblReport.getTableHeader().setFont(Global.tblHeaderFont);
        tblReport.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblReport.setDefaultRenderer(Object.class, new TableCellRender());
        tblReport.setDefaultRenderer(Boolean.class, new TableCellRender());
        tblReport.getColumnModel().getColumn(0).setPreferredWidth(900);
        tblReport.getColumnModel().getColumn(1).setPreferredWidth(50);
    }

    private void initRowSorter() {
        sorter = new TableRowSorter(tblReport.getModel());
        tblReport.setRowSorter(sorter);
    }

    private void setEnableExcel() {
        int row = tblReport.convertRowIndexToModel(tblReport.getSelectedRow());
        btnExcel.setEnabled(row > 0 ? excelReport.contains(tableModel.getReport(row).getMenuUrl()) : false);
    }

    private void getReport() {
        progress.setIndeterminate(true);
        userRepo.getReport("Inventory").doOnSuccess((t) -> {
            tableModel.setListReport(t);
            lblRecord.setText(String.valueOf(t.size()));
            progress.setIndeterminate(false);
        }).doOnError((e) -> {
            progress.setIndeterminate(false);
            JOptionPane.showConfirmDialog(Global.parentForm, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }).subscribe();
    }

    private void initData() {
        inventoryRepo.getLocation().doOnSuccess((t) -> {
            locationAutoCompleter.setListLocation(t);
        }).subscribe();
        inventoryRepo.getSaleMan().doOnSuccess((t) -> {
            saleManAutoCompleter.setListSaleMan(t);
        }).subscribe();
        inventoryRepo.getStockType().doOnSuccess((t) -> {
            stockTypeAutoCompleter.setListStockType(t);
        }).subscribe();
        inventoryRepo.getCategory().doOnSuccess((t) -> {
            categoryAutoCompleter.setListCategory(t);
        }).subscribe();
        inventoryRepo.getStockBrand().doOnSuccess((t) -> {
            brandAutoCompleter.setListStockBrand(t);
        }).subscribe();
        inventoryRepo.getRegion().doOnSuccess((t) -> {
            regionAutoCompleter.setListRegion(t);
        }).subscribe();
        userRepo.getCurrency().doOnSuccess((t) -> {
            currencyAutoCompleter.setListCurrency(t);
        }).subscribe();
        userRepo.getDefaultCurrency().doOnSuccess((c) -> {
            currencyAutoCompleter.setCurrency(c);
        }).subscribe();
        inventoryRepo.getVoucherStatus().doOnSuccess((t) -> {
            vouStatusAutoCompleter.setListData(t);
        }).subscribe();
        inventoryRepo.getWareHouse().doOnSuccess((t) -> {
            wareHouseAutoCompleter.setListObject(t);
        }).subscribe();
        inventoryRepo.getLabourGroup().doOnSuccess((t) -> {
            labourGroupAutoCompleter.setListObject(t);
        }).subscribe();
        userRepo.getDeparment(true).doOnSuccess((t) -> {
            departmentUserAutoCompleter.setListDepartment(t);
        }).subscribe();
    }

    private void initCombo() {
        locationAutoCompleter = new LocationAutoCompleter(txtLocation, null, true, true);
        locationAutoCompleter.setObserver(this);
        traderAutoCompleter = new TraderAutoCompleter(txtTrader, inventoryRepo, null, true, "-");
        saleManAutoCompleter = new SaleManAutoCompleter(txtSaleMan, null, true);
        stockTypeAutoCompleter = new StockTypeAutoCompleter(txtStockType, null, true);
        categoryAutoCompleter = new CategoryAutoCompleter(txtCategory, null, true);
        brandAutoCompleter = new BrandAutoCompleter(txtBrand, null, true);
        regionAutoCompleter = new RegionAutoCompleter(txtRegion, null, true);
        currencyAutoCompleter = new CurrencyAutoCompleter(txtCurrency, null);
        stockAutoCompleter = new StockAutoCompleter(txtStock, inventoryRepo, null, true);
        vouStatusAutoCompleter = new VouStatusAutoCompleter(txtVouType, null, true);
        dateAutoCompleter = new DateAutoCompleter(txtDate);
        dateAutoCompleter.setObserver(this);
        batchAutoCompeter = new BatchAutoCompeter(txtBatchNo, inventoryRepo, null, true);
        batchAutoCompeter.setObserver(this);
        projectAutoCompleter = new ProjectAutoCompleter(txtProjectNo, userRepo, null, true);
        projectAutoCompleter.setObserver(this);
        labourGroupAutoCompleter = new LabourGroupAutoCompleter(txtLG, null, true);
        labourGroupAutoCompleter.setObserver(this);
        wareHouseAutoCompleter = new WareHouseAutoCompleter(txtWH, null, true);
        departmentUserAutoCompleter = new DepartmentUserAutoCompleter(txtDep, null, true);
        departmentUserAutoCompleter.setObserver(this);
    }

    private void report(boolean excel) {
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
                    stDueDate = Util1.toDateStr(txtFromDueDate.getDate(), "yyyy-MM-dd");
                    enDueDate = Util1.toDateStr(txtToDueDate.getDate(), "yyyy-MM-dd");
                    filter = new ReportFilter(Global.macId, Global.compCode, Global.deptId);
                    filter.setOpDate(Util1.toDateStr(Global.startDate, "dd/MM/yyyy", "yyyy-MM-dd"));
                    filter.setFromDate(stDate);
                    filter.setToDate(enDate);
                    filter.setFromDueDate(stDueDate);
                    filter.setToDueDate(enDueDate);
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
                    filter.setWarehouseCode(wareHouseAutoCompleter.getObject().getKey().getCode());
                    filter.setLabourGroupCode(labourGroupAutoCompleter.getObject().getKey().getCode());
                    filter.setDeptId(departmentUserAutoCompleter.getDepartment().getKey().getDeptId());
                    filter.setCalSale(Util1.getBoolean(ProUtil.isDisableSale()));
                    filter.setCalPur(Util1.getBoolean(ProUtil.isDisablePur()));
                    filter.setCalRI(Util1.getBoolean(ProUtil.isDisableRetIn()));
                    filter.setCalRO(Util1.getBoolean(ProUtil.isDisableRetOut()));
                    filter.setCalMill(Util1.getBoolean(ProUtil.isDisableMill()));
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
                    param.put("p_logo_path", ProUtil.logoPath());
                    param.put("p_divider", new BigDecimal(Util1.getFloatOne(ProUtil.getProperty(ProUtil.DIVIDER))));
                    param.put("p_dep_name", txtDep.getText());
                    printReport(reportUrl, reportUrl, param, excel);
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
        if (url.equals("StockInOutDetail") || url.equals("StockInOutDetailByWeight")
               || url.equals("StockInOutPaddyDetailByLocation") || url.equals("StockInOutPaddyDetailByLocation1")) {
            if (stockAutoCompleter.getStock().getKey().getStockCode().equals("-")) {
                JOptionPane.showMessageDialog(this, "Please select stock code.", "Report Validation", JOptionPane.INFORMATION_MESSAGE);
                txtStock.requestFocus();
                return false;
            }
        }
        return true;
    }

    private void printReport(String reportUrl, String reportName, Map<String, Object> param, boolean excel) {
        filter.setReportName(reportName);
        inventoryRepo.getReport(filter).doOnSuccess((t) -> {
            try {
                observer.selected("save", true);
                if (t != null) {
                    String filePath = String.format("%s%s%s", Global.reportPath, File.separator, reportUrl.concat(".jasper"));
                    if (t.getFile() != null && t.getFile().length > 0) {
                        if (excel) {
                            excel(t.getFile());
                        } else {
                            InputStream input = new ByteArrayInputStream(t.getFile());
                            JsonDataSource ds = new JsonDataSource(input);
                            int dataCount = ds.recordCount();
                            if (dataCount > 0) {
                                JasperPrint js = JasperFillManager.fillReport(filePath, param, ds);
                                JRViewer viwer = new JRViewer(js);
                                JFrame frame = new JFrame("Core Value Report");
                                frame.setIconImage(Global.parentForm.getIconImage());
                                frame.getContentPane().add(viwer);
                                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                                frame.setVisible(true);
                            } else {
                                JOptionPane.showMessageDialog(this, "Sorry, there is no data available.");
                            }
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
    private final RowFilter<Object, Object> startsWithFilter = new RowFilter<Object, Object>() {
        @Override
        public boolean include(RowFilter.Entry<? extends Object, ? extends Object> entry) {
            String tmp1 = entry.getStringValue(0).toUpperCase().replace(" ", "");
            String tmp2 = entry.getStringValue(1).toUpperCase().replace(" ", "");
            String text = txtFilter.getText().toUpperCase().replace(" ", "");
            return tmp1.startsWith(text) || tmp2.startsWith(text);
        }
    };

    private void observeMain() {
        observer.selected("control", this);
        observer.selected("save", false);
        observer.selected("print", true);
        observer.selected("history", false);
        observer.selected("delete", false);
        observer.selected("refresh", true);
    }

    private void excel(byte[] file) {
        int row = tblReport.convertRowIndexToModel(tblReport.getSelectedRow());
        if (row >= 0) {
            btnExcel.setEnabled(false);
            VRoleMenu report = tableModel.getReport(row);
            String reportName = report.getMenuName();
            String reportUrl = report.getMenuUrl();
            InputStream input = new ByteArrayInputStream(file);
            switch (reportUrl) {
                case "StockInOutDetail", "StockInOutDetailByWeight" -> {
                    String stockName = stockAutoCompleter.getStock().getStockName();
                    List<ClosingBalance> list = Util1.readJsonToList(input, ClosingBalance.class);
                    exporter.exportStockInOutDetail(list, stockName);
                    // Use the TypeReference to specify the target type (List<Person>)
                }
                case "StockInOutSummary", "StockInOutSummaryByWeight" -> {
                    List<ClosingBalance> list = Util1.readJsonToList(input, ClosingBalance.class);
                    exporter.exportStockInOutSummary(list, reportUrl);
                }
                case "StockListByGroup" -> {
                    List<General> list = Util1.readJsonToList(input, General.class);
                    exporter.exportStockListByGroup(list, reportUrl);
                }
                case "SaleByStockDetail" -> {
                    List<VSale> list = Util1.readJsonToList(input, VSale.class);
                    exporter.exportSaleByStockDetail(list, reportUrl);
                }
                case "SaleByCustomerDetail" -> {
                    List<VSale> list = Util1.readJsonToList(input, VSale.class);
                    exporter.exportSaleByCustomerDeatail(list, reportUrl);
                }
                case "PurchaseBySupplierDetail" -> {
                    List<VPurchase> list = Util1.readJsonToList(input, VPurchase.class);
                    exporter.exportPurchaseBySupplierDeatail(list, reportUrl);
                }
                case "PurchaseByStockDetail" -> {
                    List<VPurchase> list = Util1.readJsonToList(input, VPurchase.class);
                    exporter.exportPurchaseByStockDeatail(list, reportUrl);
                }
                case "TopSaleByCustomer" -> {
                    List<General> list = Util1.readJsonToList(input, General.class);
                    exporter.exportTopSaleByCustomer(list, reportUrl);
                }
                case "TopSaleBySaleMan" -> {
                    List<General> list = Util1.readJsonToList(input, General.class);
                    exporter.exportTopSaleBySaleMan(list, reportUrl);
                }
                case "TopSaleByStock" -> {
                    List<General> list = Util1.readJsonToList(input, General.class);
                    exporter.exportTopSaleByStock(list, reportUrl);
                }
                case "SaleByStockSummary" -> {
                    List<VSale> list = Util1.readJsonToList(input, VSale.class);
                    exporter.exportSaleByStockSummary(list, reportUrl);
                }
                case "SaleByCustomerSummary" -> {
                    List<VSale> list = Util1.readJsonToList(input, VSale.class);
                    exporter.exportSaleByCustomerSummary(list, reportUrl);
                }
                case "PurchaseBySupplierSummary" -> {
                    List<VPurchase> list = Util1.readJsonToList(input, VPurchase.class);
                    exporter.exportPurchaseBySupplierSummary(list, reportUrl);
                }
                case "PurchaseByStockSummary" -> {
                    List<VPurchase> list = Util1.readJsonToList(input, VPurchase.class);
                    exporter.exportPurchaseByStockSummary(list, reportUrl);
                }
                case "OpeningByGroup" -> {
                    List<VOpening> list = Util1.readJsonToList(input, VOpening.class);
                    exporter.exportOpeningByGroup(list, reportUrl);
                }
                case "OpeningByLocation" -> {
                    List<VOpening> list = Util1.readJsonToList(input, VOpening.class);
                    exporter.exportOpeningByLocation(list, reportUrl);
                }
                case "StockOutByVoucherTypeDetail" -> {
                    List<VStockIO> list = Util1.readJsonToList(input, VStockIO.class);
                    exporter.exportStockOutByVoucherTypeDetail(list, reportUrl);
                }
                case "StockInOutPriceCalender" -> {
                    List<VStockIO> list = Util1.readJsonToList(input, VStockIO.class);
                    exporter.exportStockInOutPriceCalender(list, reportUrl);
                }
                case "SaleBySaleManDetail" -> {
                    List<VSale> list = Util1.readJsonToList(input, VSale.class);
                    exporter.exportSaleBySaleManDetail(list, reportUrl);
                }
                case "SaleBySaleManSummary" -> {
                    List<VSale> list = Util1.readJsonToList(input, VSale.class);
                    exporter.exportSaleBySaleManSummary(list, reportUrl);
                }
                case "SalePriceCalender" -> {
                    List<VSale> list = Util1.readJsonToList(input, VSale.class);
                    exporter.exportSalePriceCalender(list, reportUrl);
                }
                case "PurchasePriceCalender" -> {
                    List<VPurchase> list = Util1.readJsonToList(input, VPurchase.class);
                    exporter.exportPurchasePriceCalender(list, reportUrl);
                }
                case "StockValue" -> {
                    List<StockValue> list = Util1.readJsonToList(input, StockValue.class);
                    exporter.exportStockValue(list, reportUrl);
                }
                default -> {
                    btnExcel.setEnabled(true);
                    JOptionPane.showMessageDialog(this, String.format("%s report can't export excel.",
                            reportName), "Excel Validation", JOptionPane.ERROR_MESSAGE);
                }

            }
        } else {
            btnExcel.setEnabled(true);
            JOptionPane.showMessageDialog(Global.parentForm, "Choose Report.");
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

        scroll = new javax.swing.JScrollPane();
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
        jLabel17 = new javax.swing.JLabel();
        txtFromDueDate = new com.toedter.calendar.JDateChooser();
        txtToDueDate = new com.toedter.calendar.JDateChooser();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        txtLG = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        txtWH = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        txtDep = new javax.swing.JTextField();
        txtFilter = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        txtDate = new javax.swing.JTextField();
        txtCurrency = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        lblRecord = new javax.swing.JLabel();
        btnExcel = new javax.swing.JButton();
        lblMessage = new javax.swing.JLabel();
        btnSIF = new javax.swing.JButton();

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
        jLabel2.setText("Ware House");

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

        jLabel17.setFont(Global.lableFont);
        jLabel17.setText("From Due Date");

        txtFromDueDate.setDateFormatString("dd/MM/yyyy");
        txtFromDueDate.setFont(Global.lableFont);

        txtToDueDate.setDateFormatString("dd/MM/yyyy");
        txtToDueDate.setFont(Global.lableFont);

        jLabel18.setFont(Global.lableFont);
        jLabel18.setText("To Due Date");

        jLabel19.setFont(Global.lableFont);
        jLabel19.setText("Labour Group");

        txtLG.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtLGFocusGained(evt);
            }
        });
        txtLG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtLGActionPerformed(evt);
            }
        });

        jLabel20.setFont(Global.lableFont);
        jLabel20.setText("Location");

        txtWH.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtWHFocusGained(evt);
            }
        });
        txtWH.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtWHKeyReleased(evt);
            }
        });

        jLabel21.setFont(Global.lableFont);
        jLabel21.setText("Department");

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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtFromDueDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtFromDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtToDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtToDueDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(txtProjectNo, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtBatchNo, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtRegion, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtVouType, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtBrand, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCategory, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtStockType, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtStock, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtSaleMan, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtTrader)
                    .addComponent(txtLG, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtWH, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtLocation, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE))
                    .addComponent(txtDep))
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
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtFromDueDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtToDueDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtDep)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtLocation)
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtWH)))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtLG))
                .addContainerGap())
        );

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDate)
                    .addComponent(txtCurrency))
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

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel12.setFont(Global.lableFont);
        jLabel12.setText("Record :");

        lblRecord.setFont(Global.lableFont);
        lblRecord.setText("0");

        btnExcel.setFont(Global.lableFont);
        btnExcel.setText("Excel");
        btnExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcelActionPerformed(evt);
            }
        });

        lblMessage.setFont(Global.lableFont);
        lblMessage.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblMessage.setText("-");

        btnSIF.setFont(Global.lableFont);
        btnSIF.setText("Show In Folder");
        btnSIF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSIFActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblRecord, javax.swing.GroupLayout.PREFERRED_SIZE, 409, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblMessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSIF)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnExcel)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(lblRecord)
                    .addComponent(btnExcel)
                    .addComponent(lblMessage)
                    .addComponent(btnSIF))
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
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtFilter)
                            .addComponent(scroll, javax.swing.GroupLayout.DEFAULT_SIZE, 463, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
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
                        .addComponent(scroll))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observeMain();
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
        sorter.setRowFilter(f.isBlank() ? null : startsWithFilter);;
    }//GEN-LAST:event_txtFilterKeyReleased

    private void txtBatchNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBatchNoFocusGained
        txtBatchNo.selectAll();        // TODO add your handling code here:
    }//GEN-LAST:event_txtBatchNoFocusGained

    private void txtProjectNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtProjectNoFocusGained
        txtProjectNo.selectAll();        // TODO add your handling code here:
    }//GEN-LAST:event_txtProjectNoFocusGained

    private void btnExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcelActionPerformed
        // TODO add your handling code here:
        report(true);
    }//GEN-LAST:event_btnExcelActionPerformed

    private void btnSIFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSIFActionPerformed
        // TODO add your handling code here:
        Util1.openFolder(exporter.getLastPath());
    }//GEN-LAST:event_btnSIFActionPerformed

    private void tblReportMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblReportMouseClicked
        // TODO add your handling code here:
        setEnableExcel();
    }//GEN-LAST:event_tblReportMouseClicked

    private void txtLGFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtLGFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtLGFocusGained

    private void txtLGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtLGActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtLGActionPerformed

    private void txtWHFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtWHFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtWHFocusGained

    private void txtWHKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtWHKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtWHKeyReleased

    private void txtDepFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDepFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDepFocusGained

    private void txtDepKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDepKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDepKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExcel;
    private javax.swing.JButton btnSIF;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
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
    private javax.swing.JLabel lblMessage;
    private javax.swing.JLabel lblRecord;
    private javax.swing.JScrollPane scroll;
    private javax.swing.JTable tblReport;
    private javax.swing.JTextField txtBatchNo;
    private javax.swing.JTextField txtBrand;
    private javax.swing.JTextField txtCategory;
    private javax.swing.JTextField txtCurrency;
    private javax.swing.JTextField txtDate;
    private javax.swing.JTextField txtDep;
    private javax.swing.JTextField txtFilter;
    private com.toedter.calendar.JDateChooser txtFromDate;
    private com.toedter.calendar.JDateChooser txtFromDueDate;
    private javax.swing.JTextField txtLG;
    private javax.swing.JTextField txtLocation;
    private javax.swing.JTextField txtProjectNo;
    private javax.swing.JTextField txtRegion;
    private javax.swing.JTextField txtSaleMan;
    private javax.swing.JTextField txtStock;
    private javax.swing.JTextField txtStockType;
    private com.toedter.calendar.JDateChooser txtToDate;
    private com.toedter.calendar.JDateChooser txtToDueDate;
    private javax.swing.JTextField txtTrader;
    private javax.swing.JTextField txtVouType;
    private javax.swing.JTextField txtWH;
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
        report(false);
    }

    @Override
    public void refresh() {
        getReport();
        initData();
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
            txtFromDueDate.setDate(Util1.toDate(dateAutoCompleter.getDateModel().getStartDate(), "yyyy-MM-dd"));
            txtToDueDate.setDate(Util1.toDate(dateAutoCompleter.getDateModel().getEndDate(), "yyyy-MM-dd"));
        } else if (source.equals(ExcelExporter.MESSAGE)) {
            lblMessage.setText(selectObj.toString());
        } else if (source.equals(ExcelExporter.FINISH)) {
            btnExcel.setEnabled(true);
            lblMessage.setText(selectObj.toString());
        } else if (source.equals(ExcelExporter.ERROR)) {
            btnExcel.setEnabled(true);
            lblMessage.setText(selectObj.toString());
        }
    }

}
