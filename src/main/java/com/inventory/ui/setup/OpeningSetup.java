/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup;

import com.inventory.common.Global;
import com.inventory.common.PanelControl;
import com.inventory.common.ReturnObject;
import com.inventory.common.SelectionObserver;
import com.inventory.common.Util1;
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.editor.StockCellEditor;
import com.inventory.model.OPHis;
import com.inventory.model.OPHisDetail;
import com.inventory.model.Voucher;
import com.inventory.ui.ApplicationMainFrame;
import com.inventory.ui.common.OpeningTableModel;
import com.inventory.ui.common.VouFormatFactory;
import com.inventory.ui.entry.dialog.OPSearchDialog;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.inventory.ui.setup.dialog.common.StockUnitEditor;
import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class OpeningSetup extends javax.swing.JPanel implements PanelControl, SelectionObserver {

    @Autowired
    private OpeningTableModel openingTableModel;
    private LocationAutoCompleter locationAutoCompleter;
    @Autowired
    private WebClient webClient;
    @Autowired
    private ApplicationMainFrame mainFrame;
    private OPSearchDialog vouSearchDialog;
    private OPHis oPHis = new OPHis();

    /**
     * Creates new form OpeningSetup
     */
    public OpeningSetup() {
        initComponents();
    }

    public void initMain() {
        initCompleter();
        initTable();
        txtOPDate.setDate(Util1.getTodayDate());
        genVouNo();
    }

    private void initCompleter() {
        try {
            txtVouNo.setFormatterFactory(new VouFormatFactory());
        } catch (ParseException ex) {
        }
        locationAutoCompleter = new LocationAutoCompleter(txtLocation, Global.listLocation, null, false, false);
        locationAutoCompleter.setLocation(Global.defaultLocation);

    }

    private void initTable() {
        openingTableModel.setParent(tblOpening);
        openingTableModel.addNewRow();
        tblOpening.setModel(openingTableModel);
        tblOpening.setFont(Global.textFont);
        tblOpening.getTableHeader().setFont(Global.tblHeaderFont);
        tblOpening.setCellSelectionEnabled(true);
        tblOpening.setRowHeight(Global.tblRowHeight);
        tblOpening.getColumnModel().getColumn(0).setPreferredWidth(50);//code
        tblOpening.getColumnModel().getColumn(1).setPreferredWidth(100);//name
        tblOpening.getColumnModel().getColumn(2).setPreferredWidth(50);//qty
        tblOpening.getColumnModel().getColumn(3).setPreferredWidth(50);//std wt
        tblOpening.getColumnModel().getColumn(4).setPreferredWidth(50);//unit
        tblOpening.getColumnModel().getColumn(5).setPreferredWidth(100);//price
        tblOpening.getColumnModel().getColumn(6).setPreferredWidth(100);//amount
        tblOpening.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor());
        tblOpening.getColumnModel().getColumn(1).setCellEditor(new StockCellEditor());
        tblOpening.getColumnModel().getColumn(2).setCellEditor(new AutoClearEditor());
        tblOpening.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());
        tblOpening.getColumnModel().getColumn(4).setCellEditor(new StockUnitEditor());
        tblOpening.getColumnModel().getColumn(5).setCellEditor(new AutoClearEditor());
        tblOpening.getColumnModel().getColumn(6).setCellEditor(new AutoClearEditor());
        tblOpening.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblOpening.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void genVouNo() {
        Mono<ReturnObject> result = webClient.get()
                .uri(builder -> builder.path("/voucher/get-vou-no")
                .queryParam("macId", Global.macId)
                .queryParam("option", Voucher.OPENING.name())
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().bodyToMono(ReturnObject.class);
        ReturnObject t = result.block();
        txtVouNo.setText(t.getMessage());
    }

    private void clear() {
        genVouNo();
        txtOPDate.setDate(Util1.getTodayDate());
        locationAutoCompleter.setLocation(Global.defaultLocation);
        txtRemark.setText(null);
        openingTableModel.clear();
        openingTableModel.addNewRow();
        lblStatus.setText("NEW");
        oPHis = new OPHis();
    }

    private void saveOpening() {
        try {
            progress.setIndeterminate(true);
            String voucherNo = txtVouNo.getText();
            if (isValidEntry() && openingTableModel.isValidEntry()) {
                progress.setIndeterminate(true);
                if (lblStatus.getText().equals("NEW")) {
                    oPHis.setCreatedBy(Global.loginUser);
                    oPHis.setCreatedDate(Util1.getTodayDate());
                } else {
                    oPHis.setCreatedBy(Global.loginUser);
                }
                oPHis.setVouNo(voucherNo);
                oPHis.setVouDate(txtOPDate.getDate());
                oPHis.setRemark(txtRemark.getText());
                oPHis.setStatus(lblStatus.getText());
                oPHis.setCompCode(Global.compCode);
                oPHis.setMacId(Global.macId);
                oPHis.setLocation(locationAutoCompleter.getLocation());
                oPHis.setDetailList(openingTableModel.getListDetail());
                oPHis.setListDel(openingTableModel.getDelList());
                Mono<ReturnObject> result = webClient.post()
                        .uri("/setup/save-opening")
                        .body(Mono.just(oPHis), OPHis.class)
                        .retrieve()
                        .bodyToMono(ReturnObject.class);
                ReturnObject t = result.block();
                if (!Util1.isNull(t.getErrorMessage())) {
                    JOptionPane.showMessageDialog(Global.parentForm, t.getErrorMessage());
                }
                clear();
                progress.setIndeterminate(false);
            } else {
                progress.setIndeterminate(false);
            }
        } catch (HeadlessException ex) {
            log.error("Save Opening :" + ex.getMessage());
            JOptionPane.showMessageDialog(Global.parentForm, "Could'nt saved.");
        }
    }

    private boolean isValidEntry() {
        boolean status = true;
        if (Util1.isNull(txtVouNo.getText())) {
            status = false;
            JOptionPane.showMessageDialog(this, "Invalid Voucher No.");
        } else if (locationAutoCompleter.getLocation() == null) {
            status = false;
            JOptionPane.showMessageDialog(this, "Invalid Location");
            txtLocation.requestFocus();
        } else if (openingTableModel.getListDetail().size() == 1) {
            status = false;
            JOptionPane.showMessageDialog(this, "Invalid Transaction.");
            tblOpening.requestFocus();
        }
        return status;
    }

    public void historyOP() {
        vouSearchDialog = new OPSearchDialog();
        vouSearchDialog.setWebClient(webClient);
        vouSearchDialog.setIconImage(new ImageIcon(getClass().getResource("/images/search.png")).getImage());
        vouSearchDialog.setObserver(this);
        vouSearchDialog.initMain();
        vouSearchDialog.setSize(Global.width - 200, Global.height - 200);
        vouSearchDialog.setLocationRelativeTo(null);
        vouSearchDialog.setVisible(true);
    }

    private void focusOnTable() {
        int rc = tblOpening.getRowCount();
        if (rc > 1) {
            tblOpening.setRowSelectionInterval(rc - 1, rc - 1);
            tblOpening.setColumnSelectionInterval(0, 0);
            tblOpening.requestFocus();
        } else {
            tblOpening.requestFocus();
        }
    }

    private void setVoucher(OPHis op) {
        progress.setIndeterminate(true);
        Mono<ResponseEntity<List<OPHisDetail>>> result = webClient.get()
                .uri(builder -> builder.path("/setup/get-opening-detail")
                .queryParam("vouNo", op.getVouNo())
                .build())
                .retrieve().toEntityList(OPHisDetail.class);
        result.subscribe((t) -> {
            oPHis = op;
            txtVouNo.setText(oPHis.getVouNo());
            txtOPDate.setDate(oPHis.getVouDate());
            locationAutoCompleter.setLocation(oPHis.getLocation());
            txtRemark.setText(oPHis.getRemark());
            if (oPHis.isDeleted()) {
                lblStatus.setText("DELETED");
                lblStatus.setForeground(Color.RED);
                disableForm(false);
            } else {
                lblStatus.setText("EDIT");
                lblStatus.setForeground(Color.blue);
                disableForm(true);
            }
            openingTableModel.setListDetail(t.getBody());
            openingTableModel.addNewRow();
            focusOnTable();
            progress.setIndeterminate(false);
        }, (e) -> {
            progress.setIndeterminate(false);
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
        });
    }

    private void disableForm(boolean status) {
        txtLocation.setEnabled(status);
        txtOPDate.setEnabled(status);
        txtRemark.setEnabled(status);
        txtVouNo.setEnabled(status);
    }

    private void deleteVoucher() {
        if (lblStatus.getText().equals("EDIT")) {
            int yes_no = JOptionPane.showConfirmDialog(Global.parentForm,
                    "Are you sure to delete?", "Sale item delete", JOptionPane.YES_NO_OPTION);
            if (yes_no == 0) {
                oPHis.setDeleted(true);
                saveOpening();
            }
        } else {
            JOptionPane.showMessageDialog(Global.parentForm, "Voucher can't delete.");
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

        jPanel1 = new javax.swing.JPanel();
        txtOPDate = new com.toedter.calendar.JDateChooser();
        txtRemark = new javax.swing.JTextField();
        txtLocation = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtVouNo = new javax.swing.JFormattedTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblOpening = new javax.swing.JTable();
        progress = new javax.swing.JProgressBar();
        lblStatus = new javax.swing.JLabel();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        txtOPDate.setDateFormatString("dd/MM/yyyy");
        txtOPDate.setFont(Global.lableFont);

        txtRemark.setFont(Global.textFont);

        txtLocation.setFont(Global.textFont);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Vou No");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Location");

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Opening Date");

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Remark");

        txtVouNo.setEditable(false);
        txtVouNo.setFont(Global.lableFont);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtLocation)
                    .addComponent(txtVouNo, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtOPDate, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtOPDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtVouNo)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtLocation, txtOPDate, txtRemark});

        tblOpening.setFont(Global.textFont);
        tblOpening.setModel(new javax.swing.table.DefaultTableModel(
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
        tblOpening.setGridColor(new java.awt.Color(204, 204, 204));
        tblOpening.setShowHorizontalLines(true);
        tblOpening.setShowVerticalLines(true);
        tblOpening.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                tblOpeningComponentShown(evt);
            }
        });
        jScrollPane1.setViewportView(tblOpening);

        lblStatus.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        lblStatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStatus.setText("NEW");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator1)
                    .addComponent(progress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                        .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(progress, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        mainFrame.setControl(this);
        focusOnTable();
    }//GEN-LAST:event_formComponentShown

    private void tblOpeningComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_tblOpeningComponentShown
        // TODO add your handling code here:

    }//GEN-LAST:event_tblOpeningComponentShown


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JProgressBar progress;
    private javax.swing.JTable tblOpening;
    private javax.swing.JTextField txtLocation;
    private com.toedter.calendar.JDateChooser txtOPDate;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JFormattedTextField txtVouNo;
    // End of variables declaration//GEN-END:variables

    @Override
    public void save() {
        saveOpening();
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
        historyOP();
    }

    @Override
    public void print() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void refresh() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void selected(Object source, Object selectObj) {
        if (source.toString().equals("OP-HISTORY")) {
            if (selectObj instanceof OPHis op) {
                setVoucher(op);
            }
        }
    }

}
