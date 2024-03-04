/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com;

import com.common.JasperReportUtil;
import com.common.ProUtil;
import com.common.Util1;
import com.inventory.entity.Message;
import com.repo.InventoryRepo;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JsonDataSource;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PrinterIntegration {

    private final InventoryRepo inventoryRepo;

    public void printSale(Message message) {
        String vouNo = message.getVouNo();
        Map<String, Object> params = message.getParams();
        List<Integer> listPage = message.getPageSize();
        if (listPage != null && !listPage.isEmpty()) {
            inventoryRepo.getSaleReport(vouNo).doOnSuccess((list) -> {
                listPage.forEach((page) -> {
                    try {
                        String reportPath = getSaleReportName(page);
                        String printerName = getPrinterName(page);
                        log.info("report :" + reportPath);
                        log.info("printer name :" + printerName);
                        if (reportPath != null) {
                            ByteArrayInputStream stream = new ByteArrayInputStream(Util1.listToByteArray(list));
                            JsonDataSource ds = new JsonDataSource(stream);
                            JasperPrint jp = JasperFillManager.fillReport(reportPath, params, ds);
                            JasperReportUtil.print(jp, printerName, Util1.getInteger(ProUtil.getProperty(ProUtil.PRINTER_PAGE)), page);
                        }
                    } catch (JRException e) {
                        log.error("printSale : " + e.getMessage());
                    }
                });
            }).subscribe();
        }
    }

    private String getSaleReportName(Integer page) {
        String reportName = null;
        switch (page) {
            case 1 ->
                reportName = ProUtil.getProperty(ProUtil.SALE_VOU);
            case 5 ->
                reportName = ProUtil.getProperty(ProUtil.SALE_VOU_A5);
            case 6 ->
                reportName = ProUtil.getProperty(ProUtil.SALE_VOU_NOTE);
        }
        if (reportName != null) {
            return ProUtil.getReportPath() + reportName + ".jasper";
        }
        return reportName;
    }

    private String getPrinterName(Integer page) {
        String printerName = null;
        switch (page) {
            case 1, 6 ->
                printerName = ProUtil.getProperty(ProUtil.PRINTER_POS_NAME);
            case 5 ->
                printerName = ProUtil.getProperty(ProUtil.PRINTER_NAME);
        }
        return printerName;

    }
}
