/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.dialog;

import com.repo.AccountRepo;
import com.acc.common.JournalEntryTableModel;
import com.acc.editor.COA3CellEditor;
import com.acc.editor.DepartmentCellEditor;
import com.acc.editor.TraderCellEditor;
import com.acc.model.Gl;
import com.common.DecimalFormatRender;
import com.common.Global;
import com.common.ProUtil;
import com.common.Util1;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.toedter.calendar.JTextFieldDateEditor;
import com.repo.UserRepo;
import com.user.editor.CurrencyEditor;
import com.user.editor.ProjectAutoCompleter;
import com.user.model.ProjectKey;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

/**
 *
 * @author Lenovo
 */
public class JournalEntryDialog extends javax.swing.JDialog implements KeyListener {

    private final JournalEntryTableModel journalTablModel = new JournalEntryTableModel();
    private ProjectAutoCompleter projectAutoCompleter;
    private String status;
    private AccountRepo accountRepo;
    private UserRepo userRepo;

    public UserRepo getUserRepo() {
        return userRepo;
    }

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public AccountRepo getAccountRepo() {
        return accountRepo;
    }

    public void setAccountRepo(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Creates new form JournalEntryDialog
     *
     * @param frame
     */
    public JournalEntryDialog(JFrame frame) {
        super(frame, true);
        initComponents();
        initKeyListener();
        initFocusListener();
        keyMapping();
    }

    private void batchLock(boolean lock) {
        tblJournal.setEnabled(lock);
        btnSave.setEnabled(lock);
        txtVouDate.setEnabled(lock);
        txtRefrence.setEnabled(lock);
        btnNew.setEnabled(lock);
    }

    private void initCombo() {
        projectAutoCompleter = new ProjectAutoCompleter(txtProjectNo, userRepo, null, false);
    }

    private void initFocusListener() {
        txtVouDate.getDateEditor().getUiComponent().addFocusListener(fa);
        txtRefrence.addFocusListener(fa);
        txtProjectNo.addFocusListener(fa);
    }
    private final FocusAdapter fa = new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            if (e.getSource() instanceof JTextFieldDateEditor edit) {
                edit.selectAll();
            } else if (e.getSource() instanceof JTextField txt) {
                txt.selectAll();
            }
        }
    };

    private void keyMapping() {
        KeyStroke delete = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        tblJournal.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(delete, "delete");
        tblJournal.getActionMap().put("delete", actionDelete);
        KeyStroke f5 = KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0);
        tblJournal.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(f5, "f5");
        tblJournal.getActionMap().put("f5", actionSave);
        KeyStroke f6 = KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0);
        tblJournal.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(f6, "f6");
        tblJournal.getActionMap().put("f6", actionPrint);
    }

    private final Action actionDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            removeJournal();
        }
    };
    private final Action actionSave = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            saveGeneralVoucher();
        }
    };
    private final Action actionPrint = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            printJournal();
        }
    };

    public void initMain() {
        batchLock(!Global.batchLock);
        initTable();
        initCombo();
    }

    private void initTable() {
        txtVouDate.setDate(Util1.getTodayDate());
        tblJournal.setModel(journalTablModel);
        tblJournal.setCellSelectionEnabled(true);
        journalTablModel.setParent(tblJournal);
        journalTablModel.setTtlCrdAmt(txtFCrdAmt);
        journalTablModel.setTtlDrAmt(txtFDrAmt);
        journalTablModel.setTxtRef(txtRefrence);
        journalTablModel.setAccountRepo(accountRepo);
        tblJournal.getTableHeader().setFont(Global.lableFont);
        tblJournal.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        accountRepo.getDepartment().subscribe((t) -> {
            tblJournal.getColumnModel().getColumn(0).setCellEditor(new DepartmentCellEditor(t));
        });
        tblJournal.getColumnModel().getColumn(1).setCellEditor(new AutoClearEditor());
        tblJournal.getColumnModel().getColumn(2).setCellEditor(new TraderCellEditor(accountRepo));
        tblJournal.getColumnModel().getColumn(3).setCellEditor(new COA3CellEditor(accountRepo, 3));
        userRepo.getCurrency().subscribe((t) -> {
            tblJournal.getColumnModel().getColumn(4).setCellEditor(new CurrencyEditor(t));
        });
        tblJournal.getColumnModel().getColumn(5).setCellEditor(new AutoClearEditor());
        tblJournal.getColumnModel().getColumn(6).setCellEditor(new AutoClearEditor());
        tblJournal.getColumnModel().getColumn(0).setPreferredWidth(10);//dep
        tblJournal.getColumnModel().getColumn(1).setPreferredWidth(240);//Desp
        tblJournal.getColumnModel().getColumn(2).setPreferredWidth(240);//cus
        tblJournal.getColumnModel().getColumn(3).setPreferredWidth(240);//acc
        tblJournal.getColumnModel().getColumn(4).setPreferredWidth(60);//currency
        tblJournal.getColumnModel().getColumn(5).setPreferredWidth(60);//dr
        tblJournal.getColumnModel().getColumn(6).setPreferredWidth(60);//cr
        tblJournal.setDefaultRenderer(Double.class, new DecimalFormatRender());
        tblJournal.setDefaultRenderer(Object.class, new DecimalFormatRender());
        journalTablModel.addEmptyRow();
        tblJournal.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
    }

    public void searchJournalByVouId(String vouNo) {
        journalTablModel.clear();
        lblStatus.setText(status);
        lblStatus.setForeground(status.equals("NEW") ? Color.GREEN : Color.BLUE);
        if (status.equals("EDIT")) {
            progress.setIndeterminate(true);
            accountRepo.getJournal(vouNo).subscribe((t) -> {
                LocalDateTime glDate = t.get(0).getGlDate();
                String ref = t.get(0).getReference();
                String projectNo = t.get(0).getProjectNo();
                userRepo.find(new ProjectKey(projectNo, Global.compCode)).subscribe((p) -> {
                    projectAutoCompleter.setProject(p);
                }, (e) -> {
                    JOptionPane.showMessageDialog(this, e.getMessage());
                });
                txtVouDate.setDate(Util1.convertToDate(glDate));
                txtRefrence.setText(ref);
                txtVouNo.setText(vouNo);
                journalTablModel.setListGV(t);
                journalTablModel.addEmptyRow();
                focusOnTable();
                progress.setIndeterminate(false);
            }, (e) -> {
                progress.setIndeterminate(false);
                JOptionPane.showMessageDialog(this, e.getMessage());
            });

        } else {
            journalTablModel.addEmptyRow();
            focusOnTable();
        }

    }

    private boolean saveGeneralVoucher() {
        if (isValidEntry() && isValidData()) {
            List<Gl> list = journalTablModel.getListGV();
            if (lblStatus.getText().equals("EDIT")) {
                list.get(0).setEdit(journalTablModel.isEdit());
                list.get(0).setDelList(journalTablModel.getDelList());
            }
            progress.setIndeterminate(true);
            btnSave.setEnabled(false);
            accountRepo.saveGl(list).subscribe((t) -> {
                clear();
            }, (e) -> {
                progress.setIndeterminate(false);
                btnSave.setEnabled(true);
                JOptionPane.showMessageDialog(this, e.getMessage());
            });

        }
        return true;
    }

    public boolean isValidData() {
        String projectNo = null;
        if (projectAutoCompleter.getProject() != null) {
            projectNo = projectAutoCompleter.getProject().getKey().getProjectNo();
        }
        for (Gl g : journalTablModel.getListGV()) {
            g.setReference(txtRefrence.getText());
            g.setProjectNo(projectNo);
            g.setGlDate(Util1.convertToLocalDateTime(txtVouDate.getDate()));
            g.setMacId(Global.macId);
            if (lblStatus.getText().equals("EDIT")) {
                g.setModifyBy(Global.loginUser.getUserCode());
            }
            if (g.getSrcAccCode() != null) {
                if (g.getDeptCode() == null) {
                    JOptionPane.showMessageDialog(tblJournal, "Invalid Department.");
                    return false;
                } else if (Util1.getDouble(g.getDrAmt()) + Util1.getDouble(g.getCrAmt()) == 0) {
                    JOptionPane.showMessageDialog(tblJournal, "Invalid Amount.");
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isValidEntry() {
        if (!Objects.equals(Util1.getDouble(txtFDrAmt.getValue()), Util1.getDouble(txtFCrdAmt.getValue()))) {
            JOptionPane.showMessageDialog(this, "Out of balance.", "Invalid Entry", JOptionPane.WARNING_MESSAGE);
            return false;
        } else if (!ProUtil.isValidDate(txtVouDate.getDate())) {
            return false;
        }
        return true;
    }

    private void initKeyListener() {
        txtVouDate.getDateEditor().getUiComponent().setName("txtDate");
        txtVouDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtRefrence.addKeyListener(this);
        txtVouNo.addKeyListener(this);
        txtProjectNo.addKeyListener(this);
        tblJournal.addKeyListener(this);
        btnSave.addKeyListener(this);
    }

    public void clear() {
        progress.setIndeterminate(false);
        btnSave.setEnabled(true);
        txtVouDate.setDate(Util1.getTodayDate());
        txtFCrdAmt.setValue(0.0);
        txtFDrAmt.setValue(0.0);
        txtRefrence.setText(null);
        txtVouNo.setText(null);
        lblStatus.setText("NEW");
        lblStatus.setForeground(Color.green);
        txtVouNo.setEditable(false);
        journalTablModel.clear();
        journalTablModel.addEmptyRow();
        if (projectAutoCompleter != null) {
            projectAutoCompleter.setProject(null);
        }
        focusOnTable();
    }

    private void focusOnTable() {
        int rc = tblJournal.getRowCount();
        if (rc > 1) {
            tblJournal.setRowSelectionInterval(rc - 1, rc - 1);
            tblJournal.setColumnSelectionInterval(0, 0);
            tblJournal.requestFocus();
        } else {
            txtRefrence.requestFocus();
        }
    }

    private void removeJournal() {
        if (tblJournal.getSelectedRow() >= 0) {
            int selectRow = tblJournal.convertRowIndexToModel(tblJournal.getSelectedRow());
            journalTablModel.removeJournal(selectRow);
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

        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtVouDate = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        txtVouNo = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtRefrence = new javax.swing.JTextField();
        txtProjectNo = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblJournal = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        txtFCrdAmt = new javax.swing.JFormattedTextField();
        txtFDrAmt = new javax.swing.JFormattedTextField();
        lblStatus = new javax.swing.JLabel();
        btnSave = new javax.swing.JButton();
        btnNew = new javax.swing.JButton();
        progress = new javax.swing.JProgressBar();

        jLabel3.setText("jLabel3");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Journal Voucher");
        setFont(Global.textFont);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Date");

        txtVouDate.setDateFormatString("dd/MM/yyyy");
        txtVouDate.setFont(Global.textFont);
        txtVouDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtVouDateFocusLost(evt);
            }
        });
        txtVouDate.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtVouDatePropertyChange(evt);
            }
        });

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Voucher No");

        txtVouNo.setEditable(false);
        txtVouNo.setFont(Global.textFont);
        txtVouNo.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtVouNo.setName("txtVouNo"); // NOI18N
        txtVouNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtVouNoActionPerformed(evt);
            }
        });

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Refrence");

        txtRefrence.setFont(Global.textFont);
        txtRefrence.setName("txtRefrence"); // NOI18N
        txtRefrence.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRefrenceActionPerformed(evt);
            }
        });

        txtProjectNo.setFont(Global.textFont);
        txtProjectNo.setName("txtProjectNo"); // NOI18N
        txtProjectNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtProjectNoActionPerformed(evt);
            }
        });

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Project No");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtVouDate, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtVouNo, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtRefrence, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtProjectNo, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtRefrence, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4)
                        .addComponent(txtProjectNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5))
                    .addComponent(txtVouDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jLabel2});

        tblJournal.setFont(Global.textFont);
        tblJournal.setModel(new javax.swing.table.DefaultTableModel(
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
        tblJournal.setGridColor(new java.awt.Color(204, 204, 204));
        tblJournal.setName("tblJournal"); // NOI18N
        tblJournal.setRowHeight(Global.tblRowHeight);
        tblJournal.setShowHorizontalLines(true);
        tblJournal.setShowVerticalLines(true);
        tblJournal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblJournalKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblJournal);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        txtFCrdAmt.setEditable(false);
        txtFCrdAmt.setFormatterFactory(Util1.getDecimalFormat());
        txtFCrdAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFCrdAmt.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtFCrdAmt.setFont(Global.amtFont);
        txtFCrdAmt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFCrdAmtActionPerformed(evt);
            }
        });

        txtFDrAmt.setEditable(false);
        txtFDrAmt.setFormatterFactory(Util1.getDecimalFormat());
        txtFDrAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFDrAmt.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtFDrAmt.setFont(Global.amtFont);
        txtFDrAmt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFDrAmtActionPerformed(evt);
            }
        });

        lblStatus.setFont(Global.menuFont);
        lblStatus.setText("NEW");

        btnSave.setFont(Global.lableFont);
        btnSave.setText("Save -F5");
        btnSave.setName("btnSave"); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnNew.setFont(Global.lableFont);
        btnNew.setText("New");
        btnNew.setName("btnSave"); // NOI18N
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblStatus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 493, Short.MAX_VALUE)
                .addComponent(btnSave)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnNew)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFDrAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFCrdAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnNew)
                            .addComponent(btnSave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtFCrdAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtFDrAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblStatus))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(progress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(progress, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtVouNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtVouNoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVouNoActionPerformed

    private void txtRefrenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRefrenceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRefrenceActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
    }//GEN-LAST:event_formComponentShown

    private void txtFCrdAmtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFCrdAmtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFCrdAmtActionPerformed

    private void txtFDrAmtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFDrAmtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFDrAmtActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        saveGeneralVoucher();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void tblJournalKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblJournalKeyReleased
        // TODO add your handling code here:

    }//GEN-LAST:event_tblJournalKeyReleased

    private void txtVouDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVouDateFocusLost
        // TODO add your handling code here:

    }//GEN-LAST:event_txtVouDateFocusLost

    private void txtVouDatePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtVouDatePropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVouDatePropertyChange

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        // TODO add your handling code here:
        clear();
    }//GEN-LAST:event_btnNewActionPerformed

    private void txtProjectNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProjectNoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtProjectNoActionPerformed

    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JProgressBar progress;
    private javax.swing.JTable tblJournal;
    private javax.swing.JFormattedTextField txtFCrdAmt;
    private javax.swing.JFormattedTextField txtFDrAmt;
    private javax.swing.JTextField txtProjectNo;
    private javax.swing.JTextField txtRefrence;
    private com.toedter.calendar.JDateChooser txtVouDate;
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
        Object sourceObj = e.getSource();
        String ctrlName = "-";

        if (sourceObj instanceof JButton jButton) {
            ctrlName = jButton.getName();
        } else if (sourceObj instanceof JTextField jTextField) {
            ctrlName = jTextField.getName();
        } else if (sourceObj instanceof JTable jTable) {
            ctrlName = jTable.getName();
        } else if (sourceObj instanceof JTextFieldDateEditor jTextFieldDateEditor) {
            ctrlName = jTextFieldDateEditor.getName();
        }
        switch (ctrlName) {
            case "txtDate":
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String date = ((JTextFieldDateEditor) sourceObj).getText();
                    txtVouDate.setDate(Util1.formatDate(date));
                    if (txtVouDate.getDate() != null) {
                        if (!ProUtil.isValidDate(txtVouDate.getDate())) {
                            txtVouDate.setDate(Util1.getTodayDate());
                        }
                    }
                    txtRefrence.requestFocus();
                }
                break;
            case "txtVouNo":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtRefrence.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtVouDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                break;
            case "txtRefrence":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtProjectNo.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtVouNo.requestFocus();
                }
                break;
            case "txtProjectNo":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    focusOnTable();
                }

                break;
            case "btnSave":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtVouDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtRefrence.requestFocus();
                }
            case "tblJournal":
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtVouDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                break;

        }
    }

    private void printJournal() {

    }
}
