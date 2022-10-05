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
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.editor.StockCellEditor;
import com.inventory.model.Location;
import com.inventory.model.TransferHis;
import com.inventory.model.TransferHisDetail;
import com.inventory.model.StockUnit;
import com.inventory.model.TransferHisKey;
import com.inventory.ui.common.InventoryRepo;
import com.inventory.ui.common.TransferTableModel;
import com.inventory.ui.entry.dialog.TransferSearchDialog;
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
public class Transfer extends javax.swing.JPanel implements PanelControl, SelectionObserver {

    private static final Logger log = LoggerFactory.getLogger(Transfer.class);
    private final TransferTableModel tranTableModel = new TransferTableModel();
    private final TransferSearchDialog historyDialog = new TransferSearchDialog(Global.parentForm);
    @Autowired
    private WebClient inventoryApi;
    @Autowired
    private InventoryRepo inventoryRepo;
    @Autowired
    private UserRepo userRepo;
    private LocationAutoCompleter fromLocaitonCompleter;
    private LocationAutoCompleter toLocaitonCompleter;
    private TransferHis io = new TransferHis();
    private SelectionObserver observer;
    private JProgressBar progress;
    private List<StockUnit> listStockUnit = new ArrayList<>();

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
    }

    public void initMain() {
        initTable();
        initCombo();
        clear();
    }

    private void initCombo() {
        List<Location> listLocaiton = inventoryRepo.getLocation();
        fromLocaitonCompleter = new LocationAutoCompleter(txtFrom, listLocaiton, null, false, false);
        toLocaitonCompleter = new LocationAutoCompleter(txtTo, listLocaiton, null, false, false);
    }

    private void initTable() {
        listStockUnit = inventoryRepo.getStockUnit();
        tranTableModel.setVouDate(txtDate);
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
        tblTransfer.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor(inventoryRepo));
        tblTransfer.getColumnModel().getColumn(1).setCellEditor(new StockCellEditor(inventoryRepo));
        tblTransfer.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());
        tblTransfer.getColumnModel().getColumn(4).setCellEditor(new StockUnitEditor(listStockUnit));
        tblTransfer.setDefaultRenderer(Object.class, new DecimalFormatRender());
        tblTransfer.setDefaultRenderer(Float.class, new DecimalFormatRender());
        tblTransfer.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblTransfer.setCellSelectionEnabled(true);
        tblTransfer.changeSelection(0, 0, false, false);
        tblTransfer.requestFocus();
    }

    private void deleteVoucher() {
        if (lblStatus.getText().equals("EDIT")) {
            int yes_no = JOptionPane.showConfirmDialog(this,
                    "Are you sure to delete?", "Stock In/Out Voucher delete", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
            if (yes_no == 0) {
                io.setDeleted(true);
                saveVoucher();
            }
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

    public boolean saveVoucher() {
        boolean status = false;
        try {
            if (isValidEntry() && tranTableModel.isValidEntry()) {
                progress.setIndeterminate(true);
                io.setListTD(tranTableModel.getListTransfer());
                io.setDelList(tranTableModel.getDeleteList());
                Mono<TransferHis> result = inventoryApi.post()
                        .uri("/transfer/save-transfer")
                        .body(Mono.just(io), TransferHis.class)
                        .retrieve()
                        .bodyToMono(TransferHis.class);
                TransferHis t = result.block();
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
        fromLocaitonCompleter.setLocation(inventoryRepo.getDefaultLocation());
        disableForm(true);
    }

    private void focusOnTable() {
        int rc = tblTransfer.getRowCount();
        if (rc > 1) {
            tblTransfer.setRowSelectionInterval(rc - 1, rc - 1);
            tblTransfer.setColumnSelectionInterval(0, 0);
            tblTransfer.requestFocus();
        } else {
            txtFrom.requestFocus();
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
            TransferHisKey key = new TransferHisKey();
            key.setCompCode(Global.compCode);
            key.setVouNo(txtVou.getText());
            io.setKey(key);
            io.setRefNo(txtRefNo.getText());
            io.setRemark(txtRemark.getText());
            io.setVouDate(txtDate.getDate());
            io.setLocationFrom(fromLocaitonCompleter.getLocation());
            io.setLocationTo(toLocaitonCompleter.getLocation());
            io.setStatus(lblStatus.getText());
            if (lblStatus.getText().equals("NEW")) {
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

    private void setVoucher(TransferHis s) {
        progress.setIndeterminate(true);
        io = s;
        String vouNo = io.getKey().getVouNo();
        Mono<ResponseEntity<List<TransferHisDetail>>> result = inventoryApi.get()
                .uri(builder -> builder.path("/transfer/get-transfer-detail")
                .queryParam("vouNo", vouNo)
                .build())
                .retrieve().toEntityList(TransferHisDetail.class);
        result.subscribe((t) -> {
            tranTableModel.setListTransfer(t.getBody());
            tranTableModel.addNewRow();
            txtVou.setText(vouNo);
            txtDate.setDate(Util1.toDateFormat(io.getVouDate(), "dd/MM/yyyy"));
            txtRemark.setText(io.getRemark());
            txtRefNo.setText(io.getRefNo());
            fromLocaitonCompleter.setLocation(io.getLocationFrom());
            toLocaitonCompleter.setLocation(io.getLocationTo());
            if (Util1.getBoolean(io.isDeleted())) {
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
        jLabel7.setText("Location From");

        txtFrom.setFont(Global.textFont);

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Location To");

        txtTo.setFont(Global.textFont);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(txtVou, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(txtDate, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addComponent(txtFrom, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel9)
                .addGap(18, 18, 18)
                .addComponent(txtTo, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(txtRefNo, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(txtRemark, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)
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

        lblStatus.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        lblStatus.setText("NEW");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addComponent(lblStatus)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observer.selected("control", this);
        focusOnTable();
    }//GEN-LAST:event_formComponentShown

    private void tblTransferKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblTransferKeyReleased
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            deleteTran();
        }
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
        if (source.toString().equals("TR-HISTORY")) {
            if (selectObj != null) {
                String vouNo = selectObj.toString();
                setVoucher(inventoryRepo.findTransfer(vouNo));

            }
        }

    }

    @Override
    public String panelName() {
        return this.getName();
    }
}
