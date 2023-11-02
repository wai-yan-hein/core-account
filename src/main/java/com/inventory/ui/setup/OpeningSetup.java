/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup;

import com.common.DateLockUtil;
import com.common.DecimalFormatRender;
import com.common.ExcelExporter;
import com.common.Global;
import com.common.PanelControl;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.repo.UserRepo;
import com.common.Util1;
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.editor.StockCellEditor;
import com.inventory.model.Location;
import com.inventory.model.OPHis;
import com.inventory.model.OPHisDetail;
import com.inventory.model.OPHisKey;
import com.repo.InventoryRepo;
import com.inventory.ui.common.OpeningTableModel;
import com.inventory.ui.entry.dialog.OPHistoryDialog;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.inventory.editor.StockUnitEditor;
import com.inventory.editor.TraderAutoCompleter;
import com.toedter.calendar.JTextFieldDateEditor;
import com.user.editor.CurrencyAutoCompleter;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.task.TaskExecutor;
import reactor.core.publisher.Mono;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class OpeningSetup extends javax.swing.JPanel implements PanelControl, SelectionObserver {

    public static final int STKOPENING = 1;
    public static final int STKOPENINGPAYABLE = 2;
    private final int type;
    private final OpeningTableModel openingTableModel = new OpeningTableModel();
    private LocationAutoCompleter locationAutoCompleter;
    private CurrencyAutoCompleter currencyAAutoCompleter;
    private InventoryRepo inventoryRepo;
    private UserRepo userRepo;
    private OPHistoryDialog vouSearchDialog;
    private OPHis oPHis = new OPHis();
    private JProgressBar progress;
    private SelectionObserver observer;
    private Mono<List<Location>> monoLoc;
    private final ExcelExporter exporter = new ExcelExporter();
    private TaskExecutor taskExecutor;
    private TraderAutoCompleter traderAutoCompleter;

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    /**
     * Creates new form OpeningSetup
     *
     * @param type
     */
    public OpeningSetup(int type) {
        this.type = type;
        initComponents();
        initTextBoxFormat();
        actionMapping();
    }

    public void initMain() {
        initCompleter();
        initTable();
        txtOPDate.setDate(Util1.getTodayDate());
        txtCurrency.setEnabled(ProUtil.isMultiCur());
    }

    private void actionMapping() {
        String solve = "delete";
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        tblOpening.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, solve);
        tblOpening.getActionMap().put(solve, new DeleteAction());
    }

    private class DeleteAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            deleteTran();
        }
    }

    private void initTextBoxFormat() {
        txtQty.setFormatterFactory(Util1.getDecimalFormat());
        txtAmount.setFormatterFactory(Util1.getDecimalFormat());
        if (type == 1) {
            jLabel9.setVisible(false);
            txtCus.setVisible(false);
        } else {
            jLabel9.setVisible(true);
            txtCus.setVisible(true);
            txtCus.addFocusListener(fa);
        }
    }

    private final FocusAdapter fa = new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            if (e.getSource() instanceof JTextField txt) {
                txt.selectAll();
            } else if (e.getSource() instanceof JTextFieldDateEditor txt) {
                txt.selectAll();
            }
        }
    };

    private void initCompleter() {
        monoLoc = inventoryRepo.getLocation();
        locationAutoCompleter = new LocationAutoCompleter(txtLocation, null, false, false);
        monoLoc.subscribe((t) -> {
            locationAutoCompleter.setListLocation(t);
        });
        inventoryRepo.getDefaultLocation().doOnSuccess((tt) -> {
            locationAutoCompleter.setLocation(tt);
        }).subscribe();
        currencyAAutoCompleter = new CurrencyAutoCompleter(txtCurrency, null);
        userRepo.getCurrency().subscribe((t) -> {
            currencyAAutoCompleter.setListCurrency(t);
        });
        userRepo.getDefaultCurrency().doOnSuccess((c) -> {
            currencyAAutoCompleter.setCurrency(c);
        }).subscribe();
        traderAutoCompleter = new TraderAutoCompleter(txtCus, inventoryRepo, null, false, "CUS");
        traderAutoCompleter.setObserver(this);
        exporter.setObserver(this);
        exporter.setTaskExecutor(taskExecutor);
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
        tblOpening.getColumnModel().getColumn(1).setPreferredWidth(200);//name
        tblOpening.getColumnModel().getColumn(2).setPreferredWidth(100);//rel
        tblOpening.getColumnModel().getColumn(3).setPreferredWidth(50);//qty        
        tblOpening.getColumnModel().getColumn(4).setPreferredWidth(50);//unit
        tblOpening.getColumnModel().getColumn(5).setPreferredWidth(50);//weight
        tblOpening.getColumnModel().getColumn(6).setPreferredWidth(50);//weight_unit
        tblOpening.getColumnModel().getColumn(7).setPreferredWidth(100);//price
        tblOpening.getColumnModel().getColumn(8).setPreferredWidth(100);//amount
        tblOpening.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor(inventoryRepo));
        tblOpening.getColumnModel().getColumn(1).setCellEditor(new StockCellEditor(inventoryRepo));
        tblOpening.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());
        tblOpening.getColumnModel().getColumn(5).setCellEditor(new AutoClearEditor());//weight
        inventoryRepo.getStockUnit().subscribe((t) -> {
            tblOpening.getColumnModel().getColumn(4).setCellEditor(new StockUnitEditor(t)); // unit
            tblOpening.getColumnModel().getColumn(6).setCellEditor(new StockUnitEditor(t)); // weight_unit
        });
        tblOpening.getColumnModel().getColumn(7).setCellEditor(new AutoClearEditor());
        tblOpening.getColumnModel().getColumn(8).setCellEditor(new AutoClearEditor());
        tblOpening.setDefaultRenderer(Object.class, new DecimalFormatRender());
        tblOpening.setDefaultRenderer(Float.class, new DecimalFormatRender());
        tblOpening.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblOpening.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void clear() {
        txtOPDate.setDate(Util1.getTodayDate());
        txtRemark.setText(null);
        openingTableModel.clear();
        openingTableModel.addNewRow();
        lblStatus.setText("NEW");
        lblStatus.setForeground(Color.green);
        oPHis = new OPHis();
        txtVouNo.setText(null);
        progress.setIndeterminate(false);
        traderAutoCompleter.setTrader(null);
        disableForm(true);
        calculatAmount();
        observeMain();
    }

    private void saveOpening() {
        if (isValidEntry() && openingTableModel.isValidEntry()) {
            if (DateLockUtil.isLockDate(txtOPDate.getDate())) {
                DateLockUtil.showMessage(this);
                txtOPDate.requestFocus();
                return;
            }
            progress.setIndeterminate(true);
            observer.selected("save", false);
            if (lblStatus.getText().equals("NEW")) {
                OPHisKey key = new OPHisKey();
                key.setCompCode(Global.compCode);
                key.setVouNo(null);
                oPHis.setDeptId(Global.deptId);
                oPHis.setKey(key);
                oPHis.setCreatedBy(Global.loginUser.getUserCode());
                oPHis.setCreatedDate(LocalDateTime.now());
            } else {
                oPHis.setCreatedBy(Global.loginUser.getUserCode());
            }
            oPHis.setCurCode(currencyAAutoCompleter.getCurrency().getCurCode());
            oPHis.setVouDate(txtOPDate.getDate());
            oPHis.setRemark(txtRemark.getText());
            oPHis.setStatus(lblStatus.getText());
            oPHis.setMacId(Global.macId);
            oPHis.setLocCode(locationAutoCompleter.getLocation().getKey().getLocCode());
            oPHis.setDetailList(openingTableModel.getListDetail());
            oPHis.setListDel(openingTableModel.getDelList());
            if (txtCus.isVisible()) {

                oPHis.setTraderCode(traderAutoCompleter.getTrader().getKey().getCode());
            } else {
                oPHis.setTraderCode(null);
            }
            oPHis.setTranSource(type);
            inventoryRepo.save(oPHis).doOnSuccess((t) -> {
                clear();
            }).doOnError((e) -> {
                JOptionPane.showMessageDialog(this, e.getMessage());
                progress.setIndeterminate(false);
                observer.selected("save", true);
            }).subscribe();
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
        } else if (!Util1.isDateBetween(txtOPDate.getDate())) {
            JOptionPane.showMessageDialog(this, "Invalid Date.",
                    "Validation.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtOPDate.requestFocus();
        }
        return status;
    }

    public void historyOP() {
        try {
            if (vouSearchDialog == null) {
                vouSearchDialog = new OPHistoryDialog(Global.parentForm, type);
                vouSearchDialog.setUserRepo(userRepo);
                vouSearchDialog.setInventoryRepo(inventoryRepo);
                vouSearchDialog.setObserver(this);
                vouSearchDialog.initMain();
                vouSearchDialog.setSize(Global.width - 50, Global.height - 50);
                vouSearchDialog.setLocationRelativeTo(null);
            }
            vouSearchDialog.search();
        } catch (Exception e) {
            log.error(String.format("historyOPhistoryOP: %s", e.getMessage()));
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
        if (op != null) {
            oPHis = op;
            String vouNo = op.getKey().getVouNo();
            String compCode = op.getKey().getCompCode();
            Integer deptId = op.getDeptId();
            if (op.getTraderCode() != null) {
                inventoryRepo.findTrader(op.getTraderCode()).doOnSuccess((t) -> {
                    traderAutoCompleter.setTrader(t);
                }).subscribe();
            }
            inventoryRepo.findLocation(oPHis.getLocCode()).doOnSuccess((t) -> {
                locationAutoCompleter.setLocation(t);
            }).subscribe();
            userRepo.findCurrency(oPHis.getCurCode()).doOnSuccess((t) -> {
                currencyAAutoCompleter.setCurrency(t);
            }).subscribe();
            progress.setIndeterminate(true);
            inventoryRepo.getOpeningDetail(vouNo, compCode, deptId).subscribe((t) -> {
                txtVouNo.setText(vouNo);
                txtOPDate.setDate(oPHis.getVouDate());
                txtRemark.setText(oPHis.getRemark());
                if (oPHis.isDeleted()) {
                    lblStatus.setText("DELETED");
                    lblStatus.setForeground(Color.RED);
                    disableForm(false);
                } else if (DateLockUtil.isLockDate(oPHis.getVouDate())) {
                    lblStatus.setText(DateLockUtil.MESSAGE);
                    lblStatus.setForeground(Color.RED);
                    disableForm(false);
                } else {
                    lblStatus.setText("EDIT");
                    lblStatus.setForeground(Color.blue);
                    disableForm(true);
                }
                openingTableModel.setListDetail(t);
                openingTableModel.addNewRow();
                calculatAmount();
                focusOnTable();
                progress.setIndeterminate(false);
            }, (e) -> {
                progress.setIndeterminate(false);
                JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
            });
        }
    }

    private void disableForm(boolean status) {
        txtLocation.setEnabled(status);
        txtOPDate.setEnabled(status);
        txtRemark.setEnabled(status);
        txtVouNo.setEnabled(status);
        tblOpening.setEnabled(status);
    }

    private void deleteVoucher() {
        String status = lblStatus.getText();
        switch (status) {
            case "EDIT" -> {
                int yes_no = JOptionPane.showConfirmDialog(Global.parentForm,
                        "Are you sure to delete?", "Opening Voucher delete", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                if (yes_no == 0) {
                    inventoryRepo.delete(oPHis.getKey()).doOnSuccess((t) -> {
                        if (t) {
                            clear();
                        }
                    }).subscribe();
                }
            }
            case "DELETED" -> {
                int yes_no = JOptionPane.showConfirmDialog(this,
                        "Are you sure to restore?", "Opening Voucher Restore.", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (yes_no == 0) {
                    oPHis.setDeleted(false);
                    inventoryRepo.restore(oPHis.getKey()).doOnSuccess((t) -> {
                        lblStatus.setText("EDIT");
                        lblStatus.setForeground(Color.blue);
                        disableForm(true);
                    }).subscribe();
                }
            }
            default ->
                JOptionPane.showMessageDialog(this, "Voucher can't delete.");
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

    private void exportTemplate() {
        inventoryRepo.getStock(true).subscribe((st) -> {
            exporter.exportOpeningTemplate(st, "Opening Template");
        });
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
        List<OPHisDetail> listOP = new ArrayList<>();
        try {
            progress.setIndeterminate(true);
            Reader in = new FileReader(path);
            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setAllowMissingColumnNames(true)
                    .setIgnoreEmptyLines(true)
                    .setIgnoreHeaderCase(true)
                    .build();
            Iterable<CSVRecord> records = csvFormat.parse(in);
            records.forEach((row) -> {
                OPHisDetail op = new OPHisDetail();
                op.setStockCode(row.isMapped("SystemCode") ? Util1.convertToUniCode(row.get("SystemCode")) : "");
                op.setStockName(row.isMapped("StockName") ? Util1.convertToUniCode(row.get("StockName")) : "");
                op.setWeight(row.isMapped("Weight") ? Float.valueOf(row.get("Weight")) : 0.0f);
                op.setWeightUnit(row.isMapped("WeightUnit") ? Util1.convertToUniCode(row.get("WeightUnit")) : "");
                op.setQty(row.isMapped("Qty") ? Float.valueOf(row.get("Qty")) : 0.0f);
                op.setUnitCode(row.isMapped("Unit") ? Util1.convertToUniCode(row.get("Unit")) : "");
                op.setPrice(row.isMapped("Price") ? Float.valueOf(row.get("Price")) : 0.0f);
                op.setAmount(op.getQty() * op.getPrice());
                listOP.add(op);

            });
            openingTableModel.setListDetail(listOP);
            calculatAmount();
            progress.setIndeterminate(false);
        } catch (IOException e) {
            progress.setIndeterminate(false);
            log.error("readFile : " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Invalid Format.");
        }
    }

//    private void readFile(String path) {
//        String line;
//        String splitBy = ",";
//        int lineCount = 0;
//        List<OPHisDetail> listOP = new ArrayList<>();
//        try {
//            try (BufferedReader br = new BufferedReader(new InputStreamReader(
//                    new FileInputStream(path), "UTF8"))) {
//                while ((line = br.readLine()) != null) //returns a Boolean value
//                {
//                    OPHisDetail op = new OPHisDetail();
//                    String[] data = line.split(splitBy);    // use comma as separator
//                    String stockCode = null;
//                    String qty = null;
//                    String price = null;
//                    lineCount++;
//                    try {
//                        stockCode = data[0];
//                        qty = data[1].replace("\"", "");
//                        price = data[2].replace("\"", "");
//
//                    } catch (IndexOutOfBoundsException e) {
//                        //JOptionPane.showMessageDialog(Global.parentForm, "FORMAT ERROR IN LINE:" + lineCount + e.getMessage());
//                    }
//                    Stock s = hm.get(stockCode.toLowerCase());
//                    if (s != null) {
//                        op.setStockCode(s.getKey().getStockCode());
//                        op.setQty(Util1.getFloat(qty));
//                        op.setPrice(Util1.getFloat(price));
//                        op.setAmount(op.getQty() * op.getPrice());
//                        op.setUnitCode("pcs");
//                        if (op.getQty() != 0) {
//                            listOP.add(op);
//                        }
//                    } else {
//                        log.info(stockCode + "\n");
//                    }
//                }
//                openingTableModel.setListDetail(listOP);
//                calculatAmount();
//            }
//        } catch (IOException e) {
//            log.error("Read CSV File :" + e.getMessage());
//
//        }
//    }
    private void observeMain() {
        observer.selected("control", this);
        observer.selected("save", true);
        observer.selected("print", false);
        observer.selected("history", true);
        observer.selected("delete", true);
        observer.selected("refresh", false);
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
        lblStatus = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtCus = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblOpening = new javax.swing.JTable();
        txtAmount = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        txtQty = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblRecord = new javax.swing.JLabel();
        btnImport = new javax.swing.JButton();
        btnExport = new javax.swing.JButton();
        lblMessage = new javax.swing.JLabel();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

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

        lblStatus.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        lblStatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStatus.setText("NEW");

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Customer");

        txtCus.setFont(Global.textFont);
        txtCus.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCus.setName("txtCus"); // NOI18N
        txtCus.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCusFocusGained(evt);
            }
        });
        txtCus.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                txtCusMouseExited(evt);
            }
        });
        txtCus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCusActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCus, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtOPDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 323, Short.MAX_VALUE)
                .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtLocation, txtOPDate, txtRemark, txtVouNo});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel8, jLabel9});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(txtOPDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtVouNo)
                                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel8)
                                .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtCus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel9))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel4)
                                .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel2)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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

        btnImport.setFont(Global.lableFont);
        btnImport.setText("Import");
        btnImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImportActionPerformed(evt);
            }
        });

        btnExport.setFont(Global.lableFont);
        btnExport.setText("Export");
        btnExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportActionPerformed(evt);
            }
        });

        lblMessage.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblMessage.setText("-");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblRecord, javax.swing.GroupLayout.DEFAULT_SIZE, 411, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblMessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnExport)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnImport)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtQty, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 409, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(txtQty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(lblRecord)
                    .addComponent(btnImport)
                    .addComponent(btnExport)
                    .addComponent(lblMessage))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observeMain();
    }//GEN-LAST:event_formComponentShown

    private void tblOpeningComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_tblOpeningComponentShown
        // TODO add your handling code here:

    }//GEN-LAST:event_tblOpeningComponentShown

    private void tblOpeningKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblOpeningKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_tblOpeningKeyReleased

    private void txtQtyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtQtyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtQtyActionPerformed

    private void btnImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportActionPerformed
        // TODO add your handling code here:
        chooseFile();
    }//GEN-LAST:event_btnImportActionPerformed

    private void btnExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportActionPerformed
        exportTemplate();        // TODO add your handling code here:
    }//GEN-LAST:event_btnExportActionPerformed

    private void txtCusFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCusFocusGained
        txtCus.selectAll();
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCusFocusGained

    private void txtCusMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCusMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCusMouseExited

    private void txtCusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCusActionPerformed
        //inventoryRepo.getCustomer().subscribe()
    }//GEN-LAST:event_txtCusActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnImport;
    private javax.swing.JLabel jLabel1;
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
    private javax.swing.JLabel lblMessage;
    private javax.swing.JLabel lblRecord;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblOpening;
    private javax.swing.JFormattedTextField txtAmount;
    private javax.swing.JTextField txtCurrency;
    private javax.swing.JTextField txtCus;
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
            if (selectObj instanceof OPHis v) {
                inventoryRepo.findOpening(v.getKey()).doOnSuccess((t) -> {
                    setVoucher(t);
                }).subscribe();
            }
        } else if (source.equals(ExcelExporter.MESSAGE)) {
            lblMessage.setText(selectObj.toString());
        } else if (source.equals(ExcelExporter.FINISH)) {
            btnExport.setEnabled(true);
            lblMessage.setText(selectObj.toString());
        } else if (source.equals(ExcelExporter.ERROR)) {
            btnExport.setEnabled(true);
            lblMessage.setText(selectObj.toString());
        }
        if (source.toString().equals("CAL-TOTAL")) {
            calculatAmount();
        }
    }

}
