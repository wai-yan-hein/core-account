/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.inventory.ui.setup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.common.Global;
import com.common.PanelControl;
import com.common.ReportFilter;
import com.common.ReturnObject;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.inventory.editor.BrandAutoCompleter;
import com.inventory.editor.CategoryAutoCompleter;
import com.inventory.editor.LocationCellEditor;
import com.inventory.editor.StockAutoCompleter;
import com.inventory.editor.StockCellEditor;
import com.inventory.editor.StockTypeAutoCompleter;
import com.inventory.model.Pattern;
import com.inventory.model.Stock;
import com.inventory.model.StockUnit;
import com.inventory.ui.common.InventoryRepo;
import com.inventory.ui.common.PatternTableModel;
import com.inventory.ui.common.StockCompleterTableModel;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.inventory.ui.setup.dialog.common.StockUnitEditor;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.text.DateFormat;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
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
public class PatternSetup extends javax.swing.JPanel implements PanelControl, SelectionObserver {

    public static final Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
    private final StockCompleterTableModel stockTableModel = new StockCompleterTableModel();
    private final PatternTableModel patternTableModel = new PatternTableModel();
    private SelectionObserver observer;
    private JProgressBar progress;
    private StockTypeAutoCompleter typeAutoCompleter;
    private BrandAutoCompleter brandAutoCompleter;
    private CategoryAutoCompleter categoryAutoCompleter;
    private StockAutoCompleter stockAutoCompleter;

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
    private final FocusAdapter fa = new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            if (e.getComponent() instanceof JTextField jtf) {
                jtf.selectAll();
            }
        }

    };

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

    }

    private void initFocus() {
        txtBrand.addFocusListener(fa);
        txtCat.addFocusListener(fa);
        txtGroup.addFocusListener(fa);
        txtStock.addFocusListener(fa);
    }

    private void initCombo() {
        typeAutoCompleter = new StockTypeAutoCompleter(txtGroup, inventoryRepo.getStockType(), null, true, false);
        typeAutoCompleter.setObserver(this);
        categoryAutoCompleter = new CategoryAutoCompleter(txtCat, inventoryRepo.getCategory(), null, true, false);
        categoryAutoCompleter.setObserver(this);
        brandAutoCompleter = new BrandAutoCompleter(txtBrand, inventoryRepo.getStockBrand(), null, true, false);
        brandAutoCompleter.setObserver(this);
        stockAutoCompleter = new StockAutoCompleter(txtStock, inventoryRepo, null, true);
        stockAutoCompleter.setObserver(this);
    }

    private void focusOnPD() {
        int row = tblPD.getRowCount();
        if (row >= 1) {
            tblPD.setColumnSelectionInterval(0, 0);
            tblPD.setRowSelectionInterval(row - 1, row - 1);
        }
        tblPD.requestFocusInWindow();
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
        searchStock();
    }

    private void searchStock() {
        stockTableModel.setListStock(inventoryRepo.getStock(true));
        ReportFilter filter = new ReportFilter(Global.macId, Global.compCode);
        filter.setBrandCode(brandAutoCompleter.getBrand().getBrandCode());
        filter.setCatCode(categoryAutoCompleter.getCategory().getCatCode());
        filter.setStockTypeCode(typeAutoCompleter.getStockType().getStockTypeCode());
        filter.setStockCode(stockAutoCompleter.getStock().getKey().getStockCode());
        Mono<ResponseEntity<List<Stock>>> result = inventoryApi
                .post()
                .uri("/setup/search-stock")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .toEntityList(Stock.class);
        result.subscribe((t) -> {
            stockTableModel.setListStock(t.getBody());
            progress.setIndeterminate(false);
        }, (e) -> {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
            progress.setIndeterminate(false);
        });
    }

    private void initTablePD() {
        List<StockUnit> lisetStockUnit = inventoryRepo.getStockUnit();
        patternTableModel.setTable(tblPD);
        patternTableModel.setPanel(this);
        patternTableModel.setWebClient(inventoryApi);
        tblPD.setCellSelectionEnabled(true);
        tblPD.setModel(patternTableModel);
        tblPD.getTableHeader().setFont(Global.tblHeaderFont);
        tblPD.setRowHeight(Global.tblRowHeight);
        tblPD.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblPD.setFont(Global.textFont);
        tblPD.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor(inventoryRepo));
        tblPD.getColumnModel().getColumn(1).setCellEditor(new StockCellEditor(inventoryRepo));
        tblPD.getColumnModel().getColumn(2).setCellEditor(new LocationCellEditor(inventoryRepo.getLocation()));
        tblPD.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());
        tblPD.getColumnModel().getColumn(4).setCellEditor(new StockUnitEditor(lisetStockUnit));
        tblPD.getColumnModel().getColumn(5).setCellEditor(new AutoClearEditor());
        tblPD.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblPD.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblPD.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblPD.getColumnModel().getColumn(3).setPreferredWidth(50);
        tblPD.getColumnModel().getColumn(4).setPreferredWidth(50);
        tblPD.getColumnModel().getColumn(5).setPreferredWidth(50);
        tblPD.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblPD.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void select() {
        int row = tblStock.convertRowIndexToModel(tblStock.getSelectedRow());
        if (row >= 0) {
            Stock s = stockTableModel.getStock(row);
            String stockCode = s.getKey().getStockCode();
            patternTableModel.setListPattern(inventoryRepo.getPattern(stockCode));
            patternTableModel.setStockCode(stockCode);
            lblStockName.setText(s.getStockName());
            focusOnPD();

        }
    }

    private void deleteTran() {
        int row = tblPD.convertRowIndexToModel(tblPD.getSelectedRow());
        if (row >= 0) {
            int y = JOptionPane.showConfirmDialog(this, "Are you sure to delete?");
            if (y == JOptionPane.YES_OPTION) {
                Pattern p = patternTableModel.getPattern(row);
                Mono<ReturnObject> result = inventoryApi.get()
                        .uri(builder -> builder.path("/setup/delete-pattern")
                        .queryParam("stockCode", p.getStock().getKey().getStockCode())
                        .build())
                        .retrieve().bodyToMono(ReturnObject.class);
                ReturnObject ro = result.block();
                if (ro != null) {
                    patternTableModel.remove(row);
                    focusOnPD();
                }
            }
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
        tblStock = new javax.swing.JTable();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane2 = new javax.swing.JScrollPane();
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
        jButton1 = new javax.swing.JButton();

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
        jScrollPane2.setViewportView(tblPD);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Filter", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, Global.lableFont));

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
        lblStockName.setText("Stock Name");

        jButton1.setFont(Global.lableFont);
        jButton1.setText("Delete");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblStockName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStockName, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
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
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 559, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE))
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
        observer.selected("control", this);
    }//GEN-LAST:event_formComponentShown

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        deleteTran();
    }//GEN-LAST:event_jButton1ActionPerformed
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
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblStockName;
    private javax.swing.JTable tblPD;
    private javax.swing.JTable tblStock;
    private javax.swing.JTextField txtBrand;
    private javax.swing.JTextField txtCat;
    private javax.swing.JTextField txtGroup;
    private javax.swing.JTextField txtStock;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {
        if (source != null) {
            searchStock();
        }
    }
}
