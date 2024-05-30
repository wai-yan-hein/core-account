/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.inventory.ui.setup.dialog;

import com.common.FontUtil;
import com.repo.AccountRepo;
import com.common.Global;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.common.Util1;
import com.inventory.entity.CFont;
import com.inventory.entity.Region;
import com.inventory.entity.RegionKey;
import com.inventory.entity.Trader;
import com.inventory.entity.TraderGroup;
import com.inventory.entity.TraderGroupKey;
import com.inventory.entity.TraderKey;
import com.repo.InventoryRepo;
import com.inventory.ui.setup.dialog.common.TraderImportTableModel;
import com.repo.UserRepo;
import com.user.model.DepartmentKey;
import com.user.model.DepartmentUser;
import java.awt.Color;
import java.awt.FileDialog;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.task.TaskExecutor;
import reactor.core.publisher.Flux;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class CustomerImportDialog extends javax.swing.JDialog {

    @Setter
    private InventoryRepo inventoryRepo;
    @Setter
    private UserRepo userRepo;

    private final TraderImportTableModel tableModel = new TraderImportTableModel();
    @Setter
    private TaskExecutor taskExecutor;
    private final HashMap<String, String> hmRegion = new HashMap<>();
    private final HashMap<String, String> hmTraderGp = new HashMap<>();
    private final HashMap<String, String> hmDepartment = new HashMap<>();
    private HashMap<Integer, Integer> hmZG = new HashMap<>();
    List<CFont> listFont = new ArrayList<>();
    @Setter
    private SelectionObserver observer;

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
        progress.setIndeterminate(true);
        Flux.fromIterable(traders)
                .delayElements(Duration.ofMillis(300))
                .doOnNext((trader) -> {
                    inventoryRepo.saveTrader(trader)
                            .doOnSuccess((t) -> {
                                lblLog.setText("Importing :" + trader.getTraderName());
                                lblLog.setForeground(Color.black);
                                observer.selected("Trader", t);
                            }).subscribe();
                }).doOnError((e) -> {
            progress.setIndeterminate(false);
            btnSave.setEnabled(true);
            JOptionPane.showMessageDialog(this, e.getMessage());
        }).doOnTerminate(() -> {
            lblLog.setText("Imported");
            lblLog.setForeground(Color.green);
            tableModel.clear();
            btnSave.setEnabled(true);
        }).subscribe();
    }

    private String getRegion(String str) {
        if (hmRegion.isEmpty()) {
            List<Region> list = inventoryRepo.getRegion().block();
            if (list != null) {
                list.forEach((t) -> {
                    hmRegion.put(t.getRegionName(), t.getKey().getRegCode());
                });
            }
        }
        if (hmRegion.get(str) == null && !str.isEmpty()) {
            Region reg = saveRegion(str);
            hmRegion.put(reg.getRegionName(), reg.getKey().getRegCode());
        }
        return hmRegion.get(str);
    }

    private Region saveRegion(String str) {
        Region region = new Region();
        region.setUserCode(Global.loginUser.getUserCode());
        region.setRegionName(str);
        RegionKey key = new RegionKey();
        key.setCompCode(Global.compCode);
        key.setRegCode(null);
        region.setKey(key);
        region.setDeptId(Global.deptId);
        region.setCreatedBy(Global.loginUser.getUserCode());
        region.setCreatedDate(Util1.getTodayLocalDateTime());
        region.setMacId(Global.macId);
        return inventoryRepo.saveRegion(region).block();
    }

    private String getTraderGroup(String str) {
        if (hmTraderGp.isEmpty()) {
            List<TraderGroup> list = inventoryRepo.getTraderGroup().block();
            if (list != null) {
                list.forEach((t) -> {
                    hmTraderGp.put(t.getGroupName(), t.getKey().getGroupCode());
                });
            }
        }
        if (hmTraderGp.get(str) == null && !str.isEmpty()) {
            TraderGroup t = saveTraderGroup(str);
            hmTraderGp.put(t.getGroupName(), t.getKey().getGroupCode());
        }
        return hmTraderGp.get(str);
    }

    private TraderGroup saveTraderGroup(String str) {
        TraderGroup group = new TraderGroup();
        TraderGroupKey key = new TraderGroupKey();
        key.setCompCode(Global.compCode);
        key.setGroupCode(null);
        group.setKey(key);
        group.setGroupName(str);
        group.setDeptId(Global.deptId);
        return inventoryRepo.saveTraderGroup(group).block();
    }

    private Integer getDepartment(String str) {
        if (Util1.isNullOrEmpty(str)) {
            return Global.deptId;
        }
        if (hmDepartment.isEmpty()) {
            List<DepartmentUser> list = userRepo.getDeparment(true).block();
            if (list != null) {
                list.forEach((t) -> {
                    hmDepartment.put(t.getDeptName(), t.getKey().getDeptId().toString());
                    System.err.println(t.getDeptName());
                });
            }
        }
        if (hmDepartment.get(str) == null && !str.isEmpty()) {
            DepartmentUser t = saveDepartment(str);
            hmDepartment.put(t.getDeptName(), t.getKey().getDeptId().toString());
        }
        return Integer.valueOf(hmDepartment.get(str));
    }

    private DepartmentUser saveDepartment(String str) {
        DepartmentUser user = new DepartmentUser();
        DepartmentKey key = new DepartmentKey();
        key.setDeptId(null);
        key.setCompCode(Global.compCode);
        user.setKey(key);
        user.setUserCode(Global.loginUser.getUserCode());
        user.setDeptName(str);
        user.setActive(true);
        user.setUpdatedDate(LocalDateTime.now());

        return userRepo.saveDepartment(user).block();
    }

    private void readFile(String path) {
        progress.setIndeterminate(true);
        try {
            listFont = FontUtil.generateCFonts();
            hmZG = new HashMap<>();
            if (listFont != null) {
                listFont.forEach(f -> {
                    hmZG.put(f.getIntCode(), f.getFontKey().getZwKeyCode());
                });
            }
            Reader in = new FileReader(path);
            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setAllowMissingColumnNames(true)
                    .setIgnoreEmptyLines(true)
                    .setIgnoreHeaderCase(true)
                    .build();
            Iterable<CSVRecord> records = csvFormat.parse(in);
            Flux.fromIterable(records).doOnNext((row) -> {
                Trader t = new Trader();
                String name = row.isMapped("Name") ? row.get("Name") : ""; // Util1.convertToUniCode(row.get("Name"))
                t.setTraderName(chkIntegra.isSelected() ? Util1.convertToUniCode(FontUtil.getZawgyiText(name, hmZG)) : Util1.convertToUniCode(name));
                t.setTraderName(Util1.convertToTitleCase(t.getTraderName()));
                if (!t.getTraderName().equals("")) {
                    TraderKey key = new TraderKey();
                    key.setCompCode(Global.compCode);
                    t.setKey(key);
                    t.setUserCode(row.isMapped("UserCode") ? Util1.convertToUniCode(row.get("UserCode")) : "");
                    t.setAddress(row.isMapped("Address") ? Util1.convertToUniCode(row.get("Address")) : "");
                    t.setPhone(row.isMapped("PhoneNo") ? Util1.convertToUniCode(row.get("PhoneNo")) : "");
                    t.setContactPerson(row.isMapped("ContactPerson") ? Util1.convertToUniCode(row.get("ContactPerson")) : "");
                    t.setEmail(row.isMapped("Email") ? row.get("Email") : "");
                    t.setDeptId(row.isMapped("Department") ? getDepartment(row.get("Department")) : Global.deptId);
                    t.setRegCode(row.isMapped("Region") ? getRegion(row.get("Region")) : "");
                    t.setGroupCode(row.isMapped("Group") ? getTraderGroup(row.get("Group")) : "");
                    t.setRemark(row.isMapped("Remark") ? row.get("Remark") : "");
                    t.setNrc(row.isMapped("Nrc") ? row.get("Nrc") : "");
                    t.setActive(Boolean.TRUE);
                    t.setCreatedDate(LocalDateTime.now());
                    t.setCreatedBy(Global.loginUser.getUserCode());
                    t.setMacId(Global.macId);
                    t.setType(getImportType());
                    t.setAccount(getAccount());
                    tableModel.addObject(t);
                }
            }).doOnTerminate(() -> {
                progress.setIndeterminate(false);
            }).subscribe();
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
                "CUS";
            case "Supplier" ->
                "SUP";
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

        setTitle("Import Dialog");

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

        btnSave.setFont(Global.textFont);
        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        jButton2.setFont(Global.textFont);
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
