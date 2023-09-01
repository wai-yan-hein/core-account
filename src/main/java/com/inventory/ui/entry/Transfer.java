/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry;

import com.common.DecimalFormatRender;
import java.awt.event.KeyEvent;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.common.Global;
import com.common.PanelControl;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.repo.UserRepo;
import com.common.Util1;
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.editor.StockCellEditor;
import com.inventory.model.Location;
import com.inventory.model.TransferHis;
import com.inventory.model.TransferHisKey;
import com.inventory.model.VTransfer;
import com.repo.InventoryRepo;
import com.inventory.ui.common.TransferTableModel;
import com.inventory.ui.entry.dialog.TransferHistoryDialog;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.inventory.ui.setup.dialog.common.StockUnitEditor;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
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
@Component
public class Transfer extends javax.swing.JPanel implements PanelControl, SelectionObserver, KeyListener {

    private final TransferTableModel tranTableModel = new TransferTableModel();
    private TransferHistoryDialog dialog;
    @Autowired
    private InventoryRepo inventoryRepo;
    @Autowired
    private UserRepo userRepo;
    private LocationAutoCompleter fromLocaitonCompleter;
    private LocationAutoCompleter toLocaitonCompleter;
    private TransferHis io = new TransferHis();
    private SelectionObserver observer;
    private JProgressBar progress;

    public SelectionObserver getObserver() {
        return observer;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public JProgressBar getProgress() {
        return progress;
    }

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }

    /**
     * Creates new form StockInOutEntry
     */
    public Transfer() {
        initComponents();
        initDateListner();
        actionMapping();
    }

    public void initMain() {
        initTable();
        initCombo();
        clear();
    }

    private void initDateListner() {
        txtDate.getDateEditor().getUiComponent().setName("txtDate");
        txtDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtDate.getDateEditor().getUiComponent().addFocusListener(fa);
        txtVou.addKeyListener(this);
        txtRemark.addKeyListener(this);
        txtRefNo.addKeyListener(this);
        txtFrom.addKeyListener(this);
        txtTo.addKeyListener(this);
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
        inventoryRepo.getLocation().subscribe((t) -> {
            fromLocaitonCompleter.setListLocation(t);
            toLocaitonCompleter.setListLocation(t);
        });
    }

    private void initTable() {
        tranTableModel.setVouDate(txtDate);
        tranTableModel.setLblRec(lblRec);
        tranTableModel.setInventoryRepo(inventoryRepo);
        tranTableModel.addNewRow();
        tranTableModel.setParent(tblTransfer);
        tranTableModel.setObserver(this);
        tblTransfer.setModel(tranTableModel);
        tblTransfer.getTableHeader().setFont(Global.tblHeaderFont);
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
        inventoryRepo.getStockUnit().subscribe((t) -> {
            tblTransfer.getColumnModel().getColumn(4).setCellEditor(new StockUnitEditor(t));
            tblTransfer.getColumnModel().getColumn(6).setCellEditor(new StockUnitEditor(t));
        });
        tblTransfer.setDefaultRenderer(Object.class, new DecimalFormatRender());
        tblTransfer.setDefaultRenderer(Float.class, new DecimalFormatRender());
        tblTransfer.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblTransfer.setCellSelectionEnabled(true);
        tblTransfer.changeSelection(0, 0, false, false);
        tblTransfer.requestFocus();
    }

    private void deleteVoucher() {
        String status = lblStatus.getText();
        switch (status) {
            case "EDIT" -> {
                int yes_no = JOptionPane.showConfirmDialog(Global.parentForm,
                        "Are you sure to delete?", "Transfer Voucher delete.", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                if (yes_no == 0) {
                    inventoryRepo.delete(io.getKey()).subscribe((t) -> {
                        clear();
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

    public boolean saveVoucher(boolean print) {
        boolean status = false;
        if (isValidEntry() && tranTableModel.isValidEntry()) {
            observer.selected("save", false);
            progress.setIndeterminate(true);
            io.setListTD(tranTableModel.getListTransfer());
            io.setDelList(tranTableModel.getDeleteList());
            inventoryRepo.save(io)
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
        inventoryRepo.getDefaultLocation().subscribe((tt) -> {
            fromLocaitonCompleter.setLocation(tt);
        });
    }

    private void clear() {
        assingnDefault();
        io = new TransferHis();
        lblStatus.setForeground(Color.GREEN);
        lblStatus.setText("NEW");
        txtRefNo.setText(null);
        txtRemark.setText(null);
        tranTableModel.clear();
        tranTableModel.addNewRow();
        txtDate.setDate(Util1.getTodayDate());
        progress.setIndeterminate(false);
        txtVou.setText(null);
        disableForm(true);
    }

    private void focusOnTable() {
        int rc = tblTransfer.getRowCount();
        if (rc > 1) {
            tblTransfer.setRowSelectionInterval(rc - 1, rc - 1);
            tblTransfer.setColumnSelectionInterval(0, 0);
            tblTransfer.requestFocus();
        } else {
            txtDate.requestFocusInWindow();
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
            clear();
            status = false;
        } else if (fromLoc.getKey().getLocCode().equals(toLoc.getKey().getLocCode())) {
            status = false;
            JOptionPane.showMessageDialog(this, "Can't transfer the same location.");
            txtTo.requestFocus();
        } else {
            io.setRefNo(txtRefNo.getText());
            io.setRemark(txtRemark.getText());
            io.setVouDate(Util1.convertToLocalDateTime(txtDate.getDate()));
            io.setLocCodeFrom(fromLocaitonCompleter.getLocation().getKey().getLocCode());
            io.setLocCodeTo(toLocaitonCompleter.getLocation().getKey().getLocCode());
            io.setStatus(lblStatus.getText());
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
        inventoryRepo.findLocation(io.getLocCodeFrom()).subscribe((t) -> {
            fromLocaitonCompleter.setLocation(t);
        });
        inventoryRepo.findLocation(io.getLocCodeTo()).subscribe((t) -> {
            toLocaitonCompleter.setLocation(t);
        });
        String vouNo = io.getKey().getVouNo();
        inventoryRepo.getTransferDetail(vouNo, deptId, local).subscribe((t) -> {
            tranTableModel.setListTransfer(t);
            tranTableModel.addNewRow();
            txtVou.setText(vouNo);
            txtDate.setDate(Util1.convertToDate(io.getVouDate()));
            txtRemark.setText(io.getRemark());
            txtRefNo.setText(io.getRefNo());
            if (io.isVouLock()) {
                lblStatus.setText("Voucher is locked.");
                lblStatus.setForeground(Color.RED);
                disableForm(false);
            } else if (Util1.getBoolean(io.isDeleted())) {
                lblStatus.setText("DELETED");
                lblStatus.setForeground(Color.red);
                disableForm(false);
            } else {
                lblStatus.setText("EDIT");
                lblStatus.setForeground(Color.blue);
                disableForm(true);
            }
            int row = tblTransfer.getRowCount();
            tblTransfer.setColumnSelectionInterval(0, 0);
            tblTransfer.setRowSelectionInterval(row - 1, row - 1);
            tblTransfer.requestFocus();
            progress.setIndeterminate(false);
        }, (e) -> {
            progress.setIndeterminate(false);
            JOptionPane.showMessageDialog(this, e.getMessage());
        });

    }

    private void disableForm(boolean status) {
        txtDate.setEnabled(status);
        txtRemark.setEnabled(status);
        txtRefNo.setEnabled(status);
        tblTransfer.setEnabled(status);
        txtFrom.setEnabled(status);
        txtTo.setEnabled(status);
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
        observer.selected("refresh", false);
    }

    private void printVoucher(String vouNo) {
        inventoryRepo.getTransferReport(vouNo).subscribe((t) -> {
            try {
                if (t != null) {
                    String reportName = "TransferVoucher";
                    String logoPath = String.format("images%s%s", File.separator, ProUtil.getProperty("logo.name"));
                    Map<String, Object> param = new HashMap<>();
                    param.put("p_print_date", Util1.getTodayDateTime());
                    param.put("p_comp_name", Global.companyName);
                    param.put("p_comp_address", Global.companyAddress);
                    param.put("p_comp_phone", Global.companyPhone);
                    param.put("p_logo_path", logoPath);
                    String reportPath = String.format("report%s%s", File.separator, reportName.concat(".jasper"));
                    ByteArrayInputStream jsonDataStream = new ByteArrayInputStream(Util1.listToByteArray(t));
                    JsonDataSource ds = new JsonDataSource(jsonDataStream);
                    JasperPrint js = JasperFillManager.fillReport(reportPath, param, ds);
                    JasperViewer.viewReport(js, false);
                }
            } catch (JRException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }, (e) -> {
            JOptionPane.showMessageDialog(this, e.getMessage());
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
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
        lblStatus = new javax.swing.JLabel();
        lblRec = new javax.swing.JLabel();

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
        jScrollPane1.setViewportView(tblTransfer);

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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(txtVou, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDate, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addComponent(txtFrom, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel9)
                .addGap(18, 18, 18)
                .addComponent(txtTo, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(txtRefNo, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(txtRemark, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(txtRefNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)
                        .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7)
                        .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9)
                        .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(txtVou, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)))
                .addContainerGap(7, Short.MAX_VALUE))
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
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblStatus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblRec, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblStatus)
                    .addComponent(lblRec))
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
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblRec;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblTransfer;
    private com.toedter.calendar.JDateChooser txtDate;
    private javax.swing.JTextField txtFrom;
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
        clear();
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
            dialog.setSize(Global.width - 100, Global.height - 100);
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
