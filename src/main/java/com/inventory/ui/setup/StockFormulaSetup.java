/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.inventory.ui.setup;

import com.acc.dialog.FindDialog;
import com.common.Global;
import com.common.PanelControl;
import com.common.ProUtil;
import com.common.RowHeader;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.editor.StockCellEditor;
import com.inventory.editor.StockCriteriaEditor;
import com.inventory.editor.StockUnitEditor;
import com.inventory.entity.GradeDetail;
import com.inventory.entity.MessageType;
import com.inventory.entity.StockFormula;
import com.inventory.entity.StockFormulaPrice;
import com.inventory.ui.common.GradeDetailTableModel;
import com.inventory.ui.common.StockFormulaPriceTableModel;
import com.inventory.ui.common.StockFormulaQtyTableModel;
import com.inventory.ui.common.StockFormulaTableModel;
import com.inventory.ui.setup.dialog.StockCriteriaSetupDialog;
import com.user.editor.AutoClearEditor;
import com.repo.InventoryRepo;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class StockFormulaSetup extends javax.swing.JPanel implements SelectionObserver, PanelControl {

    private StockCriteriaSetupDialog scDialog;
    private InventoryRepo inventoryRepo;
    private SelectionObserver observer;
    private JProgressBar progress;
    private StockFormulaTableModel stockFormulaTableModel = new StockFormulaTableModel();
    private StockFormulaPriceTableModel stockFormulaPriceTableModel = new StockFormulaPriceTableModel();
    private StockFormulaQtyTableModel stockFormulaQtyTableModel = new StockFormulaQtyTableModel();
    private GradeDetailTableModel gradeDetailTableModel = new GradeDetailTableModel();
    private FindDialog findDialog;

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    /**
     * Creates new form StockFormulaSetup
     */
    public StockFormulaSetup() {
        initComponents();
        actionMapping();
    }

    public void initMain() {
        initTableFormula();
        initTablePrice();
        initTableGrade();
        initTableQty();
        searchFormual();
        initRowHeaderFormula();
        initRowHeaderQty();
        initRowHeaderPrice();
        initRowHeaderGrade();
        initFind();
    }

    private void initFind() {
        findDialog = new FindDialog(Global.parentForm, tblQty);
    }

    private void initRowHeaderFormula() {
        RowHeader header = new RowHeader();
        JList list = header.createRowHeader(tblFormula, 30);
        s1.setRowHeaderView(list);
    }

    private void initRowHeaderQty() {
        RowHeader header = new RowHeader();
        JList list = header.createRowHeader(tblQty, 30);
        s2.setRowHeaderView(list);
    }

    private void initRowHeaderPrice() {
        RowHeader header = new RowHeader();
        JList list = header.createRowHeader(tblPrice, 30);
        s3.setRowHeaderView(list);
    }

    private void initRowHeaderGrade() {
        RowHeader header = new RowHeader();
        JList list = header.createRowHeader(tblGrade, 30);
        s4.setRowHeaderView(list);
    }

    private void actionMapping() {
        String solve = "delete";
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        tblPrice.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, solve);
        tblPrice.getActionMap().put(solve, new DeleteAction("Price"));
        tblGrade.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, solve);
        tblGrade.getActionMap().put(solve, new DeleteAction("Grade"));
        tblFormula.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, solve);
        tblFormula.getActionMap().put(solve, new DeleteAction("Formula"));
    }

    private class DeleteAction extends AbstractAction {

        private String option;

        public DeleteAction(String option) {
            this.option = option;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (option) {
                case "Price" ->
                    deleteTranPrice();
                case "Grade" ->
                    deleteTranGrade();
                case "Formula" ->
                    deleteFormula();
            }
        }
    }

    private void deleteTranPrice() {
        int row = tblPrice.convertRowIndexToModel(tblPrice.getSelectedRow());
        if (row >= 0) {
            if (tblPrice.getCellEditor() != null) {
                tblPrice.getCellEditor().stopCellEditing();
            }
            int yes_no = JOptionPane.showConfirmDialog(this,
                    "Are you sure to delete?", "Criteria Transaction delete.", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (yes_no == 0) {
                StockFormulaPrice d = stockFormulaPriceTableModel.getObject(row);
                if (d != null) {
                    inventoryRepo.delete(d.getKey()).doOnSuccess((t) -> {
                        if (t) {
                            stockFormulaPriceTableModel.delete(row);
                            sendMessage(MessageType.FORMULA_PRICE, "delete.");
                        }
                    }).subscribe();
                }
            }
        }
    }

    private void deleteTranGrade() {
        int row = tblGrade.convertRowIndexToModel(tblGrade.getSelectedRow());
        if (row >= 0) {
            if (tblGrade.getCellEditor() != null) {
                tblGrade.getCellEditor().stopCellEditing();
            }
            int yes_no = JOptionPane.showConfirmDialog(this,
                    "Are you sure to delete?", "Criteria Transaction delete.", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (yes_no == 0) {
                GradeDetail d = gradeDetailTableModel.getObject(row);
                if (d != null) {
                    inventoryRepo.delete(d.getKey()).doOnSuccess((t) -> {
                        if (t) {
                            gradeDetailTableModel.delete(row);
                            sendMessage(MessageType.GRADE_DETAIL, "delete.");
                        }
                    }).subscribe();
                }
            }
        }
    }

    private void deleteFormula() {
        int row = tblFormula.convertRowIndexToModel(tblFormula.getSelectedRow());
        if (row >= 0) {
            if (tblFormula.getCellEditor() != null) {
                tblFormula.getCellEditor().stopCellEditing();
            }
            int yes_no = JOptionPane.showConfirmDialog(this,
                    "Are you sure to delete?", "Formula delete.", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (yes_no == 0) {
                StockFormula d = stockFormulaTableModel.getObject(row);
                if (d != null) {
                    inventoryRepo.delete(d.getKey()).doOnSuccess((t) -> {
                        if (t) {
                            stockFormulaTableModel.delete(row);
                            sendMessage(MessageType.FORMULA, "delete.");
                        }
                    }).subscribe();
                }
            }
        }
    }

    private void sendMessage(String mesType, String mes) {
        inventoryRepo.sendDownloadMessage(mesType, mes)
                .doOnSuccess((t) -> {
                    log.info(t);
                }).subscribe();
    }

    private void stockCriteriaDialog() {
        if (scDialog == null) {
            scDialog = new StockCriteriaSetupDialog(Global.parentForm);
            scDialog.setInventoryRepo(inventoryRepo);
            scDialog.initMain();
            scDialog.setLocationRelativeTo(null);
        }
        scDialog.search();
    }

    private void initTableFormula() {
        stockFormulaTableModel.setTable(tblFormula);
        stockFormulaTableModel.setInventoryRepo(inventoryRepo);
        tblFormula.setCellSelectionEnabled(true);
        tblFormula.setModel(stockFormulaTableModel);
        tblFormula.getTableHeader().setFont(Global.tblHeaderFont);
        tblFormula.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblFormula.setFont(Global.textFont);
        tblFormula.setRowHeight(Global.tblRowHeight);
        tblFormula.setShowGrid(true);
        tblFormula.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblFormula.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblFormula.getColumnModel().getColumn(0).setCellEditor(new AutoClearEditor());
        tblFormula.getColumnModel().getColumn(1).setCellEditor(new AutoClearEditor());
        tblFormula.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblFormula.getColumnModel().getColumn(1).setPreferredWidth(200);
        tblFormula.getColumnModel().getColumn(2).setPreferredWidth(10);
        tblFormula.getSelectionModel().addListSelectionListener((e) -> {
            if (e.getValueIsAdjusting()) {
                searchFormulaPrice();
                searchFormulaQty();
            }
        });
    }

    private void initTablePrice() {
        stockFormulaPriceTableModel.setInventoryRepo(inventoryRepo);
        stockFormulaPriceTableModel.setParent(tblPrice);
        tblPrice.setModel(stockFormulaPriceTableModel);
        tblPrice.getTableHeader().setFont(Global.tblHeaderFont);
        tblPrice.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblPrice.setFont(Global.textFont);
        tblPrice.setRowHeight(Global.tblRowHeight);
        tblPrice.setShowGrid(true);
        tblPrice.setCellSelectionEnabled(true);
        tblPrice.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblPrice.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblPrice.getColumnModel().getColumn(0).setCellEditor(new StockCriteriaEditor(inventoryRepo));
        tblPrice.getColumnModel().getColumn(1).setCellEditor(new StockCriteriaEditor(inventoryRepo));
        tblPrice.getColumnModel().getColumn(2).setCellEditor(new AutoClearEditor());
        tblPrice.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());
        tblPrice.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());
        tblPrice.getColumnModel().getColumn(0).setPreferredWidth(20);//code
        tblPrice.getColumnModel().getColumn(1).setPreferredWidth(100);//name
        tblPrice.getColumnModel().getColumn(2).setPreferredWidth(5);//percent
        tblPrice.getColumnModel().getColumn(3).setPreferredWidth(20);//price
        tblPrice.getColumnModel().getColumn(4).setPreferredWidth(5);//percent
        tblPrice.getSelectionModel().addListSelectionListener((e) -> {
            if (e.getValueIsAdjusting()) {
                searchGradeDetail();
            }
        });
    }

    private void initTableGrade() {
        gradeDetailTableModel.setInventoryRepo(inventoryRepo);
        gradeDetailTableModel.setParent(tblGrade);
        tblGrade.setModel(gradeDetailTableModel);
        tblGrade.getTableHeader().setFont(Global.tblHeaderFont);
        tblGrade.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblGrade.setFont(Global.textFont);
        tblGrade.setRowHeight(Global.tblRowHeight);
        tblGrade.setShowGrid(true);
        tblGrade.setCellSelectionEnabled(true);
        tblGrade.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblGrade.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblGrade.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor(inventoryRepo, ProUtil.isSSContain()));
        tblGrade.getColumnModel().getColumn(1).setCellEditor(new AutoClearEditor());
        tblGrade.getColumnModel().getColumn(2).setCellEditor(new AutoClearEditor());
        tblGrade.getColumnModel().getColumn(0).setPreferredWidth(200);
        tblGrade.getColumnModel().getColumn(1).setPreferredWidth(50);
        tblGrade.getColumnModel().getColumn(2).setPreferredWidth(50);
    }

    private void initTableQty() {
        stockFormulaQtyTableModel.setInventoryRepo(inventoryRepo);
        stockFormulaQtyTableModel.setParent(tblQty);
        tblQty.setModel(stockFormulaQtyTableModel);
        tblQty.getTableHeader().setFont(Global.tblHeaderFont);
        tblQty.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblQty.setFont(Global.textFont);
        tblQty.setRowHeight(Global.tblRowHeight);
        tblQty.setShowGrid(true);
        tblQty.setCellSelectionEnabled(true);
        tblQty.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblQty.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblQty.getColumnModel().getColumn(0).setCellEditor(new StockCriteriaEditor(inventoryRepo));
        tblQty.getColumnModel().getColumn(1).setCellEditor(new StockCriteriaEditor(inventoryRepo));
        tblQty.getColumnModel().getColumn(2).setCellEditor(new AutoClearEditor());
        tblQty.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());
        inventoryRepo.getStockUnit().doOnSuccess((t) -> {
            tblQty.getColumnModel().getColumn(4).setCellEditor(new StockUnitEditor(t));
        }).subscribe();
        tblQty.getColumnModel().getColumn(5).setCellEditor(new AutoClearEditor());
        tblQty.getColumnModel().getColumn(0).setPreferredWidth(20);//code
        tblQty.getColumnModel().getColumn(1).setPreferredWidth(100);//name
        tblQty.getColumnModel().getColumn(2).setPreferredWidth(5);//percent
        tblQty.getColumnModel().getColumn(3).setPreferredWidth(20);//qty
        tblQty.getColumnModel().getColumn(4).setPreferredWidth(20);//unit
        tblQty.getColumnModel().getColumn(5).setPreferredWidth(5);//percent
    }

    private void searchFormual() {
        progress.setIndeterminate(true);
        inventoryRepo.getStockFormula().doOnSuccess((t) -> {
            stockFormulaTableModel.setListDetail(t);
        }).doOnError((e) -> {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }).doOnTerminate(() -> {
            stockFormulaTableModel.addNewRow();
            progress.setIndeterminate(false);
        }).subscribe();
    }

    private void searchFormulaPrice() {
        int row = tblFormula.convertRowIndexToModel(tblFormula.getSelectedRow());
        if (row >= 0) {
            StockFormula f = stockFormulaTableModel.getObject(row);
            String formulaCode = f.getKey() == null ? null : f.getKey().getFormulaCode();
            if (!Util1.isNullOrEmpty(formulaCode)) {
                inventoryRepo.getStockFormulaPrice(formulaCode).doOnSuccess((t) -> {
                    stockFormulaPriceTableModel.setListDetail(t);
                    stockFormulaPriceTableModel.setFormulaCode(formulaCode);
                    stockFormulaPriceTableModel.addNewRow();
                    gradeDetailTableModel.clear();
                    focusOnTablePrice();
                }).subscribe();
            } else {
                stockFormulaPriceTableModel.clear();
            }
        }
    }

    private void searchFormulaQty() {
        int row = tblFormula.convertRowIndexToModel(tblFormula.getSelectedRow());
        if (row >= 0) {
            StockFormula f = stockFormulaTableModel.getObject(row);
            String formulaCode = f.getKey() == null ? null : f.getKey().getFormulaCode();
            if (!Util1.isNullOrEmpty(formulaCode)) {
                inventoryRepo.getStockFormulaQty(formulaCode).doOnSuccess((t) -> {
                    stockFormulaQtyTableModel.setListDetail(t);
                    stockFormulaQtyTableModel.setFormulaCode(formulaCode);
                    stockFormulaQtyTableModel.addNewRow();
                }).subscribe();
            } else {
                stockFormulaQtyTableModel.clear();
            }
        }
    }

    private void searchGradeDetail() {
        int row = tblPrice.convertRowIndexToModel(tblPrice.getSelectedRow());
        if (row >= 0) {
            StockFormulaPrice f = stockFormulaPriceTableModel.getObject(row);
            String formulaCode = f.getKey() == null ? null : f.getKey().getFormulaCode();
            String criteraCode = f.getCriteriaCode();
            if (!Util1.isNullOrEmpty(formulaCode) && !Util1.isNullOrEmpty(criteraCode)) {
                inventoryRepo.getGradeDetail(formulaCode, criteraCode).doOnSuccess((t) -> {
                    gradeDetailTableModel.setListDetail(t);
                    gradeDetailTableModel.setFormulaCode(formulaCode);
                    gradeDetailTableModel.setCriteriaCode(criteraCode);
                    gradeDetailTableModel.addNewRow();
                    focusOnTableGrade();
                }).subscribe();
            } else {
                gradeDetailTableModel.clear();
            }
        }
    }

    private void focusOnTablePrice() {
        int rc = tblPrice.getRowCount();
        if (rc > 1) {
            tblPrice.setRowSelectionInterval(rc - 1, rc - 1);
            tblPrice.setColumnSelectionInterval(0, 0);
            tblPrice.requestFocus();
        } else {
            tblPrice.requestFocus();
        }
    }

    private void focusOnTableGrade() {
        int rc = tblGrade.getRowCount();
        if (rc > 1) {
            tblGrade.setRowSelectionInterval(rc - 1, rc - 1);
            tblGrade.setColumnSelectionInterval(0, 0);
            tblGrade.requestFocus();
        } else {
            tblGrade.requestFocus();
        }
    }

    private void observeMain() {
        observer.selected("control", this);
        observer.selected("save", false);
        observer.selected("print", false);
        observer.selected("history", false);
        observer.selected("delete", false);
        observer.selected("refresh", true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        s4 = new javax.swing.JScrollPane();
        tblGrade = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        s3 = new javax.swing.JScrollPane();
        tblPrice = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        s1 = new javax.swing.JScrollPane();
        tblFormula = new javax.swing.JTable();
        s2 = new javax.swing.JScrollPane();
        tblQty = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel2.setFont(Global.menuFont);
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Formula List");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tblGrade.setModel(new javax.swing.table.DefaultTableModel(
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
        s4.setViewportView(tblGrade);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel3.setFont(Global.menuFont);
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Grade");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tblPrice.setModel(new javax.swing.table.DefaultTableModel(
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
        s3.setViewportView(tblPrice);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jButton3.setBackground(Global.selectionColor);
        jButton3.setFont(Global.lableFont);
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("New Criteria");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel1.setFont(Global.menuFont);
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Formula Price");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(jLabel1))
                .addContainerGap())
        );

        tblFormula.setModel(new javax.swing.table.DefaultTableModel(
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
        s1.setViewportView(tblFormula);

        tblQty.setModel(new javax.swing.table.DefaultTableModel(
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
        tblQty.setName("tblQty"); // NOI18N
        s2.setViewportView(tblQty);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel4.setFont(Global.menuFont);
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Formula Qty");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(s1, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(s2)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(s4, javax.swing.GroupLayout.DEFAULT_SIZE, 598, Short.MAX_VALUE)
                    .addComponent(s3)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(s2, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(s3, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(s4, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(s1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        stockCriteriaDialog();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observeMain();
    }//GEN-LAST:event_formComponentShown


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane s1;
    private javax.swing.JScrollPane s2;
    private javax.swing.JScrollPane s3;
    private javax.swing.JScrollPane s4;
    private javax.swing.JTable tblFormula;
    private javax.swing.JTable tblGrade;
    private javax.swing.JTable tblPrice;
    private javax.swing.JTable tblQty;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {
    }

    @Override
    public void save() {
    }

    @Override
    public void delete() {
    }

    @Override
    public void newForm() {
    }

    @Override
    public void history() {
    }

    @Override
    public void print() {
    }

    @Override
    public void refresh() {
        searchFormual();
    }

    @Override
    public void filter() {
        findDialog.setVisible(!findDialog.isVisible());
    }

    @Override
    public String panelName() {
        return this.getName();
    }
}
