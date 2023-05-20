/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup.dialog.common;

import com.inventory.model.AccKey;
import com.inventory.model.AccSetting;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class AccountSettingTableModel extends AbstractTableModel {

    private final String[] columnNames = {"Name"};
    private List<AccSetting> listSetting = new ArrayList<>();

    @Override
    public int getRowCount() {
        return listSetting.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        AccSetting category = listSetting.get(rowIndex);
        return switch (columnIndex) {
            case 0 ->
                category.getKey().getType();
            default ->
                null;
        };
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        if (value != null) {
            AccSetting record = listSetting.get(row);
            switch (column) {
                case 0 -> {
                    AccKey key = new AccKey();
                    key.setType(value.toString());
                    record.setKey(key);
                }
//                case 1 -> {
//                    record.setSourceAcc(value.toString());
//                }
//                case 2 -> {
//                    record.setPayAcc(value.toString());
//                }
//                case 3 -> {
//                    record.setDiscountAcc(value.toString());
//                }
//                case 4 -> {
//                    record.setTaxAcc(value.toString());
//                }
//                case 5 -> {
//                    record.setBalanceAcc(value.toString());
//                }
//                case 6 -> {
//                    record.setDeptCode(value.toString());
//                }
            }
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public List<AccSetting> getListSetting() {
        return listSetting;
    }

    public void setListSetting(List<AccSetting> listSetting) {
        this.listSetting = listSetting;
        fireTableDataChanged();
    }

    public AccSetting getSetting(int row) {
        return listSetting.get(row);
    }

    public void addSetting(AccSetting item) {
        if (!listSetting.isEmpty()) {
            listSetting.add(item);
            fireTableRowsInserted(listSetting.size() - 1, listSetting.size() - 1);
        }
    }

    public void refresh() {
        fireTableDataChanged();
    }

    public void setSetting(AccSetting setting, int row) {
        if (!listSetting.isEmpty()) {
            listSetting.set(row, setting);
            fireTableRowsUpdated(row, row);
        }
    }
}
