/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.inventory.ui.entry;

import com.common.DecimalFormatRender;
import com.common.FilterObject;
import com.common.Global;
import com.common.PanelControl;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.common.Util1;
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.editor.LocationCellEditor;
import com.inventory.editor.StockAutoCompleter;
import com.inventory.editor.StockCellEditor;
import com.inventory.editor.StockUnitEditor;
import com.inventory.editor.UnitAutoCompleter;
import com.inventory.editor.VouStatusAutoCompleter;
import com.inventory.model.Location;
import com.inventory.model.ProcessHis;
import com.inventory.model.ProcessHisDetail;
import com.inventory.model.ProcessHisKey;
import com.inventory.model.Stock;
import com.inventory.ui.common.InventoryRepo;
import com.inventory.ui.common.ProcessHisDetailTableModel;
import com.inventory.ui.common.ProcessHisTableModel;
import com.inventory.ui.setup.dialog.VouStatusSetupDialog;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.time.Duration;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
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
public class Manufacture extends javax.swing.JPanel implements PanelControl, SelectionObserver, KeyListener {

    private final Image icon = new ImageIcon(getClass().getResource("/images/setting.png")).getImage();
    @Autowired
    private InventoryRepo inventoryRepo;
    @Autowired
    private WebClient inventoryApi;
    private JProgressBar progress;
    private SelectionObserver observer;
    private final ProcessHisTableModel processHisTableModel = new ProcessHisTableModel();
    private final ProcessHisDetailTableModel processHisDetailTableModel = new ProcessHisDetailTableModel();
    private StockAutoCompleter stockAutoCompleter;
    private LocationAutoCompleter locationAutoCompleter;
    private UnitAutoCompleter unitAutoCompleter;
    private StockAutoCompleter stockAutoCompleter1;
    private LocationAutoCompleter locationAutoCompleter1;
    private VouStatusAutoCompleter vouStatusAutoCompleter;
    private VouStatusAutoCompleter vouStatusAutoCompleter1;
    private ProcessHis ph = new ProcessHis();
    private int selectRow = -1;

    /**
     * Creates new form Manufacture
     */
    public Manufacture() {
        initComponents();
        initKeyListener();
        initGroupRadio();
        actionMapping();
    }

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

    private void initGroupRadio() {
        ButtonGroup g = new ButtonGroup();
        g.add(rdoRecent);
        g.add(rdoAvg);
        g.add(rdoProRecent);
    }

    private void initDate() {
        txtStartDate.setDate(Util1.getTodayDate());
        txtEndDate.setDate(Util1.getTodayDate());
        txtFromDate.setDate(Util1.getTodayDate());
        txtToDate.setDate(Util1.getTodayDate());
    }

    private void initFomatFactory() {
        txtQty.setFormatterFactory(Util1.getDecimalFormat());
        txtAvgQty.setFormatterFactory(Util1.getDecimalFormat());
        txtPrice.setFormatterFactory(Util1.getDecimalFormat());
        txtAvgPrice.setFormatterFactory(Util1.getDecimalFormat());
        txtAmt.setFormatterFactory(Util1.getDecimalFormat());
    }

    private void initCompleter() {
        List<Location> listLocation = inventoryRepo.getLocation();
        locationAutoCompleter = new LocationAutoCompleter(txtLocation, listLocation, null, false, false);
        locationAutoCompleter.setObserver(this);
        locationAutoCompleter.setLocation(inventoryRepo.getDefaultLocation());
        stockAutoCompleter = new StockAutoCompleter(txtStock, inventoryRepo, null, false);
        stockAutoCompleter.setObserver(this);
        unitAutoCompleter = new UnitAutoCompleter(txtUnit, inventoryRepo.getStockUnit(), null);
        unitAutoCompleter.setObserver(this);
        locationAutoCompleter1 = new LocationAutoCompleter(txtLocation1, listLocation, null, true, false);
        locationAutoCompleter1.setObserver(this);
        stockAutoCompleter1 = new StockAutoCompleter(txtStock1, inventoryRepo, null, true);
        stockAutoCompleter1.setObserver(this);
        vouStatusAutoCompleter = new VouStatusAutoCompleter(txtPt, inventoryRepo, null, false);
        vouStatusAutoCompleter.setObserver(this);
        vouStatusAutoCompleter1 = new VouStatusAutoCompleter(txtPT1, inventoryRepo, null, true);
        vouStatusAutoCompleter1.setObserver(this);
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
        tblProcess.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting()) {
                if (tblProcess.getSelectedRow() >= 0) {
                    selectRow = tblProcess.convertRowIndexToModel(tblProcess.getSelectedRow());
                    ProcessHis his = processHisTableModel.getObject(selectRow);
                    setProcess(his);
                    List<ProcessHisDetail> listD = inventoryRepo.getProcessDetail(his.getKey().getVouNo());
                    processHisDetailTableModel.setListDetail(listD);
                    lblRecord.setText("Records : " + listD.size());
                    txtPrice.setEditable(listD.isEmpty());
                    processHisDetailTableModel.addNewRow();
                    focusOnTable();
                }
            }
        });
        tblProcess.setDefaultRenderer(Object.class, new TableCellRender());
        tblProcess.setDefaultRenderer(Float.class, new TableCellRender());
    }

    private void focusOnTable() {
        int rc = tblProcessDetail.getRowCount();
        if (rc > 0) {
            tblProcessDetail.setRowSelectionInterval(rc - 1, rc - 1);
            tblProcessDetail.setColumnSelectionInterval(0, 0);
            tblProcessDetail.requestFocus();
        } else {
            txtQty.requestFocus();
        }
    }

    private void actionMapping() {
        String solve = "delete";
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        tblProcessDetail.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, solve);
        tblProcessDetail.getActionMap().put(solve, new DeleteAction());

    }

    private class DeleteAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            int row = tblProcessDetail.convertRowIndexToModel(tblProcessDetail.getSelectedRow());
            if (row >= 0) {
                int status = JOptionPane.showConfirmDialog(tblProcessDetail,
                        "Are you sure to delete?", "Delete", JOptionPane.CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (status == JOptionPane.YES_OPTION) {
                    inventoryRepo.delete(processHisDetailTableModel.getObject(row).getKey());
                    processHisDetailTableModel.deleteObject(row);
                    processHisDetailTableModel.calPrice();
                }
            }
        }
    }

    private void initTblProcessDetail() {
        tblProcessDetail.setModel(processHisDetailTableModel);
        processHisDetailTableModel.setTxtVouNo(txtVouNo);
        processHisDetailTableModel.setTable(tblProcessDetail);
        processHisDetailTableModel.setInventoryRepo(inventoryRepo);
        processHisDetailTableModel.setTxtPrice(txtPrice);
        processHisDetailTableModel.setTxtAmt(txtAmt);
        processHisDetailTableModel.setTxtQty(txtQty);
        processHisDetailTableModel.setTxtVouDate(txtStartDate);
        processHisDetailTableModel.setRdoAvg(rdoAvg);
        processHisDetailTableModel.setRdoRecent(rdoRecent);
        processHisDetailTableModel.setRdoProRecent(rdoProRecent);
        processHisDetailTableModel.setLblRec(lblRecord);
        tblProcessDetail.getTableHeader().setFont(Global.tblHeaderFont);
        tblProcessDetail.setCellSelectionEnabled(true);
        tblProcessDetail.getColumnModel().getColumn(0).setPreferredWidth(20);//date
        tblProcessDetail.getColumnModel().getColumn(1).setPreferredWidth(20);//code
        tblProcessDetail.getColumnModel().getColumn(2).setPreferredWidth(200);//name
        tblProcessDetail.getColumnModel().getColumn(3).setPreferredWidth(40);//location
        tblProcessDetail.getColumnModel().getColumn(4).setPreferredWidth(40);//qty
        tblProcessDetail.getColumnModel().getColumn(5).setPreferredWidth(40);//unit
        tblProcessDetail.getColumnModel().getColumn(6).setPreferredWidth(40);//price
        tblProcessDetail.getColumnModel().getColumn(7).setPreferredWidth(40);//amount
        //
        tblProcessDetail.getColumnModel().getColumn(0).setCellEditor(new AutoClearEditor());//date
        tblProcessDetail.getColumnModel().getColumn(1).setCellEditor(new StockCellEditor(inventoryRepo));//code
        tblProcessDetail.getColumnModel().getColumn(2).setCellEditor(new StockCellEditor(inventoryRepo));//name
        tblProcessDetail.getColumnModel().getColumn(3).setCellEditor(new LocationCellEditor(inventoryRepo.getLocation()));//location
        tblProcessDetail.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());//qty
        tblProcessDetail.getColumnModel().getColumn(5).setCellEditor(new StockUnitEditor(inventoryRepo.getStockUnit()));//unit
        tblProcessDetail.getColumnModel().getColumn(6).setCellEditor(new AutoClearEditor());//price
        tblProcessDetail.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblProcessDetail.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblProcessDetail.setFont(Global.textFont);
        tblProcessDetail.setRowHeight(Global.tblRowHeight);
    }

    public void initMain() {
        initFomatFactory();
        initDate();
        initCompleter();
        initTblProcess();
        initTblProcessDetail();
        searchProcess();
    }

    private void initKeyListener() {
        txtStartDate.getDateEditor().getUiComponent().setName("txtStartDate");
        txtStartDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtEndDate.getDateEditor().getUiComponent().setName("txtEndDate");
        txtEndDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtPt.addKeyListener(this);
        txtStock.addKeyListener(this);
        txtLocation.addKeyListener(this);
        txtRemark.addKeyListener(this);
        txtProNo.addKeyListener(this);
        txtQty.addKeyListener(this);
        txtAvgQty.addKeyListener(this);
        txtUnit.addKeyListener(this);
        txtPrice.addKeyListener(this);
    }

    private void setProcess(ProcessHis p) {
        ph = p;
        txtVouNo.setText(ph.getKey().getVouNo());
        txtStartDate.setDate(ph.getVouDate());
        txtEndDate.setDate(ph.getEndDate());
        txtRemark.setText(ph.getRemark());
        txtProNo.setText(ph.getProcessNo());
        txtQty.setValue(ph.getQty());
        txtAvgQty.setValue(ph.getAvgQty());
        txtPrice.setValue(ph.getPrice());
        txtAmt.setValue(ph.getQty() * ph.getPrice());
        txtAvgPrice.setValue(ph.getAvgPrice());
        chkFinish.setSelected(ph.isFinished());
        unitAutoCompleter.setStockUnit(inventoryRepo.findUnit(ph.getUnit()));
        vouStatusAutoCompleter.setVoucher(inventoryRepo.findVouStatus(p.getPtCode()));
        stockAutoCompleter.setStock(inventoryRepo.findStock(ph.getKey().getStockCode()));
        locationAutoCompleter.setLocation(inventoryRepo.findLocation(ph.getKey().getLocCode()));
        btnDelete.setEnabled(true);
        if (p.isDeleted()) {
            enableProcess(false);
            lblStatus.setText("DELETED");
            lblStatus.setForeground(Color.red);
            btnDelete.setText("Restore");
        } else {
            enableProcess(true);
            lblStatus.setText("EDIT");
            lblStatus.setForeground(Color.blue);
            btnDelete.setText("Delete");
        }
    }

    private void enableProcess(boolean status) {
        txtStartDate.setEnabled(status);
        txtEndDate.setEnabled(status);
        txtRemark.setEditable(status);
        txtProNo.setEditable(status);
        txtQty.setEditable(status);
        txtAvgQty.setEditable(status);
        chkFinish.setEnabled(status);
        txtUnit.setEnabled(status);
        txtPt.setEnabled(status);
        txtStock.setEnabled(status);
        txtLocation.setEnabled(status);
        btnSave.setEnabled(status);
    }

    private void vouStatusSetup() {
        VouStatusSetupDialog vsDialog = new VouStatusSetupDialog();
        vsDialog.setIconImage(icon);
        vsDialog.setInventoryRepo(inventoryRepo);
        vsDialog.setListVou(inventoryRepo.getVoucherStatus());
        vsDialog.initMain();
        vsDialog.setSize(Global.width / 2, Global.height / 2);
        vsDialog.setLocationRelativeTo(null);
        vsDialog.setVisible(true);
    }

    private void searchProcess() {
        FilterObject f = new FilterObject(Global.compCode, Global.deptId);
        f.setFromDate(Util1.toDateStr(txtFromDate.getDate(), "yyyy-MM-dd"));
        f.setToDate(Util1.toDateStr(txtToDate.getDate(), "yyyy-MM-dd"));
        f.setProcessNo(txtProNo1.getText());
        f.setRemark(txtRemark1.getText());
        f.setVouNo(txtVouNo1.getText());
        f.setStockCode(stockAutoCompleter1.getStock().getKey().getStockCode());
        f.setVouStatus(vouStatusAutoCompleter1.getVouStatus().getKey().getCode());
        f.setLocCode(locationAutoCompleter1.getLocation().getKey().getLocCode());
        f.setFinished(chkFinish1.isSelected());
        f.setDeleted(chkDel.isSelected());
        Mono<ResponseEntity<List<ProcessHis>>> result = inventoryApi
                .post()
                .uri("/process/get-process")
                .body(Mono.just(f), FilterObject.class
                )
                .retrieve()
                .toEntityList(ProcessHis.class
                );
        List<ProcessHis> listOP = result.block(Duration.ofMinutes(1)).getBody();
        processHisTableModel.setListDetail(listOP);
    }

    private void saveProcess() {
        if (isValidProcess()) {
            if (chkFinish.isSelected()) {
                int s = JOptionPane.showConfirmDialog(this,
                        String.format("Production end date is %s", Util1.toDateStr(ph.getEndDate(), "dd/MM/yyyy")),
                        "Warning", JOptionPane.WARNING_MESSAGE);
                if (s == JOptionPane.YES_OPTION) {
                    ph = inventoryRepo.saveProcess(ph);
                    fixFilter();
                    clear();
                } else {
                    txtEndDate.requestFocusInWindow();
                }
            } else {
                ph = inventoryRepo.saveProcess(ph);
                fixFilter();
                clear();
            }
        }
    }

    private void fixFilter() {
        if (chkFinish.isSelected() || chkFinish1.isSelected()) {
            processHisTableModel.deleteObject(selectRow);
        }
    }

    private void clear() {
        lblStatus.setText("NEW");
        lblStatus.setForeground(Color.green);
        txtVouNo.setText(null);
        chkFinish.setSelected(false);
        txtStock.setText(null);
        txtRemark.setText(null);
        txtProNo.setText(null);
        txtQty.setValue(null);
        txtAvgQty.setValue(null);
        txtPrice.setValue(null);
        txtAvgPrice.setValue(null);
        btnDelete.setEnabled(false);
        btnDelete.setText("Delete");
        searchProcess();
        processHisDetailTableModel.clear();
        txtStartDate.requestFocusInWindow();
    }

    private boolean isValidProcess() {
        if (vouStatusAutoCompleter.getVouStatus() == null) {
            JOptionPane.showMessageDialog(this, "Choose Process Type.");
            txtPt.requestFocus();
            return false;
        } else if (stockAutoCompleter.getStock() == null) {
            JOptionPane.showMessageDialog(this, "Choose Stock.");
            txtStock.requestFocus();
            return false;
        } else if (locationAutoCompleter.getLocation() == null) {
            JOptionPane.showMessageDialog(this, "Choose Location.");
            txtLocation.requestFocus();
            return false;
        } else if (unitAutoCompleter.getStockUnit() == null) {
            JOptionPane.showMessageDialog(this, "Choose Unit.");
            txtUnit.requestFocus();
            return false;
        } else {
            ProcessHisKey key = new ProcessHisKey();
            key.setVouNo(txtVouNo.getText());
            key.setCompCode(Global.compCode);
            key.setDeptId(Global.deptId);
            key.setLocCode(locationAutoCompleter.getLocation().getKey().getLocCode());
            key.setStockCode(stockAutoCompleter.getStock().getKey().getStockCode());
            ph.setKey(key);
            ph.setVouDate(txtStartDate.getDate());
            ph.setEndDate(txtEndDate.getDate());
            ph.setRemark(txtRemark.getText());
            ph.setProcessNo(txtProNo.getText());
            ph.setQty(Util1.getFloat(txtQty.getValue()));
            ph.setAvgQty(Util1.getFloat(txtAvgQty.getValue()));
            ph.setPrice(Util1.getFloat(txtPrice.getValue()));
            ph.setAvgPrice(Util1.getFloat(txtAvgPrice.getValue()));
            ph.setPtCode(vouStatusAutoCompleter.getVouStatus().getKey().getCode());
            ph.setUnit(unitAutoCompleter.getStockUnit().getKey().getUnitCode());
            ph.setFinished(chkFinish.isSelected());
            ph.setDeleted(false);
            ph.setMacId(Global.macId);
            if (lblStatus.getText().equals("NEW")) {
                ph.setCreatedBy(Global.loginUser.getUserCode());
            } else {
                ph.setUpdatedBy(Global.loginUser.getUserCode());
            }
        }
        return true;
    }

    private void calAvgPrice() {
        float qty = Util1.getFloat(txtQty.getValue());
        float avgQty = Util1.getFloat(txtAvgQty.getValue());
        float price = Util1.getFloat(txtPrice.getValue());
        float avgPrice = (qty / avgQty) * price;
        txtAvgPrice.setValue(avgPrice);
    }

    private void deleteProcess() {
        String name = btnDelete.getText();
        if (name.equals("Delete")) {
            int s = JOptionPane.showConfirmDialog(this, "Are you sure to delete?", "Process Delete.", JOptionPane.ERROR_MESSAGE);
            if (s == JOptionPane.YES_OPTION) {
                ph.setDeleted(true);
                inventoryRepo.delete(ph.getKey());
                processHisTableModel.deleteObject(selectRow);
                clear();
            }
        } else {
            int s = JOptionPane.showConfirmDialog(this, "Are you sure to restore?", "Process Restore.", JOptionPane.WARNING_MESSAGE);
            if (s == JOptionPane.YES_OPTION) {
                ph.setDeleted(false);
                inventoryRepo.restore(ph.getKey());
                lblStatus.setText("EDIT");
                lblStatus.setForeground(Color.blue);
                btnDelete.setText("Delete");
                enableProcess(true);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Evgditor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtProNo1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtRemark1 = new javax.swing.JTextField();
        txtStock1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        chkFinish1 = new javax.swing.JCheckBox();
        jLabel18 = new javax.swing.JLabel();
        txtFromDate = new com.toedter.calendar.JDateChooser();
        jLabel19 = new javax.swing.JLabel();
        txtToDate = new com.toedter.calendar.JDateChooser();
        jLabel20 = new javax.swing.JLabel();
        txtVouNo1 = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        txtPT1 = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        txtLocation1 = new javax.swing.JTextField();
        chkDel = new javax.swing.JCheckBox();
        jButton4 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblProcess = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        panelProcess = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txtVouNo = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtPt = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtStartDate = new com.toedter.calendar.JDateChooser();
        txtEndDate = new com.toedter.calendar.JDateChooser();
        jLabel9 = new javax.swing.JLabel();
        txtStock = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtLocation = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txtUnit = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        chkFinish = new javax.swing.JCheckBox();
        btnSave = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txtProNo = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        txtQty = new javax.swing.JFormattedTextField();
        txtPrice = new javax.swing.JFormattedTextField();
        btnDelete = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        txtAvgQty = new javax.swing.JFormattedTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        txtAvgPrice = new javax.swing.JFormattedTextField();
        jLabel25 = new javax.swing.JLabel();
        txtAmt = new javax.swing.JFormattedTextField();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblProcessDetail = new javax.swing.JTable();
        rdoRecent = new javax.swing.JRadioButton();
        rdoAvg = new javax.swing.JRadioButton();
        rdoProRecent = new javax.swing.JRadioButton();
        jLabel24 = new javax.swing.JLabel();
        lblRecord = new javax.swing.JLabel();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Search Production History", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, Global.menuFont, Global.selectionColor));

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Process No");

        txtProNo1.setFont(Global.textFont);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Remark");

        txtRemark1.setFont(Global.textFont);

        txtStock1.setFont(Global.textFont);

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Stock");

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Status");

        chkFinish1.setText("Finished");

        jLabel18.setFont(Global.lableFont);
        jLabel18.setText("Start Date");

        txtFromDate.setDateFormatString("dd/MM/yyyy");
        txtFromDate.setFont(Global.textFont);

        jLabel19.setFont(Global.lableFont);
        jLabel19.setText("End Date");

        txtToDate.setDateFormatString("dd/MM/yyyy");
        txtToDate.setFont(Global.textFont);

        jLabel20.setFont(Global.lableFont);
        jLabel20.setText("Vou No");

        txtVouNo1.setFont(Global.textFont);

        jLabel21.setFont(Global.lableFont);
        jLabel21.setText("Process Type");

        txtPT1.setFont(Global.textFont);

        jLabel22.setFont(Global.lableFont);
        jLabel22.setText("Location");

        txtLocation1.setFont(Global.textFont);

        chkDel.setText("Deleted");

        jButton4.setFont(Global.lableFont);
        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/search.png"))); // NOI18N
        jButton4.setText("Search");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel22, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel21))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtProNo1)
                    .addComponent(txtRemark1)
                    .addComponent(txtStock1)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(txtFromDate, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtToDate, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE))
                    .addComponent(txtVouNo1)
                    .addComponent(txtPT1)
                    .addComponent(txtLocation1)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(chkFinish1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkDel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton4)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtFromDate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtProNo1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtRemark1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(txtVouNo1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtStock1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(txtPT1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(txtLocation1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jButton4)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(chkDel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(chkFinish1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
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
        jScrollPane1.setViewportView(tblProcess);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 458, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        panelProcess.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Prodcution Stock", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, Global.menuFont, Global.selectionColor));
        panelProcess.setFocusCycleRoot(true);
        panelProcess.setFocusTraversalPolicyProvider(true);

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Vou No");

        txtVouNo.setEditable(false);
        txtVouNo.setFont(Global.textFont);

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Process Type");

        txtPt.setFont(Global.textFont);
        txtPt.setName("txtPt"); // NOI18N

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Start Date");

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("End Date");

        txtStartDate.setDateFormatString("dd/MM/yyyy");
        txtStartDate.setFont(Global.textFont);

        txtEndDate.setDateFormatString("dd/MM/yyyy");
        txtEndDate.setFont(Global.textFont);

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Stock");

        txtStock.setFont(Global.textFont);
        txtStock.setName("txtStock"); // NOI18N

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Location");

        txtLocation.setFont(Global.textFont);
        txtLocation.setName("txtLocation"); // NOI18N

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Remark");

        txtRemark.setFont(Global.textFont);
        txtRemark.setName("txtRemark"); // NOI18N

        jLabel12.setFont(Global.lableFont);
        jLabel12.setText("Qty");

        txtUnit.setFont(Global.textFont);
        txtUnit.setKeymap(null);
        txtUnit.setName("txtUnit"); // NOI18N

        jLabel14.setFont(Global.lableFont);
        jLabel14.setText("Price");

        jLabel13.setFont(Global.lableFont);
        jLabel13.setText("Unit");

        jLabel15.setFont(Global.lableFont);
        jLabel15.setText("Status");

        chkFinish.setText("Finished");

        btnSave.setFont(Global.lableFont);
        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        lblStatus.setFont(Global.lableFont);
        lblStatus.setText("NEW");

        jLabel17.setFont(Global.lableFont);
        jLabel17.setText("Process No");

        txtProNo.setFont(Global.textFont);
        txtProNo.setName("txtProNo"); // NOI18N

        jButton2.setBackground(Global.selectionColor);
        jButton2.setFont(Global.lableFont);
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("...");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        txtQty.setFont(Global.textFont);
        txtQty.setName("txtQty"); // NOI18N

        txtPrice.setFont(Global.amtFont);
        txtPrice.setName("txtPrice"); // NOI18N

        btnDelete.setFont(Global.lableFont);
        btnDelete.setText("Delete");
        btnDelete.setEnabled(false);
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Weight Loss", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, Global.lableFont, Global.selectionColor));

        txtAvgQty.setFont(Global.textFont);
        txtAvgQty.setName("txtAvgQty"); // NOI18N
        txtAvgQty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAvgQtyActionPerformed(evt);
            }
        });

        jLabel16.setFont(Global.lableFont);
        jLabel16.setText("Qty");

        jLabel23.setFont(Global.lableFont);
        jLabel23.setText("Price");

        txtAvgPrice.setEditable(false);
        txtAvgPrice.setFont(Global.amtFont);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtAvgPrice, javax.swing.GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE)
                    .addComponent(txtAvgQty))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(txtAvgQty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtAvgPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23))
                .addContainerGap())
        );

        jLabel25.setFont(Global.lableFont);
        jLabel25.setText("Amount");

        txtAmt.setEditable(false);
        txtAmt.setFont(Global.amtFont);

        javax.swing.GroupLayout panelProcessLayout = new javax.swing.GroupLayout(panelProcess);
        panelProcess.setLayout(panelProcessLayout);
        panelProcessLayout.setHorizontalGroup(
            panelProcessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelProcessLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelProcessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelProcessLayout.createSequentialGroup()
                        .addGroup(panelProcessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(11, 11, 11)
                        .addComponent(chkFinish, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblStatus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSave))
                    .addGroup(panelProcessLayout.createSequentialGroup()
                        .addGroup(panelProcessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(11, 11, 11)
                        .addGroup(panelProcessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtUnit, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtQty, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtRemark, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtVouNo, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtStartDate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
                            .addComponent(txtStock, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPrice)
                            .addComponent(txtAmt))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelProcessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelProcessLayout.createSequentialGroup()
                                .addGroup(panelProcessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelProcessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtEndDate, javax.swing.GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE)
                                    .addComponent(txtLocation, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txtProNo, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(panelProcessLayout.createSequentialGroup()
                                        .addComponent(txtPt, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        panelProcessLayout.setVerticalGroup(
            panelProcessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelProcessLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelProcessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(txtPt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelProcessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtStartDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtEndDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelProcessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(txtLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelProcessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(txtProNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelProcessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelProcessLayout.createSequentialGroup()
                        .addGroup(panelProcessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(txtQty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelProcessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(txtUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelProcessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelProcessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel25)))
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelProcessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(chkFinish)
                    .addComponent(btnSave)
                    .addComponent(lblStatus)
                    .addComponent(btnDelete))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Stock Value Added", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, Global.menuFont, Global.selectionColor));

        tblProcessDetail.setModel(new javax.swing.table.DefaultTableModel(
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
        tblProcessDetail.setShowHorizontalLines(true);
        tblProcessDetail.setShowVerticalLines(true);
        jScrollPane2.setViewportView(tblProcessDetail);

        rdoRecent.setSelected(true);
        rdoRecent.setText("Purchase Recent");

        rdoAvg.setText("Purchase Avg");

        rdoProRecent.setText("Production Recent");

        jLabel24.setFont(Global.lableFont);
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel24.setText("Price");

        lblRecord.setFont(Global.lableFont);
        lblRecord.setText("Records : 0");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 789, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(lblRecord, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rdoProRecent)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rdoAvg)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rdoRecent)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(rdoRecent)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rdoAvg)
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(rdoProRecent)
                                .addComponent(jLabel24)
                                .addComponent(lblRecord)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelProcess, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelProcess, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observer.selected("control", this);
        txtStartDate.requestFocusInWindow();
    }//GEN-LAST:event_formComponentShown

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        vouStatusSetup();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        deleteProcess();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        saveProcess();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        searchProcess();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void txtAvgQtyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAvgQtyActionPerformed
        // TODO add your handling code here:
        calAvgPrice();
    }//GEN-LAST:event_txtAvgQtyActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox chkDel;
    private javax.swing.JCheckBox chkFinish;
    private javax.swing.JCheckBox chkFinish1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblRecord;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPanel panelProcess;
    private javax.swing.JRadioButton rdoAvg;
    private javax.swing.JRadioButton rdoProRecent;
    private javax.swing.JRadioButton rdoRecent;
    private javax.swing.JTable tblProcess;
    private javax.swing.JTable tblProcessDetail;
    private javax.swing.JFormattedTextField txtAmt;
    private javax.swing.JFormattedTextField txtAvgPrice;
    private javax.swing.JFormattedTextField txtAvgQty;
    private com.toedter.calendar.JDateChooser txtEndDate;
    private com.toedter.calendar.JDateChooser txtFromDate;
    private javax.swing.JTextField txtLocation;
    private javax.swing.JTextField txtLocation1;
    private javax.swing.JTextField txtPT1;
    private javax.swing.JFormattedTextField txtPrice;
    private javax.swing.JTextField txtProNo;
    private javax.swing.JTextField txtProNo1;
    private javax.swing.JTextField txtPt;
    private javax.swing.JFormattedTextField txtQty;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JTextField txtRemark1;
    private com.toedter.calendar.JDateChooser txtStartDate;
    private javax.swing.JTextField txtStock;
    private javax.swing.JTextField txtStock1;
    private com.toedter.calendar.JDateChooser txtToDate;
    private javax.swing.JTextField txtUnit;
    private javax.swing.JTextField txtVouNo;
    private javax.swing.JTextField txtVouNo1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void save() {
        saveProcess();
    }

    @Override
    public void delete() {
        deleteProcess();
    }

    @Override
    public void newForm() {
        clear();
    }

    @Override
    public void history() {
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
        String str = source.toString();
        if (str.equals("STOCK")) {
            Stock s = stockAutoCompleter.getStock();
            if (s != null) {
                unitAutoCompleter.setStockUnit(inventoryRepo.findUnit(s.getPurUnitCode()));
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        Object sourceObj = e.getSource();
        String ctrlName = "-";
        if (sourceObj instanceof JTextField jTextField) {
            ctrlName = jTextField.getName();
        } else if (sourceObj instanceof JTextFieldDateEditor jTextFieldDateEditor) {
            ctrlName = jTextFieldDateEditor.getName();
        } else if (sourceObj instanceof JFormattedTextField j) {
            ctrlName = j.getName();
        }
        switch (ctrlName) {
            case "txtStartDate" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtStock.requestFocus();
                }
            }
            case "txtStock" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtRemark.requestFocus();
                }
            }
            case "txtRemark" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtQty.requestFocus();
                }
            }
            case "txtQty" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    processHisDetailTableModel.calPrice();
                    txtUnit.requestFocus();
                }
            }
            case "txtPrice" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    processHisDetailTableModel.calPrice();
                    txtPt.requestFocus();
                }
            }
            case "txtUnit" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtPt.requestFocus();
                }
            }
            case "txtPt" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtEndDate.requestFocusInWindow();
                }
            }
            case "txtEndDate" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtLocation.requestFocus();
                }
            }
            case "txtLocation" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtProNo.requestFocus();
                }
            }
            case "txtProNo" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnSave.requestFocus();
                }
            }
        }
    }

}
