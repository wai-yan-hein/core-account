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
        if (regionSetup == null) {
            regionSetup = new RegionSetup(Global.parentForm);
            regionSetup.setInventoryRepo(inventoryRepo);
            regionSetup.initMain();
            regionSetup.setSize(Global.width / 2, Global.height / 2);
            regionSetup.setLocationRelativeTo(null);
        }
        inventoryRepo.getRegion().doOnSuccess((t) -> {
            regionSetup.setListRegion(t);
        }).doOnTerminate(() -> {
            regionSetup.setVisible(true);
        }).subscribe();

    }

    private void locationSetup() {
        if (locationSetup == null) {
            locationSetup = new LocationSetupDialog(Global.parentForm);
            locationSetup.setInventoryRepo(inventoryRepo);
            locationSetup.setAccountRepo(accountRepo);
            locationSetup.initMain();
            locationSetup.setSize(Global.width / 2, Global.height / 2);
            locationSetup.setLocationRelativeTo(null);
        }
        locationSetup.setVisible(true);
    }

    private void saleManSetup() {
        if (smDialog == null) {
            smDialog = new SaleManSetupDialog();
            smDialog.setIconImage(icon);
            smDialog.setInventoryRepo(inventoryRepo);
            smDialog.initMain();
            smDialog.setSize(Global.width / 2, Global.height / 2);
            smDialog.setLocationRelativeTo(null);
        }
        inventoryRepo.getSaleMan().doOnSuccess((t) -> {
            smDialog.setListSaleMan(t);
        }).doOnTerminate(() -> {
            smDialog.setVisible(true);
        }).subscribe();
    }

    private void vouStatusSetup() {
        if (vsDialog == null) {
            vsDialog = new VouStatusSetupDialog();
            vsDialog.setIconImage(icon);
            vsDialog.setInventoryRepo(inventoryRepo);
            vsDialog.initMain();
            vsDialog.setSize(Global.width / 2, Global.height / 2);
            vsDialog.setLocationRelativeTo(null);
        }
        inventoryRepo.getVoucherStatus().doOnSuccess((t) -> {
            vsDialog.setListVou(t);
        }).doOnTerminate(() -> {
            vsDialog.setVisible(true);
        }).subscribe();

    }

    private void orderStatusSetup() {
        if (osDialog == null) {
            osDialog = new OrderStatusSetupDialog();
            osDialog.setIconImage(icon);
            osDialog.setInventoryRepo(inventoryRepo);
            osDialog.initMain();
            osDialog.setSize(Global.width / 2, Global.height / 2);
            osDialog.setLocationRelativeTo(null);
        }
        inventoryRepo.getOrderStatus().doOnSuccess((t) -> {
            osDialog.setListVou(t);
        }).doOnTerminate(() -> {
            osDialog.setVisible(true);
        }).subscribe();

    }

    private void relationSetup() {
        if (relationSetupDialog == null) {
            relationSetupDialog = new RelationSetupDialog(Global.parentForm);
            relationSetupDialog.setInventoryRepo(inventoryRepo);
            relationSetupDialog.initMain();
            relationSetupDialog.setSize(Global.width / 2, Global.height / 2);
            relationSetupDialog.setLocationRelativeTo(null);
        }
        inventoryRepo.getUnitRelation().doOnSuccess((t) -> {
            relationSetupDialog.setListUnitRelation(t);
        }).doOnTerminate(() -> {
            relationSetupDialog.setVisible(true);
        }).subscribe();
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
        jButton8 = new javax.swing.JButton();

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

        jButton8.setFont(Global.lableFont);
        jButton8.setText("Stock Criteria");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                    .addComponent(btnRegion, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                    .addComponent(jButton8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnLocation)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
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

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton8ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLocation;
    private javax.swing.JButton btnRegion;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
