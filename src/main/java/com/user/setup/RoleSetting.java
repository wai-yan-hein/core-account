/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.user.setup;

import com.common.Global;
import com.common.PanelControl;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.user.common.UserRepo;
import com.inventory.model.AppRole;
import com.inventory.ui.common.UserRoleTableModel;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 *
 * @author Lenovo
 */
@Component
public class RoleSetting extends javax.swing.JPanel implements PanelControl {

    @Autowired
    private RolePropertySetup roleProperty;
    @Autowired
    private RoleMenuSetup roleMenuSetup;
    @Autowired
    private RoleCompany roleCompany;
    private final UserRoleTableModel userRoleTableModel = new UserRoleTableModel();
    @Autowired
    private WebClient userApi;
    @Autowired
    private UserRepo userRepo;
    private SelectionObserver observer;
    private JProgressBar progress;
    private int selectRow = -1;

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
                    AppRole role = userRoleTableModel.getRole(selectRow);
                    String roleCode = role.getRoleCode();
                    if (roleCode != null) {
                        roleProperty.searchRoleProperty(roleCode);
                        roleMenuSetup.createMenuTree(roleCode);
                        roleCompany.searchCompany(roleCode);
                    }
                }
            }
        });
        tblRole.setDefaultRenderer(Object.class, new TableCellRender());
        searchRole();
        initTabMain();
    }

    private void initTabMain() {
        roleProperty.initTable();
        roleProperty.setProgress(progress);
        roleCompany.initTable();
        roleCompany.setProgress(progress);
        roleMenuSetup.setObserver(observer);
        roleMenuSetup.setProgress(progress);
        tabMain.add("Property", roleProperty);
        tabMain.add("Role Menu", roleMenuSetup);
        tabMain.add("Company", roleCompany);
    }

    private void searchRole() {
        Mono<ResponseEntity<List<AppRole>>> result = userApi.get()
                .uri(builder -> builder.path("/user/get-role")
                .build())
                .retrieve().toEntityList(AppRole.class);
        result.subscribe((t) -> {
            userRoleTableModel.setListRole(t.getBody());
            userRoleTableModel.addEmptyRow();
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
            AppRole role = userRoleTableModel.getRole(selectRow);
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
    }

    @Override
    public void history() {
    }

    @Override
    public void print() {
    }

    @Override
    public void refresh() {
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
        jPanel2 = new javax.swing.JPanel();
        tabMain = new javax.swing.JTabbedPane();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

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
        tblRole.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblRoleKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblRole);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabMain, javax.swing.GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabMain, javax.swing.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane tabMain;
    private javax.swing.JTable tblRole;
    // End of variables declaration//GEN-END:variables
}
