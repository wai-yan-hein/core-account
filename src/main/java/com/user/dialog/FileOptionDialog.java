/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.user.dialog;

import com.common.Global;
import com.common.TableCellRender;
import com.user.common.FileTableModel;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.ListSelectionModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class FileOptionDialog extends javax.swing.JDialog {

    private final FileTableModel fileTableModel = new FileTableModel();
    private File selectdFile;

    public File getSelectdFile() {
        return selectdFile;
    }

    /**
     * Creates new form CurrencySetup
     *
     * @param frame
     */
    public FileOptionDialog(JFrame frame) {
        super(frame, true);
        initComponents();
    }

    public void initMain() {
        initTable();
    }

    public List<File> getTemplateFiles() {
        File file = new File("template");
        return Arrays.asList(file.listFiles());
    }

    private void initTable() {
        tblFile.setModel(fileTableModel);
        tblFile.getTableHeader().setFont(Global.textFont);
        tblFile.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblFile.setDefaultRenderer(Boolean.class, new TableCellRender());
        tblFile.setDefaultRenderer(Object.class, new TableCellRender());
        tblFile.setRowHeight(Global.tblRowHeight);
        tblFile.setDefaultRenderer(Object.class, new TableCellRender());
        fileTableModel.setListFile(getTemplateFiles());
    }

    private void select() {
        int row = tblFile.convertRowIndexToModel(tblFile.getSelectedRow());
        if (row >= 0) {
            selectdFile = fileTableModel.getFile(row);
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
        tblFile = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("File Option");

        tblFile.setFont(Global.textFont);
        tblFile.setModel(new javax.swing.table.DefaultTableModel(
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
        tblFile.setName("tblFile"); // NOI18N
        tblFile.setRowHeight(Global.tblRowHeight);
        jScrollPane1.setViewportView(tblFile);

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
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE)
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

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblFile;
    // End of variables declaration//GEN-END:variables

}
