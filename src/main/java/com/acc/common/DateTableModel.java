/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.common;

import com.acc.model.DateModel;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class DateTableModel extends AbstractTableModel {

    private List<DateModel> listDate = new ArrayList<>();
    private final String[] columnNames = {"Date"};

    public void setListDate(List<DateModel> listDate) {
        this.listDate = listDate;
        fireTableDataChanged();
    }


    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Class getColumnClass(int column) {
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listDate == null) {
            return null;
        }

        if (listDate.isEmpty()) {
            return null;
        }

        try {
            DateModel coa = listDate.get(row);

            switch (column) {
                case 0 -> {
                    //Code
                    return coa.getDescription();
                }
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        if (listDate == null) {
            return 0;
        } else {
            return listDate.size();
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public DateModel getDate(int row) {
        return listDate.get(row);
    }

    public int getSize() {
        if (listDate == null) {
            return 0;
        } else {
            return listDate.size();
        }
    }

    public void setDateModel(DateModel m, int row) {
        listDate.set(row, m);
        fireTableRowsUpdated(row, row);
    }
}
