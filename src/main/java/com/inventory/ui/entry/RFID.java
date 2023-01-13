/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry;

import com.acc.common.AccountRepo;
import com.common.DecimalFormatRender;
import com.common.Global;
import com.common.JasperReportUtil;
import com.common.KeyPropagate;
import com.common.PanelControl;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.editor.CurrencyAutoCompleter;
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.editor.LocationCellEditor;
import com.inventory.editor.SaleManAutoCompleter;
import com.inventory.editor.SalePriceCellEditor;
import com.inventory.editor.StockCellEditor;
import com.inventory.editor.TraderAutoCompleter;
import com.inventory.model.Location;
import com.inventory.model.Region;
import com.inventory.model.SaleHis;
import com.inventory.model.SaleHisDetail;
import com.inventory.model.SaleHisKey;
import com.inventory.model.Trader;
import com.inventory.model.VSale;
import com.inventory.ui.common.InventoryRepo;
import com.inventory.ui.common.SaleTableModel;
import com.inventory.ui.common.StockBalanceTableModel;
import com.inventory.ui.entry.dialog.SaleVouSearchDailog;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.inventory.ui.setup.dialog.common.StockUnitEditor;
import com.toedter.calendar.JTextFieldDateEditor;
import com.user.common.UserRepo;
import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.view.JasperViewer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 *
 * @author wai yan
 */
@Component
@Slf4j
public class RFID extends javax.swing.JPanel implements SelectionObserver, KeyListener, KeyPropagate, PanelControl {

    private List<SaleHisDetail> listDetail = new ArrayList();
    private final SaleTableModel saleTableModel = new SaleTableModel();
    private final SaleVouSearchDailog vouSearchDialog = new SaleVouSearchDailog(Global.parentForm);
    @Autowired
    private WebClient inventoryApi;
    @Autowired
    private InventoryRepo inventoryRepo;
    @Autowired
    private UserRepo userRepo;
    private TraderAutoCompleter traderAutoCompleter;
    private SelectionObserver observer;
    private SaleHis saleHis = new SaleHis();
    private Region region;
    private String orderCode;
    private JProgressBar progress;
    private List<Location> listLocation = new ArrayList<>();

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
    }
    private final FocusAdapter fa = new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            ((JTextFieldDateEditor) e.getSource()).selectAll();
        }

    };

    public void initMain() {
        initCombo();
        initSaleTable();
        assignDefaultValue();
        txtSaleDate.setDate(Util1.getTodayDate());
    }

    private void initSaleTable() {
        tblSale.setModel(saleTableModel);
        saleTableModel.setParent(tblSale);
        saleTableModel.setTraderAutoCompleter(traderAutoCompleter);
        saleTableModel.addNewRow();
        saleTableModel.setSelectionObserver(this);
        saleTableModel.setVouDate(txtSaleDate);
        saleTableModel.setInventoryRepo(inventoryRepo);
        tblSale.getTableHeader().setFont(Global.tblHeaderFont);
        tblSale.setCellSelectionEnabled(true);
        tblSale.getColumnModel().getColumn(0).setPreferredWidth(50);//Code
        tblSale.getColumnModel().getColumn(1).setPreferredWidth(450);//Name
        tblSale.getColumnModel().getColumn(2).setPreferredWidth(60);//Rel
        tblSale.getColumnModel().getColumn(3).setPreferredWidth(60);//Location
        tblSale.getColumnModel().getColumn(4).setPreferredWidth(60);//qty
        tblSale.getColumnModel().getColumn(5).setPreferredWidth(1);//unit
        tblSale.getColumnModel().getColumn(6).setPreferredWidth(1);//price
        tblSale.getColumnModel().getColumn(7).setPreferredWidth(40);//amt
        tblSale.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor(inventoryRepo));
        tblSale.getColumnModel().getColumn(1).setCellEditor(new StockCellEditor(inventoryRepo));
        tblSale.getColumnModel().getColumn(3).setCellEditor(new LocationCellEditor(listLocation));
        tblSale.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());//qty
        tblSale.getColumnModel().getColumn(5).setCellEditor(new StockUnitEditor(inventoryRepo.getStockUnit()));
        tblSale.getColumnModel().getColumn(6).setCellEditor(new AutoClearEditor());//
        if (ProUtil.isSalePriceChange()) {
            if (ProUtil.isPriceOption()) {
                SalePriceCellEditor editor = new SalePriceCellEditor(inventoryRepo.getPriceOption("Sale"));
                editor.setSaleTableModel(saleTableModel);
                tblSale.getColumnModel().getColumn(6).setCellEditor(editor);//price
            } else {
                tblSale.getColumnModel().getColumn(6).setCellEditor(new AutoClearEditor());//price
            }

        } else {
            tblSale.getColumnModel().getColumn(6).setCellEditor(new AutoClearEditor());//price
        }
        tblSale.setDefaultRenderer(Object.class, new DecimalFormatRender());
        tblSale.setDefaultRenderer(Float.class, new DecimalFormatRender());
        tblSale.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblSale.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void initCombo() {
        listLocation = inventoryRepo.getLocation();
        traderAutoCompleter = new TraderAutoCompleter(txtCus, inventoryRepo, null, false, "CUS");
        traderAutoCompleter.setObserver(this);
    }

    private void initKeyListener() {
        txtSaleDate.getDateEditor().getUiComponent().setName("txtSaleDate");
        txtSaleDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtVouNo.addKeyListener(this);
        txtRemark.addKeyListener(this);
        txtCus.addKeyListener(this);
        tblSale.addKeyListener(this);
    }

    private void initTextBoxValue() {
        txtVouTotal.setValue(0);
    }

    private void initTextBoxFormat() {
        txtVouTotal.setFormatterFactory(Util1.getDecimalFormat());
    }

    private void assignDefaultValue() {
        traderAutoCompleter.setTrader(inventoryRepo.getDefaultCustomer());
        progress.setIndeterminate(false);
        txtVouNo.setText(null);
    }

    private void clear() {
        disableForm(true);
        saleTableModel.removeListDetail();
        saleTableModel.clearDelList();
        saleTableModel.setChange(false);
        initTextBoxValue();
        assignDefaultValue();
        saleHis = new SaleHis();
        if (!lblStatus.getText().equals("NEW")) {
            txtSaleDate.setDate(Util1.getTodayDate());
        }
        lblStatus.setText("NEW");
        lblStatus.setForeground(Color.GREEN);
        progress.setIndeterminate(false);
        txtRemark.setText(null);
        focusTable();
    }

    public void saveSale(boolean print) {
        try {
            if (isValidEntry() && saleTableModel.isValidEntry()) {
                progress.setIndeterminate(true);
                saleHis.setListSH(saleTableModel.getListDetail());
                saleHis.setListDel(saleTableModel.getDelList());
                saleHis.setBackup(saleTableModel.isChange());
                Mono<SaleHis> result = inventoryApi.post()
                        .uri("/sale/save-sale")
                        .body(Mono.just(saleHis), SaleHis.class)
                        .retrieve()
                        .bodyToMono(SaleHis.class);
                SaleHis t = result.block();

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
        } else {
            saleHis.setRemark(txtRemark.getText());
            saleHis.setDeleted(Util1.getNullTo(saleHis.getDeleted()));
            saleHis.setOrderCode(orderCode);
            saleHis.setRegion(region);
            saleHis.setTraderCode(traderAutoCompleter.getTrader().getKey().getCode());
            saleHis.setVouTotal(Util1.getFloat(txtVouTotal.getValue()));
            saleHis.setStatus(lblStatus.getText());
            saleHis.setVouDate(txtSaleDate.getDate());
            saleHis.setMacId(Global.macId);
            if (lblStatus.getText().equals("NEW")) {
                SaleHisKey key = new SaleHisKey();
                key.setCompCode(Global.compCode);
                key.setDeptId(Global.deptId);
                key.setVouNo(null);
                saleHis.setKey(key);
                saleHis.setCreatedDate(Util1.getTodayDate());
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
                        "Are you sure to delete?", "Save Voucher Delete.", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                if (yes_no == 0) {
                    inventoryRepo.delete(saleHis.getKey());
                    clear();
                }
            }
            case "DELETED" -> {
                int yes_no = JOptionPane.showConfirmDialog(this,
                        "Are you sure to restore?", "Purchase Voucher Restore.", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (yes_no == 0) {
                    saleHis.setDeleted(false);
                    inventoryRepo.restore(saleHis.getKey());
                    lblStatus.setText("EDIT");
                    lblStatus.setForeground(Color.blue);
                    disableForm(true);

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
                saleTableModel.delete(row);
                calculateTotalAmount();
            }
        }
    }

    private void calculateTotalAmount() {
        float totalAmount = 0.0f;
        listDetail = saleTableModel.getListDetail();
        totalAmount = listDetail.stream().map(sdh -> Util1.getFloat(sdh.getAmount())).reduce(totalAmount, (accumulator, _item) -> accumulator + _item);
        txtVouTotal.setValue(totalAmount);
    }

    public void historySale() {
        vouSearchDialog.setInventoryApi(inventoryApi);
        vouSearchDialog.setInventoryRepo(inventoryRepo);
        vouSearchDialog.setUserRepo(userRepo);
        vouSearchDialog.setIconImage(new ImageIcon(getClass().getResource("/images/search.png")).getImage());
        vouSearchDialog.setObserver(this);
        vouSearchDialog.initMain();
        vouSearchDialog.setSize(Global.width - 100, Global.height - 100);
        vouSearchDialog.setLocationRelativeTo(null);
        vouSearchDialog.setVisible(true);
    }

    public void setSaleVoucher(SaleHis sh) {
        if (sh != null) {
            progress.setIndeterminate(true);
            saleHis = sh;
            Integer deptId = sh.getKey().getDeptId();
            traderAutoCompleter.setTrader(inventoryRepo.findTrader(saleHis.getTraderCode(), deptId));
            String vouNo = sh.getKey().getVouNo();
            Mono<ResponseEntity<List<SaleHisDetail>>> result = inventoryApi.get()
                    .uri(builder -> builder.path("/sale/get-sale-detail")
                    .queryParam("vouNo", vouNo)
                    .queryParam("compCode", Global.compCode)
                    .queryParam("deptId", sh.getKey().getDeptId())
                    .build())
                    .retrieve().toEntityList(SaleHisDetail.class);
            result.subscribe((t) -> {
                saleTableModel.setListDetail(t.getBody());
                saleTableModel.addNewRow();
                if (sh.isVouLock()) {
                    lblStatus.setText("Voucher is locked.");
                    lblStatus.setForeground(Color.RED);
                    disableForm(false);
                } else if (!ProUtil.isSaleEdit()) {
                    lblStatus.setText("No Permission.");
                    lblStatus.setForeground(Color.RED);
                    disableForm(false);
                } else if (Util1.getBoolean(sh.getDeleted())) {
                    lblStatus.setText("DELETED");
                    lblStatus.setForeground(Color.RED);
                    disableForm(false);
                } else {
                    lblStatus.setText("EDIT");
                    lblStatus.setForeground(Color.blue);
                    disableForm(true);
                }
                txtVouNo.setText(saleHis.getKey().getVouNo());
                txtRemark.setText(saleHis.getRemark());
                txtSaleDate.setDate(saleHis.getVouDate());
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
        tblSale.setEnabled(status);
        panelSale.setEnabled(status);
        txtSaleDate.setEnabled(status);
        txtCus.setEnabled(status);
        txtRemark.setEnabled(status);
        observer.selected("save", status);
        observer.selected("delete", status);
        observer.selected("print", status);

    }

    private void printVoucher(String vouNo, String reportName, boolean print) {
        clear();
        Mono<byte[]> result = inventoryApi.get()
                .uri(builder -> builder.path("/report/get-sale-report")
                .queryParam("vouNo", vouNo)
                .queryParam("macId", Global.macId)
                .build())
                .retrieve()
                .bodyToMono(ByteArrayResource.class)
                .map(ByteArrayResource::getByteArray);
        result.subscribe((t) -> {
            try {
                if (t != null) {
                    viewReport(t, reportName, print);
                }
            } catch (JRException ex) {
                log.error("printVoucher : " + ex.getMessage());
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }, (e) -> {
            JOptionPane.showMessageDialog(this, e.getMessage());
        });

    }

    private void viewReport(byte[] t, String reportName, boolean print) throws JRException {
        if (reportName != null) {
            String logoPath = String.format("images%s%s", File.separator, ProUtil.getProperty("logo.name"));
            Map<String, Object> param = new HashMap<>();
            param.put("p_print_date", Util1.getTodayDateTime());
            param.put("p_comp_name", Global.companyName);
            param.put("p_comp_address", Global.companyAddress);
            param.put("p_comp_phone", Global.companyPhone);
            param.put("p_logo_path", logoPath);
            String reportPath = ProUtil.getReportPath() + reportName.concat(".jasper");
            ByteArrayInputStream stream = new ByteArrayInputStream(t);
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
        }
    }

    private void focusTable() {
        int rc = tblSale.getRowCount();
        if (rc > 1) {
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
        txtRemark = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtVouTotal = new javax.swing.JFormattedTextField();
        lblStatus = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSale = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();

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
        jLabel21.setText("Remark");

        txtRemark.setFont(Global.textFont);
        txtRemark.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtRemark.setName("txtRemark"); // NOI18N
        txtRemark.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtRemarkFocusGained(evt);
            }
        });

        javax.swing.GroupLayout panelSaleLayout = new javax.swing.GroupLayout(panelSale);
        panelSale.setLayout(panelSaleLayout);
        panelSaleLayout.setHorizontalGroup(
            panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSaleLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCus)
                    .addComponent(txtVouNo, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelSaleLayout.createSequentialGroup()
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtRemark))
                    .addGroup(panelSaleLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtSaleDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        panelSaleLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel17, jLabel2, jLabel21, jLabel4});

        panelSaleLayout.setVerticalGroup(
            panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSaleLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel17)
                        .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtSaleDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21)
                    .addComponent(txtCus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
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

        jLabel18.setFont(Global.lableFont);
        jLabel18.setText("RFID ");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE)
                        .addGap(476, 476, 476)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtVouTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelSale, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelSale, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
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
        focusTable();
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

    private void tblSaleMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSaleMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tblSaleMouseClicked

    private void tblSaleKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblSaleKeyReleased
        // TODO add your handling code here:

    }//GEN-LAST:event_tblSaleKeyReleased

    private void formPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_formPropertyChange
        // TODO add your handling code here:
        log.info("change.");
    }//GEN-LAST:event_formPropertyChange

    private void txtSaleDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSaleDateFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSaleDateFocusGained

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
            case "SALE-HISTORY" -> {
                if (selectObj instanceof VSale s) {
                    SaleHis sh = inventoryRepo.findSale(s.getVouNo(), s.getDeptId());
                    setSaleVoucher(sh);
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

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPanel panelSale;
    private javax.swing.JTable tblSale;
    private javax.swing.JTextField txtCus;
    private javax.swing.JTextField txtRemark;
    private com.toedter.calendar.JDateChooser txtSaleDate;
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
    }

    @Override
    public String panelName() {
        return this.getName();
    }
}
