/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.dialog;

import com.repo.AccountRepo;
import com.acc.common.ChartOfAccountImportTableModel;
import com.acc.model.COAKey;
import com.acc.model.ChartOfAccount;
import com.common.Global;
import com.common.Util1;
import com.inventory.entity.CFont;
import java.awt.FileDialog;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class ChartOfAccountImportDialog extends javax.swing.JDialog {

    private final ChartOfAccountImportTableModel importTableModel = new ChartOfAccountImportTableModel();
    private AccountRepo accountRepo;
    private final List<ChartOfAccount> listCOA = new ArrayList<>();
    private final HashMap<Integer, Integer> hmZG = new HashMap<>();
    private final HashMap<String, String> hmCOA = new HashMap<>();
    private ChartOfAccount coaParent;

    public void setCoaParent(ChartOfAccount coaParent) {
        if (coaParent != null) {
            lblParent.setText(coaParent.getCoaNameEng());
        }
        this.coaParent = coaParent;

    }

    public void setAccountRepo(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }

    /**
     * Creates new form CustomerImportDialog
     *
     * @param frame
     */
    public ChartOfAccountImportDialog(JFrame frame) {
        super(frame, true);
        initComponents();
    }

    public void initMain() {
        tblCustomer.setModel(importTableModel);
        tblCustomer.setRowHeight(Global.tblRowHeight);
        tblCustomer.getTableHeader().setFont(Global.tblHeaderFont);
        tblCustomer.setShowGrid(true);
    }

    private void chooseFile() {
        FileDialog dialog = new FileDialog(Global.parentForm, "Choose CSV File", FileDialog.LOAD);
        dialog.setDirectory("D:\\");
        dialog.setFile(".csv");
        dialog.setVisible(true);
        String directory = dialog.getFile();
        log.info("File Path :" + directory);
        if (directory != null) {
            readFile(dialog.getDirectory() + "\\" + directory);
        }
    }

    private void readFromTextPane() {
        if (hasTextContent()) {
            Document document = txtPane.getDocument();
            Element root = document.getDefaultRootElement();
            int lineCount = root.getElementCount();
            try {
                for (int i = 0; i < lineCount; i++) {
                    Element lineElement = root.getElement(i);
                    int startOffset = lineElement.getStartOffset();
                    int endOffset = lineElement.getEndOffset();
                    String line = document.getText(startOffset, endOffset - startOffset);
                    String[] tmp = line.split("\t");
                    String userCode = tmp[0];
                    String coaName = tmp[1];
                    COAKey key = new COAKey();
                    key.setCompCode(Global.compCode);
                    ChartOfAccount coa = new ChartOfAccount();
                    coa.setKey(key);
                    coa.setCoaCodeUsr(userCode);
                    coa.setCoaNameEng(Util1.convertToUniCode(coaName));
                    coa.setActive(true);
                    coa.setDeleted(false);
                    coa.setMacId(Global.macId);
                    coa.setCreatedBy(Global.loginUser.getUserCode());
                    coa.setCreatedDate(LocalDateTime.now());
                    coa.setModifiedDate(LocalDateTime.now());
                    coa.setCoaOption("USR");
                    if (coaParent != null) {
                        coa.setCoaParent(coaParent.getKey().getCoaCode());
                        coa.setCoaLevel(coaParent.getCoaLevel() + 1);
                    }
                    listCOA.add(coa);
                }
                importTableModel.setListCOA(listCOA);
            } catch (BadLocationException e) {
                log.error("readFromTextPane : " + e.getMessage());
            }
        }
    }

    private void readFile(String path) {
        List<CFont> listFont = new ArrayList<>();
        if (listFont != null) {
            listFont.forEach(f -> {
                hmZG.put(f.getIntCode(), f.getFontKey().getZwKeyCode());
            });
        }
        List<ChartOfAccount> list = accountRepo.getChartOfAccount(3).block();
        list.forEach((t) -> {
            hmCOA.put(t.getCoaCodeUsr(), t.getKey().getCoaCode());
        });
        String line;
        String splitBy = ",";
        int lineCount = 0;

        try {
            try (FileInputStream fis = new FileInputStream(path); InputStreamReader isr = new InputStreamReader(fis); BufferedReader reader = new BufferedReader(isr)) {
                while ((line = reader.readLine()) != null) {
                    ChartOfAccount coa = new ChartOfAccount();
                    String[] data = line.split(splitBy);    // use comma as separator
                    String userCode = null;
                    String name = null;
                    String parentCode = null;
                    lineCount++;
                    try {
                        userCode = data[0];
                        name = data[1].replaceAll("\"", "");
                        parentCode = data[2];

                    } catch (IndexOutOfBoundsException e) {
                        JOptionPane.showMessageDialog(Global.parentForm, "FORMAT ERROR IN LINE:" + lineCount);
                    }
                    COAKey key = new COAKey();
                    key.setCompCode(Global.compCode);
                    coa.setKey(key);
                    coa.setCoaParent(hmCOA.get(parentCode));
                    coa.setCoaCodeUsr(userCode);
                    coa.setCoaNameEng(chkIntegra.isSelected() ? getZawgyiText(name) : name);
                    coa.setActive(Boolean.TRUE);
                    coa.setCreatedDate(LocalDateTime.now());
                    coa.setCreatedBy(Global.loginUser.getUserCode());
                    coa.setMacId(Global.macId);
                    coa.setCoaLevel(Util1.getInteger(txtLevel.getText()));
                    coa.setCoaOption("USR");
                    listCOA.add(coa);
                }
            }
            importTableModel.setListCOA(listCOA);
        } catch (IOException e) {
            log.error("Read CSV File :" + e.getMessage());

        }
    }

    private void saveCOA() {
        listCOA.forEach((t) -> {
            ChartOfAccount coa = accountRepo.saveCOA(t).block();
            log.info("saved : " + coa.getCoaNameEng());
        });
        importTableModel.clear();
        txtPane.setText(null);
    }

    private boolean hasTextContent() {
        Document document = txtPane.getDocument();
        int length = document.getLength();
        return length > 0;
    }

    private String getZawgyiText(String text) {
        String tmpStr = "";

        if (text != null) {
            for (int i = 0; i < text.length(); i++) {
                String tmpS = Character.toString(text.charAt(i));
                int tmpChar = (int) text.charAt(i);

                if (hmZG.containsKey(tmpChar)) {
                    char tmpc = (char) hmZG.get(tmpChar).intValue();
                    if (tmpStr.isEmpty()) {
                        tmpStr = Character.toString(tmpc);
                    } else {
                        tmpStr = tmpStr + Character.toString(tmpc);
                    }
                } else if (tmpS.equals("ƒ")) {
                    if (tmpStr.isEmpty()) {
                        tmpStr = "ႏ";
                    } else {
                        tmpStr = tmpStr + "ႏ";
                    }
                } else if (tmpStr.isEmpty()) {
                    tmpStr = tmpS;
                } else {
                    tmpStr = tmpStr + tmpS;
                }
            }
        }

        return tmpStr;
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
        tblCustomer = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        chkIntegra = new javax.swing.JCheckBox();
        jButton3 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        txtLevel = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtPane = new javax.swing.JTextPane();
        jLabel2 = new javax.swing.JLabel();
        lblParent = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        tblCustomer.setFont(Global.textFont);
        tblCustomer.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblCustomer);

        jButton1.setText("Save");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Choose File");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        chkIntegra.setText("Integra Font");
        chkIntegra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkIntegraActionPerformed(evt);
            }
        });

        jButton3.setText("Clear");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Level");

        txtLevel.setFont(Global.lableFont);

        jScrollPane2.setViewportView(txtPane);

        jLabel2.setText("Parent Code");

        lblParent.setText("-");

        jButton4.setText("Read");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(chkIntegra)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblParent, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtLevel, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1))
                    .addComponent(jScrollPane2)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton4)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButton1, jButton2});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(chkIntegra)
                    .addComponent(jButton3)
                    .addComponent(txtLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(lblParent))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        chooseFile();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
    }//GEN-LAST:event_formComponentShown

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        importTableModel.clear();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        saveCOA();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void chkIntegraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkIntegraActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkIntegraActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        readFromTextPane();
    }//GEN-LAST:event_jButton4ActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkIntegra;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblParent;
    private javax.swing.JTable tblCustomer;
    private javax.swing.JTextField txtLevel;
    private javax.swing.JTextPane txtPane;
    // End of variables declaration//GEN-END:variables
}
