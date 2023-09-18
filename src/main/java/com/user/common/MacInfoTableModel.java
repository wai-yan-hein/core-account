/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.user.common;

import com.user.model.MachineInfo;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class MacInfoTableModel extends AbstractTableModel {

    private List<MachineInfo> listCompany = new ArrayList();
    private final String[] columnNames = {"Machine Name"};

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
            MachineInfo p = listCompany.get(row);
            switch (column) {
                case 0 -> {
                    return p.getMachineName();
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

    public void addCompany(MachineInfo info) {
        listCompany.add(info);
        fireTableRowsInserted(listCompany.size() - 1, listCompany.size() - 1);
    }

    public void setCompany(int row, MachineInfo user) {
        if (!listCompany.isEmpty()) {
            listCompany.set(row, user);
            fireTableRowsUpdated(row, row);
        }
    }

    public List<MachineInfo> getListCompany() {
        return listCompany;
    }

    public void setListCompany(List<MachineInfo> listCompany) {
        this.listCompany = listCompany;
        fireTableDataChanged();
    }

    public MachineInfo getCompany(int row) {
        return listCompany.get(row);
    }

    public void addNewRow() {
        listCompany.add(new MachineInfo());
        fireTableRowsInserted(listCompany.size() - 1, listCompany.size() - 1);
    }

    public void delete(int row) {
        listCompany.remove(row);
        fireTableRowsDeleted(row, row);
    }

}
