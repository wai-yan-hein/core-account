/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.inventory.ui.setup;

import com.common.DecimalFormatRender;
import com.common.Global;
import com.common.PanelControl;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.common.Util1;
import com.inventory.editor.StockCriteriaEditor;
import com.inventory.model.StockFormula;
import com.inventory.ui.common.StockFormulaDetailTableModel;
import com.inventory.ui.common.StockFormulaTableModel;
import com.inventory.ui.setup.dialog.StockCriteriaSetupDialog;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.repo.InventoryRepo;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

/**
 *
 * @author Lenovo
 */
public class StockFormulaSetup extends javax.swing.JPanel implements SelectionObserver, PanelControl {

    private StockCriteriaSetupDialog scDialog;
    private InventoryRepo inventoryRepo;
    private SelectionObserver observer;
    private JProgressBar progress;
    private StockFormulaTableModel stockFormulaTableModel = new StockFormulaTableModel();
    private StockFormulaDetailTableModel stockFormulaDetailTableModel = new StockFormulaDetailTableModel();

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    /**
     * Creates new form StockFormulaSetup
     */
    public StockFormulaSetup() {
        initComponents();
    }

    public void initMain() {
        initTableFormula();
        initTableCriteria();
        searchFormual();
    }

    private void stockCriteriaDialog() {
        if (scDialog == null) {
            scDialog = new StockCriteriaSetupDialog(Global.parentForm);
            scDialog.setInventoryRepo(inventoryRepo);
            scDialog.initMain();
            scDialog.setLocationRelativeTo(null);
        }
        scDialog.search();
    }

    private void initTableFormula() {
        stockFormulaTableModel.setTable(tblStock);
        stockFormulaTableModel.setInventoryRepo(inventoryRepo);
        tblStock.setCellSelectionEnabled(true);
        tblStock.setModel(stockFormulaTableModel);
        tblStock.getTableHeader().setFont(Global.tblHeaderFont);
        tblStock.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblStock.setFont(Global.textFont);
        tblStock.setRowHeight(Global.tblRowHeight);
        tblStock.setShowGrid(true);
        tblStock.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblStock.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblStock.getColumnModel().getColumn(0).setCellEditor(new AutoClearEditor());
        tblStock.getColumnModel().getColumn(1).setCellEditor(new AutoClearEditor());
        tblStock.getSelectionModel().addListSelectionListener((e) -> {
            if (e.getValueIsAdjusting()) {
                searchFormualDetail();
            }
        });
    }

    private void initTableCriteria() {
        stockFormulaDetailTableModel.setInventoryRepo(inventoryRepo);
        stockFormulaDetailTableModel.setParent(tblCriteria);
        tblCriteria.setModel(stockFormulaDetailTableModel);
        tblCriteria.getTableHeader().setFont(Global.tblHeaderFont);
        tblCriteria.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCriteria.setFont(Global.textFont);
        tblCriteria.setRowHeight(Global.tblRowHeight);
        tblCriteria.setShowGrid(true);
        tblCriteria.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblCriteria.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCriteria.getColumnModel().getColumn(0).setCellEditor(new StockCriteriaEditor(inventoryRepo));
        tblCriteria.getColumnModel().getColumn(1).setCellEditor(new StockCriteriaEditor(inventoryRepo));
        tblCriteria.getColumnModel().getColumn(2).setCellEditor(new AutoClearEditor());
        tblCriteria.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());
    }

    private void searchFormual() {
        progress.setIndeterminate(true);
        inventoryRepo.getStockFormula().doOnSuccess((t) -> {
            stockFormulaTableModel.setListDetail(t);
        }).doOnError((e) -> {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }).doOnTerminate(() -> {
            stockFormulaTableModel.addNewRow();
            progress.setIndeterminate(false);
        }).subscribe();
    }

    private void searchFormualDetail() {
        int row = tblStock.convertRowIndexToModel(tblStock.getSelectedRow());
        if (row >= 0) {
            StockFormula f = stockFormulaTableModel.getObject(row);
            String formulaCode = f.getKey().getFormulaCode();
            if (!Util1.isNullOrEmpty(formulaCode)) {
                inventoryRepo.getStockFormulaDetail(formulaCode).doOnSuccess((t) -> {
                    stockFormulaDetailTableModel.setListDetail(t);
                }).doOnTerminate(() -> {
                    stockFormulaDetailTableModel.setFormulaCode(formulaCode);
                    stockFormulaDetailTableModel.addNewRow();
                }).subscribe();
            }
        }
    }

    private void observeMain() {
        observer.selected("control", this);
        observer.selected("save", false);
        observer.selected("print", false);
        observer.selected("history", false);
        observer.selected("delete", false);
        observer.selected("refresh", true);
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
        jScrollPane1 = new javax.swing.JScrollPane();
        tblStock = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblCriteria = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        tblStock.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblStock);

        jLabel2.setFont(Global.menuFont);
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Formula List");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 427, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jButton3.setFont(Global.lableFont);
        jButton3.setText("New Criteria");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        tblCriteria.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tblCriteria);

        jLabel1.setFont(Global.menuFont);
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Formula Detail");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        stockCriteriaDialog();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observeMain();
    }//GEN-LAST:event_formComponentShown


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblCriteria;
    private javax.swing.JTable tblStock;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {
    }

    @Override
    public void save() {
    }

    @Override
    public void delete() {
    }

    @Override
    public void newForm() {
    }

    @Override
    public void history() {
    }

    @Override
    public void print() {
    }

    @Override
    public void refresh() {
        searchFormual();
    }

    @Override
    public void filter() {
    }

    @Override
    public String panelName() {
        return this.getName();
    }
}
