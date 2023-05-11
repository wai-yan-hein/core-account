/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.user.dialog;

import com.common.Global;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.user.common.VRoleCompanyTableModel;
import com.user.model.VRoleCompany;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

/**
 *
 * @author DELL
 */
public class CompanyOptionDialog extends javax.swing.JDialog {

    private final VRoleCompanyTableModel companyTableModel = new VRoleCompanyTableModel();
    private SelectionObserver observer;
    private VRoleCompany companyInfo;
    private List<VRoleCompany> listCompany;

    public List<VRoleCompany> getListCompany() {
        return listCompany;
    }

    public void setListCompany(List<VRoleCompany> listCompany) {
        this.listCompany = listCompany;
    }

    public JButton getjButton1() {
        return jButton1;
    }

    public void setjButton1(JButton jButton1) {
        this.jButton1 = jButton1;
    }

    public VRoleCompany getCompanyInfo() {
        return companyInfo;
    }

    public void setCompanyInfo(VRoleCompany companyInfo) {
        this.companyInfo = companyInfo;
    }

    public SelectionObserver getObserver() {
        return observer;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    /**
     * Creates new form RoleSetupDialog
     *
     * @param parent
     */
    public CompanyOptionDialog(JFrame parent) {
        super(parent, true);
        initComponents();
        actionMapping();

    }

    private void actionMapping() {
        String solve = "enter";
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        tblCompany.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, solve);
        tblCompany.getActionMap().put(solve, new EnterAction());

    }

    private class EnterAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            select();
        }
    }

    public void initMain() {
        tblCompany.setModel(companyTableModel);
        tblCompany.getTableHeader().setFont(Global.tblHeaderFont);
        tblCompany.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCompany.setRowHeight(Global.tblRowHeight);
        tblCompany.setDefaultRenderer(Object.class, new TableCellRender());
        tblCompany.getColumnModel().getColumn(0).setPreferredWidth(1);
        tblCompany.getColumnModel().getColumn(1).setPreferredWidth(20);
        tblCompany.getColumnModel().getColumn(2).setPreferredWidth(50);
        companyTableModel.setListCompany(listCompany);
    }

    private void select() {
        int row = tblCompany.convertRowIndexToModel(tblCompany.getSelectedRow());
        if (row >= 0) {
            companyInfo = companyTableModel.getCompany(row);
            this.dispose();
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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCompany = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Choose Company Dialog");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setFont(Global.menuFont);
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Company List");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tblCompany.setModel(new javax.swing.table.DefaultTableModel(
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
        tblCompany.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCompanyMouseClicked(evt);
            }
        });
        tblCompany.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblCompanyKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblCompany);

        jButton1.setBackground(Global.selectionColor);
        jButton1.setFont(Global.lableFont);
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Select");
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
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 538, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 361, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        select();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void tblCompanyKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblCompanyKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_tblCompanyKeyReleased

    private void tblCompanyMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCompanyMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            select();
        }
    }//GEN-LAST:event_tblCompanyMouseClicked

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblCompany;
    // End of variables declaration//GEN-END:variables
}