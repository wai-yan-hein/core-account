/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry;

import com.acc.dialog.FindDialog;
import com.common.DateLockUtil;
import com.common.DecimalFormatRender;
import com.common.Global;
import com.common.JasperReportUtil;
import com.common.KeyPropagate;
import com.common.PanelControl;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.editor.StockCellEditor;
import com.inventory.editor.TraderAutoCompleter;
import com.inventory.entity.SaleHis;
import com.inventory.entity.SaleHisDetail;
import com.inventory.entity.SaleHisKey;
import com.inventory.entity.Trader;
import com.inventory.entity.VSale;
import com.repo.InventoryRepo;
import com.inventory.ui.common.RFIDTableModel;
import com.inventory.ui.entry.dialog.SaleHistoryDialog;
import com.user.editor.AutoClearEditor;
import com.toedter.calendar.JTextFieldDateEditor;
import com.repo.UserRepo;
import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JsonDataSource;

/**
 *
 * @author wai yan
 */
@Slf4j
public class RFID extends javax.swing.JPanel implements SelectionObserver, KeyListener, KeyPropagate, PanelControl {

    private List<SaleHisDetail> listDetail = new ArrayList();
    private final RFIDTableModel tableModel = new RFIDTableModel();
    private SaleHistoryDialog dialog;
    private InventoryRepo inventoryRepo;
    private UserRepo userRepo;
    private TraderAutoCompleter traderAutoCompleter;
    private SelectionObserver observer;
    private SaleHis saleHis = new SaleHis();
    private JProgressBar progress;
    private String locCode;
    private FindDialog findDialog;

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
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

    /**
     * Creates new form SaleEntry1
     */
    public RFID() {
        initComponents();
        lblStatus.setForeground(Color.GREEN);
        initKeyListener();
        initTextBoxFormat();
        initTextBoxValue();
        initDateListner();
        actionMapping();
    }

    private void checkValidation() {
        inventoryRepo.getDefaultLocation().subscribe((l) -> {
            if (l == null) {
                JOptionPane.showMessageDialog(this, "Config Default Location.");
            } else {
                locCode = l.getKey().getLocCode();
            }
        });
        chkPrint.setSelected(Util1.getBoolean(ProUtil.getProperty("printer.print")));
    }

    private void actionMapping() {
        String solve = "delete";
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        tblRFID.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, solve);
        tblRFID.getActionMap().put(solve, new DeleteAction());
    }

    private class DeleteAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            deleteTran();
        }
    }

    private void initDateListner() {
        txtSaleDate.getDateEditor().getUiComponent().setName("txtSaleDate");
        txtSaleDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtSaleDate.getDateEditor().getUiComponent().addFocusListener(fa);
        txtRFID.addFocusListener(fa);
        txtCus.addFocusListener(fa);
    }
    private final FocusAdapter fa = new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            if (e.getSource() instanceof JTextFieldDateEditor editor) {
                editor.selectAll();
            } else if (e.getSource() instanceof JTextField txt) {
                txt.selectAll();
            }
        }

    };

    public void initMain() {
        checkValidation();
        initCombo();
        initSaleTable();
        assignDefaultValue();
        initFind();
    }

    private void initFind() {
        findDialog = new FindDialog(Global.parentForm, tblRFID);
    }

    private void initSaleTable() {
        tblRFID.setModel(tableModel);
        tableModel.setObserver(this);
        tableModel.setInventoryRepo(inventoryRepo);
        tableModel.setTable(tblRFID);
        tableModel.setLocCode(locCode);
        tableModel.addNewRow();
        tblRFID.getTableHeader().setFont(Global.tblHeaderFont);
        tblRFID.setCellSelectionEnabled(true);
        tblRFID.getColumnModel().getColumn(0).setPreferredWidth(50);//Code
        tblRFID.getColumnModel().getColumn(1).setPreferredWidth(450);//Name
        tblRFID.getColumnModel().getColumn(2).setPreferredWidth(10);//Qty
        tblRFID.getColumnModel().getColumn(3).setPreferredWidth(5);//Unit
        tblRFID.getColumnModel().getColumn(4).setPreferredWidth(20);//price
        tblRFID.getColumnModel().getColumn(5).setPreferredWidth(20);//amount
        tblRFID.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor(inventoryRepo, ProUtil.isSSContain()));
        tblRFID.getColumnModel().getColumn(1).setCellEditor(new StockCellEditor(inventoryRepo, ProUtil.isSSContain()));
        tblRFID.getColumnModel().getColumn(2).setCellEditor(new AutoClearEditor());
        tblRFID.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());
        tblRFID.setDefaultRenderer(Object.class, new DecimalFormatRender());
        tblRFID.setDefaultRenderer(Float.class, new DecimalFormatRender());
        tblRFID.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblRFID.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void initCombo() {
        traderAutoCompleter = new TraderAutoCompleter(txtCus, inventoryRepo, null, false, "CUS");
        traderAutoCompleter.setObserver(this);
    }

    private void initKeyListener() {
        txtSaleDate.getDateEditor().getUiComponent().setName("txtSaleDate");
        txtSaleDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtVouNo.addKeyListener(this);
        txtCus.addKeyListener(this);
        tblRFID.addKeyListener(this);
    }

    private void initTextBoxValue() {
        txtVouTotal.setValue(0);
    }

    private void initTextBoxFormat() {
        txtVouTotal.setFormatterFactory(Util1.getDecimalFormat());
    }

    private void assignDefaultValue() {
        txtSaleDate.setDate(Util1.getTodayDate());
        inventoryRepo.getDefaultCustomer().subscribe((t) -> {
            traderAutoCompleter.setTrader(t);
        });
        progress.setIndeterminate(false);
        txtVouNo.setText(null);
        txtRFID.setText(null);
        setSaleInfo();
    }

    private void clear() {
        disableForm(true);
        tableModel.removeListDetail();
        tableModel.clearDelList();
        initTextBoxValue();
        assignDefaultValue();
        saleHis = new SaleHis();
        if (!lblStatus.getText().equals("NEW")) {
            txtSaleDate.setDate(Util1.getTodayDate());
        }
        lblStatus.setText("NEW");
        lblStatus.setForeground(Color.GREEN);
        progress.setIndeterminate(false);
        txtRFID.requestFocus();
    }

    private void setSaleInfo() {
        inventoryRepo.getSaleVoucherInfo(Util1.toDateStr(txtSaleDate.getDate(), "yyyy-MM-dd")).subscribe((t) -> {
            txtVouCount.setValue(Util1.getInteger(t.getQty()));
            txtVouAmt.setValue(Util1.getFloat(t.getAmount()));
        });

    }

    public void saveSale(boolean print) {
        try {
            if (isValidEntry() && tableModel.isValidEntry()) {
                if (DateLockUtil.isLockDate(txtSaleDate.getDate())) {
                    DateLockUtil.showMessage(this);
                    txtSaleDate.requestFocus();
                    return;
                }
                saleHis.setListSH(tableModel.getListDetail());
                observer.selected("save", false);
                inventoryRepo.save(saleHis).subscribe((t) -> {
                    clear();
                    if (chkPrint.isSelected() || print) {
                        printVoucher(t.getKey().getVouNo());
                    }

                }, (e) -> {
                    JOptionPane.showMessageDialog(this, e.getMessage());
                    observer.selected("save", false);
                    progress.setIndeterminate(false);
                });

            }
        } catch (HeadlessException ex) {
            log.error("Save Sale :" + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Could'nt saved.");
        }
    }

    private boolean isValidEntry() {
        boolean status = true;
        if (lblStatus.getText().equals("DELETED")) {
            status = false;
            clear();
        } else if (!Util1.isDateBetween(txtSaleDate.getDate())) {
            JOptionPane.showMessageDialog(this, "Invalid Date.",
                    "Validation.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtSaleDate.requestFocus();
        } else {
            saleHis.setDeleted(Util1.getNullTo(saleHis.isDeleted()));
            saleHis.setTraderCode(traderAutoCompleter.getTrader().getKey().getCode());
            saleHis.setVouTotal(Util1.getDouble(txtVouTotal.getValue()));
            saleHis.setGrandTotal(saleHis.getVouTotal());
            saleHis.setPaid(saleHis.getVouTotal());
            saleHis.setStatus(lblStatus.getText());
            saleHis.setVouDate(Util1.convertToLocalDateTime(txtSaleDate.getDate()));
            saleHis.setMacId(Global.macId);
            saleHis.setCurCode(Global.currency);
            saleHis.setLocCode(locCode);
            if (lblStatus.getText().equals("NEW")) {
                SaleHisKey key = new SaleHisKey();
                key.setCompCode(Global.compCode);
                key.setVouNo(null);
                saleHis.setKey(key);
                saleHis.setDeptId(Global.deptId);
                saleHis.setCreatedDate(LocalDateTime.now());
                saleHis.setCreatedBy(Global.loginUser.getUserCode());
                saleHis.setSession(Global.sessionId);
            } else {
                saleHis.setUpdatedBy(Global.loginUser.getUserCode());
            }
        }
        return status;
    }

    private void deleteSale() {
        String status = lblStatus.getText();
        switch (status) {
            case "EDIT" -> {
                int yes_no = JOptionPane.showConfirmDialog(this,
                        "Are you sure to delete?", "Sale Voucher Delete.", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                if (yes_no == 0) {
                    inventoryRepo.delete(saleHis).subscribe((t) -> {
                        clear();
                    });
                }
            }
            case "DELETED" -> {
                int yes_no = JOptionPane.showConfirmDialog(this,
                        "Are you sure to restore?", "Sale Voucher Restore.", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (yes_no == 0) {
                    saleHis.setDeleted(false);
                    inventoryRepo.restore(saleHis).subscribe((t) -> {
                        lblStatus.setText("EDIT");
                        lblStatus.setForeground(Color.blue);
                        disableForm(true);
                    });

                }
            }
            default ->
                JOptionPane.showMessageDialog(this, "Voucher can't delete.");
        }

    }

    private void deleteTran() {
        int row = tblRFID.convertRowIndexToModel(tblRFID.getSelectedRow());
        if (row >= 0) {
            if (tblRFID.getCellEditor() != null) {
                tblRFID.getCellEditor().stopCellEditing();
            }
            int yes_no = JOptionPane.showConfirmDialog(this,
                    "Are you sure to delete?", "Sale Transaction delete.", JOptionPane.YES_NO_OPTION);
            if (yes_no == 0) {
                tableModel.delete(row);
                calculateTotalAmount();
            }
        }
    }

    private void calculateTotalAmount() {
        float totalAmount = 0.0f;
        listDetail = tableModel.getListDetail();
        totalAmount = listDetail.stream().map(sdh -> Util1.getFloat(sdh.getAmount())).reduce(totalAmount, (accumulator, _item) -> accumulator + _item);
        txtVouTotal.setValue(totalAmount);
    }

    public void historySale() {
        if (dialog == null) {
            dialog = new SaleHistoryDialog(Global.parentForm, 1);
            dialog.setInventoryRepo(inventoryRepo);
            dialog.setUserRepo(userRepo);
            dialog.setObserver(this);
            dialog.initMain();
            dialog.setSize(Global.width - 20, Global.height - 20);
            dialog.setLocationRelativeTo(null);
        }
        dialog.search();
    }

    public void setSaleVoucher(SaleHis sh) {
        if (sh != null) {
            progress.setIndeterminate(true);
            saleHis = sh;
            inventoryRepo.findTrader(saleHis.getTraderCode()).doOnSuccess((t) -> {
                traderAutoCompleter.setTrader(t);
            }).subscribe();
            String vouNo = sh.getKey().getVouNo();
            inventoryRepo.getSaleDetail(vouNo).subscribe((t) -> {
                tableModel.setListDetail(t);
                tableModel.addNewRow();
                if (sh.isVouLock()) {
                    lblStatus.setText("Voucher is locked.");
                    lblStatus.setForeground(Color.RED);
                    disableForm(false);
                } else if (!ProUtil.isSaleEdit()) {
                    lblStatus.setText("No Permission.");
                    lblStatus.setForeground(Color.RED);
                    disableForm(false);
                    observer.selected("print", true);
                } else if (Util1.getBoolean(sh.isDeleted())) {
                    lblStatus.setText("DELETED");
                    lblStatus.setForeground(Color.RED);
                    disableForm(false);
                } else if (DateLockUtil.isLockDate(saleHis.getVouDate())) {
                    lblStatus.setText(DateLockUtil.MESSAGE);
                    lblStatus.setForeground(Color.RED);
                    disableForm(false);
                } else {
                    lblStatus.setText("EDIT");
                    lblStatus.setForeground(Color.blue);
                    disableForm(true);
                }
                txtVouNo.setText(saleHis.getKey().getVouNo());
                txtSaleDate.setDate(Util1.convertToDate(saleHis.getVouDate()));
                txtVouTotal.setValue(Util1.getFloat(saleHis.getVouTotal()));
                focusTable();
                progress.setIndeterminate(false);
            }, (e) -> {
                progress.setIndeterminate(false);
                JOptionPane.showMessageDialog(this, e.getMessage());
            });
        }
    }

    private void disableForm(boolean status) {
        tblRFID.setEnabled(status);
        panelSale.setEnabled(status);
        txtSaleDate.setEnabled(status);
        txtCus.setEnabled(status);
        txtRFID.setEnabled(status);
        observer.selected("save", status);
        observer.selected("print", status);

    }

    private void findTrader() {
        String rfId = txtRFID.getText();
        if (!rfId.isEmpty()) {
            inventoryRepo.findTraderRFID(rfId).subscribe((t) -> {
                traderAutoCompleter.setTrader(t);
                generateTransaction(t);
            });

        }
    }

    private void generateTransaction(Trader t) {
        if (t == null) {
            JOptionPane.showMessageDialog(this, "Customer Not Found.", "Message", JOptionPane.WARNING_MESSAGE);
        } else {
            inventoryRepo.getDefaultStock().subscribe((s) -> {
                if (s != null) {
                    tableModel.clear();
                    SaleHisDetail sd = new SaleHisDetail();
                    sd.setUserCode(sd.getUserCode());
                    sd.setStockCode(s.getKey().getStockCode());
                    sd.setStockName(s.getStockName());
                    sd.setQty(1.0);
                    sd.setUnitCode(s.getSaleUnitCode());
                    sd.setPrice(Util1.getDouble(s.getSalePriceN()));
                    sd.setAmount(Util1.getDouble(sd.getQty()) * Util1.getDouble(sd.getPrice()));
                    sd.setLocCode(locCode);
                    tableModel.addSale(sd);
                    calculateTotalAmount();
                    if (chkPrint.isSelected()) {
                        saveSale(true);
                    } else {
                        tableModel.addNewRow();
                        focusTable();
                    }
                }
            });

        }
    }

    private void printVoucher(String vouNo) {
        inventoryRepo.getSaleReport(vouNo).subscribe((t) -> {
            try {
                Map<String, Object> param = new HashMap<>();
                param.put("p_print_date", Util1.getTodayDateTime());
                param.put("p_comp_name", Global.companyName);
                param.put("p_comp_address", Global.companyAddress);
                param.put("p_comp_phone", Global.companyPhone);
                param.put("p_logo_path", ProUtil.logoPath());
                String reportPath = ProUtil.getReportPath() + "RFIDVoucher.jasper";
                ByteArrayInputStream stream = new ByteArrayInputStream(Util1.listToByteArray(t));
                JsonDataSource ds = new JsonDataSource(stream);
                JasperPrint jp = JasperFillManager.fillReport(reportPath, param, ds);
                String printerName = ProUtil.getProperty("printer.name");
                int count = Util1.getIntegerOne(ProUtil.getProperty("printer.pages"));
                JasperReportUtil.print(jp, printerName, count, 4);
            } catch (JRException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }, (e) -> {
            JOptionPane.showMessageDialog(this, e.getMessage());
        });

    }

    private void focusTable() {
        int rc = tblRFID.getRowCount();
        if (rc >= 1) {
            tblRFID.setRowSelectionInterval(rc - 1, rc - 1);
            tblRFID.setColumnSelectionInterval(0, 0);
            tblRFID.requestFocus();
        } else {
            txtRFID.requestFocus();
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

        panelSale = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtCus = new javax.swing.JTextField();
        txtVouNo = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        txtSaleDate = new com.toedter.calendar.JDateChooser();
        jLabel21 = new javax.swing.JLabel();
        txtRFID = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtVouTotal = new javax.swing.JFormattedTextField();
        lblStatus = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblRFID = new javax.swing.JTable();
        chkPrint = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtVouCount = new javax.swing.JFormattedTextField();
        txtVouAmt = new javax.swing.JFormattedTextField();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                formPropertyChange(evt);
            }
        });

        panelSale.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel17.setFont(Global.lableFont);
        jLabel17.setText("Vou No");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Customer");

        txtCus.setFont(Global.textFont);
        txtCus.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCus.setName("txtCus"); // NOI18N
        txtCus.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCusFocusGained(evt);
            }
        });
        txtCus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCusActionPerformed(evt);
            }
        });

        txtVouNo.setEditable(false);
        txtVouNo.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtVouNo.setFont(Global.textFont);
        txtVouNo.setName("txtVouNo"); // NOI18N

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Vou Date");

        txtSaleDate.setDateFormatString("dd/MM/yyyy");
        txtSaleDate.setFont(Global.textFont);
        txtSaleDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSaleDateFocusGained(evt);
            }
        });

        jLabel21.setFont(Global.lableFont);
        jLabel21.setText("RFID");

        txtRFID.setFont(Global.textFont);
        txtRFID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRFIDActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelSaleLayout = new javax.swing.GroupLayout(panelSale);
        panelSale.setLayout(panelSaleLayout);
        panelSaleLayout.setHorizontalGroup(
            panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSaleLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2)
                    .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCus, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
                    .addComponent(txtRFID))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtVouNo)
                    .addComponent(txtSaleDate, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelSaleLayout.setVerticalGroup(
            panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSaleLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtRFID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtCus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2))
                    .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(txtSaleDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel13.setFont(Global.lableFont);
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel13.setText("Vou Total :");

        txtVouTotal.setEditable(false);
        txtVouTotal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtVouTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVouTotal.setFont(Global.amtFont);

        lblStatus.setFont(Global.lableFont);
        lblStatus.setText("NEW");

        tblRFID.setFont(Global.textFont);
        tblRFID.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblRFID.setRowHeight(Global.tblRowHeight);
        tblRFID.setShowHorizontalLines(true);
        tblRFID.setShowVerticalLines(true);
        tblRFID.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblRFIDMouseClicked(evt);
            }
        });
        tblRFID.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblRFIDKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblRFID);

        chkPrint.setText("Auto Print");
        chkPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkPrintActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Total Vou :");

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Total Amt :");

        txtVouCount.setEditable(false);
        txtVouCount.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txtVouCount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVouCount.setFont(Global.amtFont);

        txtVouAmt.setEditable(false);
        txtVouAmt.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,###"))));
        txtVouAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVouAmt.setFont(Global.amtFont);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtVouAmt, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                    .addComponent(txtVouCount))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtVouCount)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtVouAmt))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtVouTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelSale, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkPrint)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 70, Short.MAX_VALUE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(panelSale, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(chkPrint)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(txtVouTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblStatus))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        observer.selected("control", this);
        txtRFID.requestFocus();
    }//GEN-LAST:event_formComponentShown

    private void txtCusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCusActionPerformed
    }//GEN-LAST:event_txtCusActionPerformed

    private void txtCusFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCusFocusGained
        txtCus.selectAll();
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCusFocusGained

    private void tblRFIDMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblRFIDMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tblRFIDMouseClicked

    private void tblRFIDKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblRFIDKeyReleased
        // TODO add your handling code here:

    }//GEN-LAST:event_tblRFIDKeyReleased

    private void formPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_formPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_formPropertyChange

    private void txtSaleDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSaleDateFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSaleDateFocusGained

    private void txtRFIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRFIDActionPerformed
        // TODO add your handling code here:
        findTrader();
    }//GEN-LAST:event_txtRFIDActionPerformed

    private void chkPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkPrintActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkPrintActionPerformed

    @Override
    public void keyEvent(KeyEvent e) {

    }

    @Override
    public void selected(Object source, Object selectObj) {
        switch (source.toString()) {
            case "CustomerList" -> {
                try {
                    Trader cus = (Trader) selectObj;
                    if (cus != null) {
                        txtCus.setText(cus.getTraderName());
                    }
                } catch (Exception ex) {
                    log.error("selected CustomerList : " + selectObj + " - " + ex.getMessage());
                }
            }
            case "CAL-TOTAL" ->
                calculateTotalAmount();
            case "SALE-HISTORY" -> {
                if (selectObj instanceof VSale s) {
                    inventoryRepo.findSale(s.getVouNo()).subscribe((t) -> {
                        setSaleVoucher(t);
                    });
                }
            }
            case "CUS" -> {
                Trader t = traderAutoCompleter.getTrader();
                generateTransaction(t);
                focusTable();
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

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkPrint;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPanel panelSale;
    private javax.swing.JTable tblRFID;
    private javax.swing.JTextField txtCus;
    private javax.swing.JTextField txtRFID;
    private com.toedter.calendar.JDateChooser txtSaleDate;
    private javax.swing.JFormattedTextField txtVouAmt;
    private javax.swing.JFormattedTextField txtVouCount;
    private javax.swing.JFormattedTextField txtVouNo;
    private javax.swing.JFormattedTextField txtVouTotal;
    // End of variables declaration//GEN-END:variables

    @Override
    public void delete() {
        deleteSale();
    }

    @Override
    public void print() {
        saveSale(true);
    }

    @Override
    public void save() {
        saveSale(false);
    }

    @Override
    public void newForm() {
        clear();
    }

    @Override
    public void history() {
        historySale();
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
}
