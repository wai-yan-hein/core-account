/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry.dialog;

import com.common.ComponentUtil;
import com.common.ReportFilter;
import com.common.Global;
import com.common.IconUtil;
import com.common.SelectionObserver;
import com.common.StartWithRowFilter;
import com.common.TableCellRender;
import com.repo.UserRepo;
import com.common.Util1;
import com.formdev.flatlaf.FlatClientProperties;
import com.user.editor.DepartmentUserAutoCompleter;
import com.inventory.entity.LabourOutput;
import com.inventory.entity.VStockIO;
import com.inventory.ui.entry.dialog.common.LabourOutputHistoryTableModel;
import com.repo.InventoryRepo;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author wai yan
 */
@Slf4j
public class LabourOutputHistory extends javax.swing.JDialog implements KeyListener, SelectionObserver {

    /**
     * Creates new form SaleVouSearchDialog
     */
    private final LabourOutputHistoryTableModel tableModel = new LabourOutputHistoryTableModel();
    @Setter
    private UserRepo userRepo;
    @Setter
    private InventoryRepo inventoryRepo;
    private DepartmentUserAutoCompleter departmentAutoCompleter;
    @Setter
    private SelectionObserver observer;
    private TableRowSorter<TableModel> sorter;
    private StartWithRowFilter tblFilter;
    private int row = 0;

    public LabourOutputHistory(JFrame frame) {
        super(frame, Dialog.ModalityType.MODELESS);
        initComponents();
        initKeyListener();
        initClientProperty();
    }

    private void initClientProperty() {
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search Here");
        txtSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        txtSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, IconUtil.getIcon(IconUtil.SEARCH_ICON));
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
        txtOut.setForeground(Color.green);
        txtReject.setForeground(Color.red);
    }

    private void initCombo() {
        departmentAutoCompleter = new DepartmentUserAutoCompleter(txtDep, null, true);
        userRepo.getDeparment(true).doOnSuccess((t) -> {
            departmentAutoCompleter.setListDepartment(t);
        }).subscribe();
        userRepo.findDepartment(Global.deptId).doOnSuccess((t) -> {
            departmentAutoCompleter.setDepartment(t);
        }).subscribe();
    }

    private void initTableVoucher() {
        tblVoucher.setModel(tableModel);
        tblVoucher.getTableHeader().setFont(Global.tblHeaderFont);
        tblVoucher.getColumnModel().getColumn(0).setPreferredWidth(40);//date
        tblVoucher.getColumnModel().getColumn(1).setPreferredWidth(50);//vouno
        tblVoucher.getColumnModel().getColumn(2).setPreferredWidth(150);//remark
        tblVoucher.getColumnModel().getColumn(3).setPreferredWidth(100);//out
        tblVoucher.getColumnModel().getColumn(4).setPreferredWidth(100);//reject
        tblVoucher.getColumnModel().getColumn(5).setPreferredWidth(150);//amount
        tblVoucher.setDefaultRenderer(Object.class, new TableCellRender());
        tblVoucher.setDefaultRenderer(Double.class, new TableCellRender());
        tblVoucher.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sorter = new TableRowSorter<>(tblVoucher.getModel());
        tblFilter = new StartWithRowFilter(txtSearch);
        tblVoucher.setRowSorter(sorter);
    }

    private void setTodayDate() {
        if (txtFromDate.getDate() == null) {
            txtFromDate.setDate(Util1.getTodayDate());
            txtToDate.setDate(Util1.getTodayDate());
        }
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
        filter.setVouNo(txtVouNo.getText());
        filter.setDeleted(chkDel.isSelected());
        filter.setDeptId(getDepId());
        txtRecord.setValue(0);
        txtOut.setValue(0);
        txtReject.setValue(0);
        txtAmount.setValue(0);
        inventoryRepo.getLabourOutputHistory(filter)
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
        txtOut.setValue(tableModel.getOutputQty());
        txtReject.setValue(tableModel.getRejectQty());
        txtAmount.setValue(tableModel.getAmount());
    }

    private void select() {
        if (row >= 0) {
            LabourOutput his = tableModel.getSelectVou(row);
            observer.selected("LO-HISTORY", his);
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
    }

    private void clearFilter() {
        txtFromDate.setDate(Util1.getTodayDate());
        txtToDate.setDate(Util1.getTodayDate());
        txtVouNo.setText(null);
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
        jLabel11 = new javax.swing.JLabel();
        txtFromDate = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        txtToDate = new com.toedter.calendar.JDateChooser();
        txtVouNo = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        jButton1 = new javax.swing.JButton();
        chkDel = new javax.swing.JCheckBox();
        jLabel12 = new javax.swing.JLabel();
        txtDep = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblVoucher = new javax.swing.JTable();
        progress = new javax.swing.JProgressBar();
        txtSearch = new javax.swing.JTextField();
        panelFoot = new javax.swing.JPanel();
        lblTtlRecord = new javax.swing.JLabel();
        txtRecord = new javax.swing.JFormattedTextField();
        lblTtlRecord2 = new javax.swing.JLabel();
        txtOut = new javax.swing.JFormattedTextField();
        txtReject = new javax.swing.JFormattedTextField();
        lblTtlRecord3 = new javax.swing.JLabel();
        txtAmount = new javax.swing.JFormattedTextField();
        lblTtlRecord4 = new javax.swing.JLabel();
        btnSearch = new javax.swing.JButton();

        setTitle("Labour Output History");

        panelFilter.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Vou No");

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

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jButton1.setFont(Global.lableFont);
        jButton1.setText("Clear Filter");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
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
                                .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtDep)
                                    .addComponent(txtVouNo)
                                    .addComponent(txtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtFromDate, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE))
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
                            .addComponent(jLabel12)
                            .addComponent(txtDep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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

        txtSearch.setFont(Global.textFont);
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });

        panelFoot.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblTtlRecord.setFont(Global.lableFont);
        lblTtlRecord.setText("Total Record :");

        txtRecord.setEditable(false);
        txtRecord.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtRecord.setFont(Global.amtFont);

        lblTtlRecord2.setFont(Global.lableFont);
        lblTtlRecord2.setText("Output Qty :");

        txtOut.setEditable(false);
        txtOut.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtOut.setFont(Global.amtFont);

        txtReject.setEditable(false);
        txtReject.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtReject.setFont(Global.amtFont);

        lblTtlRecord3.setFont(Global.lableFont);
        lblTtlRecord3.setText("Reject Qty :");

        txtAmount.setEditable(false);
        txtAmount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAmount.setFont(Global.amtFont);

        lblTtlRecord4.setFont(Global.lableFont);
        lblTtlRecord4.setText("Amount :");

        javax.swing.GroupLayout panelFootLayout = new javax.swing.GroupLayout(panelFoot);
        panelFoot.setLayout(panelFootLayout);
        panelFootLayout.setHorizontalGroup(
            panelFootLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFootLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTtlRecord)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtRecord, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTtlRecord2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtOut, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTtlRecord3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtReject, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTtlRecord4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtAmount, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelFootLayout.setVerticalGroup(
            panelFootLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFootLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFootLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTtlRecord)
                    .addComponent(txtRecord, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTtlRecord2)
                    .addComponent(txtOut, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtReject, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTtlRecord3)
                    .addComponent(txtAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                                .addComponent(txtSearch)
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
                            .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        clearFilter();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        // TODO add your handling code here:
        if (txtSearch.getText().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(tblFilter);
        }
    }//GEN-LAST:event_txtSearchKeyReleased

    private void txtDepFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDepFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDepFocusGained

    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSearch;
    private javax.swing.JCheckBox chkDel;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel lblTtlRecord;
    private javax.swing.JLabel lblTtlRecord2;
    private javax.swing.JLabel lblTtlRecord3;
    private javax.swing.JLabel lblTtlRecord4;
    private javax.swing.JPanel panelFilter;
    private javax.swing.JPanel panelFoot;
    private javax.swing.JProgressBar progress;
    private javax.swing.JTable tblVoucher;
    private javax.swing.JFormattedTextField txtAmount;
    private javax.swing.JTextField txtDep;
    private com.toedter.calendar.JDateChooser txtFromDate;
    private javax.swing.JFormattedTextField txtOut;
    private javax.swing.JFormattedTextField txtRecord;
    private javax.swing.JFormattedTextField txtReject;
    private javax.swing.JTextField txtSearch;
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

    @Override
    public void selected(Object source, Object selectObj) {
    }
}
