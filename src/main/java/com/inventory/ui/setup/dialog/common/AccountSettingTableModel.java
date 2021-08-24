/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup.dialog.common;

import com.inventory.model.AccSetting;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Component
public class AccountSettingTableModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(AccountSettingTableModel.class);
    private final String[] columnNames = {"Name"};
    private List<AccSetting> listSetting = new ArrayList<>();

    public AccountSettingTableModel() {
    }

    public AccountSettingTableModel(List<AccSetting> listSetting) {
        this.listSetting = listSetting;
    }

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
        switch (columnIndex) {
            case 0:
                return category.getType();
            default:
                return null;
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

    public void addCategory(AccSetting item) {
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
