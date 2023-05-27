/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.dialog;

import com.acc.common.AccountRepo;
import com.acc.common.CrDrVoucherEntryTableModel;
import com.acc.editor.COA3CellEditor;
import com.acc.editor.DepartmentCellEditor;
import com.acc.editor.TraderCellEditor;
import com.acc.model.DepartmentA;
import com.acc.model.Gl;
import com.common.DecimalFormatRender;
import com.common.Global;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.editor.CurrencyEditor;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.toedter.calendar.JTextFieldDateEditor;
import com.user.common.UserRepo;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class VoucherEntryDailog extends javax.swing.JDialog implements KeyListener {

    private final CrDrVoucherEntryTableModel tableModel = new CrDrVoucherEntryTableModel();

    private TableRowSorter<TableModel> sorter;
    private String status;
    private String vouType;
    private SelectionObserver observer;
    private AccountRepo accountRepo;
    private UserRepo userRepo;
    private String srcAcc;
    private List<Gl> listVGl;

    public UserRepo getUserRepo() {
        return userRepo;
    }

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public SelectionObserver getObserver() {
        return observer;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public AccountRepo getAccountRepo() {
        return accountRepo;
    }

    public void setAccountRepo(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }

    public List<Gl> getListVGl() {
        return listVGl;
    }

    public void setListVGl(List<Gl> listVGl) {
        this.listVGl = listVGl;
    }

    public String getVouType() {
        return vouType;
    }

    public void setVouType(String vouType) {
        this.vouType = vouType;
        setTitle(vouType.equals("DR") ? "Payment / Debit Voucher" : "Receipt / Credit Voucher");
        lable1.setText(vouType.equals("DR") ? "To" : "From");
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        lblStatus.setText(status);
        lblStatus.setForeground(status.equals("NEW") ? Color.GREEN : Color.BLUE);
        txtVouNo.setEditable(status.equals("NEW"));
    }


    /**
     * Creates new form JournalEntryDialog
     */
    public VoucherEntryDailog() {
        super(Global.parentForm, true);
        initComponents();
        initKeyListener();
        keyMapping();
    }

    private void batchLock(boolean lock) {
        tblJournal.setEnabled(lock);
        btnSave.setEnabled(lock);
        txtVouDate.setEnabled(lock);
        txtRefrence.setEnabled(lock);
        btnSave.setEnabled(lock);
        btnSave2.setEnabled(lock);
        txtNa.setEnabled(lock);
        txtFor.setEnabled(lock);
        txtFrom.setEnabled(lock);
    }

    private void keyMapping() {
        KeyStroke delete = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        tblJournal.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(delete, "delete");
        tblJournal.getActionMap().put("delete", actionDelete);
    }
    private final Action actionDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            removeJournal();
        }
    };

    public void initMain() {
        batchLock(!Global.batchLock);
        initTable();
        accountRepo.getDefaultCash().subscribe((coa) -> {
            if (coa != null) {
                srcAcc = coa.getKey().getCoaCode();
                lblCash.setText(coa.getCoaNameEng());
            } else {
                lblCash.setText("Configure Cash Account.");
                disableForm(false);
            }
        });

    }

    private void disableForm(boolean s) {
        txtVouDate.setEnabled(s);
        txtVouNo.setEnabled(s);
        tblJournal.setEnabled(s);
        btnSave.setEnabled(s);
    }

    private void initTable() {
        accountRepo.getDefaultDepartment().subscribe((t) -> {
            tableModel.setDepartment(t);
        });
        txtVouDate.setDate(Util1.getTodayDate());
        tblJournal.setModel(tableModel);
        tblJournal.setCellSelectionEnabled(true);
        tableModel.setParent(tblJournal);
        tableModel.setAccountRepo(accountRepo);
        tableModel.setVouType(vouType);
        tableModel.setTtlAmt(txtAmt);
        tblJournal.getTableHeader().setFont(Global.lableFont);
        tblJournal.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        Mono<List<DepartmentA>> monoDep = accountRepo.getDepartment();
        monoDep.subscribe((t) -> {
            tblJournal.getColumnModel().getColumn(0).setCellEditor(new DepartmentCellEditor(t));
        });
        tblJournal.getColumnModel().getColumn(1).setCellEditor(new AutoClearEditor());
        tblJournal.getColumnModel().getColumn(2).setCellEditor(new TraderCellEditor(accountRepo));
        tblJournal.getColumnModel().getColumn(3).setCellEditor(new COA3CellEditor(accountRepo, 3));
        userRepo.getCurrency().subscribe((t) -> {
            tblJournal.getColumnModel().getColumn(4).setCellEditor(new CurrencyEditor(t));
        });
        tblJournal.getColumnModel().getColumn(5).setCellEditor(new AutoClearEditor());
        tblJournal.getColumnModel().getColumn(0).setPreferredWidth(10);//dep
        tblJournal.getColumnModel().getColumn(1).setPreferredWidth(240);//des
        tblJournal.getColumnModel().getColumn(2).setPreferredWidth(240);//cus
        tblJournal.getColumnModel().getColumn(3).setPreferredWidth(240);//acc
        tblJournal.getColumnModel().getColumn(4).setPreferredWidth(20);//currency
        tblJournal.getColumnModel().getColumn(5).setPreferredWidth(60);//dr

        tblJournal.setDefaultRenderer(Double.class, new DecimalFormatRender());
        tblJournal.setDefaultRenderer(Object.class, new DecimalFormatRender());
        sorter = new TableRowSorter<>(tblJournal.getModel());
        tblJournal.setRowSorter(sorter);
        tblJournal.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        txtAmt.setFormatterFactory(Util1.getDecimalFormat());
        clear();
        searchDetail();
    }

    private void searchDetail() {
        if (this.listVGl != null) {
            if (!this.listVGl.isEmpty()) {
                Gl vgl = listVGl.get(0);
                txtVouDate.setDate(vgl.getGlDate());
                txtRefrence.setText(vgl.getReference());
                txtVouNo.setText(vgl.getGlVouNo());
                txtFrom.setText(vgl.getFromDes());
                txtFor.setText(vgl.getForDes());
                txtNa.setText(vgl.getNarration());
                tableModel.setListVGl(listVGl);
                lblStatus.setText("EDIT");
                accountRepo.findCOA(vgl.getSrcAccCode()).subscribe((coa) -> {
                    lblCash.setText(coa == null ? null : coa.getCoaNameEng());
                });
                tableModel.addEmptyRow();
                tableModel.calAmount();
            }
        }
    }

    private boolean saveVoucher(boolean print) {
        if (isValidEntry() && isValidData()) {
            List<Gl> list = tableModel.getListVGl();
            if (lblStatus.getText().equals("EDIT")) {
                list.get(0).setEdit(tableModel.isEdit());
                list.get(0).setDelList(tableModel.getDelList());
            }
            accountRepo.saveGl(list).subscribe((t) -> {
                if (t != null) {
                    if (print) {
                        String tranSource = t.getTranSource();
                        String glVouNo = t.getGlVouNo();
                        Gl gl = new Gl();
                        gl.setTranSource(tranSource);
                        gl.setGlVouNo(glVouNo);
                        this.dispose();
                        observer.selected("print", gl);
                    }
                    clear();
                }
            });

        }
        return true;
    }

    private boolean isValidEntry() {
        return ProUtil.isValidDate(txtVouDate.getDate());
    }

    public boolean isValidData() {
        for (Gl g : tableModel.getListVGl()) {
            g.setSrcAccCode(srcAcc);
            g.setGlDate(txtVouDate.getDate());
            g.setReference(txtRefrence.getText());
            g.setFromDes(txtFrom.getText());
            g.setForDes(txtFor.getText());
            g.setNarration(txtNa.getText());
            g.setMacId(Global.macId);
            g.setTranSource(vouType);
            g.setGlVouNo(txtVouNo.getText());
            if (lblStatus.getText().equals("EDIT")) {
                g.setModifyBy(Global.loginUser.getUserCode());
            }
            if (g.getAccCode() != null) {
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

    private void initKeyListener() {
        txtVouDate.getDateEditor().getUiComponent().setName("txtVouDate");
        txtVouDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtFor.addKeyListener(this);
        txtFrom.addKeyListener(this);
        txtNa.addKeyListener(this);
        txtRefrence.addKeyListener(this);

    }

    public void clear() {
        txtVouDate.setDate(Util1.getTodayDate());
        txtAmt.setValue(0.0);
        txtRefrence.setText(null);
        txtFrom.setText(null);
        txtFor.setText(null);
        txtNa.setText(null);
        txtVouNo.setText(null);
        lblStatus.setText("NEW");
        lblStatus.setForeground(Color.green);
        txtVouNo.setEditable(false);
        tableModel.clear();
        txtVouDate.getDateEditor().getUiComponent().requestFocus();
    }

    private void removeJournal() {
        if (tblJournal.getSelectedRow() >= 0) {
            int selectRow = tblJournal.convertRowIndexToModel(tblJournal.getSelectedRow());
            tableModel.removeJournal(selectRow);
        }
    }

    private void focusTable() {
        int row = tblJournal.getRowCount();
        if (row > 0) {
            tblJournal.setColumnSelectionInterval(0, 0);
            tblJournal.setRowSelectionInterval(row - 1, row - 1);
            tblJournal.requestFocus();
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
        jLabel5 = new javax.swing.JLabel();
        txtNa = new javax.swing.JTextField();
        lable1 = new javax.swing.JLabel();
        txtFrom = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtFor = new javax.swing.JTextField();
        lblCash = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblJournal = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        txtAmt = new javax.swing.JFormattedTextField();
        lblStatus = new javax.swing.JLabel();
        btnSave = new javax.swing.JButton();
        btnSave2 = new javax.swing.JButton();

        jLabel3.setText("jLabel3");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Journal Voucher");
        setFont(Global.textFont);
        setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
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
        txtVouDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtVouDateKeyReleased(evt);
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

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Narration");

        txtNa.setFont(Global.textFont);
        txtNa.setName("txtNa"); // NOI18N
        txtNa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNaActionPerformed(evt);
            }
        });

        lable1.setFont(Global.lableFont);
        lable1.setText("From");

        txtFrom.setFont(Global.textFont);
        txtFrom.setName("txtFrom"); // NOI18N
        txtFrom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFromActionPerformed(evt);
            }
        });

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("For");

        txtFor.setFont(Global.textFont);
        txtFor.setName("txtFor"); // NOI18N
        txtFor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtForActionPerformed(evt);
            }
        });

        lblCash.setFont(Global.menuFont);
        lblCash.setForeground(Global.selectionColor);
        lblCash.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCash.setText("Cash");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtVouNo)
                    .addComponent(txtVouDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lable1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFor)
                    .addComponent(txtFrom))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtRefrence)
                    .addComponent(txtNa))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblCash, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtRefrence, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(txtNa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(lblCash, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lable1)))
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtVouDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtFor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))))
                .addGap(10, 10, 10))
        );

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

        txtAmt.setEditable(false);
        txtAmt.setFormatterFactory(Util1.getDecimalFormat());
        txtAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAmt.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtAmt.setEnabled(false);
        txtAmt.setFont(Global.amtFont);
        txtAmt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAmtActionPerformed(evt);
            }
        });

        lblStatus.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lblStatus.setText("NEW");

        btnSave.setFont(Global.lableFont);
        btnSave.setText("Save");
        btnSave.setName("btnSave"); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnSave2.setFont(Global.lableFont);
        btnSave2.setText("Print");
        btnSave2.setName("btnSave"); // NOI18N
        btnSave2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSave2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE)
                .addGap(613, 613, 613)
                .addComponent(btnSave)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSave2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSave2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
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

    private void txtAmtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAmtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAmtActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        saveVoucher(false);
    }//GEN-LAST:event_btnSaveActionPerformed

    private void tblJournalKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblJournalKeyReleased
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            removeJournal();
        }
    }//GEN-LAST:event_tblJournalKeyReleased

    private void txtVouDateKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtVouDateKeyReleased
        // TODO add your handling code here:

    }//GEN-LAST:event_txtVouDateKeyReleased

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
        log.info("closed.");
        if (observer != null) {
            observer.selected("SEARCH", "SEARCH");
        }
    }//GEN-LAST:event_formWindowClosed

    private void txtVouDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVouDateFocusLost
        // TODO add your handling code here:

    }//GEN-LAST:event_txtVouDateFocusLost

    private void txtVouDatePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtVouDatePropertyChange
        // TODO add your handling code here:
        if (txtVouDate.getDate() != null) {
            if (!ProUtil.isValidDate(txtVouDate.getDate())) {
                txtVouDate.setDate(Util1.getTodayDate());
                txtVouDate.requestFocus();
            }
        }
    }//GEN-LAST:event_txtVouDatePropertyChange

    private void txtNaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNaActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_txtNaActionPerformed

    private void btnSave2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSave2ActionPerformed
        // TODO add your handling code here:
        saveVoucher(true);
    }//GEN-LAST:event_btnSave2ActionPerformed

    private void txtFromActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFromActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFromActionPerformed

    private void txtForActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtForActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtForActionPerformed

    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSave2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lable1;
    private javax.swing.JLabel lblCash;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblJournal;
    private javax.swing.JFormattedTextField txtAmt;
    private javax.swing.JTextField txtFor;
    private javax.swing.JTextField txtFrom;
    private javax.swing.JTextField txtNa;
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
        if (sourceObj instanceof JTextField jTextField) {
            ctrlName = jTextField.getName();
        } else if (sourceObj instanceof JTextFieldDateEditor jTextFieldDateEditor) {
            ctrlName = jTextFieldDateEditor.getName();
        }
        switch (ctrlName) {
            case "txtVouDate" -> {
                if (isEnter(e)) {
                    if (e.getSource() instanceof JTextFieldDateEditor txt) {
                        String date = ((JTextFieldDateEditor) txt).getText();
                        if (date.length() == 8 || date.length() == 6) {
                            txtVouDate.setDate(Util1.formatDate(date));
                        }
                        if (txtVouDate.getDate() != null) {
                            if (!ProUtil.isValidDate(txtVouDate.getDate())) {
                                txtVouDate.setDate(Util1.getTodayDate());
                            }
                        }
                        txtFrom.requestFocus();
                    }
                }
            }
            case "txtFrom" -> {
                if (isEnter(e)) {
                    txtFor.requestFocus();
                }
            }
            case "txtFor" -> {
                if (isEnter(e)) {
                    txtRefrence.requestFocus();
                }
            }
            case "txtRefrence" -> {
                if (isEnter(e)) {
                    txtNa.requestFocus();
                }
            }
            case "txtNa" -> {
                if (isEnter(e)) {
                    focusTable();
                }
            }
        }
    }

    private boolean isEnter(KeyEvent e) {
        return e.getKeyCode() == KeyEvent.VK_ENTER;
    }

}
