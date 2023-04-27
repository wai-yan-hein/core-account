/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.acc.entry;

import com.acc.common.AccountRepo;
import com.acc.common.DateAutoCompleter;
import com.acc.common.JournalTableModel;
import com.acc.dialog.JournalEntryDialog;
import com.acc.editor.DepartmentAutoCompleter;
import com.acc.model.DeleteObj;
import com.acc.model.Department;
import com.acc.model.Gl;
import com.common.Global;
import com.acc.model.ReportFilter;
import com.common.PanelControl;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.common.Util1;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 *
 * @author DELL
 */
@Component
public class Journal extends javax.swing.JPanel implements SelectionObserver, PanelControl {

    private final JournalTableModel tableModel = new JournalTableModel();
    private DateAutoCompleter dateAutoCompleter;
    private DepartmentAutoCompleter departmentAutoCompleter;
    private JProgressBar progress;
    private SelectionObserver observer;
    private JournalEntryDialog dialog;
    private int selectRow = 0;
    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private WebClient accountApi;

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
    public Journal() {
        initComponents();
        initListener();
    }

    public void initMain() {
        initCompleter();
        initTable();
        searchJournal();
    }

    private void deleteVoucher() {
        selectRow = tblJournal.convertRowIndexToModel(tblJournal.getSelectedRow());
        int yes_no;
        if (tblJournal.getSelectedRow() >= 0) {
            Gl gl = tableModel.getGl(selectRow);
            String glVouNo = gl.getGlVouNo();
            if (glVouNo != null) {
                yes_no = JOptionPane.showConfirmDialog(Global.parentForm, "Are you sure to delete journal?",
                        "Delete", JOptionPane.YES_NO_OPTION);
                if (yes_no == 0) {
                    DeleteObj obj = new DeleteObj();
                    obj.setGlVouNo(glVouNo);
                    obj.setCompCode(Global.compCode);
                    obj.setModifyBy(Global.loginUser.getUserCode());
                    accountRepo.deleteVoucher(obj).subscribe((t) -> {
                        if (t) {
                            tableModel.delete(selectRow);
                            focusOnTable();
                        }
                    });
                }
            }
        }
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

    private void initListener() {
        txtDep.addFocusListener(fa);
        txtRefrence.addFocusListener(fa);
        txtDesp.addFocusListener(fa);
        txtVouNo.addFocusListener(fa);
        txtRefrence.addActionListener(action);
        txtDesp.addActionListener(action);
    }
    private final ActionListener action = (ActionEvent e) -> {
        searchJournal();
    };
    private final FocusAdapter fa = new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            if (e.getSource() instanceof JTextField txt) {
                txt.selectAll();
            }
        }
    };

    private List<String> getListDep() {
        return departmentAutoCompleter == null ? new ArrayList<>() : departmentAutoCompleter.getListOption();
    }

    private void searchJournal() {
        progress.setIndeterminate(true);
        ReportFilter filter = new ReportFilter(Global.compCode, Global.macId);
        filter.setFromDate(Util1.toDateStrMYSQL(dateAutoCompleter.getStDate(), Global.dateFormat));
        filter.setToDate(Util1.toDateStrMYSQL(dateAutoCompleter.getEndDate(), Global.dateFormat));
        filter.setListDepartment(getListDep());
        filter.setDesp(txtDesp.getText());
        filter.setReference(txtRefrence.getText());
        filter.setGlVouNo(txtVouNo.getText());
        accountApi.post()
                .uri("/account/search-journal")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .bodyToFlux(Gl.class)
                .collectList()
                .subscribe((t) -> {
                    tableModel.setListGV(t);
                    lblCount.setText(tableModel.getListSize() + "");
                    progress.setIndeterminate(false);
                });
    }

    public void openJournalEntryDialog(String glVou, String status) {
        if (dialog == null) {
            dialog = new JournalEntryDialog();
            dialog.setAccountRepo(accountRepo);
            dialog.setAccountApi(accountApi);
            dialog.setSize(Global.width - 100, Global.height - 100);
            dialog.setIconImage(Global.parentForm.getIconImage());
            dialog.setLocationRelativeTo(null);
        }
        dialog.clear();
        dialog.setVouNo(glVou);
        dialog.setStatus(status);
        dialog.initMain();
        dialog.setVisible(true);
    }

    private void initCompleter() {
        dateAutoCompleter = new DateAutoCompleter(txtDate);
        dateAutoCompleter.setSelectionObserver(this);
        Mono<List<Department>> monoDep = accountRepo.getDepartment();
        monoDep.subscribe((t) -> {
            departmentAutoCompleter = new DepartmentAutoCompleter(txtDep, t, null, true, true);
            departmentAutoCompleter.setObserver(this);
        });
    }

    private void initTable() {
        tblJournal.setModel(tableModel);
        tblJournal.getTableHeader().setFont(Global.tblHeaderFont);
        tblJournal.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblJournal.getColumnModel().getColumn(0).setPreferredWidth(5);//Date
        tblJournal.getColumnModel().getColumn(1).setPreferredWidth(50);//Vou
        tblJournal.getColumnModel().getColumn(2).setPreferredWidth(400);//Ref
        tblJournal.getColumnModel().getColumn(3).setPreferredWidth(400);//Ref
        tblJournal.getColumnModel().getColumn(4).setPreferredWidth(50);//Ref
        tblJournal.setDefaultRenderer(Object.class, new TableCellRender());
        tblJournal.setDefaultRenderer(Double.class, new TableCellRender());
        tblJournal.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    if (tblJournal.getSelectedRow() >= 0) {
                        selectRow = tblJournal.convertRowIndexToModel(tblJournal.getSelectedRow());
                        Gl vGl = tableModel.getGl(selectRow);
                        openJournalEntryDialog(vGl.getGlVouNo(), "EDIT");
                    }
                }
            }

        });

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
        txtVouNo = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtRefrence = new javax.swing.JTextField();
        btnEntry = new javax.swing.JButton();
        txtDate = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtDep = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtDesp = new javax.swing.JTextField();

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

        txtVouNo.setFont(Global.shortCutFont);
        txtVouNo.setName("txtVouNo"); // NOI18N

        jLabel3.setFont(Global.lableFont);
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Voucher No");

        jLabel4.setFont(Global.lableFont);
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Refrence");

        txtRefrence.setFont(Global.shortCutFont);
        txtRefrence.setName("txtRefrence"); // NOI18N
        txtRefrence.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRefrenceActionPerformed(evt);
            }
        });

        btnEntry.setFont(Global.lableFont);
        btnEntry.setText("New Journal");
        btnEntry.setToolTipText("New Journal");
        btnEntry.setName("btnEntry"); // NOI18N
        btnEntry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEntryActionPerformed(evt);
            }
        });

        txtDate.setFont(Global.textFont);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Department");

        txtDep.setFont(Global.textFont);

        jLabel6.setFont(Global.lableFont);
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Description");

        txtDesp.setFont(Global.shortCutFont);
        txtDesp.setName("txtRefrence"); // NOI18N
        txtDesp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDespActionPerformed(evt);
            }
        });

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtVouNo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtRefrence)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtDesp)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEntry)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)
                        .addComponent(jLabel4)
                        .addComponent(txtRefrence, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnEntry, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel6)
                        .addComponent(txtDesp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtDep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void txtRefrenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRefrenceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRefrenceActionPerformed

    private void btnEntryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEntryActionPerformed
        // TODO add your handling code here:
        openJournalEntryDialog("-", "NEW");
    }//GEN-LAST:event_btnEntryActionPerformed

    private void txtDespActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDespActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDespActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEntry;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCount;
    private javax.swing.JTable tblJournal;
    private javax.swing.JTextField txtDate;
    private javax.swing.JTextField txtDep;
    private javax.swing.JTextField txtDesp;
    private javax.swing.JTextField txtRefrence;
    private javax.swing.JTextField txtVouNo;
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
