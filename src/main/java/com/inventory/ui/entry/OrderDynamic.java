/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry;

import com.acc.dialog.FindDialog;
import com.common.ComponentUtil;
import com.common.DateLockUtil;
import com.common.DecimalFormatRender;
import com.common.Global;
import com.common.JasperReportUtil;
import com.common.KeyPropagate;
import com.common.PanelControl;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.common.Util1;
import com.common.YNOptionPane;
import com.inventory.editor.DesignEditor;
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.editor.LocationCellEditor;
import com.inventory.editor.OrderStatusAutoCompleter;
import com.inventory.editor.SaleManAutoCompleter;
import com.inventory.editor.SizeEditor;
import com.inventory.editor.StockCellEditor;
import com.inventory.editor.TraderAutoCompleter;
import com.inventory.entity.Location;
import com.inventory.entity.Order;
import com.inventory.entity.OrderHis;
import com.inventory.entity.OrderHisDetail;
import com.inventory.entity.OrderHisKey;
import com.inventory.entity.OrderStatus;
import com.inventory.entity.SaleMan;
import com.inventory.entity.Trader;
import com.repo.InventoryRepo;
import com.inventory.ui.common.OrderTableModel;
import com.inventory.ui.entry.dialog.OrderHistoryDialog;
import com.user.editor.AutoClearEditor;
import com.inventory.editor.StockUnitEditor;
import com.inventory.ui.common.OrderDesginTableModel;
import com.inventory.ui.common.PurchaseOrderTableModel;
import com.inventory.ui.entry.dialog.StockBalanceFrame;
import com.toedter.calendar.JTextFieldDateEditor;
import com.repo.UserRepo;
import com.user.editor.CurrencyAutoCompleter;
import com.user.editor.ProjectAutoCompleter;
import com.user.model.Project;
import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.view.JasperViewer;
import org.springframework.web.reactive.function.client.WebClientRequestException;

/**
 *
 * @author wai yan
 */
@Slf4j
public class OrderDynamic extends javax.swing.JPanel implements SelectionObserver, KeyListener, KeyPropagate, PanelControl {

    public static final int ORDER = 1;
    public static final int PUR_ORDER = 2;
    public static final int DESIGN = 3;
    private final OrderTableModel orderTableModel = new OrderTableModel();
    private final PurchaseOrderTableModel purchaseOrderTableModel = new PurchaseOrderTableModel();
    private final OrderDesginTableModel orderDesginTableModel = new OrderDesginTableModel();
    private OrderHistoryDialog dialog;
    @Setter
    private InventoryRepo inventoryRepo;
    @Setter
    private UserRepo userRepo;
    @Setter
    private StockBalanceFrame stockBalanceDialog;
    private CurrencyAutoCompleter currAutoCompleter;
    private TraderAutoCompleter traderAutoCompleter;
    private SaleManAutoCompleter saleManCompleter;
    @Getter
    private LocationAutoCompleter locationAutoCompleter;
    private ProjectAutoCompleter projectAutoCompleter;
    private OrderStatusAutoCompleter orderStatusCompleter;
    @Setter
    private SelectionObserver observer;
    private OrderHis oh = new OrderHis();
    @Setter
    private JProgressBar progress;
    private int type;
    private FindDialog findDialog;

    public OrderDynamic(int type) {
        this.type = type;
        initComponents();
        initKeyListener();
        initTextBoxFormat();
        initTextBoxValue();
        initDateListner();
        actionMapping();
    }

    /**
     * Creates new form OrderDynamic
     */
    public OrderDynamic() {
        initComponents();
        lblStatus.setForeground(Color.GREEN);
        initKeyListener();
        initTextBoxFormat();
        initTextBoxValue();
        initDateListner();
        actionMapping();
        initFocus();
    }

    private void initFocus() {
        ComponentUtil.addFocusListener(this);
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
        txtDueDate.getDateEditor().getUiComponent().setName("txtDueDate");
        txtDueDate.getDateEditor().getUiComponent().addKeyListener(this);
    }

    public void initMain() {
        initStockBalance();
        initCombo();
        initTable();
        initModel();
        assignDefaultValue();
        initFind();
        txtOrderDate.setDate(Util1.getTodayDate());
        txtCus.requestFocus();
    }

    private void initFind() {
        findDialog = new FindDialog(Global.parentForm, tblOrder);
    }

    private void initStockBalance() {
        stockBalanceDialog.setVisible(true);
        deskPane.add(stockBalanceDialog);
    }

    private void initModel() {
        switch (type) {
            case ORDER ->
                initOrderTable();
            case PUR_ORDER ->
                initPurchaseOrderTable();
            case DESIGN ->
                initOrderDesign();
        }
    }

    private void initOrderDesign() {
        tblOrder.setModel(orderDesginTableModel);
        orderDesginTableModel.setParent(tblOrder);
        orderDesginTableModel.setLblRecord(lblRec);
        orderDesginTableModel.setOrderDynamic(this);
        orderDesginTableModel.addNewRow();
        orderDesginTableModel.setObserver(this);
        tblOrder.getColumnModel().getColumn(0).setPreferredWidth(300);//Description
        tblOrder.getColumnModel().getColumn(1).setPreferredWidth(100);//Size
        tblOrder.getColumnModel().getColumn(2).setPreferredWidth(100);//Order Qty
        tblOrder.getColumnModel().getColumn(3).setPreferredWidth(100);//Heat Press Qty
        tblOrder.getColumnModel().getColumn(0).setCellEditor(new DesignEditor(inventoryRepo));
        tblOrder.getColumnModel().getColumn(1).setCellEditor(new SizeEditor(inventoryRepo));
        tblOrder.getColumnModel().getColumn(2).setCellEditor(new AutoClearEditor());
        tblOrder.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());
    }

    private void initPurchaseOrderTable() {
        tblOrder.setModel(purchaseOrderTableModel);
        purchaseOrderTableModel.setParent(tblOrder);
        purchaseOrderTableModel.setLblRecord(lblRec);
        purchaseOrderTableModel.setOrderDynamic(this);
        purchaseOrderTableModel.addNewRow();
        purchaseOrderTableModel.setObserver(this);
        tblOrder.getColumnModel().getColumn(0).setPreferredWidth(50);//Code
        tblOrder.getColumnModel().getColumn(1).setPreferredWidth(450);//Name
        tblOrder.getColumnModel().getColumn(2).setPreferredWidth(60);//Rel
        tblOrder.getColumnModel().getColumn(3).setPreferredWidth(60);//Location
        tblOrder.getColumnModel().getColumn(4).setPreferredWidth(50);//weight
        tblOrder.getColumnModel().getColumn(5).setPreferredWidth(30);//unit
        tblOrder.getColumnModel().getColumn(6).setPreferredWidth(50);//o-qty
        tblOrder.getColumnModel().getColumn(7).setPreferredWidth(50);//qty
        tblOrder.getColumnModel().getColumn(8).setPreferredWidth(30);//unit
        tblOrder.getColumnModel().getColumn(9).setPreferredWidth(50);//price
        tblOrder.getColumnModel().getColumn(10).setPreferredWidth(50);//amt
        tblOrder.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor(inventoryRepo, ProUtil.isSSContain()));
        tblOrder.getColumnModel().getColumn(1).setCellEditor(new StockCellEditor(inventoryRepo, ProUtil.isSSContain()));
        tblOrder.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());//weight
        inventoryRepo.getStockUnit().doOnSuccess((t) -> {
            tblOrder.getColumnModel().getColumn(5).setCellEditor(new StockUnitEditor(t));//unit
            tblOrder.getColumnModel().getColumn(8).setCellEditor(new StockUnitEditor(t));//unit
        }).subscribe();
        inventoryRepo.getLocation().doOnSuccess((t) -> {
            tblOrder.getColumnModel().getColumn(3).setCellEditor(new LocationCellEditor(t));
        }).subscribe();
        tblOrder.getColumnModel().getColumn(6).setCellEditor(new AutoClearEditor());//
        tblOrder.getColumnModel().getColumn(7).setCellEditor(new AutoClearEditor());//
        tblOrder.getColumnModel().getColumn(9).setCellEditor(new AutoClearEditor());//wt
        tblOrder.setDefaultRenderer(Object.class, new DecimalFormatRender());
        tblOrder.setDefaultRenderer(Float.class, new DecimalFormatRender());
    }

    private void initTable() {
        tblOrder.getTableHeader().setFont(Global.tblHeaderFont);
        tblOrder.setCellSelectionEnabled(true);
        tblOrder.setDefaultRenderer(Object.class, new DecimalFormatRender());
        tblOrder.setDefaultRenderer(Double.class, new DecimalFormatRender());
        tblOrder.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblOrder.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void initOrderTable() {
        tblOrder.setModel(orderTableModel);
        orderTableModel.setInventoryRepo(inventoryRepo);
        orderTableModel.setParent(tblOrder);
        orderTableModel.setLblRecord(lblRec);
        orderTableModel.setOrderDynamic(this);
        orderTableModel.addNewRow();
        orderTableModel.setObserver(this);
        orderTableModel.setDialog(stockBalanceDialog);
        tblOrder.getColumnModel().getColumn(0).setPreferredWidth(50);//Code
        tblOrder.getColumnModel().getColumn(1).setPreferredWidth(450);//Name
        tblOrder.getColumnModel().getColumn(2).setPreferredWidth(60);//Rel
        tblOrder.getColumnModel().getColumn(3).setPreferredWidth(50);//o-qty
        tblOrder.getColumnModel().getColumn(4).setPreferredWidth(50);//qty
        tblOrder.getColumnModel().getColumn(5).setPreferredWidth(30);//unit
        tblOrder.getColumnModel().getColumn(6).setPreferredWidth(50);//price
        tblOrder.getColumnModel().getColumn(7).setPreferredWidth(50);//amt
        tblOrder.getColumnModel().getColumn(8).setPreferredWidth(50);//weight
        tblOrder.getColumnModel().getColumn(9).setPreferredWidth(30);//unit
        tblOrder.getColumnModel().getColumn(10).setPreferredWidth(60);//Location
        tblOrder.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor(inventoryRepo, ProUtil.isSSContain()));
        tblOrder.getColumnModel().getColumn(1).setCellEditor(new StockCellEditor(inventoryRepo, ProUtil.isSSContain()));
        tblOrder.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());//
        tblOrder.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());//
        inventoryRepo.getStockUnit().doOnSuccess((t) -> {
            tblOrder.getColumnModel().getColumn(5).setCellEditor(new StockUnitEditor(t));//unit
            tblOrder.getColumnModel().getColumn(9).setCellEditor(new StockUnitEditor(t));//unit
        }).subscribe();
        inventoryRepo.getLocation().doOnSuccess((t) -> {
            tblOrder.getColumnModel().getColumn(10).setCellEditor(new LocationCellEditor(t));
        }).subscribe();
        tblOrder.getColumnModel().getColumn(6).setCellEditor(new AutoClearEditor());//
        tblOrder.getColumnModel().getColumn(7).setCellEditor(new AutoClearEditor());//
        tblOrder.getColumnModel().getColumn(8).setCellEditor(new AutoClearEditor());//weight
        tblOrder.getColumnModel().getColumn(9).setCellEditor(new AutoClearEditor());//wt
    }

    private void initCombo() {
        traderAutoCompleter = new TraderAutoCompleter(txtCus, inventoryRepo, null, false, "CUS");
        traderAutoCompleter.setObserver(this);
        locationAutoCompleter = new LocationAutoCompleter(txtLocation, null, false, false);
        locationAutoCompleter.setObserver(this);
        inventoryRepo.getLocation().doOnSuccess((t) -> {
            locationAutoCompleter.setListLocation(t);
        }).subscribe();
        currAutoCompleter = new CurrencyAutoCompleter(txtCurrency, null);
        userRepo.getCurrency().doOnSuccess((t) -> {
            currAutoCompleter.setListCurrency(t);
        }).subscribe();
        saleManCompleter = new SaleManAutoCompleter(txtSaleman, null, false);
        inventoryRepo.getSaleMan().doOnSuccess((t) -> {
            saleManCompleter.setListSaleMan(t);
        }).subscribe();
        orderStatusCompleter = new OrderStatusAutoCompleter(txtOrderStatus, null, false);
        inventoryRepo.getOrderStatus().doOnSuccess((t) -> {
            orderStatusCompleter.setListData(t);
        }).subscribe();
        projectAutoCompleter = new ProjectAutoCompleter(txtProjectNo, null, false);
        projectAutoCompleter.setObserver(this);
        userRepo.searchProject().doOnSuccess((t) -> {
            projectAutoCompleter.setListProject(t);
        }).subscribe();
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
    }

    private void initTextBoxValue() {
        txtVouTotal.setValue(0);
    }

    private void initTextBoxFormat() {
        ComponentUtil.setTextProperty(this);
    }

    private void assignDefaultValue() {
        inventoryRepo.getDefaultCustomer().doOnSuccess((t) -> {
            traderAutoCompleter.setTrader(t);
        }).subscribe();
        inventoryRepo.getDefaultSaleMan().doOnSuccess((tt) -> {
            saleManCompleter.setSaleMan(tt);
        }).subscribe();
        userRepo.getDefaultCurrency().doOnSuccess((t) -> {
            currAutoCompleter.setCurrency(t);
        }).subscribe();
        inventoryRepo.getDefaultLocation().doOnSuccess((tt) -> {
            locationAutoCompleter.setLocation(tt);
        }).subscribe();
        txtDueDate.setDate(null);
        progress.setIndeterminate(false);
        txtCurrency.setEnabled(ProUtil.isMultiCur());
        txtVouNo.setText(null);
        if (!lblStatus.getText().equals("NEW")) {
            txtOrderDate.setDate(Util1.getTodayDate());
        }
        orderStatusCompleter.setOrderStatus(null);
        chkInvUpdate.setSelected(false);
    }

    private void clearDetail() {
        switch (type) {
            case ORDER ->
                orderTableModel.clear();
            case PUR_ORDER ->
                purchaseOrderTableModel.clear();
            case DESIGN ->
                orderDesginTableModel.clear();
        }
    }

    private void addNewRow() {
        switch (type) {
            case ORDER ->
                orderTableModel.addNewRow();
            case PUR_ORDER ->
                purchaseOrderTableModel.addNewRow();
            case DESIGN ->
                orderDesginTableModel.addNewRow();
        }
    }

    private void clear(boolean focus) {
        disableForm(true);
        clearDetail();
        addNewRow();
        initTextBoxValue();
        assignDefaultValue();
        oh = new OrderHis();
        lblStatus.setText("NEW");
        lblStatus.setForeground(Color.GREEN);
        progress.setIndeterminate(false);
        txtRemark.setText(null);
        txtReference.setText(null);
        txtRefNo.setText(null);
        projectAutoCompleter.setProject(null);
        if (focus) {
            txtCus.requestFocus();
        }
    }

    private boolean isValidDetail() {
        return switch (type) {
            case ORDER ->
                orderTableModel.isValidEntry();
            case PUR_ORDER ->
                purchaseOrderTableModel.isValidEntry();
            case DESIGN ->
                orderDesginTableModel.isValidEntry();
            default ->
                false;
        };
    }

    private void setListDetail(List<OrderHisDetail> list) {
        switch (type) {
            case ORDER ->
                orderTableModel.setListDetail(list);
            case PUR_ORDER ->
                purchaseOrderTableModel.setListDetail(list);
            case DESIGN ->
                orderDesginTableModel.setListDetail(list);
        }
    }

    private List<OrderHisDetail> getListDetail() {
        return switch (type) {
            case ORDER ->
                orderTableModel.getListDetail();
            case PUR_ORDER ->
                purchaseOrderTableModel.getListDetail();
            case DESIGN ->
                orderDesginTableModel.getListDetail();
            default ->
                null;
        };
    }

    public void saveOrder(boolean print) {
        if (isValidEntry() && isValidDetail()) {
            if (DateLockUtil.isLockDate(txtOrderDate.getDate())) {
                DateLockUtil.showMessage(this);
                txtOrderDate.requestFocus();
                return;
            }
            progress.setIndeterminate(true);
            oh.setListSH(getListDetail());
            inventoryRepo.save(oh).doOnSuccess((t) -> {
                if (print) {
                    printVoucher(t, print);
                } else {
                    clear(true);
                }
            }).doOnError((e) -> {
                progress.setIndeterminate(false);
                observeMain();
                if (e instanceof WebClientRequestException) {
                    int yn = JOptionPane.showConfirmDialog(this, "Internet Offline. Try Again?", "Offline", JOptionPane.YES_OPTION, JOptionPane.ERROR_MESSAGE);
                    if (yn == JOptionPane.YES_OPTION) {
                        saveOrder(print);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Error : " + e.getMessage(), "Server Error", JOptionPane.ERROR_MESSAGE);
                }
            }).subscribe();
        }
    }

    private boolean isValidEntry() {
        boolean status = true;
        if (lblStatus.getText().equals("DELETED")) {
            status = false;
            clear(false);
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
        } else if (!Util1.isDateBetween(txtOrderDate.getDate())) {
            JOptionPane.showMessageDialog(this, "Invalid Date.",
                    "Validation.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtOrderDate.requestFocus();
        } else {
            if (chkInvUpdate.isSelected()) {
                YNOptionPane pane = new YNOptionPane("Do you want to proceed with the inventory update for this order?", JOptionPane.WARNING_MESSAGE);
                JDialog ynDialog = pane.createDialog("Confirm Message");
                ynDialog.setVisible(true);
                int yn = (int) pane.getValue();
                if (yn == JOptionPane.NO_OPTION) {
                    return false;
                }
            }
            SaleMan sm = saleManCompleter.getSaleMan();
            Project p = projectAutoCompleter.getProject();
            oh.setCreditTerm(Util1.convertToLocalDateTime(txtDueDate.getDate()));
            oh.setSaleManCode(sm == null ? null : sm.getKey().getSaleManCode());
            oh.setRemark(txtRemark.getText());
            oh.setReference(txtReference.getText());
            oh.setCurCode(currAutoCompleter.getCurrency().getCurCode());
            oh.setDeleted(oh.isDeleted());
            oh.setLocCode(locationAutoCompleter.getLocation().getKey().getLocCode());
            oh.setTraderCode(traderAutoCompleter.getTrader().getKey().getCode());
            oh.setVouTotal(Util1.getDouble(txtVouTotal.getValue()));
            oh.setVouDate(Util1.convertToLocalDateTime(txtOrderDate.getDate()));
            oh.setMacId(Global.macId);
            oh.setProjectNo(p == null ? null : p.getKey().getProjectNo());
            OrderStatus os = orderStatusCompleter.getOrderStatus();
            oh.setOrderStatus(os == null ? null : os.getKey().getCode());
            oh.setOrderStatusName(os == null ? null : os.getDescription());
            oh.setRefNo(txtRefNo.getText());
            oh.setInvUpdate(chkInvUpdate.isSelected());
            if (lblStatus.getText().equals("NEW")) {
                OrderHisKey key = new OrderHisKey();
                key.setCompCode(Global.compCode);
                key.setVouNo(null);
                oh.setDeptId(Global.deptId);
                oh.setKey(key);
                oh.setCreatedDate(LocalDateTime.now());
                oh.setCreatedBy(Global.loginUser.getUserCode());
            } else {
                oh.setUpdatedBy(Global.loginUser.getUserCode());
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
                    inventoryRepo.delete(oh.getKey()).doOnSuccess((t) -> {
                        clear(true);
                    }).subscribe();
                }
            }
            case "DELETED" -> {
                int yes_no = JOptionPane.showConfirmDialog(this,
                        "Are you sure to restore?", "Order Voucher Restore.", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (yes_no == 0) {
                    oh.setDeleted(false);
                    inventoryRepo.restore(oh.getKey()).doOnSuccess((t) -> {
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

    private void deleteTran() {
        int row = tblOrder.convertRowIndexToModel(tblOrder.getSelectedRow());
        if (row >= 0) {
            if (tblOrder.getCellEditor() != null) {
                tblOrder.getCellEditor().stopCellEditing();
            }
            int yes_no = JOptionPane.showConfirmDialog(this,
                    "Are you sure to delete?", "Order Transaction delete.", JOptionPane.YES_NO_OPTION);
            if (yes_no == 0) {
                delete(row);
                calculateTotalAmount();
            }
        }
    }

    private void delete(int row) {
        switch (type) {
            case ORDER ->
                orderTableModel.delete(row);
            case PUR_ORDER ->
                purchaseOrderTableModel.delete(row);
            case DESIGN ->
                orderDesginTableModel.delete(row);
        }
    }

    private void calculateTotalAmount() {
        double ttlAmt = getListDetail().stream().mapToDouble((o) -> Util1.getDouble(o.getAmount())).sum();
        txtVouTotal.setValue(ttlAmt);
    }

    public void historyOrder() {
        if (dialog == null) {
            dialog = new OrderHistoryDialog(Global.parentForm);
            dialog.setInventoryRepo(inventoryRepo);
            dialog.setUserRepo(userRepo);
            dialog.setObserver(this);
            dialog.initMain();
            dialog.setSize(Global.width - 20, Global.height - 20);
            dialog.setLocationRelativeTo(null);
        }
        dialog.search();
    }

    public void setOrderVoucher(OrderHis sh) {
        if (sh != null) {
            progress.setIndeterminate(true);
            disableForm(false);
            oh = sh;
            String vouNo = sh.getKey().getVouNo();
            inventoryRepo.getOrderDetail(vouNo)
                    .doOnSuccess((t) -> {
                        setListDetail(t);
                    }).doOnTerminate(() -> {
                addNewRow();
                focusTable();
                progress.setIndeterminate(false);
                setHeader(oh);
            }).subscribe();
        }
    }

    private void setHeader(OrderHis oh) {
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
        userRepo.find(oh.getProjectNo()).doOnSuccess(t1 -> {
            projectAutoCompleter.setProject(t1);
        }).subscribe();
        inventoryRepo.findOrderStatus(oh.getOrderStatus()).doOnSuccess((o) -> {
            orderStatusCompleter.setOrderStatus(o);
        }).subscribe();
        oh.setVouLock(!oh.getDeptId().equals(Global.deptId));
        if (oh.isVouLock()) {
            lblStatus.setText("Voucher is locked.");
            lblStatus.setForeground(Color.RED);
            disableForm(false);
        } else if (!ProUtil.isSaleEdit()) {
            lblStatus.setText("No Permission.");
            lblStatus.setForeground(Color.RED);
            disableForm(false);
            observer.selected("print", true);
        } else if (oh.isDeleted()) {
            lblStatus.setText("DELETED");
            lblStatus.setForeground(Color.RED);
            disableForm(false);
            observer.selected("delete", true);
        } else if (DateLockUtil.isLockDate(oh.getVouDate())) {
            lblStatus.setText(DateLockUtil.MESSAGE);
            lblStatus.setForeground(Color.RED);
            disableForm(false);
        } else if (oh.isPost()) {
            lblStatus.setText("This voucher can't edit");
            lblStatus.setForeground(Color.RED);
            disableForm(false);
            observer.selected("print", true);
        } else {
            lblStatus.setText("EDIT");
            lblStatus.setForeground(Color.blue);
            disableForm(true);
        }
        txtVouNo.setText(oh.getKey().getVouNo());
        txtDueDate.setDate(Util1.convertToDate(oh.getCreditTerm()));
        txtRemark.setText(oh.getRemark());
        txtReference.setText(oh.getReference());
        txtRefNo.setText(oh.getRefNo());
        txtOrderDate.setDate(Util1.convertToDate(oh.getVouDate()));
        txtVouTotal.setValue(Util1.getFloat(oh.getVouTotal()));
        chkInvUpdate.setSelected(oh.isInvUpdate());
    }

    private void disableForm(boolean status) {
        ComponentUtil.enableForm(this, status);
        observer.selected("save", status);
        observer.selected("delete", status);
        observer.selected("print", status);
    }

    private void setAllLocation() {
        List<OrderHisDetail> detail = getListDetail();
        Location loc = locationAutoCompleter.getLocation();
        if (detail != null) {
            detail.forEach(sd -> {
                sd.setLocCode(loc.getKey().getLocCode());
                sd.setLocName(loc.getLocName());
            });
        }
        setListDetail(detail);
    }

    private void printVoucher(OrderHis sh, boolean print) {
        inventoryRepo.getOrderReport(sh.getKey().getVouNo()).doOnSuccess((t) -> {
            if (t != null) {
                viewReport(t, sh, print);
            }
        }).doOnError((e) -> {
            JOptionPane.showMessageDialog(this, e.getMessage());
            progress.setIndeterminate(false);
        }).doOnTerminate(() -> {
            clear(false);
        }).subscribe();
    }

    private void viewReport(List<OrderHisDetail> list, OrderHis sh, boolean print) {
        try {
            String reportName = ProUtil.getProperty(ProUtil.ORDER_VOU);
            if (reportName != null) {
                String reportPath = ProUtil.getReportPath() + reportName.concat(".jasper");
                ByteArrayInputStream stream = new ByteArrayInputStream(Util1.listToByteArray(list));
                JsonDataSource ds = new JsonDataSource(stream);
                JasperPrint jp = JasperFillManager.fillReport(reportPath, getDefaultParam(sh), ds);
                if (print) {
                    String printerName = ProUtil.getProperty(ProUtil.PRINTER_POS_NAME);
                    JasperReportUtil.print(jp, printerName, 1, 4);
                } else {
                    JasperViewer.viewReport(jp, false);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Select Report Type");
            }
        } catch (HeadlessException | JRException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private Map<String, Object> getDefaultParam(OrderHis p) {
        Map<String, Object> param = new HashMap<>();
        param.put("p_print_date", Util1.getTodayDateTime());
        param.put("p_comp_name", Global.companyName);
        param.put("p_comp_address", Global.companyAddress);
        param.put("p_comp_phone", Global.companyPhone);
        param.put("p_logo_path", ProUtil.logoPath());
        param.put("p_remark", p.getRemark());
        param.put("p_vou_no", p.getKey().getVouNo());
        param.put("p_vou_date", Util1.toDateStr(p.getVouDate(), "dd/MM/yyyy"));
        param.put("p_vou_total", p.getVouTotal());
        param.put("SUBREPORT_DIR", "report/");
        param.put("p_sub_report_dir", "report/");
        param.put("p_vou_date", Util1.getDate(p.getVouDate()));
        param.put("p_vou_time", Util1.getTime(p.getVouDate()));
        param.put("p_created_name", Global.hmUser.get(p.getCreatedBy()));
        param.put("p_report_name", p.getOrderStatusName());
        param.put("p_dep_name", Global.department.getDeptName());
        Trader t = traderAutoCompleter.getTrader();
        if (t != null) {
            param.put("p_trader_name", Util1.isNull(p.getReference(), t.getTraderName()));
            param.put("p_cus_name", t.getTraderName());
            param.put("p_trader_address", t.getAddress());
            param.put("p_trader_phone", t.getPhone());
        }
        return param;
    }

    private void searchOrder(Order order) {
        traderAutoCompleter.setTrader(order.getTrader());
        txtRemark.setText(order.getDesp());
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
        jLabel12 = new javax.swing.JLabel();
        txtRefNo = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtOrderStatus = new javax.swing.JTextField();
        txtVouNo = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        lblRec = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblOrder = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        txtVouTotal = new javax.swing.JFormattedTextField();
        chkInvUpdate = new javax.swing.JRadioButton();
        deskPane = new javax.swing.JDesktopPane();

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
        txtOrderDate.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtOrderDatePropertyChange(evt);
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

        jLabel12.setFont(Global.lableFont);
        jLabel12.setText("Ref No");

        txtRefNo.setFont(Global.textFont);
        txtRefNo.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtRefNo.setName("txtCurrency"); // NOI18N
        txtRefNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRefNoActionPerformed(evt);
            }
        });

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Order Status");

        txtOrderStatus.setFont(Global.textFont);
        txtOrderStatus.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtOrderStatus.setName("txtCurrency"); // NOI18N
        txtOrderStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtOrderStatusActionPerformed(evt);
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCus)
                    .addComponent(txtOrderDate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
                    .addComponent(txtVouNo, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel22, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtRemark, javax.swing.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
                    .addComponent(txtSaleman)
                    .addComponent(txtLocation))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDueDate, javax.swing.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
                    .addComponent(txtCurrency)
                    .addComponent(txtReference, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtRefNo, javax.swing.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
                    .addComponent(txtProjectNo)
                    .addComponent(txtOrderStatus))
                .addContainerGap())
        );

        panelSaleLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel17, jLabel2, jLabel4, jLabel5, jLabel6, jLabel9});

        panelSaleLayout.setVerticalGroup(
            panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSaleLayout.createSequentialGroup()
                .addContainerGap()
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
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(txtDueDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtOrderDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtSaleman, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel11)
                        .addComponent(txtOrderStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtCus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel21)
                        .addComponent(jLabel2))
                    .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel12)
                        .addComponent(txtRefNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtReference, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9)))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        panelSaleLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel3, jLabel5});

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblRec.setFont(Global.lableFont);
        lblRec.setText("Records");

        lblStatus.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        lblStatus.setText("NEW");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblStatus)
                    .addComponent(lblRec, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblRec)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblStatus)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel13.setFont(Global.lableFont);
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel13.setText("Vou Total :");

        txtVouTotal.setEditable(false);
        txtVouTotal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtVouTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVouTotal.setFont(Global.amtFont);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtVouTotal, javax.swing.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(txtVouTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        chkInvUpdate.setFont(Global.lableFont);
        chkInvUpdate.setText("Inventory Update");
        chkInvUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkInvUpdateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout deskPaneLayout = new javax.swing.GroupLayout(deskPane);
        deskPane.setLayout(deskPaneLayout);
        deskPaneLayout.setHorizontalGroup(
            deskPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        deskPaneLayout.setVerticalGroup(
            deskPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deskPane)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkInvUpdate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1)
                    .addComponent(panelSale, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(6, 6, 6))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelSale, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(deskPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkInvUpdate))
                        .addGap(113, 113, 113))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
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

    private void txtReferenceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtReferenceFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtReferenceFocusGained

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

    private void txtOrderDatePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtOrderDatePropertyChange
        Trader t = traderAutoCompleter.getTrader();
        if (t != null) {
            calDueDate(Util1.getInteger(t.getCreditDays()));
        }
    }//GEN-LAST:event_txtOrderDatePropertyChange

    private void txtRefNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRefNoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRefNoActionPerformed

    private void txtOrderStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtOrderStatusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtOrderStatusActionPerformed

    private void chkInvUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkInvUpdateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkInvUpdateActionPerformed

    private void txtVouNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVouNoFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVouNoFocusGained

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
                try {
                    Trader cus = traderAutoCompleter.getTrader();
                    if (cus != null) {
                        calDueDate(Util1.getInteger(cus.getCreditDays()));
                    }
                } catch (Exception ex) {
                    log.error("selected CustomerList : " + selectObj + " - " + ex.getMessage());
                }
            }
            case "ORDER-TOTAL" ->
                calculateTotalAmount();
            case "Location" ->
                setAllLocation();
            case "ORDER" -> {
                Order od = (Order) selectObj;
                searchOrder(od);
            }
            case "ORDER-HISTORY" -> {
                if (selectObj instanceof OrderHis s) {
                    inventoryRepo.findOrder(s.getKey().getVouNo()).doOnSuccess((t) -> {
                        setOrderVoucher(t);
                    }).subscribe();
                }
            }
            case "Select" -> {
                calculateTotalAmount();
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
                    txtOrderDate.setDate(Util1.formatDate(date));
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
        }
    }

    private void calDueDate(Integer day) {
        Date vouDate = txtOrderDate.getDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(vouDate);
        calendar.add(Calendar.DAY_OF_MONTH, day);
        Date dueDate = calendar.getTime();
        txtDueDate.setDate(dueDate);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton chkInvUpdate;
    private javax.swing.JDesktopPane deskPane;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblRec;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPanel panelSale;
    private javax.swing.JTable tblOrder;
    private javax.swing.JTextField txtCurrency;
    private javax.swing.JTextField txtCus;
    private com.toedter.calendar.JDateChooser txtDueDate;
    private javax.swing.JTextField txtLocation;
    private com.toedter.calendar.JDateChooser txtOrderDate;
    private javax.swing.JTextField txtOrderStatus;
    private javax.swing.JTextField txtProjectNo;
    private javax.swing.JTextField txtRefNo;
    private javax.swing.JTextField txtReference;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JTextField txtSaleman;
    private javax.swing.JTextField txtVouNo;
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
        boolean yes = ComponentUtil.checkClear(lblStatus.getText());
        if (yes) {
            clear(true);
        }
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
        findDialog.setVisible(!findDialog.isVisible());
    }

    @Override
    public String panelName() {
        return this.getName();
    }
}
