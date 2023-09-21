/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.user.common;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class FileTableModel extends AbstractTableModel {

    private List<File> listFile = new ArrayList();
    private final String[] columnNames = {"File Name"};

    public void setListFile(List<File> listFile) {
        this.listFile = listFile;
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
        try {
            File p = listFile.get(row);
            switch (column) {
                case 0 -> {
                    return p.getName();
                }
            }
        } catch (Exception e) {
            log.error(String.format("getValueAt : %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {

    }

    @Override
    public int getRowCount() {
        return listFile.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public File getFile(int row) {
        return listFile.get(row);
    }
}
