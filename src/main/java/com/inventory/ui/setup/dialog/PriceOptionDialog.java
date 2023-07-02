/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup.dialog;

import com.common.Global;
import com.common.TableCellRender;
import com.inventory.model.PriceOption;
import com.repo.InventoryRepo;
import com.inventory.ui.common.SalePriceTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class PriceOptionDialog extends javax.swing.JDialog {

    private final SalePriceTableModel tableModel = new SalePriceTableModel();
    private PriceOption option;
    private InventoryRepo inventoryRepo;
    private List<PriceOption> listPrice = new ArrayList<>();
    private boolean needToChoice;

    public boolean isNeedToChoice() {
        return needToChoice;
    }

    public void setNeedToChoice(boolean needToChoice) {
        this.needToChoice = needToChoice;
    }

    public List<PriceOption> getListPrice() {
        return listPrice;
    }

    public void setListPrice(List<PriceOption> listPrice) {
        this.listPrice = listPrice;
    }

    public InventoryRepo getInventoryRepo() {
        return inventoryRepo;
    }

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public PriceOption getOption() {
        return option;
    }

    public void setOption(PriceOption option) {
        this.option = option;
    }

    /**
     * Creates new form PriceOptionDialog
     */
    public PriceOptionDialog() {
        super(Global.parentForm, true);
        initComponents();
        initTable();
    }

    private void initTable() {
        tblPrice.setModel(tableModel);
        tblPrice.getTableHeader().setFont(Global.tblHeaderFont);
        tblPrice.setRowHeight(Global.tblRowHeight);
        tblPrice.setFont(Global.textFont);
        tblPrice.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblPrice.setShowGrid(true);
        tblPrice.setShowHorizontalLines(true);
        tblPrice.setShowVerticalLines(true);
        tblPrice.setRequestFocusEnabled(false);
        tblPrice.getColumnModel().getColumn(0).setPreferredWidth(10);
        tblPrice.getColumnModel().getColumn(1).setPreferredWidth(30);
        tblPrice.getColumnModel().getColumn(1).setPreferredWidth(30);
        tblPrice.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Enter-Action");
        tblPrice.getActionMap().put("Enter-Action", actionEnter);
        tblPrice.setDefaultRenderer(Object.class, new TableCellRender());
        tblPrice.setDefaultRenderer(Float.class, new TableCellRender());
    }

    public void initData() {
        tableModel.setListPrice(listPrice);
        if (needToChoice) {
            if (!listPrice.isEmpty()) {
                tblPrice.setRowSelectionInterval(0, 0);
            }
        }
    }
    private final Action actionEnter = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            select();
        }
    };

    private void select() {
        int row = tblPrice.getSelectedRow();
        if (row >= 0) {
            option = tableModel.getPriceOption(row);
            dispose();
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
        tblPrice = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);

        tblPrice.setModel(new javax.swing.table.DefaultTableModel(
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
        tblPrice.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPriceMouseClicked(evt);
            }
        });
        tblPrice.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblPriceKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblPrice);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblPriceKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblPriceKeyReleased
        // TODO add your handling code here:

        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            this.dispose();
        }
    }//GEN-LAST:event_tblPriceKeyReleased

    private void tblPriceMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPriceMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 1) {
            select();
        }
    }//GEN-LAST:event_tblPriceMouseClicked

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblPrice;
    // End of variables declaration//GEN-END:variables
}
