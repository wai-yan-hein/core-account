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
import com.common.RowHeader;
import com.common.SelectionObserver;
import com.repo.UserRepo;
import com.common.Util1;
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.editor.StockCellEditor;
import com.inventory.editor.TraderAutoCompleter;
import com.inventory.entity.Trader;
import com.repo.InventoryRepo;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.inventory.entity.PurOrderHis;
import com.inventory.entity.PurOrderHisKey;
import com.inventory.entity.VPurOrder;
import com.inventory.ui.common.PurOrderHisTableModel;
import com.inventory.ui.entry.dialog.PurOrderHisDialog;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyListener;
import java.time.LocalDateTime;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

/**
 *
 * @author pann
 */
public class PurOrderHisEntry extends javax.swing.JPanel implements PanelControl, SelectionObserver, KeyListener {
    
    private final PurOrderHisTableModel tableModel = new PurOrderHisTableModel();
    private PurOrderHisDialog dialog;
    private InventoryRepo inventoryRepo;
    private UserRepo userRepo;
    private LocationAutoCompleter locaitonCompleter;
    private TraderAutoCompleter traderAutoCompleter;
    private PurOrderHis po = new PurOrderHis();
    private SelectionObserver observer;
    private JProgressBar progress;
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
     * Creates new form Purchase Order
     */
    public PurOrderHisEntry() {        
        initComponents();
        initDateListner();
        actionMapping();
    }
    
    public void initMain() {
        initTable();
        initRowHeader();
        initCombo();
        initFind();
        assingnDefault();
    }
    
    private void initFind() {
        findDialog = new FindDialog(Global.parentForm, tblPurOrder);
    }
    
    private void initRowHeader() {
        RowHeader header = new RowHeader();
        JList list = header.createRowHeader(tblPurOrder, 30);
        scroll.setRowHeaderView(list);
    }
    
    private void initDateListner() {
        txtDate.getDateEditor().getUiComponent().setName("txtDate");
        txtDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtDate.getDateEditor().getUiComponent().addFocusListener(fa);
        txtDueDate.getDateEditor().getUiComponent().setName("txtDueDate");
        txtDueDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtDueDate.getDateEditor().getUiComponent().addFocusListener(fa);
        txtVou.addKeyListener(this);
        txtRemark.addKeyListener(this);        
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
        tblPurOrder.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, solve);
        tblPurOrder.getActionMap().put(solve, new DeleteAction());
        
    }
    
    private class DeleteAction extends AbstractAction {
        
        @Override
        public void actionPerformed(ActionEvent e) {
            deleteTran();
        }
    }
    
    private void initCombo() {
//        locaitonCompleter = new LocationAutoCompleter(txtLocation, null, false, false);
//        locaitonCompleter.setObserver(this);
//        inventoryRepo.getLocation().subscribe((t) -> {
//            locaitonCompleter.setListLocation(t);
//        });        
        traderAutoCompleter = new TraderAutoCompleter(txtCustomer, inventoryRepo, null, false, "CUS");
        traderAutoCompleter.setObserver(this);
    }
    
    private void initTable() {
        tableModel.setVouDate(txtDate);
        tableModel.setDueDate(txtDueDate);
        tableModel.setLblRec(lblRec);
        tableModel.setInventoryRepo(inventoryRepo);
        tableModel.addNewRow();
        tableModel.setParent(tblPurOrder);
        tableModel.setObserver(this);
        tblPurOrder.setModel(tableModel);
        tblPurOrder.getTableHeader().setFont(Global.tblHeaderFont);
        tblPurOrder.getColumnModel().getColumn(0).setPreferredWidth(10);
        tblPurOrder.getColumnModel().getColumn(1).setPreferredWidth(250);
        tblPurOrder.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblPurOrder.getColumnModel().getColumn(3).setPreferredWidth(100);
        tblPurOrder.getColumnModel().getColumn(4).setPreferredWidth(100);
        tblPurOrder.getColumnModel().getColumn(5).setPreferredWidth(100);
        tblPurOrder.getColumnModel().getColumn(6).setPreferredWidth(100);
        tblPurOrder.getColumnModel().getColumn(7).setPreferredWidth(100);
        tblPurOrder.getColumnModel().getColumn(8).setPreferredWidth(100);
        tblPurOrder.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor(inventoryRepo));
        tblPurOrder.getColumnModel().getColumn(1).setCellEditor(new StockCellEditor(inventoryRepo));
        tblPurOrder.getColumnModel().getColumn(2).setCellEditor(new AutoClearEditor());
        tblPurOrder.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());
        tblPurOrder.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());
        tblPurOrder.getColumnModel().getColumn(5).setCellEditor(new AutoClearEditor());
        tblPurOrder.getColumnModel().getColumn(6).setCellEditor(new AutoClearEditor());
        tblPurOrder.getColumnModel().getColumn(7).setCellEditor(new AutoClearEditor());
        tblPurOrder.setDefaultRenderer(Object.class, new DecimalFormatRender());
        tblPurOrder.setDefaultRenderer(Float.class, new DecimalFormatRender());
        tblPurOrder.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblPurOrder.setCellSelectionEnabled(true);
        tblPurOrder.changeSelection(0, 0, false, false);
        tblPurOrder.requestFocus();
    }
    
    private void deleteVoucher() {
        String status = lblStatus.getText();
        switch (status) {
            case "EDIT" -> {
                int yes_no = JOptionPane.showConfirmDialog(Global.parentForm,
                        "Are you sure to delete?", "Purchase Order Voucher delete.", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                if (yes_no == 0) {
                    inventoryRepo.delete(po.getKey()).subscribe((t) -> {
                        clear();
                    });
                }
            }
            case "DELETED" -> {
                int yes_no = JOptionPane.showConfirmDialog(this,
                        "Are you sure to restore?", "Purchase Order Voucher Restore.", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (yes_no == 0) {
                    po.setDeleted(false);
                    inventoryRepo.restore(po.getKey()).doOnSuccess((t) -> {
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
        int row = tblPurOrder.convertRowIndexToModel(tblPurOrder.getSelectedRow());
        if (row >= 0) {
            if (tblPurOrder.getCellEditor() != null) {
                tblPurOrder.getCellEditor().stopCellEditing();
            }
            int yes_no = JOptionPane.showConfirmDialog(this,
                    "Are you sure to delete?", "Purchase Order Transaction delete.", JOptionPane.YES_NO_OPTION);
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
            po.setListPurOrderDetail(tableModel.getListDetail());
            po.setListDel(tableModel.getDelList());            
            inventoryRepo.savePurOrder(po)
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
//        inventoryRepo.getDefaultLocation().doOnSuccess((tt) -> {
//            locaitonCompleter.setLocation(tt);
//        }).subscribe();
        inventoryRepo.getDefaultCustomer().subscribe((t) -> {
            traderAutoCompleter.setTrader(t);
        });
        txtDate.setDate(Util1.getTodayDate());
        txtDueDate.setDate(Util1.getTodayDate());        
        txtVou.setText(null);
        txtRemark.setText(null);        
    }
    
    private void clear() {
        assingnDefault();
        po = new PurOrderHis();
        lblStatus.setForeground(Color.GREEN);
        lblStatus.setText("NEW");        
        txtRemark.setText(null);
        tableModel.clear();
        tableModel.addNewRow();
        txtDate.setDate(Util1.getTodayDate());
        txtDueDate.setDate(Util1.getTodayDate());
        progress.setIndeterminate(false);
        txtVou.setText(null);
        traderAutoCompleter.setTrader(null);        
        disableForm(true);        
        lblStatus.setForeground(Color.green);
        lblStatus.setText("NEW");
    }
    
    private void focusOnTable() {
        int rc = tblPurOrder.getRowCount();
        if (rc > 1) {
            tblPurOrder.setRowSelectionInterval(rc - 1, rc - 1);
            tblPurOrder.setColumnSelectionInterval(0, 0);
            tblPurOrder.requestFocus();
        } else {
            txtDate.requestFocusInWindow();
        }
    }
    
    private boolean isValidEntry() {
        boolean status = true;
        if (lblStatus.getText().equals("DELETED")) {
            status = true;
            clear();
        } else if (!Util1.isDateBetween(txtDate.getDate())) {
            JOptionPane.showMessageDialog(this, "Invalid Date.",
                    "Validation.", JOptionPane.ERROR_MESSAGE);
            txtDate.requestFocus();
            status = false;
        } else {
            po.setRemark(txtRemark.getText());
            po.setVouDate(Util1.convertToLocalDateTime(txtDate.getDate()));
            po.setDueDate(Util1.convertToLocalDateTime(txtDueDate.getDate()));
//            po.setLocation(locaitonCompleter.getLocation().getKey().getLocCode());
            po.setStatus(lblStatus.getText());
            Trader t = traderAutoCompleter.getTrader();
            if (t != null) {
                String traderCode = t.getKey().getCode();
                po.setTraderCode(traderCode);
            }
            if (lblStatus.getText().equals("NEW")) {
                PurOrderHisKey key = new PurOrderHisKey();
                key.setCompCode(Global.compCode);
                key.setVouNo(txtVou.getText());                
                po.setDeptId(Global.deptId);
                po.setKey(key);
                po.setCreatedBy(Global.loginUser.getUserCode());
                po.setCreatedDate(LocalDateTime.now());
                po.setMacId(Global.macId);
                po.setDeleted(Boolean.FALSE);
            } else {
                po.setUpdatedBy(Global.loginUser.getUserCode());
            }
        }
        return status;
    }
    
    private void setVoucher(PurOrderHis s, boolean local) {
        progress.setIndeterminate(true);
        this.po = s;
        Integer deptId = po.getDeptId();
//        inventoryRepo.findLocation(po.getLocation()).doOnSuccess((t) -> {
//            locaitonCompleter.setLocation(t);
//        }).subscribe();      
        inventoryRepo.findTrader(s.getTraderCode()).doOnSuccess((t) -> {
            traderAutoCompleter.setTrader(t);
        }).subscribe();        
        String vouNo = po.getKey().getVouNo();
        inventoryRepo.getPurOrderHisDetail(vouNo).subscribe((t) -> {
            tableModel.setListDetail(t);
            tableModel.addNewRow();
            txtVou.setText(vouNo);
            txtDate.setDate(Util1.convertToDate(po.getVouDate()));
            txtDueDate.setDate(Util1.convertToDate(po.getDueDate()));
            txtRemark.setText(po.getRemark());            
            po.setVouLock(!po.getDeptId().equals(Global.deptId));
            if (po.isVouLock()) {
                lblStatus.setText("Voucher is Lock.");
                lblStatus.setForeground(Color.RED);
                disableForm(false);
                observer.selected("print", true);
            } else if (Util1.getBoolean(po.isDeleted())) {
                lblStatus.setText("DELETED");
                lblStatus.setForeground(Color.red);
                disableForm(false);
                observer.selected("delete", true);
            } else if (DateLockUtil.isLockDate(po.getVouDate())) {
                lblStatus.setText(DateLockUtil.MESSAGE);
                lblStatus.setForeground(Color.RED);
                disableForm(false);
            } else {
                lblStatus.setText("EDIT");
                lblStatus.setForeground(Color.blue);
                disableForm(true);
            }
            int row = tblPurOrder.getRowCount();
            tblPurOrder.setColumnSelectionInterval(0, 0);
            tblPurOrder.setRowSelectionInterval(row - 1, row - 1);
            tblPurOrder.requestFocus();
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
        tblPurOrder = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        lblVouNo = new javax.swing.JLabel();
        txtVou = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        txtDate = new com.toedter.calendar.JDateChooser();
        jLabel8 = new javax.swing.JLabel();
        txtCustomer = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtDueDate = new com.toedter.calendar.JDateChooser();
        lblStatus = new javax.swing.JLabel();
        lblRec = new javax.swing.JLabel();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        tblPurOrder.setAutoCreateRowSorter(true);
        tblPurOrder.setFont(Global.textFont);
        tblPurOrder.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblPurOrder.setRowHeight(Global.tblRowHeight);
        tblPurOrder.setShowHorizontalLines(true);
        tblPurOrder.setShowVerticalLines(true);
        tblPurOrder.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblPurOrderKeyReleased(evt);
            }
        });
        scroll.setViewportView(tblPurOrder);

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
        jLabel3.setText("Order Date");

        txtDate.setDateFormatString("dd/MM/yyyy");
        txtDate.setFont(Global.textFont);
        txtDate.setMaxSelectableDate(new java.util.Date(253370745114000L));

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Customer");

        txtCustomer.setFont(Global.textFont);
        txtCustomer.setName("txtRefNo"); // NOI18N

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Due Date");

        txtDueDate.setDateFormatString("dd/MM/yyyy");
        txtDueDate.setFont(Global.textFont);
        txtDueDate.setMaxSelectableDate(new java.util.Date(253370745114000L));

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
                    .addComponent(txtVou)
                    .addComponent(txtDate, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCustomer, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                    .addComponent(txtRemark))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDueDate, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
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
                        .addComponent(jLabel8)
                        .addComponent(txtCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel4)
                    .addComponent(txtDueDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel5)
                        .addComponent(txtRemark, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(scroll)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void tblPurOrderKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblPurOrderKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_tblPurOrderKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup chkGroupReport;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblRec;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblVouNo;
    private javax.swing.JScrollPane scroll;
    private javax.swing.JTable tblPurOrder;
    private javax.swing.JTextField txtCustomer;
    private com.toedter.calendar.JDateChooser txtDate;
    private com.toedter.calendar.JDateChooser txtDueDate;
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
            dialog = new PurOrderHisDialog(Global.parentForm);
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
        findDialog.setVisible(!findDialog.isVisible());
    }
    
    @Override
    public void selected(Object source, Object selectObj) {
        if (source.toString().equals("PURORDER-HISTORY")) {
            if (selectObj instanceof VPurOrder v) {
                inventoryRepo.findPurOrder(v.getVouNo(), v.isLocal()).subscribe((t) -> {
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
                }
            }            
            case "txtCustomer" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtRemark.requestFocus();
                }
            }            
        }
    }
}
