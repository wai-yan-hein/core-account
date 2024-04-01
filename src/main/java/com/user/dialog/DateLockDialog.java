/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.user.dialog;

import com.common.Global;
import com.common.TableCellRender;
import com.common.Util1;
import com.inventory.entity.MessageType;
import com.user.editor.AutoClearEditor;
import com.repo.UserRepo;
import com.user.common.DateLockTableModel;
import com.user.model.DateLock;
import com.user.model.DateLockKey;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.time.LocalDateTime;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author DELL
 */
@Slf4j
public class DateLockDialog extends javax.swing.JDialog {

    private final DateLockTableModel dateLockTableModel = new DateLockTableModel();
    private UserRepo userRepo;
    private DateLock dateLock = new DateLock();

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Creates new form RoleSetupDialog
     *
     * @param parent
     */
    public DateLockDialog(JFrame parent) {
        super(parent, true);
        initComponents();
        actionMapping();
    }

    private void actionMapping() {
        String solve = "enter";
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        table.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, solve);
        table.getActionMap().put(solve, new EnterAction());

    }

    private class EnterAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
        }
    }

    public void search() {
        userRepo.getDateLock().doOnSuccess((t) -> {
            dateLockTableModel.setList(t);
        }).subscribe();
        setVisible(true);
    }

    public void initMain() {
        initDate();
        initTable();
    }

    private void initTable() {
        dateLockTableModel.setUserRepo(userRepo);
        table.setModel(dateLockTableModel);
        table.getTableHeader().setFont(Global.tblHeaderFont);
        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(Global.tblRowHeight);
        table.setDefaultRenderer(Object.class, new TableCellRender());
        table.setDefaultRenderer(Boolean.class, new TableCellRender());
        table.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        table.getColumnModel().getColumn(0).setCellEditor(new AutoClearEditor());
        table.getColumnModel().getColumn(1).setCellEditor(new AutoClearEditor());
        table.getColumnModel().getColumn(2).setCellEditor(new AutoClearEditor());
        table.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting()) {
                int row = table.convertRowIndexToModel(table.getSelectedRow());
                if (row >= 0) {
                    DateLock l = dateLockTableModel.getObject(row);
                    setData(l);
                }

            }
        });
    }

    private void setData(DateLock dl) {
        dateLock = dl;
        txtRemark.setText(dl.getRemark());
        txtStartDate.setDate(Util1.convertToDate(dl.getStartDate()));
        txtEndDate.setDate(Util1.convertToDate(dl.getEndDate()));
        chkLock.setSelected(dl.isDateLock());
        lblCreateName.setText(Global.hmUser.get(dl.getCreatedBy()));
        lblUpdateName.setText(Global.hmUser.get(dl.getUpdatedBy()));
        lblCreateTime.setText(Util1.convertToLocalStorage(dl.getCreatedDateTime()));
        lblUpdateTime.setText(Util1.convertToLocalStorage(dl.getUpdatedDateTime()));
        lblStatus.setText("EDIT");
        lblStatus.setForeground(Color.blue);
    }

    private void initDate() {
        txtStartDate.setDate(Util1.getTodayDate());
        txtEndDate.setDate(Util1.getTodayDate());
    }

    private void defaultValue() {
        initDate();
        lblStatus.setText("NEW");
        lblStatus.setForeground(Color.green);
        dateLock = new DateLock();
        progress.setIndeterminate(false);
        btnSave.setEnabled(true);
    }

    private void save() {
        if (isValidEntry()) {
            progress.setIndeterminate(true);
            btnSave.setEnabled(false);
            userRepo.save(dateLock).doOnSuccess((t) -> {
                if (lblStatus.getText().equals("NEW")) {
                    dateLockTableModel.addObject(t);
                } else {
                    int row = table.convertRowIndexToModel(table.getSelectedRow());
                    dateLockTableModel.setObject(row, t);
                }
            }).doOnError((e) -> {
                JOptionPane.showMessageDialog(this, e.getMessage());
                progress.setIndeterminate(false);
                btnSave.setEnabled(true);
            }).doOnTerminate(() -> {
                clear();
                sendMessage("Date Lock");
            }).subscribe();
        }
    }

    private void clear() {
        defaultValue();
        txtRemark.setText(null);
        chkLock.setSelected(true);
        lblCreateName.setText(null);
        lblCreateTime.setText(null);
        lblUpdateName.setText(null);
        lblUpdateTime.setText(null);
    }

    private boolean isValidEntry() {
        if (txtStartDate.getDate() == null) {
            txtStartDate.requestFocus();
            return false;
        } else if (txtEndDate.getDate() == null) {
            txtEndDate.requestFocus();
            return false;
        } else {
            if (lblStatus.getText().equals("NEW")) {
                DateLockKey key = new DateLockKey();
                key.setCompCode(Global.compCode);
                dateLock.setKey(key);
                dateLock.setCreatedBy(Global.loginUser.getUserCode());
                dateLock.setCreatedDate(LocalDateTime.now());
            }
            dateLock.setUpdatedBy(Global.loginUser.getUserCode());
            dateLock.setUpdatedDate(LocalDateTime.now());
            dateLock.setStartDate(Util1.toLocalDate(txtStartDate.getDate()));
            dateLock.setEndDate(Util1.toLocalDate(txtEndDate.getDate()));
            dateLock.setRemark(txtRemark.getText());
            dateLock.setDateLock(chkLock.isSelected());
        }
        return true;
    }

    private void sendMessage(String mes) {
        userRepo.sendDownloadMessage(MessageType.DATE_LOCK, mes)
                .doOnSuccess((t) -> {
                    log.info(t);
                }).subscribe();
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
        table = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        txtStartDate = new com.toedter.calendar.JDateChooser();
        txtEndDate = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        chkLock = new javax.swing.JCheckBox();
        btnSave = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        lblCreateName = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lblUpdateName = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lblCreateTime = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lblUpdateTime = new javax.swing.JLabel();
        progress = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Date Lock Dialog");

        table.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(table);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Remark ");

        txtRemark.setFont(Global.textFont);

        txtStartDate.setDateFormatString("dd/MM/yyyy");
        txtStartDate.setFont(Global.textFont);

        txtEndDate.setDateFormatString("dd/MM/yyyy");
        txtEndDate.setFont(Global.textFont);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Start Date");

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("End Date");

        chkLock.setFont(Global.lableFont);
        chkLock.setText("Date Lock");

        btnSave.setBackground(Global.selectionColor);
        btnSave.setFont(Global.lableFont);
        btnSave.setForeground(new java.awt.Color(255, 255, 255));
        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        jButton2.setFont(Global.lableFont);
        jButton2.setText("Clear");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        lblStatus.setFont(Global.lableFont);
        lblStatus.setText("NEW");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtEndDate, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                            .addComponent(txtStartDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtRemark)
                            .addComponent(chkLock, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSave)))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnSave, jButton2});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtStartDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtEndDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkLock))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(lblStatus)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSave)
                    .addComponent(jButton2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Created By :");

        lblCreateName.setFont(Global.lableFont);
        lblCreateName.setText("-");

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Updated By :");

        lblUpdateName.setFont(Global.lableFont);
        lblUpdateName.setText("-");

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Created Time :");

        lblCreateTime.setFont(Global.lableFont);
        lblCreateTime.setText("-");

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Updated Time :");

        lblUpdateTime.setFont(Global.lableFont);
        lblUpdateTime.setText("-");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblUpdateTime, javax.swing.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE)
                            .addComponent(lblCreateTime, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE))
                        .addGap(11, 11, 11)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblUpdateName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblCreateName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(lblCreateName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(lblUpdateName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(lblCreateTime))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(lblUpdateTime))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(progress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 337, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 389, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        save();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        clear();
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox chkLock;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCreateName;
    private javax.swing.JLabel lblCreateTime;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblUpdateName;
    private javax.swing.JLabel lblUpdateTime;
    private javax.swing.JProgressBar progress;
    private javax.swing.JTable table;
    private com.toedter.calendar.JDateChooser txtEndDate;
    private javax.swing.JTextField txtRemark;
    private com.toedter.calendar.JDateChooser txtStartDate;
    // End of variables declaration//GEN-END:variables
}
