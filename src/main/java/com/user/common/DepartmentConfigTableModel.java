/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.user.common;

import com.acc.model.DepartmentA;
import com.user.model.DepartmentUser;
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
public class DepartmentConfigTableModel extends AbstractTableModel {

    private List<DepartmentUser> listDepartment = new ArrayList();
    private final String[] columnNames = {"Id", "Short Name", "Department Name", "Inventory Q", "Account Q"};
    private UserRepo userRepo;
    private JTable table;

    public JTable getTable() {
        return table;
    }

    public void setTable(JTable table) {
        this.table = table;
    }

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
        return true;
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
                    return p.getDeptId();
                }
                case 1 -> {
                    return p.getUserCode();
                }
                case 2 -> {
                    return p.getDeptName();
                }
                case 3 -> {
                    return p.getInventoryQ();
                }
                case 4 -> {
                    return p.getAccountQ();
                }
            }
        } catch (Exception e) {
            log.error(String.format("getValueAt : %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        DepartmentUser p = listDepartment.get(row);
        if (value != null) {
            switch (column) {
                case 0 -> {
                    p.setDeptId(Integer.valueOf(value.toString()));
                }
                case 1 -> {
                    p.setUserCode(value.toString());
                }
                case 2 -> {
                    p.setDeptName(value.toString());
                }
                case 3 -> {
                    p.setInventoryQ(value.toString());
                }
                case 4 -> {
                    p.setAccountQ(value.toString());
                }
            }
            save(p);
            fireTableRowsUpdated(row, row);
            table.requestFocus();
        }
    }

    private void save(DepartmentUser d) {
        if (isValidEntry(d)) {
            userRepo.saveDepartment(d);
            addNewRow();
        }
    }

    private boolean isValidEntry(DepartmentUser d) {
        if (d.getDeptId() == null) {
            return false;
        } else if (d.getUserCode() == null) {
            return false;
        } else if (d.getDeptName() == null) {
            return false;
        }
        return true;
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
