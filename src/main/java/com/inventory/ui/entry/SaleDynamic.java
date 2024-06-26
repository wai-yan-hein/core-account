/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry;

import com.acc.dialog.FindDialog;
import com.common.ComponentUtil;
import com.common.DateLockUtil;
import com.repo.AccountRepo;
import com.common.DecimalFormatRender;
import com.common.Global;
import com.common.JasperReportUtil;
import com.common.KeyPropagate;
import com.common.NumberConverter;
import com.common.PanelControl;
import com.common.ProUtil;
import com.common.RowHeader;
import com.common.SelectionObserver;
import com.common.Util1;
import com.dms.dialog.ContractDialog;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.editor.BatchAutoCompeter;
import com.inventory.editor.BatchCellEditor;
import com.inventory.editor.CarNoAutoCompleter;
import com.inventory.editor.DesignEditor;
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.editor.LocationCellEditor;
import com.inventory.editor.SaleManAutoCompleter;
import com.inventory.editor.SalePriceCellEditor;
import com.inventory.editor.SizeEditor;
import com.inventory.editor.StockCellEditor;
import com.inventory.editor.TraderAutoCompleter;
import com.inventory.entity.Location;
import com.inventory.entity.SaleHis;
import com.inventory.entity.SaleHisDetail;
import com.inventory.entity.SaleHisKey;
import com.inventory.entity.SaleMan;
import com.inventory.entity.Trader;
import com.inventory.entity.TransferHis;
import com.inventory.entity.VSale;
import com.inventory.entity.VTransfer;
import com.repo.InventoryRepo;
import com.inventory.ui.common.SaleByWeightTableModel;
import com.inventory.ui.common.SaleExportTableModel;
import com.inventory.ui.common.StockInfoPanel;
import com.inventory.ui.entry.dialog.SaleHistoryDialog;
import com.inventory.ui.entry.dialog.TransferHistoryDialog;
import com.user.editor.AutoClearEditor;
import com.inventory.editor.StockUnitEditor;
import com.inventory.entity.GRN;
import com.inventory.entity.OrderHis;
import com.inventory.entity.OrderHisDetail;
import com.inventory.entity.StockUnitPriceKey;
import com.inventory.entity.WeightHis;
import com.inventory.ui.common.SaleByBatchTableModel;
import com.inventory.ui.common.SaleDesginTableModel;
import com.inventory.ui.common.SalePaddyTableModel;
import com.inventory.ui.common.SaleQtyTableModel;
import com.inventory.ui.common.SaleRiceTableModel;
import com.inventory.ui.common.SaleTableModel;
import com.inventory.ui.entry.dialog.AccountOptionDialog;
import com.inventory.ui.entry.dialog.BatchSearchDialog;
import com.inventory.ui.entry.dialog.GRNDetailDialog;
import com.inventory.ui.entry.dialog.OrderDetailDialog;
import com.inventory.ui.entry.dialog.OrderHistoryDialog;
import com.inventory.ui.entry.dialog.PaymentDialog;
import com.inventory.ui.entry.dialog.SaleFunctionDialog;
import com.inventory.ui.entry.dialog.SaleNoteDialog;
import com.inventory.ui.entry.dialog.SaleWeightLossPriceDialog;
import com.inventory.ui.entry.dialog.StockBalanceFrame;
import com.inventory.ui.entry.dialog.VouDiscountDialog;
import com.inventory.ui.entry.dialog.WeightHistoryDialog;
import com.toedter.calendar.JTextFieldDateEditor;
import com.repo.UserRepo;
import com.user.editor.CurrencyAutoCompleter;
import com.user.editor.ProjectAutoCompleter;
import com.user.model.Branch;
import com.user.model.Project;
import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.view.JasperViewer;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 *
 * @author wai yan
 */
@Slf4j
public class SaleDynamic extends javax.swing.JPanel implements SelectionObserver, KeyListener, KeyPropagate, PanelControl {

    public static final int SALE = 0;
    public static final int WEIGHT = 1;
    public static final int EXPORT = 2;
    public static final int RICE = 3;
    public static final int PADDY = 4;
    public static final int BATCH = 5;
    public static final int QTY = 6;
    public static final int DESIGN = 7;
    private final SaleByWeightTableModel saleWeightTableModel = new SaleByWeightTableModel();
    private final SaleExportTableModel saleExportTableModel = new SaleExportTableModel();
    private final SaleRiceTableModel saleRiceTableModel = new SaleRiceTableModel();
    private final SalePaddyTableModel salePaddyTableModel = new SalePaddyTableModel();
    private final SaleByBatchTableModel saleByBatchTableModel = new SaleByBatchTableModel();
    private final SaleQtyTableModel saleQtyTableModel = new SaleQtyTableModel();
    private final SaleTableModel saleTableModel = new SaleTableModel();
    private final SaleDesginTableModel saleDesginTableModel = new SaleDesginTableModel();

    private SaleHistoryDialog dialog;
    @Setter
    private InventoryRepo inventoryRepo;
    @Setter
    private AccountRepo accountRepo;
    @Setter
    private UserRepo userRepo;
    private CurrencyAutoCompleter currAutoCompleter;
    @Getter
    private TraderAutoCompleter traderAutoCompleter;
    private BatchAutoCompeter batchAutoCompeter;
    private SaleManAutoCompleter saleManCompleter;
    @Getter
    private LocationAutoCompleter locationAutoCompleter;
    @Setter
    private SelectionObserver observer;
    private SaleHis sh = new SaleHis();
    @Setter
    private JProgressBar progress;
    private Mono<List<Location>> monoLoc;
    private CarNoAutoCompleter carNoAutoCompleter;
    private ProjectAutoCompleter projectAutoCompleter;
    private final StockInfoPanel stockInfoPanel = new StockInfoPanel();
    private final int type;
    private TransferHistoryDialog transferHistoryDialog;
    @Setter
    private StockBalanceFrame stockBalanceDialog;
    private VouDiscountDialog vouDiscountDialog;
    private SaleNoteDialog saleNoteDialog;
    private AccountOptionDialog optionDialog;
    private WeightHistoryDialog weightHistoryDialog;
    private ContractDialog contractDialog;
    private PaymentDialog paymentDialog;
    private FindDialog findDialog;
    private SaleWeightLossPriceDialog saleWeightLossPriceDialog;
    private OrderHistoryDialog orderDialog;
    private OrderDetailDialog orderDetailDialog;
    private BatchSearchDialog batchDialog;
    private GRNDetailDialog grnDialog;
    private SaleFunctionDialog saleFunctionDialog;

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
        initProperty();
        actionMapping();
    }

    private void actionMapping() {
        String solve = "delete";
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        tblSale.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, solve);
        tblSale.getActionMap().put(solve, new DeleteAction());
        tblSale.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK), "loss-price");
        tblSale.getActionMap().put("loss-price", new LossPriceAction());

    }

    private class LossPriceAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            int row = tblSale.convertRowIndexToModel(tblSale.getSelectedRow());
            SaleHisDetail pd = saleTableModel.getSale(row);
            if (pd.getStockCode() != null) {
                if (saleWeightLossPriceDialog == null) {
                    saleWeightLossPriceDialog = new SaleWeightLossPriceDialog(Global.parentForm);
                    saleWeightLossPriceDialog.setLocationRelativeTo(null);
                    saleWeightLossPriceDialog.setInventoryRepo(inventoryRepo);
                    saleWeightLossPriceDialog.addKeyListener(new KeyAdapter() {
                        @Override
                        public void keyPressed(KeyEvent e) {
                            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                                saleWeightLossPriceDialog.dispose();
                            }
                        }
                    });
                }
                saleWeightLossPriceDialog.setSaleDetail(pd);
                saleWeightLossPriceDialog.setVisible(true);
                if (saleWeightLossPriceDialog.isConfirm()) {
                    saleTableModel.setValueAt(pd, row, 0);
                }

            }
        }
    }

    private class DeleteAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            deleteTran();
        }
    }

    private void initProperty() {
        ComponentUtil.addFocusListener(this);
    }

    private void initButtonGroup() {
        ButtonGroup g = new ButtonGroup();
        g.add(chkVou);
        g.add(chkA4);
        g.add(chkA5);
    }

    public void initMain() {
        initStockBalance();
        initCombo();
        initTable();
        initModel();
        initRowHeader();
        assignDefaultValue();
        initExternal();
        initFind();
        txtSaleDate.setDate(Util1.getTodayDate());
        txtCus.requestFocus();
    }

    private void initFind() {
        findDialog = new FindDialog(Global.parentForm, tblSale);
    }

    private void initExternal() {
        btnDiscount.setVisible(false);
        btnWeight.setVisible(false);
        btnOrder.setVisible(false);
        btnTransfer.setVisible(true);
        switch (type) {
            case PADDY -> {
                btnWeight.setVisible(true);
            }
            case SALE -> {
                btnOrder.setVisible(true);
            }
            case DESIGN -> {
                btnOrder.setVisible(true);
            }
        }
    }

    private void initRowHeader() {
        RowHeader header = new RowHeader();
        JList list = header.createRowHeader(tblSale, 30);
        scroll.setRowHeaderView(list);
    }

    private void initStockBalance() {
        stockBalanceDialog.setVisible(ProUtil.isCalStock());
        deskPane.add(stockBalanceDialog);
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
            case SALE -> {
                return saleTableModel.getSale(row);
            }
            case WEIGHT -> {
                return saleWeightTableModel.getSale(row);
            }
            case EXPORT -> {
                return saleExportTableModel.getSale(row);
            }
            case RICE -> {
                return saleRiceTableModel.getSale(row);
            }
            case PADDY -> {
                return salePaddyTableModel.getObject(row);
            }
            case BATCH -> {
                return saleByBatchTableModel.getObject(row);
            }
            case QTY -> {
                return saleQtyTableModel.getObject(row);
            }
            case DESIGN -> {
                return saleDesginTableModel.getObject(row);
            }
        }
        return null;
    }

    private void initModel() {
        switch (type) {
            case SALE -> {
                initSaleTable();
            }
            case WEIGHT -> {
                initWeight();
            }
            case EXPORT -> {
                initExport();
            }
            case RICE -> {
                initRice();
            }
            case PADDY -> {
                initPaddy();
            }
            case BATCH -> {
                initBatchTable();
            }
            case QTY -> {
                initQtyTable();
            }
            case DESIGN -> {
                initSaleDesignTable();
            }
        }
    }

    private void initSaleTable() {
        tblSale.setModel(saleTableModel);
        saleTableModel.setLblRecord(lblRec);
        saleTableModel.setSale(this);
        saleTableModel.setParent(tblSale);
        saleTableModel.addNewRow();
        saleTableModel.setObserver(this);
        saleTableModel.setVouDate(txtSaleDate);
        saleTableModel.setInventoryRepo(inventoryRepo);
        saleTableModel.setDialog(stockBalanceDialog);
        tblSale.getColumnModel().getColumn(0).setPreferredWidth(50);//Code
        tblSale.getColumnModel().getColumn(1).setPreferredWidth(450);//Name
        tblSale.getColumnModel().getColumn(2).setPreferredWidth(60);//Rel
        tblSale.getColumnModel().getColumn(3).setPreferredWidth(60);//qty
        tblSale.getColumnModel().getColumn(4).setPreferredWidth(1);//unit
        tblSale.getColumnModel().getColumn(5).setPreferredWidth(1);//price
        tblSale.getColumnModel().getColumn(6).setPreferredWidth(40);//amt
        tblSale.getColumnModel().getColumn(7).setPreferredWidth(60);//Location
        tblSale.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor(inventoryRepo, ProUtil.useBarCode(), ProUtil.isSSContain()));
        tblSale.getColumnModel().getColumn(1).setCellEditor(new StockCellEditor(inventoryRepo, ProUtil.isSSContain()));
        monoLoc.doOnSuccess((t) -> {
            tblSale.getColumnModel().getColumn(7).setCellEditor(new LocationCellEditor(t));
        }).subscribe();
        tblSale.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());//qty
        inventoryRepo.getStockUnit().doOnSuccess((t) -> {
            tblSale.getColumnModel().getColumn(4).setCellEditor(new StockUnitEditor(t));
        }).subscribe();
        tblSale.getColumnModel().getColumn(5).setCellEditor(new AutoClearEditor());//
        if (ProUtil.isSalePriceChange()) {
            if (ProUtil.isPriceOption()) {
                tblSale.getColumnModel().getColumn(5).setCellEditor(new SalePriceCellEditor(inventoryRepo));//price
            } else {
                tblSale.getColumnModel().getColumn(5).setCellEditor(new AutoClearEditor());//price
            }

        } else {
            tblSale.getColumnModel().getColumn(5).setCellEditor(new AutoClearEditor());//price
        }
    }

    private void initWeight() {
        tblSale.setModel(saleWeightTableModel);
        saleWeightTableModel.setLblRecord(lblRec);
        saleWeightTableModel.setParent(tblSale);
        saleWeightTableModel.setSale(this);
        saleWeightTableModel.addNewRow();
        saleWeightTableModel.setObserver(this);
        saleWeightTableModel.setVouDate(txtSaleDate);
        saleWeightTableModel.setInventoryRepo(inventoryRepo);
        saleWeightTableModel.setDialog(stockBalanceDialog);
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
        tblSale.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor(inventoryRepo, ProUtil.isSSContain()));
        tblSale.getColumnModel().getColumn(1).setCellEditor(new StockCellEditor(inventoryRepo, ProUtil.isSSContain()));
        monoLoc.doOnSuccess((t) -> {
            tblSale.getColumnModel().getColumn(3).setCellEditor(new LocationCellEditor(t));
        }).subscribe();
        inventoryRepo.getStockUnit().doOnSuccess((t) -> {
            tblSale.getColumnModel().getColumn(5).setCellEditor(new StockUnitEditor(t));//unit
            tblSale.getColumnModel().getColumn(7).setCellEditor(new StockUnitEditor(t));//unit
        }).subscribe();
        tblSale.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());//weight
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
        tblSale.getColumnModel().getColumn(0).setPreferredWidth(50);//Code
        tblSale.getColumnModel().getColumn(1).setPreferredWidth(450);//Name
        tblSale.getColumnModel().getColumn(2).setPreferredWidth(50);//std-weight
        tblSale.getColumnModel().getColumn(3).setPreferredWidth(50);//avg-weight
        tblSale.getColumnModel().getColumn(4).setPreferredWidth(50);//qty
        tblSale.getColumnModel().getColumn(5).setPreferredWidth(50);//total
        tblSale.getColumnModel().getColumn(6).setPreferredWidth(50);//price
        tblSale.getColumnModel().getColumn(7).setPreferredWidth(60);//amt
        tblSale.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor(inventoryRepo, ProUtil.isSSContain()));
        tblSale.getColumnModel().getColumn(1).setCellEditor(new StockCellEditor(inventoryRepo, ProUtil.isSSContain()));
        tblSale.getColumnModel().getColumn(2).setCellEditor(new AutoClearEditor());//weight
        tblSale.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());//avg-weight
        tblSale.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());//qty
        tblSale.getColumnModel().getColumn(6).setCellEditor(ProUtil.isPricePopup() ? new SalePriceCellEditor(inventoryRepo) : new AutoClearEditor());//price
    }

    private void initExport() {
        tblSale.setModel(saleExportTableModel);
        saleExportTableModel.setLblRecord(lblRec);
        saleExportTableModel.setParent(tblSale);
        saleExportTableModel.setSale(this);
        saleExportTableModel.addNewRow();
        saleExportTableModel.setObserver(this);
        saleExportTableModel.setDialog(stockBalanceDialog);
        tblSale.getColumnModel().getColumn(0).setPreferredWidth(50);//Code
        tblSale.getColumnModel().getColumn(1).setPreferredWidth(300);//Name
        tblSale.getColumnModel().getColumn(2).setPreferredWidth(50);//weight
        tblSale.getColumnModel().getColumn(3).setPreferredWidth(30);//unit
        tblSale.getColumnModel().getColumn(4).setPreferredWidth(50);//qty
        tblSale.getColumnModel().getColumn(5).setPreferredWidth(30);//unit
        tblSale.getColumnModel().getColumn(6).setPreferredWidth(50);//total
        tblSale.getColumnModel().getColumn(7).setPreferredWidth(50);//price
        tblSale.getColumnModel().getColumn(8).setPreferredWidth(60);//amt
        tblSale.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor(inventoryRepo, ProUtil.isSSContain()));
        tblSale.getColumnModel().getColumn(1).setCellEditor(new StockCellEditor(inventoryRepo, ProUtil.isSSContain()));
        tblSale.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());//weight
        inventoryRepo.getStockUnit().doOnSuccess((t) -> {
            tblSale.getColumnModel().getColumn(3).setCellEditor(new StockUnitEditor(t));//unit
            tblSale.getColumnModel().getColumn(5).setCellEditor(new StockUnitEditor(t));//unit
        }).subscribe();
        tblSale.getColumnModel().getColumn(6).setCellEditor(new AutoClearEditor());//qty
        tblSale.getColumnModel().getColumn(7).setCellEditor(new AutoClearEditor());//wt
        tblSale.getColumnModel().getColumn(8).setCellEditor(new AutoClearEditor());//
    }

    private void initPaddy() {
        tblSale.setModel(salePaddyTableModel);
        salePaddyTableModel.setParent(tblSale);
        salePaddyTableModel.addNewRow();
        salePaddyTableModel.setObserver(this);
        salePaddyTableModel.setLblRec(lblRec);
        salePaddyTableModel.setSale(this);
        tblSale.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor(inventoryRepo, ProUtil.isSSContain()));
        tblSale.getColumnModel().getColumn(1).setCellEditor(new StockCellEditor(inventoryRepo, ProUtil.isSSContain()));
        tblSale.getColumnModel().getColumn(2).setCellEditor(new AutoClearEditor());
        tblSale.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());
        tblSale.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());
        tblSale.getColumnModel().getColumn(5).setCellEditor(new AutoClearEditor());
        tblSale.getColumnModel().getColumn(6).setCellEditor(new AutoClearEditor());
        tblSale.getColumnModel().getColumn(7).setCellEditor(new AutoClearEditor());
        tblSale.getColumnModel().getColumn(8).setCellEditor(new AutoClearEditor());
    }

    private void initBatchTable() {
        tblSale.setModel(saleByBatchTableModel);
        saleByBatchTableModel.setLblRecord(lblRec);
        saleByBatchTableModel.setParent(tblSale);
        saleByBatchTableModel.setSale(this);
        saleByBatchTableModel.addNewRow();
        saleByBatchTableModel.setObserver(this);
        saleByBatchTableModel.setVouDate(txtSaleDate);
        saleByBatchTableModel.setInventoryRepo(inventoryRepo);
        saleByBatchTableModel.setDialog(stockBalanceDialog);
        tblSale.getTableHeader().setFont(Global.tblHeaderFont);
        tblSale.setCellSelectionEnabled(true);
        tblSale.getColumnModel().getColumn(0).setPreferredWidth(50);//batch
        tblSale.getColumnModel().getColumn(1).setPreferredWidth(150);//sup
        tblSale.getColumnModel().getColumn(2).setPreferredWidth(50);//Code
        tblSale.getColumnModel().getColumn(3).setPreferredWidth(200);//Name
        tblSale.getColumnModel().getColumn(4).setPreferredWidth(80);//qty
        tblSale.getColumnModel().getColumn(5).setPreferredWidth(5);//unit
        tblSale.getColumnModel().getColumn(6).setPreferredWidth(80);//price
        tblSale.getColumnModel().getColumn(7).setPreferredWidth(100);//amt
        tblSale.getColumnModel().getColumn(0).setCellEditor(new BatchCellEditor(inventoryRepo));//
        tblSale.getColumnModel().getColumn(2).setCellEditor(new StockCellEditor(inventoryRepo, ProUtil.isSSContain()));
        tblSale.getColumnModel().getColumn(3).setCellEditor(new StockCellEditor(inventoryRepo, ProUtil.isSSContain()));
        tblSale.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());//qty
        inventoryRepo.getStockUnit().doOnSuccess((t) -> {
            tblSale.getColumnModel().getColumn(5).setCellEditor(new StockUnitEditor(t));
        }).subscribe();
        tblSale.getColumnModel().getColumn(6).setCellEditor(new AutoClearEditor());//
    }

    private void initQtyTable() {
        tblSale.setModel(saleQtyTableModel);
        saleQtyTableModel.setLblRecord(lblRec);
        saleQtyTableModel.setParent(tblSale);
        saleQtyTableModel.setSale(this);
        saleQtyTableModel.addNewRow();
        saleQtyTableModel.setObserver(this);
        saleQtyTableModel.setVouDate(txtSaleDate);
        saleQtyTableModel.setInventoryRepo(inventoryRepo);
        tblSale.getTableHeader().setFont(Global.tblHeaderFont);
        tblSale.setCellSelectionEnabled(true);
        tblSale.getColumnModel().getColumn(0).setPreferredWidth(50);//code
        tblSale.getColumnModel().getColumn(1).setPreferredWidth(300);//name
        tblSale.getColumnModel().getColumn(2).setPreferredWidth(50);//qty
        tblSale.getColumnModel().getColumn(3).setPreferredWidth(20);//unit
        tblSale.getColumnModel().getColumn(4).setPreferredWidth(100);//price
        tblSale.getColumnModel().getColumn(5).setPreferredWidth(120);//amount
        tblSale.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor(inventoryRepo, ProUtil.isSSContain()));//
        tblSale.getColumnModel().getColumn(1).setCellEditor(new StockCellEditor(inventoryRepo, ProUtil.isSSContain()));
        tblSale.getColumnModel().getColumn(2).setCellEditor(new AutoClearEditor());//qty
        inventoryRepo.getStockUnit().doOnSuccess((t) -> {
            tblSale.getColumnModel().getColumn(3).setCellEditor(new StockUnitEditor(t));
        }).subscribe();
        tblSale.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());//
    }

    private void initSaleDesignTable() {
        tblSale.setModel(saleDesginTableModel);
        saleDesginTableModel.setLblRecord(lblRec);
        saleDesginTableModel.setSale(this);
        saleDesginTableModel.setParent(tblSale);
        saleDesginTableModel.addNewRow();
        saleDesginTableModel.setObserver(this);
        saleDesginTableModel.setVouDate(txtSaleDate);
        saleDesginTableModel.setInventoryRepo(inventoryRepo);
        tblSale.getColumnModel().getColumn(0).setPreferredWidth(50);//Code
        tblSale.getColumnModel().getColumn(1).setPreferredWidth(50);//Name
        tblSale.getColumnModel().getColumn(2).setPreferredWidth(60);//length
        tblSale.getColumnModel().getColumn(3).setPreferredWidth(60);//height
        tblSale.getColumnModel().getColumn(4).setPreferredWidth(60);//divider
        tblSale.getColumnModel().getColumn(5).setPreferredWidth(60);//qty
        tblSale.getColumnModel().getColumn(6).setPreferredWidth(60);//total sqft
        tblSale.getColumnModel().getColumn(7).setPreferredWidth(1);//price
        tblSale.getColumnModel().getColumn(8).setPreferredWidth(40);//amt
        tblSale.getColumnModel().getColumn(0).setCellEditor(new DesignEditor(inventoryRepo));
        tblSale.getColumnModel().getColumn(1).setCellEditor(new SizeEditor(inventoryRepo));
        tblSale.getColumnModel().getColumn(2).setCellEditor(new AutoClearEditor());//length
        tblSale.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());//height
        tblSale.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());//divider
        tblSale.getColumnModel().getColumn(6).setCellEditor(new AutoClearEditor());//qty
        tblSale.getColumnModel().getColumn(7).setCellEditor(new AutoClearEditor());//qty

        if (ProUtil.isSalePriceChange()) {
            if (ProUtil.isPriceOption()) {
                tblSale.getColumnModel().getColumn(3).setCellEditor(new SalePriceCellEditor(inventoryRepo));//price
            } else {
                tblSale.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());//price
            }

        } else {
            tblSale.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());//price
        }
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
        batchAutoCompeter = new BatchAutoCompeter(txtBatchNo, inventoryRepo, null, false);
        batchAutoCompeter.setObserver(this);
        monoLoc = inventoryRepo.getLocation();
        saleManCompleter = new SaleManAutoCompleter(txtSaleman, null, false);
        inventoryRepo.getSaleMan().doOnSuccess((t) -> {
            saleManCompleter.setListSaleMan(t);
        }).subscribe();
        locationAutoCompleter = new LocationAutoCompleter(txtLocation, null, false, false);
        locationAutoCompleter.setObserver(this);
        monoLoc.doOnSuccess((t) -> {
            locationAutoCompleter.setListLocation(t);
        }).subscribe();
        currAutoCompleter = new CurrencyAutoCompleter(txtCurrency, null);
        userRepo.getCurrency().doOnSuccess((t) -> {
            currAutoCompleter.setListCurrency(t);
        }).subscribe();
        userRepo.getDefaultCurrency().doOnSuccess((c) -> {
            currAutoCompleter.setCurrency(c);
        }).subscribe();
        carNoAutoCompleter = new CarNoAutoCompleter(txtCarNo, inventoryRepo, null, false, "Sale");
        carNoAutoCompleter.setObserver(this);
        projectAutoCompleter = new ProjectAutoCompleter(txtProjectNo, null, false);
        projectAutoCompleter.setObserver(this);
        userRepo.searchProject().doOnSuccess((t) -> {
            projectAutoCompleter.setListProject(t);
        }).subscribe();
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
        ComponentUtil.setTextProperty(this);
    }

    private void assignDefaultValue() {
        userRepo.getDefaultCurrency().doOnSuccess((t) -> {
            currAutoCompleter.setCurrency(t);
        }).subscribe();
        inventoryRepo.getDefaultLocation().doOnSuccess((tt) -> {
            locationAutoCompleter.setLocation(tt);
        }).subscribe();
        inventoryRepo.getDefaultSaleMan().doOnSuccess((t) -> {
            saleManCompleter.setSaleMan(t);
        }).subscribe();
        inventoryRepo.getDefaultCustomer().doOnSuccess((t) -> {
            traderAutoCompleter.setTrader(t);
        }).subscribe();
        txtDueDate.setDate(null);
        progress.setIndeterminate(false);
        txtCurrency.setEnabled(ProUtil.isMultiCur());
        txtVouNo.setText(null);
        chkPaid.setSelected(ProUtil.isSalePaid());
        chkVou.setSelected(true);
        rdoPay.setSelected(Util1.getBoolean(Global.hmRoleProperty.get(ProUtil.DEFAULT_STOCK_PAY)));
        chkA4.setSelected(Util1.getBoolean(ProUtil.getProperty(ProUtil.CHECK_SALE_A4)));
        chkA5.setSelected(Util1.getBoolean(ProUtil.getProperty(ProUtil.CHECK_SALE_A5)));
        chkBarCode.setSelected(Util1.getBoolean(ProUtil.useBarCode()));
        if (!lblStatus.getText().equals("NEW")) {
            txtSaleDate.setDate(Util1.getTodayDate());
        }
    }

    private void clearList() {
        switch (type) {
            case SALE -> {
                saleTableModel.clear();
            }
            case WEIGHT -> {
                saleWeightTableModel.clear();
            }
            case EXPORT -> {
                saleExportTableModel.clear();
            }
            case RICE -> {
                saleRiceTableModel.clear();
            }
            case PADDY -> {
                salePaddyTableModel.clear();
            }
            case BATCH -> {
                saleByBatchTableModel.clear();
            }
            case QTY -> {
                saleQtyTableModel.clear();
            }
            case DESIGN -> {
                saleDesginTableModel.clear();
            }
        }
    }

    private void addNewRow() {
        switch (type) {
            case SALE -> {
                saleTableModel.addNewRow();
            }
            case WEIGHT -> {
                saleWeightTableModel.addNewRow();
            }
            case EXPORT -> {
                saleExportTableModel.addNewRow();
            }
            case RICE -> {
                saleRiceTableModel.addNewRow();
            }
            case PADDY -> {
                salePaddyTableModel.addNewRow();
            }
            case BATCH -> {
                saleByBatchTableModel.addNewRow();
            }
            case QTY -> {
                saleQtyTableModel.addNewRow();
            }
            case DESIGN -> {
                saleDesginTableModel.addNewRow();
            }
        }
    }

    private void clear(boolean focus) {
        disableForm(true);
        clearList();
        addNewRow();
        clearDiscount();
        clearNote();
        initTextBoxValue();
        assignDefaultValue();
        sh = new SaleHis();
        lblStatus.setText("NEW");
        lblStatus.setForeground(Color.GREEN);
        progress.setIndeterminate(false);
        txtRemark.setText(null);
        txtReference.setText(null);
        carNoAutoCompleter.setAutoText(null);
        projectAutoCompleter.setProject(null);
        progress.setIndeterminate(false);
        if (focus) {
            txtCus.requestFocus();
        }
    }

    private void clearDiscount() {
        if (vouDiscountDialog != null) {
            vouDiscountDialog.clear();
        }
    }

    private void clearNote() {
        if (saleNoteDialog != null) {
            saleNoteDialog.clear();
        }
    }

    private boolean isValidDetail() {
        switch (type) {
            case SALE -> {
                return saleTableModel.isValidEntry();
            }
            case WEIGHT -> {
                return saleWeightTableModel.isValidEntry();
            }
            case EXPORT -> {
                return saleExportTableModel.isValidEntry();
            }
            case RICE -> {
                return saleRiceTableModel.isValidEntry();
            }
            case PADDY -> {
                return salePaddyTableModel.isValidEntry();
            }
            case BATCH -> {
                return saleByBatchTableModel.isValidEntry();
            }
            case QTY -> {
                return saleQtyTableModel.isValidEntry();
            }
            case DESIGN -> {
                return saleDesginTableModel.isValidEntry();
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
            observer.selected("print", false);
            progress.setIndeterminate(true);
            sh.setListSH(getListDetail());
            sh.setListOrder(getOrderList());
            inventoryRepo.save(sh).doOnSuccess((t) -> {
                if (print) {
                    if (!Util1.isNullOrEmpty(t.getWeightVouNo())) {
                        printWeightVoucher(t);
                    } else {
                        printVoucher(t, chkVou.isSelected());
                    }
                } else {
                    clear(true);
                }
            }).doOnError((e) -> {
                progress.setIndeterminate(false);
                observeMain();
                if (e instanceof WebClientRequestException) {
                    int yn = JOptionPane.showConfirmDialog(this, "Internet Offline. Try Again?", "Offline", JOptionPane.YES_OPTION, JOptionPane.ERROR_MESSAGE);
                    if (yn == JOptionPane.YES_OPTION) {
                        saveSale(print);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Error : " + e.getMessage(), "Server Error", JOptionPane.ERROR_MESSAGE);
                }
            }).subscribe();
        }
    }

    private List<String> getOrderList() {
        return orderDialog == null ? null : orderDialog.getListOrder();
    }

    private void printWeightVoucher(SaleHis ph) {
        String vouNo = ph.getWeightVouNo();
        inventoryRepo.getWeightColumn(vouNo).doOnSuccess((t) -> {
            try {
                String reportName = "SaleWeightVoucherMyaTT";
                Map<String, Object> param = getDefaultParam(ph);
                List<SaleHisDetail> listTmp = ph.getListSH();
                if (!listTmp.isEmpty()) {
                    SaleHisDetail pd = listTmp.getFirst();
                    param.put("p_stock_name", pd.getStockName());
                    param.put("p_wet", pd.getWet());
                    param.put("p_rice", pd.getRice());
                    param.put("p_weight", pd.getWeight());
                    param.put("p_qty", pd.getQty());
                    param.put("p_bag", pd.getBag());
                    param.put("p_price", pd.getPrice());
                    param.put("p_amount", pd.getAmount());
                    param.put("p_grand_total", ph.getGrandTotal());
                    param.put("p_paid", ph.getPaid());
                    param.put("p_balance", ph.getBalance());
                }
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

    private Map<String, Object> getDefaultParam(SaleHis p) {
        Map<String, Object> param = new HashMap<>();
        param.put("p_print_date", Util1.getTodayDateTime());
        param.put("p_comp_name", Util1.isNull(Global.department.getTitle(), Global.companyName));
        param.put("p_comp_address", Global.companyAddress);
        param.put("p_comp_phone", Global.companyPhone);
        param.put("p_logo_path", ProUtil.logoPath());
        param.put("p_watermark", ProUtil.waterMark());
        param.put("p_remark", p.getRemark());
        param.put("p_vou_no", p.getKey().getVouNo());
        param.put("p_vou_date", Util1.toDateStr(p.getVouDate(), "dd/MM/yyyy"));
        param.put("p_vou_total", p.getVouTotal());
        param.put("p_exp", Util1.getDouble(p.getExpense()) * -1);
        param.put("p_vou_paid", p.getPaid());
        param.put("p_vou_balance", p.getBalance());
        param.put("SUBREPORT_DIR", "report/");
        param.put("p_sub_report_dir", "report/");
        param.put("p_vou_date", Util1.getDate(p.getVouDate()));
        param.put("p_vou_time", Util1.getTime(p.getVouDate()));
        param.put("p_created_name", Global.hmUser.get(p.getCreatedBy()));
        param.put("p_paid_text", NumberConverter.convertToWords((int) p.getPaid()));
        param.put("p_payment_type", getPaymentType(p));
        param.put("p_opening", sh.getOpening());
        param.put("p_total_balance", sh.getTotalBalance());
        param.put("p_total_payment", sh.getTotalPayment());
        param.put("p_closing", sh.getOutstanding());
        Trader t = traderAutoCompleter.getTrader();
        param.put("cus_name", t.getTraderName());
        if (t != null) {
            param.put("p_trader_name", Util1.isNull(p.getReference(), t.getTraderName()));
            param.put("p_cus_name", t.getTraderName());
            param.put("p_trader_address", t.getAddress());
            param.put("p_trader_phone", t.getPhone());
        }
        return param;
    }

    private String getPaymentType(SaleHis p) {
        double paid = p.getPaid();
        double grandTotal = p.getGrandTotal();
        if (paid == grandTotal) {
            return "Cash Down";
        } else if (paid > 0) {
            return "Partital Cash";
        }
        return "Credit";
    }

    private boolean isValidEntry() {
        boolean status = true;
        if (lblStatus.getText().equals("DELETED")) {
            status = false;
            clear(true);
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
            txtCus.requestFocus();
        } else if (!Util1.isDateBetween(txtSaleDate.getDate())) {
            JOptionPane.showMessageDialog(this, "Invalid Date.",
                    "Validation.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtSaleDate.requestFocus();
        } else {
            Project p = projectAutoCompleter.getProject();
            SaleMan sm = saleManCompleter.getSaleMan();
            sh.setCreditTerm(Util1.toLocalDate(txtDueDate.getDate()));
            sh.setSaleManCode(sm == null ? null : sm.getKey().getSaleManCode());
            String traderCode = traderAutoCompleter.getTrader().getKey().getCode();
            sh.setRemark(txtRemark.getText());
            sh.setReference(txtReference.getText());
            sh.setDiscP(Util1.getDouble(txtVouDiscP.getValue()));
            sh.setDiscount(Util1.getDouble(txtVouDiscount.getValue()));
            sh.setTaxPercent(Util1.getDouble(txtVouTaxP.getValue()));
            sh.setTaxAmt(Util1.getDouble(txtTax.getValue()));
            sh.setPaid(Util1.getDouble(txtVouPaid.getValue()));
            sh.setBalance(Util1.getDouble(txtVouBalance.getValue()));
            sh.setCurCode(currAutoCompleter.getCurrency().getCurCode());
            sh.setDeleted(sh.isDeleted());
            sh.setLocCode(locationAutoCompleter.getLocation().getKey().getLocCode());
            sh.setTraderCode(traderCode);
            sh.setVouTotal(Util1.getDouble(txtVouTotal.getValue()));
            sh.setGrandTotal(Util1.getDouble(txtGrandTotal.getValue()));
            sh.setStatus(lblStatus.getText());
            sh.setVouDate(Util1.convertToLocalDateTime(txtSaleDate.getDate()));
            sh.setMacId(Global.macId);
            sh.setCarNo(txtCarNo.getText());
            sh.setSPay(rdoPay.isSelected());
            sh.setTranSource(type);
            sh.setProjectNo(p == null ? null : p.getKey().getProjectNo());
            //financial
            sh.setCashAcc(Util1.isNull(sh.getCashAcc(), Global.department.getCashAcc()));
            sh.setDeptCode(Util1.isNull(sh.getDeptCode(), Global.department.getDeptCode()));
            if (vouDiscountDialog != null) {
                sh.setListVouDiscount(vouDiscountDialog.getListDetail());
                sh.setListDelVouDiscount(vouDiscountDialog.getListDel());
            }
            if (saleNoteDialog != null) {
                sh.setListSaleNote(saleNoteDialog.getListDetail());
            }
            if (lblStatus.getText().equals("NEW")) {
                SaleHisKey key = new SaleHisKey();
                key.setCompCode(Global.compCode);
                key.setVouNo(null);
                sh.setKey(key);
                sh.setDeptId(Global.deptId);
                sh.setCreatedDate(LocalDateTime.now());
                sh.setCreatedBy(Global.loginUser.getUserCode());
                sh.setSession(Global.sessionId);
            } else {
                sh.setUpdatedBy(Global.loginUser.getUserCode());
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
                    inventoryRepo.delete(sh).doOnSuccess((t) -> {
                        clear(true);
                    }).subscribe();
                }
            }
            case "DELETED" -> {
                int yes_no = JOptionPane.showConfirmDialog(this,
                        "Are you sure to restore?", "Purchase Voucher Restore.", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (yes_no == 0) {
                    sh.setDeleted(false);
                    inventoryRepo.restore(sh).doOnSuccess((t) -> {
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
            case SALE ->
                saleTableModel.delete(row);
            case WEIGHT ->
                saleWeightTableModel.delete(row);
            case EXPORT ->
                saleExportTableModel.delete(row);
            case RICE ->
                saleRiceTableModel.delete(row);
            case PADDY ->
                salePaddyTableModel.delete(row);
            case BATCH ->
                saleByBatchTableModel.delete(row);
            case QTY ->
                saleQtyTableModel.delete(row);
            case DESIGN ->
                saleDesginTableModel.delete(row);
        }
    }

    private void addSale(SaleHisDetail sd) {
        switch (type) {
            case SALE ->
                saleTableModel.addSale(sd);
            case WEIGHT ->
                saleWeightTableModel.addSale(sd);
            case EXPORT ->
                saleExportTableModel.addSale(sd);
            case RICE ->
                saleRiceTableModel.addSale(sd);
            case PADDY ->
                salePaddyTableModel.addSale(sd);
            case BATCH ->
                saleByBatchTableModel.addSale(sd);
            case QTY ->
                saleQtyTableModel.addSale(sd);
            case DESIGN ->
                saleDesginTableModel.addSale(sd);
        }
    }

    private List<SaleHisDetail> getListDetail() {
        switch (type) {
            case SALE -> {
                return saleTableModel.getListDetail();
            }
            case WEIGHT -> {
                return saleWeightTableModel.getListDetail();
            }
            case EXPORT -> {
                return saleExportTableModel.getListDetail();
            }
            case RICE -> {
                return saleRiceTableModel.getListDetail();
            }
            case PADDY -> {
                return salePaddyTableModel.getListDetail();
            }
            case BATCH -> {
                return saleByBatchTableModel.getListDetail();
            }
            case QTY -> {
                return saleQtyTableModel.getListDetail();
            }
            case DESIGN -> {
                return saleDesginTableModel.getListDetail();
            }
            default ->
                throw new AssertionError();
        }
    }

    private void calculateTotalAmount(boolean partial) {
        List<SaleHisDetail> listDetail = getListDetail();
        double totalAmount = listDetail.stream().mapToDouble((value) -> value.getAmount()).sum();
        txtVouTotal.setValue(totalAmount);
        //cal discAmt
        double discp = Util1.getDouble(txtVouDiscP.getValue());
        double disAmt = Util1.getDouble(txtVouDiscount.getValue());
        if (discp > 0) {
            double discountAmt = (totalAmount * (discp / 100));
            txtVouDiscount.setValue(Util1.getDouble(discountAmt));
        } else if (disAmt > 0) {
            double disP = (disAmt / totalAmount) * 100;
            txtVouDiscP.setValue(Util1.getDouble(disP));
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
        double totalVouBalance = grandTotal - paid;
        txtVouBalance.setValue(Util1.getDouble(totalVouBalance));
    }

    public void historySale() {
        if (dialog == null) {
            dialog = new SaleHistoryDialog(Global.parentForm, type);
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
            this.sh = sh;
            setDetail(sh);
        }
    }

    private void setDetail(SaleHis sh) {
        progress.setIndeterminate(true);
        disableForm(false);
        String vouNo = sh.getKey().getVouNo();
        inventoryRepo.getSaleDetail(vouNo).doOnSuccess((t) -> {
            if (t != null) {
                setListDetail(t);
            }
        }).doOnError((e) -> {
            progress.setIndeterminate(false);
            JOptionPane.showMessageDialog(this, e.getMessage());
        }).doOnTerminate(() -> {
            setHeader(sh);
            focusTable();
            progress.setIndeterminate(false);
        }).subscribe();
    }

    private void setHeader(SaleHis sh) {
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
        } else if (sh.isPost()) {
            lblStatus.setText("This Vocher Already Payment.");
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
        txtVouNo.setText(sh.getKey().getVouNo());
        txtDueDate.setDate(Util1.convertToDate(sh.getCreditTerm()));
        txtRemark.setText(sh.getRemark());
        txtReference.setText(sh.getReference());
        txtSaleDate.setDate(Util1.convertToDate(sh.getVouDate()));
        txtVouTotal.setValue(Util1.getDouble(sh.getVouTotal()));
        txtVouDiscP.setValue(Util1.getDouble(sh.getDiscP()));
        txtVouDiscount.setValue(Util1.getDouble(sh.getDiscount()));
        txtVouTaxP.setValue(Util1.getDouble(sh.getTaxPercent()));
        txtTax.setValue(Util1.getDouble(sh.getTaxAmt()));
        txtVouPaid.setValue(Util1.getDouble(sh.getPaid()));
        txtVouBalance.setValue(Util1.getDouble(sh.getBalance()));
        txtGrandTotal.setValue(Util1.getDouble(sh.getGrandTotal()));
        txtCarNo.setText(sh.getCarNo());
        chkPaid.setSelected(sh.getPaid() > 0);
        rdoPay.setSelected(sh.isSPay());
        inventoryRepo.findLocation(sh.getLocCode()).doOnSuccess((t) -> {
            locationAutoCompleter.setLocation(t);
        }).subscribe();
        inventoryRepo.findTrader(sh.getTraderCode()).doOnSuccess((t) -> {
            traderAutoCompleter.setTrader(t);
        }).subscribe();
        userRepo.findCurrency(sh.getCurCode()).doOnSuccess((t) -> {
            currAutoCompleter.setCurrency(t);
        }).subscribe();
        inventoryRepo.findSaleMan(sh.getSaleManCode()).doOnSuccess((t) -> {
            saleManCompleter.setSaleMan(t);
        }).subscribe();
        userRepo.find(sh.getProjectNo()).doOnSuccess(t -> {
            projectAutoCompleter.setProject(t);
        }).subscribe();
    }

    private void setListDetail(List<SaleHisDetail> list) {
        switch (type) {
            case SALE -> {
                saleTableModel.setListDetail(list);
                saleTableModel.addNewRow();
            }
            case WEIGHT -> {
                saleWeightTableModel.setListDetail(list);
                saleWeightTableModel.addNewRow();
            }
            case EXPORT -> {
                saleExportTableModel.setListDetail(list);
                saleExportTableModel.addNewRow();
            }
            case RICE -> {
                saleRiceTableModel.setListDetail(list);
                saleRiceTableModel.addNewRow();
            }
            case PADDY -> {
                salePaddyTableModel.setListDetail(list);
                salePaddyTableModel.addNewRow();
            }
            case BATCH -> {
                saleByBatchTableModel.setListDetail(list);
                saleByBatchTableModel.addNewRow();
            }
            case QTY -> {
                saleQtyTableModel.setListDetail(list);
                saleQtyTableModel.addNewRow();
            }
            case DESIGN -> {
                saleDesginTableModel.setListDetail(list);
                saleDesginTableModel.addNewRow();
            }
        }
    }

    private void disableForm(boolean status) {
        ComponentUtil.enableForm(this, status);
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

    private void printVoucher(SaleHis sh, boolean print) {
        inventoryRepo.getSaleReport(sh.getKey().getVouNo()).doOnSuccess((t) -> {
            if (t != null) {
                viewReport(t, sh, print);
            }
        }).doOnError((e) -> {
            JOptionPane.showMessageDialog(this, e.getMessage());
            progress.setIndeterminate(false);
        }).doOnTerminate(() -> {
            clear(false);
        }).subscribe();
    }

    private void viewReport(List<VSale> list, SaleHis sh, boolean print) {
        try {
            String reportName = getReportName();
            if (reportName != null) {
                Map<String, Object> param = getDefaultParam(sh);
                if (!list.isEmpty()) {
                    VSale sale = list.getFirst();
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode n1 = mapper.readTree(Util1.gson.toJson(sale.getListDiscount()));
                    JsonDataSource d1 = new JsonDataSource(n1, null) {
                    };
                    param.put("p_sub_data", d1);
                }
                String reportPath = ProUtil.getReportPath() + reportName.concat(".jasper");
                ByteArrayInputStream stream = new ByteArrayInputStream(Util1.listToByteArray(list));
                JsonDataSource ds = new JsonDataSource(stream);
                JasperPrint jp = JasperFillManager.fillReport(reportPath, param, ds);
                if (print) {
                    String printerName = ProUtil.getProperty(ProUtil.PRINTER_NAME);
                    int count = Util1.getIntegerOne(ProUtil.getProperty(ProUtil.PRINTER_PAGE));
                    JasperReportUtil.print(jp, printerName, count, 4);
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
        if (type == RICE) {
            return !Util1.isNull(ProUtil.getProperty(ProUtil.SALE_RICE)) ? ProUtil.getProperty(ProUtil.SALE_RICE) : "SaleVoucherA5Bag";
        } else if (type == PADDY) {
            return "SaleVoucherA5Qty";
        } else if (chkVou.isSelected()) {
            return ProUtil.getProperty(ProUtil.SALE_VOU);
        } else if (chkA4.isSelected()) {
            return ProUtil.getProperty(ProUtil.SALE_VOU_A4);
        } else if (chkA5.isSelected()) {
            return ProUtil.getProperty(ProUtil.SALE_VOU_A5);
        }
        return null;
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
        inventoryRepo.findTrader(s.getTraderCode()).doOnSuccess((t) -> {
            traderAutoCompleter.setTrader(t);
        }).subscribe();
        String vouNo = s.getKey().getVouNo();
        inventoryRepo.getTransferDetail(vouNo).subscribe((t) -> {
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
            vouDiscountDialog.setSize(Global.width / 3, Global.height / 3);
            vouDiscountDialog.setLocationRelativeTo(null);
            vouDiscountDialog.setObserver(this);
            vouDiscountDialog.setInventoryRepo(inventoryRepo);
            vouDiscountDialog.initMain();
        }
        String vouNo = txtVouNo.getText();
        vouDiscountDialog.search(vouNo);
    }

    private void optionDialog() {
        Trader trader = traderAutoCompleter.getTrader();
        if (trader != null) {
            if (optionDialog == null) {
                optionDialog = new AccountOptionDialog(Global.parentForm);
                optionDialog.setLocationRelativeTo(null);
                optionDialog.setAccountRepo(accountRepo);
                optionDialog.initMain();
            }
            optionDialog.setObject(sh, trader);
            optionDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Select Trader.");
            txtCus.requestFocus();
        }
    }

    private void weightDialog() {
        if (weightHistoryDialog == null) {
            weightHistoryDialog = new WeightHistoryDialog(Global.parentForm);
            weightHistoryDialog.setOther(true);
            weightHistoryDialog.setInventoryRepo(inventoryRepo);
            weightHistoryDialog.setUserRepo(userRepo);
            weightHistoryDialog.setObserver(this);
            weightHistoryDialog.initMain();
            weightHistoryDialog.setSize(Global.width - 20, Global.height - 20);
            weightHistoryDialog.setLocationRelativeTo(null);
        }
        weightHistoryDialog.search();
    }

    private void setWeightVoucher(WeightHis s) {
        clearList();
        sh.setWeightVouNo(s.getKey().getVouNo());
        inventoryRepo.findTrader(s.getTraderCode()).doOnSuccess((t) -> {
            traderAutoCompleter.setTrader(t);
        }).subscribe();
        txtRemark.setText(s.getRemark());
        txtReference.setText(s.getDescription());
        SaleHisDetail detail = new SaleHisDetail();
        detail.setUserCode(s.getStockUserCode());
        detail.setStockCode(s.getStockCode());
        detail.setStockName(s.getStockName());
        detail.setWeight(s.getTotalWeight());
        detail.setQty(s.getTotalQty());
        detail.setBag(s.getTotalBag());
        detail.setPrice(0.0);
        addSale(detail);
        tblSale.setRowSelectionInterval(0, 0);
        tblSale.setColumnSelectionInterval(2, 2);
        tblSale.requestFocus();
    }

    private void contractDialog() {
        if (contractDialog == null) {
            contractDialog = new ContractDialog(Global.parentForm);
            contractDialog.setSize(Global.width / 2, Global.height / 2);
            contractDialog.setLocationRelativeTo(null);
            contractDialog.initMain();
        }
        contractDialog.search();
    }

    private void paymentDialog() {
        if (isValidEntry()) {
            if (paymentDialog == null) {
                paymentDialog = new PaymentDialog(Global.parentForm);
                paymentDialog.setSize(Global.width / 3, Global.height / 2);
                paymentDialog.setLocationRelativeTo(null);
                paymentDialog.setAccountRepo(accountRepo);
            }
            paymentDialog.setData(sh, lblStatus.getText().equals("NEW"));
        }
    }

    private void orderDialog() {
        String status = btnOrder.getText();
        if (status.equals("Order")) {
            if (orderDialog == null) {
                orderDialog = new OrderHistoryDialog(Global.parentForm);
                orderDialog.setInventoryRepo(inventoryRepo);
                orderDialog.setUserRepo(userRepo);
                orderDialog.setObserver(this);
                orderDialog.setOption(true);
                orderDialog.initMain();
                orderDialog.setSize(Global.width - 20, Global.height - 20);
                orderDialog.setLocationRelativeTo(null);
            }
            orderDialog.search();
            orderDialog.setVisible(true);
        } else if (status.contains("View")) {
            if (orderDetailDialog == null) {
                orderDetailDialog = new OrderDetailDialog(Global.parentForm);
                orderDetailDialog.setSize(Global.width - 30, Global.height / 3);
                orderDetailDialog.setInventoryRepo(inventoryRepo);
                orderDetailDialog.initMain();
                orderDetailDialog.setLocationRelativeTo(null);
            }
        }
    }

    private void batchDialog() {
        String text = btnBatch.getText();
        if (text.equals("Batch")) {
            if (batchDialog == null) {
                batchDialog = new BatchSearchDialog(Global.parentForm);
                batchDialog.setInventoryRepo(inventoryRepo);
                batchDialog.setObserver(this);
                batchDialog.initMain();
                batchDialog.setLocationRelativeTo(null);
            }
            batchDialog.searchBatch();
        } else if (text.contains("View")) {
            if (grnDialog == null) {
                grnDialog = new GRNDetailDialog(Global.parentForm);
                grnDialog.setSize(Global.width - 30, Global.height / 3);
                grnDialog.setInventoryRepo(inventoryRepo);
                grnDialog.initMain();
                grnDialog.setLocationRelativeTo(null);
            }
            grnDialog.searchGRNDetail(txtBatchNo.getText());
        }
    }

    private Mono<List<SaleHisDetail>> getSaleDetailFromOrder() {
        return Flux.fromIterable(orderDialog.getListOrder())
                .flatMap(vouNo -> inventoryRepo.getOrderDetail(vouNo)
                .flatMapMany(Flux::fromIterable)
                .flatMap(od -> {
                    String relCode = od.getRelCode();
                    String compCode = od.getKey().getCompCode();
                    String stockCode = od.getStockCode();
                    String unit = od.getUnitCode();

                    if (!Util1.isNullOrEmpty(relCode)) {
                        StockUnitPriceKey key = new StockUnitPriceKey();
                        key.setCompCode(compCode);
                        key.setStockCode(stockCode);
                        key.setUnit(unit);
                        return inventoryRepo.findStockUnitPrice(key)
                                .flatMap(p -> {
                                    double price = Util1.getDouble(p.getSalePriceN());
                                    if (price == 0) {
                                        return Mono.empty();
                                    } else {
                                        var dto = mapOrderToSale(od);
                                        dto.setPrice(price);
                                        return Mono.just(dto);
                                    }
                                })
                                .switchIfEmpty(inventoryRepo.findStock(stockCode)
                                        .map(t -> {
                                            var dto = mapOrderToSale(od);
                                            dto.setPrice(Util1.getDouble(t.getSalePriceN()));
                                            return dto;
                                        })
                                );
                    } else {
                        return inventoryRepo.findStock(stockCode)
                                .map((t) -> {
                                    var dto = mapOrderToSale(od);
                                    dto.setPrice(Util1.getDouble(t.getSalePriceN()));
                                    return dto;
                                });
                    }
                })).collectList();
    }

    private SaleHisDetail mapOrderToSale(OrderHisDetail od) {
        SaleHisDetail sd = new SaleHisDetail();
        sd.setStockCode(od.getStockCode());
        sd.setUserCode(od.getUserCode());
        sd.setStockName(od.getStockName());
        sd.setTraderName(od.getTraderName());
        sd.setRelName(od.getRelName());
        sd.setWeight(od.getWeight());
        sd.setWeightUnit(od.getWeightUnit());
        sd.setQty(Util1.getDouble(od.getQty()));
        sd.setAmount(Util1.getDouble(od.getAmount()));
        sd.setUnitCode(od.getUnitCode());
        sd.setPrice(Util1.getDouble(od.getPrice()));
        sd.setLocCode(od.getLocCode());
        sd.setLocName(od.getLocName());
        sd.setDesign(od.getDesign());
        sd.setSize(od.getSize());
        return sd;
    }

    private void setSaleVoucherDetail(OrderHis oh) {
        clear(false);
        if (oh != null) {
            progress.setIndeterminate(true);
            getSaleDetailFromOrder().doOnSuccess((t) -> {
                setListDetail(t);
            }).doOnTerminate(() -> {
                addNewRow();
                calculateTotalAmount(false);
                focusTable();
                progress.setIndeterminate(false);
            }).subscribe();
            btnOrder.setBackground(Color.green);
            inventoryRepo.findLocation(oh.getLocCode()).doOnSuccess((t) -> {
                locationAutoCompleter.setLocation(t);
            }).subscribe();
            inventoryRepo.findTrader(oh.getTraderCode()).doOnSuccess((t) -> {
                traderAutoCompleter.setTrader(t);
            }).subscribe();
            userRepo.findCurrency(oh.getCurCode()).doOnSuccess((t) -> {
                currAutoCompleter.setCurrency(t);
            }).subscribe();
            inventoryRepo.findSaleMan(oh.getSaleManCode()).doOnSuccess((t) -> {
                saleManCompleter.setSaleMan(t);
            }).subscribe();
            userRepo.find(oh.getProjectNo()).doOnSuccess(t1 -> {
                projectAutoCompleter.setProject(t1);
            }).subscribe();
            txtDueDate.setDate(Util1.convertToDate(oh.getCreditTerm()));
            txtRemark.setText(oh.getRemark());
            txtReference.setText(oh.getReference());
            txtSaleDate.setDate(Util1.convertToDate(oh.getVouDate()));
            txtVouTotal.setValue(Util1.getDouble(oh.getVouTotal()));
        }
    }

    private void rowUpdate(int row) {
        saleTableModel.fireTableRowsUpdated(row, row);
    }

    private void saleFunctionDialog() {
        int row = tblSale.convertRowIndexToModel(tblSale.getSelectedRow());
        SaleHisDetail detail = getSaleHisDetail(row);
        if (saleFunctionDialog == null) {
            saleFunctionDialog = new SaleFunctionDialog(Global.parentForm);
            saleFunctionDialog.setLocationRelativeTo(null);
        }
        saleFunctionDialog.setSaleDetail(detail);
        saleFunctionDialog.setVisible(true);
        rowUpdate(row);
        calculateTotalAmount(false);
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
        txtCarNo = new javax.swing.JTextField();
        carNoLabel = new javax.swing.JLabel();
        carNoLabel1 = new javax.swing.JLabel();
        txtProjectNo = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        txtVouNo = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        rdoPay = new javax.swing.JRadioButton();
        jLabel11 = new javax.swing.JLabel();
        txtBatchNo = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        lblStatus = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        chkA5 = new javax.swing.JCheckBox();
        chkVou = new javax.swing.JCheckBox();
        chkA4 = new javax.swing.JCheckBox();
        lblRec = new javax.swing.JLabel();
        btnTransfer = new javax.swing.JButton();
        btnWeight = new javax.swing.JButton();
        btnDiscount = new javax.swing.JButton();
        btnOrder = new javax.swing.JButton();
        btnBatch = new javax.swing.JButton();
        deskPane = new javax.swing.JDesktopPane();
        scroll = new javax.swing.JScrollPane();
        tblSale = new javax.swing.JTable();
        btnPayment = new javax.swing.JButton();
        chkBarCode = new javax.swing.JRadioButton();
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

        jButton2.setBackground(Global.selectionColor);
        jButton2.setFont(Global.lableFont);
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("...");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        txtVouNo.setEditable(false);
        txtVouNo.setFont(Global.textFont);
        txtVouNo.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtVouNo.setName("txtCus"); // NOI18N
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

        jButton3.setBackground(Global.selectionColor);
        jButton3.setFont(Global.lableFont);
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Account");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        rdoPay.setFont(Global.lableFont);
        rdoPay.setSelected(true);
        rdoPay.setText("S - Pay");
        rdoPay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoPayActionPerformed(evt);
            }
        });

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Batch No");

        txtBatchNo.setEditable(false);
        txtBatchNo.setFont(Global.textFont);
        txtBatchNo.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtBatchNo.setEnabled(false);
        txtBatchNo.setName("txtCurrency"); // NOI18N
        txtBatchNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBatchNoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelSaleLayout = new javax.swing.GroupLayout(panelSale);
        panelSale.setLayout(panelSaleLayout);
        panelSaleLayout.setHorizontalGroup(
            panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSaleLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCus)
                    .addComponent(txtSaleDate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                    .addComponent(txtVouNo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel22, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtRemark)
                    .addComponent(txtSaleman)
                    .addGroup(panelSaleLayout.createSequentialGroup()
                        .addComponent(txtLocation)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rdoPay, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtDueDate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                    .addComponent(txtCurrency)
                    .addComponent(txtReference))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(carNoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(carNoLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelSaleLayout.createSequentialGroup()
                        .addComponent(txtCarNo)
                        .addGap(6, 6, 6))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelSaleLayout.createSequentialGroup()
                        .addComponent(txtBatchNo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3))
                    .addGroup(panelSaleLayout.createSequentialGroup()
                        .addComponent(txtProjectNo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)))
                .addContainerGap())
        );

        panelSaleLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel5, jLabel6, jLabel9});

        panelSaleLayout.setVerticalGroup(
            panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSaleLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(jLabel22)
                    .addComponent(txtLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCarNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(carNoLabel)
                    .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rdoPay))
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
                        .addComponent(txtProjectNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton2)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtCus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel21)
                        .addComponent(jLabel2))
                    .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtReference, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9))
                    .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel11)
                        .addComponent(txtBatchNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton3))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelSaleLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel3, jLabel5});

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblStatus.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
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

        btnTransfer.setBackground(Global.selectionColor);
        btnTransfer.setFont(Global.lableFont);
        btnTransfer.setForeground(new java.awt.Color(255, 255, 255));
        btnTransfer.setText("Transfer");
        btnTransfer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTransferActionPerformed(evt);
            }
        });

        btnWeight.setBackground(Global.selectionColor);
        btnWeight.setFont(Global.lableFont);
        btnWeight.setForeground(new java.awt.Color(255, 255, 255));
        btnWeight.setText("Weight");
        btnWeight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnWeightActionPerformed(evt);
            }
        });

        btnDiscount.setBackground(Global.selectionColor);
        btnDiscount.setFont(Global.lableFont);
        btnDiscount.setForeground(new java.awt.Color(255, 255, 255));
        btnDiscount.setText("Discount");
        btnDiscount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDiscountActionPerformed(evt);
            }
        });

        btnOrder.setBackground(Global.selectionColor);
        btnOrder.setFont(Global.lableFont);
        btnOrder.setForeground(new java.awt.Color(255, 255, 255));
        btnOrder.setText("Order");
        btnOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOrderActionPerformed(evt);
            }
        });

        btnBatch.setBackground(Global.selectionColor);
        btnBatch.setFont(Global.lableFont);
        btnBatch.setForeground(new java.awt.Color(255, 255, 255));
        btnBatch.setText("Batch");
        btnBatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblRec, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lblStatus, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(10, 10, 10)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnDiscount, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnWeight, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnTransfer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnOrder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnBatch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblStatus)
                            .addComponent(btnBatch)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnTransfer)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnWeight)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDiscount)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnOrder)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addComponent(lblRec)
                .addContainerGap())
        );

        javax.swing.GroupLayout deskPaneLayout = new javax.swing.GroupLayout(deskPane);
        deskPane.setLayout(deskPaneLayout);
        deskPaneLayout.setHorizontalGroup(
            deskPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        deskPaneLayout.setVerticalGroup(
            deskPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

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
        scroll.setViewportView(tblSale);

        btnPayment.setBackground(Global.selectionColor);
        btnPayment.setFont(Global.lableFont);
        btnPayment.setForeground(new java.awt.Color(255, 255, 255));
        btnPayment.setText("Payment");
        btnPayment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPaymentActionPerformed(evt);
            }
        });

        chkBarCode.setText("Barcode");

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

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
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addComponent(jLabel20)))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkPaid)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                            .addComponent(txtVouPaid, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator1)
                            .addComponent(jSeparator2)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtVouBalance)))))
                .addContainerGap())
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel13, jLabel14, jLabel16});

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
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtVouBalance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scroll)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deskPane)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnPayment, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(chkBarCode, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(panelSale, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelSale, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scroll, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(btnPayment)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(chkBarCode))
                        .addComponent(deskPane)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
        if (evt.getClickCount() == 2) {
            saleFunctionDialog();
        }
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

    private void btnTransferActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTransferActionPerformed
        // TODO add your handling code here:
        trasnferDialog();
    }//GEN-LAST:event_btnTransferActionPerformed

    private void btnDiscountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDiscountActionPerformed
        // TODO add your handling code here:
        discountDialog();
    }//GEN-LAST:event_btnDiscountActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        contractDialog();

    }//GEN-LAST:event_jButton2ActionPerformed

    private void btnWeightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnWeightActionPerformed
        // TODO add your handling code here:
        weightDialog();
    }//GEN-LAST:event_btnWeightActionPerformed

    private void txtVouNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVouNoFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVouNoFocusGained

    private void txtVouNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtVouNoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVouNoActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        optionDialog();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void rdoPayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoPayActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rdoPayActionPerformed

    private void btnPaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPaymentActionPerformed
        // TODO add your handling code here:
        paymentDialog();
    }//GEN-LAST:event_btnPaymentActionPerformed

    private void btnOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOrderActionPerformed
        // TODO add your handling code here:
        orderDialog();
    }//GEN-LAST:event_btnOrderActionPerformed

    private void btnBatchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatchActionPerformed
        // TODO add your handling code here:
        batchDialog();
    }//GEN-LAST:event_btnBatchActionPerformed

    private void txtBatchNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBatchNoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBatchNoActionPerformed

    private void chkPaidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkPaidActionPerformed
        // TODO add your handling code here:
        txtVouPaid.setValue(0);
        calculateTotalAmount(false);
    }//GEN-LAST:event_chkPaidActionPerformed

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

    private void txtVouPaidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtVouPaidActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVouPaidActionPerformed

    private void txtVouPaidInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_txtVouPaidInputMethodTextChanged
        // TODO add your handling code here:
        calculateTotalAmount(false);
    }//GEN-LAST:event_txtVouPaidInputMethodTextChanged

    private void txtVouPaidFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVouPaidFocusGained
        // TODO add your handling code here:
        txtVouPaid.selectAll();
    }//GEN-LAST:event_txtVouPaidFocusGained

    private void txtTaxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTaxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTaxActionPerformed

    private void txtVouTaxPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtVouTaxPActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVouTaxPActionPerformed

    private void txtVouDiscountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtVouDiscountActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVouDiscountActionPerformed

    private void txtVouDiscPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtVouDiscPActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVouDiscPActionPerformed

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
            case "TRADER" -> {
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
                    inventoryRepo.findSale(s.getVouNo()).doOnSuccess((t) -> {
                        t.setLocal(local);
                        setSaleVoucher(t);
                    }).subscribe();
                }
            }
            case "TR-HISTORY" -> {
                if (selectObj instanceof VTransfer v) {
                    inventoryRepo.findTransfer(v.getVouNo()).doOnSuccess((t) -> {
                        setTransferVoucher(t, v.isLocal());
                    }).subscribe();
                }
            }
            case "ORDER-HISTORY" -> {
                OrderHis s = (OrderHis) selectObj;
                inventoryRepo.findOrder(s.getKey().getVouNo()).doOnSuccess((t) -> {
                    setSaleVoucherDetail(t);
                }).subscribe();
            }

            case "Select" -> {
                calculateTotalAmount(false);
            }
            case "CAL_DISCOUNT" -> {
                double discount = vouDiscountDialog.getTotal();
                txtVouDiscount.setValue(discount);
                calculateTotalAmount(false);
            }
            case "WEIGHT-HISTORY" -> {
                if (selectObj instanceof WeightHis g) {
                    setWeightVoucher(g);
                }
            }
            case "Batch" -> {
                if (selectObj instanceof GRN g) {
                    inventoryRepo.findTrader(g.getTraderCode()).doOnSuccess((t) -> {
                        traderAutoCompleter.setTrader(t);
                    }).subscribe();
                    inventoryRepo.findGRN(g.getKey().getVouNo()).doOnSuccess((t) -> {
                        batchAutoCompeter.setBatch(t);
                        btnBatch.setText("Batch:View");
                    }).subscribe();
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
    private javax.swing.JButton btnBatch;
    private javax.swing.JButton btnDiscount;
    private javax.swing.JButton btnOrder;
    private javax.swing.JButton btnPayment;
    private javax.swing.JButton btnTransfer;
    private javax.swing.JButton btnWeight;
    private javax.swing.JLabel carNoLabel;
    private javax.swing.JLabel carNoLabel1;
    private javax.swing.JCheckBox chkA4;
    private javax.swing.JCheckBox chkA5;
    private javax.swing.JRadioButton chkBarCode;
    private javax.swing.JCheckBox chkPaid;
    private javax.swing.JCheckBox chkVou;
    private javax.swing.JDesktopPane deskPane;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel11;
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
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblRec;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPanel panelSale;
    private javax.swing.JRadioButton rdoPay;
    private javax.swing.JScrollPane scroll;
    private javax.swing.JTable tblSale;
    private javax.swing.JTextField txtBatchNo;
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
    private javax.swing.JTextField txtVouNo;
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
        boolean yes = ComponentUtil.checkClear(lblStatus.getText());
        if (yes) {
            clear(true);
        }
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
