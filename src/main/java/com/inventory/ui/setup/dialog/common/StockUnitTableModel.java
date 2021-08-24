/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup.dialog.common;

import com.inventory.model.StockUnit;
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
public class StockUnitTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockUnitTableModel.class);
    private String[] columnNames = {"Unit-S", "Unit-Name"};
    private List<StockUnit> listUnit = new ArrayList<>();

    public StockUnitTableModel(List<StockUnit> listUnit) {
        this.listUnit = listUnit;
    }

    @Override
    public int getRowCount() {
        return listUnit == null ? 0 : listUnit.size();
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        StockUnit itemUnit = listUnit.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return itemUnit.getItemUnitCode();
            case 1:
                return itemUnit.getItemUnitName();
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

    public List<StockUnit> getListUnit() {
        return listUnit;
    }

    public void setListUnit(List<StockUnit> listUnit) {
        this.listUnit = listUnit;
        fireTableDataChanged();
    }

    public StockUnit getStockUnit(int row) {
        return listUnit.get(row);
    }

    public void setStockUnit(StockUnit itemUnit, int row) {
        if (!listUnit.isEmpty()) {
            listUnit.set(row, itemUnit);
            fireTableRowsUpdated(row, row);
        }
    }

    public void addStockUnit(StockUnit item) {
        if (!listUnit.isEmpty()) {
            listUnit.add(item);
            fireTableRowsInserted(listUnit.size() - 1, listUnit.size() - 1);
        }
    }

    public void refresh() {
        fireTableDataChanged();
    }
}
