/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.acc.entry;

import com.acc.common.AccountRepo;
import com.acc.common.DateAutoCompleter;
import com.acc.common.JournalClosingStockTableModel;
import com.acc.dialog.JournalEntryDialog;
import com.acc.editor.COA3CellEditor;
import com.acc.editor.CurrencyAAutoCompleter;
import com.acc.editor.CurrencyAEditor;
import com.acc.editor.DepartmentAutoCompleter;
import com.acc.editor.DepartmentCellEditor;
import com.acc.model.Department;
import com.acc.model.StockOP;
import com.common.Global;
import com.acc.model.ReportFilter;
import com.common.PanelControl;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.common.Util1;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.user.model.Currency;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 *
 * @author DELL
 */
@Component
public class JournalClosingStock extends javax.swing.JPanel implements SelectionObserver, PanelControl {

    private final JournalClosingStockTableModel tableModel = new JournalClosingStockTableModel();
    private DateAutoCompleter dateAutoCompleter;
    private DepartmentAutoCompleter departmentAutoCompleter;
    private CurrencyAAutoCompleter currencyAAutoCompleter;
    private JProgressBar progress;
    private SelectionObserver observer;
    private final JournalEntryDialog dialog = new JournalEntryDialog();
    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private WebClient accountApi;
    private List<Department> listDep;
    private List<Currency> listCurrency;

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
     * Creates new form Journal
     */
    public JournalClosingStock() {
        initComponents();
        initListener();
        actionMapping();
    }

    public void initMain() {
        initCompleter();
        initTable();
        searchJournal();
    }

    private void initListener() {
        txtDep.addFocusListener(fa);
        txtCur.addFocusListener(fa);
    }

    private final FocusAdapter fa = new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            if (e.getSource() instanceof JTextField txt) {
                txt.selectAll();
            }
        }
    };

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
        int yes_no;
        if (tblJournal.getSelectedRow() >= 0) {
            StockOP op = tableModel.getGl(selectRow);
            if (op.getKey().getTranCode() != null) {
                yes_no = JOptionPane.showConfirmDialog(Global.parentForm, "Are you sure to delete?",
                        "Delete", JOptionPane.YES_NO_OPTION);
                if (yes_no == 0) {
                    if (accountRepo.delete(op.getKey())) {
                        tableModel.delete(selectRow);
                        focusOnTable();
                    }
                }
            }
        }

    }

    private void searchJournal() {
        progress.setIndeterminate(true);
        ReportFilter filter = new ReportFilter(Global.compCode, Global.macId);
        filter.setFromDate(Util1.toDateStrMYSQL(dateAutoCompleter.getStDate(), Global.dateFormat));
        filter.setToDate(Util1.toDateStrMYSQL(dateAutoCompleter.getEndDate(), Global.dateFormat));
        filter.setDeptCode(departmentAutoCompleter.getDepartment().getKey().getDeptCode());
        filter.setCurCode(currencyAAutoCompleter.getCurrency().getCurCode());
        Mono<ResponseEntity<List<StockOP>>> result = accountApi
                .post()
                .uri("/account/search-stock-op")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .toEntityList(StockOP.class);
        result.subscribe((t) -> {
            tableModel.setListGV(t.getBody());
            tableModel.addNewRow();
            lblCount.setText(tableModel.getListSize() + "");
            progress.setIndeterminate(false);
            focusOnTable();
        });
    }

    public void openJournalEntryDialog(String glVou, String status) {
        dialog.setAccountRepo(accountRepo);
        dialog.setVouNo(glVou);
        dialog.setStatus(status);
        dialog.initMain();
        dialog.setSize(Global.width - 100, Global.height - 100);
        dialog.setIconImage(Global.parentForm.getIconImage());
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void initCompleter() {
        listDep = accountRepo.getDepartment();
        listCurrency = accountRepo.getCurrency();
        dateAutoCompleter = new DateAutoCompleter(txtDate, Global.listDate);
        dateAutoCompleter.setSelectionObserver(this);
        departmentAutoCompleter = new DepartmentAutoCompleter(txtDep, listDep, null, true, false);
        departmentAutoCompleter.setObserver(this);
        currencyAAutoCompleter = new CurrencyAAutoCompleter(txtCur, listCurrency, null, true);
        currencyAAutoCompleter.setSelectionObserver(this);

    }

    private void initTable() {
        tableModel.setParent(tblJournal);
        tableModel.setDepartment(accountRepo.getDefaultDepartment());
        tableModel.setAccountRepo(accountRepo);
        tblJournal.setModel(tableModel);
        tblJournal.getTableHeader().setFont(Global.tblHeaderFont);
        tblJournal.setRowHeight(Global.tblRowHeight);
        tblJournal.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblJournal.getColumnModel().getColumn(0).setPreferredWidth(5);//Date
        tblJournal.getColumnModel().getColumn(1).setPreferredWidth(1);//Vou
        tblJournal.getColumnModel().getColumn(2).setPreferredWidth(5);//Ref
        tblJournal.getColumnModel().getColumn(3).setPreferredWidth(400);//Ref
        tblJournal.getColumnModel().getColumn(4).setPreferredWidth(1);//Ref
        tblJournal.getColumnModel().getColumn(5).setPreferredWidth(100);//amt
        tblJournal.setShowGrid(true);
        tblJournal.setCellSelectionEnabled(true);
        tblJournal.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblJournal.getColumnModel().getColumn(0).setCellEditor(new AutoClearEditor());
        tblJournal.getColumnModel().getColumn(1).setCellEditor(new DepartmentCellEditor(listDep));
        tblJournal.getColumnModel().getColumn(2).setCellEditor(new COA3CellEditor(accountApi, 3));
        tblJournal.getColumnModel().getColumn(3).setCellEditor(new COA3CellEditor(accountApi, 3));
        tblJournal.getColumnModel().getColumn(4).setCellEditor(new CurrencyAEditor(listCurrency));
        tblJournal.getColumnModel().getColumn(5).setCellEditor(new AutoClearEditor());

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
        jScrollPane1 = new javax.swing.JScrollPane();
        tblJournal = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtDate = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtDep = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtCur = new javax.swing.JTextField();

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

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(lblCount, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(lblCount))
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
                    .addComponent(txtCur, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observer.selected("control", this);
    }//GEN-LAST:event_formComponentShown


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
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
    }

    @Override
    public String panelName() {
        return this.getName();
    }
}
