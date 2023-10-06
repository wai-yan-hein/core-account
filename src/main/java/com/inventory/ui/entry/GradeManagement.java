/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.inventory.ui.entry;

import com.common.DecimalFormatRender;
import com.common.Global;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.common.Util1;
import com.inventory.editor.StockCellEditor;
import com.inventory.editor.StockUnitEditor;
import com.inventory.editor.TraderAutoCompleter;
import com.inventory.ui.common.GradeCriteriaTableModel;
import com.inventory.ui.common.GradeStockTableModel;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.repo.InventoryRepo;
import java.awt.Color;
import java.awt.event.KeyEvent;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

/**
 *
 * @author Lenovo
 */
public class GradeManagement extends javax.swing.JPanel implements SelectionObserver {

    private GradeStockTableModel gradeStockTableModel = new GradeStockTableModel();
    private GradeCriteriaTableModel gradeCriteriaTableModel = new GradeCriteriaTableModel();
    private InventoryRepo inventoryRepo;
    private TraderAutoCompleter traderAutoCompleter;
    private JProgressBar progress;

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    /**
     * Creates new form GradeManagement
     */
    public GradeManagement() {
        initComponents();
    }

    public void initMain() {
        assignDefaultValue();
        initTextBox();
        initCompleter();
        initTableStock();
        initTableCriteria();
    }

    private void initTableStock() {
        gradeStockTableModel.setParent(tblStock);
        gradeStockTableModel.addNewRow();
        tblStock.setModel(gradeStockTableModel);
        tblStock.getTableHeader().setFont(Global.tblHeaderFont);
        tblStock.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblStock.setFont(Global.textFont);
        tblStock.setRowHeight(Global.tblRowHeight);
        tblStock.setShowGrid(true);
        tblStock.setCellSelectionEnabled(true);
        tblStock.setDefaultRenderer(Object.class, new TableCellRender());
        tblStock.setDefaultRenderer(Double.class, new TableCellRender());
        tblStock.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblStock.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblStock.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor(inventoryRepo));
        tblStock.getColumnModel().getColumn(1).setCellEditor(new StockCellEditor(inventoryRepo));
        tblStock.getColumnModel().getColumn(2).setCellEditor(new AutoClearEditor());//total
        tblStock.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());//total
        tblStock.getColumnModel().getColumn(5).setCellEditor(new AutoClearEditor());//total
        inventoryRepo.getStockUnit().doOnSuccess((t) -> {
            tblStock.getColumnModel().getColumn(4).setCellEditor(new StockUnitEditor(t));
            tblStock.getColumnModel().getColumn(6).setCellEditor(new StockUnitEditor(t));
        }).subscribe();
    }

    private void initTableCriteria() {
        tblCriteria.setModel(gradeCriteriaTableModel);
        tblCriteria.getTableHeader().setFont(Global.tblHeaderFont);
        tblCriteria.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCriteria.setFont(Global.textFont);
        tblCriteria.setRowHeight(Global.tblRowHeight);
        tblCriteria.setShowGrid(true);
        tblCriteria.setCellSelectionEnabled(true);
        tblCriteria.setDefaultRenderer(Object.class, new DecimalFormatRender());
        tblCriteria.setDefaultRenderer(Double.class, new DecimalFormatRender());
        tblCriteria.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblCriteria.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void assignDefaultValue() {
        lblCriteria.setForeground(Color.BLUE);
        lblStock.setForeground(Color.BLUE);
        txtVouDate.setDate(Util1.getTodayDate());
        txtVouDate.setDateFormatString(Global.dateFormat);
    }

    private void initTextBox() {
        txtStock.setFormatterFactory(Util1.getDecimalFormat2());
        txtCriteria.setFormatterFactory(Util1.getDecimalFormat2());
        txtPurPrice.setFormatterFactory(Util1.getDecimalFormat2());
        txtStock.setHorizontalAlignment(JTextField.RIGHT);
        txtCriteria.setHorizontalAlignment(JTextField.RIGHT);
        txtPurPrice.setHorizontalAlignment(JTextField.RIGHT);
        txtStock.setFont(Global.amtFont);
        txtCriteria.setFont(Global.amtFont);
        txtPurPrice.setFont(Global.amtFont);
        txtGrade.setFont(Global.amtFont);
    }

    private void initCompleter() {
        traderAutoCompleter = new TraderAutoCompleter(txtTrader, inventoryRepo, null, false, "C");
        traderAutoCompleter.setObserver(this);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txtStock = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        txtCriteria = new javax.swing.JFormattedTextField();
        jLabel7 = new javax.swing.JLabel();
        txtPurPrice = new javax.swing.JFormattedTextField();
        jLabel8 = new javax.swing.JLabel();
        txtGrade = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        txtTrader = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtVouDate = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        txtVouNo = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        lblStock = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblStock = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        lblCriteria = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCriteria = new javax.swing.JTable();

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Total Stock :");

        txtStock.setEditable(false);

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Total Criteria :");

        txtCriteria.setEditable(false);

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Purcahse Price :");

        txtPurPrice.setEditable(false);

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Grade :");

        txtGrade.setEditable(false);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(txtPurPrice, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(txtGrade, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(txtCriteria, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE))
                    .addComponent(txtStock, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtCriteria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtPurPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtGrade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(128, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        txtTrader.setFont(Global.textFont);

        jLabel2.setText("Trader");

        txtVouDate.setFont(Global.textFont);

        jLabel3.setText("Vou No");

        txtRemark.setFont(Global.textFont);

        txtVouNo.setEditable(false);
        txtVouNo.setFont(Global.textFont);

        jLabel1.setText("Date");

        jLabel13.setText("Remark");

        lblStatus.setText("NEW");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTrader))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(12, 12, 12)
                        .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtVouDate, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblStatus)
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtRemark, txtTrader, txtVouDate, txtVouNo});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel3))
                            .addComponent(txtVouDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(0, 6, Short.MAX_VALUE)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtTrader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(9, 9, 9)
                                .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addContainerGap())
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblStock.setFont(Global.lableFont);
        lblStock.setText("Stock");

        jLabel9.setFont(Global.lableFont);
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("0");

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Records :");

        tblStock.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tblStock);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblStock, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStock)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblCriteria.setFont(Global.lableFont);
        lblCriteria.setText("Criteria");

        jLabel11.setFont(Global.lableFont);
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("0");

        jLabel12.setFont(Global.lableFont);
        jLabel12.setText("Records :");

        tblCriteria.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblCriteria);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(lblCriteria, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCriteria)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblCriteria;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblStock;
    private javax.swing.JTable tblCriteria;
    private javax.swing.JTable tblStock;
    private javax.swing.JFormattedTextField txtCriteria;
    private javax.swing.JTextField txtGrade;
    private javax.swing.JFormattedTextField txtPurPrice;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JFormattedTextField txtStock;
    private javax.swing.JTextField txtTrader;
    private com.toedter.calendar.JDateChooser txtVouDate;
    private javax.swing.JTextField txtVouNo;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {
    }
}
