/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.user.setup;

import com.repo.AccountRepo;
import com.common.Global;
import com.common.PanelControl;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.repo.UserRepo;
import com.inventory.model.AppRole;
import com.repo.InventoryRepo;
import com.inventory.ui.common.UserRoleTableModel;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.user.dialog.RoleSetupDialog;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Component
public class RoleSetting extends javax.swing.JPanel implements PanelControl, SelectionObserver {

    private final SystemProperty sysProperty = new SystemProperty();
    @Autowired
    private RoleMenuSetup roleMenuSetup;
    @Autowired
    private RoleCompany roleCompany;
    private final UserRoleTableModel userRoleTableModel = new UserRoleTableModel();
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private InventoryRepo inventoryRepo;
    @Autowired
    private AccountRepo accountRepo;
    private SelectionObserver observer;
    private JProgressBar progress;
    private int selectRow = -1;
    private AppRole role = new AppRole();

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    /**
     * Creates new form UserSetting
     */
    public RoleSetting() {
        initComponents();
    }

    private void initTableUser() {
        userRoleTableModel.setUserRepo(userRepo);
        tblRole.setModel(userRoleTableModel);
        userRoleTableModel.setTable(tblRole);
        tblRole.getTableHeader().setFont(Global.tblHeaderFont);
        tblRole.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblRole.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting()) {
                if (tblRole.getSelectedRow() >= 0) {
                    selectRow = tblRole.convertRowIndexToModel(tblRole.getSelectedRow());
                    role = userRoleTableModel.getRole(selectRow);
                    String roleCode = role.getRoleCode();
                    if (roleCode != null) {
                        sysProperty.setProperyType("Role");
                        sysProperty.setRoleCode(roleCode);
                        sysProperty.setProgress(progress);
                        sysProperty.initMain();
                        roleMenuSetup.createMenuTree(roleCode);
                        roleCompany.searchCompany(roleCode);
                    }
                }
            }
        });
        tblRole.setDefaultRenderer(Object.class, new TableCellRender());
        tblRole.getColumnModel().getColumn(0).setCellEditor(new AutoClearEditor());
        searchRole();
        initTabMain();
    }

    private void initTabMain() {
        sysProperty.setUserRepo(userRepo);
        sysProperty.setInventoryRepo(inventoryRepo);
        sysProperty.setAccountRepo(accountRepo);
        sysProperty.setProgress(progress);
        roleCompany.initTable();
        roleCompany.setProgress(progress);
        roleMenuSetup.setObserver(observer);
        roleMenuSetup.setProgress(progress);
        tabMain.add("Property", sysProperty);
        tabMain.add("Role Menu", roleMenuSetup);
        tabMain.add("Company", roleCompany);
    }

    private void searchRole() {
        userRepo.getAppRole().subscribe((t) -> {
            userRoleTableModel.setListRole(t);
        }, (e) -> {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
        });
    }

    public void initMain() {
        initTableUser();
    }

    public String getRoleCode() {
        String roleCode = null;
        if (tblRole.getSelectedRow() >= 0) {
            selectRow = tblRole.convertRowIndexToModel(tblRole.getSelectedRow());
            role = userRoleTableModel.getRole(selectRow);
            roleCode = role.getRoleCode();
        }
        return roleCode;
    }

    @Override
    public void save() {
    }

    @Override
    public void delete() {
    }

    @Override
    public void newForm() {
        role = new AppRole();
    }

    @Override
    public void history() {
    }

    @Override
    public void print() {
    }

    @Override
    public void refresh() {
        searchRole();
    }

    @Override
    public void filter() {
    }

    @Override
    public String panelName() {
        return this.getName();
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
        jScrollPane1 = new javax.swing.JScrollPane();
        tblRole = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        tabMain = new javax.swing.JTabbedPane();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        tblRole.setFont(Global.textFont);
        tblRole.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblRole.setRowHeight(Global.tblRowHeight);
        tblRole.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblRoleKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblRole);

        jButton1.setBackground(Global.selectionColor);
        jButton1.setFont(Global.lableFont);
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("New Role ");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        tabMain.setFont(Global.menuFont);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabMain, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabMain)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observer.selected("control", this);
    }//GEN-LAST:event_formComponentShown

    private void tblRoleKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblRoleKeyReleased
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            if (tblRole.getSelectedRow() >= 0) {
                int yes_no = JOptionPane.showConfirmDialog(Global.parentForm, "Are you sure to delete?");
                if (yes_no == JOptionPane.YES_OPTION) {
                    int row = tblRole.convertRowIndexToModel(tblRole.getSelectedRow());
                    userRoleTableModel.deleteRole(row);
                }
            }
        }
    }//GEN-LAST:event_tblRoleKeyReleased

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        RoleSetupDialog d = new RoleSetupDialog(Global.parentForm);
        d.setObserver(this);
        d.setUserRepo(userRepo);
        d.initTable();
        d.setLocationRelativeTo(null);
        d.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane tabMain;
    private javax.swing.JTable tblRole;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {
        String str = source.toString();
        if (str.equals("Refresh")) {
            searchRole();
        }
    }
}
