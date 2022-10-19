/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.dialog;

import com.acc.common.ChartOfAccountImportTableModel;
import com.acc.model.ChartOfAccount;
import com.common.Global;
import com.common.ReturnObject;
import com.common.Util1;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.awt.FileDialog;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 *
 * @author Lenovo
 */
@Component
public class ChartOfAccountImportDialog extends javax.swing.JDialog {

    private final Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
    private static final Logger log = LoggerFactory.getLogger(ChartOfAccountImportDialog.class);
    private final ChartOfAccountImportTableModel importTableModel = new ChartOfAccountImportTableModel();
    @Autowired
    private WebClient inventoryApi;
    @Autowired
    private TaskExecutor taskExecutor;
    private final List<ChartOfAccount> listCOA = new ArrayList<>();
    private final HashMap<Integer, Integer> hmZG = new HashMap<>();
    private final HashMap<String, String> hmCOA = new HashMap<>();

    /**
     * Creates new form CustomerImportDialog
     */
    public ChartOfAccountImportDialog() {
        super(Global.parentForm, true);
        initComponents();
    }

    private void initMain() {
        tblCustomer.setModel(importTableModel);
        tblCustomer.setRowHeight(Global.tblRowHeight);
        tblCustomer.getTableHeader().setFont(Global.tblHeaderFont);
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

    private void readFile(String path) {
        String line;
        String splitBy = ",";
        int lineCount = 0;

        try {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(path), "UTF8"))) {
                while ((line = br.readLine()) != null) //returns a Boolean value
                {
                    ChartOfAccount coa = new ChartOfAccount();
                    String[] data = line.split(splitBy);    // use comma as separator
                    String code = null;
                    String userCode = null;
                    String name = null;
                    String parentCode = null;
                    lineCount++;
                    try {
                        code = data[0];
                        userCode = data[1];
                        name = data[2].replaceAll("\"", "");
                        parentCode = data[3];

                    } catch (IndexOutOfBoundsException e) {
                        JOptionPane.showMessageDialog(Global.parentForm, "FORMAT ERROR IN LINE:" + lineCount);
                    }
                    coa.setCoaParent(parentCode);
                    coa.setCoaCodeUsr(userCode);
                    coa.setMigCode(code);
                    coa.setCoaNameEng(name);
                    coa.setActive(Boolean.TRUE);
                    coa.setCreatedDate(Util1.getTodayDate());
                    coa.setCreatedBy(Global.loginUser.getUserCode());
                    coa.setMacId(Global.macId);
                    listCOA.add(coa);

                }
            }
            if (chkIntegra.isSelected()) {
                toZawgyiFont();
            }
            importTableModel.setListCOA(listCOA);
        } catch (IOException e) {
            log.error("Read CSV File :" + e.getMessage());

        }
    }

    private void toZawgyiFont() {

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

    private void saveCOA() {
        if (!txtLevel.getText().isEmpty()) {
            int level = Util1.getInteger(txtLevel.getText());
            switch (level) {
                case 1:
                    saveLevelOne();
                    break;
                case 2:
                    saveLevel("1");
                    break;
                case 3:
                    saveLevel("2");
                    break;
            }
        }

    }

    private void saveLevelOne() {
        log.info("Save ChartOfAccount Level One.");
        JDialog loading = Util1.getLoading(this, null);
        taskExecutor.execute(() -> {
            importTableModel.getListCOA().forEach(coa -> {
                try {
                    coa.setOption("SYS");
                    coa.setCoaLevel(1);
                    saveCOA(coa);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(Global.parentForm, ex.getMessage());
                }
            });
            importTableModel.clear();
            loading.setVisible(false);
        });
        loading.setVisible(true);

    }

    private ChartOfAccount saveCOA(ChartOfAccount coa) {
        Mono<ReturnObject> result = inventoryApi.post()
                .uri("/account/save-coa")
                .body(Mono.just(coa), ChartOfAccount.class)
                .retrieve()
                .bodyToMono(ReturnObject.class);
        ReturnObject block = result.block();
        return gson.fromJson(gson.toJson(block.getData()), ChartOfAccount.class);
    }

    private void saveLevel(String level) {
        log.info("Save ChartOfAccount Level :" + level);
        /*JDialog loading = Util1.getLoading(this, loadingIcon);
        List<ChartOfAccount> listLevelOne = cOAService.search("-", "-", Global.compCode, level, "-", "-", "-");
        listLevelOne.forEach(one -> {
            hmCOA.put(one.getMigCode(), one.getCode());
        });
        taskExecutor.execute(() -> {
            importTableModel.getListCOA().stream().map(levelTwo -> {
                String pCode = hmCOA.get(levelTwo.getCoaParent());
                levelTwo.setCoaParent(pCode);
                return levelTwo;
            }).map(levelTwo -> {
                levelTwo.setOption("USR");
                return levelTwo;
            }).map(levelTwo -> {
                levelTwo.setCoaLevel(Integer.parseInt(txtLevel.getText()));
                return levelTwo;
            }).forEachOrdered(levelTwo -> {
                try {
                    cOAService.save(levelTwo);
                } catch (Exception ex) {
                    log.error("saveLevel : " + ex.getMessage());
                }
            });
            importTableModel.clear();
            loading.setVisible(false);
        });*/
        //loading.setVisible(true);
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(chkIntegra)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtLevel, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton2)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButton1, jButton2});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(chkIntegra)
                    .addComponent(jButton3)
                    .addComponent(txtLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
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
        initMain();
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

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkIntegra;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblCustomer;
    private javax.swing.JTextField txtLevel;
    // End of variables declaration//GEN-END:variables
}
