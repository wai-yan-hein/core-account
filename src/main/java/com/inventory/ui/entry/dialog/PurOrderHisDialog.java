/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry.dialog;
import com.common.ReportFilter;
import com.common.Global;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.common.StartWithRowFilter;
import com.common.TableCellRender;
import com.repo.UserRepo;
import com.common.Util1;
import com.inventory.editor.AppUserAutoCompleter;
import com.inventory.editor.StockAutoCompleter;
import com.inventory.editor.TraderAutoCompleter;
import com.user.model.AppUser;
import com.inventory.entity.Stock;
import com.inventory.entity.Trader;
import com.inventory.entity.VPurOrder;
import com.inventory.ui.entry.dialog.common.PurOrderHisVouSearchTableModel;
import com.repo.InventoryRepo;
import com.user.editor.DepartmentUserAutoCompleter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author pann
 */
@Slf4j
public class PurOrderHisDialog extends javax.swing.JDialog implements KeyListener {

    /**
     * Creates new form PurchaseOrderHistory
     */
    private final PurOrderHisVouSearchTableModel tableModel = new PurOrderHisVouSearchTableModel();
    private UserRepo userRepo;
    private InventoryRepo inventoryRepo;
    private AppUserAutoCompleter appUserAutoCompleter;
    private StockAutoCompleter stockAutoCompleter;
    private DepartmentUserAutoCompleter departmentAutoCompleter;
    private SelectionObserver observer;
    private TableRowSorter<TableModel> sorter;
    private StartWithRowFilter tblFilter;
//    private LocationAutoCompleter locationAutoCompleter;
    private TraderAutoCompleter traderAutoCompleter;
  
    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public PurOrderHisDialog(JFrame frame) {
        super(frame, true);       
        initComponents();
        initKeyListener();
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
    }    
    public void initMain() {
        initTableVoucher();
        setTodayDate();
        initCombo();
    }

    private void initCombo() {       
        appUserAutoCompleter = new AppUserAutoCompleter(txtUser, null, true);
        userRepo.getAppUser().doOnSuccess((t) -> {
            appUserAutoCompleter.setListUser(t);
        }).subscribe();
        departmentAutoCompleter = new DepartmentUserAutoCompleter(txtDep, null, true);
        userRepo.getDeparment(true).doOnSuccess((t) -> {
            departmentAutoCompleter.setListDepartment(t);
        }).subscribe();
        userRepo.findDepartment(Global.deptId).doOnSuccess((t) -> {
            departmentAutoCompleter.setDepartment(t);
        }).subscribe();
        traderAutoCompleter = new TraderAutoCompleter(txtCustomer, inventoryRepo, null, true, "CUS");
        stockAutoCompleter = new StockAutoCompleter(txtStock, inventoryRepo, null, true, ProUtil.isSSContain());
    }

    private void initTableVoucher() {
        tblVoucher.setModel(tableModel);
        tblVoucher.getTableHeader().setFont(Global.tblHeaderFont);
        tblVoucher.getColumnModel().getColumn(0).setPreferredWidth(40);
        tblVoucher.getColumnModel().getColumn(1).setPreferredWidth(100);
        tblVoucher.getColumnModel().getColumn(2).setPreferredWidth(40);
        tblVoucher.getColumnModel().getColumn(3).setPreferredWidth(50);
        tblVoucher.getColumnModel().getColumn(4).setPreferredWidth(100);
        tblVoucher.getColumnModel().getColumn(5).setPreferredWidth(100);   
        tblVoucher.setDefaultRenderer(Object.class, new TableCellRender());
        tblVoucher.setDefaultRenderer(Float.class, new TableCellRender());
        tblVoucher.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sorter = new TableRowSorter<>(tblVoucher.getModel());
        tblFilter = new StartWithRowFilter(txtFilter);
        tblVoucher.setRowSorter(sorter);
    }

    private void setTodayDate() {
        if (txtFromDate.getDate() == null) {
            txtFromDate.setDate(Util1.getTodayDate());
            txtToDate.setDate(Util1.getTodayDate());
        }
    }

    private String getUserCode() {
        return appUserAutoCompleter.getAppUser() == null ? "-" : appUserAutoCompleter.getAppUser().getUserCode();
    }

//    private String getLocCode() {
//        return locationAutoCompleter.getLocation() == null ? "-" : locationAutoCompleter.getLocation().getKey().getLocCode();
//    }

    private Integer getDepId() {
        return departmentAutoCompleter.getDepartment() == null ? 0 : departmentAutoCompleter.getDepartment().getKey().getDeptId();
    }

    public void search() {
        progess.setIndeterminate(true);
        ReportFilter filter = new ReportFilter(Global.macId,Global.compCode, Global.deptId);
        filter.setFromDate(Util1.toDateStr(txtFromDate.getDate(), "yyyy-MM-dd"));
        filter.setToDate(Util1.toDateStr(txtToDate.getDate(), "yyyy-MM-dd"));
        filter.setUserCode(getUserCode());
        filter.setVouNo(txtVouNo.getText());
        filter.setRemark(Util1.isNull(txtRemark.getText(), "-"));
        filter.setStockCode(stockAutoCompleter.getStock().getKey().getStockCode());
//        filter.setLocCode(getLocCode());
        filter.setDeleted(chkDel.isSelected());
        filter.setDeptId(getDepId());
        filter.setLocal(chkLocal.isSelected());     
        if (traderAutoCompleter.getTrader() != null) {
            filter.setTraderCode(traderAutoCompleter.getTrader().getKey().getCode());
        }
        inventoryRepo.getPurOrderHistory(filter).doOnSuccess((t) -> {
            tableModel.setListDetail(t);
            calAmount();
            progess.setIndeterminate(false);
            log.info("purchase order test", t);
        }).doOnError((e) -> {
            JOptionPane.showMessageDialog(this, e.getMessage());
            progess.setIndeterminate(false);
        }).doOnTerminate(() -> {
            setVisible(true);
//            progess.setIndeterminate(false);
        }).subscribe();
    }

    private void calAmount() {
        List<VPurOrder> list = tableModel.getListDetail();
        txtTotalRecord.setValue(list.size());
        tblVoucher.requestFocus();
    }

    private void select() {
        int row = tblVoucher.convertRowIndexToModel(tblVoucher.getSelectedRow());
        if (row >= 0) {
            VPurOrder purOrder = tableModel.getSelectVou(row);
            purOrder.setLocal(chkLocal.isSelected());
            observer.selected("PURORDER-HISTORY", purOrder);
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
        txtUser.addKeyListener(this);
    }

    private void clearFilter() {
        txtFromDate.setDate(Util1.getTodayDate());
        txtVouNo.setText(null);
        txtRemark.setText(null);
        stockAutoCompleter.setStock(new Stock("-", "All"));
//        locationAutoCompleter.setLocation(new Location("-", "All"));
        appUserAutoCompleter.setAppUser(new AppUser("-", "All"));
        traderAutoCompleter.setTrader(new Trader("-", "All"));
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
        lblVouNo = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtFromDate = new com.toedter.calendar.JDateChooser();
        txtVouNo = new javax.swing.JTextField();
        txtUser = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel7 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtStock = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        chkDel = new javax.swing.JCheckBox();
        jLabel13 = new javax.swing.JLabel();
        txtDep = new javax.swing.JTextField();
        chkLocal = new javax.swing.JCheckBox();
        jLabel14 = new javax.swing.JLabel();
        txtCustomer = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtToDate = new com.toedter.calendar.JDateChooser();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblVoucher = new javax.swing.JTable();
        progess = new javax.swing.JProgressBar();
        txtFilter = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        lblTtlAmount = new javax.swing.JLabel();
        lblTtlRecord = new javax.swing.JLabel();
        txtTotalRecord = new javax.swing.JFormattedTextField();
        txtTotalAmt = new javax.swing.JFormattedTextField();
        btnSearch = new javax.swing.JButton();
        btnSelect = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Stock Voucher Search Dialog");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblVouNo.setFont(Global.lableFont);
        lblVouNo.setText("Vou No");

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("User");

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Date");

        txtFromDate.setToolTipText("");
        txtFromDate.setDateFormatString("dd/MM/yyyy");
        txtFromDate.setFont(Global.textFont);

        txtVouNo.setFont(Global.textFont);
        txtVouNo.setName("txtVouNo"); // NOI18N
        txtVouNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtVouNoFocusGained(evt);
            }
        });

        txtUser.setFont(Global.textFont);
        txtUser.setName("txtUser"); // NOI18N
        txtUser.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtUserFocusGained(evt);
            }
        });

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Remark");

        txtRemark.setFont(Global.textFont);
        txtRemark.setName("txtVouNo"); // NOI18N
        txtRemark.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtRemarkFocusGained(evt);
            }
        });

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Stock");

        txtStock.setFont(Global.textFont);
        txtStock.setName("txtVouNo"); // NOI18N
        txtStock.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtStockFocusGained(evt);
            }
        });

        jButton1.setFont(Global.lableFont);
        jButton1.setText("Clear Filter");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        chkDel.setText("Deleted");

        jLabel13.setFont(Global.lableFont);
        jLabel13.setText("Branch");

        txtDep.setFont(Global.textFont);
        txtDep.setName("txtUser"); // NOI18N
        txtDep.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDepFocusGained(evt);
            }
        });

        chkLocal.setText("Local");

        jLabel14.setFont(Global.lableFont);
        jLabel14.setText("Customer");

        txtCustomer.setFont(Global.textFont);
        txtCustomer.setName("txtUser"); // NOI18N
        txtCustomer.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCustomerFocusGained(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("To");

        txtToDate.setToolTipText("");
        txtToDate.setDateFormatString("dd/MM/yyyy");
        txtToDate.setFont(Global.textFont);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblVouNo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkLocal, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkDel, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(txtVouNo, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtFromDate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtToDate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(txtDep)
                            .addComponent(txtCustomer)
                            .addComponent(txtStock, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtUser)
                            .addComponent(txtRemark)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel11)
                            .addComponent(txtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(txtToDate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblVouNo)
                            .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(txtDep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14)
                            .addComponent(txtCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkDel)))
                .addGap(8, 8, 8)
                .addComponent(chkLocal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel11, txtFromDate});

        tblVoucher.setFont(Global.textFont);
        tblVoucher.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblVoucher.setRowHeight(Global.tblRowHeight);
        tblVoucher.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblVoucherMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblVoucher);

        txtFilter.setFont(Global.textFont);
        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Search Bar");

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblTtlAmount.setFont(Global.lableFont);
        lblTtlAmount.setText("Total Amount :");

        lblTtlRecord.setFont(Global.lableFont);
        lblTtlRecord.setText("Total Record :");

        txtTotalRecord.setEditable(false);
        txtTotalRecord.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalRecord.setFont(Global.amtFont);

        txtTotalAmt.setEditable(false);
        txtTotalAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalAmt.setFont(Global.amtFont);

        btnSearch.setFont(Global.lableFont);
        btnSearch.setText("Search");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        btnSelect.setFont(Global.lableFont);
        btnSelect.setText("Select");
        btnSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTtlRecord)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtTotalRecord, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblTtlAmount)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtTotalAmt, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnSearch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSelect)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblTtlRecord)
                        .addComponent(lblTtlAmount)
                        .addComponent(txtTotalRecord, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtTotalAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnSelect, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(progess, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtFilter))
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(progess, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void tblVoucherMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVoucherMouseClicked
        if (evt.getClickCount() == 2) {
            select();
        }
    }//GEN-LAST:event_tblVoucherMouseClicked

    private void txtVouNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVouNoFocusGained
        // TODO add your handling code here:
        txtVouNo.selectAll();
    }//GEN-LAST:event_txtVouNoFocusGained

    private void txtUserFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtUserFocusGained
        // TODO add your handling code here:
        txtUser.requestFocus();
    }//GEN-LAST:event_txtUserFocusGained

    private void txtRemarkFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRemarkFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRemarkFocusGained

    private void txtStockFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtStockFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtStockFocusGained

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

    private void txtDepFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDepFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDepFocusGained

    private void txtCustomerFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCustomerFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCustomerFocusGained

    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnSelect;
    private javax.swing.JCheckBox chkDel;
    private javax.swing.JCheckBox chkLocal;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblTtlAmount;
    private javax.swing.JLabel lblTtlRecord;
    private javax.swing.JLabel lblVouNo;
    private javax.swing.JProgressBar progess;
    private javax.swing.JTable tblVoucher;
    private javax.swing.JTextField txtCustomer;
    private javax.swing.JTextField txtDep;
    private javax.swing.JTextField txtFilter;
    private com.toedter.calendar.JDateChooser txtFromDate;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JTextField txtStock;
    private com.toedter.calendar.JDateChooser txtToDate;
    private javax.swing.JFormattedTextField txtTotalAmt;
    private javax.swing.JFormattedTextField txtTotalRecord;
    private javax.swing.JTextField txtUser;
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
