/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.common;

import com.inventory.model.ClosingBalance;
import com.inventory.model.General;
import com.inventory.model.VPurchase;
import com.inventory.model.VSale;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.core.task.TaskExecutor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class ExcelExporter {

    public static final int MESSAGE = 1;
    public static final int FINISH = 2;
    public static final int ERROR = 3;

    private SelectionObserver observer;
    private TaskExecutor taskExecutor;
    private static final String OUTPUT_FILE_PATH = System.getProperty("user.home") + "/Downloads/";
    private String lastPath;

    public String getLastPath() {
        return lastPath;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public ExcelExporter() {
    }

    public void exportStockInOutSummary(List<ClosingBalance> data, String reportName) {
        observer.selected(MESSAGE, "ready to do." + data.size());
        observer.selected(FINISH, "ready to do." + data.size());
        String outputPath = OUTPUT_FILE_PATH + reportName.concat(".xlsx");
        taskExecutor.execute(() -> {
            try (SXSSFWorkbook workbook = new SXSSFWorkbook(); FileOutputStream outputStream = new FileOutputStream(outputPath)) {
                workbook.setCompressTempFiles(true); // Enable temporary file compression for improved performance
                Font font = workbook.createFont();
                font.setFontName("Pyidaungsu");
                font.setFontHeightInPoints((short) 12);
                CellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setFont(font);
                String sheetName = getSheetName(reportName);
                createStockInOutSummary(workbook, data, sheetName, cellStyle);
                observer.selected(MESSAGE, "Exporting File... Please wait.");
                workbook.write(outputStream);
                lastPath = outputPath;
                observer.selected(FINISH, "complete.");
            } catch (IOException e) {
                observer.selected(ERROR, e.getMessage());
            }
        });
    }

    public void exportStockInOutDetail(List<ClosingBalance> data, String stockName) {
        observer.selected(MESSAGE, "ready to do." + data.size());
        observer.selected(FINISH, "ready to do." + data.size());
        String outputPath = OUTPUT_FILE_PATH + "StockInOutDetail-" + stockName.concat(".xlsx");
        taskExecutor.execute(() -> {
            try (SXSSFWorkbook workbook = new SXSSFWorkbook(); FileOutputStream outputStream = new FileOutputStream(outputPath)) {
                workbook.setCompressTempFiles(true); // Enable temporary file compression for improved performance
                Font font = workbook.createFont();
                font.setFontName("Pyidaungsu");
                font.setFontHeightInPoints((short) 12);
                CellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setFont(font);
                String sheetName = getSheetName(stockName);
                createStockInOutDetail(workbook, data, sheetName, cellStyle);
                observer.selected(MESSAGE, "Exporting File... Please wait.");
                workbook.write(outputStream);
                lastPath = outputPath;
                observer.selected(FINISH, "complete.");
            } catch (IOException e) {
                observer.selected(ERROR, e.getMessage());
            }
        });
    }

    private void createStockInOutDetail(Workbook workbook, List<ClosingBalance> data, String sheetName,
            CellStyle cellStyle) {
        String[] HEADER = {
            "Date", "Remark", "Opening", "Purhcase", "In", "Sale", "Out", "Closing"
        };
        String uniqueSheetName = generateUniqueSheetName(workbook, sheetName);
        Sheet sheet = workbook.createSheet(uniqueSheetName);
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < HEADER.length; i++) {
            headerRow.createCell(i).setCellValue(HEADER[i]);
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
        for (ClosingBalance d : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(d.getVouDate());
            row.createCell(1).setCellValue(d.getRemark());
            row.createCell(2).setCellValue(Util1.getDouble(d.getOpenQty()));
            row.createCell(3).setCellValue(Util1.getDouble(d.getPurQty()));
            row.createCell(4).setCellValue(Util1.getDouble(d.getInQty()));
            row.createCell(5).setCellValue(Util1.getDouble(d.getSaleQty()));
            row.createCell(6).setCellValue(Util1.getDouble(d.getOutQty()));
            row.createCell(7).setCellValue(Util1.getDouble(d.getBalQty()));
            for (Cell cell : row) {
                cell.setCellStyle(cellStyle);
            }
        }
    }

    private void createStockInOutSummary(Workbook workbook, List<ClosingBalance> data, String sheetName,
            CellStyle cellStyle) {
        String[] HEADER = {
            "Stock Code", "Stock Name", "Opening", "Purhcase", "In", "Sale", "Out", "Closing"
        };
        String uniqueSheetName = generateUniqueSheetName(workbook, sheetName);
        Sheet sheet = workbook.createSheet(uniqueSheetName);
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < HEADER.length; i++) {
            headerRow.createCell(i).setCellValue(HEADER[i]);
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
        for (ClosingBalance d : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(d.getStockUsrCode());
            row.createCell(1).setCellValue(d.getStockName());
            row.createCell(2).setCellValue(Util1.getDouble(d.getOpenQty()));
            row.createCell(3).setCellValue(Util1.getDouble(d.getPurQty()));
            row.createCell(4).setCellValue(Util1.getDouble(d.getInQty()));
            row.createCell(5).setCellValue(Util1.getDouble(d.getSaleQty()));
            row.createCell(6).setCellValue(Util1.getDouble(d.getOutQty()));
            row.createCell(7).setCellValue(Util1.getDouble(d.getBalQty()));
            for (Cell cell : row) {
                cell.setCellStyle(cellStyle);
            }
        }
    }

    public void exportStockListByGroup(List<General> data, String reportName) {
        observer.selected(MESSAGE, "ready to do." + data.size());
        observer.selected(FINISH, "ready to do." + data.size());
        String outputPath = OUTPUT_FILE_PATH + reportName.concat(".xlsx");
        taskExecutor.execute(() -> {
            try (SXSSFWorkbook workbook = new SXSSFWorkbook(); FileOutputStream outputStream = new FileOutputStream(outputPath)) {
                workbook.setCompressTempFiles(true); // Enable temporary file compression for improved performance
                Font font = workbook.createFont();
                font.setFontName("Pyidaungsu");
                font.setFontHeightInPoints((short) 12);
                CellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setFont(font);
                String sheetName = getSheetName(reportName);
                createStockListByGroup(workbook, data, sheetName, cellStyle);
                observer.selected(MESSAGE, "Exporting File... Please wait.");
                workbook.write(outputStream);
                lastPath = outputPath;
                observer.selected(FINISH, "complete.");
            } catch (IOException e) {
                observer.selected(ERROR, e.getMessage());
            }
        });
    }

    private void createStockListByGroup(Workbook workbook, List<General> data, String sheetName,
            CellStyle cellStyle) {
        String[] HEADER = {
            "Group", "System Code", "Stock Code", "Stock Name", "Category", "Brand", "Unit Relation"
        };
        String uniqueSheetName = generateUniqueSheetName(workbook, sheetName);
        Sheet sheet = workbook.createSheet(uniqueSheetName);
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < HEADER.length; i++) {
            headerRow.createCell(i).setCellValue(HEADER[i]);
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
        for (General d : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(d.getStockTypeName());
            row.createCell(1).setCellValue(d.getSysCode());
            row.createCell(2).setCellValue(d.getStockCode());
            row.createCell(3).setCellValue(d.getStockName());
            row.createCell(4).setCellValue(d.getCategoryName());
            row.createCell(5).setCellValue(d.getBrandName());
            row.createCell(6).setCellValue(d.getQtyRel());
            for (Cell cell : row) {
                cell.setCellStyle(cellStyle);
            }
        }
    }

    public void exportSaleByStockDeatail(List<VSale> data, String reportName) {
        observer.selected(MESSAGE, "ready to do." + data.size());
        observer.selected(FINISH, "ready to do." + data.size());
        String outputPath = OUTPUT_FILE_PATH + reportName.concat(".xlsx");
        taskExecutor.execute(() -> {
            try (SXSSFWorkbook workbook = new SXSSFWorkbook(); FileOutputStream outputStream = new FileOutputStream(outputPath)) {
                workbook.setCompressTempFiles(true); // Enable temporary file compression for improved performance
                Font font = workbook.createFont();
                font.setFontName("Pyidaungsu");
                font.setFontHeightInPoints((short) 12);
                CellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setFont(font);
                String sheetName = getSheetName(reportName);
                createSaleByStockDeatail(workbook, data, sheetName, cellStyle);
                observer.selected(MESSAGE, "Exporting File... Please wait.");
                workbook.write(outputStream);
                lastPath = outputPath;
                observer.selected(FINISH, "complete.");
            } catch (IOException e) {
                observer.selected(ERROR, e.getMessage());
            }
        });
    }

    private void createSaleByStockDeatail(Workbook workbook, List<VSale> data, String sheetName,
            CellStyle cellStyle) {
        String[] HEADER = {
            "Stock Name", "Date", "Voucher No", "Customer Name", "Unit", "Qty", "Price", "Amount"
        };
        String uniqueSheetName = generateUniqueSheetName(workbook, sheetName);
        Sheet sheet = workbook.createSheet(uniqueSheetName);
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < HEADER.length; i++) {
            headerRow.createCell(i).setCellValue(HEADER[i]);
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
        for (VSale d : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(d.getStockName());
            row.createCell(1).setCellValue(d.getVouDate());
            row.createCell(2).setCellValue(d.getVouNo());
            row.createCell(3).setCellValue(d.getTraderName());
            row.createCell(4).setCellValue(d.getSaleUnit());
            row.createCell(5).setCellValue(Util1.getDouble(d.getQty()));
            row.createCell(6).setCellValue(Util1.getDouble(d.getSalePrice()));
            row.createCell(7).setCellValue(Util1.getDouble(d.getSaleAmount()));
            for (Cell cell : row) {
                cell.setCellStyle(cellStyle);
            }
        }
    }

    public void exportSaleByCustomerDeatail(List<VSale> data, String reportName) {
        observer.selected(MESSAGE, "ready to do." + data.size());
        observer.selected(FINISH, "ready to do." + data.size());
        String outputPath = OUTPUT_FILE_PATH + reportName.concat(".xlsx");
        taskExecutor.execute(() -> {
            try (SXSSFWorkbook workbook = new SXSSFWorkbook(); FileOutputStream outputStream = new FileOutputStream(outputPath)) {
                workbook.setCompressTempFiles(true); // Enable temporary file compression for improved performance
                Font font = workbook.createFont();
                font.setFontName("Pyidaungsu");
                font.setFontHeightInPoints((short) 12);
                CellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setFont(font);
                String sheetName = getSheetName(reportName);
                createSaleByCustomerDeatail(workbook, data, sheetName, cellStyle);
                observer.selected(MESSAGE, "Exporting File... Please wait.");
                workbook.write(outputStream);
                lastPath = outputPath;
                observer.selected(FINISH, "complete.");
            } catch (IOException e) {
                observer.selected(ERROR, e.getMessage());
            }
        });
    }

    private void createSaleByCustomerDeatail(Workbook workbook, List<VSale> data, String sheetName,
            CellStyle cellStyle) {
        String[] HEADER = {
            "Customer Name", "Address", "Date", "Voucher No", "Stock Name", "Unit", "Qty", "Price", "Amount"
        };
        String uniqueSheetName = generateUniqueSheetName(workbook, sheetName);
        Sheet sheet = workbook.createSheet(uniqueSheetName);
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < HEADER.length; i++) {
            headerRow.createCell(i).setCellValue(HEADER[i]);
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
        for (VSale d : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(d.getTraderName());
            row.createCell(1).setCellValue(d.getAddress());
            row.createCell(2).setCellValue(d.getVouDate());
            row.createCell(3).setCellValue(d.getVouNo());
            row.createCell(4).setCellValue(d.getStockName());
            row.createCell(5).setCellValue(d.getSaleUnit());
            row.createCell(6).setCellValue(Util1.getDouble(d.getQty()));
            row.createCell(7).setCellValue(Util1.getDouble(d.getSalePrice()));
            row.createCell(8).setCellValue(Util1.getDouble(d.getSaleAmount()));
            for (Cell cell : row) {
                cell.setCellStyle(cellStyle);
            }
        }
    }

    public void exportPurchaseBySupplierDeatail(List<VPurchase> data, String reportName) {
        observer.selected(MESSAGE, "ready to do." + data.size());
        observer.selected(FINISH, "ready to do." + data.size());
        String outputPath = OUTPUT_FILE_PATH + reportName.concat(".xlsx");
        taskExecutor.execute(() -> {
            try (SXSSFWorkbook workbook = new SXSSFWorkbook(); FileOutputStream outputStream = new FileOutputStream(outputPath)) {
                workbook.setCompressTempFiles(true); // Enable temporary file compression for improved performance
                Font font = workbook.createFont();
                font.setFontName("Pyidaungsu");
                font.setFontHeightInPoints((short) 12);
                CellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setFont(font);
                String sheetName = getSheetName(reportName);
                createPurchaseBySupplierDeatail(workbook, data, sheetName, cellStyle);
                observer.selected(MESSAGE, "Exporting File... Please wait.");
                workbook.write(outputStream);
                lastPath = outputPath;
                observer.selected(FINISH, "complete.");
            } catch (IOException e) {
                observer.selected(ERROR, e.getMessage());
            }
        });
    }

    private void createPurchaseBySupplierDeatail(Workbook workbook, List<VPurchase> data, String sheetName,
            CellStyle cellStyle) {
        String[] HEADER = {
            "Supplier Name", "Address", "Date", "Voucher No", "Stock Name", "Unit", "Qty", "Price", "Amount"
        };
        String uniqueSheetName = generateUniqueSheetName(workbook, sheetName);
        Sheet sheet = workbook.createSheet(uniqueSheetName);
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < HEADER.length; i++) {
            headerRow.createCell(i).setCellValue(HEADER[i]);
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
        for (VPurchase d : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(d.getTraderName());
            row.createCell(1).setCellValue(d.getAddress());
            row.createCell(2).setCellValue(d.getVouDate());
            row.createCell(3).setCellValue(d.getVouNo());
            row.createCell(4).setCellValue(d.getStockName());
            row.createCell(5).setCellValue(d.getPurUnit());
            row.createCell(6).setCellValue(Util1.getDouble(d.getQty()));
            row.createCell(7).setCellValue(Util1.getDouble(d.getPurPrice()));
            row.createCell(8).setCellValue(Util1.getDouble(d.getPurAmount()));
            for (Cell cell : row) {
                cell.setCellStyle(cellStyle);
            }
        }
    }

    public void exportPurchaseByStockDeatail(List<VPurchase> data, String reportName) {
        observer.selected(MESSAGE, "ready to do." + data.size());
        observer.selected(FINISH, "ready to do." + data.size());
        String outputPath = OUTPUT_FILE_PATH + reportName.concat(".xlsx");
        taskExecutor.execute(() -> {
            try (SXSSFWorkbook workbook = new SXSSFWorkbook(); FileOutputStream outputStream = new FileOutputStream(outputPath)) {
                workbook.setCompressTempFiles(true); // Enable temporary file compression for improved performance
                Font font = workbook.createFont();
                font.setFontName("Pyidaungsu");
                font.setFontHeightInPoints((short) 12);
                CellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setFont(font);
                String sheetName = getSheetName(reportName);
                createPurchaseByStockDeatail(workbook, data, sheetName, cellStyle);
                observer.selected(MESSAGE, "Exporting File... Please wait.");
                workbook.write(outputStream);
                lastPath = outputPath;
                observer.selected(FINISH, "complete.");
            } catch (IOException e) {
                observer.selected(ERROR, e.getMessage());
            }
        });
    }

    private void createPurchaseByStockDeatail(Workbook workbook, List<VPurchase> data, String sheetName,
            CellStyle cellStyle) {
        String[] HEADER = {
            "Stock Name", "Date", "Voucher No", "Supplier Name", "Unit", "Qty", "Price", "Amount"
        };
        String uniqueSheetName = generateUniqueSheetName(workbook, sheetName);
        Sheet sheet = workbook.createSheet(uniqueSheetName);
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < HEADER.length; i++) {
            headerRow.createCell(i).setCellValue(HEADER[i]);
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
        for (VPurchase d : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(d.getStockName());
            row.createCell(1).setCellValue(d.getVouDate());
            row.createCell(2).setCellValue(d.getVouNo());
            row.createCell(3).setCellValue(d.getTraderName());
            row.createCell(4).setCellValue(d.getPurUnit());
            row.createCell(5).setCellValue(Util1.getDouble(d.getQty()));
            row.createCell(6).setCellValue(Util1.getDouble(d.getPurPrice()));
            row.createCell(7).setCellValue(Util1.getDouble(d.getPurAmount()));
            for (Cell cell : row) {
                cell.setCellStyle(cellStyle);
            }
        }
    }

    public void exportTopSaleByCustomer(List<General> data, String reportName) {
        observer.selected(MESSAGE, "ready to do." + data.size());
        observer.selected(FINISH, "ready to do." + data.size());
        String outputPath = OUTPUT_FILE_PATH + reportName.concat(".xlsx");
        taskExecutor.execute(() -> {
            try (SXSSFWorkbook workbook = new SXSSFWorkbook(); FileOutputStream outputStream = new FileOutputStream(outputPath)) {
                workbook.setCompressTempFiles(true); // Enable temporary file compression for improved performance
                Font font = workbook.createFont();
                font.setFontName("Pyidaungsu");
                font.setFontHeightInPoints((short) 12);
                CellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setFont(font);
                String sheetName = getSheetName(reportName);
                createTopSaleByCustomer(workbook, data, sheetName, cellStyle);
                observer.selected(MESSAGE, "Exporting File... Please wait.");
                workbook.write(outputStream);
                lastPath = outputPath;
                observer.selected(FINISH, "complete.");
            } catch (IOException e) {
                observer.selected(ERROR, e.getMessage());
            }
        });
    }

    private void createTopSaleByCustomer(Workbook workbook, List<General> data, String sheetName,
            CellStyle cellStyle) {
        String[] HEADER = {
            "Customer Code", "Customer Name", "Address", "Voucher Qty", "Amount"
        };
        String uniqueSheetName = generateUniqueSheetName(workbook, sheetName);
        Sheet sheet = workbook.createSheet(uniqueSheetName);
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < HEADER.length; i++) {
            headerRow.createCell(i).setCellValue(HEADER[i]);
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
        for (General d : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(d.getTraderCode());
            row.createCell(1).setCellValue(d.getTraderName());
            row.createCell(2).setCellValue(d.getAddress());
            row.createCell(3).setCellValue(Util1.getDouble(d.getTotalQty()));
            row.createCell(4).setCellValue(Util1.getDouble(d.getAmount()));
            for (Cell cell : row) {
                cell.setCellStyle(cellStyle);
            }
        }
    }

    public void exportTopSaleBySaleMan(List<General> data, String reportName) {
        observer.selected(MESSAGE, "ready to do." + data.size());
        observer.selected(FINISH, "ready to do." + data.size());
        String outputPath = OUTPUT_FILE_PATH + reportName.concat(".xlsx");
        taskExecutor.execute(() -> {
            try (SXSSFWorkbook workbook = new SXSSFWorkbook(); FileOutputStream outputStream = new FileOutputStream(outputPath)) {
                workbook.setCompressTempFiles(true); // Enable temporary file compression for improved performance
                Font font = workbook.createFont();
                font.setFontName("Pyidaungsu");
                font.setFontHeightInPoints((short) 12);
                CellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setFont(font);
                String sheetName = getSheetName(reportName);
                createTopSaleBySaleMan(workbook, data, sheetName, cellStyle);
                observer.selected(MESSAGE, "Exporting File... Please wait.");
                workbook.write(outputStream);
                lastPath = outputPath;
                observer.selected(FINISH, "complete.");
            } catch (IOException e) {
                observer.selected(ERROR, e.getMessage());
            }
        });
    }

    private void createTopSaleBySaleMan(Workbook workbook, List<General> data, String sheetName,
            CellStyle cellStyle) {
        String[] HEADER = {
            "Saleman Code", "Saleman Name", "Voucher Qty", "Amount"
        };
        String uniqueSheetName = generateUniqueSheetName(workbook, sheetName);
        Sheet sheet = workbook.createSheet(uniqueSheetName);
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < HEADER.length; i++) {
            headerRow.createCell(i).setCellValue(HEADER[i]);
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
        for (General d : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(d.getSaleManCode());
            row.createCell(1).setCellValue(d.getSaleManName());
            row.createCell(2).setCellValue(Util1.getDouble(d.getTotalQty()));
            row.createCell(3).setCellValue(Util1.getDouble(d.getAmount()));
            for (Cell cell : row) {
                cell.setCellStyle(cellStyle);
            }
        }
    }

    public void exportTopSaleByStock(List<General> data, String reportName) {
        observer.selected(MESSAGE, "ready to do." + data.size());
        observer.selected(FINISH, "ready to do." + data.size());
        String outputPath = OUTPUT_FILE_PATH + reportName.concat(".xlsx");
        taskExecutor.execute(() -> {
            try (SXSSFWorkbook workbook = new SXSSFWorkbook(); FileOutputStream outputStream = new FileOutputStream(outputPath)) {
                workbook.setCompressTempFiles(true); // Enable temporary file compression for improved performance
                Font font = workbook.createFont();
                font.setFontName("Pyidaungsu");
                font.setFontHeightInPoints((short) 12);
                CellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setFont(font);
                String sheetName = getSheetName(reportName);
                createTopSaleByStock(workbook, data, sheetName, cellStyle);
                observer.selected(MESSAGE, "Exporting File... Please wait.");
                workbook.write(outputStream);
                lastPath = outputPath;
                observer.selected(FINISH, "complete.");
            } catch (IOException e) {
                observer.selected(ERROR, e.getMessage());
            }
        });
    }

    private void createTopSaleByStock(Workbook workbook, List<General> data, String sheetName,
            CellStyle cellStyle) {
        String[] HEADER = {
            "Stock Code", "Stock Name", "Qty", "Unit"
        };
        String uniqueSheetName = generateUniqueSheetName(workbook, sheetName);
        Sheet sheet = workbook.createSheet(uniqueSheetName);
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < HEADER.length; i++) {
            headerRow.createCell(i).setCellValue(HEADER[i]);
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
        for (General d : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(d.getStockCode());
            row.createCell(1).setCellValue(d.getStockName());
            row.createCell(2).setCellValue(Util1.getDouble(d.getSmallQty()));
            row.createCell(3).setCellValue(d.getUnit());
            for (Cell cell : row) {
                cell.setCellStyle(cellStyle);
            }
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

    private String getSheetName(String name) {
        String tmp = Util1.replaceSpecialCharactersWithSpace(name);
        return Util1.autoCorrectSheetName(tmp);
    }

}
