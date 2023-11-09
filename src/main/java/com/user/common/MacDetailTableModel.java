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
public class MacDetailTableModel extends AbstractTableModel {

    private List<MachineInfo> listDetail = new ArrayList();
    private final String[] columnNames = {"Mac Id", "Machine Name", "Mac Ip", "Mac Address",
        "Serial No", "Os Name", "Os Version", "Os arch", "Updated"};

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 8;
    }

    @Override
    public Class getColumnClass(int column) {
        return switch (column) {
            case 8 ->
                Boolean.class;
            default ->
                String.class;
        };
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            MachineInfo p = listDetail.get(row);
            switch (column) {
                case 0 -> {
                    return p.getMacId();
                }
                case 1 -> {
                    return p.getMachineName();
                }
                case 2 -> {
                    return p.getMachineIp();
                }
                case 3 -> {
                    return p.getMacAddress();
                }
                case 4 -> {
                    return p.getSerialNo();
                }
                case 5 -> {
                    return p.getOsName();
                }
                case 6 -> {
                    return p.getOsVersion();
                }
                case 7 -> {
                    return p.getOsArch();
                }
                case 8 -> {
                    return p.isProUpdate();
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
        return listDetail.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public void addObject(MachineInfo info) {
        listDetail.add(info);
        fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
    }

    public void setObject(int row, MachineInfo user) {
        if (!listDetail.isEmpty()) {
            listDetail.set(row, user);
            fireTableRowsUpdated(row, row);
        }
    }


    public MachineInfo getObject(int row) {
        return listDetail.get(row);
    }

    public void addNewRow() {
        listDetail.add(new MachineInfo());
        fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
    }

    public void delete(int row) {
        listDetail.remove(row);
        fireTableRowsDeleted(row, row);
    }

    public List<MachineInfo> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<MachineInfo> listDetail) {
        this.listDetail = listDetail;
        fireTableDataChanged();
    }
    
    

}
