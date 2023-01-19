/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.common;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.Attribute;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.PrinterName;
import javax.print.attribute.standard.PrinterState;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimplePrintServiceExporterConfiguration;

/**
 *
 * @author Lenovo
 */
public class JasperReportUtil {

    public static void print(JasperPrint jp) {
        try {
            String printerName = ProUtil.getProperty("printer.name");
            if (isOnline(printerName)) {
                PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
                printRequestAttributeSet.add(MediaSizeName.ISO_A4);
                PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
                printServiceAttributeSet.add(new PrinterName(printerName, null));
                JRPrintServiceExporter exporter = new JRPrintServiceExporter();
                SimplePrintServiceExporterConfiguration config = new SimplePrintServiceExporterConfiguration();
                config.setPrintRequestAttributeSet(printRequestAttributeSet);
                config.setPrintServiceAttributeSet(printServiceAttributeSet);
                config.setDisplayPageDialog(false);
                config.setDisplayPrintDialog(false);
                exporter.setConfiguration(config);
                exporter.setExporterInput(new SimpleExporterInput(jp));
                int count = Util1.getIntegerOne(ProUtil.getProperty("printer.pages"));
                for (int i = 0; i < count; i++) {
                    exporter.exportReport();
                }
            }
        } catch (JRException e) {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage(), "Voucher Printer", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static boolean isOnline(String printerName) {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printer : printServices) {
            if (printerName.equals(printer.getName())) {
                PrinterState state = (PrinterState) printer.getAttributes().get(PrinterState.class);
                if (state == null) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Please connect priner : " + printerName, "Printer", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        }
        return true;
    }
}
