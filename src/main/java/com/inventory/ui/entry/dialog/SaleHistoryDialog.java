/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry.dialog;

import com.common.ComponentUtil;
import com.common.ReportFilter;
import com.common.Global;
import com.common.IconUtil;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.common.StartWithRowFilter;
import com.common.TableCellRender;
import com.repo.UserRepo;
import com.common.Util1;
import com.formdev.flatlaf.FlatClientProperties;
import com.inventory.editor.AppUserAutoCompleter;
import com.inventory.editor.BatchAutoCompeter;
import com.user.editor.DepartmentUserAutoCompleter;
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.editor.PaymentTypeCompleter;
import com.inventory.editor.SaleManAutoCompleter;
import com.inventory.editor.StockAutoCompleter;
import com.inventory.editor.TraderAutoCompleter;
import com.user.model.AppUser;
import com.inventory.entity.GRN;
import com.inventory.entity.SaleMan;
import com.inventory.entity.Stock;
import com.inventory.entity.Trader;
import com.inventory.entity.VSale;
import com.inventory.ui.entry.dialog.common.SalePaddySearchTableModel;
import com.repo.InventoryRepo;
import com.inventory.ui.entry.dialog.common.SaleVouSearchTableModel;
import com.user.editor.CurrencyAutoCompleter;
import com.user.editor.ProjectAutoCompleter;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author wai yan
 */
@Slf4j
public class SaleHistoryDialog extends javax.swing.JDialog implements KeyListener {

    /**
     * Creates new form SaleVouSearchDialog
     *
     */
    @Setter
    private SelectionObserver observer;
    @Setter
    private UserRepo userRepo;
    @Setter
    private InventoryRepo inventoryRepo;

    private final SaleVouSearchTableModel saleVouTableModel = new SaleVouSearchTableModel();
    private final SalePaddySearchTableModel salePaddySearchTableModel = new SalePaddySearchTableModel();
    private TraderAutoCompleter traderAutoCompleter;
    private AppUserAutoCompleter appUserAutoCompleter;
    private StockAutoCompleter stockAutoCompleter;
    private SaleManAutoCompleter saleManAutoCompleter;
    private DepartmentUserAutoCompleter departmentAutoCompleter;
    private BatchAutoCompeter batchAutoCompeter;
    private ProjectAutoCompleter projectAutoCompleter;
    private CurrencyAutoCompleter currAutoCompleter;
    private PaymentTypeCompleter paymentTypeCompleter;
    private TableRowSorter<TableModel> sorter;
    private StartWithRowFilter tblFilter;
    private LocationAutoCompleter locationAutoCompleter;
    private int type;
    private int row = 0;

    public SaleHistoryDialog(JFrame frame, int type) {
        super(frame, Dialog.ModalityType.MODELESS);
        this.type = type;
        initComponents();
        initKeyListener();
        initProperty();
    }

    private void initClientProperty() {
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search Here");
        txtSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        txtSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, IconUtil.getIcon(IconUtil.SEARCH_ICON));
    }

    public void initMain() {
        initClientProperty();
        initCombo();
        initModel();
        initTable();
        setTodayDate();
    }

    private void initModel() {
        switch (type) {
            case 0 ->
                initTableVoucher();
            default ->
                initTablePaddy();
        }
    }

    private void initProperty() {
        ComponentUtil.addFocusListener(this);
        ComponentUtil.setTextProperty(this);
    }

    private void initCombo() {
        appUserAutoCompleter = new AppUserAutoCompleter(txtUser, null, true);
        saleManAutoCompleter = new SaleManAutoCompleter(txtSaleMan, null, true);
        locationAutoCompleter = new LocationAutoCompleter(txtLocation, null, true, false);
        departmentAutoCompleter = new DepartmentUserAutoCompleter(txtDep, null, true);
        currAutoCompleter = new CurrencyAutoCompleter(txtCurrency, null);
        traderAutoCompleter = new TraderAutoCompleter(txtCus, inventoryRepo, null, true, "CUS");
        stockAutoCompleter = new StockAutoCompleter(txtStock, inventoryRepo, null, true, ProUtil.isSSContain());
        batchAutoCompeter = new BatchAutoCompeter(txtBatchNo, inventoryRepo, null, true);
        projectAutoCompleter = new ProjectAutoCompleter(txtProjectNo, null, true);
        paymentTypeCompleter = new PaymentTypeCompleter(txtPayment, null);
        userRepo.getAppUser().doOnSuccess((t) -> {
            appUserAutoCompleter.setListUser(t);
        }).subscribe();
        inventoryRepo.getSaleMan().doOnSuccess((t) -> {
            saleManAutoCompleter.setListSaleMan(t);
        }).subscribe();
        inventoryRepo.getLocation().doOnSuccess((t) -> {
            locationAutoCompleter.setListLocation(t);
        }).subscribe();
        userRepo.getDeparment(true).doOnSuccess((t) -> {
            departmentAutoCompleter.setListDepartment(t);
        }).subscribe();
        userRepo.findDepartment(Global.deptId).doOnSuccess((t) -> {
            departmentAutoCompleter.setDepartment(t);
        }).subscribe();
        userRepo.getCurrency().doOnSuccess((t) -> {
            currAutoCompleter.setListCurrency(t);
        }).subscribe();
        userRepo.getDefaultCurrency().doOnSuccess((c) -> {
            currAutoCompleter.setCurrency(c);
        }).subscribe();
        userRepo.searchProject().doOnSuccess((t) -> {
            projectAutoCompleter.setListProject(t);
        }).subscribe();
    }

    private void initTableVoucher() {
        tblVoucher.setModel(saleVouTableModel);
        tblVoucher.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblVoucher.getColumnModel().getColumn(1).setPreferredWidth(50);
        tblVoucher.getColumnModel().getColumn(2).setPreferredWidth(180);
        tblVoucher.getColumnModel().getColumn(3).setPreferredWidth(180);
        tblVoucher.getColumnModel().getColumn(4).setPreferredWidth(50);
        tblVoucher.getColumnModel().getColumn(5).setPreferredWidth(15);
        tblVoucher.getColumnModel().getColumn(6).setPreferredWidth(100);
        tblVoucher.getColumnModel().getColumn(7).setPreferredWidth(100);
    }

    private void initTablePaddy() {
        salePaddySearchTableModel.setInventoryRepo(inventoryRepo);
        tblVoucher.setModel(salePaddySearchTableModel);
        tblVoucher.getColumnModel().getColumn(0).setPreferredWidth(50);//d
        tblVoucher.getColumnModel().getColumn(1).setPreferredWidth(40);//v
        tblVoucher.getColumnModel().getColumn(2).setPreferredWidth(180);//c
        tblVoucher.getColumnModel().getColumn(3).setPreferredWidth(180);//r
        tblVoucher.getColumnModel().getColumn(4).setPreferredWidth(100);//r
        tblVoucher.getColumnModel().getColumn(5).setPreferredWidth(20);//q
        tblVoucher.getColumnModel().getColumn(6).setPreferredWidth(20);//b
        tblVoucher.getColumnModel().getColumn(7).setPreferredWidth(100);//p
        tblVoucher.getColumnModel().getColumn(8).setPreferredWidth(100);//v
        tblVoucher.getColumnModel().getColumn(9).setPreferredWidth(10);//v
    }

    private void initTable() {
        tblVoucher.setDefaultRenderer(Object.class, new TableCellRender());
        tblVoucher.setDefaultRenderer(Double.class, new TableCellRender());
        tblVoucher.setDefaultRenderer(Boolean.class, new TableCellRender());
        tblVoucher.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sorter = new TableRowSorter<>(tblVoucher.getModel());
        tblFilter = new StartWithRowFilter(txtSearch);
        tblVoucher.setRowSorter(sorter);
        tblVoucher.getTableHeader().setFont(Global.tblHeaderFont);

    }

    private void setTodayDate() {
        txtFromDate.setDate(Util1.getTodayDate());
        txtToDate.setDate(Util1.getTodayDate());
        chkLocal.setSelected(false);
    }

    private String getUserCode() {
        return appUserAutoCompleter.getAppUser() == null ? "-" : appUserAutoCompleter.getAppUser().getUserCode();
    }

    private String getSaleMancode() {
        return saleManAutoCompleter.getSaleMan() == null ? "-" : saleManAutoCompleter.getSaleMan().getKey().getSaleManCode();
    }

    private String getLocCode() {
        return locationAutoCompleter.getLocation() == null ? "-" : locationAutoCompleter.getLocation().getKey().getLocCode();
    }

    private Integer getDepId() {
        return departmentAutoCompleter.getDepartment() == null ? 0 : departmentAutoCompleter.getDepartment().getKey().getDeptId();
    }

    private String getCurCode() {
        if (currAutoCompleter == null || currAutoCompleter.getCurrency() == null) {
            return Global.currency;
        }
        return currAutoCompleter.getCurrency().getCurCode();
    }

    public void search() {
        progress.setIndeterminate(true);
        ReportFilter filter = new ReportFilter(Global.macId, Global.compCode, Global.deptId);
        filter.setTraderCode(traderAutoCompleter.getTrader().getKey().getCode());
        filter.setFromDate(Util1.toDateStr(txtFromDate.getDate(), "yyyy-MM-dd"));
        filter.setToDate(Util1.toDateStr(txtToDate.getDate(), "yyyy-MM-dd"));
        filter.setUserCode(getUserCode());
        filter.setSaleManCode(getSaleMancode());
        filter.setLocCode(getLocCode());
        filter.setDeptId(getDepId());
        filter.setVouNo(txtVouNo.getText());
        filter.setRemark(Util1.isNull(txtRemark.getText(), "-"));
        filter.setStockCode(stockAutoCompleter.getStock().getKey().getStockCode());
        filter.setReference(txtRef.getText());
        filter.setDeleted(chkDel.isSelected());
        String batchNo = batchAutoCompeter.getBatch().getBatchNo();
        filter.setBatchNo(batchNo.equals("All") ? "-" : batchNo);
        String projectNo = projectAutoCompleter.getProject().getKey().getProjectNo();
        filter.setProjectNo(projectNo.equals("All") ? "-" : projectNo);
        filter.setCurCode(getCurCode());
        filter.setNullBatch(chkBatch.isSelected());
        filter.setLocal(chkLocal.isSelected());
        filter.setPaymentType(paymentTypeCompleter.getObject().getId());
        clearModel();
        txtRecord.setValue(0);
        txtTotalAmt.setValue(0);
        txtPaid.setValue(0);
        inventoryRepo.getSaleHistory(filter)
                .doOnNext(obj -> btnSearch.setEnabled(false))
                .doOnNext(this::addObject)
                .doOnNext(obj -> calTotal())
                .doOnError(e -> {
                    progress.setIndeterminate(false);
                    btnSearch.setEnabled(true);
                    JOptionPane.showMessageDialog(this, e.getMessage());
                })
                .doOnTerminate(() -> {
                    progress.setIndeterminate(false);
                    btnSearch.setEnabled(true);
                    ComponentUtil.scrollTable(tblVoucher, row, 0);
                }).subscribe();
        setVisible(true);
    }

    private void clearModel() {
        switch (type) {
            case 0 ->
                saleVouTableModel.clear();
            default ->
                salePaddySearchTableModel.clear();
        }
    }

    private void addObject(VSale obj) {
        switch (type) {
            case 0 ->
                saleVouTableModel.addObject(obj);
            default ->
                salePaddySearchTableModel.addObject(obj);
        }
    }

    private void calTotal() {
        switch (type) {
            case 0 -> {
                txtPaid.setValue(saleVouTableModel.getPaidTotal());
                txtTotalAmt.setValue(saleVouTableModel.getVouTotal());
                txtRecord.setValue(saleVouTableModel.getSize());
                txtQty.setValue(saleVouTableModel.getQty());
                txtBag.setValue(saleVouTableModel.getBag());
                txtDis.setValue(saleVouTableModel.getDiscount());
            }
            default -> {
                txtPaid.setValue(salePaddySearchTableModel.getPaidTotal());
                txtTotalAmt.setValue(salePaddySearchTableModel.getVouTotal());
                txtRecord.setValue(salePaddySearchTableModel.getSize());
                txtQty.setValue(salePaddySearchTableModel.getQty());
                txtBag.setValue(salePaddySearchTableModel.getBag());
            }
        }

    }

    private VSale getSale(int row) {
        return switch (type) {
            case 0 ->
                saleVouTableModel.getSelectVou(row);
            default ->
                salePaddySearchTableModel.getSelectVou(row);
        };
    }

    private void select() {
        if (row >= 0) {
            VSale his = getSale(row);
            his.setLocal(chkLocal.isSelected());
            observer.selected("SALE-HISTORY", his);
            setVisible(false);
        } else {
            JOptionPane.showMessageDialog(this, "Please select the voucher.",
                    "No Voucher Selected", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void print() {
        if (row >= 0) {
            VSale his = getSale(row);
            his.setLocal(chkLocal.isSelected());
            observer.selected("PRINT", his);
        } else {
            JOptionPane.showMessageDialog(this, "Please select the voucher.",
                    "No Voucher Selected", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Map<String, Object> getDefaultParam() {
        Map<String, Object> param = new HashMap<>();
        param.put("p_dep_name", Global.department.getDeptName());
        param.put("p_print_date", Util1.getTodayDateTime());
        param.put("p_comp_name", Global.companyName);
        param.put("p_comp_address", Global.companyAddress);
        param.put("p_comp_phone", Global.companyPhone);
        param.put("p_logo_path", ProUtil.logoPath());
        param.put("p_watermark", ProUtil.waterMark());
        return param;
    }

    private void viewReport() {
        ReportFilter filter = new ReportFilter(Global.macId, Global.compCode, Global.deptId);
        filter.setFromDate(Util1.toDateStr(txtFromDate.getDate(), "yyyy-MM-dd"));
        filter.setToDate(Util1.toDateStr(txtToDate.getDate(), "yyyy-MM-dd"));
        progress.setIndeterminate(true);
        inventoryRepo.getSaleSummaryByDepartment(filter)
                .doOnSuccess((list) -> {
                    if (list != null) {
                        if (!list.isEmpty()) {
                            try {
                                String reportName = "SaleSessionCheck";
                                Map<String, Object> param = getDefaultParam();
                                String fromDate = Util1.toDateStr(txtFromDate.getDate(), Global.dateFormat);
                                String toDate = Util1.toDateStr(txtFromDate.getDate(), Global.dateFormat);
                                String date;
                                if (fromDate.equals(toDate)) {
                                    date = fromDate;
                                } else {
                                    date = String.format("Between %s and %s", fromDate, toDate);
                                }
                                param.put("p_date", date);
                                String reportPath = ProUtil.getReportPath() + reportName.concat(".jasper");
                                ByteArrayInputStream stream = new ByteArrayInputStream(Util1.listToByteArray(list));
                                JsonDataSource ds = new JsonDataSource(stream);
                                JasperPrint jp = JasperFillManager.fillReport(reportPath, param, ds);
                                JasperViewer.viewReport(jp, false);
                                progress.setIndeterminate(false);
                            } catch (JRException e) {
                                progress.setIndeterminate(false);
                                JOptionPane.showMessageDialog(this, e.getMessage());
                            }
                        } else {
                            progress.setIndeterminate(false);
                            JOptionPane.showMessageDialog(this, "No Sale Voucher.");
                        }
                    }
                }).subscribe();

    }

    private void initKeyListener() {
        txtFromDate.getDateEditor().getUiComponent().setName("txtFromDate");
        txtFromDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtToDate.getDateEditor().getUiComponent().setName("txtToDate");
        txtToDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtVouNo.addKeyListener(this);
        txtCus.addKeyListener(this);
        txtUser.addKeyListener(this);
        txtCurrency.addKeyListener(this);
    }

    private void clearFilter() {
        txtFromDate.setDate(Util1.getTodayDate());
        txtToDate.setDate(Util1.getTodayDate());
        txtVouNo.setText(null);
        txtRemark.setText(null);
        txtRef.setText(null);
        traderAutoCompleter.setTrader(new Trader("-", "All"));
        stockAutoCompleter.setStock(new Stock("-", "All"));
        appUserAutoCompleter.setAppUser(new AppUser("-", "All"));
        saleManAutoCompleter.setSaleMan(new SaleMan("-", "All"));
        batchAutoCompeter.setBatch(new GRN("All"));
        currAutoCompleter.setCurrency(null);
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
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtFromDate = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        txtToDate = new com.toedter.calendar.JDateChooser();
        txtCus = new javax.swing.JTextField();
        txtVouNo = new javax.swing.JTextField();
        txtUser = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtStock = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        txtSaleMan = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtRef = new javax.swing.JTextField();
        txtLocation = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        chkDel = new javax.swing.JCheckBox();
        jLabel12 = new javax.swing.JLabel();
        txtDep = new javax.swing.JTextField();
        chkBatch = new javax.swing.JCheckBox();
        jLabel13 = new javax.swing.JLabel();
        txtBatchNo = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        txtProjectNo = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txtCurrency = new javax.swing.JTextField();
        chkLocal = new javax.swing.JCheckBox();
        jLabel16 = new javax.swing.JLabel();
        txtPayment = new javax.swing.JTextField();
        txtSearch = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblVoucher = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        txtPaid = new javax.swing.JFormattedTextField();
        lblTtlAmount1 = new javax.swing.JLabel();
        lblTtlRecord = new javax.swing.JLabel();
        txtTotalAmt = new javax.swing.JFormattedTextField();
        txtRecord = new javax.swing.JFormattedTextField();
        lblTtlAmount = new javax.swing.JLabel();
        lblTtlAmount2 = new javax.swing.JLabel();
        txtBag = new javax.swing.JFormattedTextField();
        lblTtlAmount3 = new javax.swing.JLabel();
        txtQty = new javax.swing.JFormattedTextField();
        lblTtlAmount4 = new javax.swing.JLabel();
        txtDis = new javax.swing.JFormattedTextField();
        progress = new javax.swing.JProgressBar();
        btnSearch1 = new javax.swing.JButton();
        btnSearch = new javax.swing.JButton();
        btnSearch2 = new javax.swing.JButton();

        setTitle("Sale Voucher Search");
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel2.setFont(Global.lableFont);
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Customer :");

        jLabel4.setFont(Global.lableFont);
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Vou No :");

        jLabel8.setFont(Global.lableFont);
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("User :");

        jLabel11.setFont(Global.lableFont);
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("From :");

        txtFromDate.setToolTipText("");
        txtFromDate.setDateFormatString("dd/MM/yyyy");
        txtFromDate.setFont(Global.lableFont);

        jLabel3.setFont(Global.lableFont);
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("To :");

        txtToDate.setToolTipText("");
        txtToDate.setDateFormatString("dd/MM/yyyy");
        txtToDate.setFont(Global.lableFont);

        txtCus.setFont(Global.textFont);
        txtCus.setName("txtCus"); // NOI18N
        txtCus.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCusFocusGained(evt);
            }
        });

        txtVouNo.setFont(Global.textFont);
        txtVouNo.setName("txtVouNo"); // NOI18N
        txtVouNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtVouNoFocusGained(evt);
            }
        });

        txtUser.setFont(Global.textFont);
        txtUser.setName("txtUser"); // NOI18N
        txtUser.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtUserFocusGained(evt);
            }
        });

        jLabel5.setFont(Global.lableFont);
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Remark :");

        txtRemark.setFont(Global.textFont);
        txtRemark.setName("txtVouNo"); // NOI18N
        txtRemark.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtRemarkFocusGained(evt);
            }
        });

        jLabel6.setFont(Global.lableFont);
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Stock :");

        txtStock.setFont(Global.textFont);
        txtStock.setName("txtCus"); // NOI18N
        txtStock.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtStockFocusGained(evt);
            }
        });

        jButton1.setFont(Global.lableFont);
        jButton1.setText("Clear Filter");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel7.setFont(Global.lableFont);
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Sale Man :");

        txtSaleMan.setFont(Global.textFont);
        txtSaleMan.setName("txtCus"); // NOI18N
        txtSaleMan.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSaleManFocusGained(evt);
            }
        });

        jLabel9.setFont(Global.lableFont);
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Reference :");

        txtRef.setFont(Global.textFont);
        txtRef.setName("txtVouNo"); // NOI18N
        txtRef.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtRefFocusGained(evt);
            }
        });

        txtLocation.setFont(Global.textFont);
        txtLocation.setName("txtCus"); // NOI18N
        txtLocation.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtLocationFocusGained(evt);
            }
        });

        jLabel10.setFont(Global.lableFont);
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Location :");

        chkDel.setFont(Global.lableFont);
        chkDel.setText("Deleted");
        chkDel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkDelActionPerformed(evt);
            }
        });

        jLabel12.setFont(Global.lableFont);
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setText("Branch :");

        txtDep.setFont(Global.textFont);
        txtDep.setName("txtUser"); // NOI18N
        txtDep.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDepFocusGained(evt);
            }
        });

        chkBatch.setFont(Global.lableFont);
        chkBatch.setText("No Batch");
        chkBatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkBatchActionPerformed(evt);
            }
        });

        jLabel13.setFont(Global.lableFont);
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel13.setText("Batch No :");

        txtBatchNo.setFont(Global.textFont);
        txtBatchNo.setName("txtVouNo"); // NOI18N
        txtBatchNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtBatchNoFocusGained(evt);
            }
        });

        jLabel14.setFont(Global.lableFont);
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel14.setText("PJ No :");

        txtProjectNo.setFont(Global.textFont);
        txtProjectNo.setName("txtVouNo"); // NOI18N
        txtProjectNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtProjectNoFocusGained(evt);
            }
        });

        jLabel15.setFont(Global.lableFont);
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel15.setText("Currency :");

        txtCurrency.setFont(Global.textFont);
        txtCurrency.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCurrency.setName("txtCurrency"); // NOI18N
        txtCurrency.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCurrencyFocusGained(evt);
            }
        });
        txtCurrency.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCurrencyActionPerformed(evt);
            }
        });

        chkLocal.setFont(Global.lableFont);
        chkLocal.setText("Local");
        chkLocal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkLocalActionPerformed(evt);
            }
        });

        jLabel16.setFont(Global.lableFont);
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel16.setText("Payment :");

        txtPayment.setFont(Global.textFont);
        txtPayment.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtPayment.setName("txtCurrency"); // NOI18N
        txtPayment.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPaymentFocusGained(evt);
            }
        });
        txtPayment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPaymentActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(8, 8, 8))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jLabel11)
                                        .addComponent(jLabel3)
                                        .addComponent(jLabel4)
                                        .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCus)
                            .addComponent(txtUser)
                            .addComponent(txtRemark)
                            .addComponent(txtStock)
                            .addComponent(txtSaleMan)
                            .addComponent(txtRef)
                            .addComponent(txtLocation)
                            .addComponent(txtDep)
                            .addComponent(txtBatchNo)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtToDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtFromDate, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
                                    .addComponent(txtVouNo))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(txtProjectNo)
                            .addComponent(txtCurrency, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtPayment, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(chkBatch, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkLocal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkDel)))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel10, jLabel11, jLabel12, jLabel13, jLabel14, jLabel15, jLabel3, jLabel4, jLabel6, jLabel7, jLabel8});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel11)
                    .addComponent(txtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(txtBatchNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(txtProjectNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtRef, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtCus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtSaleMan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(txtDep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtPayment)
                    .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkBatch)
                    .addComponent(chkDel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkLocal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel11, jLabel3, txtFromDate, txtToDate});

        txtSearch.setFont(Global.textFont);
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });

        tblVoucher.setFont(Global.textFont);
        tblVoucher.setModel(new javax.swing.table.DefaultTableModel(
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
        tblVoucher.setRowHeight(Global.tblRowHeight);
        tblVoucher.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblVoucherMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblVoucher);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        txtPaid.setEditable(false);
        txtPaid.setForeground(Color.green);
        txtPaid.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPaid.setFont(Global.amtFont);

        lblTtlAmount1.setFont(Global.lableFont);
        lblTtlAmount1.setText("Paid");

        lblTtlRecord.setFont(Global.lableFont);
        lblTtlRecord.setText("Records :");

        txtTotalAmt.setEditable(false);
        txtTotalAmt.setForeground(Color.red);
        txtTotalAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalAmt.setFont(Global.amtFont);

        txtRecord.setEditable(false);
        txtRecord.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtRecord.setFont(Global.amtFont);

        lblTtlAmount.setFont(Global.lableFont);
        lblTtlAmount.setText("Vou Total :");

        lblTtlAmount2.setFont(Global.lableFont);
        lblTtlAmount2.setText("Bag :");

        txtBag.setEditable(false);
        txtBag.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtBag.setFont(Global.amtFont);

        lblTtlAmount3.setFont(Global.lableFont);
        lblTtlAmount3.setText("Qty :");

        txtQty.setEditable(false);
        txtQty.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtQty.setFont(Global.amtFont);

        lblTtlAmount4.setFont(Global.lableFont);
        lblTtlAmount4.setText("Dis :");

        txtDis.setEditable(false);
        txtDis.setForeground(Color.green);
        txtDis.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDis.setFont(Global.amtFont);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTtlRecord)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtRecord)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTtlAmount3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtQty)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTtlAmount2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtBag)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTtlAmount4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDis)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTtlAmount1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPaid)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTtlAmount)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTotalAmt)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTtlRecord, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtRecord)
                    .addComponent(txtQty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTtlAmount3)
                    .addComponent(txtBag, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTtlAmount2)
                    .addComponent(lblTtlAmount1)
                    .addComponent(txtPaid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTotalAmt)
                    .addComponent(lblTtlAmount)
                    .addComponent(lblTtlAmount4)
                    .addComponent(txtDis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(8, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lblTtlAmount, lblTtlAmount1, lblTtlRecord, txtPaid, txtRecord, txtTotalAmt});

        btnSearch1.setFont(Global.lableFont);
        btnSearch1.setText("Print");
        btnSearch1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearch1ActionPerformed(evt);
            }
        });

        btnSearch.setBackground(Global.selectionColor);
        btnSearch.setFont(Global.lableFont);
        btnSearch.setForeground(new java.awt.Color(255, 255, 255));
        btnSearch.setText("Search");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        btnSearch2.setFont(Global.lableFont);
        btnSearch2.setText("Report");
        btnSearch2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearch2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(progress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtSearch)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSearch2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSearch1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSearch))
                            .addComponent(jScrollPane1)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(progress, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSearch1)
                            .addComponent(btnSearch)
                            .addComponent(btnSearch2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtCusFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCusFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCusFocusGained

    private void txtVouNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVouNoFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVouNoFocusGained

    private void txtUserFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtUserFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUserFocusGained

    private void txtRemarkFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRemarkFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRemarkFocusGained

    private void txtStockFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtStockFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtStockFocusGained

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        clearFilter();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void txtSaleManFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSaleManFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSaleManFocusGained

    private void txtRefFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRefFocusGained
        // TODO add your handling code here:

    }//GEN-LAST:event_txtRefFocusGained

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        // TODO add your handling code here:
        if (txtSearch.getText().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(tblFilter);
        }
    }//GEN-LAST:event_txtSearchKeyReleased

    private void tblVoucherMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVoucherMouseClicked
        // TODO add your handling code here:
        row = tblVoucher.convertRowIndexToModel(tblVoucher.getSelectedRow());
        if (evt.getClickCount() > 1) {
            select();
        }
    }//GEN-LAST:event_tblVoucherMouseClicked

    private void txtLocationFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtLocationFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtLocationFocusGained

    private void txtDepFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDepFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDepFocusGained

    private void chkBatchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkBatchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkBatchActionPerformed

    private void txtBatchNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBatchNoFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBatchNoFocusGained

    private void txtProjectNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtProjectNoFocusGained
    }//GEN-LAST:event_txtProjectNoFocusGained

    private void txtCurrencyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCurrencyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCurrencyActionPerformed

    private void txtCurrencyFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCurrencyFocusGained
    }//GEN-LAST:event_txtCurrencyFocusGained

    private void chkLocalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkLocalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkLocalActionPerformed

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        // TODO add your handling code here:
    }//GEN-LAST:event_formComponentResized

    private void btnSearch1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearch1ActionPerformed
        // TODO add your handling code here:
        print();
    }//GEN-LAST:event_btnSearch1ActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        // TODO add your handling code here:
        search();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void chkDelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkDelActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkDelActionPerformed

    private void txtPaymentFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPaymentFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPaymentFocusGained

    private void txtPaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPaymentActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPaymentActionPerformed

    private void btnSearch2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearch2ActionPerformed
        // TODO add your handling code here:
        viewReport();
    }//GEN-LAST:event_btnSearch2ActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnSearch1;
    private javax.swing.JButton btnSearch2;
    private javax.swing.JCheckBox chkBatch;
    private javax.swing.JCheckBox chkDel;
    private javax.swing.JCheckBox chkLocal;
    private javax.swing.JButton jButton1;
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
    private javax.swing.JLabel lblTtlAmount;
    private javax.swing.JLabel lblTtlAmount1;
    private javax.swing.JLabel lblTtlAmount2;
    private javax.swing.JLabel lblTtlAmount3;
    private javax.swing.JLabel lblTtlAmount4;
    private javax.swing.JLabel lblTtlRecord;
    private javax.swing.JProgressBar progress;
    private javax.swing.JTable tblVoucher;
    private javax.swing.JFormattedTextField txtBag;
    private javax.swing.JTextField txtBatchNo;
    private javax.swing.JTextField txtCurrency;
    private javax.swing.JTextField txtCus;
    private javax.swing.JTextField txtDep;
    private javax.swing.JFormattedTextField txtDis;
    private com.toedter.calendar.JDateChooser txtFromDate;
    private javax.swing.JTextField txtLocation;
    private javax.swing.JFormattedTextField txtPaid;
    private javax.swing.JTextField txtPayment;
    private javax.swing.JTextField txtProjectNo;
    private javax.swing.JFormattedTextField txtQty;
    private javax.swing.JFormattedTextField txtRecord;
    private javax.swing.JTextField txtRef;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JTextField txtSaleMan;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtStock;
    private com.toedter.calendar.JDateChooser txtToDate;
    private javax.swing.JFormattedTextField txtTotalAmt;
    private javax.swing.JTextField txtUser;
    private javax.swing.JTextField txtVouNo;
    // End of variables declaration//GEN-END:variables

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
