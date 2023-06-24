/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.inventory.ui.entry;

import com.common.DecimalFormatRender;
import com.common.Global;
import com.common.PanelControl;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.editor.LocationCellEditor;
import com.inventory.editor.StockCellEditor;
import com.inventory.editor.TraderAutoCompleter;
import com.inventory.model.GRN;
import com.inventory.model.GRNDetail;
import com.inventory.model.GRNKey;
import com.inventory.model.Location;
import com.inventory.model.StockUnit;
import com.inventory.model.Trader;
import com.inventory.ui.common.GRNTableModel;
import com.inventory.ui.common.InventoryRepo;
import com.inventory.ui.entry.dialog.GRNHistoryDialog;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.inventory.ui.setup.dialog.common.StockUnitEditor;
import com.toedter.calendar.JTextFieldDateEditor;
import com.user.common.UserRepo;
import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 *
 * @author DELL
 */
@Component
@Slf4j
public class GRNEntry extends javax.swing.JPanel implements SelectionObserver, PanelControl, KeyListener {

    private SelectionObserver observer;
    private JProgressBar progress;
    private TraderAutoCompleter traderAutoCompleter;
    @Autowired
    private InventoryRepo inventoryRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private WebClient inventoryApi;
    private LocationAutoCompleter locationAutoCompleter;
    private final GRNTableModel tableModel = new GRNTableModel();
    private GRNHistoryDialog dialog;
    private GRN grn = new GRN();

    public LocationAutoCompleter getLocationAutoCompleter() {
        return locationAutoCompleter;
    }

    public void setLocationAutoCompleter(LocationAutoCompleter locationAutoCompleter) {
        this.locationAutoCompleter = locationAutoCompleter;
    }

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
     * Creates new form Batch
     */
    public GRNEntry() {
        initComponents();
        initFocusAdapter();
        initKeyListener();
        actionMapping();
    }

    public void initMain() {
        assignDefaultValue();
        initCombo();
        initTable();
    }

    private void initFocusAdapter() {
        txtDate.addFocusListener(fa);
        txtTrader.addFocusListener(fa);
        txtBatchNo.addFocusListener(fa);
        txtRemark.addFocusListener(fa);
        txtLocation.addFocusListener(fa);
    }

    private void initKeyListener() {
        txtDate.getDateEditor().getUiComponent().setName("txtDate");
        txtDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtTrader.addKeyListener(this);
        txtBatchNo.addKeyListener(this);
        txtRemark.addKeyListener(this);
    }
    private final FocusAdapter fa = new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            if (e.getSource() instanceof JTextFieldDateEditor txt) {
                txt.selectAll();
            } else if (e.getSource() instanceof JTextField txt) {
                txt.selectAll();
            }
        }
    };

    private void initCombo() {
        inventoryRepo.getLocation().subscribe((t) -> {
            locationAutoCompleter = new LocationAutoCompleter(txtLocation, t, null, false, false);
            locationAutoCompleter.setObserver(this);
            inventoryRepo.getDefaultLocation().subscribe((tt) -> {
                locationAutoCompleter.setLocation(tt);
            });
        });
        traderAutoCompleter = new TraderAutoCompleter(txtTrader, inventoryRepo, null, false, "-");
        traderAutoCompleter.setObserver(this);
    }

    private void assignDefaultValue() {
        txtDate.setDate(Util1.getTodayDate());
    }

    private void actionMapping() {
        String solve = "delete";
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        tblGRN.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, solve);
        tblGRN.getActionMap().put(solve, new DeleteAction());

    }

    private class DeleteAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            deleteTran();
        }
    }

    private void deleteTran() {
        int row = tblGRN.convertRowIndexToModel(tblGRN.getSelectedRow());
        if (row >= 0) {
            if (tblGRN.getCellEditor() != null) {
                tblGRN.getCellEditor().stopCellEditing();
            }
            int yes_no = JOptionPane.showConfirmDialog(this,
                    "Are you sure to delete?", " Transaction delete.", JOptionPane.YES_NO_OPTION);
            if (yes_no == 0) {
                tableModel.delete(row);
            }
        }
    }

    private void initTable() {
        tableModel.setLblRec(lblRec);
        tableModel.setParent(tblGRN);
        tableModel.setGrn(this);
        tableModel.addNewRow();
        tblGRN.setModel(tableModel);
        tblGRN.getTableHeader().setFont(Global.tblHeaderFont);
        tblGRN.setCellSelectionEnabled(true);
        tblGRN.setRowHeight(Global.tblRowHeight);
        tblGRN.setShowGrid(true);
        tblGRN.setFont(Global.textFont);
        tblGRN.getColumnModel().getColumn(0).setPreferredWidth(50);//Code
        tblGRN.getColumnModel().getColumn(1).setPreferredWidth(350);//Name
        tblGRN.getColumnModel().getColumn(2).setPreferredWidth(60);//relation
        tblGRN.getColumnModel().getColumn(3).setPreferredWidth(60);//qty
        tblGRN.getColumnModel().getColumn(4).setPreferredWidth(1);//unit
        tblGRN.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor(inventoryRepo));
        tblGRN.getColumnModel().getColumn(1).setCellEditor(new StockCellEditor(inventoryRepo));
        inventoryRepo.getLocation().subscribe((t) -> {
            tblGRN.getColumnModel().getColumn(3).setCellEditor(new LocationCellEditor(t));
        });
        tblGRN.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());//qty
        Mono<List<StockUnit>> monoUnit = inventoryRepo.getStockUnit();
        monoUnit.subscribe((t) -> {
            tblGRN.getColumnModel().getColumn(5).setCellEditor(new StockUnitEditor(t));
        });
        tblGRN.getColumnModel().getColumn(6).setCellEditor(new AutoClearEditor());//qty
        monoUnit.subscribe((t) -> {
            tblGRN.getColumnModel().getColumn(7).setCellEditor(new StockUnitEditor(t));
        });
        tblGRN.setDefaultRenderer(Object.class, new DecimalFormatRender());
        tblGRN.setDefaultRenderer(Float.class, new DecimalFormatRender());
        tblGRN.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblGRN.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    }

    public boolean saveGRN() {
        boolean status = false;
        try {
            if (isValidEntry() && tableModel.isValidEntry()) {
                progress.setIndeterminate(true);
                observer.selected("save", false);
                grn.setListDetail(tableModel.getListDetail());
                grn.setListDel(tableModel.getListDel());
                inventoryRepo.saveGRN(grn).subscribe((t) -> {
                    clear();
                }, (e) -> {
                    JOptionPane.showMessageDialog(this, e.getMessage());
                    progress.setIndeterminate(false);
                    observer.selected("save", false);
                });

            }
        } catch (HeadlessException ex) {
            log.error("savePur :" + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Could not saved.");
        }
        return status;
    }

    private boolean isValidEntry() {
        boolean status = true;
        if (lblStatus.getText().equals("DELETED")) {
            status = false;
            clear();
        } else if (Objects.isNull(traderAutoCompleter.getTrader())) {
            JOptionPane.showMessageDialog(this, "Choose Supplier.",
                    "Choose Supplier.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtTrader.requestFocus();
        } else if (Objects.isNull(locationAutoCompleter.getLocation())) {
            JOptionPane.showMessageDialog(this, "Choose Location.",
                    "Choose Location.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtLocation.requestFocus();
        } else if (tableModel.getListDetail().size() == 1) {
            JOptionPane.showMessageDialog(Global.parentForm, "No Stock Records.");
            status = false;
        } else {
            grn.setMacId(Global.macId);
            grn.setVouDate(Util1.convertToLocalDateTime(txtDate.getDate()));
            grn.setTraderCode(traderAutoCompleter.getTrader().getKey().getCode());
            grn.setLocCode(locationAutoCompleter.getLocation().getKey().getLocCode());
            grn.setBatchNo(txtBatchNo.getText());
            grn.setRemark(txtRemark.getText());
            grn.setClosed(chkClose.isSelected());
            if (lblStatus.getText().equals("NEW")) {
                String batchNo = txtBatchNo.getText();
                if (!batchNo.isEmpty()) {
                    if (!inventoryRepo.getBatchList(txtBatchNo.getText()).block().isEmpty()) {
                        JOptionPane.showMessageDialog(Global.parentForm, String.format("Batch No %s already exists.", txtBatchNo.getText()));
                        return false;
                    }
                }
                GRNKey key = new GRNKey();
                key.setCompCode(Global.compCode);
                key.setDeptId(Global.deptId);
                key.setVouNo(null);
                grn.setKey(key);
                grn.setCreatedDate(LocalDateTime.now());
                grn.setCreatedBy(Global.loginUser.getUserCode());
            } else {
                grn.setUpdatedBy(Global.loginUser.getUserCode());
            }
        }
        return status;
    }

    private void setAllLocation() {
        List<GRNDetail> listSaleDetail = tableModel.getListDetail();
        Location loc = locationAutoCompleter.getLocation();
        if (listSaleDetail != null) {
            listSaleDetail.forEach(sd -> {
                sd.setLocCode(loc.getKey().getLocCode());
                sd.setLocName(loc.getLocName());
            });
        }
        tableModel.setListDetail(listSaleDetail);
    }

    private void clear() {
        disableForm(true);
        assignDefaultValue();
        tableModel.clear();
        tableModel.addNewRow();
        lblStatus.setText("NEW");
        lblStatus.setForeground(Color.GREEN);
        progress.setIndeterminate(false);
        txtVouNo.setText(null);
        traderAutoCompleter.setTrader(null);
        txtBatchNo.setText(null);
        txtRemark.setText(null);
        progress.setIndeterminate(false);
        grn = new GRN();
        focusTable();
    }

    private void disableForm(boolean status) {
        txtDate.setEnabled(status);
        txtTrader.setEnabled(status);
        txtBatchNo.setEditable(status);
        chkClose.setEnabled(status);
        txtRemark.setEnabled(status);
        tblGRN.setEnabled(status);
        txtLocation.setEnabled(status);
        observer.selected("save", status);
        observer.selected("print", status);
    }

    private void focusTable() {
        int rc = tblGRN.getRowCount();
        if (rc >= 1) {
            tblGRN.setRowSelectionInterval(rc - 1, rc - 1);
            tblGRN.setColumnSelectionInterval(0, 0);
            tblGRN.requestFocus();
        } else {
            txtDate.requestFocusInWindow();
        }
    }

    private void historyGRN() {
        if (dialog == null) {
            dialog = new GRNHistoryDialog(Global.parentForm);
            dialog.setInventoryRepo(inventoryRepo);
            dialog.setUserRepo(userRepo);
            dialog.setObserver(this);
            dialog.initMain();
            dialog.setSize(Global.width - 100, Global.height - 100);
            dialog.setLocationRelativeTo(null);
        }
        dialog.search();
    }

    public void setVoucher(GRN g) {
        if (g != null) {
            progress.setIndeterminate(true);
            tableModel.clear();
            grn = g;
            Integer deptId = g.getKey().getDeptId();
            Mono<Trader> trader = inventoryRepo.findTrader(grn.getTraderCode(), grn.getKey().getDeptId());
            trader.subscribe((t) -> {
                traderAutoCompleter.setTrader(t);
            });
            Mono<Location> loc = inventoryRepo.findLocation(grn.getLocCode());
            loc.hasElement().subscribe((element) -> {
                if (element) {
                    loc.subscribe((t) -> {
                        locationAutoCompleter.setLocation(t);
                    });
                } else {
                    locationAutoCompleter.setLocation(null);
                }
            });

            String vouNo = grn.getKey().getVouNo();
            inventoryApi.get()
                    .uri(builder -> builder.path("/grn/get-grn-detail")
                    .queryParam("vouNo", vouNo)
                    .queryParam("compCode", Global.compCode)
                    .queryParam("deptId", deptId)
                    .build())
                    .retrieve().bodyToFlux(GRNDetail.class)
                    .collectList()
                    .subscribe((t) -> {
                        tableModel.setListDetail(t);
                        tableModel.addNewRow();
                        if (grn.isClosed()) {
                            lblStatus.setText("CLOSED");
                            lblStatus.setForeground(Color.RED);
                            disableForm(false);
                        } else if (grn.isDeleted()) {
                            lblStatus.setText("DELETED");
                            lblStatus.setForeground(Color.RED);
                            disableForm(false);
                            observer.selected("delete", true);
                        } else {
                            lblStatus.setText("EDIT");
                            lblStatus.setForeground(Color.blue);
                            disableForm(true);
                            txtBatchNo.setEditable(g.getBatchNo().isEmpty());
                        }
                        txtVouNo.setText(vouNo);
                        txtRemark.setText(grn.getRemark());
                        txtBatchNo.setText(grn.getBatchNo());
                        txtDate.setDate(Util1.convertToDate(grn.getVouDate()));
                        chkClose.setSelected(grn.isClosed());
                        focusTable();
                        progress.setIndeterminate(false);
                    }, (e) -> {
                        progress.setIndeterminate(false);
                        JOptionPane.showMessageDialog(this, e.getMessage());
                    });

        }
    }

    private void deleteGRN() {
        String status = lblStatus.getText();
        switch (status) {
            case "EDIT" -> {
                int yes_no = JOptionPane.showConfirmDialog(this,
                        "Are you sure to delete?", "GRN Voucher Delete.", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                if (yes_no == 0) {
                    inventoryRepo.delete(grn.getKey()).subscribe((t) -> {
                        clear();
                    });
                }
            }
            case "CLOSED" -> {
                int yes_no = JOptionPane.showConfirmDialog(this,
                        "Are you sure to open batch?", "GRN Voucher Batch Open.", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (yes_no == 0) {
                    chkClose.setSelected(false);
                    inventoryRepo.open(grn.getKey()).subscribe((t) -> {
                        lblStatus.setText("EDIT");
                        lblStatus.setForeground(Color.blue);
                        disableForm(true);
                    });
                }
            }
            case "DELETED" -> {
                int yes_no = JOptionPane.showConfirmDialog(this,
                        "Are you sure to restore?", "Purchase Voucher Restore.", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (yes_no == 0) {
                    grn.setDeleted(false);
                    inventoryRepo.restore(grn.getKey()).subscribe((t) -> {
                        if (t) {
                            lblStatus.setText("EDIT");
                            lblStatus.setForeground(Color.blue);
                            disableForm(true);
                        }
                    });

                }
            }
            default ->
                JOptionPane.showMessageDialog(this, "Voucher can't delete.");
        }
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
        jLabel2 = new javax.swing.JLabel();
        txtBatchNo = new javax.swing.JTextField();
        txtVouNo = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtDate = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        txtTrader = new javax.swing.JTextField();
        chkClose = new javax.swing.JCheckBox();
        lblStatus = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtLocation = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblGRN = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        lblRec = new javax.swing.JLabel();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Vou No");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Batch No");

        txtBatchNo.setFont(Global.textFont);
        txtBatchNo.setName("txtBatchNo"); // NOI18N
        txtBatchNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBatchNoActionPerformed(evt);
            }
        });

        txtVouNo.setEditable(false);
        txtVouNo.setFont(Global.textFont);
        txtVouNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtVouNoActionPerformed(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Date");

        txtDate.setDateFormatString("dd/MM/yyyy");
        txtDate.setFont(Global.textFont);

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Trader");

        txtTrader.setFont(Global.textFont);
        txtTrader.setName("txtTrader"); // NOI18N
        txtTrader.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTraderActionPerformed(evt);
            }
        });

        chkClose.setText("Close");

        lblStatus.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        lblStatus.setText("NEW");
        lblStatus.setToolTipText("NEW");

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Remark");

        txtRemark.setFont(Global.textFont);
        txtRemark.setName("txtRemark"); // NOI18N
        txtRemark.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRemarkActionPerformed(evt);
            }
        });

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Locaiton");

        txtLocation.setFont(Global.textFont);
        txtLocation.setName("txtRemark"); // NOI18N
        txtLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtLocationActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkClose, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtVouNo)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txtBatchNo, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtDate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE)
                                    .addComponent(txtTrader)
                                    .addComponent(txtRemark, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtLocation, javax.swing.GroupLayout.Alignment.LEADING))
                                .addGap(1, 1, 1)))
                        .addContainerGap())
                    .addComponent(lblStatus, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtVouNo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTrader))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtBatchNo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtRemark))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtLocation))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkClose)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblStatus)
                .addGap(273, 273, 273))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel3, jLabel4});

        tblGRN.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblGRN);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblRec.setText("Record : 0");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblRec)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblRec)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtBatchNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBatchNoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBatchNoActionPerformed

    private void txtVouNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtVouNoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVouNoActionPerformed

    private void txtTraderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTraderActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTraderActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observer.selected("control", this);
        focusTable();
    }//GEN-LAST:event_formComponentShown

    private void txtRemarkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRemarkActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRemarkActionPerformed

    private void txtLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtLocationActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtLocationActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkClose;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblRec;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblGRN;
    private javax.swing.JTextField txtBatchNo;
    private com.toedter.calendar.JDateChooser txtDate;
    private javax.swing.JTextField txtLocation;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JTextField txtTrader;
    private javax.swing.JTextField txtVouNo;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {
        if (source.equals("GRN-HISTORY")) {
            if (selectObj instanceof GRN g) {
                setVoucher(g);
            }
        } else if (source.equals("Location")) {
            setAllLocation();
        }

    }

    @Override
    public void save() {
        saveGRN();
    }

    @Override
    public void delete() {
        deleteGRN();
    }

    @Override
    public void newForm() {
        clear();
    }

    @Override
    public void history() {
        historyGRN();
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
                    txtTrader.requestFocus();
                }
            }
            case "txtTrader" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtBatchNo.requestFocus();
                }
            }
            case "txtBatchNo" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtRemark.requestFocus();
                }
            }
            case "txtRemark" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    focusTable();
                }
            }
        }
    }
}
