/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.inventory.ui.entry.dialog;

import com.common.FilterObject;
import com.common.Global;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.common.Util1;
import com.inventory.model.Job;
import com.repo.InventoryRepo;
import com.inventory.ui.entry.dialog.common.JobSearchTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class JobSearchDialog extends javax.swing.JDialog {

    private InventoryRepo inventoryRepo;
    private SelectionObserver observer;
    private TableRowSorter<TableModel> sorter;
    private JobSearchTableModel tableModel = new JobSearchTableModel();

    public InventoryRepo getInventoryRepo() {
        return inventoryRepo;
    }

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public SelectionObserver getObserver() {
        return observer;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    /**
     * Creates new form BatchSearchDialog
     *
     * @param parent
     */
    public JobSearchDialog(java.awt.Frame parent) {
        super(parent, true);
        initComponents();
    }

    public void initMain() {
        initTable();
        initDate();
    }

    private void initDate() {
        txtFromDate.setFont(Global.textFont);
        txtToDate.setFont(Global.textFont);
        txtFromDate.setDateFormatString(Global.dateFormat);
        txtToDate.setDateFormatString(Global.dateFormat);
        txtFromDate.setDate(Util1.getTodayDate());
        txtToDate.setDate(Util1.getTodayDate());
    }

    public void search() {
        progress.setIndeterminate(true);
        FilterObject filter = new FilterObject(Global.compCode, Global.deptId);
        String fromDate = Util1.toDateStr(txtFromDate.getDate(), "yyyy-MM-dd");
        String toDate = Util1.toDateStr(txtToDate.getDate(), "yyyy-MM-dd");
        filter.setFromDate(fromDate);
        filter.setToDate(toDate);
        filter.setDeleted(false);
        filter.setClose(false);
        filter.setOrderByBatch(true);
        inventoryRepo.getJob(false).subscribe((t) -> {
            tableModel.setListDetail(t);
            lblRecord.setText(String.valueOf(t.size()));
            txtFilter.requestFocus();
            progress.setIndeterminate(false);
        }, (e) -> {
            progress.setIndeterminate(false);
            JOptionPane.showMessageDialog(this, e.getMessage());
        }, () -> {
            setVisible(true);
        });
    }

    private void initTable() {
        tblBatch.setModel(tableModel);
        tblBatch.getTableHeader().setFont(Global.tblHeaderFont);
        tblBatch.getColumnModel().getColumn(0).setPreferredWidth(20);
        tblBatch.getColumnModel().getColumn(1).setPreferredWidth(100);
        tblBatch.getColumnModel().getColumn(2).setPreferredWidth(150);
        tblBatch.getColumnModel().getColumn(3).setPreferredWidth(20);
        tblBatch.setDefaultRenderer(Object.class, new TableCellRender());
        tblBatch.setDefaultRenderer(Float.class, new TableCellRender());
        tblBatch.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblBatch.setRowHeight(Global.tblRowHeight);
        tblBatch.setFont(Global.textFont);
        sorter = new TableRowSorter<>(tblBatch.getModel());
        tblBatch.setRowSorter(sorter);
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        tblBatch.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, "enter");
        tblBatch.getActionMap().put("enter", new EnterAction());
    }

    private class EnterAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            select();
        }
    }

    private void select() {
        int row = tblBatch.convertRowIndexToModel(tblBatch.getSelectedRow());
        if (row >= 0) {
            Job g = tableModel.getSelectVou(row);
            observer.selected("Job", g);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Select Batch.");
        }
    }
    private final RowFilter<Object, Object> startsWithFilter = new RowFilter<Object, Object>() {
        @Override
        public boolean include(RowFilter.Entry<? extends Object, ? extends Object> entry) {
            String tmp1 = entry.getStringValue(0).toUpperCase().replace(" ", "");
            String tmp2 = entry.getStringValue(1).toUpperCase().replace(" ", "");
            String tmp3 = entry.getStringValue(3).toUpperCase().replace(" ", "");
            String tmp4 = entry.getStringValue(4).toUpperCase().replace(" ", "");
            String tmp5 = entry.getStringValue(4).toUpperCase().replace(" ", "");
            String text = txtFilter.getText().toUpperCase().replace(" ", "");
            return tmp1.startsWith(text) || tmp2.startsWith(text)
                    || tmp3.startsWith(text) || tmp4.startsWith(text) || tmp5.startsWith(text);
        }
    };

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblBatch = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtFilter = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        lblRecord = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtFromDate = new com.toedter.calendar.JDateChooser();
        txtToDate = new com.toedter.calendar.JDateChooser();
        jButton3 = new javax.swing.JButton();
        progress = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Search Batch ");

        tblBatch.setModel(new javax.swing.table.DefaultTableModel(
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
        tblBatch.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblBatchMouseClicked(evt);
            }
        });
        tblBatch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblBatchKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblBatch);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Search");

        txtFilter.setFont(Global.textFont);
        txtFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFilterActionPerformed(evt);
            }
        });
        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtFilter)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtFilter)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jButton2.setFont(Global.textFont);
        jButton2.setText("Cancel");

        jButton1.setFont(Global.lableFont);
        jButton1.setText("Select");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Records : ");

        lblRecord.setFont(Global.lableFont);
        lblRecord.setText("0");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblRecord, javax.swing.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
                .addGap(188, 188, 188)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton1)
                        .addComponent(jButton2))
                    .addComponent(lblRecord)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("From Date :");

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("To Date :");

        jButton3.setFont(Global.lableFont);
        jButton3.setText("Search");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton3)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton3)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(txtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(progress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(progress, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 381, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyReleased
        // TODO add your handling code here:
        if (txtFilter.getText().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(startsWithFilter);
            if (tblBatch.getRowCount() > 0) {
                tblBatch.setRowSelectionInterval(0, 0);
            }
        }
        if (evt.getKeyCode() == KeyEvent.VK_ENTER || evt.getKeyCode() == KeyEvent.VK_DOWN) {
            tblBatch.requestFocus();
        }
    }//GEN-LAST:event_txtFilterKeyReleased

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        select();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void tblBatchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblBatchKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_tblBatchKeyReleased

    private void tblBatchMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBatchMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            select();
        }
    }//GEN-LAST:event_tblBatchMouseClicked

    private void txtFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFilterActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFilterActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        search();
    }//GEN-LAST:event_jButton3ActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblRecord;
    private javax.swing.JProgressBar progress;
    private javax.swing.JTable tblBatch;
    private javax.swing.JTextField txtFilter;
    private com.toedter.calendar.JDateChooser txtFromDate;
    private com.toedter.calendar.JDateChooser txtToDate;
    // End of variables declaration//GEN-END:variables
}
