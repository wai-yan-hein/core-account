/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.inventory.ui.entry;

import com.repo.AccountRepo;
import com.common.Global;
import com.common.SelectionObserver;
import com.repo.InventoryRepo;
import com.repo.UserRepo;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class OtherSetupMain extends javax.swing.JPanel {

    private InventoryRepo inventoryRepo;
    private AccountRepo accountRepo;
    private UserRepo userRepo;
    private JProgressBar progress;
    private SelectionObserver observer;
    private AccountSettingEntry entry;
    private OtherSetup setup;

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public void setAccountRepo(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }
    

    public JProgressBar getProgress() {
        return progress;
    }

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }

    public SelectionObserver getObserver() {
        return observer;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    /**
     * Creates new form OtherSetupMain
     */
    public OtherSetupMain() {
        initComponents();
    }

    public void initMain() {
        tabMain.add("Setup", getOtherSetup());
        tabMain.add("Account Setting", getSettingPanel());
        tabMain.setSelectedIndex(0);
    }

    private JPanel getOtherSetup() {
        if (setup == null) {
            setup = new OtherSetup();
            setup.setInventoryRepo(inventoryRepo);
            setup.setAccountRepo(accountRepo);
            setup.setUserRepo(userRepo);
        }
        return setup;

    }

    private JPanel getSettingPanel() {
        if (entry == null) {
            entry = new AccountSettingEntry();
            entry.setInventoryRepo(inventoryRepo);
            entry.setAccountRepo(accountRepo);
            entry.initMain();
        }
        entry.searchAccSetting();
        return entry;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabMain = new javax.swing.JTabbedPane();

        tabMain.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        tabMain.setFont(Global.menuFont);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabMain, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabMain, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane tabMain;
    // End of variables declaration//GEN-END:variables
}
