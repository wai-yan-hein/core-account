/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry;

import com.repo.AccountRepo;
import com.common.Global;
import com.repo.InventoryRepo;
import com.inventory.ui.setup.dialog.LocationSetupDialog;
import com.inventory.ui.setup.dialog.OrderStatusSetupDialog;
import com.inventory.ui.setup.dialog.ProcessTypeSetupDialog;
import com.inventory.ui.setup.dialog.RegionSetup;
import com.inventory.ui.setup.dialog.RelationSetupDialog;
import com.inventory.ui.setup.dialog.SaleManSetupDialog;
import com.inventory.ui.setup.dialog.VouStatusSetupDialog;
import java.awt.Image;
import javax.swing.ImageIcon;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class OtherSetup extends javax.swing.JPanel {

    private final Image icon = new ImageIcon(getClass().getResource("/images/setting.png")).getImage();
    private InventoryRepo inventoryRepo;
    private AccountRepo accountRepo;
    private RegionSetup regionSetup;
    private LocationSetupDialog locationSetup;
    private RelationSetupDialog relationSetupDialog;
    private ProcessTypeSetupDialog processTypeDialog;
    private SaleManSetupDialog smDialog;
    private VouStatusSetupDialog vsDialog;
    private OrderStatusSetupDialog osDialog;

    public void setAccountRepo(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    /**
     * Creates new form OtherSetup
     */
    public OtherSetup() {
        initComponents();
    }

    private void regionSetup() {
        inventoryRepo.getRegion().subscribe((t) -> {
            regionSetup = new RegionSetup(Global.parentForm);
            regionSetup.setInventoryRepo(inventoryRepo);
            regionSetup.setListRegion(t);
            regionSetup.initMain();
            regionSetup.setSize(Global.width / 2, Global.height / 2);
            regionSetup.setLocationRelativeTo(null);
            regionSetup.setVisible(true);
        });
    }

    private void locationSetup() {
        locationSetup = new LocationSetupDialog(Global.parentForm);
        locationSetup.setInventoryRepo(inventoryRepo);
        locationSetup.setAccountRepo(accountRepo);
        locationSetup.initMain();
        locationSetup.setSize(Global.width / 2, Global.height / 2);
        locationSetup.setLocationRelativeTo(null);
        locationSetup.setVisible(true);
    }

    private void saleManSetup() {
        inventoryRepo.getSaleMan().subscribe((t) -> {
            smDialog = new SaleManSetupDialog();
            smDialog.setIconImage(icon);
            smDialog.setInventoryRepo(inventoryRepo);
            smDialog.setListSaleMan(t);
            smDialog.initMain();
            smDialog.setSize(Global.width / 2, Global.height / 2);
            smDialog.setLocationRelativeTo(null);
            smDialog.setVisible(true);
        });
    }

    private void vouStatusSetup() {
        inventoryRepo.getVoucherStatus().subscribe((t) -> {
            vsDialog = new VouStatusSetupDialog();
            vsDialog.setIconImage(icon);
            vsDialog.setInventoryRepo(inventoryRepo);
            vsDialog.setListVou(t);
            vsDialog.initMain();
            vsDialog.setSize(Global.width / 2, Global.height / 2);
            vsDialog.setLocationRelativeTo(null);
            vsDialog.setVisible(true);
        });

    }

    private void orderStatusSetup() {
        inventoryRepo.getOrderStatus().subscribe((t) -> {
            osDialog = new OrderStatusSetupDialog();
            osDialog.setIconImage(icon);
            osDialog.setInventoryRepo(inventoryRepo);
            osDialog.setListVou(t);
            osDialog.initMain();
            osDialog.setSize(Global.width / 2, Global.height / 2);
            osDialog.setLocationRelativeTo(null);
            osDialog.setVisible(true);
        });

    }

    private void relationSetup() {
        inventoryRepo.getUnitRelation().subscribe((t) -> {
            relationSetupDialog = new RelationSetupDialog();
            relationSetupDialog.setIconImage(icon);
            relationSetupDialog.setInventoryRepo(inventoryRepo);
            relationSetupDialog.setListUnitRelation(t);
            relationSetupDialog.initMain();
            relationSetupDialog.setSize(Global.width / 2, Global.height / 2);
            relationSetupDialog.setLocationRelativeTo(null);
            relationSetupDialog.setVisible(true);
        });

    }

    private void processType() {
        inventoryRepo.getProcessType().subscribe((t) -> {
            processTypeDialog = new ProcessTypeSetupDialog();
            processTypeDialog.setInventoryRepo(inventoryRepo);
            processTypeDialog.setListType(t);
            processTypeDialog.setIconImage(icon);
            processTypeDialog.initMain();
            processTypeDialog.setSize(Global.width / 2, Global.height / 2);
            processTypeDialog.setLocationRelativeTo(null);
            processTypeDialog.setVisible(true);
        });

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
        jButton5 = new javax.swing.JButton();
        btnLocation = new javax.swing.JButton();
        btnRegion = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();

        jButton5.setFont(Global.lableFont);
        jButton5.setText("Voucher Status");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        btnLocation.setFont(Global.lableFont);
        btnLocation.setText("Location");
        btnLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLocationActionPerformed(evt);
            }
        });

        btnRegion.setFont(Global.lableFont);
        btnRegion.setText("Region");
        btnRegion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegionActionPerformed(evt);
            }
        });

        jButton6.setFont(Global.lableFont);
        jButton6.setText("Relation");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton4.setFont(Global.lableFont);
        jButton4.setText("Sale Man");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton7.setFont(Global.lableFont);
        jButton7.setText("Order Status");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRegion, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnLocation)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton7)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnLocation, btnRegion, jButton4, jButton5, jButton6});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnRegion, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(85, 85, 85))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnRegionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegionActionPerformed
        // TODO add your handling code here:
        regionSetup();
    }//GEN-LAST:event_btnRegionActionPerformed

    private void btnLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLocationActionPerformed
        // TODO add your handling code here:
        locationSetup();
    }//GEN-LAST:event_btnLocationActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        saleManSetup();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        vouStatusSetup();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        relationSetup();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        orderStatusSetup();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton7ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLocation;
    private javax.swing.JButton btnRegion;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
