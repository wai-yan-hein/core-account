/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.inventory.ui.entry;

import com.CloudIntegration;
import com.common.DateLockUtil;
import com.common.DecimalFormatRender;
import com.common.Global;
import com.common.PanelControl;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.editor.LocationCellEditor;
import com.inventory.editor.StockAutoCompleter;
import com.inventory.editor.StockCellEditor;
import com.inventory.editor.StockUnitEditor;
import com.inventory.editor.UnitAutoCompleter;
import com.inventory.editor.VouStatusAutoCompleter;
import com.inventory.model.Pattern;
import com.inventory.model.ProcessHis;
import com.inventory.model.ProcessHisDetail;
import com.inventory.model.ProcessHisDetailKey;
import com.inventory.model.ProcessHisKey;
import com.inventory.model.Stock;
import com.repo.InventoryRepo;
import com.inventory.ui.common.ProcessHisDetailTableModel;
import com.inventory.ui.entry.dialog.ManufactureHistoryDialog;
import com.inventory.ui.setup.dialog.VouStatusSetupDialog;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.toedter.calendar.JTextFieldDateEditor;
import com.repo.UserRepo;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.time.LocalDateTime;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    private UserRepo userRepo;
    @Autowired
    private CloudIntegration integration;
    private JProgressBar progress;
    private SelectionObserver observer;
    private final ProcessHisDetailTableModel processHisDetailTableModel = new ProcessHisDetailTableModel();
    private ManufactureHistoryDialog dialog;
    private StockAutoCompleter stockAutoCompleter;
    private LocationAutoCompleter locationAutoCompleter;
    private UnitAutoCompleter unitAutoCompleter;
    private VouStatusAutoCompleter vouStatusAutoCompleter;
    private ProcessHis ph = new ProcessHis();
    private final Image searchIcon = new ImageIcon(this.getClass().getResource("/images/search.png")).getImage();

    /**
     * Creates new form Manufacture
     */
    public Manufacture() {
        initComponents();
        initKeyListener();
        initGroupRadio();
        actionMapping();
    }

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
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
    }

    private void initFomatFactory() {
        txtQty.setFormatterFactory(Util1.getDecimalFormat());
        txtPrice.setFormatterFactory(Util1.getDecimalFormat());
        txtAmt.setFormatterFactory(Util1.getDecimalFormat());
    }

    private void initCompleter() {
        locationAutoCompleter = new LocationAutoCompleter(txtLocation, null, false, false);
        locationAutoCompleter.setObserver(this);
        inventoryRepo.getLocation().subscribe((t) -> {
            locationAutoCompleter.setListLocation(t);
        });
        stockAutoCompleter = new StockAutoCompleter(txtStock, inventoryRepo, null, false);
        stockAutoCompleter.setObserver(this);
        unitAutoCompleter = new UnitAutoCompleter(txtUnit, null);
        unitAutoCompleter.setObserver(this);
        inventoryRepo.getStockUnit().doOnSuccess((t) -> {
            unitAutoCompleter.setListUnit(t);
        }).subscribe();
        vouStatusAutoCompleter = new VouStatusAutoCompleter(txtPt, null, false);
        vouStatusAutoCompleter.setObserver(this);
        inventoryRepo.getVoucherStatus().doOnSuccess((t) -> {
            vouStatusAutoCompleter.setListVouStatus(t);
        }).subscribe();

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
                    inventoryRepo.delete(processHisDetailTableModel.getObject(row).getKey()).subscribe((t) -> {
                        processHisDetailTableModel.deleteObject(row);
                        processHisDetailTableModel.calPrice();
                    });

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
        inventoryRepo.getLocation().subscribe((t) -> {
            tblProcessDetail.getColumnModel().getColumn(3).setCellEditor(new LocationCellEditor(t));//location
        });
        tblProcessDetail.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());//qty
        inventoryRepo.getStockUnit().subscribe((t) -> {
            tblProcessDetail.getColumnModel().getColumn(5).setCellEditor(new StockUnitEditor(t));//unit
        });
        tblProcessDetail.getColumnModel().getColumn(6).setCellEditor(new AutoClearEditor());//price
        //
        tblProcessDetail.getColumnModel().getColumn(4).setCellRenderer(new DecimalFormatRender());//qty
        tblProcessDetail.getColumnModel().getColumn(6).setCellRenderer(new DecimalFormatRender());//price
        tblProcessDetail.getColumnModel().getColumn(7).setCellRenderer(new DecimalFormatRender());//amount

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
        initTblProcessDetail();
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
        txtUnit.addKeyListener(this);
        txtPrice.addKeyListener(this);
    }

    private void setProcess(ProcessHis p, boolean local) {
        ph = p;
        Integer deptId = ph.getKey().getDeptId();
        txtVouNo.setText(ph.getKey().getVouNo());
        txtStartDate.setDate(Util1.convertToDate(ph.getVouDate()));
        txtEndDate.setDate(Util1.convertToDate(ph.getEndDate()));
        txtRemark.setText(ph.getRemark());
        txtProNo.setText(ph.getProcessNo());
        txtQty.setValue(ph.getQty());
        txtPrice.setValue(ph.getPrice());
        txtAmt.setValue(ph.getQty() * ph.getPrice());
        chkFinish.setSelected(ph.isFinished());
        inventoryRepo.findUnit(ph.getUnit()).subscribe((t) -> {
            unitAutoCompleter.setStockUnit(t);
        });
        inventoryRepo.findVouStatus(ph.getPtCode()).subscribe((t) -> {
            vouStatusAutoCompleter.setVoucher(t);
        });
        inventoryRepo.findStock(ph.getStockCode()).subscribe((t) -> {
            stockAutoCompleter.setStock(t);
        });
        inventoryRepo.findLocation(ph.getLocCode()).subscribe((t) -> {
            locationAutoCompleter.setLocation(t);
        });
        if (p.isDeleted()) {
            enableProcess(false);
            lblStatus.setText("DELETED");
            lblStatus.setForeground(Color.red);
        } else if (DateLockUtil.isLockDate(p.getVouDate())) {
            lblStatus.setText(DateLockUtil.MESSAGE);
            lblStatus.setForeground(Color.RED);
            enableProcess(false);
        } else if (DateLockUtil.isLockDate(p.getEndDate())) {
            lblStatus.setText(DateLockUtil.MESSAGE);
            lblStatus.setForeground(Color.RED);
            enableProcess(false);
        } else {
            enableProcess(true);
            lblStatus.setText("EDIT");
            lblStatus.setForeground(Color.blue);
        }
        inventoryRepo.getProcessDetail(ph.getKey().getVouNo(), ph.getKey().getDeptId(), local)
                .subscribe((t) -> {
                    processHisDetailTableModel.setListDetail(t);
                    processHisDetailTableModel.addNewRow();
                    lblRecord.setText("Records : " + String.valueOf(processHisDetailTableModel.getListDetail().size() - 1));
                    focusOnTable();
                });

    }

    private void enableProcess(boolean status) {
        txtStartDate.setEnabled(status);
        txtEndDate.setEnabled(status);
        txtRemark.setEditable(status);
        txtProNo.setEditable(status);
        txtQty.setEditable(status);
        chkFinish.setEnabled(status);
        txtUnit.setEnabled(status);
        txtPt.setEnabled(status);
        txtStock.setEnabled(status);
        txtLocation.setEnabled(status);
        observer.selected("save", status);
    }

    private void vouStatusSetup() {
        inventoryRepo.getVoucherStatus().subscribe((t) -> {
            VouStatusSetupDialog vsDialog = new VouStatusSetupDialog();
            vsDialog.setIconImage(icon);
            vsDialog.setInventoryRepo(inventoryRepo);
            vsDialog.setListVou(t);
            vsDialog.initMain();
            vsDialog.setSize(Global.width / 2, Global.height / 2);
            vsDialog.setLocationRelativeTo(null);
            vsDialog.setVisible(true);
        });

    }

    private void saveProcess() {
        if (isValidProcess() && processHisDetailTableModel.validEntry()) {
            if (DateLockUtil.isLockDate(txtStartDate.getDate())) {
                DateLockUtil.showMessage(this);
                txtStartDate.requestFocus();
                return;
            } else if (DateLockUtil.isLockDate(txtEndDate.getDate())) {
                DateLockUtil.showMessage(this);
                txtEndDate.requestFocus();
                return;
            }
            ph.setListDetail(processHisDetailTableModel.getListDetail());
            progress.setIndeterminate(true);
            observer.selected("save", false);
            inventoryRepo.saveProcess(ph).subscribe((t) -> {
                clear();
            }, (e) -> {
                JOptionPane.showMessageDialog(this, e.getMessage());
                progress.setIndeterminate(false);
                observer.selected("save", false);
            });

        }
    }

    private void clear() {
        inventoryRepo.getDefaultLocation().subscribe((tt) -> {
            locationAutoCompleter.setLocation(tt);
        });
        lblStatus.setText("NEW");
        lblStatus.setForeground(Color.green);
        txtVouNo.setText(null);
        chkFinish.setSelected(false);
        txtStock.setText(null);
        txtRemark.setText(null);
        txtProNo.setText(null);
        txtQty.setValue(1);
        txtPrice.setValue(null);
        txtAmt.setValue(null);
        processHisDetailTableModel.clear();
        progress.setIndeterminate(false);
        enableProcess(true);
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
        } else if (Util1.getFloat(txtQty.getValue()) <= 0) {
            JOptionPane.showMessageDialog(this, "Invalid Qty.");
            txtQty.requestFocus();
            return false;
        } else if (!Util1.isDateBetween(txtStartDate.getDate())) {
            JOptionPane.showMessageDialog(this, "Invalid Start Date.",
                    "Validation.", JOptionPane.ERROR_MESSAGE);
            txtStartDate.requestFocus();
            return false;
        } else if (!Util1.isDateBetween(txtEndDate.getDate())) {
            JOptionPane.showMessageDialog(this, "Invalid End Date.",
                    "Validation.", JOptionPane.ERROR_MESSAGE);
            txtEndDate.requestFocus();
            return false;
        } else {
            if (chkFinish.isSelected()) {
                int s = JOptionPane.showConfirmDialog(this,
                        String.format("Production end date is %s", Util1.toDateStr(ph.getEndDate(), "dd/MM/yyyy")),
                        "Warning", JOptionPane.WARNING_MESSAGE);
                if (s == JOptionPane.NO_OPTION) {
                    return false;
                }
            }
            ProcessHisKey key = new ProcessHisKey();
            key.setVouNo(txtVouNo.getText());
            key.setCompCode(Global.compCode);
            key.setDeptId(Global.deptId);
            ph.setKey(key);
            ph.setStockCode(stockAutoCompleter.getStock().getKey().getStockCode());
            ph.setLocCode(locationAutoCompleter.getLocation().getKey().getLocCode());
            ph.setVouDate(Util1.convertToLocalDateTime(txtStartDate.getDate()));
            ph.setEndDate(Util1.convertToLocalDateTime(txtEndDate.getDate()));
            ph.setRemark(txtRemark.getText());
            ph.setProcessNo(txtProNo.getText());
            ph.setQty(Util1.getFloat(txtQty.getValue()));
            ph.setPrice(Util1.getFloat(txtPrice.getValue()));
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

    private void deleteProcess() {
        String name = lblStatus.getText();
        if (name.equals("Delete")) {
            int s = JOptionPane.showConfirmDialog(this, "Are you sure to delete?", "Process Delete.", JOptionPane.ERROR_MESSAGE);
            if (s == JOptionPane.YES_OPTION) {
                ph.setDeleted(true);
                inventoryRepo.delete(ph.getKey()).subscribe((t) -> {
                    clear();
                });
            }
        } else {
            int s = JOptionPane.showConfirmDialog(this, "Are you sure to restore?", "Process Restore.", JOptionPane.WARNING_MESSAGE);
            if (s == JOptionPane.YES_OPTION) {
                ph.setDeleted(false);
                inventoryRepo.restore(ph.getKey()).subscribe((t) -> {
                    lblStatus.setText("EDIT");
                    lblStatus.setForeground(Color.blue);
                    enableProcess(true);
                });

            }
        }
    }

    private void historyDialog() {
        if (dialog == null) {
            dialog = new ManufactureHistoryDialog(Global.parentForm);
            dialog.setIconImage(searchIcon);
            dialog.setObserver(this);
            dialog.setInventoryRepo(inventoryRepo);
            dialog.setIntegration(integration);
            dialog.setUserRepo(userRepo);
            dialog.initMain();
            dialog.setSize(Global.width - 100, Global.height - 100);
            dialog.setLocationRelativeTo(null);
        }
        dialog.searchProcess();
    }

    private void getPattern() {
        Stock s = stockAutoCompleter.getStock();
        if (s != null) {
            String stockCode = s.getKey().getStockCode();
            if (processHisDetailTableModel.getListDetail().size() >= 1) {
                int status = JOptionPane.showConfirmDialog(this, "Are you sure to generate pattern.?");
                if (status == JOptionPane.OK_OPTION) {
                    processHisDetailTableModel.clear();
                    generatePattern(stockCode, Global.deptId);

                }
            } else {
                generatePattern(stockCode, Global.deptId);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select stock.");
            txtStock.requestFocus();
        }
    }

    private void generatePattern(String code, Integer deptId) {
        LocalDateTime vouDate = Util1.convertToLocalDateTime(txtStartDate.getDate());
        inventoryRepo.getPattern(code, deptId, Util1.toDateStr(vouDate, "yyyy-MM-dd")).subscribe((t) -> {
            if (!t.isEmpty()) {
                String input = JOptionPane.showInputDialog("Enter Qty.");
                float qty = Util1.getFloat(input);
                if (qty > 0) {
                    txtQty.setValue(qty);
                    for (Pattern p : t) {
                        String stockCode = p.getKey().getStockCode();
                        ProcessHisDetail his = new ProcessHisDetail();
                        ProcessHisDetailKey key = new ProcessHisDetailKey();
                        key.setCompCode(p.getKey().getCompCode());
                        key.setDeptId(p.getKey().getDeptId());
                        key.setLocCode(p.getLocCode());
                        key.setStockCode(stockCode);
                        his.setKey(key);
                        his.setStockName(p.getStockName());
                        his.setStockUsrCode(p.getUserCode());
                        his.setLocName(p.getLocName());
                        his.setQty(p.getQty() * qty);
                        his.setUnit(p.getUnitCode());
                        his.setVouDate(vouDate);
                        his.setPrice(p.getPrice());
                        processHisDetailTableModel.addObject(his);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Qty");
                }

            } else {
                JOptionPane.showMessageDialog(this, "No pattern.");
            }
        }, (e) -> {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }, () -> {
            processHisDetailTableModel.addNewRow();
            processHisDetailTableModel.calPrice();
            focusOnTable();
        });

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Evgditor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

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
        lblStatus = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txtProNo = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        txtQty = new javax.swing.JFormattedTextField();
        txtPrice = new javax.swing.JFormattedTextField();
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

        chkFinish.setFont(Global.lableFont);
        chkFinish.setText("Finished");

        lblStatus.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        lblStatus.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
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

        txtQty.setEditable(false);
        txtQty.setFont(Global.textFont);
        txtQty.setName("txtQty"); // NOI18N

        txtPrice.setEditable(false);
        txtPrice.setFont(Global.amtFont);
        txtPrice.setName("txtPrice"); // NOI18N

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
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(11, 11, 11)
                        .addComponent(chkFinish, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelProcessLayout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11)
                        .addComponent(txtUnit))
                    .addGroup(panelProcessLayout.createSequentialGroup()
                        .addGroup(panelProcessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(11, 11, 11)
                        .addGroup(panelProcessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtVouNo, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPrice)
                            .addComponent(txtAmt)))
                    .addGroup(panelProcessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE))
                    .addGroup(panelProcessLayout.createSequentialGroup()
                        .addGap(69, 69, 69)
                        .addGroup(panelProcessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtQty, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtStock))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelProcessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelProcessLayout.createSequentialGroup()
                        .addGroup(panelProcessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelProcessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtRemark)
                            .addComponent(txtLocation)
                            .addComponent(txtEndDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtStartDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtProNo, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(panelProcessLayout.createSequentialGroup()
                                .addComponent(txtPt)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(panelProcessLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(lblStatus)))
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
                    .addGroup(panelProcessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel9)
                        .addComponent(txtStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelProcessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtEndDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelProcessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel12)
                        .addComponent(txtQty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(6, 6, 6)
                .addGroup(panelProcessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(txtUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelProcessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(txtProNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelProcessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelProcessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(panelProcessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel15)
                        .addComponent(chkFinish))
                    .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
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
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(lblRecord, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)
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
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE))
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelProcess, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelProcess, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkFinish;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblRecord;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPanel panelProcess;
    private javax.swing.JRadioButton rdoAvg;
    private javax.swing.JRadioButton rdoProRecent;
    private javax.swing.JRadioButton rdoRecent;
    private javax.swing.JTable tblProcessDetail;
    private javax.swing.JFormattedTextField txtAmt;
    private com.toedter.calendar.JDateChooser txtEndDate;
    private javax.swing.JTextField txtLocation;
    private javax.swing.JFormattedTextField txtPrice;
    private javax.swing.JTextField txtProNo;
    private javax.swing.JTextField txtPt;
    private javax.swing.JFormattedTextField txtQty;
    private javax.swing.JTextField txtRemark;
    private com.toedter.calendar.JDateChooser txtStartDate;
    private javax.swing.JTextField txtStock;
    private javax.swing.JTextField txtUnit;
    private javax.swing.JTextField txtVouNo;
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
        historyDialog();
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
                inventoryRepo.findUnit(s.getPurUnitCode()).subscribe((t) -> {
                    unitAutoCompleter.setStockUnit(t);
                });
                getPattern();
            }
        } else if (str.equals("Selected")) {
            if (selectObj instanceof ProcessHis his) {
                inventoryRepo.findProcess(his.getKey(), his.isLocal()).subscribe((t) -> {
                    setProcess(t, his.isLocal());
                });
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
                }
            }
        }
    }

}
