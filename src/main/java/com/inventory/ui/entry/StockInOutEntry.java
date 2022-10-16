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
import com.inventory.model.StockIOKey;
import com.inventory.model.StockInOut;
import com.inventory.model.StockInOutDetail;
import com.inventory.model.StockUnit;
import com.inventory.ui.common.InventoryRepo;
import com.inventory.ui.common.StockInOutTableModel;
import com.inventory.ui.entry.dialog.StockIOSearchDialog;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.inventory.ui.setup.dialog.common.StockUnitEditor;
import java.awt.Color;
import java.awt.HeadlessException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 *
 * @author Lenovo
 */
@Component
public class StockInOutEntry extends javax.swing.JPanel implements PanelControl, SelectionObserver {

    private static final Logger log = LoggerFactory.getLogger(StockInOutEntry.class);
    private final StockInOutTableModel outTableModel = new StockInOutTableModel();
    private final StockIOSearchDialog historyDialog = new StockIOSearchDialog(Global.parentForm);
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
    private List<StockUnit> listStockUnit = new ArrayList<>();
    private List<Location> listLocation = new ArrayList<>();

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
    }

    public void initMain() {
        initTable();
        initCombo();
        clear();
    }

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
        listLocation = inventoryRepo.getLocation();
        listStockUnit = inventoryRepo.getStockUnit();
        outTableModel.setVouDate(txtDate);
        outTableModel.setInventoryRepo(inventoryRepo);
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
        tblStock.getColumnModel().getColumn(2).setCellEditor(new LocationCellEditor(listLocation));
        tblStock.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());
        tblStock.getColumnModel().getColumn(4).setCellEditor(new StockUnitEditor(listStockUnit));
        tblStock.getColumnModel().getColumn(5).setCellEditor(new AutoClearEditor());
        tblStock.getColumnModel().getColumn(6).setCellEditor(new StockUnitEditor(listStockUnit));
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
        if (lblStatus.getText().equals("EDIT")) {
            int yes_no = JOptionPane.showConfirmDialog(this,
                    "Are you sure to delete?", "Stock In/Out Voucher delete", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
            if (yes_no == 0) {
                inventoryRepo.delete(io.getKey());
                clear();
            }
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
        try {
            if (isValidEntry() && outTableModel.isValidEntry()) {
                progress.setIndeterminate(true);
                io.setListSH(outTableModel.getListStock());
                io.setListDel(outTableModel.getDeleteList());
                Mono<StockInOut> result = inventoryApi.post()
                        .uri("/stockio/save-stockio")
                        .body(Mono.just(io), StockInOut.class)
                        .retrieve()
                        .bodyToMono(StockInOut.class);
                StockInOut t = result.block();
                if (t != null) {
                    clear();
                    focusOnTable();
                }
            }
        } catch (HeadlessException ex) {
            log.error("saveVoucher :" + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Could'nt saved.");
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
            txtVouType.requestFocus();
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
        if (s != null) {
            progress.setIndeterminate(true);
            io = s;
            vouStatusAutoCompleter.setVoucher(inventoryRepo.findVouStatus(io.getVouStatusCode()));
            String vouNo = io.getKey().getVouNo();
            Mono<ResponseEntity<List<StockInOutDetail>>> result = inventoryApi.get()
                    .uri(builder -> builder.path("/stockio/get-stockio-detail")
                    .queryParam("vouNo", vouNo)
                    .queryParam("compCode", Global.compCode)
                    .queryParam("deptId", Global.deptId)
                    .build())
                    .retrieve().toEntityList(StockInOutDetail.class);
            result.subscribe((t) -> {
                outTableModel.setListStock(t.getBody());
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
                int row = tblStock.getRowCount();
                tblStock.setColumnSelectionInterval(0, 0);
                tblStock.setRowSelectionInterval(row - 1, row - 1);
                tblStock.requestFocus();
                progress.setIndeterminate(false);
            }, (e) -> {
                progress.setIndeterminate(false);
                JOptionPane.showMessageDialog(this, e.getMessage());
            });
        }
    }

    private void disableForm(boolean status) {
        txtDate.setEnabled(status);
        txtRemark.setEnabled(status);
        txtDesp.setEnabled(status);
        tblStock.setEnabled(status);
        txtVouType.setEnabled(status);

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

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Remark");

        txtRemark.setFont(Global.textFont);

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
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                        .addComponent(jLabel3)))
                .addContainerGap(7, Short.MAX_VALUE))
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
                        .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtOutQty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtInQty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)
                        .addComponent(jLabel1)
                        .addComponent(txtCost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8))
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
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            deleteTran();
        }
    }//GEN-LAST:event_tblStockKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
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
        try {
            historyDialog.setInventoryRepo(inventoryRepo);
            historyDialog.setUserRepo(userRepo);
            historyDialog.setIconImage(new ImageIcon(getClass().getResource("/images/search.png")).getImage());
            historyDialog.setWebClient(inventoryApi);
            historyDialog.setObserver(this);
            historyDialog.initMain();
            historyDialog.setSize(Global.width - 100, Global.height - 100);
            historyDialog.setLocationRelativeTo(null);
            historyDialog.setVisible(true);
        } catch (Exception e) {
            log.info(e.getLocalizedMessage());
        }
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
            String vouNo = selectObj.toString();
            setVoucher(inventoryRepo.findStockIO(vouNo));
        }
        if (source.toString().equals("CAL-TOTAL")) {
            calTotalAmt();
        }

    }

    @Override
    public String panelName() {
        return this.getName();
    }
}
