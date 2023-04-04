/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.user.common;

import com.user.model.PrivilegeCompany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class RoleCompanyTableModel extends AbstractTableModel {

    private List<PrivilegeCompany> listProperty = new ArrayList();
    private final String[] columnNames = {"Company Name", "Allow"};
    private String roleCode;
    private UserRepo userRepo;

    public UserRepo getUserRepo() {
        return userRepo;
    }

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 1;
    }

    @Override
    public Class getColumnClass(int column) {
        return column == 1 ? Boolean.class : String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            PrivilegeCompany p = listProperty.get(row);
            switch (column) {
                case 0 -> {
                    return p.getCompName();
                }
                case 1 -> {
                    return p.isAllow();
                }
            }
        } catch (Exception e) {
            log.error(String.format("getValueAt : %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        try {
            PrivilegeCompany p = listProperty.get(row);
            if (!Objects.isNull(value)) {
                switch (column) {
                    case 1 -> {
                        p.setAllow((Boolean) value);
                    }
                }
                save(p);
                fireTableRowsUpdated(row, row);
            }

        } catch (Exception e) {
            log.error(String.format("setValueAt : %s", e.getMessage()));
        }
    }

    private void save(PrivilegeCompany property) {
        userRepo.saveCompRole(property);
    }

    @Override
    public int getRowCount() {
        return listProperty.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<PrivilegeCompany> getListProperty() {
        return listProperty;
    }

    public PrivilegeCompany getProperty(int row) {
        return listProperty.get(row);
    }

    public void setListProperty(List<PrivilegeCompany> listProperty) {
        this.listProperty = listProperty;
        fireTableDataChanged();
    }

    public void addNewRow() {
        listProperty.add(new PrivilegeCompany());
        fireTableRowsInserted(listProperty.size() - 1, listProperty.size() - 1);
    }

    public void delete(int row) {
        listProperty.remove(row);
        fireTableRowsDeleted(row, row);
    }

    public void clear() {
        listProperty.clear();
        fireTableDataChanged();
    }

}
