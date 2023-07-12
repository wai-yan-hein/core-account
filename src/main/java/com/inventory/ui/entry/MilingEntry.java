/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry;

import com.repo.AccountRepo;
import com.common.DecimalFormatRender;
import com.common.Global;
import com.common.KeyPropagate;
import com.common.PanelControl;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.common.Util1;
import com.inventory.editor.LocationCellEditor;
import com.inventory.editor.StockCellEditor;
import com.inventory.editor.TraderAutoCompleter;
import com.inventory.model.Location;
import com.inventory.model.OrderHis;
import com.inventory.model.MillingHis;
import com.inventory.model.MillingRawDetail;
import com.inventory.model.MillingHisKey;
import com.inventory.model.MillingOutDetail;
import com.inventory.model.Trader;
import com.inventory.model.VOrder;
import com.inventory.model.VSale;
import com.inventory.ui.common.MilingRawTableModel;
import com.repo.InventoryRepo;
import com.inventory.ui.common.MilingOutTableModel;
import com.inventory.ui.common.StockBalanceTableModel;
import com.inventory.ui.entry.dialog.OrderHistoryDialog;
import com.inventory.ui.setup.dialog.ExpenseSetupDialog;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.inventory.ui.setup.dialog.common.StockUnitEditor;
import com.toedter.calendar.JTextFieldDateEditor;
import com.repo.UserRepo;
import com.user.editor.CurrencyAutoCompleter;
import com.user.editor.ProjectAutoCompleter;
import com.user.model.Project;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 *
 * @author wai yan
 */
@Component
@Slf4j
public class MilingEntry extends javax.swing.JPanel implements SelectionObserver, KeyListener, KeyPropagate, PanelControl {

    private List<MillingRawDetail> listDetail = new ArrayList();
    private List<MillingOutDetail> listOutDetail = new ArrayList();
    private final MilingOutTableModel milingOutTableModel = new MilingOutTableModel();
    private final MilingRawTableModel milingRawTableModel = new MilingRawTableModel();
//    private RiceMilingHisHistoryDialog dialog;
    private final StockBalanceTableModel stockBalanceTableModel = new StockBalanceTableModel();
    @Autowired
    private InventoryRepo inventoryRepo;
    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private TaskExecutor taskExecutor;
    private OrderHistoryDialog orderDialog;
    private CurrencyAutoCompleter currAutoCompleter;
    private TraderAutoCompleter traderAutoCompleter;
    private ProjectAutoCompleter projectAutoCompleter;
    private SelectionObserver observer;
    private MillingHis milling = new MillingHis();
    private JProgressBar progress;
    private Mono<List<Location>> monoLoc;
    private double prvBal = 0;
    private double balance = 0;

    public TraderAutoCompleter getTraderAutoCompleter() {
        return traderAutoCompleter;
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
     * Creates new form SaleEntry1
     */
    public MilingEntry() {
        initComponents();
        lblStatus.setForeground(Color.GREEN);
        initKeyListener();
        initDateListner();
        initTextFormat();
        actionMapping();
    }

    private void initTextFormat() {
        txtLoadAmt.setFormatterFactory(Util1.getDecimalFormat());
        txtOutputAmt.setFormatterFactory(Util1.getDecimalFormat());
        txtOutputQty.setFormatterFactory(Util1.getDecimalFormat());
        txtOutputWeight.setFormatterFactory(Util1.getDecimalFormat());
        txtLoadQty.setFormatterFactory(Util1.getDecimalFormat());
        txtLoadWeight.setFormatterFactory(Util1.getDecimalFormat());
        txtDiffWeight.setFormatterFactory(Util1.getDecimalFormat());
        txtLoadExpense.setFormatterFactory(Util1.getDecimalFormat());
        //font
        txtLoadAmt.setFont(Global.amtFont);
        txtOutputAmt.setFont(Global.amtFont);
        txtOutputQty.setFont(Global.amtFont);
        txtOutputWeight.setFont(Global.amtFont);
        txtLoadQty.setFont(Global.amtFont);
        txtLoadWeight.setFont(Global.amtFont);
        txtDiffWeight.setFont(Global.amtFont);
        txtLoadExpense.setFont(Global.amtFont);
        //align
        txtLoadAmt.setHorizontalAlignment(JTextField.RIGHT);
        txtOutputAmt.setHorizontalAlignment(JTextField.RIGHT);
        txtOutputQty.setHorizontalAlignment(JTextField.RIGHT);
        txtOutputWeight.setHorizontalAlignment(JTextField.RIGHT);
        txtLoadQty.setHorizontalAlignment(JTextField.RIGHT);
        txtLoadWeight.setHorizontalAlignment(JTextField.RIGHT);
        txtDiffWeight.setHorizontalAlignment(JTextField.RIGHT);
        txtLoadExpense.setHorizontalAlignment(JTextField.RIGHT);
    }

    private void actionMapping() {
        String solve = "delete";
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        tblRaw.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, solve);
        tblRaw.getActionMap().put(solve, new DeleteAction());

    }

    private void setSaleVoucherDetail(OrderHis oh, boolean local) {
        milingOutTableModel.clear();
        if (oh != null) {
            progress.setIndeterminate(true);
            Integer deptId = oh.getKey().getDeptId();
            String vouNo = oh.getKey().getVouNo();
//            inventoryRepo.getOrderDetail(vouNo, deptId, local)
//                    .subscribe((list) -> {
//                        list.forEach((od) -> {
//                            RiceMilingHisDetail sd = new RiceMilingHisDetail();
//                            sd.setStockCode(od.getStockCode());
//                            sd.setUserCode(od.getUserCode());
//                            sd.setStockName(od.getStockName());
//                            sd.setTraderName(od.getTraderName());
//                            sd.setRelName(od.getRelName());
//                            sd.setQty(od.getQty());
//                            sd.setAmount(od.getAmount());
//                            sd.setUnitCode(od.getUnitCode());
//                            sd.setPrice(od.getPrice());
//                            sd.setLocCode(od.getLocCode());
//                            sd.setLocName(od.getLocName());
//                            riceMilingTableModel.addSale(sd);
//                        });
//                    }, (e) -> {
//                        progress.setIndeterminate(false);
//                        JOptionPane.showMessageDialog(this, e.getMessage());
//                    }, () -> {
//                        inventoryRepo.findLocation(oh.getLocCode()).subscribe((t) -> {
//                            locationAutoCompleter.setLocation(t);
//                        });
//                        Mono<Trader> trader = inventoryRepo.findTrader(oh.getTraderCode());
//                        trader.subscribe((t) -> {
//                            traderAutoCompleter.setTrader(t);
//                        });
//
//                        userRepo.findCurrency(oh.getCurCode()).subscribe((t) -> {
//                            currAutoCompleter.setCurrency(t);
//                        });
//                        inventoryRepo.findSaleMan(oh.getSaleManCode()).subscribe((t) -> {
//                            saleManCompleter.setSaleMan(t);
//                        });
//                        if (oh.getProjectNo() != null) {
//                            userRepo.find(new ProjectKey(oh.getProjectNo(), Global.compCode)).subscribe(t1 -> {
//                                projectAutoCompleter.setProject(t1);
//                            });
//                        } else {
//                            projectAutoCompleter.setProject(null);
//                        }
//
//                        txtDueDate.setDate(Util1.convertToDate(oh.getCreditTerm()));
//                        txtRemark.setText(oh.getRemark());
//                        txtReference.setText(oh.getReference());
//                        txtSaleDate.setDate(Util1.convertToDate(oh.getVouDate()));
//                        txtVouTotal.setValue(Util1.getFloat(oh.getVouTotal()));
//                        txtVouDiscP.setValue(Util1.getFloat(oh.getDiscP()));
//                        txtVouDiscount.setValue(Util1.getFloat(oh.getDiscount()));
//                        txtVouTaxP.setValue(Util1.getFloat(oh.getTaxPercent()));
//                        txtTax.setValue(Util1.getFloat(oh.getTaxAmt()));
//                        txtVouPaid.setValue(Util1.getFloat(oh.getPaid()));
//                        txtVouBalance.setValue(Util1.getFloat(oh.getBalance()));
//                        txtGrandTotal.setValue(Util1.getFloat(oh.getGrandTotal()));
//                        chkPaid.setSelected(Util1.getFloat(oh.getPaid()) > 0);
//                        txtOrderNo.setText(oh.getKey().getVouNo());
//                        riceMilingTableModel.addNewRow();
//                        focusTable();
//                        progress.setIndeterminate(false);
//                    });
        }

    }

    private class DeleteAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            deleteTran();
        }
    }

    private void initDateListner() {
        txtSaleDate.getDateEditor().getUiComponent().setName("txtSaleDate");
        txtSaleDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtSaleDate.getDateEditor().getUiComponent().addFocusListener(fa);
        txtCurrency.addFocusListener(fa);
        txtCus.addFocusListener(fa);;
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

    public void initMain() {
        initCombo();
        initStockBalanceTable();
        initRawTable();
        initOutputTable();
        assignDefaultValue();
        txtSaleDate.setDate(Util1.getTodayDate());
        txtCus.requestFocus();
    }

    private void initRawTable() {
        tblRaw.setModel(milingRawTableModel);
        milingRawTableModel.setParent(tblRaw);
        milingRawTableModel.setSale(this);
        milingRawTableModel.addNewRow();
        milingRawTableModel.setSelectionObserver(this);
        milingRawTableModel.setVouDate(txtSaleDate);
        milingRawTableModel.setInventoryRepo(inventoryRepo);
        milingRawTableModel.setSbTableModel(stockBalanceTableModel);
        tblRaw.getTableHeader().setFont(Global.tblHeaderFont);
        tblRaw.setCellSelectionEnabled(true);
        tblRaw.setCellSelectionEnabled(true);
        tblRaw.getColumnModel().getColumn(0).setPreferredWidth(50);//Code
        tblRaw.getColumnModel().getColumn(1).setPreferredWidth(200);//Name
        tblRaw.getColumnModel().getColumn(2).setPreferredWidth(60);//Location
        tblRaw.getColumnModel().getColumn(3).setPreferredWidth(50);//weight
        tblRaw.getColumnModel().getColumn(4).setPreferredWidth(30);//unit
        tblRaw.getColumnModel().getColumn(5).setPreferredWidth(50);//qty
        tblRaw.getColumnModel().getColumn(6).setPreferredWidth(30);//unit
        tblRaw.getColumnModel().getColumn(7).setPreferredWidth(50);//std
        tblRaw.getColumnModel().getColumn(8).setPreferredWidth(50);//total
        tblRaw.getColumnModel().getColumn(9).setPreferredWidth(50);//price
        tblRaw.getColumnModel().getColumn(10).setPreferredWidth(60);//amt
        tblRaw.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor(inventoryRepo));
        tblRaw.getColumnModel().getColumn(1).setCellEditor(new StockCellEditor(inventoryRepo));
        monoLoc.subscribe((t) -> {
            tblRaw.getColumnModel().getColumn(2).setCellEditor(new LocationCellEditor(t));
        });
        tblRaw.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());//weight
        inventoryRepo.getStockUnit().subscribe((t) -> {
            tblRaw.getColumnModel().getColumn(4).setCellEditor(new StockUnitEditor(t));
            tblRaw.getColumnModel().getColumn(6).setCellEditor(new StockUnitEditor(t));
        }, (e) -> {
            log.error("getStockUnit: " + e.getMessage());
        });
        tblRaw.getColumnModel().getColumn(5).setCellEditor(new AutoClearEditor());//weight
        tblRaw.getColumnModel().getColumn(7).setCellEditor(new AutoClearEditor());//price
        tblRaw.getColumnModel().getColumn(9).setCellEditor(new AutoClearEditor());//price
        tblRaw.getColumnModel().getColumn(10).setCellEditor(new AutoClearEditor());//price
        tblRaw.setDefaultRenderer(Object.class, new DecimalFormatRender());
        tblRaw.setDefaultRenderer(Float.class, new DecimalFormatRender());
        tblRaw.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblRaw.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void initOutputTable() {

        // tbl Output
        tblOutput.setModel(milingOutTableModel);
        milingOutTableModel.setParent(tblOutput);
        milingOutTableModel.setSale(this);
        milingOutTableModel.addNewRow();
        milingOutTableModel.setSelectionObserver(this);
        milingOutTableModel.setVouDate(txtSaleDate);
        milingOutTableModel.setInventoryRepo(inventoryRepo);
        milingOutTableModel.setSbTableModel(stockBalanceTableModel);
        milingOutTableModel.setModel(txtLoadWeight);
        tblOutput.getTableHeader().setFont(Global.tblHeaderFont);
        tblOutput.setCellSelectionEnabled(true);
        tblOutput.getColumnModel().getColumn(0).setPreferredWidth(50);//Code
        tblOutput.getColumnModel().getColumn(1).setPreferredWidth(200);//Name
        tblOutput.getColumnModel().getColumn(2).setPreferredWidth(60);//Location
        tblOutput.getColumnModel().getColumn(3).setPreferredWidth(50);//weight
        tblOutput.getColumnModel().getColumn(4).setPreferredWidth(30);//unit
        tblOutput.getColumnModel().getColumn(5).setPreferredWidth(50);//qty
        tblOutput.getColumnModel().getColumn(6).setPreferredWidth(30);//unit
        tblOutput.getColumnModel().getColumn(7).setPreferredWidth(50);//std
        tblOutput.getColumnModel().getColumn(8).setPreferredWidth(50);//total
        tblOutput.getColumnModel().getColumn(9).setPreferredWidth(50);//percent
        tblOutput.getColumnModel().getColumn(10).setPreferredWidth(50);//price
        tblOutput.getColumnModel().getColumn(11).setPreferredWidth(60);//amt
        tblOutput.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor(inventoryRepo));
        tblOutput.getColumnModel().getColumn(1).setCellEditor(new StockCellEditor(inventoryRepo));
        monoLoc.subscribe((t) -> {
            tblOutput.getColumnModel().getColumn(2).setCellEditor(new LocationCellEditor(t));
        });
        tblOutput.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());//weight
        tblOutput.getColumnModel().getColumn(5).setCellEditor(new AutoClearEditor());//qty
        inventoryRepo.getStockUnit().subscribe((t) -> {
            tblOutput.getColumnModel().getColumn(4).setCellEditor(new StockUnitEditor(t));
            tblOutput.getColumnModel().getColumn(6).setCellEditor(new StockUnitEditor(t));
        }, (e) -> {
            log.error("getStockUnit: " + e.getMessage());
        });
        tblOutput.getColumnModel().getColumn(10).setCellEditor(new AutoClearEditor());//price
        tblOutput.setDefaultRenderer(Object.class, new DecimalFormatRender());
        tblOutput.setDefaultRenderer(Float.class, new DecimalFormatRender());
        tblOutput.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblOutput.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void initCombo() {
        traderAutoCompleter = new TraderAutoCompleter(txtCus, inventoryRepo, null, false, "CUS");
        traderAutoCompleter.setObserver(this);
        monoLoc = inventoryRepo.getLocation();
        currAutoCompleter = new CurrencyAutoCompleter(txtCurrency, null);
        userRepo.getCurrency().subscribe((t) -> {
            currAutoCompleter.setListCurrency(t);
        });
        userRepo.getDefaultCurrency().subscribe((c) -> {
            currAutoCompleter.setCurrency(c);
        });
        projectAutoCompleter = new ProjectAutoCompleter(txtProjectNo, userRepo, null, false);
        projectAutoCompleter.setObserver(this);
    }

    private void initKeyListener() {
        txtSaleDate.getDateEditor().getUiComponent().setName("txtSaleDate");
        txtSaleDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtVouNo.addKeyListener(this);
        txtRemark.addKeyListener(this);
        txtCus.addKeyListener(this);
        txtCurrency.addKeyListener(this);
        tblRaw.addKeyListener(this);
        txtProjectNo.addKeyListener(this);
    }

    private void initStockBalanceTable() {
        if (ProUtil.isCalStock()) {
            stockBalanceTableModel.setInventoryRepo(inventoryRepo);
            tblStockBalance.setModel(stockBalanceTableModel);
            stockBalanceTableModel.setProgress(sbProgress);
            stockBalanceTableModel.setChkSummary(chkSummary);
            tblStockBalance.getColumnModel().getColumn(0).setPreferredWidth(100);//Unit
            tblStockBalance.getColumnModel().getColumn(1).setPreferredWidth(140);//Cost Price
            tblStockBalance.getTableHeader().setFont(Global.tblHeaderFont);
            tblStockBalance.setDefaultRenderer(Object.class, new TableCellRender());
            tblStockBalance.setDefaultRenderer(Float.class, new TableCellRender());
            tblStockBalance.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }
        sbPanel.setVisible(ProUtil.isCalStock());
    }

    private void assignDefaultValue() {
        userRepo.getDefaultCurrency().subscribe((t) -> {
            currAutoCompleter.setCurrency(t);
        });
        inventoryRepo.getDefaultCustomer().subscribe((t) -> {
            traderAutoCompleter.setTrader(t);
        });
        inventoryRepo.getDefaultLocation().subscribe((t) -> {
            milingRawTableModel.setLocation(t);
            milingOutTableModel.setLocation(t);
        });
        progress.setIndeterminate(false);
        txtCurrency.setEnabled(ProUtil.isMultiCur());
        txtVouNo.setText(null);
        if (!lblStatus.getText().equals("NEW")) {
            txtSaleDate.setDate(Util1.getTodayDate());
        }
    }

    private void clear() {
        disableForm(true);
        milingOutTableModel.removeListDetail();
        milingOutTableModel.clearDelList();
        milingOutTableModel.setChange(false);
        milingRawTableModel.removeListDetail();
        milingRawTableModel.clearDelList();
        milingRawTableModel.setChange(false);
        stockBalanceTableModel.clearList();
        assignDefaultValue();
        milling = new MillingHis();
        lblStatus.setText("NEW");
        lblStatus.setForeground(Color.GREEN);
        progress.setIndeterminate(false);
        txtRemark.setText(null);
        txtReference.setText(null);
        txtCus.requestFocus();
        projectAutoCompleter.setProject(null);
        txtLoadQty.setValue(null);
        txtLoadWeight.setValue(null);
        txtLoadAmt.setValue(null);
        txtOutputQty.setValue(null);
        txtOutputWeight.setValue(null);
        txtOutputAmt.setValue(null);
    }

    private void historyOrder() {
        if (orderDialog == null) {
            orderDialog = new OrderHistoryDialog(Global.parentForm);
            orderDialog.setInventoryRepo(inventoryRepo);
            orderDialog.setUserRepo(userRepo);
            orderDialog.setObserver(this);
            orderDialog.initMain();
            orderDialog.setSize(Global.width - 100, Global.height - 100);
            orderDialog.setLocationRelativeTo(null);
        }
        orderDialog.search();
        orderDialog.setVisible(true);
    }

    public void saveSale(boolean print) {
        if (isValidEntry() && milingRawTableModel.isValidEntry()) {
//            milling.setListSH(riceMilingTableModel.getListDetail());
//            milling.setListDel(riceMilingTableModel.getDelList());
//            milling.setBackup(riceMilingTableModel.isChange());
            observer.selected("save", false);
            progress.setIndeterminate(true);
            if (print) {
                observer.selected("print", false);
                if (Util1.getBoolean(ProUtil.getProperty("trader.balance"))) {
                    String date = Util1.toDateStr(txtSaleDate.getDate(), "yyyy-MM-dd");
                    String traderCode = traderAutoCompleter.getTrader().getKey().getCode();
                    balance = accountRepo.getTraderBalance(date, traderCode, milling.getCurCode(), Global.compCode);
                }
            }
//            inventoryRepo.save(milling).subscribe((t) -> {
//                progress.setIndeterminate(false);
//                clear();
//                if (print) {
//                    String reportName = getReportName();
//                    String vouNo = t.getKey().getVouNo();
//                    boolean local = t.isLocal();
//                    printVoucher(vouNo, reportName, local);
//                }
//            }, (e) -> {
//                observer.selected("save", true);
//                JOptionPane.showMessageDialog(this, e.getMessage());
//                progress.setIndeterminate(false);
//            });
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
        } else if (traderAutoCompleter.getTrader() == null) {
            JOptionPane.showMessageDialog(this, "Choose Trader.",
                    "No Trader.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtCus.requestFocus();
        } else if (Util1.getFloat(txtOutputAmt.getValue()) <= 0) {
            JOptionPane.showMessageDialog(this, "Invalid Amount.",
                    "No Sale Record.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtOutputAmt.requestFocus();
        } else {
            milling.setRemark(txtRemark.getText());
            milling.setReference(txtReference.getText());
            milling.setCurCode(currAutoCompleter.getCurrency().getCurCode());
            milling.setTraderCode(traderAutoCompleter.getTrader().getKey().getCode());
            milling.setStatus(lblStatus.getText());
            milling.setVouDate(Util1.convertToLocalDateTime(txtSaleDate.getDate()));
            milling.setMacId(Global.macId);
            Project p = projectAutoCompleter.getProject();
            milling.setProjectNo(p == null ? null : p.getKey().getProjectNo());
            if (lblStatus.getText().equals("NEW")) {
                MillingHisKey key = new MillingHisKey();
                key.setCompCode(Global.compCode);
                key.setDeptId(Global.deptId);
                key.setVouNo(null);
                milling.setKey(key);
                milling.setCreatedDate(Util1.getTodayDate());
                milling.setCreatedBy(Global.loginUser.getUserCode());
                milling.setSession(Global.sessionId);
            } else {
                milling.setUpdatedBy(Global.loginUser.getUserCode());
            }
        }
        return status;
    }

    private void deleteSale() {
        String status = lblStatus.getText();
        switch (status) {
            case "EDIT" -> {
                int yes_no = JOptionPane.showConfirmDialog(this,
                        "Are you sure to delete?", "Sale Voucher Delete.", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                if (yes_no == 0) {
//                    inventoryRepo.delete(milling).subscribe((t) -> {
//                        clear();
//                    });
                }
            }
            case "DELETED" -> {
                int yes_no = JOptionPane.showConfirmDialog(this,
                        "Are you sure to restore?", "Sale Voucher Restore.", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (yes_no == 0) {
                    milling.setDeleted(false);
//                    inventoryRepo.restore(milling).subscribe((t) -> {
//                        lblStatus.setText("EDIT");
//                        lblStatus.setForeground(Color.blue);
//                        disableForm(true);
//                    });
                }
            }
            default ->
                JOptionPane.showMessageDialog(this, "Voucher can't delete.");
        }
    }

    private void deleteTran() {
        int row = tblRaw.convertRowIndexToModel(tblRaw.getSelectedRow());
        if (row >= 0) {
            if (tblRaw.getCellEditor() != null) {
                tblRaw.getCellEditor().stopCellEditing();
            }
            int yes_no = JOptionPane.showConfirmDialog(this,
                    "Are you sure to delete?", "Sale Transaction delete.", JOptionPane.YES_NO_OPTION);
            if (yes_no == 0) {
                milingOutTableModel.delete(row);
                calculateMilling();
            }
        }
    }

    private void calculateMilling() {
        //cal raw
        listDetail = milingRawTableModel.getListDetail();
        float loadQty = listDetail.stream().map(s -> Util1.getFloat(s.getQty())).reduce(0.0f, (accumulator, _item) -> accumulator + _item);
        float loadWt = listDetail.stream().map(d -> Util1.getFloat(d.getTotalWeight())).reduce(0.0f, (accumulator, _item) -> accumulator + _item);
        float loadAmt = listDetail.stream().map(sdh -> Util1.getFloat(sdh.getAmount())).reduce(0.0f, (accumulator, _item) -> accumulator + _item);
        txtLoadQty.setValue(loadQty);
        txtLoadWeight.setValue(loadWt);
        txtLoadAmt.setValue(loadAmt);
        //cal output
        listOutDetail = milingOutTableModel.getListDetail();
        listOutDetail.get(0).setPrice(0.0f);
        listOutDetail.get(0).setAmount(0.0f);
        float outAmt = listOutDetail.stream().map(sdh -> Util1.getFloat(sdh.getAmount())).reduce(0.0f, (accumulator, _item) -> accumulator + _item);
        listOutDetail.get(0).setAmount(loadAmt - outAmt);
        listOutDetail.get(0).setPrice(listOutDetail.get(0).getAmount() / listOutDetail.get(0).getQty());
        log.info(listOutDetail.get(0).getAmount().toString());

        //calculate total
        milingOutTableModel.setListDetail(listOutDetail);
        listOutDetail = milingOutTableModel.getListDetail();
        outAmt = listOutDetail.stream().map(sdh -> Util1.getFloat(sdh.getAmount())).reduce(0.0f, (accumulator, _item) -> accumulator + _item);
        float outQty = listOutDetail.stream().map(s -> Util1.getFloat(s.getQty())).reduce(0.0f, (accumulator, _item) -> accumulator + _item);
        float outWt = listOutDetail.stream().map(d -> Util1.getFloat(d.getTotalWeight())).reduce(0.0f, (accumulator, _item) -> accumulator + _item);
        txtOutputQty.setValue(outQty);
        txtOutputWeight.setValue(outWt);
        txtOutputAmt.setValue(outAmt);
        txtDiffWeight.setValue(loadWt - outWt);
    }

    public void historySale() {
//        if (dialog == null) {
//            dialog = new RiceMilingHisHistoryDialog(Global.parentForm);
//            dialog.setInventoryRepo(inventoryRepo);
//            dialog.setIntegration(integration);
//            dialog.setUserRepo(userRepo);
//            dialog.setTaskExecutor(taskExecutor);
//            dialog.setObserver(this);
//            dialog.initMain();
//            dialog.setSize(Global.width - 100, Global.height - 100);
//            dialog.setLocationRelativeTo(null);
//        }
//        dialog.search();
    }

    public void setSaleVoucher(MillingHis sh) {
        if (sh != null) {
            progress.setIndeterminate(true);
            milling = sh;
            Mono<Trader> trader = inventoryRepo.findTrader(milling.getTraderCode());
            trader.subscribe((t) -> {
                traderAutoCompleter.setTrader(t);
            });

            userRepo.findCurrency(milling.getCurCode()).subscribe((t) -> {
                currAutoCompleter.setCurrency(t);
            });
            String vouNo = sh.getKey().getVouNo();
//            inventoryRepo.getSaleDetail(vouNo, sh.getKey().getDeptId(), milling.isLocal()).subscribe((t) -> {
//                riceMilingTableModel.setListDetail(t);
//                riceMilingTableModel.addNewRow();
//                if (sh.isVouLock()) {
//                    lblStatus.setText("Voucher is locked.");
//                    lblStatus.setForeground(Color.RED);
//                    disableForm(false);
//                } else if (!ProUtil.isSaleEdit()) {
//                    lblStatus.setText("No Permission.");
//                    lblStatus.setForeground(Color.RED);
//                    disableForm(false);
//                    observer.selected("print", true);
//                } else if (sh.isDeleted()) {
//                    lblStatus.setText("DELETED");
//                    lblStatus.setForeground(Color.RED);
//                    disableForm(false);
//                    observer.selected("delete", true);
//                } else {
//                    lblStatus.setText("EDIT");
//                    lblStatus.setForeground(Color.blue);
//                    disableForm(true);
//                }
//                txtVouNo.setText(milling.getKey().getVouNo());
//                txtDueDate.setDate(Util1.convertToDate(milling.getCreditTerm()));
//                txtRemark.setText(milling.getRemark());
//                txtReference.setText(milling.getReference());
//                txtSaleDate.setDate(Util1.convertToDate(milling.getVouDate()));
//                txtVouTotal.setValue(Util1.getFloat(milling.getVouTotal()));
//                txtVouDiscP.setValue(Util1.getFloat(milling.getDiscP()));
//                txtVouDiscount.setValue(Util1.getFloat(milling.getDiscount()));
//                txtVouTaxP.setValue(Util1.getFloat(milling.getTaxPercent()));
//                txtTax.setValue(Util1.getFloat(milling.getTaxAmt()));
//                txtVouPaid.setValue(Util1.getFloat(milling.getPaid()));
//                txtVouBalance.setValue(Util1.getFloat(milling.getBalance()));
//                txtGrandTotal.setValue(Util1.getFloat(milling.getGrandTotal()));
//                chkPaid.setSelected(milling.getPaid() > 0);
//                if (milling.getProjectNo() != null) {
//                    userRepo.find(new ProjectKey(milling.getProjectNo(), Global.compCode)).subscribe(t1 -> {
//                        projectAutoCompleter.setProject(t1);
//                    });
//                } else {
//                    projectAutoCompleter.setProject(null);
//                }
//                focusTable();
//                progress.setIndeterminate(false);
//            }, (e) -> {
//                progress.setIndeterminate(false);
//                JOptionPane.showMessageDialog(this, e.getMessage());
//            });
        }
    }

    private void disableForm(boolean status) {
        tblRaw.setEnabled(status);
        panelSale.setEnabled(status);
        txtSaleDate.setEnabled(status);
        txtCus.setEnabled(status);
        txtRemark.setEnabled(status);
        txtCurrency.setEnabled(status);
        txtReference.setEnabled(status);
        observer.selected("save", status);
        observer.selected("delete", status);
        observer.selected("print", status);

    }

    private void setAllLocation() {
//        List<RiceMilingHisDetail> listSaleDetail = riceMilingTableModel.getListDetail();
//        Location loc = locationAutoCompleter.getLocation();
//        if (listSaleDetail != null) {
//            listSaleDetail.forEach(sd -> {
//                sd.setLocCode(loc.getKey().getLocCode());
//                sd.setLocName(loc.getLocName());
//            });
//        }
//        riceMilingTableModel.setListDetail(listSaleDetail);
    }

    private void printVoucher(String vouNo, String reportName, boolean local) {
        inventoryRepo.getSaleReport(vouNo).subscribe((t) -> {
            viewReport(t, reportName);
        }, (e) -> {
            JOptionPane.showMessageDialog(this, e.getMessage());
        });
    }

    private void viewReport(byte[] t, String reportName) {
        if (reportName != null) {
            try {
                String logoPath = String.format("images%s%s", File.separator, ProUtil.getProperty("logo.name"));
                Map<String, Object> param = new HashMap<>();
                param.put("p_print_date", Util1.getTodayDateTime());
                param.put("p_comp_name", Global.companyName);
                param.put("p_comp_address", Global.companyAddress);
                param.put("p_comp_phone", Global.companyPhone);
                param.put("p_logo_path", logoPath);
                param.put("p_balance", balance);
                param.put("p_prv_balance", prvBal);
                String reportPath = ProUtil.getReportPath() + reportName.concat(".jasper");
                ByteArrayInputStream stream = new ByteArrayInputStream(t);
                JsonDataSource ds = new JsonDataSource(stream);
                JasperPrint jp = JasperFillManager.fillReport(reportPath, param, ds);
                log.info(ProUtil.getFontPath());
//                if (chkVou.isSelected()) {
//                    JasperReportUtil.print(jp);
//                } else {
//                    JasperViewer.viewReport(jp, false);
//                }
            } catch (JRException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Select Report Type");
//            chkVou.requestFocus();
        }
    }

    private String getReportName() {
        String name = null;
//        if (chkVou.isSelected()) {
//            name = ProUtil.getProperty("report.sale.voucher");
//        }
//        if (chkA4.isSelected()) {
//            name = ProUtil.getProperty("report.sale.A4");
//        }
//        if (chkA5.isSelected()) {
//            name = ProUtil.getProperty("report.sale.A5");
//        }
        return name;
    }

    private void focusTable() {
        int rc = tblRaw.getRowCount();
        if (rc >= 1) {
            tblRaw.setRowSelectionInterval(rc - 1, rc - 1);
            tblRaw.setColumnSelectionInterval(0, 0);
            tblRaw.requestFocus();
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
        txtVouNo = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtSaleDate = new com.toedter.calendar.JDateChooser();
        txtCurrency = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        txtReference = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtProjectNo = new javax.swing.JTextField();
        lblStatus = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        sbPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblStockBalance = new javax.swing.JTable();
        sbProgress = new javax.swing.JProgressBar();
        chkSummary = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblRaw = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblOutput = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtLoadQty = new javax.swing.JFormattedTextField();
        jLabel7 = new javax.swing.JLabel();
        txtLoadWeight = new javax.swing.JFormattedTextField();
        jLabel8 = new javax.swing.JLabel();
        txtLoadAmt = new javax.swing.JFormattedTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtOutputAmt = new javax.swing.JFormattedTextField();
        txtOutputWeight = new javax.swing.JFormattedTextField();
        txtOutputQty = new javax.swing.JFormattedTextField();
        jLabel16 = new javax.swing.JLabel();
        txtDiffWeight = new javax.swing.JFormattedTextField();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblExpense = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        txtLoadExpense = new javax.swing.JFormattedTextField();

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

        txtVouNo.setEditable(false);
        txtVouNo.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtVouNo.setFont(Global.textFont);
        txtVouNo.setName("txtVouNo"); // NOI18N

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Milling Date");

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Currency");

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

        lblStatus.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        lblStatus.setText("NEW");

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
                    .addComponent(txtSaleDate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtVouNo, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelSaleLayout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtReference))
                    .addGroup(panelSaleLayout.createSequentialGroup()
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtRemark))
                    .addGroup(panelSaleLayout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtCurrency)))
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelSaleLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelSaleLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10)
                        .addGap(14, 14, 14)
                        .addComponent(txtProjectNo)))
                .addContainerGap())
        );

        panelSaleLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel17, jLabel2, jLabel21, jLabel4, jLabel6, jLabel9});

        panelSaleLayout.setVerticalGroup(
            panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSaleLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(txtProjectNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelSaleLayout.createSequentialGroup()
                        .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(txtSaleDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtReference, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel9)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtCus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel21)
                            .addComponent(jLabel2)))
                    .addComponent(lblStatus))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        sbPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Stock Balance", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, Global.lableFont));

        tblStockBalance.setFont(Global.textFont);
        tblStockBalance.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblStockBalance.setRowHeight(Global.tblRowHeight);
        jScrollPane2.setViewportView(tblStockBalance);

        chkSummary.setText("Summary");

        javax.swing.GroupLayout sbPanelLayout = new javax.swing.GroupLayout(sbPanel);
        sbPanel.setLayout(sbPanelLayout);
        sbPanelLayout.setHorizontalGroup(
            sbPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sbPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sbPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(sbProgress, javax.swing.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE)
                    .addGroup(sbPanelLayout.createSequentialGroup()
                        .addComponent(chkSummary)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        sbPanelLayout.setVerticalGroup(
            sbPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sbPanelLayout.createSequentialGroup()
                .addComponent(sbProgress, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkSummary)
                .addContainerGap())
        );

        tblRaw.setFont(Global.textFont);
        tblRaw.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblRaw.setRowHeight(Global.tblRowHeight);
        tblRaw.setShowHorizontalLines(true);
        tblRaw.setShowVerticalLines(true);
        tblRaw.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblRawMouseClicked(evt);
            }
        });
        tblRaw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblRawKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblRaw);

        tblOutput.setFont(Global.textFont);
        tblOutput.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblOutput.setRowHeight(Global.tblRowHeight);
        tblOutput.setShowHorizontalLines(true);
        tblOutput.setShowVerticalLines(true);
        tblOutput.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblOutputMouseClicked(evt);
            }
        });
        tblOutput.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblOutputKeyReleased(evt);
            }
        });
        jScrollPane4.setViewportView(tblOutput);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setText("Load Qty : ");

        txtLoadQty.setEditable(false);
        txtLoadQty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtLoadQtyActionPerformed(evt);
            }
        });

        jLabel7.setText("Load Weight : ");

        txtLoadWeight.setEditable(false);

        jLabel8.setText("Load Amt : ");

        txtLoadAmt.setEditable(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtLoadAmt)
                    .addComponent(txtLoadQty)
                    .addComponent(txtLoadWeight, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtLoadQty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtLoadWeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtLoadAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel11.setText("Output Qty : ");

        jLabel12.setText("Output Weight :");

        jLabel13.setText("Output Amt :");

        txtOutputAmt.setEditable(false);

        txtOutputWeight.setEditable(false);

        txtOutputQty.setEditable(false);

        jLabel16.setText("Diff Weight :");

        txtDiffWeight.setEditable(false);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtOutputAmt, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtOutputWeight, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtOutputQty)
                    .addComponent(txtDiffWeight))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtOutputQty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtOutputWeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtOutputAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDiffWeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jScrollPane5.setViewportView(tblExpense);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel14.setText("Load Expense :");

        txtLoadExpense.setEditable(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtLoadExpense)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(txtLoadExpense, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(36, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panelSale, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 598, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(sbPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelSale, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(67, 67, 67))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sbPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(12, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGap(12, 12, 12))))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        observer.selected("control", this);
    }//GEN-LAST:event_formComponentShown

    private void txtCusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCusActionPerformed
        //inventoryRepo.getCustomer().subscribe()
    }//GEN-LAST:event_txtCusActionPerformed

    private void txtCusFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCusFocusGained
        txtCus.selectAll();
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCusFocusGained

    private void txtRemarkFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRemarkFocusGained

        // TODO add your handling code here:
    }//GEN-LAST:event_txtRemarkFocusGained

    private void tblRawMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblRawMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tblRawMouseClicked

    private void tblRawKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblRawKeyReleased
        // TODO add your handling code here:

    }//GEN-LAST:event_tblRawKeyReleased

    private void txtCurrencyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCurrencyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCurrencyActionPerformed

    private void txtReferenceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtReferenceFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtReferenceFocusGained

    private void formPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_formPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_formPropertyChange

    private void txtSaleDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSaleDateFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSaleDateFocusGained

    private void txtProjectNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProjectNoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtProjectNoActionPerformed

    private void txtCusMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCusMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCusMouseExited

    private void txtSaleDatePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtSaleDatePropertyChange
    }//GEN-LAST:event_txtSaleDatePropertyChange

    private void tblOutputMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblOutputMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tblOutputMouseClicked

    private void tblOutputKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblOutputKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_tblOutputKeyReleased

    private void txtLoadQtyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtLoadQtyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtLoadQtyActionPerformed

    @Override
    public void keyEvent(KeyEvent e) {

    }

    @Override
    public void selected(Object source, Object selectObj) {
        switch (source.toString()) {
            case "TRADER" -> {
            }
            case "SALE-TOTAL" ->
                calculateMilling();
            case "SALE-TOTAL-OUT" ->
                calculateMilling();
            case "Location" ->
                setAllLocation();
            case "SALE-HISTORY" -> {
                if (selectObj instanceof VSale s) {
                    boolean local = s.isLocal();
//                    inventoryRepo.findSale(s.getVouNo(), s.getDeptId(), local).subscribe((t) -> {
//                        t.setLocal(local);
//                        setSaleVoucher(t);
//                    }, (e) -> {
//                        JOptionPane.showMessageDialog(this, e.getMessage());
//                    });
                }
            }
            case "ORDER-HISTORY" -> {
                VOrder s = (VOrder) selectObj;
                inventoryRepo.findOrder(s.getVouNo(), s.getDeptId(), s.isLocal()).subscribe((t) -> {
                    setSaleVoucherDetail(t, s.isLocal());
                }, (e) -> {
                    JOptionPane.showMessageDialog(this, e.getMessage());
                });
            }
            case "Select" -> {
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
    public void keyReleased(KeyEvent e
    ) {
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
                    if (date.length() == 8 || date.length() == 6) {
                        txtSaleDate.setDate(Util1.convertToDate(Util1.formatDate(date)));
                    }
                    txtCus.requestFocus();
                }
            }
            case "txtCurrency" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtRemark.requestFocus();
                }
            }
            case "txtVouTaxP" -> {
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
    private javax.swing.JCheckBox chkSummary;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPanel panelSale;
    private javax.swing.JPanel sbPanel;
    private javax.swing.JProgressBar sbProgress;
    private javax.swing.JTable tblExpense;
    private javax.swing.JTable tblOutput;
    private javax.swing.JTable tblRaw;
    private javax.swing.JTable tblStockBalance;
    private javax.swing.JTextField txtCurrency;
    private javax.swing.JTextField txtCus;
    private javax.swing.JFormattedTextField txtDiffWeight;
    private javax.swing.JFormattedTextField txtLoadAmt;
    private javax.swing.JFormattedTextField txtLoadExpense;
    private javax.swing.JFormattedTextField txtLoadQty;
    private javax.swing.JFormattedTextField txtLoadWeight;
    private javax.swing.JFormattedTextField txtOutputAmt;
    private javax.swing.JFormattedTextField txtOutputQty;
    private javax.swing.JFormattedTextField txtOutputWeight;
    private javax.swing.JTextField txtProjectNo;
    private javax.swing.JTextField txtReference;
    private javax.swing.JTextField txtRemark;
    private com.toedter.calendar.JDateChooser txtSaleDate;
    private javax.swing.JFormattedTextField txtVouNo;
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
        clear();
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
    }

    @Override
    public String panelName() {
        return this.getName();
    }

    private void expenseDialog() {
        ExpenseSetupDialog d = new ExpenseSetupDialog(Global.parentForm);
        d.setInventoryRepo(inventoryRepo);
        d.setAccountRepo(accountRepo);
        d.initMain();
        d.setLocationRelativeTo(null);
        d.setVisible(true);
    }
}