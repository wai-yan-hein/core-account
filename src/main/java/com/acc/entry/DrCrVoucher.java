/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.acc.entry;

import com.acc.common.AccountRepo;
import com.acc.common.DateAutoCompleter;
import com.acc.common.VoucherTableModel;
import com.acc.dialog.VoucherEntryDailog;
import com.acc.model.Gl;
import com.common.Global;
import com.common.PanelControl;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.acc.editor.DepartmentAutoCompleter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Component
public class DrCrVoucher extends javax.swing.JPanel implements SelectionObserver, PanelControl, KeyListener {

    private static final Logger log = LoggerFactory.getLogger(DrCrVoucher.class);
    private int selectRow = -1;
    private DateAutoCompleter dateAutoCompleter;
    private DepartmentAutoCompleter departmentAutoCompleter;
    private SelectionObserver observer;
    private JProgressBar progress;
    private final VoucherTableModel voucherTableModel = new VoucherTableModel();
    @Autowired
    private AccountRepo accountRepo;

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
     * Creates new form CrDrVoucher1
     */
    public DrCrVoucher() {
        initComponents();
        initKeyListener();
    }

    private void initTable() {
        tblVoucher.setModel(voucherTableModel);
        tblVoucher.getTableHeader().setFont(Global.tblHeaderFont);
        tblVoucher.setDefaultRenderer(Object.class, new TableCellRender());
        tblVoucher.setDefaultRenderer(Double.class, new TableCellRender());
        tblVoucher.getTableHeader().setFont(Global.tblHeaderFont);
    }

    private void initCombo() {
        dateAutoCompleter = new DateAutoCompleter(txtDate, Global.listDate);
        dateAutoCompleter.setSelectionObserver(this);
        departmentAutoCompleter = new DepartmentAutoCompleter(txtDept, accountRepo.getDepartment(), null, true, true);
        departmentAutoCompleter.setObserver(this);
    }

    public void initMain() {
        initCombo();
        initTable();
        search();
    }

    private void initKeyListener() {
        txtDate.addKeyListener(this);
        txtDept.addKeyListener(this);
        txtDesp.addKeyListener(this);
        txtRef.addKeyListener(this);
        txtRefNo.addKeyListener(this);
        txtVouNo.addKeyListener(this);
    }

    private void search() {
        if (progress != null) {
            progress.setIndeterminate(true);
        }

    }

    public void openVoucherDialog(String type, List<Gl> listVGl) {
        VoucherEntryDailog dailog = new VoucherEntryDailog();
        dailog.setIconImage(new ImageIcon("image/book_26px.png").getImage());
        dailog.setVouType(type);
        dailog.setObserver(this);
        dailog.setListVGl(listVGl);
        dailog.initTable();
        dailog.setSize(Global.width - 200, Global.height - 200);
        dailog.setLocationRelativeTo(null);
        dailog.setVisible(true);
    }

    private void searchDetail() {
        int row = tblVoucher.getSelectedRow();
        if (row >= 0) {

        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblVoucher = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        txtDesp = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtRefNo = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtRef = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        txtDate = new javax.swing.JTextField();
        txtDept = new javax.swing.JTextField();
        txtVouNo = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblRecord = new javax.swing.JLabel();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
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

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Dep :");

        jButton1.setFont(Global.lableFont);
        jButton1.setText("Payment / Debit");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Ref No");

        txtDesp.setFont(Global.textFont);
        txtDesp.setName("txtDesp"); // NOI18N

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Reference");

        txtRefNo.setFont(Global.textFont);
        txtRefNo.setName("txtRefNo"); // NOI18N

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Date");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Vou No");

        txtRef.setFont(Global.textFont);
        txtRef.setName("txtRef"); // NOI18N

        jButton2.setFont(Global.lableFont);
        jButton2.setText("Receipt / Credit");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        txtDate.setFont(Global.textFont);
        txtDate.setName("txtDate"); // NOI18N
        txtDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDateActionPerformed(evt);
            }
        });

        txtDept.setFont(Global.textFont);
        txtDept.setName("txtDept"); // NOI18N
        txtDept.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDeptActionPerformed(evt);
            }
        });

        txtVouNo.setFont(Global.textFont);
        txtVouNo.setName("txtVouNo"); // NOI18N

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Description");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, 66, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtDept, javax.swing.GroupLayout.PREFERRED_SIZE, 66, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, 66, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtDesp, javax.swing.GroupLayout.PREFERRED_SIZE, 66, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtRef, javax.swing.GroupLayout.PREFERRED_SIZE, 66, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtRefNo, javax.swing.GroupLayout.PREFERRED_SIZE, 68, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtDate)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtVouNo)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtDesp)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtRef)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtRefNo)
                        .addComponent(jButton1)
                        .addComponent(jButton2))
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtDept, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Record :");

        lblRecord.setFont(Global.lableFont);
        lblRecord.setText("0");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblRecord, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(lblRecord))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        openVoucherDialog("CR", null);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        openVoucherDialog("DR", null);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observer.selected("CONTROL", this);
    }//GEN-LAST:event_formComponentShown

    private void tblVoucherMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVoucherMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            searchDetail();
        }
    }//GEN-LAST:event_tblVoucherMouseClicked

    private void txtDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDateActionPerformed

    private void txtDeptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDeptActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDeptActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblRecord;
    private javax.swing.JTable tblVoucher;
    private javax.swing.JTextField txtDate;
    private javax.swing.JTextField txtDept;
    private javax.swing.JTextField txtDesp;
    private javax.swing.JTextField txtRef;
    private javax.swing.JTextField txtRefNo;
    private javax.swing.JTextField txtVouNo;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {
        if (source != null) {
            search();
        }
    }

    @Override
    public void save() {
    }

    @Override
    public void delete() {
    }

    @Override
    public void newForm() {
    }

    @Override
    public void history() {
    }

    @Override
    public void print() {
    }

    @Override
    public void refresh() {
        search();
    }

    @Override
    public String panelName() {
        return this.getName();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        Object sourceObj = e.getSource();
        String ctrlName = "-";
        if (sourceObj instanceof JTextField txt) {
            ctrlName = txt.getName();
        }
        switch (ctrlName) {
            case "txtDate" -> {
                log.info(e.getKeyCode() + "");
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtDept.requestFocus();
                }
            }

            case "txtDept" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtVouNo.requestFocus();
                }
            }

            case "txtVouNo" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtDesp.requestFocus();
                    search();
                }
            }

            case "txtDesp" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtRef.requestFocus();
                    search();
                }
            }

            case "txtRef" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtRefNo.requestFocus();
                    search();
                }
            }

            case "txtRefNo" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    tblVoucher.requestFocus();
                    search();
                }
            }

        }
    }

    @Override
    public void keyReleased(KeyEvent e
    ) {
    }

    @Override
    public void filter() {
    }
}
