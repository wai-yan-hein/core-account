/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup.dialog.common;

import com.inventory.model.StockType;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Component
public class StockTypeTableModel extends AbstractTableModel {

    private final String[] columnNames = {"Code", "Type Name"};
    private List<StockType> listType = new ArrayList<>();

    public StockTypeTableModel(List<StockType> listType) {
        this.listType = listType;
    }

    @Override
    public int getRowCount() {
        return listType == null ? 0 : listType.size();
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
        StockType itemType = listType.get(rowIndex);
        return switch (columnIndex) {
            case 0 ->
                itemType.getUserCode();
            case 1 ->
                itemType.getStockTypeName();
            default ->
                null;
        };
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public List<StockType> getListType() {
        return listType;
    }

    public void setListType(List<StockType> listType) {
        this.listType = listType;
        fireTableDataChanged();
    }

    public StockType getItemType(int row) {
        return listType.get(row);
    }

    public void setItemType(StockType itemType, int row) {
        if (!listType.isEmpty()) {
            listType.set(row, itemType);
            fireTableRowsUpdated(row, row);
        }
    }

    public void addItemType(StockType item) {
        if (!listType.isEmpty()) {
            listType.add(item);
            fireTableRowsInserted(listType.size() - 1, listType.size() - 1);
        }
    }

    public void refresh() {
        fireTableDataChanged();
    }

}
