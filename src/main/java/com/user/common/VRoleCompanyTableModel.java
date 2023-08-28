/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.user.common;

import com.common.Util1;
import com.user.model.VRoleCompany;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class VRoleCompanyTableModel extends AbstractTableModel {

    private List<VRoleCompany> listCompany = new ArrayList();
    private final String[] columnNames = {"No.", "Company Name", "Financial Year"};

    private Integer selectedIndex;

    public Integer getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(Integer selectedIndex) {
        this.selectedIndex = selectedIndex;
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
            VRoleCompany p = listCompany.get(row);
            switch (column) {
                case 0 -> {
                    return String.valueOf(row + 1 + ". ");
                }
                case 1 -> {
                    return p.getCompName();
                }
                case 2 -> {
                    return Util1.toDateStr(p.getStartDate(), "dd/MM/yyyy")
                            + " to "
                            + Util1.toDateStr(p.getEndDate(), "dd/MM/yyyy");
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

    public void addCompany(VRoleCompany info) {
        listCompany.add(info);
        fireTableRowsInserted(listCompany.size() - 1, listCompany.size() - 1);
    }

    public void setCompany(int row, VRoleCompany user) {
        if (!listCompany.isEmpty()) {
            listCompany.set(row, user);
            fireTableRowsUpdated(row, row);
        }
    }

    public List<VRoleCompany> getListCompany() {
        return listCompany;
    }

    public void setListCompany(List<VRoleCompany> listCompany) {
        this.listCompany = listCompany;
        fireTableDataChanged();
    }

    public VRoleCompany getCompany(int row) {
        return listCompany.get(row);
    }

    public void addNewRow() {
        listCompany.add(new VRoleCompany());
        fireTableRowsInserted(listCompany.size() - 1, listCompany.size() - 1);
    }

    public void delete(int row) {
        listCompany.remove(row);
        fireTableRowsDeleted(row, row);
    }

}
