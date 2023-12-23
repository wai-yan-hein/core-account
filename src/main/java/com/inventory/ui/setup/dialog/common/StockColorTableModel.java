/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup.dialog.common;

import com.inventory.model.StockColor;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Lenovo
 */
public class StockColorTableModel extends AbstractTableModel {

    private final String[] columnNames = {"Code", "Color Name"};
    private List<StockColor> listVou = new ArrayList<>();

    public StockColorTableModel() {
    }

    public StockColorTableModel(List<StockColor> listVou) {
        this.listVou = listVou;
    }

    public List<StockColor> getListVou() {
        return listVou;
    }

    public void setListVou(List<StockColor> listVou) {
        this.listVou = listVou;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return listVou == null ? 0 : listVou.size();
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
        StockColor color = listVou.get(rowIndex);
        return switch (columnIndex) {
            case 0 ->
                color.getColorId();
            case 1 ->
                color.getColorName();
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

    public StockColor getObject(int row) {
        return listVou.get(row);
    }

    public void setObject(StockColor obj, int row) {
        if (!listVou.isEmpty()) {
            listVou.set(row, obj);
            fireTableRowsUpdated(row, row);
        }
    }

    public void addObject(StockColor obj) {
        if (!listVou.isEmpty()) {
            listVou.add(obj);
            fireTableRowsInserted(listVou.size() - 1, listVou.size() - 1);
        }
    }

    public void remove(int row) {
        if (listVou.isEmpty()) {
            listVou.remove(row);
            fireTableRowsDeleted(row, row);
        }
    }

    public void refresh() {
        fireTableDataChanged();
    }
}
