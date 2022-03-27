/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.user.common.UserRepo;
import com.inventory.model.AppRole;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author winswe
 */
@Slf4j
public class UserRoleTableModel extends AbstractTableModel {

    private List<AppRole> listRole = new ArrayList();
    private String[] columnNames = {"Role Name"};
    private JTable table;
    private UserRepo userRepo;

    public UserRepo getUserRepo() {
        return userRepo;
    }

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public JTable getTable() {
        return table;
    }

    public void setTable(JTable table) {
        this.table = table;
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
            AppRole user = listRole.get(row);

            return switch (column) {
                case 0 ->
                    user.getRoleName();
                default ->
                    null;
            }; //Id
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        try {
            AppRole user = listRole.get(row);
            if (value != null) {
                switch (column) {
                    case 0 ->
                        user.setRoleName(value.toString());
                }
            }
            userRepo.saveAppRole(user);
        } catch (Exception e) {
            log.error("setValueAt :" + e.getMessage());
        }

    }

    public void addEmptyRow() {
        if (hasEmptyRow()) {
            AppRole user = new AppRole();
            addRole(user);
        }
    }

    private boolean hasEmptyRow() {
        boolean status = true;
        if (listRole.isEmpty() || listRole == null) {
            status = true;
        } else {
            AppRole user = listRole.get(listRole.size() - 1);
            if (user.getRoleCode() == null) {
                status = false;
            }
        }

        return status;

    }

    @Override
    public int getRowCount() {
        if (listRole == null) {
            return 0;
        }
        return listRole.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

    public List<AppRole> getListRole() {
        return listRole;
    }

    public void setListRole(List<AppRole> listRole) {
        this.listRole = listRole;
        fireTableDataChanged();
    }

    public AppRole getRole(int row) {
        return listRole.get(row);
    }

    public void deleteRole(int row) {
        if (!listRole.isEmpty()) {
            String roleCode = getRole(row).getRoleCode();
            if (roleCode != null) {
                listRole.remove(row);
                fireTableRowsDeleted(row, row);
                if (table.getCellEditor() != null) {
                    table.getCellEditor().stopCellEditing();
                }
            }
        }
    }

    public void addRole(AppRole user) {
        listRole.add(user);
        fireTableRowsInserted(listRole.size() - 1, listRole.size() - 1);
    }

    public void setRole(int row, AppRole user) {
        if (!listRole.isEmpty()) {
            listRole.set(row, user);
            fireTableRowsUpdated(row, row);
        }
    }

}
