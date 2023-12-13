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
import com.common.RowHeader;
import com.common.SelectionObserver;
import com.repo.UserRepo;
import com.common.Util1;
import com.inventory.editor.LabourGroupAutoCompleter;
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.editor.StockCellEditor;
import com.inventory.editor.TraderAutoCompleter;
import com.inventory.model.Trader;
import com.inventory.model.StockIssueReceive;
import com.repo.InventoryRepo;
import com.inventory.ui.common.StockIssueRecTableModel;
import com.inventory.ui.entry.dialog.StockIssRecHistoryDialog;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.inventory.model.LabourGroup;
import com.inventory.model.StockIssueReceiveKey;
import com.inventory.model.VStockIssueReceive;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyListener;
import java.time.LocalDateTime;
import java.util.Objects;
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
public class StockIssRecEntry extends javax.swing.JPanel implements PanelControl, SelectionObserver, KeyListener {

    private final StockIssueRecTableModel tableModel = new StockIssueRecTableModel();
    private StockIssRecHistoryDialog dialog;
    private InventoryRepo inventoryRepo;
    private UserRepo userRepo;
    private LocationAutoCompleter locaitonCompleter;
    private TraderAutoCompleter traderAutoCompleter;
    private StockIssueReceive io = new StockIssueReceive();
    private SelectionObserver observer;
    private JProgressBar progress;
    private LabourGroupAutoCompleter labourGroupAutoCompleter;
    private String tranSource;

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
     */
    public StockIssRecEntry(String tranSource) {
        this.tranSource=tranSource;
        initComponents();
        initDateListner();
        actionMapping();
    }
    
    private void configureOption() {
        lblVouNo.setText(tranSource.equals("I") ? "Issue Vou No" : " Receive Vou No");
    }

    public void initMain() {
        initTable();
        initRowHeader();
        initCombo();
        clear();
    }

    private void initRowHeader() {
        RowHeader header = new RowHeader();
        JList list = header.createRowHeader(tblStockIR, 30);
        scroll.setRowHeaderView(list);
    }

    private void initDateListner() {
        txtDate.getDateEditor().getUiComponent().setName("txtDate");
        txtDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtDate.getDateEditor().getUiComponent().addFocusListener(fa);
        txtVou.addKeyListener(this);
        txtRemark.addKeyListener(this);
        txtLocation.addKeyListener(this);
        txtLG.addKeyListener(this);       
    }
    private final FocusAdapter fa = new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            ((JTextFieldDateEditor) e.getSource()).selectAll();
        }
    };

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
        inventoryRepo.getLocation().subscribe((t) -> {
            locaitonCompleter.setListLocation(t);
        });   
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
        tableModel.setVouDate(txtDate);
        tableModel.setLblRec(lblRec);
        tableModel.setInventoryRepo(inventoryRepo);
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
        tblStockIR.getColumnModel().getColumn(7).setPreferredWidth(100);
        tblStockIR.getColumnModel().getColumn(8).setPreferredWidth(100);
        tblStockIR.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor(inventoryRepo));
        tblStockIR.getColumnModel().getColumn(1).setCellEditor(new StockCellEditor(inventoryRepo));
        tblStockIR.getColumnModel().getColumn(2).setCellEditor(new AutoClearEditor());
        tblStockIR.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());
        tblStockIR.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());
        tblStockIR.getColumnModel().getColumn(5).setCellEditor(new AutoClearEditor());
        tblStockIR.getColumnModel().getColumn(6).setCellEditor(new AutoClearEditor());
        tblStockIR.getColumnModel().getColumn(7).setCellEditor(new AutoClearEditor());
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
                        if(t){
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
            io.setListIRDetail(tableModel.getListDetail());
            io.setListDel(tableModel.getDelList());
            io.setTranSource(tranSource.equals("I") ? 1 : 2);
            inventoryRepo.saveStockIssRec(io)
                    .subscribe((t) -> {
                        clear();
                        focusOnTable();
                        if (print) {
                            printVoucher(t.getKey().getVouNo());
                        }
                    }, (e) -> {
                        observer.selected("save", true);
                        JOptionPane.showMessageDialog(this, e.getMessage());
                        progress.setIndeterminate(false);
                    });
        }
        return status;
    }

    private void assingnDefault() {
        inventoryRepo.getDefaultLocation().doOnSuccess((tt) -> {
            locaitonCompleter.setLocation(tt);
        }).subscribe();
    }

    private void clear() {
        assingnDefault();
        io = new StockIssueReceive();
        lblStatus.setForeground(Color.GREEN);
        lblStatus.setText("NEW");     
        txtRemark.setText(null);
        tableModel.clear();
        tableModel.addNewRow();
        txtDate.setDate(Util1.getTodayDate());
        progress.setIndeterminate(false);
        txtVou.setText(null);
        traderAutoCompleter.setTrader(null);        
        labourGroupAutoCompleter.setObject(null);
        disableForm(true);       
        lblStatus.setForeground(Color.green);
        lblStatus.setText("NEW");
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
        boolean status = true;
        if (lblStatus.getText().equals("DELETED")) {
            status = true;
            clear();
        }else if(locaitonCompleter.getLocation() == null) {
            JOptionPane.showMessageDialog(this, "Choose Location.",
                    "No Location.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtLocation.requestFocus();
        }else if (!Util1.isDateBetween(txtDate.getDate())) {
            JOptionPane.showMessageDialog(this, "Invalid Date.",
                    "Validation.", JOptionPane.ERROR_MESSAGE);
            txtDate.requestFocus();
            status = false;
        } else if (Objects.isNull(labourGroupAutoCompleter.getObject())) {
            JOptionPane.showMessageDialog(this, "Choose Labour Group",
                    "Choose Labour Group", JOptionPane.ERROR_MESSAGE);
            txtLG.requestFocus();
            return false;
        }
        else {
            io.setRemark(txtRemark.getText());
            io.setVouDate(Util1.convertToLocalDateTime(txtDate.getDate()));
            io.setLocation(locaitonCompleter.getLocation().getKey().getLocCode());
            io.setStatus(lblStatus.getText());
            Trader t = traderAutoCompleter.getTrader();
            if (t != null) {
                String traderCode = t.getKey().getCode();
                io.setTraderCode(traderCode);
            }
            LabourGroup lg = labourGroupAutoCompleter.getObject();
            if (lg != null) {
                io.setLabourGroupCode(lg == null ? null : lg.getKey().getCode());
            }
            if (lblStatus.getText().equals("NEW")) {
                StockIssueReceiveKey key = new StockIssueReceiveKey();
                key.setCompCode(Global.compCode);
                key.setVouNo(txtVou.getText());
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
        return status;
    }

    private void setVoucher(StockIssueReceive s, boolean local) {
        progress.setIndeterminate(true);
        this.io = s;
        Integer deptId = io.getDeptId();
        inventoryRepo.findLocation(io.getLocation()).doOnSuccess((t) -> {
            locaitonCompleter.setLocation(t);
        }).subscribe();      
        inventoryRepo.findTrader(s.getTraderCode()).doOnSuccess((t) -> {
            traderAutoCompleter.setTrader(t);
        }).subscribe();
        inventoryRepo.findLabourGroup(s.getLabourGroupCode()).doOnSuccess((t) -> {
            labourGroupAutoCompleter.setObject(t);
        }).subscribe();
        String vouNo = io.getKey().getVouNo();
        inventoryRepo.getStockIssRecDetail(vouNo).subscribe((t) -> {
            tableModel.setListDetail(t);
            tableModel.addNewRow();
            txtVou.setText(vouNo);
            txtDate.setDate(Util1.convertToDate(io.getVouDate()));
            txtRemark.setText(io.getRemark());  
            io.setVouLock(!io.getDeptId().equals(Global.deptId));
            if (io.isVouLock()) {
            lblStatus.setText("Voucher is Lock.");
            lblStatus.setForeground(Color.RED);
            disableForm(false);
            observer.selected("print", true);
            }else if (Util1.getBoolean(io.isDeleted())) {
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
            int row = tblStockIR.getRowCount();
            tblStockIR.setColumnSelectionInterval(0, 0);
            tblStockIR.setRowSelectionInterval(row - 1, row - 1);
            tblStockIR.requestFocus();
            progress.setIndeterminate(false);
        }, (e) -> {
            progress.setIndeterminate(false);
            JOptionPane.showMessageDialog(this, e.getMessage());
        });
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

    private void printVoucher(String vouNo) {
//        inventoryRepo.getTransferReport(vouNo).doOnSuccess((t) -> {
//            try {
//                if (t != null) {
//                    String a5 = ProUtil.getProperty(ProUtil.STOCK_IR_VOUCHER);
//                    String reportName = rdoA4.isSelected() ? "StockIssRecVoucher" : Util1.isNull(a5, "StockIssRecVoucherA5");
//                    String logoPath = String.format("images%s%s", File.separator, ProUtil.getProperty("logo.name"));
//                    Map<String, Object> param = new HashMap<>();
//                    param.put("p_print_date", Util1.getTodayDateTime());
//                    param.put("p_comp_name", Global.companyName);
//                    param.put("p_comp_address", Global.companyAddress);
//                    param.put("p_comp_phone", Global.companyPhone);
//                    param.put("p_logo_path", logoPath);
//                    param.put("p_logo_path", logoPath);
//                    param.put("p_tran_option", tranSource);
//                    String reportPath = String.format("report%s%s", File.separator, reportName.concat(".jasper"));
//                    ByteArrayInputStream jsonDataStream = new ByteArrayInputStream(Util1.listToByteArray(t));
//                    JsonDataSource ds = new JsonDataSource(jsonDataStream);
//                    JasperPrint js = JasperFillManager.fillReport(reportPath, param, ds);
//                    JasperViewer.viewReport(js, false);
//                }
//            } catch (JRException ex) {
//                JOptionPane.showMessageDialog(this, ex.getMessage());
//            }
//        }).subscribe();
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
        txtVou = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        txtDate = new com.toedter.calendar.JDateChooser();
        jLabel7 = new javax.swing.JLabel();
        txtLocation = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtCustomer = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtLG = new javax.swing.JTextField();
        lblStatus = new javax.swing.JLabel();
        lblRec = new javax.swing.JLabel();

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

        txtVou.setEditable(false);
        txtVou.setFont(Global.textFont);

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
                    .addComponent(txtDate, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                    .addComponent(txtVou))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtLocation, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                    .addComponent(txtCustomer))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtRemark, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
                    .addComponent(txtLG, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblVouNo)
                        .addComponent(txtVou, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7)
                        .addComponent(txtLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5))
                    .addComponent(txtRemark, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel8)
                        .addComponent(txtCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel11)
                        .addComponent(txtLG, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtDate, txtVou});

        lblStatus.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        lblStatus.setText("NEW");

        lblRec.setFont(Global.lableFont);
        lblRec.setText("Records");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblStatus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblRec, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(scroll))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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

    private void tblStockIRKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblStockIRKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_tblStockIRKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup chkGroupReport;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblRec;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblVouNo;
    private javax.swing.JScrollPane scroll;
    private javax.swing.JTable tblStockIR;
    private javax.swing.JTextField txtCustomer;
    private com.toedter.calendar.JDateChooser txtDate;
    private javax.swing.JTextField txtLG;
    private javax.swing.JTextField txtLocation;
    private javax.swing.JTextField txtRemark;
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
        clear();
    }

    @Override
    public void history() {
        if (dialog == null) {
            dialog = new StockIssRecHistoryDialog(Global.parentForm, tranSource);
            dialog.setInventoryRepo(inventoryRepo);
            dialog.setUserRepo(userRepo);
            dialog.setTitle(String.format("%s Voucher History Dialog", tranSource.equals("I") ? "Stock Issue" : "Stock Receive"));
//            tranSource.equals("I") ? "Issue Vou No" : "Receive Vou No"
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
        if (source.toString().equals("ISSREC-HISTORY")) {
            if (selectObj instanceof VStockIssueReceive v) {
                inventoryRepo.findStockIR(v.getVouNo(), v.isLocal()).subscribe((t) -> {
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
