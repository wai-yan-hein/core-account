/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.acc.dialog;

import com.acc.common.TraderAImportTableModel;
import com.acc.model.TraderA;
import com.acc.model.TraderAKey;
import com.repo.AccountRepo;
import com.common.Global;
import com.common.ProUtil;
import com.common.TableCellRender;
import com.common.Util1;
import java.awt.Color;
import java.awt.FileDialog;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.task.TaskExecutor;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class TraderImportDialog extends javax.swing.JDialog {

    private AccountRepo accountRepo;
    private final TraderAImportTableModel tableModel = new TraderAImportTableModel();
    private TaskExecutor taskExecutor;
    private final HashMap<String, String> hmRegion = new HashMap<>();

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

    /**
     * Creates new form CustomerImportDialog
     *
     * @param parent
     */
    public TraderImportDialog(JFrame parent) {
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
        List<TraderA> traders = tableModel.getListTrader();
        btnSave.setEnabled(false);
        progress.setIndeterminate(true);
        traders.forEach((trader) -> {
            try {
                TraderA t = accountRepo.saveTrader(trader).block();
                lblLog.setText("Importing :" + t.getTraderName());
                lblLog.setForeground(Color.black);
            } catch (Exception e) {
                progress.setIndeterminate(false);
                btnSave.setEnabled(true);
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        });
        lblLog.setText("Imported");
        lblLog.setForeground(Color.green);
        tableModel.clear();
        btnSave.setEnabled(true);
    }

    private void readFile(String path) {
        List<TraderA> listTrader = new ArrayList<>();
        try {
            progress.setIndeterminate(true);
            Reader in = new FileReader(path);
            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setAllowMissingColumnNames(true)
                    .setIgnoreEmptyLines(true)
                    .setIgnoreHeaderCase(true)
                    .build();
            Iterable<CSVRecord> records = csvFormat.parse(in);
            records.forEach((row) -> {
                TraderA t = new TraderA();
                t.setTraderName(row.isMapped("Name") ? Util1.convertToUniCode(row.get("Name")) : "");
                if (!t.getTraderName().equals("")) {
                    TraderAKey key = new TraderAKey();
                    key.setCompCode(Global.compCode);
                    t.setKey(key);
                    t.setUserCode(row.isMapped("UserCode") ? Util1.convertToUniCode(row.get("UserCode")) : "");
                    t.setAddress(row.isMapped("Address") ? Util1.convertToUniCode(row.get("Address")) : "");
                    t.setPhone(row.isMapped("PhoneNo") ? Util1.convertToUniCode(row.get("PhoneNo")) : "");
                    t.setEmail(row.isMapped("Email") ? row.get("Email") : "");
                    t.setRemark(row.isMapped("Remark") ? row.get("Remark") : "");
                    t.setNrc(row.isMapped("Nrc") ? Util1.convertToUniCode(row.get("Nrc")) : "");
                    t.setActive(Boolean.TRUE);
                    t.setCreatedDate(LocalDateTime.now());
                    t.setCreatedBy(Global.loginUser.getUserCode());
                    t.setMacId(Global.macId);
                    t.setTraderType(getImportType());
                    t.setAccount(getAccount());
                    listTrader.add(t);
                }

            });
            tableModel.setListTrader(listTrader);
            progress.setIndeterminate(false);
        } catch (IOException e) {
            progress.setIndeterminate(false);
            log.error("readFile : " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Invalid Format.");
        }
    }

    private String getAccount() {
        String type = cboType.getSelectedItem().toString();
        return switch (type) {
            case "Customer" ->
                ProUtil.getProperty(ProUtil.DEBTOR_ACC);
            case "Supplier" ->
                ProUtil.getProperty(ProUtil.CREDITOR_ACC);
            default ->
                null;
        };
    }

    private String getImportType() {
        String type = cboType.getSelectedItem().toString();
        return switch (type) {
            case "Customer" ->
                "C";
            case "Supplier" ->
                "S";
            default ->
                null;
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
        cboType = new javax.swing.JComboBox<>();
        lblLog = new javax.swing.JLabel();

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
        tblTrader.setRowHeight(Global.tblRowHeight);
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

        chkIntegra.setText("Integra Font");

        cboType.setFont(Global.textFont);
        cboType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Customer", "Supplier" }));

        lblLog.setFont(Global.lableFont);
        lblLog.setText("-");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 612, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(chkIntegra)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblLog, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSave))
                    .addComponent(progress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(progress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 389, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblLog, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton2)
                        .addComponent(chkIntegra)
                        .addComponent(cboType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnSave))
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
    private javax.swing.JComboBox<String> cboType;
    private javax.swing.JCheckBox chkIntegra;
    private javax.swing.JButton jButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblLog;
    private javax.swing.JProgressBar progress;
    private javax.swing.JTable tblTrader;
    // End of variables declaration//GEN-END:variables
}
