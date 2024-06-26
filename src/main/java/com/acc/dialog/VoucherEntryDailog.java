/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.dialog;

import com.repo.AccountRepo;
import com.acc.common.CrDrVoucherEntryTableModel;
import com.acc.editor.COA3CellEditor;
import com.acc.editor.COAAutoCompleter;
import com.acc.editor.DepartmentCellEditor;
import com.acc.editor.DespEditor;
import com.acc.editor.TraderCellEditor;
import com.acc.model.ChartOfAccount;
import com.acc.model.Gl;
import com.common.ComponentUtil;
import com.common.DateLockUtil;
import com.common.DecimalFormatRender;
import com.common.Global;
import com.common.ProUtil;
import com.common.RowHeader;
import com.common.SelectionObserver;
import com.common.Util1;
import com.user.editor.AutoClearEditor;
import com.toedter.calendar.JTextFieldDateEditor;
import com.repo.UserRepo;
import com.user.editor.CurrencyAutoCompleter;
import com.user.editor.ProjectAutoCompleter;
import com.user.model.Currency;
import com.user.model.Project;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClientRequestException;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class VoucherEntryDailog extends javax.swing.JDialog implements KeyListener {

    private final CrDrVoucherEntryTableModel tableModel = new CrDrVoucherEntryTableModel();

    private TableRowSorter<TableModel> sorter;
    private String vouType;
    @Setter
    private SelectionObserver observer;
    @Setter
    private AccountRepo accountRepo;
    @Setter
    private UserRepo userRepo;
    private COAAutoCompleter completer;
    private CurrencyAutoCompleter currencyAutoCompleter;
    private ProjectAutoCompleter projectAutoCompleter;

    public void setVouType(String vouType) {
        this.vouType = vouType;
        setTitle(vouType.equals("DR") ? "Payment / Debit Voucher" : "Receipt / Credit Voucher");
        tableModel.setVouType(vouType);
        lable1.setText(vouType.equals("DR") ? "To" : "From");
    }

    private void setStatus(String status) {
        lblStatus.setText(status);
        lblStatus.setForeground(status.equals("NEW") ? Color.GREEN : Color.BLUE);
        txtVouNo.setEditable(status.equals("NEW"));
    }

    /**
     * Creates new form JournalEntryDialog
     *
     * @param frame
     */
    public VoucherEntryDailog(JFrame frame) {
        super(frame, true);
        initComponents();
        initKeyListener();
        keyMapping();
    }

    private void batchLock(boolean lock) {
        ComponentUtil.enableForm(this, lock);
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
        initTextBox();
        initCompleter();
        initTable();
        initRowHeader();
    }

    private void initTextBox() {
        ComponentUtil.setTextProperty(panelFooter);
    }

    private void initRowHeader() {
        RowHeader header = new RowHeader();
        JList list = header.createRowHeader(tblJournal, 30);
        scroll.setRowHeaderView(list);
    }

    private void initCompleter() {
        completer = new COAAutoCompleter(txtCB, null, false);
        currencyAutoCompleter = new CurrencyAutoCompleter(txtCur, null);
        projectAutoCompleter = new ProjectAutoCompleter(txtProject, null, false);
        accountRepo.getCashBank().doOnSuccess((t) -> {
            completer.setListCOA(t);
        }).subscribe();
        accountRepo.getDefaultCash().doOnSuccess((coa) -> {
            completer.setCoa(coa);
        }).subscribe();
        userRepo.getCurrency().doOnSuccess((t) -> {
            currencyAutoCompleter.setListCurrency(t);
        }).subscribe();
        userRepo.getDefaultCurrency().doOnSuccess((t) -> {
            currencyAutoCompleter.setCurrency(t);
        }).subscribe();
        userRepo.searchProject().doOnSuccess((t) -> {
            projectAutoCompleter.setListProject(t);
        }).subscribe();
    }

    private void assignDefault() {
        txtCur.setEnabled(ProUtil.isMultiCur());
        clear();
    }

    private void initTable() {
        txtVouDate.setDate(Util1.getTodayDate());
        tblJournal.setModel(tableModel);
        tblJournal.setCellSelectionEnabled(true);
        tableModel.setParent(tblJournal);
        tableModel.setAccountRepo(accountRepo);
        tableModel.setTtlAmt(txtAmt);
        tblJournal.getTableHeader().setFont(Global.lableFont);
        tblJournal.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        accountRepo.getDepartment().doOnSuccess((t) -> {
            tblJournal.getColumnModel().getColumn(0).setCellEditor(new DepartmentCellEditor(t));
        }).subscribe();
        tblJournal.getColumnModel().getColumn(1).setCellEditor(new DespEditor(accountRepo));
        tblJournal.getColumnModel().getColumn(2).setCellEditor(new TraderCellEditor(accountRepo));
        tblJournal.getColumnModel().getColumn(3).setCellEditor(new COA3CellEditor(accountRepo, 3));
        tblJournal.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());
        tblJournal.getColumnModel().getColumn(5).setCellEditor(new AutoClearEditor());
        tblJournal.getColumnModel().getColumn(6).setCellEditor(new AutoClearEditor());
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
    }

    public void searchDetail(String glVouNo) {
        if (glVouNo == null) {
            setStatus("NEW");
            assignDefault();
            observer.selected("progress", false);
            setVisible(true);
        } else {
            accountRepo.getVoucher(glVouNo).doOnSuccess((list) -> {
                if (list != null && !list.isEmpty()) {
                    Gl vgl = list.getFirst();
                    accountRepo.findCOA(vgl.getSrcAccCode()).doOnSuccess((coa) -> {
                        completer.setCoa(coa);
                    }).subscribe();
                    userRepo.findCurrency(vgl.getCurCode()).doOnSuccess((t) -> {
                        currencyAutoCompleter.setCurrency(t);
                    }).subscribe();
                    userRepo.find(vgl.getProjectNo()).doOnSuccess((t) -> {
                        projectAutoCompleter.setProject(t);
                    }).subscribe();
                    if (DateLockUtil.isLockDate(vgl.getGlDate())) {
                        enableForm(false);
                        lblMessage.setText(DateLockUtil.MESSAGE);
                    } else {
                        enableForm(true);
                        lblMessage.setText("");
                    }
                    txtVouDate.setDate(Util1.convertToDate(vgl.getGlDate()));
                    txtRefrence.setText(vgl.getReference());
                    txtVouNo.setText(vgl.getGlVouNo());
                    txtFrom.setText(vgl.getFromDes());
                    txtFor.setText(vgl.getForDes());
                    txtNa.setText(vgl.getNarration());
                    setStatus("EDIT");
                }
                tableModel.setListVGl(list);
            }).doOnTerminate(() -> {
                tableModel.addEmptyRow();
                tableModel.calTotalAmount();
                observer.selected("progress", false);
                setVisible(true);
            }).subscribe();
        }

    }

    private void enableForm(boolean status) {
        ComponentUtil.enableForm(this, status);
    }

    private boolean saveVoucher(boolean print) {
        if (isValidEntry() && isValidData()) {
            if (DateLockUtil.isLockDate(txtVouDate.getDate())) {
                DateLockUtil.showMessage(this);
                return false;
            }
            progress.setIndeterminate(true);
            btnSave.setEnabled(false);
            List<Gl> list = tableModel.getListVGl();
            if (lblStatus.getText().equals("EDIT")) {
                list.getFirst().setDelList(tableModel.getDelList());
            }
            accountRepo.saveGl(list).doOnSuccess((t) -> {
                log.info("save.");
                if (print) {
                    String tranSource = t.getTranSource();
                    String glVouNo = t.getGlVouNo();
                    Gl gl = new Gl();
                    gl.setTranSource(tranSource);
                    gl.setGlVouNo(glVouNo);
                    setVisible(false);
                    observer.selected("print", gl);
                }
            }).doOnError((e) -> {
                log.info("error.");
                progress.setIndeterminate(false);
                btnSave.setEnabled(true);
                if (e instanceof WebClientRequestException) {
                    int yn = JOptionPane.showConfirmDialog(this, "Internet Offline. Try Again?", "Offline", JOptionPane.YES_OPTION, JOptionPane.ERROR_MESSAGE);
                    if (yn == JOptionPane.YES_OPTION) {
                        saveVoucher(print);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Error : " + e.getMessage(), "Server Error", JOptionPane.ERROR_MESSAGE);
                }
            }).doOnTerminate(() -> {
                clear();
            }).subscribe();

        }
        return true;
    }

    private boolean isValidEntry() {
        double amt = Util1.getDouble(txtAmt.getValue());
        if (amt <= 0) {
            JOptionPane.showMessageDialog(this, "Invalid Amount.", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        } else if (!ProUtil.isValidDate(txtVouDate.getDate())) {
            return false;
        }
        return true;
    }

    public boolean isValidData() {
        ChartOfAccount coa = completer.getCOA();
        Currency currency = currencyAutoCompleter.getCurrency();
        Project project = projectAutoCompleter.getProject();
        if (coa == null) {
            JOptionPane.showMessageDialog(tblJournal, "Please select Cash / Bank");
            return false;
        } else if (currency == null) {
            JOptionPane.showMessageDialog(tblJournal, "Please select Currency");
            return false;
        } else {
            for (Gl g : tableModel.getListData()) {
                g.setCurCode(currency.getCurCode());
                g.setSrcAccCode(coa.getKey().getCoaCode());
                g.setGlDate(Util1.toDateTime(txtVouDate.getDate()));
                g.setReference(txtRefrence.getText());
                g.setFromDes(txtFrom.getText());
                g.setForDes(txtFor.getText());
                g.setNarration(txtNa.getText());
                g.setMacId(Global.macId);
                g.setTranSource(vouType);
                g.setGlVouNo(txtVouNo.getText());
                g.setEdit(tableModel.isEdit());
                g.setProjectNo(project == null ? null : project.getKey().getProjectNo());
                if (lblStatus.getText().equals("EDIT")) {
                    g.setModifyBy(Global.loginUser.getUserCode());
                }
                double amt = g.getDrAmt() + g.getCrAmt();
                if (amt > 0) {
                    if (Util1.isNullOrEmpty(g.getAccCode())) {
                        JOptionPane.showMessageDialog(tblJournal, "Invalid Account.", "Validation", JOptionPane.WARNING_MESSAGE);
                        return false;
                    }
                }
                String srcAcc = coa.getKey().getCoaCode();
                if (!Util1.isNullOrEmpty(g.getAccCode())) {
                    if (g.getDeptCode() == null) {
                        JOptionPane.showMessageDialog(tblJournal, "Invalid Department.");
                        return false;
                    } else if (Util1.getDouble(g.getDrAmt()) + Util1.getDouble(g.getCrAmt()) == 0) {
                        JOptionPane.showMessageDialog(tblJournal, "Invalid Amount.");
                        return false;
                    } else if (srcAcc.equals(g.getAccCode())) {
                        JOptionPane.showMessageDialog(tblJournal, "Invalid, Same Account.");
                        return false;
                    }
                }
            }
            return true;
        }
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
        log.info("clear.");
        progress.setIndeterminate(false);
        btnSave.setEnabled(true);
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
        tableModel.addEmptyRow();
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
        jLabel6 = new javax.swing.JLabel();
        txtCB = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtCur = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtProject = new javax.swing.JTextField();
        scroll = new javax.swing.JScrollPane();
        tblJournal = new javax.swing.JTable();
        panelFooter = new javax.swing.JPanel();
        txtAmt = new javax.swing.JFormattedTextField();
        lblStatus = new javax.swing.JLabel();
        btnSave = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        lblMessage = new javax.swing.JLabel();
        progress = new javax.swing.JProgressBar();

        jLabel3.setText("jLabel3");

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
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
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

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Cash / Bank");

        txtCB.setFont(Global.textFont);

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Currency");

        txtCur.setFont(Global.textFont);

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Project No");

        txtProject.setFont(Global.textFont);

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
                    .addComponent(txtVouNo, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                    .addComponent(txtVouDate, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lable1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFor, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                    .addComponent(txtFrom, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtRefrence, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                    .addComponent(txtNa, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCur, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                    .addComponent(txtCB, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtProject, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
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
                            .addComponent(jLabel4)
                            .addComponent(jLabel6)
                            .addComponent(txtCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9)
                            .addComponent(txtProject, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel8)
                                .addComponent(txtCur, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel5)
                                .addComponent(txtNa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
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
        scroll.setViewportView(tblJournal);

        panelFooter.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

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

        btnSave.setBackground(Global.selectionColor);
        btnSave.setFont(Global.lableFont);
        btnSave.setForeground(new java.awt.Color(255, 255, 255));
        btnSave.setText("Save");
        btnSave.setName("btnSave"); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnPrint.setFont(Global.lableFont);
        btnPrint.setText("Print");
        btnPrint.setName("btnSave"); // NOI18N
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });

        lblMessage.setFont(Global.lableFont);
        lblMessage.setForeground(Color.red);
        lblMessage.setText("-");

        javax.swing.GroupLayout panelFooterLayout = new javax.swing.GroupLayout(panelFooter);
        panelFooter.setLayout(panelFooterLayout);
        panelFooterLayout.setHorizontalGroup(
            panelFooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFooterLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblStatus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblMessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSave)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnPrint)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panelFooterLayout.setVerticalGroup(
            panelFooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFooterLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnPrint, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblMessage))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(progress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scroll, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelFooter, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addComponent(scroll, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelFooter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        // TODO add your handling code here:
        saveVoucher(true);
    }//GEN-LAST:event_btnPrintActionPerformed

    private void txtFromActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFromActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFromActionPerformed

    private void txtForActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtForActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtForActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        observer.selected("refresh", "refrsh");
    }//GEN-LAST:event_formWindowClosing

    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel lable1;
    private javax.swing.JLabel lblMessage;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPanel panelFooter;
    private javax.swing.JProgressBar progress;
    private javax.swing.JScrollPane scroll;
    private javax.swing.JTable tblJournal;
    private javax.swing.JFormattedTextField txtAmt;
    private javax.swing.JTextField txtCB;
    private javax.swing.JTextField txtCur;
    private javax.swing.JTextField txtFor;
    private javax.swing.JTextField txtFrom;
    private javax.swing.JTextField txtNa;
    private javax.swing.JTextField txtProject;
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
                        txtVouDate.setDate(Util1.formatDate(date));
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
