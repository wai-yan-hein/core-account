/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.inventory.ui.entry.dialog;

import com.common.Global;
import com.inventory.editor.TraderAutoCompleter;
import com.inventory.model.StockInOut;
import com.inventory.model.Trader;
import com.repo.InventoryRepo;
import javax.swing.JFrame;

/**
 *
 * @author Lenovo
 */
public class StockIOMoreDialog extends javax.swing.JDialog {

    private InventoryRepo inventoryRepo;
    private TraderAutoCompleter traderAutoCompleter;
    private StockInOut io;

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    /**
     * Creates new form StockIOMoreDialog
     *
     * @param frame
     */
    public StockIOMoreDialog(JFrame frame) {
        super(frame, true);
        initComponents();
    }

    public void initMain() {
        initCombo();
    }

    private void initCombo() {
        traderAutoCompleter = new TraderAutoCompleter(txtTrader, inventoryRepo, null, false, "CUS");
    }

    public void setData(StockInOut io) {
        this.io = io;
        txtRecName.setText(io.getReceivedName());
        txtRecPhone.setText(io.getReceivedPhoneNo());
        txtCarNo.setText(io.getCarNo());
        inventoryRepo.findTrader(io.getTraderCode()).doOnSuccess((t) -> {
            traderAutoCompleter.setTrader(t);
        }).subscribe();
    }

    private void select() {
        io.setReceivedName(txtRecName.getText());
        io.setReceivedPhoneNo(txtRecPhone.getText());
        io.setCarNo(txtCarNo.getText());
        Trader t = traderAutoCompleter.getTrader();
        if (t != null) {
            io.setTraderCode(t.getKey().getCode());
        }
        this.dispose();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txtRecName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtRecPhone = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtCarNo = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtTrader = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Stock IO More Dialog");

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Received Name");

        txtRecName.setFont(Global.textFont);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Received Phone");

        txtRecPhone.setFont(Global.textFont);

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Car No");

        txtCarNo.setFont(Global.textFont);

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Customer");

        txtTrader.setFont(Global.textFont);

        jButton1.setBackground(Global.selectionColor);
        jButton1.setFont(Global.lableFont);
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtRecName))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCarNo)
                            .addComponent(txtRecPhone, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
                            .addComponent(txtTrader)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtTrader)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtRecName)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtRecPhone)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtCarNo)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 138, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        select();
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField txtCarNo;
    private javax.swing.JTextField txtRecName;
    private javax.swing.JTextField txtRecPhone;
    private javax.swing.JTextField txtTrader;
    // End of variables declaration//GEN-END:variables
}