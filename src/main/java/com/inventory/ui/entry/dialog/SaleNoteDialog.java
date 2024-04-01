/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.inventory.ui.entry.dialog;

import com.common.Global;
import com.common.JasperReportUtil;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.dto.SaleNote;
import com.inventory.editor.StockUnitEditor;
import com.inventory.entity.SaleHis;
import com.inventory.entity.SaleHisDetail;
import com.inventory.entity.Trader;
import com.inventory.ui.entry.dialog.common.SaleNoteTableModel;
import com.user.editor.AutoClearEditor;
import com.repo.InventoryRepo;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JsonDataSource;
import reactor.core.publisher.Flux;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class SaleNoteDialog extends javax.swing.JDialog implements SelectionObserver {

    private SaleNoteTableModel tableModel = new SaleNoteTableModel();
    private InventoryRepo inventoryRepo;
    private SaleHis sh;

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    /**
     * Creates new form VouDiscountDialog
     *
     * @param frame
     */
    public SaleNoteDialog(JFrame frame) {
        super(frame, false);
        initComponents();
        actionMapping();
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

    public List<SaleNote> getListDetail() {
        return tableModel.getListDetail();
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
        double ttlAmt = tableModel.getListDetail().stream().mapToDouble((t) -> t.getQty()).sum();
        txtDiscount.setValue(ttlAmt);
    }

    public void initMain() {
        initTable();
    }

    private void initTable() {
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
        tblDiscount.getColumnModel().getColumn(0).setPreferredWidth(100);//des
        tblDiscount.getColumnModel().getColumn(1).setPreferredWidth(30);//sale - qty
        tblDiscount.getColumnModel().getColumn(2).setPreferredWidth(30);//qty
        tblDiscount.getColumnModel().getColumn(3).setPreferredWidth(10);//unit
        tblDiscount.getColumnModel().getColumn(0).setCellEditor(new AutoClearEditor());
        tblDiscount.getColumnModel().getColumn(1).setCellEditor(new AutoClearEditor());
        tblDiscount.getColumnModel().getColumn(2).setCellEditor(new AutoClearEditor());
        inventoryRepo.getStockUnit().doOnSuccess((t) -> {
            tblDiscount.getColumnModel().getColumn(3).setCellEditor(new StockUnitEditor(t));
        }).subscribe();
    }

    public void search(SaleHis sh) {
        this.sh = sh;
        String vouNo = sh.getKey() == null ? null : sh.getKey().getVouNo();
        progress.setIndeterminate(true);
        if (!Util1.isNullOrEmpty(vouNo)) {
            inventoryRepo.getSaleNote(vouNo).doOnSuccess((t) -> {
                tableModel.setListDetail(t);
            }).doOnTerminate(() -> {
                progress.setIndeterminate(false);
                tableModel.addNewRow();
                focusTable(0, 0);
            }).subscribe();
            setVisible(true);
        } else {
            getDetailList();
            setVisible(true);
        }
    }

    private void getDetailList() {
        tableModel.clear();
        List<SaleHisDetail> listFilter = sh.getListSH().stream().filter((t) -> !t.isCalculate()).toList();
        Flux.fromIterable(listFilter).doOnNext((t) -> {
            SaleNote note = new SaleNote();
            note.setDescription(t.getStockName());
            note.setSaleQty(t.getQty());
            tableModel.addObject(note);
        }).doOnTerminate(() -> {
            tableModel.addNewRow();
            focusTable(0, 2);
            progress.setIndeterminate(false);
        }).subscribe();
    }

    private void selectManual() {
        if (chkManual.isSelected()) {
            tableModel.clear();
            tableModel.addNewRow();
            focusTable(0, 0);
        } else {
            getDetailList();
        }
    }

    private void focusTable(int row, int column) {
        try {
            tblDiscount.setRowSelectionInterval(row, row);
            tblDiscount.setColumnSelectionInterval(column, column);
            tblDiscount.requestFocus();
        } catch (Exception e) {
            log.error("foucsTable : " + e.getMessage());
        }
    }

    @Override
    public void selected(Object source, Object selectObj) {
        if (source.equals("CAL_TOTAL")) {
            calTotalAmount();
        }

    }

    private void print() {
        btnPrint.setEnabled(false);
        String printerName = ProUtil.getProperty(ProUtil.PRINTER_POS_NAME);
        String reportName = ProUtil.getProperty(ProUtil.SALE_VOU_NOTE);
        String reportPath = ProUtil.getReportPath() + reportName + ".jasper";
        try {
            if (reportPath != null) {
                List<SaleNote> list = tableModel.getListDetail().stream().filter((t) -> !Util1.isNullOrEmpty(t.getDescription())).toList();
                Trader trader = sh.getTrader();
                Map<String, Object> params = new HashMap<>();
                params.put("p_comp_name", Global.companyName);
                params.put("p_trader_name", trader.getTraderName());
                params.put("p_trader_address", trader.getAddress());
                params.put("p_vou_date", sh.getVouDateStr());
                ByteArrayInputStream stream = new ByteArrayInputStream(Util1.listToByteArray(list));
                JsonDataSource ds = new JsonDataSource(stream);
                JasperPrint jp = JasperFillManager.fillReport(reportPath, params, ds);
                JasperReportUtil.print(jp, printerName, 1, 4);
            }
        } catch (JRException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            btnPrint.setEnabled(true);
        }
        btnPrint.setEnabled(true);
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
        progress = new javax.swing.JProgressBar();
        jPanel1 = new javax.swing.JPanel();
        chkManual = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        txtDiscount = new javax.swing.JFormattedTextField();
        btnPrint = new javax.swing.JButton();

        setTitle("Sale Note Dialog");

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

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        chkManual.setFont(Global.lableFont);
        chkManual.setText("Manual");
        chkManual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkManualActionPerformed(evt);
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Total Qty :");

        txtDiscount.setEditable(false);
        txtDiscount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDiscount.setFont(Global.amtFont);

        btnPrint.setBackground(Global.selectionColor);
        btnPrint.setFont(Global.lableFont);
        btnPrint.setForeground(Color.white);
        btnPrint.setText("Print");
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkManual)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnPrint)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(chkManual)
                    .addComponent(btnPrint))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 534, Short.MAX_VALUE)
                    .addComponent(progress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(progress, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void chkManualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkManualActionPerformed
        // TODO add your handling code here:
        selectManual();
    }//GEN-LAST:event_chkManualActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        // TODO add your handling code here
        print();
    }//GEN-LAST:event_btnPrintActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnPrint;
    private javax.swing.JRadioButton chkManual;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JProgressBar progress;
    private javax.swing.JTable tblDiscount;
    private javax.swing.JFormattedTextField txtDiscount;
    // End of variables declaration//GEN-END:variables
}
