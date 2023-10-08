/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.inventory.ui.entry;

import com.common.DateLockUtil;
import com.common.DecimalFormatRender;
import com.common.Global;
import com.common.PanelControl;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.editor.StockAutoCompleter1;
import com.inventory.editor.StockCriteriaEditor;
import com.inventory.editor.TraderAutoCompleter;
import com.inventory.model.LandingHisCriteria;
import com.inventory.model.LandingHis;
import com.inventory.model.LandingHisKey;
import com.inventory.model.Stock;
import com.inventory.model.StockUnit;
import com.inventory.ui.common.LandingCriteriaTableModel;
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
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

/**
 *
 * @author Lenovo
 */
public class LandingEntry extends javax.swing.JPanel implements SelectionObserver, PanelControl, KeyListener {

    private LandingCriteriaTableModel landingCriteriaTableModel = new LandingCriteriaTableModel();
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
        initTableCriteria();
    }

    private void actionMapping() {
        String solve = "delete";
        KeyStroke delete = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        tblCriteria.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(delete, solve);
        tblCriteria.getActionMap().put(solve, new DeleteAction("Criteria"));
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
        int row = tblCriteria.convertRowIndexToModel(tblCriteria.getSelectedRow());
        if (row >= 0) {
            if (tblCriteria.getCellEditor() != null) {
                tblCriteria.getCellEditor().stopCellEditing();
            }
            int yes_no = JOptionPane.showConfirmDialog(this,
                    "Are you sure to delete?", "Stock Transaction delete.", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (yes_no == 0) {
                landingCriteriaTableModel.delete(row);
            }
        }
    }

    private void initTableCriteria() {
        landingCriteriaTableModel.setLblRec(lblRC);
        landingCriteriaTableModel.setObserver(this);
        landingCriteriaTableModel.setParent(tblCriteria);
        tblCriteria.setModel(landingCriteriaTableModel);
        tblCriteria.getTableHeader().setFont(Global.tblHeaderFont);
        tblCriteria.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCriteria.setFont(Global.textFont);
        tblCriteria.setRowHeight(Global.tblRowHeight);
        tblCriteria.setShowGrid(true);
        tblCriteria.setCellSelectionEnabled(true);
        tblCriteria.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblCriteria.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCriteria.getColumnModel().getColumn(0).setCellEditor(new StockCriteriaEditor(inventoryRepo));
        tblCriteria.getColumnModel().getColumn(1).setCellEditor(new StockCriteriaEditor(inventoryRepo));
        tblCriteria.getColumnModel().getColumn(2).setCellEditor(new AutoClearEditor());
        tblCriteria.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());
        tblCriteria.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());
        tblCriteria.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblCriteria.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblCriteria.getColumnModel().getColumn(2).setPreferredWidth(5);
        tblCriteria.getColumnModel().getColumn(3).setPreferredWidth(5);
        tblCriteria.getColumnModel().getColumn(4).setPreferredWidth(100);
        tblCriteria.getColumnModel().getColumn(5).setPreferredWidth(150);
        tblCriteria.setDefaultRenderer(Object.class, new DecimalFormatRender(2));
        tblCriteria.setDefaultRenderer(Double.class, new DecimalFormatRender(2));
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
        traderAutoCompleter = new TraderAutoCompleter(txtTrader, inventoryRepo, null, false, "C");
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
        double qty = Util1.getDouble(txtQty.getValue());
        List<LandingHisCriteria> listC = landingCriteriaTableModel.getListDetail();
        double ttlCriteria = listC.stream().mapToDouble((t) -> t.getAmount()).sum();
        double purAmt = orgAmt + ttlCriteria;
        txtPurAmt.setValue(purAmt);
        txtPurPrice.setValue(purAmt / qty);
        txtCriteriaAmt.setValue(ttlCriteria);
        txtOriginalAmt.setValue(orgAmt);
    }

    private void setCriteria(String formulaCode) {
        List<LandingHisCriteria> data = landingCriteriaTableModel.getListDetail();
        if (!data.isEmpty()) {
            int yn = JOptionPane.showConfirmDialog(this, "Are you sure to create new criteria?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (yn == JOptionPane.NO_OPTION) {
                return;
            }
        }
        if (formulaCode != null) {
            inventoryRepo.getStockFormulaDetail(formulaCode).doOnSuccess((list) -> {
                if (list != null) {
                    landingCriteriaTableModel.clear();
                    list.forEach((t) -> {
                        LandingHisCriteria ld = new LandingHisCriteria();
                        ld.setCriteriaCode(t.getCriteriaCode());
                        ld.setCriteriaName(t.getCriteriaName());
                        ld.setCriteriaUserCode(t.getUserCode());
                        ld.setPercent(t.getPercent());
                        ld.setPrice(t.getPrice());
                        ld.setPercentAllow(t.getPercentAllow());
                        ld.setAmount(t.getPercent() * t.getPrice());
                        landingCriteriaTableModel.addObject(ld);
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
            landing.setMacId(Global.macId);
            landing.setStockCode(stockAutoCompleter.getStock().getKey().getStockCode());
            landing.setQty(Util1.getDouble(txtQty.getValue()));
            landing.setWeight(Util1.getDouble(txtWeight.getValue()));
            landing.setTotalWeight(Util1.getDouble(txtWtTotal.getValue()));
            landing.setUnit(qtyUnitComboBoxModel.getSelectedObject().getKey().getUnitCode());
            landing.setWeightUnit(weightUnitComboBoxModel.getSelectedObject().getKey().getUnitCode());
            landing.setRemark(txtRemark.getText());
            landing.setRemark(txtCargo.getText());
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

    private void saveLanding() {
        if (isValidEntry()
                && landingCriteriaTableModel.isValidEntry()) {
            if (DateLockUtil.isLockDate(txtVouDate.getDate())) {
                DateLockUtil.showMessage(this);
                txtVouDate.requestFocus();
                return;
            }
            observer.selected("save", false);
            progress.setIndeterminate(true);
            landing.setListDel(landingCriteriaTableModel.getListDel());
            landing.setListDetail(landingCriteriaTableModel.getListDetail());
            inventoryRepo.save(landing).doOnSuccess((t) -> {
                progress.setIndeterminate(false);
                clear();
            }).doOnError((e) -> {
                observer.selected("save", true);
                JOptionPane.showMessageDialog(this, e.getMessage());
                progress.setIndeterminate(false);
            }).subscribe();
        }
    }

    private void clear() {
        landingCriteriaTableModel.clear();
        landingCriteriaTableModel.clearDelList();
        txtVouNo.setText(null);
        txtRemark.setText(null);
        txtCargo.setText(null);
        txtQty.setValue(null);
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
            txtQty.setValue(1);
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
            setCriteria(s.getFormulaCode());
            txtWtTotal.requestFocus();
        }
    }

    private void calQty() {
        double ttlWt = Util1.getDouble(txtWtTotal.getValue());
        double weight = Util1.getDouble(txtWeight.getValue());
        double amt = Util1.getDouble(txtAmt.getValue());
        double qty = ttlWt / weight;
        txtQty.setValue(qty);
        txtPrice.setValue(amt / qty);
        calCriteria();
    }

    private void calWtTotal() {
        double weight = Util1.getDouble(txtWeight.getValue());
        double qty = Util1.getDouble(txtQty.getValue());
        double price = Util1.getDouble(txtPrice.getValue());
        txtQty.setValue(weight * qty);
        txtAmt.setValue(qty * price);
        calCriteria();

    }

    private void focusTable() {
        int rc = tblCriteria.getRowCount();
        if (rc >= 1) {
            tblCriteria.setRowSelectionInterval(rc - 1, rc - 1);
            tblCriteria.setColumnSelectionInterval(0, 0);
            tblCriteria.requestFocus();
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
        landing = his;
        String vouNo = his.getKey().getVouNo();
        setLandingHis(vouNo);
        setLandingHisCriteria(vouNo);
    }

    private void setLandingHis(String vouNo) {
        inventoryRepo.findLanding(vouNo).doOnSuccess((l) -> {
            if (l != null) {
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
                txtQty.setValue(Util1.getDouble(l.getQty()));
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

    private void setLandingHisCriteria(String vouNo) {
        inventoryRepo.getLandingCriteria(vouNo).doOnSuccess((t) -> {
            if (t != null) {
                landingCriteriaTableModel.setListDetail(t);
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
        txtQty.setEnabled(t);
        txtWeight.setEnabled(t);
        cboQtyUnit.setEnabled(t);
        cboWUnit.setEnabled(t);
        txtWtTotal.setEditable(t);
        txtPrice.setEditable(t);
        txtAmt.setEnabled(t);
        tblCriteria.setEnabled(t);

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
            paymentDialog.setVouBalance(landing.getVouBalance());
            paymentDialog.setVisible(true);
            if (paymentDialog.isConfirm()) {
                landing.setVouPaid(paymentDialog.getVouPaid());
                landing.setVouBalance(paymentDialog.getVouBalance());
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
        lblStatus = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        txtCargo = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        lblCriteria = new javax.swing.JLabel();
        lblRC = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCriteria = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        txtStock = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtQty = new javax.swing.JFormattedTextField();
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
        chkPurchase = new javax.swing.JCheckBox();

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
                    .addComponent(txtGrade)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtPurAmt, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtCriteriaAmt, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(txtOriginalAmt, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                    .addComponent(txtPurPrice, javax.swing.GroupLayout.Alignment.TRAILING))
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
                .addContainerGap(159, Short.MAX_VALUE))
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

        lblStatus.setFont(Global.menuFont);
        lblStatus.setText("NEW");

        jLabel14.setFont(Global.lableFont);
        jLabel14.setText("Remark");

        txtRemark.setFont(Global.textFont);
        txtRemark.setName("txtRemark"); // NOI18N

        jLabel17.setFont(Global.lableFont);
        jLabel17.setText("Cargo");

        txtCargo.setFont(Global.textFont);
        txtCargo.setName("txtCargo"); // NOI18N

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 351, Short.MAX_VALUE)
                .addComponent(lblStatus)
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtLocation, txtRemark, txtTrader, txtVouDate, txtVouNo});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel3))
                            .addComponent(txtVouDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtRemark, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                                .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblCriteria.setFont(Global.lableFont);
        lblCriteria.setText("Criteria");

        lblRC.setFont(Global.lableFont);
        lblRC.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblRC.setText("0");

        jLabel12.setFont(Global.lableFont);
        jLabel12.setText("Records :");

        tblCriteria.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblCriteria);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(lblCriteria, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblRC, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 840, Short.MAX_VALUE))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
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

        txtQty.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtQty.setFont(Global.amtFont);
        txtQty.setName("txtQty"); // NOI18N
        txtQty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtQtyActionPerformed(evt);
            }
        });

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Qty");

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Unit");

        cboQtyUnit.setFont(Global.textFont);

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Weight");

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

        chkPurchase.setFont(Global.lableFont);
        chkPurchase.setText("Purchase");
        chkPurchase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkPurchaseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, 46, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtQty, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                            .addComponent(txtWeight))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboQtyUnit, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cboWUnit, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(txtStock, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE))
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtWtTotal, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                    .addComponent(txtPrice)
                    .addComponent(txtAmt))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(chkPurchase)
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
                        .addComponent(txtWtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(chkPurchase)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtQty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10)
                    .addComponent(cboQtyUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel18)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtWeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel15)
                    .addComponent(cboWUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel19)))
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
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(4, 4, 4)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observeMain();
    }//GEN-LAST:event_formComponentShown

    private void txtWtTotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtWtTotalActionPerformed
        // TODO add your handling code here:
        calQty();
    }//GEN-LAST:event_txtWtTotalActionPerformed

    private void txtQtyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtQtyActionPerformed
        // TODO add your handling code here:
        calWtTotal();
    }//GEN-LAST:event_txtQtyActionPerformed

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
        calQty();
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCriteria;
    private javax.swing.JLabel lblRC;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblCriteria;
    private javax.swing.JFormattedTextField txtAmt;
    private javax.swing.JTextField txtCargo;
    private javax.swing.JFormattedTextField txtCriteriaAmt;
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
            case "STOCK" ->
                setStock();
            case "LANDING-HISTORY" ->
                setVoucher((LandingHis) selectObj);
        }
    }

    @Override
    public void save() {
        saveLanding();
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
