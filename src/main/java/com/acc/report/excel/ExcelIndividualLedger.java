/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.acc.report.excel;

import com.acc.common.COAOptionTableModel;
import com.acc.editor.DateAutoCompleter;
import com.acc.common.TraderAReportTableModel;
import com.acc.model.ChartOfAccount;
import com.acc.model.Gl;
import com.acc.model.TraderA;
import com.common.Global;
import com.common.ReportFilter;
import com.common.SelectionObserver;
import com.common.StartWithRowFilter;
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
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.core.task.TaskExecutor;

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
    private TraderAReportTableModel traderTableModel = new TraderAReportTableModel();
    private CurrencyAutoCompleter currencyAutoCompleter;
    private TableRowSorter<TableModel> sorterCOA;
    private TableRowSorter<TableModel> sorterTrader;
    private StartWithRowFilter swrfCOA;
    private StartWithRowFilter swrfTrader;
    private TaskExecutor taskExecutor;
    private static final String OUTPUT_FILE_PATH = System.getProperty("user.home") + "/Downloads/";
    private String lastPath = "";
    private static final String[] HEADERS_COA = {
        "Date", "Dep :", "Description", "Reference", "Ref No", "Trader Name",
        "Account", "Currency", "Dr Amt", "Cr Amt", "Opening", "Closing", "Tran Id", "Src Id", "Acc Id"
    };
    private static final String[] HEADERS_TRADER = {
        "Date", "Dep :", "Description", "Reference", "Ref No", "Account", "Currency", "Dr Amt", "Cr Amt", "Opening", "Closing"
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

    public void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    /**
     * Creates new form IndividualLedger
     */
    public ExcelIndividualLedger() {
        initComponents();
    }

    public void initMain() {
        initCombo();
        initTableCOA();
        initTableTrader();
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

    private void initTableCOA() {
        tblCOA.setModel(cOATableModel);
        tblCOA.setFont(Global.textFont);
        tblCOA.getTableHeader().setFont(Global.tblHeaderFont);
        tblCOA.setRowHeight(Global.tblRowHeight);
        tblCOA.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCOA.setDefaultRenderer(Object.class, new TableCellRender());
        tblCOA.setDefaultRenderer(Boolean.class, new TableCellRender());
        tblCOA.getColumnModel().getColumn(0).setPreferredWidth(50);// Code
        tblCOA.getColumnModel().getColumn(1).setPreferredWidth(200);// Name
        tblCOA.getColumnModel().getColumn(2).setPreferredWidth(200);// Group
        tblCOA.getColumnModel().getColumn(3).setPreferredWidth(50);// Select
        sorterCOA = new TableRowSorter<>(tblCOA.getModel());
        tblCOA.setRowSorter(sorterCOA);
        swrfCOA = new StartWithRowFilter(txtSearch);
        searchCOA();
    }

    private void initTableTrader() {
        tblTrader.setModel(traderTableModel);
        tblTrader.setFont(Global.textFont);
        tblTrader.getTableHeader().setFont(Global.tblHeaderFont);
        tblTrader.setRowHeight(Global.tblRowHeight);
        tblTrader.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblTrader.setDefaultRenderer(Object.class, new TableCellRender());
        tblTrader.setDefaultRenderer(Boolean.class, new TableCellRender());
        tblTrader.getTableHeader().setFont(Global.textFont);
        tblTrader.getColumnModel().getColumn(0).setPreferredWidth(30);// Code
        tblTrader.getColumnModel().getColumn(1).setPreferredWidth(200);// Name
        tblTrader.getColumnModel().getColumn(2).setPreferredWidth(20);// Name
        sorterTrader = new TableRowSorter<>(tblTrader.getModel());
        tblTrader.setRowSorter(sorterTrader);
        swrfTrader = new StartWithRowFilter(txtTraderSearch);
        searchTrader();
    }

    private void searchCOA() {
        progress.setIndeterminate(true);
        accountRepo.getChartOfAccount(3).doOnSuccess((t) -> {
            selectCOA(t);
            progress.setIndeterminate(false);
        }).doOnError((e) -> {
            JOptionPane.showMessageDialog(this, e.getMessage());
            progress.setIndeterminate(false);
        }).subscribe();
    }

    private void searchTrader() {
        progress.setIndeterminate(true);
        accountRepo.getTrader().doOnSuccess((t) -> {
            selectTrader(t);
            progress.setIndeterminate(false);
        }).doOnError((e) -> {
            JOptionPane.showMessageDialog(this, e.getMessage());
            progress.setIndeterminate(false);
        }).subscribe();
    }

    private void selectCOA(List<ChartOfAccount> list) {
        list.forEach((t) -> {
            t.setActive(chkSelect.isSelected());
        });
        cOATableModel.setListCOA(list);
        lblCOARecord.setText(list.size() + "");
    }

    private void selectTrader(List<TraderA> list) {
        list.forEach((t) -> {
            t.setActive(chkTraderSelect.isSelected());
        });
        traderTableModel.setListTrader(list);
        lblTraderRecord.setText(list.size() + "");
    }

    private void exportCOAExcels() {
        btnExport.setEnabled(false);
        String outputPath = OUTPUT_FILE_PATH + "IndividualLedgerExcel.xlsx";
        taskExecutor.execute(() -> {
            try (SXSSFWorkbook workbook = new SXSSFWorkbook(); FileOutputStream outputStream = new FileOutputStream(outputPath)) {
                workbook.setCompressTempFiles(true); // Enable temporary file compression for improved performance
                Font font = workbook.createFont();
                font.setFontName("Pyidaungsu");
                font.setFontHeightInPoints((short) 12);
                CellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setFont(font);
                List<ChartOfAccount> coaList = cOATableModel.getListCOA().stream().filter((c) -> c.isActive()).toList();
                if (!coaList.isEmpty()) {
                    coaList.forEach((t) -> {
                        String coaCode = t.getKey().getCoaCode();
                        String coaName = t.getCoaNameEng();
                        lblMessage.setText("Data requesting for " + coaName);
                        List<Gl> data = accountRepo.searchGl(getFilter(coaCode, null)).block();
                        lblMessage.setText("Data ready for " + coaName + " Record : " + data.size());
                        String sheetName = Util1.replaceSpecialCharactersWithSpace(coaName);
                        createCOASheet(workbook, data, coaCode, Util1.autoCorrectSheetName(sheetName), cellStyle);
                    });
                    lblMessage.setText("Exporting File... Please wait.");
                    workbook.write(outputStream);
                    lastPath = outputPath;
                    lblMessage.setText("complete.");
                } else {
                    JOptionPane.showMessageDialog(this, "Please Select Chart Of Account.");
                }
                btnExport.setEnabled(true);
            } catch (IOException e) {
                btnExport.setEnabled(true);
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        });
    }

    private void exportTraderExcels() {
        btnExport.setEnabled(false);
        String outputPath = OUTPUT_FILE_PATH + "TraderIndividualLedgerExcel.xlsx";
        taskExecutor.execute(() -> {
            try (SXSSFWorkbook workbook = new SXSSFWorkbook(); FileOutputStream outputStream = new FileOutputStream(outputPath)) {
                workbook.setCompressTempFiles(true); // Enable temporary file compression for improved performance
                Font font = workbook.createFont();
                font.setFontName("Pyidaungsu");
                font.setFontHeightInPoints((short) 12);
                CellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setFont(font);
                List<TraderA> listTrader = traderTableModel.getListTrader().stream().filter((t) -> t.isActive()).toList();
                if (!listTrader.isEmpty()) {
                    listTrader.forEach((t) -> {
                        String traderCode = t.getKey().getCode();
                        String traderName = t.getTraderName();
                        String coaCode = t.getAccount();
                        lblMessage.setText("Data requesting for " + traderName);
                        List<Gl> data = accountRepo.searchGl(getFilter(coaCode, traderCode)).block();
                        lblMessage.setText("Data ready for " + traderName + " Record : " + data.size());
                        String sheetName = Util1.replaceSpecialCharactersWithSpace(traderName);
                        createTraderSheet(workbook, data, coaCode, traderCode, Util1.autoCorrectSheetName(sheetName), cellStyle);
                    });
                    lblMessage.setText("Exporting File... Please wait.");
                    workbook.write(outputStream);
                    lastPath = outputPath;
                    lblMessage.setText("complete.");
                } else {
                    JOptionPane.showMessageDialog(this, "Please Select Customer or Supplier.");
                }
                btnExport.setEnabled(true);
            } catch (IOException e) {
                btnExport.setEnabled(true);
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        });
    }

    private void createCOASheet(Workbook workbook, List<Gl> data,
            String coaCode, String sheetName,
            CellStyle cellStyle) {
        String uniqueSheetName = generateUniqueSheetName(workbook, sheetName);
        Sheet sheet = workbook.createSheet(uniqueSheetName);
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < HEADERS_COA.length; i++) {
            headerRow.createCell(i).setCellValue(HEADERS_COA[i]);
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
            lblMessage.setText(rowNum + " / " + data.size());
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
            row.createCell(12).setCellValue(gl.getKey().getGlCode());
            row.createCell(13).setCellValue(gl.getSrcAccCode());
            row.createCell(14).setCellValue(gl.getAccCode());
            for (Cell cell : row) {
                cell.setCellStyle(cellStyle);
            }
        }
        lblMessage.setText("calculating opening.");
        double drAmt = data.stream().mapToDouble((t) -> Util1.getDouble(t.getDrAmt())).sum();
        double crAmt = data.stream().mapToDouble((t) -> Util1.getDouble(t.getCrAmt())).sum();
        double opening = accountRepo.getOpening(getOPFilter(coaCode, null)).block().getOpening();
        double closing = drAmt - crAmt + opening;
        Row row = sheet.createRow(data.size() + 1);
        row.createCell(8).setCellValue(drAmt);
        row.createCell(9).setCellValue(crAmt);
        row.createCell(10).setCellValue(opening);
        row.createCell(11).setCellValue(closing);
        for (Cell cell : row) {
            cell.setCellStyle(headerStyle);
        }
        try {
            for (int i = 0; i < HEADERS_COA.length; i++) {
                sheet.autoSizeColumn(i);
            }
        } catch (Exception e) {
            log.info("autoResize ‌: " + e.getMessage());
        }
    }

    private void createTraderSheet(Workbook workbook, List<Gl> data,
            String coaCode, String traderCode, String sheetName,
            CellStyle cellStyle) {
        String uniqueSheetName = generateUniqueSheetName(workbook, sheetName);
        Sheet sheet = workbook.createSheet(uniqueSheetName);
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < HEADERS_TRADER.length; i++) {
            headerRow.createCell(i).setCellValue(HEADERS_TRADER[i]);
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
            row.createCell(5).setCellValue(gl.getAccName());
            row.createCell(6).setCellValue(gl.getCurCode());
            row.createCell(7).setCellValue(Util1.getDouble(gl.getDrAmt()));
            row.createCell(8).setCellValue(Util1.getDouble(gl.getCrAmt()));

            for (Cell cell : row) {
                cell.setCellStyle(cellStyle);
            }
        }
        lblMessage.setText("calculating opening.");
        double drAmt = data.stream().mapToDouble((t) -> Util1.getDouble(t.getDrAmt())).sum();
        double crAmt = data.stream().mapToDouble((t) -> Util1.getDouble(t.getCrAmt())).sum();
        double opening = accountRepo.getOpening(getOPFilter(coaCode, traderCode)).block().getOpening();
        double closing = drAmt - crAmt + opening;
        Row row = sheet.createRow(data.size() + 1);
        row.createCell(7).setCellValue(drAmt);
        row.createCell(8).setCellValue(crAmt);
        row.createCell(9).setCellValue(opening);
        row.createCell(10).setCellValue(closing);
        for (Cell cell : row) {
            cell.setCellStyle(headerStyle);
        }
        try {
            for (int i = 0; i < HEADERS_TRADER.length; i++) {
                sheet.autoSizeColumn(i);
            }
        } catch (Exception e) {
            log.info("autoResize ‌: " + e.getMessage());
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

    private ReportFilter getFilter(String srcAcc, String traderCode) {
        ReportFilter filter = new ReportFilter(Global.macId, Global.compCode, Global.deptId);
        filter.setFromDate(dateAutoCompleter.getDateModel().getStartDate());
        filter.setToDate(dateAutoCompleter.getDateModel().getEndDate());
        filter.setSrcAcc(srcAcc);
        filter.setCurCode(getCurrency());
        filter.setTraderCode(traderCode);
        return filter;
    }

    private ReportFilter getOPFilter(String srcAcc, String traderCode) {
        String clDate = dateAutoCompleter.getDateModel().getStartDate();
        ReportFilter filter = new ReportFilter(Global.macId, Global.compCode, Global.deptId);
        filter.setFromDate(clDate);
        filter.setCurCode(getCurrency());
        filter.setCoaCode(srcAcc);
        filter.setTraderCode(traderCode);
        return filter;
    }

    private String getCurrency() {
        return currencyAutoCompleter.getCurrency() == null ? Global.currency : currencyAutoCompleter.getCurrency().getCurCode();
    }

    private void showInFolder() {
        Util1.openFolder(lastPath);
    }

    private void export() {
        String name = tabMain.getTitleAt(tabMain.getSelectedIndex());
        switch (name) {
            case "COA" ->
                exportCOAExcels();
            case "Trader" ->
                exportTraderExcels();
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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtDate = new javax.swing.JTextField();
        btnExport = new javax.swing.JButton();
        lblMessage = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtCurrency = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        tabMain = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        chkSelect = new javax.swing.JCheckBox();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCOA = new javax.swing.JTable();
        txtSearch = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblCOARecord = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblTrader = new javax.swing.JTable();
        txtTraderSearch = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        chkTraderSelect = new javax.swing.JCheckBox();
        jSeparator2 = new javax.swing.JSeparator();
        lblTraderRecord = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Date");

        btnExport.setBackground(Global.selectionColor);
        btnExport.setFont(Global.lableFont);
        btnExport.setForeground(new java.awt.Color(255, 255, 255));
        btnExport.setText("Export");
        btnExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportActionPerformed(evt);
            }
        });

        lblMessage.setFont(Global.lableFont);
        lblMessage.setText("-");

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Currency");

        jButton2.setFont(Global.lableFont);
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

        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Search");

        jLabel5.setText("Records : ");

        lblCOARecord.setText("0");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(12, 12, 12)
                        .addComponent(txtSearch))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(chkSelect)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblCOARecord, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(lblCOARecord))
                .addContainerGap())
        );

        tabMain.addTab("COA", jPanel2);

        tblTrader.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tblTrader);

        txtTraderSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTraderSearchKeyReleased(evt);
            }
        });

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Search");

        chkTraderSelect.setFont(Global.lableFont);
        chkTraderSelect.setText("Select All");
        chkTraderSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkTraderSelectActionPerformed(evt);
            }
        });

        lblTraderRecord.setText("0");

        jLabel6.setText("Records : ");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 487, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(chkTraderSelect)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(12, 12, 12)
                        .addComponent(txtTraderSearch))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblTraderRecord, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkTraderSelect)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTraderSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(lblTraderRecord))
                .addContainerGap())
        );

        tabMain.addTab("Trader", jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabMain, javax.swing.GroupLayout.PREFERRED_SIZE, 499, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tabMain)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void chkSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSelectActionPerformed
        // TODO add your handling code here:
        selectCOA(cOATableModel.getListCOA());
    }//GEN-LAST:event_chkSelectActionPerformed

    private void btnExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportActionPerformed
        // TODO add your handling code here:
        export();
    }//GEN-LAST:event_btnExportActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        showInFolder();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        // TODO add your handling code here:
        if (txtSearch.getText().isEmpty()) {
            sorterCOA.setRowFilter(null);
        } else {
            sorterCOA.setRowFilter(swrfCOA);
        }
    }//GEN-LAST:event_txtSearchKeyReleased

    private void txtTraderSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTraderSearchKeyReleased
        // TODO add your handling code here:
        if (txtTraderSearch.getText().isEmpty()) {
            sorterTrader.setRowFilter(null);
        } else {
            sorterTrader.setRowFilter(swrfTrader);
        }
    }//GEN-LAST:event_txtTraderSearchKeyReleased

    private void chkTraderSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkTraderSelectActionPerformed
        // TODO add your handling code here:
        selectTrader(traderTableModel.getListTrader());
    }//GEN-LAST:event_chkTraderSelectActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExport;
    private javax.swing.JCheckBox chkSelect;
    private javax.swing.JCheckBox chkTraderSelect;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblCOARecord;
    private javax.swing.JLabel lblMessage;
    private javax.swing.JLabel lblTraderRecord;
    private javax.swing.JTabbedPane tabMain;
    private javax.swing.JTable tblCOA;
    private javax.swing.JTable tblTrader;
    private javax.swing.JTextField txtCurrency;
    private javax.swing.JTextField txtDate;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtTraderSearch;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {
    }
}
