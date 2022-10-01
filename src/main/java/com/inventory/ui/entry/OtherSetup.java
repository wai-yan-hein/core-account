/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry;

import com.common.Global;
import com.inventory.ui.common.InventoryRepo;
import com.inventory.ui.setup.dialog.CurrencySetupDialog;
import com.inventory.ui.setup.dialog.LocationSetupDialog;
import com.inventory.ui.setup.dialog.ProcessTypeSetupDialog;
import com.inventory.ui.setup.dialog.RegionSetup;
import com.inventory.ui.setup.dialog.RelationSetupDialog;
import com.inventory.ui.setup.dialog.SaleManSetupDialog;
import com.inventory.ui.setup.dialog.VouStatusSetupDialog;
import java.awt.Image;
import javax.swing.ImageIcon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Component
public class OtherSetup extends javax.swing.JPanel {

    private final Image icon = new ImageIcon(getClass().getResource("/images/setting.png")).getImage();
    @Autowired
    private InventoryRepo inventoryRepo;
    private RegionSetup regionSetup;
    private LocationSetupDialog locationSetup;
    private RelationSetupDialog relationSetupDialog;
    private ProcessTypeSetupDialog processTypeDialog;
    @Autowired
    private CurrencySetupDialog currencySetup;
    @Autowired
    private SaleManSetupDialog smDialog;
    private VouStatusSetupDialog vsDialog;

    /**
     * Creates new form OtherSetup
     */
    public OtherSetup() {
        initComponents();
    }

    private void regionSetup() {
        regionSetup = new RegionSetup(Global.parentForm);
        regionSetup.setInventoryRepo(inventoryRepo);
        regionSetup.setListRegion(inventoryRepo.getRegion());
        regionSetup.initMain();
        regionSetup.setSize(Global.width / 2, Global.height / 2);
        regionSetup.setLocationRelativeTo(null);
        regionSetup.setVisible(true);
    }

    private void locationSetup() {
        locationSetup = new LocationSetupDialog();
        locationSetup.setInventoryRepo(inventoryRepo);
        locationSetup.setIconImage(icon);
        locationSetup.initMain();
        locationSetup.setSize(Global.width / 2, Global.height / 2);
        locationSetup.setLocationRelativeTo(null);
        locationSetup.setVisible(true);
    }

    private void currencySetup() {
        currencySetup.setIconImage(icon);
        currencySetup.initMain();
        currencySetup.setSize(Global.width / 2, Global.height / 2);
        currencySetup.setLocationRelativeTo(null);
        currencySetup.setVisible(true);
    }

    private void saleManSetup() {
        smDialog.setIconImage(icon);
        smDialog.setInventoryRepo(inventoryRepo);
        smDialog.setListSaleMan(inventoryRepo.getSaleMan());
        smDialog.initMain();
        smDialog.setSize(Global.width / 2, Global.height / 2);
        smDialog.setLocationRelativeTo(null);
        smDialog.setVisible(true);
    }

    private void vouStatusSetup() {
        vsDialog = new VouStatusSetupDialog();
        vsDialog.setIconImage(icon);
        vsDialog.setInventoryRepo(inventoryRepo);
        vsDialog.setListVou(inventoryRepo.getVoucherStatus());
        vsDialog.initMain();
        vsDialog.setSize(Global.width / 2, Global.height / 2);
        vsDialog.setLocationRelativeTo(null);
        vsDialog.setVisible(true);
    }

    private void relationSetup() {
        relationSetupDialog = new RelationSetupDialog();
        relationSetupDialog.setIconImage(icon);
        relationSetupDialog.setInventoryRepo(inventoryRepo);
        relationSetupDialog.setListUnitRelation(inventoryRepo.getUnitRelation());
        relationSetupDialog.initMain();
        relationSetupDialog.setSize(Global.width / 2, Global.height / 2);
        relationSetupDialog.setLocationRelativeTo(null);
        relationSetupDialog.setVisible(true);
    }

    private void processType() {
        processTypeDialog = new ProcessTypeSetupDialog();
        processTypeDialog.setInventoryRepo(inventoryRepo);
        processTypeDialog.setListType(inventoryRepo.getProcessType());
        processTypeDialog.setIconImage(icon);
        processTypeDialog.initMain();
        processTypeDialog.setSize(Global.width / 2, Global.height / 2);
        processTypeDialog.setLocationRelativeTo(null);
        processTypeDialog.setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnRegion = new javax.swing.JButton();
        btnLocation = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        btnProcess = new javax.swing.JButton();

        btnRegion.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        btnRegion.setText("Region");
        btnRegion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegionActionPerformed(evt);
            }
        });

        btnLocation.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        btnLocation.setText("Location");
        btnLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLocationActionPerformed(evt);
            }
        });

        jButton3.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jButton3.setText("Currency");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jButton4.setText("Sale Man");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jButton5.setText("Voucher Status");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jButton6.setText("Relation");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        btnProcess.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        btnProcess.setText("Process Type");
        btnProcess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProcessActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnProcess, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnRegion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnLocation)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton6)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnLocation, btnRegion, jButton3, jButton4, jButton5, jButton6});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRegion, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnProcess, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(142, Short.MAX_VALUE))
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

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        currencySetup();
    }//GEN-LAST:event_jButton3ActionPerformed

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

    private void btnProcessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProcessActionPerformed
        // TODO add your handling code here:
        processType();
    }//GEN-LAST:event_btnProcessActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLocation;
    private javax.swing.JButton btnProcess;
    private javax.swing.JButton btnRegion;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    // End of variables declaration//GEN-END:variables
}
