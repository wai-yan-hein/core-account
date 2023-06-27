/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.acc.setup;

import com.acc.common.AccountRepo;
import com.acc.common.OpeningBalanceTableModel;
import com.acc.editor.COA3AutoCompleter;
import com.acc.model.OpeningBalance;
import com.acc.model.ChartOfAccount;
import com.acc.model.ReportFilter;
import com.acc.editor.COA3CellEditor;
import com.acc.editor.TraderCellEditor;
import com.acc.editor.DepartmentCellEditor;
import com.acc.editor.CurrencyAEditor;
import com.acc.editor.DepartmentAutoCompleter;
import com.acc.editor.TraderAAutoCompleter;
import com.user.editor.CurrencyAutoCompleter;
import com.acc.model.OpeningKey;
import com.common.SelectionObserver;
import com.common.PanelControl;
import com.common.Global;
import com.common.Util1;
import com.common.DecimalFormatRender;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import com.toedter.calendar.JTextFieldDateEditor;
import com.user.common.UserRepo;
import com.user.editor.ProjectAutoCompleter;
import com.user.editor.ProjectCellEditor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
//import com.cv.accountswing.util.Util1;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import lombok.extern.slf4j.Slf4j;

import net.coderazzi.filters.gui.AutoChoices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.coderazzi.filters.gui.TableFilterHeader;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author myoht
 */
@Slf4j
@Component
public class COAOpening extends javax.swing.JPanel implements SelectionObserver, PanelControl {

    private final OpeningBalanceTableModel openingTableModel = new OpeningBalanceTableModel();

    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private UserRepo userRepo;

    private DepartmentAutoCompleter departmenttAutoCompleter;
    private CurrencyAutoCompleter currencyAutoCompleter;
    private COA3AutoCompleter coaAutoCompleter;
    private TraderAAutoCompleter tradeAutoCompleter;
    private ProjectAutoCompleter projectAutoCompleter;
    private SelectionObserver observer;
    private TableFilterHeader filterHeader;
    private JProgressBar progress;

    /**
     * Creates new form COAOpening
     */
    public COAOpening() {
        initComponents();
        initFocusListener();
        actionMapping();
    }

    private void batchLock(boolean lock) {
        tblOpening.setEnabled(lock);
        btnGenerateZero.setEnabled(lock);
        observer.selected("save", lock);
        observer.selected("delete", lock);
    }

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }

    public SelectionObserver getObserver() {
        return observer;
    }

    public void setObservaer(SelectionObserver observer) {
        this.observer = observer;
    }

    private void actionMapping() {
        tblOpening.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");
        tblOpening.getActionMap().put("delete", new DeleteAction());
    }

    public class DeleteAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            deleteTran();
        }
    }

    private void deleteTran() {
        int row = tblOpening.convertRowIndexToModel(tblOpening.getSelectedRow());
        if (row >= 0) {
            OpeningBalance b = openingTableModel.getOpening(row);
            OpeningKey key = b.getKey();
            if (key != null) {
                int yn = JOptionPane.showConfirmDialog(this, "Are you sure to delete?");
                if (yn == JOptionPane.YES_OPTION) {
                    accountRepo.delete(key).subscribe((sucess) -> {
                        if (sucess) {
                            openingTableModel.deleteOpening(row);
                        }
                    }, (e) -> {
                        JOptionPane.showMessageDialog(this, e.getMessage());
                    });
                }
            } else {
                openingTableModel.deleteOpening(row);
            }
        }
    }

    public void initMain() {
        batchLock(!Global.batchLock);
        txtDate.setDate(Util1.parseDate(Global.startDate, "dd/MM/yyyy"));
        initComboBox();
        initTable();
        searchOpening();
    }

    // initialize data for combo box
    private void initComboBox() {
        accountRepo.getDepartment().subscribe((t) -> {
            departmenttAutoCompleter = new DepartmentAutoCompleter(txtDept, t, null, true, true);
            departmenttAutoCompleter.setObserver(this);
        });
        userRepo.getCurrency().subscribe((t) -> {
            currencyAutoCompleter = new CurrencyAutoCompleter(txtCurrency, t, null);
            currencyAutoCompleter.setObserver(this);
            userRepo.getDefaultCurrency().subscribe((c) -> {
                currencyAutoCompleter.setCurrency(c);
            });
        });
        coaAutoCompleter = new COA3AutoCompleter(txtCOA, accountRepo, null, true, 0);
        coaAutoCompleter.setSelectionObserver(this);
        projectAutoCompleter = new ProjectAutoCompleter(txtProjectNo, userRepo, null, true);
        projectAutoCompleter.setObserver(this);
    }

    private void initFocusListener() {
        txtDept.addFocusListener(fa);
        txtCOA.addFocusListener(fa);
        txtCurrency.addFocusListener(fa);
        txtDate.addFocusListener(fa);
        txtProjectNo.addFocusListener(fa);
    }

    private final FocusAdapter fa = new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            if (e.getSource() instanceof JTextField txt) {
                txt.selectAll();
            } else if (e.getSource() instanceof JTextFieldDateEditor ch) {
                ch.selectAll();
            }

        }
    };

    private void initTable() {
        tblOpening.setModel(openingTableModel);
        tblOpening.getTableHeader().setFont(Global.tblHeaderFont);
        tblOpening.getTableHeader().setPreferredSize(new Dimension(25, 25));
        tblOpening.setCellSelectionEnabled(true);
        tblOpening.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblOpening.setFont(Global.textFont);
        tblOpening.setShowGrid(true);
        tblOpening.setRowHeight(Global.tblRowHeight);
        tblOpening.setCellSelectionEnabled(true);
        openingTableModel.setOpdate(txtDate);
        openingTableModel.setTradeAutoCompleter(tradeAutoCompleter);
        openingTableModel.setParent(tblOpening);
        openingTableModel.setObserver(this);
        openingTableModel.setAccountRepo(accountRepo);
        openingTableModel.setObserver(this);
        openingTableModel.setProgress(progress);
        openingTableModel.addNewRow();
        tblOpening.getColumnModel().getColumn(0).setPreferredWidth(10);
        tblOpening.getColumnModel().getColumn(1).setPreferredWidth(250);
        tblOpening.getColumnModel().getColumn(2).setPreferredWidth(20);
        tblOpening.getColumnModel().getColumn(3).setPreferredWidth(250);
        tblOpening.getColumnModel().getColumn(4).setPreferredWidth(5);
        tblOpening.getColumnModel().getColumn(5).setPreferredWidth(10);
        tblOpening.getColumnModel().getColumn(6).setPreferredWidth(20);
        tblOpening.getColumnModel().getColumn(7).setPreferredWidth(20);
        tblOpening.getColumnModel().getColumn(6).setCellRenderer(new DecimalFormatRender());
        tblOpening.getColumnModel().getColumn(7).setCellRenderer(new DecimalFormatRender());
        tblOpening.getColumnModel().getColumn(0).setCellEditor(new COA3CellEditor(accountRepo, 3));
        tblOpening.getColumnModel().getColumn(1).setCellEditor(new COA3CellEditor(accountRepo, 3));
        tblOpening.getColumnModel().getColumn(2).setCellEditor(new TraderCellEditor(accountRepo));
        tblOpening.getColumnModel().getColumn(3).setCellEditor(new TraderCellEditor(accountRepo));
        accountRepo.getDepartment().subscribe((t) -> {
            tblOpening.getColumnModel().getColumn(4).setCellEditor(new DepartmentCellEditor(t));
        });
        tblOpening.getColumnModel().getColumn(5).setCellEditor(new ProjectCellEditor(userRepo));
        userRepo.getCurrency().subscribe((t) -> {
            tblOpening.getColumnModel().getColumn(6).setCellEditor(new CurrencyAEditor(t));
        });
        tblOpening.getColumnModel().getColumn(7).setCellEditor(new AutoClearEditor());
        tblOpening.getColumnModel().getColumn(8).setCellEditor(new AutoClearEditor());
        tblOpening.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblOpening.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        filterHeader = new TableFilterHeader(tblOpening, AutoChoices.ENABLED);
        filterHeader.setPosition(TableFilterHeader.Position.TOP);
        filterHeader.setFont(Global.textFont);
        filterHeader.setVisible(false);
    }

    // allo cell editable for tabl e
    public boolean isCellEditable(int row, int column) {
        return tblOpening.getModel().isCellEditable(row, column);
    }

    private String getCurCode() {
        if (currencyAutoCompleter == null || currencyAutoCompleter.getCurrency() == null) {
            return Global.currency;
        }
        return currencyAutoCompleter.getCurrency().getCurCode();
    }

    private String getDepCode() {
        return departmenttAutoCompleter == null ? "-" : departmenttAutoCompleter.getDepartment().getKey().getDeptCode();
    }

    // search openig balance with filtered data for table
    private void searchOpening() {
        progress.setIndeterminate(true);
        ReportFilter filter = new ReportFilter(Global.compCode, Global.macId);
        filter.setOpeningDate(Util1.toDateStr(txtDate.getDate(), "yyyy-MM-dd"));
        filter.setCurCode(getCurCode());
        filter.setDeptCode(getDepCode());
        ChartOfAccount coa = coaAutoCompleter.getCOA();
        filter.setCoaLv1(Util1.getInteger(coa.getCoaLevel()) == 1 ? coa.getKey().getCoaCode() : "-");
        filter.setCoaLv2(Util1.getInteger(coa.getCoaLevel()) == 2 ? coa.getKey().getCoaCode() : "-");
        filter.setAcc(Util1.getInteger(coa.getCoaLevel()) == 3 ? coa.getKey().getCoaCode() : "-");
        filter.setProjectNo(projectAutoCompleter.getProject().getKey().getProjectNo());
        if (chkCus.isSelected()) {
            filter.setTraderType("C");
        } else if (chkSup.isSelected()) {
            filter.setTraderType("S");
        } else {
            filter.setTraderType("-");
        }
        openingTableModel.clear();
        accountRepo.getOpeningBalance(filter).subscribe((t) -> {
            openingTableModel.setListOpening(t);
            openingTableModel.addNewRow();
            btnGenerateZero.setEnabled(openingTableModel.getListOpening().isEmpty());
            calTotalAmt();
            focusOnTable();
            progress.setIndeterminate(false);
        }, (e) -> {
            JOptionPane.showMessageDialog(this, e.getMessage());
        });

    }

    private void focusOnTable() {
        int rc = tblOpening.getRowCount();
        if (rc > 1) {
            tblOpening.setRowSelectionInterval(rc - 1, rc - 1);
            tblOpening.setColumnSelectionInterval(0, 0);
            tblOpening.requestFocus();
        }
    }

    // calculate dr-amt and cr-amt and total
    private void calTotalAmt() {
        List<OpeningBalance> listOpening = openingTableModel.getListOpening();
        double drAmt = 0.0;
        double crAmt = 0.0;
        for (OpeningBalance opening : listOpening) {
            drAmt += Util1.getDouble(opening.getDrAmt());
            crAmt += Util1.getDouble(opening.getCrAmt());
        }
        txtFDrAmt.setValue(drAmt);
        txtFCrAmt.setValue(crAmt);
        double op = drAmt - crAmt;
        txtFOB.setValue(op);
        lblCount.setText(listOpening.size() - 1 + "");
    }

    private void printOpening() {
        progress.setIndeterminate(true);
        ReportFilter filter = new ReportFilter(Global.compCode, Global.macId);
        filter.setReportName("OpeningTri");
        filter.setOpeningDate(Util1.toDateStr(txtDate.getDate(), "yyyy-MM-dd"));
        filter.setDeptCode(departmenttAutoCompleter.getDepartment().getKey().getDeptCode());
        filter.setCurCode(currencyAutoCompleter.getCurrency().getCurCode());
        accountRepo.getReport(filter).subscribe((t) -> {
            try {
                Map<String, Object> p = new HashMap();
                p.put("p_report_name", "Opening Tri Balance");
                p.put("p_date", "Opening Date : " + Util1.toDateStr(txtDate.getDate(), "dd/MM/yyyy"));
                p.put("p_print_date", Util1.getTodayDateTime());
                p.put("p_comp_name", Global.companyName);
                p.put("p_comp_address", Global.companyAddress);
                p.put("p_comp_phone", Global.companyPhone);
                p.put("p_currency", currencyAutoCompleter.getCurrency().getCurCode());
                String filePath = String.format(Global.accountRP + "OpeningTri.jasper");
                InputStream input = new ByteArrayInputStream(t.getFile());
                JsonDataSource ds = new JsonDataSource(input);
                JasperPrint js = JasperFillManager.fillReport(filePath, p, ds);
                JasperViewer.viewReport(js, false);
                progress.setIndeterminate(false);
            } catch (JRException e) {
                progress.setIndeterminate(false);
                log.error("printVoucher : " + e.getMessage());
            }
        }, (e) -> {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
            progress.setIndeterminate(false);
        });

    }

    @Override
    public void selected(Object source, Object selectObj) {
        if (selectObj != null) {
            if (source.equals("CAL-TOTAL")) {
                calTotalAmt();
            } else {
                searchOpening();
            }
        }
    }

    @Override
    public void save() {
    }

    @Override
    public void delete() {
    }

    @Override
    public void newForm() {
    }

    @Override
    public void history() {
    }

    @Override
    public void print() {
        printOpening();
    }

    @Override
    public void refresh() {
        searchOpening();
    }

    @Override
    public void filter() {
        filterHeader.setVisible(!filterHeader.isVisible());
    }

    @Override
    public String panelName() {
        return this.getName();
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
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        btnGenerateZero = new javax.swing.JButton();
        txtDept = new javax.swing.JTextField();
        txtCOA = new javax.swing.JTextField();
        chkCus = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        txtCurrency = new javax.swing.JTextField();
        chkSup = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();
        txtDate = new com.toedter.calendar.JDateChooser();
        jLabel6 = new javax.swing.JLabel();
        txtProjectNo = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblOpening = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtFCrAmt = new javax.swing.JFormattedTextField();
        jLabel7 = new javax.swing.JLabel();
        txtFOB = new javax.swing.JFormattedTextField();
        lblCount = new javax.swing.JLabel();
        txtFDrAmt = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Date");

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("COA");

        btnGenerateZero.setFont(Global.lableFont);
        btnGenerateZero.setText("Generate Zero");

        txtDept.setFont(Global.textFont);

        txtCOA.setFont(Global.textFont);

        chkCus.setFont(Global.lableFont);
        chkCus.setText("Customer");
        chkCus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCusActionPerformed(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Currency");

        txtCurrency.setFont(Global.textFont);

        chkSup.setFont(Global.lableFont);
        chkSup.setText("Supplier");
        chkSup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSupActionPerformed(evt);
            }
        });

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Department");

        txtDate.setDateFormatString("dd/MM/yyyy");
        txtDate.setEnabled(false);
        txtDate.setFont(Global.textFont);
        txtDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDateFocusGained(evt);
            }
        });

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Project No");

        txtProjectNo.setFont(Global.textFont);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtDate, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDept, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCurrency, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCOA, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtProjectNo, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(chkCus)
                .addGap(18, 18, 18)
                .addComponent(chkSup)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnGenerateZero)
                .addGap(10, 10, 10))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtDept, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCOA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5)
                            .addComponent(chkSup)
                            .addComponent(chkCus)
                            .addComponent(btnGenerateZero)
                            .addComponent(jLabel6)
                            .addComponent(txtProjectNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        tblOpening.setAutoCreateRowSorter(true);
        tblOpening.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblOpening.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tblOpening.setGridColor(new java.awt.Color(204, 204, 204));
        tblOpening.setName("tblOpening"); // NOI18N
        jScrollPane1.setViewportView(tblOpening);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Cr-Amt");

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Total Count :");

        txtFCrAmt.setEditable(false);
        txtFCrAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFCrAmt.setFont(Global.amtFont);

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Dr-Amt");

        txtFOB.setEditable(false);
        txtFOB.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFOB.setFont(Global.amtFont);

        lblCount.setFont(Global.lableFont);
        lblCount.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCount.setText("0");

        txtFDrAmt.setEditable(false);
        txtFDrAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFDrAmt.setFont(Global.amtFont);

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Out Of Balance");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblCount, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFDrAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFOB, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFCrAmt, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(lblCount)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(txtFDrAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFCrAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtFOB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void chkCusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCusActionPerformed
        // TODO add your handling code here:
        searchOpening();
    }//GEN-LAST:event_chkCusActionPerformed

    private void chkSupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSupActionPerformed
        // TODO add your handling code here:
        searchOpening();
    }//GEN-LAST:event_chkSupActionPerformed

    private void txtDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDateFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDateFocusGained

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observer.selected("control", this);
    }//GEN-LAST:event_formComponentShown


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGenerateZero;
    private javax.swing.JCheckBox chkCus;
    private javax.swing.JCheckBox chkSup;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCount;
    private javax.swing.JTable tblOpening;
    private javax.swing.JTextField txtCOA;
    private javax.swing.JTextField txtCurrency;
    private com.toedter.calendar.JDateChooser txtDate;
    private javax.swing.JTextField txtDept;
    private javax.swing.JFormattedTextField txtFCrAmt;
    private javax.swing.JFormattedTextField txtFDrAmt;
    private javax.swing.JFormattedTextField txtFOB;
    private javax.swing.JTextField txtProjectNo;
    // End of variables declaration//GEN-END:variables

}
