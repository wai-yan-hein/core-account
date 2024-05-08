/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry;

import com.repo.AccountRepo;
import com.common.Global;
import com.inventory.ui.setup.dialog.LabourGroupSetupDialog;
import com.repo.InventoryRepo;
import com.inventory.ui.setup.dialog.LocationSetupDialog;
import com.inventory.ui.setup.dialog.OrderStatusSetupDialog;
import com.inventory.ui.setup.dialog.RegionSetup;
import com.inventory.ui.setup.dialog.RelationSetupDialog;
import com.inventory.ui.setup.dialog.SaleManSetupDialog;
import com.inventory.ui.setup.dialog.StockColorSetupDialog;
import com.inventory.ui.setup.dialog.VouStatusSetupDialog;
import com.inventory.ui.setup.dialog.WareHouseSetupDialog;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class OtherSetup extends javax.swing.JPanel {

    @Setter
    private InventoryRepo inventoryRepo;
    @Setter
    private AccountRepo accountRepo;
    private RegionSetup regionSetup;
    private LocationSetupDialog locationSetup;
    private RelationSetupDialog relationSetupDialog;
    private SaleManSetupDialog smDialog;
    private VouStatusSetupDialog vsDialog;
    private OrderStatusSetupDialog osDialog;
    private LabourGroupSetupDialog lgDialog;
    private WareHouseSetupDialog wareHouseSetupDialog;
    private StockColorSetupDialog stockColorSetupDialog;

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
        regionSetup.search();

    }

    private void locationSetup() {
        if (locationSetup == null) {
            locationSetup = new LocationSetupDialog(Global.parentForm);
            locationSetup.setInventoryRepo(inventoryRepo);
            locationSetup.setAccountRepo(accountRepo);
            locationSetup.initMain();
            locationSetup.setSize(Global.width - 200, Global.height - 200);
            locationSetup.setLocationRelativeTo(null);
        }
        locationSetup.search();
    }

    private void saleManSetup() {
        if (smDialog == null) {
            smDialog = new SaleManSetupDialog(Global.parentForm);
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
            vsDialog = new VouStatusSetupDialog(Global.parentForm);
            vsDialog.setInventoryRepo(inventoryRepo);
            vsDialog.initMain();
            vsDialog.setSize(Global.width / 2, Global.height / 2);
            vsDialog.setLocationRelativeTo(null);
        }
        vsDialog.search();

    }

    private void orderStatusSetup() {
        if (osDialog == null) {
            osDialog = new OrderStatusSetupDialog(Global.parentForm);
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

    private void labourGroupSetup() {
        if (lgDialog == null) {
            lgDialog = new LabourGroupSetupDialog(Global.parentForm);
            lgDialog.setInventoryRepo(inventoryRepo);
            lgDialog.initMain();
            lgDialog.setSize(Global.width - 200, Global.height - 200);
            lgDialog.setLocationRelativeTo(null);
        }
        lgDialog.search();
    }

    private void relationSetup() {
        if (relationSetupDialog == null) {
            relationSetupDialog = new RelationSetupDialog(Global.parentForm);
            relationSetupDialog.setInventoryRepo(inventoryRepo);
            relationSetupDialog.initMain();
            relationSetupDialog.setSize(Global.width - 200, Global.height - 200);
            relationSetupDialog.setLocationRelativeTo(null);
        }
        relationSetupDialog.search();
    }

    private void wareHouseDialog() {
        if (wareHouseSetupDialog == null) {
            wareHouseSetupDialog = new WareHouseSetupDialog(Global.parentForm);
            wareHouseSetupDialog.setInventoryRepo(inventoryRepo);
            wareHouseSetupDialog.initMain();
            wareHouseSetupDialog.setSize(Global.width - 200, Global.height - 200);
            wareHouseSetupDialog.setLocationRelativeTo(null);
        }
        wareHouseSetupDialog.search();

    }

    private void stockColorDialog() {
        if (stockColorSetupDialog == null) {
            stockColorSetupDialog = new StockColorSetupDialog(Global.parentForm);
            stockColorSetupDialog.setInventoryRepo(inventoryRepo);
            stockColorSetupDialog.initMain();
            stockColorSetupDialog.setSize(Global.width / 2, Global.height / 2);
            stockColorSetupDialog.setLocationRelativeTo(null);
        }
        inventoryRepo.getStockColor().doOnSuccess((t) -> {
            if (t != null) {
                stockColorSetupDialog.setListVou(t);
            }
        }).doOnTerminate(() -> {
            stockColorSetupDialog.setVisible(true);
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
        btnRegion = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        btnLocation = new javax.swing.JButton();
        tbnWH = new javax.swing.JButton();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jButton5.setFont(Global.lableFont);
        jButton5.setText("Voucher Status");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
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
        jButton7.setText("Labour Group");
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

        jButton10.setFont(Global.lableFont);
        jButton10.setText("Order Status");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton9.setFont(Global.lableFont);
        jButton9.setText("Stock Color");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                    .addComponent(btnRegion, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                    .addComponent(jButton9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jButton4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnRegion, jButton10, jButton4, jButton5, jButton6, jButton7, jButton8});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnRegion, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        btnLocation.setFont(Global.lableFont);
        btnLocation.setText("Location");
        btnLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLocationActionPerformed(evt);
            }
        });

        tbnWH.setFont(Global.lableFont);
        tbnWH.setText("Ware House");
        tbnWH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbnWHActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tbnWH)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(107, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbnWH, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(158, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(263, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(157, Short.MAX_VALUE))
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
        labourGroupSetup();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        orderStatusSetup();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton10ActionPerformed

    private void tbnWHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbnWHActionPerformed
        // TODO add your handling code here:
        wareHouseDialog();
    }//GEN-LAST:event_tbnWHActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        // TODO add your handling code here:
        stockColorDialog();
    }//GEN-LAST:event_jButton9ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLocation;
    private javax.swing.JButton btnRegion;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton tbnWH;
    // End of variables declaration//GEN-END:variables
}
