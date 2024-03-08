/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.user.common;

import com.user.model.SyncModel;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author wai yan
 */
@Slf4j
public class HMSVoucherTableModel extends AbstractTableModel {

    private List<SyncModel> listSync = new ArrayList();
    private String[] columnNames = {"Name", "Sync"};

    public HMSVoucherTableModel() {
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
        return switch (column) {
            case 1 ->
                Boolean.class;
            default ->
                String.class;
        };

    }

    @Override
    public Object getValueAt(int row, int column) {

        try {
            SyncModel obj = listSync.get(row);

            return switch (column) {
                case 0 ->
                    obj.getTranSource();
                case 1 ->
                    obj.isSync();
                default ->
                    null;
            }; //Id
            //Name
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        try {

            SyncModel m = listSync.get(row);
            switch (column) {
                case 1 -> {
                    if (value instanceof Boolean b) {
                        m.setSync(b);
                    }
                }
            }

        } catch (Exception e) {
            log.error("setValueAt : " + e.getMessage());
        }
    }

    @Override
    public int getRowCount() {
        if (listSync == null) {
            return 0;
        }
        return listSync.size();
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

    public List<SyncModel> getListSync() {
        return listSync;
    }

    public void setListSync(List<SyncModel> listSync) {
        this.listSync = listSync;
        fireTableDataChanged();
    }

    public void addObject(SyncModel obj) {
        listSync.add(obj);
        fireTableRowsInserted(listSync.size() - 1, listSync.size() - 1);
    }

    public void setObject(int row, SyncModel obj) {
        if (!listSync.isEmpty()) {
            listSync.set(row, obj);
            fireTableRowsUpdated(row, row);
        }
    }

    public List<SyncModel> getFilterList() {
        return listSync.stream().filter((t) -> t.isSync()).toList();
    }

    public void clear() {
        listSync.clear();
        fireTableDataChanged();
    }

}
