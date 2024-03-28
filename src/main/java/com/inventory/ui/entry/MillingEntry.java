/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry;

import com.acc.dialog.FindDialog;
import com.common.ColumnColorCellRenderer;
import com.common.ComponentUtil;
import com.common.CustomTableCellRenderer;
import com.common.DateLockUtil;
import com.repo.AccountRepo;
import com.common.DecimalFormatRender;
import com.common.Global;
import com.common.KeyPropagate;
import com.common.PanelControl;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.common.TableRowTransferHandler;
import com.common.Util1;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.editor.ExpenseEditor;
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.editor.StockCellEditor;
import com.inventory.editor.TraderAutoCompleter;
import com.inventory.entity.MillingExpense;
import com.inventory.entity.MillingHis;
import com.inventory.entity.MillingHisKey;
import com.inventory.entity.MillingOutDetail;
import com.inventory.entity.MillingRawDetail;
import com.inventory.entity.Trader;
import com.inventory.entity.VouStatus;
import com.inventory.ui.common.MillingExpenseTableModel;
import com.repo.InventoryRepo;
import com.inventory.ui.common.MillingOutTableModel;
import com.inventory.ui.entry.dialog.MillingHistoryDialog;
import com.inventory.ui.setup.dialog.ExpenseSetupDialog;
import com.inventory.ui.setup.dialog.VouStatusSetupDialog;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.inventory.editor.VouStatusAutoCompleter;
import com.inventory.entity.Job;
import com.inventory.entity.MessageType;
import com.inventory.entity.MillingUsage;
import com.inventory.ui.common.MillingRawTableModel;
import com.inventory.ui.entry.dialog.JobSearchDialog;
import com.inventory.ui.entry.dialog.MillingUsageDialog;
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
import javax.swing.DropMode;
import javax.swing.ImageIcon;
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
import org.springframework.stereotype.Component;

/**
 *
 * @author wai yan
 */
@Component
@Slf4j
public class MillingEntry extends javax.swing.JPanel implements SelectionObserver, KeyListener, KeyPropagate, PanelControl {

    private final Image searchIcon = new ImageIcon(this.getClass().getResource("/images/search.png")).getImage();
    private final Image icon = new ImageIcon(getClass().getResource("/images/setting.png")).getImage();
    private List<MillingRawDetail> listDetail = new ArrayList();
    private List<MillingOutDetail> listOutDetail = new ArrayList();
    private List<MillingExpense> listExpense = new ArrayList<>();
    private final MillingOutTableModel millingOutTableModel = new MillingOutTableModel();
    private final MillingRawTableModel millingRawTableModel = new MillingRawTableModel();
    private final MillingExpenseTableModel millingExpenseTableModel = new MillingExpenseTableModel();
    private MillingHistoryDialog dialog;
    private InventoryRepo inventoryRepo;
    private AccountRepo accountRepo;
    private UserRepo userRepo;
    private CurrencyAutoCompleter currAutoCompleter;
    private TraderAutoCompleter traderAutoCompleter;
    private ProjectAutoCompleter projectAutoCompleter;
    private LocationAutoCompleter locationAutoCompleter;
    private VouStatusAutoCompleter vouStatusAutoCompleter;
    private SelectionObserver observer;
    private MillingHis milling = new MillingHis();
    private JProgressBar progress;
    private MillingUsageDialog usageDialog;
    private JobSearchDialog jobSearchDialog;
    private FindDialog findDialog;

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public void setAccountRepo(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public TraderAutoCompleter getTraderAutoCompleter() {
        return traderAutoCompleter;
    }

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    /**
     * Creates new form SaleEntry1
     */
    public MillingEntry() {
        initComponents();
        lblStatus.setForeground(Color.GREEN);
        initKeyListener();
        initDateListner();
        initTextFormat();
        actionMapping();
    }

    private void initTextFormat() {
        ComponentUtil.setTextProperty(this);
        txtEffWt.setForeground(Color.green);
        txtEffQty.setForeground(Color.red);
    }

    private void actionMapping() {
        String solve = "delete";
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        tblRaw.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, solve);
        tblRaw.getActionMap().put(solve, new DeleteAction("tblRaw"));
        tblOutput.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, solve);
        tblOutput.getActionMap().put(solve, new DeleteAction("tblOutput"));
        tblExpense.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, solve);
        tblExpense.getActionMap().put(solve, new DeleteAction("tblExpense"));
    }

    private class DeleteAction extends AbstractAction {

        private String table;

        public DeleteAction(String table) {
            this.table = table;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (table != null) {
                switch (table) {
                    case "tblRaw" ->
                        deleteRaw();
                    case "tblOutput" ->
                        deleteOutput();
                    case "tblExpense" ->
                        deleteExpense();
                    default -> {
                    }
                }
            }
        }
    }

    private void initDateListner() {
        txtSaleDate.getDateEditor().getUiComponent().setName("txtSaleDate");
        txtSaleDate.getDateEditor().getUiComponent().addKeyListener(this);
        ComponentUtil.addFocusListener(this);
    }

    public void initMain() {
        initCombo();
        initData();
        initRawTable();
        initOutputTable();
        initExpenseTable();
        assignDefaultValue();
        usageDialog();
        initFind();
        txtSaleDate.setDate(Util1.getTodayDate());
        txtCus.requestFocus();
    }

    private void initFind() {
        findDialog = new FindDialog(Global.parentForm, tblRaw, tblOutput, tblExpense);
    }

    private void initRawTable() {
        tblRaw.setModel(millingRawTableModel);
        millingRawTableModel.setParent(tblRaw);
        millingRawTableModel.addNewRow();
        millingRawTableModel.setObserver(this);
        tblRaw.getTableHeader().setFont(Global.tblHeaderFont);
        tblRaw.setCellSelectionEnabled(true);
        tblRaw.getColumnModel().getColumn(0).setPreferredWidth(50);//Code
        tblRaw.getColumnModel().getColumn(1).setPreferredWidth(200);//Name
        tblRaw.getColumnModel().getColumn(2).setPreferredWidth(30);//weight
        tblRaw.getColumnModel().getColumn(3).setPreferredWidth(30);//qty
        tblRaw.getColumnModel().getColumn(4).setPreferredWidth(50);//price
        tblRaw.getColumnModel().getColumn(5).setPreferredWidth(60);//amt
        tblRaw.getColumnModel().getColumn(6).setPreferredWidth(50);//total
        tblRaw.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor(inventoryRepo));
        tblRaw.getColumnModel().getColumn(1).setCellEditor(new StockCellEditor(inventoryRepo));
        tblRaw.getColumnModel().getColumn(2).setCellEditor(new AutoClearEditor());//weight
        tblRaw.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());//qty
        tblRaw.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());//price
        tblRaw.getColumnModel().getColumn(5).setCellEditor(new AutoClearEditor());//amt
        tblRaw.getColumnModel().getColumn(6).setCellEditor(new AutoClearEditor());//amt
        tblRaw.setDefaultRenderer(Object.class, new DecimalFormatRender());
        tblRaw.setDefaultRenderer(Double.class, new DecimalFormatRender());
        tblRaw.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblRaw.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    }

    private void initExpenseTable() {
        tblExpense.setModel(millingExpenseTableModel);
        millingExpenseTableModel.setTable(tblExpense);
        millingExpenseTableModel.setObserver(this);
        millingExpenseTableModel.addNewRow();
        tblExpense.getTableHeader().setFont(Global.tblHeaderFont);
        tblExpense.setCellSelectionEnabled(true);
        tblExpense.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblExpense.getColumnModel().getColumn(1).setPreferredWidth(250);
        tblExpense.getColumnModel().getColumn(2).setPreferredWidth(50);
        tblExpense.getColumnModel().getColumn(3).setPreferredWidth(60);
        tblExpense.getColumnModel().getColumn(4).setPreferredWidth(100);
        inventoryRepo.getExpense().subscribe((t) -> {
            tblExpense.getColumnModel().getColumn(0).setCellEditor(new ExpenseEditor(t));
        });
        tblExpense.getColumnModel().getColumn(2).setCellEditor(new AutoClearEditor());//qty
        tblExpense.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());//price
        tblExpense.setDefaultRenderer(Object.class, new DecimalFormatRender());
        tblExpense.setDefaultRenderer(Double.class, new DecimalFormatRender());
        tblExpense.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblExpense.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void initOutputTable() {
        // tbl Output
        tblOutput.setModel(millingOutTableModel);
        millingOutTableModel.setParent(tblOutput);
        millingOutTableModel.addNewRow();
        millingOutTableModel.setObserver(this);
        millingOutTableModel.setVouDate(txtSaleDate);
        millingOutTableModel.setInventoryRepo(inventoryRepo);
        tblOutput.getTableHeader().setFont(Global.tblHeaderFont);
        tblOutput.setCellSelectionEnabled(true);
        tblOutput.getColumnModel().getColumn(0).setPreferredWidth(50);//Code
        tblOutput.getColumnModel().getColumn(1).setPreferredWidth(150);//Name
        tblOutput.getColumnModel().getColumn(2).setPreferredWidth(20);//Weight
        tblOutput.getColumnModel().getColumn(3).setPreferredWidth(20);//qty
        tblOutput.getColumnModel().getColumn(4).setPreferredWidth(50);//price
        tblOutput.getColumnModel().getColumn(5).setPreferredWidth(60);//amt
        tblOutput.getColumnModel().getColumn(6).setPreferredWidth(60);//total
        tblOutput.getColumnModel().getColumn(7).setPreferredWidth(10);//wt %
        tblOutput.getColumnModel().getColumn(8).setPreferredWidth(10);//qty %
        tblOutput.getColumnModel().getColumn(9).setPreferredWidth(5);//qty %
        tblOutput.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor(inventoryRepo));
        tblOutput.getColumnModel().getColumn(1).setCellEditor(new StockCellEditor(inventoryRepo));
        tblOutput.getColumnModel().getColumn(2).setCellEditor(new AutoClearEditor());//weight
        tblOutput.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());//qty
        tblOutput.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());//price
        tblOutput.getColumnModel().getColumn(5).setCellRenderer(new CustomTableCellRenderer(0, 6, Color.orange, true));
        tblOutput.getColumnModel().getColumn(6).setCellEditor(new AutoClearEditor());//total
        tblOutput.getColumnModel().getColumn(7).setCellRenderer(new ColumnColorCellRenderer(Color.green));//total
        tblOutput.getColumnModel().getColumn(8).setCellRenderer(new ColumnColorCellRenderer(Color.red));//total
        tblOutput.setDefaultRenderer(Object.class, new DecimalFormatRender());
        tblOutput.setDefaultRenderer(Double.class, new DecimalFormatRender());
        tblOutput.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblOutput.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblOutput.setDragEnabled(true);
        tblOutput.setDropMode(DropMode.INSERT);
        tblOutput.setTransferHandler(new TableRowTransferHandler());

    }

    private void initCombo() {
        traderAutoCompleter = new TraderAutoCompleter(txtCus, inventoryRepo, null, false, "CUS");
        traderAutoCompleter.setObserver(this);
        currAutoCompleter = new CurrencyAutoCompleter(txtCurrency, null);
        projectAutoCompleter = new ProjectAutoCompleter(txtProjectNo, userRepo, null, false);
        projectAutoCompleter.setObserver(this);
        locationAutoCompleter = new LocationAutoCompleter(txtLocation, null, false, false);
        locationAutoCompleter.setObserver(this);
        vouStatusAutoCompleter = new VouStatusAutoCompleter(txtVouStatus, null, false);
        vouStatusAutoCompleter.setObserver(this);
    }

    private void initData() {
        userRepo.getCurrency().doOnSuccess((t) -> {
            currAutoCompleter.setListCurrency(t);
        }).subscribe();
        userRepo.getDefaultCurrency().doOnSuccess((c) -> {
            currAutoCompleter.setCurrency(c);
        }).subscribe();
        inventoryRepo.getLocation().doOnSuccess((t) -> {
            locationAutoCompleter.setListLocation(t);
        }).subscribe();
        inventoryRepo.getVoucherStatus().doOnSuccess((t) -> {
            vouStatusAutoCompleter.setListData(t);
        }).subscribe();
    }

    private void initKeyListener() {
        txtSaleDate.getDateEditor().getUiComponent().setName("txtSaleDate");
        txtSaleDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtVouNo.addKeyListener(this);
        txtRemark.addKeyListener(this);
        txtCus.addKeyListener(this);
        txtCurrency.addKeyListener(this);
        tblRaw.addKeyListener(this);
        txtProjectNo.addKeyListener(this);
    }

    private void assignDefaultValue() {
        userRepo.getDefaultCurrency().doOnSuccess((t) -> {
            currAutoCompleter.setCurrency(t);
        }).subscribe();
        inventoryRepo.getDefaultCustomer().doOnSuccess((t) -> {
            traderAutoCompleter.setTrader(t);
        }).subscribe();
        inventoryRepo.getDefaultLocation().doOnSuccess((t) -> {
            locationAutoCompleter.setLocation(t);
        }).subscribe();
        vouStatusAutoCompleter.setVoucher(null);
        progress.setIndeterminate(false);
        txtCurrency.setEnabled(ProUtil.isMultiCur());
        txtVouNo.setText(null);
        lblLoadRecord.setText("0");
        lblExpRecord.setText("0");
        lblOutRecord.setText("0");
    }

    private void clear() {
        disableForm(true);
        assignDefaultValue();
        txtSaleDate.setDate(Util1.getTodayDate());
        millingOutTableModel.clear();
        millingOutTableModel.addNewRow();
        millingOutTableModel.clearDelList();
        millingRawTableModel.clear();
        millingRawTableModel.addNewRow();
        millingRawTableModel.clearDelList();
        millingExpenseTableModel.clear();
        millingExpenseTableModel.addNewRow();
        millingExpenseTableModel.setChange(false);
        milling = new MillingHis();
        lblStatus.setText("NEW");
        lblStatus.setForeground(Color.GREEN);
        progress.setIndeterminate(false);
        txtRemark.setText(null);
        txtReference.setText(null);
        projectAutoCompleter.setProject(null);
        txtLoadQty.setValue(null);
        txtLoadWeight.setValue(null);
        txtLoadAmt.setValue(null);
        txtOutputQty.setValue(null);
        txtOutputWeight.setValue(null);
        txtOutputAmt.setValue(null);
        txtWtLoss.setValue(null);
        txtLoadExpense.setValue(null);
        txtLoadCost.setValue(null);
        txtQtyLoss.setValue(null);
        txtEffQty.setValue(null);
        txtEffWt.setValue(null);
        txtJob.setText(null);
    }

    public void saveSale(boolean print) {
        if (isValidEntry() && millingRawTableModel.isValidEntry()
                && millingOutTableModel.isValidEntry() && millingExpenseTableModel.isValidEntry()) {
            if (DateLockUtil.isLockDate(txtSaleDate.getDate())) {
                DateLockUtil.showMessage(this);
                txtSaleDate.requestFocus();
                return;
            }
            milling.setListUsage(usageDialog.getListUsage());
            milling.setListRaw(millingRawTableModel.getListDetail());
            milling.setListRawDel(millingRawTableModel.getDelList());
            milling.setListOutput(millingOutTableModel.getListDetail());
            milling.setListOutputDel(millingOutTableModel.getDelList());
            milling.setListExpense(millingExpenseTableModel.getListDetail());
            milling.setListExpenseDel(millingExpenseTableModel.getDelList());
            observer.selected("save", false);
            progress.setIndeterminate(true);
            inventoryRepo.save(milling).doOnSuccess((t) -> {
                milling.getKey().setVouNo(t.getKey().getVouNo());
                updateJob(t.getJobNo());
            }).doOnError((e) -> {
                observer.selected("save", true);
                JOptionPane.showMessageDialog(this, e.getMessage());
                progress.setIndeterminate(false);
            }).doOnTerminate(() -> {
                if (print) {
                    printVoucher(milling);
                } else {
                    txtCus.requestFocus();
                }
                clear();
            }).subscribe();
        }
    }

    private void updateJob(String jobNo) {
        if (!Util1.isNullOrEmpty(jobNo)) {
            inventoryRepo.findJob(jobNo).doOnSuccess((s) -> {
                if (s != null) {
                    s.setFinished(true);
                    s.setEndDate(Util1.convertToDate(milling.getVouDate()));
                    inventoryRepo.saveJob(s).doOnSuccess((a) -> {
                        progress.setIndeterminate(false);
                    }).doOnError((e) -> {
                        observer.selected("save", true);
                        JOptionPane.showMessageDialog(this, e.getMessage());
                        progress.setIndeterminate(false);
                    }).doOnTerminate(() -> {
                        sendMessage("Job No : " + jobNo + " : Finished.");
                    }).subscribe();
                }
            }).doOnError((e) -> {
                observer.selected("save", true);
                JOptionPane.showMessageDialog(this, e.getMessage());
                progress.setIndeterminate(false);
            }).subscribe();
        }
    }

    private void sendMessage(String mes) {
        inventoryRepo.sendDownloadMessage(MessageType.JOB, mes)
                .doOnSuccess((t) -> {
                    log.info(t);
                }).subscribe();
    }

    private void printVoucher(MillingHis his) {
        VouStatus v = vouStatusAutoCompleter.getVouStatus();
        String reportName = Util1.isNull(v.getMillReportName(), "MillingReport");
        if (reportName != null) {
            try {
                List<MillingRawDetail> listRaw = his.getListRaw();
                listRaw.removeIf((t) -> t.getStockName() == null);
                List<MillingOutDetail> listOutput = his.getListOutput();
                listOutput.removeIf((t) -> t.getStockName() == null);
                Map<String, Object> param = new HashMap<>();
                param.put("p_print_date", Util1.getTodayDateTime());
                param.put("p_comp_name", Global.companyName);
                param.put("p_comp_address", Global.companyAddress);
                param.put("p_comp_phone", Global.companyPhone);
                param.put("p_logo_path", ProUtil.logoPath());
                param.put("p_vou_no", his.getKey().getVouNo());
                param.put("p_vou_date", Util1.toDateStr(his.getVouDate(), Global.dateFormat));
                param.put("p_job_no", his.getJobNo());
                param.put("p_vou_type", v.getDescription());
                param.put("p_sub_report_dir", "report/");
                double ttlQty = listRaw.stream().mapToDouble((t) -> t.getQty()).sum();
                param.put("p_raw_qty", ttlQty);
                String reportPath = String.format("report%s%s", File.separator, reportName.concat(".jasper"));
                ObjectMapper mapper = new ObjectMapper();
                JsonNode n1 = mapper.readTree(Util1.gson.toJson(listOutput));
                JsonDataSource d1 = new JsonDataSource(n1, null) {
                };
                JsonNode n2 = mapper.readTree(Util1.gson.toJson(listRaw));
                JsonDataSource d2 = new JsonDataSource(n2, null) {
                };
                param.put("p_sub_data", d2);
                JasperPrint main = JasperFillManager.fillReport(reportPath, param, d1);
                JasperViewer.viewReport(main, false);
            } catch (JsonProcessingException | JRException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    private boolean isValidEntry() {
        boolean status = true;
        if (lblStatus.getText().equals("DELETED")) {
            status = false;
            clear();
        } else if (currAutoCompleter.getCurrency() == null) {
            JOptionPane.showMessageDialog(this, "Choose Currency.",
                    "Validation", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtCurrency.requestFocus();
        } else if (traderAutoCompleter.getTrader() == null) {
            JOptionPane.showMessageDialog(this, "Choose Trader.",
                    "Validation.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtCus.requestFocus();
        } else if (vouStatusAutoCompleter.getVouStatus() == null) {
            JOptionPane.showMessageDialog(this, "You must choose process type.",
                    "Validation.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtOutputAmt.requestFocus();
        } else if (locationAutoCompleter.getLocation() == null) {
            JOptionPane.showMessageDialog(this, "You must choose location.",
                    "Validation.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtOutputAmt.requestFocus();
        } else if (!Util1.isDateBetween(txtSaleDate.getDate())) {
            JOptionPane.showMessageDialog(this, "Invalid Date.",
                    "Validation.", JOptionPane.ERROR_MESSAGE);
            txtSaleDate.requestFocus();
            status = false;
        } else if (!Util1.isDateBetween(txtSaleDate.getDate())) {
            JOptionPane.showMessageDialog(this, "Invalid Date.",
                    "Validation.", JOptionPane.ERROR_MESSAGE);
            txtSaleDate.requestFocus();
            status = false;
        } else {
            milling.setRemark(txtRemark.getText());
            milling.setReference(txtReference.getText());
            milling.setCurCode(currAutoCompleter.getCurrency().getCurCode());
            milling.setTraderCode(traderAutoCompleter.getTrader().getKey().getCode());
            milling.setStatus(lblStatus.getText());
            milling.setVouDate(Util1.convertToLocalDateTime(txtSaleDate.getDate()));
            milling.setMacId(Global.macId);
            Project p = projectAutoCompleter.getProject();
            milling.setProjectNo(p == null ? null : p.getKey().getProjectNo());
            VouStatus v = vouStatusAutoCompleter.getVouStatus();
            milling.setVouStatusId(v.getKey() == null ? null : v.getKey().getCode());
            milling.setLoadQty(Util1.getDouble(txtLoadQty.getValue()));
            milling.setLoadWeight(Util1.getDouble(txtLoadWeight.getValue()));
            milling.setLoadAmount(Util1.getDouble(txtLoadAmt.getValue()));
            milling.setLoadExpense(Util1.getDouble(txtLoadExpense.getValue()));
            milling.setLoadCost(Util1.getDouble(txtLoadCost.getValue()));
            milling.setOutputQty(Util1.getDouble(txtOutputQty.getValue()));
            milling.setOutputWeight(Util1.getDouble(txtOutputWeight.getValue()));
            milling.setOutputAmount(Util1.getDouble(txtOutputAmt.getValue()));
            milling.setDiffWeight(Util1.getDouble(txtWtLoss.getValue()));
            milling.setDiffQty(Util1.getDouble(txtQtyLoss.getValue()));
            milling.setPercentQty(Util1.getDouble(txtEffQty.getValue()));
            milling.setPercentWeight(Util1.getDouble(txtEffWt.getValue()));
            milling.setDeptId(Global.deptId);
            milling.setLocCode(locationAutoCompleter.getLocation().getKey().getLocCode());
            if (lblStatus.getText().equals("NEW")) {
                MillingHisKey key = new MillingHisKey();
                key.setCompCode(Global.compCode);
                key.setVouNo(null);
                milling.setKey(key);
                milling.setCreatedDate(LocalDateTime.now());
                milling.setCreatedBy(Global.loginUser.getUserCode());
            } else {
                milling.setUpdatedBy(Global.loginUser.getUserCode());
            }
        }
        return status;
    }

    private void deleteSale() {
        String status = lblStatus.getText();
        switch (status) {
            case "EDIT" -> {
                int yes_no = JOptionPane.showConfirmDialog(this,
                        "Are you sure to delete?", "Milling Voucher Delete.", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                if (yes_no == 0) {
                    inventoryRepo.deleteMilling(milling).subscribe((t) -> {
                        clear();
                    });
                }
            }
            case "DELETED" -> {
                int yes_no = JOptionPane.showConfirmDialog(this,
                        "Are you sure to restore?", "Milling Voucher Restore.", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (yes_no == 0) {
                    milling.setDeleted(false);
                    inventoryRepo.restoreMilling(milling).subscribe((t) -> {
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

    private void deleteRaw() {
        int row = tblRaw.convertRowIndexToModel(tblRaw.getSelectedRow());
        if (row >= 0) {
            if (tblRaw.getCellEditor() != null) {
                tblRaw.getCellEditor().stopCellEditing();
            }
            int yes_no = JOptionPane.showConfirmDialog(this,
                    "Are you sure to delete?", "Sale Transaction delete.", JOptionPane.YES_NO_OPTION);
            if (yes_no == 0) {
                millingRawTableModel.delete(row);
                calculateMilling();
            }
        }
    }

    private void deleteOutput() {
        int row = tblOutput.convertRowIndexToModel(tblOutput.getSelectedRow());
        if (row >= 0) {
            if (tblOutput.getCellEditor() != null) {
                tblOutput.getCellEditor().stopCellEditing();
            }
            int yes_no = JOptionPane.showConfirmDialog(this,
                    "Are you sure to delete?", "Sale Transaction delete.", JOptionPane.YES_NO_OPTION);
            if (yes_no == 0) {
                millingOutTableModel.delete(row);
                calculateMilling();
            }
        }
    }

    private void deleteExpense() {
        int row = tblExpense.convertRowIndexToModel(tblExpense.getSelectedRow());
        if (row >= 0) {
            if (tblExpense.getCellEditor() != null) {
                tblExpense.getCellEditor().stopCellEditing();
            }
            int yes_no = JOptionPane.showConfirmDialog(this,
                    "Are you sure to delete?", "Sale Transaction delete.", JOptionPane.YES_NO_OPTION);
            if (yes_no == 0) {
                millingExpenseTableModel.delete(row);
                calculateMilling();
            }
        }
    }

    private void calculateMilling() {
        //cal raw
        listDetail = millingRawTableModel.getListDetail();
        double loadQty = listDetail.stream().mapToDouble((t) -> t.getQty()).sum();
        double loadWt = listDetail.stream().mapToDouble((t) -> t.getTotalWeight()).sum();
        double loadAmt = listDetail.stream().mapToDouble((t) -> t.getAmount()).sum();
        txtLoadQty.setValue(loadQty);
        txtLoadWeight.setValue(loadWt);
        txtLoadAmt.setValue(loadAmt);

        //cal expense
        listExpense = millingExpenseTableModel.getListDetail();
        double expAmt = listExpense.stream().mapToDouble((t) -> t.getAmount()).sum();
        txtLoadExpense.setValue(expAmt);
        double costAmt = loadAmt + expAmt;
        txtLoadCost.setValue(costAmt);

        listOutDetail = millingOutTableModel.getListDetail();
        for (int i = 0; i < listOutDetail.size(); i++) {
            MillingOutDetail t = listOutDetail.get(i);
            double wt = t.getTotalWeight();
            double qty = t.getQty();
            if (wt > 0) {
                t.setPercent((wt / loadWt) * 100);
            }
            if (qty > 0) {
                t.setPercentQty((qty / loadQty) * 100);
            }
            millingOutTableModel.setObject(i, t);
        }
        // calculate price
        double knowAmt = listOutDetail.stream()
                .filter((f) -> !f.isCalculate())
                .mapToDouble((t) -> t.getAmount())
                .sum();
        double amt = costAmt - knowAmt;
        List<MillingOutDetail> unknowList = listOutDetail.stream().filter((t) -> t.isCalculate()).toList();
        double ttlQty = unknowList.stream().mapToDouble((t) -> t.getQty()).sum();
        if (ttlQty > 0) {
            double ttlPrice = Util1.format2(amt / ttlQty);
            for (int i = 0; i < unknowList.size(); i++) {
                MillingOutDetail mod = unknowList.get(i);
                mod.setPrice(ttlPrice);
                mod.setAmount(ttlPrice * mod.getQty());
                millingOutTableModel.rowUpdate(i);
            }
        }
        double outAmt = listOutDetail.stream().mapToDouble((t) -> t.getAmount()).sum();
        double outQty = listOutDetail.stream().mapToDouble((t) -> t.getQty()).sum();
        double outWt = listOutDetail.stream().mapToDouble((t) -> t.getTotalWeight()).sum();
        double effWt = listOutDetail.stream().mapToDouble((t) -> t.getPercent()).sum();
        double effQty = listOutDetail.stream().mapToDouble((t) -> t.getPercentQty()).sum();

        txtOutputQty.setValue(outQty);
        txtOutputWeight.setValue(outWt);
        txtOutputAmt.setValue(outAmt);
        txtWtLoss.setValue(loadWt - outWt);
        txtEffWt.setValue(effWt);
        txtEffQty.setValue(effQty);
        txtQtyLoss.setValue(loadQty - outQty);
        lblLoadRecord.setText(String.valueOf(listDetail.size() - 1));
        lblExpRecord.setText(String.valueOf(listExpense.size() - 1));
        lblOutRecord.setText(String.valueOf(listOutDetail.size() - 1));
    }

    public void historySale() {
        if (dialog == null) {
            dialog = new MillingHistoryDialog(Global.parentForm);
            dialog.setTitle("Milling Voucher Search");
            dialog.setInventoryRepo(inventoryRepo);
            dialog.setUserRepo(userRepo);
            dialog.setIconImage(searchIcon);
            dialog.setObserver(this);
            dialog.initMain();
            dialog.setSize(Global.width - 20, Global.height - 20);
            dialog.setLocationRelativeTo(null);
        }
        dialog.search();
    }

    private void searchRawDetail(String vouNo, Integer deptId) {
        inventoryRepo.getRawDetail(vouNo, deptId).subscribe((l) -> {
            lblLoadRecord.setText(String.valueOf(l.size()));
            millingRawTableModel.setListDetail(l);
        });
    }

    private void searchExpenseDetail(String vouNo) {
        inventoryRepo.getMillingExpense(vouNo).subscribe((l) -> {
            lblExpRecord.setText(String.valueOf(l.size()));
            millingExpenseTableModel.setListDetail(l);
        });
    }

    private void searchOutputDetail(String vouNo, Integer deptId) {
        inventoryRepo.getOutputDetail(vouNo, deptId).subscribe((l) -> {
            lblOutRecord.setText(String.valueOf(l.size()));
            millingOutTableModel.setListDetail(l);
            progress.setIndeterminate(false);
        });
    }

    private void searchUsageDetail(String vouNo) {
        inventoryRepo.getUsageDetail(vouNo).subscribe((t) -> {
            usageDialog.setListUsage(t);
            progress.setIndeterminate(false);
        });
    }

    public void setVoucher(MillingHis sh) {
        if (sh != null) {
            progress.setIndeterminate(true);
            milling = sh;
            inventoryRepo.findTrader(milling.getTraderCode()).doOnSuccess((t) -> {
                traderAutoCompleter.setTrader(t);
            }).subscribe();
            inventoryRepo.findLocation(milling.getLocCode()).doOnSuccess((t) -> {
                locationAutoCompleter.setLocation(t);
            }).subscribe();
            inventoryRepo.findJob(milling.getJobNo()).doOnSuccess((t) -> {
                if (t != null) {
                    txtJob.setText(t.getJobName());
                }
            }).subscribe();
            userRepo.findCurrency(milling.getCurCode()).doOnSuccess((t) -> {
                currAutoCompleter.setCurrency(t);
            }).subscribe();
            sh.setVouLock(!sh.getDeptId().equals(Global.deptId));
            if (sh.isVouLock()) {
                lblStatus.setText("Voucher is locked.");
                lblStatus.setForeground(Color.RED);
                disableForm(false);
            } else if (!ProUtil.isSaleEdit()) {
                lblStatus.setText("No Permission.");
                lblStatus.setForeground(Color.RED);
                disableForm(false);
                observer.selected("print", true);
            } else if (sh.isDeleted()) {
                lblStatus.setText("DELETED");
                lblStatus.setForeground(Color.RED);
                disableForm(false);
                observer.selected("delete", true);
            } else if (DateLockUtil.isLockDate(sh.getVouDate())) {
                lblStatus.setText(DateLockUtil.MESSAGE);
                lblStatus.setForeground(Color.RED);
                disableForm(false);
            } else {
                lblStatus.setText("EDIT");
                lblStatus.setForeground(Color.blue);
                disableForm(true);
            }
            txtVouNo.setText(milling.getKey().getVouNo());
            txtRemark.setText(milling.getRemark());
            txtReference.setText(milling.getReference());
            txtSaleDate.setDate(Util1.convertToDate(milling.getVouDate()));
            txtLoadQty.setValue(milling.getLoadQty());
            txtLoadWeight.setValue(milling.getLoadWeight());
            txtLoadAmt.setValue(milling.getLoadAmount());
            txtLoadExpense.setValue(milling.getLoadExpense());
            txtLoadCost.setValue(milling.getLoadCost());
            txtOutputQty.setValue(milling.getOutputQty());
            txtOutputWeight.setValue(milling.getOutputWeight());
            txtOutputAmt.setValue(milling.getOutputAmount());
            txtWtLoss.setValue(milling.getDiffWeight());
            txtQtyLoss.setValue(milling.getDiffQty());
            txtEffQty.setValue(milling.getPercentQty());
            txtEffWt.setValue(milling.getPercentWeight());
            userRepo.find(new ProjectKey(milling.getProjectNo(), Global.compCode)).doOnSuccess(t1 -> {
                projectAutoCompleter.setProject(t1);
            }).subscribe();
            inventoryRepo.findVouStatus(milling.getVouStatusId()).doOnSuccess(t1 -> {
                vouStatusAutoCompleter.setVoucher(t1);
            }).subscribe();
            //detail
            String vouNo = sh.getKey().getVouNo();
            searchRawDetail(vouNo, sh.getDeptId());
            searchExpenseDetail(vouNo);
            searchOutputDetail(vouNo, sh.getDeptId());
            searchUsageDetail(vouNo);
            focusTable();

        }
    }

    private void disableForm(boolean status) {
        ComponentUtil.enableForm(this, status);
        observer.selected("save", status);
        observer.selected("delete", status);
        observer.selected("print", status);

    }

    private void focusTable() {
        int rc = tblRaw.getRowCount();
        if (rc >= 1) {
            tblRaw.setRowSelectionInterval(rc - 1, rc - 1);
            tblRaw.setColumnSelectionInterval(0, 0);
            tblRaw.requestFocus();
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

    private void usageDialog() {
        if (usageDialog == null) {
            usageDialog = new MillingUsageDialog(Global.parentForm);
            usageDialog.setSize(Global.width - 200, Global.height - 200);
            usageDialog.setInventoryRepo(inventoryRepo);
            usageDialog.setMilingOutTableModel(millingOutTableModel);
            usageDialog.initMain();
            usageDialog.setLocationRelativeTo(null);
        }
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
        txtSaleDate = new com.toedter.calendar.JDateChooser();
        txtCurrency = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        txtReference = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtProjectNo = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        btnTransfer = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        txtLocation = new javax.swing.JTextField();
        addExpense1 = new javax.swing.JButton();
        txtJob = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        txtVouStatus = new javax.swing.JTextField();
        txtVouNo = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblRaw = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblOutput = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtLoadQty = new javax.swing.JFormattedTextField();
        jLabel7 = new javax.swing.JLabel();
        txtLoadWeight = new javax.swing.JFormattedTextField();
        jLabel8 = new javax.swing.JLabel();
        txtLoadAmt = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        lblLoadRecord = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtOutputAmt = new javax.swing.JFormattedTextField();
        txtOutputWeight = new javax.swing.JFormattedTextField();
        txtOutputQty = new javax.swing.JFormattedTextField();
        jLabel16 = new javax.swing.JLabel();
        txtWtLoss = new javax.swing.JFormattedTextField();
        txtEffWt = new javax.swing.JFormattedTextField();
        jLabel20 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        txtEffQty = new javax.swing.JFormattedTextField();
        jLabel23 = new javax.swing.JLabel();
        txtQtyLoss = new javax.swing.JFormattedTextField();
        jButton1 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        lblOutRecord = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblExpense = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        txtLoadExpense = new javax.swing.JFormattedTextField();
        jLabel18 = new javax.swing.JLabel();
        txtLoadCost = new javax.swing.JFormattedTextField();
        jLabel24 = new javax.swing.JLabel();
        lblExpRecord = new javax.swing.JLabel();

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

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Milling Date");

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Currency");

        txtSaleDate.setDateFormatString("dd/MM/yyyy");
        txtSaleDate.setFont(Global.textFont);
        txtSaleDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSaleDateFocusGained(evt);
            }
        });
        txtSaleDate.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtSaleDatePropertyChange(evt);
            }
        });

        txtCurrency.setFont(Global.textFont);
        txtCurrency.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCurrency.setName("txtCurrency"); // NOI18N
        txtCurrency.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCurrencyActionPerformed(evt);
            }
        });

        jLabel21.setFont(Global.lableFont);
        jLabel21.setText("Remark");

        txtRemark.setFont(Global.textFont);
        txtRemark.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtRemark.setName("txtRemark"); // NOI18N
        txtRemark.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtRemarkFocusGained(evt);
            }
        });

        txtReference.setFont(Global.textFont);
        txtReference.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtReference.setName("txtRemark"); // NOI18N
        txtReference.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtReferenceFocusGained(evt);
            }
        });

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Reference");

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Project No");

        txtProjectNo.setFont(Global.textFont);
        txtProjectNo.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtProjectNo.setName("txtCurrency"); // NOI18N
        txtProjectNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtProjectNoActionPerformed(evt);
            }
        });

        jLabel15.setFont(Global.lableFont);
        jLabel15.setText("Process Type");

        jButton2.setBackground(Global.selectionColor);
        jButton2.setFont(Global.lableFont);
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("...");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        btnTransfer.setBackground(Global.selectionColor);
        btnTransfer.setFont(Global.lableFont);
        btnTransfer.setForeground(new java.awt.Color(255, 255, 255));
        btnTransfer.setText("Job");
        btnTransfer.setInheritsPopupMenu(true);
        btnTransfer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTransferActionPerformed(evt);
            }
        });

        lblStatus.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        lblStatus.setText("NEW");

        jLabel19.setFont(Global.lableFont);
        jLabel19.setText("Location");

        txtLocation.setFont(Global.textFont);
        txtLocation.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtLocation.setName("txtCurrency"); // NOI18N
        txtLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtLocationActionPerformed(evt);
            }
        });

        addExpense1.setFont(Global.lableFont);
        addExpense1.setText("Add Expense");
        addExpense1.setInheritsPopupMenu(true);
        addExpense1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addExpense1ActionPerformed(evt);
            }
        });

        txtJob.setEditable(false);
        txtJob.setFont(Global.textFont);
        txtJob.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtJob.setName("txtCurrency"); // NOI18N
        txtJob.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtJobActionPerformed(evt);
            }
        });

        jLabel25.setFont(Global.lableFont);
        jLabel25.setText("Job");

        txtVouStatus.setFont(Global.textFont);

        txtVouNo.setEditable(false);
        txtVouNo.setFont(Global.textFont);
        txtVouNo.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtVouNo.setName("txtCus"); // NOI18N
        txtVouNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtVouNoFocusGained(evt);
            }
        });
        txtVouNo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                txtVouNoMouseExited(evt);
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
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtCus, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 298, Short.MAX_VALUE)
                        .addComponent(addExpense1, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelSaleLayout.createSequentialGroup()
                        .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtSaleDate, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelSaleLayout.createSequentialGroup()
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtReference, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelSaleLayout.createSequentialGroup()
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(panelSaleLayout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addGap(12, 12, 12)
                                .addComponent(txtProjectNo, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelSaleLayout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtVouStatus)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel25)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtJob)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnTransfer, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
                            .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );

        panelSaleLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel17, jLabel2, jLabel21, jLabel4, jLabel6, jLabel9});

        panelSaleLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel10, jLabel15});

        panelSaleLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtCurrency, txtCus, txtLocation, txtProjectNo, txtReference, txtRemark, txtSaleDate});

        panelSaleLayout.setVerticalGroup(
            panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSaleLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel17)
                        .addComponent(jLabel6)
                        .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel10)
                        .addComponent(txtProjectNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtJob, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel25)
                        .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(txtSaleDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jButton2)
                    .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtReference, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9)
                        .addComponent(jLabel15)
                        .addComponent(btnTransfer)
                        .addComponent(txtVouStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel19)
                        .addComponent(txtLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtCus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel21)
                        .addComponent(jLabel2))
                    .addComponent(addExpense1))
                .addContainerGap())
        );

        tblRaw.setFont(Global.textFont);
        tblRaw.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblRaw.setRowHeight(Global.tblRowHeight);
        tblRaw.setShowGrid(true);
        tblRaw.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblRawMouseClicked(evt);
            }
        });
        tblRaw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblRawKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblRaw);

        tblOutput.setFont(Global.textFont);
        tblOutput.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblOutput.setRowHeight(Global.tblRowHeight);
        tblOutput.setShowHorizontalLines(true);
        tblOutput.setShowVerticalLines(true);
        tblOutput.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblOutputMouseClicked(evt);
            }
        });
        tblOutput.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblOutputKeyReleased(evt);
            }
        });
        jScrollPane4.setViewportView(tblOutput);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Load Qty : ");

        txtLoadQty.setEditable(false);
        txtLoadQty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtLoadQtyActionPerformed(evt);
            }
        });

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Load Weight : ");

        txtLoadWeight.setEditable(false);

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Load Amt : ");

        txtLoadAmt.setEditable(false);

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Records :");

        lblLoadRecord.setFont(Global.lableFont);
        lblLoadRecord.setText("-");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtLoadAmt, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                            .addComponent(txtLoadQty, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtLoadWeight)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblLoadRecord, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtLoadQty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtLoadWeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtLoadAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(lblLoadRecord))
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Output Qty : ");

        jLabel12.setFont(Global.lableFont);
        jLabel12.setText("Output Weight :");

        jLabel13.setFont(Global.lableFont);
        jLabel13.setText("Output Amt :");

        txtOutputAmt.setEditable(false);

        txtOutputWeight.setEditable(false);

        txtOutputQty.setEditable(false);

        jLabel16.setFont(Global.lableFont);
        jLabel16.setText("Weight Loss :");

        txtWtLoss.setEditable(false);

        txtEffWt.setEditable(false);
        txtEffWt.setForeground(Color.red);

        jLabel20.setFont(Global.lableFont);
        jLabel20.setText("Eff Weight :");

        jLabel22.setFont(Global.lableFont);
        jLabel22.setText("Eff Qty :");

        txtEffQty.setEditable(false);
        txtEffQty.setForeground(Color.red);

        jLabel23.setFont(Global.lableFont);
        jLabel23.setText("Qty Loss :");

        txtQtyLoss.setEditable(false);

        jButton1.setFont(Global.lableFont);
        jButton1.setText("Usage");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Records :");

        lblOutRecord.setFont(Global.lableFont);
        lblOutRecord.setText("-");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtOutputWeight, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtOutputAmt, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtOutputQty)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(txtWtLoss, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(txtEffWt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtEffQty)
                                    .addComponent(txtQtyLoss))))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblOutRecord, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtOutputQty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtOutputWeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtOutputAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtWtLoss, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(jLabel23)
                    .addComponent(txtQtyLoss, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtEffWt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20)
                    .addComponent(jLabel22)
                    .addComponent(txtEffQty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jLabel5)
                    .addComponent(lblOutRecord))
                .addContainerGap())
        );

        tblExpense.setFont(Global.textFont);
        tblExpense.setRowHeight(Global.tblRowHeight);
        tblExpense.setShowGrid(true);
        jScrollPane5.setViewportView(tblExpense);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel14.setFont(Global.lableFont);
        jLabel14.setText("Load Expense :");

        txtLoadExpense.setEditable(false);

        jLabel18.setFont(Global.lableFont);
        jLabel18.setText("Load Cost :");

        txtLoadCost.setEditable(false);

        jLabel24.setFont(Global.lableFont);
        jLabel24.setText("Records :");

        lblExpRecord.setFont(Global.lableFont);
        lblExpRecord.setText("-");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtLoadExpense, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
                            .addComponent(txtLoadCost)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel24)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblExpRecord, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(txtLoadExpense, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(txtLoadCost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(lblExpRecord))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panelSale, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelSale, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(55, 55, 55))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jPanel2, jScrollPane5});

    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        observeMain();    }//GEN-LAST:event_formComponentShown

    private void txtCusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCusActionPerformed
        //inventoryRepo.getCustomer().subscribe()
    }//GEN-LAST:event_txtCusActionPerformed

    private void txtCusFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCusFocusGained
        txtCus.selectAll();
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCusFocusGained

    private void txtRemarkFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRemarkFocusGained

        // TODO add your handling code here:
    }//GEN-LAST:event_txtRemarkFocusGained

    private void tblRawMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblRawMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tblRawMouseClicked

    private void tblRawKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblRawKeyReleased
        // TODO add your handling code here:

    }//GEN-LAST:event_tblRawKeyReleased

    private void txtCurrencyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCurrencyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCurrencyActionPerformed

    private void txtReferenceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtReferenceFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtReferenceFocusGained

    private void formPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_formPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_formPropertyChange

    private void txtSaleDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSaleDateFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSaleDateFocusGained

    private void txtProjectNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProjectNoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtProjectNoActionPerformed

    private void txtCusMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCusMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCusMouseExited

    private void txtSaleDatePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtSaleDatePropertyChange
    }//GEN-LAST:event_txtSaleDatePropertyChange

    private void tblOutputMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblOutputMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tblOutputMouseClicked

    private void tblOutputKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblOutputKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_tblOutputKeyReleased

    private void txtLoadQtyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtLoadQtyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtLoadQtyActionPerformed

    private void btnTransferActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTransferActionPerformed
        jobDialog();
    }//GEN-LAST:event_btnTransferActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        vouStatusSetup();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void txtLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtLocationActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtLocationActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        usageDialog.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void addExpense1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addExpense1ActionPerformed
        expenseDialog();
    }//GEN-LAST:event_addExpense1ActionPerformed

    private void txtJobActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtJobActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtJobActionPerformed

    private void txtVouNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVouNoFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVouNoFocusGained

    private void txtVouNoMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtVouNoMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVouNoMouseExited

    private void txtVouNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtVouNoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVouNoActionPerformed

    @Override
    public void keyEvent(KeyEvent e) {

    }

    @Override
    public void selected(Object source, Object selectObj) {
        switch (source.toString()) {
            case "TRADER" -> {
            }
            case "SALE-TOTAL" ->
                calculateMilling();
            case "SALE-TOTAL-OUT" ->
                calculateMilling();
            case "EXPENSE" ->
                calculateMilling();
            case "MILLING-HISTORY" -> {
                if (selectObj instanceof MillingHis s) {
                    boolean local = s.isLocal();
                    inventoryRepo.findMilling(s.getKey().getVouNo(), s.getDeptId(), local).subscribe((t) -> {
                        t.setLocal(local);
                        setVoucher(t);
                    }, (e) -> {
                        JOptionPane.showMessageDialog(this, e.getMessage());
                    });
                }
            }
            case "Job" -> {
                if (selectObj instanceof Job v) {
                    inventoryRepo.findJob(v.getKey().getJobNo()).doOnSuccess((t) -> {
                        setMillingDetail(t);
                    }).subscribe();
                }
            }
            case "Select" -> {
            }
        }
    }

    private void setMillingDetail(Job oh) {
        if (oh != null) {
            clear();
            progress.setIndeterminate(true);
            String jobNo = oh.getKey().getJobNo();
            inventoryRepo.getStockIODetailByJob(jobNo).doOnSuccess((list) -> {
                List<MillingOutDetail> listOut = new ArrayList<>();
                List<MillingRawDetail> listRaw = new ArrayList<>();
                list.forEach((od) -> {
                    if (od.getInQty() > 0) {
                        MillingOutDetail sd = new MillingOutDetail();
                        sd.setStockCode(od.getStockCode());
                        sd.setUserCode(od.getUserCode());
                        sd.setStockName(od.getStockName());
                        sd.setRelName(od.getRelName());
                        sd.setWeight(od.getWeight());
                        sd.setWeightUnit("-");
                        sd.setTotalWeight(Util1.getDouble(od.getInQty() * od.getWeight()));
                        sd.setQty(Util1.getDouble(od.getInQty()));
                        sd.setAmount(Util1.getDouble(od.getInQty() * od.getCostPrice()));
                        sd.setUnitCode("-");
                        sd.setPrice(Util1.getDouble(od.getCostPrice()));
                        sd.setLocCode(od.getLocCode());
                        sd.setLocName(od.getLocName());
                        listOut.add(sd);
                    } else if (od.getOutQty() > 0) {
                        MillingRawDetail sd = new MillingRawDetail();
                        sd.setStockCode(od.getStockCode());
                        sd.setUserCode(od.getUserCode());
                        sd.setStockName(od.getStockName());
                        sd.setRelName(od.getRelName());
                        sd.setWeight(od.getWeight());
                        sd.setWeightUnit("-");
                        sd.setTotalWeight(Util1.getDouble(od.getOutQty() * od.getWeight()));
                        sd.setQty(Util1.getDouble(od.getOutQty()));
                        sd.setAmount(Util1.getDouble(od.getOutQty() * od.getCostPrice()));
                        sd.setUnitCode("-");
                        sd.setPrice(Util1.getDouble(od.getCostPrice()));
                        sd.setLocCode(od.getLocCode());
                        sd.setLocName(od.getLocName());
                        listRaw.add(sd);
                    }
                });
                millingOutTableModel.setListDetail(listOut);
                millingRawTableModel.setListDetail(listRaw);
            }).doOnTerminate(() -> {
                milling.setJobNo(oh.getKey().getJobNo());
                txtJob.setText(oh.getJobName());
                millingExpenseTableModel.addNewRow();
                millingRawTableModel.addNewRow();
                millingOutTableModel.addNewRow();
                setMillingUsage();
                calculateMilling();
                focusTable();
                progress.setIndeterminate(false);
            }).subscribe();
        }
    }

    public void setMillingUsage() {
        List<MillingOutDetail> list = millingOutTableModel.getListDetail();
        if (list != null) {
            List<MillingUsage> listUsage = new ArrayList<>();
            list.forEach((t) -> {
                String stockCode = t.getStockCode();
                if (!Util1.isNullOrEmpty(stockCode)) {
                    double qty = t.getQty();
                    inventoryRepo.getPattern(stockCode, null).doOnSuccess((listPattern) -> {
                        if (listPattern != null) {
                            listPattern.forEach((p) -> {
                                MillingUsage u = new MillingUsage();
                                u.setUserCode(u.getUserCode());
                                u.setStockCode(p.getKey().getStockCode());
                                u.setStockName(p.getStockName());
                                u.setQty(qty * p.getQty());
                                u.setUnit(p.getUnitCode());
                                u.setLocCode(p.getLocCode());
                                u.setLocName(p.getLocName());
                                listUsage.add(u);
                            });
                        }
                    }).subscribe();
                }
            });
            usageDialog.setListUsage(listUsage);
        }
    }

    @Override
    public void keyTyped(KeyEvent e
    ) {

    }

    @Override
    public void keyPressed(KeyEvent e
    ) {

    }

    @Override
    public void keyReleased(KeyEvent e
    ) {
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
            }
            case "txtCus" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {

                }
            }

            case "txtVouStatus" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtCus.requestFocus();
                }
            }
            case "txtRemark" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    focusTable();
                }
            }
            case "txtSaleDate" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String date = ((JTextFieldDateEditor) sourceObj).getText();
                    txtSaleDate.setDate(Util1.formatDate(date));
                    txtCus.requestFocus();
                }
            }
            case "txtCurrency" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtRemark.requestFocus();
                }
            }
            case "txtVouTaxP" -> {
            }
        }
    }

    public String addCreditDay(String date, Integer dCount) {
        return LocalDateTime.parse(date)
                .plusDays(dCount)
                .toString();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addExpense1;
    private javax.swing.JButton btnTransfer;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JLabel lblExpRecord;
    private javax.swing.JLabel lblLoadRecord;
    private javax.swing.JLabel lblOutRecord;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPanel panelSale;
    private javax.swing.JTable tblExpense;
    private javax.swing.JTable tblOutput;
    private javax.swing.JTable tblRaw;
    private javax.swing.JTextField txtCurrency;
    private javax.swing.JTextField txtCus;
    private javax.swing.JFormattedTextField txtEffQty;
    private javax.swing.JFormattedTextField txtEffWt;
    private javax.swing.JTextField txtJob;
    private javax.swing.JFormattedTextField txtLoadAmt;
    private javax.swing.JFormattedTextField txtLoadCost;
    private javax.swing.JFormattedTextField txtLoadExpense;
    private javax.swing.JFormattedTextField txtLoadQty;
    private javax.swing.JFormattedTextField txtLoadWeight;
    private javax.swing.JTextField txtLocation;
    private javax.swing.JFormattedTextField txtOutputAmt;
    private javax.swing.JFormattedTextField txtOutputQty;
    private javax.swing.JFormattedTextField txtOutputWeight;
    private javax.swing.JTextField txtProjectNo;
    private javax.swing.JFormattedTextField txtQtyLoss;
    private javax.swing.JTextField txtReference;
    private javax.swing.JTextField txtRemark;
    private com.toedter.calendar.JDateChooser txtSaleDate;
    private javax.swing.JTextField txtVouNo;
    private javax.swing.JTextField txtVouStatus;
    private javax.swing.JFormattedTextField txtWtLoss;
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
        initData();
    }

    @Override
    public void filter() {
        findDialog.setVisible(!findDialog.isVisible());
    }

    @Override
    public String panelName() {
        return this.getName();
    }

    private void expenseDialog() {
        ExpenseSetupDialog d = new ExpenseSetupDialog(Global.parentForm, false);
        d.setInventoryRepo(inventoryRepo);
        d.setAccountRepo(accountRepo);
        d.initMain();
        d.setLocationRelativeTo(null);
        d.setVisible(true);
    }

    public void jobDialog() {
        if (jobSearchDialog == null) {
            jobSearchDialog = new JobSearchDialog(Global.parentForm);
            jobSearchDialog.setInventoryRepo(inventoryRepo);
            jobSearchDialog.setUserRepo(userRepo);
            jobSearchDialog.setObserver(this);
            jobSearchDialog.initMain();
            jobSearchDialog.setSize(Global.width - 20, Global.height - 20);
            jobSearchDialog.setLocationRelativeTo(null);
        }
        jobSearchDialog.search();
    }

    private void vouStatusSetup() {
        VouStatusSetupDialog vsDialog = new VouStatusSetupDialog();
        vsDialog.setIconImage(icon);
        vsDialog.setInventoryRepo(inventoryRepo);
        vsDialog.setListVou(vouStatusAutoCompleter.getListData());
        vsDialog.initMain();
        vsDialog.setSize(Global.width / 2, Global.height / 2);
        vsDialog.setLocationRelativeTo(null);
        vsDialog.setVisible(true);

    }
}
