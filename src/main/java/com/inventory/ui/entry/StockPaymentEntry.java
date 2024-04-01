/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.inventory.ui.entry;

import com.acc.common.COAComboBoxModel;
import com.acc.dialog.FindDialog;
import com.common.ColumnColorCellRenderer;
import com.common.ComponentUtil;
import com.common.DateLockUtil;
import com.common.DecimalFormatRender;
import com.common.Global;
import com.common.PanelControl;
import com.common.ProUtil;
import com.common.RowHeader;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.editor.TraderAutoCompleter;
import com.inventory.entity.Location;
import com.inventory.entity.StockPayment;
import com.inventory.entity.StockPaymentDetail;
import com.inventory.entity.StockPaymentDetailKey;
import com.inventory.entity.Trader;
import com.inventory.ui.common.StockPaymentBagTableModel;
import com.repo.InventoryRepo;
import com.inventory.ui.common.StockPaymentQtyTableModel;
import com.inventory.ui.entry.dialog.StockPaymentHistoryDialog;
import com.user.editor.AutoClearEditor;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
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
 * @author Lenovo
 */
@Slf4j
public class StockPaymentEntry extends javax.swing.JPanel implements SelectionObserver, PanelControl {
    
    public static final int QTY = 1;
    public static final int BAG = 2;
    private final StockPaymentQtyTableModel qtyTableModel = new StockPaymentQtyTableModel();
    private final StockPaymentBagTableModel bagTableModel = new StockPaymentBagTableModel();
    private SelectionObserver observer;
    private JProgressBar progress;
    private InventoryRepo inventoryRepo;
    private TraderAutoCompleter traderAutoCompleter;
    private LocationAutoCompleter locationAutoCompleter;
    private COAComboBoxModel coaComboModel = new COAComboBoxModel();
    private StockPayment ph = new StockPayment();
    private StockPaymentHistoryDialog dialog;
    private String tranOption;
    private int type;
    private FindDialog findDialog;
    
    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }
    
    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }
    
    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    /**
     * Creates new form ReceiveEntry
     *
     * @param tranOption
     * @param type
     */
    public StockPaymentEntry(String tranOption, int type) {
        this.tranOption = tranOption;
        this.type = type;
        initComponents();
        initFocusAdapter();
        initFormat();
        initFind();
        configureOption();
        actionMapping();
    }
    
    private void initFind() {
        findDialog = new FindDialog(Global.parentForm, tblPayment);
    }
    
    private void configureOption() {
        lblTrader.setText(tranOption.equals("C") ? "Customer" : "Supplier");
        lblDate.setText(tranOption.equals("C") ? "Issue Date" : "Received Date");
    }
    
    private void initFormat() {
        ComponentUtil.setTextProperty(this);
    }
    
    private void actionMapping() {
        String solve = "delete";
        KeyStroke delete = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        tblPayment.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(delete, solve);
        tblPayment.getActionMap().put(solve, new DeleteAction());
    }
    
    private class DeleteAction extends AbstractAction {
        
        @Override
        public void actionPerformed(ActionEvent e) {
            deleteTran();
        }
    }
    
    private void deleteTran() {
        if (lblStatus.getText().equals("EDIT")) {
            int row = tblPayment.convertRowIndexToModel(tblPayment.getSelectedRow());
            if (row >= 0) {
                if (tblPayment.getCellEditor() != null) {
                    tblPayment.getCellEditor().stopCellEditing();
                }
                int yes_no = JOptionPane.showConfirmDialog(this,
                        "Are you sure to delete?", "Payment Transaction delete.", JOptionPane.YES_NO_OPTION);
                if (yes_no == 0) {
                    delete(row);
                    calTotalPayment();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Can't delete in payment mode.");
        }
    }
    
    private void delete(int row) {
        switch (type) {
            case QTY ->
                qtyTableModel.delete(row);
            case BAG ->
                bagTableModel.delete(row);
        }
    }
    
    private void initCombo() {
        traderAutoCompleter = new TraderAutoCompleter(txtTrader, inventoryRepo, null, false, "-");
        traderAutoCompleter.setObserver(this);
        locationAutoCompleter = new LocationAutoCompleter(txtLoc, null, false, false);
        inventoryRepo.getLocation().doOnSuccess((t) -> {
            locationAutoCompleter.setListLocation(t);
        }).then(inventoryRepo.getDefaultLocation().doOnSuccess((t) -> {
            locationAutoCompleter.setLocation(t);
        })).subscribe();
    }
    
    private void initFocusAdapter() {
        ComponentUtil.addFocusListener(this);
    }
    
    public void initMain() {
        initDate();
        initCombo();
        initTable();
        initModel();
        initRowHeader();
    }
    
    private void initDate() {
        txtVouDate.setDate(Util1.getTodayDate());
    }
    
    private void initRowHeader() {
        RowHeader header = new RowHeader();
        JList list = header.createRowHeader(tblPayment, 30);
        scroll.setRowHeaderView(list);
    }
    
    private void initModel() {
        switch (type) {
            case QTY ->
                initTableQty();
            case BAG ->
                initTableBag();
        }
    }
    
    private void initTable() {
        tblPayment.getTableHeader().setFont(Global.tblHeaderFont);
        tblPayment.setCellSelectionEnabled(true);
        tblPayment.setRowHeight(Global.tblRowHeight);
        tblPayment.setShowGrid(true);
        tblPayment.setFont(Global.textFont);
        tblPayment.setDefaultRenderer(Object.class, new DecimalFormatRender());
        tblPayment.setDefaultRenderer(Double.class, new DecimalFormatRender());
    }
    
    private void initTableQty() {
        qtyTableModel.changeColumnName(9, tranOption.equals("C") ? "Issue Qty" : "Receive Qty");
        qtyTableModel.changeColumnName(10, tranOption.equals("C") ? "Single Isuue" : "Single Receive");
        qtyTableModel.setObserver(this);
        qtyTableModel.setTable(tblPayment);
        tblPayment.setModel(qtyTableModel);
        tblPayment.getColumnModel().getColumn(0).setPreferredWidth(20);//Date
        tblPayment.getColumnModel().getColumn(1).setPreferredWidth(40);//VouNo
        tblPayment.getColumnModel().getColumn(2).setPreferredWidth(40);//con
        tblPayment.getColumnModel().getColumn(3).setPreferredWidth(100);//Remark
        tblPayment.getColumnModel().getColumn(4).setPreferredWidth(100);//Ref
        tblPayment.getColumnModel().getColumn(5).setPreferredWidth(40);//stock
        tblPayment.getColumnModel().getColumn(6).setPreferredWidth(150);//stockName
        tblPayment.getColumnModel().getColumn(7).setPreferredWidth(60);//qty
        tblPayment.getColumnModel().getColumn(8).setPreferredWidth(60);//bal
        tblPayment.getColumnModel().getColumn(9).setPreferredWidth(60);//Payment
        tblPayment.getColumnModel().getColumn(10).setPreferredWidth(1);//paid
        tblPayment.getColumnModel().getColumn(9).setCellEditor(new AutoClearEditor());
        tblPayment.getColumnModel().getColumn(8).setCellRenderer(new ColumnColorCellRenderer(Color.red));
        tblPayment.getColumnModel().getColumn(9).setCellRenderer(new ColumnColorCellRenderer(Color.green));
        tblPayment.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblPayment.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
    
    private void initTableBag() {
        
        bagTableModel.changeColumnName(9, tranOption.equals("C") ? "Issue Qty" : "Receive Qty");
        bagTableModel.changeColumnName(10, tranOption.equals("C") ? "Single Isuue" : "Single Receive");
        bagTableModel.setObserver(this);
        bagTableModel.setTable(tblPayment);
        tblPayment.setModel(bagTableModel);
        tblPayment.getColumnModel().getColumn(0).setPreferredWidth(20);//Date
        tblPayment.getColumnModel().getColumn(1).setPreferredWidth(40);//VouNo
        tblPayment.getColumnModel().getColumn(2).setPreferredWidth(40);//con
        tblPayment.getColumnModel().getColumn(3).setPreferredWidth(100);//Remark
        tblPayment.getColumnModel().getColumn(4).setPreferredWidth(100);//Ref
        tblPayment.getColumnModel().getColumn(5).setPreferredWidth(40);//stock
        tblPayment.getColumnModel().getColumn(6).setPreferredWidth(150);//stockName
        tblPayment.getColumnModel().getColumn(7).setPreferredWidth(60);//qty
        tblPayment.getColumnModel().getColumn(8).setPreferredWidth(60);//bal
        tblPayment.getColumnModel().getColumn(9).setPreferredWidth(60);//Payment
        tblPayment.getColumnModel().getColumn(10).setPreferredWidth(1);//paid
        tblPayment.getColumnModel().getColumn(9).setCellEditor(new AutoClearEditor());
        tblPayment.getColumnModel().getColumn(8).setCellRenderer(new ColumnColorCellRenderer(Color.red));
        tblPayment.getColumnModel().getColumn(9).setCellRenderer(new ColumnColorCellRenderer(Color.green));
        tblPayment.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblPayment.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
    
    private void calTraderBalance() {
        Trader trader = traderAutoCompleter.getTrader();
        if (trader != null) {
            if (lblStatus.getText().equals("NEW")) {
                progress.setIndeterminate(true);
                String traderCode = trader.getKey().getCode();
                switch (type) {
                    case QTY ->
                        inventoryRepo.getTraderStockBalanceQty(traderCode, tranOption).doOnSuccess((t) -> {
                            qtyTableModel.setListDetail(t);
                        }).doOnTerminate(() -> {
                            calTotalPayment();
                            progress.setIndeterminate(false);
                        }).subscribe();
                    case BAG ->
                        inventoryRepo.getTraderStockBalanceBag(traderCode, tranOption).doOnSuccess((t) -> {
                            bagTableModel.setListDetail(t);
                        }).doOnTerminate(() -> {
                            calTotalPayment();
                            progress.setIndeterminate(false);
                        }).subscribe();
                    default ->
                        throw new AssertionError();
                }
                
            } else {
                JOptionPane.showMessageDialog(this, "Create New Payment Voucher.");
            }
        }
    }
    
    private void calTotalPayment() {
        int size = getPaymentList().size();
        txtRecord.setValue(size);
        lblMessage.setForeground(Color.red);
        lblMessage.setText(size == 0 ? "No Data Records." : "");
    }
    
    private boolean isValidDetail() {
        return switch (type) {
            case QTY ->
                qtyTableModel.isValidEntry();
            case BAG ->
                bagTableModel.isValidEntry();
            default ->
                false;
        };
    }
    
    private List<StockPaymentDetail> getPaymentList() {
        return switch (type) {
            case QTY ->
                qtyTableModel.getListDetail();
            case BAG ->
                bagTableModel.getListDetail();
            default ->
                null;
        };
    }
    
    private List<StockPaymentDetailKey> getListDelete() {
        return switch (type) {
            case QTY ->
                qtyTableModel.getListDelete();
            case BAG ->
                bagTableModel.getListDelete();
            default ->
                null;
        };
    }
    
    private void savePayment(boolean print) {
        if (isValidEntry() && isValidDetail()) {
            if (DateLockUtil.isLockDate(txtVouDate.getDate())) {
                DateLockUtil.showMessage(this);
                txtVouDate.requestFocus();
                return;
            }
            observer.selected("save", false);
            progress.setIndeterminate(true);
            ph.setListDetail(getPaymentList());
            ph.setListDelete(getListDelete());
            ph.setTranOption(tranOption);
            inventoryRepo.saveStockPayment(ph).doOnSuccess((t) -> {
                if (print) {
                    printVoucher(t);
                }
            }).doOnError((e) -> {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                progress.setIndeterminate(false);
                observer.selected("save", true);
            }).doOnTerminate(() -> {
                clear();
            }).subscribe();
        }
    }
    
    private void enableToolBar(boolean status) {
        progress.setIndeterminate(!status);
        observer.selected("refresh", status);
        observer.selected("print", status);
        observer.selected("save", false);
        observer.selected("history", true);
    }
    
    private String getReportName() {
        return switch (type) {
            case QTY ->
                "StockIssueQtyVoucherA5";
            case BAG ->
                "StockIssueBagVoucherA5";
            default ->
                null;
        };
    }
    
    private void printVoucher(StockPayment spd) {
        try {
            enableToolBar(false);
            List<StockPaymentDetail> detail = spd.getListDetail();
            byte[] data = Util1.listToByteArray(detail);
            String reportName = getReportName();
            Map<String, Object> param = getDefaultParam(ph);
            String reportPath = ProUtil.getReportPath() + reportName.concat(".jasper");
            ByteArrayInputStream stream = new ByteArrayInputStream(data);
            JsonDataSource ds = new JsonDataSource(stream);
            JasperPrint jp = JasperFillManager.fillReport(reportPath, param, ds);
            JasperViewer.viewReport(jp, false);
            enableToolBar(true);
        } catch (JRException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
    
    private Map<String, Object> getDefaultParam(StockPayment p) {
        Map<String, Object> param = new HashMap<>();
        param.put("p_print_date", Util1.getTodayDateTime());
        param.put("p_comp_name", Global.companyName);
        param.put("p_comp_address", Global.companyAddress);
        param.put("p_comp_phone", Global.companyPhone);
        param.put("p_logo_path", ProUtil.logoPath());
        param.put("p_remark", p.getRemark());
        param.put("p_vou_no", p.getVouNo());
        param.put("p_vou_date", Util1.toDateStr(p.getVouDate(), "dd/MM/yyyy"));
        param.put("SUBREPORT_DIR", "report/");
        param.put("p_sub_report_dir", "report/");
        param.put("p_vou_date", Util1.getDate(p.getVouDate()));
        param.put("p_vou_time", Util1.getTime(p.getVouDate()));
        param.put("p_created_name", Global.hmUser.get(p.getCreatedBy()));
        param.put("p_tran_option", tranOption);
        Trader t = traderAutoCompleter.getTrader();
        if (t != null) {
            param.put("p_trader_name", Util1.isNull(p.getReference(), t.getTraderName()));
            param.put("p_cus_name", t.getTraderName());
            param.put("p_trader_address", t.getAddress());
            param.put("p_trader_phone", t.getPhone());
        }
        return param;
    }
    
    private boolean isValidEntry() {
        Trader t = traderAutoCompleter.getTrader();
        Location l = locationAutoCompleter.getLocation();
        if (t == null) {
            JOptionPane.showMessageDialog(this, "Invalid Trader.");
            txtTrader.requestFocus();
            return false;
        } else if (txtVouDate.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Invalid Date.");
            txtVouDate.requestFocus();
            return false;
        } else if (!Util1.isDateBetween(txtVouDate.getDate())) {
            JOptionPane.showMessageDialog(this, "Invalid Date.",
                    "Validation.", JOptionPane.ERROR_MESSAGE);
            txtVouDate.requestFocus();
            return false;
        } else if (!Util1.isDateBetween(txtVouDate.getDate())) {
            JOptionPane.showMessageDialog(this, "Invalid Date.",
                    "Validation.", JOptionPane.ERROR_MESSAGE);
            txtVouDate.requestFocus();
            return false;
        } else if (l == null) {
            JOptionPane.showMessageDialog(this, "Invalid Location.");
            txtLoc.requestFocus();
            return false;
        } else {
            ph.setCalculate(chkCal.isSelected());
            ph.setTraderCode(t.getKey().getCode());
            ph.setLocCode(l.getKey().getLocCode());
            ph.setVouDate(Util1.convertToLocalDateTime(txtVouDate.getDate()));
            ph.setDeleted(false);
            ph.setRemark(txtRemark.getText());
            ph.setMacId(Global.macId);
            ph.setReference(txtReference.getText());
            if (lblStatus.getText().equals("NEW")) {
                ph.setCompCode(Global.compCode);
                ph.setDeptId(Global.deptId);
                ph.setCreatedBy(Global.loginUser.getUserCode());
                ph.setCreatedDate(LocalDateTime.now());
            } else {
                ph.setUpdatedBy(Global.loginUser.getUserCode());
            }
        }
        return true;
    }
    
    private void clear() {
        traderAutoCompleter.setTrader(null);
        coaComboModel.setSelectedItem(null);
        progress.setIndeterminate(false);
        txtVouNo.setText(null);
        txtRemark.setText(null);
        txtReference.setText(null);
        chkCal.setSelected(true);
        txtRecord.setValue(0);
        clearModel();
        lblStatus.setForeground(Color.green);
        lblStatus.setText("NEW");
        lblMessage.setText("");
        enableForm(true);
        ph = new StockPayment();
    }
    
    private void clearModel() {
        switch (type) {
            case QTY ->
                qtyTableModel.clear();
            case BAG ->
                bagTableModel.clear();
        }
    }
    
    private void historyPayment() {
        if (dialog == null) {
            dialog = new StockPaymentHistoryDialog(Global.parentForm, tranOption);
            dialog.setObserver(this);
            dialog.setInventoryRepo(inventoryRepo);
            dialog.setTitle(String.format("%s History Dialog", tranOption.equals("C") ? "Stock Issue" : "Stock Received"));
            dialog.initMain();
            dialog.setSize(Global.width - 20, Global.height - 20);
            dialog.setLocationRelativeTo(null);
        }
        dialog.search();
    }
    
    private void setVoucherDetail(StockPayment ph) {
        this.ph = ph;
        String vouNo = ph.getVouNo();
        inventoryRepo.findTrader(ph.getTraderCode()).doOnSuccess((t) -> {
            traderAutoCompleter.setTrader(t);
        }).subscribe();
        inventoryRepo.findLocation(ph.getLocCode()).doOnSuccess((t) -> {
            locationAutoCompleter.setLocation(t);
        }).subscribe();
        txtVouNo.setText(vouNo);
        txtVouDate.setDate(Util1.convertToDate(ph.getVouDate()));
        txtRemark.setText(ph.getRemark());
        txtReference.setText(ph.getReference());
        chkCal.setSelected(ph.isCalculate());
        ph.setVouLock(!ph.getDeptId().equals(Global.deptId));
        if (ph.isDeleted()) {
            lblStatus.setText("DELETED");
            lblStatus.setForeground(Color.red);
            enableForm(false);
            observer.selected("delete", true);
        } else if (!ProUtil.isPaymentEdit()) {
            lblStatus.setText("No Permission.");
            lblStatus.setForeground(Color.RED);
            enableForm(false);
            observer.selected("print", true);
        } else if (ph.isVouLock()) {
            lblStatus.setText("Voucher is Lock.");
            lblStatus.setForeground(Color.RED);
            enableForm(false);
            observer.selected("print", true);
        } else if (DateLockUtil.isLockDate(ph.getVouDate())) {
            lblStatus.setText(DateLockUtil.MESSAGE);
            lblStatus.setForeground(Color.RED);
            enableForm(false);
            observer.selected("print", true);
        } else {
            lblStatus.setText("EDIT");
            lblStatus.setForeground(Color.blue);
            enableForm(true);
        }
        inventoryRepo.getStockPaymentDetail(vouNo).doOnSuccess((t) -> {
            setListDetail(t);
        }).doOnTerminate(() -> {
            calTotalPayment();
            progress.setIndeterminate(false);
            tblPayment.requestFocus();
        }).subscribe();
    }
    
    private void setListDetail(List<StockPaymentDetail> list) {
        switch (type) {
            case QTY ->
                qtyTableModel.setListDetail(list);
            case BAG ->
                bagTableModel.setListDetail(list);
        }
    }
    
    private void enableForm(boolean status) {
        ComponentUtil.enableForm(this, status);
        observer.selected("save", status);
        observer.selected("delete", status);
        observer.selected("print", status);
    }
    
    private void deletePayment() {
        String status = lblStatus.getText();
        switch (status) {
            case "EDIT" -> {
                int yes_no = JOptionPane.showConfirmDialog(this,
                        "Are you sure to delete?", "Payment Voucher Delete.", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                if (yes_no == 0) {
                    inventoryRepo.deleteStockPayment(ph.getVouNo()).doOnSuccess((t) -> {
                        calTotalPayment();
                        clear();
                    }).subscribe();
                }
            }
            case "DELETED" -> {
                int yes_no = JOptionPane.showConfirmDialog(this,
                        "Are you sure to restore?", "Payment Voucher Restore.", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (yes_no == 0) {
                    ph.setDeleted(false);
                    inventoryRepo.restoreStockPayment(ph.getVouNo()).doOnSuccess((t) -> {
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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtVouNo = new javax.swing.JTextField();
        lblDate = new javax.swing.JLabel();
        txtVouDate = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        chkCal = new javax.swing.JRadioButton();
        txtLoc = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtReference = new javax.swing.JTextField();
        scroll = new javax.swing.JScrollPane();
        tblPayment = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        lblTrader = new javax.swing.JLabel();
        txtTrader = new javax.swing.JTextField();
        lblStatus = new javax.swing.JLabel();
        lblMessage = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        txtRecord = new javax.swing.JFormattedTextField();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel1.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jPanel1ComponentShown(evt);
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Vou No");

        txtVouNo.setEditable(false);
        txtVouNo.setFont(Global.textFont);

        lblDate.setFont(Global.lableFont);
        lblDate.setText("Vou Date");

        txtVouDate.setDateFormatString("dd/MM/yyyy");
        txtVouDate.setFont(Global.textFont);
        txtVouDate.setMaxSelectableDate(new java.util.Date(253370745073000L));

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Remark");

        txtRemark.setFont(Global.textFont);

        chkCal.setSelected(true);
        chkCal.setText("Calculate");

        txtLoc.setFont(Global.textFont);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Location");

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Reference");

        txtReference.setFont(Global.textFont);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(313, 815, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtLoc, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(lblDate)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtVouDate, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 303, Short.MAX_VALUE)
                                .addComponent(chkCal))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel5)
                                .addGap(3, 3, 3)
                                .addComponent(txtReference, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(txtVouNo)
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtVouDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(chkCal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(txtReference, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtLoc, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 6, Short.MAX_VALUE))
        );

        tblPayment.setModel(new javax.swing.table.DefaultTableModel(
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
        scroll.setViewportView(tblPayment);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblTrader.setFont(Global.lableFont);
        lblTrader.setText("Customer");

        txtTrader.setFont(Global.textFont);

        lblStatus.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        lblStatus.setText("NEW");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTrader)
                .addGap(12, 12, 12)
                .addComponent(txtTrader, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblMessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblStatus)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTrader, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTrader)
                    .addComponent(lblStatus)
                    .addComponent(lblMessage, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Record :");

        txtRecord.setEditable(false);
        txtRecord.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtRecord.setFont(Global.textFont);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtRecord, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtRecord)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                    .addComponent(scroll)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scroll, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jPanel1ComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jPanel1ComponentShown
        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel1ComponentShown

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observeMain();
    }//GEN-LAST:event_formComponentShown


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton chkCal;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel lblDate;
    private javax.swing.JLabel lblMessage;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblTrader;
    private javax.swing.JScrollPane scroll;
    private javax.swing.JTable tblPayment;
    private javax.swing.JTextField txtLoc;
    private javax.swing.JFormattedTextField txtRecord;
    private javax.swing.JTextField txtReference;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JTextField txtTrader;
    private com.toedter.calendar.JDateChooser txtVouDate;
    private javax.swing.JTextField txtVouNo;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {
        if (source.equals("CAL_PAYMENT")) {
            calTotalPayment();
        } else if (source.equals("PAYMENT_HISTORY")) {
            if (selectObj instanceof StockPayment p) {
                setVoucherDetail(p);
            }
        } else if (source != null) {
            calTraderBalance();
        }
    }
    
    @Override
    public void save() {
        savePayment(false);
    }
    
    @Override
    public void delete() {
        deletePayment();
    }
    
    @Override
    public void newForm() {
        clear();
    }
    
    @Override
    public void history() {
        historyPayment();
    }
    
    @Override
    public void print() {
        savePayment(true);
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
