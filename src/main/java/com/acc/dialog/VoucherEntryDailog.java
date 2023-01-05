/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.dialog;

import com.acc.common.AccountRepo;
import com.acc.common.CrDrVoucherEntryTableModel;
import com.acc.editor.COA3CellEditor;
import com.acc.editor.CurrencyAAutoCompleter;
import com.acc.editor.DepartmentCellEditor;
import com.acc.editor.TraderCellEditor;
import com.acc.model.Gl;
import com.common.DecimalFormatRender;
import com.common.Global;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.editor.CurrencyEditor;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lenovo
 */
public class VoucherEntryDailog extends javax.swing.JDialog implements KeyListener {

    private static final Logger log = LoggerFactory.getLogger(VoucherEntryDailog.class);
    private final CrDrVoucherEntryTableModel tableModel = new CrDrVoucherEntryTableModel();

    private TableRowSorter<TableModel> sorter;
    private String glVouId = null;
    private CurrencyAAutoCompleter autoCompleter;
    private String status;
    private String vouType;
    private SelectionObserver observer;
    private AccountRepo accountRepo;
    private List<Gl> listVGl;

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

    public void setGlVouId(String glVouId) {
        this.glVouId = glVouId;
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

    private void keyMapping() {
        this.getRootPane().getInputMap().put(KeyStroke.getKeyStroke("F5"), "Save-Action");
        this.getRootPane().getActionMap().put("Save-Action", actionSave);
        this.getRootPane().getInputMap().put(KeyStroke.getKeyStroke("F6"), "Print-Action");
        this.getRootPane().getActionMap().put("Print-Action", actionPrint);

    }
    private final Action actionSave = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            log.info("saved.");
            saveVoucher();
        }
    };
    private final Action actionPrint = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            printVoucher();
        }
    };

    public void initTable() {
        txtVouDate.setDate(Util1.getTodayDate());
        tblJournal.setModel(tableModel);
        tblJournal.setCellSelectionEnabled(true);
        tableModel.setParent(tblJournal);
        tableModel.setAccountRepo(accountRepo);
        tableModel.setDepartment(accountRepo.getDefaultDepartment());
        tableModel.setVouType(vouType);
        tableModel.setTtlAmt(txtAmt);
        tblJournal.getTableHeader().setFont(Global.lableFont);
        tblJournal.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tblJournal.getColumnModel().getColumn(0).setCellEditor(new DepartmentCellEditor(accountRepo.getDepartment()));
        tblJournal.getColumnModel().getColumn(1).setCellEditor(new AutoClearEditor());
        tblJournal.getColumnModel().getColumn(2).setCellEditor(new TraderCellEditor(accountRepo));
        tblJournal.getColumnModel().getColumn(3).setCellEditor(new COA3CellEditor(accountRepo, 3));
        tblJournal.getColumnModel().getColumn(4).setCellEditor(new CurrencyEditor(accountRepo.getCurrency()));
        tblJournal.getColumnModel().getColumn(5).setCellEditor(new AutoClearEditor());
        tblJournal.getColumnModel().getColumn(0).setPreferredWidth(10);//dep
        tblJournal.getColumnModel().getColumn(1).setPreferredWidth(240);//des
        tblJournal.getColumnModel().getColumn(2).setPreferredWidth(240);//cus
        tblJournal.getColumnModel().getColumn(3).setPreferredWidth(240);//acc
        tblJournal.getColumnModel().getColumn(4).setPreferredWidth(60);//currency
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
            Gl vgl = listVGl.get(0);
            txtVouDate.setDate(vgl.getGlDate());
            txtRefrence.setText(vgl.getReference());
            txtVouNo.setText(vgl.getGlVouNo());
            tableModel.setListVGl(listVGl);
            lblStatus.setText("EDIT");
            tableModel.addEmptyRow();
            tableModel.calAmount();
        }
    }

    private boolean saveVoucher() {
        if (Util1.getDouble(txtAmt.getValue()) > 0) {
            List<Gl> list = tableModel.getListVGl();
            if (!list.isEmpty()) {
                
            }
        }
        return false;
    }

    private boolean isValidEntry(Gl vgl) {
        if (Objects.isNull(vgl.getDeptCode())) {
            return false;
        } else if (Objects.isNull(vgl.getAccCode())) {
            return false;
        } else if (!ProUtil.isValidDate(txtVouDate.getDate())) {
            return false;
        }
        return true;
    }

    private void initKeyListener() {
        txtVouDate.getDateEditor().getUiComponent().setName("txtDate");
        txtVouDate.getDateEditor().getUiComponent().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
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
                        txtRefrence.requestFocus();
                    }
                }
            }

        });

    }

    public void clear() {
        txtVouDate.setDate(Util1.getTodayDate());
        txtAmt.setValue(0.0);
        txtRefrence.setText(null);
        txtVouNo.setText(null);
        lblStatus.setText("NEW");
        lblStatus.setForeground(Color.green);
        txtVouNo.setEditable(false);
        tableModel.clear();
        txtVouDate.getDateEditor().getUiComponent().requestFocus();
    }

    private void removeJournal() {
        if (tblJournal.getCellEditor() != null) {
            tblJournal.getCellEditor().stopCellEditing();
        }
        if (tblJournal.getSelectedRow() >= 0) {
            int selectRow = tblJournal.convertRowIndexToModel(tblJournal.getSelectedRow());
            tableModel.removeJournal(selectRow);
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
        jScrollPane1 = new javax.swing.JScrollPane();
        tblJournal = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        txtAmt = new javax.swing.JFormattedTextField();
        lblStatus = new javax.swing.JLabel();
        btnPrint = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();

        jLabel3.setText("jLabel3");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Journal Voucher");
        setFont(Global.textFont);
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

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(txtVouDate, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtRefrence, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtRefrence, txtVouDate, txtVouNo});

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(txtVouDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtRefrence, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))))
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

        btnPrint.setFont(Global.lableFont);
        btnPrint.setText("Save & Print - F6");
        btnPrint.setName("btnSave"); // NOI18N
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });

        btnSave.setFont(Global.lableFont);
        btnSave.setText("Save - F5");
        btnSave.setName("btnSave"); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(459, 459, 459)
                .addComponent(btnSave)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnPrint)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnPrint)
                    .addComponent(btnSave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
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
        int row = tblJournal.getRowCount();
        if (row > 0) {
            tblJournal.setColumnSelectionInterval(0, 0);
            tblJournal.setRowSelectionInterval(row - 1, row - 1);
            tblJournal.requestFocus();
        }
    }//GEN-LAST:event_txtRefrenceActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
    }//GEN-LAST:event_formComponentShown

    private void txtAmtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAmtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAmtActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        saveVoucher();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        printVoucher();
    }//GEN-LAST:event_btnPrintActionPerformed

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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblJournal;
    private javax.swing.JFormattedTextField txtAmt;
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
    }

    private void printVoucher() {
        if (saveVoucher()) {
            this.dispose();
            String rpName = vouType.equals("DR") ? "Payment / Debit Voucher" : "Receipt / Credit Voucher";
            String reportPath = "report";
            String filePath = reportPath + File.separator + "VoucherA5";
            Map<String, Object> parameters = new HashMap();
            parameters.put("p_comp_name", Global.companyName);
            parameters.put("p_phone", Global.companyPhone);
            parameters.put("p_address", Global.companyAddress);
            parameters.put("p_vou_no", glVouId);
            parameters.put("p_report_name", rpName);

        }
    }

}
