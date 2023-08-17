/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.inventory.ui.setup.dialog;

import com.common.Global;
import com.common.TableCellRender;
import com.common.Util1;
import com.inventory.model.CFont;
import com.inventory.model.Category;
import com.inventory.model.CategoryKey;
import com.inventory.model.Stock;
import com.inventory.model.StockBrand;
import com.inventory.model.StockBrandKey;
import com.inventory.model.StockKey;
import com.inventory.model.StockType;
import com.inventory.model.StockTypeKey;
import com.repo.InventoryRepo;
import com.inventory.ui.setup.dialog.common.StockImportTableModel;
import java.awt.FileDialog;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JFrame;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.task.TaskExecutor;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class StockImportDialog extends javax.swing.JDialog {

    private final StockImportTableModel tableModel = new StockImportTableModel();
    private TaskExecutor taskExecutor;
    private InventoryRepo inventoryRepo;
    private final HashMap<Integer, Integer> hmIntToZw = new HashMap<>();
    private final HashMap<String, String> hmGroup = new HashMap<>();
    private final HashMap<String, String> hmCat = new HashMap<>();
    private final HashMap<String, String> hmBrand = new HashMap<>();

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
        tblTrader.setFont(Global.textFont);
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
            String filePath = dialog.getDirectory() + "\\" + directory;
            removeBOMFromFile(filePath);
            readFile(filePath);
        }
    }

    private void removeBOMFromFile(String filePath) {
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8); BufferedReader bufferedReader = new BufferedReader(reader)) {

            // Skip the BOM by reading the first character
            bufferedReader.mark(1);
            if (bufferedReader.read() != '\uFEFF') {
                // Reset the reader if no BOM found
                bufferedReader.reset();
            }

            // Create a temporary file to write the BOM-free content
            File tempFile = File.createTempFile("temp", null);
            try (FileWriter writer = new FileWriter(tempFile)) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    writer.write(line);
                    writer.write(System.lineSeparator());
                }
            }

            // Replace the original file with the temporary file
            File originalFile = new File(filePath);
            if (originalFile.delete()) {
                tempFile.renameTo(originalFile);
            }
        } catch (IOException e) {
        }
    }

    private void save() {
        List<Stock> traders = tableModel.getListStock();
        btnSave.setEnabled(false);
        lblLog.setText("Importing.");
        for (Stock stock : traders) {
            inventoryRepo.saveStock(stock).block();
        }
        lblLog.setText("Success.");
        dispose();
    }

    private static int parseIntegerOrDefault(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static boolean isContainBOM(Path path) throws IOException {

        if (Files.notExists(path)) {
            throw new IllegalArgumentException("Path: " + path + " does not exists!");
        }

        boolean result = false;

        byte[] bom = new byte[3];
        try (InputStream is = new FileInputStream(path.toFile())) {

            // read first 3 bytes of a file.
            is.read(bom);

            // BOM encoded as ef bb bf
            String content = new String(Hex.encodeHex(bom));
            if ("efbbbf".equalsIgnoreCase(content)) {
                result = true;
            }

        }

        return result;
    }

    private void readFile(String path) {
        List<CFont> listFont = new ArrayList<>();
        listFont.forEach(f -> {
            hmIntToZw.put(f.getIntCode(), f.getFontKey().getZwKeyCode());
        });

//        HashMap<String, StockType> hm = new HashMap<>();
//        List<StockType> listST = inventoryRepo.getStockType().block();
//        if (!listST.isEmpty()) {
//            for (StockType st : listST) {
//                hm.put(st.getUserCode(), st);
//            }
//        }
        List<Stock> listStock = new ArrayList<>();
        try {
            Reader in = new FileReader(path);
            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setAllowMissingColumnNames(true)
                    .setIgnoreEmptyLines(true)
                    .setIgnoreHeaderCase(true)
                    .build();
            Iterable<CSVRecord> records = csvFormat.parse(in);
            records.forEach(r -> {
                Stock t = new Stock();
                t.setStockName(r.isMapped("StockName") ? Util1.convertToUniCode(r.get("StockName")) : "");
                if (!t.getStockName().equals("")) {
                    StockKey key = new StockKey();
                    key.setCompCode(Global.compCode);
                    key.setStockCode(null);
                    t.setKey(key);
                    t.setDeptId(r.isMapped("Department") ? parseIntegerOrDefault(r.get("Department"), Global.deptId) : Global.deptId);
                    t.setUserCode(r.isMapped("UserCode") ? Util1.convertToUniCode(r.get("UserCode")) : "");
                    t.setSalePriceN(r.isMapped("SalePrice") ? Util1.getFloat(r.get("SalePrice")) : Util1.getFloat("0"));
                    t.setTypeCode(r.isMapped("StockGroup") ? getGroupCode(r.get("StockGroup"), t.getDeptId()) : "");
                    t.setGroupName(r.isMapped("StockGroup") ? r.get("StockGroup") : "");
                    t.setTypeCode(r.isMapped("GroupId") ? r.get("GroupId") : null);
                    log.info(t.getTypeCode());
                    t.setCatCode(r.isMapped("Category") ? getCategoryCode(r.get("Category"), t.getDeptId()) : "");
                    t.setCatName(r.isMapped("Category") ? Util1.convertToUniCode(r.get("Category")) : "");
                    t.setBrandCode(r.isMapped("Brand") ? getBrandCode(r.get("Brand"), t.getDeptId()) : "");
                    t.setBrandName(r.isMapped("Brand") ? Util1.convertToUniCode(r.get("Brand")) : "");
                    t.setActive(true);
                    t.setCreatedDate(LocalDateTime.now());
                    t.setCreatedBy(Global.loginUser.getUserCode());
                    t.setMacId(Global.macId);
                    t.setCalculate(true);
                    listStock.add(t);
                }
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

    private String getGroupCode(String str, Integer deptId) {
        if (hmGroup.isEmpty()) {
            List<StockType> list = inventoryRepo.getStockType().block();
            if (list != null) {
                list.forEach((t) -> {
                    hmGroup.put(t.getStockTypeName(), t.getKey().getStockTypeCode());
                });
            }

        }
        if (hmGroup.get(str) == null && !str.isEmpty()) {
            StockType st = saveGroup(str, deptId);
            hmGroup.put(st.getStockTypeName(), st.getKey().getStockTypeCode());
        }
        return hmGroup.get(str);
    }

    private StockType saveGroup(String str, Integer deptId) {
        StockType stockType = new StockType();
        stockType.setUserCode(Global.loginUser.getUserCode());
        stockType.setStockTypeName(str);
        stockType.setAccount("");
        StockTypeKey key = new StockTypeKey();
        key.setCompCode(Global.compCode);
        key.setStockTypeCode(null);
        stockType.setKey(key);
        stockType.setDeptId(deptId);
        stockType.setCreatedBy(Global.loginUser.getUserCode());
        stockType.setCreatedDate(LocalDateTime.now());
        stockType.setMacId(Global.macId);
        return inventoryRepo.saveStockType(stockType).block();
    }

    private String getCategoryCode(String str, Integer deptId) {
        if (hmCat.isEmpty()) {
            List<Category> list = inventoryRepo.getCategory().block();
            if (list != null) {
                list.forEach((t) -> {
                    hmCat.put(t.getCatName(), t.getKey().getCatCode());
                });
            }

        }
        if (hmCat.get(str) == null && !str.isEmpty()) {
            Category ct = saveCategory(str, deptId);
            hmCat.put(ct.getCatName(), ct.getKey().getCatCode());
        }
        return hmCat.get(str);
    }

    private Category saveCategory(String str, Integer deptId) {
        Category category = new Category();
        CategoryKey key = new CategoryKey();
        key.setCatCode(null);
        key.setCompCode(Global.compCode);
        category.setDeptId(Global.deptId);
        category.setKey(key);
        category.setDeptId(deptId);
        category.setCreatedBy(Global.loginUser.getUserCode());
        category.setCreatedDate(LocalDateTime.now());
        category.setMacId(Global.macId);
        category.setUserCode(Global.loginUser.getUserCode());//txtUserCode.getText()
        category.setCatName(str);
        return inventoryRepo.saveCategory(category).block();

    }

    private String getBrandCode(String str, Integer deptId) {
        if (hmBrand.isEmpty()) {
            List<StockBrand> list = inventoryRepo.getStockBrand().block();
            if (list != null) {
                list.forEach((t) -> {
                    hmBrand.put(t.getBrandName(), t.getKey().getBrandCode());
                });
            }
        }
        if (hmBrand.get(str) == null && !str.isEmpty()) {
            StockBrand sb = saveBrand(str, deptId);
            hmBrand.put(sb.getBrandName(), sb.getKey().getBrandCode());
        }
        return hmBrand.get(str);
    }

    private StockBrand saveBrand(String str, Integer deptId) {
        StockBrand brand = new StockBrand();
        brand.setUserCode(Global.loginUser.getUserCode());
        brand.setBrandName(str);
        StockBrandKey key = new StockBrandKey();
        key.setBrandCode(null);
        key.setCompCode(Global.compCode);
        brand.setDeptId(Global.deptId);
        brand.setKey(key);
        brand.setDeptId(deptId);
        brand.setCreatedBy(Global.loginUser.getUserCode());
        brand.setCreatedDate(LocalDateTime.now());
        brand.setMacId(Global.macId);
        return inventoryRepo.saveBrand(brand).block();
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
