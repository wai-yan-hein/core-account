/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup.dialog.common;

import com.inventory.entity.Trader;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Lenovo
 */
public class TraderImportTableModel extends AbstractTableModel {

    private final String[] columnNames = {"User Code", "Trader Name", "Address", "Account"};
    private List<Trader> listTrader = new ArrayList<>();

    public TraderImportTableModel() {
    }

    public TraderImportTableModel(List<Trader> listTrader) {
        this.listTrader = listTrader;
    }

    @Override
    public int getRowCount() {
        return listTrader == null ? 0 : listTrader.size();
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
        Trader t = listTrader.get(rowIndex);
        return switch (columnIndex) {
            case 0 ->
                t.getUserCode();
            case 1 ->
                t.getTraderName();
            case 2 ->
                t.getAddress();
            case 3 ->
                t.getAccount();
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

    public List<Trader> getListTrader() {
        return listTrader;
    }

    public void setListTrader(List<Trader> listTrader) {
        this.listTrader = listTrader;
        fireTableDataChanged();
    }

    public void clear() {
        listTrader.clear();
        fireTableDataChanged();
    }

    public void addObject(Trader t) {
        listTrader.add(t);
        int lastIndex = listTrader.size() - 1;
        if (lastIndex >= 0) {
            fireTableRowsInserted(lastIndex, lastIndex);
        } else {
            fireTableRowsInserted(0, 0);
        }
    }

    public void delete(int row) {
        listTrader.remove(row);
        fireTableRowsDeleted(row, row);
    }

}
