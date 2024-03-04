/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.common;

import com.acc.model.VApar;
import com.acc.model.VTriBalance;
import com.inventory.entity.General;
import com.inventory.entity.Stock;
import com.inventory.entity.VOpening;
import com.inventory.entity.VPurchase;
import com.inventory.entity.VSale;
import com.inventory.entity.VStockIO;
import com.ui.management.model.ClosingBalance;
import com.inventory.entity.StockValue;
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
            var row = sheet.createRow(rowNum++);
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

    public void exportSaleByStockDetail(List<VSale> data, String reportName) {
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
                createSaleByStockDetail(workbook, data, sheetName, cellStyle);
                observer.selected(MESSAGE, "Exporting File... Please wait.");
                workbook.write(outputStream);
                lastPath = outputPath;
                observer.selected(FINISH, "complete.");
            } catch (IOException e) {
                observer.selected(ERROR, e.getMessage());
            }
        });
    }

    private void createSaleByStockDetail(Workbook workbook, List<VSale> data, String sheetName,
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
            "Stock Code", "Stock Name", "Relation", "Qty"
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
            row.createCell(2).setCellValue(d.getRelation());
            row.createCell(3).setCellValue(Util1.getDouble(d.getSmallQty()));
            for (Cell cell : row) {
                cell.setCellStyle(cellStyle);
            }
        }
    }

    public void exportSaleByStockSummary(List<VSale> data, String reportName) {
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
                createSaleByStockSummary(workbook, data, sheetName, cellStyle);
                observer.selected(MESSAGE, "Exporting File... Please wait.");
                workbook.write(outputStream);
                lastPath = outputPath;
                observer.selected(FINISH, "complete.");
            } catch (IOException e) {
                observer.selected(ERROR, e.getMessage());
            }
        });
    }

    private void createSaleByStockSummary(Workbook workbook, List<VSale> data, String sheetName,
            CellStyle cellStyle) {
        String[] HEADER = {
            "Stock Code", "Stock Name", "Relation", "Total Qty", "Total Amount"
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
            var row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(d.getStockCode());
            row.createCell(1).setCellValue(d.getStockName());
            row.createCell(2).setCellValue(d.getRelName());
            row.createCell(3).setCellValue(Util1.getDouble(d.getTotalQty()));
            row.createCell(4).setCellValue(Util1.getDouble(d.getSaleAmount()));
            for (Cell cell : row) {
                cell.setCellStyle(cellStyle);
            }
        }
    }

    public void exportSaleByCustomerSummary(List<VSale> data, String reportName) {
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
                createSaleByCustomerSummary(workbook, data, sheetName, cellStyle);
                observer.selected(MESSAGE, "Exporting File... Please wait.");
                workbook.write(outputStream);
                lastPath = outputPath;
                observer.selected(FINISH, "complete.");
            } catch (IOException e) {
                observer.selected(ERROR, e.getMessage());
            }
        });
    }

    private void createSaleByCustomerSummary(Workbook workbook, List<VSale> data, String sheetName,
            CellStyle cellStyle) {
        String[] HEADER = {
            "Customer Name", "Address", "Stock Code", "Stock Name", "Relation", "Total Qty", "Total Amount"
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
            row.createCell(2).setCellValue(d.getStockCode());
            row.createCell(3).setCellValue(d.getStockName());
            row.createCell(4).setCellValue(d.getRelName());
            row.createCell(5).setCellValue(Util1.getDouble(d.getTotalQty()));
            row.createCell(6).setCellValue(Util1.getDouble(d.getSaleAmount()));
            for (Cell cell : row) {
                cell.setCellStyle(cellStyle);
            }
        }
    }

    public void exportPurchaseBySupplierSummary(List<VPurchase> data, String reportName) {
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
                createPurchaseBySupplierSummary(workbook, data, sheetName, cellStyle);
                observer.selected(MESSAGE, "Exporting File... Please wait.");
                workbook.write(outputStream);
                lastPath = outputPath;
                observer.selected(FINISH, "complete.");
            } catch (IOException e) {
                observer.selected(ERROR, e.getMessage());
            }
        });
    }

    private void createPurchaseBySupplierSummary(Workbook workbook, List<VPurchase> data, String sheetName,
            CellStyle cellStyle) {
        String[] HEADER = {
            "Supplier Name", "Address", "Stock Code", "Stock Name", "Relation", "Total Qty", "Total Amount"
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
            row.createCell(2).setCellValue(d.getStockCode());
            row.createCell(3).setCellValue(d.getStockName());
            row.createCell(4).setCellValue(d.getRelName());
            row.createCell(5).setCellValue(Util1.getDouble(d.getTotalQty()));
            row.createCell(6).setCellValue(Util1.getDouble(d.getPurAmount()));
            for (Cell cell : row) {
                cell.setCellStyle(cellStyle);
            }
        }
    }

    public void exportPurchaseByStockSummary(List<VPurchase> data, String reportName) {
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
                createPurchaseByStockSummary(workbook, data, sheetName, cellStyle);
                observer.selected(MESSAGE, "Exporting File... Please wait.");
                workbook.write(outputStream);
                lastPath = outputPath;
                observer.selected(FINISH, "complete.");
            } catch (IOException e) {
                observer.selected(ERROR, e.getMessage());
            }
        });
    }

    private void createPurchaseByStockSummary(Workbook workbook, List<VPurchase> data, String sheetName,
            CellStyle cellStyle) {
        String[] HEADER = {
            "Stock Code", "Stock Name", "Relation", "Total Qty", "Total Amount"
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
            row.createCell(0).setCellValue(d.getStockCode());
            row.createCell(1).setCellValue(d.getStockName());
            row.createCell(2).setCellValue(d.getRelName());
            row.createCell(3).setCellValue(Util1.getDouble(d.getTotalQty()));
            row.createCell(4).setCellValue(Util1.getDouble(d.getPurAmount()));
            for (Cell cell : row) {
                cell.setCellStyle(cellStyle);
            }
        }
    }

    public void exportOpeningByGroup(List<VOpening> data, String reportName) {
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
                createOpeningByGroup(workbook, data, sheetName, cellStyle);
                observer.selected(MESSAGE, "Exporting File... Please wait.");
                workbook.write(outputStream);
                lastPath = outputPath;
                observer.selected(FINISH, "complete.");
            } catch (IOException e) {
                observer.selected(ERROR, e.getMessage());
            }
        });
    }

    private void createOpeningByGroup(Workbook workbook, List<VOpening> data, String sheetName,
            CellStyle cellStyle) {
        String[] HEADER = {
            "Group Name", "System Code", "Stock Code", "Stock Name", "Unit", "Qty", "Price", "Amount"
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
        for (VOpening d : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(d.getStockTypeName());
            row.createCell(1).setCellValue(d.getStockCode());
            row.createCell(2).setCellValue(d.getStockUserCode());
            row.createCell(3).setCellValue(d.getStockName());
            row.createCell(4).setCellValue(d.getUnit());
            row.createCell(5).setCellValue(Util1.getDouble(d.getQty()));
            row.createCell(6).setCellValue(Util1.getDouble(d.getPrice()));
            row.createCell(7).setCellValue(Util1.getDouble(d.getAmount()));
            for (Cell cell : row) {
                cell.setCellStyle(cellStyle);
            }
        }
    }

    public void exportOpeningByLocation(List<VOpening> data, String reportName) {
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
                createOpeningByLocation(workbook, data, sheetName, cellStyle);
                observer.selected(MESSAGE, "Exporting File... Please wait.");
                workbook.write(outputStream);
                lastPath = outputPath;
                observer.selected(FINISH, "complete.");
            } catch (IOException e) {
                observer.selected(ERROR, e.getMessage());
            }
        });
    }

    private void createOpeningByLocation(Workbook workbook, List<VOpening> data, String sheetName,
            CellStyle cellStyle) {
        String[] HEADER = {
            "Location", "Stock Code", "Stock Name", "Unit", "Qty", "Price", "Amount"
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
        for (VOpening d : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(d.getLocationName());
            row.createCell(1).setCellValue(d.getStockUserCode());
            row.createCell(2).setCellValue(d.getStockName());
            row.createCell(3).setCellValue(d.getUnit());
            row.createCell(4).setCellValue(Util1.getDouble(d.getQty()));
            row.createCell(5).setCellValue(Util1.getDouble(d.getPrice()));
            row.createCell(6).setCellValue(Util1.getDouble(d.getAmount()));
            for (Cell cell : row) {
                cell.setCellStyle(cellStyle);
            }
        }
    }

    public void exportStockOutByVoucherTypeDetail(List<VStockIO> data, String reportName) {
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
                createStockOutByVoucherTypeDetail(workbook, data, sheetName, cellStyle);
                observer.selected(MESSAGE, "Exporting File... Please wait.");
                workbook.write(outputStream);
                lastPath = outputPath;
                observer.selected(FINISH, "complete.");
            } catch (IOException e) {
                observer.selected(ERROR, e.getMessage());
            }
        });
    }

    private void createStockOutByVoucherTypeDetail(Workbook workbook, List<VStockIO> data, String sheetName,
            CellStyle cellStyle) {
        String[] HEADER = {
            "Date", "Location", "Vou No", "Stock Code", "Stock Name", "Unit", "Qty", "Price", "Out-Amount"
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
        for (VStockIO d : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(d.getVouDate());
            row.createCell(1).setCellValue(d.getLocName());
            row.createCell(2).setCellValue(d.getVouNo());
            row.createCell(3).setCellValue(d.getStockUsrCode());
            row.createCell(4).setCellValue(d.getStockName());
            row.createCell(5).setCellValue(d.getOutUnit());
            row.createCell(6).setCellValue(Util1.getDouble(d.getOutQty()));
            row.createCell(7).setCellValue(Util1.getDouble(d.getCostPrice()));
            row.createCell(8).setCellValue(Util1.getDouble(d.getOutAmt()));
            for (Cell cell : row) {
                cell.setCellStyle(cellStyle);
            }
        }
    }

    public void exportStockInOutPriceCalender(List<VStockIO> data, String reportName) {
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
                createStockInOutPriceCalender(workbook, data, sheetName, cellStyle);
                observer.selected(MESSAGE, "Exporting File... Please wait.");
                workbook.write(outputStream);
                lastPath = outputPath;
                observer.selected(FINISH, "complete.");
            } catch (IOException e) {
                observer.selected(ERROR, e.getMessage());
            }
        });
    }

    private void createStockInOutPriceCalender(Workbook workbook, List<VStockIO> data, String sheetName,
            CellStyle cellStyle) {
        String[] HEADER = {
            "Stock Code", "Stock Name", "Relation", "Date", "Vou No", "Vou Type", "Remark", "Unit", "Price", "Smallest Price"
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
        for (VStockIO d : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(d.getStockCode());
            row.createCell(1).setCellValue(d.getStockName());
            row.createCell(2).setCellValue(d.getRelName());
            row.createCell(3).setCellValue(d.getVouDate());
            row.createCell(4).setCellValue(d.getVouNo());
            row.createCell(5).setCellValue(d.getVouTypeName());
            row.createCell(6).setCellValue(d.getRemark());
            row.createCell(7).setCellValue(d.getInUnit());
            row.createCell(8).setCellValue(Util1.getDouble(d.getCostPrice()));
            row.createCell(9).setCellValue(Util1.getDouble(d.getSmallPrice()));
            for (Cell cell : row) {
                cell.setCellStyle(cellStyle);
            }
        }
    }

    public void exportSaleBySaleManDetail(List<VSale> data, String reportName) {
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
                createSaleBySaleManDetail(workbook, data, sheetName, cellStyle);
                observer.selected(MESSAGE, "Exporting File... Please wait.");
                workbook.write(outputStream);
                lastPath = outputPath;
                observer.selected(FINISH, "complete.");
            } catch (IOException e) {
                observer.selected(ERROR, e.getMessage());
            }
        });
    }

    private void createSaleBySaleManDetail(Workbook workbook, List<VSale> data, String sheetName,
            CellStyle cellStyle) {
        String[] HEADER = {
            "Customer Name", "Date", "Vou No", "Stock Name", "Unit", "Qty", "Price", "Amount"
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
            row.createCell(0).setCellValue(d.getSaleManName());
            row.createCell(1).setCellValue(d.getVouDate());
            row.createCell(2).setCellValue(d.getVouNo());
            row.createCell(3).setCellValue(d.getStockName());
            row.createCell(4).setCellValue(d.getSaleUnit());
            row.createCell(5).setCellValue(Util1.getDouble(d.getQty()));
            row.createCell(6).setCellValue(Util1.getDouble(d.getSalePrice()));
            row.createCell(7).setCellValue(Util1.getDouble(d.getSaleAmount()));
            for (Cell cell : row) {
                cell.setCellStyle(cellStyle);
            }
        }
    }

    public void exportSaleBySaleManSummary(List<VSale> data, String reportName) {
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
                createSaleBySaleManSummary(workbook, data, sheetName, cellStyle);
                observer.selected(MESSAGE, "Exporting File... Please wait.");
                workbook.write(outputStream);
                lastPath = outputPath;
                observer.selected(FINISH, "complete.");
            } catch (IOException e) {
                observer.selected(ERROR, e.getMessage());
            }
        });
    }

    private void createSaleBySaleManSummary(Workbook workbook, List<VSale> data, String sheetName,
            CellStyle cellStyle) {
        String[] HEADER = {
            "Customer Name", "Stock Code", "Stock Name", "Relation", "Total Qty", "Total Amount"
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
            row.createCell(0).setCellValue(d.getSaleManName());
            row.createCell(1).setCellValue(d.getStockCode());
            row.createCell(2).setCellValue(d.getStockName());
            row.createCell(3).setCellValue(d.getRelName());
            row.createCell(4).setCellValue(Util1.getDouble(d.getTotalQty()));
            row.createCell(5).setCellValue(Util1.getDouble(d.getSaleAmount()));
            for (Cell cell : row) {
                cell.setCellStyle(cellStyle);
            }
        }
    }

    public void exportSalePriceCalender(List<VSale> data, String reportName) {
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
                createSalePriceCalender(workbook, data, sheetName, cellStyle);
                observer.selected(MESSAGE, "Exporting File... Please wait.");
                workbook.write(outputStream);
                lastPath = outputPath;
                observer.selected(FINISH, "complete.");
            } catch (IOException e) {
                observer.selected(ERROR, e.getMessage());
            }
        });
    }

    private void createSalePriceCalender(Workbook workbook, List<VSale> data, String sheetName,
            CellStyle cellStyle) {
        String[] HEADER = {
            "Stock Code", "Stock Name", "Date", "Vou No", "Customer Name", "Remark", "Currency", "Unit", "Price"
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
            row.createCell(0).setCellValue(d.getStockUserCode());
            row.createCell(1).setCellValue(d.getStockName());
            row.createCell(2).setCellValue(d.getVouDate());
            row.createCell(3).setCellValue(d.getVouNo());
            row.createCell(4).setCellValue(d.getTraderName());
            row.createCell(5).setCellValue(d.getRemark());
            row.createCell(6).setCellValue(d.getCurCode());
            row.createCell(7).setCellValue(d.getSaleUnit());
            row.createCell(8).setCellValue(Util1.getDouble(d.getSalePrice()));
            for (Cell cell : row) {
                cell.setCellStyle(cellStyle);
            }
        }
    }

    public void exportPurchasePriceCalender(List<VPurchase> data, String reportName) {
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
                createPurchasePriceCalender(workbook, data, sheetName, cellStyle);
                observer.selected(MESSAGE, "Exporting File... Please wait.");
                workbook.write(outputStream);
                lastPath = outputPath;
                observer.selected(FINISH, "complete.");
            } catch (IOException e) {
                observer.selected(ERROR, e.getMessage());
            }
        });
    }

    private void createPurchasePriceCalender(Workbook workbook, List<VPurchase> data, String sheetName,
            CellStyle cellStyle) {
        String[] HEADER = {
            "Stock Code", "Stock Name", "Date", "Vou No", "Supplier Name", "Remark", "Currency", "Unit", "Price"
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
            row.createCell(0).setCellValue(d.getStockUserCode());
            row.createCell(1).setCellValue(d.getStockName());
            row.createCell(2).setCellValue(d.getVouDate());
            row.createCell(3).setCellValue(d.getVouNo());
            row.createCell(4).setCellValue(d.getTraderName());
            row.createCell(5).setCellValue(d.getRemark());
            row.createCell(6).setCellValue(d.getCurCode());
            row.createCell(7).setCellValue(d.getPurUnit());
            row.createCell(8).setCellValue(Util1.getDouble(d.getPurPrice()));
            for (Cell cell : row) {
                cell.setCellStyle(cellStyle);
            }
        }
    }

    public void exportStockValue(List<StockValue> data, String reportName) {
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
                createStockValue(workbook, data, sheetName, cellStyle);
                observer.selected(MESSAGE, "Exporting File... Please wait.");
                workbook.write(outputStream);
                lastPath = outputPath;
                observer.selected(FINISH, "complete.");
            } catch (IOException e) {
                observer.selected(ERROR, e.getMessage());
            }
        });
    }

    public void exportStockValueQty(List<StockValue> data, String reportName) {
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
                createStockValueQty(workbook, data, sheetName, cellStyle);
                observer.selected(MESSAGE, "Exporting File... Please wait.");
                workbook.write(outputStream);
                lastPath = outputPath;
                observer.selected(FINISH, "complete.");
            } catch (IOException e) {
                observer.selected(ERROR, e.getMessage());
            }
        });
    }

    private void createStockValue(Workbook workbook, List<StockValue> data, String sheetName,
            CellStyle cellStyle) {
        String[] HEADER = {
            "Stock Code", "Stock Name", "Qty", "Relation", "Price(Avg)", "Amount(Avg)", "Price(Recent)", "Amount(Recent)",
            "Price(Cost Avg)", "Amount(Cost Avg)", "Price(Cost Recent)", "Amount(Cost Recent)", "Price(FIFO)", "Amount(FIFO)"
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
        for (StockValue d : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(d.getStockUserCode());
            row.createCell(1).setCellValue(d.getStockName());
            row.createCell(2).setCellValue(Util1.getDouble(d.getQty()));
            row.createCell(3).setCellValue(d.getRelation());
            row.createCell(4).setCellValue(Util1.getDouble(d.getPurAvgPrice()));
            row.createCell(5).setCellValue(Util1.getDouble(d.getPurAvgAmount()));
            row.createCell(6).setCellValue(Util1.getDouble(d.getRecentPrice()));
            row.createCell(7).setCellValue(Util1.getDouble(d.getRecentAmt()));
            row.createCell(8).setCellValue(Util1.getDouble(d.getInAvgPrice()));
            row.createCell(9).setCellValue(Util1.getDouble(d.getInAvgAmount()));
            row.createCell(10).setCellValue(Util1.getDouble(d.getIoRecentPrice()));
            row.createCell(11).setCellValue(Util1.getDouble(d.getIoRecentAmt()));
            row.createCell(12).setCellValue(Util1.getDouble(d.getFifoPrice()));
            row.createCell(13).setCellValue(Util1.getDouble(d.getFifoAmt()));

            for (Cell cell : row) {
                cell.setCellStyle(cellStyle);
            }
        }
    }

    private void createStockValueQty(Workbook workbook, List<StockValue> data, String sheetName,
            CellStyle cellStyle) {
        String[] HEADER = {
            "Stock Code", "Stock Name", "Balance Qty", "Purchase Price (Avg)",
            "Purchase Amount (Avg)", "Purchase Price (Recent)", "Purchase Amount (Recent)"
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
        for (StockValue d : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(d.getStockUserCode());
            row.createCell(1).setCellValue(d.getStockName());
            row.createCell(2).setCellValue(Util1.getDouble(d.getQty()));
            row.createCell(3).setCellValue(Util1.getDouble(d.getPurAvgPrice()));
            row.createCell(4).setCellValue(Util1.getDouble(d.getPurAvgAmount()));
            row.createCell(5).setCellValue(Util1.getDouble(d.getRecentPrice()));
            row.createCell(6).setCellValue(Util1.getDouble(d.getRecentAmt()));
            for (Cell cell : row) {
                cell.setCellStyle(cellStyle);
            }
        }
    }

    public void exportOpeningTemplate(List<Stock> data, String reportName) {
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
                createOpeningTemplate(workbook, data, sheetName, cellStyle);
                observer.selected(MESSAGE, "Exporting File... Please wait.");
                workbook.write(outputStream);
                lastPath = outputPath;
                observer.selected(FINISH, "complete.");
            } catch (IOException e) {
                observer.selected(ERROR, e.getMessage());
            }
        });
    }

    public void exportTriBalance(List<VTriBalance> data, String reportName) {
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
                createTriBalance(workbook, data, sheetName, cellStyle);
                observer.selected(MESSAGE, "Exporting File... Please wait.");
                workbook.write(outputStream);
                lastPath = outputPath;
                observer.selected(FINISH, "complete export in download foler.");
            } catch (IOException e) {
                observer.selected(ERROR, e.getMessage());
            }
        });
    }

    private void createTriBalance(Workbook workbook, List<VTriBalance> data, String sheetName,
            CellStyle cellStyle) {
        String[] HEADER = {
            "Code", "Chart of Account", "Currency", "Dr Amt", "Cr Amt"
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
        for (VTriBalance d : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(d.getCoaUsrCode());
            row.createCell(1).setCellValue(d.getCoaName());
            row.createCell(2).setCellValue(d.getCurCode());
            row.createCell(3).setCellValue(d.getDrAmt());
            row.createCell(4).setCellValue(d.getCrAmt());
            for (Cell cell : row) {
                cell.setCellStyle(cellStyle);
            }
        }
    }

    public void exportArAp(List<VApar> data, String reportName) {
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
                createArAp(workbook, data, sheetName, cellStyle);
                observer.selected(MESSAGE, "Exporting File... Please wait.");
                workbook.write(outputStream);
                lastPath = outputPath;
                observer.selected(FINISH, "complete export in download foler.");
            } catch (IOException e) {
                observer.selected(ERROR, e.getMessage());
            }
        });
    }

    private void createArAp(Workbook workbook, List<VApar> data, String sheetName,
            CellStyle cellStyle) {
        String[] HEADER = {
            "Code", "Name", "Account", "Currency", "Dr Amt", "Cr Amt"
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
        for (VApar d : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(d.getUserCode());
            row.createCell(1).setCellValue(d.getTraderName());
            row.createCell(2).setCellValue(d.getCoaName());
            row.createCell(3).setCellValue(d.getCurCode());
            row.createCell(4).setCellValue(d.getDrAmt());
            row.createCell(5).setCellValue(d.getCrAmt());
            for (Cell cell : row) {
                cell.setCellStyle(cellStyle);
            }
        }
    }

    private void createOpeningTemplate(Workbook workbook, List<Stock> data, String sheetName,
            CellStyle cellStyle) {
        String[] HEADER = {
            "", "SystemCode", "UserCode", "StockName",
            "Weight", "WeightUnit", "Qty", "Unit", "Price"
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
        for (Stock d : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue("");
            row.createCell(1).setCellValue(d.getKey().getStockCode());
            row.createCell(2).setCellValue(d.getUserCode());
            row.createCell(3).setCellValue(d.getStockName());
            row.createCell(4).setCellValue(Util1.getDouble(d.getWeight()));
            row.createCell(5).setCellValue(d.getWeightUnit());
            row.createCell(6).setCellValue(Util1.getDouble(0.0));
            row.createCell(7).setCellValue(d.getPurUnitCode());
            row.createCell(8).setCellValue(Util1.getDouble(d.getPurPrice()));

            for (Cell cell : row) {
                cell.setCellStyle(cellStyle);
            }
        }
    }

    // end
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
