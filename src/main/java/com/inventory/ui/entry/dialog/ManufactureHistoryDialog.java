/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.inventory.ui.entry.dialog;

import com.common.DecimalFormatRender;
import com.common.FilterObject;
import com.common.Global;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.common.Util1;
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.editor.StockAutoCompleter;
import com.inventory.editor.VouStatusAutoCompleter;
import com.inventory.model.Location;
import com.inventory.model.Stock;
import com.inventory.model.VouStatus;
import com.inventory.ui.common.InventoryRepo;
import com.inventory.ui.common.ProcessHisTableModel;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

/**
 *
 * @author DELL
 */
public class ManufactureHistoryDialog extends javax.swing.JDialog implements SelectionObserver {

    private final ProcessHisTableModel processHisTableModel = new ProcessHisTableModel();
    private int selectRow = -1;
    private InventoryRepo inventoryRepo;
    private StockAutoCompleter stockAutoCompleter;
    private LocationAutoCompleter locationAutoCompleter;
    private VouStatusAutoCompleter vouStatusAutoCompleter;
    private SelectionObserver observer;

    public SelectionObserver getObserver() {
        return observer;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public InventoryRepo getInventoryRepo() {
        return inventoryRepo;
    }

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }
    private final FocusAdapter fa = new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            if (e.getSource() instanceof JTextField txt) {
                txt.selectAll();
            }
        }
    };

    /**
     * Creates new form ManufactureHistoryDialog
     */
    public ManufactureHistoryDialog() {
        super(Global.parentForm, true);
        initComponents();
        initFocusAdapter();
    }

    private void initFocusAdapter() {
        txtProNo.addFocusListener(fa);
        txtRemark.addFocusListener(fa);
        txtVouNo.addFocusListener(fa);
        txtStock.addFocusListener(fa);
        txtPT.addFocusListener(fa);
        txtLoc.addFocusListener(fa);

    }

    public void initMain() {
        initDate();
        initCompleter();
        initTblProcess();
        searchProcess();
    }

    private void clear() {
        initDate();
        txtProNo.setText(null);
        txtRemark.setText(null);
        txtVouNo.setText(null);
        vouStatusAutoCompleter.setVoucher(new VouStatus("-", "All"));
        locationAutoCompleter.setLocation(new Location("-", "All"));
        stockAutoCompleter.setStock(new Stock("-", "All"));
        chkDel.setSelected(false);
        chkFinish.setSelected(false);

    }

    private void initCompleter() {
        locationAutoCompleter = new LocationAutoCompleter(txtLoc, inventoryRepo.getLocation(), null, true, false);
        locationAutoCompleter.setObserver(this);
        stockAutoCompleter = new StockAutoCompleter(txtStock, inventoryRepo, null, true);
        stockAutoCompleter.setObserver(this);
        vouStatusAutoCompleter = new VouStatusAutoCompleter(txtPT, inventoryRepo, null, true);
        vouStatusAutoCompleter.setObserver(this);
    }

    private void initDate() {
        txtFromDate.setDate(Util1.getTodayDate());
        txtToDate.setDate(Util1.getTodayDate());
    }

    private void initTblProcess() {
        tblProcess.setModel(processHisTableModel);
        tblProcess.getTableHeader().setFont(Global.tblHeaderFont);
        tblProcess.getColumnModel().getColumn(0).setPreferredWidth(30);//date
        tblProcess.getColumnModel().getColumn(1).setPreferredWidth(20);//code
        tblProcess.getColumnModel().getColumn(2).setPreferredWidth(200);//name
        tblProcess.getColumnModel().getColumn(3).setPreferredWidth(40);//type
        tblProcess.setDefaultRenderer(Object.class, new DecimalFormatRender());
        tblProcess.setDefaultRenderer(Float.class, new DecimalFormatRender());
        tblProcess.setFont(Global.textFont);
        tblProcess.setRowHeight(Global.tblRowHeight);
        tblProcess.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblProcess.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblProcess.setDefaultRenderer(Object.class, new TableCellRender());
        tblProcess.setDefaultRenderer(Float.class, new TableCellRender());
    }

    private void select() {
        if (tblProcess.getSelectedRow() >= 0) {
            selectRow = tblProcess.convertRowIndexToModel(tblProcess.getSelectedRow());
            if (observer != null) {
                observer.selected("Selected", processHisTableModel.getObject(selectRow).getKey());
                this.dispose();
            }
        }
    }

    private void searchProcess() {
        FilterObject f = new FilterObject(Global.compCode, Global.deptId);
        f.setFromDate(Util1.toDateStr(txtFromDate.getDate(), "yyyy-MM-dd"));
        f.setToDate(Util1.toDateStr(txtToDate.getDate(), "yyyy-MM-dd"));
        f.setProcessNo(txtProNo.getText());
        f.setRemark(txtRemark.getText());
        f.setVouNo(txtVouNo.getText());
        f.setStockCode(stockAutoCompleter.getStock().getKey().getStockCode());
        f.setVouStatus(vouStatusAutoCompleter.getVouStatus().getKey().getCode());
        f.setLocCode(locationAutoCompleter.getLocation().getKey().getLocCode());
        f.setFinished(chkFinish.isSelected());
        f.setDeleted(chkDel.isSelected());
        processHisTableModel.setListDetail(inventoryRepo.getProcess(f));
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
        jLabel18 = new javax.swing.JLabel();
        txtFromDate = new com.toedter.calendar.JDateChooser();
        jLabel1 = new javax.swing.JLabel();
        txtToDate = new com.toedter.calendar.JDateChooser();
        jLabel19 = new javax.swing.JLabel();
        txtProNo = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        txtVouNo = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtStock = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        txtPT = new javax.swing.JTextField();
        txtLoc = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        chkFinish = new javax.swing.JCheckBox();
        chkDel = new javax.swing.JCheckBox();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblProcess = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel18.setFont(Global.lableFont);
        jLabel18.setText("Start Date");

        txtFromDate.setDateFormatString("dd/MM/yyyy");
        txtFromDate.setFont(Global.textFont);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Process No");

        txtToDate.setDateFormatString("dd/MM/yyyy");
        txtToDate.setFont(Global.textFont);

        jLabel19.setFont(Global.lableFont);
        jLabel19.setText("End Date");

        txtProNo.setFont(Global.textFont);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Remark");

        txtRemark.setFont(Global.textFont);

        jLabel20.setFont(Global.lableFont);
        jLabel20.setText("Vou No");

        txtVouNo.setFont(Global.textFont);

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Stock");

        txtStock.setFont(Global.textFont);

        jLabel21.setFont(Global.lableFont);
        jLabel21.setText("Process Type");

        txtPT.setFont(Global.textFont);

        txtLoc.setFont(Global.textFont);

        jLabel22.setFont(Global.lableFont);
        jLabel22.setText("Location");

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Status");

        chkFinish.setText("Finished");

        chkDel.setText("Deleted");

        jButton4.setFont(Global.lableFont);
        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/search.png"))); // NOI18N
        jButton4.setText("Search");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setFont(Global.lableFont);
        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/new_copy_18px.png"))); // NOI18N
        jButton5.setText("Clear");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel22, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel21))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtRemark)
                            .addComponent(txtStock)
                            .addComponent(txtVouNo)
                            .addComponent(txtPT)
                            .addComponent(txtLoc)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(chkFinish)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkDel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton4))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFromDate, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtToDate, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtProNo)))
                .addContainerGap())
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel18, jLabel2, jLabel20, jLabel21, jLabel22, jLabel3, jLabel4});

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtFromDate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtProNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(txtPT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(txtLoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4)
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(chkDel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(chkFinish, javax.swing.GroupLayout.Alignment.LEADING))
                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(328, Short.MAX_VALUE))
        );

        tblProcess.setModel(new javax.swing.table.DefaultTableModel(
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
        tblProcess.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblProcessMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblProcess);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 850, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 558, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        searchProcess();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void tblProcessMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblProcessMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            select();
        }
    }//GEN-LAST:event_tblProcessMouseClicked

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        clear();
    }//GEN-LAST:event_jButton5ActionPerformed

    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkDel;
    private javax.swing.JCheckBox chkFinish;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblProcess;
    private com.toedter.calendar.JDateChooser txtFromDate;
    private javax.swing.JTextField txtLoc;
    private javax.swing.JTextField txtPT;
    private javax.swing.JTextField txtProNo;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JTextField txtStock;
    private com.toedter.calendar.JDateChooser txtToDate;
    private javax.swing.JTextField txtVouNo;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
