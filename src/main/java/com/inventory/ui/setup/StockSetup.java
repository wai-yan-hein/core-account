/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup;

import com.inventory.common.Global;
import com.inventory.common.StartWithRowFilter;
import com.inventory.common.TableCellRender;
import com.inventory.common.Util1;
import com.inventory.editor.BrandAutoCompleter;
import com.inventory.editor.CategoryAutoCompleter;
import com.inventory.editor.StockTypeAutoCompleter;
import com.inventory.editor.UnitAutoCompleter;
import com.inventory.model.StockType;
import com.inventory.model.Stock;
import com.inventory.ui.setup.common.StockTableModel;
import com.inventory.ui.setup.dialog.CategorySetupDialog;
import com.inventory.ui.setup.dialog.StockBrandSetupDialog;
import com.inventory.ui.setup.dialog.StockTypeSetupDialog;
import com.inventory.ui.setup.dialog.StockUnitSetupDailog;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.JTextComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 *
 * @author Lenovo
 */
@Component
public class StockSetup extends javax.swing.JPanel implements KeyListener {

    private final Image icon = new ImageIcon(getClass().getResource("/images/settings_26px.png")).getImage();
    private int selectRow = -1;
    @Autowired
    private StockTypeSetupDialog itemTypeSetupDialog;
    @Autowired
    private CategorySetupDialog categorySetupDailog;
    @Autowired
    private StockBrandSetupDialog itemBrandDailog;
    @Autowired
    private StockUnitSetupDailog itemUnitSetupDailog;
    @Autowired
    private StockTableModel stockTableModel;
    @Autowired
    private WebClient webClient;
    private StockTypeAutoCompleter typeAutoCompleter;
    private CategoryAutoCompleter categoryAutoCompleter;
    private BrandAutoCompleter brandAutoCompleter;
    private UnitAutoCompleter purUnitCompleter;
    private UnitAutoCompleter saleUnitCompleter;
    private Stock stock = new Stock();
    private boolean isShown = false;

    private TableRowSorter<TableModel> sorter;
    private StartWithRowFilter swrf;

    public void setIsShown(boolean isShown) {
        this.isShown = isShown;
        clear();
    }

    /**
     * Creates new form StockSetup
     */
    public StockSetup() {
        initComponents();
        initKeyListener();
    }

    private void initMain() {
        txtStockCode.requestFocus();
        initCombo();
        initTable();
        searchStock();
        clear();
        isShown = true;

    }

    private void initTable() {
        stockTableModel.setListStock(Global.listStock);
        tblStock.getTableHeader().setFont(Global.tblHeaderFont);
        tblStock.setModel(stockTableModel);
        tblStock.setDefaultRenderer(Boolean.class, new TableCellRender());
        tblStock.setDefaultRenderer(Object.class, new TableCellRender());
        tblStock.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblStock.getColumnModel().getColumn(0).setPreferredWidth(10);
        tblStock.getColumnModel().getColumn(0).setPreferredWidth(100);
        tblStock.getColumnModel().getColumn(0).setPreferredWidth(10);
        tblStock.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblStock.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting()) {
                if (tblStock.getSelectedRow() >= 0) {
                    selectRow = tblStock.convertRowIndexToModel(tblStock.getSelectedRow());
                    setStock(selectRow);
                }
            }
        });
        sorter = new TableRowSorter<>(tblStock.getModel());
        tblStock.setRowSorter(sorter);
        swrf = new StartWithRowFilter(txtFilter);
    }

    private void setStock(int row) {
        stock = stockTableModel.getStock(row);
        txtBarCode.setText(stock.getBarcode());
        txtStockCode.setText(stock.getUserCode());
        txtStockName.setText(stock.getStockName());
        chkActive.setSelected(Util1.getBoolean(stock.getIsActive()));
        brandAutoCompleter.setBrand(stock.getBrand());
        categoryAutoCompleter.setCategory(stock.getCategory());
        saleUnitCompleter.setStockUnit(stock.getSaleUnit());
        typeAutoCompleter.setStockType(stock.getStockType());
        purUnitCompleter.setStockUnit(stock.getPurUnit());
        txtPurPrice.setText(Util1.getString(stock.getPurPrice()));
        txtSalePrice.setText(Util1.getString(stock.getSalePriceN()));
        txtSalePriceA.setText(Util1.getString(stock.getSalePriceA()));
        txtSalePriceB.setText((Util1.getString(stock.getSalePriceB())));
        txtSalePriceC.setText(Util1.getString(stock.getSalePriceC()));
        txtSalePriceD.setText(Util1.getString(stock.getSalePriceD()));
        txtSalePriceStd.setText(Util1.getString(stock.getSttCostPrice()));
        txtExpDate.setDate(stock.getExpireDate());
        lblStatus.setText("EDIT");
    }

    private void searchStock() {
        stockTableModel.setListStock(Global.listStock);
    }

    private void initCombo() {
        typeAutoCompleter = new StockTypeAutoCompleter(txtType, Global.listStockType, null);
        typeAutoCompleter.setStockType(null);
        categoryAutoCompleter = new CategoryAutoCompleter(txtCat, Global.listCategory, null);
        categoryAutoCompleter.setCategory(null);
        brandAutoCompleter = new BrandAutoCompleter(txtBrand, Global.listStockBrand, null);
        brandAutoCompleter.setBrand(null);
        purUnitCompleter = new UnitAutoCompleter(txtPurUnit, Global.listStockUnit, null);
        purUnitCompleter.setStockUnit(null);
        saleUnitCompleter = new UnitAutoCompleter(txtSaleUnit, Global.listStockUnit, null);
        saleUnitCompleter.setStockUnit(null);

    }

    private boolean isValidEntry() {
        boolean status = true;
        if (txtStockName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Stock name must not be blank.",
                    "Stock name.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtStockName.requestFocus();
        } else if (typeAutoCompleter.getStockType() == null) {
            JOptionPane.showMessageDialog(this, "You must choose stock type.",
                    "Stock Type.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtType.requestFocus();
        } else if (purUnitCompleter.getStockUnit() == null) {
            JOptionPane.showMessageDialog(this, "Purchase Unit  cannot be blank.",
                    "Stock Unit Pattern.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtPurUnit.requestFocus();
        } else {
            stock.setExpireDate(txtExpDate.getDate());
            stock.setUserCode(txtStockCode.getText().trim());
            stock.setStockType((StockType) typeAutoCompleter.getStockType());
            stock.setStockName(txtStockName.getText().trim());
            stock.setCategory(categoryAutoCompleter.getCategory());
            stock.setBrand(brandAutoCompleter.getBrand());
            stock.setIsActive(chkActive.isSelected());
            stock.setBarcode(txtBarCode.getText().trim());
            stock.setPurPrice(Util1.getFloat(txtPurPrice.getText()));
            stock.setPurUnit(purUnitCompleter.getStockUnit());
            stock.setSaleUnit(saleUnitCompleter.getStockUnit());
            stock.setSalePriceN(Util1.getFloat(txtSalePrice.getText()));
            stock.setSalePriceA(Util1.getFloat(txtSalePriceA.getText()));
            stock.setSalePriceB(Util1.getFloat(txtSalePriceB.getText()));
            stock.setSalePriceC(Util1.getFloat(txtSalePriceC.getText()));
            stock.setSalePriceD(Util1.getFloat(txtSalePriceD.getText()));
            stock.setSttCostPrice(Util1.getFloat(txtSalePriceStd.getText()));

            if (lblStatus.getText().equals("NEW")) {
                stock.setMacId(Global.machineId);
                stock.setCompCode(Global.compCode);
                stock.setCreatedDate(Util1.getTodayDate());
                stock.setCreatedBy(Global.loginUser);
            } else {
                stock.setUpdatedBy(Global.loginUser);
            }
        }

        return status;
    }

    private void saveStock() {
        if (isValidEntry()) {
            Mono<Stock> result = webClient.post()
                    .uri("/setup/save-stock")
                    .body(Mono.just(stock), Stock.class)
                    .retrieve()
                    .bodyToMono(Stock.class);
            result.subscribe((t) -> {
                if (t != null) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Saved");
                    if (lblStatus.getText().equals("EDIT")) {
                        Global.listStock.set(selectRow, stock);
                    } else {
                        Global.listStock.add(t);
                    }
                    clear();
                }
            }, (e) -> {
                JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
            });
        }
    }

    public void clear() {
        brandAutoCompleter.setBrand(null);
        categoryAutoCompleter.setCategory(null);
        purUnitCompleter.setStockUnit(null);
        saleUnitCompleter.setStockUnit(null);
        typeAutoCompleter.setStockType(null);
        txtBarCode.setText(null);
        txtExpDate.setDate(null);
        txtStockCode.setText(null);
        txtStockName.setText(null);
        chkActive.setSelected(true);
        txtPurPrice.setText(null);
        txtSalePriceStd.setText(null);
        txtSalePrice.setText(null);
        txtSalePriceA.setText(null);
        txtSalePriceB.setText(null);
        txtSalePriceC.setText(null);
        txtSalePriceD.setText(null);
        lblStatus.setText("NEW");
        txtStockCode.setEnabled(true);
        txtStockCode.requestFocus();
        stockTableModel.refresh();
        stock = new Stock();

    }

    private void initKeyListener() {
        txtBarCode.addKeyListener(this);
        txtExpDate.getDateEditor().getUiComponent().setName("txtExpDate");
        txtExpDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtSalePriceStd.addKeyListener(this);
        txtSalePrice.addKeyListener(this);
        txtSalePriceA.addKeyListener(this);
        txtSalePriceB.addKeyListener(this);
        txtSalePriceC.addKeyListener(this);
        txtSalePriceD.addKeyListener(this);
        txtPurPrice.addKeyListener(this);
        txtStockCode.addKeyListener(this);
        txtStockName.addKeyListener(this);
        txtTotalCount.addKeyListener(this);
        txtType.addKeyListener(this);
        txtCat.addKeyListener(this);
        txtBrand.addKeyListener(this);
        chkActive.addKeyListener(this);
        btnAddBrand.addKeyListener(this);
        btnAddCategory.addKeyListener(this);
        btnAddItemType.addKeyListener(this);
        btnUnit.addKeyListener(this);
        tblStock.addKeyListener(this);
        txtPurUnit.addKeyListener(this);
        txtSaleUnit.addKeyListener(this);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblStock = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtStockCode = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        btnAddItemType = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        txtStockName = new javax.swing.JTextField();
        btnAddCategory = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        btnAddBrand = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        txtBarCode = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        chkActive = new javax.swing.JCheckBox();
        lblStatus = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtSalePrice = new javax.swing.JTextField();
        txtSalePriceA = new javax.swing.JTextField();
        txtSalePriceB = new javax.swing.JTextField();
        txtSalePriceC = new javax.swing.JTextField();
        txtSalePriceD = new javax.swing.JTextField();
        txtSalePriceStd = new javax.swing.JTextField();
        txtSaleUnit = new javax.swing.JTextField();
        txtExpDate = new com.toedter.calendar.JDateChooser();
        btnAddItemType1 = new javax.swing.JButton();
        txtType = new javax.swing.JTextField();
        txtCat = new javax.swing.JTextField();
        txtBrand = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        txtPurPrice = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtPurUnit = new javax.swing.JTextField();
        btnUnit = new javax.swing.JButton();
        btnAddItemType2 = new javax.swing.JButton();
        btnAddItemType3 = new javax.swing.JButton();
        txtTotalCount = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        txtFilter = new javax.swing.JTextField();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        tblStock.setFont(Global.textFont);
        tblStock.setModel(new javax.swing.table.DefaultTableModel(
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
        tblStock.setName("tblStock"); // NOI18N
        tblStock.setRowHeight(Global.tblRowHeight);
        jScrollPane1.setViewportView(tblStock);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Stock Code");

        txtStockCode.setFont(Global.textFont);
        txtStockCode.setName("txtStockCode"); // NOI18N
        txtStockCode.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtStockCodeFocusGained(evt);
            }
        });
        txtStockCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtStockCodeActionPerformed(evt);
            }
        });

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Stock Type");

        btnAddItemType.setFont(Global.lableFont);
        btnAddItemType.setText("Add Stock Type");
        btnAddItemType.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnAddItemType.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        btnAddItemType.setName("btnAddItemType"); // NOI18N
        btnAddItemType.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnAddItemType.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddItemType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddItemTypeActionPerformed(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Stock Name");

        txtStockName.setFont(Global.textFont);
        txtStockName.setName("txtStockName"); // NOI18N
        txtStockName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtStockNameFocusGained(evt);
            }
        });

        btnAddCategory.setFont(Global.lableFont);
        btnAddCategory.setText("Add Category");
        btnAddCategory.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnAddCategory.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        btnAddCategory.setName("btnAddCategory"); // NOI18N
        btnAddCategory.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnAddCategory.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddCategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddCategoryActionPerformed(evt);
            }
        });

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Category");

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Brand");

        btnAddBrand.setFont(Global.lableFont);
        btnAddBrand.setText("Add Brand");
        btnAddBrand.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnAddBrand.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        btnAddBrand.setName("btnAddBrand"); // NOI18N
        btnAddBrand.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnAddBrand.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddBrand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddBrandActionPerformed(evt);
            }
        });

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Bar Code");

        txtBarCode.setFont(Global.textFont);
        txtBarCode.setName("txtBarCode"); // NOI18N
        txtBarCode.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtBarCodeFocusGained(evt);
            }
        });

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Licene Exp Date");

        chkActive.setFont(Global.lableFont);
        chkActive.setSelected(true);
        chkActive.setText("Active");
        chkActive.setName("chkActive"); // NOI18N

        lblStatus.setFont(Global.lableFont);
        lblStatus.setText("NEW");

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Sale Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, Global.lableFont));

        jLabel11.setFont(Global.lableFont);
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel11.setText("Sale Price");

        jLabel14.setFont(Global.lableFont);
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel14.setText("Sale Price A");

        jLabel15.setFont(Global.lableFont);
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel15.setText("Sale Price B");

        jLabel16.setFont(Global.lableFont);
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel16.setText("Sale Price C");

        jLabel17.setFont(Global.lableFont);
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel17.setText("Sale Price D");

        jLabel18.setFont(Global.lableFont);
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel18.setText("Cost Price Std");

        jLabel12.setFont(Global.lableFont);
        jLabel12.setText("Sale Unit");

        txtSalePrice.setFont(Global.textFont);
        txtSalePrice.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSalePrice.setName("txtSalePrice"); // NOI18N
        txtSalePrice.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSalePriceFocusGained(evt);
            }
        });
        txtSalePrice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSalePriceActionPerformed(evt);
            }
        });

        txtSalePriceA.setFont(Global.textFont);
        txtSalePriceA.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSalePriceA.setName("txtSalePriceA"); // NOI18N
        txtSalePriceA.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSalePriceAFocusGained(evt);
            }
        });

        txtSalePriceB.setFont(Global.textFont);
        txtSalePriceB.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSalePriceB.setName("txtSalePriceB"); // NOI18N
        txtSalePriceB.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSalePriceBFocusGained(evt);
            }
        });
        txtSalePriceB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSalePriceBActionPerformed(evt);
            }
        });

        txtSalePriceC.setFont(Global.textFont);
        txtSalePriceC.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSalePriceC.setName("txtSalePriceC"); // NOI18N
        txtSalePriceC.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSalePriceCFocusGained(evt);
            }
        });

        txtSalePriceD.setFont(Global.textFont);
        txtSalePriceD.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSalePriceD.setName("txtSalePriceD"); // NOI18N
        txtSalePriceD.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSalePriceDFocusGained(evt);
            }
        });

        txtSalePriceStd.setFont(Global.textFont);
        txtSalePriceStd.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSalePriceStd.setName("txtSalePriceStd"); // NOI18N
        txtSalePriceStd.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSalePriceStdFocusGained(evt);
            }
        });

        txtSaleUnit.setFont(Global.textFont);
        txtSaleUnit.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtSaleUnit.setName("txtSaleUnit"); // NOI18N
        txtSaleUnit.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSaleUnitFocusGained(evt);
            }
        });
        txtSaleUnit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSaleUnitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtSaleUnit))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtSalePriceA, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
                            .addComponent(txtSalePrice, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtSalePriceB))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(txtSalePriceC, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                                    .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtSalePriceD)
                                    .addComponent(txtSalePriceStd))))))
                .addGap(4, 4, 4))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(txtSaleUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(txtSalePrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(txtSalePriceC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(txtSalePriceA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17)
                    .addComponent(txtSalePriceD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(txtSalePriceB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18)
                    .addComponent(txtSalePriceStd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        txtExpDate.setDateFormatString("dd/MM/yyyy");
        txtExpDate.setFont(Global.textFont);
        txtExpDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtExpDateFocusGained(evt);
            }
        });

        btnAddItemType1.setFont(Global.lableFont);
        btnAddItemType1.setText("Import");
        btnAddItemType1.setName("btnAddItemType"); // NOI18N
        btnAddItemType1.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnAddItemType1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddItemType1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddItemType1ActionPerformed(evt);
            }
        });

        txtType.setFont(Global.textFont);
        txtType.setName("txtType"); // NOI18N

        txtCat.setFont(Global.textFont);
        txtCat.setName("txtCat"); // NOI18N

        txtBrand.setFont(Global.textFont);
        txtBrand.setName("txtBrand"); // NOI18N
        txtBrand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBrandActionPerformed(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Purchase Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, Global.lableFont));

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Purchase Unit");

        txtPurPrice.setFont(Global.textFont);
        txtPurPrice.setToolTipText("Purchase Price");
        txtPurPrice.setName("txtPurPrice"); // NOI18N
        txtPurPrice.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPurPriceFocusGained(evt);
            }
        });

        jLabel13.setFont(Global.lableFont);
        jLabel13.setText("Purchase Price");

        txtPurUnit.setFont(Global.textFont);
        txtPurUnit.setToolTipText("Purchase Price");
        txtPurUnit.setName("txtPurUnit"); // NOI18N
        txtPurUnit.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPurUnitFocusGained(evt);
            }
        });

        btnUnit.setFont(Global.lableFont);
        btnUnit.setText("Add Unit");
        btnUnit.setName("btnUnit"); // NOI18N
        btnUnit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUnitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(txtPurUnit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnUnit))
                    .addComponent(txtPurPrice))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(txtPurUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnUnit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtPurPrice, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        btnAddItemType2.setFont(Global.lableFont);
        btnAddItemType2.setText("Save");
        btnAddItemType2.setName("btnAddItemType"); // NOI18N
        btnAddItemType2.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnAddItemType2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddItemType2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddItemType2ActionPerformed(evt);
            }
        });

        btnAddItemType3.setFont(Global.lableFont);
        btnAddItemType3.setText("New");
        btnAddItemType3.setName("btnAddItemType"); // NOI18N
        btnAddItemType3.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnAddItemType3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddItemType3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddItemType3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(txtExpDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(chkActive)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(txtBarCode)
                                    .addComponent(txtStockName)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtCat)
                                            .addComponent(txtBrand))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(btnAddBrand, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(btnAddCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(6, 6, 6))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtType)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnAddItemType))
                            .addComponent(txtStockCode))
                        .addContainerGap())))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnAddItemType1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnAddItemType3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnAddItemType2)
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel3, jLabel4, jLabel5, jLabel7, jLabel9});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnAddBrand, btnAddCategory, btnAddItemType});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtStockCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtStockName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2))
                    .addComponent(btnAddItemType))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(txtCat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnAddCategory))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(txtBrand, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtBarCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel9)
                                .addComponent(lblStatus)
                                .addComponent(chkActive))
                            .addComponent(txtExpDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnAddBrand))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAddItemType1)
                    .addComponent(btnAddItemType2)
                    .addComponent(btnAddItemType3))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        txtTotalCount.setEditable(false);
        txtTotalCount.setFont(Global.lableFont);
        txtTotalCount.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel19.setFont(Global.lableFont);
        jLabel19.setText("Total Stock  :");

        txtFilter.setFont(Global.textFont);
        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addGap(18, 18, 18)
                        .addComponent(txtTotalCount))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 631, Short.MAX_VALUE)
                    .addComponent(txtFilter))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTotalCount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19)))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddItemTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddItemTypeActionPerformed
        // TODO add your handling code here:
        itemTypeSetupDialog.setIconImage(icon);
        itemTypeSetupDialog.initMain();
        itemTypeSetupDialog.setSize(Global.width / 2, Global.height / 2);
        itemTypeSetupDialog.setLocationRelativeTo(null);
        itemTypeSetupDialog.setVisible(true);
    }//GEN-LAST:event_btnAddItemTypeActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        /* mainFrame.setControl(this);
        mainFrame.setFilterObserver(this);*/
        if (!isShown) {
            initMain();
        }
    }//GEN-LAST:event_formComponentShown

    private void btnAddCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddCategoryActionPerformed
        // TODO add your handling code here:
        categorySetupDailog.setIconImage(icon);
        categorySetupDailog.initMain();
        categorySetupDailog.setSize(Global.width / 2, Global.height / 2);
        categorySetupDailog.setLocationRelativeTo(null);
        categorySetupDailog.setVisible(true);
    }//GEN-LAST:event_btnAddCategoryActionPerformed

    private void btnAddBrandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddBrandActionPerformed
        // TODO add your handling code here:
        itemBrandDailog.setIconImage(icon);
        itemBrandDailog.initMain();
        itemBrandDailog.setSize(Global.width / 2, Global.height / 2);
        itemBrandDailog.setLocationRelativeTo(null);
        itemBrandDailog.setVisible(true);
    }//GEN-LAST:event_btnAddBrandActionPerformed

    private void btnUnitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUnitActionPerformed
        // TODO add your handling code here:
        itemUnitSetupDailog.setIconImage(icon);
        itemUnitSetupDailog.initMain();
        itemUnitSetupDailog.setSize(Global.width / 2, Global.height / 2);
        itemUnitSetupDailog.setLocationRelativeTo(null);
        itemUnitSetupDailog.setVisible(true);

    }//GEN-LAST:event_btnUnitActionPerformed

    private void txtStockNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtStockNameFocusGained
        // TODO add your handling code here:
        txtStockName.selectAll();
    }//GEN-LAST:event_txtStockNameFocusGained

    private void txtBarCodeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBarCodeFocusGained
        // TODO add your handling code here:
        txtBarCode.selectAll();
    }//GEN-LAST:event_txtBarCodeFocusGained

    private void txtExpDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtExpDateFocusGained
        // TODO add your handling code here:

    }//GEN-LAST:event_txtExpDateFocusGained

    private void txtPurPriceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPurPriceFocusGained
        // TODO add your handling code here:
        txtPurPrice.selectAll();
    }//GEN-LAST:event_txtPurPriceFocusGained

    private void txtSalePriceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSalePriceFocusGained
        // TODO add your handling code here:
        txtSalePrice.selectAll();
    }//GEN-LAST:event_txtSalePriceFocusGained

    private void txtSalePriceAFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSalePriceAFocusGained
        // TODO add your handling code here:
        txtSalePriceA.selectAll();
    }//GEN-LAST:event_txtSalePriceAFocusGained

    private void txtSalePriceBFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSalePriceBFocusGained
        // TODO add your handling code here:
        txtSalePriceB.selectAll();
    }//GEN-LAST:event_txtSalePriceBFocusGained

    private void txtSalePriceCFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSalePriceCFocusGained
        // TODO add your handling code here:
        txtSalePriceC.selectAll();
    }//GEN-LAST:event_txtSalePriceCFocusGained

    private void txtSalePriceDFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSalePriceDFocusGained
        // TODO add your handling code here:
        txtSalePriceD.selectAll();
    }//GEN-LAST:event_txtSalePriceDFocusGained

    private void txtSalePriceStdFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSalePriceStdFocusGained
        // TODO add your handling code here:
        txtSalePriceStd.selectAll();
    }//GEN-LAST:event_txtSalePriceStdFocusGained

    private void btnAddItemType1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddItemType1ActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_btnAddItemType1ActionPerformed

    private void txtSalePriceBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSalePriceBActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSalePriceBActionPerformed

    private void txtSalePriceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSalePriceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSalePriceActionPerformed

    private void txtPurUnitFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPurUnitFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPurUnitFocusGained

    private void txtStockCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtStockCodeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtStockCodeActionPerformed

    private void txtBrandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBrandActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBrandActionPerformed

    private void txtSaleUnitFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSaleUnitFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSaleUnitFocusGained

    private void txtSaleUnitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSaleUnitActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSaleUnitActionPerformed

    private void btnAddItemType2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddItemType2ActionPerformed
        // TODO add your handling code here:
        saveStock();
    }//GEN-LAST:event_btnAddItemType2ActionPerformed

    private void btnAddItemType3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddItemType3ActionPerformed
        // TODO add your handling code here:
        clear();
    }//GEN-LAST:event_btnAddItemType3ActionPerformed

    private void txtFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyReleased
        // TODO add your handling code here:
        if (txtFilter.getText().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(swrf);
        }
    }//GEN-LAST:event_txtFilterKeyReleased

    private void txtStockCodeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtStockCodeFocusGained
        // TODO add your handling code here:
        txtStockCode.requestFocus();
    }//GEN-LAST:event_txtStockCodeFocusGained


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddBrand;
    private javax.swing.JButton btnAddCategory;
    private javax.swing.JButton btnAddItemType;
    private javax.swing.JButton btnAddItemType1;
    private javax.swing.JButton btnAddItemType2;
    private javax.swing.JButton btnAddItemType3;
    private javax.swing.JButton btnUnit;
    private javax.swing.JCheckBox chkActive;
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
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblStock;
    private javax.swing.JTextField txtBarCode;
    private javax.swing.JTextField txtBrand;
    private javax.swing.JTextField txtCat;
    private com.toedter.calendar.JDateChooser txtExpDate;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JTextField txtPurPrice;
    private javax.swing.JTextField txtPurUnit;
    private javax.swing.JTextField txtSalePrice;
    private javax.swing.JTextField txtSalePriceA;
    private javax.swing.JTextField txtSalePriceB;
    private javax.swing.JTextField txtSalePriceC;
    private javax.swing.JTextField txtSalePriceD;
    private javax.swing.JTextField txtSalePriceStd;
    private javax.swing.JTextField txtSaleUnit;
    private javax.swing.JTextField txtStockCode;
    private javax.swing.JTextField txtStockName;
    private javax.swing.JTextField txtTotalCount;
    private javax.swing.JTextField txtType;
    // End of variables declaration//GEN-END:variables

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        Object sourceObj = e.getSource();
        String ctrlName = "-";

        if (sourceObj instanceof JTable jTable) {
            ctrlName = jTable.getName();
        } else if (sourceObj instanceof JTextField jTextField) {
            ctrlName = jTextField.getName();
        } else if (sourceObj instanceof JButton jButton) {
            ctrlName = jButton.getName();
        } else if (sourceObj instanceof JCheckBox jCheckBox) {
            ctrlName = jCheckBox.getName();
        } else if (sourceObj instanceof JFormattedTextField jFormattedTextField) {
            ctrlName = jFormattedTextField.getName();
        } else if (sourceObj instanceof JTextComponent jTextComponent) {
            ctrlName = jTextComponent.getName();
        } else if (sourceObj instanceof JTextFieldDateEditor jTextFieldDateEditor) {
            ctrlName = jTextFieldDateEditor.getName();
        }
        switch (ctrlName) {
            case "txtStockCode" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtStockName.requestFocus();
                    case KeyEvent.VK_DOWN ->
                        txtStockName.requestFocus();
                    case KeyEvent.VK_UP -> {
                    }
                    case KeyEvent.VK_RIGHT ->
                        txtType.requestFocus();
                    case KeyEvent.VK_LEFT -> {
                    }
                }
                //btnSave.requestFocus();
                //btnSave.requestFocus();
                tabToTable(e);
            }
            case "txtType" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtCat.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        txtStockCode.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        btnAddItemType.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtStockCode.requestFocus();
                        break;
                }
                tabToTable(e);
            }
            case "btnAddItemType" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtStockName.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        txtType.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        txtStockName.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtType.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        txtStockName.requestFocus();
                        break;
                }
                tabToTable(e);
            }
            case "txtStockName" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtType.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        txtType.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        txtType.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtType.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        txtCat.requestFocus();
                        break;

                }
                tabToTable(e);
            }
            case "txtCat" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtBrand.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        txtType.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        btnAddCategory.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtStockName.requestFocus();
                        break;
                }
                tabToTable(e);
            }
            case "btnAddCategory" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtBrand.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        txtCat.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        txtBrand.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtCat.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        txtBrand.requestFocus();
                        break;
                }
                tabToTable(e);
            }
            case "txtBrand" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtBarCode.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        txtCat.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        btnAddBrand.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtCat.requestFocus();
                        break;
                }
                tabToTable(e);
            }
            case "btnAddBrand" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        //txtRemark.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        txtBrand.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        //txtRemark.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtBrand.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        //txtRemark.requestFocus();
                        break;
                }
                tabToTable(e);
            }
            case "txtRemark" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtBarCode.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        txtBarCode.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        txtBrand.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        txtBarCode.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtBrand.requestFocus();
                        break;

                }
                tabToTable(e);
            }
            case "txtBarCode" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtExpDate.getDateEditor().getUiComponent().requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        txtExpDate.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        txtBrand.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        txtExpDate.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtBrand.requestFocus();
                        break;

                }
                tabToTable(e);
            }
            case "txtShortName" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtExpDate.requestFocusInWindow();
                }
                tabToTable(e);
            }

            case "txtExpDate" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtPurUnit.requestFocus();
                }
                tabToTable(e);
            }

            case "cboUnitPattern" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        break;
                    case KeyEvent.VK_DOWN:
                        break;
                    case KeyEvent.VK_UP:
                        break;
                    /*case KeyEvent.VK_RIGHT:
                        btnChNo.requestFocus();
                        break;
                        case KeyEvent.VK_LEFT:
                        txtShortName.requestFocus();
                        break;
                     */
                }
                tabToTable(e);
            }
            case "txtPurWt" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtPurUnit.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        txtPurUnit.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        txtExpDate.requestFocus();
                        break;
                    /*case KeyEvent.VK_RIGHT:
                        btnChNo.requestFocus();
                        break;
                        case KeyEvent.VK_LEFT:
                        txtShortName.requestFocus();
                        break;
                     */
                }
                tabToTable(e);
            }
            case "btnChNo" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        btnUnit.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        txtPurPrice.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        txtExpDate.requestFocusInWindow();
                        break;
                    case KeyEvent.VK_RIGHT:
                        btnUnit.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtExpDate.requestFocusInWindow();
                        break;

                }
                tabToTable(e);
            }
            case "btnUnit" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtPurPrice.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        txtPurPrice.requestFocus();
                        break;

                    case KeyEvent.VK_RIGHT:
                        txtPurPrice.requestFocus();
                        break;

                }
                tabToTable(e);
            }
            case "txtPurPrice" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtSaleUnit.requestFocus();
                }
                tabToTable(e);
            }
            case "txtSaleUnit" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtSalePrice.requestFocus();
                }
                tabToTable(e);
            }

            case "txtSaleWt" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtSaleUnit.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        chkActive.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        txtSaleUnit.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        chkActive.requestFocus();
                        break;
                }
                tabToTable(e);
            }
            case "txtPurUnit" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtPurPrice.requestFocus();
                }
                tabToTable(e);
            }

            case "txtSalePrice" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtSalePriceA.requestFocus();
                }
                tabToTable(e);
            }

            case "txtSalePriceA" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtSalePriceB.requestFocus();
                }
                tabToTable(e);
            }

            case "txtSalePriceB" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtSalePriceC.requestFocus();
                }
                tabToTable(e);
            }

            case "txtSalePriceC" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtSalePriceD.requestFocus();
                }
                tabToTable(e);
            }

            case "txtSalePriceD" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtSalePriceStd.requestFocus();

                }
                tabToTable(e);
            }

            case "txtSalePriceStd" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER -> {
                    }

                }
                //btnSave.requestFocus();
                tabToTable(e);
            }

            case "btnNew" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        //btnSave.requestFocus();
                        break;
                }
                tabToTable(e);
            }
            case "btnSave" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtStockCode.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        txtStockCode.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        //btnNew.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        txtStockCode.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        //btnNew.requestFocus();
                        break;
                }
                tabToTable(e);
            }
            case "tblStock" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_DOWN:
                        selectRow = tblStock.convertRowIndexToModel(tblStock.getSelectedRow());
                        setStock(selectRow);
                        break;
                    case KeyEvent.VK_UP:
                        selectRow = tblStock.convertRowIndexToModel(tblStock.getSelectedRow());
                        setStock(selectRow);
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (e.isControlDown()) {
                            txtStockCode.requestFocus();
                        }
                        break;
                }
            }
        }
    }

    private void tabToTable(KeyEvent e) {
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (tblStock.getRowCount() >= 0) {
                tblStock.requestFocusInWindow();
                tblStock.setRowSelectionInterval(0, 0);
            }
        }
    }
}
