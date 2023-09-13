/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.common;

import com.inventory.model.ClosingBalance;
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
                String sheetName = Util1.replaceSpecialCharactersWithSpace(stockName);
                createStockInOutDetail(workbook, data, Util1.autoCorrectSheetName(sheetName), cellStyle);
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
        try {
            for (int i = 0; i < HEADER.length; i++) {
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

}
