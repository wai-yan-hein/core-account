/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry;

import com.CloudIntegration;
import com.common.ComponentUtil;
import com.common.DateLockUtil;
import com.common.DecimalFormatRender;
import com.common.Global;
import com.common.KeyPropagate;
import com.common.PanelControl;
import com.common.ProUtil;
import com.common.RowHeader;
import com.common.SelectionObserver;
import com.common.Util1;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.editor.LocationCellEditor;
import com.inventory.editor.StockCellEditor;
import com.inventory.editor.TraderAutoCompleter;
import com.inventory.model.Location;
import com.inventory.model.Order;
import com.inventory.model.RetInHis;
import com.inventory.model.RetInHisDetail;
import com.inventory.model.RetInHisKey;
import com.inventory.model.Trader;
import com.inventory.model.VReturnIn;
import com.repo.InventoryRepo;
import com.inventory.ui.common.ReturnInTableModel;
import com.inventory.ui.entry.dialog.ReturnInHistoryDialog;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.inventory.editor.StockUnitEditor;
import com.toedter.calendar.JTextFieldDateEditor;
import com.repo.UserRepo;
import com.user.editor.CurrencyAutoCompleter;
import com.user.editor.ProjectAutoCompleter;
import com.user.model.Project;
import com.user.model.ProjectKey;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JList;
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
import net.sf.jasperreports.view.JasperViewer;
import reactor.core.publisher.Mono;

/**
 *
 * @author wai yan
 */
@Slf4j
public class ReturnIn extends javax.swing.JPanel implements SelectionObserver, KeyListener, KeyPropagate, PanelControl {

    private final Image searchIcon = new ImageIcon(this.getClass().getResource("/images/search.png")).getImage();
    private List<RetInHisDetail> listDetail = new ArrayList();
    private final ReturnInTableModel retInTableModel = new ReturnInTableModel();
    private ReturnInHistoryDialog dialog;
    private InventoryRepo inventoryRepo;
    private UserRepo userRepo;
    private CloudIntegration integration;
    private CurrencyAutoCompleter currAutoCompleter;
    private TraderAutoCompleter traderAutoCompleter;
    private LocationAutoCompleter locationAutoCompleter;
    private ProjectAutoCompleter projectAutoCompleter;
    private SelectionObserver observer;
    private JProgressBar progress;
    private RetInHis ri = new RetInHis();
    private Mono<List<Location>> monoLoc;

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public void setIntegration(CloudIntegration integration) {
        this.integration = integration;
    }

    public LocationAutoCompleter getLocationAutoCompleter() {
        return locationAutoCompleter;
    }

    public void setLocationAutoCompleter(LocationAutoCompleter locationAutoCompleter) {
        this.locationAutoCompleter = locationAutoCompleter;
    }

    public SelectionObserver getObserver() {
        return observer;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }

    /**
     * Creates new form RetInchase
     */
    public ReturnIn() {
        initComponents();
        lblStatus.setForeground(Color.GREEN);
        initKeyListener();
        initTextBoxFormat();
        initTextBoxValue();
        initDateListner();
        actionMapping();
    }

    public void initMain() {
        initCombo();
        initRetInTable();
        initRowHeader();
        assignDefaultValue();
    }

    private void initRowHeader() {
        RowHeader header = new RowHeader();
        JList list = header.createRowHeader(tblRet, 30);
        scroll.setRowHeaderView(list);
    }

    private void actionMapping() {
        String solve = "delete";
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        tblRet.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, solve);
        tblRet.getActionMap().put(solve, new DeleteAction());

    }

    private class DeleteAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            deleteTran();
        }
    }

    private void initDateListner() {
        ComponentUtil.addFocusListener(this);

    }

    private void initRetInTable() {

        tblRet.setModel(retInTableModel);
        retInTableModel.setParent(tblRet);
        retInTableModel.setReturnIn(this);
        retInTableModel.addNewRow();
        retInTableModel.setObserver(this);
        retInTableModel.setInventoryRepo(inventoryRepo);
        retInTableModel.setVouDate(txtVouDate);
        retInTableModel.setLblRec(lblRec);
        tblRet.getTableHeader().setFont(Global.tblHeaderFont);
        tblRet.setCellSelectionEnabled(true);
        tblRet.getColumnModel().getColumn(0).setPreferredWidth(50);//Code
        tblRet.getColumnModel().getColumn(1).setPreferredWidth(450);//Name
        tblRet.getColumnModel().getColumn(2).setPreferredWidth(60);//rel
        tblRet.getColumnModel().getColumn(3).setPreferredWidth(60);//Location
        tblRet.getColumnModel().getColumn(4).setPreferredWidth(60);//qty
        tblRet.getColumnModel().getColumn(5).setPreferredWidth(1);//unit
        tblRet.getColumnModel().getColumn(6).setPreferredWidth(60);//weight
        tblRet.getColumnModel().getColumn(7).setPreferredWidth(1);//weight unit
        tblRet.getColumnModel().getColumn(8).setPreferredWidth(1);//price
        tblRet.getColumnModel().getColumn(9).setPreferredWidth(40);//amt
        tblRet.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor(inventoryRepo));
        tblRet.getColumnModel().getColumn(1).setCellEditor(new StockCellEditor(inventoryRepo));
        monoLoc.subscribe((t) -> {
            tblRet.getColumnModel().getColumn(3).setCellEditor(new LocationCellEditor(t));
        });
        tblRet.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());//qty
        tblRet.getColumnModel().getColumn(6).setCellEditor(new AutoClearEditor());
        inventoryRepo.getStockUnit().subscribe((t) -> {
            tblRet.getColumnModel().getColumn(5).setCellEditor(new StockUnitEditor(t));
            tblRet.getColumnModel().getColumn(7).setCellEditor(new StockUnitEditor(t));
        });
        tblRet.getColumnModel().getColumn(7).setCellEditor(new AutoClearEditor());
        tblRet.getColumnModel().getColumn(8).setCellEditor(new AutoClearEditor());
        tblRet.setDefaultRenderer(Object.class, new DecimalFormatRender());
        tblRet.setDefaultRenderer(Double.class, new DecimalFormatRender());
        tblRet.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblRet.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    }

    private void initCombo() {
        traderAutoCompleter = new TraderAutoCompleter(txtCus, inventoryRepo, null, false, "CUS");
        traderAutoCompleter.setObserver(this);
        monoLoc = inventoryRepo.getLocation();
        locationAutoCompleter = new LocationAutoCompleter(txtLocation, null, false, false);
        locationAutoCompleter.setObserver(this);
        currAutoCompleter = new CurrencyAutoCompleter(txtCurrency, null);
        projectAutoCompleter = new ProjectAutoCompleter(txtProjectNo, userRepo, null, false);
        projectAutoCompleter.setObserver(this);
        monoLoc.doOnSuccess((t) -> {
            locationAutoCompleter.setListLocation(t);
        }).subscribe();
        userRepo.getCurrency().doOnSuccess((t) -> {
            currAutoCompleter.setListCurrency(t);
        }).subscribe();
        userRepo.getDefaultCurrency().doOnSuccess((c) -> {
            currAutoCompleter.setCurrency(c);
        }).subscribe();
    }

    private void initKeyListener() {
        txtVouDate.getDateEditor().getUiComponent().setName("txtVouDate");
        txtVouDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtVouNo.addKeyListener(this);
        txtRemark.addKeyListener(this);
        txtCus.addKeyListener(this);
        txtLocation.addKeyListener(this);
        txtCurrency.addKeyListener(this);
        tblRet.addKeyListener(this);
    }

    private void initTextBoxValue() {
        txtVouTotal.setValue(0.00);
        txtVouDiscount.setValue(0.00);
        txtTax.setValue(0.00);
        txtVouPaid.setValue(0.00);
        txtVouBalance.setValue(0.00);
        txtVouTaxP.setValue(0.00);
        txtVouDiscP.setValue(0.00);
        txtGrandTotal.setValue(0.00);
    }

    private void initTextBoxFormat() {
        ComponentUtil.setTextProperty(this);
    }

    private void assignDefaultValue() {
        userRepo.getDefaultCurrency().doOnSuccess((t) -> {
            currAutoCompleter.setCurrency(t);
        }).subscribe();
        inventoryRepo.getDefaultLocation().doOnSuccess((t) -> {
            locationAutoCompleter.setLocation(t);
        }).subscribe();
        inventoryRepo.getDefaultCustomer().doOnSuccess((t) -> {
            traderAutoCompleter.setTrader(t);
        }).subscribe();
        txtVouDate.setDate(Util1.getTodayDate());
        progress.setIndeterminate(false);
        txtCus.requestFocus();
        txtCurrency.setEnabled(ProUtil.isMultiCur());
        txtVouNo.setText(null);
    }

    private void clear(boolean focus) {
        disableForm(true);
        retInTableModel.clear();
        retInTableModel.addNewRow();
        retInTableModel.clearDelList();
        initTextBoxValue();
        assignDefaultValue();
        ri = new RetInHis();
        lblStatus.setText("NEW");
        lblStatus.setForeground(Color.GREEN);
        progress.setIndeterminate(false);
        txtRemark.setText(null);
        projectAutoCompleter.setProject(null);
        txtRemark.requestFocus(focus);

    }

    private void deleteTran() {
        int row = tblRet.convertRowIndexToModel(tblRet.getSelectedRow());
        if (row >= 0) {
            if (tblRet.getCellEditor() != null) {
                tblRet.getCellEditor().stopCellEditing();
            }
            int yes_no = JOptionPane.showConfirmDialog(this,
                    "Are you sure to delete?", "Return In Transaction delete.", JOptionPane.YES_NO_OPTION);
            if (yes_no == 0) {
                retInTableModel.delete(row);
                calculateTotalAmount();
            }
        }
    }

    public void saveRetIn(boolean print) {
        if (isValidEntry() && retInTableModel.isValidEntry()) {
            if (DateLockUtil.isLockDate(txtVouDate.getDate())) {
                DateLockUtil.showMessage(this);
                txtVouDate.requestFocus();
                return;
            }
            progress.setIndeterminate(true);
            observer.selected("save", false);
            ri.setListRD(retInTableModel.getListDetail());
            ri.setListDel(retInTableModel.getDelList());
            inventoryRepo.save(ri).doOnSuccess((t) -> {
                progress.setIndeterminate(false);
            }).doOnError((e) -> {
                JOptionPane.showMessageDialog(this, e.getMessage());
                progress.setIndeterminate(false);
                observer.selected("save", false);
            }).doOnTerminate(() -> {
                if (print) {
                    printVoucher(ri);
                }
                clear(false);
            }).subscribe();

        }
    }

    private boolean isValidEntry() {
        boolean status = true;
        Trader trader = traderAutoCompleter.getTrader();
        if (lblStatus.getText().equals("DELETED")) {
            status = false;
            clear(true);
        } else if (trader == null) {
            JOptionPane.showMessageDialog(Global.parentForm, "Invalid Customer Name.",
                    "Choose Customer Name", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtCus.requestFocus();
        } else if (currAutoCompleter.getCurrency() == null) {
            JOptionPane.showMessageDialog(Global.parentForm, "Choose Currency.",
                    "No Currency.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtCurrency.requestFocus();
        } else if (locationAutoCompleter.getLocation() == null) {
            JOptionPane.showMessageDialog(Global.parentForm, "Choose Location.",
                    "No Location.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtLocation.requestFocus();
        } else if (Util1.getDouble(txtVouTotal.getValue()) <= 0) {
            JOptionPane.showMessageDialog(Global.parentForm, "Invalid Amount.",
                    "No RetIn Record.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtLocation.requestFocus();
        } else if (!Util1.isDateBetween(txtVouDate.getDate())) {
            JOptionPane.showMessageDialog(this, "Invalid Date.",
                    "Validation.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtVouDate.requestFocus();
        } else {
            ri.setRemark(txtRemark.getText());
            ri.setDiscP(Util1.getDouble(txtVouDiscP.getValue()));
            ri.setDiscount(Util1.getDouble(txtVouDiscount.getValue()));
            ri.setPaid(Util1.getDouble(txtVouPaid.getValue()));
            ri.setBalance(Util1.getDouble(txtVouBalance.getValue()));
            ri.setCurCode(currAutoCompleter.getCurrency().getCurCode());
            ri.setDeleted(Util1.getNullTo(ri.getDeleted()));
            ri.setLocCode(locationAutoCompleter.getLocation().getKey().getLocCode());
            Project p = projectAutoCompleter.getProject();
            ri.setProjectNo(p == null ? null : p.getKey().getProjectNo());
            ri.setVouDate(Util1.convertToLocalDateTime(txtVouDate.getDate()));
            ri.setTraderCode(trader.getKey().getCode());
            ri.setTraderName(trader.getTraderName());
            ri.setVouTotal(Util1.getDouble(txtVouTotal.getValue()));
            ri.setStatus(lblStatus.getText());
            if (lblStatus.getText().equals("NEW")) {
                RetInHisKey key = new RetInHisKey();
                key.setCompCode(Global.compCode);
                key.setVouNo(null);
                ri.setKey(key);
                ri.setDeptId(Global.deptId);
                ri.setCreatedDate(LocalDateTime.now());
                ri.setCreatedBy(Global.loginUser.getUserCode());
                ri.setSession(Global.sessionId);
                ri.setMacId(Global.macId);
            } else {
                ri.setUpdatedBy(Global.loginUser.getUserCode());
            }
        }
        return status;
    }

    private void deleteRetIn() {
        String status = lblStatus.getText();
        switch (status) {
            case "EDIT" -> {
                int yes_no = JOptionPane.showConfirmDialog(Global.parentForm,
                        "Are you sure to delete?", "Return In Voucher delete.", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                if (yes_no == 0) {
                    inventoryRepo.delete(ri.getKey()).subscribe((t) -> {
                        clear(true);
                    });
                }
            }
            case "DELETED" -> {
                int yes_no = JOptionPane.showConfirmDialog(this,
                        "Are you sure to restore?", "Return In Voucher Restore.", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (yes_no == 0) {
                    ri.setDeleted(false);
                    inventoryRepo.restore(ri.getKey()).subscribe((t) -> {
                        lblStatus.setText("EDIT");
                        lblStatus.setForeground(Color.blue);
                        disableForm(true);
                    });

                }
            }
            default ->
                JOptionPane.showMessageDialog(Global.parentForm, "Voucher can't delete.");
        }

    }

    private void calculateTotalAmount() {
        double totalVouBalance;
        double totalAmount = 0.0f;
        listDetail = retInTableModel.getListDetail();
        totalAmount = listDetail.stream().map(sdh -> Util1.getDouble(sdh.getAmount())).reduce(totalAmount, (accumulator, _item) -> accumulator + _item);
        txtVouTotal.setValue(totalAmount);

        //cal discAmt
        double discp = Util1.getDouble(txtVouDiscP.getValue());
        double discountAmt = (totalAmount * (discp / 100));
        txtVouDiscount.setValue(Util1.getDouble(discountAmt));

        //calculate taxAmt
        double taxp = Util1.getDouble(txtVouTaxP.getValue());
        double afterDiscountAmt = totalAmount - Util1.getDouble(txtVouDiscount.getValue());
        double totalTax = (afterDiscountAmt * taxp) / 100;
        txtTax.setValue(Util1.getDouble(totalTax));
        //
        txtGrandTotal.setValue(totalAmount
                + Util1.getDouble(txtTax.getValue())
                - Util1.getDouble(txtVouDiscount.getValue()));
        totalVouBalance = Util1.getDouble(txtGrandTotal.getValue()) - Util1.getDouble(txtVouPaid.getValue());
        txtVouBalance.setValue(Util1.getDouble(totalVouBalance));
    }

    public void historyRetIn() {
        if (dialog == null) {
            dialog = new ReturnInHistoryDialog(Global.parentForm);
            dialog.setUserRepo(userRepo);
            dialog.setInventoryRepo(inventoryRepo);
            dialog.setIconImage(searchIcon);
            dialog.setObserver(this);
            dialog.initMain();
            dialog.setSize(Global.width - 20, Global.height - 20);
            dialog.setLocationRelativeTo(null);
        }
        dialog.search();
    }

    public void setVoucher(RetInHis retin, boolean local) {
        if (retin != null) {
            progress.setIndeterminate(true);
            ri = retin;
            inventoryRepo.findLocation(ri.getLocCode()).doOnSuccess((t) -> {
                locationAutoCompleter.setLocation(t);
            }).subscribe();
            inventoryRepo.findTrader(ri.getTraderCode()).doOnSuccess((t) -> {
                traderAutoCompleter.setTrader(t);
            }).subscribe();
            userRepo.findCurrency(ri.getCurCode()).doOnSuccess((t) -> {
                currAutoCompleter.setCurrency(t);
            }).subscribe();
            userRepo.find(new ProjectKey(retin.getProjectNo(), Global.compCode)).doOnSuccess(t -> {
                projectAutoCompleter.setProject(t);
            }).subscribe();
            String vouNo = ri.getKey().getVouNo();
            Integer deptId = ri.getDeptId();
            ri.setVouLock(!deptId.equals(Global.deptId));
            inventoryRepo.getReturnInDetail(vouNo, deptId, local)
                    .subscribe((t) -> {
                        retInTableModel.setListDetail(t);
                        retInTableModel.addNewRow();
                        if (ri.isVouLock()) {
                            lblStatus.setText("Voucher is locked.");
                            lblStatus.setForeground(Color.RED);
                            disableForm(false);
                        } else if (Util1.getBoolean(ri.getDeleted())) {
                            lblStatus.setText("DELETED");
                            lblStatus.setForeground(Color.RED);
                            disableForm(false);
                        } else if (DateLockUtil.isLockDate(ri.getVouDate())) {
                            lblStatus.setText(DateLockUtil.MESSAGE);
                            lblStatus.setForeground(Color.RED);
                            disableForm(false);
                        } else {
                            lblStatus.setText("EDIT");
                            lblStatus.setForeground(Color.blue);
                            disableForm(true);
                        }
                        txtVouNo.setText(ri.getKey().getVouNo());
                        txtRemark.setText(ri.getRemark());
                        txtVouDate.setDate(Util1.convertToDate(ri.getVouDate()));
                        txtVouTotal.setValue(Util1.getDouble(ri.getVouTotal()));
                        txtVouDiscP.setValue(Util1.getDouble(ri.getDiscP()));
                        txtVouDiscount.setValue(Util1.getDouble(ri.getDiscount()));
                        txtVouPaid.setValue(Util1.getDouble(ri.getPaid()));
                        txtVouBalance.setValue(Util1.getDouble(ri.getBalance()));
                        txtGrandTotal.setValue(Util1.getDouble(txtGrandTotal.getValue()));
                        chkPaid.setSelected(Util1.getDouble(ri.getPaid()) > 0);
                        focusTable();
                        progress.setIndeterminate(false);
                    }, (e) -> {
                        progress.setIndeterminate(false);
                        JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
                    });
        }
    }

    private void disableForm(boolean status) {
        ComponentUtil.enableForm(this, status);
        observer.selected("save", status);
        observer.selected("delete", status);
        observer.selected("print", status);
    }

    private void setAllLocation() {
        List<RetInHisDetail> listRetInDetail = retInTableModel.getListDetail();
        Location loc = locationAutoCompleter.getLocation();
        if (listRetInDetail != null) {
            listRetInDetail.forEach(sd -> {
                sd.setLocCode(loc.getKey().getLocCode());
                sd.setLocName(loc.getLocName());
            });
        }
        retInTableModel.setListDetail(listRetInDetail);
    }

    private void printVoucher(RetInHis ri) {
        try {
            List<RetInHisDetail> list = ri.getListRD().stream().filter((t) -> t.getStockCode() != null).toList();
            String reportName = "ReturnInVoucher";
            Map<String, Object> param = new HashMap<>();
            param.put("p_print_date", Util1.getTodayDateTime());
            param.put("p_comp_name", Global.companyName);
            param.put("p_comp_address", Global.companyAddress);
            param.put("p_comp_phone", Global.companyPhone);
            param.put("p_logo_path", ProUtil.logoPath());
            param.put("p_vou_no", ri.getKey().getVouNo());
            param.put("p_trader_name", ri.getTraderName());
            param.put("p_remark", ri.getRemark());
            param.put("p_vou_total", ri.getVouTotal());
            param.put("p_vou_paid", ri.getPaid());
            param.put("p_vou_balance", ri.getBalance());
            param.put("p_vou_date", Util1.toDateStr(ri.getVouDate(), Global.dateFormat));
            param.put("p_sub_report_dir", "report/");
            String reportPath = String.format("report%s%s", File.separator, reportName.concat(".jasper"));
            ObjectMapper mapper = new ObjectMapper();
            JsonNode n = mapper.readTree(Util1.gson.toJson(list));
            JsonDataSource d = new JsonDataSource(n, null) {
            };
            JasperPrint js = JasperFillManager.fillReport(reportPath, param, d);
            JasperViewer.viewReport(js, false);
        } catch (JRException | JsonProcessingException ex) {
            log.error("printVoucher : " + ex.getMessage());
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void focusTable() {
        int rc = tblRet.getRowCount();
        if (rc > 1) {
            tblRet.setRowSelectionInterval(rc - 1, rc - 1);
            tblRet.setColumnSelectionInterval(0, 0);
            tblRet.requestFocus();
        } else {
            txtCus.requestFocus();
        }
    }

    public void addTrader(Trader t) {
        traderAutoCompleter.addTrader(t);
    }

    public void setTrader(Trader t, int row) {
        traderAutoCompleter.setTrader(t, row);
    }

    private void observeMain() {
        observer.selected("control", this);
        observer.selected("save", true);
        observer.selected("print", true);
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

        panelSale = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtCus = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtVouDate = new com.toedter.calendar.JDateChooser();
        txtCurrency = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        txtLocation = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        txtProjectNo = new javax.swing.JTextField();
        txtVouNo = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        lblStatus = new javax.swing.JLabel();
        lblRec = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        txtVouTotal = new javax.swing.JFormattedTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        txtVouDiscP = new javax.swing.JFormattedTextField();
        txtVouDiscount = new javax.swing.JFormattedTextField();
        txtVouTaxP = new javax.swing.JFormattedTextField();
        txtTax = new javax.swing.JFormattedTextField();
        txtVouPaid = new javax.swing.JFormattedTextField();
        txtVouBalance = new javax.swing.JFormattedTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel20 = new javax.swing.JLabel();
        txtGrandTotal = new javax.swing.JFormattedTextField();
        jSeparator2 = new javax.swing.JSeparator();
        chkPaid = new javax.swing.JCheckBox();
        scroll = new javax.swing.JScrollPane();
        tblRet = new javax.swing.JTable();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        panelSale.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel17.setFont(Global.lableFont);
        jLabel17.setText("Vou No");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Customer");

        txtCus.setFont(Global.textFont);
        txtCus.setName("txtCus"); // NOI18N
        txtCus.setNextFocusableComponent(txtLocation);
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

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Vou Date");

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Currency");

        txtVouDate.setDateFormatString("dd/MM/yyyy");
        txtVouDate.setFont(Global.textFont);

        txtCurrency.setFont(Global.textFont);
        txtCurrency.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCurrency.setEnabled(false);
        txtCurrency.setName("txtCurrency"); // NOI18N
        txtCurrency.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCurrencyActionPerformed(evt);
            }
        });

        jLabel21.setFont(Global.lableFont);
        jLabel21.setText("Remark");

        txtRemark.setFont(Global.textFont);
        txtRemark.setName("txtRemark"); // NOI18N
        txtRemark.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtRemarkFocusGained(evt);
            }
        });

        jLabel22.setFont(Global.lableFont);
        jLabel22.setText("Location");

        txtLocation.setFont(Global.textFont);
        txtLocation.setName("txtLocation"); // NOI18N
        txtLocation.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtLocationFocusGained(evt);
            }
        });
        txtLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtLocationActionPerformed(evt);
            }
        });

        jLabel23.setFont(Global.lableFont);
        jLabel23.setText("Project No");

        txtProjectNo.setFont(Global.textFont);
        txtProjectNo.setName("txtRemark"); // NOI18N
        txtProjectNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtProjectNoFocusGained(evt);
            }
        });

        txtVouNo.setEditable(false);
        txtVouNo.setFont(Global.textFont);
        txtVouNo.setName("txtCus"); // NOI18N
        txtVouNo.setNextFocusableComponent(txtLocation);
        txtVouNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtVouNoFocusGained(evt);
            }
        });
        txtVouNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtVouNoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelSaleLayout = new javax.swing.GroupLayout(panelSale);
        panelSale.setLayout(panelSaleLayout);
        panelSaleLayout.setHorizontalGroup(
            panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSaleLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelSaleLayout.createSequentialGroup()
                        .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addGap(18, 18, 18)
                        .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtVouDate, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
                            .addComponent(txtVouNo)))
                    .addGroup(panelSaleLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(txtCus)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCurrency, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                    .addComponent(txtLocation)
                    .addComponent(txtRemark))
                .addGap(9, 9, 9)
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtProjectNo, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                .addContainerGap())
        );

        panelSaleLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel17, jLabel2, jLabel4});

        panelSaleLayout.setVerticalGroup(
            panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSaleLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(jLabel22)
                    .addComponent(txtLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23)
                    .addComponent(txtProjectNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtVouDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(txtCus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel21)
                        .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        lblStatus.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        lblStatus.setText("NEW");

        lblRec.setFont(Global.lableFont);
        lblRec.setText("Records");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblRec, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblRec)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel13.setFont(Global.lableFont);
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel13.setText("Gross Total :");

        jLabel14.setFont(Global.lableFont);
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel14.setText("Discount :");

        jLabel16.setFont(Global.lableFont);
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel16.setText("Tax( + ) :");

        jLabel19.setFont(Global.lableFont);
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel19.setText("Paid :");

        txtVouTotal.setEditable(false);
        txtVouTotal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtVouTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVouTotal.setFont(Global.amtFont);

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("%");

        jLabel8.setFont(Global.lableFont);
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel8.setText("Vou Balance :");

        jLabel15.setFont(Global.lableFont);
        jLabel15.setText("%");

        txtVouDiscP.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtVouDiscP.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVouDiscP.setFont(Global.amtFont);
        txtVouDiscP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtVouDiscPActionPerformed(evt);
            }
        });

        txtVouDiscount.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtVouDiscount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVouDiscount.setFont(Global.amtFont);
        txtVouDiscount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtVouDiscountActionPerformed(evt);
            }
        });

        txtVouTaxP.setEditable(false);
        txtVouTaxP.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtVouTaxP.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVouTaxP.setFont(Global.amtFont);
        txtVouTaxP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtVouTaxPActionPerformed(evt);
            }
        });

        txtTax.setEditable(false);
        txtTax.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtTax.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTax.setFont(Global.amtFont);
        txtTax.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTaxActionPerformed(evt);
            }
        });

        txtVouPaid.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtVouPaid.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVouPaid.setFont(Global.amtFont);
        txtVouPaid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtVouPaidActionPerformed(evt);
            }
        });

        txtVouBalance.setEditable(false);
        txtVouBalance.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtVouBalance.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVouBalance.setFont(Global.amtFont);
        txtVouBalance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtVouBalanceActionPerformed(evt);
            }
        });

        jLabel20.setFont(Global.lableFont);
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel20.setText("Grand Total :");

        txtGrandTotal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtGrandTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtGrandTotal.setFont(Global.amtFont);
        txtGrandTotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtGrandTotalActionPerformed(evt);
            }
        });

        chkPaid.setSelected(true);
        chkPaid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkPaidActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addComponent(jSeparator2)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(chkPaid)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtVouTotal)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtVouDiscP)
                                    .addComponent(txtVouTaxP))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel15)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtVouDiscount)
                                    .addComponent(txtTax)))
                            .addComponent(txtGrandTotal)
                            .addComponent(txtVouPaid, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtVouBalance))))
                .addContainerGap())
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel13, jLabel14, jLabel16, jLabel20, jLabel8});

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(txtVouTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(jLabel7)
                    .addComponent(txtVouDiscP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtVouDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(jLabel15)
                    .addComponent(txtVouTaxP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtGrandTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtVouPaid)
                    .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkPaid, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtVouBalance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tblRet.setFont(Global.textFont);
        tblRet.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblRet.setRowHeight(Global.tblRowHeight);
        tblRet.setShowHorizontalLines(true);
        tblRet.setShowVerticalLines(true);
        tblRet.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblRetKeyReleased(evt);
            }
        });
        scroll.setViewportView(tblRet);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scroll, javax.swing.GroupLayout.DEFAULT_SIZE, 499, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(0, 0, 0)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(panelSale, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelSale, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scroll, javax.swing.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        observeMain();
    }//GEN-LAST:event_formComponentShown

    private void txtCusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCusActionPerformed
        //getCustomer();
    }//GEN-LAST:event_txtCusActionPerformed

    private void txtCusFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCusFocusGained
        txtCus.selectAll();
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCusFocusGained

    private void txtRemarkFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRemarkFocusGained

        // TODO add your handling code here:
    }//GEN-LAST:event_txtRemarkFocusGained

    private void txtLocationFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtLocationFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtLocationFocusGained

    private void tblRetInMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblRetInMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tblRetInMouseClicked

    private void tblRetInKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblRetInKeyReleased
        // TODO add your handling code here:

    }//GEN-LAST:event_tblRetInKeyReleased

    private void txtCurrencyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCurrencyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCurrencyActionPerformed

    private void txtVouDiscPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtVouDiscPActionPerformed
        // TODO add your handling code here:
        calculateTotalAmount();
    }//GEN-LAST:event_txtVouDiscPActionPerformed

    private void txtVouTaxPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtVouTaxPActionPerformed
        // TODO add your handling code here:
        calculateTotalAmount();
    }//GEN-LAST:event_txtVouTaxPActionPerformed

    private void txtVouDiscountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtVouDiscountActionPerformed
        // TODO add your handling code here:
        calculateTotalAmount();
    }//GEN-LAST:event_txtVouDiscountActionPerformed

    private void txtTaxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTaxActionPerformed
        // TODO add your handling code here:
        calculateTotalAmount();
    }//GEN-LAST:event_txtTaxActionPerformed

    private void txtVouPaidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtVouPaidActionPerformed
        // TODO add your handling code here:
        calculateTotalAmount();
    }//GEN-LAST:event_txtVouPaidActionPerformed

    private void txtGrandTotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtGrandTotalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtGrandTotalActionPerformed

    private void txtVouBalanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtVouBalanceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVouBalanceActionPerformed

    private void txtLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtLocationActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtLocationActionPerformed

    private void tblRetKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblRetKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_tblRetKeyReleased

    private void chkPaidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkPaidActionPerformed
        // TODO add your handling code here:
        if (chkPaid.isSelected()) {
            txtVouPaid.setValue(txtGrandTotal.getValue());
        } else {
            txtVouPaid.setValue(0);
        }
        calculateTotalAmount();
    }//GEN-LAST:event_chkPaidActionPerformed

    private void txtProjectNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtProjectNoFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtProjectNoFocusGained

    private void txtVouNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVouNoFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVouNoFocusGained

    private void txtVouNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtVouNoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVouNoActionPerformed
    private void tabToTable(KeyEvent e) {
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
            tblRet.requestFocus();
            if (tblRet.getRowCount() >= 0) {
                tblRet.setRowSelectionInterval(0, 0);
            }
        }
    }

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
            case "SALE-TOTAL" ->
                calculateTotalAmount();
            case "Location" ->
                setAllLocation();
            case "ORDER" -> {
                Order od = (Order) selectObj;
            }
            case "RI-HISTORY" -> {
                if (selectObj instanceof VReturnIn v) {
                    boolean local = v.isLocal();
                    inventoryRepo.findReturnIn(v.getVouNo(), v.getDeptId(), local).subscribe((t) -> {
                        setVoucher(t, local);
                    });
                }
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
        }
        switch (ctrlName) {
            case "txtVouNo" -> {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtRemark.requestFocus();
                }
                tabToTable(e);
            }
            case "txtCus" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtLocation.requestFocus();
                }
                tabToTable(e);
            }
            case "txtLocation" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtRemark.requestFocus();
                }
                tabToTable(e);
            }
            case "txtRetInman" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    //  txtVouDate.getDateEditor().getUiComponent().requestFocusInWindow();
                    tblRet.requestFocus();
                }
                tabToTable(e);
            }
            case "txtVouStatus" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtCus.requestFocus();
                }
                tabToTable(e);
            }
            case "txtRemark" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    tblRet.setRowSelectionInterval(0, 0);
                    tblRet.setColumnSelectionInterval(0, 0);
                    tblRet.requestFocus();
                }
                tabToTable(e);
            }
            case "txtVouDate" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String date = ((JTextFieldDateEditor) sourceObj).getText();
                    txtVouDate.setDate(Util1.formatDate(date));
                    txtCus.requestFocus();
                }
                tabToTable(e);
            }
            case "txtCurrency" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtRemark.requestFocus();
                }

                tabToTable(e);
            }
            case "txtDiscP" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    calculateTotalAmount();
                    txtVouTaxP.requestFocus();
                }
            }
            case "txtTaxP" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    calculateTotalAmount();
                    txtVouBalance.requestFocus();
                }
            }
            case "txtVouDiscount" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtVouTaxP.requestFocus();
                }
            }
            case "txtVouPaid" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    calculateTotalAmount();
                    txtVouBalance.requestFocus();
                }
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkPaid;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblRec;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPanel panelSale;
    private javax.swing.JScrollPane scroll;
    private javax.swing.JTable tblRet;
    private javax.swing.JTextField txtCurrency;
    private javax.swing.JTextField txtCus;
    private javax.swing.JFormattedTextField txtGrandTotal;
    private javax.swing.JTextField txtLocation;
    private javax.swing.JTextField txtProjectNo;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JFormattedTextField txtTax;
    private javax.swing.JFormattedTextField txtVouBalance;
    private com.toedter.calendar.JDateChooser txtVouDate;
    private javax.swing.JFormattedTextField txtVouDiscP;
    private javax.swing.JFormattedTextField txtVouDiscount;
    private javax.swing.JTextField txtVouNo;
    private javax.swing.JFormattedTextField txtVouPaid;
    private javax.swing.JFormattedTextField txtVouTaxP;
    private javax.swing.JFormattedTextField txtVouTotal;
    // End of variables declaration//GEN-END:variables

    @Override
    public void delete() {
        deleteRetIn();
    }

    @Override
    public void print() {
        saveRetIn(true);
    }

    @Override
    public void save() {
        saveRetIn(false);
    }

    @Override
    public void newForm() {
        clear(true);
    }

    @Override
    public void history() {
        historyRetIn();
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
}
