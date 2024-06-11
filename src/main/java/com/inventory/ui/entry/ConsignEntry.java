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
import java.awt.event.KeyEvent;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import com.common.Global;
import com.common.PanelControl;
import com.common.ProUtil;
import com.common.RowHeader;
import com.common.SelectionObserver;
import com.repo.UserRepo;
import com.common.Util1;
import com.inventory.editor.LabourGroupAutoCompleter;
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.editor.StockCellEditor;
import com.inventory.editor.TraderAutoCompleter;
import com.inventory.entity.Trader;
import com.inventory.entity.ConsignHis;
import com.inventory.entity.ConsignHisDetail;
import com.repo.InventoryRepo;
import com.inventory.ui.common.ConsignTableModel;
import com.inventory.ui.entry.dialog.ConsignHistoryDialog;
import com.user.editor.AutoClearEditor;
import com.inventory.entity.LabourGroup;
import com.inventory.entity.Location;
import com.inventory.entity.ConsignHisKey;
import com.inventory.entity.VConsign;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.view.JasperViewer;
import org.springframework.web.reactive.function.client.WebClientRequestException;

/**
 *
 * @author Lenovo
 */
public class ConsignEntry extends javax.swing.JPanel implements PanelControl, SelectionObserver, KeyListener {

    private final ConsignTableModel tableModel = new ConsignTableModel();
    private ConsignHistoryDialog dialog;
    private InventoryRepo inventoryRepo;
    private UserRepo userRepo;
    private LocationAutoCompleter locaitonCompleter;
    private TraderAutoCompleter traderAutoCompleter;
    private ConsignHis io = new ConsignHis();
    private SelectionObserver observer;
    private JProgressBar progress;
    private LabourGroupAutoCompleter labourGroupAutoCompleter;
    private String tranSource;
    private FindDialog findDialog;

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }

    public void setTraderAutoCompleter(TraderAutoCompleter traderAutoCompleter) {
        this.traderAutoCompleter = traderAutoCompleter;
    }

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public LocationAutoCompleter getLocationAutoCompleter() {
        return locaitonCompleter;
    }

    /**
     * Creates new form StockIR
     *
     * @param tranSource
     */
    public ConsignEntry(String tranSource) {
        this.tranSource = tranSource;
        initComponents();
        initDateListner();
        actionMapping();
    }

    public void initMain() {
        initTable();
        initRowHeader();
        initCombo();
        initFind();
        clear();
    }

    private void initFind() {
        findDialog = new FindDialog(Global.parentForm, tblStockIR);
    }

    private void initRowHeader() {
        RowHeader header = new RowHeader();
        JList list = header.createRowHeader(tblStockIR, 30);
        scroll.setRowHeaderView(list);
    }

    private void initDateListner() {
        txtDate.getDateEditor().getUiComponent().setName("txtDate");
        txtDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtLocation.addKeyListener(this);
        txtRemark.addKeyListener(this);
        txtLocation.addKeyListener(this);
        txtLG.addKeyListener(this);
        ComponentUtil.addFocusListener(this);
        ComponentUtil.setTextProperty(this);
    }

    private void actionMapping() {
        String solve = "delete";
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        tblStockIR.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, solve);
        tblStockIR.getActionMap().put(solve, new DeleteAction());

    }

    private class DeleteAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            deleteTran();
        }
    }

    private void initCombo() {
        locaitonCompleter = new LocationAutoCompleter(txtLocation, null, false, false);
        locaitonCompleter.setObserver(this);
        inventoryRepo.getLocation().doOnSuccess((t) -> {
            locaitonCompleter.setListLocation(t);
        }).subscribe();
        labourGroupAutoCompleter = new LabourGroupAutoCompleter(txtLG, null, false);
        labourGroupAutoCompleter.setObserver(this);
        inventoryRepo.getLabourGroup().doOnSuccess((t) -> {
            t.add(new LabourGroup());
            labourGroupAutoCompleter.setListObject(t);
        }).subscribe();
        traderAutoCompleter = new TraderAutoCompleter(txtCustomer, inventoryRepo, null, false, "CUS");
        traderAutoCompleter.setObserver(this);
    }

    private void initTable() {
        tableModel.addNewRow();
        tableModel.setParent(tblStockIR);
        tableModel.setObserver(this);
        tblStockIR.setModel(tableModel);
        tblStockIR.getTableHeader().setFont(Global.tblHeaderFont);
        tblStockIR.getColumnModel().getColumn(0).setPreferredWidth(10);
        tblStockIR.getColumnModel().getColumn(1).setPreferredWidth(250);
        tblStockIR.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblStockIR.getColumnModel().getColumn(3).setPreferredWidth(100);
        tblStockIR.getColumnModel().getColumn(4).setPreferredWidth(100);
        tblStockIR.getColumnModel().getColumn(5).setPreferredWidth(100);
        tblStockIR.getColumnModel().getColumn(6).setPreferredWidth(100);
        tblStockIR.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor(inventoryRepo, ProUtil.isSSContain()));
        tblStockIR.getColumnModel().getColumn(1).setCellEditor(new StockCellEditor(inventoryRepo, ProUtil.isSSContain()));
        tblStockIR.getColumnModel().getColumn(2).setCellEditor(new AutoClearEditor());
        tblStockIR.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());
        tblStockIR.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());
        tblStockIR.getColumnModel().getColumn(5).setCellEditor(new AutoClearEditor());
        tblStockIR.getColumnModel().getColumn(6).setCellEditor(new AutoClearEditor());
        tblStockIR.setDefaultRenderer(Object.class, new DecimalFormatRender());
        tblStockIR.setDefaultRenderer(Float.class, new DecimalFormatRender());
        tblStockIR.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblStockIR.setCellSelectionEnabled(true);
        tblStockIR.changeSelection(0, 0, false, false);
        tblStockIR.requestFocus();
    }

    private void deleteVoucher() {
        String status = lblStatus.getText();
        switch (status) {
            case "EDIT" -> {
                int yes_no = JOptionPane.showConfirmDialog(Global.parentForm,
                        "Are you sure to delete?", "Stock IR Voucher delete.", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                if (yes_no == 0) {
                    inventoryRepo.delete(io.getKey()).subscribe((t) -> {
                        clear();
                    });
                }
            }
            case "DELETED" -> {
                int yes_no = JOptionPane.showConfirmDialog(this,
                        "Are you sure to restore?", "Stock IR Voucher Restore.", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (yes_no == 0) {
                    io.setDeleted(false);
                    inventoryRepo.restore(io.getKey()).doOnSuccess((t) -> {
                        if (t) {
                            lblStatus.setText("EDIT");
                            lblStatus.setForeground(Color.blue);
                            disableForm(true);
                        }
                    }).subscribe();
                }
            }
            default ->
                JOptionPane.showMessageDialog(Global.parentForm, "Voucher can't delete.");
        }
    }

    private void deleteTran() {
        int row = tblStockIR.convertRowIndexToModel(tblStockIR.getSelectedRow());
        if (row >= 0) {
            if (tblStockIR.getCellEditor() != null) {
                tblStockIR.getCellEditor().stopCellEditing();
            }
            int yes_no = JOptionPane.showConfirmDialog(this,
                    "Are you sure to delete?", "Stock IR Transaction delete.", JOptionPane.YES_NO_OPTION);
            if (yes_no == 0) {
                tableModel.delete(row);
            }
        }
    }

    public boolean saveVoucher(boolean print) {
        boolean status = false;
        if (isValidEntry() && tableModel.isValidEntry()) {
            if (DateLockUtil.isLockDate(txtDate.getDate())) {
                DateLockUtil.showMessage(this);
                txtDate.requestFocus();
                return false;
            }
            observer.selected("save", false);
            progress.setIndeterminate(true);
            io.setListDetail(tableModel.getListDetail());
            io.setTranSource(tranSource.equals("I") ? 1 : 2);
            inventoryRepo.saveConsign(io).doOnSuccess((t) -> {
                if (print) {
                    printVoucher(t);
                }
                clear();
            }).doOnError((e) -> {
                observer.selected("save", true);
                progress.setIndeterminate(false);
                if (e instanceof WebClientRequestException) {
                    int yn = JOptionPane.showConfirmDialog(this, "Internet Offline. Try Again?", "Offline", JOptionPane.YES_OPTION, JOptionPane.ERROR_MESSAGE);
                    if (yn == JOptionPane.YES_OPTION) {
                        saveVoucher(print);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Error : " + e.getMessage(), "Server Error", JOptionPane.ERROR_MESSAGE);
                }
            }).doOnTerminate(() -> {
                focusOnTable();
            }).subscribe();
        }
        return status;
    }

    private void assingnDefault() {
        inventoryRepo.getDefaultLocation().doOnSuccess((tt) -> {
            locaitonCompleter.setLocation(tt);
        }).subscribe();
        txtWt.setValue(0);
        txtBag.setValue(0);
        txtDate.setDate(Util1.getTodayDate());
    }

    private void clear() {
        assingnDefault();
        io = new ConsignHis();
        lblStatus.setForeground(Color.GREEN);
        lblStatus.setText("NEW");
        txtRemark.setText(null);
        txtVou.setText(null);
        tableModel.clear();
        tableModel.addNewRow();
        progress.setIndeterminate(false);
        traderAutoCompleter.setTrader(null);
        labourGroupAutoCompleter.setObject(null);
        disableForm(true);
    }

    private void focusOnTable() {
        int rc = tblStockIR.getRowCount();
        if (rc > 1) {
            tblStockIR.setRowSelectionInterval(rc - 1, rc - 1);
            tblStockIR.setColumnSelectionInterval(0, 0);
            tblStockIR.requestFocus();
        } else {
            txtDate.requestFocusInWindow();
        }
    }

    private boolean isValidEntry() {
        LabourGroup lg = labourGroupAutoCompleter.getObject();
        Trader t = traderAutoCompleter.getTrader();
        Location l = locaitonCompleter.getLocation();
        if (lblStatus.getText().equals("DELETED")) {
            clear();
            return false;
        } else if (l == null) {
            JOptionPane.showMessageDialog(this, "Choose Location.",
                    "No Location.", JOptionPane.ERROR_MESSAGE);
            txtLocation.requestFocus();
            return false;
        } else if (!Util1.isDateBetween(txtDate.getDate())) {
            JOptionPane.showMessageDialog(this, "Invalid Date.",
                    "Validation.", JOptionPane.ERROR_MESSAGE);
            txtDate.requestFocus();
            return false;
        } else if (t == null) {
            JOptionPane.showMessageDialog(this, "Choose Trader.",
                    "No Trader.", JOptionPane.ERROR_MESSAGE);
            txtCustomer.requestFocus();
            return false;
        } else {
            io.setRemark(txtRemark.getText());
            io.setVouDate(Util1.convertToLocalDateTime(txtDate.getDate()));
            io.setLocCode(l.getKey().getLocCode());
            io.setStatus(lblStatus.getText());
            io.setTraderCode(t.getKey().getCode());
            io.setLabourGroupCode(lg == null ? null : lg.getKey().getCode());
            if (lblStatus.getText().equals("NEW")) {
                ConsignHisKey key = new ConsignHisKey();
                key.setCompCode(Global.compCode);
                io.setDeptId(Global.deptId);
                io.setKey(key);
                io.setCreatedBy(Global.loginUser.getUserCode());
                io.setCreatedDate(LocalDateTime.now());
                io.setMacId(Global.macId);
                io.setDeleted(Boolean.FALSE);
            } else {
                io.setUpdatedBy(Global.loginUser.getUserCode());
            }
        }
        return true;
    }

    private void setVoucher(ConsignHis s) {
        progress.setIndeterminate(true);
        this.io = s;
        inventoryRepo.findLocation(io.getLocCode()).doOnSuccess((t) -> {
            locaitonCompleter.setLocation(t);
        }).subscribe();
        inventoryRepo.findTrader(s.getTraderCode()).doOnSuccess((t) -> {
            traderAutoCompleter.setTrader(t);
        }).subscribe();
        inventoryRepo.findLabourGroup(s.getLabourGroupCode()).doOnSuccess((t) -> {
            labourGroupAutoCompleter.setObject(t);
        }).subscribe();
        String vouNo = io.getKey().getVouNo();
        txtDate.setDate(Util1.convertToDate(io.getVouDate()));
        txtRemark.setText(io.getRemark());
        txtVou.setText(vouNo);
        io.setVouLock(!io.getDeptId().equals(Global.deptId));
        if (io.isVouLock()) {
            lblStatus.setText("Voucher is Lock.");
            lblStatus.setForeground(Color.RED);
            disableForm(false);
            observer.selected("print", true);
        } else if (Util1.getBoolean(io.isDeleted())) {
            lblStatus.setText("DELETED");
            lblStatus.setForeground(Color.red);
            disableForm(false);
            observer.selected("delete", true);
        } else if (DateLockUtil.isLockDate(io.getVouDate())) {
            lblStatus.setText(DateLockUtil.MESSAGE);
            lblStatus.setForeground(Color.RED);
            disableForm(false);
        } else {
            lblStatus.setText("EDIT");
            lblStatus.setForeground(Color.blue);
            disableForm(true);
        }
        inventoryRepo.getStockIssRecDetail(vouNo).doOnSuccess((t) -> {
            tableModel.setListDetail(t);
        }).doOnError((e) -> {
            JOptionPane.showMessageDialog(this, e.getMessage());
            progress.setIndeterminate(false);
        }).doOnTerminate(() -> {
            calTotal();
            tableModel.addNewRow();
            progress.setIndeterminate(false);
        }).subscribe();
    }

    private void calTotal() {
        double bag = tableModel.getListDetail().stream().mapToDouble((t) -> t.getBag()).sum();
        double weight = tableModel.getListDetail().stream().mapToDouble((t) -> t.getTotalWeight()).sum();
        txtBag.setValue(bag);
        txtWt.setValue(weight);
    }

    private void disableForm(boolean status) {
        ComponentUtil.enableForm(this, status);
        observer.selected("save", status);
        observer.selected("delete", status);
        observer.selected("print", status);
    }

    private void observeMain() {
        observer.selected("control", this);
        observer.selected("save", true);
        observer.selected("print", true);
        observer.selected("history", true);
        observer.selected("delete", true);
        observer.selected("refresh", true);
    }

    private void enableToolBar(boolean status) {
        progress.setIndeterminate(!status);
        observer.selected("refresh", status);
        observer.selected("print", status);
        observer.selected("save", false);
        observer.selected("history", true);
    }

    private void printVoucher(ConsignHis spd) {
        try {
            enableToolBar(false);
            List<ConsignHisDetail> detail = spd.getListDetail();
            byte[] data = Util1.listToByteArray(detail);
            String reportName = spd.getTranSource() == 1 ? "StockConsignIssue" : "StockConsignReceive";
            Map<String, Object> param = getDefaultParam(spd);
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

    private Map<String, Object> getDefaultParam(ConsignHis p) {
        Map<String, Object> param = new HashMap<>();
        param.put("p_print_date", Util1.getTodayDateTime());
        param.put("p_comp_name", Global.companyName);
        param.put("p_comp_address", Global.companyAddress);
        param.put("p_comp_phone", Global.companyPhone);
        param.put("p_logo_path", ProUtil.logoPath());
        param.put("p_remark", p.getRemark());
        param.put("p_vou_no", p.getKey().getVouNo());
        param.put("p_vou_date", Util1.toDateStr(p.getVouDate(), "dd/MM/yyyy"));
        param.put("SUBREPORT_DIR", "report/");
        param.put("p_sub_report_dir", "report/");
        param.put("p_vou_date", Util1.getDate(p.getVouDate()));
        param.put("p_vou_time", Util1.getTime(p.getVouDate()));
        param.put("p_created_name", Global.hmUser.get(p.getCreatedBy()));
        param.put("p_tran_source", p.getTranSource());
        Trader t = traderAutoCompleter.getTrader();
        if (t != null) {
            param.put("p_trader_name", t.getTraderName());
            param.put("p_cus_name", t.getTraderName());
            param.put("p_trader_address", t.getAddress());
            param.put("p_trader_phone", t.getPhone());
        }
        return param;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        chkGroupReport = new javax.swing.ButtonGroup();
        scroll = new javax.swing.JScrollPane();
        tblStockIR = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        lblVouNo = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtDate = new com.toedter.calendar.JDateChooser();
        jLabel7 = new javax.swing.JLabel();
        txtLocation = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtCustomer = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtLG = new javax.swing.JTextField();
        txtVou = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        txtBag = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        txtWt = new javax.swing.JFormattedTextField();
        lblStatus = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        tblStockIR.setAutoCreateRowSorter(true);
        tblStockIR.setFont(Global.textFont);
        tblStockIR.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblStockIR.setRowHeight(Global.tblRowHeight);
        tblStockIR.setShowHorizontalLines(true);
        tblStockIR.setShowVerticalLines(true);
        tblStockIR.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblStockIRKeyReleased(evt);
            }
        });
        scroll.setViewportView(tblStockIR);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Remark");

        txtRemark.setFont(Global.textFont);
        txtRemark.setName("txtRemark"); // NOI18N

        lblVouNo.setFont(Global.lableFont);
        lblVouNo.setText("Vou No    ");

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Date");

        txtDate.setDateFormatString("dd/MM/yyyy");
        txtDate.setFont(Global.textFont);
        txtDate.setMaxSelectableDate(new java.util.Date(253370745114000L));

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Location");

        txtLocation.setFont(Global.textFont);
        txtLocation.setName("txtLocation"); // NOI18N

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Customer");

        txtCustomer.setFont(Global.textFont);
        txtCustomer.setName("txtRefNo"); // NOI18N

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Labour Group");

        txtVou.setEditable(false);
        txtVou.setFont(Global.textFont);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblVouNo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDate, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
                    .addComponent(txtVou))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtLocation, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(txtCustomer))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtRemark, javax.swing.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)
                    .addComponent(txtLG, javax.swing.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblVouNo)
                        .addComponent(jLabel7)
                        .addComponent(txtLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)
                        .addComponent(txtVou, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtRemark, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel8)
                        .addComponent(txtCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel11)
                        .addComponent(txtLG, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        txtBag.setEditable(false);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Total Weight :");

        txtWt.setEditable(false);

        lblStatus.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        lblStatus.setText("NEW");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Total Bag :");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblStatus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtBag, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtWt, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtBag, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtWt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scroll, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scroll, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observeMain();
    }//GEN-LAST:event_formComponentShown

    private void tblStockIRKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblStockIRKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_tblStockIRKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup chkGroupReport;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblVouNo;
    private javax.swing.JScrollPane scroll;
    private javax.swing.JTable tblStockIR;
    private javax.swing.JFormattedTextField txtBag;
    private javax.swing.JTextField txtCustomer;
    private com.toedter.calendar.JDateChooser txtDate;
    private javax.swing.JTextField txtLG;
    private javax.swing.JTextField txtLocation;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JTextField txtVou;
    private javax.swing.JFormattedTextField txtWt;
    // End of variables declaration//GEN-END:variables

    @Override
    public void save() {
        saveVoucher(false);
    }

    @Override
    public void delete() {
        deleteVoucher();
    }

    @Override
    public void newForm() {
        clear();
    }

    @Override
    public void history() {
        if (dialog == null) {
            dialog = new ConsignHistoryDialog(Global.parentForm, tranSource);
            dialog.setInventoryRepo(inventoryRepo);
            dialog.setUserRepo(userRepo);
            dialog.setTitle(String.format("%s Voucher History Dialog", tranSource.equals("I") ? "Consign Issue" : "Consign Receive"));
            dialog.setObserver(this);
            dialog.initMain();
            dialog.setSize(Global.width - 20, Global.height - 20);
            dialog.setLocationRelativeTo(null);
        }
        dialog.search();
    }

    @Override
    public void print() {
        saveVoucher(true);
    }

    @Override
    public void refresh() {
        initCombo();
    }

    @Override
    public void filter() {
        findDialog.setVisible(!findDialog.isVisible());
    }

    @Override
    public void selected(Object source, Object selectObj) {
        if (source.toString().equals("ISSREC-HISTORY")) {
            if (selectObj instanceof VConsign v) {
                inventoryRepo.findStockIR(v.getVouNo(), v.isLocal()).doOnSuccess((t) -> {
                    setVoucher(t);
                }).subscribe();
            }
        }
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
            case "txtDate" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String date = ((JTextFieldDateEditor) sourceObj).getText();
                    txtDate.setDate(Util1.formatDate(date));
                    txtLocation.requestFocus();
                }
            }
            case "txtFrom" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtRemark.requestFocus();
                }
            }
            case "txtCustomer" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtRemark.requestFocus();
                }
            }
            case "txtRemark" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtLG.requestFocus();
                }
            }
            case "txtLG" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    tblStockIR.requestFocus();
                }
            }

        }
    }
}
