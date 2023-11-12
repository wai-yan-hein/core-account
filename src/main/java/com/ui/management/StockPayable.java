/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.ui.management;

import com.common.DecimalFormatRender;
import com.common.Global;
import com.common.PanelControl;
import com.common.ProUtil;
import com.common.ReportFilter;
import com.common.SelectionObserver;
import com.common.StartWithRowFilter;
import com.common.Util1;
import com.google.gson.reflect.TypeToken;
import com.inventory.editor.BrandAutoCompleter;
import com.inventory.editor.CategoryAutoCompleter;
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.editor.StockTypeAutoCompleter;
import com.repo.InventoryRepo;
import com.ui.management.common.SPConsignorSummaryTableModel;
import com.ui.management.common.SPCustomerSummaryTableModel;
import com.ui.management.dialog.SPWeightDetailDialog;
import com.ui.management.model.ClosingBalance;
import java.lang.reflect.Type;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Lenovo
 */
public class StockPayable extends javax.swing.JPanel implements SelectionObserver, PanelControl {

    public static final int SPCUS = 1;
    public static final int SPCON = 2;
    private StockTypeAutoCompleter stockTypeAutoCompleter;
    private BrandAutoCompleter brandAutoCompleter;
    private CategoryAutoCompleter categoryAutoCompleter;
    private LocationAutoCompleter locationAutoCompleter;
    private final SPCustomerSummaryTableModel customerSummaryTableModel = new SPCustomerSummaryTableModel();
    private final SPConsignorSummaryTableModel consignorSummaryTableModel = new SPConsignorSummaryTableModel();
    private InventoryRepo inventoryRepo;
    private JProgressBar progress;
    private SelectionObserver observer;
    private SPWeightDetailDialog dialog;
    private TableRowSorter<TableModel> sorter;
    private StartWithRowFilter swrf;
    private int type;

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    /**
     * Creates new form StockBalance
     *
     * @param type
     */
    public StockPayable(int type) {
        initComponents();
        this.type = type;
    }

    public void initMain() {
        setTodayDate();
        initCompeter();
        initModel();
        initTable();

    }

    private void setTodayDate() {
        txtFromDate.setDate(Util1.getTodayDate());
        txtToDate.setDate(Util1.getTodayDate());
    }

    private void initModel() {
        switch (type) {
            case 1 -> {
                tblBalance.setModel(customerSummaryTableModel);
                initTableWeight();
            }
            case 2 -> {
                tblBalance.setModel(consignorSummaryTableModel);
                initTableWeight();
            }

        }

    }

    private void initTableWeight() {
        tblBalance.getColumnModel().getColumn(0).setPreferredWidth(70);
        tblBalance.getColumnModel().getColumn(1).setPreferredWidth(30);
        tblBalance.getColumnModel().getColumn(2).setPreferredWidth(100);//op
        tblBalance.getColumnModel().getColumn(3).setPreferredWidth(20);
        tblBalance.getColumnModel().getColumn(4).setPreferredWidth(20);
        tblBalance.getColumnModel().getColumn(5).setPreferredWidth(20);
        tblBalance.getColumnModel().getColumn(6).setPreferredWidth(20);
        tblBalance.getColumnModel().getColumn(7).setPreferredWidth(20);
        tblBalance.getColumnModel().getColumn(8).setPreferredWidth(20);
        tblBalance.getColumnModel().getColumn(9).setPreferredWidth(20);
        tblBalance.getColumnModel().getColumn(10).setPreferredWidth(40);//cl
    }

    private void initTable() {
        tblBalance.getTableHeader().setFont(Global.tblHeaderFont);
        tblBalance.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblBalance.setRowHeight(Global.tblRowHeight);
        tblBalance.setFont(Global.textFont);
        tblBalance.setDefaultRenderer(Object.class, new DecimalFormatRender(2));
        tblBalance.setDefaultRenderer(Double.class, new DecimalFormatRender(2));
        tblBalance.setShowGrid(true);
        sorter = new TableRowSorter<>(tblBalance.getModel());
        tblBalance.setRowSorter(sorter);
        swrf = new StartWithRowFilter(txtSearch);
    }

    private void initCompeter() {
        stockTypeAutoCompleter = new StockTypeAutoCompleter(txtGroup, null, true);
        stockTypeAutoCompleter.setObserver(this);
        brandAutoCompleter = new BrandAutoCompleter(txtBrand, null, true);
        brandAutoCompleter.setObserver(this);
        categoryAutoCompleter = new CategoryAutoCompleter(txtCat, null, true);
        categoryAutoCompleter.setObserver(this);
        locationAutoCompleter = new LocationAutoCompleter(txtLoc, null, true, true);
        locationAutoCompleter.setObserver(this);
        inventoryRepo.getStockType().doOnSuccess((t) -> {
            stockTypeAutoCompleter.setListStockType(t);
        }).subscribe();
        inventoryRepo.getStockBrand().doOnSuccess((t) -> {
            brandAutoCompleter.setListStockBrand(t);
        }).subscribe();
        inventoryRepo.getCategory().doOnSuccess((t) -> {
            categoryAutoCompleter.setListCategory(t);
        }).subscribe();
        inventoryRepo.getDefaultLocation().doOnSuccess((t) -> {
            locationAutoCompleter.setLocation(t);
        }).subscribe();
        inventoryRepo.getLocation().doOnSuccess((t) -> {
            locationAutoCompleter.setListLocation(t);
        }).subscribe();
    }

    private void calculate() {
        btnCalculate.setEnabled(false);
        progress.setIndeterminate(true);
        inventoryRepo.getReport(getFilter()).doOnSuccess((t) -> {
            if (t.getFile() != null) {
                Type listType = new TypeToken<List<ClosingBalance>>() {
                }.getType();
                List<ClosingBalance> list = Util1.byteArrayToList(t.getFile(), listType);
                setListDetail(list);
            }
        }).doOnError((e) -> {
            btnCalculate.setEnabled(true);
            progress.setIndeterminate(false);
            JOptionPane.showMessageDialog(this, e.getMessage());
        }).doOnTerminate(() -> {
            calTotal();
            btnCalculate.setEnabled(true);
            progress.setIndeterminate(false);
        }).subscribe();
    }

    private void setListDetail(List<ClosingBalance> list) {
        switch (type) {
            case 1 ->
                customerSummaryTableModel.setListDetail(list);
            case 2 ->
                consignorSummaryTableModel.setListDetail(list);

        }
    }

    private List<ClosingBalance> getListDetail() {
        switch (type) {
            case SPCUS -> {
                return customerSummaryTableModel.getListDetail();
            }
            case SPCON -> {
                return consignorSummaryTableModel.getListDetail();
            }
        }
        return null;
    }

    private void calTotal() {
        List<ClosingBalance> list = getListDetail();
        double opQty = list.stream().mapToDouble((t) -> t.getOpenQty()).sum();
        double opWt = list.stream().mapToDouble((t) -> t.getOpenWeight()).sum();
        double outQty = list.stream().mapToDouble((t) -> t.getOutQty()).sum();
        double outWt = list.stream().mapToDouble((t) -> t.getOutWeight()).sum();
        double clQty = list.stream().mapToDouble((t) -> t.getBalQty()).sum();
        double clWt = list.stream().mapToDouble((t) -> t.getBalWeight()).sum();
        txtOpQty.setValue(opQty);
        txtOpWt.setValue(opWt);
        txtOutQty.setValue(outQty);
        txtOutWt.setValue(outWt);
        txtClQty.setValue(clQty);
        txtClWt.setValue(clWt);
        lblRecord.setText(String.valueOf(list.size()));

    }

    private ReportFilter getFilter() {
        String startDate = Util1.toDateStr(txtFromDate.getDate(), "yyyy-MM-dd");
        String endDate = Util1.toDateStr(txtToDate.getDate(), "yyyy-MM-dd");
        ReportFilter filter = new ReportFilter(Global.macId, Global.compCode, Global.deptId);
        filter.setFromDate(startDate);
        filter.setToDate(endDate);
        filter.setListLocation(locationAutoCompleter.getListOption());
        filter.setStockTypeCode(stockTypeAutoCompleter.getStockType().getKey().getStockTypeCode());
        filter.setBrandCode(brandAutoCompleter.getBrand().getKey().getBrandCode());
        filter.setCatCode(categoryAutoCompleter.getCategory().getKey().getCatCode());
        filter.setLocCode(locationAutoCompleter.getLocation().getKey().getLocCode());
        filter.setCalSale(Util1.getBoolean(ProUtil.isDisableSale()));
        filter.setCalPur(Util1.getBoolean(ProUtil.isDisablePur()));
        filter.setCalRI(Util1.getBoolean(ProUtil.isDisableRetIn()));
        filter.setCalRO(Util1.getBoolean(ProUtil.isDisableRetOut()));
        filter.setCalMill(Util1.getBoolean(ProUtil.isDisableMill()));
        filter.setReportName(getReportName());
        return filter;
    }

    private String getReportName() {
        switch (type) {
            case SPCUS -> {
                return "StockPayableCustomerSummary";
            }
            case SPCON -> {
                return "StockPayableConsignorSummary";
            }
        }
        return null;
    }

    private String getDetailReportName() {
        switch (type) {
            case SPCUS -> {
                return "StockPayableCustomerDetail";
            }
            case SPCON -> {
                return "StockPayableConsignorDetail";
            }
        }
        return null;
    }

    private void observeMain() {
        observer.selected("control", this);
        observer.selected("save", false);
        observer.selected("print", true);
        observer.selected("history", false);
        observer.selected("delete", false);
        observer.selected("refresh", true);
    }

    private ClosingBalance getObject(int row) {
        switch (type) {
            case 1 -> {
                return customerSummaryTableModel.getSelectVou(row);
            }
            case 2 -> {
                return consignorSummaryTableModel.getSelectVou(row);
            }
        }
        return null;
    }

    private void detailDialog() {
        if (dialog == null) {
            dialog = new SPWeightDetailDialog(Global.parentForm, type);
            dialog.setSize(Global.width - 20, Global.height - 20);
            dialog.setLocationRelativeTo(null);
            dialog.initMain();
        }
        int row = tblBalance.convertRowIndexToModel(tblBalance.getSelectedRow());
        if (row >= 0) {
            ClosingBalance b = getObject(row);
            String stockCode = b.getStockCode();
            String stockName = b.getStockName();
            String traderName = b.getTraderName();
            String traderCode = b.getTraderCode();
            if (!Util1.isNullOrEmpty(stockCode)) {
                tblBalance.setEnabled(false);
                progress.setIndeterminate(true);
                ReportFilter filter = getFilter();
                filter.setStockCode(stockCode);
                filter.setTraderCode(traderCode);
                filter.setReportName(getDetailReportName());
                inventoryRepo.getReport(filter).doOnSuccess((t) -> {
                    if (t.getFile() != null) {
                        Type listType = new TypeToken<List<ClosingBalance>>() {
                        }.getType();
                        List<ClosingBalance> list = Util1.byteArrayToList(t.getFile(), listType);
                        dialog.setListDetail(list);
                        dialog.setDescription(traderName, stockName);
                    }
                }).doOnError((e) -> {
                    tblBalance.setEnabled(true);
                    progress.setIndeterminate(false);
                    JOptionPane.showMessageDialog(this, e.getMessage());
                }).doOnTerminate(() -> {
                    tblBalance.setEnabled(true);
                    progress.setIndeterminate(false);
                    dialog.setVisible(true);
                }).subscribe();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Select Row.");
        }
    }

    private void searchTable() {
        if (txtSearch.getText().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(swrf);
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
        txtFromDate = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        txtToDate = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        txtGroup = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtBrand = new javax.swing.JTextField();
        txtCat = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtLoc = new javax.swing.JTextField();
        btnCalculate = new javax.swing.JButton();
        lblRecord = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblBalance = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        txtClWt = new javax.swing.JFormattedTextField();
        jLabel11 = new javax.swing.JLabel();
        txtClQty = new javax.swing.JFormattedTextField();
        txtOutWt = new javax.swing.JFormattedTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtOutQty = new javax.swing.JFormattedTextField();
        txtOpWt = new javax.swing.JFormattedTextField();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        txtOpQty = new javax.swing.JFormattedTextField();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("From Date");

        txtFromDate.setDateFormatString("dd/MM/yyyy");
        txtFromDate.setFont(Global.textFont);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("To Date");

        txtToDate.setDateFormatString("dd/MM/yyyy");
        txtToDate.setFont(Global.textFont);

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Group");

        txtGroup.setFont(Global.textFont);

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Brand");

        txtBrand.setFont(Global.textFont);

        txtCat.setFont(Global.textFont);

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Category");

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Location");

        txtLoc.setFont(Global.textFont);

        btnCalculate.setBackground(Global.selectionColor);
        btnCalculate.setFont(Global.lableFont);
        btnCalculate.setForeground(new java.awt.Color(255, 255, 255));
        btnCalculate.setText("Calculate");
        btnCalculate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCalculateActionPerformed(evt);
            }
        });

        lblRecord.setFont(Global.lableFont);
        lblRecord.setText("0");

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Records :");

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Search");

        txtSearch.setFont(Global.textFont);
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(txtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBrand, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtLoc, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCalculate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblRecord, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtBrand, txtCat, txtFromDate, txtGroup, txtLoc, txtToDate});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblRecord)
                        .addComponent(jLabel9))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtCat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnCalculate)
                        .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtFromDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(txtToDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtLoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtBrand, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tblBalance.setModel(new javax.swing.table.DefaultTableModel(
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
        tblBalance.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblBalanceMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblBalance);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Closing Weight");

        txtClWt.setEditable(false);
        txtClWt.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("###,###.##;(###,###.##)"))));
        txtClWt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtClWt.setFont(Global.amtFont);

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Closing Qty");

        txtClQty.setEditable(false);
        txtClQty.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("###,###.##;(###,###.##)"))));
        txtClQty.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtClQty.setFont(Global.amtFont);

        txtOutWt.setEditable(false);
        txtOutWt.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("###,###.##;(###,###.##)"))));
        txtOutWt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtOutWt.setFont(Global.amtFont);

        jLabel12.setFont(Global.lableFont);
        jLabel12.setText("Out Weight");

        jLabel13.setFont(Global.lableFont);
        jLabel13.setText("Out Qty");

        txtOutQty.setEditable(false);
        txtOutQty.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("###,###.##;(###,###.##)"))));
        txtOutQty.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtOutQty.setFont(Global.amtFont);

        txtOpWt.setEditable(false);
        txtOpWt.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("###,###.##;(###,###.##)"))));
        txtOpWt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtOpWt.setFont(Global.amtFont);

        jLabel20.setFont(Global.lableFont);
        jLabel20.setText("OP Weight");

        jLabel21.setFont(Global.lableFont);
        jLabel21.setText("OP Qty");

        txtOpQty.setEditable(false);
        txtOpQty.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("###,###.##;(###,###.##)"))));
        txtOpQty.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtOpQty.setFont(Global.amtFont);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtOpWt, javax.swing.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE)
                    .addComponent(txtOpQty, javax.swing.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtOutWt, javax.swing.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE)
                    .addComponent(txtOutQty, javax.swing.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtClWt, javax.swing.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE)
                    .addComponent(txtClQty, javax.swing.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtOutQty)
                        .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtOpQty))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtClQty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtOpWt)
                    .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtOutWt)
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtClWt)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnCalculateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalculateActionPerformed
        // TODO add your handling code here:
        calculate();
    }//GEN-LAST:event_btnCalculateActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observeMain();
    }//GEN-LAST:event_formComponentShown

    private void tblBalanceMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBalanceMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            detailDialog();
        }
    }//GEN-LAST:event_tblBalanceMouseClicked

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        // TODO add your handling code here:
        searchTable();
    }//GEN-LAST:event_txtSearchKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCalculate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblRecord;
    private javax.swing.JTable tblBalance;
    private javax.swing.JTextField txtBrand;
    private javax.swing.JTextField txtCat;
    private javax.swing.JFormattedTextField txtClQty;
    private javax.swing.JFormattedTextField txtClWt;
    private com.toedter.calendar.JDateChooser txtFromDate;
    private javax.swing.JTextField txtGroup;
    private javax.swing.JTextField txtLoc;
    private javax.swing.JFormattedTextField txtOpQty;
    private javax.swing.JFormattedTextField txtOpWt;
    private javax.swing.JFormattedTextField txtOutQty;
    private javax.swing.JFormattedTextField txtOutWt;
    private javax.swing.JTextField txtSearch;
    private com.toedter.calendar.JDateChooser txtToDate;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {
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
    }

    @Override
    public void refresh() {
        calculate();
    }

    @Override
    public void filter() {
    }

    @Override
    public String panelName() {
        return this.getName();
    }
}
