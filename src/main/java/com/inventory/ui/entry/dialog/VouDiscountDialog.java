/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.inventory.ui.entry.dialog;

import com.common.Global;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.editor.DiscountDescriptionEditor;
import com.inventory.editor.StockUnitEditor;
import com.inventory.model.VouDiscount;
import com.inventory.model.VouDiscountKey;
import com.inventory.ui.common.VoucherDiscountTableModel;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.repo.InventoryRepo;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class VouDiscountDialog extends javax.swing.JDialog implements SelectionObserver {

    private VoucherDiscountTableModel tableModel = new VoucherDiscountTableModel();
    private InventoryRepo inventoryRepo;
    private SelectionObserver observer;

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    /**
     * Creates new form VouDiscountDialog
     *
     * @param frame
     */
    public VouDiscountDialog(JFrame frame) {
        super(frame, false);
        initComponents();
        actionMapping();
    }

    public List<VouDiscount> getListDetail() {
        return tableModel.getListDetail();
    }

    public List<VouDiscountKey> getListDel() {
        return tableModel.getListDel();
    }

    public double getTotal() {
        return Util1.getDouble(txtDiscount.getValue());
    }

    private void actionMapping() {
        String solve = "delete";
        KeyStroke delete = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        tblDiscount.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(delete, solve);
        tblDiscount.getActionMap().put(solve, new DeleteExpense());
    }

    public void clear() {
        tableModel.clear();
        txtDiscount.setValue(0);
    }

    private class DeleteExpense extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            deleteTranExpense();
        }
    }

    private void deleteTranExpense() {
        int row = tblDiscount.convertRowIndexToModel(tblDiscount.getSelectedRow());
        if (row >= 0) {
            int yes_no = JOptionPane.showConfirmDialog(this,
                    "Are you sure to delete?", "Expense Transaction delete.", JOptionPane.YES_NO_OPTION);
            if (yes_no == 0) {
                tableModel.delete(row);
                calTotalAmount();
            }
        }
    }

    private void calTotalAmount() {
        double ttlAmt = tableModel.getListDetail().stream().mapToDouble((t) -> t.getAmount()).sum();
        txtDiscount.setValue(ttlAmt);
        observer.selected("CAL_DISCOUNT", "CAL_DISCOUNT");
    }

    public void initMain() {
        initTable();
    }

    private void initTable() {
        tableModel.addNewRow();
        tableModel.setObserver(this);
        tableModel.setTable(tblDiscount);
        tblDiscount.setModel(tableModel);
        tblDiscount.getTableHeader().setFont(Global.tblHeaderFont);
        tblDiscount.setFont(Global.textFont);
        tblDiscount.setRowHeight(Global.tblRowHeight);
        tblDiscount.setCellSelectionEnabled(true);
        tblDiscount.setShowGrid(true);
        tblDiscount.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblDiscount.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblDiscount.getColumnModel().getColumn(0).setPreferredWidth(100);
        tblDiscount.getColumnModel().getColumn(1).setPreferredWidth(20);
        tblDiscount.getColumnModel().getColumn(2).setPreferredWidth(60);
        tblDiscount.getColumnModel().getColumn(3).setPreferredWidth(60);
        tblDiscount.getColumnModel().getColumn(4).setPreferredWidth(60);
        tblDiscount.getColumnModel().getColumn(0).setCellEditor(new DiscountDescriptionEditor(inventoryRepo));
        inventoryRepo.getStockUnit().subscribe((t) -> {
            tblDiscount.getColumnModel().getColumn(1).setCellEditor(new StockUnitEditor(t));
        });
        tblDiscount.getColumnModel().getColumn(2).setCellEditor(new AutoClearEditor());
        tblDiscount.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());
        tblDiscount.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());
    }

    public void search(String vouNo) {
        if (!Util1.isNullOrEmpty(vouNo)) {
            inventoryRepo.getVoucherDiscount(vouNo).doOnSuccess((t) -> {
                tableModel.setListDetail(t);
                tableModel.addNewRow();
            }).doOnTerminate(() -> {
                focusTable();
                setVisible(true);
            }).subscribe();
        } else {
            tableModel.addNewRow();
            focusTable();
            setVisible(true);
        }
    }

    private void focusTable() {
        int rc = tblDiscount.getRowCount();
        if (rc >= 1) {
            tblDiscount.setRowSelectionInterval(rc - 1, rc - 1);
            tblDiscount.setColumnSelectionInterval(0, 0);
            tblDiscount.requestFocus();
        }
    }

    @Override
    public void selected(Object source, Object selectObj) {
        if (source.equals("CAL_TOTAL")) {
            calTotalAmount();
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
        tblDiscount = new javax.swing.JTable();
        txtDiscount = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Voucher Discount");

        tblDiscount.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblDiscount);

        txtDiscount.setEditable(false);
        txtDiscount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDiscount.setFont(Global.amtFont);

        jLabel1.setText("Discount Total :");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 534, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblDiscount;
    private javax.swing.JFormattedTextField txtDiscount;
    // End of variables declaration//GEN-END:variables
}
