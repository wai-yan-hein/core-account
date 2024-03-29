/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.common;

import com.acc.model.DepartmentA;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class DepartmentTableModel extends AbstractTableModel {

    private List<DepartmentA> listDep = new ArrayList<>();
    private final String[] columnNames = {"Code", "Name"};
    private JTable table;

    public void setTable(JTable table) {
        this.table = table;
    }
    public DepartmentTableModel() {
    }

    public List<DepartmentA> getListDep() {
        return listDep;
    }

    public void setListDep(List<DepartmentA> listDep) {
        this.listDep = listDep;
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
            DepartmentA dep = listDep.get(row);
            return switch (column) {
                case 0 ->
                    dep.getUserCode();
                case 1 ->
                    dep.getDeptName();
                default ->
                    null;
            }; //Code
            //Description
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
        if (listDep == null) {
            return 0;
        } else {
            return listDep.size();
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public DepartmentA getDepatment(int row) {
        return listDep.get(row);

    }

    public void addNewRow() {
        if (hasEmptyRow()) {
            DepartmentA dep = new DepartmentA();
            listDep.add(dep);
            fireTableRowsInserted(listDep.size() - 1, listDep.size() - 1);
        }
    }

    public boolean hasEmptyRow() {
        boolean status = true;
        if (listDep.isEmpty() || listDep == null) {
            status = true;
        } else {
            DepartmentA dep = listDep.get(listDep.size() - 1);
            if (dep.getKey().getDeptCode() == null) {
                status = false;
            }
        }

        return status;
    }

    public void addDepartment(DepartmentA dep) {
        if (!listDep.isEmpty()) {
            if (dep != null) {
                listDep.add(dep);
                fireTableRowsInserted(listDep.size() - 1, listDep.size() - 1);
            }
        }
    }

    public void delete(int row) {
        if (!listDep.isEmpty()) {
            DepartmentA dep = listDep.get(row);
            if (dep.getKey().getDeptCode() != null) {
                listDep.remove(row);
                if (table.getCellEditor() != null) {
                    table.getCellEditor().stopCellEditing();
                }
                fireTableRowsDeleted(row - 1, row - 1);
            }
        }
    }
}
