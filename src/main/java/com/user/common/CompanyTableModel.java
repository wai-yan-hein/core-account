/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.user.common;

import com.user.model.CompanyInfo;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class CompanyTableModel extends AbstractTableModel {

    private List<CompanyInfo> listCompany = new ArrayList();
    private final String[] columnNames = {"Company Code", "Company Name"};
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
        return false;
    }

    @Override
    public Class getColumnClass(int column) {
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            CompanyInfo p = listCompany.get(row);
            switch (column) {
                case 0 -> {
                    return p.getCompCode();
                }
                case 1 -> {
                    return p.getCompName();
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
        return listCompany.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public void addCompany(CompanyInfo info) {
        listCompany.add(info);
        fireTableRowsInserted(listCompany.size() - 1, listCompany.size() - 1);
    }

    public void setCompany(int row, CompanyInfo user) {
        if (!listCompany.isEmpty()) {
            listCompany.set(row, user);
            fireTableRowsUpdated(row, row);
        }
    }

    public List<CompanyInfo> getListCompany() {
        return listCompany;
    }

    public void setListCompany(List<CompanyInfo> listCompany) {
        this.listCompany = listCompany;
        fireTableDataChanged();
    }

    public CompanyInfo getCompany(int row) {
        return listCompany.get(row);
    }

    public void addNewRow() {
        listCompany.add(new CompanyInfo());
        fireTableRowsInserted(listCompany.size() - 1, listCompany.size() - 1);
    }

    public void delete(int row) {
        listCompany.remove(row);
        fireTableRowsDeleted(row, row);
    }

}
