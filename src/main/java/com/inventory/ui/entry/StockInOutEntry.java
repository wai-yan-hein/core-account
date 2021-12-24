/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry;

import com.google.gson.reflect.TypeToken;
import java.awt.event.KeyEvent;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.inventory.common.Global;
import static com.inventory.common.Global.macId;
import com.inventory.common.PanelControl;
import com.inventory.common.ReturnObject;
import com.inventory.common.SelectionObserver;
import com.inventory.common.Util1;
import com.inventory.editor.LocationCellEditor;
import com.inventory.editor.StockCellEditor;
import com.inventory.editor.VouStatusAutoCompleter;
import com.inventory.model.Pattern;
import com.inventory.model.PatternDetail;
import com.inventory.model.StockInOut;
import com.inventory.model.StockInOutDetail;
import static com.inventory.model.Voucher.STOCKINOUT;
import com.inventory.ui.ApplicationMainFrame;
import com.inventory.ui.common.StockInOutTableModel;
import com.inventory.ui.common.VouFormatFactory;
import com.inventory.ui.entry.dialog.PatternOptionDialog;
import com.inventory.ui.entry.dialog.StockInOutVouSearchDialog;
import static com.inventory.ui.setup.PatternSetup.gson;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.inventory.ui.setup.dialog.common.StockUnitEditor;
import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.Image;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 *
 * @author Lenovo
 */
@Component
public class StockInOutEntry extends javax.swing.JPanel implements PanelControl, SelectionObserver {

    private static final Logger log = LoggerFactory.getLogger(StockInOutEntry.class);
    @Autowired
    private StockInOutTableModel outTableModel;
    @Autowired
    private StockInOutVouSearchDialog historyDialog;
    @Autowired
    private WebClient webClient;
    @Autowired
    private ApplicationMainFrame mainFrame;
    private VouStatusAutoCompleter vouStatusAutoCompleter;
    private StockInOut io = new StockInOut();

    /**
     * Creates new form StockInOutEntry
     */
    public StockInOutEntry() {
        initComponents();
        initVoucherFormat();
    }

    private void initVoucherFormat() {
        try {
            txtVou.setFormatterFactory(new VouFormatFactory());
            txtDate.setDate(Util1.getTodayDate());
        } catch (ParseException ex) {
            log.error(ex.getMessage());
        }
    }

    public void initMain() {
        initTable();
        initCombo();
        clear();
    }

    private void initCombo() {
        vouStatusAutoCompleter = new VouStatusAutoCompleter(txtVouType, Global.listVouStatus, null, false);
        vouStatusAutoCompleter.setVoucher(null);
    }

    private void initTable() {
        outTableModel.addNewRow();
        outTableModel.setParent(tblStock);
        outTableModel.setInTotal(txtInTotalWt);
        outTableModel.setOutTotal(txtOutTotalWt);
        tblStock.setModel(outTableModel);
        tblStock.getTableHeader().setFont(Global.tblHeaderFont);
        tblStock.getColumnModel().getColumn(0).setPreferredWidth(100);
        tblStock.getColumnModel().getColumn(1).setPreferredWidth(250);
        tblStock.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblStock.getColumnModel().getColumn(3).setPreferredWidth(50);
        tblStock.getColumnModel().getColumn(4).setPreferredWidth(50);
        tblStock.getColumnModel().getColumn(5).setPreferredWidth(50);
        tblStock.getColumnModel().getColumn(6).setPreferredWidth(50);
        tblStock.getColumnModel().getColumn(7).setPreferredWidth(50);
        tblStock.getColumnModel().getColumn(8).setPreferredWidth(50);
        tblStock.getColumnModel().getColumn(9).setPreferredWidth(50);
        tblStock.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor());
        tblStock.getColumnModel().getColumn(1).setCellEditor(new StockCellEditor());
        tblStock.getColumnModel().getColumn(2).setCellEditor(new LocationCellEditor());
        tblStock.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());
        tblStock.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());
        tblStock.getColumnModel().getColumn(5).setCellEditor(new StockUnitEditor());
        tblStock.getColumnModel().getColumn(6).setCellEditor(new AutoClearEditor());
        tblStock.getColumnModel().getColumn(7).setCellEditor(new AutoClearEditor());
        tblStock.getColumnModel().getColumn(8).setCellEditor(new StockUnitEditor());
        tblStock.getColumnModel().getColumn(9).setCellEditor(new AutoClearEditor());
        tblStock.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblStock.setCellSelectionEnabled(true);
        tblStock.changeSelection(0, 0, false, false);
        tblStock.requestFocus();
    }

    private void deleteVoucher() {
        if (lblStatus.getText().equals("EDIT")) {
            int yes_no = JOptionPane.showConfirmDialog(Global.parentForm,
                    "Are you sure to delete?", "Damage item delete", JOptionPane.YES_NO_OPTION);
            if (yes_no == 0) {
                io.setDeleted(true);
                saveVoucher();
            }
        }
    }

    public boolean saveVoucher() {
        boolean status = false;
        try {
            if (isValidEntry() && outTableModel.isValidEntry()) {
                progess.setIndeterminate(true);
                io.setListSH(outTableModel.getListStock());
                io.setListDel(outTableModel.getDeleteList());
                Mono<StockInOut> result = webClient.post()
                        .uri("/stockio/save-stockio")
                        .body(Mono.just(io), StockInOut.class)
                        .retrieve()
                        .bodyToMono(StockInOut.class);
                StockInOut t = result.block();
                if (t != null) {
                    clear();
                    focusOnTable();
                }
            }
        } catch (HeadlessException ex) {
            log.error("saveVoucher :" + ex.getMessage());
            JOptionPane.showMessageDialog(Global.parentForm, "Could'nt saved.");
        }
        return status;
    }

    private void clear() {
        io = new StockInOut();
        lblStatus.setForeground(Color.GREEN);
        lblStatus.setText("NEW");
        txtDesp.setText(null);
        txtRemark.setText(null);
        txtInTotalWt.setValue(0.0);
        txtOutTotalWt.setValue(0.0);
        vouStatusAutoCompleter.setVoucher(null);
        outTableModel.clear();
        outTableModel.addNewRow();
        txtDate.setDate(Util1.getTodayDate());
        progess.setIndeterminate(false);
        disableForm(true);
        genVouNo();
    }

    private void focusOnTable() {
        int row = tblStock.getRowCount();
        tblStock.setColumnSelectionInterval(0, 0);
        tblStock.setRowSelectionInterval(row - 1, row - 1);
        tblStock.requestFocus();
    }

    private void genVouNo() {
        Mono<ReturnObject> result = webClient.get()
                .uri(builder -> builder.path("/voucher/get-vou-no")
                .queryParam("macId", macId)
                .queryParam("option", STOCKINOUT.name())
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().bodyToMono(ReturnObject.class);
        result.subscribe((t) -> {
            log.info("Vou No : " + t.getMessage());
            txtVou.setText(t.getMessage());
        }, (e) -> {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
        });
    }

    private boolean isValidEntry() {
        boolean status = true;
        if (Util1.isNull(txtVou.getText())) {
            JOptionPane.showMessageDialog(Global.parentForm, "Invalid Voucher No.");
            status = false;
        } else if (vouStatusAutoCompleter.getVouStatus() == null) {
            JOptionPane.showMessageDialog(Global.parentForm, "Select Voucher Status.");
            status = false;
            txtVouType.requestFocus();
        } else {
            io.setVouNo(txtVou.getText());
            io.setDescription(txtDesp.getText());
            io.setRemark(txtRemark.getText());
            io.setVouDate(txtDate.getDate());
            io.setVouStatus(vouStatusAutoCompleter.getVouStatus());
            if (lblStatus.getText().equals("NEW")) {
                io.setCreatedBy(Global.loginUser);
                io.setCreatedDate(Util1.getTodayDate());
                io.setCompCode(Global.compCode);
                io.setMacId(Global.macId);
                io.setDeleted(Boolean.FALSE);
            } else {
                io.setUpdatedBy(Global.loginUser);
            }
        }
        return status;
    }

    private void setVoucher(StockInOut s) {
        progess.setIndeterminate(true);
        io = s;
        String vouNo = io.getVouNo();
        Mono<ResponseEntity<List<StockInOutDetail>>> result = webClient.get()
                .uri(builder -> builder.path("/stockio/get-stockio-detail")
                .queryParam("vouNo", vouNo)
                .build())
                .retrieve().toEntityList(StockInOutDetail.class);
        result.subscribe((t) -> {
            outTableModel.setListStock(t.getBody());
            outTableModel.addNewRow();
            txtVou.setText(io.getVouNo());
            txtDate.setDate(io.getVouDate());
            txtRemark.setText(io.getRemark());
            txtDesp.setText(io.getDescription());
            vouStatusAutoCompleter.setVoucher(io.getVouStatus());
            if (Util1.getBoolean(io.getDeleted())) {
                lblStatus.setText("DELETED");
                lblStatus.setForeground(Color.red);
                disableForm(false);
            } else {
                lblStatus.setText("EDIT");
                lblStatus.setForeground(Color.blue);
                disableForm(true);
            }
            progess.setIndeterminate(false);
        }, (e) -> {
            progess.setIndeterminate(false);
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
        });

    }

    private void disableForm(boolean status) {
        txtDate.setEnabled(status);
        txtRemark.setEnabled(status);
        txtDesp.setEnabled(status);
        tblStock.setEnabled(status);

    }

    private void searchPD(String patternCode, float qty) {
        Mono<ReturnObject> result = webClient
                .get()
                .uri(builder -> builder.path("/setup/get-pattern-detail")
                .queryParam("patternCode", patternCode)
                .build())
                .retrieve().bodyToMono(ReturnObject.class);
        result.subscribe((t) -> {
            java.lang.reflect.Type listType = new TypeToken<ArrayList<PatternDetail>>() {
            }.getType();
            List<PatternDetail> listOP = gson.fromJson(gson.toJsonTree(t.getList()), listType);
            addStockInout(listOP, qty);

        }, (e) -> {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
        });
    }

    private void addStockInout(List<PatternDetail> listPD, float qty) {
        List<StockInOutDetail> listIO = new ArrayList<>();
        if (!listPD.isEmpty()) {
            for (PatternDetail pd : listPD) {
                StockInOutDetail iod = new StockInOutDetail();
                iod.setStock(pd.getStock());
                iod.setLocation(pd.getLocation());
                iod.setInQty(Util1.getFloat(pd.getInQty()) * qty);
                iod.setInWt(1.0f);
                iod.setInUnit(pd.getInUnit());
                iod.setOutQty(Util1.getFloat(pd.getOutQty()) * qty);
                iod.setOutWt(1.0f);
                iod.setOutUnit(pd.getOutUnit());
                iod.setCostPrice(pd.getCostPrice());
                listIO.add(iod);
            }
            outTableModel.setListStock(listIO);
        } else {
            JOptionPane.showMessageDialog(this, "Pattern is empty.");
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
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtDesp = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtVou = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        txtDate = new com.toedter.calendar.JDateChooser();
        jLabel7 = new javax.swing.JLabel();
        txtVouType = new javax.swing.JTextField();
        txtOutTotalWt = new javax.swing.JFormattedTextField();
        txtInTotalWt = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        progess = new javax.swing.JProgressBar();
        jButton1 = new javax.swing.JButton();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        tblStock.setFont(Global.textFont);
        tblStock.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblStock.setRowHeight(Global.tblRowHeight);
        tblStock.setShowHorizontalLines(true);
        tblStock.setShowVerticalLines(true);
        tblStock.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblStockKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblStock);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Description");

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Remark");

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Vou No    ");

        txtVou.setEditable(false);

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Date");

        txtDate.setDateFormatString("dd/MM/yyyy");
        txtDate.setFont(Global.lableFont);
        txtDate.setMaxSelectableDate(new java.util.Date(253370745114000L));

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Vou Type");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(txtVou, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(txtDate, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addComponent(txtVouType, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(txtDesp, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(txtRemark, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(txtDesp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)
                        .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7)
                        .addComponent(txtVouType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(txtVou, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)))
                .addContainerGap(7, Short.MAX_VALUE))
        );

        txtOutTotalWt.setEditable(false);
        txtOutTotalWt.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter()));
        txtOutTotalWt.setFont(Global.amtFont);

        txtInTotalWt.setEditable(false);
        txtInTotalWt.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter()));
        txtInTotalWt.setFont(Global.amtFont);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Stock Out Total");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Stock In Total");

        lblStatus.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        lblStatus.setText("NEW");

        jButton1.setBackground(Global.selectionColor);
        jButton1.setFont(Global.lableFont);
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Pattern");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(progess, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(txtInTotalWt, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(txtOutTotalWt, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(progess, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtOutTotalWt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtInTotalWt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)
                        .addComponent(jLabel1))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        mainFrame.setControl(this);
        focusOnTable();

    }//GEN-LAST:event_formComponentShown

    private void tblStockKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblStockKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_tblStockKeyReleased

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        PatternOptionDialog dialog = new PatternOptionDialog(Global.parentForm);
        dialog.setWebClient(webClient);
        dialog.searchPattern();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        Pattern p = dialog.getPattern();
        String patternCode = p.getPatternCode();
        Float qty = Util1.getFloat(dialog.getTxtQty().getText());
        if (!Util1.isNull(patternCode)) {
            vouStatusAutoCompleter.setVoucher(p.getVouStatus());
            searchPD(patternCode, qty == 0 ? 1 : qty);
        }
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JProgressBar progess;
    private javax.swing.JTable tblStock;
    private com.toedter.calendar.JDateChooser txtDate;
    private javax.swing.JTextField txtDesp;
    private javax.swing.JFormattedTextField txtInTotalWt;
    private javax.swing.JFormattedTextField txtOutTotalWt;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JFormattedTextField txtVou;
    private javax.swing.JTextField txtVouType;
    // End of variables declaration//GEN-END:variables

    @Override
    public void save() {
        saveVoucher();
    }

    @Override
    public void delete() {
        deleteVoucher();
    }

    @Override
    public void newForm() {
        clear();
    }

    @Override
    public void history() {
        Image searchIcon = new ImageIcon(getClass().getResource("/images/search.png")).getImage();
        historyDialog.setIconImage(searchIcon);
        historyDialog.setObserver(this);
        historyDialog.initMain();
        historyDialog.setSize(Global.width - 200, Global.height - 200);
        historyDialog.setLocationRelativeTo(null);
        historyDialog.setVisible(true);
    }

    @Override
    public void print() {
    }

    @Override
    public void refresh() {
    }

    @Override
    public void selected(Object source, Object selectObj) {
        if (source.toString().equals("IO-HISTORY")) {
            setVoucher((StockInOut) selectObj);
        }

    }
}
