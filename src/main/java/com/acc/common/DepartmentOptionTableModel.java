/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.common;

import com.acc.model.Department;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lenovo
 */
public class DepartmentOptionTableModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(DepartmentOptionTableModel.class);
    private List<Department> listDep = new ArrayList();
    private final String[] columnNames = {"Code", "Name", "Select"};

    @Override
    public int getRowCount() {
        return listDep.size();
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
    public boolean isCellEditable(int row, int column) {
        return column == 2;
    }

    @Override
    public Class getColumnClass(int column) {
        return switch (column) {
            case 0,1 ->
                String.class;
            case 2 ->
                Boolean.class;
            default ->
                Object.class;
        }; //case 2:
        //return Boolean.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            Department dep = listDep.get(row);
            return switch (column) {
                case 0 ->
                    dep.getUserCode();
                case 1 ->
                    dep.getDeptName();
                case 2 ->
                    dep.isActive();
                default ->
                    null;
            }; //Code
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        try {
            if (!listDep.isEmpty()) {
                Department dep = listDep.get(row);
                if (value != null) {
                    switch (column) {
                        case 2 ->
                            dep.setActive((Boolean) value);
                    }
                }
                fireTableRowsUpdated(row, row);
            }
        } catch (Exception e) {
            log.error("setValueAt : " + e.getMessage());
        }
    }

    public List<Department> getListDep() {
        return listDep;
    }

    public void setListDep(List<Department> listDep) {
        this.listDep = new ArrayList<>(listDep);
        this.listDep.forEach((d) -> d.setActive(false));
        fireTableDataChanged();
    }

    public void clear() {
        listDep.clear();
        fireTableDataChanged();
    }

}
