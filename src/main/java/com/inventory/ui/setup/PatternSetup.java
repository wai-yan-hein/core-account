/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.inventory.ui.setup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.common.Global;
import com.common.PanelControl;
import com.common.ReturnObject;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.common.Util1;
import com.inventory.editor.LocationCellEditor;
import com.inventory.editor.StockCellEditor;
import com.inventory.model.Pattern;
import com.inventory.model.PatternDetail;
import com.inventory.model.Stock;
import com.inventory.model.StockUnit;
import com.inventory.ui.common.InventoryRepo;
import com.inventory.ui.common.PatternDetailTableModel;
import com.inventory.ui.common.PatternTableModel;
import com.inventory.ui.setup.dialog.PatterCreateDialog;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.inventory.ui.setup.dialog.common.StockUnitEditor;
import java.awt.event.KeyEvent;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 *
 * @author Lenovo
 */
@Component
public class PatternSetup extends javax.swing.JPanel implements PanelControl {

    public static final Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
    private final PatternTableModel patternTableModel = new PatternTableModel();
    private final PatternDetailTableModel patternDetailTableModel = new PatternDetailTableModel();
    private PatterCreateDialog dialog;
    private SelectionObserver observer;
    private JProgressBar progress;
    private List<Stock> listStock = new ArrayList<>();
    private List<StockUnit> lisetStockUnit = new ArrayList<>();

    @Autowired
    private WebClient inventoryApi;
    @Autowired
    private InventoryRepo inventoryRepo;

    public JProgressBar getProgress() {
        return progress;
    }

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }

    public SelectionObserver getObserver() {
        return observer;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    /**
     * Creates new form PatternSetup
     */
    public PatternSetup() {
        initComponents();
    }

    public void initMain() {
        initTablePattern();
        initTablePD();
        searchPattern();
        focusInTablePattern();
    }

    private void focusInTablePattern() {
        int row = tblPattern.getRowCount();
        if (row >= 1) {
            tblPattern.setRowSelectionInterval(row - 1, row - 1);
        }
        tblPattern.requestFocusInWindow();
    }

    private void focusOnPD() {
        int row = tblPD.getRowCount();
        if (row >= 1) {
            tblPD.setColumnSelectionInterval(0, 0);
            tblPD.setRowSelectionInterval(row - 1, row - 1);
        }
        tblPD.requestFocusInWindow();
    }

    private void searchPattern() {
        Mono<ReturnObject> result = inventoryApi
                .get()
                .uri(builder -> builder.path("/setup/get-pattern")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().bodyToMono(ReturnObject.class);
        result.subscribe((t) -> {
            java.lang.reflect.Type listType = new TypeToken<ArrayList<Pattern>>() {
            }.getType();
            List<Pattern> listOP = gson.fromJson(gson.toJsonTree(t.getList()), listType);
            patternTableModel.setListPattern(listOP);
            lblRecord.setText(listOP.size() + "");
        }, (e) -> {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
        });
    }

    private void initTablePattern() {
        patternTableModel.setWebClient(inventoryApi);
        tblPattern.setModel(patternTableModel);
        tblPattern.getTableHeader().setFont(Global.tblHeaderFont);
        tblPattern.setRowHeight(Global.tblRowHeight);
        tblPattern.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblPattern.setFont(Global.textFont);
        tblPattern.getColumnModel().getColumn(0).setPreferredWidth(20);
        tblPattern.getColumnModel().getColumn(1).setPreferredWidth(50);
        tblPattern.getColumnModel().getColumn(2).setPreferredWidth(200);
        tblPattern.getColumnModel().getColumn(3).setPreferredWidth(10);
        tblPattern.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblPattern.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblPattern.setDefaultRenderer(Object.class, new TableCellRender());
        tblPattern.setDefaultRenderer(Boolean.class, new TableCellRender());
    }

    private void searchPD(String patternCode) {
        Mono<ReturnObject> result = inventoryApi
                .get()
                .uri(builder -> builder.path("/setup/get-pattern-detail")
                .queryParam("patternCode", patternCode)
                .build())
                .retrieve().bodyToMono(ReturnObject.class);
        result.subscribe((t) -> {
            java.lang.reflect.Type listType = new TypeToken<ArrayList<PatternDetail>>() {
            }.getType();
            List<PatternDetail> listOP = gson.fromJson(gson.toJsonTree(t.getList()), listType);
            patternDetailTableModel.setListPattern(listOP);
            patternDetailTableModel.addRow();
            focusOnPD();
        }, (e) -> {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
        });
    }

    private void save(Pattern p) {
        if (isValidEntry(p)) {
            if (Objects.isNull(p.getPatternCode())) {
                p.setCompCode(Global.compCode);
                p.setMacId(Global.macId);
                p.setCreatedBy(Global.loginUser.getUserCode());
                p.setCreatedDate(Util1.getTodayDate());
            } else {
                p.setUpdatedBy(Global.loginUser.getUserCode());
            }
            p = inventoryRepo.savePattern(p);
            if (p.getPatternCode() != null) {
                if (dialog.getStatus().equals("NEW")) {
                    patternTableModel.addPattern(p);
                } else {
                    int selectRow = tblPattern.convertRowIndexToModel(tblPattern.getSelectedRow());
                    patternTableModel.setPattern(p, selectRow);
                }
                int size = patternTableModel.getListPattern().size();
                lblRecord.setText(String.valueOf(size));
            }
        }
    }

    private boolean isValidEntry(Pattern p) {
        boolean status = true;
        if (Util1.isNull(p.getPatternName())) {
            status = false;
        }
        return status;
    }

    private void initTablePD() {
        listStock = inventoryRepo.getStock(true);
        lisetStockUnit = inventoryRepo.getStockUnit();
        patternDetailTableModel.setTable(tblPD);
        patternDetailTableModel.setPanel(this);
        patternDetailTableModel.setWebClient(inventoryApi);
        tblPD.setModel(patternDetailTableModel);
        tblPD.getTableHeader().setFont(Global.tblHeaderFont);
        tblPD.setRowHeight(Global.tblRowHeight);
        tblPD.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblPD.setFont(Global.textFont);
        tblPD.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor(listStock));
        tblPD.getColumnModel().getColumn(1).setCellEditor(new StockCellEditor(listStock));
        tblPD.getColumnModel().getColumn(2).setCellEditor(new LocationCellEditor(inventoryRepo.getLocation()));
        tblPD.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());
        tblPD.getColumnModel().getColumn(4).setCellEditor(new StockUnitEditor(lisetStockUnit));
        tblPD.getColumnModel().getColumn(5).setCellEditor(new AutoClearEditor());
        tblPD.getColumnModel().getColumn(6).setCellEditor(new StockUnitEditor(lisetStockUnit));
        tblPD.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblPD.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblPD.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblPD.getColumnModel().getColumn(3).setPreferredWidth(50);
        tblPD.getColumnModel().getColumn(4).setPreferredWidth(50);
        tblPD.getColumnModel().getColumn(5).setPreferredWidth(50);
        tblPD.getColumnModel().getColumn(6).setPreferredWidth(50);
        tblPD.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblPD.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
        tblPattern = new javax.swing.JTable();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblPD = new javax.swing.JTable();
        lblName = new javax.swing.JLabel();
        lblPatternName = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        lblRecord = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        tblPattern.setModel(new javax.swing.table.DefaultTableModel(
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
        tblPattern.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPatternMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblPattern);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        tblPD.setAutoCreateRowSorter(true);
        tblPD.setFont(Global.textFont);
        tblPD.setModel(new javax.swing.table.DefaultTableModel(
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
        tblPD.setShowHorizontalLines(true);
        tblPD.setShowVerticalLines(true);
        jScrollPane2.setViewportView(tblPD);

        lblName.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        lblName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblName.setText("Pattern List");

        lblPatternName.setFont(Global.menuFont);
        lblPatternName.setText("Pattern Name");

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Record :");

        lblRecord.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        lblRecord.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblRecord.setText("-");

        jButton1.setBackground(Global.selectionColor);
        jButton1.setFont(Global.lableFont);
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Create Pattern");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setBackground(Global.selectionColor);
        jButton2.setFont(Global.lableFont);
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Edit Pattern");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator2)
                    .addComponent(lblName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblRecord, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(3, 3, 3))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblPatternName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 494, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblPatternName, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addComponent(jSeparator1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(lblName, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton1)
                            .addComponent(jButton2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(lblRecord))
                        .addGap(6, 6, 6)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here
        dialog = new PatterCreateDialog(Global.parentForm);
        dialog.setInventoryRepo(inventoryRepo);
        dialog.initCombo();
        dialog.setStatus("NEW");
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        Pattern p = dialog.getPattern();
        save(p);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void tblPatternMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPatternMouseClicked
        // TODO add your handling code here:
        int row = tblPattern.convertRowIndexToModel(tblPattern.getSelectedRow());
        if (row >= 0) {
            Pattern pattern = patternTableModel.getPattern(row);
            String patternCode = pattern.getPatternCode();
            String name = pattern.getPatternName();
            lblPatternName.setText(name);
            patternDetailTableModel.setPatternCode(patternCode);
            searchPD(patternCode);
        }

    }//GEN-LAST:event_tblPatternMouseClicked

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observer.selected("control", this);
    }//GEN-LAST:event_formComponentShown

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        int row = tblPattern.convertRowIndexToModel(tblPattern.getSelectedRow());
        if (row >= 0) {
            Pattern p = patternTableModel.getPattern(row);
            dialog = new PatterCreateDialog(Global.parentForm);
            dialog.setInventoryRepo(inventoryRepo);
            dialog.initCombo();
            dialog.setPattern(p);
            dialog.setStatus("EDIT");
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
            Pattern pp = dialog.getPattern();
            save(pp);
        } else {
            JOptionPane.showMessageDialog(this, "Select Pattern.");
        }
    }//GEN-LAST:event_jButton2ActionPerformed
    @Override
    public void delete() {
    }

    @Override
    public void print() {
    }

    @Override
    public void save() {
        JOptionPane.showMessageDialog(this, "Pattern Creation is auto saved.");
    }

    @Override
    public void newForm() {
    }

    @Override
    public void history() {
    }

    @Override
    public void refresh() {
        searchPattern();
    }

    @Override
    public String panelName() {
        return this.getName();
    }

    @Override
    public void filter() {
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblPatternName;
    private javax.swing.JLabel lblRecord;
    private javax.swing.JTable tblPD;
    private javax.swing.JTable tblPattern;
    // End of variables declaration//GEN-END:variables
}
