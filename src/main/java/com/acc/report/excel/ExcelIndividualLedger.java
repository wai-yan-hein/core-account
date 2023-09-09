/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.acc.report.excel;

import com.acc.common.COAOptionTableModel;
import com.acc.common.DateAutoCompleter;
import com.acc.model.ChartOfAccount;
import com.acc.model.Gl;
import com.common.Global;
import com.common.ReportFilter;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.common.Util1;
import com.repo.AccountRepo;
import com.repo.UserRepo;
import com.user.editor.CurrencyAutoCompleter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class ExcelIndividualLedger extends javax.swing.JPanel implements SelectionObserver {

    private JProgressBar progress;
    private SelectionObserver observer;
    private DateAutoCompleter dateAutoCompleter;
    private AccountRepo accountRepo;
    private UserRepo userRepo;
    private COAOptionTableModel cOATableModel = new COAOptionTableModel();
    private CurrencyAutoCompleter currencyAutoCompleter;
    private static final String OUTPUT_FILE_PATH = System.getProperty("user.home") + "/Downloads/IndividualLedgerExcel.xlsx";
    private static final String[] HEADERS = {
        "Date", "Dep :", "Description", "Reference", "Ref No", "Trader Name",
        "Account", "Currency", "Dr Amt", "Cr Amt", "Opening", "Closing"
    };

    public void setAccountRepo(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Creates new form IndividualLedger
     */
    public ExcelIndividualLedger() {
        initComponents();
    }

    public void initMain() {
        initCombo();
        initTable();
        searchCOA();
    }

    private void initCombo() {
        dateAutoCompleter = new DateAutoCompleter(txtDate);
        dateAutoCompleter.setObserver(this);
        currencyAutoCompleter = new CurrencyAutoCompleter(txtCurrency, null);
        currencyAutoCompleter.setObserver(this);
        userRepo.getDefaultCurrency().doOnSuccess((t) -> {
            currencyAutoCompleter.setCurrency(t);
        }).subscribe();
        userRepo.getCurrency().doOnSuccess((t) -> {
            currencyAutoCompleter.setListCurrency(t);
        }).subscribe();
    }

    private void initTable() {
        tblCOA.setModel(cOATableModel);
        tblCOA.setFont(Global.textFont);
        tblCOA.getTableHeader().setFont(Global.tblHeaderFont);
        tblCOA.setRowHeight(Global.tblRowHeight);
        tblCOA.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCOA.setDefaultRenderer(Object.class, new TableCellRender());
        tblCOA.setDefaultRenderer(Boolean.class, new TableCellRender());
    }

    private void searchCOA() {
        progress.setIndeterminate(true);
        accountRepo.getChartOfAccount().doOnSuccess((t) -> {
            cOATableModel.setListCOA(t);
            progress.setIndeterminate(false);
        }).doOnError((e) -> {
            JOptionPane.showMessageDialog(this, e.getMessage());
            progress.setIndeterminate(false);
        }).subscribe();
    }

    private void select() {
        List<ChartOfAccount> list = cOATableModel.getListCOA();
        list.forEach((t) -> {
            t.setActive(chkSelect.isSelected());
        });
        cOATableModel.fireTableDataChanged();
    }

    private void exportExcels(String outputPath) {
        btnExport.setEnabled(false);

        SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
            @Override
            protected Void doInBackground() throws Exception {
                try (Workbook workbook = new XSSFWorkbook(); FileOutputStream outputStream = new FileOutputStream(outputPath)) {
                    Font font = workbook.createFont();
                    font.setFontName("Pyidaungsu");
                    font.setFontHeightInPoints((short) 12);
                    CellStyle cellStyle = workbook.createCellStyle();
                    cellStyle.setFont(font);
                    List<ChartOfAccount> coaList = cOATableModel.getListCOA();
                    int completedCOAs = 0;

                    for (ChartOfAccount t : coaList) {
                        if (t.isActive()) {
                            publish(completedCOAs);
                            String coaCode = t.getKey().getCoaCode();
                            List<Gl> data = accountRepo.searchGl(getFilter(coaCode)).block();
                            String sheetName = Util1.replaceSpecialCharactersWithSpace(t.getCoaNameEng());
                            createSheet(workbook, data, coaCode, Util1.autoCorrectSheetName(sheetName), cellStyle);
                            completedCOAs++;
                        }
                    }

                    workbook.write(outputStream);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage());
                }

                return null;
            }

            @Override
            protected void process(List<Integer> chunks) {
                int completedCOAs = chunks.get(chunks.size() - 1);
                int totalCOAs = cOATableModel.getListCOA().size();
                int progress = (int) (((double) completedCOAs / totalCOAs) * 100);
                progressExcel.setValue(progress);
                lblMessage.setText("Progress: " + progress + "%");
            }

            @Override
            protected void done() {
                lblMessage.setText("Complete.");
                progressExcel.setValue(100);
                btnExport.setEnabled(true);
            }
        };

        worker.execute();
    }

    private void createSheet(Workbook workbook, List<Gl> data, String coaCode, String sheetName, CellStyle cellStyle) {
        String uniqueSheetName = generateUniqueSheetName(workbook, sheetName);
        Sheet sheet = workbook.createSheet(uniqueSheetName);
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < HEADERS.length; i++) {
            headerRow.createCell(i).setCellValue(HEADERS[i]);
        }
        Font font = workbook.createFont();
        font.setFontName("Pyidaungsu");
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(font);
        for (Cell cell : headerRow) {
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (Gl gl : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(Util1.toDateStr(gl.getGlDate(), Global.dateFormat));
            row.createCell(1).setCellValue(gl.getDeptUsrCode());
            row.createCell(2).setCellValue(gl.getDescription());
            row.createCell(3).setCellValue(gl.getReference());
            row.createCell(4).setCellValue(gl.getRefNo());
            row.createCell(5).setCellValue(gl.getTraderName());
            row.createCell(6).setCellValue(gl.getAccName());
            row.createCell(7).setCellValue(gl.getCurCode());
            row.createCell(8).setCellValue(Util1.getDouble(gl.getDrAmt()));
            row.createCell(9).setCellValue(Util1.getDouble(gl.getCrAmt()));

            for (Cell cell : row) {
                cell.setCellStyle(cellStyle);
            }
        }
        double drAmt = data.stream().mapToDouble((t) -> Util1.getDouble(t.getDrAmt())).sum();
        double crAmt = data.stream().mapToDouble((t) -> Util1.getDouble(t.getCrAmt())).sum();
        double opening = accountRepo.getOpening(getOPFilter(coaCode)).block().getOpening();
        double closing = drAmt - crAmt + opening;
        Row row = sheet.createRow(data.size());
        row.createCell(8).setCellValue(drAmt);
        row.createCell(9).setCellValue(crAmt);
        row.createCell(10).setCellValue(opening);
        row.createCell(11).setCellValue(closing);
        for (Cell cell : row) {
            cell.setCellStyle(headerStyle);
        }
        for (int i = 0; i < HEADERS.length; i++) {
            sheet.autoSizeColumn(i);
        }

    }

    private String generateUniqueSheetName(Workbook workbook, String baseName) {
        String uniqueName = baseName;
        int suffix = 2; // Start with 2 as the suffix

        // Check if the sheet name already exists, and if so, append a numeric suffix
        while (workbook.getSheet(uniqueName) != null) {
            uniqueName = baseName + "_" + suffix;
            suffix++;
        }

        return uniqueName;
    }

    private void process() {
        exportExcels(OUTPUT_FILE_PATH);
    }

    private ReportFilter getFilter(String srcAcc) {
        ReportFilter filter = new ReportFilter(Global.macId, Global.compCode, Global.deptId);
        filter.setFromDate(dateAutoCompleter.getDateModel().getStartDate());
        filter.setToDate(dateAutoCompleter.getDateModel().getEndDate());
        filter.setSrcAcc(srcAcc);
        filter.setCurCode(getCurrency());
        return filter;
    }

    private ReportFilter getOPFilter(String srcAcc) {
        String clDate = dateAutoCompleter.getDateModel().getStartDate();
        ReportFilter filter = new ReportFilter(Global.macId, Global.compCode, Global.deptId);
        filter.setFromDate(clDate);
        filter.setCurCode(getCurrency());
        filter.setCoaCode(srcAcc);
        return filter;
    }

    private String getCurrency() {
        return currencyAutoCompleter.getCurrency() == null ? Global.currency : currencyAutoCompleter.getCurrency().getCurCode();
    }

    private void showInFolder() {
        Util1.openFolder(OUTPUT_FILE_PATH);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtDate = new javax.swing.JTextField();
        btnExport = new javax.swing.JButton();
        progressExcel = new javax.swing.JProgressBar();
        lblMessage = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtCurrency = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        chkSelect = new javax.swing.JCheckBox();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCOA = new javax.swing.JTable();
        txtSearch = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Date");

        btnExport.setText("Export");
        btnExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportActionPerformed(evt);
            }
        });

        progressExcel.setFont(Global.textFont);
        progressExcel.setStringPainted(true);

        lblMessage.setFont(Global.lableFont);
        lblMessage.setText("-");

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Currency");

        jButton2.setText("Show In Folder");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(progressExcel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnExport, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblMessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDate)
                            .addComponent(txtCurrency, javax.swing.GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtDate))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtCurrency))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnExport)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(progressExcel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblMessage))
                    .addComponent(jButton2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        chkSelect.setFont(Global.lableFont);
        chkSelect.setText("Select All");
        chkSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSelectActionPerformed(evt);
            }
        });

        tblCOA.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblCOA);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Search");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(chkSelect)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(12, 12, 12)
                        .addComponent(txtSearch))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkSelect)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jPanel1, jPanel2});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void chkSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSelectActionPerformed
        // TODO add your handling code here:
        select();
    }//GEN-LAST:event_chkSelectActionPerformed

    private void btnExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportActionPerformed
        // TODO add your handling code here:
        process();
    }//GEN-LAST:event_btnExportActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        showInFolder();
    }//GEN-LAST:event_jButton2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExport;
    private javax.swing.JCheckBox chkSelect;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblMessage;
    private javax.swing.JProgressBar progressExcel;
    private javax.swing.JTable tblCOA;
    private javax.swing.JTextField txtCurrency;
    private javax.swing.JTextField txtDate;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {
    }
}
