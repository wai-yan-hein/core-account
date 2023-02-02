/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.common.ui;

import com.common.Global;
import com.common.TableCellRender;
import com.common.Util1;
import com.user.model.VRoleCompany;
import com.inventory.ui.common.CompanyNameTableModel;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lenovo
 */
public class CompanyDialog extends javax.swing.JDialog {

    private static final Logger log = LoggerFactory.getLogger(ApplicationMainFrame.class);
    private List<VRoleCompany> listCompany;
    private CompanyNameTableModel companyTableModel;

    public List<VRoleCompany> getListCompany() {
        return listCompany;
    }

    public void setListCompany(List<VRoleCompany> listCompany) {
        this.listCompany = listCompany;
    }

    /**
     * Creates new form CompanyDialog
     */
    public CompanyDialog() {
        super(Global.parentForm, true);
        initComponents();
    }

    public void initTable() {
        companyTableModel = new CompanyNameTableModel(listCompany);
        tblCompany.getTableHeader().setFont(Global.lableFont);
        tblCompany.setModel(companyTableModel);
        tblCompany.setDefaultRenderer(Object.class, new TableCellRender());
    }

    private void select() {
        if (tblCompany.getSelectedRow() >= 0) {
            int row = tblCompany.convertRowIndexToModel(tblCompany.getSelectedRow());
            VRoleCompany company = companyTableModel.getCompany(row);
            Global.roleCode = company.getRoleCode();
            Global.compCode = company.getCompCode();
            Global.companyName = company.getCompName();
            Global.currency = company.getCurrency();
            Global.companyPhone = company.getCompPhone();
            Global.companyAddress = company.getCompAddress();
            Global.startDate = Util1.toDateStr(company.getStartDate(), "dd/MM/yyyy");
            Global.endate = Util1.toDateStr(company.getEndDate(), "dd/MM/yyyy");
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

        jScrollPane1 = new javax.swing.JScrollPane();
        tblCompany = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Select Company");

        tblCompany.setFont(Global.companyFont);
        tblCompany.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblCompany.setRowHeight(40);
        tblCompany.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCompanyMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblCompany);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblCompanyMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCompanyMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            select();
        }
    }//GEN-LAST:event_tblCompanyMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblCompany;
    // End of variables declaration//GEN-END:variables
}