/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.inventory.ui.setup;

import com.common.ReportFilter;
import com.common.Global;
import com.common.PanelControl;
import com.common.SelectionObserver;
import com.common.StartWithRowFilter;
import com.common.TableCellRender;
import com.common.Util1;
import com.inventory.entity.Job;
import com.inventory.entity.JobKey;
import com.inventory.entity.MessageType;
import com.inventory.ui.setup.dialog.common.JobTableModel;
import com.repo.InventoryRepo;
import com.repo.UserRepo;
import com.user.editor.DepartmentUserAutoCompleter;
import java.awt.Color;
import java.time.LocalDateTime;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class JobSetup extends javax.swing.JPanel implements PanelControl {

    private JProgressBar progress;
    private InventoryRepo inventoryRepo;
    private UserRepo userRepo;
    private SelectionObserver observer;

    private int selectRow = - 1;
    private Job ord = new Job();
    private final JobTableModel jobTableModel = new JobTableModel();

    private TableRowSorter<TableModel> sorter;
    private StartWithRowFilter swrf;
    private DepartmentUserAutoCompleter departmentUserAutoCompleter;

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }

    /**
     * Creates new form JobSetup
     */
    public JobSetup() {
        initComponents();
    }

    public void initMain() {
        initCombo();
        initTable();
        searchJob();
        txtStartDate.setDate(Util1.getTodayDate());
        txtEndDate.setDate(Util1.getTodayDate());
        txtUserCode.requestFocus();
    }

    private void searchJob() {
        progress.setIndeterminate(true);
        ReportFilter ReportFilter = new ReportFilter(Global.macId,Global.compCode, Global.deptId);
        ReportFilter.setFinished(rdoFinish.isSelected());
        ReportFilter.setFromDate("");
        ReportFilter.setToDate("");
        inventoryRepo.getJob(ReportFilter).doOnSuccess((t) -> {
            jobTableModel.setListVou(t);
            setSize();
        }).doOnTerminate(() -> {
            progress.setIndeterminate(false);
        }).subscribe();
    }

    private void setSize() {
        List<Job> listData = jobTableModel.getListData();
        long countFinished = listData.stream().filter((t) -> t.isFinished()).count();
        long process = listData.size() - countFinished;
        lblFinised.setText(String.valueOf(countFinished));
        lblProcess.setText(String.valueOf(process));
        lblRecord.setText(String.valueOf(listData.size()));

    }

    private void initCombo() {
        departmentUserAutoCompleter = new DepartmentUserAutoCompleter(txtDep, null, false);
        userRepo.findDepartment(Global.deptId).doOnSuccess((t) -> {
            departmentUserAutoCompleter.setDepartment(t);
        }).subscribe();
        userRepo.getDeparment(true).doOnSuccess((t) -> {
            departmentUserAutoCompleter.setListDepartment(t);
        }).subscribe();
    }

    private void initTable() {
        tblVou.setModel(jobTableModel);
        sorter = new TableRowSorter<>(tblVou.getModel());
        tblVou.setRowSorter(sorter);
        tblVou.getTableHeader().setFont(Global.lableFont);
        tblVou.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblVou.setRowHeight(Global.tblRowHeight);
        tblVou.setDefaultRenderer(Object.class, new TableCellRender());
        tblVou.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting()) {
                if (tblVou.getSelectedRow() >= 0) {
                    selectRow = tblVou.convertRowIndexToModel(tblVou.getSelectedRow());
                    setJob(jobTableModel.getJob(selectRow));
                }
            }
        });
        swrf = new StartWithRowFilter(txtFilter);
    }

    private void setJob(Job cat) {
        ord = cat;
        ord.setKey(cat.getKey());
        txtName.setText(ord.getJobName());
        txtUserCode.setText(ord.getKey().getJobNo());
        chkFinished.setSelected(ord.isFinished());
        txtStartDate.setDate(ord.getStartDate());
        txtEndDate.setDate(ord.getEndDate());
        userRepo.findDepartment(ord.getDeptId()).doOnSuccess((t) -> {
            departmentUserAutoCompleter.setDepartment(t);
        }).subscribe();
        txtName.requestFocus();
        lblStatus.setText("EDIT");
        lblStatus.setForeground(Color.blue);
        chkFinished.setEnabled(true);

    }

    private void saveJob() {
        if (isValidEntry()) {
            progress.setIndeterminate(true);
            observer.selected("save", false);
            inventoryRepo.saveJob(ord).doOnSuccess((t) -> {
                if (t != null) {
                    if (lblStatus.getText().equals("EDIT")) {
                        jobTableModel.setJob(t, selectRow);
                    } else {
                        jobTableModel.addJob(t);
                    }
                }
            }).doOnError((e) -> {
                progress.setIndeterminate(false);
                observer.selected("save", true);
                JOptionPane.showMessageDialog(this, e.getMessage());
            }).doOnTerminate(() -> {
                sendMessage(ord.getJobName());
                clear();
            }).subscribe();
        }
    }

    private void sendMessage(String mes) {
        inventoryRepo.sendDownloadMessage(MessageType.JOB, mes)
                .doOnSuccess((t) -> {
                    log.info(t);
                }).subscribe();
    }

    private void clear() {
        progress.setIndeterminate(false);
        txtUserCode.setText("");
        txtFilter.setText(null);
        txtName.setText(null);
        lblStatus.setText("NEW");
        chkFinished.setSelected(false);
        chkFinished.setEnabled(false);
        lblStatus.setForeground(Color.green);
        ord = new Job();
        jobTableModel.refresh();
        tblVou.requestFocus();
        txtName.requestFocus();
        observeMain();
        setSize();
    }

    private boolean isValidEntry() {
        if (txtName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Invalid Name");
            txtName.requestFocus();
            return false;
        } else if (departmentUserAutoCompleter.getDepartment() == null) {
            JOptionPane.showMessageDialog(this, "Invalid Department.");
            txtDep.requestFocus();
            return false;
        } else {
            if (lblStatus.getText().equals("NEW")) {
                JobKey key = new JobKey();
                key.setJobNo(null);
                key.setCompCode(Global.compCode);
                ord.setKey(key);
                ord.setCreatedBy(Global.loginUser.getUserCode());
                ord.setCreatedDate(LocalDateTime.now());
            } else {
                ord.setUpdatedBy(Global.loginUser.getUserCode());
            }
            ord.setDeptId(departmentUserAutoCompleter.getDepartment().getKey().getDeptId());
            ord.setStartDate(txtStartDate.getDate());
            ord.setEndDate(txtEndDate.getDate());
            ord.setJobName(txtName.getText());
            ord.setFinished(chkFinished.isSelected());
        }
        return true;
    }

    private void observeMain() {
        observer.selected("control", this);
        observer.selected("save", true);
        observer.selected("print", false);
        observer.selected("history", false);
        observer.selected("delete", true);
        observer.selected("refresh", true);
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
        tblVou = new javax.swing.JTable();
        txtFilter = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        lblStatus = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtUserCode = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        txtStartDate = new com.toedter.calendar.JDateChooser();
        txtEndDate = new com.toedter.calendar.JDateChooser();
        jLabel5 = new javax.swing.JLabel();
        chkFinished = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
        lblRecord = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lblFinised = new javax.swing.JLabel();
        lblProcess = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtDep = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        rdoFinish = new javax.swing.JRadioButton();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        tblVou.setFont(Global.textFont);
        tblVou.setModel(new javax.swing.table.DefaultTableModel(
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
        tblVou.setName("tblVou"); // NOI18N
        jScrollPane1.setViewportView(tblVou);

        txtFilter.setFont(Global.textFont);
        txtFilter.setName("txtFilter"); // NOI18N
        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Job Name");

        txtName.setFont(Global.textFont);
        txtName.setName("txtName"); // NOI18N
        txtName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtNameFocusGained(evt);
            }
        });
        txtName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNameActionPerformed(evt);
            }
        });

        lblStatus.setFont(Global.lableFont);
        lblStatus.setText("NEW");

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Job No");

        txtUserCode.setFont(Global.textFont);
        txtUserCode.setEnabled(false);
        txtUserCode.setName("txtUserCode"); // NOI18N
        txtUserCode.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtUserCodeFocusGained(evt);
            }
        });
        txtUserCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUserCodeActionPerformed(evt);
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Start Date");

        txtStartDate.setDateFormatString("dd/MM/yyyy");
        txtStartDate.setFont(Global.textFont);
        txtStartDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtStartDateFocusGained(evt);
            }
        });
        txtStartDate.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtStartDatePropertyChange(evt);
            }
        });

        txtEndDate.setDateFormatString("dd/MM/yyyy");
        txtEndDate.setFont(Global.textFont);
        txtEndDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtEndDateFocusGained(evt);
            }
        });
        txtEndDate.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtEndDatePropertyChange(evt);
            }
        });

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("End Date");

        chkFinished.setFont(Global.lableFont);
        chkFinished.setText("Finished");
        chkFinished.setEnabled(false);

        jLabel4.setText("Record : ");

        lblRecord.setText("0");

        jLabel6.setText("Finished :");

        lblFinised.setText("0");

        lblProcess.setText("0");

        jLabel7.setText("Process :");

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Department");

        txtDep.setFont(Global.textFont);
        txtDep.setName("txtName"); // NOI18N
        txtDep.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDepFocusGained(evt);
            }
        });
        txtDep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDepActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE)
                                .addGap(168, 168, 168))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtName)
                                    .addComponent(txtUserCode)
                                    .addComponent(txtStartDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtEndDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(chkFinished, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtDep, javax.swing.GroupLayout.Alignment.TRAILING))))
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblProcess, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblFinised, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblRecord, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtUserCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtStartDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(txtEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(txtDep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkFinished)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblStatus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(lblRecord))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(lblFinised))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(lblProcess))
                .addContainerGap(158, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        rdoFinish.setText("Finished");
        rdoFinish.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoFinishActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rdoFinish)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rdoFinish)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 510, Short.MAX_VALUE)
                    .addComponent(txtFilter)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyReleased
        // TODO add your handling code here:
        if (txtFilter.getText().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(swrf);
        }
    }//GEN-LAST:event_txtFilterKeyReleased

    private void txtNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNameFocusGained
        // TODO add your handling code here:
        txtName.selectAll();
    }//GEN-LAST:event_txtNameFocusGained

    private void txtNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNameActionPerformed

    private void txtUserCodeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtUserCodeFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUserCodeFocusGained

    private void txtUserCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUserCodeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUserCodeActionPerformed

    private void txtStartDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtStartDateFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtStartDateFocusGained

    private void txtStartDatePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtStartDatePropertyChange

    }//GEN-LAST:event_txtStartDatePropertyChange

    private void txtEndDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtEndDateFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEndDateFocusGained

    private void txtEndDatePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtEndDatePropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEndDatePropertyChange

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observeMain();
    }//GEN-LAST:event_formComponentShown

    private void txtDepFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDepFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDepFocusGained

    private void txtDepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDepActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDepActionPerformed

    private void rdoFinishActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoFinishActionPerformed
        // TODO add your handling code here:
        searchJob();
    }//GEN-LAST:event_rdoFinishActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkFinished;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblFinised;
    private javax.swing.JLabel lblProcess;
    private javax.swing.JLabel lblRecord;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JRadioButton rdoFinish;
    private javax.swing.JTable tblVou;
    private javax.swing.JTextField txtDep;
    private com.toedter.calendar.JDateChooser txtEndDate;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JTextField txtName;
    private com.toedter.calendar.JDateChooser txtStartDate;
    private javax.swing.JTextField txtUserCode;
    // End of variables declaration//GEN-END:variables

    @Override
    public void delete() {
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
    }

    @Override
    public void refresh() {
        searchJob();
    }

    @Override
    public void filter() {
    }

    @Override
    public String panelName() {
        return this.getName();
    }

    @Override
    public void save() {
        saveJob();
    }
}
