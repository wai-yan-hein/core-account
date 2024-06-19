/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup.dialog;

import com.common.Global;
import com.common.IconUtil;
import com.common.StartWithRowFilter;
import com.common.TableCellRender;
import com.common.UnitFormatRender;
import com.formdev.flatlaf.FlatClientProperties;
import com.inventory.editor.StockUnitEditor;
import com.inventory.entity.MessageType;
import com.inventory.entity.RelationKey;
import com.inventory.entity.UnitRelationDetail;
import com.inventory.entity.UnitRelation;
import com.repo.InventoryRepo;
import com.user.editor.AutoClearEditor;
import com.inventory.ui.setup.dialog.common.RelationDetailTableModel;
import com.inventory.ui.setup.dialog.common.RelationTableModel;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.Objects;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class RelationSetupDialog extends javax.swing.JDialog implements KeyListener {

    private int selectRow = - 1;
    private final RelationTableModel relationTableModel = new RelationTableModel();
    private final RelationDetailTableModel relationDetailTableModel = new RelationDetailTableModel();
    @Setter
    private InventoryRepo inventoryRepo;
    private TableRowSorter<TableModel> sorter;
    private StartWithRowFilter swrf;

    /**
     * Creates new form ItemTypeSetupDialog
     *
     * @param frame
     */
    public RelationSetupDialog(JFrame frame) {
        super(frame, false);
        initComponents();
        initKeyListener();
        initClientProperty();
        lblStatus.setForeground(Color.green);
    }

    public void initMain() {
        initTable();
        initTableRelD();
    }

    private void initClientProperty() {
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search Here");
        txtSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        txtSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, IconUtil.getIcon(IconUtil.SEARCH_ICON));
    }

    private void initKeyListener() {
        btnClear.addKeyListener(this);
        btnSave.addKeyListener(this);
        tblRel.addKeyListener(this);
    }

    private void initTable() {
        tblRel.setModel(relationTableModel);
        sorter = new TableRowSorter<>(tblRel.getModel());
        tblRel.setRowSorter(sorter);
        tblRel.getTableHeader().setFont(Global.lableFont);
        tblRel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblRel.setDefaultRenderer(Object.class, new TableCellRender());
        tblRel.setRowHeight(Global.tblRowHeight);
        tblRel.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting()) {
                if (tblRel.getSelectedRow() >= 0) {
                    selectRow = tblRel.convertRowIndexToModel(tblRel.getSelectedRow());
                    UnitRelation rel = relationTableModel.getRelation(selectRow);
                    String relCode = rel.getKey().getRelCode();
                    if (relCode != null) {
                        progress.setIndeterminate(true);
                        lblName.setText(rel.getRelName());
                        lblStatus.setText("EDIT");
                        lblStatus.setForeground(Color.blue);
                        inventoryRepo.getRelationDetail(relCode).doOnSuccess((t) -> {
                            relationDetailTableModel.setListRelation(t);
                            relationDetailTableModel.addEmptyRow();
                            relationDetailTableModel.setRelation(rel);
                            progress.setIndeterminate(false);
                        }).subscribe();

                    }

                }
            }
        });
        swrf = new StartWithRowFilter(txtSearch);
    }

    private void initTableRelD() {
        relationDetailTableModel.setTable(tblRelD);
        tblRelD.setModel(relationDetailTableModel);
        tblRelD.getTableHeader().setFont(Global.lableFont);
        tblRelD.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblRelD.setDefaultRenderer(Double.class, new UnitFormatRender());
        tblRel.setRowHeight(Global.tblRowHeight);
        tblRelD.getColumnModel().getColumn(0).setCellEditor(new AutoClearEditor());
        inventoryRepo.getStockUnit().doOnSuccess((t) -> {
            tblRelD.getColumnModel().getColumn(1).setCellEditor(new StockUnitEditor(t));
        }).subscribe();
        relationDetailTableModel.addEmptyRow();
        tblRelD.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblRelD.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblRelD.setCellSelectionEnabled(true);
    }

    private void save() {
        UnitRelation rel = relationDetailTableModel.getRelation();
        List<UnitRelationDetail> listD = relationDetailTableModel.getListRelation();
        if (Objects.isNull(rel)) {
            rel = new UnitRelation();
            RelationKey key = new RelationKey();
            key.setCompCode(Global.compCode);
            key.setRelCode(null);
            rel.setKey(key);
            rel.setDeptId(Global.deptId);
            rel.setDetailList(listD);
        } else {
            rel.setDetailList(listD);
        }
        if (!listD.isEmpty()) {
            progress.setIndeterminate(true);
            inventoryRepo.saveUnitRelation(rel).doOnSuccess((t) -> {
                if (lblStatus.getText().equals("NEW")) {
                    relationTableModel.addRelation(t);
                } else {
                    relationTableModel.setRelation(t, selectRow);
                }
                sendMessage(t.getRelName());
            }).doOnTerminate(() -> {
                clear();
            }).subscribe();

        }
    }

    private void sendMessage(String mes) {
        inventoryRepo.sendDownloadMessage(MessageType.RELATION, mes)
                .doOnSuccess((t) -> {
                    log.info(t);
                }).subscribe();
    }

    private void clear() {
        progress.setIndeterminate(false);
        txtSearch.setText(null);
        lblStatus.setText("NEW");
        lblStatus.setForeground(Color.green);
        lblName.setText("");
        relationDetailTableModel.clear();
        relationDetailTableModel.addEmptyRow();
        focusRDTable();
    }

    private void focusRDTable() {
        int row = tblRelD.getRowCount();
        if (row > 0) {
            tblRelD.setRowSelectionInterval(row - 1, row - 1);
        } else {
            tblRelD.setRowSelectionInterval(0, 0);
        }
        tblRelD.setColumnSelectionInterval(0, 0);
        tblRelD.requestFocus();
    }

    public void search() {
        progress.setIndeterminate(true);
        inventoryRepo.getUnitRelation().doOnSuccess((t) -> {
            relationTableModel.setListRelation(t);
            progress.setIndeterminate(false);
        }).subscribe();
        setVisible(true);
    }

    public List<UnitRelationDetail> getDetail() {
        return relationDetailTableModel.getListRelation();
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
        tblRel = new javax.swing.JTable();
        txtSearch = new javax.swing.JTextField();
        lblStatus = new javax.swing.JLabel();
        btnSave = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblRelD = new javax.swing.JTable();
        lblName = new javax.swing.JLabel();
        progress = new javax.swing.JProgressBar();

        setTitle("Relation Setup");
        setModalityType(java.awt.Dialog.ModalityType.TOOLKIT_MODAL);

        tblRel.setFont(Global.textFont);
        tblRel.setModel(new javax.swing.table.DefaultTableModel(
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
        tblRel.setName("tblRel"); // NOI18N
        jScrollPane1.setViewportView(tblRel);

        txtSearch.setName("txtSearch"); // NOI18N
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });

        lblStatus.setFont(Global.menuFont);
        lblStatus.setText("NEW");

        btnSave.setBackground(Global.selectionColor);
        btnSave.setFont(Global.lableFont);
        btnSave.setForeground(new java.awt.Color(255, 255, 255));
        btnSave.setText("Save");
        btnSave.setName("btnSave"); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnClear.setFont(Global.lableFont);
        btnClear.setText("Clear");
        btnClear.setName("btnClear"); // NOI18N
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        tblRelD.setFont(Global.textFont);
        tblRelD.setModel(new javax.swing.table.DefaultTableModel(
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
        tblRelD.setRowHeight(Global.tblRowHeight);
        tblRelD.setShowHorizontalLines(true);
        tblRelD.setShowVerticalLines(true);
        jScrollPane3.setViewportView(tblRelD);

        lblName.setFont(Global.menuFont);
        lblName.setText("-");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(progress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtSearch)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 468, Short.MAX_VALUE)
                            .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 468, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSave))
                            .addComponent(lblName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnClear, btnSave});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(progress, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtSearch))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStatus)
                    .addComponent(btnSave)
                    .addComponent(btnClear))
                .addContainerGap())
        );

        getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        save();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        // TODO add your handling code here:
        clear();
    }//GEN-LAST:event_btnClearActionPerformed

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        // TODO add your handling code here:
        if (txtSearch.getText().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(swrf);
        }
    }//GEN-LAST:event_txtSearchKeyReleased

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnSave;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JProgressBar progress;
    private javax.swing.JTable tblRel;
    private javax.swing.JTable tblRelD;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
