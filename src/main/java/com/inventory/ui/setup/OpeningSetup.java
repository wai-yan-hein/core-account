/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup;

import com.common.DecimalFormatRender;
import com.common.Global;
import com.common.PanelControl;
import com.common.ProUtil;
import com.common.ReturnObject;
import com.common.SelectionObserver;
import com.user.common.UserRepo;
import com.common.Util1;
import com.inventory.editor.CurrencyAutoCompleter;
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.editor.StockCellEditor;
import com.inventory.model.Location;
import com.inventory.model.OPHis;
import com.inventory.model.OPHisDetail;
import com.inventory.model.Stock;
import com.inventory.model.StockUnit;
import com.inventory.ui.common.InventoryRepo;
import com.inventory.ui.common.OpeningTableModel;
import com.inventory.ui.entry.dialog.OPSearchDialog;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.inventory.ui.setup.dialog.common.StockUnitEditor;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
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
    private CurrencyAutoCompleter currencyAAutoCompleter;
    @Autowired
    private WebClient inventoryApi;
    @Autowired
    private InventoryRepo inventoryRepo;
    @Autowired
    private UserRepo userRepo;
    private final OPSearchDialog vouSearchDialog = new OPSearchDialog(Global.parentForm);
    private OPHis oPHis = new OPHis();
    private JProgressBar progress;
    private SelectionObserver observer;
    private List<Location> listLocation = new ArrayList<>();
    private List<Stock> listStock = new ArrayList<>();

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
     * Creates new form OpeningSetup
     */
    public OpeningSetup() {
        initComponents();
        initTextBoxFormat();
    }

    public void initMain() {
        initCompleter();
        initTable();
        txtOPDate.setDate(Util1.getTodayDate());
        txtCurrency.setEnabled(ProUtil.isMultiCur());
    }

    private void initTextBoxFormat() {
        txtQty.setFormatterFactory(Util1.getDecimalFormat());
        txtAmount.setFormatterFactory(Util1.getDecimalFormat());
    }

    private void initCompleter() {
        listLocation = inventoryRepo.getLocation();
        listStock = inventoryRepo.getStock(true);
        locationAutoCompleter = new LocationAutoCompleter(txtLocation, listLocation, null, false, false);
        locationAutoCompleter.setLocation(inventoryRepo.getDefaultLocation());
        currencyAAutoCompleter = new CurrencyAutoCompleter(txtCurrency, userRepo.getCurrency(), null, false);
        currencyAAutoCompleter.setCurrency(userRepo.getDefaultCurrency());
    }

    private void initTable() {
        openingTableModel.setObserver(this);
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
        tblOpening.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor(listStock));
        tblOpening.getColumnModel().getColumn(1).setCellEditor(new StockCellEditor(listStock));
        tblOpening.getColumnModel().getColumn(2).setCellEditor(new AutoClearEditor());
        tblOpening.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());
        tblOpening.getColumnModel().getColumn(4).setCellEditor(new StockUnitEditor(inventoryRepo.getStockUnit()));
        tblOpening.getColumnModel().getColumn(5).setCellEditor(new AutoClearEditor());
        tblOpening.getColumnModel().getColumn(6).setCellEditor(new AutoClearEditor());
        tblOpening.setDefaultRenderer(Object.class, new DecimalFormatRender());
        tblOpening.setDefaultRenderer(Float.class, new DecimalFormatRender());
        tblOpening.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblOpening.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void clear() {
        txtOPDate.setDate(Util1.getTodayDate());
        locationAutoCompleter.setLocation(inventoryRepo.getDefaultLocation());
        txtRemark.setText(null);
        openingTableModel.clear();
        openingTableModel.addNewRow();
        lblStatus.setText("NEW");
        oPHis = new OPHis();
        txtVouNo.setText(null);
        currencyAAutoCompleter.setCurrency(userRepo.getDefaultCurrency());
        calculatAmount();
    }

    private void saveOpening() {
        try {
            progress.setIndeterminate(true);
            String voucherNo = txtVouNo.getText();
            if (isValidEntry() && openingTableModel.isValidEntry()) {
                progress.setIndeterminate(true);
                if (lblStatus.getText().equals("NEW")) {
                    oPHis.setCreatedBy(Global.loginUser.getUserCode());
                    oPHis.setCreatedDate(Util1.getTodayDate());
                } else {
                    oPHis.setCreatedBy(Global.loginUser.getUserCode());
                }
                oPHis.setVouNo(voucherNo);
                oPHis.setCurrency(currencyAAutoCompleter.getCurrency());
                oPHis.setVouDate(txtOPDate.getDate());
                oPHis.setRemark(txtRemark.getText());
                oPHis.setStatus(lblStatus.getText());
                oPHis.setCompCode(Global.compCode);
                oPHis.setMacId(Global.macId);
                oPHis.setLocation(locationAutoCompleter.getLocation());
                oPHis.setDetailList(openingTableModel.getListDetail());
                oPHis.setListDel(openingTableModel.getDelList());
                Mono<ReturnObject> result = inventoryApi.post()
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
        if (locationAutoCompleter.getLocation() == null) {
            status = false;
            JOptionPane.showMessageDialog(this, "Invalid Location");
            txtLocation.requestFocus();
        } else if (lblStatus.getText().equals("DELETED")) {
            status = false;
            clear();
        } else if (openingTableModel.getListDetail().size() == 1) {
            status = false;
            JOptionPane.showMessageDialog(this, "Invalid Transaction.");
            tblOpening.requestFocus();
        } else if (currencyAAutoCompleter.getCurrency() == null) {
            status = false;
            JOptionPane.showMessageDialog(this, "Invalid Currency.");
            txtCurrency.requestFocus();
        }
        return status;
    }

    public void historyOP() {
        try {
            vouSearchDialog.setUserRepo(userRepo);
            vouSearchDialog.setInventoryRepo(inventoryRepo);
            vouSearchDialog.setWebClient(inventoryApi);
            vouSearchDialog.setObserver(this);
            vouSearchDialog.initMain();
            vouSearchDialog.setSize(Global.width - 200, Global.height - 200);
            vouSearchDialog.setLocationRelativeTo(null);
            vouSearchDialog.setIconImage(new ImageIcon(getClass().getResource("/images/search.png")).getImage());
            vouSearchDialog.setVisible(true);
        } catch (Exception e) {
            log.error(String.format("historyOPhistoryOP: %s", Arrays.toString(e.getStackTrace())));
        }
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
        Mono<ResponseEntity<List<OPHisDetail>>> result = inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-opening-detail")
                .queryParam("vouNo", op.getVouNo())
                .build())
                .retrieve().toEntityList(OPHisDetail.class);
        result.subscribe((t) -> {
            oPHis = op;
            txtVouNo.setText(oPHis.getVouNo());
            txtOPDate.setDate(oPHis.getVouDate());
            locationAutoCompleter.setLocation(oPHis.getLocation());
            currencyAAutoCompleter.setCurrency(oPHis.getCurrency());
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
            calculatAmount();
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
                    "Are you sure to delete?", "Opening Voucher delete", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
            if (yes_no == 0) {
                oPHis.setDeleted(true);
                saveOpening();
            }
        } else {
            JOptionPane.showMessageDialog(Global.parentForm, "Voucher can't delete.");
        }
    }

    private void deleteTran() {
        int row = tblOpening.convertRowIndexToModel(tblOpening.getSelectedRow());
        if (row >= 0) {
            if (tblOpening.getCellEditor() != null) {
                tblOpening.getCellEditor().stopCellEditing();
            }
            int yes_no = JOptionPane.showConfirmDialog(this,
                    "Are you sure to delete?", "Opening Transaction delete.", JOptionPane.YES_NO_OPTION);
            if (yes_no == 0) {
                openingTableModel.delete(row);
                calculatAmount();
            }
        }
    }

    private void calculatAmount() {
        float ttlQty = 0.0f;
        float ttlAmt = 0.0f;
        List<OPHisDetail> listDetail = openingTableModel.getListDetail();
        if (!listDetail.isEmpty()) {
            for (OPHisDetail op : listDetail) {
                ttlQty += Util1.getFloat(op.getQty());
                ttlAmt += Util1.getFloat(op.getAmount());
            }
        }
        txtQty.setValue(ttlQty);
        txtAmount.setValue(ttlAmt);
        lblRecord.setText(String.valueOf(listDetail.size() - 1));
    }

    private void chooseFile() {
        FileDialog d = new FileDialog(Global.parentForm, "CSV FIle", FileDialog.LOAD);
        d.setDirectory("D:\\");
        d.setFile(".csv");
        d.setVisible(true);
        String directory = d.getFile();
        log.info("File Path :" + directory);
        if (directory != null) {
            readFile(d.getDirectory() + "\\" + directory);
        }
    }

    private void readFile(String path) {
        HashMap<String, Stock> hm = new HashMap<>();
        if (!listStock.isEmpty()) {
            for (Stock s : listStock) {
                hm.put(s.getUserCode(), s);
            }
        }
        String line;
        String splitBy = ",";
        int lineCount = 0;
        List<OPHisDetail> listOP = new ArrayList<>();
        try {
            try ( BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(path), "UTF8"))) {
                while ((line = br.readLine()) != null) //returns a Boolean value
                {
                    OPHisDetail op = new OPHisDetail();
                    String[] data = line.split(splitBy);    // use comma as separator
                    String stockCode = null;
                    String qty = null;
                    lineCount++;
                    try {
                        stockCode = data[1];
                        qty = data[5];

                    } catch (IndexOutOfBoundsException e) {
                        //JOptionPane.showMessageDialog(Global.parentForm, "FORMAT ERROR IN LINE:" + lineCount + e.getMessage());
                    }
                    Stock s = hm.get(stockCode);
                    if (s != null) {
                        op.setStock(s);
                        op.setQty(Util1.getFloat(qty));
                        op.setStdWt(1.0f);
                        op.setStockUnit(new StockUnit("pcs"));
                        if (op.getQty() != 0) {
                            listOP.add(op);
                        }
                    } else {
                        log.info(stockCode + "\n");
                    }
                }
                openingTableModel.setListDetail(listOP);
                calculatAmount();
            }
        } catch (IOException e) {
            log.error("Read CSV File :" + e.getMessage());

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
        jLabel8 = new javax.swing.JLabel();
        txtCurrency = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblOpening = new javax.swing.JTable();
        lblStatus = new javax.swing.JLabel();
        txtAmount = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        txtQty = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblRecord = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

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

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Currency");

        txtCurrency.setFont(Global.textFont);
        txtCurrency.setDisabledTextColor(new java.awt.Color(0, 0, 0));

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
                    .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtOPDate, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(txtOPDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtVouNo)
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel8)
                        .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtLocation, txtOPDate, txtRemark});

        tblOpening.setAutoCreateRowSorter(true);
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
        tblOpening.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblOpeningKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblOpening);

        lblStatus.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        lblStatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStatus.setText("NEW");

        txtAmount.setEditable(false);
        txtAmount.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtAmount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAmount.setFont(Global.amtFont);

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Total Amount");

        txtQty.setEditable(false);
        txtQty.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtQty.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtQty.setFont(Global.amtFont);
        txtQty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtQtyActionPerformed(evt);
            }
        });

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Total Qty");

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Record :");

        lblRecord.setFont(Global.lableFont);
        lblRecord.setText("0");

        jButton1.setText("Import");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblRecord, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1)
                        .addGap(53, 53, 53)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtQty, javax.swing.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtAmount, javax.swing.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(txtQty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(lblRecord)
                    .addComponent(jButton1))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observer.selected("control", this);
        focusOnTable();
    }//GEN-LAST:event_formComponentShown

    private void tblOpeningComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_tblOpeningComponentShown
        // TODO add your handling code here:

    }//GEN-LAST:event_tblOpeningComponentShown

    private void tblOpeningKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblOpeningKeyReleased
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            deleteTran();
        }
    }//GEN-LAST:event_tblOpeningKeyReleased

    private void txtQtyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtQtyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtQtyActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        chooseFile();
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
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblRecord;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblOpening;
    private javax.swing.JFormattedTextField txtAmount;
    private javax.swing.JTextField txtCurrency;
    private javax.swing.JTextField txtLocation;
    private com.toedter.calendar.JDateChooser txtOPDate;
    private javax.swing.JFormattedTextField txtQty;
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
    }

    @Override
    public void refresh() {
    }

    @Override
    public void filter() {
    }

    @Override
    public String panelName() {
        return this.getName();
    }

    @Override
    public void selected(Object source, Object selectObj) {
        if (source.toString().equals("OP-HISTORY")) {
            String vouNo = selectObj.toString();
            OPHis op = inventoryRepo.findOpening(vouNo);
            setVoucher(op);
        }
        if (source.toString().equals("CAL-TOTAL")) {
            calculatAmount();
        }
    }

}
