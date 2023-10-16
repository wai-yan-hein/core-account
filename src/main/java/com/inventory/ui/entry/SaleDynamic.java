/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry;

import com.common.DateLockUtil;
import com.repo.AccountRepo;
import com.common.DecimalFormatRender;
import com.common.Global;
import com.common.JasperReportUtil;
import com.common.KeyPropagate;
import com.common.PanelControl;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.common.Util1;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.editor.CarNoAutoCompleter;
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.editor.LocationCellEditor;
import com.inventory.editor.SaleManAutoCompleter;
import com.inventory.editor.StockCellEditor;
import com.inventory.editor.TraderAutoCompleter;
import com.inventory.model.Location;
import com.inventory.model.SaleDetailKey;
import com.inventory.model.SaleHis;
import com.inventory.model.SaleHisDetail;
import com.inventory.model.SaleHisKey;
import com.inventory.model.SaleMan;
import com.inventory.model.StockUnit;
import com.inventory.model.Trader;
import com.inventory.model.TransferHis;
import com.inventory.model.VSale;
import com.inventory.model.VTransfer;
import com.repo.InventoryRepo;
import com.inventory.ui.common.SaleByWeightTableModel;
import com.inventory.ui.common.SaleExportTableModel;
import com.inventory.ui.common.StockInfoPanel;
import com.inventory.ui.entry.dialog.SaleHistoryDialog;
import com.inventory.ui.entry.dialog.TransferHistoryDialog;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.inventory.editor.StockUnitEditor;
import com.inventory.ui.common.SaleRiceTableModel;
import com.inventory.ui.entry.dialog.StockBalanceDialog;
import com.inventory.ui.entry.dialog.VouDiscountDialog;
import com.toedter.calendar.JTextFieldDateEditor;
import com.repo.UserRepo;
import com.user.editor.CurrencyAutoCompleter;
import com.user.editor.ProjectAutoCompleter;
import com.user.model.Project;
import com.user.model.ProjectKey;
import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
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
public class SaleDynamic extends javax.swing.JPanel implements SelectionObserver, KeyListener, KeyPropagate, PanelControl {

    public static final int WEIGHT = 1;
    public static final int EXPORT = 2;
    public static final int RICE = 3;
    private List<SaleHisDetail> listDetail = new ArrayList();
    private final SaleByWeightTableModel saleTableModel = new SaleByWeightTableModel();
    private final SaleExportTableModel saleExportTableModel = new SaleExportTableModel();
    private final SaleRiceTableModel saleRiceTableModel = new SaleRiceTableModel();
    private SaleHistoryDialog dialog;
    private InventoryRepo inventoryRepo;
    private AccountRepo accountRepo;
    private UserRepo userRepo;
    private CurrencyAutoCompleter currAutoCompleter;
    private TraderAutoCompleter traderAutoCompleter;
    private SaleManAutoCompleter saleManCompleter;
    private LocationAutoCompleter locationAutoCompleter;
    private SelectionObserver observer;
    private SaleHis saleHis = new SaleHis();
    private JProgressBar progress;
    private Mono<List<Location>> monoLoc;
    private double prvBal = 0;
    private double balance = 0;
    private CarNoAutoCompleter carNoAutoCompleter;
    private ProjectAutoCompleter projectAutoCompleter;
    private final StockInfoPanel stockInfoPanel = new StockInfoPanel();
    private int type;
    private TransferHistoryDialog transferHistoryDialog;
    private StockBalanceDialog stockBalanceDialog;
    private VouDiscountDialog vouDiscountDialog;

    public void setStockBalanceDialog(StockBalanceDialog stockBalanceDialog) {
        this.stockBalanceDialog = stockBalanceDialog;
    }

    public LocationAutoCompleter getLocationAutoCompleter() {
        return locationAutoCompleter;
    }

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public void setAccountRepo(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Creates new form SaleEntry1
     *
     * @param type
     */
    public SaleDynamic(int type) {
        this.type = type;
        initComponents();
        initButtonGroup();
        initKeyListener();
        initTextBoxFormat();
        initTextBoxValue();
        initDateListner();
        actionMapping();
    }

    private void actionMapping() {
        String solve = "delete";
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        tblSale.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, solve);
        tblSale.getActionMap().put(solve, new DeleteAction());

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
        txtDueDate.getDateEditor().getUiComponent().setName("txtDueDate");
        txtDueDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtDueDate.getDateEditor().getUiComponent().addFocusListener(fa);
        txtCurrency.addFocusListener(fa);
        txtCus.addFocusListener(fa);
        txtLocation.addFocusListener(fa);
        txtSaleman.addFocusListener(fa);
        txtRemark.addFocusListener(fa);
        txtReference.addFocusListener(fa);
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

    private void initButtonGroup() {
        ButtonGroup g = new ButtonGroup();
        g.add(chkVou);
        g.add(chkA4);
        g.add(chkA5);
    }

    public void initMain() {
        initCombo();
        initTable();
        initModel();
        assignDefaultValue();
        txtSaleDate.setDate(Util1.getTodayDate());
        txtCus.requestFocus();
    }

    private void setStockInfo() {
        int row = tblSale.convertRowIndexToModel(tblSale.getSelectedRow());
        if (row >= 0) {
            SaleHisDetail shd = getSaleHisDetail(row);
            stockInfoPanel.setStock(shd.getStockCode());
        }
    }

    private SaleHisDetail getSaleHisDetail(int row) {
        switch (type) {
            case WEIGHT -> {
                return saleTableModel.getSale(row);
            }
            case EXPORT -> {
                return saleExportTableModel.getSale(row);
            }
            case RICE -> {
                return saleRiceTableModel.getSale(row);
            }
        }
        return null;
    }

    private void initModel() {
        switch (type) {
            case WEIGHT -> {
                initWeight();
            }
            case EXPORT -> {
                initExport();
            }
            case RICE -> {
                initRice();
            }
        }
    }

    private void initWeight() {
        tblSale.setModel(saleTableModel);
        saleTableModel.setLblRecord(lblRec);
        saleTableModel.setParent(tblSale);
        saleTableModel.setSale(this);
        saleTableModel.addNewRow();
        saleTableModel.setObserver(this);
        saleTableModel.setVouDate(txtSaleDate);
        saleTableModel.setInventoryRepo(inventoryRepo);
        saleTableModel.setDialog(stockBalanceDialog);
        tblSale.getTableHeader().setFont(Global.tblHeaderFont);
        tblSale.setCellSelectionEnabled(true);
        tblSale.getColumnModel().getColumn(0).setPreferredWidth(50);//Code
        tblSale.getColumnModel().getColumn(1).setPreferredWidth(450);//Name
        tblSale.getColumnModel().getColumn(2).setPreferredWidth(60);//Rel
        tblSale.getColumnModel().getColumn(3).setPreferredWidth(60);//Location
        tblSale.getColumnModel().getColumn(4).setPreferredWidth(50);//weight
        tblSale.getColumnModel().getColumn(5).setPreferredWidth(30);//unit
        tblSale.getColumnModel().getColumn(6).setPreferredWidth(50);//qty
        tblSale.getColumnModel().getColumn(7).setPreferredWidth(30);//unit
        tblSale.getColumnModel().getColumn(8).setPreferredWidth(50);//std
        tblSale.getColumnModel().getColumn(9).setPreferredWidth(50);//total
        tblSale.getColumnModel().getColumn(10).setPreferredWidth(50);//price
        tblSale.getColumnModel().getColumn(11).setPreferredWidth(60);//amt
        tblSale.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor(inventoryRepo));
        tblSale.getColumnModel().getColumn(1).setCellEditor(new StockCellEditor(inventoryRepo));
        monoLoc.subscribe((t) -> {
            tblSale.getColumnModel().getColumn(3).setCellEditor(new LocationCellEditor(t));
        });
        Mono<List<StockUnit>> monoUnit = inventoryRepo.getStockUnit();
        tblSale.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());//weight
        monoUnit.subscribe((t) -> {
            tblSale.getColumnModel().getColumn(5).setCellEditor(new StockUnitEditor(t));//unit
        });
        tblSale.getColumnModel().getColumn(6).setCellEditor(new AutoClearEditor());//qty
        monoUnit.subscribe((t) -> {
            tblSale.getColumnModel().getColumn(7).setCellEditor(new StockUnitEditor(t));//unit
        });
        tblSale.getColumnModel().getColumn(8).setCellEditor(new AutoClearEditor());//wt
        tblSale.getColumnModel().getColumn(10).setCellEditor(new AutoClearEditor());//
    }

    private void initRice() {
        tblSale.setModel(saleRiceTableModel);
        saleRiceTableModel.setLblRecord(lblRec);
        saleRiceTableModel.setParent(tblSale);
        saleRiceTableModel.setSale(this);
        saleRiceTableModel.addNewRow();
        saleRiceTableModel.setObserver(this);
        saleRiceTableModel.setDialog(stockBalanceDialog);
        tblSale.getTableHeader().setFont(Global.tblHeaderFont);
        tblSale.setCellSelectionEnabled(true);
        tblSale.getColumnModel().getColumn(0).setPreferredWidth(50);//Code
        tblSale.getColumnModel().getColumn(1).setPreferredWidth(450);//Name
        tblSale.getColumnModel().getColumn(2).setPreferredWidth(60);//Location
        tblSale.getColumnModel().getColumn(3).setPreferredWidth(50);//weight
        tblSale.getColumnModel().getColumn(4).setPreferredWidth(30);//unit
        tblSale.getColumnModel().getColumn(5).setPreferredWidth(50);//qty
        tblSale.getColumnModel().getColumn(6).setPreferredWidth(30);//unit
        tblSale.getColumnModel().getColumn(7).setPreferredWidth(50);//total
        tblSale.getColumnModel().getColumn(8).setPreferredWidth(50);//price
        tblSale.getColumnModel().getColumn(9).setPreferredWidth(60);//amt
        tblSale.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor(inventoryRepo));
        tblSale.getColumnModel().getColumn(1).setCellEditor(new StockCellEditor(inventoryRepo));
        monoLoc.subscribe((t) -> {
            tblSale.getColumnModel().getColumn(2).setCellEditor(new LocationCellEditor(t));
        });
        Mono<List<StockUnit>> monoUnit = inventoryRepo.getStockUnit();
        tblSale.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());//weight
        monoUnit.subscribe((t) -> {
            tblSale.getColumnModel().getColumn(4).setCellEditor(new StockUnitEditor(t));//unit
        });
        tblSale.getColumnModel().getColumn(5).setCellEditor(new AutoClearEditor());//qty
        monoUnit.subscribe((t) -> {
            tblSale.getColumnModel().getColumn(6).setCellEditor(new StockUnitEditor(t));//unit
        });
        tblSale.getColumnModel().getColumn(8).setCellEditor(new AutoClearEditor());//wt
        tblSale.getColumnModel().getColumn(9).setCellEditor(new AutoClearEditor());//
    }

    private void initExport() {
        tblSale.setModel(saleExportTableModel);
        saleExportTableModel.setLblRecord(lblRec);
        saleExportTableModel.setParent(tblSale);
        saleExportTableModel.setSale(this);
        saleExportTableModel.addNewRow();
        saleExportTableModel.setObserver(this);
        saleExportTableModel.setVouDate(txtSaleDate);
        saleExportTableModel.setInventoryRepo(inventoryRepo);
        saleExportTableModel.setObserver(this);
        saleExportTableModel.setDialog(stockBalanceDialog);
        tblSale.getColumnModel().getColumn(0).setPreferredWidth(50);//Code
        tblSale.getColumnModel().getColumn(1).setPreferredWidth(450);//Name
        tblSale.getColumnModel().getColumn(2).setPreferredWidth(60);//Rel
        tblSale.getColumnModel().getColumn(3).setPreferredWidth(60);//Location
        tblSale.getColumnModel().getColumn(4).setPreferredWidth(50);//weight
        tblSale.getColumnModel().getColumn(5).setPreferredWidth(30);//unit
        tblSale.getColumnModel().getColumn(6).setPreferredWidth(50);//qty
        tblSale.getColumnModel().getColumn(7).setPreferredWidth(30);//unit
        tblSale.getColumnModel().getColumn(8).setPreferredWidth(50);//total
        tblSale.getColumnModel().getColumn(9).setPreferredWidth(50);//price
        tblSale.getColumnModel().getColumn(10).setPreferredWidth(60);//amt
        tblSale.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor(inventoryRepo));
        tblSale.getColumnModel().getColumn(1).setCellEditor(new StockCellEditor(inventoryRepo));
        monoLoc.subscribe((t) -> {
            tblSale.getColumnModel().getColumn(3).setCellEditor(new LocationCellEditor(t));
        });
        Mono<List<StockUnit>> monoUnit = inventoryRepo.getStockUnit();
        tblSale.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());//weight
        monoUnit.subscribe((t) -> {
            tblSale.getColumnModel().getColumn(5).setCellEditor(new StockUnitEditor(t));//unit
        });
        tblSale.getColumnModel().getColumn(6).setCellEditor(new AutoClearEditor());//qty
        monoUnit.subscribe((t) -> {
            tblSale.getColumnModel().getColumn(7).setCellEditor(new StockUnitEditor(t));//unit
        });
        tblSale.getColumnModel().getColumn(9).setCellEditor(new AutoClearEditor());//wt
        tblSale.getColumnModel().getColumn(10).setCellEditor(new AutoClearEditor());//
    }

    private void initTable() {
        tblSale.getTableHeader().setFont(Global.tblHeaderFont);
        tblSale.setCellSelectionEnabled(true);
        tblSale.setDefaultRenderer(Object.class, new DecimalFormatRender());
        tblSale.setDefaultRenderer(Double.class, new DecimalFormatRender());
        tblSale.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblSale.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void initCombo() {
        traderAutoCompleter = new TraderAutoCompleter(txtCus, inventoryRepo, null, false, "CUS");
        traderAutoCompleter.setObserver(this);
        monoLoc = inventoryRepo.getLocation();
        saleManCompleter = new SaleManAutoCompleter(txtSaleman, null, false);
        inventoryRepo.getSaleMan().subscribe((t) -> {
            saleManCompleter.setListSaleMan(t);
        });
        locationAutoCompleter = new LocationAutoCompleter(txtLocation, null, false, false);
        locationAutoCompleter.setObserver(this);
        monoLoc.subscribe((t) -> {
            locationAutoCompleter.setListLocation(t);
        });
        currAutoCompleter = new CurrencyAutoCompleter(txtCurrency, null);
        userRepo.getCurrency().subscribe((t) -> {
            currAutoCompleter.setListCurrency(t);
        });
        userRepo.getDefaultCurrency().subscribe((c) -> {
            currAutoCompleter.setCurrency(c);
        });
        carNoAutoCompleter = new CarNoAutoCompleter(txtCarNo, inventoryRepo, null, false, "Sale");
        carNoAutoCompleter.setObserver(this);
        projectAutoCompleter = new ProjectAutoCompleter(txtProjectNo, userRepo, null, false);
        projectAutoCompleter.setObserver(this);
    }

    private void initKeyListener() {
        txtSaleDate.getDateEditor().getUiComponent().setName("txtSaleDate");
        txtSaleDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtDueDate.getDateEditor().getUiComponent().setName("txtDueDate");
        txtDueDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtVouNo.addKeyListener(this);
        txtRemark.addKeyListener(this);
        txtCus.addKeyListener(this);
        txtLocation.addKeyListener(this);
        txtSaleman.addKeyListener(this);
        txtCurrency.addKeyListener(this);
        tblSale.addKeyListener(this);
        txtVouDiscP.addKeyListener(this);
        txtVouDiscount.addKeyListener(this);
        txtTax.addKeyListener(this);
        txtVouTaxP.addKeyListener(this);
        txtVouPaid.addKeyListener(this);
    }

    private void initTextBoxValue() {
        txtVouTotal.setValue(0);
        txtVouDiscount.setValue(0);
        txtTax.setValue(0);
        txtVouPaid.setValue(0);
        txtVouBalance.setValue(0);
        txtVouTaxP.setValue(0);
        txtVouDiscP.setValue(0);
        txtGrandTotal.setValue(0);
    }

    private void initTextBoxFormat() {
        txtVouBalance.setFormatterFactory(Util1.getDecimalFormat());
        txtVouDiscount.setFormatterFactory(Util1.getDecimalFormat());
        txtVouPaid.setFormatterFactory(Util1.getDecimalFormat());
        txtVouTotal.setFormatterFactory(Util1.getDecimalFormat());
        txtVouDiscP.setFormatterFactory(Util1.getDecimalFormat());
        txtVouTaxP.setFormatterFactory(Util1.getDecimalFormat());
        txtGrandTotal.setFormatterFactory(Util1.getDecimalFormat());
        txtTax.setFormatterFactory(Util1.getDecimalFormat());
    }

    private void assignDefaultValue() {
        if (currAutoCompleter != null) {
            userRepo.getDefaultCurrency().subscribe((t) -> {
                currAutoCompleter.setCurrency(t);
            });
        }
        if (locationAutoCompleter != null) {
            inventoryRepo.getDefaultLocation().subscribe((tt) -> {
                locationAutoCompleter.setLocation(tt);
            }, (e) -> {
                log.error(e.getMessage());
            });
        }
        if (saleManCompleter != null) {
            inventoryRepo.getDefaultSaleMan().subscribe((t) -> {
                saleManCompleter.setSaleMan(t);
            });
        }

        inventoryRepo.getDefaultCustomer().subscribe((t) -> {
            traderAutoCompleter.setTrader(t);
        });
        txtDueDate.setDate(null);
        progress.setIndeterminate(false);
        txtCurrency.setEnabled(ProUtil.isMultiCur());
        txtVouNo.setText(null);
        chkPaid.setSelected(ProUtil.isSalePaid());
        chkVou.setSelected(true);
        chkA4.setSelected(Util1.getBoolean(ProUtil.getProperty("check.sale.A4")));
        chkA5.setSelected(Util1.getBoolean(ProUtil.getProperty("check.sale.A5")));
        if (!lblStatus.getText().equals("NEW")) {
            txtSaleDate.setDate(Util1.getTodayDate());
        }
    }

    private void clearList() {
        switch (type) {
            case WEIGHT -> {
                saleTableModel.removeListDetail();
                saleTableModel.clearDelList();
            }
            case EXPORT -> {
                saleExportTableModel.removeListDetail();
                saleExportTableModel.clearDelList();
            }
            case RICE -> {
                saleRiceTableModel.removeListDetail();
                saleRiceTableModel.clearDelList();
            }
        }

    }

    private void clear() {
        disableForm(true);
        clearList();
        clearDiscount();
        initTextBoxValue();
        assignDefaultValue();
        saleHis = new SaleHis();
        lblStatus.setText("NEW");
        lblStatus.setForeground(Color.GREEN);
        progress.setIndeterminate(false);
        txtRemark.setText(null);
        txtReference.setText(null);
        txtCus.requestFocus();
        carNoAutoCompleter.setAutoText(null);
        projectAutoCompleter.setProject(null);
    }

    private void clearDiscount() {
        if (vouDiscountDialog != null) {
            vouDiscountDialog.clear();
        }
    }

    private boolean isValidDetail() {
        switch (type) {
            case WEIGHT -> {
                return saleTableModel.isValidEntry();
            }
            case EXPORT -> {
                return saleExportTableModel.isValidEntry();
            }
            case RICE -> {
                return saleRiceTableModel.isValidEntry();
            }
        }
        return false;
    }

    public void saveSale(boolean print) {
        if (isValidEntry() && isValidDetail()) {
            if (DateLockUtil.isLockDate(txtSaleDate.getDate())) {
                DateLockUtil.showMessage(this);
                txtSaleDate.requestFocus();
                return;
            }
            observer.selected("save", false);
            if (Util1.getBoolean(ProUtil.getProperty("trader.balance"))) {
                String date = Util1.toDateStr(txtSaleDate.getDate(), "yyyy-MM-dd");
                String traderCode = traderAutoCompleter.getTrader().getKey().getCode();
                balance = accountRepo.getTraderBalance(date, traderCode, saleHis.getCurCode(), Global.compCode);
                if (balance != 0) {
                    prvBal = balance - Util1.getDouble(txtVouBalance.getValue());
                }
            }
            progress.setIndeterminate(true);
            saleHis.setListSH(getListDetail());
            saleHis.setListDel(getListDel());
            inventoryRepo.save(saleHis).subscribe((t) -> {
                progress.setIndeterminate(false);
                clear();
                if (print) {
                    String reportName = getReportName();
                    printVoucher(t.getKey().getVouNo(), reportName, chkVou.isSelected());
                }
            }, (e) -> {
                observer.selected("save", true);
                JOptionPane.showMessageDialog(this, e.getMessage());
                progress.setIndeterminate(false);
            });
        }
    }

    private boolean isValidEntry() {
        boolean status = true;
        if (lblStatus.getText().equals("DELETED")) {
            status = false;
            clear();
        } else if (currAutoCompleter.getCurrency() == null) {
            JOptionPane.showMessageDialog(this, "Choose Currency.",
                    "No Currency.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtCurrency.requestFocus();
        } else if (locationAutoCompleter.getLocation() == null) {
            JOptionPane.showMessageDialog(this, "Choose Location.",
                    "No Location.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtLocation.requestFocus();
        } else if (traderAutoCompleter.getTrader() == null) {
            JOptionPane.showMessageDialog(this, "Choose Trader.",
                    "No Trader.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtLocation.requestFocus();
        } else if (Util1.getDouble(txtVouTotal.getValue()) <= 0) {
            JOptionPane.showMessageDialog(this, "Invalid Amount.",
                    "No Sale Record.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtLocation.requestFocus();
        } else if (!Util1.isDateBetween(txtSaleDate.getDate())) {
            JOptionPane.showMessageDialog(this, "Invalid Date.",
                    "Validation.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtSaleDate.requestFocus();
        } else {
            saleHis.setCreditTerm(Util1.convertToLocalDateTime(txtDueDate.getDate()));
            SaleMan sm = saleManCompleter.getSaleMan();
            if (sm != null) {
                saleHis.setSaleManCode(sm.getKey().getSaleManCode());
            }
            String traderCode = traderAutoCompleter.getTrader().getKey().getCode();
            saleHis.setRemark(txtRemark.getText());
            saleHis.setReference(txtReference.getText());
            saleHis.setDiscP(Util1.getDouble(txtVouDiscP.getValue()));
            saleHis.setDiscount(Util1.getDouble(txtVouDiscount.getValue()));
            saleHis.setTaxPercent(Util1.getDouble(txtVouTaxP.getValue()));
            saleHis.setTaxAmt(Util1.getDouble(txtTax.getValue()));
            saleHis.setPaid(Util1.getDouble(txtVouPaid.getValue()));
            saleHis.setBalance(Util1.getDouble(txtVouBalance.getValue()));
            saleHis.setCurCode(currAutoCompleter.getCurrency().getCurCode());
            saleHis.setDeleted(saleHis.isDeleted());
            saleHis.setLocCode(locationAutoCompleter.getLocation().getKey().getLocCode());
            saleHis.setTraderCode(traderCode);
            saleHis.setVouTotal(Util1.getDouble(txtVouTotal.getValue()));
            saleHis.setGrandTotal(Util1.getDouble(txtGrandTotal.getValue()));
            saleHis.setStatus(lblStatus.getText());
            saleHis.setVouDate(Util1.convertToLocalDateTime(txtSaleDate.getDate()));
            saleHis.setMacId(Global.macId);
            saleHis.setCarNo(txtCarNo.getText());
            Project p = projectAutoCompleter.getProject();
            saleHis.setProjectNo(p == null ? null : p.getKey().getProjectNo());
            if (vouDiscountDialog != null) {
                saleHis.setListVouDiscount(vouDiscountDialog.getListDetail());
                saleHis.setListDelVouDiscount(vouDiscountDialog.getListDel());
            }
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
                String vouNo = saleHis.getKey().getVouNo();
                boolean exist = inventoryRepo.checkPaymentExist(vouNo, traderCode, "C").block();
                if (exist) {
                    JOptionPane.showMessageDialog(this, "This voucher is already paid in Customer Payment.", "Message", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        }
        return status;
    }

    private void deleteSale() {
        String status = lblStatus.getText();
        switch (status) {
            case "EDIT" -> {
                int yes_no = JOptionPane.showConfirmDialog(this,
                        "Are you sure to delete?", "Save Voucher Delete.", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                if (yes_no == 0) {
                    inventoryRepo.delete(saleHis).subscribe((t) -> {
                        clear();
                    });
                }
            }
            case "DELETED" -> {
                int yes_no = JOptionPane.showConfirmDialog(this,
                        "Are you sure to restore?", "Purchase Voucher Restore.", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
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
        int row = tblSale.convertRowIndexToModel(tblSale.getSelectedRow());
        if (row >= 0) {
            if (tblSale.getCellEditor() != null) {
                tblSale.getCellEditor().stopCellEditing();
            }
            int yes_no = JOptionPane.showConfirmDialog(this,
                    "Are you sure to delete?", "Sale Transaction delete.", JOptionPane.YES_NO_OPTION);
            if (yes_no == 0) {
                deleteDetail(row);
                calculateTotalAmount(false);
            }
        }
    }

    private void deleteDetail(int row) {
        switch (type) {
            case WEIGHT ->
                saleTableModel.delete(row);
            case EXPORT ->
                saleExportTableModel.delete(row);
            case RICE -> {
                saleRiceTableModel.delete(row);
            }

        }
    }

    private List<SaleHisDetail> getListDetail() {
        switch (type) {
            case WEIGHT -> {
                return saleTableModel.getListDetail();
            }
            case EXPORT -> {
                return saleExportTableModel.getListDetail();
            }
            case RICE -> {
                return saleRiceTableModel.getListDetail();
            }
            default ->
                throw new AssertionError();
        }
    }

    private List<SaleDetailKey> getListDel() {
        switch (type) {
            case WEIGHT -> {
                return saleTableModel.getDelList();
            }
            case EXPORT -> {
                return saleExportTableModel.getDelList();
            }
            case RICE -> {
                return saleRiceTableModel.getDelList();
            }
        }
        return null;
    }

    private void calculateTotalAmount(boolean partial) {
        double totalVouBalance;
        double totalAmount = 0.0f;
        listDetail = getListDetail();
        totalAmount = listDetail.stream().map(sdh -> Util1.getDouble(sdh.getAmount())).reduce(totalAmount, (accumulator, _item) -> accumulator + _item);
        txtVouTotal.setValue(totalAmount);
        //cal discAmt
        double discp = Util1.getDouble(txtVouDiscP.getValue());
        if (discp > 0) {
            double discountAmt = (totalAmount * (discp / 100));
            txtVouDiscount.setValue(Util1.getDouble(discountAmt));
        }
        //calculate taxAmt
        double taxp = Util1.getDouble(txtVouTaxP.getValue());
        double taxAmt = Util1.getDouble(txtTax.getValue());
        if (taxp > 0) {
            double afterDiscountAmt = totalAmount - Util1.getDouble(txtVouDiscount.getValue());
            double totalTax = (afterDiscountAmt * taxp) / 100;
            txtTax.setValue(Util1.getDouble(totalTax));
        } else if (taxAmt > 0) {
            double afterDiscountAmt = totalAmount - Util1.getDouble(txtVouDiscount.getValue());
            taxp = (taxAmt / afterDiscountAmt) * 100;
            txtVouTaxP.setValue(Util1.getDouble(taxp));
        }
        //
        txtGrandTotal.setValue(totalAmount
                + Util1.getDouble(txtTax.getValue())
                - Util1.getDouble(txtVouDiscount.getValue()));
        double grandTotal = Util1.getDouble(txtGrandTotal.getValue());
        double paid = Util1.getDouble(txtVouPaid.getText());
        if (!partial) {
            if (paid == 0 || paid != grandTotal) {
                if (chkPaid.isSelected()) {
                    txtVouPaid.setValue(grandTotal);
                } else {
                    txtVouPaid.setValue(0);
                }
            }
        }
        paid = Util1.getDouble(txtVouPaid.getText());
        if (paid > grandTotal) {
            txtVouPaid.setValue(grandTotal);
            paid = grandTotal;
        }
        totalVouBalance = grandTotal - paid;
        txtVouBalance.setValue(Util1.getDouble(totalVouBalance));
    }

    public void historySale() {
        if (dialog == null) {
            dialog = new SaleHistoryDialog(Global.parentForm);
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
            inventoryRepo.findLocation(saleHis.getLocCode()).subscribe((t) -> {
                locationAutoCompleter.setLocation(t);
            });
            inventoryRepo.findTrader(saleHis.getTraderCode()).subscribe((t) -> {
                traderAutoCompleter.setTrader(t);
            });
            userRepo.findCurrency(saleHis.getCurCode()).subscribe((t) -> {
                currAutoCompleter.setCurrency(t);
            });
            inventoryRepo.findSaleMan(saleHis.getSaleManCode()).subscribe((t) -> {
                saleManCompleter.setSaleMan(t);
            });
            if (sh.getProjectNo() != null) {
                userRepo.find(new ProjectKey(sh.getProjectNo(), Global.compCode)).subscribe(t -> {
                    projectAutoCompleter.setProject(t);
                }, (e) -> {
                    JOptionPane.showMessageDialog(this, e.getMessage());
                });
            } else {
                projectAutoCompleter.setProject(null);
            }
            String vouNo = sh.getKey().getVouNo();
            sh.setVouLock(!sh.getDeptId().equals(Global.deptId));
            inventoryRepo.getSaleDetail(vouNo, saleHis.getDeptId(), saleHis.isLocal()).subscribe((t) -> {
                setListDetail(t);
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
                txtDueDate.setDate(Util1.convertToDate(saleHis.getCreditTerm()));
                txtRemark.setText(saleHis.getRemark());
                txtReference.setText(saleHis.getReference());
                txtSaleDate.setDate(Util1.convertToDate(saleHis.getVouDate()));
                txtVouTotal.setValue(Util1.getDouble(saleHis.getVouTotal()));
                txtVouDiscP.setValue(Util1.getDouble(saleHis.getDiscP()));
                txtVouDiscount.setValue(Util1.getDouble(saleHis.getDiscount()));
                txtVouTaxP.setValue(Util1.getDouble(saleHis.getTaxPercent()));
                txtTax.setValue(Util1.getDouble(saleHis.getTaxAmt()));
                txtVouPaid.setValue(Util1.getDouble(saleHis.getPaid()));
                txtVouBalance.setValue(Util1.getDouble(saleHis.getBalance()));
                txtGrandTotal.setValue(Util1.getDouble(saleHis.getGrandTotal()));
                txtCarNo.setText(saleHis.getCarNo());
                chkPaid.setSelected(saleHis.getPaid() > 0);
                focusTable();
                progress.setIndeterminate(false);
            }, (e) -> {
                progress.setIndeterminate(false);
                JOptionPane.showMessageDialog(this, e.getMessage());
            });
        }
    }

    private void setListDetail(List<SaleHisDetail> list) {
        switch (type) {
            case WEIGHT -> {
                saleTableModel.setListDetail(list);
                saleTableModel.addNewRow();
            }
            case EXPORT -> {
                saleExportTableModel.setListDetail(list);
                saleExportTableModel.addNewRow();
            }
            case RICE -> {
                saleRiceTableModel.setListDetail(list);
                saleRiceTableModel.addNewRow();
            }
        }
    }

    private void disableForm(boolean status) {
        tblSale.setEnabled(status);
        panelSale.setEnabled(status);
        txtSaleDate.setEnabled(status);
        txtCus.setEnabled(status);
        txtLocation.setEnabled(status);
        txtSaleman.setEnabled(status);
        txtRemark.setEnabled(status);
        txtCurrency.setEnabled(status);
        txtDueDate.setEnabled(status);
        txtVouPaid.setEnabled(status);
        txtTax.setEnabled(status);
        txtVouTaxP.setEnabled(status);
        txtVouDiscP.setEnabled(status);
        txtVouDiscount.setEnabled(status);
        txtGrandTotal.setEnabled(status);
        txtReference.setEnabled(status);
        observer.selected("save", status);
        observer.selected("delete", status);
        observer.selected("print", status);

    }

    private void setAllLocation() {
        List<SaleHisDetail> listSaleDetail = getListDetail();
        Location loc = locationAutoCompleter.getLocation();
        if (listSaleDetail != null) {
            listSaleDetail.forEach(sd -> {
                sd.setLocCode(loc.getKey().getLocCode());
                sd.setLocName(loc.getLocName());
            });
        }
        setListDetail(listSaleDetail);
    }

    private void printVoucher(String vouNo, String reportName, boolean print) {
        clear();
        inventoryRepo.getSaleReport(vouNo).subscribe((t) -> {
            if (t != null) {
                viewReport(t, reportName, print);
            }
        }, (e) -> {
            JOptionPane.showMessageDialog(this, e.getMessage());
        });

    }

    private void viewReport(List<VSale> list, String reportName, boolean print) {
        try {
            if (reportName != null) {
                String logoPath = String.format("images%s%s", File.separator, ProUtil.getProperty("logo.name"));
                Map<String, Object> param = new HashMap<>();
                param.put("p_print_date", Util1.getTodayDateTime());
                param.put("p_comp_name", Global.companyName);
                param.put("p_comp_address", Global.companyAddress);
                param.put("p_comp_phone", Global.companyPhone);
                param.put("p_logo_path", logoPath);
                param.put("p_balance", balance);
                param.put("p_prv_balance", prvBal);
                param.put("p_sub_report_dir", "report/");
                if (!list.isEmpty()) {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode n1 = mapper.readTree(Util1.gson.toJson(list.get(0).getListDiscount()));
                    JsonDataSource d1 = new JsonDataSource(n1, null) {
                    };
                    param.put("p_sub_data", d1);
                }
                String reportPath = ProUtil.getReportPath() + reportName.concat(".jasper");
                ByteArrayInputStream stream = new ByteArrayInputStream(Util1.listToByteArray(list));
                JsonDataSource ds = new JsonDataSource(stream);
                JasperPrint jp = JasperFillManager.fillReport(reportPath, param, ds);
                log.info(ProUtil.getFontPath());
                if (print) {
                    JasperReportUtil.print(jp);
                } else {
                    JasperViewer.viewReport(jp, false);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Select Report Type");
                chkVou.requestFocus();
            }
        } catch (HeadlessException | JRException | JsonProcessingException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private String getReportName() {
        String name = null;
        if (chkVou.isSelected()) {
            name = ProUtil.getProperty("report.sale.voucher");
        }
        if (chkA4.isSelected()) {
            name = ProUtil.getProperty("report.sale.A4");
        }
        if (chkA5.isSelected()) {
            name = ProUtil.getProperty("report.sale.A5");
        }
        return name;
    }

    private void focusTable() {
        int rc = tblSale.getRowCount();
        if (rc >= 1) {
            tblSale.setRowSelectionInterval(rc - 1, rc - 1);
            tblSale.setColumnSelectionInterval(0, 0);
            tblSale.requestFocus();
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

    private void setTransferVoucher(TransferHis s, boolean local) {
        progress.setIndeterminate(true);
        clearList();
        Integer deptId = s.getDeptId();
        inventoryRepo.findTrader(s.getTraderCode()).doOnSuccess((t) -> {
            traderAutoCompleter.setTrader(t);
        }).subscribe();
        String vouNo = s.getKey().getVouNo();
        inventoryRepo.getTransferDetail(vouNo, deptId, local).subscribe((t) -> {
            List<SaleHisDetail> list = new ArrayList<>();
            t.forEach((od) -> {
                SaleHisDetail sd = new SaleHisDetail();
                sd.setStockCode(od.getStockCode());
                sd.setUserCode(od.getUserCode());
                sd.setStockName(od.getStockName());
                sd.setRelName(od.getRelName());
                sd.setQty(Util1.getDouble(od.getQty()));
                sd.setUnitCode(od.getUnitCode());
                sd.setWeight(od.getWeight());
                sd.setWeightUnit(od.getWeightUnit());
                list.add(sd);
            });
            setListDetail(list);
            inventoryRepo.findLocation(s.getLocCodeTo()).doOnSuccess((l) -> {
                locationAutoCompleter.setLocation(l);
                setAllLocation();
            }).subscribe();
            progress.setIndeterminate(false);
        }, (e) -> {
            progress.setIndeterminate(false);
            JOptionPane.showMessageDialog(this, e.getMessage());
        });
    }

    private void trasnferDialog() {
        if (transferHistoryDialog == null) {
            transferHistoryDialog = new TransferHistoryDialog(Global.parentForm);
            transferHistoryDialog.setInventoryRepo(inventoryRepo);
            transferHistoryDialog.setUserRepo(userRepo);
            transferHistoryDialog.setObserver(this);
            transferHistoryDialog.initMain();
            transferHistoryDialog.setSize(Global.width - 20, Global.height - 20);
            transferHistoryDialog.setLocationRelativeTo(null);
        }
        transferHistoryDialog.search();
    }

    private void discountDialog() {
        if (vouDiscountDialog == null) {
            vouDiscountDialog = new VouDiscountDialog(Global.parentForm);
            vouDiscountDialog.setLocationRelativeTo(null);
            vouDiscountDialog.setObserver(this);
            vouDiscountDialog.setInventoryRepo(inventoryRepo);
            vouDiscountDialog.initMain();
        }
        String vouNo = txtVouNo.getText();
        vouDiscountDialog.search(vouNo);
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
        jLabel3 = new javax.swing.JLabel();
        txtSaleman = new javax.swing.JTextField();
        txtVouNo = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtDueDate = new com.toedter.calendar.JDateChooser();
        txtSaleDate = new com.toedter.calendar.JDateChooser();
        txtCurrency = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        txtLocation = new javax.swing.JTextField();
        txtReference = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSale = new javax.swing.JTable();
        txtCarNo = new javax.swing.JTextField();
        carNoLabel = new javax.swing.JLabel();
        carNoLabel1 = new javax.swing.JLabel();
        txtProjectNo = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        lblStatus = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        chkA5 = new javax.swing.JCheckBox();
        chkVou = new javax.swing.JCheckBox();
        chkA4 = new javax.swing.JCheckBox();
        lblRec = new javax.swing.JLabel();
        btnBatch1 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
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
        jButton1 = new javax.swing.JButton();

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

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Sale Man");

        txtSaleman.setFont(Global.textFont);
        txtSaleman.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtSaleman.setName("txtSaleman"); // NOI18N
        txtSaleman.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSalemanFocusGained(evt);
            }
        });
        txtSaleman.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSalemanActionPerformed(evt);
            }
        });

        txtVouNo.setEditable(false);
        txtVouNo.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtVouNo.setFont(Global.textFont);
        txtVouNo.setName("txtVouNo"); // NOI18N

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Sale Date");

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Credit Term");

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Currency");

        txtDueDate.setDateFormatString("dd/MM/yyyy");
        txtDueDate.setFont(Global.textFont);

        txtSaleDate.setDateFormatString("dd/MM/yyyy");
        txtSaleDate.setFont(Global.textFont);
        txtSaleDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSaleDateFocusGained(evt);
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

        jLabel22.setFont(Global.lableFont);
        jLabel22.setText("Location");

        txtLocation.setFont(Global.textFont);
        txtLocation.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtLocation.setName("txtLocation"); // NOI18N
        txtLocation.setNextFocusableComponent(txtSaleman);
        txtLocation.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtLocationFocusGained(evt);
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

        tblSale.setFont(Global.textFont);
        tblSale.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblSale.setRowHeight(Global.tblRowHeight);
        tblSale.setShowHorizontalLines(true);
        tblSale.setShowVerticalLines(true);
        tblSale.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblSaleMouseClicked(evt);
            }
        });
        tblSale.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblSaleKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblSale);

        txtCarNo.setFont(Global.textFont);
        txtCarNo.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCarNo.setName("txtCurrency"); // NOI18N
        txtCarNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCarNoActionPerformed(evt);
            }
        });

        carNoLabel.setFont(Global.lableFont);
        carNoLabel.setText("Car No");

        carNoLabel1.setFont(Global.lableFont);
        carNoLabel1.setText("Project No");

        txtProjectNo.setFont(Global.textFont);
        txtProjectNo.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtProjectNo.setName("txtCurrency"); // NOI18N
        txtProjectNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtProjectNoActionPerformed(evt);
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
                        .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCus)
                            .addComponent(txtSaleDate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)
                            .addComponent(txtVouNo, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtRemark, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                            .addComponent(txtSaleman)
                            .addComponent(txtLocation))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtDueDate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)
                            .addComponent(txtCurrency)
                            .addComponent(txtReference))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelSaleLayout.createSequentialGroup()
                                .addComponent(carNoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtCarNo, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE))
                            .addGroup(panelSaleLayout.createSequentialGroup()
                                .addComponent(carNoLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12)
                                .addComponent(txtProjectNo))))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );

        panelSaleLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel17, jLabel2, jLabel21, jLabel22, jLabel3, jLabel4, jLabel5, jLabel6, jLabel9});

        panelSaleLayout.setVerticalGroup(
            panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSaleLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22)
                    .addComponent(txtLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCarNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(carNoLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(txtDueDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtSaleDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtSaleman, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(carNoLabel1)
                        .addComponent(txtProjectNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtCus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel21)
                        .addComponent(jLabel2))
                    .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtReference, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
                .addContainerGap())
        );

        panelSaleLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel3, jLabel5});

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblStatus.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        lblStatus.setForeground(Color.green);
        lblStatus.setText("NEW");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Report Type", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, Global.lableFont));

        chkA5.setFont(Global.textFont);
        chkA5.setText("A5");
        chkA5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkA5ActionPerformed(evt);
            }
        });

        chkVou.setFont(Global.textFont);
        chkVou.setText("Voucher Printer");
        chkVou.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkVouActionPerformed(evt);
            }
        });

        chkA4.setFont(Global.textFont);
        chkA4.setText("A4");
        chkA4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkA4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkVou, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkA5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkA4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(chkVou)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkA5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkA4))
        );

        lblRec.setFont(Global.lableFont);
        lblRec.setText("Records");

        btnBatch1.setFont(Global.lableFont);
        btnBatch1.setText("Transfer");
        btnBatch1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatch1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblRec, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBatch1)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBatch1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblRec)
                .addGap(18, 18, 18)
                .addComponent(lblStatus)
                .addContainerGap())
        );

        jLabel13.setFont(Global.lableFont);
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel13.setText("Vou Total :");

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
        jLabel7.setForeground(Global.selectionColor);
        jLabel7.setText("%");

        jLabel8.setFont(Global.lableFont);
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel8.setText("Vou Balance :");

        jLabel15.setFont(Global.lableFont);
        jLabel15.setForeground(Global.selectionColor);
        jLabel15.setText("%");

        txtVouDiscP.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtVouDiscP.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVouDiscP.setFont(Global.amtFont);
        txtVouDiscP.setName("txtVouDiscP"); // NOI18N
        txtVouDiscP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtVouDiscPActionPerformed(evt);
            }
        });

        txtVouDiscount.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtVouDiscount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVouDiscount.setFont(Global.amtFont);
        txtVouDiscount.setName("txtVouDiscount"); // NOI18N
        txtVouDiscount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtVouDiscountActionPerformed(evt);
            }
        });

        txtVouTaxP.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtVouTaxP.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVouTaxP.setFont(Global.amtFont);
        txtVouTaxP.setName("txtVouTaxP"); // NOI18N
        txtVouTaxP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtVouTaxPActionPerformed(evt);
            }
        });

        txtTax.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtTax.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTax.setFont(Global.amtFont);
        txtTax.setName("txtTax"); // NOI18N
        txtTax.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTaxActionPerformed(evt);
            }
        });

        txtVouPaid.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtVouPaid.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVouPaid.setFont(Global.amtFont);
        txtVouPaid.setName("txtVouPaid"); // NOI18N
        txtVouPaid.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtVouPaidFocusGained(evt);
            }
        });
        txtVouPaid.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                txtVouPaidInputMethodTextChanged(evt);
            }
        });
        txtVouPaid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtVouPaidActionPerformed(evt);
            }
        });
        txtVouPaid.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtVouPaidKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtVouPaidKeyTyped(evt);
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

        txtGrandTotal.setEditable(false);
        txtGrandTotal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtGrandTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtGrandTotal.setFont(Global.amtFont);
        txtGrandTotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtGrandTotalActionPerformed(evt);
            }
        });

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
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator1)
                            .addComponent(jSeparator2)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGap(8, 8, 8)
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                            .addComponent(jLabel20))
                                        .addGap(0, 0, Short.MAX_VALUE)))
                                .addGap(18, 18, 18))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkPaid)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtVouTotal)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtVouDiscP)
                                    .addComponent(txtVouTaxP))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtVouDiscount)
                                    .addComponent(txtTax)))
                            .addComponent(txtGrandTotal)
                            .addComponent(txtVouPaid, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtVouBalance))))
                .addContainerGap())
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel13, jLabel14, jLabel16, jLabel8});

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
                    .addComponent(txtVouPaid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkPaid, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtVouBalance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jScrollPane3.setViewportView(jPanel3);

        jButton1.setFont(Global.lableFont);
        jButton1.setText("Discount");
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
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(512, 512, 512)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(panelSale, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(panelSale, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jButton1))
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

    private void txtSalemanFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSalemanFocusGained
        txtSaleman.selectAll();
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSalemanFocusGained

    private void txtRemarkFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRemarkFocusGained

        // TODO add your handling code here:
    }//GEN-LAST:event_txtRemarkFocusGained

    private void txtLocationFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtLocationFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtLocationFocusGained

    private void tblSaleMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSaleMouseClicked
        setStockInfo();        // TODO add your handling code here:
    }//GEN-LAST:event_tblSaleMouseClicked

    private void tblSaleKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblSaleKeyReleased
        // TODO add your handling code here:

    }//GEN-LAST:event_tblSaleKeyReleased

    private void txtSalemanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSalemanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSalemanActionPerformed

    private void txtCurrencyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCurrencyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCurrencyActionPerformed

    private void txtVouDiscPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtVouDiscPActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVouDiscPActionPerformed

    private void txtVouTaxPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtVouTaxPActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVouTaxPActionPerformed

    private void txtVouDiscountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtVouDiscountActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVouDiscountActionPerformed

    private void txtTaxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTaxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTaxActionPerformed

    private void txtVouPaidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtVouPaidActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVouPaidActionPerformed

    private void txtGrandTotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtGrandTotalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtGrandTotalActionPerformed

    private void txtVouBalanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtVouBalanceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVouBalanceActionPerformed

    private void txtVouPaidKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtVouPaidKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVouPaidKeyTyped

    private void txtVouPaidKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtVouPaidKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVouPaidKeyReleased

    private void chkPaidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkPaidActionPerformed
        // TODO add your handling code here:
        txtVouPaid.setValue(0);
        calculateTotalAmount(false);
    }//GEN-LAST:event_chkPaidActionPerformed

    private void txtReferenceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtReferenceFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtReferenceFocusGained

    private void chkVouActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkVouActionPerformed
        // TODO add your handling code here:


    }//GEN-LAST:event_chkVouActionPerformed

    private void chkA4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkA4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkA4ActionPerformed

    private void chkA5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkA5ActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_chkA5ActionPerformed

    private void txtVouPaidFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVouPaidFocusGained
        // TODO add your handling code here:
        txtVouPaid.selectAll();
    }//GEN-LAST:event_txtVouPaidFocusGained

    private void txtVouPaidInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_txtVouPaidInputMethodTextChanged
        // TODO add your handling code here:
        calculateTotalAmount(false);
    }//GEN-LAST:event_txtVouPaidInputMethodTextChanged

    private void formPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_formPropertyChange
        // TODO add your handling code here:
        log.info("change.");
    }//GEN-LAST:event_formPropertyChange

    private void txtSaleDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSaleDateFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSaleDateFocusGained

    private void txtCarNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCarNoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCarNoActionPerformed

    private void txtProjectNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProjectNoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtProjectNoActionPerformed

    private void btnBatch1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatch1ActionPerformed
        // TODO add your handling code here:
        trasnferDialog();
    }//GEN-LAST:event_btnBatch1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        discountDialog();
    }//GEN-LAST:event_jButton1ActionPerformed

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
                calculateTotalAmount(false);
            case "Location" ->
                setAllLocation();
            case "STOCK-INFO" ->
                setStockInfo();
            case "SALE-HISTORY" -> {
                if (selectObj instanceof VSale s) {
                    boolean local = s.isLocal();
                    inventoryRepo.findSale(s.getVouNo(), s.getDeptId(), local).subscribe((t) -> {
                        t.setLocal(local);
                        setSaleVoucher(t);
                    });
                }
            }
            case "TR-HISTORY" -> {
                if (selectObj instanceof VTransfer v) {
                    inventoryRepo.findTransfer(v.getVouNo(), v.getDeptId(), v.isLocal()).subscribe((t) -> {
                        setTransferVoucher(t, v.isLocal());
                    });
                }
            }
            case "Select" -> {
                calculateTotalAmount(false);
            }
            case "CAL_DISCOUNT" -> {
                double discount = vouDiscountDialog.getTotal();
                txtVouDiscount.setValue(discount);
                calculateTotalAmount(false);
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
            }
            case "txtCus" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtLocation.requestFocus();
                }
            }
            case "txtLocation" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtSaleman.requestFocus();
                }
            }
            case "txtSaleman" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    focusTable();
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
            case "txtDueDate" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String date = ((JTextFieldDateEditor) sourceObj).getText();
                    txtDueDate.setDate(Util1.formatDate(date));
                    txtReference.requestFocus();
                }
            }
            case "txtCurrency" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtRemark.requestFocus();
                }
            }
            case "txtVouTaxP" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (Util1.getDouble(txtVouTaxP.getValue()) <= 0) {
                        txtTax.setValue(0);
                    }
                    calculateTotalAmount(false);
                    focusTable();
                }
            }
            case "txtTax" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtVouTaxP.setValue(0);
                    calculateTotalAmount(false);
                    focusTable();
                }
            }
            case "txtVouDiscount" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (Util1.getDouble(txtVouDiscount.getValue()) >= 0) {
                        txtVouDiscP.setValue(0);
                    }
                    calculateTotalAmount(false);
                    focusTable();
                }
            }
            case "txtVouDiscP" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (Util1.getDouble(txtVouDiscP.getValue()) <= 0) {
                        txtVouDiscount.setValue(0);
                    }
                    calculateTotalAmount(false);
                    focusTable();
                }
            }
            case "txtVouPaid" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    calculateTotalAmount(true);
                    focusTable();
                }
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBatch1;
    private javax.swing.JLabel carNoLabel;
    private javax.swing.JLabel carNoLabel1;
    private javax.swing.JCheckBox chkA4;
    private javax.swing.JCheckBox chkA5;
    private javax.swing.JCheckBox chkPaid;
    private javax.swing.JCheckBox chkVou;
    private javax.swing.JButton jButton1;
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
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblRec;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPanel panelSale;
    private javax.swing.JTable tblSale;
    private javax.swing.JTextField txtCarNo;
    private javax.swing.JTextField txtCurrency;
    private javax.swing.JTextField txtCus;
    private com.toedter.calendar.JDateChooser txtDueDate;
    private javax.swing.JFormattedTextField txtGrandTotal;
    private javax.swing.JTextField txtLocation;
    private javax.swing.JTextField txtProjectNo;
    private javax.swing.JTextField txtReference;
    private javax.swing.JTextField txtRemark;
    private com.toedter.calendar.JDateChooser txtSaleDate;
    private javax.swing.JTextField txtSaleman;
    private javax.swing.JFormattedTextField txtTax;
    private javax.swing.JFormattedTextField txtVouBalance;
    private javax.swing.JFormattedTextField txtVouDiscP;
    private javax.swing.JFormattedTextField txtVouDiscount;
    private javax.swing.JFormattedTextField txtVouNo;
    private javax.swing.JFormattedTextField txtVouPaid;
    private javax.swing.JFormattedTextField txtVouTaxP;
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
    }

    @Override
    public String panelName() {
        return this.getName();
    }
}