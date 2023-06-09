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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.common.Global;
import com.common.PanelControl;
import com.common.SelectionObserver;
import com.user.common.UserRepo;
import com.common.Util1;
import com.inventory.editor.LocationCellEditor;
import com.inventory.editor.StockCellEditor;
import com.inventory.editor.VouStatusAutoCompleter;
import com.inventory.model.Location;
import com.inventory.model.OPHis;
import com.inventory.model.OPHisDetail;
import com.inventory.model.StockIOKey;
import com.inventory.model.StockInOut;
import com.inventory.model.StockInOutDetail;
import com.inventory.model.StockInOutKey;
import com.inventory.model.StockUnit;
import com.inventory.model.VStockIO;
import com.inventory.ui.common.InventoryRepo;
import com.inventory.ui.common.StockInOutTableModel;
import com.inventory.ui.entry.dialog.OPHistoryDialog;
import com.inventory.ui.entry.dialog.StockIOHistoryDialog;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.inventory.ui.setup.dialog.common.StockUnitEditor;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyListener;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 *
 * @author Lenovo
 */
@Component
public class StockInOutEntry extends javax.swing.JPanel implements PanelControl, SelectionObserver, KeyListener {

    private static final Logger log = LoggerFactory.getLogger(StockInOutEntry.class);
    private final StockInOutTableModel outTableModel = new StockInOutTableModel();
    private StockIOHistoryDialog dialog;
    @Autowired
    private WebClient inventoryApi;
    @Autowired
    private InventoryRepo inventoryRepo;
    @Autowired
    private UserRepo userRepo;
    private VouStatusAutoCompleter vouStatusAutoCompleter;
    private StockInOut io = new StockInOut();
    private SelectionObserver observer;
    private JProgressBar progress;
    private Mono<List<Location>> monoLoc;

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
    public StockInOutEntry() {
        initComponents();
        initTextBoxFormat();
        initDateListner();
        actionMapping();
    }

    public void initMain() {
        initTable();
        initCombo();
        clear();
    }

    private void actionMapping() {
        String solve = "delete";
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        tblStock.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, solve);
        tblStock.getActionMap().put(solve, new DeleteAction());
    }

    private class DeleteAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            deleteTran();
        }
    }

    private void initDateListner() {
        txtDate.getDateEditor().getUiComponent().setName("txtDate");
        txtDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtDate.getDateEditor().getUiComponent().addFocusListener(fa);
        txtRemark.addKeyListener(this);
        txtDesp.addKeyListener(this);
        txtVouType.addKeyListener(this);
    }
    private final FocusAdapter fa = new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            ((JTextFieldDateEditor) e.getSource()).selectAll();
        }

    };

    private void initTextBoxFormat() {
        txtInQty.setFormatterFactory(Util1.getDecimalFormat());
        txtOutQty.setFormatterFactory(Util1.getDecimalFormat());
        txtCost.setFormatterFactory(Util1.getDecimalFormat());
    }

    private void initCombo() {
        vouStatusAutoCompleter = new VouStatusAutoCompleter(txtVouType, inventoryRepo, null, false);
        vouStatusAutoCompleter.setVoucher(null);
    }

    private void initTable() {
        monoLoc = inventoryRepo.getLocation();
        Mono<List<StockUnit>> monoUnit = inventoryRepo.getStockUnit();
        outTableModel.setVouDate(txtDate);
        outTableModel.setInventoryRepo(inventoryRepo);
        outTableModel.setLblRec(lblRec);
        outTableModel.addNewRow();
        outTableModel.setParent(tblStock);
        outTableModel.setObserver(this);
        tblStock.setModel(outTableModel);
        tblStock.getTableHeader().setFont(Global.tblHeaderFont);
        tblStock.getColumnModel().getColumn(0).setPreferredWidth(100);
        tblStock.getColumnModel().getColumn(1).setPreferredWidth(250);
        tblStock.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblStock.getColumnModel().getColumn(3).setPreferredWidth(50);
        tblStock.getColumnModel().getColumn(4).setPreferredWidth(50);
        tblStock.getColumnModel().getColumn(5).setPreferredWidth(50);
        tblStock.getColumnModel().getColumn(6).setPreferredWidth(50);
        tblStock.getColumnModel().getColumn(7).setPreferredWidth(50);
        tblStock.getColumnModel().getColumn(8).setPreferredWidth(50);
        tblStock.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor(inventoryRepo));
        tblStock.getColumnModel().getColumn(1).setCellEditor(new StockCellEditor(inventoryRepo));
        monoLoc.subscribe((t) -> {
            tblStock.getColumnModel().getColumn(2).setCellEditor(new LocationCellEditor(t));
        });
        tblStock.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());
        monoUnit.subscribe((t) -> {
            tblStock.getColumnModel().getColumn(4).setCellEditor(new StockUnitEditor(t));
        });
        tblStock.getColumnModel().getColumn(5).setCellEditor(new AutoClearEditor());
        monoUnit.subscribe((t) -> {
            tblStock.getColumnModel().getColumn(6).setCellEditor(new StockUnitEditor(t));
        });
        tblStock.getColumnModel().getColumn(7).setCellEditor(new AutoClearEditor());
        tblStock.setDefaultRenderer(Object.class, new DecimalFormatRender());
        tblStock.setDefaultRenderer(Float.class, new DecimalFormatRender());
        tblStock.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblStock.setCellSelectionEnabled(true);
        tblStock.changeSelection(0, 0, false, false);
        tblStock.requestFocus();
    }

    private void deleteVoucher() {
        String status = lblStatus.getText();
        switch (status) {
            case "EDIT" -> {
                int yes_no = JOptionPane.showConfirmDialog(Global.parentForm,
                        "Are you sure to delete?", "Stock In/Out Voucher delete.", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                if (yes_no == 0) {
                    inventoryRepo.delete(io.getKey()).subscribe((t) -> {
                        clear();
                    });
                }
            }
            case "DELETED" -> {
                int yes_no = JOptionPane.showConfirmDialog(this,
                        "Are you sure to restore?", "Stock In/Out Voucher Restore.", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
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
        int row = tblStock.convertRowIndexToModel(tblStock.getSelectedRow());
        if (row >= 0) {
            if (tblStock.getCellEditor() != null) {
                tblStock.getCellEditor().stopCellEditing();
            }
            int yes_no = JOptionPane.showConfirmDialog(this,
                    "Are you sure to delete?", "Stock Transaction delete.", JOptionPane.YES_NO_OPTION);
            if (yes_no == 0) {
                outTableModel.delete(row);
                calTotalAmt();
            }
        }
    }

    public boolean saveVoucher() {
        boolean status = false;
        if (isValidEntry() && outTableModel.isValidEntry()) {
            observer.selected("save", true);
            progress.setIndeterminate(true);
            io.setListSH(outTableModel.getListStock());
            io.setListDel(outTableModel.getDeleteList());
            inventoryRepo.save(io)
                    .subscribe((t) -> {
                        clear();
                        focusOnTable();
                    }, (e) -> {
                        observer.selected("save", true);
                        JOptionPane.showMessageDialog(this, e.getMessage());
                        progress.setIndeterminate(false);
                    });

        }
        return status;
    }

    private void clear() {
        io = new StockInOut();
        lblStatus.setForeground(Color.GREEN);
        lblStatus.setText("NEW");
        txtDesp.setText(null);
        txtRemark.setText(null);
        txtInQty.setValue(0.0);
        txtOutQty.setValue(0.0);
        vouStatusAutoCompleter.setVoucher(null);
        outTableModel.clear();
        outTableModel.addNewRow();
        txtDate.setDate(Util1.getTodayDate());
        progress.setIndeterminate(false);
        txtVou.setText(null);
        disableForm(true);
        calTotalAmt();
    }

    private void calTotalAmt() {
        float ttlInQty = 0.0f;
        float ttlOutQty = 0.0f;
        float ttlPrice = 0.0f;
        List<StockInOutDetail> listIO = outTableModel.getListStock();
        if (!listIO.isEmpty()) {
            for (StockInOutDetail s : listIO) {
                ttlInQty += Util1.getFloat(s.getInQty());
                ttlOutQty += Util1.getFloat(s.getOutQty());
                ttlPrice += Util1.getFloat(s.getCostPrice());
            }
        }
        txtInQty.setValue(ttlInQty);
        txtOutQty.setValue(ttlOutQty);
        txtCost.setValue(ttlPrice);

    }

    private void focusOnTable() {
        int rc = tblStock.getRowCount();
        if (rc > 1) {
            tblStock.setRowSelectionInterval(rc - 1, rc - 1);
            tblStock.setColumnSelectionInterval(0, 0);
            tblStock.requestFocus();
        } else {
            txtDate.requestFocusInWindow();
        }
    }

    private boolean isValidEntry() {
        boolean status = true;
        if (vouStatusAutoCompleter.getVouStatus() == null) {
            JOptionPane.showMessageDialog(this, "Select Voucher Status.");
            status = false;
            txtVouType.requestFocus();
        } else if (lblStatus.getText().equals("DELETED")) {
            clear();
            status = false;
        } else if (Util1.getFloat(txtInQty.getValue()) + Util1.getFloat(txtOutQty.getValue()) <= 0) {
            status = false;
            JOptionPane.showMessageDialog(this, "No records.");
        } else {
            io.setDescription(txtDesp.getText());
            io.setRemark(txtRemark.getText());
            io.setVouDate(txtDate.getDate());
            io.setVouStatusCode(vouStatusAutoCompleter.getVouStatus().getKey().getCode());
            if (lblStatus.getText().equals("NEW")) {
                StockIOKey key = new StockIOKey();
                key.setCompCode(Global.compCode);
                key.setDeptId(Global.deptId);
                key.setVouNo(null);
                io.setKey(key);
                io.setCreatedBy(Global.loginUser.getUserCode());
                io.setCreatedDate(Util1.getTodayDate());
                io.setMacId(Global.macId);
                io.setDeleted(Boolean.FALSE);
            } else {
                io.setUpdatedBy(Global.loginUser.getUserCode());
            }
        }
        return status;
    }

    private void setVoucher(StockInOut s) {
        outTableModel.clear();
        txtCost.setValue(0);
        txtInQty.setValue(0);
        txtOutQty.setValue(0);
        if (s != null) {
            progress.setIndeterminate(true);
            io = s;
            inventoryRepo.findVouStatus(io.getVouStatusCode(), io.getKey().getDeptId()).subscribe((t) -> {
                vouStatusAutoCompleter.setVoucher(t);
            });
            String vouNo = io.getKey().getVouNo();
            inventoryRepo.searchStkIODetail(vouNo, io.getKey().getDeptId())
                    .subscribe((t) -> {
                        t.forEach((a) -> {
                            outTableModel.addObject(a);
                        });

                    }, (e) -> {
                        progress.setIndeterminate(false);
                        JOptionPane.showMessageDialog(this, e.getMessage());
                    }, () -> {
                        outTableModel.addNewRow();
                        txtVou.setText(vouNo);
                        txtDate.setDate(Util1.toDateFormat(io.getVouDate(), "dd/MM/yyyy"));
                        txtRemark.setText(io.getRemark());
                        txtDesp.setText(io.getDescription());
                        if (Util1.getBoolean(io.getDeleted())) {
                            lblStatus.setText("DELETED");
                            lblStatus.setForeground(Color.red);
                            disableForm(false);
                        } else {
                            lblStatus.setText("EDIT");
                            lblStatus.setForeground(Color.blue);
                            disableForm(true);
                        }
                        calTotalAmt();
                        focusOnTable();
                        progress.setIndeterminate(false);
                    });
        }
    }

    private void disableForm(boolean status) {
        txtDate.setEnabled(status);
        txtRemark.setEnabled(status);
        txtDesp.setEnabled(status);
        tblStock.setEnabled(status);
        txtVouType.setEnabled(status);
        observer.selected("save", status);
        observer.selected("delete", status);
        observer.selected("print", status);

    }

    public void historyOP() {
        try {
            OPHistoryDialog dialog = new OPHistoryDialog(Global.parentForm);
            dialog.setUserRepo(userRepo);
            dialog.setInventoryRepo(inventoryRepo);
            dialog.setWebClient(inventoryApi);
            dialog.setObserver(this);
            dialog.initMain();
            dialog.setSize(Global.width - 200, Global.height - 200);
            dialog.setLocationRelativeTo(null);
            dialog.setIconImage(new ImageIcon(getClass().getResource("/images/search.png")).getImage());
            dialog.setVisible(true);
        } catch (Exception e) {
            log.error(String.format("historyOPhistoryOP: %s", e.getMessage()));
        }

    }

    private void importOP(OPHis op) {
        outTableModel.clear();
        Mono<ResponseEntity<List<OPHisDetail>>> result = inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-opening-detail")
                .queryParam("vouNo", op.getKey().getVouNo())
                .queryParam("compCode", op.getKey().getCompCode())
                .queryParam("deptId", op.getKey().getDeptId())
                .build())
                .retrieve().toEntityList(OPHisDetail.class);
        result.subscribe((t) -> {
            List<OPHisDetail> list = t.getBody();
            for (int i = 0; i < list.size(); i++) {
                OPHisDetail his = list.get(i);
                StockInOutDetail iod = new StockInOutDetail();
                StockInOutKey key = new StockInOutKey();
                iod.setStockCode(his.getStockCode());
                iod.setStockName(his.getStockName());
                iod.setInQty(his.getQty());
                iod.setCostPrice(his.getPrice());
                iod.setInUnitCode(his.getUnitCode());
                iod.setLocCode(op.getLocCode());
                iod.setLocName(op.getLocName());
                key.setUniqueId(i + 1);
                iod.setKey(key);
//                iod.setUniqueId(i + 1);
                outTableModel.addObject(iod);
            }
            calTotalAmt();
            outTableModel.setNegative(true);
            focusOnTable();
            progress.setIndeterminate(false);
        }, (e) -> {
            progress.setIndeterminate(false);
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
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
        tblStock = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtDesp = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtVou = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        txtDate = new com.toedter.calendar.JDateChooser();
        jLabel7 = new javax.swing.JLabel();
        txtVouType = new javax.swing.JTextField();
        txtOutQty = new javax.swing.JFormattedTextField();
        txtInQty = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        txtCost = new javax.swing.JFormattedTextField();
        jLabel8 = new javax.swing.JLabel();
        lblRec = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        tblStock.setAutoCreateRowSorter(true);
        tblStock.setFont(Global.textFont);
        tblStock.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblStock.setRowHeight(Global.tblRowHeight);
        tblStock.setShowHorizontalLines(true);
        tblStock.setShowVerticalLines(true);
        tblStock.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblStockKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblStock);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Description");

        txtDesp.setFont(Global.textFont);
        txtDesp.setName("txtDesp"); // NOI18N

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
        jLabel7.setText("Vou Type");

        txtVouType.setFont(Global.textFont);
        txtVouType.setName("txtVouType"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(txtVou)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(txtDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addComponent(txtVouType)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(txtDesp)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(txtRemark)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(txtDesp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)
                        .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7)
                        .addComponent(txtVouType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(txtVou, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3))
                    .addComponent(txtDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        txtOutQty.setEditable(false);
        txtOutQty.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtOutQty.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtOutQty.setFont(Global.amtFont);

        txtInQty.setEditable(false);
        txtInQty.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtInQty.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtInQty.setFont(Global.amtFont);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Total Out Qty");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Total In Qty");

        lblStatus.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        lblStatus.setText("NEW");

        txtCost.setEditable(false);
        txtCost.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtCost.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtCost.setFont(Global.amtFont);

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Total Cost");

        lblRec.setFont(Global.lableFont);
        lblRec.setText("Records");

        jButton1.setText("Import");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(lblStatus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblRec, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 212, Short.MAX_VALUE)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(txtInQty, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(txtOutQty, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(jLabel8)
                        .addGap(18, 18, 18)
                        .addComponent(txtCost, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 393, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtOutQty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtInQty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)
                        .addComponent(jLabel1)
                        .addComponent(txtCost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8)
                        .addComponent(lblRec)
                        .addComponent(jButton1))
                    .addComponent(lblStatus))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observer.selected("control", this);
        focusOnTable();
    }//GEN-LAST:event_formComponentShown

    private void tblStockKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblStockKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_tblStockKeyReleased

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        historyOP();
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblRec;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblStock;
    private javax.swing.JFormattedTextField txtCost;
    private com.toedter.calendar.JDateChooser txtDate;
    private javax.swing.JTextField txtDesp;
    private javax.swing.JFormattedTextField txtInQty;
    private javax.swing.JFormattedTextField txtOutQty;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JFormattedTextField txtVou;
    private javax.swing.JTextField txtVouType;
    // End of variables declaration//GEN-END:variables

    @Override
    public void save() {
        saveVoucher();
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
            dialog = new StockIOHistoryDialog(Global.parentForm);
            dialog.setInventoryRepo(inventoryRepo);
            dialog.setUserRepo(userRepo);
            dialog.setIconImage(new ImageIcon(getClass().getResource("/images/search.png")).getImage());
            dialog.setWebClient(inventoryApi);
            dialog.setObserver(this);
            dialog.initMain();
            dialog.setSize(Global.width - 100, Global.height - 100);
            dialog.setLocationRelativeTo(null);
        }
        dialog.search();
    }

    @Override
    public void print() {
    }

    @Override
    public void refresh() {
    }

    @Override
    public void filter() {
    }

    @Override
    public void selected(Object source, Object selectObj) {
        if (source.toString().equals("IO-HISTORY")) {
            if (selectObj instanceof VStockIO v) {
                inventoryRepo.findStockIO(v.getVouNo(), v.getDeptId()).subscribe((t) -> {
                    setVoucher(t);
                });
            }
        }
        if (source.toString().equals("CAL-TOTAL")) {
            calTotalAmt();
        }
        if (source.toString().equals("OP-HISTORY")) {
            if (selectObj instanceof OPHis v) {
                inventoryRepo.findOpening(v.getKey()).subscribe((t) -> {
                    importOP(t);
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
                    if (date.length() == 8 || date.length() == 6) {
                        txtDate.setDate(Util1.formatDate(date));
                    }
                    txtVouType.requestFocus();
                }
            }
            case "txtVouType" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtDesp.requestFocus();
                }
            }
            case "txtDesp" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtRemark.requestFocus();
                }
            }
            case "txtRemark" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    tblStock.requestFocus();
                }
            }

        }
    }
}
