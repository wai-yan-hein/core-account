/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.inventory.ui.entry;

import com.common.Global;
import com.common.ReportFilter;
import com.common.SelectionObserver;
import com.inventory.editor.BrandAutoCompleter;
import com.inventory.editor.CategoryAutoCompleter;
import com.inventory.editor.StockAutoCompleter;
import com.inventory.editor.StockCellEditor;
import com.inventory.editor.StockTypeAutoCompleter;
import com.inventory.model.ReorderLevel;
import com.inventory.model.Stock;
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
public class ReorderLevelEntry extends javax.swing.JPanel implements SelectionObserver {

    @Autowired
    private WebClient inventoryApi;
    @Autowired
    private InventoryRepo inventoryRepo;
    ReorderTableModel reorderTableModel = new ReorderTableModel();
    private StockTypeAutoCompleter typeAutoCompleter;
    private CategoryAutoCompleter categoryAutoCompleter;
    private BrandAutoCompleter brandAutoCompleter;
    private StockAutoCompleter stockAutoCompleter;
    private JProgressBar progress;
    private SelectionObserver observer;
    private List<StockUnit> listStockUnit = new ArrayList<>();
    private List<Stock> listStock = new ArrayList<>();

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
        stockAutoCompleter = new StockAutoCompleter(txtStock, inventoryRepo.getStock(true), null, true, false);
    }

    private void initTable() {
        listStock = inventoryRepo.getStock(true);
        listStockUnit = inventoryRepo.getStockUnit();
        reorderTableModel.setTable(tblOrder);
        reorderTableModel.setInventoryRepo(inventoryRepo);
        tblOrder.setModel(reorderTableModel);
        tblOrder.getTableHeader().setFont(Global.tblHeaderFont);
        tblOrder.setRowHeight(Global.tblRowHeight);
        tblOrder.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblOrder.setFont(Global.textFont);
        tblOrder.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor(listStock));
        tblOrder.getColumnModel().getColumn(1).setCellEditor(new StockCellEditor(listStock));
        tblOrder.getColumnModel().getColumn(2).setCellEditor(new AutoClearEditor());
        tblOrder.getColumnModel().getColumn(3).setCellEditor(new StockUnitEditor(listStockUnit));
        tblOrder.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());
        tblOrder.getColumnModel().getColumn(5).setCellEditor(new StockUnitEditor(listStockUnit));
        tblOrder.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblOrder.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblOrder.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblOrder.getColumnModel().getColumn(3).setPreferredWidth(50);
        tblOrder.getColumnModel().getColumn(4).setPreferredWidth(50);
        tblOrder.getColumnModel().getColumn(5).setPreferredWidth(50);
        tblOrder.getColumnModel().getColumn(6).setPreferredWidth(50);
        tblOrder.getColumnModel().getColumn(6).setPreferredWidth(50);
        tblOrder.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblOrder.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        getReorderLevel();
    }

    private void getReorderLevel() {
        progress.setIndeterminate(true);
        ReportFilter filter = new ReportFilter(Global.macId, Global.compCode);
        filter.setBrandCode(brandAutoCompleter.getBrand().getBrandCode());
        filter.setCatCode(categoryAutoCompleter.getCategory().getCatCode());
        filter.setStockTypeCode(typeAutoCompleter.getStockType().getStockTypeCode());
        filter.setStockCode(stockAutoCompleter.getStock().getStockCode());
        Mono<ResponseEntity<List<ReorderLevel>>> result = inventoryApi
                .post()
                .uri("/report/get-reorder-level")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .toEntityList(ReorderLevel.class);
        result.subscribe((t) -> {
            reorderTableModel.setListPattern(t.getBody());
            progress.setIndeterminate(false);
        }, (e) -> {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
            progress.setIndeterminate(false);
        });
    }

    @Override
    public void selected(Object source, Object selectObj) {
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
                        .addGap(2, 2, 2)))
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable tblOrder;
    private javax.swing.JTextField txtBrand;
    private javax.swing.JTextField txtCat;
    private javax.swing.JTextField txtGroup;
    private javax.swing.JTextField txtStock;
    // End of variables declaration//GEN-END:variables
}
