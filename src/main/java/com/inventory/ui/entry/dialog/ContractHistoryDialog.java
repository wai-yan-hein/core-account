/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry.dialog;

import com.common.ComponentUtil;
import com.common.FilterObject;
import com.common.Global;
import com.common.SelectionObserver;
import com.common.StartWithRowFilter;
import com.common.TableCellRender;
import com.common.Util1;
import com.inventory.editor.StockAutoCompleter;
import com.inventory.editor.TraderAutoCompleter;
import com.inventory.model.Stock;
import com.inventory.model.StockPayment;
import com.inventory.model.Trader;
import com.repo.InventoryRepo;
import com.inventory.ui.entry.dialog.common.StockPaymentSearchTableModel;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author wai yan
 */
@Slf4j
public class ContractHistoryDialog extends javax.swing.JDialog implements KeyListener {

    /**
     * Creates new form SaleVouSearchDialog
     *
     */
    private final StockPaymentSearchTableModel tableModel = new StockPaymentSearchTableModel();
    private InventoryRepo inventoryRepo;
    private TraderAutoCompleter traderAutoCompleter;
    private StockAutoCompleter stockAutoCompleter;
    private SelectionObserver observer;
    private TableRowSorter<TableModel> sorter;
    private StartWithRowFilter tblFilter;
    private String tranOption;

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public ContractHistoryDialog(JFrame frame, String tranOption) {
        super(frame, true);
        this.tranOption = tranOption;
        initComponents();
        initKeyListener();
        initFocus();
        configOption();
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
    }

    public void initMain() {
        initCombo();
        initTableVoucher();
        setTodayDate();
    }

    private void configOption() {
        lblVouNo.setText(tranOption.equals("C") ? "Sale Vou No" : "Purchase Vou No");
    }

    private void initFocus() {
        ComponentUtil.addFocusListener(this);
    }

    private void initCombo() {
        traderAutoCompleter = new TraderAutoCompleter(txtCus, inventoryRepo, null, true, "-");
        stockAutoCompleter = new StockAutoCompleter(txtAccount, inventoryRepo, null, true);
    }

    private void initTableVoucher() {
        tblVoucher.setModel(tableModel);
        tblVoucher.getTableHeader().setFont(Global.tblHeaderFont);
        tblVoucher.getColumnModel().getColumn(0).setPreferredWidth(40);//date
        tblVoucher.getColumnModel().getColumn(1).setPreferredWidth(60);//vou
        tblVoucher.getColumnModel().getColumn(2).setPreferredWidth(80);//con
        tblVoucher.getColumnModel().getColumn(3).setPreferredWidth(180);//cus
        tblVoucher.getColumnModel().getColumn(4).setPreferredWidth(180);//remark
        tblVoucher.getColumnModel().getColumn(5).setPreferredWidth(100);//cre
        tblVoucher.getColumnModel().getColumn(6).setPreferredWidth(100);//cre
        tblVoucher.setDefaultRenderer(Object.class, new TableCellRender());
        tblVoucher.setDefaultRenderer(Double.class, new TableCellRender());
        tblVoucher.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sorter = new TableRowSorter<>(tblVoucher.getModel());
        tblFilter = new StartWithRowFilter(txtFilter);
        tblVoucher.setRowSorter(sorter);
    }

    private void setTodayDate() {
        txtFromDate.setDate(Util1.getTodayDate());
        txtToDate.setDate(Util1.getTodayDate());
    }

    public void search() {
        tableModel.clear();
        txtRecord.setValue(0);
        FilterObject filter = new FilterObject(Global.compCode, Global.deptId);
        filter.setTraderCode(traderAutoCompleter.getTrader().getKey().getCode());
        filter.setFromDate(Util1.toDateStr(txtFromDate.getDate(), "yyyy-MM-dd"));
        filter.setToDate(Util1.toDateStr(txtToDate.getDate(), "yyyy-MM-dd"));
        filter.setVouNo(txtVouNo.getText());
        filter.setSaleVouNo(txtSaleVouNo.getText());
        filter.setRemark(Util1.isNull(txtRemark.getText(), "-"));
        filter.setStockCode(stockAutoCompleter.getStock().getKey().getStockCode());
        filter.setDeleted(chkDel.isSelected());
        filter.setTranOption(tranOption);
        progress.setIndeterminate(true);
        tableModel.clear();
        txtRecord.setValue(0);
        inventoryRepo.getStockPaymentHistory(filter)
                .doOnNext(obj -> btnSearch.setEnabled(false))
                .doOnNext(tableModel::addObject)
                .doOnNext(obj -> calTotal())
                .doOnError(e -> {
                    progress.setIndeterminate(false);
                    btnSearch.setEnabled(true);
                    JOptionPane.showMessageDialog(this, e.getMessage());
                }).doOnTerminate(() -> {
            progress.setIndeterminate(false);
            btnSearch.setEnabled(true);
            tblVoucher.requestFocus();
            setVisible(true);
        }).subscribe();

    }

    private void calTotal() {
        txtQty.setValue(tableModel.getQty());
        txtBag.setValue(tableModel.getBag());
        txtRecord.setValue(tableModel.getSize());
    }

    private void select() {
        int row = tblVoucher.convertRowIndexToModel(tblVoucher.getSelectedRow());
        if (row >= 0) {
            StockPayment his = tableModel.getSelectVou(row);
            observer.selected("PAYMENT_HISTORY", his);
            setVisible(false);
        } else {
            JOptionPane.showMessageDialog(this, "Please select the voucher.",
                    "No Voucher Selected", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initKeyListener() {
        txtFromDate.getDateEditor().getUiComponent().setName("txtFromDate");
        txtFromDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtToDate.getDateEditor().getUiComponent().setName("txtToDate");
        txtToDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtVouNo.addKeyListener(this);
        txtCus.addKeyListener(this);
    }

    private void clearFilter() {
        txtFromDate.setDate(Util1.getTodayDate());
        txtToDate.setDate(Util1.getTodayDate());
        txtVouNo.setText(null);
        txtRemark.setText(null);
        traderAutoCompleter.setTrader(new Trader("-", "All"));
        stockAutoCompleter.setStock(new Stock("-", "All"));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtFromDate = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        txtToDate = new com.toedter.calendar.JDateChooser();
        txtCus = new javax.swing.JTextField();
        txtVouNo = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtAccount = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        chkDel = new javax.swing.JCheckBox();
        jLabel7 = new javax.swing.JLabel();
        txtProjectNo = new javax.swing.JTextField();
        lblVouNo = new javax.swing.JLabel();
        txtSaleVouNo = new javax.swing.JTextField();
        txtFilter = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblVoucher = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        btnSelect = new javax.swing.JButton();
        btnSearch = new javax.swing.JButton();
        lblTtlRecord = new javax.swing.JLabel();
        txtRecord = new javax.swing.JFormattedTextField();
        txtQty = new javax.swing.JFormattedTextField();
        lblTtlRecord1 = new javax.swing.JLabel();
        lblTtlRecord2 = new javax.swing.JLabel();
        txtBag = new javax.swing.JFormattedTextField();
        progress = new javax.swing.JProgressBar();

        setTitle("Payment History Dialog");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Trader");

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Vou No");

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Date");

        txtFromDate.setToolTipText("");
        txtFromDate.setDateFormatString("dd/MM/yyyy");
        txtFromDate.setFont(Global.lableFont);

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("To");

        txtToDate.setToolTipText("");
        txtToDate.setDateFormatString("dd/MM/yyyy");
        txtToDate.setFont(Global.lableFont);

        txtCus.setFont(Global.textFont);
        txtCus.setName("txtCus"); // NOI18N
        txtCus.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCusFocusGained(evt);
            }
        });

        txtVouNo.setFont(Global.textFont);
        txtVouNo.setName("txtVouNo"); // NOI18N
        txtVouNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtVouNoFocusGained(evt);
            }
        });

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Remark");

        txtRemark.setFont(Global.textFont);
        txtRemark.setName("txtVouNo"); // NOI18N
        txtRemark.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtRemarkFocusGained(evt);
            }
        });

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Cash");

        txtAccount.setFont(Global.textFont);
        txtAccount.setName("txtCus"); // NOI18N
        txtAccount.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtAccountFocusGained(evt);
            }
        });

        jButton1.setFont(Global.lableFont);
        jButton1.setText("Clear Filter");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        chkDel.setFont(Global.lableFont);
        chkDel.setText("Deleted");

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Project No");

        txtProjectNo.setFont(Global.textFont);
        txtProjectNo.setName("txtCus"); // NOI18N
        txtProjectNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtProjectNoFocusGained(evt);
            }
        });

        lblVouNo.setFont(Global.lableFont);
        lblVouNo.setText("Sale Vou No");

        txtSaleVouNo.setFont(Global.textFont);
        txtSaleVouNo.setName("txtVouNo"); // NOI18N
        txtSaleVouNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSaleVouNoFocusGained(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblVouNo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(0, 7, Short.MAX_VALUE))
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(txtFromDate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                                .addComponent(txtToDate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
                                .addComponent(txtVouNo, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtSaleVouNo, javax.swing.GroupLayout.Alignment.LEADING))
                            .addComponent(chkDel)
                            .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCus, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtAccount, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtProjectNo, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtFromDate, txtToDate});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel11, jLabel2, jLabel4});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel11)
                    .addComponent(txtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtToDate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblVouNo)
                    .addComponent(txtSaleVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtCus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtAccount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtProjectNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkDel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel11, jLabel3, txtFromDate, txtToDate});

        txtFilter.setFont(Global.textFont);
        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });

        tblVoucher.setFont(Global.textFont);
        tblVoucher.setModel(new javax.swing.table.DefaultTableModel(
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
        tblVoucher.setRowHeight(Global.tblRowHeight);
        tblVoucher.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblVoucherMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblVoucher);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Search Bar");

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        btnSelect.setFont(Global.lableFont);
        btnSelect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/checked_20px.png"))); // NOI18N
        btnSelect.setText("Select");
        btnSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectActionPerformed(evt);
            }
        });

        btnSearch.setFont(Global.lableFont);
        btnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/search.png"))); // NOI18N
        btnSearch.setText("Search");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        lblTtlRecord.setFont(Global.lableFont);
        lblTtlRecord.setText("Total Record :");

        txtRecord.setEditable(false);
        txtRecord.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtRecord.setFont(Global.amtFont);

        txtQty.setEditable(false);
        txtQty.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtQty.setFont(Global.amtFont);

        lblTtlRecord1.setFont(Global.lableFont);
        lblTtlRecord1.setText("Total Qty :");

        lblTtlRecord2.setFont(Global.lableFont);
        lblTtlRecord2.setText("Total Bag :");

        txtBag.setEditable(false);
        txtBag.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtBag.setFont(Global.amtFont);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTtlRecord)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtRecord, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTtlRecord1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtQty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTtlRecord2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtBag, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSearch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSelect)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTtlRecord1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtQty)
                    .addComponent(lblTtlRecord, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtRecord)
                    .addComponent(btnSelect, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(lblTtlRecord2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtBag))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnSearch, btnSelect, lblTtlRecord, txtRecord});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(progress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 704, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtFilter))
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(progress, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        search();

    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectActionPerformed
        select();
    }//GEN-LAST:event_btnSelectActionPerformed

    private void txtCusFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCusFocusGained
        // TODO add your handling code here:
        txtCus.selectAll();
    }//GEN-LAST:event_txtCusFocusGained

    private void txtVouNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVouNoFocusGained
        // TODO add your handling code here:
        txtVouNo.selectAll();
    }//GEN-LAST:event_txtVouNoFocusGained

    private void txtRemarkFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRemarkFocusGained
        // TODO add your handling code here:
        txtRemark.selectAll();
    }//GEN-LAST:event_txtRemarkFocusGained

    private void txtAccountFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAccountFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAccountFocusGained

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        clearFilter();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void txtFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyReleased
        // TODO add your handling code here:
        if (txtFilter.getText().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(tblFilter);
        }
    }//GEN-LAST:event_txtFilterKeyReleased

    private void tblVoucherMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVoucherMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() > 1) {
            select();
        }
    }//GEN-LAST:event_tblVoucherMouseClicked

    private void txtProjectNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtProjectNoFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtProjectNoFocusGained

    private void txtSaleVouNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSaleVouNoFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSaleVouNoFocusGained

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnSelect;
    private javax.swing.JCheckBox chkDel;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblTtlRecord;
    private javax.swing.JLabel lblTtlRecord1;
    private javax.swing.JLabel lblTtlRecord2;
    private javax.swing.JLabel lblVouNo;
    private javax.swing.JProgressBar progress;
    private javax.swing.JTable tblVoucher;
    private javax.swing.JTextField txtAccount;
    private javax.swing.JFormattedTextField txtBag;
    private javax.swing.JTextField txtCus;
    private javax.swing.JTextField txtFilter;
    private com.toedter.calendar.JDateChooser txtFromDate;
    private javax.swing.JTextField txtProjectNo;
    private javax.swing.JFormattedTextField txtQty;
    private javax.swing.JFormattedTextField txtRecord;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JTextField txtSaleVouNo;
    private com.toedter.calendar.JDateChooser txtToDate;
    private javax.swing.JTextField txtVouNo;
    // End of variables declaration//GEN-END:variables

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}