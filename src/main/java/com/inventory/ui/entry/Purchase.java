/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry;

import com.acc.common.AccountRepo;
import com.common.DecimalFormatRender;
import com.common.Global;
import com.common.KeyPropagate;
import com.common.PanelControl;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.common.Util1;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.inventory.editor.CurrencyAutoCompleter;
import com.inventory.editor.ExpenseEditor;
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.editor.LocationCellEditor;
import com.inventory.editor.StockCellEditor;
import com.inventory.editor.TraderAutoCompleter;
import com.inventory.model.Expense;
import com.inventory.model.GRN;
import com.inventory.model.GRNDetail;
import com.inventory.model.Location;
import com.inventory.model.PurExpense;
import com.inventory.model.PurExpenseKey;
import com.inventory.model.PurHis;
import com.inventory.model.PurHisDetail;
import com.inventory.model.PurHisKey;
import com.inventory.model.SaleHisDetail;
import com.inventory.model.StockUnit;
import com.inventory.model.Trader;
import com.inventory.model.VPurchase;
import com.inventory.ui.common.GRNTableModel;
import com.inventory.ui.common.InventoryRepo;
import com.inventory.ui.common.PurExpenseTableModel;
import com.inventory.ui.common.PurchaseTableModel;
import com.inventory.ui.entry.dialog.BatchSearchDialog;
import com.inventory.ui.entry.dialog.PurchaseAvgPriceDialog;
import com.inventory.ui.entry.dialog.PurchaseHistoryDialog;
import com.inventory.ui.setup.dialog.ExpenseSetupDialog;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.inventory.ui.setup.dialog.common.StockUnitEditor;
import com.toedter.calendar.JTextFieldDateEditor;
import com.user.common.UserRepo;
import com.user.editor.ProjectAutoCompleter;
import com.user.model.Project;
import com.user.model.ProjectKey;
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
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
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
    private WebClient inventoryApi;
    @Autowired
    private InventoryRepo inventoryRepo;
    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private UserRepo userRepo;
    private CurrencyAutoCompleter currAutoCompleter;
    private TraderAutoCompleter traderAutoCompleter;
    private LocationAutoCompleter locationAutoCompleter;
    private ProjectAutoCompleter projectAutoCompleter;
    private SelectionObserver observer;
    private JProgressBar progress;
    private PurHis ph = new PurHis();
    private Mono<List<Location>> monoLoc;
    private Mono<List<StockUnit>> monoUnit;
    private List<StockUnit> listUnit;
    private final PurExpenseTableModel expenseTableModel = new PurExpenseTableModel();
    private final GRNTableModel grnTableModel = new GRNTableModel();
    
    public LocationAutoCompleter getLocationAutoCompleter() {
        return locationAutoCompleter;
    }
    
    public void setLocationAutoCompleter(LocationAutoCompleter locationAutoCompleter) {
        this.locationAutoCompleter = locationAutoCompleter;
    }
    
    public JProgressBar getProgress() {
        return progress;
    }
    
    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }
    
    public SelectionObserver getObserver() {
        return observer;
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
        initDateListner();
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
        initPanelGRN();
        assignDefaultValue();
        txtCus.requestFocus();
    }
    
    private void initDateListner() {
        txtPurDate.getDateEditor().getUiComponent().setName("txtPurDate");
        txtPurDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtPurDate.getDateEditor().getUiComponent().addFocusListener(fa);
        txtDueDate.getDateEditor().getUiComponent().setName("txtDueDate");
        txtDueDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtDueDate.getDateEditor().getUiComponent().addFocusListener(fa);
        
    }
    private final FocusAdapter fa = new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            ((JTextFieldDateEditor) e.getSource()).selectAll();
        }
    };
    
    private void initPanelExpesne() {
        boolean status = Util1.getBoolean(ProUtil.getProperty(ProUtil.P_SHOW_EXPENSE));
        if (status) {
            expenseTableModel.setObserver(this);
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
            tblExpense.getColumnModel().getColumn(1).setPreferredWidth(40);
            inventoryRepo.getExpense().subscribe((t) -> {
                tblExpense.getColumnModel().getColumn(0).setCellEditor(new ExpenseEditor(t));
            });
            tblExpense.getColumnModel().getColumn(1).setCellEditor(new AutoClearEditor());
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
                p.setAmount(0.0f);
                expenseTableModel.addObject(p);
            }
            expenseTableModel.addNewRow();
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
        purTableModel.setSelectionObserver(this);
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
        tblPur.setDefaultRenderer(Float.class, new DecimalFormatRender());
        tblPur.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblPur.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
    
    private void initPanelGRN() {
        boolean status = Util1.getBoolean(ProUtil.getProperty(ProUtil.P_SHOW_GRN));
        if (status) {
            grnTableModel.setEditable(false);
            tblGRN.setModel(grnTableModel);
            tblGRN.getTableHeader().setFont(Global.tblHeaderFont);
            tblGRN.setCellSelectionEnabled(true);
            tblGRN.setRowHeight(Global.tblRowHeight);
            tblGRN.setFont(Global.textFont);
            tblGRN.getColumnModel().getColumn(0).setPreferredWidth(30);//Code
            tblGRN.getColumnModel().getColumn(1).setPreferredWidth(200);//Name
            tblGRN.getColumnModel().getColumn(2).setPreferredWidth(30);//relation
            tblGRN.getColumnModel().getColumn(3).setPreferredWidth(30);//loc
            tblGRN.getColumnModel().getColumn(4).setPreferredWidth(20);//w
            tblGRN.getColumnModel().getColumn(5).setPreferredWidth(10);//wu
            tblGRN.getColumnModel().getColumn(6).setPreferredWidth(20);//qty
            tblGRN.getColumnModel().getColumn(7).setPreferredWidth(10);//unit 
            tblGRN.getColumnModel().getColumn(8).setPreferredWidth(10);//total   
            tblGRN.setDefaultRenderer(Object.class, new TableCellRender());
            tblGRN.setDefaultRenderer(Float.class, new TableCellRender());
            tblGRN.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                    .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
            tblGRN.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        } else {
            panelGRN.setVisible(false);
        }
    }
    
    private void initCombo() {
        traderAutoCompleter = new TraderAutoCompleter(txtCus, inventoryRepo, null, false, "SUP");
        traderAutoCompleter.setObserver(this);
        monoLoc = inventoryRepo.getLocation();
        userRepo.getCurrency().subscribe((t) -> {
            currAutoCompleter = new CurrencyAutoCompleter(txtCurrency, t, null, false);
            currAutoCompleter.setObserver(this);
            userRepo.getDefaultCurrency().subscribe((tt) -> {
                currAutoCompleter.setCurrency(tt);
            });
        });
        
        monoLoc.subscribe((t) -> {
            locationAutoCompleter = new LocationAutoCompleter(txtLocation, t, null, false, false);
            locationAutoCompleter.setObserver(this);
            inventoryRepo.getDefaultLocation().subscribe((tt) -> {
                locationAutoCompleter.setLocation(tt);
            });
        });
        
        projectAutoCompleter = new ProjectAutoCompleter(txtProjectNo, userRepo, null, false);
        projectAutoCompleter.setObserver(this);
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
        txtPurDate.setDate(Util1.getTodayDate());
        Mono<Trader> trader = inventoryRepo.findTrader(Global.hmRoleProperty.get("default.customer"), Global.deptId);
        trader.subscribe((t) -> {
            traderAutoCompleter.setTrader(t);
        });
        txtDueDate.setDate(null);
        txtCurrency.setEnabled(ProUtil.isMultiCur());
        txtVouNo.setText(null);
        txtRemark.setText(null);
        txtReference.setText(null);
        txtBatchNo.setText(null);
        txtComPercent.setValue(Util1.getFloat(ProUtil.getProperty("purchase.commission")));
        float commAmt = Util1.getFloat(ProUtil.getProperty(ProUtil.P_COM_AMT));
        txtDefaultCom.setValue(commAmt);
    }
    
    private void clear() {
        disableForm(true);
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
            progress.setIndeterminate(true);
            ph.setListPD(purTableModel.getListDetail());
            ph.setListDel(purTableModel.getDelList());
            ph.setListExpense(expenseTableModel.getListDetail());
            inventoryRepo.save(ph).subscribe((t) -> {
                clear();
                progress.setIndeterminate(false);
                if (print) {
                    printVoucher(t);
                }
            }, (e) -> {
                progress.setIndeterminate(false);
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
        } else if (Util1.getFloat(txtVouTotal.getValue()) <= 0) {
            JOptionPane.showMessageDialog(this, "Invalid Amount.",
                    "No Pur Record.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtLocation.requestFocus();
        } else if (Objects.isNull(traderAutoCompleter.getTrader())) {
            JOptionPane.showMessageDialog(this, "Choose Supplier.",
                    "Choose Supplier.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtCus.requestFocus();
        } else {
            ph.setRemark(txtRemark.getText());
            ph.setDiscP(Util1.getFloat(txtVouDiscP.getValue()));
            ph.setDiscount(Util1.getFloat(txtVouDiscount.getValue()));
            ph.setTaxP(Util1.getFloat(txtVouTaxP.getValue()));
            ph.setTaxAmt(Util1.getFloat(txtTax.getValue()));
            ph.setPaid(Util1.getFloat(txtVouPaid.getValue()));
            ph.setBalance(Util1.getFloat(txtVouBalance.getValue()));
            ph.setCurCode(currAutoCompleter.getCurrency().getCurCode());
            ph.setDeleted(Util1.getNullTo(ph.getDeleted()));
            ph.setLocCode(locationAutoCompleter.getLocation().getKey().getLocCode());
            Project p = projectAutoCompleter.getProject();
            ph.setProjectNo(p == null ? null : p.getKey().getProjectNo());
            ph.setVouDate(txtPurDate.getDate());
            ph.setTraderCode(traderAutoCompleter.getTrader().getKey().getCode());
            ph.setVouTotal(Util1.getFloat(txtVouTotal.getValue()));
            ph.setStatus(lblStatus.getText());
            ph.setReference(txtReference.getText());
            ph.setBatchNo(txtBatchNo.getText());
            ph.setCommP(Util1.getFloat(txtComPercent.getValue()));
            ph.setCommAmt(Util1.getFloat(txtComAmt.getValue()));
            ph.setExpense(Util1.getFloat(txtExpense.getValue()));
            if (lblStatus.getText().equals("NEW")) {
                PurHisKey key = new PurHisKey();
                key.setCompCode(Global.compCode);
                key.setDeptId(Global.deptId);
                key.setVouNo(null);
                ph.setKey(key);
                ph.setCreatedDate(Util1.getTodayDate());
                ph.setCreatedBy(Global.loginUser.getUserCode());
                ph.setSession(Global.sessionId);
                ph.setMacId(Global.macId);
            } else {
                ph.setUpdatedBy(Global.loginUser.getUserCode());
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
        for (PurExpense p : list) {
            ttlExp += Util1.getFloat(p.getAmount());
        }
        txtExpense.setValue(ttlExp);
    }
    
    private void calQty(List<PurHisDetail> list) {
        float ttlQty = 0.0f;
        for (PurHisDetail p : list) {
            ttlQty += Util1.getFloat(p.getQty());
        }
        txtQty.setValue(ttlQty);
    }
    
    private void calculateTotalAmount(boolean partial) {
        calExpense();
        List<PurHisDetail> list = purTableModel.getListDetail();
        calQty(list);
        calComission();
        float totalVouBalance;
        float totalAmount = Util1.getFloat(list.stream().mapToDouble(sd -> Util1.getFloat(sd.getAmount())).sum());
        txtVouTotal.setValue(totalAmount);
        //cal discAmt
        float discp = Util1.getFloat(txtVouDiscP.getValue());
        if (discp > 0) {
            float discountAmt = (totalAmount * (discp / 100));
            txtVouDiscount.setValue(Util1.getFloat(discountAmt));
        }
        //cal Commission
        float comp = Util1.getFloat(txtComPercent.getValue());
        if (comp > 0) {
            float amt = (totalAmount * (comp / 100));
            txtComAmt.setValue(amt);
        }

        //calculate taxAmt
        float taxp = Util1.getFloat(txtVouTaxP.getValue());
        float taxAmt = Util1.getFloat(txtTax.getValue());
        if (taxp > 0) {
            float afterDiscountAmt = totalAmount - Util1.getFloat(txtVouDiscount.getValue());
            float totalTax = (afterDiscountAmt * taxp) / 100;
            txtTax.setValue(Util1.getFloat(totalTax));
        } else if (taxAmt > 0) {
            float afterDiscountAmt = totalAmount - Util1.getFloat(txtVouDiscount.getValue());
            taxp = (taxAmt / afterDiscountAmt) * 100;
            txtVouTaxP.setValue(Util1.getFloat(taxp));
        }
        float ttlExp = Util1.getFloat(txtExpense.getValue());
        txtGrandTotal.setValue(totalAmount
                + Util1.getFloat(txtTax.getValue())
                - Util1.getFloat(txtVouDiscount.getValue())
                - ttlExp);
        float grandTotal = Util1.getFloat(txtGrandTotal.getValue());
        
        float paid = Util1.getFloat(txtVouPaid.getText());
        if (!partial) {
            if (paid == 0 || paid != grandTotal) {
                if (chkPaid.isSelected()) {
                    txtVouPaid.setValue(grandTotal);
                } else {
                    txtVouPaid.setValue(0);
                }
            }
        }
        
        paid = Util1.getFloat(txtVouPaid.getText());
        totalVouBalance = grandTotal - paid;
        txtVouBalance.setValue(Util1.getFloat(totalVouBalance));
    }
    
    public void historyPur() {
        if (dialog == null) {
            dialog = new PurchaseHistoryDialog(Global.parentForm);
            dialog.setInventoryApi(inventoryApi);
            dialog.setInventoryRepo(inventoryRepo);
            dialog.setUserRepo(userRepo);
            dialog.setIconImage(searchIcon);
            dialog.setObserver(this);
            dialog.initMain();
            dialog.setSize(Global.width - 100, Global.height - 100);
            dialog.setLocationRelativeTo(null);
        }
        dialog.search();
        dialog.setVisible(true);
    }
    
    public void setVoucher(PurHis pur) {
        if (pur != null) {
            try {
                purTableModel.clear();
                expenseTableModel.clear();
                progress.setIndeterminate(true);
                ph = pur;
                Integer deptId = ph.getKey().getDeptId();
                userRepo.findCurrency(ph.getCurCode()).subscribe((t) -> {
                    currAutoCompleter.setCurrency(t);
                }, (e) -> {
                    JOptionPane.showMessageDialog(this, e.getMessage());
                });
                inventoryRepo.findLocation(ph.getLocCode(), deptId).subscribe((t) -> {
                    locationAutoCompleter.setLocation(t);
                }, (e) -> {
                    JOptionPane.showMessageDialog(this, e.getMessage());
                });
                Mono<Trader> trader = inventoryRepo.findTrader(ph.getTraderCode(), ph.getKey().getDeptId());
                trader.subscribe((t) -> {
                    traderAutoCompleter.setTrader(t);
                }, (e) -> {
                    JOptionPane.showMessageDialog(this, e.getMessage());
                });
                if (pur.getProjectNo() != null) {
                    userRepo.find(new ProjectKey(pur.getProjectNo(), Global.compCode)).subscribe(t -> {
                        projectAutoCompleter.setProject(t);
                    }, (e) -> {
                        JOptionPane.showMessageDialog(this, e.getMessage());
                    });
                } else {
                    projectAutoCompleter.setProject(null);
                }
                
                String vouNo = ph.getKey().getVouNo();
                inventoryApi.get()
                        .uri(builder -> builder.path("/pur/get-pur-detail")
                        .queryParam("vouNo", vouNo)
                        .queryParam("compCode", Global.compCode)
                        .queryParam("deptId", ph.getKey().getDeptId())
                        .build())
                        .retrieve()
                        .bodyToFlux(PurHisDetail.class)
                        .collectList()
                        .subscribe((t) -> {
                            purTableModel.setListDetail(t);
                            purTableModel.addNewRow();
                            if (ph.isVouLock()) {
                                lblStatus.setText("Voucher is locked.");
                                lblStatus.setForeground(Color.RED);
                                disableForm(false);
                            } else if (!ProUtil.isPurchaseEdit()) {
                                lblStatus.setText("No Permission.");
                                lblStatus.setForeground(Color.RED);
                                disableForm(false);
                            } else if (Util1.getBoolean(ph.getDeleted())) {
                                lblStatus.setText("DELETED");
                                lblStatus.setForeground(Color.RED);
                                disableForm(false);
                            } else {
                                lblStatus.setText("EDIT");
                                lblStatus.setForeground(Color.blue);
                                disableForm(true);
                            }
                            txtVouNo.setText(ph.getKey().getVouNo());
                            txtDueDate.setDate(ph.getDueDate());
                            txtRemark.setText(ph.getRemark());
                            txtPurDate.setDate(ph.getVouDate());
                            txtVouTotal.setValue(Util1.getFloat(ph.getVouTotal()));
                            txtVouDiscP.setValue(Util1.getFloat(ph.getDiscP()));
                            txtVouDiscount.setValue(Util1.getFloat(ph.getDiscount()));
                            txtVouTaxP.setValue(Util1.getFloat(ph.getTaxP()));
                            txtTax.setValue(Util1.getFloat(ph.getTaxAmt()));
                            txtVouPaid.setValue(Util1.getFloat(ph.getPaid()));
                            txtVouBalance.setValue(Util1.getFloat(ph.getBalance()));
                            txtGrandTotal.setValue(Util1.getFloat(txtGrandTotal.getValue()));
                            chkPaid.setSelected(Util1.getFloat(ph.getPaid()) > 0);
                            txtReference.setText(ph.getReference());
                            txtBatchNo.setText(ph.getBatchNo());
                            txtComPercent.setValue(Util1.getFloat(ph.getCommP()));
                            txtComAmt.setValue(Util1.getFloat(ph.getCommAmt()));
                            txtExpense.setValue(Util1.getFloat(ph.getExpense()));
                            calQty(t);
                            focusTable();
                            progress.setIndeterminate(false);
                        }, (e) -> {
                            JOptionPane.showMessageDialog(this, e.getMessage());
                            progress.setIndeterminate(false);
                        }, () -> {
                        });
                searchExpense(vouNo);
                searchGRN(ph.getBatchNo(), deptId);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            
        }
    }
    
    private void searchExpense(String vouNo) {
        expProgress.setIndeterminate(true);
        inventoryApi.get()
                .uri(builder -> builder.path("/purExpense/get-pur-expense")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(PurExpense.class)
                .collectList()
                .subscribe((t) -> {
                    expenseTableModel.setListDetail(t);
                    expenseTableModel.addNewRow();
                    expProgress.setIndeterminate(false);
                }, (e) -> {
                    JOptionPane.showMessageDialog(this, e.getMessage());
                    expProgress.setIndeterminate(false);
                });
    }
    
    private void searchGRN(String batchNo, Integer deptId) {
        boolean status = Util1.getBoolean(ProUtil.getProperty(ProUtil.P_SHOW_GRN));
        if (status) {
            grnTableModel.clear();
            if (!batchNo.isEmpty()) {
                grnProgress.setIndeterminate(true);
                inventoryApi.get()
                        .uri(builder -> builder.path("/grn/get-grn-detail-batch")
                        .queryParam("batchNo", batchNo)
                        .queryParam("compCode", Global.compCode)
                        .queryParam("deptId", deptId)
                        .build())
                        .retrieve()
                        .bodyToFlux(GRNDetail.class)
                        .collectList()
                        .subscribe((t) -> {
                            grnTableModel.setListDetail(t);
                            grnProgress.setIndeterminate(false);
                        }, (e) -> {
                            grnProgress.setIndeterminate(false);
                            JOptionPane.showMessageDialog(this, e.getMessage());
                        });
            }
        }
    }
    
    private void disableForm(boolean status) {
        tblPur.setEnabled(status);
        panelPur.setEnabled(status);
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
        Flux<VPurchase> fr = inventoryApi.get()
                .uri(builder -> builder.path("/report/get-purchase-report")
                .queryParam("vouNo", vouNo)
                .queryParam("batchNo", p.getBatchNo())
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(VPurchase.class);
        Flux<PurExpense> sr = inventoryApi.get()
                .uri(builder -> builder.path("/purExpense/get-pur-expense")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().bodyToFlux(PurExpense.class);
        Mono<List<VPurchase>> p1 = fr.collectList();
        Mono<List<PurExpense>> p2 = sr.collectList();
        p1.zipWith(p2).hasElement().subscribe((t) -> {
            log.info("" + t);
        });
        p1.zipWith(p2).subscribe((t) -> {
            List<VPurchase> list = t.getT1();
            List<PurExpense> listEx = t.getT2();
            listEx.removeIf((ex) -> Util1.getFloat(ex.getAmount()) == 0);
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
                        param.put("p_exp", Util1.getFloat(p.getExpense()) * -1);
                        param.put("p_vou_paid", p.getPaid());
                        param.put("p_vou_balance", p.getBalance());
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
        BatchSearchDialog d = new BatchSearchDialog(Global.parentForm);
        d.setInventoryRepo(inventoryRepo);
        d.setObserver(this);
        d.initMain();
        d.setLocationRelativeTo(null);
        d.setVisible(true);
    }
    
    private void expenseDialog() {
        ExpenseSetupDialog d = new ExpenseSetupDialog(Global.parentForm);
        d.setInventoryRepo(inventoryRepo);
        d.setAccountRepo(accountRepo);
        d.initMain();
        d.setLocationRelativeTo(null);
        d.setVisible(true);
    }
    
    private void setVoucherDetail(GRN g) {
        purTableModel.clear();
        String batchNo = g.getBatchNo();
        Integer deptId = g.getKey().getDeptId();
        boolean detail = Util1.getBoolean(ProUtil.getProperty(ProUtil.P_BATCH_DETAIL));
        Flux<SaleHisDetail> result = inventoryRepo.getSaleByBatch(batchNo, detail);
        result.subscribe((sd) -> {
            PurHisDetail pd = new PurHisDetail();
            pd.setStockCode(sd.getStockCode());
            pd.setUserCode(sd.getUserCode());
            pd.setStockName(sd.getStockName());
            pd.setRelName(sd.getRelName());
            pd.setAvgQty(0.0f);
            pd.setQty(sd.getQty());
            pd.setUnitCode(sd.getUnitCode());
            pd.setPrice(sd.getPrice());
            pd.setAmount(sd.getAmount());
            pd.setLocCode(sd.getLocCode());
            pd.setLocName(sd.getLocName());
            purTableModel.addPurchase(pd);
        }, (e) -> {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }, () -> {
            purTableModel.addNewRow();
            calculateTotalAmount(false);
            Mono<Trader> trader = inventoryRepo.findTrader(g.getTraderCode(), g.getKey().getDeptId());
            trader.subscribe((t) -> {
                traderAutoCompleter.setTrader(t);
            });
            txtBatchNo.setText(batchNo);
            txtBatchNo.setEditable(false);
            searchGRN(batchNo, deptId);
        });
    }
    
    private void calComission() {
        float qty = Util1.getFloat(txtQty.getText());
        float price = Util1.getFloat(txtDefaultCom.getValue());
        txtComAmt.setValue(qty * price);
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
        jButton1 = new javax.swing.JButton();
        panelExpense = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblExpense = new javax.swing.JTable();
        txtExpense = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        expProgress = new javax.swing.JProgressBar();
        jLabel11 = new javax.swing.JLabel();
        panelGRN = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblGRN = new javax.swing.JTable();
        grnProgress = new javax.swing.JProgressBar();
        jLabel3 = new javax.swing.JLabel();
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
        txtCus.setNextFocusableComponent(txtLocation);
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
                            .addComponent(txtPurDate, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
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
                    .addComponent(txtRemark, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                    .addComponent(txtLocation)
                    .addComponent(txtCurrency))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPurLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelPurLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDueDate, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                    .addComponent(txtReference)
                    .addComponent(txtBatchNo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtProjectNo, javax.swing.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
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

        jButton1.setFont(Global.lableFont);
        jButton1.setText("Batch");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

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

        tblGRN.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(tblGRN);

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("GRN");

        javax.swing.GroupLayout panelGRNLayout = new javax.swing.GroupLayout(panelGRN);
        panelGRN.setLayout(panelGRNLayout);
        panelGRNLayout.setHorizontalGroup(
            panelGRNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGRNLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelGRNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(grnProgress, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelGRNLayout.setVerticalGroup(
            panelGRNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelGRNLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(grnProgress, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
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
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelGRN, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(panelExpense, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelGRN, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
        observer.selected("control", this);
    }//GEN-LAST:event_formComponentShown

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
        // TODO add your handling code here:
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

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        batchDialog();
    }//GEN-LAST:event_jButton1ActionPerformed

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
            case "CustomerList" -> {
                try {
                    Trader cus = (Trader) selectObj;
                    if (cus != null) {
                        txtCus.setText(cus.getTraderName());
                    }
                } catch (Exception ex) {
                    log.error("selected CustomerList : " + selectObj + " - " + ex.getMessage());
                }
            }
            case "CAL-TOTAL" ->
                calculateTotalAmount(false);
            case "Location" ->
                setAllLocation();
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
                    inventoryRepo.findPurchase(v.getVouNo(), v.getDeptId()).subscribe((t) -> {
                        setVoucher(t);
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
                    if (date.length() == 8 || date.length() == 6) {
                        txtPurDate.setDate(Util1.formatDate(date));
                    }
                    txtCus.requestFocus();
                }
                tabToTable(e);
            }
            case "txtDueDate" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String date = ((JTextFieldDateEditor) sourceObj).getText();
                    if (date.length() == 8 || date.length() == 6) {
                        txtDueDate.setDate(Util1.formatDate(date));
                    }
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
                    if (Util1.getFloat(txtVouDiscount.getValue()) >= 0) {
                        txtVouDiscP.setValue(0);
                    }
                    calculateTotalAmount(false);
                    tblPur.requestFocus();
                }
            }
            case "txtVouDiscP" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (Util1.getFloat(txtVouDiscP.getValue()) <= 0) {
                        txtVouDiscount.setValue(0);
                    }
                    calculateTotalAmount(false);
                    tblPur.requestFocus();
                }
            }
            case "txtComPercent" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (Util1.getFloat(txtComPercent.getValue()) <= 0) {
                        txtComAmt.setValue(0);
                    }
                    calculateTotalAmount(false);
                    tblPur.requestFocus();
                }
            }
            case "txtComAmt" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (Util1.getFloat(txtComAmt.getValue()) >= 0) {
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkPaid;
    private javax.swing.JProgressBar expProgress;
    private javax.swing.JProgressBar grnProgress;
    private javax.swing.JButton jButton1;
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
    private javax.swing.JLabel jLabel3;
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
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblRec;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPanel panelExpense;
    private javax.swing.JPanel panelGRN;
    private javax.swing.JPanel panelPur;
    private javax.swing.JTable tblExpense;
    private javax.swing.JTable tblGRN;
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
