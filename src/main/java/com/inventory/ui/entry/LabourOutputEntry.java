/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.inventory.ui.entry;

import com.common.ComponentUtil;
import com.common.DateLockUtil;
import com.common.DecimalFormatRender;
import com.common.Global;
import com.common.PanelControl;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.editor.DesignEditor;
import com.inventory.editor.JobEditor;
import com.inventory.editor.OrderRefNoCellEditor;
import com.inventory.editor.TraderInvEditor;
import com.inventory.editor.VouStatusEditor;
import com.inventory.entity.LabourOutput;
import com.inventory.entity.LabourOutputDetail;
import com.inventory.ui.common.LabourOutputTableModel;
import com.repo.InventoryRepo;
import com.user.editor.AutoClearEditor;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.time.LocalDateTime;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import lombok.Setter;
import org.springframework.web.reactive.function.client.WebClientRequestException;

/**
 *
 * @author Lenovo
 */
public class LabourOutputEntry extends javax.swing.JPanel implements PanelControl, SelectionObserver {

    @Setter
    private InventoryRepo inventoryRepo;
    @Setter
    private SelectionObserver observer;
    @Setter
    private JProgressBar progress;
    private final LabourOutputTableModel tableModel = new LabourOutputTableModel();
    private LabourOutput dto = LabourOutput.builder().build();

    /**
     * Creates new form LabourOutputEntry
     */
    public LabourOutputEntry() {
        initComponents();
    }

    public void initMain() {
        initTextProperty();
        initTable();
        setTodayDate();
    }

    private void setTodayDate() {
        txtVouDate.setDate(Util1.getTodayDate());
    }

    private void initTextProperty() {
        ComponentUtil.setTextProperty(panelFooter);
        ComponentUtil.addFocusListener(panelHeader);
        txtOutput.setForeground(Global.GREEN);
        txtReject.setForeground(Color.red);
    }

    private void initTable() {
        tblOutput.setModel(tableModel);
        tableModel.setParent(tblOutput);
        tableModel.setLblRecord(lblRecord);
        tableModel.addNewRow();
        tableModel.setObserver(this);
        tblOutput.getColumnModel().getColumn(0).setPreferredWidth(150);//Name
        tblOutput.getColumnModel().getColumn(1).setPreferredWidth(50);//Order No
        tblOutput.getColumnModel().getColumn(2).setPreferredWidth(200);//Name
        tblOutput.getColumnModel().getColumn(3).setPreferredWidth(200);//Desp
        tblOutput.getColumnModel().getColumn(4).setPreferredWidth(100);//Job
        tblOutput.getColumnModel().getColumn(5).setPreferredWidth(50);//status
        tblOutput.getColumnModel().getColumn(6).setPreferredWidth(30);//out-qty
        tblOutput.getColumnModel().getColumn(7).setPreferredWidth(30);//reject-qty
        tblOutput.getColumnModel().getColumn(8).setPreferredWidth(100);//remark
        tblOutput.getTableHeader().setFont(Global.tblHeaderFont);
        tblOutput.setCellSelectionEnabled(true);
        tblOutput.setDefaultRenderer(Object.class, new DecimalFormatRender());
        tblOutput.setDefaultRenderer(Double.class, new DecimalFormatRender());
        tblOutput.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblOutput.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblOutput.setShowGrid(true);
        tblOutput.setRowHeight(Global.tblRowHeight);
        tblOutput.setFont(Global.textFont);
        tblOutput.getColumnModel().getColumn(0).setCellEditor(new TraderInvEditor(inventoryRepo, "EMP"));
        tblOutput.getColumnModel().getColumn(1).setCellEditor(new OrderRefNoCellEditor(inventoryRepo));
        tblOutput.getColumnModel().getColumn(3).setCellEditor(new DesignEditor(inventoryRepo));
        tblOutput.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());
        tblOutput.getColumnModel().getColumn(7).setCellEditor(new AutoClearEditor());
        tblOutput.getColumnModel().getColumn(8).setCellEditor(new AutoClearEditor());
        tblOutput.getColumnModel().getColumn(9).setCellEditor(new AutoClearEditor());
        tblOutput.getColumnModel().getColumn(10).setCellEditor(new AutoClearEditor());
        inventoryRepo.getActiveJob().doOnSuccess((t) -> {
            tblOutput.getColumnModel().getColumn(5).setCellEditor(new JobEditor(t));
        }).subscribe();
        inventoryRepo.getVoucherStatus().doOnSuccess((t) -> {
            tblOutput.getColumnModel().getColumn(6).setCellEditor(new VouStatusEditor(t));//status
        }).subscribe();
    }

    private void calToal() {
        List<LabourOutputDetail> detail = tableModel.getListDetail();
        double output = detail.stream().mapToDouble((t) -> t.getOutputQty()).sum();
        double reject = detail.stream().mapToDouble((t) -> t.getRejectQty()).sum();
        double amount = detail.stream().mapToDouble((t) -> t.getAmount()).sum();
        txtOutput.setValue(output);
        txtReject.setValue(reject);
        txtTotal.setValue(amount);
    }

    public void saveVoucher(boolean print) {
        if (isValidEntry() && tableModel.isValidEntry()) {
            if (DateLockUtil.isLockDate(txtVouDate.getDate())) {
                DateLockUtil.showMessage(this);
                txtVouDate.requestFocus();
                return;
            }
            observer.selected("save", false);
            progress.setIndeterminate(true);
            dto.setListDetail(tableModel.getListDetail());
            inventoryRepo.save(dto).doOnSuccess((t) -> {
                clear();
            }).doOnError((e) -> {
                progress.setIndeterminate(false);
                observeMain();
                if (e instanceof WebClientRequestException) {
                    int yn = JOptionPane.showConfirmDialog(this, "Internet Offline. Try Again?", "Offline", JOptionPane.YES_OPTION, JOptionPane.ERROR_MESSAGE);
                    if (yn == JOptionPane.YES_OPTION) {
                        saveVoucher(print);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Error : " + e.getMessage(), "Server Error", JOptionPane.ERROR_MESSAGE);
                }
            }).subscribe();
        }
    }

    private boolean isValidEntry() {
        if (txtVouDate.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Select Date");
            return false;
        } else if (lblStatus.getText().equals("DELETED")) {
            clear();
            return false;
        } else {
            dto.setVouDate(Util1.convertToLocalDateTime(txtVouDate.getDate()));
            dto.setRemark(txtRemark.getText());
            if (lblStatus.getText().equals("NEW")) {
                dto.setCompCode(Global.compCode);
                dto.setDeptId(Global.deptId);
                dto.setCreatedBy(Global.loginUser.getUserCode());
                dto.setCreatedDate(LocalDateTime.now());
                dto.setMacId(Global.macId);
                dto.setDeleted(Boolean.FALSE);
            } else {
                dto.setUpdatedBy(Global.loginUser.getUserCode());
            }
        }
        return true;
    }

    private void deleteVoucher() {
        String status = lblStatus.getText();
        switch (status) {
            case "EDIT" -> {
                int yes_no = JOptionPane.showConfirmDialog(Global.parentForm,
                        "Are you sure to delete?", "Labour Output Voucher delete.", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                if (yes_no == 0) {
                    inventoryRepo.deleteLabourOutput(dto.getVouNo(), dto.getCompCode()).doOnSuccess((t) -> {
                        if (t) {
                            clear();
                        }
                    }).subscribe();
                }
            }
            case "DELETED" -> {
                int yes_no = JOptionPane.showConfirmDialog(this,
                        "Are you sure to restore?", "Labour Output Voucher Restore.", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (yes_no == 0) {
                    dto.setDeleted(false);
                    inventoryRepo.restoreLabourOutput(dto.getVouNo(), dto.getCompCode()).doOnSuccess((t) -> {
                        lblStatus.setText("EDIT");
                        lblStatus.setForeground(Color.blue);
                        disableForm(true);
                    }).subscribe();

                }
            }
            default ->
                JOptionPane.showMessageDialog(Global.parentForm, "Voucher can't delete.");
        }
    }

    private void clear() {
        txtVouNo.setText(null);
        txtRemark.setText(null);
        txtOutput.setValue(0);
        txtReject.setValue(0);
        txtTotal.setValue(0);
        tableModel.clear();
        lblStatus.setText("NEW");
        disableForm(true);
    }

    private void disableForm(boolean status) {
        ComponentUtil.enableForm(this, status);
        observer.selected("save", status);
        observer.selected("delete", status);
        observer.selected("print", status);
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

        panelHeader = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtVouNo = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtVouDate = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        lblStatus = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblOutput = new javax.swing.JTable();
        panelFooter = new javax.swing.JPanel();
        lblRecord = new javax.swing.JLabel();
        txtReject = new javax.swing.JFormattedTextField();
        lblRecord1 = new javax.swing.JLabel();
        lblRecord2 = new javax.swing.JLabel();
        txtOutput = new javax.swing.JFormattedTextField();
        lblRecord3 = new javax.swing.JLabel();
        txtTotal = new javax.swing.JFormattedTextField();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        panelHeader.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Vou No");

        txtVouNo.setEditable(false);
        txtVouNo.setFont(Global.lableFont);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Date");

        txtVouDate.setDateFormatString("dd/MM/yyyy");
        txtVouDate.setFont(Global.textFont);

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Remark");

        txtRemark.setFont(Global.lableFont);

        lblStatus.setFont(Global.menuFont);
        lblStatus.setText("NEW");

        javax.swing.GroupLayout panelHeaderLayout = new javax.swing.GroupLayout(panelHeader);
        panelHeader.setLayout(panelHeaderLayout);
        panelHeaderLayout.setHorizontalGroup(
            panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelHeaderLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtVouDate, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblStatus)
                .addContainerGap())
        );
        panelHeaderLayout.setVerticalGroup(
            panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelHeaderLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblStatus)
                    .addGroup(panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtVouNo)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtVouDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tblOutput.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblOutput);

        panelFooter.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblRecord.setFont(Global.lableFont);
        lblRecord.setText("Records : 0");

        txtReject.setEditable(false);

        lblRecord1.setFont(Global.lableFont);
        lblRecord1.setText("Reject");

        lblRecord2.setFont(Global.lableFont);
        lblRecord2.setText("Output");

        txtOutput.setEditable(false);

        lblRecord3.setFont(Global.lableFont);
        lblRecord3.setText("Amount");

        txtTotal.setEditable(false);

        javax.swing.GroupLayout panelFooterLayout = new javax.swing.GroupLayout(panelFooter);
        panelFooter.setLayout(panelFooterLayout);
        panelFooterLayout.setHorizontalGroup(
            panelFooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFooterLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblRecord, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblRecord2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtOutput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblRecord1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtReject, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblRecord3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panelFooterLayout.setVerticalGroup(
            panelFooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFooterLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelFooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtOutput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblRecord2))
                    .addGroup(panelFooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblRecord)
                        .addComponent(txtReject, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblRecord1)
                        .addComponent(lblRecord3)
                        .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelFooter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1140, Short.MAX_VALUE)
                    .addComponent(panelHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelFooter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observeMain();
    }//GEN-LAST:event_formComponentShown


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblRecord;
    private javax.swing.JLabel lblRecord1;
    private javax.swing.JLabel lblRecord2;
    private javax.swing.JLabel lblRecord3;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPanel panelFooter;
    private javax.swing.JPanel panelHeader;
    private javax.swing.JTable tblOutput;
    private javax.swing.JFormattedTextField txtOutput;
    private javax.swing.JFormattedTextField txtReject;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JFormattedTextField txtTotal;
    private com.toedter.calendar.JDateChooser txtVouDate;
    private javax.swing.JTextField txtVouNo;
    // End of variables declaration//GEN-END:variables

    @Override
    public void save() {
        saveVoucher(false);
    }

    @Override
    public void delete() {
        deleteVoucher();
    }

    @Override
    public void newForm() {
        clear();

    }

    @Override
    public void history() {
    }

    @Override
    public void print() {
        saveVoucher(true);

    }

    @Override
    public void refresh() {
        initMain();
    }

    @Override
    public void filter() {
    }

    @Override
    public String panelName() {
        return this.getName();
    }

    @Override
    public void selected(Object source, Object selectObj) {
        if (source.equals("LABOUR_TOTAL")) {
            calToal();
        }
    }
}
