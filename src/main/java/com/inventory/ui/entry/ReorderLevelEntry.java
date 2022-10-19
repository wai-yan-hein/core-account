/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.inventory.ui.entry;

import com.common.Global;
import com.common.PanelControl;
import com.common.ReportFilter;
import com.common.RightCellRender;
import com.common.SelectionObserver;
import com.inventory.editor.BrandAutoCompleter;
import com.inventory.editor.CategoryAutoCompleter;
import com.inventory.editor.StockAutoCompleter;
import com.inventory.editor.StockCellEditor;
import com.inventory.editor.StockTypeAutoCompleter;
import com.inventory.model.ReorderLevel;
import com.inventory.model.StockUnit;
import com.inventory.ui.common.InventoryRepo;
import com.inventory.ui.common.ReorderTableModel;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.inventory.ui.setup.dialog.common.StockUnitEditor;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 *
 * @author Lenovo
 */
@Component
public class ReorderLevelEntry extends javax.swing.JPanel implements SelectionObserver, PanelControl {

    @Autowired
    private WebClient inventoryApi;
    @Autowired
    private InventoryRepo inventoryRepo;
    private final ReorderTableModel reorderTableModel = new ReorderTableModel();
    private StockTypeAutoCompleter typeAutoCompleter;
    private CategoryAutoCompleter categoryAutoCompleter;
    private BrandAutoCompleter brandAutoCompleter;
    private StockAutoCompleter stockAutoCompleter;
    private JProgressBar progress;
    private SelectionObserver observer;
    private List<StockUnit> listStockUnit = new ArrayList<>();

    public JProgressBar getProgress() {
        return progress;
    }

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }

    public SelectionObserver getObserver() {
        return observer;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    /**
     * Creates new form ReorderLevel
     */
    public ReorderLevelEntry() {
        initComponents();
    }

    public void initMain() {
        initCombo();
        initTable();
    }

    private void initCombo() {
        typeAutoCompleter = new StockTypeAutoCompleter(txtGroup, inventoryRepo.getStockType(), null, true, false);
        typeAutoCompleter.setObserver(this);
        categoryAutoCompleter = new CategoryAutoCompleter(txtCat, inventoryRepo.getCategory(), null, true, false);
        categoryAutoCompleter.setObserver(this);
        brandAutoCompleter = new BrandAutoCompleter(txtBrand, inventoryRepo.getStockBrand(), null, true, false);
        brandAutoCompleter.setObserver(this);
        stockAutoCompleter = new StockAutoCompleter(txtStock, inventoryRepo, null, true);
        stockAutoCompleter.setObserver(this);
    }

    private void initTable() {
        listStockUnit = inventoryRepo.getStockUnit();
        reorderTableModel.setTable(tblOrder);
        reorderTableModel.setInventoryRepo(inventoryRepo);
        tblOrder.setModel(reorderTableModel);
        tblOrder.getTableHeader().setFont(Global.tblHeaderFont);
        tblOrder.setRowHeight(Global.tblRowHeight);
        tblOrder.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblOrder.setFont(Global.textFont);
        tblOrder.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor(inventoryRepo));
        tblOrder.getColumnModel().getColumn(1).setCellEditor(new StockCellEditor(inventoryRepo));
        tblOrder.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());
        tblOrder.getColumnModel().getColumn(4).setCellEditor(new StockUnitEditor(listStockUnit));
        tblOrder.getColumnModel().getColumn(5).setCellEditor(new AutoClearEditor());
        tblOrder.getColumnModel().getColumn(6).setCellEditor(new StockUnitEditor(listStockUnit));
        tblOrder.getColumnModel().getColumn(0).setPreferredWidth(30);
        tblOrder.getColumnModel().getColumn(1).setPreferredWidth(200);
        tblOrder.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblOrder.getColumnModel().getColumn(3).setPreferredWidth(50);
        tblOrder.getColumnModel().getColumn(4).setPreferredWidth(50);
        tblOrder.getColumnModel().getColumn(5).setPreferredWidth(50);
        tblOrder.getColumnModel().getColumn(6).setPreferredWidth(50);
        tblOrder.getColumnModel().getColumn(7).setCellRenderer(new RightCellRender());
        tblOrder.getColumnModel().getColumn(8).setCellRenderer(new RightCellRender());
        tblOrder.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblOrder.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        getReorderLevel();
    }

    private void getReorderLevel() {
        progress.setIndeterminate(true);
        ReportFilter filter = new ReportFilter(Global.macId, Global.compCode, Global.deptId);
        filter.setBrandCode(brandAutoCompleter.getBrand().getKey().getBrandCode());
        filter.setCatCode(categoryAutoCompleter.getCategory().getKey().getCatCode());
        filter.setStockTypeCode(typeAutoCompleter.getStockType().getKey().getStockTypeCode());
        filter.setStockCode(stockAutoCompleter.getStock().getKey().getStockCode());
        Mono<ResponseEntity<List<ReorderLevel>>> result = inventoryApi
                .post()
                .uri("/report/get-reorder-level")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .toEntityList(ReorderLevel.class);
        result.subscribe((t) -> {
            reorderTableModel.setListPattern(t.getBody());
            calQty();
            progress.setIndeterminate(false);
        }, (e) -> {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
            progress.setIndeterminate(false);
        });
    }

    private void calQty() {
        int low = 0;
        int normal = 0;
        int high = 0;
        List<ReorderLevel> orders = reorderTableModel.getListPattern();
        if (!orders.isEmpty()) {
            for (ReorderLevel od : orders) {
                String status = od.getStatus();
                if (status != null) {
                    switch (status) {
                        case "LOW" ->
                            low += 1;
                        case "NORMAL" ->
                            low += 1;
                        case "HIGH" ->
                            high += 1;
                    }
                }
            }
        }
        txtLow.setText(String.valueOf(low));
        txtNormal.setText(String.valueOf(normal));
        txtHigh.setText(String.valueOf(high));
    }

    @Override
    public void selected(Object source, Object selectObj) {
        switch (source.toString()) {
            case "STOCK","ST","SC","SB" ->
                getReorderLevel();
            default -> {
                break;
            }
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

        jScrollPane1 = new javax.swing.JScrollPane();
        tblOrder = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtGroup = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtCat = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtBrand = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtStock = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel5 = new javax.swing.JLabel();
        txtLow = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtNormal = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtHigh = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        tblOrder.setAutoCreateRowSorter(true);
        tblOrder.setModel(new javax.swing.table.DefaultTableModel(
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
        tblOrder.setShowHorizontalLines(true);
        tblOrder.setShowVerticalLines(true);
        jScrollPane1.setViewportView(tblOrder);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Group");

        txtGroup.setFont(Global.textFont);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Category");

        txtCat.setFont(Global.textFont);

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Brand");

        txtBrand.setFont(Global.textFont);

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Stock Name");

        txtStock.setFont(Global.textFont);

        jLabel5.setFont(Global.textFont);
        jLabel5.setText("Low");

        txtLow.setEditable(false);
        txtLow.setFont(Global.textFont);
        txtLow.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtLow.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel6.setFont(Global.textFont);
        jLabel6.setText("Normal");

        txtNormal.setEditable(false);
        txtNormal.setFont(Global.textFont);
        txtNormal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtNormal.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel7.setFont(Global.textFont);
        jLabel7.setText("High");

        txtHigh.setEditable(false);
        txtHigh.setFont(Global.textFont);
        txtHigh.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtHigh.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtGroup)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtCat)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtBrand)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtStock)
                        .addGap(2, 2, 2))
                    .addComponent(jSeparator2)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtLow, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtNormal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtHigh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtGroup)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtCat)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtBrand)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtStock))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtLow, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(txtNormal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(txtHigh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observer.selected("control", this);
    }//GEN-LAST:event_formComponentShown


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTable tblOrder;
    private javax.swing.JTextField txtBrand;
    private javax.swing.JTextField txtCat;
    private javax.swing.JTextField txtGroup;
    private javax.swing.JTextField txtHigh;
    private javax.swing.JTextField txtLow;
    private javax.swing.JTextField txtNormal;
    private javax.swing.JTextField txtStock;
    // End of variables declaration//GEN-END:variables

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
        getReorderLevel();
    }

    @Override
    public void filter() {
    }

    @Override
    public String panelName() {
        return this.getName();
    }
}
