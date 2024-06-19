/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.ui.management;

import com.acc.dialog.FindDialog;
import com.common.ColumnColorCellRenderer;
import com.common.ComponentUtil;
import com.common.DecimalFormatRender;
import com.common.Global;
import com.common.PanelControl;
import com.common.ProUtil;
import com.common.ReportFilter;
import com.common.SelectionObserver;
import com.common.Util1;
import com.google.gson.reflect.TypeToken;
import com.inventory.editor.BrandAutoCompleter;
import com.inventory.editor.CategoryAutoCompleter;
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.editor.StockAutoCompleter;
import com.inventory.editor.StockTypeAutoCompleter;
import com.repo.InventoryRepo;
import com.ui.management.common.SBSummaryTableModel;
import com.ui.management.common.SBWeightSummaryTableModel;
import com.ui.management.dialog.SBWeightDetailDialog;
import com.ui.management.model.ClosingBalance;
import java.awt.Color;
import java.lang.reflect.Type;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.ListSelectionModel;

/**
 *
 * @author Lenovo
 */
public class StockBalance extends javax.swing.JPanel implements SelectionObserver, PanelControl {

    private StockTypeAutoCompleter stockTypeAutoCompleter;
    private BrandAutoCompleter brandAutoCompleter;
    private CategoryAutoCompleter categoryAutoCompleter;
    private LocationAutoCompleter locationAutoCompleter;
    private SBWeightSummaryTableModel sbwTableModel = new SBWeightSummaryTableModel();
    private SBSummaryTableModel stockBSummaryTableModel = new SBSummaryTableModel();
    private InventoryRepo inventoryRepo;
    private JProgressBar progress;
    private SelectionObserver observer;
    private SBWeightDetailDialog dialog;
    private StockAutoCompleter stockAutoCompleter;
    private FindDialog findDialog;

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
     */
    public StockBalance() {
        initComponents();
    }

    public void initMain() {
        ComponentUtil.addFocusListener(panelFilter);
        setTodayDate();
        initCompeter();
        initModel();
        initFindDialog();
        initTable();

    }

    private void initFindDialog() {
        findDialog = new FindDialog(Global.parentForm, tblBalance);
    }

    private void setTodayDate() {
        txtFromDate.setDate(Util1.getTodayDate());
        txtToDate.setDate(Util1.getTodayDate());
    }

    private void initModel() {
        if (!chkQty.isSelected()) {
            tblBalance.setModel(sbwTableModel);
            initTableWeight();
        } else {
            tblBalance.setModel(stockBSummaryTableModel);
            initQtyTable();
        }
    }

    private void initTableWeight() {
        tblBalance.getColumnModel().getColumn(0).setPreferredWidth(30);
        tblBalance.getColumnModel().getColumn(1).setPreferredWidth(100);
        tblBalance.getColumnModel().getColumn(2).setPreferredWidth(40);//op
        tblBalance.getColumnModel().getColumn(3).setPreferredWidth(20);//op
        tblBalance.getColumnModel().getColumn(4).setPreferredWidth(20);//pur
        tblBalance.getColumnModel().getColumn(5).setPreferredWidth(20);//pur
        tblBalance.getColumnModel().getColumn(6).setPreferredWidth(20);//in
        tblBalance.getColumnModel().getColumn(7).setPreferredWidth(20);//in
        tblBalance.getColumnModel().getColumn(8).setPreferredWidth(20);//sale
        tblBalance.getColumnModel().getColumn(9).setPreferredWidth(20);//sale
        tblBalance.getColumnModel().getColumn(10).setPreferredWidth(20);//out
        tblBalance.getColumnModel().getColumn(11).setPreferredWidth(20);//out
        tblBalance.getColumnModel().getColumn(12).setPreferredWidth(20);//cl
        tblBalance.getColumnModel().getColumn(13).setPreferredWidth(40);//cl
        tblBalance.getColumnModel().getColumn(4).setCellRenderer(new ColumnColorCellRenderer(Color.green));
        tblBalance.getColumnModel().getColumn(5).setCellRenderer(new ColumnColorCellRenderer(Color.green));
        tblBalance.getColumnModel().getColumn(6).setCellRenderer(new ColumnColorCellRenderer(Color.green));
        tblBalance.getColumnModel().getColumn(7).setCellRenderer(new ColumnColorCellRenderer(Color.green));

        tblBalance.getColumnModel().getColumn(8).setCellRenderer(new ColumnColorCellRenderer(Color.red));
        tblBalance.getColumnModel().getColumn(9).setCellRenderer(new ColumnColorCellRenderer(Color.red));
        tblBalance.getColumnModel().getColumn(10).setCellRenderer(new ColumnColorCellRenderer(Color.red));
        tblBalance.getColumnModel().getColumn(11).setCellRenderer(new ColumnColorCellRenderer(Color.red));

    }

    private void initQtyTable() {
        tblBalance.getColumnModel().getColumn(0).setPreferredWidth(30);
        tblBalance.getColumnModel().getColumn(1).setPreferredWidth(100);
        tblBalance.getColumnModel().getColumn(2).setPreferredWidth(40);//op
        tblBalance.getColumnModel().getColumn(3).setPreferredWidth(20);
        tblBalance.getColumnModel().getColumn(4).setPreferredWidth(20);
        tblBalance.getColumnModel().getColumn(5).setPreferredWidth(20);
        tblBalance.getColumnModel().getColumn(6).setPreferredWidth(20);
        tblBalance.getColumnModel().getColumn(7).setPreferredWidth(40);
    }

    private void initTable() {
        tblBalance.getTableHeader().setFont(Global.tblHeaderFont);
        tblBalance.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblBalance.setRowHeight(Global.tblRowHeight);
        tblBalance.setFont(Global.textFont);
        tblBalance.setDefaultRenderer(Object.class, new DecimalFormatRender(2));
        tblBalance.setDefaultRenderer(Double.class, new DecimalFormatRender(2));
        tblBalance.setShowGrid(true);
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
        stockAutoCompleter = new StockAutoCompleter(txtStock, inventoryRepo, null, true, ProUtil.isSSContain());
        stockAutoCompleter.setObserver(this);
        inventoryRepo.getStockType().doOnSuccess((t) -> {
            stockTypeAutoCompleter.setListStockType(t);
        }).subscribe();
        inventoryRepo.getStockBrand().doOnSuccess((t) -> {
            brandAutoCompleter.setListStockBrand(t);
        }).subscribe();
        inventoryRepo.getCategory().doOnSuccess((t) -> {
            categoryAutoCompleter.setListCategory(t);
        }).subscribe();
        inventoryRepo.getLocation().doOnSuccess((t) -> {
            locationAutoCompleter.setListLocation(t);
        }).then(inventoryRepo.getDefaultLocation().doOnSuccess((t) -> {
            locationAutoCompleter.setLocation(t);
        })).subscribe();

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
        if (!chkQty.isSelected()) {
            sbwTableModel.setListDetail(list);
        } else {
            stockBSummaryTableModel.setListDetail(list);
        }
    }

    private List<ClosingBalance> getListDetail() {
        if (!chkQty.isSelected()) {
            return sbwTableModel.getListDetail();
        } else {
            return stockBSummaryTableModel.getListDetail();
        }
    }

    private void calTotal() {
        List<ClosingBalance> list = getListDetail();
        double opQty = list.stream().mapToDouble((t) -> t.getOpenQty()).sum();
        double opWt = list.stream().mapToDouble((t) -> t.getOpenWeight()).sum();
        double purQty = list.stream().mapToDouble((t) -> t.getPurQty()).sum();
        double purWt = list.stream().mapToDouble((t) -> t.getPurWeight()).sum();
        double inQty = list.stream().mapToDouble((t) -> t.getInQty()).sum();
        double inWt = list.stream().mapToDouble((t) -> t.getInWeight()).sum();
        double outQty = list.stream().mapToDouble((t) -> t.getOutQty()).sum();
        double outWt = list.stream().mapToDouble((t) -> t.getOutWeight()).sum();
        double saleQty = list.stream().mapToDouble((t) -> t.getSaleQty()).sum();
        double saleWt = list.stream().mapToDouble((t) -> t.getSaleWeight()).sum();
        double clQty = list.stream().mapToDouble((t) -> t.getBalQty()).sum();
        double clWt = list.stream().mapToDouble((t) -> t.getBalWeight()).sum();
        txtOpQty.setValue(opQty);
        txtOpWt.setValue(opWt);
        txtPurQty.setValue(purQty);
        txtPurWt.setValue(purWt);
        txtInQty.setValue(inQty);
        txtInWt.setValue(inWt);
        txtOutQty.setValue(outQty);
        txtOutWt.setValue(outWt);
        txtSaleQty.setValue(saleQty);
        txtSaleWt.setValue(saleWt);
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
        filter.setStockCode(stockAutoCompleter.getStock().getKey().getStockCode());
        filter.setCalSale(Util1.getBoolean(ProUtil.isDisableSale()));
        filter.setCalPur(Util1.getBoolean(ProUtil.isDisablePur()));
        filter.setCalRI(Util1.getBoolean(ProUtil.isDisableRetIn()));
        filter.setCalRO(Util1.getBoolean(ProUtil.isDisableRetOut()));
        filter.setCalMill(Util1.getBoolean(ProUtil.isDisableMill()));
        String reportName = "StockInOutQtySummary"; //  : "StockInOutSummary"
        filter.setReportName(reportName);
        return filter;
    }

    private void observeMain() {
        observer.selected("control", this);
        observer.selected("save", false);
        observer.selected("print", true);
        observer.selected("history", false);
        observer.selected("delete", false);
        observer.selected("refresh", true);
    }

    private void detailDialog() {
        if (dialog == null) {
            dialog = new SBWeightDetailDialog(Global.parentForm);
            dialog.setSize(Global.width - 20, Global.height - 20);
            dialog.setLocationRelativeTo(null);
            dialog.initMain();
        }
        int row = tblBalance.convertRowIndexToModel(tblBalance.getSelectedRow());
        if (row >= 0) {
            ClosingBalance b = getClosingBalance(row);
            String stockCode = b.getStockCode();
            String stockName = b.getStockName();
            if (!Util1.isNullOrEmpty(stockCode)) {
                tblBalance.setEnabled(false);
                progress.setIndeterminate(true);
                ReportFilter filter = getFilter();
                filter.setStockCode(stockCode);
                String reportName = ProUtil.isUseWeight() ? "StockInOutDetailByWeight" : "StockInOutDetail";
                filter.setReportName(reportName);
                inventoryRepo.getReport(filter).doOnSuccess((t) -> {
                    if (t.getFile() != null) {
                        Type listType = new TypeToken<List<ClosingBalance>>() {
                        }.getType();
                        List<ClosingBalance> list = Util1.byteArrayToList(t.getFile(), listType);
                        dialog.setListDetail(list);
                        dialog.setStockName(stockName);
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

    private ClosingBalance getClosingBalance(int row) {
        if (ProUtil.isUseWeight()) {
            return sbwTableModel.getSelectVou(row);
        } else {
            return stockBSummaryTableModel.getSelectVou(row);
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

        panelFilter = new javax.swing.JPanel();
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
        txtStock = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        chkQty = new javax.swing.JCheckBox();
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
        jLabel14 = new javax.swing.JLabel();
        txtSaleWt = new javax.swing.JFormattedTextField();
        jLabel15 = new javax.swing.JLabel();
        txtSaleQty = new javax.swing.JFormattedTextField();
        jLabel16 = new javax.swing.JLabel();
        txtInWt = new javax.swing.JFormattedTextField();
        jLabel17 = new javax.swing.JLabel();
        txtInQty = new javax.swing.JFormattedTextField();
        jLabel18 = new javax.swing.JLabel();
        txtPurWt = new javax.swing.JFormattedTextField();
        jLabel19 = new javax.swing.JLabel();
        txtPurQty = new javax.swing.JFormattedTextField();
        txtOpWt = new javax.swing.JFormattedTextField();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        txtOpQty = new javax.swing.JFormattedTextField();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        panelFilter.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

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

        txtStock.setFont(Global.textFont);
        txtStock.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtStockKeyReleased(evt);
            }
        });

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Stock");

        chkQty.setSelected(true);
        chkQty.setText("Qty");
        chkQty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkQtyActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelFilterLayout = new javax.swing.GroupLayout(panelFilter);
        panelFilter.setLayout(panelFilterLayout);
        panelFilterLayout.setHorizontalGroup(
            panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFilterLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFilterLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFilterLayout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(txtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBrand, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtLoc, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkQty, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtStock, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addComponent(btnCalculate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 71, Short.MAX_VALUE)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblRecord, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        panelFilterLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtBrand, txtCat, txtFromDate, txtGroup, txtLoc, txtToDate});

        panelFilterLayout.setVerticalGroup(
            panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFilterLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblRecord)
                        .addComponent(jLabel9))
                    .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtCat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnCalculate))
                    .addComponent(txtFromDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(txtToDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtLoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(chkQty))
                        .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
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

        jLabel14.setFont(Global.lableFont);
        jLabel14.setText("Sale Weight");

        txtSaleWt.setEditable(false);
        txtSaleWt.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("###,###.##;(###,###.##)"))));
        txtSaleWt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSaleWt.setFont(Global.amtFont);

        jLabel15.setFont(Global.lableFont);
        jLabel15.setText("Sale Qty");

        txtSaleQty.setEditable(false);
        txtSaleQty.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("###,###.##;(###,###.##)"))));
        txtSaleQty.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSaleQty.setFont(Global.amtFont);

        jLabel16.setFont(Global.lableFont);
        jLabel16.setText("In Weight");

        txtInWt.setEditable(false);
        txtInWt.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("###,###.##;(###,###.##)"))));
        txtInWt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtInWt.setFont(Global.amtFont);

        jLabel17.setFont(Global.lableFont);
        jLabel17.setText("In Qty");

        txtInQty.setEditable(false);
        txtInQty.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("###,###.##;(###,###.##)"))));
        txtInQty.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtInQty.setFont(Global.amtFont);

        jLabel18.setFont(Global.lableFont);
        jLabel18.setText("Pur Weight");

        txtPurWt.setEditable(false);
        txtPurWt.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("###,###.##;(###,###.##)"))));
        txtPurWt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPurWt.setFont(Global.amtFont);

        jLabel19.setFont(Global.lableFont);
        jLabel19.setText("Pur Qty");

        txtPurQty.setEditable(false);
        txtPurQty.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("###,###.##;(###,###.##)"))));
        txtPurQty.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPurQty.setFont(Global.amtFont);

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
                    .addComponent(txtOpWt)
                    .addComponent(txtOpQty))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtPurWt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel16))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtPurQty)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtInWt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtInQty)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtSaleWt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtSaleQty)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtOutWt)
                    .addComponent(txtOutQty))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtClWt)
                    .addComponent(txtClQty))
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
                        .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtSaleQty)
                        .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtInQty)
                        .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtPurQty)
                        .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtOpQty))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtClQty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtOpWt)
                    .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtPurWt)
                    .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtInWt)
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtSaleWt)
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
                    .addComponent(panelFilter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
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

    private void txtStockKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtStockKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtStockKeyReleased

    private void chkQtyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkQtyActionPerformed
        initModel();        // TODO add your handling code here:
    }//GEN-LAST:event_chkQtyActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCalculate;
    private javax.swing.JCheckBox chkQty;
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
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblRecord;
    private javax.swing.JPanel panelFilter;
    private javax.swing.JTable tblBalance;
    private javax.swing.JTextField txtBrand;
    private javax.swing.JTextField txtCat;
    private javax.swing.JFormattedTextField txtClQty;
    private javax.swing.JFormattedTextField txtClWt;
    private com.toedter.calendar.JDateChooser txtFromDate;
    private javax.swing.JTextField txtGroup;
    private javax.swing.JFormattedTextField txtInQty;
    private javax.swing.JFormattedTextField txtInWt;
    private javax.swing.JTextField txtLoc;
    private javax.swing.JFormattedTextField txtOpQty;
    private javax.swing.JFormattedTextField txtOpWt;
    private javax.swing.JFormattedTextField txtOutQty;
    private javax.swing.JFormattedTextField txtOutWt;
    private javax.swing.JFormattedTextField txtPurQty;
    private javax.swing.JFormattedTextField txtPurWt;
    private javax.swing.JFormattedTextField txtSaleQty;
    private javax.swing.JFormattedTextField txtSaleWt;
    private javax.swing.JTextField txtStock;
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
        findDialog.setVisible(!findDialog.isVisible());
    }

    @Override
    public String panelName() {
        return this.getName();
    }
}
