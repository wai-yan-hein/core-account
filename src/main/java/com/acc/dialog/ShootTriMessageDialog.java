/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.acc.dialog;

import com.common.Global;
import com.common.Util1;
import com.google.gson.reflect.TypeToken;
import com.repo.AccountRepo;
import java.util.List;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

/**
 *
 * @author Athu Sint
 */
public class ShootTriMessageDialog extends javax.swing.JDialog {

    private AccountRepo accountRepo;

    public AccountRepo getAccountRepo() {
        return accountRepo;
    }

    public void setAccountRepo(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }

    public ShootTriMessageDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public void initMain() {
        initEditorPane();
        refresh();
    }

    private void initEditorPane() {
        editor.setContentType("text/html");
        // Create an HTML document and set it in the editor pane
        HTMLDocument doc = new HTMLDocument();
        editor.setDocument(doc);
        // Create an HTML editor kit and set it in the editor pane
        HTMLEditorKit kit = new HTMLEditorKit();
        editor.setEditorKit(kit);
    }

    private void refresh() {
        accountRepo.getShootTriMessage().subscribe((m) -> {
            if (!m.isEmpty()) {
                List<String> listStr =convertJsonToPlainStringList(m);
                StringBuilder htmlContent = new StringBuilder("<html><body>");
                for (String str : listStr) {
                    htmlContent.append(str);
                }
                htmlContent.append("</body></html>");
                // Set the formatted HTML in the editor pane
                editor.setText(htmlContent.toString());
                editor.setEditable(false);
            }
        });
    }

    public static List<String> convertJsonToPlainStringList(String jsonString) {

        // Use TypeToken to handle generic types
       return Util1.gson.fromJson(jsonString, new TypeToken<List<String>>() {}.getType());   
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        editor = new javax.swing.JEditorPane();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Trouble Shoot Tri Balance");

        jScrollPane1.setViewportView(editor);

        jButton1.setBackground(Global.selectionColor);
        jButton1.setFont(Global.lableFont);
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Refresh");
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
                .addComponent(jScrollPane1)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(164, 164, 164)
                .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(164, 164, 164))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        refresh();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JEditorPane editor;
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
