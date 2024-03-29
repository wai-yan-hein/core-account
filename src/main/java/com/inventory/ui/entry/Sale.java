/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry;

import com.CloudIntegration;
import com.H2Repo;
import com.acc.common.COAComboBoxModel;
import com.acc.dialog.FindDialog;
import com.acc.model.ChartOfAccount;
import com.common.ComponentUtil;
import com.common.DateLockUtil;
import com.repo.AccountRepo;
import com.common.DecimalFormatRender;
import com.common.Global;
import com.common.JasperReportUtil;
import com.common.KeyPropagate;
import com.common.PanelControl;
import com.common.ProUtil;
import com.common.RowHeader;
import com.common.SelectionObserver;
import com.common.Util1;
import com.common.YNOptionPane;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.editor.BatchAutoCompeter;
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.editor.LocationCellEditor;
import com.inventory.editor.SaleManAutoCompleter;
import com.inventory.editor.SalePriceCellEditor;
import com.inventory.editor.StockCellEditor;
import com.inventory.editor.TraderAutoCompleter;
import com.inventory.entity.GRN;
import com.inventory.entity.Location;
import com.inventory.entity.OrderHis;
import com.inventory.entity.SaleExpense;
import com.inventory.entity.SaleHis;
import com.inventory.entity.SaleHisDetail;
import com.inventory.entity.SaleHisKey;
import com.inventory.entity.SaleMan;
import com.inventory.entity.Trader;
import com.inventory.entity.TransferHis;
import com.inventory.entity.VOrder;
import com.inventory.entity.VSale;
import com.inventory.entity.VTransfer;
import com.repo.InventoryRepo;
import com.inventory.ui.common.SaleTableModel;
import com.inventory.ui.common.StockInfoPanel;
import com.inventory.ui.entry.dialog.BatchSearchDialog;
import com.inventory.ui.entry.dialog.GRNDetailDialog;
import com.inventory.ui.entry.dialog.OrderDetailDialog;
import com.inventory.ui.entry.dialog.OrderHistoryDialog;
import com.inventory.ui.entry.dialog.SaleHistoryDialog;
import com.inventory.ui.entry.dialog.TransferHistoryDialog;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.inventory.editor.StockUnitEditor;
import com.inventory.ui.entry.dialog.AccountOptionDialog;
import com.inventory.ui.entry.dialog.SaleExpenseFrame;
import com.inventory.ui.entry.dialog.SaleWeightLossPriceDialog;
import com.inventory.ui.entry.dialog.StockBalanceFrame;
import com.toedter.calendar.JTextFieldDateEditor;
import com.repo.UserRepo;
import com.user.editor.CurrencyAutoCompleter;
import com.user.editor.ProjectAutoCompleter;
import com.user.model.Project;
import com.user.model.ProjectKey;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JList;
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
import org.springframework.core.task.TaskExecutor;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 *
 * @author wai yan
 */
@Slf4j
public class Sale extends javax.swing.JPanel implements SelectionObserver, KeyListener, KeyPropagate, PanelControl {
    
    private List<SaleHisDetail> listDetail = new ArrayList();
    private final SaleTableModel saleTableModel = new SaleTableModel();
    
    private SaleHistoryDialog dialog;
    private InventoryRepo inventoryRepo;
    private H2Repo h2Repo;
    private CloudIntegration integration;
    private AccountRepo accountRepo;
    private UserRepo userRepo;
    private TaskExecutor taskExecutor;
    private StockBalanceFrame stockBalanceDialog;
    private OrderHistoryDialog orderDialog;
    private TransferHistoryDialog transferHistoryDialog;
    private CurrencyAutoCompleter currAutoCompleter;
    private TraderAutoCompleter traderAutoCompleter;
    private SaleManAutoCompleter saleManCompleter;
    private LocationAutoCompleter locationAutoCompleter;
    private ProjectAutoCompleter projectAutoCompleter;
    private BatchAutoCompeter batchAutoCompeter;
    private SelectionObserver observer;
    private SaleHis saleHis = new SaleHis();
    private JProgressBar progress;
    private Mono<List<Location>> monoLoc;
    private double prvBal = 0;
    private double balance = 0;
    private BatchSearchDialog batchDialog;
    private GRNDetailDialog grnDialog;
    private OrderDetailDialog orderDetailDialog;
    private COAComboBoxModel coaComboModel = new COAComboBoxModel();
    private final StockInfoPanel stockInfoPanel = new StockInfoPanel();
    private SaleExpenseFrame saleExpenseFrame;
    private SaleWeightLossPriceDialog saleWeightLossPriceDialog;
    private AccountOptionDialog optionDialog;
    private FindDialog findDialog;
    
    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }
    
    public void setH2Repo(H2Repo h2Repo) {
        this.h2Repo = h2Repo;
    }
    
    public void setIntegration(CloudIntegration integration) {
        this.integration = integration;
    }
    
    public void setAccountRepo(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }
    
    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }
    
    public void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }
    
    public TraderAutoCompleter getTraderAutoCompleter() {
        return traderAutoCompleter;
    }
    
    public LocationAutoCompleter getLocationAutoCompleter() {
        return locationAutoCompleter;
    }
    
    public JProgressBar getProgress() {
        return progress;
    }
    
    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }
    
    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }
    
    public void setStockBalanceDialog(StockBalanceFrame stockBalanceDialog) {
        this.stockBalanceDialog = stockBalanceDialog;
    }

    /**
     * Creates new form SaleEntry1
     */
    public Sale() {
        initComponents();
        initButtonGroup();
        lblStatus.setForeground(Color.GREEN);
        initKeyListener();
        initTextBoxFormat();
        initTextBoxValue();
        initDateListner();
        actionMapping();
    }
    
    private void actionMapping() {
        String solve = "delete";
        KeyStroke delete = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        tblSale.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(delete, solve);
        tblSale.getActionMap().put(solve, new DeleteSale());
        tblSale.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK), "loss-price");
        tblSale.getActionMap().put("loss-price", new LossPriceAction());
        
    }
    
    private class LossPriceAction extends AbstractAction {
        
        @Override
        public void actionPerformed(ActionEvent e) {
            int row = tblSale.convertRowIndexToModel(tblSale.getSelectedRow());
            SaleHisDetail pd = saleTableModel.getSale(row);
            if (pd.getStockCode() != null) {
                if (saleWeightLossPriceDialog == null) {
                    saleWeightLossPriceDialog = new SaleWeightLossPriceDialog(Global.parentForm);
                    saleWeightLossPriceDialog.setLocationRelativeTo(null);
                    saleWeightLossPriceDialog.setInventoryRepo(inventoryRepo);
                    saleWeightLossPriceDialog.addKeyListener(new KeyAdapter() {
                        @Override
                        public void keyPressed(KeyEvent e) {
                            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                                saleWeightLossPriceDialog.dispose();
                            }
                        }
                    });
                }
                saleWeightLossPriceDialog.setSaleDetail(pd);
                saleWeightLossPriceDialog.setVisible(true);
                if (saleWeightLossPriceDialog.isConfirm()) {
                    saleTableModel.setValueAt(pd, row, 0);
                }
                
            }
        }
    }
    
    private void setSaleVoucherDetail(OrderHis oh) {
        clear(false);
        if (oh != null) {
            progress.setIndeterminate(true);
            getSaleDetailFromOrder(oh.getDeptId()).doOnSuccess((t) -> {
                saleTableModel.setListDetail(t);
            }).doOnTerminate(() -> {
                saleTableModel.addNewRow();
                calculateTotalAmount(false);
                focusTable();
                progress.setIndeterminate(false);
            }).subscribe();
            btnOrder.setBackground(Color.green);
            inventoryRepo.findLocation(oh.getLocCode()).doOnSuccess((t) -> {
                locationAutoCompleter.setLocation(t);
            }).subscribe();
            inventoryRepo.findTrader(oh.getTraderCode()).doOnSuccess((t) -> {
                traderAutoCompleter.setTrader(t);
            }).subscribe();
            userRepo.findCurrency(oh.getCurCode()).doOnSuccess((t) -> {
                currAutoCompleter.setCurrency(t);
            }).subscribe();
            inventoryRepo.findSaleMan(oh.getSaleManCode()).doOnSuccess((t) -> {
                saleManCompleter.setSaleMan(t);
            }).subscribe();
            userRepo.find(new ProjectKey(oh.getProjectNo(), Global.compCode)).doOnSuccess(t1 -> {
                projectAutoCompleter.setProject(t1);
            }).subscribe();
            txtDueDate.setDate(Util1.convertToDate(oh.getCreditTerm()));
            txtRemark.setText(oh.getRemark());
            txtReference.setText(oh.getReference());
            txtSaleDate.setDate(Util1.convertToDate(oh.getVouDate()));
            txtVouTotal.setValue(Util1.getDouble(oh.getVouTotal()));
        }
    }
    
    private Mono<List<SaleHisDetail>> getSaleDetailFromOrder(int deptId) {
        return Flux.fromIterable(orderDialog.getListOrder())
                .flatMap(vouNo -> inventoryRepo.getOrderDetail(vouNo, deptId)
                .flatMapMany(Flux::fromIterable)
                .map(od -> {
                    SaleHisDetail sd = new SaleHisDetail();
                    sd.setStockCode(od.getStockCode());
                    sd.setUserCode(od.getUserCode());
                    sd.setStockName(od.getStockName());
                    sd.setTraderName(od.getTraderName());
                    sd.setRelName(od.getRelName());
                    sd.setWeight(od.getWeight());
                    sd.setWeightUnit(od.getWeightUnit());
                    sd.setQty(Util1.getDouble(od.getQty()));
                    sd.setAmount(Util1.getDouble(od.getAmount()));
                    sd.setUnitCode(od.getUnitCode());
                    sd.setPrice(Util1.getDouble(od.getPrice()));
                    sd.setLocCode(od.getLocCode());
                    sd.setLocName(od.getLocName());
                    return sd;
                })).collectList();
    }
    
    private class DeleteSale extends AbstractAction {
        
        @Override
        public void actionPerformed(ActionEvent e) {
            deleteTranSale();
        }
    }
    
    private void initDateListner() {
        txtSaleDate.getDateEditor().getUiComponent().setName("txtSaleDate");
        txtSaleDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtSaleDate.getDateEditor().getUiComponent().addFocusListener(fa);
        txtDueDate.getDateEditor().getUiComponent().setName("txtDueDate");
        txtDueDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtDueDate.getDateEditor().getUiComponent().addFocusListener(fa);
        txtCurrency.addFocusListener(fa);
        txtCus.addFocusListener(fa);
        txtLocation.addFocusListener(fa);
        txtSaleman.addFocusListener(fa);
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
    
    private void initButtonGroup() {
        ButtonGroup g = new ButtonGroup();
        g.add(chkVou);
        g.add(chkA4);
        g.add(chkA5);
    }
    
    public void initMain() {
        initStockBalance();
        initCombo();
        initSaleTable();
        initRowHeader();
        initPanelExpense();
        assignDefaultValue();
        initFind();
        txtSaleDate.setDate(Util1.getTodayDate());
        txtCus.requestFocus();
    }
    
    private void initFind() {
        findDialog = new FindDialog(Global.parentForm, tblSale);
    }
    
    private void initRowHeader() {
        RowHeader header = new RowHeader();
        JList list = header.createRowHeader(tblSale, 30);
        scroll.setRowHeaderView(list);
    }
    
    private void initStockBalance() {
        stockBalanceDialog.setVisible(ProUtil.isCalStock());
        deskPane.add(stockBalanceDialog);
    }
    
    private void setStockInfo() {
        int row = tblSale.convertRowIndexToModel(tblSale.getSelectedRow());
        if (row >= 0) {
            SaleHisDetail shd = saleTableModel.getSale(row);
            stockInfoPanel.setStock(shd.getStockCode());
        }
    }
    
    private void initSaleTable() {
        tblSale.setModel(saleTableModel);
        saleTableModel.setLblRecord(lblRec);
        saleTableModel.setParent(tblSale);
        saleTableModel.setSale(this);
        saleTableModel.addNewRow();
        saleTableModel.setObserver(this);
        saleTableModel.setVouDate(txtSaleDate);
        saleTableModel.setInventoryRepo(inventoryRepo);
        saleTableModel.setDialog(stockBalanceDialog);
        tblSale.getTableHeader().setFont(Global.tblHeaderFont);
        tblSale.setCellSelectionEnabled(true);
        tblSale.getColumnModel().getColumn(0).setPreferredWidth(50);//Code
        tblSale.getColumnModel().getColumn(1).setPreferredWidth(450);//Name
        tblSale.getColumnModel().getColumn(2).setPreferredWidth(60);//Rel
        tblSale.getColumnModel().getColumn(3).setPreferredWidth(60);//Location
        tblSale.getColumnModel().getColumn(4).setPreferredWidth(60);//qty
        tblSale.getColumnModel().getColumn(5).setPreferredWidth(1);//unit
        tblSale.getColumnModel().getColumn(6).setPreferredWidth(1);//price
        tblSale.getColumnModel().getColumn(7).setPreferredWidth(40);//amt
        tblSale.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor(inventoryRepo));
        tblSale.getColumnModel().getColumn(1).setCellEditor(new StockCellEditor(inventoryRepo));
        monoLoc.subscribe((t) -> {
            tblSale.getColumnModel().getColumn(3).setCellEditor(new LocationCellEditor(t));
        });
        tblSale.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());//qty
        inventoryRepo.getStockUnit().doOnSuccess((t) -> {
            tblSale.getColumnModel().getColumn(5).setCellEditor(new StockUnitEditor(t));
        }).subscribe();
        tblSale.getColumnModel().getColumn(6).setCellEditor(new AutoClearEditor());//
        if (ProUtil.isSalePriceChange()) {
            if (ProUtil.isPriceOption()) {
                tblSale.getColumnModel().getColumn(6).setCellEditor(new SalePriceCellEditor(inventoryRepo));//price
            } else {
                tblSale.getColumnModel().getColumn(6).setCellEditor(new AutoClearEditor());//price
            }
            
        } else {
            tblSale.getColumnModel().getColumn(6).setCellEditor(new AutoClearEditor());//price
        }
        tblSale.setDefaultRenderer(Object.class, new DecimalFormatRender(2));
        tblSale.setDefaultRenderer(Double.class, new DecimalFormatRender(2));
        tblSale.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblSale.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
    
    private void initCombo() {
        traderAutoCompleter = new TraderAutoCompleter(txtCus, inventoryRepo, null, false, "CUS");
        traderAutoCompleter.setObserver(this);
        locationAutoCompleter = new LocationAutoCompleter(txtLocation, null, false, false);
        locationAutoCompleter.setObserver(this);
        monoLoc = inventoryRepo.getLocation();
        monoLoc.subscribe((t) -> {
            locationAutoCompleter.setListLocation(t);
        }, (e) -> {
            log.error(e.getMessage());
        });
        currAutoCompleter = new CurrencyAutoCompleter(txtCurrency, null);
        userRepo.getCurrency().subscribe((t) -> {
            currAutoCompleter.setListCurrency(t);
        });
        userRepo.getDefaultCurrency().subscribe((c) -> {
            currAutoCompleter.setCurrency(c);
        });
        saleManCompleter = new SaleManAutoCompleter(txtSaleman, null, false);
        inventoryRepo.getSaleMan().subscribe((t) -> {
            saleManCompleter.setListSaleMan(t);
        });
        projectAutoCompleter = new ProjectAutoCompleter(txtProjectNo, userRepo, null, false);
        projectAutoCompleter.setObserver(this);
        batchAutoCompeter = new BatchAutoCompeter(txtBatchNo, inventoryRepo, null, false);
        batchAutoCompeter.setObserver(this);
        userRepo.getDefaultCurrency().doOnSuccess((t) -> {
            currAutoCompleter.setCurrency(t);
        }).subscribe();
        inventoryRepo.getDefaultLocation().doOnSuccess((tt) -> {
            locationAutoCompleter.setLocation(tt);
        }).subscribe();
        inventoryRepo.getDefaultCustomer().doOnSuccess((t) -> {
            traderAutoCompleter.setTrader(t);
        }).subscribe();
        inventoryRepo.getDefaultSaleMan().doOnSuccess((tt) -> {
            saleManCompleter.setSaleMan(tt);
        }).subscribe();
    }
    
    private void initKeyListener() {
        txtSaleDate.getDateEditor().getUiComponent().setName("txtSaleDate");
        txtSaleDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtDueDate.getDateEditor().getUiComponent().setName("txtDueDate");
        txtDueDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtVouNo.addKeyListener(this);
        txtRemark.addKeyListener(this);
        txtCus.addKeyListener(this);
        txtLocation.addKeyListener(this);
        txtSaleman.addKeyListener(this);
        txtCurrency.addKeyListener(this);
        tblSale.addKeyListener(this);
        txtVouDiscP.addKeyListener(this);
        txtVouDiscount.addKeyListener(this);
        txtTax.addKeyListener(this);
        txtVouTaxP.addKeyListener(this);
        txtVouPaid.addKeyListener(this);
        txtProjectNo.addKeyListener(this);
    }
    
    private void initTextBoxValue() {
        txtVouTotal.setValue(0);
        txtVouDiscount.setValue(0);
        txtTax.setValue(0);
        txtVouPaid.setValue(0);
        txtVouBalance.setValue(0);
        txtVouTaxP.setValue(0);
        txtVouDiscP.setValue(0);
        txtGrandTotal.setValue(0);
    }
    
    private void initTextBoxFormat() {
        ComponentUtil.addFocusListener(this);
        ComponentUtil.setTextProperty(this);
    }
    
    private void assignDefaultValue() {
        coaComboModel.setSelectedItem(null);
        batchAutoCompeter.setBatch(null);
        btnBatch.setText("Batch");
        btnOrder.setText("Order");
        btnOrder.setBackground(Global.selectionColor);
        txtDueDate.setDate(null);
        progress.setIndeterminate(false);
        txtCurrency.setEnabled(ProUtil.isMultiCur());
        txtVouNo.setText(null);
        chkPaid.setSelected(ProUtil.isSalePaid());
        chkVou.setSelected(true);
        chkA4.setSelected(Util1.getBoolean(ProUtil.getProperty("check.sale.A4")));
        chkA5.setSelected(Util1.getBoolean(ProUtil.getProperty("check.sale.A5")));
        if (!lblStatus.getText().equals("NEW")) {
            txtSaleDate.setDate(Util1.getTodayDate());
        }
    }
    
    private void clear(boolean foucs) {
        disableForm(true);
        saleTableModel.removeListDetail();
        saleTableModel.clearDelList();
        saleTableModel.setChange(false);
        saleExpenseFrame.clear();
        stockBalanceDialog.clear();
        initTextBoxValue();
        assignDefaultValue();
        saleHis = new SaleHis();
        lblStatus.setText("NEW");
        lblStatus.setForeground(Color.GREEN);
        progress.setIndeterminate(false);
        txtRemark.setText(null);
        txtReference.setText(null);
        projectAutoCompleter.setProject(null);
        if (foucs) {
            txtCus.requestFocus();
        }
    }
    
    private void orderDialog() {
        String status = btnOrder.getText();
        if (status.equals("Order")) {
            if (orderDialog == null) {
                orderDialog = new OrderHistoryDialog(Global.parentForm);
                orderDialog.setInventoryRepo(inventoryRepo);
                orderDialog.setUserRepo(userRepo);
                orderDialog.setObserver(this);
                orderDialog.setOption(true);
                orderDialog.initMain();
                orderDialog.setSize(Global.width - 20, Global.height - 20);
                orderDialog.setLocationRelativeTo(null);
            }
            orderDialog.search();
            orderDialog.setVisible(true);
        } else if (status.contains("View")) {
            if (orderDetailDialog == null) {
                orderDetailDialog = new OrderDetailDialog(Global.parentForm);
                orderDetailDialog.setSize(Global.width - 30, Global.height / 3);
                orderDetailDialog.setInventoryRepo(inventoryRepo);
                orderDetailDialog.initMain();
                orderDetailDialog.setLocationRelativeTo(null);
            }
        }
    }
    
    private List<String> getOrderList() {
        return orderDialog == null ? null : orderDialog.getListOrder();
    }
    
    public void saveSale(boolean print) {
        if (isValidEntry() && saleTableModel.isValidEntry()) {
            if (DateLockUtil.isLockDate(txtSaleDate.getDate())) {
                DateLockUtil.showMessage(this);
                txtSaleDate.requestFocus();
                return;
            }
            if (!Util1.isNullOrEmpty(saleHis.getKey().getVouNo())) {
                YNOptionPane optionPane = new YNOptionPane("Are you sure to edit?", JOptionPane.WARNING_MESSAGE);
                JDialog d = optionPane.createDialog("Edit Confirmation");
                d.setVisible(true);
                int yn = (int) optionPane.getValue();
                if (yn != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            saleHis.setListSH(saleTableModel.getListDetail());
            saleHis.setListDel(saleTableModel.getDelList());
            saleHis.setBackup(saleTableModel.isChange());
            saleHis.setListOrder(getOrderList());
            observer.selected("save", false);
            progress.setIndeterminate(true);
            inventoryRepo.save(saleHis).doOnSuccess((t) -> {
                if (print) {
                    printVoucher(t);
                } else {
                    clear(true);
                }
            }).doOnError((e) -> {
                progress.setIndeterminate(false);
                observeMain();
                if (e instanceof WebClientRequestException) {
                    int yn = JOptionPane.showConfirmDialog(this, "Internet Offline. Try Again?", "Offline", JOptionPane.YES_OPTION, JOptionPane.ERROR_MESSAGE);
                    if (yn == JOptionPane.YES_OPTION) {
                        saveSale(print);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Error : " + e.getMessage(), "Server Error", JOptionPane.ERROR_MESSAGE);
                }
            }).subscribe();
        }
    }
    
    private boolean isValidEntry() {
        if (lblStatus.getText().equals("DELETED")) {
            clear(true);
            return false;
        } else if (currAutoCompleter.getCurrency() == null) {
            JOptionPane.showMessageDialog(this, "Choose Currency.",
                    "No Currency.", JOptionPane.ERROR_MESSAGE);
            txtCurrency.requestFocus();
            return false;
        } else if (locationAutoCompleter.getLocation() == null) {
            JOptionPane.showMessageDialog(this, "Choose Location.",
                    "No Location.", JOptionPane.ERROR_MESSAGE);
            txtLocation.requestFocus();
            return false;
        } else if (traderAutoCompleter.getTrader() == null) {
            JOptionPane.showMessageDialog(this, "Choose Trader.",
                    "No Trader.", JOptionPane.ERROR_MESSAGE);
            txtCus.requestFocus();
            return false;
        } else if (Util1.getDouble(txtVouTotal.getValue()) <= 0) {
            JOptionPane.showMessageDialog(this, "Invalid Amount.",
                    "No Sale Record.", JOptionPane.ERROR_MESSAGE);
            txtVouTotal.requestFocus();
            return false;
        } else if (!Util1.isDateBetween(txtSaleDate.getDate())) {
            JOptionPane.showMessageDialog(this, "Invalid Date.",
                    "Validation.", JOptionPane.ERROR_MESSAGE);
            txtSaleDate.requestFocus();
            return false;
        } else {
            saleHis.setCreditTerm(Util1.convertToLocalDateTime(txtDueDate.getDate()));
            SaleMan sm = saleManCompleter.getSaleMan();
            if (sm != null) {
                saleHis.setSaleManCode(sm.getKey().getSaleManCode());
            }
            String traderCode = traderAutoCompleter.getTrader().getKey().getCode();
            saleHis.setRemark(txtRemark.getText());
            saleHis.setReference(txtReference.getText());
            saleHis.setDiscP(Util1.getDouble(txtVouDiscP.getValue()));
            saleHis.setDiscount(Util1.getDouble(txtVouDiscount.getValue()));
            saleHis.setTaxPercent(Util1.getDouble(txtVouTaxP.getValue()));
            saleHis.setTaxAmt(Util1.getDouble(txtTax.getValue()));
            saleHis.setPaid(Util1.getDouble(txtVouPaid.getValue()));
            saleHis.setBalance(Util1.getDouble(txtVouBalance.getValue()));
            saleHis.setCurCode(currAutoCompleter.getCurrency().getCurCode());
            saleHis.setLocCode(locationAutoCompleter.getLocation().getKey().getLocCode());
            saleHis.setVouTotal(Util1.getDouble(txtVouTotal.getValue()));
            saleHis.setGrandTotal(Util1.getDouble(txtGrandTotal.getValue()));
            saleHis.setStatus(lblStatus.getText());
            saleHis.setVouDate(Util1.convertToLocalDateTime(txtSaleDate.getDate()));
            saleHis.setMacId(Global.macId);
            saleHis.setTraderCode(traderCode);
            if (saleExpenseFrame != null) {
                saleHis.setListExpense(saleExpenseFrame.getListDetail());
                saleHis.setListDelExpense(saleExpenseFrame.getDeleteList());
                saleHis.setExpense(Util1.getDouble(saleExpenseFrame.getExpense()));
            }
            Project p = projectAutoCompleter.getProject();
            saleHis.setProjectNo(p == null ? null : p.getKey().getProjectNo());
            GRN g = batchAutoCompeter.getBatch();
            saleHis.setGrnVouNo(g == null ? null : g.getKey().getVouNo());
            if (coaComboModel.getSelectedItem() instanceof ChartOfAccount coa) {
                saleHis.setSaleAcc(coa.getKey().getCoaCode());
            }
            if (lblStatus.getText().equals("NEW")) {
                SaleHisKey key = new SaleHisKey();
                key.setCompCode(Global.compCode);
                key.setVouNo(null);
                saleHis.setKey(key);
                saleHis.setDeptId(Global.deptId);
                saleHis.setCreatedDate(LocalDateTime.now());
                saleHis.setCreatedBy(Global.loginUser.getUserCode());
                saleHis.setSession(Global.sessionId);
            } else {
                saleHis.setUpdatedBy(Global.loginUser.getUserCode());
            }
        }
        return true;
    }
    
    private void deleteSale() {
        String status = lblStatus.getText();
        switch (status) {
            case "EDIT" -> {
                int yes_no = JOptionPane.showConfirmDialog(this,
                        "Are you sure to delete?", "Sale Voucher Delete.", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                if (yes_no == 0) {
                    inventoryRepo.delete(saleHis).doOnSuccess((t) -> {
                        if (t) {
                            clear(true);
                        }
                    }).subscribe();
                }
            }
            case "DELETED" -> {
                int yes_no = JOptionPane.showConfirmDialog(this,
                        "Are you sure to restore?", "Sale Voucher Restore.", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (yes_no == 0) {
                    saleHis.setDeleted(false);
                    inventoryRepo.restore(saleHis).doOnSuccess((t) -> {
                        lblStatus.setText("EDIT");
                        lblStatus.setForeground(Color.blue);
                        disableForm(true);
                    }).subscribe();
                }
            }
            default ->
                JOptionPane.showMessageDialog(this, "Voucher can't delete.");
        }
    }
    
    private void deleteTranSale() {
        int row = tblSale.convertRowIndexToModel(tblSale.getSelectedRow());
        if (row >= 0) {
            if (tblSale.getCellEditor() != null) {
                tblSale.getCellEditor().stopCellEditing();
            }
            int yes_no = JOptionPane.showConfirmDialog(this,
                    "Are you sure to delete?", "Sale Transaction delete.", JOptionPane.YES_NO_OPTION);
            if (yes_no == 0) {
                saleTableModel.delete(row);
                calculateTotalAmount(false);
            }
        }
    }
    
    private void calculateTotalAmount(boolean partial) {
        double totalVouBalance;
        double totalAmount = 0.0f;
        listDetail = saleTableModel.getListDetail();
        totalAmount = listDetail.stream().map(sdh -> Util1.getDouble(sdh.getAmount())).reduce(totalAmount, (accumulator, _item) -> accumulator + _item);
        txtVouTotal.setValue(totalAmount);
        //cal discAmt
        double discp = Util1.getDouble(txtVouDiscP.getValue());
        if (discp > 0) {
            double discountAmt = (totalAmount * (discp / 100));
            txtVouDiscount.setValue(Util1.getDouble(discountAmt));
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
        //
        double ttlExp = saleExpenseFrame == null ? 0 : saleExpenseFrame.getExpense();
        txtGrandTotal.setValue(totalAmount
                + Util1.getDouble(txtTax.getValue())
                - Util1.getDouble(txtVouDiscount.getValue())
                - ttlExp);
        double grandTotal = Util1.getDouble(txtGrandTotal.getValue());
        double paid = Util1.getDouble(txtVouPaid.getText());
        if (!partial) {
            if (paid == 0 || paid != grandTotal) {
                if (chkPaid.isSelected()) {
                    txtVouPaid.setValue(grandTotal);
                } else {
                    txtVouPaid.setValue(0);
                }
            }
        }
        paid = Util1.getDouble(txtVouPaid.getText());
        if (paid > grandTotal) {
            txtVouPaid.setValue(grandTotal);
            paid = grandTotal;
        }
        totalVouBalance = grandTotal - paid;
        txtVouBalance.setValue(Util1.getDouble(totalVouBalance));
    }
    
    public void historySale() {
        if (dialog == null) {
            dialog = new SaleHistoryDialog(Global.parentForm, 1);
            dialog.setInventoryRepo(inventoryRepo);
            dialog.setIntegration(integration);
            dialog.setUserRepo(userRepo);
            dialog.setTaskExecutor(taskExecutor);
            dialog.setObserver(this);
            dialog.initMain();
            dialog.setSize(Global.width - 20, Global.height - 20);
            dialog.setLocationRelativeTo(null);
        }
        dialog.search();
    }
    
    public void setSaleVoucher(SaleHis sh) {
        if (sh != null) {
            progress.setIndeterminate(true);
            saleHis = sh;
            inventoryRepo.findLocation(saleHis.getLocCode()).doOnSuccess((t) -> {
                locationAutoCompleter.setLocation(t);
            }).subscribe();
            inventoryRepo.findTrader(saleHis.getTraderCode()).doOnSuccess((t) -> {
                traderAutoCompleter.setTrader(t);
            }).subscribe();
            userRepo.findCurrency(saleHis.getCurCode()).doOnSuccess((t) -> {
                currAutoCompleter.setCurrency(t);
            }).subscribe();
            inventoryRepo.findSaleMan(saleHis.getSaleManCode()).doOnSuccess((t) -> {
                saleManCompleter.setSaleMan(t);
            }).subscribe();
            inventoryRepo.findGRN(sh.getGrnVouNo()).doOnSuccess((t) -> {
                batchAutoCompeter.setBatch(t);
                btnBatch.setText(t == null ? "Batch" : "Batch:View");
            }).subscribe();
            userRepo.find(new ProjectKey(saleHis.getProjectNo(), Global.compCode)).doOnSuccess(t -> {
                projectAutoCompleter.setProject(t);
            }).subscribe();
            if (saleHis.isVouLock()) {
                lblStatus.setText("Voucher is locked.");
                lblStatus.setForeground(Color.RED);
                disableForm(false);
            } else if (!ProUtil.isSaleEdit()) {
                lblStatus.setText("No Permission.");
                lblStatus.setForeground(Color.RED);
                disableForm(false);
                observer.selected("print", true);
            } else if (saleHis.isDeleted()) {
                lblStatus.setText("DELETED");
                lblStatus.setForeground(Color.RED);
                disableForm(false);
                observer.selected("delete", true);
            } else if (DateLockUtil.isLockDate(saleHis.getVouDate())) {
                lblStatus.setText(DateLockUtil.MESSAGE);
                lblStatus.setForeground(Color.RED);
                disableForm(false);
            } else {
                lblStatus.setText("EDIT");
                lblStatus.setForeground(Color.blue);
                disableForm(true);
            }
            txtVouNo.setText(saleHis.getKey().getVouNo());
            txtDueDate.setDate(Util1.convertToDate(saleHis.getCreditTerm()));
            txtRemark.setText(saleHis.getRemark());
            txtReference.setText(saleHis.getReference());
            txtSaleDate.setDate(Util1.convertToDate(saleHis.getVouDate()));
            txtVouTotal.setValue(Util1.getDouble(saleHis.getVouTotal()));
            txtVouDiscP.setValue(Util1.getDouble(saleHis.getDiscP()));
            txtVouDiscount.setValue(Util1.getDouble(saleHis.getDiscount()));
            txtVouTaxP.setValue(Util1.getDouble(saleHis.getTaxPercent()));
            txtTax.setValue(Util1.getDouble(saleHis.getTaxAmt()));
            txtVouPaid.setValue(Util1.getDouble(saleHis.getPaid()));
            txtVouBalance.setValue(Util1.getDouble(saleHis.getBalance()));
            txtGrandTotal.setValue(Util1.getDouble(saleHis.getGrandTotal()));
            chkPaid.setSelected(saleHis.getPaid() > 0);
            String vouNo = sh.getKey().getVouNo();
            checkOrderNo(vouNo);
            inventoryRepo.getSaleDetail(vouNo, sh.getDeptId(), saleHis.isLocal()).doOnSuccess((t) -> {
                saleTableModel.setListDetail(t);
                saleTableModel.addNewRow();
                focusTable();
                progress.setIndeterminate(false);
            }).subscribe();
            saleExpenseFrame.searchExpense(vouNo);
            saleExpenseFrame.setExpense(saleHis.getExpense());
        }
    }
    
    private void checkOrderNo(String vouNo) {
        if (!Util1.isNullOrEmpty(vouNo)) {
            inventoryRepo.getSaleOrder(vouNo).doOnSuccess((t) -> {
                if (t != null && !t.isEmpty()) {
                    btnOrder.setText("Order:View");
                }
            }).subscribe();
        }
    }
    
    private void disableForm(boolean status) {
        ComponentUtil.enableForm(this, status);
        observer.selected("save", status);
        observer.selected("delete", status);
        observer.selected("print", status);
    }
    
    private void setAllLocation() {
        List<SaleHisDetail> listSaleDetail = saleTableModel.getListDetail();
        Location loc = locationAutoCompleter.getLocation();
        if (listSaleDetail != null) {
            listSaleDetail.forEach(sd -> {
                sd.setLocCode(loc.getKey().getLocCode());
                sd.setLocName(loc.getLocName());
            });
        }
        saleTableModel.setListDetail(listSaleDetail);
    }
    
    private void printVoucher(SaleHis sh) {
        String reportName = getReportName();
        String vouNo = sh.getKey().getVouNo();
        String grnVouNo = sh.getGrnVouNo();
        boolean local = sh.isLocal();
        if (local) {
            List<VSale> list = h2Repo.getSaleReport(vouNo);
            if (!list.isEmpty()) {
                viewReport(list, sh, reportName);
            }
        } else {
            if (!Util1.isNullOrEmpty(grnVouNo)) {
                Mono<List<SaleExpense>> m1 = Mono.just(saleExpenseFrame.getListDetail());
                Mono<List<VSale>> m2 = inventoryRepo.getSaleByBatchReport(vouNo, grnVouNo);
                Mono<Trader> m3 = inventoryRepo.findTrader(sh.getTraderCode());
                Mono.zip(m1, m2, m3).doOnSuccess((tuple) -> {
                    try {
                        String reportPath = ProUtil.getReportPath() + "SaleByGRNReport.jasper";
                        Map<String, Object> param = getDefaultParam(sh);
                        JsonNode jn1 = new ObjectMapper().readTree(Util1.gson.toJson(tuple.getT1()));
                        JsonDataSource d1 = new JsonDataSource(jn1, null) {
                        };
                        JsonNode jn2 = new ObjectMapper().readTree(Util1.gson.toJson(tuple.getT2()));
                        JsonDataSource d2 = new JsonDataSource(jn2, null) {
                        };
                        param.put("p_expense", Util1.getDouble(sh.getExpense()) * -1);
                        param.put("p_trader_name", tuple.getT3().getTraderName());
                        param.put("p_sub_data", d1);
                        JasperPrint main = JasperFillManager.fillReport(reportPath, param, d2);
                        JasperViewer.viewReport(main, false);
                    } catch (JsonProcessingException | JRException e) {
                        log.error("printVoucher : " + e.getMessage());
                    }
                }).doOnTerminate(() -> {
                    clear(false);
                }).subscribe();
            } else {
                inventoryRepo.getSaleReport(vouNo).doOnSuccess((t) -> {
                    viewReport(t, sh, reportName);
                }).doOnError((e) -> {
                    JOptionPane.showMessageDialog(this, e.getMessage());
                }).doOnTerminate(() -> {
                    clear(false);
                }).subscribe();
            }
        }
    }
    
    private Map<String, Object> getDefaultParam(SaleHis sh) throws JsonProcessingException, JRException {
        Map<String, Object> param = new HashMap<>();
        param.put("p_print_date", Util1.getTodayDateTime());
        param.put("p_comp_name", Global.companyName);
        param.put("p_comp_address", Global.companyAddress);
        param.put("p_comp_phone", Global.companyPhone);
        param.put("p_logo_path", ProUtil.logoPath());
        param.put("p_balance", balance);
        param.put("p_prv_balance", prvBal);
        param.put("p_sub_report_dir", "report/");
        param.put("p_vou_total", sh.getVouTotal());
        param.put("p_vou_paid", sh.getPaid());
        param.put("p_vou_balance", sh.getBalance());
        param.put("p_expense", sh.getExpense());
        param.put("p_vou_no", sh.getKey().getVouNo());
        param.put("p_remark", sh.getRemark());
        param.put("p_vou_date", Util1.toDateStr(sh.getVouDate(), Global.dateFormat));
        List<SaleExpense> listExp = saleExpenseFrame.getListDetail();
        JsonNode jn = new ObjectMapper().readTree(Util1.gson.toJson(listExp));
        JsonDataSource d = new JsonDataSource(jn, null) {
        };
        param.put("p_sub_exp_data", d);
        return param;
    }
    
    private void viewReport(List<VSale> list, SaleHis sh, String reportName) {
        if (reportName != null) {
            try {
                String reportPath = ProUtil.getReportPath() + reportName.concat(".jasper");
                ByteArrayInputStream stream = new ByteArrayInputStream(Util1.listToByteArray(list));
                JsonDataSource ds = new JsonDataSource(stream);
                Map<String, Object> hm = getDefaultParam(sh);
                JasperPrint jp = JasperFillManager.fillReport(reportPath, hm, ds);
                if (chkVou.isSelected()) {
                    String printerName = ProUtil.getProperty("printer.name");
                    int count = Util1.getIntegerOne(ProUtil.getProperty("printer.pages"));
                    JasperReportUtil.print(jp, printerName, count, 4);
                } else {
                    JasperViewer.viewReport(jp, false);
                }
            } catch (JRException | JsonProcessingException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Select Report Type");
            chkVou.requestFocus();
        }
    }
    
    private String getReportName() {
        String name = null;
        if (chkVou.isSelected()) {
            name = ProUtil.getProperty("report.sale.voucher");
        }
        if (chkA4.isSelected()) {
            name = ProUtil.getProperty("report.sale.A4");
        }
        if (chkA5.isSelected()) {
            name = ProUtil.getProperty("report.sale.A5");
        }
        return name;
    }
    
    private void focusTable() {
        int rc = tblSale.getRowCount();
        if (rc >= 1) {
            tblSale.setRowSelectionInterval(rc - 1, rc - 1);
            tblSale.setColumnSelectionInterval(0, 0);
            tblSale.requestFocus();
        } else {
            txtCus.requestFocus();
        }
    }
    
    public void addTrader(Trader t) {
        traderAutoCompleter.addTrader(t);
    }
    
    public void setTrader(Trader t, int row) {
        traderAutoCompleter.setTrader(t, row);
    }
    
    private void calDueDate(Integer day) {
        Date vouDate = txtSaleDate.getDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(vouDate);
        calendar.add(Calendar.DAY_OF_MONTH, day);
        Date dueDate = calendar.getTime();
        txtDueDate.setDate(dueDate);
    }
    
    private void observeMain() {
        observer.selected("control", this);
        observer.selected("save", true);
        observer.selected("print", true);
        observer.selected("history", true);
        observer.selected("delete", true);
        observer.selected("refresh", false);
    }
    
    private void initPanelExpense() {
        saleExpenseFrame = new SaleExpenseFrame();
        saleExpenseFrame.setInventoryRepo(inventoryRepo);
        saleExpenseFrame.setAccountRepo(accountRepo);
        saleExpenseFrame.setObserver(this);
        saleExpenseFrame.initMain();
        deskPane.add(saleExpenseFrame);
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
        } else if (text.contains("View")) {
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
    
    private void trasnferDialog() {
        if (transferHistoryDialog == null) {
            transferHistoryDialog = new TransferHistoryDialog(Global.parentForm);
            transferHistoryDialog.setInventoryRepo(inventoryRepo);
            transferHistoryDialog.setUserRepo(userRepo);
            transferHistoryDialog.setObserver(this);
            transferHistoryDialog.initMain();
            transferHistoryDialog.setSize(Global.width - 20, Global.height - 20);
            transferHistoryDialog.setLocationRelativeTo(null);
        }
        transferHistoryDialog.search();
    }
    
    private void setTransferVoucher(TransferHis s, boolean local) {
        progress.setIndeterminate(true);
        saleTableModel.clear();
        Integer deptId = s.getDeptId();
        inventoryRepo.findTrader(s.getTraderCode()).doOnSuccess((t) -> {
            traderAutoCompleter.setTrader(t);
        }).subscribe();
        String vouNo = s.getKey().getVouNo();
        inventoryRepo.getTransferDetail(vouNo, deptId, local).subscribe((t) -> {
            t.forEach((od) -> {
                SaleHisDetail sd = new SaleHisDetail();
                sd.setStockCode(od.getStockCode());
                sd.setUserCode(od.getUserCode());
                sd.setStockName(od.getStockName());
                sd.setRelName(od.getRelName());
                sd.setQty(Util1.getDouble(od.getQty()));
                sd.setUnitCode(od.getUnitCode());
                sd.setWeight(od.getWeight());
                sd.setWeightUnit(od.getWeightUnit());
                saleTableModel.addSale(sd);
            });
            inventoryRepo.findLocation(s.getLocCodeTo()).doOnSuccess((l) -> {
                locationAutoCompleter.setLocation(l);
                setAllLocation();
            }).subscribe();
            saleTableModel.addNewRow();
            progress.setIndeterminate(false);
        }, (e) -> {
            progress.setIndeterminate(false);
            JOptionPane.showMessageDialog(this, e.getMessage());
        });
        
    }
    
    private void optionDialog() {
        Trader trader = traderAutoCompleter.getTrader();
        if (trader != null) {
            if (optionDialog == null) {
                optionDialog = new AccountOptionDialog(Global.parentForm);
                optionDialog.setLocationRelativeTo(null);
                optionDialog.setAccountRepo(accountRepo);
                optionDialog.initMain();
            }
            optionDialog.setObject(saleHis, trader);
            optionDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Select Trader.");
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

        panelSale = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtCus = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtSaleman = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtDueDate = new com.toedter.calendar.JDateChooser();
        txtSaleDate = new com.toedter.calendar.JDateChooser();
        txtCurrency = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        txtLocation = new javax.swing.JTextField();
        txtReference = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtProjectNo = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtBatchNo = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        txtVouNo = new javax.swing.JTextField();
        panelInfo = new javax.swing.JPanel();
        lblStatus = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        chkA5 = new javax.swing.JCheckBox();
        chkVou = new javax.swing.JCheckBox();
        chkA4 = new javax.swing.JCheckBox();
        lblRec = new javax.swing.JLabel();
        btnOrder = new javax.swing.JButton();
        btnBatch = new javax.swing.JButton();
        btnBatch1 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
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
        scroll = new javax.swing.JScrollPane();
        tblSale = new javax.swing.JTable();
        deskPane = new javax.swing.JDesktopPane();
        jButton1 = new javax.swing.JButton();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                formPropertyChange(evt);
            }
        });

        panelSale.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel17.setFont(Global.lableFont);
        jLabel17.setText("Vou No");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Customer");

        txtCus.setFont(Global.textFont);
        txtCus.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCus.setName("txtCus"); // NOI18N
        txtCus.setNextFocusableComponent(txtLocation);
        txtCus.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCusFocusGained(evt);
            }
        });
        txtCus.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                txtCusMouseExited(evt);
            }
        });
        txtCus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCusActionPerformed(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Sale Man");

        txtSaleman.setFont(Global.textFont);
        txtSaleman.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtSaleman.setName("txtSaleman"); // NOI18N
        txtSaleman.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSalemanFocusGained(evt);
            }
        });
        txtSaleman.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSalemanActionPerformed(evt);
            }
        });

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Sale Date");

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Credit Term");

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Currency");

        txtDueDate.setDateFormatString("dd/MM/yyyy");
        txtDueDate.setFont(Global.textFont);

        txtSaleDate.setDateFormatString("dd/MM/yyyy");
        txtSaleDate.setFont(Global.textFont);
        txtSaleDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSaleDateFocusGained(evt);
            }
        });
        txtSaleDate.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtSaleDatePropertyChange(evt);
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
        txtRemark.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtRemark.setName("txtRemark"); // NOI18N
        txtRemark.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtRemarkFocusGained(evt);
            }
        });

        jLabel22.setFont(Global.lableFont);
        jLabel22.setText("Location");

        txtLocation.setFont(Global.textFont);
        txtLocation.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtLocation.setName("txtLocation"); // NOI18N
        txtLocation.setNextFocusableComponent(txtSaleman);
        txtLocation.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtLocationFocusGained(evt);
            }
        });

        txtReference.setFont(Global.textFont);
        txtReference.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtReference.setName("txtRemark"); // NOI18N
        txtReference.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtReferenceFocusGained(evt);
            }
        });

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Reference");

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Project No");

        txtProjectNo.setFont(Global.textFont);
        txtProjectNo.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtProjectNo.setName("txtCurrency"); // NOI18N
        txtProjectNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtProjectNoActionPerformed(evt);
            }
        });

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Batch No");

        txtBatchNo.setEditable(false);
        txtBatchNo.setFont(Global.textFont);
        txtBatchNo.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtBatchNo.setEnabled(false);
        txtBatchNo.setName("txtCurrency"); // NOI18N
        txtBatchNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBatchNoActionPerformed(evt);
            }
        });

        jButton2.setBackground(Global.selectionColor);
        jButton2.setFont(Global.lableFont);
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Account");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        txtVouNo.setEditable(false);
        txtVouNo.setFont(Global.textFont);
        txtVouNo.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtVouNo.setName("txtCus"); // NOI18N
        txtVouNo.setNextFocusableComponent(txtLocation);
        txtVouNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtVouNoFocusGained(evt);
            }
        });
        txtVouNo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                txtVouNoMouseExited(evt);
            }
        });
        txtVouNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtVouNoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelSaleLayout = new javax.swing.GroupLayout(panelSale);
        panelSale.setLayout(panelSaleLayout);
        panelSaleLayout.setHorizontalGroup(
            panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSaleLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCus)
                    .addComponent(txtSaleDate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
                    .addComponent(txtVouNo, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtRemark, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
                    .addComponent(txtSaleman)
                    .addComponent(txtLocation))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtDueDate, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
                    .addComponent(txtCurrency)
                    .addComponent(txtReference))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelSaleLayout.createSequentialGroup()
                        .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtProjectNo, javax.swing.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)
                            .addComponent(txtBatchNo)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelSaleLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton2)))
                .addContainerGap())
        );

        panelSaleLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel17, jLabel2, jLabel21, jLabel22, jLabel3, jLabel4, jLabel5, jLabel6, jLabel9});

        panelSaleLayout.setVerticalGroup(
            panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSaleLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelSaleLayout.createSequentialGroup()
                        .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17)
                            .addComponent(jLabel22)
                            .addComponent(txtLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6)
                            .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10)
                            .addComponent(txtProjectNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtDueDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtSaleDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel3)
                                .addComponent(txtSaleman, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel5))
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel11)
                        .addComponent(txtBatchNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtCus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel21)
                        .addComponent(jLabel2))
                    .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtReference, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9)
                        .addComponent(jButton2)))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        panelSaleLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel3, jLabel5});

        panelInfo.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblStatus.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        lblStatus.setText("NEW");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Report Type", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, Global.lableFont));

        chkA5.setFont(Global.textFont);
        chkA5.setText("A5");
        chkA5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkA5ActionPerformed(evt);
            }
        });

        chkVou.setFont(Global.textFont);
        chkVou.setText("Voucher Printer");
        chkVou.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkVouActionPerformed(evt);
            }
        });

        chkA4.setFont(Global.textFont);
        chkA4.setText("A4");
        chkA4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkA4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkVou, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkA5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkA4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(chkVou)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkA5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkA4))
        );

        lblRec.setFont(Global.lableFont);
        lblRec.setText("Records");

        btnOrder.setFont(Global.lableFont);
        btnOrder.setText("Order");
        btnOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOrderActionPerformed(evt);
            }
        });

        btnBatch.setFont(Global.lableFont);
        btnBatch.setText("Batch");
        btnBatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatchActionPerformed(evt);
            }
        });

        btnBatch1.setFont(Global.lableFont);
        btnBatch1.setText("Transfer");
        btnBatch1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatch1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelInfoLayout = new javax.swing.GroupLayout(panelInfo);
        panelInfo.setLayout(panelInfoLayout);
        panelInfoLayout.setHorizontalGroup(
            panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblRec, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelInfoLayout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnOrder)
                            .addComponent(btnBatch)
                            .addComponent(btnBatch1)))))
        );
        panelInfoLayout.setVerticalGroup(
            panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelInfoLayout.createSequentialGroup()
                        .addComponent(btnOrder)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBatch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBatch1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblRec)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblStatus)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel13.setFont(Global.lableFont);
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel13.setText("Vou Total :");

        jLabel14.setFont(Global.lableFont);
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel14.setText("Discount :");

        jLabel16.setFont(Global.lableFont);
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel16.setText("Tax( + ) :");

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
        txtVouPaid.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtVouPaidFocusGained(evt);
            }
        });
        txtVouPaid.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                txtVouPaidInputMethodTextChanged(evt);
            }
        });
        txtVouPaid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtVouPaidActionPerformed(evt);
            }
        });
        txtVouPaid.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtVouPaidKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtVouPaidKeyTyped(evt);
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

        txtGrandTotal.setEditable(false);
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

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator1)
                            .addComponent(jSeparator2)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGap(8, 8, 8)
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                            .addComponent(jLabel20))
                                        .addGap(0, 0, Short.MAX_VALUE)))
                                .addGap(18, 18, 18))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkPaid)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtVouTotal)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtVouDiscP)
                                    .addComponent(txtVouTaxP))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtVouDiscount)
                                    .addComponent(txtTax)))
                            .addComponent(txtGrandTotal)
                            .addComponent(txtVouPaid, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtVouBalance))))
                .addContainerGap())
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel13, jLabel14, jLabel16, jLabel8});

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

        jScrollPane3.setViewportView(jPanel3);

        tblSale.setFont(Global.textFont);
        tblSale.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblSale.setRowHeight(Global.tblRowHeight);
        tblSale.setShowHorizontalLines(true);
        tblSale.setShowVerticalLines(true);
        tblSale.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblSaleMouseClicked(evt);
            }
        });
        tblSale.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblSaleKeyReleased(evt);
            }
        });
        scroll.setViewportView(tblSale);

        javax.swing.GroupLayout deskPaneLayout = new javax.swing.GroupLayout(deskPane);
        deskPane.setLayout(deskPaneLayout);
        deskPaneLayout.setHorizontalGroup(
            deskPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        deskPaneLayout.setVerticalGroup(
            deskPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jButton1.setFont(Global.lableFont);
        jButton1.setText("Expense");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
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
                        .addComponent(panelInfo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deskPane)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(panelSale, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scroll, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelSale, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scroll, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jScrollPane3)
                        .addComponent(panelInfo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(deskPane))
                    .addComponent(jButton1))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        observeMain();
    }//GEN-LAST:event_formComponentShown

    private void txtCusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCusActionPerformed
        //inventoryRepo.getCustomer().subscribe()
    }//GEN-LAST:event_txtCusActionPerformed

    private void txtCusFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCusFocusGained
        txtCus.selectAll();
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCusFocusGained

    private void txtSalemanFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSalemanFocusGained
        txtSaleman.selectAll();
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSalemanFocusGained

    private void txtRemarkFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRemarkFocusGained

        // TODO add your handling code here:
    }//GEN-LAST:event_txtRemarkFocusGained

    private void txtLocationFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtLocationFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtLocationFocusGained

    private void tblSaleMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSaleMouseClicked
        setStockInfo();        // TODO add your handling code here:
    }//GEN-LAST:event_tblSaleMouseClicked

    private void tblSaleKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblSaleKeyReleased
        // TODO add your handling code here:

    }//GEN-LAST:event_tblSaleKeyReleased

    private void txtSalemanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSalemanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSalemanActionPerformed

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

    private void txtVouPaidKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtVouPaidKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVouPaidKeyTyped

    private void txtVouPaidKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtVouPaidKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVouPaidKeyReleased

    private void chkPaidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkPaidActionPerformed
        // TODO add your handling code here:
        txtVouPaid.setValue(0);
        calculateTotalAmount(false);
    }//GEN-LAST:event_chkPaidActionPerformed

    private void txtReferenceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtReferenceFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtReferenceFocusGained

    private void chkVouActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkVouActionPerformed
        // TODO add your handling code here:


    }//GEN-LAST:event_chkVouActionPerformed

    private void chkA4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkA4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkA4ActionPerformed

    private void chkA5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkA5ActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_chkA5ActionPerformed

    private void txtVouPaidFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVouPaidFocusGained
        // TODO add your handling code here:
        txtVouPaid.selectAll();
    }//GEN-LAST:event_txtVouPaidFocusGained

    private void txtVouPaidInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_txtVouPaidInputMethodTextChanged
        // TODO add your handling code here:
        calculateTotalAmount(false);
    }//GEN-LAST:event_txtVouPaidInputMethodTextChanged

    private void formPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_formPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_formPropertyChange

    private void txtSaleDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSaleDateFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSaleDateFocusGained

    private void btnOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOrderActionPerformed
        // TODO add your handling code here:
        orderDialog();
    }//GEN-LAST:event_btnOrderActionPerformed

    private void txtProjectNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProjectNoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtProjectNoActionPerformed

    private void txtCusMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCusMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCusMouseExited

    private void txtSaleDatePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtSaleDatePropertyChange
        
        Trader t = traderAutoCompleter.getTrader();
        if (t != null) {
            calDueDate(Util1.getInteger(t.getCreditDays()));
        }
    }//GEN-LAST:event_txtSaleDatePropertyChange

    private void txtBatchNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBatchNoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBatchNoActionPerformed

    private void btnBatchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatchActionPerformed
        // TODO add your handling code here:
        batchDialog();
    }//GEN-LAST:event_btnBatchActionPerformed

    private void btnBatch1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatch1ActionPerformed
        // TODO add your handling code here:
        trasnferDialog();
    }//GEN-LAST:event_btnBatch1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        saleExpenseFrame.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        optionDialog();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void txtVouNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVouNoFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVouNoFocusGained

    private void txtVouNoMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtVouNoMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVouNoMouseExited

    private void txtVouNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtVouNoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVouNoActionPerformed
    
    @Override
    public void keyEvent(KeyEvent e) {
        
    }
    
    @Override
    public void selected(Object source, Object selectObj) {
        switch (source.toString()) {
            case "TRADER" -> {
                Trader cus = traderAutoCompleter.getTrader();
                if (cus != null) {
                    calDueDate(Util1.getInteger(cus.getCreditDays()));
                }
            }
            case "SALE-TOTAL", "CAL-TOTAL" ->
                calculateTotalAmount(false);
            case "Location" ->
                setAllLocation();
            case "STOCK-INFO" ->
                setStockInfo();
            case "SALE-HISTORY" -> {
                if (selectObj instanceof VSale s) {
                    boolean local = s.isLocal();
                    inventoryRepo.findSale(s.getVouNo(), s.getDeptId(), local).doOnSuccess((t) -> {
                        t.setLocal(local);
                        setSaleVoucher(t);
                    }).subscribe();
                }
            }
            case "PRINT" -> {
                if (selectObj instanceof VSale s) {
                    boolean local = s.isLocal();
                    inventoryRepo.findSale(s.getVouNo(), s.getDeptId(), local).doOnSuccess((t) -> {
                        printVoucher(t);
                    }).subscribe();
                }
            }
            case "ORDER-HISTORY" -> {
                VOrder s = (VOrder) selectObj;
                inventoryRepo.findOrder(s.getVouNo()).doOnSuccess((t) -> {
                    setSaleVoucherDetail(t);
                }).subscribe();
            }
            case "TR-HISTORY" -> {
                if (selectObj instanceof VTransfer v) {
                    inventoryRepo.findTransfer(v.getVouNo(), v.getDeptId(), v.isLocal()).doOnSuccess((t) -> {
                        setTransferVoucher(t, v.isLocal());
                    }).subscribe();
                }
            }
            case "Select" -> {
                calculateTotalAmount(false);
            }
            case "Batch" -> {
                if (selectObj instanceof GRN g) {
                    inventoryRepo.findTrader(g.getTraderCode()).doOnSuccess((t) -> {
                        traderAutoCompleter.setTrader(t);
                    }).subscribe();
                    inventoryRepo.findGRN(g.getKey().getVouNo()).doOnSuccess((t) -> {
                        batchAutoCompeter.setBatch(t);
                        btnBatch.setText("Batch:View");
                    }).subscribe();
                }
            }
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e
    ) {
        
    }
    
    @Override
    public void keyPressed(KeyEvent e
    ) {
        
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
            }
            case "txtCus" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtLocation.requestFocus();
                    
                }
            }
            case "txtLocation" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtSaleman.requestFocus();
                }
            }
            case "txtSaleman" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    focusTable();
                }
            }
            case "txtVouStatus" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtCus.requestFocus();
                }
            }
            case "txtRemark" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    focusTable();
                }
            }
            case "txtSaleDate" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String date = ((JTextFieldDateEditor) sourceObj).getText();
                    txtSaleDate.setDate(Util1.formatDate(date));
                    txtCus.requestFocus();
                }
            }
            case "txtDueDate" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String date = ((JTextFieldDateEditor) sourceObj).getText();
                    txtDueDate.setDate(Util1.formatDate(date));
                    txtReference.requestFocus();
                }
            }
            case "txtCurrency" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtRemark.requestFocus();
                }
            }
            case "txtVouTaxP" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (Util1.getDouble(txtVouTaxP.getValue()) <= 0) {
                        txtTax.setValue(0);
                    }
                    calculateTotalAmount(false);
                    focusTable();
                }
            }
            case "txtTax" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtVouTaxP.setValue(0);
                    calculateTotalAmount(false);
                    focusTable();
                }
            }
            case "txtVouDiscount" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (Util1.getDouble(txtVouDiscount.getValue()) >= 0) {
                        txtVouDiscP.setValue(0);
                    }
                    calculateTotalAmount(false);
                    focusTable();
                }
            }
            case "txtVouDiscP" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (Util1.getDouble(txtVouDiscP.getValue()) <= 0) {
                        txtVouDiscount.setValue(0);
                    }
                    calculateTotalAmount(false);
                    focusTable();
                }
            }
            case "txtVouPaid" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    calculateTotalAmount(true);
                    focusTable();
                }
            }
        }
    }
    
    public String addCreditDay(String date, Integer dCount) {
        return LocalDateTime
                .parse(date)
                .plusDays(dCount)
                .toString();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBatch;
    private javax.swing.JButton btnBatch1;
    private javax.swing.JButton btnOrder;
    private javax.swing.JCheckBox chkA4;
    private javax.swing.JCheckBox chkA5;
    private javax.swing.JCheckBox chkPaid;
    private javax.swing.JCheckBox chkVou;
    private javax.swing.JDesktopPane deskPane;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblRec;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPanel panelInfo;
    private javax.swing.JPanel panelSale;
    private javax.swing.JScrollPane scroll;
    private javax.swing.JTable tblSale;
    private javax.swing.JTextField txtBatchNo;
    private javax.swing.JTextField txtCurrency;
    private javax.swing.JTextField txtCus;
    private com.toedter.calendar.JDateChooser txtDueDate;
    private javax.swing.JFormattedTextField txtGrandTotal;
    private javax.swing.JTextField txtLocation;
    private javax.swing.JTextField txtProjectNo;
    private javax.swing.JTextField txtReference;
    private javax.swing.JTextField txtRemark;
    private com.toedter.calendar.JDateChooser txtSaleDate;
    private javax.swing.JTextField txtSaleman;
    private javax.swing.JFormattedTextField txtTax;
    private javax.swing.JFormattedTextField txtVouBalance;
    private javax.swing.JFormattedTextField txtVouDiscP;
    private javax.swing.JFormattedTextField txtVouDiscount;
    private javax.swing.JTextField txtVouNo;
    private javax.swing.JFormattedTextField txtVouPaid;
    private javax.swing.JFormattedTextField txtVouTaxP;
    private javax.swing.JFormattedTextField txtVouTotal;
    // End of variables declaration//GEN-END:variables

    @Override
    public void delete() {
        deleteSale();
    }
    
    @Override
    public void print() {
        saveSale(true);
    }
    
    @Override
    public void save() {
        saveSale(false);
    }
    
    @Override
    public void newForm() {
        clear(true);
    }
    
    @Override
    public void history() {
        historySale();
    }
    
    @Override
    public void refresh() {
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
