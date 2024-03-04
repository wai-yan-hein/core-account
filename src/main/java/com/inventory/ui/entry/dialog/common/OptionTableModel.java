/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry.dialog.common;

import com.inventory.entity.OptionModel;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class OptionTableModel extends AbstractTableModel {

    private List<OptionModel> listOption = new ArrayList();
    private final String[] columnNames = {"Code", "Name", "Select"};

    @Override
    public int getRowCount() {
        return listOption.size();
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
    public boolean isCellEditable(int row, int column) {
        return column == 2;
    }

    @Override
    public Class getColumnClass(int column) {
        return switch (column) {
            case 0,1 ->
                String.class;
            case 2 ->
                Boolean.class;
            default ->
                Object.class;
        }; //case 2:
        //return Boolean.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            OptionModel trader = listOption.get(row);
            return switch (column) {
                case 0 ->
                    trader.getCode();
                case 1 ->
                    trader.getName();
                case 2 ->
                    trader.isSelected();
                default ->
                    null;
            }; //Code
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        try {
            if (!listOption.isEmpty()) {
                OptionModel trader = listOption.get(row);
                if (value != null) {
                    switch (column) {
                        case 2 ->
                            trader.setSelected((Boolean) value);
                    }
                }
                fireTableRowsUpdated(row, row);
            }
        } catch (Exception e) {
            log.error("setValueAt : " + e.getMessage());
        }
    }

    public List<OptionModel> getListOption() {
        return listOption;
    }

    public void setListTrader(List<OptionModel> listOption) {
        this.listOption = listOption;
        this.listOption.forEach((d) -> d.setSelected(false));
        fireTableDataChanged();
    }

    public void clear() {
        listOption.clear();
        fireTableDataChanged();
    }

}
