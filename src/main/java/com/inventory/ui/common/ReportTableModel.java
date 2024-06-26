/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.user.model.Menu;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Lenovo
 */
public class ReportTableModel extends AbstractTableModel {

    private final List<String> columnNames = new ArrayList<>();
    private List<Menu> listReport = new ArrayList<>();
    private Set<String> excelReport;

    public void setExcelReport(Set<String> excelReport) {
        this.excelReport = excelReport;
    }

    @Override
    public int getRowCount() {
        return listReport.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.size();
    }

    @Override
    public String getColumnName(int column) {
        return columnNames.get(column);
    }

    public ReportTableModel(String columName) {
        this.columnNames.add(0, columName);
        this.columnNames.add(1, "Excel");
        fireTableStructureChanged();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Menu report = listReport.get(rowIndex);
        return switch (columnIndex) {
            case 0 ->
                report.getMenuName();
            case 1 ->
                excelReport == null ? false : excelReport.contains(report.getMenuUrl());
            default ->
                null;
        };
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnIndex == 1 ? Boolean.class : String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public List<Menu> getListReport() {
        return listReport;
    }

    public void setListReport(List<Menu> listReport) {
        this.listReport = listReport;
        fireTableDataChanged();
    }

    public Menu getReport(int row) {
        return listReport.get(row);
    }

    public void setReport(Menu report, int row) {
        if (!listReport.isEmpty()) {
            listReport.set(row, report);
            fireTableRowsUpdated(row, row);
        }
    }

    public void addReport(Menu item) {
        if (!listReport.isEmpty()) {
            listReport.add(item);
            fireTableRowsInserted(listReport.size() - 1, listReport.size() - 1);
        }
    }

    public void refresh() {
        fireTableDataChanged();
    }
}
