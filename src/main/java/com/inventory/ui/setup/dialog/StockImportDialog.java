/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.inventory.ui.setup.dialog;

import com.common.Global;
import com.common.TableCellRender;
import com.common.Util1;
import com.inventory.model.CFont;
import com.inventory.model.Stock;
import com.inventory.model.StockKey;
import com.inventory.model.StockType;
import com.inventory.ui.common.InventoryRepo;
import com.inventory.ui.setup.dialog.common.StockImportTableModel;
import java.awt.FileDialog;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JFrame;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.task.TaskExecutor;
import org.springframework.web.reactive.function.client.WebClient;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class StockImportDialog extends javax.swing.JDialog {
    
    private WebClient inventoryApi;
    private final StockImportTableModel tableModel = new StockImportTableModel();
    private TaskExecutor taskExecutor;
    private InventoryRepo inventoryRepo;
    private final HashMap<Integer, Integer> hmIntToZw = new HashMap<>();
    private final HashMap<String, String> hmGroup = new HashMap<>();
    
    public InventoryRepo getInventoryRepo() {
        return inventoryRepo;
    }
    
    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }
    
    public TaskExecutor getTaskExecutor() {
        return taskExecutor;
    }
    
    public void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }
    
    public WebClient getWebClient() {
        return inventoryApi;
    }
    
    public void setWebClient(WebClient inventoryApi) {
        this.inventoryApi = inventoryApi;
    }

    /**
     * Creates new form CustomerImportDialog
     *
     * @param parent
     */
    public StockImportDialog(JFrame parent) {
        super(parent, true);
        initComponents();
        initTable();
    }
    
    private void initTable() {
        tblTrader.setModel(tableModel);
        tblTrader.getTableHeader().setFont(Global.tblHeaderFont);
        tblTrader.setDefaultRenderer(Object.class, new TableCellRender());
        tblTrader.setDefaultRenderer(Float.class, new TableCellRender());
        
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
        List<Stock> traders = tableModel.getListStock();
        btnSave.setEnabled(false);
        lblLog.setText("Importing.");
        for (Stock stock : traders) {
            inventoryRepo.saveStock(stock).subscribe();
        }
        lblLog.setText("Success.");
        dispose();
    }
    
    private void readFile(String path) {
        List<CFont> listFont = new ArrayList<>();
        if (listFont != null) {
            listFont.forEach(f -> {
                hmIntToZw.put(f.getIntCode(), f.getFontKey().getZwKeyCode());
            });
        }

//        HashMap<String, StockType> hm = new HashMap<>();
//        List<StockType> listST = inventoryRepo.getStockType().block();
//        if (!listST.isEmpty()) {
//            for (StockType st : listST) {
//                hm.put(st.getUserCode(), st);
//            }
//        }
        List<Stock> listStock = new ArrayList<>();
        try {
            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setAllowMissingColumnNames(true)
                    .setIgnoreEmptyLines(true)
                    .build();
            Reader in = new FileReader(path);
            Iterable<CSVRecord> records = csvFormat.parse(in);
            records.forEach(r -> {
                Stock t = new Stock();
                StockKey key = new StockKey();
                key.setCompCode(Global.compCode);
                key.setDeptId(Global.deptId);
                key.setStockCode(null);
                t.setKey(key);
                t.setUserCode(r.isMapped("UserCode") ? Util1.convertToUniCode(r.get("UserCode")) : "");
                t.setStockName(r.isMapped("StockName") ? Util1.convertToUniCode(r.get("StockName")) : "");
                t.setSalePriceN(r.isMapped("SalePrice") ? Util1.getFloat(r.get("SalePrice")) : Util1.getFloat("0"));
                t.setTypeCode(r.isMapped("StockGroup") ? getGroupCode(r.get("StockGroup")) : "");
                t.setActive(true);
                t.setCreatedDate(LocalDateTime.now());
                t.setCreatedBy(Global.loginUser.getUserCode());
                t.setMacId(Global.macId);
                t.setCalculate(true);
                listStock.add(t);
            });
            tableModel.setListStock(listStock);
        } catch (IOException e) {
            log.error("Read CSV File :" + e.getMessage());
            
        }
    }
    
    private String getZawgyiText(String text) {
        String tmpStr = "";
        if (text != null) {
            for (int i = 0; i < text.length(); i++) {
                String tmpS = Character.toString(text.charAt(i));
                int tmpChar = (int) text.charAt(i);
                if (hmIntToZw.containsKey(tmpChar)) {
                    char tmpc = (char) hmIntToZw.get(tmpChar).intValue();
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
    
    private String getGroupCode(String str) {
        if (hmGroup.isEmpty()) {
            List<StockType> list = inventoryRepo.getStockType().block();
            list.forEach((t) -> {
                hmGroup.put(t.getUserCode(), t.getKey().getStockTypeCode());
            });
        }
        return hmGroup.get(str);
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
        lblLog = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        tblTrader.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 668, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblLog, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 399, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSave, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblLog, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
    private javax.swing.JButton jButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblLog;
    private javax.swing.JTable tblTrader;
    // End of variables declaration//GEN-END:variables
}
