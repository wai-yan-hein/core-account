/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry;

import com.inventory.common.FilterObject;
import com.inventory.common.Global;
import com.inventory.common.PanelControl;
import com.inventory.common.ReportFilter;
import com.inventory.common.ReturnObject;
import com.inventory.common.TableCellRender;
import com.inventory.common.Util1;
import com.inventory.editor.BrandAutoCompleter;
import com.inventory.editor.CategoryAutoCompleter;
import com.inventory.editor.CurrencyAutoCompleter;
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.editor.RegionAutoCompleter;
import com.inventory.editor.SaleManAutoCompleter;
import com.inventory.editor.StockAutoCompleter;
import com.inventory.editor.StockTypeAutoCompleter;
import com.inventory.editor.TraderAutoCompleter;
import com.inventory.model.VRoleMenu;
import com.inventory.ui.ApplicationMainFrame;
import com.inventory.ui.common.ReportTableModel;
import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.view.JasperViewer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 *
 * @author Lenovo
 */
@Component
public class Reports extends javax.swing.JPanel implements PanelControl {

    private static final Logger log = LoggerFactory.getLogger(Reports.class);
    @Autowired
    private ReportTableModel tableModel;
    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private WebClient webClient;
    @Autowired
    private ApplicationMainFrame mainFrame;
    private boolean isReport = false;
    private String stDate;
    private String enDate;
    private TraderAutoCompleter traderAutoCompleter;
    private SaleManAutoCompleter saleManAutoCompleter;
    private LocationAutoCompleter locationAutoCompleter;
    private StockTypeAutoCompleter stockTypeAutoCompleter;
    private BrandAutoCompleter brandAutoCompleter;
    private CategoryAutoCompleter categoryAutoCompleter;
    private RegionAutoCompleter regionAutoCompleter;
    private CurrencyAutoCompleter currencyAutoCompleter;
    private StockAutoCompleter stockAutoCompleter;
    private ReportFilter filter;

    /**
     * Creates new form Reports
     */
    public Reports() {
        initComponents();
    }

    public void initMain() {
        initTableReport();
        initCombo();
        initDate();
    }

    private void initDate() {
        txtFromDate.setDate(Util1.getTodayDate());
        txtToDate.setDate(Util1.getTodayDate());
    }

    private void initTableReport() {
        tblReport.setModel(tableModel);
        tblReport.getTableHeader().setFont(Global.tblHeaderFont);
        tblReport.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblReport.setDefaultRenderer(Object.class, new TableCellRender());
        getReport();
    }

    private void getReport() {
        Mono<ResponseEntity<List<VRoleMenu>>> result = webClient.get()
                .uri(builder -> builder.path("/get-report")
                .queryParam("roleCode", Global.roleCode)
                .build())
                .retrieve().toEntityList(VRoleMenu.class);
        result.subscribe((t) -> {
            tableModel.setListReport(t.getBody());
        }, (e) -> {
            JOptionPane.showConfirmDialog(Global.parentForm, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        });
    }

    private void initCombo() {
        Global.listTrader.addAll(Global.listCustomer);
        Global.listTrader.addAll(Global.listSupplier);
        traderAutoCompleter = new TraderAutoCompleter(txtTrader, Global.listTrader, null, true, 1, true);
        saleManAutoCompleter = new SaleManAutoCompleter(txtSaleMan, Global.listSaleMan, null, true, true);
        locationAutoCompleter = new LocationAutoCompleter(txtLocation, Global.listLocation, null, true, true);
        stockTypeAutoCompleter = new StockTypeAutoCompleter(txtStockType, Global.listStockType, null, true, true);
        categoryAutoCompleter = new CategoryAutoCompleter(txtCategory, Global.listCategory, null, true, true);
        brandAutoCompleter = new BrandAutoCompleter(txtBrand, Global.listStockBrand, null, true, true);
        regionAutoCompleter = new RegionAutoCompleter(txtRegion, Global.listRegion, null, true, true);
        currencyAutoCompleter = new CurrencyAutoCompleter(txtCurrency, Global.listCurrency, null, false);
        stockAutoCompleter = new StockAutoCompleter(txtStock, Global.listStock, null, true, true);
        currencyAutoCompleter.setCurrency(Global.defaultCurrency);

    }

    private void report() {
        progress.setIndeterminate(true);
        if (!isReport) {
            isReport = true;
            stDate = Util1.toDateStr(txtFromDate.getDate(), "yyyy-MM-dd");
            enDate = Util1.toDateStr(txtToDate.getDate(), "yyyy-MM-dd");
            filter = new ReportFilter(Global.macId, Global.compCode);
            filter.setFromDate(stDate);
            filter.setToDate(enDate);
            filter.setCurCode(currencyAutoCompleter.getCurrency().getCurCode());
            log.info("Report Date : " + stDate + " - " + enDate);
            int selectRow = tblReport.convertRowIndexToModel(tblReport.getSelectedRow());
            if (selectRow >= 0) {
                VRoleMenu report = tableModel.getReport(selectRow);
                String reportName = report.getMenuName();
                String reportUrl = report.getMenuUrl();
                Map<String, Object> param = new HashMap<>();
                param.put("p_report_name", reportName);
                param.put("p_date", String.format("Between %s and %s",
                        Util1.toDateStr(stDate, "yyyy-MM-dd", "dd/MM/yyyy"),
                        Util1.toDateStr(enDate, "yyyy-MM-dd", "dd/MM/yyyy")));
                param.put("p_print_date", Util1.getTodayDateTime());
                prepareReport(reportUrl, reportUrl, param);
                progress.setIndeterminate(false);
            } else {
                isReport = false;
                progress.setIndeterminate(false);
                JOptionPane.showMessageDialog(Global.parentForm, "Choose Report.");
            }
            isReport = false;
        }
    }

    private void prepareReport(String reportUrl, String reportName, Map<String, Object> param) {
        ReportFilter insertFilter = new ReportFilter(Global.macId, Global.compCode);
        insertFilter.setListTrader(traderAutoCompleter.getListOption());
        insertFilter.setListSaleMan(saleManAutoCompleter.getListOption());
        insertFilter.setListLocation(locationAutoCompleter.getListOption());
        insertFilter.setListStockType(stockTypeAutoCompleter.getListOption());
        insertFilter.setListBrand(brandAutoCompleter.getListOption());
        insertFilter.setListRegion(regionAutoCompleter.getListOption());
        insertFilter.setListCategory(categoryAutoCompleter.getListOption());
        insertFilter.setListStock(stockAutoCompleter.getListOption());
        insertFilter.setCurCode(currencyAutoCompleter.getCurrency().getCurCode());
        Mono<ReturnObject> result = webClient.post()
                .uri("/report/save-filter")
                .body(Mono.just(insertFilter), ReportFilter.class)
                .retrieve()
                .bodyToMono(ReturnObject.class);
        ReturnObject t = result.block();
        if (t != null) {
            log.info(String.format("insertFilter %s", t.getMessage()));
            printReport(reportUrl, reportName, param);
        }
    }

    private void printReport(String reportUrl, String reportName, Map<String, Object> param) {
        filter.setReportName(reportName);
        Mono<ReturnObject> result = webClient
                .post()
                .uri("/report/get-report")
                .body(Mono.just(filter), FilterObject.class)
                .retrieve()
                .bodyToMono(ReturnObject.class);
        result.subscribe((t) -> {
            try {
                if (t != null) {
                    log.info(String.format("printReport %s", t.getMessage()));
                    String filePath = String.format("%s%s%s", Global.reportPath, File.separator, reportUrl.concat(".jasper"));
                    ByteArrayInputStream jsonDataStream = new ByteArrayInputStream(t.getFile());
                    JsonDataSource ds = new JsonDataSource(jsonDataStream);
                    JasperPrint js = JasperFillManager.fillReport(filePath, param, ds);
                    JasperViewer.viewReport(js, false);
                }
            } catch (JRException ex) {
                log.error("printVoucher : " + ex.getMessage());
                JOptionPane.showMessageDialog(Global.parentForm, ex.getMessage());
            }
        }, (e) -> {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
        });

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
        tblReport = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        txtFromDate = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtToDate = new com.toedter.calendar.JDateChooser();
        jLabel1 = new javax.swing.JLabel();
        txtTrader = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtLocation = new javax.swing.JTextField();
        txtStockType = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtCategory = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtBrand = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtRegion = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtSaleMan = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel10 = new javax.swing.JLabel();
        txtCurrency = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtStock = new javax.swing.JTextField();
        progress = new javax.swing.JProgressBar();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        tblReport.setFont(Global.textFont);
        tblReport.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblReport.setRowHeight(Global.tblRowHeight);
        jScrollPane1.setViewportView(tblReport);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtFromDate.setDateFormatString("dd/MM/yyyy");

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("From Date");

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("To Date");

        txtToDate.setDateFormatString("dd/MM/yyyy");

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Trader");

        txtTrader.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTraderKeyReleased(evt);
            }
        });

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Location");

        txtLocation.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtLocationKeyReleased(evt);
            }
        });

        txtStockType.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtStockTypeKeyReleased(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Stock Type");

        txtCategory.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCategoryKeyReleased(evt);
            }
        });

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Stock Category");

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Stock Brand");

        txtBrand.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBrandKeyReleased(evt);
            }
        });

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Region");

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Sale Man");

        txtSaleMan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSaleManKeyReleased(evt);
            }
        });

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Currency");

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Stock ");

        txtStock.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtStockKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtFromDate, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtToDate, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE))
                            .addComponent(txtTrader)
                            .addComponent(txtLocation)
                            .addComponent(txtStockType)
                            .addComponent(txtCategory)
                            .addComponent(txtBrand)
                            .addComponent(txtRegion)
                            .addComponent(txtSaleMan)
                            .addComponent(txtCurrency)
                            .addComponent(txtStock))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtFromDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(txtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtTrader)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtSaleMan)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtLocation)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtStock)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtStockType)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtCategory)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtBrand)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtRegion)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtCurrency)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(progress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 483, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(progress, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 497, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        mainFrame.setControl(this);
    }//GEN-LAST:event_formComponentShown

    private void txtTraderKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTraderKeyReleased
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtSaleMan.requestFocus();
        }
    }//GEN-LAST:event_txtTraderKeyReleased

    private void txtSaleManKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSaleManKeyReleased
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtLocation.requestFocus();
        }
    }//GEN-LAST:event_txtSaleManKeyReleased

    private void txtLocationKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtLocationKeyReleased
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtStockType.requestFocus();
        }
    }//GEN-LAST:event_txtLocationKeyReleased

    private void txtStockTypeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtStockTypeKeyReleased
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtCategory.requestFocus();
        }
    }//GEN-LAST:event_txtStockTypeKeyReleased

    private void txtCategoryKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCategoryKeyReleased
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtBrand.requestFocus();
        }
    }//GEN-LAST:event_txtCategoryKeyReleased

    private void txtBrandKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBrandKeyReleased
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtRegion.requestFocus();
        }
    }//GEN-LAST:event_txtBrandKeyReleased

    private void txtStockKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtStockKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtStockKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JProgressBar progress;
    private javax.swing.JTable tblReport;
    private javax.swing.JTextField txtBrand;
    private javax.swing.JTextField txtCategory;
    private javax.swing.JTextField txtCurrency;
    private com.toedter.calendar.JDateChooser txtFromDate;
    private javax.swing.JTextField txtLocation;
    private javax.swing.JTextField txtRegion;
    private javax.swing.JTextField txtSaleMan;
    private javax.swing.JTextField txtStock;
    private javax.swing.JTextField txtStockType;
    private com.toedter.calendar.JDateChooser txtToDate;
    private javax.swing.JTextField txtTrader;
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
        report();
    }

    @Override
    public void refresh() {
        getReport();
    }
}
