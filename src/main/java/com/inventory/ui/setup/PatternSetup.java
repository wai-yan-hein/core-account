/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.inventory.ui.setup;

import com.acc.dialog.FindDialog;
import com.common.ComponentUtil;
import com.common.Global;
import com.common.PanelControl;
import com.common.ProUtil;
import com.common.ReportFilter;
import com.common.RowHeader;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.common.Util1;
import com.inventory.editor.BrandAutoCompleter;
import com.inventory.editor.CategoryAutoCompleter;
import com.inventory.editor.LocationCellEditor;
import com.inventory.editor.PriceEditor;
import com.inventory.editor.StockAutoCompleter;
import com.inventory.editor.StockCellEditor;
import com.inventory.editor.StockTypeAutoCompleter;
import com.inventory.entity.Pattern;
import com.inventory.entity.Stock;
import com.repo.InventoryRepo;
import com.inventory.ui.common.PatternTableModel;
import com.inventory.ui.common.StockCompleterTableModel;
import com.user.editor.AutoClearEditor;
import com.inventory.editor.StockUnitEditor;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import lombok.Setter;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Component
public class PatternSetup extends javax.swing.JPanel implements PanelControl, SelectionObserver {

    private final StockCompleterTableModel stockTableModel = new StockCompleterTableModel();
    private final PatternTableModel patternTableModel = new PatternTableModel();
    @Setter
    private SelectionObserver observer;
    @Setter
    private JProgressBar progress;
    private StockTypeAutoCompleter typeAutoCompleter;
    private BrandAutoCompleter brandAutoCompleter;
    private CategoryAutoCompleter categoryAutoCompleter;
    private StockAutoCompleter stockAutoCompleter;
    @Setter
    private InventoryRepo inventoryRepo;
    private FindDialog findDialog;
    private int row = 0;

    /**
     * Creates new form PatternSetup
     */
    public PatternSetup() {
        initComponents();
        initFocus();
    }

    public void initMain() {
        initCombo();
        initTableStock();
        initTablePD();
        initRowHeader();
        initFind();
        actionMapping();
    }

    private void initFind() {
        findDialog = new FindDialog(Global.parentForm, tblStock, tblPD);
    }

    private void initRowHeader() {
        RowHeader header = new RowHeader();
        JList list = header.createRowHeader(tblPD, 30);
        s2.setRowHeaderView(list);
    }

    private void initFocus() {
        ComponentUtil.addFocusListener(this);
        ComponentUtil.setTextProperty(this);
    }

    private void initCombo() {
        typeAutoCompleter = new StockTypeAutoCompleter(txtGroup, null, true);
        typeAutoCompleter.setObserver(this);
        inventoryRepo.getStockType().doOnSuccess((t) -> {
            typeAutoCompleter.setListStockType(t);
        }).subscribe();
        categoryAutoCompleter = new CategoryAutoCompleter(txtCat, null, true);
        categoryAutoCompleter.setObserver(this);
        inventoryRepo.getCategory().doOnSuccess((t) -> {
            categoryAutoCompleter.setListCategory(t);
        }).subscribe();
        brandAutoCompleter = new BrandAutoCompleter(txtBrand, null, true);
        brandAutoCompleter.setObserver(this);
        inventoryRepo.getStockBrand().doOnSuccess((t) -> {
            brandAutoCompleter.setListStockBrand(t);
        }).subscribe();
        stockAutoCompleter = new StockAutoCompleter(txtStock, inventoryRepo, null, true, ProUtil.isSSContain());
        stockAutoCompleter.setObserver(this);
    }

    private void focusOnPD() {
        int r = tblPD.getRowCount();
        if (r >= 1) {
            tblPD.setColumnSelectionInterval(0, 0);
            tblPD.setRowSelectionInterval(r - 1, r - 1);
        }
        tblPD.requestFocusInWindow();
    }

    private void actionMapping() {
        String solve = "delete";
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        tblPD.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, solve);
        tblPD.getActionMap().put(solve, new DeleteAction());

    }

    private class DeleteAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            deleteTran();
        }
    }

    private void initTableStock() {
        tblStock.setModel(stockTableModel);
        tblStock.getTableHeader().setFont(Global.tblHeaderFont);
        tblStock.setRowHeight(Global.tblRowHeight);
        tblStock.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblStock.setFont(Global.textFont);
        tblStock.getColumnModel().getColumn(0).setPreferredWidth(20);
        tblStock.getColumnModel().getColumn(1).setPreferredWidth(200);
        tblStock.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblStock.getColumnModel().getColumn(3).setPreferredWidth(100);
        tblStock.getColumnModel().getColumn(4).setPreferredWidth(100);
        tblStock.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblStock.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblStock.setDefaultRenderer(Object.class, new TableCellRender());
        tblStock.setDefaultRenderer(Boolean.class, new TableCellRender());
        tblStock.getSelectionModel().addListSelectionListener((e) -> {
            row = tblStock.convertRowIndexToModel(tblStock.getSelectedRow());
        });
        searchStock();
    }

    private String getBrand() {
        return brandAutoCompleter == null ? "-" : brandAutoCompleter.getBrand().getKey().getBrandCode();
    }

    private String getType() {
        return typeAutoCompleter == null ? "-" : typeAutoCompleter.getStockType().getKey().getStockTypeCode();
    }

    private String getStock() {
        return stockAutoCompleter == null ? "-" : stockAutoCompleter.getStock().getKey().getStockCode();
    }

    private String getCategory() {
        return categoryAutoCompleter == null ? "-" : categoryAutoCompleter.getCategory().getKey().getCatCode();
    }

    private void searchStock() {
        ReportFilter filter = new ReportFilter(Global.macId, Global.compCode, Global.deptId);
        filter.setBrandCode(getBrand());
        filter.setCatCode(getCategory());
        filter.setStockTypeCode(getType());
        filter.setStockCode(getStock());
        filter.setDeleted(false);
        filter.setActive(true);
        stockTableModel.clear();
        inventoryRepo.searchStock(filter).doOnNext(stockTableModel::addStock)
                .doOnComplete(() -> {
                    progress.setIndeterminate(false);
                    ComponentUtil.scrollTable(tblStock, row, 0);
                }).subscribe();
    }

    private void initTablePD() {
        inventoryRepo.getDefaultLocation().doOnSuccess((t) -> {
            patternTableModel.setLocation(t);
        }).subscribe();
        patternTableModel.setTable(tblPD);
        patternTableModel.setPanel(this);
        patternTableModel.setInventoryRepo(inventoryRepo);
        patternTableModel.setObserver(this);
        tblPD.setCellSelectionEnabled(true);
        tblPD.setModel(patternTableModel);
        tblPD.getTableHeader().setFont(Global.tblHeaderFont);
        tblPD.setRowHeight(Global.tblRowHeight);
        tblPD.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblPD.setFont(Global.textFont);
        tblPD.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor(inventoryRepo, ProUtil.isSSContain()));
        tblPD.getColumnModel().getColumn(1).setCellEditor(new StockCellEditor(inventoryRepo, ProUtil.isSSContain()));
        inventoryRepo.getLocation().doOnSuccess((t) -> {
            tblPD.getColumnModel().getColumn(2).setCellEditor(new LocationCellEditor(t));
        }).subscribe();
        tblPD.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());
        inventoryRepo.getStockUnit().doOnSuccess((t) -> {
            tblPD.getColumnModel().getColumn(4).setCellEditor(new StockUnitEditor(t));
        }).subscribe();
        tblPD.getColumnModel().getColumn(5).setCellEditor(new AutoClearEditor());
        inventoryRepo.getPriceOption("Purchase").doOnSuccess((t) -> {
            tblPD.getColumnModel().getColumn(7).setCellEditor(new PriceEditor(t));
        }).subscribe();
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

    private void select() {
        if (row >= 0) {
            progress.setIndeterminate(true);
            Stock s = stockTableModel.getStock(row);
            String stockCode = s.getKey().getStockCode();
            lblStockName.setText(s.getStockName());
            chkEx.setSelected(s.isExplode());
            inventoryRepo.getPattern(stockCode, null).doOnSuccess((t) -> {
                lblRec.setText("Records : " + t.size());
                patternTableModel.setListPattern(t);
                patternTableModel.addNewRow();
                patternTableModel.setStockCode(stockCode);
            }).doOnTerminate(() -> {
                calPrice();
                focusOnPD();
                progress.setIndeterminate(false);
            }).subscribe();

        }
    }

    private void deleteTran() {
        if (row >= 0) {
            int y = JOptionPane.showConfirmDialog(this, "Are you sure to delete?");
            if (y == JOptionPane.YES_OPTION) {
                Pattern p = patternTableModel.getPattern(row);
                inventoryRepo.delete(p).subscribe((t) -> {
                    if (t) {
                        patternTableModel.remove(row);
                        focusOnPD();
                    }
                }, (e) -> {
                    JOptionPane.showMessageDialog(this, e.getMessage());
                }
                );
            }
        }
    }

    private void calPrice() {
        List<Pattern> list = patternTableModel.getListPattern();
        double amt = list.stream().mapToDouble((p) -> Util1.getDouble(p.getAmount())).sum();
        txtPrice.setValue(amt);

    }

    private void observeMain() {
        observer.selected("control", this);
        observer.selected("save", true);
        observer.selected("print", false);
        observer.selected("history", false);
        observer.selected("delete", true);
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

        jScrollPane1 = new javax.swing.JScrollPane();
        tblStock = new javax.swing.JTable();
        jSeparator1 = new javax.swing.JSeparator();
        s2 = new javax.swing.JScrollPane();
        tblPD = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtGroup = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtCat = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtBrand = new javax.swing.JTextField();
        txtStock = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        lblStockName = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        txtPrice = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        lblRec = new javax.swing.JLabel();
        chkEx = new javax.swing.JCheckBox();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

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
        tblStock.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblStockMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblStock);

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
        s2.setViewportView(tblPD);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Group");

        txtGroup.setFont(Global.textFont);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Category");

        txtCat.setFont(Global.textFont);

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Brand");

        txtBrand.setFont(Global.textFont);

        txtStock.setFont(Global.textFont);

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Stock");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtGroup)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtCat)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtBrand)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtStock)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtGroup)
                    .addComponent(txtCat)
                    .addComponent(txtBrand)
                    .addComponent(txtStock)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblStockName.setFont(Global.menuFont);
        lblStockName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStockName.setText("Stock Name");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblStockName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblStockName, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        txtPrice.setEditable(false);
        txtPrice.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPrice.setFont(Global.amtFont);

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Cost Price");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(369, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblRec.setFont(Global.lableFont);
        lblRec.setText("Records");

        chkEx.setFont(Global.lableFont);
        chkEx.setText("Explode");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkEx)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblRec)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblRec, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkEx))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(s2, javax.swing.GroupLayout.DEFAULT_SIZE, 565, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(s2, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void tblStockMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblStockMouseClicked
        // TODO add your handling code here:
        select();

    }//GEN-LAST:event_tblStockMouseClicked

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observeMain();
    }//GEN-LAST:event_formComponentShown
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
        searchStock();
    }

    @Override
    public String panelName() {
        return this.getName();
    }

    @Override
    public void filter() {
        findDialog.setVisible(!findDialog.isVisible());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkEx;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblRec;
    private javax.swing.JLabel lblStockName;
    private javax.swing.JScrollPane s2;
    private javax.swing.JTable tblPD;
    private javax.swing.JTable tblStock;
    private javax.swing.JTextField txtBrand;
    private javax.swing.JTextField txtCat;
    private javax.swing.JTextField txtGroup;
    private javax.swing.JFormattedTextField txtPrice;
    private javax.swing.JTextField txtStock;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {
        if (source != null) {
            String select = source.toString();
            if (select.equals("CAL_PRICE")) {
                calPrice();
            } else {
                searchStock();
            }
        }
    }
}
