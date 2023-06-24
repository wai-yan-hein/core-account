/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry;

import com.common.DecimalFormatRender;
import com.common.Global;
import com.common.JasperReportUtil;
import com.common.KeyPropagate;
import com.common.PanelControl;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.common.Util1;
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.editor.LocationCellEditor;
import com.inventory.editor.SaleManAutoCompleter;
import com.inventory.editor.StockCellEditor;
import com.inventory.editor.TraderAutoCompleter;
import com.inventory.model.Location;
import com.inventory.model.Region;
import com.inventory.model.Order;
import com.inventory.model.OrderHis;
import com.inventory.model.OrderHisDetail;
import com.inventory.model.OrderHisKey;
import com.inventory.model.OrderStatusType;
import com.inventory.model.SaleMan;
import com.inventory.model.StockUnit;
import com.inventory.model.Trader;
import com.inventory.model.VOrder;
import com.inventory.ui.common.InventoryRepo;
import com.inventory.ui.common.OrderTableModel;
import com.inventory.ui.common.StockBalanceTableModel;
import com.inventory.ui.entry.dialog.OrderHistoryDialog;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.inventory.ui.setup.dialog.common.StockUnitEditor;
import com.toedter.calendar.JTextFieldDateEditor;
import com.user.common.UserRepo;
import com.user.editor.CurrencyAutoCompleter;
import com.user.editor.ProjectAutoCompleter;
import com.user.model.Project;
import com.user.model.ProjectKey;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
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
public class OrderEntry extends javax.swing.JPanel implements SelectionObserver, KeyListener, KeyPropagate, PanelControl {

    private List<OrderHisDetail> listDetail = new ArrayList();
    private final OrderTableModel orderTableModel = new OrderTableModel();
    private OrderHistoryDialog dialog;
    private final StockBalanceTableModel stockBalanceTableModel = new StockBalanceTableModel();
    @Autowired
    private InventoryRepo inventoryRepo;
    @Autowired
    private UserRepo userRepo;
    private CurrencyAutoCompleter currAutoCompleter;
    private TraderAutoCompleter traderAutoCompleter;
    private SaleManAutoCompleter saleManCompleter;
    private LocationAutoCompleter locationAutoCompleter;
    private ProjectAutoCompleter projectAutoCompleter;
    private SelectionObserver observer;
    private OrderHis orderHis = new OrderHis();
    private Region region;
    private String orderCode;
    private JProgressBar progress;
    private Mono<List<Location>> monoLoc;
    private double prvBal = 0;
    private double balance = 0;

    public TraderAutoCompleter getTraderAutoCompleter() {
        return traderAutoCompleter;
    }

    public void setTraderAutoCompleter(TraderAutoCompleter traderAutoCompleter) {
        this.traderAutoCompleter = traderAutoCompleter;
    }

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
     * Creates new form SaleEntry1
     */
    public OrderEntry() {
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
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        tblOrder.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, solve);
        tblOrder.getActionMap().put(solve, new DeleteAction());

    }

    private class DeleteAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            deleteTran();
        }
    }

    private void initDateListner() {
        txtOrderDate.getDateEditor().getUiComponent().setName("txtSaleDate");
        txtOrderDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtOrderDate.getDateEditor().getUiComponent().addFocusListener(fa);
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

    private void initButtonGroup() {
        ButtonGroup g = new ButtonGroup();
        g.add(chkVou);
        g.add(chkA4);
        g.add(chkA5);
    }

    public void initMain() {
        initCombo();
        initStockBalanceTable();
        initOrderTable();
        assignDefaultValue();
        txtOrderDate.setDate(Util1.getTodayDate());
        txtCus.requestFocus();
    }

    private void initOrderTable() {
        tblOrder.setModel(orderTableModel);
        orderTableModel.setParent(tblOrder);
        orderTableModel.setLblRecord(lblRec);
        orderTableModel.setOrderEntry(this);
        orderTableModel.addNewRow();
        orderTableModel.setSelectionObserver(this);
        orderTableModel.setVouDate(txtOrderDate);
        orderTableModel.setInventoryRepo(inventoryRepo);
        orderTableModel.setSbTableModel(stockBalanceTableModel);
        tblOrder.getTableHeader().setFont(Global.tblHeaderFont);
        tblOrder.setCellSelectionEnabled(true);
        tblOrder.getColumnModel().getColumn(0).setPreferredWidth(50);//Code
        tblOrder.getColumnModel().getColumn(1).setPreferredWidth(450);//Name
        tblOrder.getColumnModel().getColumn(2).setPreferredWidth(60);//Rel
        tblOrder.getColumnModel().getColumn(3).setPreferredWidth(60);//Location
        tblOrder.getColumnModel().getColumn(4).setPreferredWidth(50);//weight
        tblOrder.getColumnModel().getColumn(5).setPreferredWidth(30);//unit
        tblOrder.getColumnModel().getColumn(6).setPreferredWidth(50);//qty
        tblOrder.getColumnModel().getColumn(7).setPreferredWidth(30);//unit
        tblOrder.getColumnModel().getColumn(8).setPreferredWidth(50);//total
        tblOrder.getColumnModel().getColumn(9).setPreferredWidth(50);//price
        tblOrder.getColumnModel().getColumn(10).setPreferredWidth(60);//amt
        tblOrder.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor(inventoryRepo));
        tblOrder.getColumnModel().getColumn(1).setCellEditor(new StockCellEditor(inventoryRepo));
        monoLoc.subscribe((t) -> {
            tblOrder.getColumnModel().getColumn(3).setCellEditor(new LocationCellEditor(t));
        });
        Mono<List<StockUnit>> monoUnit = inventoryRepo.getStockUnit();
        tblOrder.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());//weight
        monoUnit.subscribe((t) -> {
            tblOrder.getColumnModel().getColumn(5).setCellEditor(new StockUnitEditor(t));//unit
        });
        tblOrder.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());//qty
        inventoryRepo.getStockUnit().subscribe((t) -> {
            tblOrder.getColumnModel().getColumn(5).setCellEditor(new StockUnitEditor(t));
        });
        tblOrder.getColumnModel().getColumn(6).setCellEditor(new AutoClearEditor());//
        monoUnit.subscribe((t) -> {
            tblOrder.getColumnModel().getColumn(7).setCellEditor(new StockUnitEditor(t));//unit
        });
        tblOrder.getColumnModel().getColumn(8).setCellEditor(new AutoClearEditor());//wt
        tblOrder.getColumnModel().getColumn(9).setCellEditor(new AutoClearEditor());//price
        tblOrder.getColumnModel().getColumn(10).setCellEditor(new AutoClearEditor());//
        tblOrder.setDefaultRenderer(Object.class, new DecimalFormatRender());
        tblOrder.setDefaultRenderer(Float.class, new DecimalFormatRender());
        tblOrder.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblOrder.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void initCombo() {
        traderAutoCompleter = new TraderAutoCompleter(txtCus, inventoryRepo, null, false, "CUS");
        traderAutoCompleter.setObserver(this);
        monoLoc = inventoryRepo.getLocation();
        monoLoc.subscribe((t) -> {
            locationAutoCompleter = new LocationAutoCompleter(txtLocation, t, null, false, false);
            locationAutoCompleter.setObserver(this);
            inventoryRepo.getDefaultLocation().subscribe((tt) -> {
                locationAutoCompleter.setLocation(tt);
            });
        });
        userRepo.getCurrency().subscribe((t) -> {
            currAutoCompleter = new CurrencyAutoCompleter(txtCurrency, t, null);
            currAutoCompleter.setObserver(this);
            userRepo.getDefaultCurrency().subscribe((tt) -> {
                currAutoCompleter.setCurrency(tt);
            });
        });
        inventoryRepo.getSaleMan().subscribe((t) -> {
            saleManCompleter = new SaleManAutoCompleter(txtSaleman, t, null, false, false);
        });
        projectAutoCompleter = new ProjectAutoCompleter(txtProjectNo, userRepo, null, false);
        projectAutoCompleter.setObserver(this);
    }

    private void initKeyListener() {
        txtOrderDate.getDateEditor().getUiComponent().setName("txtOrderDate");
        txtOrderDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtDueDate.getDateEditor().getUiComponent().setName("txtDueDate");
        txtDueDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtVouNo.addKeyListener(this);
        txtRemark.addKeyListener(this);
        txtCus.addKeyListener(this);
        txtLocation.addKeyListener(this);
        txtSaleman.addKeyListener(this);
        txtCurrency.addKeyListener(this);
        tblOrder.addKeyListener(this);
        txtVouDiscP.addKeyListener(this);
        txtVouDiscount.addKeyListener(this);
        txtTax.addKeyListener(this);
        txtVouTaxP.addKeyListener(this);
        txtVouPaid.addKeyListener(this);
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
        txtVouBalance.setFormatterFactory(Util1.getDecimalFormat());
        txtVouDiscount.setFormatterFactory(Util1.getDecimalFormat());
        txtVouPaid.setFormatterFactory(Util1.getDecimalFormat());
        txtVouTotal.setFormatterFactory(Util1.getDecimalFormat());
        txtVouDiscP.setFormatterFactory(Util1.getDecimalFormat());
        txtVouTaxP.setFormatterFactory(Util1.getDecimalFormat());
        txtGrandTotal.setFormatterFactory(Util1.getDecimalFormat());
        txtTax.setFormatterFactory(Util1.getDecimalFormat());
    }

    private void initStockBalanceTable() {
        if (ProUtil.isCalStock()) {
            stockBalanceTableModel.setInventoryRepo(inventoryRepo);
            tblStockBalance.setModel(stockBalanceTableModel);
            stockBalanceTableModel.setProgress(sbProgress);
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
        Mono<Trader> trader = inventoryRepo.findTrader(Global.hmRoleProperty.get("default.customer"), Global.deptId);
        trader.subscribe((t) -> {
            traderAutoCompleter.setTrader(t);
        });
        if (saleManCompleter != null) {
            inventoryRepo.getDefaultSaleMan().subscribe((tt) -> {
                saleManCompleter.setSaleMan(tt);
            });
        }
        txtDueDate.setDate(null);
        progress.setIndeterminate(false);
        txtCurrency.setEnabled(ProUtil.isMultiCur());
        txtVouNo.setText(null);
        chkPaid.setSelected(ProUtil.isSalePaid());
        chkVou.setSelected(true);
        chkA4.setSelected(Util1.getBoolean(ProUtil.getProperty("check.sale.A4")));
        chkA5.setSelected(Util1.getBoolean(ProUtil.getProperty("check.sale.A5")));
        if (!lblStatus.getText().equals("NEW")) {
            txtOrderDate.setDate(Util1.getTodayDate());
        }
    }

    private void clear() {
        disableForm(true);
        orderTableModel.removeListDetail();
        orderTableModel.clearDelList();
        orderTableModel.setChange(false);
        stockBalanceTableModel.clearList();
        initTextBoxValue();
        assignDefaultValue();
        orderHis = new OrderHis();
        lblStatus.setText("NEW");
        lblStatus.setForeground(Color.GREEN);
        progress.setIndeterminate(false);
        txtRemark.setText(null);
        txtReference.setText(null);
        txtCus.requestFocus();
        projectAutoCompleter.setProject(null);
        cboOrderStatusType.setSelectedIndex(OrderStatusType.valueOf("Pending").ordinal());
    }

    public void saveOrder(boolean print) {
        if (isValidEntry() && orderTableModel.isValidEntry()) {
            progress.setIndeterminate(true);
            orderHis.setListSH(orderTableModel.getListDetail());
            orderHis.setListDel(orderTableModel.getDelList());
            orderHis.setBackup(orderTableModel.isChange());
            inventoryRepo.save(orderHis).subscribe((t) -> {
                clear();
                if (print) {
                    String reportName = "OrderVoucher";
                    printVoucher(t.getKey().getVouNo(), reportName, chkVou.isSelected());
                }
            }, (e) -> {
                JOptionPane.showMessageDialog(this, e.getMessage());
                progress.setIndeterminate(false);
                observer.selected("save", true);
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
        } else if (traderAutoCompleter.getTrader() == null) {
            JOptionPane.showMessageDialog(this, "Choose Trader.",
                    "No Trader.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtLocation.requestFocus();
        } else if (Util1.getFloat(txtVouTotal.getValue()) <= 0) {
            JOptionPane.showMessageDialog(this, "Invalid Amount.",
                    "No Sale Record.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtLocation.requestFocus();
        } else {
            orderHis.setCreditTerm(Util1.convertToLocalDateTime(txtDueDate.getDate()));
            SaleMan sm = saleManCompleter.getSaleMan();
            if (sm != null) {
                orderHis.setSaleManCode(sm.getKey().getSaleManCode());
            }
            orderHis.setRemark(txtRemark.getText());
            orderHis.setReference(txtReference.getText());
            orderHis.setDiscP(Util1.getFloat(txtVouDiscP.getValue()));
            orderHis.setDiscount(Util1.getFloat(txtVouDiscount.getValue()));
            orderHis.setTaxPercent(Util1.getFloat(txtVouTaxP.getValue()));
            orderHis.setTaxAmt(Util1.getFloat(txtTax.getValue()));
            orderHis.setPaid(Util1.getFloat(txtVouPaid.getValue()));
            orderHis.setBalance(Util1.getFloat(txtVouBalance.getValue()));
            orderHis.setCurCode(currAutoCompleter.getCurrency().getCurCode());
            orderHis.setDeleted(orderHis.isDeleted());
            orderHis.setLocCode(locationAutoCompleter.getLocation().getKey().getLocCode());
            orderHis.setTraderCode(traderAutoCompleter.getTrader().getKey().getCode());
            orderHis.setVouTotal(Util1.getFloat(txtVouTotal.getValue()));
            orderHis.setGrandTotal(Util1.getFloat(txtGrandTotal.getValue()));
            orderHis.setStatus(lblStatus.getText());
            orderHis.setVouDate(Util1.convertToLocalDateTime(txtOrderDate.getDate()));
            orderHis.setMacId(Global.macId);
            Project p = projectAutoCompleter.getProject();
            orderHis.setProjectNo(p == null ? null : p.getKey().getProjectNo());
            if (cboOrderStatusType.getSelectedItem() instanceof OrderStatusType type) {
                orderHis.setOrderStatus(type.name());
            }
            if (lblStatus.getText().equals("NEW")) {
                OrderHisKey key = new OrderHisKey();
                key.setCompCode(Global.compCode);
                key.setDeptId(Global.deptId);
                key.setVouNo(null);
                orderHis.setKey(key);
                orderHis.setCreatedDate(Util1.getTodayDate());
                orderHis.setCreatedBy(Global.loginUser.getUserCode());
//                orderHis.setSession(Global.sessionId);
            } else {
                orderHis.setUpdatedBy(Global.loginUser.getUserCode());
            }
        }
        return status;
    }

    private void deleteOrder() {
        String status = lblStatus.getText();
        switch (status) {
            case "EDIT" -> {
                int yes_no = JOptionPane.showConfirmDialog(this,
                        "Are you sure to delete?", "Save Order Vocher Delete.", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                if (yes_no == 0) {
                    inventoryRepo.delete(orderHis.getKey()).subscribe((t) -> {
                        clear();
                    });
                }
            }
            case "DELETED" -> {
                int yes_no = JOptionPane.showConfirmDialog(this,
                        "Are you sure to restore?", "Order Voucher Restore.", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (yes_no == 0) {
                    orderHis.setDeleted(false);
                    inventoryRepo.restore(orderHis.getKey()).subscribe((t) -> {
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
        int row = tblOrder.convertRowIndexToModel(tblOrder.getSelectedRow());
        if (row >= 0) {
            if (tblOrder.getCellEditor() != null) {
                tblOrder.getCellEditor().stopCellEditing();
            }
            int yes_no = JOptionPane.showConfirmDialog(this,
                    "Are you sure to delete?", "Order Transaction delete.", JOptionPane.YES_NO_OPTION);
            if (yes_no == 0) {
                orderTableModel.delete(row);
                calculateTotalAmount(false);
            }
        }
    }

    private void calculateTotalAmount(boolean partial) {
        float totalVouBalance;
        float totalAmount = 0.0f;
        listDetail = orderTableModel.getListDetail();
        totalAmount = listDetail.stream().map(sdh -> Util1.getFloat(sdh.getAmount())).reduce(totalAmount, (accumulator, _item) -> accumulator + _item);
        txtVouTotal.setValue(totalAmount);
        //cal discAmt
        float discp = Util1.getFloat(txtVouDiscP.getValue());
        if (discp > 0) {
            float discountAmt = (totalAmount * (discp / 100));
            txtVouDiscount.setValue(Util1.getFloat(discountAmt));
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
        //
        txtGrandTotal.setValue(totalAmount
                + Util1.getFloat(txtTax.getValue())
                - Util1.getFloat(txtVouDiscount.getValue()));
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
        if (paid > grandTotal) {
            txtVouPaid.setValue(grandTotal);
            paid = grandTotal;
        }
        totalVouBalance = grandTotal - paid;
        txtVouBalance.setValue(Util1.getFloat(totalVouBalance));
    }

    public void historyOrder() {
        if (dialog == null) {
            dialog = new OrderHistoryDialog(Global.parentForm);
            dialog.setInventoryRepo(inventoryRepo);
            dialog.setUserRepo(userRepo);
            dialog.setObserver(this);
            dialog.initMain();
            dialog.setSize(Global.width - 100, Global.height - 100);
            dialog.setLocationRelativeTo(null);
        }
        dialog.search();
    }

    public void setOrderVoucher(OrderHis sh, boolean local) {
        if (sh != null) {
            progress.setIndeterminate(true);
            orderHis = sh;
            Integer deptId = sh.getKey().getDeptId();
            String vouNo = sh.getKey().getVouNo();
            inventoryRepo.findLocation(orderHis.getLocCode()).subscribe((t) -> {
                locationAutoCompleter.setLocation(t);
            });
            Mono<Trader> trader = inventoryRepo.findTrader(orderHis.getTraderCode(), deptId);
            trader.subscribe((t) -> {
                traderAutoCompleter.setTrader(t);
            });
            userRepo.findCurrency(orderHis.getCurCode()).subscribe((t) -> {
                currAutoCompleter.setCurrency(t);
            });
            inventoryRepo.findSaleMan(orderHis.getSaleManCode(), deptId).subscribe((t) -> {
                saleManCompleter.setSaleMan(t);
            });
            if (orderHis.getProjectNo() != null) {
                userRepo.find(new ProjectKey(orderHis.getProjectNo(), Global.compCode)).subscribe(t1 -> {
                    projectAutoCompleter.setProject(t1);
                });
            } else {
                projectAutoCompleter.setProject(null);
            }            
            cboOrderStatusType.setSelectedIndex(OrderStatusType.valueOf(orderHis.getOrderStatus()).ordinal());
            inventoryRepo.getOrderDetail(vouNo, sh.getKey().getDeptId(), local)
                    .subscribe((t) -> {
                        orderTableModel.setListDetail(t);
                        orderTableModel.addNewRow();
                        if (sh.isVouLock()) {
                            lblStatus.setText("Voucher is locked.");
                            lblStatus.setForeground(Color.RED);
                            disableForm(false);
                        } else if (!ProUtil.isSaleEdit()) {
                            lblStatus.setText("No Permission.");
                            lblStatus.setForeground(Color.RED);
                            disableForm(false);
                            observer.selected("print", true);
                        } else if (sh.isDeleted()) {
                            lblStatus.setText("DELETED");
                            lblStatus.setForeground(Color.RED);
                            disableForm(false);
                            observer.selected("delete", true);
                        } else {
                            lblStatus.setText("EDIT");
                            lblStatus.setForeground(Color.blue);
                            disableForm(true);
                        }
                        txtVouNo.setText(orderHis.getKey().getVouNo());
                        txtDueDate.setDate(Util1.convertToDate(orderHis.getCreditTerm()));
                        txtRemark.setText(orderHis.getRemark());
                        txtReference.setText(orderHis.getReference());
                        txtOrderDate.setDate(Util1.convertToDate(orderHis.getVouDate()));
                        txtVouTotal.setValue(Util1.getFloat(orderHis.getVouTotal()));
                        txtVouDiscP.setValue(Util1.getFloat(orderHis.getDiscP()));
                        txtVouDiscount.setValue(Util1.getFloat(orderHis.getDiscount()));
                        txtVouTaxP.setValue(Util1.getFloat(orderHis.getTaxPercent()));
                        txtTax.setValue(Util1.getFloat(orderHis.getTaxAmt()));
                        txtVouPaid.setValue(Util1.getFloat(orderHis.getPaid()));
                        txtVouBalance.setValue(Util1.getFloat(orderHis.getBalance()));
                        txtGrandTotal.setValue(Util1.getFloat(orderHis.getGrandTotal()));                          
                        chkPaid.setSelected(orderHis.getPaid() > 0);
                        focusTable();
                        progress.setIndeterminate(false);
                    }, (e) -> {
                        progress.setIndeterminate(false);
                        JOptionPane.showMessageDialog(this, e.getMessage());
                    });
        }
    }

    private void disableForm(boolean status) {
        tblOrder.setEnabled(status);
        panelSale.setEnabled(status);
        txtOrderDate.setEnabled(status);
        txtCus.setEnabled(status);
        txtLocation.setEnabled(status);
        txtSaleman.setEnabled(status);
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
        observer.selected("save", status);
        observer.selected("delete", status);
        observer.selected("print", status);

    }

    private void setAllLocation() {
        List<OrderHisDetail> listSaleDetail = orderTableModel.getListDetail();
        Location loc = locationAutoCompleter.getLocation();
        if (listSaleDetail != null) {
            listSaleDetail.forEach(sd -> {
                sd.setLocCode(loc.getKey().getLocCode());
                sd.setLocName(loc.getLocName());
            });
        }
        orderTableModel.setListDetail(listSaleDetail);
    }

    private void printVoucher(String vouNo, String reportName, boolean print) {
        clear();
        inventoryRepo.getOrderReport(vouNo).subscribe((t) -> {
            viewReport(t, reportName, print);
        }, (e) -> {
            JOptionPane.showMessageDialog(this, e.getMessage());
        });

    }

    private void viewReport(byte[] t, String reportName, boolean print) {
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
                if (print) {
                    JasperReportUtil.print(jp);
                } else {
                    JasperViewer.viewReport(jp, false);
                }
            } catch (JRException ex) {
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

    private void searchOrder(Order order) {
        traderAutoCompleter.setTrader(order.getTrader());
        txtRemark.setText(order.getDesp());
        orderCode = order.getOrderCode();
        lblStatus.setText("NEW");

    }

    private void focusTable() {
        int rc = tblOrder.getRowCount();
        if (rc >= 1) {
            tblOrder.setRowSelectionInterval(rc - 1, rc - 1);
            tblOrder.setColumnSelectionInterval(0, 0);
            tblOrder.requestFocus();
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
        jLabel3 = new javax.swing.JLabel();
        txtSaleman = new javax.swing.JTextField();
        txtVouNo = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtDueDate = new com.toedter.calendar.JDateChooser();
        txtOrderDate = new com.toedter.calendar.JDateChooser();
        txtCurrency = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        txtLocation = new javax.swing.JTextField();
        txtReference = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtProjectNo = new javax.swing.JTextField();
        cboOrderStatusType = new javax.swing.JComboBox<>(OrderStatusType.values());
        jLabel11 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        lblStatus = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        chkA5 = new javax.swing.JCheckBox();
        chkVou = new javax.swing.JCheckBox();
        chkA4 = new javax.swing.JCheckBox();
        lblRec = new javax.swing.JLabel();
        lblRec1 = new javax.swing.JLabel();
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
        sbPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblStockBalance = new javax.swing.JTable();
        sbProgress = new javax.swing.JProgressBar();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblOrder = new javax.swing.JTable();

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

        txtVouNo.setEditable(false);
        txtVouNo.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtVouNo.setFont(Global.textFont);
        txtVouNo.setName("txtVouNo"); // NOI18N

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Order Date");

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Credit Term");

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Currency");

        txtDueDate.setDateFormatString("dd/MM/yyyy");
        txtDueDate.setFont(Global.textFont);

        txtOrderDate.setDateFormatString("dd/MM/yyyy");
        txtOrderDate.setFont(Global.textFont);
        txtOrderDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtOrderDateFocusGained(evt);
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

        cboOrderStatusType.setFont(Global.textFont);

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Order Status");

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
                    .addComponent(txtOrderDate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                    .addComponent(txtVouNo, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtRemark, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                    .addComponent(txtSaleman)
                    .addComponent(txtLocation))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtDueDate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                    .addComponent(txtCurrency, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtReference))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cboOrderStatusType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtProjectNo, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE))
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
                            .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel22)
                            .addComponent(txtLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6)
                            .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10)
                            .addComponent(txtProjectNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtDueDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtOrderDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel3)
                                .addComponent(txtSaleman, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel5))
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel11)
                        .addComponent(cboOrderStatusType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtCus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel21)
                        .addComponent(jLabel2))
                    .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtReference, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelSaleLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel3, jLabel5});

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

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

        lblRec1.setFont(Global.lableFont);
        lblRec1.setText("Records");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblRec, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE))
                    .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGap(69, 69, 69)
                    .addComponent(lblRec1, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
                    .addGap(70, 70, 70)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRec))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblStatus)
                .addContainerGap())
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGap(88, 88, 88)
                    .addComponent(lblRec1)
                    .addContainerGap(88, Short.MAX_VALUE)))
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

        javax.swing.GroupLayout sbPanelLayout = new javax.swing.GroupLayout(sbPanel);
        sbPanel.setLayout(sbPanelLayout);
        sbPanelLayout.setHorizontalGroup(
            sbPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sbPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sbPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(sbProgress, javax.swing.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE))
                .addContainerGap())
        );
        sbPanelLayout.setVerticalGroup(
            sbPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sbPanelLayout.createSequentialGroup()
                .addComponent(sbProgress, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        tblOrder.setFont(Global.textFont);
        tblOrder.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblOrder.setRowHeight(Global.tblRowHeight);
        tblOrder.setShowHorizontalLines(true);
        tblOrder.setShowVerticalLines(true);
        tblOrder.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblOrderMouseClicked(evt);
            }
        });
        tblOrder.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblOrderKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblOrder);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sbPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1)
                            .addComponent(panelSale, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(6, 6, 6)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelSale, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sbPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void tblOrderMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblOrderMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tblOrderMouseClicked

    private void tblOrderKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblOrderKeyReleased
        // TODO add your handling code here:

    }//GEN-LAST:event_tblOrderKeyReleased

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
        log.info("change.");
    }//GEN-LAST:event_formPropertyChange

    private void txtOrderDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtOrderDateFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtOrderDateFocusGained

    private void txtProjectNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProjectNoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtProjectNoActionPerformed

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
            case "ORDER-TOTAL" ->
                calculateTotalAmount(false);
            case "Location" ->
                setAllLocation();
            case "ORDER" -> {
                Order od = (Order) selectObj;
                searchOrder(od);
            }
            case "ORDER-HISTORY" -> {
                if (selectObj instanceof VOrder s) {
                    inventoryRepo.findOrder(s.getVouNo(), s.getDeptId(), s.isLocal()).subscribe((t) -> {
                        setOrderVoucher(t, s.isLocal());
                    });
                }
            }
            case "Select" -> {
                calculateTotalAmount(false);
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
                    if (date.length() == 8 || date.length() == 6) {
                        txtOrderDate.setDate(Util1.convertToDate(Util1.formatDate(date)));
                    }
                    txtCus.requestFocus();
                }
            }
            case "txtDueDate" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String date = ((JTextFieldDateEditor) sourceObj).getText();
                    if (date.length() == 8 || date.length() == 6) {
                        txtDueDate.setDate(Util1.convertToDate(Util1.formatDate(date)));
                    }
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
                    if (Util1.getFloat(txtVouTaxP.getValue()) <= 0) {
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
                    if (Util1.getFloat(txtVouDiscount.getValue()) >= 0) {
                        txtVouDiscP.setValue(0);
                    }
                    calculateTotalAmount(false);
                    focusTable();
                }
            }
            case "txtVouDiscP" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (Util1.getFloat(txtVouDiscP.getValue()) <= 0) {
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<OrderStatusType> cboOrderStatusType;
    private javax.swing.JCheckBox chkA4;
    private javax.swing.JCheckBox chkA5;
    private javax.swing.JCheckBox chkPaid;
    private javax.swing.JCheckBox chkVou;
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
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblRec;
    private javax.swing.JLabel lblRec1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPanel panelSale;
    private javax.swing.JPanel sbPanel;
    private javax.swing.JProgressBar sbProgress;
    private javax.swing.JTable tblOrder;
    private javax.swing.JTable tblStockBalance;
    private javax.swing.JTextField txtCurrency;
    private javax.swing.JTextField txtCus;
    private com.toedter.calendar.JDateChooser txtDueDate;
    private javax.swing.JFormattedTextField txtGrandTotal;
    private javax.swing.JTextField txtLocation;
    private com.toedter.calendar.JDateChooser txtOrderDate;
    private javax.swing.JTextField txtProjectNo;
    private javax.swing.JTextField txtReference;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JTextField txtSaleman;
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
        deleteOrder();
    }

    @Override
    public void print() {
        saveOrder(true);
    }

    @Override
    public void save() {
        saveOrder(false);
    }

    @Override
    public void newForm() {
        clear();
    }

    @Override
    public void history() {
        historyOrder();
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
