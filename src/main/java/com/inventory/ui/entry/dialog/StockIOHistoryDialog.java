/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry.dialog;

import com.common.ComponentUtil;
import com.common.ReportFilter;
import com.common.Global;
import com.common.SelectionObserver;
import com.common.StartWithRowFilter;
import com.common.TableCellRender;
import com.repo.UserRepo;
import com.common.Util1;
import com.inventory.editor.AppUserAutoCompleter;
import com.user.editor.DepartmentUserAutoCompleter;
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.editor.StockAutoCompleter;
import com.inventory.editor.TraderAutoCompleter;
import com.inventory.editor.VouStatusAutoCompleter;
import com.inventory.model.Job;
import com.user.model.AppUser;
import com.inventory.model.Stock;
import com.inventory.model.VStockIO;
import com.inventory.model.VouStatus;
import com.repo.InventoryRepo;
import com.inventory.ui.entry.dialog.common.StockIOVouSearchTableModel;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
public class StockIOHistoryDialog extends javax.swing.JDialog implements KeyListener, SelectionObserver {

    /**
     * Creates new form SaleVouSearchDialog
     */
    private final StockIOVouSearchTableModel tableModel = new StockIOVouSearchTableModel();
    private UserRepo userRepo;
    private InventoryRepo inventoryRepo;
    private AppUserAutoCompleter appUserAutoCompleter;
    private VouStatusAutoCompleter vouStatusAutoCompleter;
    private StockAutoCompleter stockAutoCompleter;
    private DepartmentUserAutoCompleter departmentAutoCompleter;
    private SelectionObserver observer;
    private TableRowSorter<TableModel> sorter;
    private StartWithRowFilter tblFilter;
    private LocationAutoCompleter locationAutoCompleter;
    private TraderAutoCompleter traderAutoCompleter;
    private JobSearchDialog jobSearchDialog;
    private Job job;
    private int row = 0;

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public StockIOHistoryDialog(JFrame frame) {
        super(frame, true);
        initComponents();
        initKeyListener();
    }

    public void initMain() {
        ComponentUtil.addFocusListener(panelFilter);
        initTextBox();
        initTableVoucher();
        setTodayDate();
        initCombo();
    }

    private void initTextBox() {
        ComponentUtil.setTextProperty(panelFoot);
        txtInQty.setForeground(Color.green);
        txtInBag.setForeground(Color.green);
        txtOutBag.setForeground(Color.red);
        txtOutQty.setForeground(Color.red);
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
        locationAutoCompleter = new LocationAutoCompleter(txtLocation, null, true, false);
        inventoryRepo.getLocation().subscribe((t) -> {
            locationAutoCompleter.setListLocation(t);
        });
        vouStatusAutoCompleter = new VouStatusAutoCompleter(txtVouType, null, true);
        inventoryRepo.getVoucherStatus().doOnSuccess((t) -> {
            vouStatusAutoCompleter.setListData(t);
        }).subscribe();

        stockAutoCompleter = new StockAutoCompleter(txtStock, inventoryRepo, null, true);
        traderAutoCompleter = new TraderAutoCompleter(txtTrader, inventoryRepo, null, true, "CUS");
    }

    private void initTableVoucher() {
        tblVoucher.setModel(tableModel);
        tblVoucher.getTableHeader().setFont(Global.tblHeaderFont);
        tblVoucher.getColumnModel().getColumn(0).setPreferredWidth(40);//date
        tblVoucher.getColumnModel().getColumn(1).setPreferredWidth(50);//vouno
        tblVoucher.getColumnModel().getColumn(2).setPreferredWidth(150);
        tblVoucher.getColumnModel().getColumn(3).setPreferredWidth(100);
        tblVoucher.getColumnModel().getColumn(4).setPreferredWidth(100);
        tblVoucher.getColumnModel().getColumn(5).setPreferredWidth(50);
        tblVoucher.getColumnModel().getColumn(6).setPreferredWidth(50);
        tblVoucher.setDefaultRenderer(Object.class, new TableCellRender());
        tblVoucher.setDefaultRenderer(Double.class, new TableCellRender());
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

    private String getLocCode() {
        return locationAutoCompleter.getLocation() == null ? "-" : locationAutoCompleter.getLocation().getKey().getLocCode();
    }

    private String getVouStatus() {
        return vouStatusAutoCompleter.getVouStatus() == null ? "-" : vouStatusAutoCompleter.getVouStatus().getKey().getCode();
    }

    private Integer getDepId() {
        return departmentAutoCompleter.getDepartment() == null ? 0 : departmentAutoCompleter.getDepartment().getKey().getDeptId();
    }

    public void search() {
        progress.setIndeterminate(true);
        txtRecord.setValue(0);
        tableModel.clear();
        ReportFilter filter = new ReportFilter(Global.macId, Global.compCode, Global.deptId);
        filter.setFromDate(Util1.toDateStr(txtFromDate.getDate(), "yyyy-MM-dd"));
        filter.setToDate(Util1.toDateStr(txtToDate.getDate(), "yyyy-MM-dd"));
        filter.setUserCode(getUserCode());
        filter.setVouNo(txtVouNo.getText());
        filter.setRemark(Util1.isNull(txtRemark.getText(), "-"));
        filter.setDescription(Util1.isNull(txtDesp.getText(), "-"));
        filter.setVouStatus(getVouStatus());
        filter.setStockCode(stockAutoCompleter.getStock().getKey().getStockCode());
        filter.setLocCode(getLocCode());
        filter.setDeleted(chkDel.isSelected());
        filter.setDeptId(getDepId());
        filter.setTraderCode(traderAutoCompleter.getTrader().getKey().getCode());
        if (getJob() != null) {
            filter.setJobNo(getJob().getKey().getJobNo());
        }
        txtRecord.setValue(0);
        txtInBag.setValue(0);
        txtInQty.setValue(0);
        txtOutQty.setValue(0);
        txtOutBag.setValue(0);
        inventoryRepo.getStockIO(filter)
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
            ComponentUtil.scrollTable(tblVoucher, row, 0);
        }).subscribe();
        setVisible(true);
    }

    private void calTotal() {
        txtRecord.setValue(tableModel.getSize());
        txtInQty.setValue(tableModel.getInQty());
        txtInBag.setValue(tableModel.getInBag());
        txtOutQty.setValue(tableModel.getOutQty());
        txtOutBag.setValue(tableModel.getOutBag());
    }

    private void select() {
        if (row >= 0) {
            VStockIO his = tableModel.getSelectVou(row);
            observer.selected("IO-HISTORY", his);
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
        txtToDate.setDate(Util1.getTodayDate());
        vouStatusAutoCompleter.setVoucher(new VouStatus("-", "All"));
        txtVouNo.setText(null);
        txtDesp.setText(null);
        txtRemark.setText(null);
        txtJob.setText(null);
        setJob(null);
        stockAutoCompleter.setStock(new Stock("-", "All"));
        appUserAutoCompleter.setAppUser(new AppUser("-", "All"));
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelFilter = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtFromDate = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        txtToDate = new com.toedter.calendar.JDateChooser();
        txtVouNo = new javax.swing.JTextField();
        txtUser = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel5 = new javax.swing.JLabel();
        txtVouType = new javax.swing.JTextField();
        txtDesp = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtStock = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        txtLocation = new javax.swing.JTextField();
        chkDel = new javax.swing.JCheckBox();
        jLabel12 = new javax.swing.JLabel();
        txtDep = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtTrader = new javax.swing.JTextField();
        txtJob = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblVoucher = new javax.swing.JTable();
        progress = new javax.swing.JProgressBar();
        txtFilter = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        panelFoot = new javax.swing.JPanel();
        lblTtlRecord = new javax.swing.JLabel();
        txtRecord = new javax.swing.JFormattedTextField();
        lblTtlRecord1 = new javax.swing.JLabel();
        txtInQty = new javax.swing.JFormattedTextField();
        lblTtlRecord2 = new javax.swing.JLabel();
        txtInBag = new javax.swing.JFormattedTextField();
        txtOutQty = new javax.swing.JFormattedTextField();
        lblTtlRecord3 = new javax.swing.JLabel();
        txtOutBag = new javax.swing.JFormattedTextField();
        lblTtlRecord4 = new javax.swing.JLabel();
        btnSearch = new javax.swing.JButton();

        setTitle("Stock In/Out Voucher Search Dialog");

        panelFilter.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Vou No");

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("User");

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("From");

        txtFromDate.setToolTipText("");
        txtFromDate.setDateFormatString("dd/MM/yyyy");
        txtFromDate.setFont(Global.textFont);

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("To");

        txtToDate.setToolTipText("");
        txtToDate.setDateFormatString("dd/MM/yyyy");
        txtToDate.setFont(Global.textFont);

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

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Vou Type");

        txtVouType.setFont(Global.textFont);
        txtVouType.setName("txtVouNo"); // NOI18N
        txtVouType.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtVouTypeFocusGained(evt);
            }
        });

        txtDesp.setFont(Global.textFont);
        txtDesp.setName("txtVouNo"); // NOI18N
        txtDesp.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDespFocusGained(evt);
            }
        });

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Desp");

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

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Location");

        txtLocation.setFont(Global.textFont);
        txtLocation.setName("txtVouNo"); // NOI18N
        txtLocation.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtLocationFocusGained(evt);
            }
        });

        chkDel.setText("Deleted");

        jLabel12.setFont(Global.lableFont);
        jLabel12.setText("Department");

        txtDep.setFont(Global.textFont);
        txtDep.setName("txtUser"); // NOI18N
        txtDep.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDepFocusGained(evt);
            }
        });

        jLabel13.setFont(Global.lableFont);
        jLabel13.setText("Trader");

        txtTrader.setFont(Global.textFont);
        txtTrader.setName("txtVouNo"); // NOI18N
        txtTrader.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtTraderFocusGained(evt);
            }
        });

        txtJob.setEditable(false);
        txtJob.setFont(Global.textFont);
        txtJob.setName("txtUser"); // NOI18N
        txtJob.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtJobFocusGained(evt);
            }
        });
        txtJob.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtJobMouseClicked(evt);
            }
        });

        jLabel14.setFont(Global.lableFont);
        jLabel14.setText("Job");

        javax.swing.GroupLayout panelFilterLayout = new javax.swing.GroupLayout(panelFilter);
        panelFilter.setLayout(panelFilterLayout);
        panelFilterLayout.setHorizontalGroup(
            panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFilterLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton1)
                    .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jSeparator3)
                        .addGroup(panelFilterLayout.createSequentialGroup()
                            .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtDep)
                                    .addComponent(txtUser)
                                    .addComponent(txtLocation)
                                    .addComponent(txtStock)
                                    .addComponent(txtRemark)
                                    .addComponent(txtDesp)
                                    .addComponent(txtVouType)
                                    .addComponent(txtVouNo)
                                    .addComponent(txtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtFromDate, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                                    .addComponent(txtTrader)
                                    .addComponent(txtJob))
                                .addComponent(chkDel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelFilterLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtFromDate, txtToDate});

        panelFilterLayout.setVerticalGroup(
            panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFilterLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(panelFilterLayout.createSequentialGroup()
                        .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addComponent(txtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(txtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(txtVouType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(txtDesp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(txtStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(txtLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(txtTrader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(txtUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(txtDep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14)
                            .addComponent(txtJob, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkDel))
                    .addComponent(jSeparator2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelFilterLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel11, jLabel3, txtFromDate, txtToDate});

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

        panelFoot.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblTtlRecord.setFont(Global.lableFont);
        lblTtlRecord.setText("Total Record :");

        txtRecord.setEditable(false);
        txtRecord.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtRecord.setFont(Global.amtFont);

        lblTtlRecord1.setFont(Global.lableFont);
        lblTtlRecord1.setText("In Qty :");

        txtInQty.setEditable(false);
        txtInQty.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtInQty.setFont(Global.amtFont);

        lblTtlRecord2.setFont(Global.lableFont);
        lblTtlRecord2.setText("In Bag :");

        txtInBag.setEditable(false);
        txtInBag.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtInBag.setFont(Global.amtFont);

        txtOutQty.setEditable(false);
        txtOutQty.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtOutQty.setFont(Global.amtFont);

        lblTtlRecord3.setFont(Global.lableFont);
        lblTtlRecord3.setText("Out Qty :");

        txtOutBag.setEditable(false);
        txtOutBag.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtOutBag.setFont(Global.amtFont);

        lblTtlRecord4.setFont(Global.lableFont);
        lblTtlRecord4.setText("Out Bag :");

        javax.swing.GroupLayout panelFootLayout = new javax.swing.GroupLayout(panelFoot);
        panelFoot.setLayout(panelFootLayout);
        panelFootLayout.setHorizontalGroup(
            panelFootLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFootLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTtlRecord)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtRecord)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTtlRecord1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtInQty)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTtlRecord2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtInBag)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTtlRecord3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtOutQty)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTtlRecord4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtOutBag)
                .addContainerGap())
        );
        panelFootLayout.setVerticalGroup(
            panelFootLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFootLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFootLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTtlRecord)
                    .addComponent(txtRecord, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTtlRecord1)
                    .addComponent(txtInQty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTtlRecord2)
                    .addComponent(txtInBag, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtOutQty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTtlRecord3)
                    .addComponent(txtOutBag, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTtlRecord4))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnSearch.setBackground(Global.selectionColor);
        btnSearch.setFont(Global.lableFont);
        btnSearch.setForeground(new java.awt.Color(255, 255, 255));
        btnSearch.setText("Search");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(progress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtFilter)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSearch))
                            .addComponent(panelFoot, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
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
                            .addComponent(jLabel1)
                            .addComponent(btnSearch))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelFoot, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addComponent(panelFilter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        search();

    }//GEN-LAST:event_btnSearchActionPerformed

    private void tblVoucherMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVoucherMouseClicked
        row = tblVoucher.convertRowIndexToModel(tblVoucher.getSelectedRow());
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

    private void txtVouTypeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVouTypeFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVouTypeFocusGained

    private void txtDespFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDespFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDespFocusGained

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

    private void txtLocationFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtLocationFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtLocationFocusGained

    private void txtDepFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDepFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDepFocusGained

    private void txtTraderFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTraderFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTraderFocusGained

    private void txtJobFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtJobFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtJobFocusGained

    private void txtJobMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtJobMouseClicked
        if (evt.getClickCount() > 1) {
            jobDialog();
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_txtJobMouseClicked

    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSearch;
    private javax.swing.JCheckBox chkDel;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel lblTtlRecord;
    private javax.swing.JLabel lblTtlRecord1;
    private javax.swing.JLabel lblTtlRecord2;
    private javax.swing.JLabel lblTtlRecord3;
    private javax.swing.JLabel lblTtlRecord4;
    private javax.swing.JPanel panelFilter;
    private javax.swing.JPanel panelFoot;
    private javax.swing.JProgressBar progress;
    private javax.swing.JTable tblVoucher;
    private javax.swing.JTextField txtDep;
    private javax.swing.JTextField txtDesp;
    private javax.swing.JTextField txtFilter;
    private com.toedter.calendar.JDateChooser txtFromDate;
    private javax.swing.JFormattedTextField txtInBag;
    private javax.swing.JFormattedTextField txtInQty;
    private javax.swing.JTextField txtJob;
    private javax.swing.JTextField txtLocation;
    private javax.swing.JFormattedTextField txtOutBag;
    private javax.swing.JFormattedTextField txtOutQty;
    private javax.swing.JFormattedTextField txtRecord;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JTextField txtStock;
    private com.toedter.calendar.JDateChooser txtToDate;
    private javax.swing.JTextField txtTrader;
    private javax.swing.JTextField txtUser;
    private javax.swing.JTextField txtVouNo;
    private javax.swing.JTextField txtVouType;
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

    @Override
    public void selected(Object source, Object selectObj) {
        switch (source.toString()) {
            case "Job" -> {
                if (selectObj instanceof Job v) {
                    inventoryRepo.findJob(v.getKey().getJobNo()).subscribe((t) -> {
                        setJob(t);
                        txtJob.setText(t.getJobName());
                    });
                }
            }
            case "Select" -> {
            }
        }
    }
}
