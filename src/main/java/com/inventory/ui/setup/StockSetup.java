/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup;

import com.common.Global;
import com.common.PanelControl;
import com.common.ProUtil;
import com.common.ReportFilter;
import com.common.SelectionObserver;
import com.common.StartWithRowFilter;
import com.common.TableCellRender;
import com.common.Util1;
import com.inventory.editor.BrandAutoCompleter;
import com.inventory.editor.CategoryAutoCompleter;
import com.inventory.editor.StockAutoCompleter;
import com.inventory.editor.StockFormulaCompleter;
import com.inventory.editor.StockTypeAutoCompleter;
import com.inventory.editor.UnitAutoCompleter;
import com.inventory.editor.UnitRelationAutoCompleter;
import com.inventory.model.Category;
import com.inventory.model.MessageType;
import com.inventory.model.Stock;
import com.inventory.model.StockBrand;
import com.inventory.model.StockFormula;
import com.inventory.model.StockKey;
import com.inventory.model.StockType;
import com.inventory.model.StockUnit;
import com.repo.InventoryRepo;
import com.inventory.ui.setup.common.StockTableModel;
import com.inventory.ui.setup.dialog.CategorySetupDialog;
import com.inventory.ui.setup.dialog.RelationSetupDialog;
import com.inventory.ui.setup.dialog.StockBrandSetupDialog;
import com.inventory.ui.setup.dialog.StockImportDialog;
import com.inventory.ui.setup.dialog.StockTypeSetupDialog;
import com.inventory.ui.setup.dialog.StockUnitSetupDailog;
import com.toedter.calendar.JTextFieldDateEditor;
import com.user.common.DepartmentComboBoxModel;
import com.repo.UserRepo;
import com.user.model.DepartmentUser;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.JTextComponent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class StockSetup extends javax.swing.JPanel implements KeyListener, PanelControl, SelectionObserver {

    private int selectRow = -1;
    private CategorySetupDialog categorySetupDailog;
    private StockTypeSetupDialog typeSetupDialog;
    private StockBrandSetupDialog itemBrandDailog;
    private StockUnitSetupDailog itemUnitSetupDailog;
    private final StockTableModel stockTableModel = new StockTableModel();
    private StockTypeAutoCompleter typeAutoCompleter;
    private CategoryAutoCompleter categoryAutoCompleter;
    private BrandAutoCompleter brandAutoCompleter;
    private StockTypeAutoCompleter typeAutoCompleterF;
    private BrandAutoCompleter brandAutoCompleterF;
    private CategoryAutoCompleter categoryAutoCompleterF;
    private StockAutoCompleter stockAutoCompleterF;
    private UnitAutoCompleter purUnitCompleter;
    private UnitAutoCompleter saleUnitCompleter;
    private UnitAutoCompleter wlUnitCompleter;
    private UnitRelationAutoCompleter relationAutoCompleter;
    private StockFormulaCompleter stockFormulaCompleter;
    private Stock stock = new Stock();
    private TableRowSorter<TableModel> sorter;
    private StartWithRowFilter swrf;
    private SelectionObserver observer;
    private JProgressBar progress;
    private InventoryRepo inventoryRepo;
    private final DepartmentComboBoxModel departmentComboBoxModel = new DepartmentComboBoxModel();
    private final DepartmentComboBoxModel departmentComboBoxModel1 = new DepartmentComboBoxModel();
    private UserRepo userRepo;
    private RelationSetupDialog relationSetupDialog;

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public enum StockHeader {
        UserCode,
        StockName,
        StockGroup,
        Category,
        Brand,
        Unit,
        PurchaseUnit,
        SaleUnit,
        SalePrice
    }

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

    /**
     * Creates new form StockSetup
     */
    public StockSetup() {
        initComponents();
        initKeyListener();
        initFocusListener();
    }
    private final FocusAdapter fa = new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            ((JTextField) e.getSource()).selectAll();
        }
    };

    public void initMain() {
        initData();
        initCombo();
        initTable();
    }

    private void initFocusListener() {
        txtUserCode.addFocusListener(fa);
        txtStockName.addFocusListener(fa);
        txtBrand.addFocusListener(fa);

        txtType.addFocusListener(fa);
        txtCat.addFocusListener(fa);
        txtBarCode.addFocusListener(fa);
        txtRelation.addFocusListener(fa);
        txtPurUnit.addFocusListener(fa);
        txtWeightUnit.addFocusListener(fa);
        txtWt.addFocusListener(fa);
        txtPurPrice.addFocusListener(fa);
        txtSaleUnit.addFocusListener(fa);
        txtSalePriceA.addFocusListener(fa);
        txtSalePriceB.addFocusListener(fa);
        txtSalePriceC.addFocusListener(fa);
        txtSalePriceD.addFocusListener(fa);
        txtSalePriceE.addFocusListener(fa);
        txtBrand1.addFocusListener(fa);
        txtCat1.addFocusListener(fa);
        txtGroup1.addFocusListener(fa);
        txtStock1.addFocusListener(fa);
        txtSaleAmt.addFocusListener(fa);
        txtPurAmt.addFocusListener(fa);
    }

    private void initData() {
        progress.setIndeterminate(true);
        searchStock();
        progress.setIndeterminate(false);
    }

    private void initTable() {
        stockTableModel.setInventoryRepo(inventoryRepo);
        tblStock.setModel(stockTableModel);
        tblStock.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblStock.getColumnModel().getColumn(0).setPreferredWidth(1);
        tblStock.getColumnModel().getColumn(1).setPreferredWidth(50);
        tblStock.getColumnModel().getColumn(2).setPreferredWidth(300);
        tblStock.getColumnModel().getColumn(3).setPreferredWidth(10);
        tblStock.getColumnModel().getColumn(4).setPreferredWidth(50);
        tblStock.getTableHeader().setFont(Global.tblHeaderFont);
        tblStock.setDefaultRenderer(Boolean.class, new TableCellRender());
        tblStock.setDefaultRenderer(Object.class, new TableCellRender());
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
        txtStockCode.setText(stock.getKey().getStockCode());
        txtBarCode.setText(stock.getBarcode());
        txtUserCode.setText(stock.getUserCode());
        txtStockName.setText(stock.getStockName());
        chkActive.setSelected(stock.isActive());
        chkEx.setSelected(stock.isExplode());
        inventoryRepo.findBrand(stock.getBrandCode()).doOnSuccess((t) -> {
            brandAutoCompleter.setBrand(t);
        }).subscribe();
        inventoryRepo.findCategory(stock.getCatCode()).doOnSuccess((t) -> {
            categoryAutoCompleter.setCategory(t);
        }).subscribe();
        inventoryRepo.findUnit(stock.getSaleUnitCode()).doOnSuccess((t) -> {
            saleUnitCompleter.setStockUnit(t);
        }).subscribe();
        inventoryRepo.findGroup(stock.getTypeCode()).doOnSuccess((t) -> {
            typeAutoCompleter.setStockType(t);
        }).subscribe();
        inventoryRepo.findUnit(stock.getPurUnitCode()).doOnSuccess((t) -> {
            purUnitCompleter.setStockUnit(t);
        }).subscribe();
        inventoryRepo.findUnit(stock.getWeightUnit()).doOnSuccess((t) -> {
            wlUnitCompleter.setStockUnit(t);
        }).subscribe();
        inventoryRepo.findRelation(stock.getRelCode()).doOnSuccess((t) -> {
            relationAutoCompleter.setRelation(t);
        }).subscribe();
        inventoryRepo.findStockFormula(stock.getFormulaCode()).doOnSuccess((t) -> {
            stockFormulaCompleter.setStockFormula(t);
        }).subscribe();
        Integer deptId = stock.getDeptId();
        if (!Util1.isNullOrEmpty(deptId)) {
            userRepo.findDepartment(deptId).subscribe((t) -> {
                departmentComboBoxModel.setSelectedItem(t);
                cboDept.repaint();
            });
        } else {
            departmentComboBoxModel.setSelectedItem(null);
            cboDept.repaint();
        }
        txtWt.setText(Util1.getString(stock.getWeight()));
        txtPurPrice.setText(Util1.getString(stock.getPurPrice()));
        txtSalePrice.setText(Util1.getString(stock.getSalePriceN()));
        txtSalePriceA.setText(Util1.getString(stock.getSalePriceA()));
        txtSalePriceB.setText((Util1.getString(stock.getSalePriceB())));
        txtSalePriceC.setText(Util1.getString(stock.getSalePriceC()));
        txtSalePriceD.setText(Util1.getString(stock.getSalePriceD()));
        txtSalePriceE.setText(Util1.getString(stock.getSalePriceE()));
        chkCal.setSelected(stock.isCalculate());
        txtSaleAmt.setText(Util1.getString(stock.getSaleAmt()));
        txtPurAmt.setText(Util1.getString(stock.getPurAmt()));
        txtPurQty.setText(Util1.getString(stock.getPurQty()));
        lblStatus.setText("EDIT");
    }

    private String getBrand() {
        return brandAutoCompleterF == null ? "-" : brandAutoCompleterF.getBrand().getKey().getBrandCode();
    }

    private String getType() {
        return typeAutoCompleterF == null ? "-" : typeAutoCompleterF.getStockType().getKey().getStockTypeCode();
    }

    private String getStock() {
        return stockAutoCompleterF == null ? "-" : stockAutoCompleterF.getStock().getKey().getStockCode();
    }

    private String getCategory() {
        return categoryAutoCompleterF == null ? "-" : categoryAutoCompleterF.getCategory().getKey().getCatCode();
    }

    private void searchStock() {
        progress.setIndeterminate(true);
        Integer deptId = 0;
        if (cboDept1.getSelectedItem() instanceof DepartmentUser dep) {
            deptId = dep.getKey().getDeptId();
        }
        ReportFilter filter = new ReportFilter(Global.macId, Global.compCode, deptId);
        filter.setBrandCode(getBrand());
        filter.setCatCode(getCategory());
        filter.setStockTypeCode(getType());
        filter.setStockCode(getStock());
        inventoryRepo.searchStock(filter).subscribe((t) -> {
            stockTableModel.setListStock(t);
            lblRecord.setText(t.size() + "");
            progress.setIndeterminate(false);
        }, (e) -> {
            JOptionPane.showMessageDialog(this, e.getMessage());
            progress.setIndeterminate(false);
        });
    }

    private void initCombo() {
        typeAutoCompleter = new StockTypeAutoCompleter(txtType, null, false);
        typeAutoCompleter.setStockType(null);
        typeAutoCompleterF = new StockTypeAutoCompleter(txtGroup1, null, true);
        typeAutoCompleterF.setObserver(this);
        inventoryRepo.getStockType().subscribe((t) -> {
            typeAutoCompleter.setListStockType(t);
            List<StockType> sList = new ArrayList<>(t);
            typeAutoCompleterF.setListStockType(sList);
        });
        categoryAutoCompleter = new CategoryAutoCompleter(txtCat, null, false);
        categoryAutoCompleter.setCategory(null);
        categoryAutoCompleterF = new CategoryAutoCompleter(txtCat1, null, true);
        categoryAutoCompleterF.setObserver(this);
        inventoryRepo.getCategory().subscribe((t) -> {
            categoryAutoCompleter.setListCategory(t);
            List<Category> cList = new ArrayList<>(t);
            categoryAutoCompleterF.setListCategory(cList);
        });
        brandAutoCompleter = new BrandAutoCompleter(txtBrand, null, false);
        brandAutoCompleter.setBrand(null);
        brandAutoCompleterF = new BrandAutoCompleter(txtBrand1, null, true);
        brandAutoCompleterF.setObserver(this);
        inventoryRepo.getStockBrand().subscribe((t) -> {
            brandAutoCompleter.setListStockBrand(t);
            List<StockBrand> bList = new ArrayList<>(t);
            brandAutoCompleterF.setListStockBrand(bList);
        });
        purUnitCompleter = new UnitAutoCompleter(txtPurUnit, null);
        purUnitCompleter.setStockUnit(null);
        saleUnitCompleter = new UnitAutoCompleter(txtSaleUnit, null);
        saleUnitCompleter.setStockUnit(null);
        wlUnitCompleter = new UnitAutoCompleter(txtWeightUnit, null);
        wlUnitCompleter.setStockUnit(null);
        inventoryRepo.getStockUnit().doOnSuccess((t) -> {
            purUnitCompleter.setListUnit(t);
            saleUnitCompleter.setListUnit(t);
            wlUnitCompleter.setListUnit(t);
        }).subscribe();
        relationAutoCompleter = new UnitRelationAutoCompleter(txtRelation, null, false);
        relationAutoCompleter.setRelation(null);
        inventoryRepo.getUnitRelation().subscribe((t) -> {
            relationAutoCompleter.setListRelation(t);
        });
        userRepo.getDeparment(true).subscribe((t) -> {
            departmentComboBoxModel.setData(t);
            cboDept.setModel(departmentComboBoxModel);

            DepartmentUser dep = new DepartmentUser(0, "All");
            List<DepartmentUser> list = new ArrayList<>(t);
            list.add(0, dep);
            departmentComboBoxModel1.setData(list);
            cboDept1.setModel(departmentComboBoxModel1);
            cboDept1.setSelectedIndex(0);
        });
        stockFormulaCompleter = new StockFormulaCompleter(txtFormula, inventoryRepo, null);
        stockFormulaCompleter.setObserver(this);
        stockAutoCompleterF = new StockAutoCompleter(txtStock1, inventoryRepo, null, true);
        stockAutoCompleterF.setObserver(this);
        assignDefault();
    }

    private void assignDefault() {
        userRepo.findDepartment(Global.deptId).subscribe((t) -> {
            departmentComboBoxModel.setSelectedItem(t);
            cboDept.repaint();
        });
        txtFilter.setText(null);
    }

    private boolean isValidEntry() {
        boolean status = true;
        if (txtStockName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Stock Name must not be blank.",
                    "Stock name.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtStockName.requestFocus();
        } else if (typeAutoCompleter.getStockType() == null) {
            JOptionPane.showMessageDialog(this, "You must choose stock type.",
                    "Stock Type.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtBrand.requestFocus();
        } else if (purUnitCompleter.getStockUnit() == null) {
            JOptionPane.showMessageDialog(this, "Purchase Unit  cannot be blank.",
                    "Stock Unit Pattern.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtPurUnit.requestFocus();
        } else if (saleUnitCompleter.getStockUnit() == null) {
            JOptionPane.showMessageDialog(this, "Sale Unit  cannot be blank.",
                    "Sale Unit", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtSaleUnit.requestFocus();
        } else if (relationAutoCompleter.getRelation() == null) {
            JOptionPane.showMessageDialog(this, "Relation Unit  cannot be blank.",
                    "Stock Relation.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtRelation.requestFocus();
        } else if (cboDept.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "You must choose department.",
                    "Department.", JOptionPane.ERROR_MESSAGE);
            status = false;
            cboDept.requestFocus();
        } else if (ProUtil.isUseWeight() && wlUnitCompleter.getStockUnit() == null) {
            JOptionPane.showMessageDialog(this, "Weight Unit can not be blank.",
                    "Weight Unit.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtWeightUnit.requestFocus();
        } else {
            stock.setUserCode(txtUserCode.getText().trim());
            stock.setTypeCode(typeAutoCompleter.getStockType().getKey().getStockTypeCode());
            stock.setStockName(txtStockName.getText().trim());
            Category c = categoryAutoCompleter.getCategory();
            if (c != null) {
                stock.setCatCode(c.getKey().getCatCode());
            }
            StockBrand b = brandAutoCompleter.getBrand();
            if (b != null) {
                stock.setBrandCode(b.getKey().getBrandCode());
            }
            stock.setActive(chkActive.isSelected());
            stock.setBarcode(txtBarCode.getText().trim());
            stock.setWeight(Util1.getFloat(txtWt.getText()));
            stock.setPurUnitCode(purUnitCompleter.getStockUnit().getKey().getUnitCode());
            stock.setSaleUnitCode(saleUnitCompleter.getStockUnit().getKey().getUnitCode());
            stock.setRelCode(relationAutoCompleter.getRelation().getKey().getRelCode());
            StockUnit unit = wlUnitCompleter.getStockUnit();
            if (unit != null) {
                stock.setWeightUnit(unit.getKey().getUnitCode());
            }
            StockFormula f = stockFormulaCompleter.getStockFormula();
            if (f != null) {
                stock.setFormulaCode(f.getKey().getFormulaCode());
            }
            stock.setPurPrice(Util1.getFloat(txtPurPrice.getText()));
            stock.setSalePriceN(Util1.getFloat(txtSalePrice.getText()));
            stock.setSalePriceA(Util1.getFloat(txtSalePriceA.getText()));
            stock.setSalePriceB(Util1.getFloat(txtSalePriceB.getText()));
            stock.setSalePriceC(Util1.getFloat(txtSalePriceC.getText()));
            stock.setSalePriceD(Util1.getFloat(txtSalePriceD.getText()));
            stock.setSalePriceE(Util1.getFloat(txtSalePriceE.getText()));
            stock.setCalculate(chkCal.isSelected());
            stock.setExplode(chkEx.isSelected());
            stock.setPurAmt(Util1.getDouble(txtPurAmt.getText()));
            stock.setSaleAmt(Util1.getDouble(txtSaleAmt.getText()));
            stock.setPurQty(Util1.getDouble(txtPurQty.getText()));
            if (cboDept.getSelectedItem() instanceof DepartmentUser dep) {
                stock.setDeptId(dep.getKey().getDeptId());
            }
            if (lblStatus.getText().equals("NEW")) {
                StockKey key = new StockKey();
                key.setStockCode(null);
                key.setCompCode(Global.compCode);
                stock.setKey(key);
                stock.setMacId(Global.macId);
                stock.setCreatedDate(LocalDateTime.now());
                stock.setCreatedBy(Global.loginUser.getUserCode());
            } else {
                stock.setUpdatedBy(Global.loginUser.getUserCode());
            }
        }

        return status;
    }

    private void saveStock() {
        if (isValidEntry()) {
            observer.selected("save", false);
            progress.setIndeterminate(true);
            inventoryRepo.saveStock(stock).doOnSuccess((t) -> {
                if (t.getKey().getStockCode() != null) {
                    if (lblStatus.getText().equals("NEW")) {
                        stockTableModel.addStock(t);
                    } else {
                        stockTableModel.setStock(selectRow, t);
                    }
                    clear();
                    sendMessage(t.getStockName() + " : " + "update.");
                }
            }).doOnError((e) -> {
                observer.selected("save", true);
                progress.setIndeterminate(false);
                JOptionPane.showMessageDialog(this, e.getMessage());
            }).subscribe();
        }
    }

    private void sendMessage(String mes) {
        inventoryRepo.sendDownloadMessage(MessageType.STOCK, mes)
                .doOnSuccess((t) -> {
                    log.info(t);
                }).subscribe();
    }

    public void clear() {
        observer.selected("save", true);
        progress.setIndeterminate(false);
        relationAutoCompleter.setRelation(null);
        brandAutoCompleter.setBrand(null);
        categoryAutoCompleter.setCategory(null);
        purUnitCompleter.setStockUnit(null);
        saleUnitCompleter.setStockUnit(null);
        wlUnitCompleter.setStockUnit(null);
        stockFormulaCompleter.setStockFormula(null);
        txtStockCode.setText(null);
        txtBarCode.setText(null);
        txtUserCode.setText(null);
        txtStockName.setText(null);
        chkActive.setSelected(true);
        chkCal.setSelected(true);
        txtWt.setText(null);
        txtPurPrice.setText(null);
        txtSalePriceE.setText(null);
        txtSalePrice.setText(null);
        txtSalePriceA.setText(null);
        txtSalePriceB.setText(null);
        txtSalePriceC.setText(null);
        txtSalePriceD.setText(null);
        txtSaleAmt.setText(null);
        txtPurAmt.setText(null);
        txtPurQty.setText(null);
        lblStatus.setText("NEW");
        txtUserCode.setEnabled(true);
        txtUserCode.requestFocus();
        stock = new Stock();
        lblRecord.setText(stockTableModel.getListStock().size() + "");
        assignDefault();
    }

    private void initKeyListener() {
        txtBarCode.addKeyListener(this);
        txtSalePriceE.addKeyListener(this);
        txtSalePrice.addKeyListener(this);
        txtSalePriceA.addKeyListener(this);
        txtSalePriceB.addKeyListener(this);
        txtSalePriceC.addKeyListener(this);
        txtSalePriceD.addKeyListener(this);
        txtWt.addKeyListener(this);
        txtUserCode.addKeyListener(this);
        txtStockName.addKeyListener(this);
        lblRecord.addKeyListener(this);
        txtBrand.addKeyListener(this);
        txtCat.addKeyListener(this);
        txtType.addKeyListener(this);
        chkActive.addKeyListener(this);
        btnAddBrand.addKeyListener(this);
        btnAddCategory.addKeyListener(this);
        btnAddItemType.addKeyListener(this);
        btnUnit.addKeyListener(this);
        tblStock.addKeyListener(this);
        txtPurUnit.addKeyListener(this);
        txtSaleUnit.addKeyListener(this);
        txtRelation.addKeyListener(this);
    }

    private void relationSetup() {
        if (relationSetupDialog == null) {
            relationSetupDialog = new RelationSetupDialog(Global.parentForm);
            relationSetupDialog.setInventoryRepo(inventoryRepo);
            relationSetupDialog.initMain();
            relationSetupDialog.setSize(Global.width / 2, Global.height / 2);
            relationSetupDialog.setLocationRelativeTo(null);
        }
        relationSetupDialog.setListUnitRelation(relationAutoCompleter.getListRelation());
        relationSetupDialog.setVisible(true);
    }

    private void printFile() {
        try {
            progress.setIndeterminate(true);
            String CSV_FILE_PATH = "stocksetup.csv";
            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                    .setHeader(StockHeader.class)
                    .setSkipHeaderRecord(false)
                    .build();
            FileWriter out = new FileWriter(CSV_FILE_PATH);
            try (CSVPrinter printer = csvFormat.print(out)) {
                printer.flush();
            }
            progress.setIndeterminate(false);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void categoryDialog() {
        if (categoryAutoCompleter != null) {
            if (categorySetupDailog == null) {
                categorySetupDailog = new CategorySetupDialog(Global.parentForm);
                categorySetupDailog.setInventoryRepo(inventoryRepo);
                categorySetupDailog.setSize(Global.width / 2, Global.height / 2);
                categorySetupDailog.setLocationRelativeTo(null);
                categorySetupDailog.initMain();
            }
            categorySetupDailog.setListCategory(categoryAutoCompleter.getListCategory());
            categorySetupDailog.setVisible(true);
        }
    }

    private void stockTypeDialog() {
        if (typeAutoCompleter != null) {
            if (typeSetupDialog == null) {
                typeSetupDialog = new StockTypeSetupDialog(Global.parentForm);
                typeSetupDialog.setInventoryRepo(inventoryRepo);
                typeSetupDialog.setSize(Global.width / 2, Global.height / 2);
                typeSetupDialog.setLocationRelativeTo(null);
                typeSetupDialog.initMain();
            }
            typeSetupDialog.setListStockType(typeAutoCompleter.getListStockType());
            typeSetupDialog.setVisible(true);
        }
    }

    private void stockUnitDialog() {
        if (purUnitCompleter != null) {
            if (itemUnitSetupDailog == null) {
                itemUnitSetupDailog = new StockUnitSetupDailog(Global.parentForm);
                itemUnitSetupDailog.setInventoryRepo(inventoryRepo);
                itemUnitSetupDailog.initMain();
                itemUnitSetupDailog.setSize(Global.width / 2, Global.height / 2);
                itemUnitSetupDailog.setLocationRelativeTo(null);
            }
            List<StockUnit> listUnit = purUnitCompleter.getListUnit();
            itemUnitSetupDailog.setListStockUnit(listUnit);
            saleUnitCompleter.setListUnit(listUnit);
            wlUnitCompleter.setListUnit(listUnit);
            itemUnitSetupDailog.setVisible(true);
        }
    }

    private void brandDialog() {
        if (brandAutoCompleter != null) {
            if (itemBrandDailog == null) {
                itemBrandDailog = new StockBrandSetupDialog(Global.parentForm);
                itemBrandDailog.setInventoryRepo(inventoryRepo);
                itemBrandDailog.initMain();
                itemBrandDailog.setSize(Global.width / 2, Global.height / 2);
                itemBrandDailog.setLocationRelativeTo(null);
            }
            itemBrandDailog.setListStockBrand(brandAutoCompleter.getListStockBrand());
            itemBrandDailog.setVisible(true);
        }
    }

    private void observeMain() {
        observer.selected("control", this);
        observer.selected("save", true);
        observer.selected("print", false);
        observer.selected("history", false);
        observer.selected("delete", true);
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

        jScrollPane1 = new javax.swing.JScrollPane();
        tblStock = new javax.swing.JTable();
        lblRecord = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        txtFilter = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtUserCode = new javax.swing.JTextField();
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
        txtSalePriceE = new javax.swing.JTextField();
        txtSaleUnit = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        txtSaleAmt = new javax.swing.JTextField();
        btnAddItemType1 = new javax.swing.JButton();
        txtBrand = new javax.swing.JTextField();
        txtCat = new javax.swing.JTextField();
        txtType = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        txtPurUnit = new javax.swing.JTextField();
        btnUnit = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        txtWeightUnit = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        txtWt = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtPurPrice = new javax.swing.JTextField();
        txtPurAmt = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        txtPurQty = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtStockCode = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtRelation = new javax.swing.JTextField();
        btnAddRelation = new javax.swing.JButton();
        chkCal = new javax.swing.JCheckBox();
        chkEx = new javax.swing.JCheckBox();
        btnDownload = new javax.swing.JButton();
        chkDeleted = new javax.swing.JCheckBox();
        jLabel30 = new javax.swing.JLabel();
        cboDept = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        txtFormula = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        txtGroup1 = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        txtCat1 = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        txtBrand1 = new javax.swing.JTextField();
        txtStock1 = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        cboDept1 = new javax.swing.JComboBox<>();
        jLabel22 = new javax.swing.JLabel();

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

        lblRecord.setEditable(false);
        lblRecord.setFont(Global.lableFont);
        lblRecord.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        lblRecord.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jLabel19.setFont(Global.lableFont);
        jLabel19.setText("Record :");

        txtFilter.setFont(Global.textFont);
        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel1.setPreferredSize(new java.awt.Dimension(57, 16));

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("User Code");

        txtUserCode.setFont(Global.textFont);
        txtUserCode.setName("txtUserCode"); // NOI18N
        txtUserCode.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtUserCodeFocusGained(evt);
            }
        });
        txtUserCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUserCodeActionPerformed(evt);
            }
        });

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Stock Group");

        btnAddItemType.setBackground(Global.selectionColor);
        btnAddItemType.setFont(Global.lableFont);
        btnAddItemType.setForeground(new java.awt.Color(255, 255, 255));
        btnAddItemType.setText("...");
        btnAddItemType.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAddItemType.setName("btnAddItemType"); // NOI18N
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

        btnAddCategory.setBackground(Global.selectionColor);
        btnAddCategory.setFont(Global.lableFont);
        btnAddCategory.setForeground(new java.awt.Color(255, 255, 255));
        btnAddCategory.setText("...");
        btnAddCategory.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAddCategory.setName("btnAddCategory"); // NOI18N
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

        btnAddBrand.setBackground(Global.selectionColor);
        btnAddBrand.setFont(Global.lableFont);
        btnAddBrand.setForeground(new java.awt.Color(255, 255, 255));
        btnAddBrand.setText("...");
        btnAddBrand.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAddBrand.setName("btnAddBrand"); // NOI18N
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

        chkActive.setFont(Global.lableFont);
        chkActive.setSelected(true);
        chkActive.setText("Active");
        chkActive.setName("chkActive"); // NOI18N

        lblStatus.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        lblStatus.setText("NEW");

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Sale Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, Global.lableFont));

        jLabel11.setFont(Global.lableFont);
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel11.setText("Sale Price N");

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
        jLabel18.setText("Sale Price E");

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

        txtSalePriceE.setFont(Global.textFont);
        txtSalePriceE.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSalePriceE.setName("txtSalePriceE"); // NOI18N
        txtSalePriceE.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSalePriceEFocusGained(evt);
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

        jLabel23.setFont(Global.lableFont);
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel23.setText("Sale Amount");

        txtSaleAmt.setFont(Global.textFont);
        txtSaleAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSaleAmt.setName("txtSalePriceC"); // NOI18N
        txtSaleAmt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSaleAmtFocusGained(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(txtSalePrice))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtSalePriceB)
                                    .addComponent(txtSalePriceA)))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSaleUnit, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSalePriceC, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                            .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtSalePriceD)
                            .addComponent(txtSalePriceE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSaleAmt, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel11, jLabel12, jLabel14, jLabel15});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel16, jLabel17, jLabel18});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(txtSaleUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23)
                    .addComponent(txtSaleAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(txtSalePrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(txtSalePriceC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(txtSalePriceA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17)
                    .addComponent(txtSalePriceD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(txtSalePriceB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18)
                    .addComponent(txtSalePriceE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 6, Short.MAX_VALUE))
        );

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

        txtBrand.setFont(Global.textFont);
        txtBrand.setName("txtBrand"); // NOI18N

        txtCat.setFont(Global.textFont);
        txtCat.setName("txtCat"); // NOI18N

        txtType.setFont(Global.textFont);
        txtType.setName("txtType"); // NOI18N
        txtType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTypeActionPerformed(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Purchase Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, Global.lableFont));

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Purchase Unit");

        txtPurUnit.setFont(Global.textFont);
        txtPurUnit.setToolTipText("Purchase Price");
        txtPurUnit.setName("txtPurUnit"); // NOI18N
        txtPurUnit.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPurUnitFocusGained(evt);
            }
        });

        btnUnit.setBackground(Global.selectionColor);
        btnUnit.setFont(Global.lableFont);
        btnUnit.setForeground(new java.awt.Color(255, 255, 255));
        btnUnit.setText("...");
        btnUnit.setName("btnUnit"); // NOI18N
        btnUnit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUnitActionPerformed(evt);
            }
        });

        jLabel20.setFont(Global.lableFont);
        jLabel20.setText("Weight Unit");

        txtWeightUnit.setFont(Global.textFont);
        txtWeightUnit.setToolTipText("Purchase Price");
        txtWeightUnit.setName("txtPurUnit"); // NOI18N
        txtWeightUnit.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtWeightUnitFocusGained(evt);
            }
        });

        jLabel21.setFont(Global.lableFont);
        jLabel21.setText("Weight");

        txtWt.setFont(Global.textFont);
        txtWt.setToolTipText("Purchase Price");
        txtWt.setName("txtWt"); // NOI18N
        txtWt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtWtFocusGained(evt);
            }
        });

        jLabel13.setFont(Global.lableFont);
        jLabel13.setText("Pur Price");

        txtPurPrice.setFont(Global.textFont);
        txtPurPrice.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPurPrice.setName("txtSalePrice"); // NOI18N
        txtPurPrice.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPurPriceFocusGained(evt);
            }
        });
        txtPurPrice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPurPriceActionPerformed(evt);
            }
        });

        txtPurAmt.setFont(Global.textFont);
        txtPurAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPurAmt.setName("txtSalePriceC"); // NOI18N
        txtPurAmt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPurAmtFocusGained(evt);
            }
        });

        jLabel24.setFont(Global.lableFont);
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel24.setText("Pur Amt");

        jLabel31.setFont(Global.lableFont);
        jLabel31.setText("Pur Qty");

        txtPurQty.setFont(Global.textFont);
        txtPurQty.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPurQty.setName("txtSalePrice"); // NOI18N
        txtPurQty.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPurQtyFocusGained(evt);
            }
        });
        txtPurQty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPurQtyActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtWt)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtWeightUnit, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPurUnit, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(txtPurPrice)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel31)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtPurQty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnUnit, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtPurAmt, javax.swing.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtPurUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUnit)
                    .addComponent(txtPurPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtPurQty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel31)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel24)
                        .addComponent(txtPurAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel20)
                        .addComponent(txtWeightUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(txtWt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("System Code");

        txtStockCode.setEditable(false);
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

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Unit Relation");

        txtRelation.setFont(Global.textFont);
        txtRelation.setName("txtRelation"); // NOI18N
        txtRelation.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtRelationFocusGained(evt);
            }
        });
        txtRelation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRelationActionPerformed(evt);
            }
        });

        btnAddRelation.setBackground(Global.selectionColor);
        btnAddRelation.setFont(Global.lableFont);
        btnAddRelation.setForeground(new java.awt.Color(255, 255, 255));
        btnAddRelation.setText("...");
        btnAddRelation.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAddRelation.setName("btnAddBrand"); // NOI18N
        btnAddRelation.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddRelation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddRelationActionPerformed(evt);
            }
        });

        chkCal.setFont(Global.lableFont);
        chkCal.setSelected(true);
        chkCal.setText("Calulate Stock");
        chkCal.setName("chkActive"); // NOI18N

        chkEx.setFont(Global.lableFont);
        chkEx.setSelected(true);
        chkEx.setText("Explode");
        chkEx.setName("chkActive"); // NOI18N

        btnDownload.setFont(Global.lableFont);
        btnDownload.setText("Download");
        btnDownload.setName("btnAddItemType"); // NOI18N
        btnDownload.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnDownload.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDownloadActionPerformed(evt);
            }
        });

        chkDeleted.setFont(Global.lableFont);
        chkDeleted.setSelected(true);
        chkDeleted.setText("Deleted");
        chkDeleted.setName("chkActive"); // NOI18N

        jLabel30.setFont(Global.lableFont);
        jLabel30.setText("Department");

        cboDept.setFont(Global.textFont);

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Formula");

        txtFormula.setFont(Global.textFont);
        txtFormula.setName("txtRelation"); // NOI18N
        txtFormula.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtFormulaFocusGained(evt);
            }
        });
        txtFormula.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFormulaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtStockCode))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel5))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtBrand)
                                            .addComponent(txtType))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(btnAddItemType)
                                            .addComponent(btnAddBrand, javax.swing.GroupLayout.Alignment.TRAILING)))
                                    .addComponent(txtUserCode)))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtRelation)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnAddRelation)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtCat)
                                .addGap(4, 4, 4)
                                .addComponent(btnAddCategory))
                            .addComponent(txtStockName)
                            .addComponent(txtBarCode, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboDept, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtFormula, javax.swing.GroupLayout.Alignment.LEADING)))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
                        .addComponent(chkEx)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkCal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkActive)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkDeleted)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnDownload)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAddItemType1)))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel3, jLabel30, jLabel4, jLabel5, jLabel6, jLabel7, jLabel8});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnAddItemType1, btnDownload});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtStockCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtUserCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addGap(9, 9, 9)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnAddItemType)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel2)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnAddBrand)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtBrand, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel5))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel30)
                            .addComponent(cboDept, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtStockName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtCat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel4))
                            .addComponent(btnAddCategory))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(txtBarCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtRelation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8))
                    .addComponent(btnAddRelation)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel9)
                        .addComponent(txtFormula, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAddItemType1)
                    .addComponent(chkActive)
                    .addComponent(lblStatus)
                    .addComponent(btnDownload)
                    .addComponent(chkDeleted)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(chkEx, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(chkCal, javax.swing.GroupLayout.Alignment.LEADING)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel2, jLabel5, jLabel6, jLabel8});

        jScrollPane2.setViewportView(jPanel1);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel25.setFont(Global.lableFont);
        jLabel25.setText("Group");

        txtGroup1.setFont(Global.textFont);

        jLabel26.setFont(Global.lableFont);
        jLabel26.setText("Category");

        txtCat1.setFont(Global.textFont);

        jLabel27.setFont(Global.lableFont);
        jLabel27.setText("Brand");

        txtBrand1.setFont(Global.textFont);

        txtStock1.setFont(Global.textFont);

        jLabel28.setFont(Global.lableFont);
        jLabel28.setText("Stock");

        jLabel29.setFont(Global.lableFont);
        jLabel29.setText("Department");

        cboDept1.setFont(Global.textFont);
        cboDept1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboDept1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel25)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtGroup1, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel26)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCat1, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel27)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtBrand1, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel28)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtStock1, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel29)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboDept1, 0, 82, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtGroup1)
                    .addComponent(txtCat1)
                    .addComponent(txtBrand1)
                    .addComponent(txtStock1)
                    .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cboDept1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel22.setFont(Global.lableFont);
        jLabel22.setText("Search Contains :");

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
                        .addComponent(lblRecord, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFilter))
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 683, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblRecord, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19)
                            .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel22))))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observeMain();
    }//GEN-LAST:event_formComponentShown

    private void txtFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyReleased
        // TODO add your handling code here:
        if (txtFilter.getText().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(swrf);
        }
    }//GEN-LAST:event_txtFilterKeyReleased

    private void btnDownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDownloadActionPerformed
        printFile();
    }//GEN-LAST:event_btnDownloadActionPerformed

    private void btnAddRelationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddRelationActionPerformed
        // TODO add your handling code here:
        relationSetup();
    }//GEN-LAST:event_btnAddRelationActionPerformed

    private void txtRelationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRelationActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRelationActionPerformed

    private void txtRelationFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRelationFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRelationFocusGained

    private void txtStockCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtStockCodeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtStockCodeActionPerformed

    private void txtStockCodeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtStockCodeFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtStockCodeFocusGained

    private void txtPurPriceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPurPriceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPurPriceActionPerformed

    private void txtPurPriceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPurPriceFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPurPriceFocusGained

    private void txtWtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtWtFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtWtFocusGained

    private void txtWeightUnitFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtWeightUnitFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtWeightUnitFocusGained

    private void btnUnitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUnitActionPerformed
        // TODO add your handling code here:
        stockUnitDialog();
    }//GEN-LAST:event_btnUnitActionPerformed

    private void txtPurUnitFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPurUnitFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPurUnitFocusGained

    private void txtTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTypeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTypeActionPerformed

    private void btnAddItemType1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddItemType1ActionPerformed
        // TODO add your handling code here:
        StockImportDialog dialog = new StockImportDialog(Global.parentForm);
        dialog.setInventoryRepo(inventoryRepo);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);

    }//GEN-LAST:event_btnAddItemType1ActionPerformed

    private void txtSaleUnitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSaleUnitActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSaleUnitActionPerformed

    private void txtSaleUnitFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSaleUnitFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSaleUnitFocusGained

    private void txtSalePriceEFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSalePriceEFocusGained
        // TODO add your handling code here:
        txtSalePriceE.selectAll();
    }//GEN-LAST:event_txtSalePriceEFocusGained

    private void txtSalePriceDFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSalePriceDFocusGained
        // TODO add your handling code here:
        txtSalePriceD.selectAll();
    }//GEN-LAST:event_txtSalePriceDFocusGained

    private void txtSalePriceCFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSalePriceCFocusGained
        // TODO add your handling code here:
        txtSalePriceC.selectAll();
    }//GEN-LAST:event_txtSalePriceCFocusGained

    private void txtSalePriceBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSalePriceBActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSalePriceBActionPerformed

    private void txtSalePriceBFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSalePriceBFocusGained
        // TODO add your handling code here:
        txtSalePriceB.selectAll();
    }//GEN-LAST:event_txtSalePriceBFocusGained

    private void txtSalePriceAFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSalePriceAFocusGained
        // TODO add your handling code here:
        txtSalePriceA.selectAll();
    }//GEN-LAST:event_txtSalePriceAFocusGained

    private void txtSalePriceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSalePriceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSalePriceActionPerformed

    private void txtSalePriceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSalePriceFocusGained
        // TODO add your handling code here:
        txtSalePrice.selectAll();
    }//GEN-LAST:event_txtSalePriceFocusGained

    private void txtBarCodeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBarCodeFocusGained
        // TODO add your handling code here:
        txtBarCode.selectAll();
    }//GEN-LAST:event_txtBarCodeFocusGained

    private void btnAddBrandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddBrandActionPerformed
        // TODO add your handling code here:
        brandDialog();
    }//GEN-LAST:event_btnAddBrandActionPerformed

    private void btnAddCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddCategoryActionPerformed
        // TODO add your handling code here:
        categoryDialog();
    }//GEN-LAST:event_btnAddCategoryActionPerformed

    private void txtStockNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtStockNameFocusGained
        // TODO add your handling code here:
        txtStockName.selectAll();
    }//GEN-LAST:event_txtStockNameFocusGained

    private void btnAddItemTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddItemTypeActionPerformed
        // TODO add your handling code here:
        stockTypeDialog();
    }//GEN-LAST:event_btnAddItemTypeActionPerformed

    private void txtUserCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUserCodeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUserCodeActionPerformed

    private void txtUserCodeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtUserCodeFocusGained
        // TODO add your handling code here:
        txtUserCode.requestFocus();
    }//GEN-LAST:event_txtUserCodeFocusGained

    private void cboDept1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboDept1ActionPerformed
        searchStock();
    }//GEN-LAST:event_cboDept1ActionPerformed

    private void txtFormulaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFormulaFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFormulaFocusGained

    private void txtFormulaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFormulaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFormulaActionPerformed

    private void txtSaleAmtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSaleAmtFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSaleAmtFocusGained

    private void txtPurAmtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPurAmtFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPurAmtFocusGained

    private void txtPurQtyFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPurQtyFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPurQtyFocusGained

    private void txtPurQtyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPurQtyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPurQtyActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddBrand;
    private javax.swing.JButton btnAddCategory;
    private javax.swing.JButton btnAddItemType;
    private javax.swing.JButton btnAddItemType1;
    private javax.swing.JButton btnAddRelation;
    private javax.swing.JButton btnDownload;
    private javax.swing.JButton btnUnit;
    private javax.swing.JComboBox<DepartmentUser> cboDept;
    private javax.swing.JComboBox<DepartmentUser> cboDept1;
    private javax.swing.JCheckBox chkActive;
    private javax.swing.JCheckBox chkCal;
    private javax.swing.JCheckBox chkDeleted;
    private javax.swing.JCheckBox chkEx;
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
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField lblRecord;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblStock;
    private javax.swing.JTextField txtBarCode;
    private javax.swing.JTextField txtBrand;
    private javax.swing.JTextField txtBrand1;
    private javax.swing.JTextField txtCat;
    private javax.swing.JTextField txtCat1;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JTextField txtFormula;
    private javax.swing.JTextField txtGroup1;
    private javax.swing.JTextField txtPurAmt;
    private javax.swing.JTextField txtPurPrice;
    private javax.swing.JTextField txtPurQty;
    private javax.swing.JTextField txtPurUnit;
    private javax.swing.JTextField txtRelation;
    private javax.swing.JTextField txtSaleAmt;
    private javax.swing.JTextField txtSalePrice;
    private javax.swing.JTextField txtSalePriceA;
    private javax.swing.JTextField txtSalePriceB;
    private javax.swing.JTextField txtSalePriceC;
    private javax.swing.JTextField txtSalePriceD;
    private javax.swing.JTextField txtSalePriceE;
    private javax.swing.JTextField txtSaleUnit;
    private javax.swing.JTextField txtStock1;
    private javax.swing.JTextField txtStockCode;
    private javax.swing.JTextField txtStockName;
    private javax.swing.JTextField txtType;
    private javax.swing.JTextField txtUserCode;
    private javax.swing.JTextField txtWeightUnit;
    private javax.swing.JTextField txtWt;
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
            case "txtUserCode" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtStockName.requestFocus();

                }
            }
            case "txtType" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtCat.requestFocus();

                }
            }
            case "btnAddItemType" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtStockName.requestFocus();
                }
            }
            case "txtStockName" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtBrand.requestFocus();

                }
            }
            case "txtCat" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtType.requestFocus();
                }
            }
            case "btnAddCategory" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtType.requestFocus();
                }
            }
            case "txtBrand" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtBarCode.requestFocus();
                }
            }
            case "btnAddBrand" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER -> {
                    }
                }
            }
            case "txtRemark" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtBarCode.requestFocus();
                }
            }
            case "txtBarCode" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtRelation.requestFocus();
                }
            }
            case "txtRelation" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtPurUnit.requestFocus();
                }
            }
            case "txtPurWt" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtPurUnit.requestFocus();
                }
            }
            case "btnUnit" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtWt.requestFocus();
                }
            }
            case "txtWt" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtSaleUnit.requestFocus();
                }
            }
            case "txtSaleUnit" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtSalePrice.requestFocus();
                }
            }

            case "txtSaleWt" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtSaleUnit.requestFocus();
                }
            }
            case "txtPurUnit" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtWt.requestFocus();
                }
            }

            case "txtSalePrice" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtSalePriceA.requestFocus();
                }
            }

            case "txtSalePriceA" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtSalePriceB.requestFocus();
                }
            }

            case "txtSalePriceB" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtSalePriceC.requestFocus();
                }
            }

            case "txtSalePriceC" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtSalePriceD.requestFocus();
                }
            }

            case "txtSalePriceD" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtSalePriceE.requestFocus();

                }
            }

            case "txtSalePriceStd" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER -> {
                    }

                }
            }

            case "tblStock" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_DOWN -> {
                        selectRow = tblStock.convertRowIndexToModel(tblStock.getSelectedRow());
                        setStock(selectRow);
                    }
                    case KeyEvent.VK_UP -> {
                        selectRow = tblStock.convertRowIndexToModel(tblStock.getSelectedRow());
                        setStock(selectRow);
                    }
                    case KeyEvent.VK_RIGHT -> {
                        if (e.isControlDown()) {
                            txtUserCode.requestFocus();
                        }
                    }

                }
            }

        }
    }

    @Override
    public void save() {
        saveStock();
    }

    @Override
    public void delete() {
        if (stock.getKey() != null) {
            inventoryRepo.deleteStock(stock.getKey()).subscribe((t) -> {
                if (!t.isEmpty()) {
                    JOptionPane.showMessageDialog(this, t.get(0).getMessage());
                } else {
                    stockTableModel.deleteStock(selectRow);
                    sendMessage(stock.getStockName() + " : deleted");
                    clear();
                    JOptionPane.showMessageDialog(this, "Deleted.");
                }

            });

        }
    }

    @Override
    public void newForm() {
        clear();
    }

    @Override
    public void history() {
    }

    @Override
    public void print() {
    }

    @Override
    public void refresh() {
        searchStock();
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
        searchStock();
    }
}
