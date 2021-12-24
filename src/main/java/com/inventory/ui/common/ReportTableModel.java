/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.inventory.model.VRoleMenu;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Component
public class ReportTableModel extends AbstractTableModel {

    private final String[] columnNames = {"Inventory Report"};
    private List<VRoleMenu> listReport = new ArrayList<>();

    @Override
    public int getRowCount() {
        return listReport.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        VRoleMenu report = listReport.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return report.getMenuName();
            default:
                return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public List<VRoleMenu> getListReport() {
        return listReport;
    }

    public void setListReport(List<VRoleMenu> listReport) {
        this.listReport = listReport;
        fireTableDataChanged();
    }

    public VRoleMenu getReport(int row) {
        return listReport.get(row);
    }

    public void setReport(VRoleMenu report, int row) {
        if (!listReport.isEmpty()) {
            listReport.set(row, report);
            fireTableRowsUpdated(row, row);
        }
    }

    public void addReport(VRoleMenu item) {
        if (!listReport.isEmpty()) {
            listReport.add(item);
            fireTableRowsInserted(listReport.size() - 1, listReport.size() - 1);
        }
    }

    public void refresh() {
        fireTableDataChanged();
    }
}
