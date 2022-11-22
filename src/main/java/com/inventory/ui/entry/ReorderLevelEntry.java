/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.inventory.ui.entry;

import com.common.ColorCellRender;
import com.common.Global;
import com.common.PanelControl;
import com.common.ProUtil;
import com.common.ReportFilter;
import com.common.RightCellRender;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.editor.BrandAutoCompleter;
import com.inventory.editor.CategoryAutoCompleter;
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.editor.StockAutoCompleter;
import com.inventory.editor.StockCellEditor;
import com.inventory.editor.StockTypeAutoCompleter;
import com.inventory.model.ReorderLevel;
import com.inventory.model.StockUnit;
import com.inventory.ui.common.InventoryRepo;
import com.inventory.ui.common.ReorderTableModel;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.inventory.ui.setup.dialog.common.StockUnitEditor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 *
 * @author Lenovo
 */
@Component
public class ReorderLevelEntry extends javax.swing.JPanel implements SelectionObserver, PanelControl {

    private final ButtonGroup g = new ButtonGroup();
    @Autowired
    private WebClient inventoryApi;
    @Autowired
    private InventoryRepo inventoryRepo;
    private final ReorderTableModel reorderTableModel = new ReorderTableModel();
    private StockTypeAutoCompleter typeAutoCompleter;
    private CategoryAutoCompleter categoryAutoCompleter;
    private BrandAutoCompleter brandAutoCompleter;
    private StockAutoCompleter stockAutoCompleter;
    private LocationAutoCompleter locationAutoCompleter;
    private JProgressBar progress;
    private SelectionObserver observer;
    private List<StockUnit> listStockUnit = new ArrayList<>();
    private TableRowSorter<TableModel> sorter;
    private final FocusAdapter fa = new FocusAdapter() {
        @Override
        public void focusLost(FocusEvent e) {
        }

        @Override
        public void focusGained(FocusEvent e) {
            JTextField jtf = (JTextField) e.getSource();
            jtf.selectAll();
        }

    };

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
     * Creates new form ReorderLevel
     */
    public ReorderLevelEntry() {
        initComponents();
        chkGroup();
        initFoucsAdapter();
    }

    private void initFoucsAdapter() {
        txtStock.addFocusListener(fa);
        txtLoc.addFocusListener(fa);
        txtGroup.addFocusListener(fa);
        txtCat.addFocusListener(fa);
        txtBrand.addFocusListener(fa);
    }

    public void initMain() {
        initCombo();
        initTable();
    }

    private void chkGroup() {
        g.add(chk1);
        g.add(chk2);
        g.add(chk3);
        g.add(chk4);
        g.add(chk5);
        chk1.setActionCommand(chk1.getText());
        chk2.setActionCommand(chk2.getText());
        chk3.setActionCommand(chk3.getText());
        chk4.setActionCommand(chk4.getText());
        chk5.setActionCommand(chk5.getText());
        chk1.addActionListener(a);
        chk2.addActionListener(a);
        chk3.addActionListener(a);
        chk4.addActionListener(a);
        chk5.addActionListener(a);

    }

    private void filterStatus() {
        sorter.setRowFilter(startsWithFilter);

    }
    private ActionListener a = (ActionEvent e) -> {
        if (e.getActionCommand().equals("All")) {
            sorter.setRowFilter(null);
        } else {
            filterStatus();
        }
        calQty();
    };

    private void initCombo() {
        typeAutoCompleter = new StockTypeAutoCompleter(txtGroup, inventoryRepo.getStockType(), null, true, false);
        typeAutoCompleter.setObserver(this);
        categoryAutoCompleter = new CategoryAutoCompleter(txtCat, inventoryRepo.getCategory(), null, true, false);
        categoryAutoCompleter.setObserver(this);
        brandAutoCompleter = new BrandAutoCompleter(txtBrand, inventoryRepo.getStockBrand(), null, true, false);
        brandAutoCompleter.setObserver(this);
        stockAutoCompleter = new StockAutoCompleter(txtStock, inventoryRepo, null, true);
        stockAutoCompleter.setObserver(this);
        locationAutoCompleter = new LocationAutoCompleter(txtLoc, inventoryRepo.getLocation(), null, true, true);
        locationAutoCompleter.setObserver(this);
    }

    private void initTable() {
        listStockUnit = inventoryRepo.getStockUnit();
        reorderTableModel.setTable(tblOrder);
        reorderTableModel.setInventoryRepo(inventoryRepo);
        tblOrder.setModel(reorderTableModel);
        tblOrder.setCellSelectionEnabled(true);
        tblOrder.getTableHeader().setFont(Global.tblHeaderFont);
        tblOrder.setRowHeight(Global.tblRowHeight);
        tblOrder.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblOrder.setFont(Global.textFont);
        tblOrder.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor(inventoryRepo));
        tblOrder.getColumnModel().getColumn(1).setCellEditor(new StockCellEditor(inventoryRepo));
        tblOrder.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());
        tblOrder.getColumnModel().getColumn(4).setCellEditor(new StockUnitEditor(listStockUnit));
        tblOrder.getColumnModel().getColumn(5).setCellEditor(new AutoClearEditor());
        tblOrder.getColumnModel().getColumn(6).setCellEditor(new StockUnitEditor(listStockUnit));
        tblOrder.getColumnModel().getColumn(0).setPreferredWidth(30);
        tblOrder.getColumnModel().getColumn(1).setPreferredWidth(200);
        tblOrder.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblOrder.getColumnModel().getColumn(3).setPreferredWidth(50);
        tblOrder.getColumnModel().getColumn(4).setPreferredWidth(50);
        tblOrder.getColumnModel().getColumn(5).setPreferredWidth(50);
        tblOrder.getColumnModel().getColumn(6).setPreferredWidth(50);
        tblOrder.getColumnModel().getColumn(7).setCellRenderer(new RightCellRender());
        tblOrder.getColumnModel().getColumn(9).setCellRenderer(new ColorCellRender());
        tblOrder.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblOrder.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sorter = new TableRowSorter<>(tblOrder.getModel());
        tblOrder.setRowSorter(sorter);
        getReorderLevel();
    }
    private final RowFilter<Object, Object> startsWithFilter = new RowFilter<Object, Object>() {
        @Override
        public boolean include(RowFilter.Entry<? extends Object, ? extends Object> entry) {
            String tmp1 = entry.getStringValue(9).toUpperCase().replace(" ", "");
            String text = g.getSelection().getActionCommand().toUpperCase();
            return tmp1.startsWith(text);

        }
    };

    private void getReorderLevel() {
        progress.setIndeterminate(true);
        ReportFilter filter = new ReportFilter(Global.macId, Global.compCode, Global.deptId);
        filter.setBrandCode(brandAutoCompleter.getBrand().getKey().getBrandCode());
        filter.setCatCode(categoryAutoCompleter.getCategory().getKey().getCatCode());
        filter.setStockTypeCode(typeAutoCompleter.getStockType().getKey().getStockTypeCode());
        filter.setStockCode(stockAutoCompleter.getStock().getKey().getStockCode());
        filter.setLocCode(locationAutoCompleter.getLocation().getKey().getLocCode());
        filter.setCalSale(Util1.getBoolean(ProUtil.getProperty("disable.calculate.sale.stock")));
        filter.setCalPur(Util1.getBoolean(ProUtil.getProperty("disable.calculate.purchase.stock")));
        filter.setCalRI(Util1.getBoolean(ProUtil.getProperty("disable.calculate.returin.stock")));
        filter.setCalRO(Util1.getBoolean(ProUtil.getProperty("disable.calculate.retunout.stock")));
        Mono<ResponseEntity<List<ReorderLevel>>> result = inventoryApi
                .post()
                .uri("/report/get-reorder-level")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .toEntityList(ReorderLevel.class);
        result.subscribe((t) -> {
            reorderTableModel.setListPattern(t.getBody());
            calQty();
            progress.setIndeterminate(false);
        }, (e) -> {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
            progress.setIndeterminate(false);
        });
    }

    private void calQty() {
        int lowMin = 0;
        int overMin = 0;
        int lowMax = 0;
        int overMax = 0;
        int row = tblOrder.getRowCount();
        for (int i = 0; i < row; i++) {
            String status = tblOrder.getValueAt(i, 9).toString();
            switch (status) {
                case "Below-Min" ->
                    lowMin += 1;
                case "Over-Min" ->
                    overMin += 1;
                case "Below-Max" ->
                    lowMax += 1;
                case "Over-Max" ->
                    overMax += 1;
            }
        }
        txtBMin.setText(String.valueOf(lowMin));
        txtOMin.setText(String.valueOf(overMin));
        txtBMax.setText(String.valueOf(lowMax));
        txtOMax.setText(String.valueOf(overMax));

    }

    @Override
    public void selected(Object source, Object selectObj) {
        if (source != null) {
            getReorderLevel();
        }
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
        tblOrder = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtGroup = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtCat = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtBrand = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtStock = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel5 = new javax.swing.JLabel();
        txtOMin = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtBMax = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtOMax = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        txtLoc = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        chk1 = new javax.swing.JCheckBox();
        chk2 = new javax.swing.JCheckBox();
        chk3 = new javax.swing.JCheckBox();
        chk4 = new javax.swing.JCheckBox();
        chk5 = new javax.swing.JCheckBox();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel9 = new javax.swing.JLabel();
        txtBMin = new javax.swing.JTextField();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        tblOrder.setAutoCreateRowSorter(true);
        tblOrder.setModel(new javax.swing.table.DefaultTableModel(
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
        tblOrder.setShowHorizontalLines(true);
        tblOrder.setShowVerticalLines(true);
        jScrollPane1.setViewportView(tblOrder);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Group");

        txtGroup.setFont(Global.textFont);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Category");

        txtCat.setFont(Global.textFont);

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Brand");

        txtBrand.setFont(Global.textFont);

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Stock");

        txtStock.setFont(Global.textFont);

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Over Min");

        txtOMin.setEditable(false);
        txtOMin.setFont(Global.lableFont);
        txtOMin.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtOMin.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Below Max");

        txtBMax.setEditable(false);
        txtBMax.setFont(Global.lableFont);
        txtBMax.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtBMax.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Over Max");

        txtOMax.setEditable(false);
        txtOMax.setFont(Global.lableFont);
        txtOMax.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtOMax.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        txtLoc.setFont(Global.textFont);

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Location");

        chk1.setFont(Global.lableFont);
        chk1.setText("Below-Min");

        chk2.setFont(Global.lableFont);
        chk2.setText("Over-Min");

        chk3.setFont(Global.lableFont);
        chk3.setText("Below-Max");

        chk4.setFont(Global.lableFont);
        chk4.setText("Over-Max");

        chk5.setFont(Global.lableFont);
        chk5.setSelected(true);
        chk5.setText("All");

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Below Min");

        txtBMin.setEditable(false);
        txtBMin.setFont(Global.lableFont);
        txtBMin.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtBMin.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtGroup)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtCat)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtBrand)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtStock)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtLoc)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chk5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chk1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chk2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chk3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chk4))
                    .addComponent(jSeparator2)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtBMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtOMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtBMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtOMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtGroup)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtCat)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtBrand)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtStock)
                    .addComponent(txtLoc)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(chk1)
                        .addComponent(chk2)
                        .addComponent(chk3)
                        .addComponent(chk4)
                        .addComponent(chk5))
                    .addComponent(jSeparator3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtOMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(txtBMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(txtOMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(txtBMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observer.selected("control", this);
    }//GEN-LAST:event_formComponentShown


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chk1;
    private javax.swing.JCheckBox chk2;
    private javax.swing.JCheckBox chk3;
    private javax.swing.JCheckBox chk4;
    private javax.swing.JCheckBox chk5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTable tblOrder;
    private javax.swing.JTextField txtBMax;
    private javax.swing.JTextField txtBMin;
    private javax.swing.JTextField txtBrand;
    private javax.swing.JTextField txtCat;
    private javax.swing.JTextField txtGroup;
    private javax.swing.JTextField txtLoc;
    private javax.swing.JTextField txtOMax;
    private javax.swing.JTextField txtOMin;
    private javax.swing.JTextField txtStock;
    // End of variables declaration//GEN-END:variables

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
        getReorderLevel();
    }

    @Override
    public void filter() {
    }

    @Override
    public String panelName() {
        return this.getName();
    }
}
