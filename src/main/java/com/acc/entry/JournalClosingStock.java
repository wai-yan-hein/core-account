/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.acc.entry;

import com.repo.AccountRepo;
import com.acc.common.ColumnHeaderListener;
import com.acc.editor.DateAutoCompleter;
import com.acc.common.JournalClosingStockTableModel;
import com.acc.dialog.FindDialog;
import com.acc.editor.COA3CellEditor;
import com.user.editor.CurrencyAutoCompleter;
import com.acc.editor.DepartmentAutoCompleter;
import com.acc.editor.DepartmentCellEditor;
import com.acc.model.DepartmentA;
import com.acc.model.StockOP;
import com.common.ComponentUtil;
import com.common.DateLockUtil;
import com.common.Global;
import com.common.PanelControl;
import com.common.ReportFilter;
import com.common.SelectionObserver;
import com.user.editor.AutoClearEditor;
import com.repo.UserRepo;
import com.user.editor.CurrencyEditor;
import com.user.editor.ProjectAutoCompleter;
import com.user.editor.ProjectCellEditor;
import com.user.model.Currency;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import reactor.core.publisher.Mono;

/**
 *
 * @author DELL
 */
public class JournalClosingStock extends javax.swing.JPanel implements SelectionObserver, PanelControl {

    private final JournalClosingStockTableModel tableModel = new JournalClosingStockTableModel();
    private DateAutoCompleter dateAutoCompleter;
    private DepartmentAutoCompleter departmentAutoCompleter;
    private CurrencyAutoCompleter currencyAAutoCompleter;
    private ProjectAutoCompleter projectAutoCompleter;
    private JProgressBar progress;
    private SelectionObserver observer;
    private AccountRepo accountRepo;
    private UserRepo userRepo;
    private Mono<List<Currency>> monoCur;
    private Mono<List<DepartmentA>> monoDep;
    private int row = 0;
    private int column = 0;
    private FindDialog findDialog;

    public void setAccountRepo(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    /**
     * Creates new form Journal
     */
    public JournalClosingStock() {
        initComponents();
        initListener();
        actionMapping();
    }

    private void batchLock(boolean lock) {
        tblJournal.setEnabled(lock);
        observer.selected("save", lock);
        observer.selected("delete", lock);
    }

    public void initMain() {
        batchLock(!Global.batchLock);
        initCompleter();
        initTable();
        initFindDialog();
        searchJournal();
    }

    private void initFindDialog() {
        findDialog = new FindDialog(Global.parentForm, tblJournal);
    }

    private void initListener() {
        ComponentUtil.addFocusListener(this);
        ComponentUtil.setTextProperty(this);
        tblJournal.addMouseListener(new ColumnHeaderListener(tblJournal));
    }

    private void actionMapping() {
        String solve = "delete";
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        tblJournal.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, solve);
        tblJournal.getActionMap().put(solve, actionDelete);
    }
    private final Action actionDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            deleteVoucher();
        }
    };

    private void deleteVoucher() {
        int selectRow = tblJournal.convertRowIndexToModel(tblJournal.getSelectedRow());
        if (selectRow >= 0) {
            StockOP op = tableModel.getGl(selectRow);
            if (op.isTranLock()) {
                DateLockUtil.showMessage(this);
                return;
            }
            if (op.getKey().getTranCode() != null) {
                int yn = JOptionPane.showConfirmDialog(this, "Are you sure to delete?",
                        "Delete", JOptionPane.YES_NO_OPTION);
                if (yn == 0) {
                    accountRepo.delete(op.getKey()).doOnSuccess((t) -> {
                        if (t) {
                            tableModel.delete(selectRow);
                            focusOnTable();
                        }
                    }).subscribe();
                }
            }
        }

    }

    private String getCurCode() {
        if (currencyAAutoCompleter == null || currencyAAutoCompleter.getCurrency() == null) {
            return Global.currency;
        }
        return currencyAAutoCompleter.getCurrency().getCurCode();
    }

    private String getDeptCode() {
        return departmentAutoCompleter.getDepartment() == null ? "-" : departmentAutoCompleter.getDepartment().getKey().getDeptCode();
    }

    private void searchJournal() {
        progress.setIndeterminate(true);
        ReportFilter filter = new ReportFilter(Global.macId, Global.compCode, Global.deptId);
        filter.setFromDate(dateAutoCompleter.getDateModel().getStartDate());
        filter.setToDate(dateAutoCompleter.getDateModel().getEndDate());
        filter.setDeptCode(getDeptCode());
        filter.setCurCode(getCurCode());
        filter.setProjectNo(projectAutoCompleter.getProject().getKey().getProjectNo());
        accountRepo.searchOP(filter).doOnSuccess((t) -> {
            checkDateLock(t);
            tableModel.setListGV(t);
        }).doOnTerminate(() -> {
            tableModel.addNewRow();
            lblCount.setText(tableModel.getListSize() + "");
            progress.setIndeterminate(false);
            calTotal();
            ComponentUtil.scrollTable(tblJournal, row, column);
        }).subscribe();
    }

    private void calTotal() {
        double ttlAmt = tableModel.getListGV().stream().mapToDouble((value) -> value.getClAmt()).sum();
        txtTotal.setValue(ttlAmt);
    }

    private void checkDateLock(List<StockOP> list) {
        list.forEach((t) -> {
            if (DateLockUtil.isLockDate(t.getTranDate())) {
                t.setTranLock(true);
            }
        });
    }

    private void initCompleter() {
        monoDep = accountRepo.getDepartment();
        monoCur = userRepo.getCurrency();
        departmentAutoCompleter = new DepartmentAutoCompleter(txtDep, null, true, true);
        departmentAutoCompleter.setObserver(this);
        monoDep.doOnSuccess((t) -> {
            departmentAutoCompleter.setListDepartment(t);
        }).subscribe();
        dateAutoCompleter = new DateAutoCompleter(txtDate);
        dateAutoCompleter.setObserver(this);
        projectAutoCompleter = new ProjectAutoCompleter(txtProjectNo, null, true);
        projectAutoCompleter.setObserver(this);
        currencyAAutoCompleter = new CurrencyAutoCompleter(txtCur, null);
        currencyAAutoCompleter.setObserver(this);
        monoCur.subscribe((t) -> {
            currencyAAutoCompleter.setListCurrency(t);
        });
        userRepo.getDefaultCurrency().doOnSuccess((c) -> {
            currencyAAutoCompleter.setCurrency(c);
        }).subscribe();
        userRepo.searchProject().doOnSuccess((t) -> {
            projectAutoCompleter.setListProject(t);
        }).subscribe();

    }

    private void initTable() {
        accountRepo.getDefaultDepartment().doOnSuccess((t) -> {
            tableModel.setDepartment(t);
        }).subscribe();
        tableModel.setParent(tblJournal);
        tableModel.setProgress(progress);
        tableModel.setAccountRepo(accountRepo);
        tblJournal.setModel(tableModel);
        tblJournal.getTableHeader().setFont(Global.tblHeaderFont);
        tblJournal.setRowHeight(Global.tblRowHeight);
        tblJournal.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblJournal.getColumnModel().getColumn(0).setPreferredWidth(5);//Date
        tblJournal.getColumnModel().getColumn(1).setPreferredWidth(1);//dep
        tblJournal.getColumnModel().getColumn(2).setPreferredWidth(5);//code
        tblJournal.getColumnModel().getColumn(3).setPreferredWidth(450);//name
        tblJournal.getColumnModel().getColumn(4).setPreferredWidth(100);//project
        tblJournal.getColumnModel().getColumn(5).setPreferredWidth(1);//cur
        tblJournal.getColumnModel().getColumn(6).setPreferredWidth(150);//amt
        tblJournal.setShowGrid(true);
        tblJournal.setCellSelectionEnabled(true);
        tblJournal.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblJournal.getColumnModel().getColumn(0).setCellEditor(new AutoClearEditor());
        monoDep.doOnSuccess((t) -> {
            tblJournal.getColumnModel().getColumn(1).setCellEditor(new DepartmentCellEditor(t));
        }).subscribe();
        tblJournal.getColumnModel().getColumn(2).setCellEditor(new COA3CellEditor(accountRepo, 3));
        tblJournal.getColumnModel().getColumn(3).setCellEditor(new COA3CellEditor(accountRepo, 3));
        userRepo.searchProject().doOnSuccess((t) -> {
            tblJournal.getColumnModel().getColumn(4).setCellEditor(new ProjectCellEditor(t));
        }).subscribe();
        monoCur.doOnSuccess((t) -> {
            tblJournal.getColumnModel().getColumn(5).setCellEditor(new CurrencyEditor(t));
        }).subscribe();
        tblJournal.getColumnModel().getColumn(6).setCellEditor(new AutoClearEditor());

    }

    private void focusOnTable() {
        int rc = tblJournal.getRowCount();
        if (rc > 1) {
            tblJournal.setRowSelectionInterval(rc - 1, rc - 1);
            tblJournal.setColumnSelectionInterval(0, 0);
            tblJournal.requestFocus();
        } else {
            txtDate.requestFocusInWindow();
        }
    }

    private void observeMain() {
        observer.selected("control", this);
        observer.selected("save", false);
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

        jPanel2 = new javax.swing.JPanel();
        lblCount = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtTotal = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblJournal = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtDate = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtDep = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtCur = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtProjectNo = new javax.swing.JTextField();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblCount.setFont(Global.lableFont);
        lblCount.setText("0");

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Records :");

        txtTotal.setEditable(false);

        jLabel4.setText("Total :");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(lblCount, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(lblCount)
                    .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addContainerGap())
        );

        tblJournal.setFont(Global.textFont);
        tblJournal.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblJournal.setName("tblJournal"); // NOI18N
        tblJournal.setRowHeight(Global.tblRowHeight);
        tblJournal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblJournalMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblJournal);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setFont(Global.lableFont);
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Date");

        txtDate.setFont(Global.textFont);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Department");

        txtDep.setFont(Global.textFont);

        jLabel7.setFont(Global.lableFont);
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Currency");

        txtCur.setFont(Global.textFont);

        jLabel3.setFont(Global.lableFont);
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Project No");

        txtProjectNo.setFont(Global.textFont);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtDate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtDep)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtProjectNo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtCur)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtDep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtCur, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtProjectNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 932, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observeMain();
    }//GEN-LAST:event_formComponentShown

    private void tblJournalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblJournalMouseClicked
        // TODO add your handling code here:
        row = tblJournal.convertRowIndexToModel(tblJournal.getSelectedRow());
        column = tblJournal.convertColumnIndexToModel(tblJournal.getSelectedColumn());

    }//GEN-LAST:event_tblJournalMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCount;
    private javax.swing.JTable tblJournal;
    private javax.swing.JTextField txtCur;
    private javax.swing.JTextField txtDate;
    private javax.swing.JTextField txtDep;
    private javax.swing.JTextField txtProjectNo;
    private javax.swing.JFormattedTextField txtTotal;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {
        if (source != null) {
            searchJournal();
        }
    }

    @Override
    public void save() {
    }

    @Override
    public void delete() {
        deleteVoucher();
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
        searchJournal();
    }

    @Override
    public void filter() {
        findDialog.setVisible(!findDialog.isVisible());
    }

    @Override
    public String panelName() {
        return this.getName();
    }
}
