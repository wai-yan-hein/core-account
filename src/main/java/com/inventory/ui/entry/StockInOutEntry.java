/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry;

import com.common.DateLockUtil;
import com.common.DecimalFormatRender;
import java.awt.event.KeyEvent;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import com.common.Global;
import com.common.PanelControl;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.repo.UserRepo;
import com.common.Util1;
import com.inventory.editor.LocationCellEditor;
import com.inventory.editor.StockCellEditor;
import com.inventory.editor.VouStatusAutoCompleter;
import com.inventory.model.Location;
import com.inventory.model.OPHis;
import com.inventory.model.OPHisDetail;
import com.inventory.model.StockIOKey;
import com.inventory.model.StockInOut;
import com.inventory.model.StockInOutDetail;
import com.inventory.model.StockInOutKey;
import com.inventory.model.VStockIO;
import com.repo.InventoryRepo;
import com.inventory.ui.common.StockInOutTableModel;
import com.inventory.ui.common.StockInOutWeightTableModel;
import com.inventory.ui.entry.dialog.OPHistoryDialog;
import com.inventory.ui.entry.dialog.StockIOHistoryDialog;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.inventory.editor.StockUnitEditor;
import com.inventory.model.Job;
import com.inventory.model.LabourGroup;
import com.inventory.ui.common.JobComboBoxModel;
import com.inventory.ui.common.LabourGroupComboBoxModel;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.view.JasperViewer;
import reactor.core.publisher.Mono;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class StockInOutEntry extends javax.swing.JPanel implements PanelControl, SelectionObserver, KeyListener {

    public static final int IO = 1;
    public static final int IO_W = 2;
    private final StockInOutTableModel outTableModel = new StockInOutTableModel();
    private final StockInOutWeightTableModel weightTableModel = new StockInOutWeightTableModel();
    private StockIOHistoryDialog dialog;
    private InventoryRepo inventoryRepo;
    private UserRepo userRepo;
    private VouStatusAutoCompleter vouStatusAutoCompleter;
    private StockInOut io = new StockInOut();
    private SelectionObserver observer;
    private JProgressBar progress;
    private Mono<List<Location>> monoLoc;
    private int type;
    private final LabourGroupComboBoxModel labourGroupComboBoxModel = new LabourGroupComboBoxModel();
    private final JobComboBoxModel jobComboBoxModel = new JobComboBoxModel();

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public JProgressBar getProgress() {
        return progress;
    }

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }

    /**
     * Creates new form StockInOutEntry
     *
     * @param type
     */
    public StockInOutEntry(int type) {
        this.type = type;
        initComponents();
        initTextBoxFormat();
        initDateListner();
        actionMapping();
    }

    public void initMain() {
        initTable();
        initModel();
        initCombo();
        clear();
    }

    private void actionMapping() {
        String solve = "delete";
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        tblStock.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, solve);
        tblStock.getActionMap().put(solve, new DeleteAction());
    }

    private class DeleteAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            deleteTran();
        }
    }

    private void initDateListner() {
        txtDate.getDateEditor().getUiComponent().setName("txtDate");
        txtDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtDate.getDateEditor().getUiComponent().addFocusListener(fa);
        txtRemark.addKeyListener(this);
        txtDesp.addKeyListener(this);
        txtVouType.addKeyListener(this);
    }
    private final FocusAdapter fa = new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            ((JTextFieldDateEditor) e.getSource()).selectAll();
        }

    };

    private void initTextBoxFormat() {
        txtInQty.setFormatterFactory(Util1.getDecimalFormat());
        txtOutQty.setFormatterFactory(Util1.getDecimalFormat());
        txtCost.setFormatterFactory(Util1.getDecimalFormat());
    }

    private void initCombo() {
        vouStatusAutoCompleter = new VouStatusAutoCompleter(txtVouType, null, false);
        vouStatusAutoCompleter.setVoucher(null);
        inventoryRepo.getVoucherStatus().doOnSuccess((t) -> {
            vouStatusAutoCompleter.setListVouStatus(t);
        }).subscribe();

        inventoryRepo.getLabourGroup().subscribe((t) -> {
            t.add(new LabourGroup());
            labourGroupComboBoxModel.setData(t);
            cboLabourGroup.setModel(labourGroupComboBoxModel);
            cboLabourGroup.setSelectedItem(null);
        });

        inventoryRepo.getJob(false).subscribe((t) -> {
            t.add(new Job());
            jobComboBoxModel.setData(t);
            cboJob.setModel(jobComboBoxModel);
            cboJob.setSelectedItem(null);
        });
    }

    private void initModel() {
        switch (type) {
            case IO -> {
                initStockIO();
            }
            case IO_W -> {
                initStockIOWeight();
            }
        }
    }

    private void initStockIO() {
        outTableModel.setVouDate(txtDate);
        outTableModel.setInventoryRepo(inventoryRepo);
        outTableModel.setLblRec(lblRec);
        outTableModel.addNewRow();
        outTableModel.setParent(tblStock);
        outTableModel.setObserver(this);
        tblStock.setModel(outTableModel);
        monoLoc = inventoryRepo.getLocation();
        tblStock.getColumnModel().getColumn(0).setPreferredWidth(100);
        tblStock.getColumnModel().getColumn(1).setPreferredWidth(250);
        tblStock.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblStock.getColumnModel().getColumn(3).setPreferredWidth(50);
        tblStock.getColumnModel().getColumn(4).setPreferredWidth(50);
        tblStock.getColumnModel().getColumn(5).setPreferredWidth(50);
        tblStock.getColumnModel().getColumn(6).setPreferredWidth(50);
        tblStock.getColumnModel().getColumn(7).setPreferredWidth(50);
        tblStock.getColumnModel().getColumn(8).setPreferredWidth(50);
        tblStock.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor(inventoryRepo));
        tblStock.getColumnModel().getColumn(1).setCellEditor(new StockCellEditor(inventoryRepo));
        monoLoc.subscribe((t) -> {
            tblStock.getColumnModel().getColumn(2).setCellEditor(new LocationCellEditor(t));
        });
        tblStock.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());
        inventoryRepo.getStockUnit().subscribe((t) -> {
            tblStock.getColumnModel().getColumn(4).setCellEditor(new StockUnitEditor(t));
            tblStock.getColumnModel().getColumn(6).setCellEditor(new StockUnitEditor(t));
        });
        tblStock.getColumnModel().getColumn(5).setCellEditor(new AutoClearEditor());
        tblStock.getColumnModel().getColumn(7).setCellEditor(new AutoClearEditor());
    }

    private void initStockIOWeight() {
        weightTableModel.setVouDate(txtDate);
        weightTableModel.setInventoryRepo(inventoryRepo);
        weightTableModel.setLblRec(lblRec);
        weightTableModel.addNewRow();
        weightTableModel.setParent(tblStock);
        weightTableModel.setObserver(this);
        tblStock.setModel(weightTableModel);
        monoLoc = inventoryRepo.getLocation();
        tblStock.getColumnModel().getColumn(0).setPreferredWidth(80);//code
        tblStock.getColumnModel().getColumn(1).setPreferredWidth(200);//name
        tblStock.getColumnModel().getColumn(2).setPreferredWidth(50);//location
        tblStock.getColumnModel().getColumn(3).setPreferredWidth(50);//weight
        tblStock.getColumnModel().getColumn(4).setPreferredWidth(30);//unit
        tblStock.getColumnModel().getColumn(5).setPreferredWidth(50);//inqty
        tblStock.getColumnModel().getColumn(6).setPreferredWidth(20);//unit
        tblStock.getColumnModel().getColumn(7).setPreferredWidth(50);//out qty
        tblStock.getColumnModel().getColumn(8).setPreferredWidth(20);//unit
        tblStock.getColumnModel().getColumn(9).setPreferredWidth(50);//cost
        tblStock.getColumnModel().getColumn(10).setPreferredWidth(80);//amt
        tblStock.getColumnModel().getColumn(11).setPreferredWidth(80);//total weight

        tblStock.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor(inventoryRepo));
        tblStock.getColumnModel().getColumn(1).setCellEditor(new StockCellEditor(inventoryRepo));
        monoLoc.subscribe((t) -> {
            tblStock.getColumnModel().getColumn(2).setCellEditor(new LocationCellEditor(t));
        });
        tblStock.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());
        inventoryRepo.getStockUnit().subscribe((t) -> {
            tblStock.getColumnModel().getColumn(4).setCellEditor(new StockUnitEditor(t));
            tblStock.getColumnModel().getColumn(6).setCellEditor(new StockUnitEditor(t));
            tblStock.getColumnModel().getColumn(8).setCellEditor(new StockUnitEditor(t));
        });
        tblStock.getColumnModel().getColumn(5).setCellEditor(new AutoClearEditor());
        tblStock.getColumnModel().getColumn(7).setCellEditor(new AutoClearEditor());
    }

    private void initTable() {
        tblStock.getTableHeader().setFont(Global.tblHeaderFont);
        tblStock.setDefaultRenderer(Object.class, new DecimalFormatRender(2));
        tblStock.setDefaultRenderer(Double.class, new DecimalFormatRender(2));
        tblStock.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblStock.setCellSelectionEnabled(true);
        tblStock.changeSelection(0, 0, false, false);
        tblStock.requestFocus();
    }

    private void deleteVoucher() {
        String status = lblStatus.getText();
        switch (status) {
            case "EDIT" -> {
                int yes_no = JOptionPane.showConfirmDialog(Global.parentForm,
                        "Are you sure to delete?", "Stock In/Out Voucher delete.", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                if (yes_no == 0) {
                    inventoryRepo.delete(io.getKey()).doOnSuccess((t) -> {
                        if (t) {
                            clear();
                        }
                    }).subscribe();
                }
            }
            case "DELETED" -> {
                int yes_no = JOptionPane.showConfirmDialog(this,
                        "Are you sure to restore?", "Stock In/Out Voucher Restore.", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (yes_no == 0) {
                    io.setDeleted(false);
                    inventoryRepo.restore(io.getKey()).doOnSuccess((t) -> {
                        lblStatus.setText("EDIT");
                        lblStatus.setForeground(Color.blue);
                        disableForm(true);
                    }).subscribe();

                }
            }
            default ->
                JOptionPane.showMessageDialog(Global.parentForm, "Voucher can't delete.");
        }
    }

    private void deleteTran() {
        int row = tblStock.convertRowIndexToModel(tblStock.getSelectedRow());
        if (row >= 0) {
            if (tblStock.getCellEditor() != null) {
                tblStock.getCellEditor().stopCellEditing();
            }
            int yes_no = JOptionPane.showConfirmDialog(this,
                    "Are you sure to delete?", "Stock Transaction delete.", JOptionPane.YES_NO_OPTION);
            if (yes_no == 0) {
                deltetDetail(row);
                calTotalAmt();
            }
        }
    }

    public void saveVoucher(boolean print) {
        if (isValidEntry() && isValidDetail()) {
            if (DateLockUtil.isLockDate(txtDate.getDate())) {
                DateLockUtil.showMessage(this);
                txtDate.requestFocus();
                return;
            }
            observer.selected("save", true);
            progress.setIndeterminate(true);
            io.setListSH(getListDetail());
            io.setListDel(getListDelete());
            inventoryRepo.save(io)
                    .subscribe((t) -> {
                        clear();
                        focusOnTable();
                        if (print) {
                            printVoucher(t.getKey().getVouNo());
                        }
                    }, (e) -> {
                        observer.selected("save", true);
                        JOptionPane.showMessageDialog(this, e.getMessage());
                        progress.setIndeterminate(false);
                    });

        }
    }

    private void printVoucher(String vouNo) {
        inventoryRepo.getStockInOutVoucher(vouNo).subscribe((t) -> {
            try {
                if (t != null) {
                    String reportName = "StockInOutVoucher";
                    String logoPath = String.format("images%s%s", File.separator, ProUtil.getProperty("logo.name"));
                    Map<String, Object> param = new HashMap<>();
                    param.put("p_print_date", Util1.getTodayDateTime());
                    param.put("p_comp_name", Global.companyName);
                    param.put("p_comp_address", Global.companyAddress);
                    param.put("p_comp_phone", Global.companyPhone);
                    param.put("p_logo_path", logoPath);
                    String reportPath = String.format("report%s%s", File.separator, reportName.concat(".jasper"));
                    ByteArrayInputStream jsonDataStream = new ByteArrayInputStream(Util1.listToByteArray(t));
                    JsonDataSource ds = new JsonDataSource(jsonDataStream);
                    JasperPrint js = JasperFillManager.fillReport(reportPath, param, ds);
                    JasperViewer.viewReport(js, false);
                }
            } catch (JRException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }, (e) -> {
            JOptionPane.showMessageDialog(this, e.getMessage());
        });
    }

    private void clear() {
        io = new StockInOut();
        lblStatus.setForeground(Color.GREEN);
        lblStatus.setText("NEW");
        txtDesp.setText(null);
        txtRemark.setText(null);
        txtInQty.setValue(0.0);
        txtOutQty.setValue(0.0);
        vouStatusAutoCompleter.setVoucher(null);
        jobComboBoxModel.setSelectedItem(null);
        labourGroupComboBoxModel.setSelectedItem(null);
        cboJob.repaint();
        cboLabourGroup.repaint();
        txtDate.setDate(Util1.getTodayDate());
        progress.setIndeterminate(false);
        txtVou.setText(null);
        clearModel();
        disableForm(true);
        calTotalAmt();
    }

    private void calTotalAmt() {
        float ttlInQty = 0.0f;
        float ttlOutQty = 0.0f;
        float ttlPrice = 0.0f;
        List<StockInOutDetail> listIO = getListDetail();
        if (!listIO.isEmpty()) {
            for (StockInOutDetail s : listIO) {
                ttlInQty += Util1.getDouble(s.getInQty());
                ttlOutQty += Util1.getDouble(s.getOutQty());
                ttlPrice += Util1.getDouble(s.getCostPrice()) * (Util1.getDouble(s.getInQty()) + Util1.getDouble(s.getOutQty()));
            }
        }
        txtInQty.setValue(ttlInQty);
        txtOutQty.setValue(ttlOutQty);
        txtCost.setValue(ttlPrice);

    }

    private void deltetDetail(int row) {
        switch (type) {
            case IO -> {
                outTableModel.delete(row);
            }
            case IO_W -> {
                weightTableModel.delete(row);
            }
        }
    }

    private boolean isValidDetail() {
        switch (type) {
            case IO -> {
                return outTableModel.isValidEntry();
            }
            case IO_W -> {
                return weightTableModel.isValidEntry();
            }
            default -> {
                return false;
            }
        }
    }

    private void clearModel() {
        switch (type) {
            case IO -> {
                outTableModel.clear();
                outTableModel.addNewRow();
            }
            case IO_W -> {
                weightTableModel.clear();
                weightTableModel.addNewRow();
            }
        }
    }

    private List<StockInOutDetail> getListDetail() {
        switch (type) {
            case IO -> {
                return outTableModel.getListStock();
            }
            case IO_W -> {
                return weightTableModel.getListStock();
            }
        }
        return null;
    }

    private List<StockInOutKey> getListDelete() {
        switch (type) {
            case IO -> {
                return outTableModel.getDeleteList();
            }
            case IO_W -> {
                return weightTableModel.getDeleteList();
            }
        }
        return null;
    }

    private void focusOnTable() {
        int rc = tblStock.getRowCount();
        if (rc > 1) {
            tblStock.setRowSelectionInterval(rc - 1, rc - 1);
            tblStock.setColumnSelectionInterval(0, 0);
            tblStock.requestFocus();
        } else {
            txtDate.requestFocusInWindow();
        }
    }

    private boolean isValidEntry() {
        boolean status = true;
        if (vouStatusAutoCompleter.getVouStatus() == null) {
            JOptionPane.showMessageDialog(this, "Select Voucher Status.");
            status = false;
            txtVouType.requestFocus();
        } else if (lblStatus.getText().equals("DELETED")) {
            clear();
            status = false;
        } else if (Util1.getDouble(txtInQty.getValue()) + Util1.getDouble(txtOutQty.getValue()) <= 0) {
            status = false;
            JOptionPane.showMessageDialog(this, "No records.");
        } else if (!Util1.isDateBetween(txtDate.getDate())) {
            JOptionPane.showMessageDialog(this, "Invalid Date.",
                    "Validation.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtDate.requestFocus();
        } else {
            io.setDescription(txtDesp.getText());
            io.setRemark(txtRemark.getText());
            io.setVouDate(Util1.convertToLocalDateTime(txtDate.getDate()));
            io.setVouStatusCode(vouStatusAutoCompleter.getVouStatus().getKey().getCode());
            if (lblStatus.getText().equals("NEW")) {
                StockIOKey key = new StockIOKey();
                key.setCompCode(Global.compCode);
                key.setVouNo(null);
                io.setKey(key);
                io.setDeptId(Global.deptId);
                io.setCreatedBy(Global.loginUser.getUserCode());
                io.setCreatedDate(LocalDateTime.now());
                io.setMacId(Global.macId);
                io.setDeleted(Boolean.FALSE);
                if (cboLabourGroup.getSelectedItem() instanceof LabourGroup lg) {
                    if (lg.getKey() != null) {
                        io.setLabourGroupCode(lg.getKey().getCode());
                    } else {
                        io.setLabourGroupCode(null);
                    }
                }
                if (cboJob.getSelectedItem() instanceof Job job) {
                    if (job.getKey() != null) {
                        io.setJobCode(job.getKey().getJobNo());
                    } else {
                        io.setJobCode(null);
                    }
                }
            } else {
                io.setUpdatedBy(Global.loginUser.getUserCode());
            }
        }
        return status;
    }

    private void setVoucher(StockInOut s, boolean local) {
        txtCost.setValue(0);
        txtInQty.setValue(0);
        txtOutQty.setValue(0);
        if (s != null) {
            progress.setIndeterminate(true);
            io = s;
            io.setVouLock(!Global.deptId.equals(io.getDeptId()));
            inventoryRepo.findVouStatus(io.getVouStatusCode()).doOnSuccess((t) -> {
                vouStatusAutoCompleter.setVoucher(t);
            }).subscribe();
            inventoryRepo.findJob(io.getJobCode()).doOnSuccess((t) -> {
                jobComboBoxModel.setSelectedItem(t);
                cboJob.repaint();
            }).subscribe();
            inventoryRepo.findLabourGroup(io.getLabourGroupCode()).doOnSuccess((t) -> {
                labourGroupComboBoxModel.setSelectedItem(t);
                cboLabourGroup.repaint();
            }).subscribe();
            String vouNo = io.getKey().getVouNo();
            txtVou.setText(vouNo);
            txtDate.setDate(Util1.convertToDate(io.getVouDate()));
            txtRemark.setText(io.getRemark());
            txtDesp.setText(io.getDescription());
            if (Util1.getBoolean(io.isVouLock())) {
                lblStatus.setText("Voucher Locked.");
                lblStatus.setForeground(Color.red);
                disableForm(false);
            } else if (Util1.getBoolean(io.getDeleted())) {
                lblStatus.setText("DELETED");
                lblStatus.setForeground(Color.red);
                disableForm(false);
            } else if (DateLockUtil.isLockDate(io.getVouDate())) {
                lblStatus.setText(DateLockUtil.MESSAGE);
                lblStatus.setForeground(Color.RED);
                disableForm(false);
            } else {
                lblStatus.setText("EDIT");
                lblStatus.setForeground(Color.blue);
                disableForm(true);
            }
            inventoryRepo.getStockIODetail(vouNo, local).doOnSuccess((t) -> {
                setListDetail(t);
            }).doOnTerminate(() -> {
                calTotalAmt();
                focusOnTable();
                progress.setIndeterminate(false);
            }).subscribe();
        }
    }

    private void setListDetail(List<StockInOutDetail> list) {
        switch (type) {
            case IO -> {
                outTableModel.setListStock(list);
                outTableModel.addNewRow();
            }
            case IO_W -> {
                weightTableModel.setListStock(list);
                weightTableModel.addNewRow();
            }
        }
    }

    private void disableForm(boolean status) {
        txtDate.setEnabled(status);
        txtRemark.setEnabled(status);
        txtDesp.setEnabled(status);
        tblStock.setEnabled(status);
        txtVouType.setEnabled(status);
        observer.selected("save", status);
        observer.selected("delete", status);
        observer.selected("print", status);

    }

    public void historyOP() {
        try {
            OPHistoryDialog d = new OPHistoryDialog(Global.parentForm);
            d.setUserRepo(userRepo);
            d.setInventoryRepo(inventoryRepo);
            d.setObserver(this);
            d.initMain();
            d.setSize(Global.width - 200, Global.height - 200);
            d.setLocationRelativeTo(null);
            d.setIconImage(new ImageIcon(getClass().getResource("/images/search.png")).getImage());
            d.setVisible(true);
        } catch (Exception e) {
            log.error(String.format("historyOPhistoryOP: %s", e.getMessage()));
        }

    }

    private void importOP(OPHis op) {
        clear();
        inventoryRepo.getOpeningDetail(op.getKey().getVouNo(), op.getKey().getCompCode(), op.getDeptId())
                .doOnSuccess((list) -> {
                    for (int i = 0; i < list.size(); i++) {
                        OPHisDetail his = list.get(i);
                        StockInOutDetail iod = new StockInOutDetail();
                        StockInOutKey key = new StockInOutKey();
                        iod.setStockCode(his.getStockCode());
                        iod.setStockName(his.getStockName());
                        iod.setInQty(his.getQty());
                        iod.setCostPrice(his.getPrice());
                        iod.setInUnitCode(his.getUnitCode());
                        iod.setLocCode(op.getLocCode());
                        iod.setLocName(op.getLocName());
                        key.setUniqueId(i + 1);
                        iod.setKey(key);
                        outTableModel.addObject(iod);
                    }
                    calTotalAmt();
                    outTableModel.setNegative(true);
                    focusOnTable();
                    progress.setIndeterminate(false);
                }).subscribe();
    }

    private void observeMain() {
        observer.selected("control", this);
        observer.selected("save", true);
        observer.selected("print", true);
        observer.selected("history", true);
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
        jLabel9 = new javax.swing.JLabel();
        cboLabourGroup = new javax.swing.JComboBox<>();
        cboJob = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        txtOutQty = new javax.swing.JFormattedTextField();
        jLabel8 = new javax.swing.JLabel();
        txtInQty = new javax.swing.JFormattedTextField();
        txtCost = new javax.swing.JFormattedTextField();
        lblRec = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        tblStock.setAutoCreateRowSorter(true);
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

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Description");

        txtDesp.setFont(Global.textFont);
        txtDesp.setName("txtDesp"); // NOI18N

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Remark");

        txtRemark.setFont(Global.textFont);
        txtRemark.setName("txtRemark"); // NOI18N

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Vou No    ");

        txtVou.setEditable(false);
        txtVou.setFont(Global.textFont);

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Date");

        txtDate.setDateFormatString("dd/MM/yyyy");
        txtDate.setFont(Global.textFont);
        txtDate.setMaxSelectableDate(new java.util.Date(253370745114000L));

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Vou Type");

        txtVouType.setFont(Global.textFont);
        txtVouType.setName("txtVouType"); // NOI18N

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Job");

        cboLabourGroup.setFont(Global.textFont);
        cboLabourGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboLabourGroupActionPerformed(evt);
            }
        });

        cboJob.setFont(Global.textFont);
        cboJob.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboJobActionPerformed(evt);
            }
        });

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Labour Group");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtVou)
                    .addComponent(cboLabourGroup, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cboJob, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDesp))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtVouType)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtRemark)
                .addGap(18, 18, 18))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel4, jLabel7});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7)
                        .addComponent(txtVouType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel6)
                                    .addComponent(txtVou, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(txtDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(cboLabourGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboJob, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10)
                            .addComponent(jLabel4)
                            .addComponent(txtDesp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jButton1.setText("Import");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Total Out Qty");

        lblStatus.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        lblStatus.setText("NEW");

        txtOutQty.setEditable(false);
        txtOutQty.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtOutQty.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtOutQty.setFont(Global.amtFont);

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Total Cost");

        txtInQty.setEditable(false);
        txtInQty.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtInQty.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtInQty.setFont(Global.amtFont);

        txtCost.setEditable(false);
        txtCost.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtCost.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtCost.setFont(Global.amtFont);

        lblRec.setFont(Global.lableFont);
        lblRec.setText("Records");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Total In Qty");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblStatus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblRec, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 133, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(txtInQty, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(txtOutQty, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addGap(18, 18, 18)
                .addComponent(txtCost, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtOutQty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtInQty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)
                        .addComponent(jLabel1)
                        .addComponent(txtCost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8)
                        .addComponent(lblRec)
                        .addComponent(jButton1))
                    .addComponent(lblStatus))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observeMain();
    }//GEN-LAST:event_formComponentShown

    private void tblStockKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblStockKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_tblStockKeyReleased

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        historyOP();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void cboLabourGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboLabourGroupActionPerformed
//        searchStock();
    }//GEN-LAST:event_cboLabourGroupActionPerformed

    private void cboJobActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboJobActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboJobActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<Job> cboJob;
    private javax.swing.JComboBox<LabourGroup> cboLabourGroup;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblRec;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblStock;
    private javax.swing.JFormattedTextField txtCost;
    private com.toedter.calendar.JDateChooser txtDate;
    private javax.swing.JTextField txtDesp;
    private javax.swing.JFormattedTextField txtInQty;
    private javax.swing.JFormattedTextField txtOutQty;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JFormattedTextField txtVou;
    private javax.swing.JTextField txtVouType;
    // End of variables declaration//GEN-END:variables

    @Override
    public void save() {
        saveVoucher(false);
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
        if (dialog == null) {
            dialog = new StockIOHistoryDialog(Global.parentForm);
            dialog.setInventoryRepo(inventoryRepo);
            dialog.setUserRepo(userRepo);
            dialog.setObserver(this);
            dialog.initMain();
            dialog.setSize(Global.width - 20, Global.height - 20);
            dialog.setLocationRelativeTo(null);
        }
        dialog.search();
    }

    @Override
    public void print() {
        saveVoucher(true);
    }

    @Override
    public void refresh() {
        initCombo();
    }

    @Override
    public void filter() {
    }

    @Override
    public void selected(Object source, Object selectObj) {
        if (source.toString().equals("IO-HISTORY")) {
            if (selectObj instanceof VStockIO v) {
                inventoryRepo.findStockIO(v.getVouNo(), false).subscribe((t) -> {
                    setVoucher(t, false);
                });
            }
        }
        if (source.toString().equals("CAL-TOTAL")) {
            calTotalAmt();
        }
        if (source.toString().equals("OP-HISTORY")) {
            if (selectObj instanceof OPHis v) {
                inventoryRepo.findOpening(v.getKey()).subscribe((t) -> {
                    importOP(t);
                });
            }
        }

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
                    String date = ((JTextFieldDateEditor) sourceObj).getText();
                    txtDate.setDate(Util1.formatDate(date));
                    txtVouType.requestFocus();
                }
            }
            case "txtVouType" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtDesp.requestFocus();
                }
            }
            case "txtDesp" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtRemark.requestFocus();
                }
            }
            case "txtRemark" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    tblStock.requestFocus();
                }
            }

        }
    }
}
