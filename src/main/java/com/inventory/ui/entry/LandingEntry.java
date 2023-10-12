/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.inventory.ui.entry;

import com.common.DateLockUtil;
import com.common.DecimalFormatRender;
import com.common.Global;
import com.common.PanelControl;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.common.Util1;
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.editor.StockAutoCompleter1;
import com.inventory.editor.StockCriteriaEditor;
import com.inventory.editor.TraderAutoCompleter;
import com.inventory.model.LandingHisPrice;
import com.inventory.model.LandingHis;
import com.inventory.model.LandingHisKey;
import com.inventory.model.LandingHisQty;
import com.inventory.model.Stock;
import com.inventory.model.StockUnit;
import com.inventory.model.VLanding;
import com.inventory.ui.common.LandingPriceTableModel;
import com.inventory.ui.common.LandingGradeTableModel;
import com.inventory.ui.common.LandingQtyTableModel;
import com.inventory.ui.common.UnitComboBoxModel;
import com.inventory.ui.entry.dialog.LandingHistoryDialog;
import com.inventory.ui.entry.dialog.PaymentDialog;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.repo.InventoryRepo;
import com.repo.UserRepo;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author Lenovo
 */
public class LandingEntry extends javax.swing.JPanel implements SelectionObserver, PanelControl, KeyListener {

    private LandingPriceTableModel landingPriceTableModel = new LandingPriceTableModel();
    private LandingQtyTableModel landingQtyTableModel = new LandingQtyTableModel();
    private LandingGradeTableModel landingGradeTableModel = new LandingGradeTableModel();
    private InventoryRepo inventoryRepo;
    private UserRepo userRepo;
    private TraderAutoCompleter traderAutoCompleter;
    private LocationAutoCompleter locationAutoCompleter;
    private StockAutoCompleter1 stockAutoCompleter;
    private UnitComboBoxModel qtyUnitComboBoxModel = new UnitComboBoxModel();
    private UnitComboBoxModel weightUnitComboBoxModel = new UnitComboBoxModel();
    private JProgressBar progress;
    private SelectionObserver observer;
    private LandingHis landing = new LandingHis();
    private LandingHistoryDialog dialog;
    private PaymentDialog paymentDialog;

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
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
     * Creates new form GradeManagement
     */
    public LandingEntry() {
        initComponents();
        initKeyListener();
        initFoucsAdapter();
        actionMapping();
    }

    private void initFoucsAdapter() {
        txtTrader.addFocusListener(fa);
        txtVouDate.addFocusListener(fa);
        txtLocation.addFocusListener(fa);
        txtRemark.addFocusListener(fa);
        txtCargo.addFocusListener(fa);
        txtStock.addFocusListener(fa);

    }

    private void initKeyListener() {
        txtTrader.addKeyListener(this);
        txtStock.addKeyListener(this);
        txtLocation.addKeyListener(this);
        txtWtTotal.addKeyListener(this);
    }

    public void initMain() {
        assignDefaultValue();
        initTextBox();
        initCompleter();
        initTablePrice();
        initTableQty();
        initTableGrade();
    }

    private void actionMapping() {
        String solve = "delete";
        KeyStroke delete = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        tblPrice.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(delete, solve);
        tblPrice.getActionMap().put(solve, new DeleteAction("Criteria"));
    }

    private class DeleteAction extends AbstractAction {

        private String table;

        public DeleteAction(String table) {
            this.table = table;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (table != null) {
                switch (table) {
                    case "Criteria" ->
                        deleteTranCriteria();
                }
            } else {
                // Handle null table case
            }
        }
    }

    private void deleteTranCriteria() {
        int row = tblPrice.convertRowIndexToModel(tblPrice.getSelectedRow());
        if (row >= 0) {
            if (tblPrice.getCellEditor() != null) {
                tblPrice.getCellEditor().stopCellEditing();
            }
            int yes_no = JOptionPane.showConfirmDialog(this,
                    "Are you sure to delete?", "Stock Transaction delete.", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (yes_no == 0) {
                landingPriceTableModel.delete(row);
                calCriteria();
            }
        }
    }

    private void initTablePrice() {
        landingPriceTableModel.setLblRec(lblRC);
        landingPriceTableModel.setObserver(this);
        landingPriceTableModel.setParent(tblPrice);
        tblPrice.setModel(landingPriceTableModel);
        tblPrice.getTableHeader().setFont(Global.tblHeaderFont);
        tblPrice.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblPrice.setFont(Global.textFont);
        tblPrice.setRowHeight(Global.tblRowHeight);
        tblPrice.setShowGrid(true);
        tblPrice.setCellSelectionEnabled(true);
        tblPrice.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblPrice.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblPrice.getColumnModel().getColumn(0).setCellEditor(new StockCriteriaEditor(inventoryRepo));
        tblPrice.getColumnModel().getColumn(1).setCellEditor(new StockCriteriaEditor(inventoryRepo));
        tblPrice.getColumnModel().getColumn(2).setCellEditor(new AutoClearEditor());
        tblPrice.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());
        tblPrice.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());
        tblPrice.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblPrice.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblPrice.getColumnModel().getColumn(2).setPreferredWidth(5);
        tblPrice.getColumnModel().getColumn(3).setPreferredWidth(5);
        tblPrice.getColumnModel().getColumn(4).setPreferredWidth(100);
        tblPrice.getColumnModel().getColumn(5).setPreferredWidth(150);
        tblPrice.setDefaultRenderer(Object.class, new DecimalFormatRender(2));
        tblPrice.setDefaultRenderer(Double.class, new DecimalFormatRender(2));
    }

    private void initTableQty() {
        landingQtyTableModel.setLblRec(lblRC);
        landingQtyTableModel.setObserver(this);
        landingQtyTableModel.setParent(tblQty);
        tblQty.setModel(landingQtyTableModel);
        tblQty.getTableHeader().setFont(Global.tblHeaderFont);
        tblQty.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblQty.setFont(Global.textFont);
        tblQty.setRowHeight(Global.tblRowHeight);
        tblQty.setShowGrid(true);
        tblQty.setCellSelectionEnabled(true);
        tblQty.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblQty.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblQty.getColumnModel().getColumn(0).setCellEditor(new StockCriteriaEditor(inventoryRepo));
        tblQty.getColumnModel().getColumn(1).setCellEditor(new StockCriteriaEditor(inventoryRepo));
        tblQty.getColumnModel().getColumn(2).setCellEditor(new AutoClearEditor());
        tblQty.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());
        tblQty.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());
        tblQty.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblQty.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblQty.getColumnModel().getColumn(2).setPreferredWidth(5);
        tblQty.getColumnModel().getColumn(3).setPreferredWidth(5);
        tblPrice.getColumnModel().getColumn(4).setPreferredWidth(100);
        tblQty.getColumnModel().getColumn(5).setPreferredWidth(150);
        tblQty.setDefaultRenderer(Object.class, new DecimalFormatRender(2));
        tblQty.setDefaultRenderer(Double.class, new DecimalFormatRender(2));
    }

    private void initTableGrade() {
        landingGradeTableModel.setInventoryRepo(inventoryRepo);
        tblGrade.setModel(landingGradeTableModel);
        tblGrade.getTableHeader().setFont(Global.tblHeaderFont);
        tblGrade.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblGrade.setFont(Global.textFont);
        tblGrade.setRowHeight(Global.tblRowHeight);
        tblGrade.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblGrade.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblGrade.setDefaultRenderer(Object.class, new TableCellRender());
        tblGrade.setDefaultRenderer(Boolean.class, new TableCellRender());
        tblGrade.setDefaultRenderer(Double.class, new TableCellRender());
        tblGrade.getColumnModel().getColumn(0).setPreferredWidth(100);
        tblGrade.getColumnModel().getColumn(1).setPreferredWidth(10);
        tblGrade.getColumnModel().getColumn(2).setPreferredWidth(10);
        tblGrade.getColumnModel().getColumn(3).setPreferredWidth(20);
    }

    private void assignDefaultValue() {
        lblCriteria.setForeground(Color.BLUE);
        txtVouDate.setDate(Util1.getTodayDate());
        txtVouDate.setDateFormatString(Global.dateFormat);
    }

    private void initTextBox() {
        txtPurPrice.setFormatterFactory(Util1.getDecimalFormat2());
        txtPrice.setFormatterFactory(Util1.getDecimalFormat2());
        txtAmt.setFormatterFactory(Util1.getDecimalFormat2());
        txtGQty.setFormatterFactory(Util1.getDecimalFormat2());
        txtQty.setFormatterFactory(Util1.getDecimalFormat2());
        txtWeight.setFormatterFactory(Util1.getDecimalFormat2());
        txtWtTotal.setFormatterFactory(Util1.getDecimalFormat2());
        txtOriginalAmt.setFormatterFactory(Util1.getDecimalFormat2());
        txtCriteriaAmt.setFormatterFactory(Util1.getDecimalFormat2());
        txtPurAmt.setFormatterFactory(Util1.getDecimalFormat2());
        txtOriginalAmt.setHorizontalAlignment(JTextField.RIGHT);
        txtCriteriaAmt.setHorizontalAlignment(JTextField.RIGHT);
        txtPurAmt.setHorizontalAlignment(JTextField.RIGHT);
        txtOriginalAmt.setFont(Global.amtFont);
        txtCriteriaAmt.setFont(Global.amtFont);
        txtPurAmt.setFont(Global.amtFont);
        txtPurPrice.setFont(Global.amtFont);
    }

    private void initCompleter() {
        stockAutoCompleter = new StockAutoCompleter1(txtStock, inventoryRepo, null, false);
        stockAutoCompleter.setObserver(this);
        traderAutoCompleter = new TraderAutoCompleter(txtTrader, inventoryRepo, null, false, "SUP");
        traderAutoCompleter.setObserver(this);
        locationAutoCompleter = new LocationAutoCompleter(txtLocation, null, false, false);
        locationAutoCompleter.setObserver(this);
        inventoryRepo.getLocation().doOnSuccess((t) -> {
            locationAutoCompleter.setListLocation(t);
        }).subscribe();
        inventoryRepo.getDefaultLocation().doOnSuccess((t) -> {
            locationAutoCompleter.setLocation(t);
        }).subscribe();
        cboQtyUnit.setModel(qtyUnitComboBoxModel);
        cboWUnit.setModel(weightUnitComboBoxModel);
        inventoryRepo.getStockUnit().doOnSuccess((t) -> {
            qtyUnitComboBoxModel.setData(t);
            weightUnitComboBoxModel.setData(t);
            cboQtyUnit.repaint();
            cboWUnit.repaint();
        }).subscribe();
    }

    private void calCriteria() {
        double orgAmt = Util1.getDouble(txtAmt.getValue());
        List<LandingHisPrice> listC = landingPriceTableModel.getListDetail();
        double ttlCriteria = listC.stream().mapToDouble((t) -> t.getAmount()).sum();
        double purAmt = orgAmt + ttlCriteria;
        txtPurAmt.setValue(purAmt);
        txtPurPrice.setValue(purAmt / 100);
        txtCriteriaAmt.setValue(ttlCriteria);
        txtOriginalAmt.setValue(orgAmt);
        checkGrade();
    }

    private void checkGrade() {
        Stock s = stockAutoCompleter.getStock();
        if (s != null) {
            String formulaCode = s.getFormulaCode();
            landingGradeTableModel.checkGrade(formulaCode, landingPriceTableModel.getListDetail());
        }
    }

    private void setLandingPrice(String formulaCode) {
        List<LandingHisPrice> data = landingPriceTableModel.getListDetail();
        if (!data.isEmpty()) {
            int yn = JOptionPane.showConfirmDialog(this, "Are you sure to create new criteria?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (yn == JOptionPane.NO_OPTION) {
                return;
            }
        }
        if (formulaCode != null) {
            inventoryRepo.getStockFormulaPrice(formulaCode).doOnSuccess((list) -> {
                if (list != null) {
                    landingPriceTableModel.clear();
                    list.forEach((t) -> {
                        LandingHisPrice ld = new LandingHisPrice();
                        ld.setCriteriaCode(t.getCriteriaCode());
                        ld.setCriteriaName(t.getCriteriaName());
                        ld.setCriteriaUserCode(t.getUserCode());
                        ld.setPercent(0);
                        ld.setPrice(t.getPrice());
                        ld.setPercentAllow(t.getPercentAllow());
                        landingPriceTableModel.addObject(ld);
                    });
                }
            }).subscribe();
        }
    }

    private void setLandingQty(String formulaCode) {
        if (formulaCode != null) {
            inventoryRepo.getStockFormulaQty(formulaCode).doOnSuccess((list) -> {
                if (list != null) {
                    landingQtyTableModel.clear();
                    list.forEach((t) -> {
                        LandingHisQty ld = new LandingHisQty();
                        ld.setCriteriaCode(t.getCriteriaCode());
                        ld.setCriteriaName(t.getCriteriaName());
                        ld.setCriteriaUserCode(t.getUserCode());
                        ld.setPercent(0);
                        ld.setQty(t.getQty());
                        ld.setUnit(t.getUnit());
                        ld.setPercentAllow(t.getPercentAllow());
                        landingQtyTableModel.addObject(ld);
                    });
                }
            }).subscribe();
        }
    }

    private boolean isValidEntry() {
        if (locationAutoCompleter.getLocation() == null) {
            JOptionPane.showMessageDialog(this, "Invalid Location", "Location", JOptionPane.WARNING_MESSAGE);
            txtLocation.requestFocus();
            return false;
        } else if (traderAutoCompleter.getTrader() == null) {
            JOptionPane.showMessageDialog(this, "Invalid Trader", "Trader", JOptionPane.WARNING_MESSAGE);
            txtTrader.requestFocus();
            return false;
        } else if (stockAutoCompleter.getStock() == null) {
            JOptionPane.showMessageDialog(this, "Invalid Stock", "Stock", JOptionPane.WARNING_MESSAGE);
            txtStock.requestFocus();
            return false;
        } else if (Util1.getDouble(txtWtTotal.getValue()) <= 0) {
            JOptionPane.showMessageDialog(this, "Invalid Weight Total", "Weight Total", JOptionPane.WARNING_MESSAGE);
            txtWtTotal.requestFocus();
            return false;
        } else if (qtyUnitComboBoxModel.getSelectedObject() == null) {
            JOptionPane.showMessageDialog(this, "Invalid Qty Unit", "Unit", JOptionPane.WARNING_MESSAGE);
            txtWtTotal.requestFocus();
            return false;
        } else if (weightUnitComboBoxModel.getSelectedObject() == null) {
            JOptionPane.showMessageDialog(this, "Invalid Weight Unit", "Unit", JOptionPane.WARNING_MESSAGE);
            txtWtTotal.requestFocus();
            return false;
        } else if (Util1.getDouble(txtAmt.getValue()) == 0) {
            JOptionPane.showMessageDialog(this, "Invalid Amount", "Amount", JOptionPane.WARNING_MESSAGE);
            return false;
        } else {
            landing.setTraderCode(traderAutoCompleter.getTrader().getKey().getCode());
            landing.setLocCode(locationAutoCompleter.getLocation().getKey().getLocCode());
            landing.setVouDate(Util1.convertToLocalDateTime(txtVouDate.getDate()));
            landing.setRemark(txtRemark.getText());
            landing.setCargo(txtCargo.getText());
            landing.setMacId(Global.macId);
            landing.setStockCode(stockAutoCompleter.getStock().getKey().getStockCode());
            landing.setQty(Util1.getDouble(txtGQty.getValue()));
            landing.setWeight(Util1.getDouble(txtWeight.getValue()));
            landing.setTotalWeight(Util1.getDouble(txtWtTotal.getValue()));
            landing.setUnit(qtyUnitComboBoxModel.getSelectedObject().getKey().getUnitCode());
            landing.setWeightUnit(weightUnitComboBoxModel.getSelectedObject().getKey().getUnitCode());
            landing.setPrice(Util1.getDouble(txtPrice.getValue()));
            landing.setAmount(Util1.getDouble(txtAmt.getValue()));
            landing.setCriteriaAmt(Util1.getDouble(txtCriteriaAmt.getValue()));
            landing.setPurAmt(Util1.getDouble(txtPurAmt.getValue()));
            landing.setPurPrice(Util1.getDouble(txtPurPrice.getValue()));
            landing.setPurchase(chkPurchase.isSelected());
            if (lblStatus.getText().equals("NEW")) {
                LandingHisKey key = new LandingHisKey();
                key.setCompCode(Global.compCode);
                landing.setKey(key);
                landing.setCreatedBy(Global.loginUser.getUserCode());
                landing.setDeptId(Global.deptId);
            } else {
                landing.setUpdatedBy(Global.loginUser.getUserCode());
            }
        }
        return true;
    }

    private void saveLanding(boolean print) {
        if (isValidEntry()
                && landingPriceTableModel.isValidEntry()) {
            if (DateLockUtil.isLockDate(txtVouDate.getDate())) {
                DateLockUtil.showMessage(this);
                txtVouDate.requestFocus();
                return;
            }
            observer.selected("save", false);
            progress.setIndeterminate(true);
            landing.setListDelPrice(landingPriceTableModel.getListDel());
            landing.setListPrice(landingPriceTableModel.getListDetail());
            landing.setListDelQty(landingQtyTableModel.getListDel());
            landing.setListQty(landingQtyTableModel.getListDetail());
            landing.setListGrade(landingGradeTableModel.getListGrade());
            inventoryRepo.save(landing).doOnSuccess((t) -> {
                progress.setIndeterminate(false);
                printReport(t.getKey().getVouNo());
                clear();
            }).doOnError((e) -> {
                observer.selected("save", true);
                JOptionPane.showMessageDialog(this, e.getMessage());
                progress.setIndeterminate(false);
            }).subscribe();
        }
    }

    private void printReport(String vouNo) {
        inventoryRepo.getLandingReport(vouNo).doOnSuccess((t) -> {
            if (t != null) {
                try {
                    String reportName = "LandingVoucherA5";
                    String logoPath = String.format("images%s%s", File.separator, ProUtil.getProperty("logo.name"));
                    Map<String, Object> param = new HashMap<>();
                    param.put("p_print_date", Util1.getTodayDateTime());
                    param.put("p_comp_name", Global.companyName);
                    param.put("p_comp_address", Global.companyAddress);
                    param.put("p_comp_phone", Global.companyPhone);
                    param.put("p_logo_path", logoPath);
                    param.put("p_vou_date", t.getVouDate());
                    param.put("p_vou_no", t.getVouNo());
                    param.put("p_loc_name", t.getLocName());
                    param.put("p_trader_name", t.getTraderName());
                    param.put("p_remark", t.getRemark());
                    param.put("p_cargo", t.getCargo());
                    param.put("p_stock_name", t.getStockName());
                    param.put("p_grade_stock_name", t.getGradeStockName());
                    param.put("p_pur_qty", t.getPurQty());
                    param.put("p_pur_unit", t.getPurUnit());
                    param.put("p_pur_price", t.getPurPrice());
                    param.put("p_pur_amount", t.getPurAmt());
                    String reportPath = String.format("report%s%s", File.separator, reportName.concat(".jasper"));
                    ByteArrayInputStream jsonDataStream = new ByteArrayInputStream(Util1.listToByteArray(t.getListDetail()));
                    JsonDataSource ds = new JsonDataSource(jsonDataStream);
                    JasperPrint js = JasperFillManager.fillReport(reportPath, param, ds);
                    JasperViewer.viewReport(js, false);
                } catch (JRException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Landing Report", JOptionPane.ERROR_MESSAGE);
                }
            }
        }).subscribe();
    }

    private void clear() {
        landingPriceTableModel.clear();
        landingPriceTableModel.clearDelList();
        landingQtyTableModel.clear();
        landingQtyTableModel.clearDelList();
        landingGradeTableModel.clear();
        txtVouNo.setText(null);
        txtRemark.setText(null);
        txtCargo.setText(null);
        txtGQty.setValue(null);
        txtWeight.setValue(null);
        txtPrice.setValue(null);
        txtAmt.setValue(null);
        txtWtTotal.setValue(null);
        txtPurAmt.setValue(null);
        txtPurPrice.setValue(null);
        txtCriteriaAmt.setValue(null);
        txtOriginalAmt.setValue(null);
        chkPurchase.setSelected(false);
        lblRC.setText("0");
        cboQtyUnit.setSelectedItem(null);
        cboQtyUnit.repaint();
        cboWUnit.setSelectedItem(null);
        cboWUnit.repaint();
        stockAutoCompleter.setStock(null);
        lblStatus.setText("NEW");
        lblStatus.setForeground(Color.green);
        landing = new LandingHis();
        observeMain();
    }

    private void setStock() {
        Stock s = stockAutoCompleter.getStock();
        if (s != null) {
            txtGQty.setValue(1);
            txtWeight.setValue(s.getWeight());
            txtAmt.setValue(s.getPurAmt());
            inventoryRepo.findUnit(s.getPurUnitCode()).doOnSuccess((t) -> {
                qtyUnitComboBoxModel.setSelectedItem(t);
                cboQtyUnit.repaint();
            }).subscribe();
            inventoryRepo.findUnit(s.getWeightUnit()).doOnSuccess((t) -> {
                weightUnitComboBoxModel.setSelectedItem(t);
                cboWUnit.repaint();
            }).subscribe();
            setLandingPrice(s.getFormulaCode());
            setLandingQty(s.getFormulaCode());
            txtWtTotal.requestFocus();
        }
    }

    private void calQty(boolean g) {
        double lossQty = landingQtyTableModel.getListDetail().stream().mapToDouble((t) -> t.getTotalQty()).sum();
        double ttlWt = Util1.getDouble(txtWtTotal.getValue());
        double weight = Util1.getDouble(txtWeight.getValue());
        double amt = Util1.getDouble(txtAmt.getValue());
        double gQty = g ? ttlWt / weight : Util1.getDouble(txtGQty.getValue());
        double qty = gQty - lossQty;
        txtGQty.setValue(gQty);
        txtQty.setValue(qty);
        txtPrice.setValue(amt / qty);
        calWtTotal();
        calCriteria();
    }

    private void calWtTotal() {
        double weight = Util1.getDouble(txtWeight.getValue());
        double qty = Util1.getDouble(txtQty.getValue());
        double price = Util1.getDouble(txtPrice.getValue());
        txtWtTotal.setValue(weight * qty);
        txtAmt.setValue(qty * price);
    }

    private void focusTable() {
        int rc = tblPrice.getRowCount();
        if (rc >= 1) {
            tblPrice.setRowSelectionInterval(rc - 1, rc - 1);
            tblPrice.setColumnSelectionInterval(0, 0);
            tblPrice.requestFocus();
        } else {
            txtTrader.requestFocus();
        }
    }

    private void historyLanding() {
        if (dialog == null) {
            dialog = new LandingHistoryDialog(Global.parentForm);
            dialog.setInventoryRepo(inventoryRepo);
            dialog.setUserRepo(userRepo);
            dialog.setObserver(this);
            dialog.initMain();
            dialog.setSize(Global.width - 20, Global.height - 20);
            dialog.setLocationRelativeTo(null);
        }
        dialog.search();
    }

    private void setVoucher(LandingHis his) {
        String vouNo = his.getKey().getVouNo();
        setLandingHis(vouNo);
        setLandingHisPrice(vouNo);
        setLandingHisQty(vouNo);
        setLandingHisGrade(vouNo);
    }

    private void setLandingHis(String vouNo) {
        inventoryRepo.findLanding(vouNo).doOnSuccess((l) -> {
            if (l != null) {
                landing = l;
                if (l.isDeleted()) {
                    lblStatus.setText("DELETED");
                    lblStatus.setForeground(Color.red);
                    enableForm(false);
                } else {
                    lblStatus.setText("EDIT");
                    lblStatus.setForeground(Color.blue);
                    enableForm(true);
                }
                txtVouNo.setText(l.getKey().getVouNo());
                txtVouDate.setDate(Util1.convertToDate(l.getVouDate()));
                txtRemark.setText(l.getRemark());
                txtCargo.setText(l.getCargo());
                txtGQty.setValue(Util1.getDouble(l.getQty()));
                txtWeight.setValue(Util1.getDouble(l.getWeight()));
                txtWtTotal.setValue(Util1.getDouble(l.getTotalWeight()));
                txtPrice.setValue(Util1.getDouble(l.getPrice()));
                txtAmt.setValue(Util1.getDouble(l.getAmount()));
                txtOriginalAmt.setValue(Util1.getDouble(l.getAmount()));
                txtCriteriaAmt.setValue(Util1.getDouble(l.getCriteriaAmt()));
                txtPurAmt.setValue(Util1.getDouble(l.getPurAmt()));
                txtPurPrice.setValue(Util1.getDouble(l.getPrice()));
                chkPurchase.setSelected(l.isPurchase());
                inventoryRepo.findTrader(l.getTraderCode()).doOnSuccess((t) -> {
                    traderAutoCompleter.setTrader(t);
                }).subscribe();
                inventoryRepo.findLocation(l.getLocCode()).doOnSuccess((t) -> {
                    locationAutoCompleter.setLocation(t);
                }).subscribe();
                inventoryRepo.findStock(l.getStockCode()).doOnSuccess((t) -> {
                    stockAutoCompleter.setStock(t);
                }).subscribe();
                inventoryRepo.findUnit(l.getUnit()).doOnSuccess((t) -> {
                    qtyUnitComboBoxModel.setSelectedItem(t);
                    cboQtyUnit.repaint();
                }).subscribe();
                inventoryRepo.findUnit(l.getWeightUnit()).doOnSuccess((t) -> {
                    weightUnitComboBoxModel.setSelectedItem(t);
                    cboWUnit.repaint();
                }).subscribe();
            }
        }).subscribe();
    }

    private void setLandingHisPrice(String vouNo) {
        inventoryRepo.getLandingHisPrice(vouNo).doOnSuccess((t) -> {
            if (t != null) {
                landingPriceTableModel.setListDetail(t);
            }
        }).subscribe();
    }

    private void setLandingHisQty(String vouNo) {
        inventoryRepo.getLandingHisQty(vouNo).doOnSuccess((t) -> {
            if (t != null) {
                landingQtyTableModel.setListDetail(t);
            }
        }).subscribe();
    }

    private void setLandingHisGrade(String vouNo) {
        inventoryRepo.getLandingHisGrade(vouNo).doOnSuccess((t) -> {
            if (t != null) {
                landingGradeTableModel.setListGrade(t);
            }
        }).subscribe();
    }

    private void enableForm(boolean t) {
        txtTrader.setEnabled(t);
        txtVouDate.setEnabled(t);
        txtLocation.setEnabled(t);
        txtRemark.setEnabled(t);
        txtCargo.setEnabled(t);
        txtStock.setEnabled(t);
        txtGQty.setEnabled(t);
        txtWeight.setEnabled(t);
        cboQtyUnit.setEnabled(t);
        cboWUnit.setEnabled(t);
        txtWtTotal.setEditable(t);
        txtPrice.setEditable(t);
        txtAmt.setEnabled(t);
        tblPrice.setEnabled(t);

    }

    private void paymentDialog() {
        if (chkPurchase.isSelected()) {
            if (paymentDialog == null) {
                paymentDialog = new PaymentDialog(Global.parentForm);
                paymentDialog.setLocationRelativeTo(null);
            }
            double purAmt = Util1.getDouble(txtPurAmt.getValue());
            if (purAmt <= 0) {
                JOptionPane.showMessageDialog(this, "Invalid Purchase Amt.");
                return;
            }
            paymentDialog.setVouTotal(purAmt);
            paymentDialog.setVouPaid(landing.getVouPaid());
            paymentDialog.setVouGrandTotal(landing.getGrandTotal());
            paymentDialog.setVouBalance(landing.getVouBalance());
            paymentDialog.setVouDiscount(landing.getVouDiscount());
            paymentDialog.calPayment(true);
            paymentDialog.setVisible(true);
            if (paymentDialog.isConfirm()) {
                landing.setVouPaid(paymentDialog.getVouPaid());
                landing.setVouBalance(paymentDialog.getVouBalance());
                landing.setGrandTotal(paymentDialog.getVouGrandTotal());
                landing.setVouDiscount(paymentDialog.getVouDiscount());
                save();
            }
        }
    }

    private void deleteLanding() {
        String status = lblStatus.getText();
        switch (status) {
            case "EDIT" -> {
                int yes_no = JOptionPane.showConfirmDialog(this,
                        "Are you sure to delete?", "Landing Voucher Delete.", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                if (yes_no == 0) {
                    inventoryRepo.delete(landing.getKey()).doOnSuccess((t) -> {
                        clear();
                    }).subscribe();
                }
            }
            case "DELETED" -> {
                int yes_no = JOptionPane.showConfirmDialog(this,
                        "Are you sure to restore?", "Landing Voucher Restore.", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (yes_no == 0) {
                    landing.setDeleted(false);
                    inventoryRepo.restore(landing.getKey()).doOnSuccess((t) -> {
                        lblStatus.setText("EDIT");
                        lblStatus.setForeground(Color.blue);
                        enableForm(true);
                    }).subscribe();
                }
            }
            default ->
                JOptionPane.showMessageDialog(this, "Voucher can't delete.");
        }
    }

    private void observeMain() {
        observer.selected("control", this);
        observer.selected("save", true);
        observer.selected("print", true);
        observer.selected("history", true);
        observer.selected("delete", true);
        observer.selected("refresh", false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txtOriginalAmt = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        txtCriteriaAmt = new javax.swing.JFormattedTextField();
        jLabel7 = new javax.swing.JLabel();
        txtPurAmt = new javax.swing.JFormattedTextField();
        jLabel8 = new javax.swing.JLabel();
        txtPurPrice = new javax.swing.JFormattedTextField();
        jLabel20 = new javax.swing.JLabel();
        txtGrade = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        txtTrader = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtVouDate = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        txtLocation = new javax.swing.JTextField();
        txtVouNo = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        txtCargo = new javax.swing.JTextField();
        chkPurchase = new javax.swing.JCheckBox();
        jPanel4 = new javax.swing.JPanel();
        lblCriteria = new javax.swing.JLabel();
        lblRC = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        txtStock = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtGQty = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        cboQtyUnit = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        txtWeight = new javax.swing.JFormattedTextField();
        jLabel15 = new javax.swing.JLabel();
        cboWUnit = new javax.swing.JComboBox<>();
        jLabel16 = new javax.swing.JLabel();
        txtWtTotal = new javax.swing.JFormattedTextField();
        jLabel18 = new javax.swing.JLabel();
        txtPrice = new javax.swing.JFormattedTextField();
        jLabel19 = new javax.swing.JLabel();
        txtAmt = new javax.swing.JFormattedTextField();
        lblStatus = new javax.swing.JLabel();
        txtQty = new javax.swing.JFormattedTextField();
        jLabel22 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblGrade = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPrice = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblQty = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        lblCriteria1 = new javax.swing.JLabel();
        lblRC1 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Original Amt :");

        txtOriginalAmt.setEditable(false);

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Criteria Amt :");

        txtCriteriaAmt.setEditable(false);

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Purchase Amt :");

        txtPurAmt.setEditable(false);

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Purchase Price :");

        txtPurPrice.setEditable(false);
        txtPurPrice.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel20.setFont(Global.lableFont);
        jLabel20.setText("Grade :");

        txtGrade.setEditable(false);
        txtGrade.setFont(Global.textFont);
        txtGrade.setName("txtCargo"); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtGrade, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
                    .addComponent(txtOriginalAmt)
                    .addComponent(txtPurPrice, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtCriteriaAmt)
                    .addComponent(txtPurAmt))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtOriginalAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtCriteriaAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtPurAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtPurPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtGrade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        txtTrader.setFont(Global.textFont);
        txtTrader.setName("txtTrader"); // NOI18N
        txtTrader.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtTraderFocusGained(evt);
            }
        });

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Trader");

        txtVouDate.setFont(Global.textFont);
        txtVouDate.setName("txtVouDate"); // NOI18N

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Vou No");

        txtLocation.setFont(Global.textFont);
        txtLocation.setName("txtLocation"); // NOI18N

        txtVouNo.setEditable(false);
        txtVouNo.setFont(Global.textFont);
        txtVouNo.setName("txtVouNo"); // NOI18N

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Date");

        jLabel13.setFont(Global.lableFont);
        jLabel13.setText("Location");

        jLabel14.setFont(Global.lableFont);
        jLabel14.setText("Remark");

        txtRemark.setFont(Global.textFont);
        txtRemark.setName("txtRemark"); // NOI18N

        jLabel17.setFont(Global.lableFont);
        jLabel17.setText("Cargo");

        txtCargo.setFont(Global.textFont);
        txtCargo.setName("txtCargo"); // NOI18N

        chkPurchase.setFont(Global.lableFont);
        chkPurchase.setText("Purchase");
        chkPurchase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkPurchaseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTrader))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(12, 12, 12)
                        .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCargo))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtVouDate, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(chkPurchase)
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtLocation, txtRemark, txtTrader, txtVouDate, txtVouNo});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3))
                    .addComponent(txtVouDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(chkPurchase))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(0, 6, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTrader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCargo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblCriteria.setFont(Global.lableFont);
        lblCriteria.setText("Criteria Price");

        lblRC.setFont(Global.lableFont);
        lblRC.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblRC.setText("0");

        jLabel12.setFont(Global.lableFont);
        jLabel12.setText("Records :");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblCriteria, javax.swing.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblRC, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCriteria)
                    .addComponent(lblRC)
                    .addComponent(jLabel12))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        txtStock.setFont(Global.textFont);
        txtStock.setName("txtStock"); // NOI18N
        txtStock.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtStockFocusGained(evt);
            }
        });

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Stock");

        txtGQty.setEditable(false);
        txtGQty.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtGQty.setFont(Global.amtFont);
        txtGQty.setName("txtGQty"); // NOI18N
        txtGQty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtGQtyActionPerformed(evt);
            }
        });

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Gross Qty");

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Unit");

        cboQtyUnit.setFont(Global.textFont);

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Weight");

        txtWeight.setEditable(false);
        txtWeight.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtWeight.setFont(Global.amtFont);
        txtWeight.setName("txtWeight"); // NOI18N
        txtWeight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtWeightActionPerformed(evt);
            }
        });

        jLabel15.setFont(Global.lableFont);
        jLabel15.setText("W-Unit");

        cboWUnit.setFont(Global.textFont);

        jLabel16.setFont(Global.lableFont);
        jLabel16.setText("Weight Total ");

        txtWtTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtWtTotal.setFont(Global.amtFont);
        txtWtTotal.setName("txtWtTotal"); // NOI18N
        txtWtTotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtWtTotalActionPerformed(evt);
            }
        });

        jLabel18.setFont(Global.lableFont);
        jLabel18.setText("Price");

        txtPrice.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPrice.setFont(Global.amtFont);
        txtPrice.setName("txtPrice"); // NOI18N
        txtPrice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPriceActionPerformed(evt);
            }
        });

        jLabel19.setFont(Global.lableFont);
        jLabel19.setText("Amount");

        txtAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAmt.setFont(Global.amtFont);
        txtAmt.setName("txtAmt"); // NOI18N
        txtAmt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAmtActionPerformed(evt);
            }
        });

        lblStatus.setFont(Global.menuFont);
        lblStatus.setText("NEW");

        txtQty.setEditable(false);
        txtQty.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtQty.setFont(Global.amtFont);
        txtQty.setName("txtQty"); // NOI18N
        txtQty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtQtyActionPerformed(evt);
            }
        });

        jLabel22.setFont(Global.lableFont);
        jLabel22.setText("Qty");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(txtGQty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtQty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtWeight)
                    .addComponent(txtStock))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cboQtyUnit, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cboWUnit, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtPrice, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                            .addComponent(txtAmt)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtWtTotal)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 285, Short.MAX_VALUE)
                .addComponent(lblStatus)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel16)
                        .addComponent(txtWtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel18))
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtGQty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9)
                        .addComponent(jLabel10)
                        .addComponent(cboQtyUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtQty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel22)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel19)
                        .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtWeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel11)
                        .addComponent(jLabel15)
                        .addComponent(cboWUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tblGrade.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tblGrade);

        tblPrice.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblPrice);

        tblQty.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(tblQty);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblCriteria1.setFont(Global.lableFont);
        lblCriteria1.setText("Criteria Qty");

        lblRC1.setFont(Global.lableFont);
        lblRC1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblRC1.setText("0");

        jLabel21.setFont(Global.lableFont);
        jLabel21.setText("Records :");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblCriteria1, javax.swing.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblRC1, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCriteria1)
                    .addComponent(lblRC1)
                    .addComponent(jLabel21))
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
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 610, Short.MAX_VALUE)
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 610, Short.MAX_VALUE)
                            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observeMain();
    }//GEN-LAST:event_formComponentShown

    private void txtWtTotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtWtTotalActionPerformed
        // TODO add your handling code here:
        calQty(true);
    }//GEN-LAST:event_txtWtTotalActionPerformed

    private void txtGQtyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtGQtyActionPerformed
        // TODO add your handling code here:
        calWtTotal();
    }//GEN-LAST:event_txtGQtyActionPerformed

    private void txtWeightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtWeightActionPerformed
        // TODO add your handling code here:
        calWtTotal();
    }//GEN-LAST:event_txtWeightActionPerformed

    private void txtPriceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPriceActionPerformed
        // TODO add your handling code here:
        calWtTotal();
    }//GEN-LAST:event_txtPriceActionPerformed

    private void txtAmtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAmtActionPerformed
        // TODO add your handling code here:
        calQty(true);
    }//GEN-LAST:event_txtAmtActionPerformed

    private void chkPurchaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkPurchaseActionPerformed
        // TODO add your handling code here:
        paymentDialog();
    }//GEN-LAST:event_chkPurchaseActionPerformed

    private void txtStockFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtStockFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtStockFocusGained

    private void txtTraderFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTraderFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTraderFocusGained

    private void txtQtyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtQtyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtQtyActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<StockUnit> cboQtyUnit;
    private javax.swing.JComboBox<StockUnit> cboWUnit;
    private javax.swing.JCheckBox chkPurchase;
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
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblCriteria;
    private javax.swing.JLabel lblCriteria1;
    private javax.swing.JLabel lblRC;
    private javax.swing.JLabel lblRC1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblGrade;
    private javax.swing.JTable tblPrice;
    private javax.swing.JTable tblQty;
    private javax.swing.JFormattedTextField txtAmt;
    private javax.swing.JTextField txtCargo;
    private javax.swing.JFormattedTextField txtCriteriaAmt;
    private javax.swing.JFormattedTextField txtGQty;
    private javax.swing.JTextField txtGrade;
    private javax.swing.JTextField txtLocation;
    private javax.swing.JFormattedTextField txtOriginalAmt;
    private javax.swing.JFormattedTextField txtPrice;
    private javax.swing.JFormattedTextField txtPurAmt;
    private javax.swing.JFormattedTextField txtPurPrice;
    private javax.swing.JFormattedTextField txtQty;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JTextField txtStock;
    private javax.swing.JTextField txtTrader;
    private com.toedter.calendar.JDateChooser txtVouDate;
    private javax.swing.JTextField txtVouNo;
    private javax.swing.JFormattedTextField txtWeight;
    private javax.swing.JFormattedTextField txtWtTotal;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {
        String src = source.toString();
        switch (src) {
            case "CAL_CRITERIA" ->
                calCriteria();
            case "CAL_QTY" -> {
                calQty(false);
            }
            case "STOCK" ->
                setStock();
            case "LANDING-HISTORY" ->
                setVoucher((LandingHis) selectObj);
        }
    }

    @Override
    public void save() {
        saveLanding(false);
    }

    @Override
    public void delete() {
        deleteLanding();
    }

    @Override
    public void newForm() {
        clear();
    }

    @Override
    public void history() {
        historyLanding();
    }

    @Override
    public void print() {
        saveLanding(true);
    }

    @Override
    public void refresh() {
    }

    @Override
    public void filter() {
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
        Object sourceObj = e.getSource();
        String ctrlName = "-";
        if (sourceObj instanceof JTextField jTextField) {
            ctrlName = jTextField.getName();
        } else if (sourceObj instanceof JTextFieldDateEditor jTextFieldDateEditor) {
            ctrlName = jTextFieldDateEditor.getName();
        }
        switch (ctrlName) {
            case "txtTrader" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtLocation.requestFocus();

                }
            }
            case "txtLocation" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtStock.requestFocus();
                }
            }
            case "txtStock" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtWtTotal.requestFocus();
                }
            }
            case "txtWtTotal" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    focusTable();
                }
            }
        }
    }
}
