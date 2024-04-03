/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup.dialog.common;

import com.inventory.entity.WareHouse;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Lenovo
 */
public class WareHouseTableModel extends AbstractTableModel {

    private final String[] columnNames = {"Code", "Name"};
    private List<WareHouse> listVou = new ArrayList<>();

    public WareHouseTableModel() {
    }

    public WareHouseTableModel(List<WareHouse> listVou) {
        this.listVou = listVou;
    }

    public List<WareHouse> getListVou() {
        return listVou;
    }

    public void setListVou(List<WareHouse> listVou) {
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
        WareHouse category = listVou.get(rowIndex);
        return switch (columnIndex) {
            case 0 ->
                category.getUserCode();
            case 1 ->
                category.getDescription();
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

    public WareHouse getObject(int row) {
        return listVou.get(row);
    }

    public void setObject(WareHouse obj, int row) {
        if (!listVou.isEmpty()) {
            listVou.set(row, obj);
            fireTableRowsUpdated(row, row);
        }
    }

    public void addOBject(WareHouse obj) {
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
