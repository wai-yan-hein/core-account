/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.inventory.ui.entry;

import com.acc.dialog.FindDialog;
import com.common.*;
import com.common.ComponentUtil;
import com.common.DateLockUtil;
import com.common.DecimalFormatRender;
import com.common.FontCellRender;
import com.common.Global;
import com.common.PanelControl;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.editor.StockAutoCompleter;
import com.inventory.editor.TraderAutoCompleter;
import com.inventory.entity.Stock;
import com.inventory.entity.Trader;
import com.inventory.entity.WeightHis;
import com.inventory.entity.WeightHisDetail;
import com.inventory.entity.WeightHisKey;
import com.inventory.entity.WeightStatus;
import com.inventory.ui.common.WeightDetailTableModel;
import com.repo.InventoryRepo;
import com.inventory.ui.entry.dialog.WeightHistoryDialog;
import com.user.editor.AutoClearEditor;
import com.toedter.calendar.JTextFieldDateEditor;
import com.repo.UserRepo;
import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.swing.AbstractAction;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author DELL
 */
@Slf4j
public class WeightEntry extends javax.swing.JPanel implements SelectionObserver, PanelControl, KeyListener {

    @Setter
    private SelectionObserver observer;
    @Setter
    private JProgressBar progress;
    @Setter
    private InventoryRepo inventoryRepo;
    @Setter
    private UserRepo userRepo;
    private TraderAutoCompleter traderAutoCompleter;
    private StockAutoCompleter stockAutoCompleter;

    private final WeightDetailTableModel tableModel = new WeightDetailTableModel();
    private WeightHistoryDialog dialog;
    private WeightHis his = new WeightHis();
    private FindDialog findDialog;

    /**
     * Creates new form Batch
     */
    public WeightEntry() {
        initComponents();
        initKeyListener();
        initTextBox();
        actionMapping();
        ComponentUtil.addFocusListener(panelHeader);
        txtTrader.requestFocus();
    }

    public void initMain() {
        initCombo();
        initTable();
        initModel();
        initRowHeader();
        initFind();
        assignDefaultValue();
    }

    private void initFind() {
        findDialog = new FindDialog(Global.parentForm, tblWeight);
    }

    private void initRowHeader() {
        RowHeader header = new RowHeader();
        JList list = header.createRowHeader(tblWeight, 30);
        scroll.setRowHeaderView(list);
    }

    private void initTextBox() {
        ComponentUtil.setTextProperty(this);
    }

    private void initKeyListener() {
        txtDate.getDateEditor().getUiComponent().setName("txtDate");
        txtDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtTrader.addKeyListener(this);
        txtRemark.addKeyListener(this);
        txtStock.addKeyListener(this);
    }

    private void initCombo() {
        traderAutoCompleter = new TraderAutoCompleter(txtTrader, inventoryRepo, null, false, "-");
        traderAutoCompleter.setObserver(this);
        stockAutoCompleter = new StockAutoCompleter(txtStock, inventoryRepo, null, false, ProUtil.isSSContain());
        stockAutoCompleter.setObserver(this);
    }

    private void assignDefaultValue() {
        txtDate.setDate(Util1.getTodayDate());
        inventoryRepo.getDefaultSupplier().doOnSuccess((t) -> {
            traderAutoCompleter.setTrader(t);
        }).subscribe();
        progress.setIndeterminate(false);
    }

    private void actionMapping() {
        String solve = "delete";
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        tblWeight.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, solve);
        tblWeight.getActionMap().put(solve, new DeleteAction());

    }

    private class DeleteAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            deleteTran();
        }
    }

    private void deleteTran() {
        int row = tblWeight.convertRowIndexToModel(tblWeight.getSelectedRow());
        if (row >= 0) {
            if (tblWeight.getCellEditor() != null) {
                tblWeight.getCellEditor().stopCellEditing();
            }
            int yes_no = JOptionPane.showConfirmDialog(this,
                    "Are you sure to delete?", " Transaction delete.", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (yes_no == 0) {
                tableModel.removeRow(row);
                calTotal();
                focusTable();
            }
        }
    }

    private void initTable() {
        tableModel.setObserver(this);
        tableModel.setTable(tblWeight);
        tableModel.addNewRow(false);
        tblWeight.getTableHeader().setFont(Global.tblHeaderFont);
        tblWeight.setCellSelectionEnabled(true);
        tblWeight.setRowHeight(Global.tblRowHeight);
        tblWeight.setShowGrid(true);
        tblWeight.setFont(Global.textFont);
        tblWeight.setDefaultRenderer(Object.class, new DecimalFormatRender(2));
        tblWeight.setDefaultRenderer(Double.class, new DecimalFormatRender(2));
        tblWeight.setDefaultEditor(Object.class, new AutoClearEditor());
        tblWeight.setDefaultEditor(Double.class, new AutoClearEditor());
        tblWeight.getColumnModel().getColumn(0).setCellRenderer(new FontCellRender());
        tblWeight.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblWeight.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    }

    public boolean saveWeight(boolean print) {
        boolean status = false;
        try {
            if (isValidEntry()) {
                if (DateLockUtil.isLockDate(txtDate.getDate())) {
                    DateLockUtil.showMessage(this);
                    txtDate.requestFocus();
                    return false;
                }
                progress.setIndeterminate(true);
                observer.selected("save", false);
                his.setListDetail(getListDetail());
                inventoryRepo.saveWeight(his).doOnSuccess((t) -> {
                    if (print) {
                        printWeightVoucher(t);
                    } else {
                        clear(true);
                    }
                }).doOnError((e) -> {
                    JOptionPane.showMessageDialog(this, e.getMessage());
                    progress.setIndeterminate(false);
                    observer.selected("save", false);
                }).subscribe();

            }
        } catch (HeadlessException ex) {
            log.error("savePur :" + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Could not saved.");
        }
        return status;
    }

    private boolean isValidEntry() {
        boolean status = true;
        WeightStatus weightStatus = (WeightStatus) cboStatus.getSelectedItem();
        if (lblStatus.getText().equals("DELETED")) {
            status = false;
            clear(true);
        } else if (weightStatus == null) {
            JOptionPane.showMessageDialog(this, "Choose.",
                    "Choose Weighting For.", JOptionPane.WARNING_MESSAGE);
            cboStatus.requestFocus();
            return false;
        } else if (Objects.isNull(traderAutoCompleter.getTrader())) {
            JOptionPane.showMessageDialog(this, "Choose Supplier.",
                    "Choose Supplier.", JOptionPane.ERROR_MESSAGE);
            txtTrader.requestFocus();
            return false;
        } else if (Objects.isNull(stockAutoCompleter.getStock())) {
            JOptionPane.showMessageDialog(this, "Choose Stock.",
                    "Choose Stock.", JOptionPane.ERROR_MESSAGE);
            txtStock.requestFocus();
            return false;
        } else if (!Util1.isDateBetween(txtDate.getDate())) {
            JOptionPane.showMessageDialog(this, "Invalid Date.",
                    "Validation.", JOptionPane.ERROR_MESSAGE);
            txtDate.requestFocus();
            return false;
        } else {
            his.setTranSource(weightStatus.name());
            his.setMacId(Global.macId);
            his.setVouDate(Util1.convertToLocalDateTime(txtDate.getDate()));
            his.setTraderCode(traderAutoCompleter.getTrader().getKey().getCode());
            his.setStockCode(stockAutoCompleter.getStock().getKey().getStockCode());
            his.setRemark(txtRemark.getText());
            his.setDescription(txtDesp.getText());
            his.setTotalBag(Util1.getDouble(txtBag.getValue()));
            his.setTotalQty(Util1.getDouble(txtQty.getValue()));
            his.setTotalWeight(Util1.getDouble(txtWeightTotal.getValue()));
            his.setWeight(Util1.getDouble(txtWeight.getValue()));
            his.setDraft(chkDraft.isSelected());
            if (lblStatus.getText().equals("NEW")) {
                WeightHisKey key = new WeightHisKey();
                key.setCompCode(Global.compCode);
                key.setVouNo(null);
                his.setKey(key);
                his.setDeptId(Global.deptId);
                his.setCreatedDate(LocalDateTime.now());
                his.setCreatedBy(Global.loginUser.getUserCode());
            } else {
                his.setUpdatedBy(Global.loginUser.getUserCode());
            }
        }
        return status;
    }

    private void clearTableModel() {
        tableModel.clear();
        tableModel.addNewRow(false);
    }

    private void clearTextBox() {
        lblRec.setText("0");
        txtQty.setValue(0);
        txtWeight.setValue(0);
        txtBag.setValue(0);
        txtWeightTotal.setValue(0);
        lblStatus.setText("NEW");
        lblStatus.setForeground(Color.GREEN);
        progress.setIndeterminate(false);
        txtVouNo.setText(null);
        traderAutoCompleter.setTrader(null);
        stockAutoCompleter.setStock(null);
        txtRemark.setText(null);
        txtDesp.setText(null);
        chkDraft.setSelected(false);
        progress.setIndeterminate(false);
    }

    private void clear(boolean focus) {
        his = new WeightHis();
        assignDefaultValue();
        disableForm(true);
        clearTableModel();
        clearTextBox();
        if (focus) {
            txtTrader.requestFocus();
        }
    }

    private void disableForm(boolean status) {
        ComponentUtil.enableForm(this, status);
        observer.selected("save", status);
        observer.selected("print", status);
        observer.selected("deleted", status);
    }

    private void printWeightVoucher(WeightHis his) {
        String vouNo = his.getKey().getVouNo();
        inventoryRepo.getWeightColumn(vouNo).doOnSuccess((t) -> {
            try {
                String reportName = "WeightVoucher";
                Map<String, Object> param = getDefaultParam(his);
                String reportPath = String.format("report%s%s", File.separator, reportName.concat(".jasper"));
                ByteArrayInputStream stream = new ByteArrayInputStream(Util1.listToByteArray(t));
                JsonDataSource ds = new JsonDataSource(stream);
                JasperPrint main = JasperFillManager.fillReport(reportPath, param, ds);
                JasperViewer.viewReport(main, false);
            } catch (JRException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }).doOnTerminate(() -> {
            clear(false);
        }).subscribe();
    }

    private Map<String, Object> getDefaultParam(WeightHis p) {
        Map<String, Object> param = new HashMap<>();
        param.put("p_print_date", Util1.getTodayDateTime());
        param.put("p_comp_name", Global.companyName);
        param.put("p_comp_address", Global.companyAddress);
        param.put("p_comp_phone", Global.companyPhone);
        param.put("p_logo_path", ProUtil.logoPath());
        param.put("p_remark", p.getRemark());
        param.put("p_vou_no", p.getKey().getVouNo());
        param.put("p_vou_date", Util1.getDate(p.getVouDate()));
        param.put("p_vou_time", Util1.getTime(p.getVouDate()));
        param.put("p_created_name", Global.hmUser.get(p.getCreatedBy()));
        String type = WeightStatus.values()[cboStatus.getSelectedIndex()].name();
        log.info(type);
        param.put("p_type", type);
        Trader t = traderAutoCompleter.getTrader();
        if (t != null) {
            param.put("p_trader_name", Util1.isNull(p.getDescription(), t.getTraderName()));
            param.put("p_trader_address", t.getAddress());
            param.put("p_trader_phone", t.getRemark());
        }
        return param;
    }

    private void focusTable() {
        int rc = tblWeight.getRowCount();
        if (rc >= 1) {
            tblWeight.setRowSelectionInterval(rc - 1, rc - 1);
            tblWeight.setColumnSelectionInterval(0, 0);
            tblWeight.requestFocus();
        } else {
            txtDate.requestFocusInWindow();
        }
    }

    private void historyWeight() {
        if (dialog == null) {
            dialog = new WeightHistoryDialog(Global.parentForm);
            dialog.setInventoryRepo(inventoryRepo);
            dialog.setUserRepo(userRepo);
            dialog.setObserver(this);
            dialog.initMain();
            dialog.setSize(Global.width - 20, Global.height - 20);
            dialog.setLocationRelativeTo(null);
        }
        dialog.search();
    }

    public void setVoucher(WeightHis g) {
        if (g != null) {
            progress.setIndeterminate(true);
            his = g;
            inventoryRepo.findTrader(his.getTraderCode()).doOnSuccess((t) -> {
                traderAutoCompleter.setTrader(t);
            }).subscribe();
            inventoryRepo.findStock(his.getStockCode()).doOnSuccess((t) -> {
                stockAutoCompleter.setStock(t);
            }).subscribe();
            if (his.isPost()) {
                lblStatus.setText("This voucher can't edit");
                lblStatus.setForeground(Color.RED);
                disableForm(false);
                observer.selected("print", true);
            } else if (his.isDeleted()) {
                lblStatus.setText("DELETED");
                lblStatus.setForeground(Color.RED);
                disableForm(false);
                observer.selected("delete", true);
            } else if (DateLockUtil.isLockDate(his.getVouDate())) {
                lblStatus.setText(DateLockUtil.MESSAGE);
                lblStatus.setForeground(Color.RED);
                disableForm(false);
            } else {
                lblStatus.setText("EDIT");
                lblStatus.setForeground(Color.blue);
                disableForm(true);
            }
            String vouNo = his.getKey().getVouNo();
            txtVouNo.setText(vouNo);
            txtRemark.setText(his.getRemark());
            txtDate.setDate(Util1.convertToDate(his.getVouDate()));
            cboStatus.setSelectedItem(WeightStatus.valueOf(his.getTranSource()));
            txtWeight.setValue(his.getWeight());
            txtQty.setValue(his.getTotalQty());
            txtBag.setValue(his.getTotalBag());
            txtWeightTotal.setValue(his.getTotalWeight());
            txtDesp.setText(his.getDescription());
            chkDraft.setSelected(his.isDraft());
            inventoryRepo.getWeightDetail(vouNo).doOnSuccess((t) -> {
                setData(t);
            }).doOnTerminate(() -> {
                focusTable();
                progress.setIndeterminate(false);
            }).subscribe();

        }
    }

    private void deleteVoucher() {
        String status = lblStatus.getText();
        switch (status) {
            case "EDIT" -> {
                int yes_no = JOptionPane.showConfirmDialog(this,
                        "Are you sure to delete?", "Weight Voucher Delete.", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                if (yes_no == 0) {
                    inventoryRepo.delete(his.getKey()).doOnSuccess((t) -> {
                        clear(true);
                    }).subscribe();
                }
            }
            case "DELETED" -> {
                int yes_no = JOptionPane.showConfirmDialog(this,
                        "Are you sure to restore?", "Weight Voucher Restore.", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (yes_no == 0) {
                    his.setDeleted(false);
                    inventoryRepo.restore(his.getKey()).doOnSuccess((t) -> {
                        if (t) {
                            lblStatus.setText("EDIT");
                            lblStatus.setForeground(Color.blue);
                            disableForm(true);
                        }
                    }).subscribe();

                }
            }
            default ->
                JOptionPane.showMessageDialog(this, "Voucher can't delete.");
        }
    }

    private void calTotal() {
        int rowCount = tblWeight.getRowCount();
        int colCount = tblWeight.getColumnCount();
        double ttlWt = 0;
        for (int row = 0; row < rowCount; row++) {
            ttlWt += Util1.getDouble(tblWeight.getValueAt(row, colCount - 1));
        }
        double weight = Util1.getDouble(txtWeight.getValue());
        if (weight > 0) {
            double qty = ttlWt / weight;
            txtQty.setValue(Util1.roundDown2D(qty));
        }
        txtWeightTotal.setValue(ttlWt);
        txtBag.setValue(getListDetail().size());
        lblRec.setText(String.valueOf(rowCount));
    }

    private void observeMain() {
        observer.selected("control", this);
        observer.selected("save", true);
        observer.selected("print", true);
        observer.selected("history", true);
        observer.selected("delete", true);
        observer.selected("refresh", true);
    }

    private void initModel() {
        for (int i = 1; i <= 15; i++) {
            tableModel.addColumn(i);
        }
        tableModel.addColumn("Total");
        tblWeight.setModel(tableModel);
    }

    private void setData(List<WeightHisDetail> list) {
        tableModel.clear();
        if (list != null) {
            int rowCount = list.size() / 15;
            int remainingElements = list.size() % 15;

            for (int i = 0; i < rowCount; i++) {
                Double[] rowData = new Double[15];
                for (int j = 0; j < 15; j++) {
                    WeightHisDetail weightDetailHis = list.get(i * 15 + j);
                    double weight = weightDetailHis.getWeight();
                    rowData[j] = weight;
                }
                tableModel.addRow(rowData);
            }

            // Add the remaining elements in a separate row
            if (remainingElements > 0) {
                Double[] remainingRow = new Double[15];
                for (int i = 0; i < remainingElements; i++) {
                    WeightHisDetail weightDetailHis = list.get(rowCount * 15 + i);
                    double weight = weightDetailHis.getWeight();
                    remainingRow[i] = weight;
                }
                tableModel.addRow(remainingRow);
            }
            for (int row = 0; row < tblWeight.getRowCount(); row++) {
                tableModel.calTotal(row);
            }
        } else {
            addNewRow();
        }
    }

    private void addNewRow() {
        Double[] rowData = new Double[15];
        tableModel.addRow(rowData);
    }

    public List<WeightHisDetail> getListDetail() {
        List<WeightHisDetail> list = new ArrayList<>();
        for (int row = 0; row < tblWeight.getRowCount(); row++) {
            for (int col = 0; col < tblWeight.getColumnCount() - 1; col++) {
                double value = Util1.getDouble(tblWeight.getValueAt(row, col));
                if (value > 0) {
                    WeightHisDetail w = new WeightHisDetail(value);
                    list.add(w);
                }
            }
        }
        return list;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelHeader = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtVouNo = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtDate = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        txtTrader = new javax.swing.JTextField();
        lblStatus = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        cboStatus = new javax.swing.JComboBox<>(WeightStatus.values());
        jLabel10 = new javax.swing.JLabel();
        txtStock = new javax.swing.JTextField();
        txtDesp = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        chkDraft = new javax.swing.JRadioButton();
        scroll = new javax.swing.JScrollPane();
        tblWeight = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        lblRec = new javax.swing.JLabel();
        txtWeightTotal = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtQty = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        txtBag = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        txtWeight = new javax.swing.JFormattedTextField();
        lblRec1 = new javax.swing.JLabel();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        panelHeader.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Vou No");

        txtVouNo.setEditable(false);
        txtVouNo.setFont(Global.textFont);
        txtVouNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtVouNoActionPerformed(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Date");

        txtDate.setDateFormatString("dd/MM/yyyy");
        txtDate.setFont(Global.textFont);

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Supplier");

        txtTrader.setFont(Global.textFont);
        txtTrader.setName("txtTrader"); // NOI18N
        txtTrader.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTraderActionPerformed(evt);
            }
        });

        lblStatus.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        lblStatus.setText("NEW");
        lblStatus.setToolTipText("NEW");

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Remark");

        txtRemark.setFont(Global.textFont);
        txtRemark.setName("txtRemark"); // NOI18N
        txtRemark.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRemarkActionPerformed(evt);
            }
        });

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Stock");

        cboStatus.setFont(Global.textFont);

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Type");

        txtStock.setFont(Global.textFont);
        txtStock.setName("txtStock"); // NOI18N
        txtStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtStockActionPerformed(evt);
            }
        });

        txtDesp.setFont(Global.textFont);
        txtDesp.setName("txtRemark"); // NOI18N
        txtDesp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDespActionPerformed(evt);
            }
        });

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Description");

        chkDraft.setFont(Global.lableFont);
        chkDraft.setForeground(Color.red);
        chkDraft.setText("Draft");

        javax.swing.GroupLayout panelHeaderLayout = new javax.swing.GroupLayout(panelHeader);
        panelHeader.setLayout(panelHeaderLayout);
        panelHeaderLayout.setHorizontalGroup(
            panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelHeaderLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtStock)
                    .addComponent(txtTrader))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtRemark, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                    .addComponent(txtDesp))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelHeaderLayout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cboStatus, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblStatus))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelHeaderLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(chkDraft)))
                .addContainerGap())
        );

        panelHeaderLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cboStatus, txtDate, txtStock, txtTrader, txtVouNo});

        panelHeaderLayout.setVerticalGroup(
            panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelHeaderLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelHeaderLayout.createSequentialGroup()
                        .addGroup(panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtVouNo)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblStatus))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(panelHeaderLayout.createSequentialGroup()
                        .addGroup(panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(panelHeaderLayout.createSequentialGroup()
                                .addGroup(panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtTrader)
                                    .addGroup(panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cboStatus)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(panelHeaderLayout.createSequentialGroup()
                                .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(6, 6, 6)))
                        .addGroup(panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(chkDraft))
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(txtDesp, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        panelHeaderLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jLabel3, jLabel4});

        tblWeight.setFont(Global.textFont);
        tblWeight.setModel(new javax.swing.table.DefaultTableModel(
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
        scroll.setViewportView(tblWeight);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblRec.setText("0");

        txtWeightTotal.setEditable(false);
        txtWeightTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Total Weight");

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Total Qty");

        txtQty.setEditable(false);
        txtQty.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Total Bag");

        txtBag.setEditable(false);
        txtBag.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Weight");

        txtWeight.setEditable(false);
        txtWeight.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        lblRec1.setText("Records :");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(lblRec1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblRec, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 588, Short.MAX_VALUE)
                        .addComponent(jLabel6)
                        .addGap(6, 6, 6)
                        .addComponent(txtWeight, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addGap(6, 6, 6)
                        .addComponent(txtWeightTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtQty, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addGap(6, 6, 6)
                                .addComponent(txtBag, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(9, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtWeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblRec)
                        .addComponent(txtWeightTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)
                        .addComponent(lblRec1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtQty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBag, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scroll, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scroll, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtVouNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtVouNoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVouNoActionPerformed

    private void txtTraderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTraderActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTraderActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observeMain();
    }//GEN-LAST:event_formComponentShown

    private void txtRemarkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRemarkActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRemarkActionPerformed

    private void txtStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtStockActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtStockActionPerformed

    private void txtDespActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDespActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDespActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<WeightStatus> cboStatus;
    private javax.swing.JRadioButton chkDraft;
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
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblRec;
    private javax.swing.JLabel lblRec1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPanel panelHeader;
    private javax.swing.JScrollPane scroll;
    private javax.swing.JTable tblWeight;
    private javax.swing.JFormattedTextField txtBag;
    private com.toedter.calendar.JDateChooser txtDate;
    private javax.swing.JTextField txtDesp;
    private javax.swing.JFormattedTextField txtQty;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JTextField txtStock;
    private javax.swing.JTextField txtTrader;
    private javax.swing.JTextField txtVouNo;
    private javax.swing.JFormattedTextField txtWeight;
    private javax.swing.JFormattedTextField txtWeightTotal;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {
        String src = source.toString();
        if (src.equals("WEIGHT-HISTORY")) {
            if (selectObj instanceof WeightHis g) {
                setVoucher(g);
            }
        } else if (src.equals("CAL_TOTAL")) {
            calTotal();
        } else if (src.equals("STOCK")) {
            Stock s = stockAutoCompleter.getStock();
            if (s != null) {
                txtWeight.setValue(s.getWeight());
                calTotal();
            }
        }

    }

    @Override
    public void save() {
        saveWeight(false);
    }

    @Override
    public void delete() {
        deleteVoucher();
    }

    @Override
    public void newForm() {
        boolean yes = ComponentUtil.checkClear(lblStatus.getText());
        if (yes) {
            clear(true);
        }
    }

    @Override
    public void history() {
        historyWeight();
    }

    @Override
    public void print() {
        saveWeight(true);
    }

    @Override
    public void refresh() {
    }

    @Override
    public void filter() {
        findDialog.setVisible(!findDialog.isVisible());
    }

    @Override
    public String panelName() {
        return this.getName();
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
        }
        switch (ctrlName) {
            case "txtDate" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtTrader.requestFocus();
                }
            }
            case "txtTrader" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtStock.requestFocus();
                }
            }
            case "txtStock" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    focusTable();
                }
            }
        }
    }
}
