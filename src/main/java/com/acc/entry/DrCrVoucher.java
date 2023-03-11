/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.acc.entry;

import com.acc.common.AccountRepo;
import com.acc.common.DateAutoCompleter;
import com.acc.common.VoucherTableModel;
import com.acc.dialog.VoucherEntryDailog;
import com.acc.model.Gl;
import com.common.Global;
import com.common.PanelControl;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.acc.editor.DepartmentAutoCompleter;
import com.acc.editor.DespAutoCompleter;
import com.acc.editor.RefAutoCompleter;
import com.acc.model.ChartOfAccount;
import com.acc.model.DeleteObj;
import com.acc.model.ReportFilter;
import com.acc.model.TmpOpening;
import com.common.ProUtil;
import static com.common.ProUtil.gson;
import com.common.Util1;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.view.JasperViewer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 *
 * @author Lenovo
 */
@Component
public class DrCrVoucher extends javax.swing.JPanel implements SelectionObserver, PanelControl, KeyListener {

    private static final Logger log = LoggerFactory.getLogger(DrCrVoucher.class);
    private int selectRow = -1;
    private DateAutoCompleter dateAutoCompleter;
    private DepartmentAutoCompleter departmentAutoCompleter;
    private DespAutoCompleter despAutoCompleter;
    private RefAutoCompleter refAutoCompleter;
    private SelectionObserver observer;
    private JProgressBar progress;
    private final VoucherTableModel voucherTableModel = new VoucherTableModel();
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
     * Creates new form CrDrVoucher1
     */
    public DrCrVoucher() {
        initComponents();
        initKeyListener();
        initFocusListener();
        initDateFormat();
    }

    private void initDateFormat() {
        txtDr.setFormatterFactory(Util1.getDecimalFormat());
        txtCr.setFormatterFactory(Util1.getDecimalFormat());
        txtOpening.setFormatterFactory(Util1.getDecimalFormat());
        txtClosing.setFormatterFactory(Util1.getDecimalFormat());
        txtDr.setHorizontalAlignment(JTextField.RIGHT);
        txtCr.setHorizontalAlignment(JTextField.RIGHT);
        txtClosing.setHorizontalAlignment(JTextField.RIGHT);
        txtOpening.setHorizontalAlignment(JTextField.RIGHT);
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

    private void initFocusListener() {
        txtDate.addFocusListener(fa);
        txtDept.addFocusListener(fa);
        txtVouNo.addFocusListener(fa);
        txtDesp.addFocusListener(fa);
        txtRef.addFocusListener(fa);
        txtRefNo.addFocusListener(fa);

    }

    private void initTable() {
        tblVoucher.setModel(voucherTableModel);
        tblVoucher.getTableHeader().setFont(Global.tblHeaderFont);
        tblVoucher.setDefaultRenderer(Object.class, new TableCellRender());
        tblVoucher.setDefaultRenderer(Double.class, new TableCellRender());
        tblVoucher.getTableHeader().setFont(Global.tblHeaderFont);
        tblVoucher.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    if (tblVoucher.getSelectedRow() >= 0) {
                        selectRow = tblVoucher.convertRowIndexToModel(tblVoucher.getSelectedRow());
                        Gl gl = voucherTableModel.getVGl(selectRow);
                        List<Gl> list = accountRepo.getVoucher(gl.getGlVouNo());
                        openVoucherDialog(gl.getTranSource(), list);
                    }
                }
            }

        });
    }

    private void initCombo() {
        dateAutoCompleter = new DateAutoCompleter(txtDate, Global.listDate);
        dateAutoCompleter.setSelectionObserver(this);
        departmentAutoCompleter = new DepartmentAutoCompleter(txtDept, accountRepo.getDepartment(), null, true, true);
        departmentAutoCompleter.setObserver(this);
        despAutoCompleter = new DespAutoCompleter(txtDesp, accountApi, null, true);
        despAutoCompleter.setSelectionObserver(this);
        refAutoCompleter = new RefAutoCompleter(txtRef, accountApi, null, true);
        refAutoCompleter.setSelectionObserver(this);
    }

    public void initMain() {
        initCombo();
        initTable();
        search();
    }

    private void initKeyListener() {
        txtDate.addKeyListener(this);
        txtDept.addKeyListener(this);
        txtDesp.addKeyListener(this);
        txtRef.addKeyListener(this);
        txtRefNo.addKeyListener(this);
        txtVouNo.addKeyListener(this);
    }

    private void search() {
        if (progress != null) {
            progress.setIndeterminate(true);
            calOpening();
            ReportFilter filter = new ReportFilter(Global.compCode, Global.macId);
            filter.setFromDate(Util1.toDateStrMYSQL(dateAutoCompleter.getStDate(), Global.dateFormat));
            filter.setToDate(Util1.toDateStrMYSQL(dateAutoCompleter.getEndDate(), Global.dateFormat));
            filter.setListDepartment(departmentAutoCompleter.getListOption());
            filter.setDesp(txtDesp.getText());
            filter.setGlVouNo(txtVouNo.getText());
            filter.setReference(txtRef.getText());
            Mono<ResponseEntity<List<Gl>>> result = accountApi
                    .post()
                    .uri("/account/search-voucher")
                    .body(Mono.just(filter), ReportFilter.class)
                    .retrieve()
                    .toEntityList(Gl.class);
            result.subscribe((t) -> {
                voucherTableModel.setListGV(t.getBody());
                lblRecord.setText(voucherTableModel.getListSize() + "");
                calAmt();
                progress.setIndeterminate(false);
            });
        }
    }

    private void calAmt() {
        double ttlDr = 0.0;
        double ttlCr = 0.0;
        double opening = Util1.getDouble(txtOpening.getValue());
        List<Gl> listGl = voucherTableModel.getListGV();
        for (Gl g : listGl) {
            ttlDr += Util1.getDouble(g.getDrAmt());
            ttlCr += Util1.getDouble(g.getCrAmt());
        }
        txtDr.setValue(ttlDr);
        txtCr.setValue(ttlCr);
        txtClosing.setValue(ttlDr - ttlCr + opening);
    }

    private void calOpening() {
        ChartOfAccount coa = accountRepo.getDefaultCash();
        if (coa != null) {
            String stDate = Util1.toDateStrMYSQL(dateAutoCompleter.getStDate(), Global.dateFormat);
            String opDate = Util1.toDateStrMYSQL(Global.startDate, "dd/MM/yyyy");
            String clDate = Util1.toDateStrMYSQL(stDate, "dd/MM/yyyy");
            ReportFilter filter = new ReportFilter(Global.compCode, Global.macId);
            filter.setOpeningDate(opDate);
            filter.setFromDate(clDate);
            filter.setCurCode(Global.currency);
            filter.setListDepartment(departmentAutoCompleter.getListOption());
            filter.setCoaCode(coa.getKey().getCoaCode());
            Mono<TmpOpening> result = accountRepo.getOpening(filter);
            result.subscribe((t) -> {
                if (t.equals(new TmpOpening())) {
                    txtOpening.setValue(0);
                } else {
                    txtOpening.setValue(t.getOpening());
                }
            }, (e) -> {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }, () -> {
            });
        }
    }

    public void openVoucherDialog(String type, List<Gl> listVGl) {
        VoucherEntryDailog dailog = new VoucherEntryDailog();
        dailog.setIconImage(Global.parentForm.getIconImage());
        dailog.setAccountRepo(accountRepo);
        dailog.setAccountApi(accountApi);
        dailog.setVouType(type);
        dailog.setObserver(this);
        dailog.setListVGl(listVGl);
        dailog.initMain();
        dailog.setSize(Global.width - 200, Global.height - 200);
        dailog.setLocationRelativeTo(null);
        dailog.setVisible(true);
    }

    private void deleteVoucher() {
        int row = tblVoucher.convertRowIndexToModel(tblVoucher.getSelectedRow());
        int yes_no;
        if (row >= 0) {
            Gl gl = voucherTableModel.getVGl(row);
            String glVouNo = gl.getGlVouNo();
            if (glVouNo != null) {
                yes_no = JOptionPane.showConfirmDialog(Global.parentForm, "Are you sure to delete voucher?",
                        "Delete", JOptionPane.YES_NO_OPTION);
                if (yes_no == 0) {
                    DeleteObj obj = new DeleteObj();
                    obj.setGlVouNo(glVouNo);
                    obj.setCompCode(Global.compCode);
                    obj.setModifyBy(Global.loginUser.getUserCode());
                    if (accountRepo.deleteVoucher(obj)) {
                        voucherTableModel.remove(selectRow);
                        focusOnTable();
                    }
                }
            }
        }
    }

    private void focusOnTable() {
        int rc = tblVoucher.getRowCount();
        if (rc >= 1) {
            tblVoucher.setRowSelectionInterval(rc - 1, rc - 1);
            tblVoucher.setColumnSelectionInterval(0, 0);
            tblVoucher.requestFocus();
        } else {
            txtDate.requestFocusInWindow();
        }
    }

    private void printVoucher(Gl gl) {
        try {
            String glVouNo = gl.getGlVouNo();
            String rpName = gl.getTranSource().equals("DR") ? "Payment / Debit Voucher" : "Receipt / Credit Voucher";
            String rpPath = Global.accountRP + "DrCrVoucherA5.jasper";
            Map<String, Object> p = new HashMap();
            p.put("p_report_name", rpName);
            p.put("p_date", String.format("Between %s and %s", dateAutoCompleter.getStDate(), dateAutoCompleter.getEndDate()));
            p.put("p_print_date", Util1.getTodayDateTime());
            p.put("p_comp_name", Global.companyName);
            p.put("p_comp_address", Global.companyAddress);
            p.put("p_comp_phone", Global.companyPhone);
            p.put("p_vou_type", gl.getTranSource());
            Util1.initJasperContext();
            List<Gl> list = accountRepo.getVoucher(glVouNo);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(gson.toJson(list));
            JsonDataSource ds = new JsonDataSource(node, null) {
            };
            JasperPrint js = JasperFillManager.fillReport(rpPath, p, ds);
            JasperViewer.viewReport(js, false);
        } catch (JsonProcessingException | JRException ex) {
            log.error("printVoucher : " + ex.getMessage());
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

        jScrollPane1 = new javax.swing.JScrollPane();
        tblVoucher = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        txtDesp = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtRefNo = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtRef = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        txtDate = new javax.swing.JTextField();
        txtDept = new javax.swing.JTextField();
        txtVouNo = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        txtClosing = new javax.swing.JFormattedTextField();
        txtCr = new javax.swing.JFormattedTextField();
        lblRecord = new javax.swing.JLabel();
        txtDr = new javax.swing.JFormattedTextField();
        jLabel7 = new javax.swing.JLabel();
        txtOpening = new javax.swing.JFormattedTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        tblVoucher.setFont(Global.textFont);
        tblVoucher.setModel(new javax.swing.table.DefaultTableModel(
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
        tblVoucher.setRowHeight(Global.tblRowHeight);
        tblVoucher.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblVoucherMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblVoucher);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Dep :");

        jButton1.setFont(Global.lableFont);
        jButton1.setText("Payment / Debit");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Ref No");

        txtDesp.setFont(Global.textFont);
        txtDesp.setName("txtDesp"); // NOI18N

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Reference");

        txtRefNo.setFont(Global.textFont);
        txtRefNo.setName("txtRefNo"); // NOI18N

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Date");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Vou No");

        txtRef.setFont(Global.textFont);
        txtRef.setName("txtRef"); // NOI18N

        jButton2.setFont(Global.lableFont);
        jButton2.setText("Receipt / Credit");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        txtDate.setFont(Global.textFont);
        txtDate.setName("txtDate"); // NOI18N
        txtDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDateActionPerformed(evt);
            }
        });

        txtDept.setFont(Global.textFont);
        txtDept.setName("txtDept"); // NOI18N
        txtDept.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDeptActionPerformed(evt);
            }
        });

        txtVouNo.setFont(Global.textFont);
        txtVouNo.setName("txtVouNo"); // NOI18N

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Description");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, 66, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtDept, javax.swing.GroupLayout.PREFERRED_SIZE, 66, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, 66, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtDesp, javax.swing.GroupLayout.PREFERRED_SIZE, 66, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtRef, javax.swing.GroupLayout.PREFERRED_SIZE, 66, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtRefNo, javax.swing.GroupLayout.PREFERRED_SIZE, 68, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtDate)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtVouNo)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtDesp)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtRef)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtRefNo)
                        .addComponent(jButton1)
                        .addComponent(jButton2))
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtDept, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        txtClosing.setEditable(false);
        txtClosing.setFont(Global.amtFont);

        txtCr.setEditable(false);
        txtCr.setFont(Global.amtFont);

        lblRecord.setFont(Global.lableFont);
        lblRecord.setText("0");

        txtDr.setEditable(false);
        txtDr.setFont(Global.amtFont);

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Record :");

        txtOpening.setEditable(false);
        txtOpening.setFont(Global.amtFont);

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Opening");

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Dr Amt");

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Cr Amt");

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Closing");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtOpening, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDr, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblRecord, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel11)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtClosing, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtCr, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtCr, txtDr});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtOpening, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtClosing, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(lblRecord)
                    .addComponent(jLabel11))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        openVoucherDialog("CR", null);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        openVoucherDialog("DR", null);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observer.selected("control", this);
    }//GEN-LAST:event_formComponentShown

    private void tblVoucherMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVoucherMouseClicked
        // TODO add your handling code here:

    }//GEN-LAST:event_tblVoucherMouseClicked

    private void txtDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDateActionPerformed

    private void txtDeptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDeptActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDeptActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblRecord;
    private javax.swing.JTable tblVoucher;
    private javax.swing.JFormattedTextField txtClosing;
    private javax.swing.JFormattedTextField txtCr;
    private javax.swing.JTextField txtDate;
    private javax.swing.JTextField txtDept;
    private javax.swing.JTextField txtDesp;
    private javax.swing.JFormattedTextField txtDr;
    private javax.swing.JFormattedTextField txtOpening;
    private javax.swing.JTextField txtRef;
    private javax.swing.JTextField txtRefNo;
    private javax.swing.JTextField txtVouNo;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {
        if (source != null) {
            search();
            if (source.equals("print")) {
                if (selectObj instanceof Gl gl) {
                    printVoucher(gl);
                }
            }
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
        int row = tblVoucher.convertRowIndexToModel(tblVoucher.getSelectedRow());
        if (row >= 0) {
            printVoucher(voucherTableModel.getVGl(row));
        }
    }

    @Override
    public void refresh() {
        search();
    }

    @Override
    public String panelName() {
        return this.getName();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        Object sourceObj = e.getSource();
        String ctrlName = "-";
        if (sourceObj instanceof JTextField txt) {
            ctrlName = txt.getName();
        }
        switch (ctrlName) {
            case "txtDate" -> {
                log.info(e.getKeyCode() + "");
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtDept.requestFocus();
                }
            }

            case "txtDept" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtVouNo.requestFocus();
                }
            }

            case "txtVouNo" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtDesp.requestFocus();
                    search();
                }
            }

            case "txtDesp" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtRef.requestFocus();
                    search();
                }
            }

            case "txtRef" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtRefNo.requestFocus();
                    search();
                }
            }

            case "txtRefNo" -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    tblVoucher.requestFocus();
                    search();
                }
            }

        }
    }

    @Override
    public void keyReleased(KeyEvent e
    ) {
    }

    @Override
    public void filter() {
    }
}
