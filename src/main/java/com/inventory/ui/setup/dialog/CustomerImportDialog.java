/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.inventory.ui.setup.dialog;

import com.acc.common.AccountRepo;
import com.common.Global;
import com.common.TableCellRender;
import com.common.Util1;
import com.inventory.model.Trader;
import com.inventory.model.CFont;
import com.inventory.model.TraderKey;
import com.inventory.ui.common.InventoryRepo;
import com.inventory.ui.setup.dialog.common.TraderImportTableModel;
import java.awt.FileDialog;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class CustomerImportDialog extends javax.swing.JDialog {

    private InventoryRepo inventoryRepo;
    private AccountRepo accountRepo;
    private final TraderImportTableModel tableModel = new TraderImportTableModel();
    private TaskExecutor taskExecutor;
    private final HashMap<Integer, Integer> hmZG = new HashMap<>();
    private final HashMap<String, String> hmCOA = new HashMap<>();

    public AccountRepo getAccountRepo() {
        return accountRepo;
    }

    public void setAccountRepo(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }

    public TaskExecutor getTaskExecutor() {
        return taskExecutor;
    }

    public void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public InventoryRepo getInventoryRepo() {
        return inventoryRepo;
    }

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    /**
     * Creates new form CustomerImportDialog
     *
     * @param parent
     */
    public CustomerImportDialog(JFrame parent) {
        super(parent, true);
        initComponents();
        initTable();
        progress.setVisible(false);
    }

    private void initTable() {
        tblTrader.setModel(tableModel);
        tblTrader.getTableHeader().setFont(Global.tblHeaderFont);
        tblTrader.setDefaultRenderer(Object.class, new TableCellRender());
    }

    private void chooseFile() {
        FileDialog dialog = new FileDialog(this, "Choose CSV File", FileDialog.LOAD);
        dialog.setDirectory("D:\\");
        dialog.setFile(".csv");
        dialog.setVisible(true);
        String directory = dialog.getFile();
        log.info("File Path :" + directory);
        if (directory != null) {
            readFile(dialog.getDirectory() + "\\" + directory);
        }
    }

    private void save() {
        List<Trader> traders = tableModel.getListTrader();
        btnSave.setEnabled(false);
        progress.setVisible(true);
        for (Trader trader : traders) {
            inventoryRepo.saveTrader(trader);
        }
        dispose();
    }

    private String getZawgyiText(String text) {
        String tmpStr = "";

        if (text != null) {
            for (int i = 0; i < text.length(); i++) {
                String tmpS = Character.toString(text.charAt(i));
                int tmpChar = (int) text.charAt(i);

                if (hmZG.containsKey(tmpChar)) {
                    log.info(tmpChar + "");
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

    private void readFile(String path) {
        List<CFont> listFont = inventoryRepo.getFont();
        if (listFont != null) {
            listFont.forEach(f -> {
                hmZG.put(f.getIntCode(), f.getFontKey().getZwKeyCode());
            });
        }
        /*  List<ChartOfAccount> list = accountRepo.getChartOfAccount();
        list.forEach((t) -> {
        hmCOA.put(t.getCoaCodeUsr(), t.getKey().getCoaCode());
        });*/
        String line;
        String splitBy = "\t";
        int lineCount = 0;
        List<Trader> listTrader = new ArrayList<>();
        try {
            try (FileInputStream fis = new FileInputStream(path); InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8); BufferedReader reader = new BufferedReader(isr)) {
                while ((line = reader.readLine()) != null) {
                    Trader t = new Trader();
                    String[] data = line.split(splitBy);    // use comma as separator
                    String no = null;
                    String code = null;
                    String rfid = null;
                    String name = null;
                    String ph1 = null;
                    String ph2 = null;
                    String remark = null;
                    String address = null;
                    String nrc = null;
                    String cp = null;
                    lineCount++;
                    try {
                        name = data[0];
                        nrc = data[1];
                        no = data[2];
                        code = data[3];
                        rfid = data[4];
                        address = data[5];
                        ph1 = data[6];
                        ph2 = data[7];
                        cp = data[8];
                        remark = data[9];

                    } catch (IndexOutOfBoundsException e) {
                        log.error(e.getMessage());
                    }
                    t.setUserCode(no == null ? code : no.concat(code));
                    t.setRfId(rfid);
                    t.setTraderName(Util1.convertToUniCode(name));
                    t.setAddress(Util1.convertToUniCode(address));
                    t.setPhone(Util1.convertToUniCode(Util1.getString(ph1).concat("," + Util1.getString(ph2))));
                    t.setRemark(Util1.convertToUniCode(remark));
                    t.setContactPerson(Util1.convertToUniCode(cp));
                    t.setNrc(Util1.convertToUniCode(nrc));
                    TraderKey key = new TraderKey();
                    key.setCompCode(Global.compCode);
                    key.setDeptId(Global.deptId);
                    t.setKey(key);
                    t.setActive(Boolean.TRUE);
                    t.setCreatedDate(Util1.getTodayDate());
                    t.setCreatedBy(Global.loginUser.getUserCode());
                    t.setMacId(Global.macId);
                    t.setType("CUS");
                    listTrader.add(t);
                }
            }
            tableModel.setListTrader(listTrader);
        } catch (IOException e) {
            log.error("Read CSV File :" + e.getMessage());

        }
    }

    private String getTraderType(String code) {
        return switch (code) {
            case "310001" ->
                "SUP";
            default ->
                "CUS";
        };
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
        tblTrader = new javax.swing.JTable();
        btnSave = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        progress = new javax.swing.JProgressBar();
        chkIntegra = new javax.swing.JCheckBox();
        chkCus = new javax.swing.JCheckBox();
        chkSup = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        tblTrader.setFont(Global.textFont);
        tblTrader.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblTrader);

        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        jButton2.setText("Choose File");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        progress.setIndeterminate(true);

        chkIntegra.setText("Integra Font");

        chkCus.setText("CUS");

        chkSup.setText("SUP");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(chkIntegra, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkCus, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkSup, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(progress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 399, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton2)
                        .addComponent(chkIntegra)
                        .addComponent(chkCus)
                        .addComponent(chkSup))
                    .addComponent(btnSave)
                    .addComponent(progress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        chooseFile();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        save();
    }//GEN-LAST:event_btnSaveActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox chkCus;
    private javax.swing.JCheckBox chkIntegra;
    private javax.swing.JCheckBox chkSup;
    private javax.swing.JButton jButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JProgressBar progress;
    private javax.swing.JTable tblTrader;
    // End of variables declaration//GEN-END:variables
}
