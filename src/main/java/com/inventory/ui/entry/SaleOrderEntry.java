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
import com.common.KeyPropagate;
import com.common.PanelControl;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.editor.TraderAutoCompleter;
import com.inventory.entity.Order;
import com.inventory.entity.OrderHis;
import com.inventory.entity.OrderHisKey;
import com.inventory.entity.OrderStatus;
import com.inventory.entity.Trader;
import com.inventory.entity.VOrder;
import com.inventory.ui.common.OrderStatusComboBoxModel;
import com.repo.InventoryRepo;
import com.inventory.ui.common.StockBalanceTableModel;
import com.inventory.ui.entry.dialog.OrderHistoryDialog;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.inventory.ui.common.SaleOrderTableModel;
import com.toedter.calendar.JTextFieldDateEditor;
import com.repo.UserRepo;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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

/**
 *
 * @author wai yan
 */
@Slf4j
public class SaleOrderEntry extends javax.swing.JPanel implements SelectionObserver, KeyListener, KeyPropagate, PanelControl {
    
    private final SaleOrderTableModel saleOrderTableModel = new SaleOrderTableModel();
    private OrderHistoryDialog dialog;
    private final StockBalanceTableModel stockBalanceTableModel = new StockBalanceTableModel();
    private InventoryRepo inventoryRepo;
    private UserRepo userRepo;
    private TraderAutoCompleter traderAutoCompleter;
    private OrderStatusComboBoxModel orderStatusComboModel = new OrderStatusComboBoxModel();
    private SelectionObserver observer;
    private OrderHis orderHis = new OrderHis();
    private JProgressBar progress;
    private FindDialog findDialog;
    
    public void setTraderAutoCompleter(TraderAutoCompleter traderAutoCompleter) {
        this.traderAutoCompleter = traderAutoCompleter;
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
    
    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }
    
    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }
    
    public SaleOrderEntry(int type) {
        initComponents();
        initButtonGroup();
        initKeyListener();
        initTextBoxFormat();
        initTextBoxValue();
        initDateListner();
        actionMapping();
    }

    /**
     * Creates new form SaleEntry1
     */
    public SaleOrderEntry() {
        initComponents();
        initButtonGroup();
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
    
    private void initButtonGroup() {
        ButtonGroup g = new ButtonGroup();
        g.add(chkVou);
        g.add(chkA4);
        g.add(chkA5);
    }
    
    public void initMain() {
        initCombo();
        initModel();
        assignDefaultValue();
        initFind();
        txtOrderDate.setDate(Util1.getTodayDate());
        txtCus.requestFocus();
    }
    
    private void initFind() {
        findDialog = new FindDialog(Global.parentForm, tblOrder);
    }
    
    private void initModel() {
        initSaleOrderTable();
    }
    
    private void initSaleOrderTable() {
        tblOrder.setModel(saleOrderTableModel);
        saleOrderTableModel.setParent(tblOrder);
        saleOrderTableModel.setLblRecord(lblRec);
//        saleOrderTableModel.setOrderDynamic(this);
        saleOrderTableModel.addNewRow();
        saleOrderTableModel.setObserver(this);
        saleOrderTableModel.setSbTableModel(stockBalanceTableModel);
        tblOrder.getTableHeader().setFont(Global.tblHeaderFont);
        tblOrder.setCellSelectionEnabled(true);
        tblOrder.getColumnModel().getColumn(0).setPreferredWidth(50);//Code
        tblOrder.getColumnModel().getColumn(1).setPreferredWidth(450);//Name
        tblOrder.getColumnModel().getColumn(2).setPreferredWidth(50);//qty
        tblOrder.getColumnModel().getColumn(0).setCellEditor(new AutoClearEditor());
        tblOrder.getColumnModel().getColumn(1).setCellEditor(new AutoClearEditor());
        tblOrder.getColumnModel().getColumn(2).setCellEditor(new AutoClearEditor());//weight        
        tblOrder.setDefaultRenderer(Object.class, new DecimalFormatRender());
        tblOrder.setDefaultRenderer(Float.class, new DecimalFormatRender());
        tblOrder.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblOrder.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
    
    private void initCombo() {
        traderAutoCompleter = new TraderAutoCompleter(txtCus, inventoryRepo, null, false, "CUS");
        traderAutoCompleter.setObserver(this);
        
        inventoryRepo.getOrderStatus().subscribe((t) -> {
            orderStatusComboModel.setList(t);
            cboOrderStatus.setModel(orderStatusComboModel);
        });
    }
    
    private void initKeyListener() {
        txtOrderDate.getDateEditor().getUiComponent().setName("txtOrderDate");
        txtOrderDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtDueDate.getDateEditor().getUiComponent().setName("txtDueDate");
        txtDueDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtVouNo.addKeyListener(this);
        txtRemark.addKeyListener(this);
        txtCus.addKeyListener(this);
        tblOrder.addKeyListener(this);
//        txtOrderStatus.addKeyListener(this);
    }
    
    private void initTextBoxValue() {
        txtVouTotal.setValue(0);
    }
    
    private void initTextBoxFormat() {
        txtVouTotal.setFormatterFactory(Util1.getDecimalFormat());
    }
    
    private void assignDefaultValue() {
        inventoryRepo.getDefaultCustomer().doOnSuccess((t) -> {
            traderAutoCompleter.setTrader(t);
        }).subscribe();
        txtDueDate.setDate(null);
        progress.setIndeterminate(false);
        txtVouNo.setText(null);
        chkVou.setSelected(true);
        chkA4.setSelected(Util1.getBoolean(ProUtil.getProperty("check.sale.A4")));
        chkA5.setSelected(Util1.getBoolean(ProUtil.getProperty("check.sale.A5")));
        if (!lblStatus.getText().equals("NEW")) {
            txtOrderDate.setDate(Util1.getTodayDate());
        }
        cboOrderStatus.setSelectedItem(null);
    }
    
    private void clear() {
        disableForm(true);
        saleOrderTableModel.removeListDetail();
        saleOrderTableModel.clearDelList();
        saleOrderTableModel.setChange(false);
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
    }
    
    public void saveOrder(boolean print) {
        if (isValidEntry() && saleOrderTableModel.isValidEntry()) {
            if (DateLockUtil.isLockDate(txtOrderDate.getDate())) {
                DateLockUtil.showMessage(this);
                txtOrderDate.requestFocus();
                return;
            }
            progress.setIndeterminate(true);
            orderHis.setListSH(saleOrderTableModel.getListDetail());
            orderHis.setListDel(saleOrderTableModel.getDelList());
            orderHis.setBackup(saleOrderTableModel.isChange());
            inventoryRepo.save(orderHis).doOnSuccess((t) -> {
                clear();
                if (print) {
                    String reportName = "OrderVoucher";
                    printVoucher(t.getKey().getVouNo(), reportName, chkVou.isSelected());
                }
            }).doOnError((e) -> {
                JOptionPane.showMessageDialog(this, e.getMessage());
                progress.setIndeterminate(false);
                observer.selected("save", true);
            }).subscribe();
        }
    }
    
    private boolean isValidEntry() {
        boolean status = true;
        if (lblStatus.getText().equals("DELETED")) {
            status = false;
            clear();
        } else if (traderAutoCompleter.getTrader() == null) {
            JOptionPane.showMessageDialog(this, "Choose Trader.",
                    "No Trader.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtRemark.requestFocus();
        } else if (!Util1.isDateBetween(txtOrderDate.getDate())) {
            JOptionPane.showMessageDialog(this, "Invalid Date.",
                    "Validation.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtOrderDate.requestFocus();
        } else {
            orderHis.setCreditTerm(Util1.convertToLocalDateTime(txtDueDate.getDate()));
            
            orderHis.setRemark(txtRemark.getText());
            orderHis.setReference(txtReference.getText());
            orderHis.setDeleted(orderHis.isDeleted());
            orderHis.setTraderCode(traderAutoCompleter.getTrader().getKey().getCode());
            orderHis.setVouTotal(Util1.getDouble(txtVouTotal.getValue()));
            orderHis.setStatus(lblStatus.getText());
            orderHis.setVouDate(Util1.convertToLocalDateTime(txtOrderDate.getDate()));
            orderHis.setMacId(Global.macId);
            if (orderStatusComboModel.getSelectedItem() instanceof OrderStatus ord) {
                orderHis.setOrderStatus(ord.getKey().getCode());
            }
            if (lblStatus.getText().equals("NEW")) {
                OrderHisKey key = new OrderHisKey();
                key.setCompCode(Global.compCode);
                key.setVouNo(null);
                orderHis.setDeptId(Global.deptId);
                orderHis.setKey(key);
                orderHis.setCreatedDate(LocalDateTime.now());
                orderHis.setCreatedBy(Global.loginUser.getUserCode());
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
                saleOrderTableModel.delete(row);
                calculateTotalAmount();
            }
        }
    }
    
    private void calculateTotalAmount() {
        double ttlAmt = saleOrderTableModel.getListDetail().stream().mapToDouble((o) -> Util1.getDouble(o.getAmount())).sum();
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
            orderHis = sh;
            String vouNo = sh.getKey().getVouNo();
            
            inventoryRepo.findTrader(orderHis.getTraderCode()).doOnSuccess((t) -> {
                traderAutoCompleter.setTrader(t);
            }).subscribe();
            inventoryRepo.findOrderStatus(orderHis.getOrderStatus()).doOnSuccess((o) -> {
                orderStatusComboModel.setSelectedItem(o);
            }).subscribe();
            sh.setVouLock(!sh.getDeptId().equals(Global.deptId));
            inventoryRepo.getOrderDetail(vouNo, sh.getDeptId())
                    .subscribe((t) -> {
                        saleOrderTableModel.setListDetail(t);
                        saleOrderTableModel.addNewRow();
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
                        } else if (DateLockUtil.isLockDate(orderHis.getVouDate())) {
                            lblStatus.setText(DateLockUtil.MESSAGE);
                            lblStatus.setForeground(Color.RED);
                            disableForm(false);
                        } else if (sh.isPost()) {
                            lblStatus.setText("This voucher can't edit");
                            lblStatus.setForeground(Color.RED);
                            disableForm(false);
                            observer.selected("print", true);
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
                        focusTable();
                        progress.setIndeterminate(false);
                    }, (e) -> {
                        progress.setIndeterminate(false);
                        JOptionPane.showMessageDialog(this, e.getMessage());
                    });
        }
    }
    
    private void disableForm(boolean status) {
        ComponentUtil.enableForm(this, status);
        observer.selected("save", status);
        observer.selected("delete", status);
        observer.selected("print", status);
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
                Map<String, Object> param = new HashMap<>();
                param.put("p_print_date", Util1.getTodayDateTime());
                param.put("p_comp_name", Global.companyName);
                param.put("p_comp_address", Global.companyAddress);
                param.put("p_comp_phone", Global.companyPhone);
                param.put("p_logo_path", ProUtil.logoPath());
                String reportPath = ProUtil.getReportPath() + reportName.concat(".jasper");
                ByteArrayInputStream stream = new ByteArrayInputStream(t);
                JsonDataSource ds = new JsonDataSource(stream);
                JasperPrint jp = JasperFillManager.fillReport(reportPath, param, ds);
                JasperViewer.viewReport(jp, false);
            } catch (JRException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Select Report Type");
            chkVou.requestFocus();
        }
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
    
    public void addTrader(Trader t) {
        traderAutoCompleter.addTrader(t);
    }
    
    public void setTrader(Trader t, int row) {
        traderAutoCompleter.setTrader(t, row);
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
        txtVouNo = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtDueDate = new com.toedter.calendar.JDateChooser();
        txtOrderDate = new com.toedter.calendar.JDateChooser();
        jLabel21 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        txtReference = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        cboOrderStatus = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        chkA5 = new javax.swing.JCheckBox();
        chkVou = new javax.swing.JCheckBox();
        chkA4 = new javax.swing.JCheckBox();
        lblRec = new javax.swing.JLabel();
        lblRec1 = new javax.swing.JLabel();
        sbPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblStockBalance = new javax.swing.JTable();
        sbProgress = new javax.swing.JProgressBar();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblOrder = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        txtVouTotal = new javax.swing.JFormattedTextField();
        lblStatus = new javax.swing.JLabel();

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
        jLabel4.setText("Order Date");

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Credit Term");

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

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Order Status");

        cboOrderStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboOrderStatusActionPerformed(evt);
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
                    .addComponent(txtCus, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtOrderDate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtVouNo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelSaleLayout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtReference, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelSaleLayout.createSequentialGroup()
                        .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDueDate, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboOrderStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );

        panelSaleLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel17, jLabel2, jLabel4, jLabel5, jLabel9});

        panelSaleLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cboOrderStatus, txtCus, txtDueDate, txtOrderDate, txtReference, txtRemark, txtVouNo});

        panelSaleLayout.setVerticalGroup(
            panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSaleLayout.createSequentialGroup()
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel17)
                        .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel21))
                    .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel11)
                        .addComponent(cboOrderStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtDueDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtOrderDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtCus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2))
                    .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtReference, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

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
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(lblRec, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGap(69, 69, 69)
                    .addComponent(lblRec1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGap(70, 70, 70)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblRec)
                .addContainerGap(63, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGap(88, 88, 88)
                    .addComponent(lblRec1)
                    .addContainerGap(88, Short.MAX_VALUE)))
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

        lblStatus.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        lblStatus.setText("NEW");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sbPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1))
                .addGap(6, 6, 6))
            .addComponent(panelSale, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelSale, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(sbPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblStatus))
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

    private void tblOrderMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblOrderMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tblOrderMouseClicked

    private void tblOrderKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblOrderKeyReleased
        // TODO add your handling code here:

    }//GEN-LAST:event_tblOrderKeyReleased

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

    private void formPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_formPropertyChange
        // TODO add your handling code here:
        log.info("change.");
    }//GEN-LAST:event_formPropertyChange

    private void txtOrderDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtOrderDateFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtOrderDateFocusGained

    private void txtOrderDatePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtOrderDatePropertyChange
        Trader t = traderAutoCompleter.getTrader();
        if (t != null) {
            calDueDate(Util1.getInteger(t.getCreditDays()));
        }
    }//GEN-LAST:event_txtOrderDatePropertyChange

    private void cboOrderStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboOrderStatusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboOrderStatusActionPerformed
    
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
            case "ORDER" -> {
                Order od = (Order) selectObj;
                searchOrder(od);
            }
            case "ORDER-HISTORY" -> {
                if (selectObj instanceof VOrder s) {
                    inventoryRepo.findOrder(s.getVouNo()).doOnSuccess((t) -> {
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
                    txtRemark.requestFocus();
                }
            }
            case "txtRemark" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtDueDate.requestFocus();
                }
            }
            case "txtReference" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    cboOrderStatus.requestFocus();
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
    private javax.swing.JComboBox<OrderStatus> cboOrderStatus;
    private javax.swing.JCheckBox chkA4;
    private javax.swing.JCheckBox chkA5;
    private javax.swing.JCheckBox chkVou;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblRec;
    private javax.swing.JLabel lblRec1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPanel panelSale;
    private javax.swing.JPanel sbPanel;
    private javax.swing.JProgressBar sbProgress;
    private javax.swing.JTable tblOrder;
    private javax.swing.JTable tblStockBalance;
    private javax.swing.JTextField txtCus;
    private com.toedter.calendar.JDateChooser txtDueDate;
    private com.toedter.calendar.JDateChooser txtOrderDate;
    private javax.swing.JTextField txtReference;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JFormattedTextField txtVouNo;
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
        findDialog.setVisible(!findDialog.isVisible());
    }
    
    @Override
    public String panelName() {
        return this.getName();
    }
}
