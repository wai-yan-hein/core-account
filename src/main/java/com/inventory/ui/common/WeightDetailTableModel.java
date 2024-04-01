/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.ui.common;

import com.common.ProUtil;
import com.common.SelectionObserver;
import com.common.Util1;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import lombok.Setter;

/**
 *
 * @author Lenovo
 */
public class WeightDetailTableModel extends DefaultTableModel {

    @Setter
    private SelectionObserver observer;
    @Setter
    private JTable table;

    @Override
    public boolean isCellEditable(int row, int column) {
        int col = table.getColumnCount() - 1;
        return column != col;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        double weight = Util1.getDouble(value);
        if (weight > 0) {
            double maxWt = ProUtil.getMaxSW();
            if (weight > maxWt) {
                int yn = JOptionPane.showConfirmDialog(table, "Input Weight is greater than " + maxWt, "Warning", JOptionPane.YES_OPTION, JOptionPane.WARNING_MESSAGE);
                if (yn == JOptionPane.YES_OPTION) {
                    super.setValueAt(weight, row, column);
                } else {
                    super.setValueAt(0.0, row, column);
                }
            } else {
                super.setValueAt(weight, row, column);
            }
            calTotal(row);
            observer.selected("CAL_TOTAL", "CAL_TOTAL");
            addNewRow(true);

        }

    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        // Return Double.class for all columns
        return Double.class;
    }

    public void calTotal(int row) {
        double ttlWt = 0;
        int colCount = table.getColumnCount();
        for (int col = 0; col < colCount - 1; col++) {
            ttlWt += Util1.getDouble(table.getValueAt(row, col));
        }
        super.setValueAt(ttlWt, row, colCount - 1);
        fireTableCellUpdated(row, colCount - 1);
    }

    public void addNewRow(boolean foucs) {
        if (needAddRow()) {
            Object[] rowData = new Object[15];
            addRow(rowData);
            if (foucs) {
                focusTable();
            }
        }
    }

    private boolean needAddRow() {
        int rowCount = table.getRowCount();
        if (rowCount == 0) {
            return true;
        }
        int colCount = getColumnCount();
        for (int col = 0; col < colCount; col++) {
            double wt = Util1.getDouble(getValueAt(rowCount - 1, col));
            if (wt <= 0) {
                return false;
            }
        }
        return true;
    }

    private void focusTable() {
        int rc = table.getRowCount();
        if (rc >= 1) {
            table.setRowSelectionInterval(rc - 1, rc - 1);
            table.setColumnSelectionInterval(0, 0);
            table.requestFocus();
        } else {
            findZeroAndFoucs();
        }
    }

    private void findZeroAndFoucs() {
        int row = table.getRowCount();
        int col = table.getColumnCount();
        for (int r = 0; r < row; r++) {
            for (int c = 0; c < col; c++) {
                double value = Util1.getDouble(table.getValueAt(row, col));
                if (value <= 0) {
                    setSelection(row, col);
                }
            }
        }
    }

    private void setSelection(int row, int column) {
        table.setRowSelectionInterval(row, row);
        table.setColumnSelectionInterval(column, column);
        table.requestFocus();
    }

    public void clear() {
        setRowCount(0);
    }

}
