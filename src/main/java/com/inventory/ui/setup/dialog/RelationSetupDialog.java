/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup.dialog;

import com.common.Global;
import com.common.StartWithRowFilter;
import com.common.TableCellRender;
import com.common.UnitFormatRender;
import com.inventory.editor.StockUnitEditor;
import com.inventory.model.UnitRelationDetail;
import com.inventory.model.UnitRelation;
import com.inventory.ui.common.InventoryRepo;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.inventory.ui.setup.dialog.common.RelationDetailTableModel;
import com.inventory.ui.setup.dialog.common.RelationTableModel;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.Objects;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lenovo
 */
public class RelationSetupDialog extends javax.swing.JDialog implements KeyListener {

    private static final Logger log = LoggerFactory.getLogger(RelationSetupDialog.class);

    private int selectRow = - 1;
    private final RelationTableModel relationTableModel = new RelationTableModel();
    private final RelationDetailTableModel relationDetailTableModel = new RelationDetailTableModel();
    private InventoryRepo inventoryRepo;
    private TableRowSorter<TableModel> sorter;
    private StartWithRowFilter swrf;
    private List<UnitRelation> listUnitRelation;

    public InventoryRepo getInventoryRepo() {
        return inventoryRepo;
    }

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public List<UnitRelation> getListUnitRelation() {
        return listUnitRelation;
    }

    public void setListUnitRelation(List<UnitRelation> listUnitRelation) {
        this.listUnitRelation = listUnitRelation;
    }

    /**
     * Creates new form ItemTypeSetupDialog
     */
    public RelationSetupDialog() {
        super(Global.parentForm, false);
        initComponents();
        initKeyListener();
        lblStatus.setForeground(Color.green);
    }

    public void initMain() {
        swrf = new StartWithRowFilter(txtFilter);
        initTable();
        initTableRelD();
        searchCategory();
    }

    private void initKeyListener() {
        btnClear.addKeyListener(this);
        btnSave.addKeyListener(this);
        tblRel.addKeyListener(this);
    }

    private void searchCategory() {
        relationTableModel.setListRelation(listUnitRelation);
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
                    if (rel.getRelCode() != null) {
                        relationDetailTableModel.setListRelation(inventoryRepo.getRelationDetail(rel.getRelCode()));
                        relationDetailTableModel.setRelation(rel);
                        lblName.setText(rel.getRelName());
                        lblStatus.setText("EDIT");
                        lblStatus.setForeground(Color.blue);
                    }

                }
            }
        });
    }

    private void initTableRelD() {
        relationDetailTableModel.setTable(tblRelD);
        tblRelD.setModel(relationDetailTableModel);
        tblRelD.getTableHeader().setFont(Global.lableFont);
        tblRelD.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblRelD.setDefaultRenderer(Float.class, new UnitFormatRender());
        tblRel.setRowHeight(Global.tblRowHeight);
        tblRelD.getColumnModel().getColumn(0).setCellEditor(new AutoClearEditor());
        tblRelD.getColumnModel().getColumn(1).setCellEditor(new StockUnitEditor(inventoryRepo.getStockUnit()));
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
            rel.setDetailList(listD);
        } else {
            rel.setDetailList(listD);
        }
        if (!listD.isEmpty()) {
            rel = inventoryRepo.saveUnitRelation(rel);
            if (lblStatus.getText().equals("NEW")) {
                relationTableModel.addRelation(rel);
            } else {
                relationTableModel.setRelation(rel, selectRow);
            }
            clear();
        }
    }

    private void clear() {
        txtFilter.setText(null);
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
        txtFilter = new javax.swing.JTextField();
        lblStatus = new javax.swing.JLabel();
        btnSave = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblRelD = new javax.swing.JTable();
        lblName = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
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

        txtFilter.setName("txtFilter"); // NOI18N
        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
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
        tblRelD.setShowGrid(true);
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
                    .addComponent(txtFilter)
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
                    .addComponent(lblName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnClear, btnSave});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtFilter))
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
        try {
            save();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Save Category", JOptionPane.ERROR_MESSAGE);
            log.error("Save Categor :" + e.getMessage());
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        // TODO add your handling code here:
        clear();
    }//GEN-LAST:event_btnClearActionPerformed

    private void txtFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyReleased
        // TODO add your handling code here:
        if (txtFilter.getText().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(swrf);
        }
    }//GEN-LAST:event_txtFilterKeyReleased

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
    private javax.swing.JTable tblRel;
    private javax.swing.JTable tblRelD;
    private javax.swing.JTextField txtFilter;
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
