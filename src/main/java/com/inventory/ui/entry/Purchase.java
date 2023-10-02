/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry;

import com.CloudIntegration;
import com.common.DateLockUtil;
import com.repo.AccountRepo;
import com.common.DecimalFormatRender;
import com.common.Global;
import com.common.KeyPropagate;
import com.common.PanelControl;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.common.Util1;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.inventory.editor.BatchAutoCompeter;
import com.inventory.editor.ExpenseEditor;
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.editor.LocationCellEditor;
import com.inventory.editor.StockCellEditor;
import com.inventory.editor.TraderAutoCompleter;
import com.inventory.model.Expense;
import com.inventory.model.GRN;
import com.inventory.model.Location;
import com.inventory.model.PurExpense;
import com.inventory.model.PurExpenseKey;
import com.inventory.model.PurHis;
import com.inventory.model.PurHisDetail;
import com.inventory.model.PurHisKey;
import com.inventory.model.StockUnit;
import com.inventory.model.Trader;
import com.inventory.model.VPurchase;
import com.inventory.ui.common.GRNTableModel;
import com.repo.InventoryRepo;
import com.inventory.ui.common.PurExpenseTableModel;
import com.inventory.ui.common.PurchaseTableModel;
import com.inventory.ui.common.StockInfoPanel;
import com.inventory.ui.entry.dialog.BatchSearchDialog;
import com.inventory.ui.entry.dialog.GRNDetailDialog;
import com.inventory.ui.entry.dialog.PurchaseAvgPriceDialog;
import com.inventory.ui.entry.dialog.PurchaseHistoryDialog;
import com.inventory.ui.setup.dialog.ExpenseSetupDialog;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.inventory.ui.setup.dialog.common.StockUnitEditor;
import com.toedter.calendar.JTextFieldDateEditor;
import com.repo.UserRepo;
import com.user.editor.CurrencyAutoCompleter;
import com.user.editor.ProjectAutoCompleter;
import com.user.model.Project;
import com.user.model.ProjectKey;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.view.JasperViewer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 *
 * @author wai yan
 */
@Component
@Slf4j
public class Purchase extends javax.swing.JPanel implements SelectionObserver, KeyListener, KeyPropagate, PanelControl {

    private final Image searchIcon = new ImageIcon(this.getClass().getResource("/images/search.png")).getImage();
    private final PurchaseTableModel purTableModel = new PurchaseTableModel();
    private PurchaseHistoryDialog dialog;
    private final Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
    @Autowired
    private InventoryRepo inventoryRepo;
    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private CloudIntegration integration;
    private CurrencyAutoCompleter currAutoCompleter;
    private TraderAutoCompleter traderAutoCompleter;
    private LocationAutoCompleter locationAutoCompleter;
    private ProjectAutoCompleter projectAutoCompleter;
    private BatchAutoCompeter batchAutoCompeter;
    private SelectionObserver observer;
    private JProgressBar progress;
    private PurHis ph = new PurHis();
    private Mono<List<Location>> monoLoc;
    private Mono<List<StockUnit>> monoUnit;
    private List<StockUnit> listUnit;
    private final PurExpenseTableModel expenseTableModel = new PurExpenseTableModel();
    private final GRNTableModel grnTableModel = new GRNTableModel();
    private BatchSearchDialog batchDialog;
    private GRNDetailDialog grnDialog;
    private final StockInfoPanel stockInfoPanel = new StockInfoPanel();

    public LocationAutoCompleter getLocationAutoCompleter() {
        return locationAutoCompleter;
    }

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    /**
     * Creates new form Purchase
     */
    public Purchase() {
        initComponents();
        lblStatus.setForeground(Color.GREEN);
        initKeyListener();
        initTextBoxFormat();
        initTextBoxValue();
        initDateListener();
        actionMapping();
    }

    private void actionMapping() {
        String solve = "delete";
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        tblPur.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, solve);
        tblPur.getActionMap().put(solve, new DeleteAction());
        tblPur.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK), "avg-price");
        tblPur.getActionMap().put("avg-price", new AvgPriceAction());

    }

    private class AvgPriceAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            int row = tblPur.convertRowIndexToModel(tblPur.getSelectedRow());
            PurHisDetail pd = purTableModel.getObject(row);
            if (pd.getStockCode() != null) {
                PurchaseAvgPriceDialog d = new PurchaseAvgPriceDialog(Global.parentForm);
                d.setInventoryRepo(inventoryRepo);
                d.setListUnit(listUnit);
                d.setPd(pd);
                d.initMain();
                d.setLocationRelativeTo(null);
                d.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                            d.dispose();
                        }
                    }
                });
                d.setVisible(true);
                if (d.isConfirm()) {
                    purTableModel.setValueAt(pd, row, 0);
                }
            }
        }
    }

    private class DeleteAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            deleteTran();
        }
    }

    public void initMain() {
        initCombo();
        initPurTable();
        initPanelExpesne();
        initStockInfo();
        assignDefaultValue();
        txtCus.requestFocus();
    }

    private void initStockInfo() {
        if (Util1.getBoolean(ProUtil.getProperty(ProUtil.P_SHOW_STOCKINFO))) {
            stockInfoPanel.setUserRepo(userRepo);
            stockInfoPanel.setInventoryRepo(inventoryRepo);
            panelStockInfo.setLayout(new BorderLayout());
            panelStockInfo.add(stockInfoPanel, BorderLayout.NORTH);
        } else {
            panelStockInfo.setVisible(false);
        }

    }

    private void setStockInfo() {
        int row = tblPur.convertRowIndexToModel(tblPur.getSelectedRow());
        if (row >= 0) {
            PurHisDetail phd = purTableModel.getObject(row);
            stockInfoPanel.setStock(phd.getStockCode());
        }
    }

    private void initDateListener() {
        txtPurDate.getDateEditor().getUiComponent().setName("txtPurDate");
        txtPurDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtPurDate.getDateEditor().getUiComponent().addFocusListener(fa);
        txtDueDate.getDateEditor().getUiComponent().setName("txtDueDate");
        txtDueDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtDueDate.getDateEditor().getUiComponent().addFocusListener(fa);
        txtCurrency.addFocusListener(fa);
        txtCus.addFocusListener(fa);
        txtLocation.addFocusListener(fa);
        txtRemark.addFocusListener(fa);
        txtReference.addFocusListener(fa);
        txtProjectNo.addFocusListener(fa);

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

    private void initPanelExpesne() {
        boolean status = Util1.getBoolean(ProUtil.getProperty(ProUtil.P_SHOW_EXPENSE));
        if (status) {
            expenseTableModel.setObserver(this);
            expenseTableModel.setTxtVouTotal(txtVouTotal);
            expenseTableModel.setTable(tblExpense);
            tblExpense.setModel(expenseTableModel);
            tblExpense.getTableHeader().setFont(Global.tblHeaderFont);
            tblExpense.setFont(Global.textFont);
            tblExpense.setRowHeight(Global.tblRowHeight);
            tblExpense.setCellSelectionEnabled(true);
            tblExpense.setShowGrid(true);
            tblExpense.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                    .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
            tblExpense.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            tblExpense.getColumnModel().getColumn(0).setPreferredWidth(100);
            tblExpense.getColumnModel().getColumn(1).setPreferredWidth(50);
            tblExpense.getColumnModel().getColumn(2).setPreferredWidth(20);
            inventoryRepo.getExpense().subscribe((t) -> {
                tblExpense.getColumnModel().getColumn(0).setCellEditor(new ExpenseEditor(t));
            });
            tblExpense.getColumnModel().getColumn(1).setCellEditor(new AutoClearEditor());
            tblExpense.getColumnModel().getColumn(2).setCellEditor(new AutoClearEditor());
            txtExpense.setFormatterFactory(Util1.getDecimalFormat());
            txtExpense.setFont(Global.amtFont);
            txtExpense.setHorizontalAlignment(JTextField.RIGHT);
            getExpense();
        } else {
            panelExpense.setVisible(false);
        }
    }

    private void getExpense() {
        expenseTableModel.clear();
        inventoryRepo.getExpense().subscribe((t) -> {
            for (Expense e : t) {
                PurExpense p = new PurExpense();
                PurExpenseKey key = new PurExpenseKey();
                key.setExpenseCode(e.getKey().getExpenseCode());
                key.setCompCode(e.getKey().getCompCode());
                p.setKey(key);
                p.setExpenseName(e.getExpenseName());
                p.setPercent(Util1.getDouble(e.getPercent()));
                p.setAmount(0.0);
                expenseTableModel.addObject(p);
            }
            expenseTableModel.addNewRow();
            expProgress.setIndeterminate(false);
        });

    }

    private void initPurTable() {
        monoUnit = inventoryRepo.getStockUnit();
        tblPur.setModel(purTableModel);
        purTableModel.setLblRec(lblRec);
        purTableModel.setInventoryRepo(inventoryRepo);
        purTableModel.setVouDate(txtPurDate);
        purTableModel.setParent(tblPur);
        purTableModel.setPurchase(this);
        purTableModel.addNewRow();
        purTableModel.setObserver(this);
        tblPur.getTableHeader().setFont(Global.tblHeaderFont);
        tblPur.setCellSelectionEnabled(true);
        tblPur.getColumnModel().getColumn(0).setPreferredWidth(50);//Code
        tblPur.getColumnModel().getColumn(1).setPreferredWidth(350);//Name
        tblPur.getColumnModel().getColumn(2).setPreferredWidth(100);//amt
        tblPur.getColumnModel().getColumn(3).setPreferredWidth(60);//Location
        tblPur.getColumnModel().getColumn(4).setPreferredWidth(60);//qty
        tblPur.getColumnModel().getColumn(5).setPreferredWidth(1);//unit
        tblPur.getColumnModel().getColumn(6).setPreferredWidth(1);//price
        tblPur.getColumnModel().getColumn(7).setPreferredWidth(40);//amt
        tblPur.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor(inventoryRepo));
        tblPur.getColumnModel().getColumn(1).setCellEditor(new StockCellEditor(inventoryRepo));
        monoLoc.subscribe((t) -> {
            tblPur.getColumnModel().getColumn(3).setCellEditor(new LocationCellEditor(t));
        });
        tblPur.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());//qty
        monoUnit.subscribe((t) -> {
            listUnit = t;
            tblPur.getColumnModel().getColumn(5).setCellEditor(new StockUnitEditor(t));
        });
        tblPur.getColumnModel().getColumn(6).setCellEditor(new AutoClearEditor());
        tblPur.getColumnModel().getColumn(7).setCellEditor(new AutoClearEditor());
        tblPur.setDefaultRenderer(Object.class, new DecimalFormatRender());
        tblPur.setDefaultRenderer(Double.class, new DecimalFormatRender());
        tblPur.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblPur.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void initCombo() {
        traderAutoCompleter = new TraderAutoCompleter(txtCus, inventoryRepo, null, false, "SUP");
        traderAutoCompleter.setObserver(this);
        monoLoc = inventoryRepo.getLocation();
        currAutoCompleter = new CurrencyAutoCompleter(txtCurrency, null);
        userRepo.getCurrency().subscribe((t) -> {
            currAutoCompleter.setListCurrency(t);
        });
        userRepo.getDefaultCurrency().subscribe((c) -> {
            currAutoCompleter.setCurrency(c);
        });
        locationAutoCompleter = new LocationAutoCompleter(txtLocation, null, false, false);
        locationAutoCompleter.setObserver(this);
        monoLoc.subscribe((t) -> {
            locationAutoCompleter.setListLocation(t);
        });
        projectAutoCompleter = new ProjectAutoCompleter(txtProjectNo, userRepo, null, false);
        projectAutoCompleter.setObserver(this);
        batchAutoCompeter = new BatchAutoCompeter(txtBatchNo, inventoryRepo, null, false);
        batchAutoCompeter.setObserver(this);
    }

    private void initKeyListener() {
        txtPurDate.getDateEditor().getUiComponent().setName("txtPurDate");
        txtPurDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtDueDate.getDateEditor().getUiComponent().setName("txtDueDate");
        txtDueDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtVouNo.addKeyListener(this);
        txtRemark.addKeyListener(this);
        txtCus.addKeyListener(this);
        txtLocation.addKeyListener(this);
        txtCurrency.addKeyListener(this);
        tblPur.addKeyListener(this);
        txtVouDiscP.addKeyListener(this);
        txtVouDiscount.addKeyListener(this);
        txtTax.addKeyListener(this);
        txtVouTaxP.addKeyListener(this);
        txtVouPaid.addKeyListener(this);
        txtComAmt.addKeyListener(this);
        txtComPercent.addKeyListener(this);
        txtDefaultCom.addKeyListener(this);
    }

    private void initTextBoxValue() {
        txtVouTotal.setValue(0.00);
        txtVouDiscount.setValue(0.00);
        txtTax.setValue(0.00);
        txtVouPaid.setValue(0.00);
        txtVouBalance.setValue(0.00);
        txtVouTaxP.setValue(0.00);
        txtVouDiscP.setValue(0.00);
        txtGrandTotal.setValue(0.00);
        txtComPercent.setValue(0.00);
        txtComAmt.setValue(0.00);
        txtExpense.setValue(0);
        txtQty.setText("0");
    }

    private void initTextBoxFormat() {
        txtVouBalance.setFormatterFactory(Util1.getDecimalFormat());
        txtVouDiscount.setFormatterFactory(Util1.getDecimalFormat());
        txtVouPaid.setFormatterFactory(Util1.getDecimalFormat());
        txtVouTotal.setFormatterFactory(Util1.getDecimalFormat());
        txtVouDiscP.setFormatterFactory(Util1.getDecimalFormat());
        txtVouTaxP.setFormatterFactory(Util1.getDecimalFormat());
        txtGrandTotal.setFormatterFactory(Util1.getDecimalFormat());
        txtTax.setFormatterFactory(Util1.getDecimalFormat());
        txtComPercent.setFormatterFactory(Util1.getDecimalFormat());
        txtComAmt.setFormatterFactory(Util1.getDecimalFormat());
        txtQty.setFormatterFactory(Util1.getDecimalFormat());
        txtQty.setHorizontalAlignment(JTextField.RIGHT);
        txtDefaultCom.setFormatterFactory(Util1.getDecimalFormat());
        txtDefaultCom.setHorizontalAlignment(JTextField.RIGHT);
        txtDefaultCom.setFont(Global.textFont);
    }

    private void assignDefaultValue() {
        if (currAutoCompleter != null) {
            userRepo.getDefaultCurrency().subscribe((t) -> {
                currAutoCompleter.setCurrency(t);
            });
        }
        if (locationAutoCompleter != null) {
            inventoryRepo.getDefaultLocation().subscribe((tt) -> {
                locationAutoCompleter.setLocation(tt);
            }, (e) -> {
                log.error(e.getMessage());
            });
        }
        inventoryRepo.getDefaultCustomer().subscribe((t) -> {
            traderAutoCompleter.setTrader(t);
        });

        txtPurDate.setDate(Util1.getTodayDate());
        txtDueDate.setDate(null);
        txtCurrency.setEnabled(ProUtil.isMultiCur());
        txtVouNo.setText(null);
        txtRemark.setText(null);
        txtReference.setText(null);
        batchAutoCompeter.setBatch(null);
        btnBatch.setText("Batch");
        txtComPercent.setValue(Util1.getDouble(ProUtil.getProperty("purchase.commission")));
        double commAmt = Util1.getDouble(ProUtil.getProperty(ProUtil.P_COM_AMT));
        txtDefaultCom.setValue(commAmt);
    }

    private void clear() {
        disableForm(true);
        btnBatch.setEnabled(true);
        purTableModel.clear();
        purTableModel.addNewRow();
        purTableModel.clearDelList();
        grnTableModel.clear();
        initTextBoxValue();
        assignDefaultValue();
        ph = new PurHis();
        lblStatus.setText("NEW");
        lblStatus.setForeground(Color.GREEN);
        projectAutoCompleter.setProject(null);
        getExpense();
        txtCus.requestFocus();
    }

    public void savePur(boolean print) {
        if (isValidEntry() && purTableModel.isValidEntry()) {
            if (DateLockUtil.isLockDate(txtPurDate.getDate())) {
                DateLockUtil.showMessage(this);
                txtPurDate.requestFocus();
                return;
            }
            progress.setIndeterminate(true);
            observer.selected("save", false);
            ph.setListPD(purTableModel.getListDetail());
            ph.setListDel(purTableModel.getDelList());
            ph.setListExpense(expenseTableModel.getExpenseList());
            inventoryRepo.save(ph).subscribe((t) -> {
                clear();
                progress.setIndeterminate(false);
                if (print) {
                    printVoucher(t);
                }
            }, (e) -> {
                progress.setIndeterminate(false);
                observer.selected("save", true);
                JOptionPane.showMessageDialog(this, e.getMessage());
            });
        }
    }

    private boolean isValidEntry() {
        boolean status = true;
        if (lblStatus.getText().equals("DELETED")) {
            status = false;
            clear();
        } else if (currAutoCompleter.getCurrency() == null) {
            JOptionPane.showMessageDialog(this, "Choose Currency.",
                    "No Currency.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtCurrency.requestFocus();
        } else if (locationAutoCompleter.getLocation() == null) {
            JOptionPane.showMessageDialog(this, "Choose Location.",
                    "No Location.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtLocation.requestFocus();
        } else if (Util1.getDouble(txtVouTotal.getValue()) <= 0) {
            JOptionPane.showMessageDialog(this, "Invalid Amount.",
                    "No Pur Record.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtLocation.requestFocus();
        } else if (Objects.isNull(traderAutoCompleter.getTrader())) {
            JOptionPane.showMessageDialog(this, "Choose Supplier.",
                    "Choose Supplier.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtCus.requestFocus();
        } else if (!Util1.isDateBetween(txtPurDate.getDate())) {
            JOptionPane.showMessageDialog(this, "Invalid Date.",
                    "Validation.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtPurDate.requestFocus();
        } else {
            String traderCode = traderAutoCompleter.getTrader().getKey().getCode();
            ph.setRemark(txtRemark.getText());
            ph.setDiscP(Util1.getDouble(txtVouDiscP.getValue()));
            ph.setDiscount(Util1.getDouble(txtVouDiscount.getValue()));
            ph.setTaxP(Util1.getDouble(txtVouTaxP.getValue()));
            ph.setTaxAmt(Util1.getDouble(txtTax.getValue()));
            ph.setPaid(Util1.getDouble(txtVouPaid.getValue()));
            ph.setBalance(Util1.getDouble(txtVouBalance.getValue()));
            ph.setCurCode(currAutoCompleter.getCurrency().getCurCode());
            ph.setDeleted(ph.isDeleted());
            ph.setLocCode(locationAutoCompleter.getLocation().getKey().getLocCode());
            Project p = projectAutoCompleter.getProject();
            ph.setProjectNo(p == null ? null : p.getKey().getProjectNo());
            ph.setVouDate(Util1.convertToLocalDateTime(txtPurDate.getDate()));
            ph.setTraderCode(traderCode);
            ph.setVouTotal(Util1.getDouble(txtVouTotal.getValue()));
            ph.setStatus(lblStatus.getText());
            ph.setReference(txtReference.getText());
            ph.setBatchNo(txtBatchNo.getText());
            ph.setCommP(Util1.getDouble(txtComPercent.getValue()));
            ph.setCommAmt(Util1.getDouble(txtComAmt.getValue()));
            ph.setExpense(Util1.getDouble(txtExpense.getValue()));
            ph.setDueDate(txtDueDate.getDate());
            if (lblStatus.getText().equals("NEW")) {
                PurHisKey key = new PurHisKey();
                key.setCompCode(Global.compCode);
                key.setVouNo(null);
                ph.setKey(key);
                ph.setDeptId(Global.deptId);
                ph.setCreatedDate(LocalDateTime.now());
                ph.setCreatedBy(Global.loginUser.getUserCode());
                ph.setSession(Global.sessionId);
                ph.setMacId(Global.macId);
            } else {
                ph.setUpdatedBy(Global.loginUser.getUserCode());
                String vouNo = ph.getKey().getVouNo();
                boolean exist = inventoryRepo.checkPaymentExist(vouNo, traderCode, "S").block();
                if (exist) {
                    JOptionPane.showMessageDialog(this, "This voucher is already paid in supplier payment.", "Message", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        }
        return status;
    }

    private void deletePur() {
        String status = lblStatus.getText();
        switch (status) {
            case "EDIT" -> {
                int yes_no = JOptionPane.showConfirmDialog(this,
                        "Are you sure to delete?", "Purchase Voucher Delete.", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                if (yes_no == 0) {
                    inventoryRepo.delete(ph.getKey()).subscribe((t) -> {
                        clear();
                    });
                }
            }
            case "DELETED" -> {
                int yes_no = JOptionPane.showConfirmDialog(this,
                        "Are you sure to restore?", "Save Voucher Restore.", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (yes_no == 0) {
                    ph.setDeleted(false);
                    inventoryRepo.restore(ph.getKey()).subscribe((t) -> {
                        lblStatus.setText("EDIT");
                        lblStatus.setForeground(Color.blue);
                        disableForm(true);
                    });
                }
            }
            default ->
                JOptionPane.showMessageDialog(this, "Voucher can't delete.");
        }

    }

    private void deleteTran() {
        int row = tblPur.convertRowIndexToModel(tblPur.getSelectedRow());
        if (row >= 0) {
            if (tblPur.getCellEditor() != null) {
                tblPur.getCellEditor().stopCellEditing();
            }
            int yes_no = JOptionPane.showConfirmDialog(this,
                    "Are you sure to delete?", "Purchase Transaction delete.", JOptionPane.YES_NO_OPTION);
            if (yes_no == 0) {
                purTableModel.delete(row);
                calculateTotalAmount(false);
            }
        }
    }

    private void calExpense() {
        float ttlExp = 0.0f;
        List<PurExpense> list = expenseTableModel.getListDetail();
        for (int i = 0; i < list.size(); i++) {
            PurExpense p = list.get(i);
            if (p.getPercent() != null) {
                double percent = Util1.getDouble(p.getPercent());
                if (percent > 0) {
                    double vouTotal = Util1.getDouble(txtVouTotal.getValue());
                    p.setAmount(vouTotal * (percent / 100));
                } else {
                    p.setAmount(0.0);
                }
            }
            expenseTableModel.fireTableRowsUpdated(i, i);
            ttlExp += Util1.getFloat(p.getAmount());
        }
        txtExpense.setValue(ttlExp);
    }

    private void calQty(List<PurHisDetail> list) {
        double ttlQty = 0;
        for (PurHisDetail p : list) {
            ttlQty += Util1.getDouble(p.getQty());
        }
        txtQty.setValue(ttlQty);
    }

    private void calculateTotalAmount(boolean partial) {
        List<PurHisDetail> list = purTableModel.getListDetail();
        calQty(list);
        calComission();
        double totalVouBalance;
        double ttl = list.stream().mapToDouble((obj) -> Util1.getDouble(obj.getAmount())).sum();
        double totalAmount = Math.round(ttl);
        txtVouTotal.setValue(totalAmount);
        calExpense();
        //cal discAmt
        double discp = Util1.getDouble(txtVouDiscP.getValue());
        if (discp > 0) {
            double discountAmt = (totalAmount * (discp / 100));
            txtVouDiscount.setValue(Util1.getDouble(discountAmt));
        }
        //cal Commission
        double comp = Util1.getDouble(txtComPercent.getValue());
        if (comp > 0) {
            double amt = (totalAmount * (comp / 100));
            txtComAmt.setValue(amt);
        }

        //calculate taxAmt
        double taxp = Util1.getDouble(txtVouTaxP.getValue());
        double taxAmt = Util1.getDouble(txtTax.getValue());
        if (taxp > 0) {
            double afterDiscountAmt = totalAmount - Util1.getDouble(txtVouDiscount.getValue());
            double totalTax = (afterDiscountAmt * taxp) / 100;
            txtTax.setValue(Util1.getDouble(totalTax));
        } else if (taxAmt > 0) {
            double afterDiscountAmt = totalAmount - Util1.getDouble(txtVouDiscount.getValue());
            taxp = (taxAmt / afterDiscountAmt) * 100;
            txtVouTaxP.setValue(Util1.getDouble(taxp));
        }
        double ttlExp = Util1.getDouble(txtExpense.getValue());
        txtGrandTotal.setValue(totalAmount
                + Util1.getDouble(txtTax.getValue())
                - Util1.getDouble(txtVouDiscount.getValue())
                - ttlExp);
        double grandTotal = Util1.getDouble(txtGrandTotal.getValue());

        double paid = Util1.getDouble(txtVouPaid.getValue());
        if (!partial) {
            if (paid == 0 || paid != grandTotal) {
                if (chkPaid.isSelected()) {
                    txtVouPaid.setValue(grandTotal);
                } else {
                    txtVouPaid.setValue(0);
                }
            }
        }

        paid = Util1.getDouble(txtVouPaid.getValue());
        totalVouBalance = grandTotal - paid;
        txtVouBalance.setValue(totalVouBalance);
    }

    public void historyPur() {
        if (dialog == null) {
            dialog = new PurchaseHistoryDialog(Global.parentForm);
            dialog.setInventoryRepo(inventoryRepo);
            dialog.setUserRepo(userRepo);
            dialog.setIconImage(searchIcon);
            dialog.setCloudIntegration(integration);
            dialog.setObserver(this);
            dialog.initMain();
            dialog.setSize(Global.width - 100, Global.height - 100);
            dialog.setLocationRelativeTo(null);
        }
        dialog.search();
    }

    public void setVoucher(PurHis pur, boolean local) {
        purTableModel.clear();
        expenseTableModel.clear();
        progress.setIndeterminate(true);
        ph = pur;
        setCompeter(ph);
        searchVoucher(ph);
        searchVoucherDetail(ph, local);
    }

    private void searchVoucherDetail(PurHis ph, boolean local) {
        String vouNo = ph.getKey().getVouNo();
        inventoryRepo.getPurDetail(vouNo, ph.getDeptId(), local)
                .subscribe((t) -> {
                    purTableModel.setListDetail(t);
                    purTableModel.addNewRow();
                    calQty(t);
                    focusTable();
                    progress.setIndeterminate(false);
                }, (e) -> {
                    JOptionPane.showMessageDialog(this, e.getMessage());
                    progress.setIndeterminate(false);
                }, () -> {
                });
        searchExpense(vouNo);
    }

    private void searchVoucher(PurHis ph) {
        String vouNo = ph.getKey().getVouNo();
        ph.setVouLock(!ph.getDeptId().equals(Global.deptId));
        if (ph.isVouLock()) {
            lblStatus.setText("Voucher is locked.");
            lblStatus.setForeground(Color.RED);
            disableForm(false);
        } else if (!ProUtil.isPurchaseEdit()) {
            lblStatus.setText("No Permission.");
            lblStatus.setForeground(Color.RED);
            disableForm(false);
        } else if (ph.isDeleted()) {
            lblStatus.setText("DELETED");
            lblStatus.setForeground(Color.RED);
            disableForm(false);
        } else if (DateLockUtil.isLockDate(ph.getVouDate())) {
            lblStatus.setText(DateLockUtil.MESSAGE);
            lblStatus.setForeground(Color.RED);
            disableForm(false);
        } else {
            inventoryRepo.checkPaymentExist(vouNo, ph.getTraderCode(), "S").subscribe((exist) -> {
                if (exist) {
                    lblStatus.setText("Voucher is locked because of payment.");
                    lblStatus.setForeground(Color.red);
                    disableForm(false);
                } else {
                    lblStatus.setForeground(Color.blue);
                    lblStatus.setText("EDIT");
                    disableForm(true);
                }
                btnBatch.setText("View");
            });
        }
        txtVouNo.setText(ph.getKey().getVouNo());
        txtDueDate.setDate(ph.getDueDate());
        txtRemark.setText(ph.getRemark());
        txtPurDate.setDate(Util1.convertToDate(ph.getVouDate()));
        txtVouTotal.setValue(Util1.getDouble(ph.getVouTotal()));
        txtVouDiscP.setValue(Util1.getDouble(ph.getDiscP()));
        txtVouDiscount.setValue(Util1.getDouble(ph.getDiscount()));
        txtVouTaxP.setValue(Util1.getDouble(ph.getTaxP()));
        txtTax.setValue(Util1.getDouble(ph.getTaxAmt()));
        txtVouPaid.setValue(Util1.getDouble(ph.getPaid()));
        txtVouBalance.setValue(Util1.getDouble(ph.getBalance()));
        txtGrandTotal.setValue(Util1.getDouble(txtGrandTotal.getValue()));
        chkPaid.setSelected(Util1.getDouble(ph.getPaid()) > 0);
        txtReference.setText(ph.getReference());
        txtBatchNo.setText(ph.getBatchNo());
        txtComPercent.setValue(Util1.getDouble(ph.getCommP()));
        txtComAmt.setValue(Util1.getDouble(ph.getCommAmt()));
        txtExpense.setValue(Util1.getDouble(ph.getExpense()));
    }

    private void setCompeter(PurHis ph) {
        userRepo.findCurrency(ph.getCurCode()).doOnSuccess((t) -> {
            currAutoCompleter.setCurrency(t);
        }).subscribe();
        inventoryRepo.findLocation(ph.getLocCode()).doOnSuccess((t) -> {
            locationAutoCompleter.setLocation(t);
        }).subscribe();
        inventoryRepo.findTrader(ph.getTraderCode()).doOnSuccess((t) -> {
            traderAutoCompleter.setTrader(t);
        }).subscribe();
        userRepo.find(new ProjectKey(ph.getProjectNo(), Global.compCode)).doOnSuccess(t -> {
            projectAutoCompleter.setProject(t);
        }).subscribe();
    }

    private void searchExpense(String vouNo) {
        if (panelExpense.isVisible()) {
            expProgress.setIndeterminate(true);
            inventoryRepo.getPurExpense(vouNo).subscribe((t) -> {
                if (t.isEmpty()) {
                    getExpense();
                } else {
                    expenseTableModel.setListDetail(t);
                    expenseTableModel.addNewRow();
                    expProgress.setIndeterminate(false);
                }
            }, (e) -> {
                JOptionPane.showMessageDialog(this, e.getMessage());
                expProgress.setIndeterminate(false);
            });
        }
    }

    private void disableForm(boolean status) {
        tblPur.setEnabled(status);
        tblExpense.setEnabled(status);
        panelPur.setEnabled(status);
        panelExpense.setEnabled(status);
        txtQty.setEnabled(status);
        txtComAmt.setEnabled(status);
        txtPurDate.setEnabled(status);
        txtCus.setEnabled(status);
        txtLocation.setEnabled(status);
        txtRemark.setEnabled(status);
        txtCurrency.setEnabled(status);
        txtDueDate.setEnabled(status);
        txtVouPaid.setEnabled(status);
        txtTax.setEnabled(status);
        txtVouTaxP.setEnabled(status);
        txtVouDiscP.setEnabled(status);
        txtVouDiscount.setEnabled(status);
        txtGrandTotal.setEnabled(status);
        txtReference.setEnabled(status);
        txtComAmt.setEnabled(status);
        txtComPercent.setEnabled(status);
        txtBatchNo.setEnabled(status);
        observer.selected("save", status);
        observer.selected("print", status);
    }

    private void setAllLocation() {
        List<PurHisDetail> listPurDetail = purTableModel.getListDetail();
        Location l = locationAutoCompleter.getLocation();
        if (listPurDetail != null) {
            listPurDetail.forEach(sd -> {
                sd.setLocCode(l.getKey().getLocCode());
                sd.setLocName(l.getLocName());

            });
        }
        purTableModel.setListDetail(listPurDetail);
    }

    private void printVoucher(PurHis p) {
        String vouNo = p.getKey().getVouNo();
        Mono<List<VPurchase>> p1 = inventoryRepo.getPurchaseReport(vouNo);
        Mono<List<PurExpense>> p2 = inventoryRepo.getPurExpense(vouNo);
        p1.zipWith(p2).hasElement().subscribe((t) -> {
            log.info("" + t);
        });
        p1.zipWith(p2).subscribe((t) -> {
            List<VPurchase> list = t.getT1();
            List<PurExpense> listEx = t.getT2();
            listEx.removeIf((ex) -> Util1.getDouble(ex.getAmount()) == 0);
            if (list != null) {
                String key = "report.purchase.voucher";
                String reportName = ProUtil.getProperty(key);
                if (reportName != null) {
                    try {
                        String logoPath = String.format("images%s%s", File.separator, ProUtil.getProperty("logo.name"));
                        Map<String, Object> param = new HashMap<>();
                        param.put("p_print_date", Util1.getTodayDateTime());
                        param.put("p_comp_name", Global.companyName);
                        param.put("p_comp_address", Global.companyAddress);
                        param.put("p_comp_phone", Global.companyPhone);
                        param.put("p_logo_path", logoPath);
                        param.put("p_remark", p.getRemark());
                        param.put("p_vou_no", vouNo);
                        param.put("p_vou_date", Util1.toDateStr(p.getVouDate(), "dd/MM/yyyy"));
                        param.put("p_vou_total", p.getVouTotal());
                        param.put("p_exp", Util1.getDouble(p.getExpense()) * -1);
                        param.put("p_vou_paid", p.getPaid());
                        param.put("p_vou_balance", p.getBalance());
                        param.put("p_batch_no", p.getBatchNo());
                        param.put("SUBREPORT_DIR", "report/");
                        String reportPath = String.format("report%s%s", File.separator, reportName.concat(".jasper"));
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode n1 = mapper.readTree(gson.toJson(list));
                        JsonDataSource d1 = new JsonDataSource(n1, null) {
                        };
                        ObjectMapper m2 = new ObjectMapper();
                        JsonNode n2 = m2.readTree(gson.toJson(listEx));
                        JsonDataSource d2 = new JsonDataSource(n2, null) {
                        };
                        param.put("p_sub_data", d2);
                        JasperPrint main = JasperFillManager.fillReport(reportPath, param, d1);
                        JasperViewer.viewReport(main, false);
                    } catch (JsonProcessingException | JRException e) {
                        JOptionPane.showMessageDialog(this, e.getMessage());
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "define report in " + key);
                }
            }
        }, (e) -> {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }, () -> {
        });
    }

    private void focusTable() {
        int rc = tblPur.getRowCount();
        if (rc >= 1) {
            tblPur.setRowSelectionInterval(rc - 1, rc - 1);
            tblPur.setColumnSelectionInterval(0, 0);
            tblPur.requestFocus();
        } else {
            txtPurDate.requestFocusInWindow();
        }
    }

    public void addTrader(Trader t) {
        traderAutoCompleter.addTrader(t);
    }

    public void setTrader(Trader t, int row) {
        traderAutoCompleter.setTrader(t, row);
    }

    private void batchDialog() {
        String text = btnBatch.getText();
        if (text.equals("Batch")) {
            if (batchDialog == null) {
                batchDialog = new BatchSearchDialog(Global.parentForm);
                batchDialog.setInventoryRepo(inventoryRepo);
                batchDialog.setObserver(this);
                batchDialog.initMain();
                batchDialog.setLocationRelativeTo(null);
            }
            batchDialog.searchBatch();
        } else if (text.equals("View")) {
            if (grnDialog == null) {
                grnDialog = new GRNDetailDialog(Global.parentForm);
                grnDialog.setSize(Global.width - 30, Global.height / 3);
                grnDialog.setInventoryRepo(inventoryRepo);
                grnDialog.initMain();
                grnDialog.setLocationRelativeTo(null);
            }
            grnDialog.searchGRNDetail(txtBatchNo.getText());
        }
    }

    private void expenseDialog() {
        ExpenseSetupDialog d = new ExpenseSetupDialog(Global.parentForm, true);
        d.setInventoryRepo(inventoryRepo);
        d.setAccountRepo(accountRepo);
        d.initMain();
        d.setLocationRelativeTo(null);
        d.setVisible(true);
    }

    private void setVoucherDetail(GRN g) {
        purTableModel.clear();
        String vouNo = g.getKey().getVouNo();
        Integer deptId = g.getDeptId();
        String batchNo = g.getBatchNo();
        inventoryRepo.findGRN(vouNo).subscribe((grn) -> {
            batchAutoCompeter.setBatch(grn);
            btnBatch.setText(grn.getBatchNo() == null ? "Batch" : "View");
        });
        Mono<Trader> trader = inventoryRepo.findTrader(g.getTraderCode());
        trader.subscribe((tt) -> {
            traderAutoCompleter.setTrader(tt);
        });
        txtRemark.setText(g.getRemark());
        if (batchDialog.getChkGRN().isSelected()) {
            inventoryRepo.getGRNDetail(vouNo, deptId).subscribe((t) -> {
                t.forEach((sd) -> {
                    PurHisDetail pd = new PurHisDetail();
                    pd.setStockCode(sd.getStockCode());
                    pd.setUserCode(sd.getUserCode());
                    pd.setStockName(sd.getStockName());
                    pd.setRelName(sd.getRelName());
                    pd.setAvgQty(0.0);
                    pd.setQty(Util1.getDouble(sd.getQty()));
                    pd.setUnitCode(sd.getUnit());
                    pd.setPrice(0.0);
                    pd.setAmount(0.0);
                    pd.setLocCode(sd.getLocCode());
                    pd.setLocName(sd.getLocName());
                    purTableModel.addPurchase(pd);
                });
                purTableModel.addNewRow();
                calculateTotalAmount(false);
            });
        } else if (batchDialog.getChkSale().isSelected()) {
            boolean detail = Util1.getBoolean(ProUtil.getProperty(ProUtil.P_BATCH_DETAIL));
            inventoryRepo.getSaleByBatch(batchNo, detail).subscribe((t) -> {
                t.forEach((sd) -> {
                    PurHisDetail pd = new PurHisDetail();
                    pd.setStockCode(sd.getStockCode());
                    pd.setUserCode(sd.getUserCode());
                    pd.setStockName(sd.getStockName());
                    pd.setRelName(sd.getRelName());
                    pd.setAvgQty(0.0);
                    pd.setQty(sd.getQty());
                    pd.setUnitCode(sd.getUnitCode());
                    pd.setPrice(sd.getPrice());
                    pd.setAmount(sd.getAmount());
                    pd.setLocCode(sd.getLocCode());
                    pd.setLocName(sd.getLocName());
                    purTableModel.addPurchase(pd);
                });
                purTableModel.addNewRow();
                calculateTotalAmount(false);
            });
        }

    }

    private void calComission() {
        double qty = Util1.getDouble(txtQty.getText());
        double price = Util1.getDouble(txtDefaultCom.getValue());
        txtComAmt.setValue(qty * price);
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

        panelPur = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtCus = new javax.swing.JTextField();
        txtVouNo = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtDueDate = new com.toedter.calendar.JDateChooser();
        txtPurDate = new com.toedter.calendar.JDateChooser();
        txtCurrency = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        txtLocation = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtReference = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtBatchNo = new javax.swing.JTextField();
        txtProjectNo = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        lblStatus = new javax.swing.JLabel();
        lblRec = new javax.swing.JLabel();
        btnBatch = new javax.swing.JButton();
        panelExpense = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblExpense = new javax.swing.JTable();
        txtExpense = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        expProgress = new javax.swing.JProgressBar();
        jLabel11 = new javax.swing.JLabel();
        panelStockInfo = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        txtVouTotal = new javax.swing.JFormattedTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        txtVouDiscP = new javax.swing.JFormattedTextField();
        txtVouDiscount = new javax.swing.JFormattedTextField();
        txtVouTaxP = new javax.swing.JFormattedTextField();
        txtTax = new javax.swing.JFormattedTextField();
        txtVouPaid = new javax.swing.JFormattedTextField();
        txtVouBalance = new javax.swing.JFormattedTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel20 = new javax.swing.JLabel();
        txtGrandTotal = new javax.swing.JFormattedTextField();
        jSeparator2 = new javax.swing.JSeparator();
        chkPaid = new javax.swing.JCheckBox();
        jLabel18 = new javax.swing.JLabel();
        txtComPercent = new javax.swing.JFormattedTextField();
        jLabel23 = new javax.swing.JLabel();
        txtComAmt = new javax.swing.JFormattedTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPur = new javax.swing.JTable();
        txtDefaultCom = new javax.swing.JFormattedTextField();
        txtQty = new javax.swing.JFormattedTextField();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        panelPur.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel17.setFont(Global.lableFont);
        jLabel17.setText("Vou No");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Supplier");

        txtCus.setFont(Global.textFont);
        txtCus.setName("txtCus"); // NOI18N
        txtCus.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCusFocusGained(evt);
            }
        });
        txtCus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCusActionPerformed(evt);
            }
        });

        txtVouNo.setEditable(false);
        txtVouNo.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtVouNo.setFont(Global.textFont);
        txtVouNo.setName("txtVouNo"); // NOI18N

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Pur Date");

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Credit Term");

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Currency");

        txtDueDate.setDateFormatString("dd/MM/yyyy");
        txtDueDate.setFont(Global.textFont);

        txtPurDate.setDateFormatString("dd/MM/yyyy");
        txtPurDate.setFont(Global.textFont);
        txtPurDate.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtPurDatePropertyChange(evt);
            }
        });

        txtCurrency.setFont(Global.textFont);
        txtCurrency.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCurrency.setName("txtCurrency"); // NOI18N
        txtCurrency.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCurrencyActionPerformed(evt);
            }
        });

        jLabel21.setFont(Global.lableFont);
        jLabel21.setText("Remark");

        txtRemark.setFont(Global.textFont);
        txtRemark.setName("txtRemark"); // NOI18N
        txtRemark.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtRemarkFocusGained(evt);
            }
        });

        jLabel22.setFont(Global.lableFont);
        jLabel22.setText("Location");

        txtLocation.setFont(Global.textFont);
        txtLocation.setName("txtLocation"); // NOI18N
        txtLocation.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtLocationFocusGained(evt);
            }
        });
        txtLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtLocationActionPerformed(evt);
            }
        });

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Reference");

        txtReference.setFont(Global.textFont);
        txtReference.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtReference.setName("txtCurrency"); // NOI18N
        txtReference.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtReferenceActionPerformed(evt);
            }
        });

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Batch No");

        txtBatchNo.setEditable(false);
        txtBatchNo.setFont(Global.textFont);
        txtBatchNo.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtBatchNo.setName("txtCurrency"); // NOI18N
        txtBatchNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBatchNoActionPerformed(evt);
            }
        });

        txtProjectNo.setFont(Global.textFont);
        txtProjectNo.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtProjectNo.setName("txtCurrency"); // NOI18N
        txtProjectNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtProjectNoActionPerformed(evt);
            }
        });

        jLabel12.setFont(Global.lableFont);
        jLabel12.setText("Project No");

        javax.swing.GroupLayout panelPurLayout = new javax.swing.GroupLayout(panelPur);
        panelPur.setLayout(panelPurLayout);
        panelPurLayout.setHorizontalGroup(
            panelPurLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPurLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPurLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelPurLayout.createSequentialGroup()
                        .addGroup(panelPurLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelPurLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPurDate, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
                            .addComponent(txtVouNo)))
                    .addGroup(panelPurLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtCus)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelPurLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelPurLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelPurLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtRemark, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
                    .addComponent(txtLocation)
                    .addComponent(txtCurrency))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPurLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelPurLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDueDate, javax.swing.GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE)
                    .addComponent(txtReference)
                    .addComponent(txtBatchNo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtProjectNo, javax.swing.GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)
                .addContainerGap())
        );

        panelPurLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel17, jLabel2, jLabel22, jLabel4, jLabel5, jLabel6});

        panelPurLayout.setVerticalGroup(
            panelPurLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPurLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPurLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelPurLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(panelPurLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17)
                            .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel22)
                            .addComponent(txtLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addGroup(panelPurLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtProjectNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12)))
                    .addComponent(txtDueDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPurLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPurDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelPurLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel9)
                        .addComponent(txtReference, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelPurLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPurLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(txtCus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelPurLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel10)
                        .addComponent(txtBatchNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelPurLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel21)
                        .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelPurLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel4, jLabel5});

        panelPurLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtCurrency, txtCus, txtDueDate, txtLocation, txtPurDate, txtRemark, txtVouNo});

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblStatus.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        lblStatus.setText("NEW");

        lblRec.setFont(Global.lableFont);
        lblRec.setText("Records");

        btnBatch.setFont(Global.lableFont);
        btnBatch.setText("Batch");
        btnBatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatchActionPerformed(evt);
            }
        });

        panelExpense.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        tblExpense.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tblExpense);

        txtExpense.setEditable(false);

        jLabel1.setText("Total : ");

        jButton2.setFont(Global.amtFont);
        jButton2.setText("+");
        jButton2.setAlignmentY(0.0F);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Expense");

        javax.swing.GroupLayout panelExpenseLayout = new javax.swing.GroupLayout(panelExpense);
        panelExpense.setLayout(panelExpenseLayout);
        panelExpenseLayout.setHorizontalGroup(
            panelExpenseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelExpenseLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelExpenseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(expProgress, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(panelExpenseLayout.createSequentialGroup()
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtExpense, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelExpenseLayout.setVerticalGroup(
            panelExpenseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelExpenseLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(expProgress, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelExpenseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtExpense, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jButton2))
                .addContainerGap())
        );

        javax.swing.GroupLayout panelStockInfoLayout = new javax.swing.GroupLayout(panelStockInfo);
        panelStockInfo.setLayout(panelStockInfoLayout);
        panelStockInfoLayout.setHorizontalGroup(
            panelStockInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelStockInfoLayout.setVerticalGroup(
            panelStockInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblRec, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(btnBatch))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelStockInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelExpense, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lblRec)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBatch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(panelExpense, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelStockInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel13.setFont(Global.lableFont);
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel13.setText("Gross Total :");

        jLabel14.setFont(Global.lableFont);
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel14.setText("Discount     :");

        jLabel16.setFont(Global.lableFont);
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel16.setText("Tax( + )       :");

        jLabel19.setFont(Global.lableFont);
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel19.setText("Paid :");

        txtVouTotal.setEditable(false);
        txtVouTotal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtVouTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVouTotal.setFont(Global.amtFont);

        jLabel7.setFont(Global.lableFont);
        jLabel7.setForeground(Global.selectionColor);
        jLabel7.setText("%");

        jLabel8.setFont(Global.lableFont);
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel8.setText("Vou Balance :");

        jLabel15.setFont(Global.lableFont);
        jLabel15.setForeground(Global.selectionColor);
        jLabel15.setText("%");

        txtVouDiscP.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtVouDiscP.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVouDiscP.setFont(Global.amtFont);
        txtVouDiscP.setName("txtVouDiscP"); // NOI18N
        txtVouDiscP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtVouDiscPActionPerformed(evt);
            }
        });

        txtVouDiscount.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtVouDiscount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVouDiscount.setFont(Global.amtFont);
        txtVouDiscount.setName("txtVouDiscount"); // NOI18N
        txtVouDiscount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtVouDiscountActionPerformed(evt);
            }
        });

        txtVouTaxP.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtVouTaxP.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVouTaxP.setFont(Global.amtFont);
        txtVouTaxP.setName("txtVouTaxP"); // NOI18N
        txtVouTaxP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtVouTaxPActionPerformed(evt);
            }
        });

        txtTax.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtTax.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTax.setFont(Global.amtFont);
        txtTax.setName("txtTax"); // NOI18N
        txtTax.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTaxActionPerformed(evt);
            }
        });

        txtVouPaid.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtVouPaid.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVouPaid.setFont(Global.amtFont);
        txtVouPaid.setName("txtVouPaid"); // NOI18N
        txtVouPaid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtVouPaidActionPerformed(evt);
            }
        });

        txtVouBalance.setEditable(false);
        txtVouBalance.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtVouBalance.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVouBalance.setFont(Global.amtFont);
        txtVouBalance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtVouBalanceActionPerformed(evt);
            }
        });

        jLabel20.setFont(Global.lableFont);
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel20.setText("Grand Total :");

        txtGrandTotal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtGrandTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtGrandTotal.setFont(Global.amtFont);
        txtGrandTotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtGrandTotalActionPerformed(evt);
            }
        });

        chkPaid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkPaidActionPerformed(evt);
            }
        });

        jLabel18.setFont(Global.lableFont);
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel18.setText("Comm         :");

        txtComPercent.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtComPercent.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtComPercent.setFont(Global.amtFont);
        txtComPercent.setName("txtComPercent"); // NOI18N
        txtComPercent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtComPercentActionPerformed(evt);
            }
        });

        jLabel23.setFont(Global.lableFont);
        jLabel23.setForeground(Global.selectionColor);
        jLabel23.setText("%");

        txtComAmt.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtComAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtComAmt.setFont(Global.amtFont);
        txtComAmt.setName("txtComAmt"); // NOI18N
        txtComAmt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtComAmtActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addComponent(jSeparator2)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                        .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(chkPaid)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtVouTotal)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtVouDiscP)
                                    .addComponent(txtVouTaxP)
                                    .addComponent(txtComPercent))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtVouDiscount)
                                    .addComponent(txtTax)
                                    .addComponent(txtComAmt)))
                            .addComponent(txtGrandTotal)
                            .addComponent(txtVouPaid, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtVouBalance))))
                .addContainerGap())
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel13, jLabel14, jLabel16, jLabel20, jLabel8});

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(txtVouTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(jLabel7)
                    .addComponent(txtVouDiscP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtVouDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(jLabel15)
                    .addComponent(txtVouTaxP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(jLabel23)
                    .addComponent(txtComPercent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtComAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtGrandTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtVouPaid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkPaid, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtVouBalance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tblPur.setFont(Global.textFont);
        tblPur.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblPur.setRowHeight(Global.tblRowHeight);
        tblPur.setShowHorizontalLines(true);
        tblPur.setShowVerticalLines(true);
        tblPur.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPurMouseClicked(evt);
            }
        });
        tblPur.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblPurKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblPur);

        txtDefaultCom.setName("txtDefaultCom"); // NOI18N

        txtQty.setFont(Global.lableFont);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(15, 15, 15)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtDefaultCom, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
                            .addComponent(txtQty))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(panelPur, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(panelPur, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtQty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDefaultCom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        observeMain();    }//GEN-LAST:event_formComponentShown

    private void txtCusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCusActionPerformed
        //getCustomer();
    }//GEN-LAST:event_txtCusActionPerformed

    private void txtCusFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCusFocusGained
        txtCus.selectAll();
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCusFocusGained

    private void txtRemarkFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRemarkFocusGained

        // TODO add your handling code here:
    }//GEN-LAST:event_txtRemarkFocusGained

    private void txtLocationFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtLocationFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtLocationFocusGained

    private void tblPurMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPurMouseClicked
        setStockInfo();        // TODO add your handling code here:
    }//GEN-LAST:event_tblPurMouseClicked

    private void tblPurKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblPurKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_tblPurKeyReleased

    private void txtCurrencyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCurrencyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCurrencyActionPerformed

    private void txtVouDiscPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtVouDiscPActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_txtVouDiscPActionPerformed

    private void txtVouTaxPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtVouTaxPActionPerformed
        // TODO add your handling code here:


    }//GEN-LAST:event_txtVouTaxPActionPerformed

    private void txtVouDiscountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtVouDiscountActionPerformed
        // TODO add your handling code here:


    }//GEN-LAST:event_txtVouDiscountActionPerformed

    private void txtTaxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTaxActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_txtTaxActionPerformed

    private void txtVouPaidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtVouPaidActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVouPaidActionPerformed

    private void txtGrandTotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtGrandTotalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtGrandTotalActionPerformed

    private void txtVouBalanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtVouBalanceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVouBalanceActionPerformed

    private void txtLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtLocationActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtLocationActionPerformed

    private void chkPaidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkPaidActionPerformed
        // TODO add your handling code here:
        txtVouPaid.setValue(0);
        calculateTotalAmount(false);
    }//GEN-LAST:event_chkPaidActionPerformed

    private void txtReferenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtReferenceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtReferenceActionPerformed

    private void btnBatchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatchActionPerformed
        // TODO add your handling code here:
        batchDialog();
    }//GEN-LAST:event_btnBatchActionPerformed

    private void txtBatchNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBatchNoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBatchNoActionPerformed

    private void txtComPercentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtComPercentActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtComPercentActionPerformed

    private void txtComAmtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtComAmtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtComAmtActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        expenseDialog();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void txtProjectNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProjectNoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtProjectNoActionPerformed

    private void txtPurDatePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtPurDatePropertyChange
        Trader t = traderAutoCompleter.getTrader();
        if (t != null) {
            calDueDate(Util1.getInteger(t.getCreditDays()));
        }
    }//GEN-LAST:event_txtPurDatePropertyChange
    private void tabToTable(KeyEvent e) {
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
            tblPur.requestFocus();
            if (tblPur.getRowCount() >= 0) {
                tblPur.setRowSelectionInterval(0, 0);
            }
        }
    }

    @Override
    public void keyEvent(KeyEvent e) {

    }

    @Override
    public void selected(Object source, Object selectObj) {
        switch (source.toString()) {
            case "TRADER" -> {
                try {
                    Trader cus = traderAutoCompleter.getTrader();
                    if (cus != null) {
                        calDueDate(Util1.getInteger(cus.getCreditDays()));
                    }
                } catch (Exception ex) {
                    log.error("selected CustomerList : " + selectObj + " - " + ex.getMessage());
                }
            }
            case "CAL-TOTAL" ->
                calculateTotalAmount(false);
            case "Location" ->
                setAllLocation();
            case "STOCK-INFO" -> {
                setStockInfo();
            }
            case "ORDER" -> {
            }
            case "Select" -> {
                calculateTotalAmount(false);
            }
            case "Batch" -> {
                if (selectObj instanceof GRN g) {
                    //get sale
                    setVoucherDetail(g);
                }
            }
            case "PUR-HISTORY" -> {
                if (selectObj instanceof VPurchase v) {
                    boolean local = v.isLocal();
                    inventoryRepo.findPurchase(v.getVouNo(), v.getDeptId(), local).subscribe((t) -> {
                        setVoucher(t, local);
                    }, (e) -> {
                        JOptionPane.showMessageDialog(this, e.getMessage());
                    });
                }
            }
        }
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
            case "txtVouNo" -> {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtRemark.requestFocus();
                }
                tabToTable(e);
            }
            case "txtCus" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtLocation.requestFocus();
                }
                tabToTable(e);
            }
            case "txtLocation" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtRemark.requestFocus();
                }
                tabToTable(e);
            }
            case "txtPurman" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    //  txtPurDate.getDateEditor().getUiComponent().requestFocusInWindow();
                    tblPur.requestFocus();
                }
                tabToTable(e);
            }
            case "txtVouStatus" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtCus.requestFocus();
                }
                tabToTable(e);
            }
            case "txtRemark" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    tblPur.setRowSelectionInterval(0, 0);
                    tblPur.setColumnSelectionInterval(0, 0);
                    tblPur.requestFocus();
                }
                tabToTable(e);
            }
            case "txtPurDate" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String date = ((JTextFieldDateEditor) sourceObj).getText();
                    txtPurDate.setDate(Util1.formatDate(date));
                    txtCus.requestFocus();
                }
                tabToTable(e);
            }
            case "txtDueDate" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String date = ((JTextFieldDateEditor) sourceObj).getText();
                    txtDueDate.setDate(Util1.formatDate(date));
                    txtReference.requestFocus();
                }
                tabToTable(e);
            }
            case "txtCurrency" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    tblPur.requestFocus();
                }
                tabToTable(e);
            }
            case "txtVouTaxP" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtTax.setValue(0);
                    calculateTotalAmount(false);
                    tblPur.requestFocus();
                }
            }
            case "txtTax" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtVouTaxP.setValue(0);
                    calculateTotalAmount(false);
                    tblPur.requestFocus();
                }
            }
            case "txtVouDiscount" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (Util1.getDouble(txtVouDiscount.getValue()) >= 0) {
                        txtVouDiscP.setValue(0);
                    }
                    calculateTotalAmount(false);
                    tblPur.requestFocus();
                }
            }
            case "txtVouDiscP" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (Util1.getDouble(txtVouDiscP.getValue()) <= 0) {
                        txtVouDiscount.setValue(0);
                    }
                    calculateTotalAmount(false);
                    tblPur.requestFocus();
                }
            }
            case "txtComPercent" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (Util1.getDouble(txtComPercent.getValue()) <= 0) {
                        txtComAmt.setValue(0);
                    }
                    calculateTotalAmount(false);
                    tblPur.requestFocus();
                }
            }
            case "txtComAmt" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (Util1.getDouble(txtComAmt.getValue()) >= 0) {
                        txtComPercent.setValue(0);
                    }
                    calculateTotalAmount(false);
                    tblPur.requestFocus();
                }
            }
            case "txtVouPaid" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    calculateTotalAmount(true);
                    tblPur.requestFocus();
                }
            }
            case "txtDefaultCom" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    calComission();
                }
            }
        }
    }

    private void calDueDate(Integer day) {
        Date vouDate = txtPurDate.getDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(vouDate);
        calendar.add(Calendar.DAY_OF_MONTH, day);
        Date dueDate = calendar.getTime();
        txtDueDate.setDate(dueDate);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBatch;
    private javax.swing.JCheckBox chkPaid;
    private javax.swing.JProgressBar expProgress;
    private javax.swing.JButton jButton2;
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
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblRec;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPanel panelExpense;
    private javax.swing.JPanel panelPur;
    private javax.swing.JPanel panelStockInfo;
    private javax.swing.JTable tblExpense;
    private javax.swing.JTable tblPur;
    private javax.swing.JTextField txtBatchNo;
    private javax.swing.JFormattedTextField txtComAmt;
    private javax.swing.JFormattedTextField txtComPercent;
    private javax.swing.JTextField txtCurrency;
    private javax.swing.JTextField txtCus;
    private javax.swing.JFormattedTextField txtDefaultCom;
    private com.toedter.calendar.JDateChooser txtDueDate;
    private javax.swing.JFormattedTextField txtExpense;
    private javax.swing.JFormattedTextField txtGrandTotal;
    private javax.swing.JTextField txtLocation;
    private javax.swing.JTextField txtProjectNo;
    private com.toedter.calendar.JDateChooser txtPurDate;
    private javax.swing.JFormattedTextField txtQty;
    private javax.swing.JTextField txtReference;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JFormattedTextField txtTax;
    private javax.swing.JFormattedTextField txtVouBalance;
    private javax.swing.JFormattedTextField txtVouDiscP;
    private javax.swing.JFormattedTextField txtVouDiscount;
    private javax.swing.JFormattedTextField txtVouNo;
    private javax.swing.JFormattedTextField txtVouPaid;
    private javax.swing.JFormattedTextField txtVouTaxP;
    private javax.swing.JFormattedTextField txtVouTotal;
    // End of variables declaration//GEN-END:variables

    @Override
    public void delete() {
        deletePur();
    }

    @Override
    public void print() {
        savePur(true);
    }

    @Override
    public void save() {
        savePur(false);
    }

    @Override
    public void newForm() {
        clear();
    }

    @Override
    public void history() {
        historyPur();
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

}
