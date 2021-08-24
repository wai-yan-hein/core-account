/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.inventory.model.AppUser;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author winswe
 */
@Component
public class UserTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserTableModel.class);
    private List<AppUser> listUser = new ArrayList();
    private String[] columnNames = {"User Short", "Name"};

    public UserTableModel(List<AppUser> listUser) {
        this.listUser = listUser;

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
            AppUser user = listUser.get(row);

            switch (column) {
                case 0: //user short
                    return user.getUserShort();
                case 1: //Name
                    return user.getUserName();
                default:
                    return null;
            }
        } catch (Exception ex) {
            LOGGER.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {

    }

    @Override
    public int getRowCount() {
        if (listUser == null) {
            return 0;
        }
        return listUser.size();
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

    public List<AppUser> getListUser() {
        return listUser;
    }

    public void setListUser(List<AppUser> listUser) {
        this.listUser = listUser;
        fireTableDataChanged();
    }

    public AppUser getUser(int row) {
        return listUser.get(row);
    }

    public void deleteUser(int row) {
        if (!listUser.isEmpty()) {
            listUser.remove(row);
            fireTableRowsDeleted(0, listUser.size());
        }

    }

    public void addUser(AppUser user) {
        listUser.add(user);
        fireTableRowsInserted(listUser.size() - 1, listUser.size() - 1);
    }

    public void setUser(int row, AppUser user) {
        if (!listUser.isEmpty()) {
            listUser.set(row, user);
            fireTableRowsUpdated(row, row);
        }
    }

    public void modifyColumn() {
        String[] newColumn = {"User Name"};
        this.columnNames = newColumn;
        fireTableStructureChanged();
    }
}
