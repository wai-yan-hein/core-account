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
import com.inventory.editor.StockAutoCompleter;
import com.inventory.editor.StockCellEditor;
import com.inventory.model.WeightLossDetail;
import com.inventory.model.WeightLossHis;
import com.inventory.model.WeightLossHisKey;
import com.inventory.ui.common.InventoryRepo;
import com.inventory.ui.common.WeightLossTableModel;
import com.inventory.ui.entry.dialog.WeightLossSearchDialog;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.inventory.ui.setup.dialog.common.StockUnitEditor;
import com.user.common.UserRepo;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 *
 * @author DELL
 */
@Component
@Slf4j
public class WeightLossEntry extends javax.swing.JPanel implements SelectionObserver, PanelControl {

    @Autowired
    private InventoryRepo inventoryRepo;
    @Autowired
    private WebClient inventoryApi;
    @Autowired
    private UserRepo userRepo;
    private final WeightLossTableModel tableModel = new WeightLossTableModel();
    private final WeightLossSearchDialog dialog = new WeightLossSearchDialog(Global.parentForm);
    private final Image searchIcon = new ImageIcon(this.getClass().getResource("/images/search.png")).getImage();
    private LocationAutoCompleter locationAutoCompleter;
    private StockAutoCompleter stockAutoCompleter;
    private SelectionObserver observer;
    private JProgressBar progress;
    private WeightLossHis his = new WeightLossHis();

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
     * Creates new form WeightLossEntry
     */
    public WeightLossEntry() {
        initComponents();
        actionMapping();
    }

    public void initMain() {
        txtDate.setDate(Util1.getTodayDate());
        initTable();
    }

    private void initTable() {
        tableModel.setVouDate(txtDate);
        tableModel.setInventoryRepo(inventoryRepo);
        tableModel.setLocation(inventoryRepo.getDefaultLocation());
        tableModel.setLblRecord(lblRecord);
        tableModel.addNewRow();
        tableModel.setTable(tblWeight);
        tblWeight.setModel(tableModel);
        tblWeight.getTableHeader().setFont(Global.tblHeaderFont);
        tblWeight.setFont(Global.textFont);
        tblWeight.setCellSelectionEnabled(true);
        tblWeight.setShowGrid(true);
        tblWeight.setRowHeight(Global.tblRowHeight);
        tblWeight.getColumnModel().getColumn(0).setPreferredWidth(10);
        tblWeight.getColumnModel().getColumn(1).setPreferredWidth(200);
        tblWeight.getColumnModel().getColumn(2).setPreferredWidth(50);
        tblWeight.getColumnModel().getColumn(3).setPreferredWidth(10);
        tblWeight.getColumnModel().getColumn(4).setPreferredWidth(10);
        tblWeight.getColumnModel().getColumn(5).setPreferredWidth(10);
        tblWeight.getColumnModel().getColumn(6).setPreferredWidth(10);
        tblWeight.getColumnModel().getColumn(7).setPreferredWidth(10);
        tblWeight.getColumnModel().getColumn(8).setPreferredWidth(10);
        tblWeight.getColumnModel().getColumn(9).setPreferredWidth(10);
        tblWeight.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor(inventoryRepo));
        tblWeight.getColumnModel().getColumn(1).setCellEditor(new StockCellEditor(inventoryRepo));
        tblWeight.getColumnModel().getColumn(3).setCellEditor(new LocationCellEditor(inventoryRepo.getLocation()));
        tblWeight.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());
        tblWeight.getColumnModel().getColumn(5).setCellEditor(new StockUnitEditor(inventoryRepo.getStockUnit()));
        tblWeight.getColumnModel().getColumn(6).setCellEditor(new AutoClearEditor());
        tblWeight.getColumnModel().getColumn(7).setCellEditor(new AutoClearEditor());
        tblWeight.getColumnModel().getColumn(8).setCellEditor(new StockUnitEditor(inventoryRepo.getStockUnit()));
        tblWeight.getColumnModel().getColumn(9).setCellEditor(new AutoClearEditor());
        tblWeight.getColumnModel().getColumn(4).setCellRenderer(new DecimalFormatRender());
        tblWeight.getColumnModel().getColumn(6).setCellRenderer(new DecimalFormatRender());
        tblWeight.getColumnModel().getColumn(7).setCellRenderer(new DecimalFormatRender());
        tblWeight.getColumnModel().getColumn(9).setCellRenderer(new DecimalFormatRender());
        tblWeight.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblWeight.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void actionMapping() {
        String solve = "delete";
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        tblWeight.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, solve);
        tblWeight.getActionMap().put(solve, new DeleteAction());

    }

    private class DeleteAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            deleteTran();
        }
    }

    private void deleteTran() {
        int row = tblWeight.convertRowIndexToModel(tblWeight.getSelectedRow());
        if (row >= 0) {
            if (tblWeight.getCellEditor() != null) {
                tblWeight.getCellEditor().stopCellEditing();
            }
            int yes_no = JOptionPane.showConfirmDialog(this,
                    "Are you sure to delete?", "Transaction delete.", JOptionPane.YES_NO_OPTION);
            if (yes_no == 0) {
                tableModel.remove(row);
            }
        }
    }

    private void saveVoucher() {
        if (isValidEntry() && tableModel.isValidEntry()) {
            his.setListDetail(tableModel.getListDetail());
            his.setDelKeys(tableModel.getDelKeys());
            inventoryRepo.saveWeightLoss(his);
            clear();
        }
    }

    private void deleteVoucher() {
        if (lblStatus.getText().equals("EDIT")) {
            int status = JOptionPane.showConfirmDialog(this, "Are you sure to delete?", "Delete Voucher", JOptionPane.ERROR_MESSAGE, JOptionPane.NO_OPTION);
            if (status == JOptionPane.YES_OPTION) {
                inventoryRepo.delete(his.getKey());
                clear();
            }
        } else {
            int status = JOptionPane.showConfirmDialog(this, "Are you sure to restore?", "Delete Voucher", JOptionPane.WARNING_MESSAGE, JOptionPane.NO_OPTION);
            if (status == JOptionPane.YES_OPTION) {
                inventoryRepo.restore(his.getKey());
                lblStatus.setText("EDIT");
                lblStatus.setForeground(Color.blue);
                enableControl(true);
                focusTable();
            }
        }
    }

    private void focusTable() {
        int rc = tblWeight.getRowCount();
        if (rc > 1) {
            tblWeight.setRowSelectionInterval(rc - 1, rc - 1);
            tblWeight.setColumnSelectionInterval(0, 0);
            tblWeight.requestFocus();
        } else {
            txtDate.requestFocus();
        }
    }

    private void enableControl(boolean status) {
        txtDate.setEnabled(status);
        txtRemark.setEnabled(status);
        tblWeight.setEnabled(status);
    }

    private void clear() {
        txtVouNo.setText(null);
        txtRemark.setText(null);
        txtRefNo.setText(null);
        lblStatus.setText("NEW");
        lblStatus.setForeground(Color.green);
        tableModel.clear();
        tableModel.addNewRow();
    }

    private boolean isValidEntry() {
        if (txtDate.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Invalid Date.");
            txtDate.requestFocusInWindow();
            return false;
        } else {
            String userCode = Global.loginUser.getUserCode();
            if (lblStatus.getText().equals("NEW")) {
                WeightLossHisKey key = new WeightLossHisKey();
                key.setCompCode(Global.compCode);
                key.setDeptId(Global.deptId);
                key.setVouNo(null);
                his.setKey(key);
                his.setCreatedBy(userCode);
            } else {
                his.setUpdatedBy(userCode);
            }
            his.setVouDate(txtDate.getDate());
            his.setRemark(txtRemark.getText());
            his.setRefNo(txtRefNo.getText());
            his.setMacId(Global.macId);
        }
        return true;
    }

    public void historyWeight() {
        dialog.setInventoryApi(inventoryApi);
        dialog.setInventoryRepo(inventoryRepo);
        dialog.setUserRepo(userRepo);
        dialog.setIconImage(searchIcon);
        dialog.setObserver(this);
        dialog.initMain();
        dialog.setSize(Global.width - 100, Global.height - 100);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void setVoucher(WeightLossHis his) {
        this.his = his;
        String vouNo = his.getKey().getVouNo();
        Mono<ResponseEntity<List<WeightLossDetail>>> result = inventoryApi.get()
                .uri(builder -> builder.path("/weight/get-weight-loss-detail")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", his.getKey().getDeptId())
                .build())
                .retrieve().toEntityList(WeightLossDetail.class);
        result.subscribe((t) -> {
            List<WeightLossDetail> list = t.getBody();
            lblRecord.setText("Records : " + list.size());
            tableModel.setListDetail(list);
            tableModel.addNewRow();
            txtVouNo.setText(his.getKey().getVouNo());
            txtDate.setDate(his.getVouDate());
            txtRefNo.setText(his.getRefNo());
            txtRemark.setText(his.getRefNo());
            if (his.isDeleted()) {
                lblStatus.setText("DELETED");
                lblStatus.setForeground(Color.red);
                enableControl(false);
            } else {
                lblStatus.setText("EDIT");
                lblStatus.setForeground(Color.blue);
                enableControl(true);
            }
            focusTable();
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

        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtVouNo = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtDate = new com.toedter.calendar.JDateChooser();
        jLabel6 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        txtRefNo = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblWeight = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        lblStatus = new javax.swing.JLabel();
        lblRecord = new javax.swing.JLabel();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Vou No");

        txtVouNo.setEditable(false);
        txtVouNo.setFont(Global.textFont);

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Vou Date");

        txtDate.setDateFormatString("dd/MM/yyyy");
        txtDate.setFont(Global.textFont);

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Remark");

        txtRemark.setFont(Global.textFont);

        txtRefNo.setFont(Global.textFont);

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Ref No");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtVouNo, javax.swing.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtDate, javax.swing.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtRemark, javax.swing.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtRefNo, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtRefNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7))
                    .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tblWeight.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblWeight);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblStatus.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        lblStatus.setText("NEW");

        lblRecord.setFont(Global.lableFont);
        lblRecord.setText("Records");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblStatus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblRecord)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblRecord))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observer.selected("control", this);

    }//GEN-LAST:event_formComponentShown


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblRecord;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblWeight;
    private com.toedter.calendar.JDateChooser txtDate;
    private javax.swing.JTextField txtRefNo;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JTextField txtVouNo;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {
        if (source != null) {
            if (source.equals("WL-HISTORY")) {
                if (selectObj instanceof WeightLossHisKey key) {
                    setVoucher(inventoryRepo.findWeightLoss(key.getVouNo(), key.getDeptId()));
                }
            }
        }
    }

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
        historyWeight();
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

}