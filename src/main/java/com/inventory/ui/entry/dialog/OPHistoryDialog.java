/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry.dialog;

import com.common.FilterObject;
import com.common.Global;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.user.common.UserRepo;
import com.common.Util1;
import com.inventory.editor.AppUserAutoCompleter;
import com.inventory.editor.DepartmentAutoCompleter;
import com.inventory.editor.StockAutoCompleter;
import com.inventory.model.OPHis;
import com.inventory.model.VOpening;
import com.inventory.ui.common.InventoryRepo;
import com.inventory.ui.entry.dialog.common.OPVouSearchTableModel;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.time.Duration;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 *
 * @author wai yan
 */
@Slf4j
public class OPHistoryDialog extends javax.swing.JDialog implements KeyListener {

    /**
     * Creates new form SaleVouSearchDialog
     */
    private final OPVouSearchTableModel tableModel = new OPVouSearchTableModel();
    private WebClient inventoryApi;
    private InventoryRepo inventoryRepo;
    private UserRepo userRepo;
    private AppUserAutoCompleter appUserAutoCompleter;
    private SelectionObserver observer;
    private StockAutoCompleter stockAutoCompleter;
    private DepartmentAutoCompleter departmentAutoCompleter;
    private boolean status = false;

    public InventoryRepo getInventoryRepo() {
        return inventoryRepo;
    }

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public WebClient getWebClient() {
        return inventoryApi;
    }

    public void setWebClient(WebClient inventoryApi) {
        this.inventoryApi = inventoryApi;
    }

    public SelectionObserver getObserver() {
        return observer;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public UserRepo getUserRepo() {
        return userRepo;
    }

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public OPHistoryDialog(JFrame frame) {
        super(frame, true);
        initComponents();
        initKeyListener();
        txtTotalAmt.setFormatterFactory(Util1.getDecimalFormat());
        progess.setIndeterminate(false);
    }

    public void initMain() {
        if (!status) {
            initCombo();
            initTableVoucher();
            setTodayDate();
            status = true;
        }
        search();
    }

    private void initCombo() {
        appUserAutoCompleter = new AppUserAutoCompleter(txtUser, userRepo.getAppUser(), null, true);
        stockAutoCompleter = new StockAutoCompleter(txtStock, inventoryRepo, null, true);
        departmentAutoCompleter = new DepartmentAutoCompleter(txtDep, userRepo.getDeparment(), null, true);
        departmentAutoCompleter.setDepartment(userRepo.findDepartment(Global.deptId));

    }

    private void initTableVoucher() {
        tableModel.setParent(tblVoucher);
        tblVoucher.setModel(tableModel);
        tblVoucher.getTableHeader().setFont(Global.tblHeaderFont);
        tblVoucher.getColumnModel().getColumn(0).setPreferredWidth(30);
        tblVoucher.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblVoucher.getColumnModel().getColumn(2).setPreferredWidth(150);
        tblVoucher.getColumnModel().getColumn(3).setPreferredWidth(50);
        tblVoucher.getColumnModel().getColumn(4).setPreferredWidth(100);
        tblVoucher.getColumnModel().getColumn(5).setPreferredWidth(50);
        tblVoucher.setDefaultRenderer(Float.class, new TableCellRender());
        tblVoucher.setDefaultRenderer(Object.class, new TableCellRender());
    }

    private void setTodayDate() {
        if (txtFromDate.getDate() == null) {
            txtFromDate.setDate(Util1.toDate(Global.startDate, "dd/MM/yyyy"));
            txtToDate.setDate(Util1.getTodayDate());
        }
    }

    private void search() {
        progess.setIndeterminate(true);
        FilterObject filter = new FilterObject(Global.compCode, Global.deptId);
        filter.setFromDate(Util1.toDateStr(txtFromDate.getDate(), "yyyy-MM-dd"));
        filter.setToDate(Util1.toDateStr(txtToDate.getDate(), "yyyy-MM-dd"));
        filter.setUserCode(appUserAutoCompleter.getAppUser().getUserCode());
        filter.setVouNo(txtVouNo.getText());
        filter.setRemark(txtRemark.getText());
        filter.setStockCode(stockAutoCompleter.getStock().getKey().getStockCode());
        filter.setDeptId(departmentAutoCompleter.getDepartment().getDeptId());
        //
        Mono<ResponseEntity<List<OPHis>>> result = inventoryApi
                .post()
                .uri("/setup/get-opening")
                .body(Mono.just(filter), FilterObject.class)
                .retrieve()
                .toEntityList(OPHis.class);
        List<OPHis> listOP = result.block(Duration.ofMinutes(1)).getBody();
        tableModel.setListDetail(listOP);
        txtTotalRecord.setValue(listOP.size());
        calAmt();
        progess.setIndeterminate(false);
    }

    private void calAmt() {
        List<OPHis> list = tableModel.getListDetail();
        double ttlAmt = 0.0;
        for (OPHis op : list) {
            ttlAmt += Util1.getDouble(op.getOpAmt());
        }
        txtTotalAmt.setValue(ttlAmt);
    }

    private void select() {
        int row = tblVoucher.convertRowIndexToModel(tblVoucher.getSelectedRow());
        if (row >= 0) {
            OPHis his = tableModel.getSelectVou(row);
            observer.selected("OP-HISTORY", his);
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtFromDate = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        txtToDate = new com.toedter.calendar.JDateChooser();
        txtVouNo = new javax.swing.JTextField();
        txtUser = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel5 = new javax.swing.JLabel();
        txtStock = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtDep = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblVoucher = new javax.swing.JTable();
        progess = new javax.swing.JProgressBar();
        jPanel2 = new javax.swing.JPanel();
        btnSelect = new javax.swing.JButton();
        btnSearch = new javax.swing.JButton();
        lblTtlAmount = new javax.swing.JLabel();
        lblTtlRecord = new javax.swing.JLabel();
        txtTotalAmt = new javax.swing.JFormattedTextField();
        txtTotalRecord = new javax.swing.JFormattedTextField();

        setTitle("Opening Voucher Search Dialog");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Vou No");

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("User");

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Date");

        txtFromDate.setDateFormatString("dd/MM/yyyy");
        txtFromDate.setFont(Global.lableFont);

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("To");

        txtToDate.setDateFormatString("dd/MM/yyyy");
        txtToDate.setFont(Global.lableFont);

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
        jLabel5.setText("Stock");

        txtStock.setFont(Global.textFont);
        txtStock.setName("txtVouNo"); // NOI18N
        txtStock.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtStockFocusGained(evt);
            }
        });

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Remark");

        txtRemark.setFont(Global.textFont);
        txtRemark.setName("txtVouNo"); // NOI18N
        txtRemark.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtRemarkFocusGained(evt);
            }
        });

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Department");

        txtDep.setFont(Global.textFont);
        txtDep.setName("txtUser"); // NOI18N
        txtDep.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDepFocusGained(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(txtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtVouNo)
                    .addComponent(txtUser)
                    .addComponent(txtStock)
                    .addComponent(txtRemark)
                    .addComponent(txtDep))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtFromDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtToDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(txtStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(txtUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(txtDep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jSeparator2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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

        lblTtlAmount.setFont(Global.lableFont);
        lblTtlAmount.setText("Total Amount :");

        lblTtlRecord.setFont(Global.lableFont);
        lblTtlRecord.setText("Total Record :");

        txtTotalAmt.setEditable(false);
        txtTotalAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalAmt.setFont(Global.amtFont);

        txtTotalRecord.setEditable(false);
        txtTotalRecord.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalRecord.setFont(Global.amtFont);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTtlRecord)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtTotalRecord)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblTtlAmount)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtTotalAmt)
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
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(lblTtlAmount, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(txtTotalAmt)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnSearch, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnSelect, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(txtTotalRecord, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblTtlRecord, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
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
                        .addComponent(jScrollPane2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtVouNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVouNoFocusGained
        // TODO add your handling code here:
        txtVouNo.selectAll();
    }//GEN-LAST:event_txtVouNoFocusGained

    private void txtUserFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtUserFocusGained
        // TODO add your handling code here:
        txtUser.requestFocus();
    }//GEN-LAST:event_txtUserFocusGained

    private void tblVoucherMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVoucherMouseClicked
        if (evt.getClickCount() == 2) {
            select();
        }
    }//GEN-LAST:event_tblVoucherMouseClicked

    private void btnSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectActionPerformed
        select();
    }//GEN-LAST:event_btnSelectActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        search();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void txtStockFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtStockFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtStockFocusGained

    private void txtRemarkFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRemarkFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRemarkFocusGained

    private void txtDepFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDepFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDepFocusGained

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnSelect;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblTtlAmount;
    private javax.swing.JLabel lblTtlRecord;
    private javax.swing.JProgressBar progess;
    private javax.swing.JTable tblVoucher;
    private javax.swing.JTextField txtDep;
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
