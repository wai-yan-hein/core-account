/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.user.common;

import com.user.model.DepartmentUser;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class DepartmentTableModel extends AbstractTableModel {

    private List<DepartmentUser> listDepartment = new ArrayList();
    private final String[] columnNames = {"Code", "Department Name"};
    private UserRepo userRepo;

    public UserRepo getUserRepo() {
        return userRepo;
    }

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
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
            DepartmentUser p = listDepartment.get(row);
            switch (column) {
                case 0 -> {
                    return p.getUserCode();
                }
                case 1 -> {
                    return p.getDeptName();
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
        return listDepartment.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public void addDepartment(DepartmentUser info) {
        listDepartment.add(info);
        fireTableRowsInserted(listDepartment.size() - 1, listDepartment.size() - 1);
    }

    public void setDepartment(int row, DepartmentUser user) {
        if (!listDepartment.isEmpty()) {
            listDepartment.set(row, user);
            fireTableRowsUpdated(row, row);
        }
    }

    public DepartmentUser getDepartment(int row) {
        return listDepartment.get(row);
    }

    public void addNewRow() {
        listDepartment.add(new DepartmentUser());
        fireTableRowsInserted(listDepartment.size() - 1, listDepartment.size() - 1);
    }

    public void delete(int row) {
        listDepartment.remove(row);
        fireTableRowsDeleted(row, row);
    }

    public List<DepartmentUser> getListDepartment() {
        return listDepartment;
    }

    public void setListDepartment(List<DepartmentUser> listDepartment) {
        this.listDepartment = listDepartment;
        fireTableDataChanged();
    }

}
