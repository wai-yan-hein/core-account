/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.inventory.ui.entry.dialog;

import com.common.Global;
import com.common.TableCellRender;
import com.inventory.ui.common.OrderTableModel;
import com.repo.InventoryRepo;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

/**
 *
 * @author Lenovo
 */
public class OrderDetailDialog extends javax.swing.JDialog {

    private final OrderTableModel orderTableModel = new OrderTableModel();
    private InventoryRepo inventoryRepo;

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    /**
     * Creates new form GRNDetailDialog
     *
     * @param frame
     */
    public OrderDetailDialog(JFrame frame) {
        super(frame, true);
        initComponents();
    }

    public void initMain() {
        initTable();
    }

    private void initTable() {
        orderTableModel.setLblRecord(lblRecord);
        tblGRN.setModel(orderTableModel);
        tblGRN.getTableHeader().setFont(Global.tblHeaderFont);
        tblGRN.setCellSelectionEnabled(true);
        tblGRN.setRowHeight(Global.tblRowHeight);
        tblGRN.setFont(Global.textFont);
        tblGRN.getColumnModel().getColumn(0).setPreferredWidth(30);//Code
        tblGRN.getColumnModel().getColumn(1).setPreferredWidth(200);//Name
        tblGRN.getColumnModel().getColumn(2).setPreferredWidth(30);//relation
        tblGRN.getColumnModel().getColumn(3).setPreferredWidth(30);//loc
        tblGRN.getColumnModel().getColumn(4).setPreferredWidth(20);//w
        tblGRN.getColumnModel().getColumn(5).setPreferredWidth(10);//wu
        tblGRN.getColumnModel().getColumn(6).setPreferredWidth(20);//qty
        tblGRN.getColumnModel().getColumn(7).setPreferredWidth(10);//unit 
        tblGRN.getColumnModel().getColumn(8).setPreferredWidth(10);//total   
        tblGRN.setDefaultRenderer(Object.class, new TableCellRender());
        tblGRN.setDefaultRenderer(Float.class, new TableCellRender());
        tblGRN.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblGRN.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    public void searchOrderDetail(String vouNo, Integer deptId) {
        if (!vouNo.isEmpty()) {
            progress.setIndeterminate(true);
            inventoryRepo.getOrderDetail(vouNo).doOnSuccess((t) -> {
                orderTableModel.setListDetail(t);
            }).doOnTerminate(() -> {
                progress.setIndeterminate(false);
                setVisible(true);
            }).subscribe();
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
        tblGRN = new javax.swing.JTable();
        progress = new javax.swing.JProgressBar();
        jLabel1 = new javax.swing.JLabel();
        lblRecord = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Order Detail Dialog");

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

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Records : ");

        lblRecord.setFont(Global.lableFont);
        lblRecord.setText("0");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 518, Short.MAX_VALUE)
                    .addComponent(progress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblRecord, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(progress, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(lblRecord))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblRecord;
    private javax.swing.JProgressBar progress;
    private javax.swing.JTable tblGRN;
    // End of variables declaration//GEN-END:variables
}
