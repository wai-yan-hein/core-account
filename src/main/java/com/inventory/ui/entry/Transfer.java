/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry;

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
import com.inventory.model.Location;
import com.inventory.model.Trader;
import com.inventory.model.TransferHis;
import com.inventory.model.TransferHisKey;
import com.inventory.model.VTransfer;
import com.repo.InventoryRepo;
import com.inventory.ui.common.TransferTableModel;
import com.inventory.ui.entry.dialog.TransferHistoryDialog;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.inventory.editor.StockUnitEditor;
import com.inventory.model.LabourGroup;
import com.inventory.model.THDetailKey;
import com.inventory.model.TransferHisDetail;
import com.inventory.ui.common.TransferPaddingTableModel;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author Lenovo
 */
public class Transfer extends javax.swing.JPanel implements PanelControl, SelectionObserver, KeyListener {

    public static final int TRAN = 1;
    public static final int TRAN_PADDY = 2;
    private final TransferTableModel tranTableModel = new TransferTableModel();
    private final TransferPaddingTableModel tranPaddingTableModel = new TransferPaddingTableModel();
    private TransferHistoryDialog dialog;
    private InventoryRepo inventoryRepo;
    private UserRepo userRepo;
    private LocationAutoCompleter fromLocaitonCompleter;
    private LocationAutoCompleter toLocaitonCompleter;
    private TraderAutoCompleter traderAutoCompleter;
    private LabourGroupAutoCompleter labourGroupAutoCompleter;
    private TransferHis io = new TransferHis();
    private SelectionObserver observer;
    private JProgressBar progress;
    private int type;

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }

    public void setTraderAutoCompleter(TraderAutoCompleter traderAutoCompleter) {
        this.traderAutoCompleter = traderAutoCompleter;
    }

    /**
     * Creates new form StockInOutEntry
     */
    public Transfer(int type) {
        this.type = type;
        initComponents();
        initDateListner();
        actionMapping();
    }

    public void initMain() {
        initTable();
        initModel();
        initRowHeader();
        initCombo();
        initButttonGroup();
        clear(true);
    }

    private void initRowHeader() {
        RowHeader header = new RowHeader();
        JList list = header.createRowHeader(tblTransfer, 30);
        scroll.setRowHeaderView(list);
    }

    private void initButttonGroup() {
        chkGroupReport.add(rdoA4);
        chkGroupReport.add(rdoA5);
        rdoA5.setSelected(true);
    }

    private void initDateListner() {
        txtDate.getDateEditor().getUiComponent().setName("txtDate");
        txtDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtVou.addKeyListener(this);
        txtRemark.addKeyListener(this);
        txtRefNo.addKeyListener(this);
        txtFrom.addKeyListener(this);
        txtTo.addKeyListener(this);
        ComponentUtil.addFocusListener(this);
    }

    private void actionMapping() {
        String solve = "delete";
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        tblTransfer.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, solve);
        tblTransfer.getActionMap().put(solve, new DeleteAction());

    }

    private class DeleteAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            deleteTran();
        }
    }

    private void initCombo() {
        fromLocaitonCompleter = new LocationAutoCompleter(txtFrom, null, false, false);
        toLocaitonCompleter = new LocationAutoCompleter(txtTo, null, false, false);
        labourGroupAutoCompleter = new LabourGroupAutoCompleter(txtLabour, null, false);
        inventoryRepo.getLocation().doOnSuccess((t) -> {
            fromLocaitonCompleter.setListLocation(t);
            toLocaitonCompleter.setListLocation(t);
        }).subscribe();
        inventoryRepo.getLabourGroup().doOnSuccess((t) -> {
            t.add(new LabourGroup());
            labourGroupAutoCompleter.setListObject(t);
        }).subscribe();
        traderAutoCompleter = new TraderAutoCompleter(txtCustomer, inventoryRepo, null, false, "CUS");
        traderAutoCompleter.setObserver(this);
    }

    private void initTable() {
        tblTransfer.getTableHeader().setFont(Global.tblHeaderFont);
        tblTransfer.setDefaultRenderer(Object.class, new DecimalFormatRender());
        tblTransfer.setDefaultRenderer(Double.class, new DecimalFormatRender());
        tblTransfer.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblTransfer.setCellSelectionEnabled(true);
        tblTransfer.changeSelection(0, 0, false, false);
        tblTransfer.requestFocus();
    }

    private void initModel() {
        switch (type) {
            case TRAN -> {
                initTransfer();
            }
            case TRAN_PADDY -> {
                initTransferPaddy();
            }
        }
    }

    private void initTransfer() {
        tranTableModel.setVouDate(txtDate);
        tranTableModel.setLblRec(lblRec);
        tranTableModel.setInventoryRepo(inventoryRepo);
        tranTableModel.addNewRow();
        tranTableModel.setParent(tblTransfer);
        tranTableModel.setObserver(this);
        tblTransfer.setModel(tranTableModel);
        tblTransfer.getColumnModel().getColumn(0).setPreferredWidth(10);
        tblTransfer.getColumnModel().getColumn(1).setPreferredWidth(250);
        tblTransfer.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblTransfer.getColumnModel().getColumn(3).setPreferredWidth(100);
        tblTransfer.getColumnModel().getColumn(4).setPreferredWidth(10);
        tblTransfer.getColumnModel().getColumn(5).setPreferredWidth(100);
        tblTransfer.getColumnModel().getColumn(6).setPreferredWidth(10);
        tblTransfer.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor(inventoryRepo));
        tblTransfer.getColumnModel().getColumn(1).setCellEditor(new StockCellEditor(inventoryRepo));
        tblTransfer.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());
        tblTransfer.getColumnModel().getColumn(5).setCellEditor(new AutoClearEditor());
        inventoryRepo.getStockUnit().doOnSuccess((t) -> {
            tblTransfer.getColumnModel().getColumn(4).setCellEditor(new StockUnitEditor(t));
            tblTransfer.getColumnModel().getColumn(6).setCellEditor(new StockUnitEditor(t));
        }).subscribe();
    }

    private void initTransferPaddy() {
        tranPaddingTableModel.setVouDate(txtDate);
        tranPaddingTableModel.setLblRec(lblRec);
        tranPaddingTableModel.setInventoryRepo(inventoryRepo);
        tranPaddingTableModel.addNewRow();
        tranPaddingTableModel.setParent(tblTransfer);
        tranPaddingTableModel.setObserver(this);
        tblTransfer.setModel(tranPaddingTableModel);
        tblTransfer.getColumnModel().getColumn(0).setPreferredWidth(10);
        tblTransfer.getColumnModel().getColumn(1).setPreferredWidth(250);
        tblTransfer.getColumnModel().getColumn(2).setPreferredWidth(50);
        tblTransfer.getColumnModel().getColumn(3).setPreferredWidth(50);
        tblTransfer.getColumnModel().getColumn(4).setPreferredWidth(50);
        tblTransfer.getColumnModel().getColumn(5).setPreferredWidth(50);
        tblTransfer.getColumnModel().getColumn(6).setPreferredWidth(50);
        tblTransfer.getColumnModel().getColumn(7).setPreferredWidth(100);
        tblTransfer.getColumnModel().getColumn(8).setPreferredWidth(150);
        tblTransfer.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor(inventoryRepo));
        tblTransfer.getColumnModel().getColumn(1).setCellEditor(new StockCellEditor(inventoryRepo));
        tblTransfer.getColumnModel().getColumn(2).setCellEditor(new AutoClearEditor());
        tblTransfer.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());
        tblTransfer.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());
        tblTransfer.getColumnModel().getColumn(5).setCellEditor(new AutoClearEditor());
        tblTransfer.getColumnModel().getColumn(6).setCellEditor(new AutoClearEditor());
        tblTransfer.getColumnModel().getColumn(7).setCellEditor(new AutoClearEditor());
    }

    private void deleteVoucher() {
        String status = lblStatus.getText();
        switch (status) {
            case "EDIT" -> {
                int yes_no = JOptionPane.showConfirmDialog(Global.parentForm,
                        "Are you sure to delete?", "Transfer Voucher delete.", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                if (yes_no == 0) {
                    inventoryRepo.delete(io.getKey()).subscribe((t) -> {
                        clear(true);
                    });
                }
            }
            case "DELETED" -> {
                int yes_no = JOptionPane.showConfirmDialog(this,
                        "Are you sure to restore?", "Transfer Voucher Restore.", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (yes_no == 0) {
                    io.setDeleted(false);
                    inventoryRepo.restore(io.getKey()).subscribe((t) -> {
                        lblStatus.setText("EDIT");
                        lblStatus.setForeground(Color.blue);
                        disableForm(true);
                    });

                }
            }
            default ->
                JOptionPane.showMessageDialog(Global.parentForm, "Voucher can't delete.");
        }
    }

    private void deleteTran() {
        int row = tblTransfer.convertRowIndexToModel(tblTransfer.getSelectedRow());
        if (row >= 0) {
            if (tblTransfer.getCellEditor() != null) {
                tblTransfer.getCellEditor().stopCellEditing();
            }
            int yes_no = JOptionPane.showConfirmDialog(this,
                    "Are you sure to delete?", "Stock Transaction delete.", JOptionPane.YES_NO_OPTION);
            if (yes_no == 0) {
                tranTableModel.delete(row);
            }
        }
    }

    private boolean isValidDetail() {
        switch (type) {
            case TRAN -> {
                return tranTableModel.isValidEntry();
            }
            case TRAN_PADDY -> {
                return tranPaddingTableModel.isValidEntry();
            }
            default -> {
                return false;

            }
        }
    }

    private List<TransferHisDetail> getListDetail() {
        switch (type) {
            case TRAN -> {
                return tranTableModel.getListTransfer();
            }
            case TRAN_PADDY -> {
                return tranPaddingTableModel.getListTransfer();
            }
        }
        return null;
    }

    private List<THDetailKey> getDeleteList() {
        switch (type) {
            case TRAN -> {
                return tranTableModel.getDeleteList();
            }
            case TRAN_PADDY -> {
                return tranPaddingTableModel.getDeleteList();
            }
        }
        return null;
    }

    public void saveVoucher(boolean print) {
        if (isValidEntry() && isValidDetail()) {
            if (DateLockUtil.isLockDate(txtDate.getDate())) {
                DateLockUtil.showMessage(this);
                txtDate.requestFocus();
                return;
            }
            observer.selected("save", false);
            progress.setIndeterminate(true);
            io.setListTD(getListDetail());
            io.setDelList(getDeleteList());
            inventoryRepo.save(io).doOnSuccess((t) -> {
                io.getKey().setVouNo(t.getKey().getVouNo());
                io.setVouDate(t.getVouDate());
            }).doOnError((e) -> {
                observer.selected("save", true);
                JOptionPane.showMessageDialog(this, e.getMessage());
                progress.setIndeterminate(false);
            }).doOnTerminate(() -> {
                if (print) {
                    printVoucher(io);
                } else {
                    clear(true);
                }
            }).subscribe();
        }
    }

    private void assingnDefault() {
        inventoryRepo.getDefaultLocation().doOnSuccess((tt) -> {
            fromLocaitonCompleter.setLocation(tt);
        }).subscribe();
    }

    private void clearModel() {
        switch (type) {
            case TRAN -> {
                tranTableModel.clear();
                tranTableModel.addNewRow();
            }
            case TRAN_PADDY -> {
                tranPaddingTableModel.clear();
                tranPaddingTableModel.addNewRow();
            }
        }
    }

    private void clear(boolean focus) {
        assingnDefault();
        clearModel();
        progress.setIndeterminate(false);
        io = new TransferHis();
        lblStatus.setForeground(Color.GREEN);
        lblStatus.setText("NEW");
        txtRefNo.setText(null);
        txtRemark.setText(null);
        txtDate.setDate(Util1.getTodayDate());
        progress.setIndeterminate(false);
        txtVou.setText(null);
        traderAutoCompleter.setTrader(null);
        toLocaitonCompleter.setLocation(null);
        labourGroupAutoCompleter.setObject(null);
        disableForm(true);
    }

    private void focusOnTable() {
        int rc = tblTransfer.getRowCount();
        if (rc > 1) {
            tblTransfer.setRowSelectionInterval(rc - 1, rc - 1);
            tblTransfer.setColumnSelectionInterval(0, 0);
            tblTransfer.requestFocus();
        } else {
            txtDate.requestFocus();
        }
    }

    private boolean isValidEntry() {
        boolean status = true;
        Location fromLoc = fromLocaitonCompleter.getLocation();
        Location toLoc = toLocaitonCompleter.getLocation();
        if (fromLoc == null) {
            JOptionPane.showMessageDialog(this, "Select From Location.");
            status = false;
            txtFrom.requestFocus();
        } else if (toLoc == null) {
            JOptionPane.showMessageDialog(this, "Select To Location.");
            status = false;
            txtTo.requestFocus();
        } else if (lblStatus.getText().equals("DELETED")) {
            clear(true);
            status = false;
        } else if (fromLoc.getKey().getLocCode().equals(toLoc.getKey().getLocCode())) {
            status = false;
            JOptionPane.showMessageDialog(this, "Can't transfer the same location.");
            txtTo.requestFocus();
        } else if (!Util1.isDateBetween(txtDate.getDate())) {
            JOptionPane.showMessageDialog(this, "Invalid Date.",
                    "Validation.", JOptionPane.ERROR_MESSAGE);
            txtDate.requestFocus();
            status = false;
        } else {
            io.setRefNo(txtRefNo.getText());
            io.setRemark(txtRemark.getText());
            io.setVouDate(Util1.convertToLocalDateTime(txtDate.getDate()));
            io.setLocCodeFrom(fromLocaitonCompleter.getLocation().getKey().getLocCode());
            io.setLocCodeTo(toLocaitonCompleter.getLocation().getKey().getLocCode());
            io.setStatus(lblStatus.getText());
            Trader t = traderAutoCompleter.getTrader();
            if (t != null) {
                String traderCode = t.getKey().getCode();
                io.setTraderCode(traderCode);
            }
            if (lblStatus.getText().equals("NEW")) {
                TransferHisKey key = new TransferHisKey();
                key.setCompCode(Global.compCode);
                key.setVouNo(txtVou.getText());
                io.setDeptId(Global.deptId);
                io.setKey(key);
                io.setCreatedBy(Global.loginUser.getUserCode());
                io.setCreatedDate(LocalDateTime.now());
                io.setMacId(Global.macId);
                io.setDeleted(Boolean.FALSE);
                LabourGroup lg = labourGroupAutoCompleter.getObject();
                io.setLabourGroupCode(lg == null ? null : lg.getKey().getCode());
            } else {
                io.setUpdatedBy(Global.loginUser.getUserCode());
            }
        }
        return status;
    }

    private void setVoucher(TransferHis s, boolean local) {
        progress.setIndeterminate(true);
        io = s;
        Integer deptId = io.getDeptId();
        inventoryRepo.findLocation(io.getLocCodeFrom()).doOnSuccess((t) -> {
            fromLocaitonCompleter.setLocation(t);
        }).subscribe();
        inventoryRepo.findLocation(io.getLocCodeTo()).doOnSuccess((t) -> {
            toLocaitonCompleter.setLocation(t);
        }).subscribe();
        inventoryRepo.findTrader(s.getTraderCode()).doOnSuccess((t) -> {
            traderAutoCompleter.setTrader(t);
        }).subscribe();
        inventoryRepo.findLabourGroup(io.getLabourGroupCode()).doOnSuccess((t) -> {
            labourGroupAutoCompleter.setObject(t);
        }).subscribe();
        String vouNo = io.getKey().getVouNo();
        txtVou.setText(vouNo);
        txtDate.setDate(Util1.convertToDate(io.getVouDate()));
        txtRemark.setText(io.getRemark());
        txtRefNo.setText(io.getRefNo());
        if (io.isVouLock()) {
            lblStatus.setText("Voucher is locked.");
            lblStatus.setForeground(Color.RED);
            disableForm(false);
        } else if (!ProUtil.isTransferEdit()) {
            lblStatus.setText("No Permission.");
            lblStatus.setForeground(Color.RED);
            disableForm(false);
            observer.selected("print", true);
        } else if (Util1.getBoolean(io.isDeleted())) {
            lblStatus.setText("DELETED");
            lblStatus.setForeground(Color.red);
            disableForm(false);
        } else if (DateLockUtil.isLockDate(io.getVouDate())) {
            lblStatus.setText(DateLockUtil.MESSAGE);
            lblStatus.setForeground(Color.RED);
            disableForm(false);
        } else {
            lblStatus.setText("EDIT");
            lblStatus.setForeground(Color.blue);
            disableForm(true);
        }
        inventoryRepo.getTransferDetail(vouNo, deptId, local).doOnSuccess((t) -> {
            setListDetail(t);
        }).doOnError((e) -> {
            progress.setIndeterminate(false);
            JOptionPane.showMessageDialog(this, e.getMessage());
        }).doOnTerminate(() -> {
            progress.setIndeterminate(false);
            focusOnTable();
        }).subscribe();

    }

    private void setListDetail(List<TransferHisDetail> list) {
        switch (type) {
            case TRAN -> {
                tranTableModel.setListTransfer(list);
                tranTableModel.addNewRow();
            }
            case TRAN_PADDY -> {
                tranPaddingTableModel.setListTransfer(list);
                tranPaddingTableModel.addNewRow();
            }
        }
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

    private void printVoucher(TransferHis his) {
        String vouNo = his.getKey().getVouNo();
        inventoryRepo.getTransferReport(vouNo).doOnSuccess((t) -> {
            try {
                if (t != null) {
                    String a5 = ProUtil.getProperty(ProUtil.TRANSFER_VOUCHER);
                    String reportName = rdoA4.isSelected() ? "TransferVoucher" : Util1.isNull(a5, "TransferVoucherA5");
                    Map<String, Object> param = new HashMap<>();
                    param.put("p_print_date", Util1.getTodayDateTime());
                    param.put("p_comp_name", Global.companyName);
                    param.put("p_comp_address", Global.companyAddress);
                    param.put("p_comp_phone", Global.companyPhone);
                    param.put("p_logo_path", ProUtil.logoPath());
                    param.put("p_vou_date", Util1.convertToLocalStorage(his.getVouDate()));
                    String reportPath = String.format("report%s%s", File.separator, reportName.concat(".jasper"));
                    ByteArrayInputStream jsonDataStream = new ByteArrayInputStream(Util1.listToByteArray(t));
                    JsonDataSource ds = new JsonDataSource(jsonDataStream);
                    JasperPrint js = JasperFillManager.fillReport(reportPath, param, ds);
                    JasperViewer.viewReport(js, false);
                }
            } catch (JRException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }).doOnTerminate(() -> {
            clear(false);
        }).subscribe();
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
        tblTransfer = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtRefNo = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtVou = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        txtDate = new com.toedter.calendar.JDateChooser();
        jLabel7 = new javax.swing.JLabel();
        txtFrom = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtTo = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtCustomer = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtLabour = new javax.swing.JTextField();
        lblStatus = new javax.swing.JLabel();
        lblRec = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        rdoA5 = new javax.swing.JRadioButton();
        rdoA4 = new javax.swing.JRadioButton();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        tblTransfer.setAutoCreateRowSorter(true);
        tblTransfer.setFont(Global.textFont);
        tblTransfer.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblTransfer.setRowHeight(Global.tblRowHeight);
        tblTransfer.setShowHorizontalLines(true);
        tblTransfer.setShowVerticalLines(true);
        tblTransfer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblTransferKeyReleased(evt);
            }
        });
        scroll.setViewportView(tblTransfer);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Ref No");

        txtRefNo.setFont(Global.textFont);
        txtRefNo.setName("txtRefNo"); // NOI18N

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Remark");

        txtRemark.setFont(Global.textFont);
        txtRemark.setName("txtRemark"); // NOI18N

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Vou No    ");

        txtVou.setEditable(false);
        txtVou.setFont(Global.textFont);

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Date");

        txtDate.setDateFormatString("dd/MM/yyyy");
        txtDate.setFont(Global.textFont);
        txtDate.setMaxSelectableDate(new java.util.Date(253370745114000L));

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Location From");

        txtFrom.setFont(Global.textFont);
        txtFrom.setName("txtFrom"); // NOI18N

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Location To");

        txtTo.setFont(Global.textFont);
        txtTo.setName("txtTo"); // NOI18N

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Customer");

        txtCustomer.setFont(Global.textFont);
        txtCustomer.setName("txtRefNo"); // NOI18N

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Labour Group");

        txtLabour.setFont(Global.textFont);
        txtLabour.setName("txtRefNo"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtVou)
                    .addComponent(txtDate, javax.swing.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFrom, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTo)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtRemark, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtRefNo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCustomer, javax.swing.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
                    .addComponent(txtLabour, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel7, jLabel9});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(txtRefNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)
                            .addComponent(txtCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel5)
                                .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel11)
                                .addComponent(txtLabour, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(txtVou, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7)
                            .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel9)
                                .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtDate, txtVou});

        lblStatus.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        lblStatus.setText("NEW");

        lblRec.setFont(Global.lableFont);
        lblRec.setText("Records");

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        rdoA5.setText("A5");

        rdoA4.setText("A4");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rdoA5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rdoA4)
                .addContainerGap(33, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rdoA5)
                    .addComponent(rdoA4))
                .addContainerGap(35, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblStatus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblRec, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(208, 208, 208))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(scroll, javax.swing.GroupLayout.DEFAULT_SIZE, 1107, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scroll, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblRec, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observeMain();
    }//GEN-LAST:event_formComponentShown

    private void tblTransferKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblTransferKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_tblTransferKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup chkGroupReport;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblRec;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JRadioButton rdoA4;
    private javax.swing.JRadioButton rdoA5;
    private javax.swing.JScrollPane scroll;
    private javax.swing.JTable tblTransfer;
    private javax.swing.JTextField txtCustomer;
    private com.toedter.calendar.JDateChooser txtDate;
    private javax.swing.JTextField txtFrom;
    private javax.swing.JTextField txtLabour;
    private javax.swing.JTextField txtRefNo;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JTextField txtTo;
    private javax.swing.JFormattedTextField txtVou;
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
        clear(true);
    }

    @Override
    public void history() {
        if (dialog == null) {
            dialog = new TransferHistoryDialog(Global.parentForm);
            dialog.setInventoryRepo(inventoryRepo);
            dialog.setUserRepo(userRepo);
            dialog.setIconImage(new ImageIcon(getClass().getResource("/images/search.png")).getImage());
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
    }

    @Override
    public void selected(Object source, Object selectObj) {
        if (source.toString().equals("TR-HISTORY")) {
            if (selectObj instanceof VTransfer v) {
                inventoryRepo.findTransfer(v.getVouNo(), v.getDeptId(), v.isLocal()).subscribe((t) -> {
                    setVoucher(t, v.isLocal());
                });
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
                    txtFrom.requestFocus();
                }
            }
            case "txtFrom" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtTo.requestFocus();
                }
            }
            case "txtTo" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtRefNo.requestFocus();
                }
            }
            case "txtRefNo" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtRemark.requestFocus();
                }
            }
            case "txtRemark" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    tblTransfer.requestFocus();
                }
            }
        }
    }
}
