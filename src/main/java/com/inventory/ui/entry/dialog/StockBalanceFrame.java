/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package com.inventory.ui.entry.dialog;

import com.common.Global;
import com.common.ProUtil;
import com.common.ReportFilter;
import com.common.TableCellRender;
import com.common.Util1;
import com.inventory.entity.Stock;
import com.inventory.ui.common.StockBalanceRelationTableModel;
import com.inventory.ui.common.StockBalanceTableModel;
import com.inventory.ui.common.StockBalanceWeightTableModel;
import com.repo.InventoryRepo;
import com.ui.management.model.ClosingBalance;
import javax.swing.JFrame;
import javax.swing.ListSelectionModel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class StockBalanceFrame extends javax.swing.JInternalFrame {

    @Setter
    private InventoryRepo inventoryRepo;
    private StockBalanceTableModel stockBalanceTableModel = new StockBalanceTableModel();
    private StockBalanceRelationTableModel relationTableModel = new StockBalanceRelationTableModel();
    private StockBalanceWeightTableModel stockBalanceWeightTableModel = new StockBalanceWeightTableModel();

    /**
     * Creates new form StockBalanceIFrame
     */
    public StockBalanceFrame() {
        initComponents();
    }

    private void initMain(boolean weight, String relCode) {
        if (weight) {
            tblStock.setModel(stockBalanceWeightTableModel);
            tblStock.getColumnModel().getColumn(0).setPreferredWidth(100);//loc
            tblStock.getColumnModel().getColumn(1).setPreferredWidth(80);//weight
            tblStock.getColumnModel().getColumn(2).setPreferredWidth(80);//qty
            tblStock.getColumnModel().getColumn(3).setPreferredWidth(80);//bag
        } else {
            if (Util1.isNullOrEmpty(relCode)) {
                tblStock.setModel(stockBalanceTableModel);
            } else {
                tblStock.setModel(relationTableModel);
            }
            tblStock.getColumnModel().getColumn(0).setPreferredWidth(100);//
            tblStock.getColumnModel().getColumn(1).setPreferredWidth(100); // qty
        }
        tblStock.getTableHeader().setFont(Global.tblHeaderFont);
        tblStock.setDefaultRenderer(Object.class, new TableCellRender());
        tblStock.setDefaultRenderer(Double.class, new TableCellRender());
        tblStock.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    public void calStock(Stock s, JFrame frame) {
        String stockCode = s.getKey().getStockCode();
        String relCode = s.getRelCode();
        if (ProUtil.isCalStock()) {
            progress.setIndeterminate(true);
            boolean weight = ProUtil.isUseWeight();
            initMain(weight, relCode);
            ReportFilter filter = new ReportFilter(Global.macId, Global.compCode, Global.deptId);
            filter.setStockCode(stockCode);
            filter.setToDate(Util1.toDateStr(Util1.getTodayDate(), "yyyy-MM-dd"));
            filter.setSummary(chkSummary.isSelected());
            clear();
            if (Util1.isNullOrEmpty(relCode)) {
                inventoryRepo.getStockBalanceQty(filter)
                        .doOnNext(this::addObject)
                        .doOnNext(obj -> calTotal())
                        .doOnError((e) -> {
                            log.error("getStockBalanceQty : " + e.getMessage());
                            progress.setIndeterminate(false);
                        }).doOnTerminate(() -> {
                    progress.setIndeterminate(false);
                    setVisible(true);
                }).subscribe();
            } else {
                inventoryRepo.getStockBalanceRel(filter)
                        .doOnNext(relationTableModel::addObject)
                        .doOnNext(obj -> calTotal())
                        .doOnError((e) -> {
                            log.error("getStockBalanceRel : " + e.getMessage());
                            progress.setIndeterminate(false);
                        }).doOnTerminate(() -> {
                    progress.setIndeterminate(false);
                    setVisible(true);
                }).subscribe();
            }
        }
    }

    public void clear() {
        stockBalanceTableModel.clearList();
        stockBalanceWeightTableModel.clearList();
        relationTableModel.clearList();

    }

    private void addObject(ClosingBalance cl) {
        boolean weight = ProUtil.isUseWeight();
        if (weight) {
            stockBalanceWeightTableModel.addObject(cl);
        } else {
            stockBalanceTableModel.addObject(cl);
        }
    }

    private void calTotal() {
        boolean weight = ProUtil.isUseWeight();
        if (weight) {
            txtTotal.setValue(stockBalanceWeightTableModel.getTotal());
        } else {
            txtTotal.setValue(stockBalanceTableModel.getTotal());
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

        jScrollPane2 = new javax.swing.JScrollPane();
        tblStock = new javax.swing.JTable();
        chkSummary = new javax.swing.JCheckBox();
        progress = new javax.swing.JProgressBar();
        txtTotal = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Stock Balance Dialog");
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/images/spring-icon.png"))); // NOI18N

        tblStock.setFont(Global.textFont);
        tblStock.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblStock.setRowHeight(Global.tblRowHeight);
        jScrollPane2.setViewportView(tblStock);

        chkSummary.setText("Summary");

        txtTotal.setEditable(false);
        txtTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotal.setFont(Global.amtFont);

        jLabel1.setText("Total :");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(progress, javax.swing.GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE)
                    .addComponent(chkSummary, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(progress, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkSummary)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkSummary;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JProgressBar progress;
    private javax.swing.JTable tblStock;
    private javax.swing.JFormattedTextField txtTotal;
    // End of variables declaration//GEN-END:variables
}
