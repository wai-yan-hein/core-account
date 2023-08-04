/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.common;

import com.acc.model.TraderA;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Lenovo
 */
public class TraderAImportTableModel extends AbstractTableModel {

    private final String[] columnNames = {"User Code", "TraderA Name", "Address", "Account"};
    private List<TraderA> listTrader = new ArrayList<>();

    public TraderAImportTableModel() {
    }

    public TraderAImportTableModel(List<TraderA> listTrader) {
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
        TraderA t = listTrader.get(rowIndex);
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

    public List<TraderA> getListTrader() {
        return listTrader;
    }

    public void setListTrader(List<TraderA> listTrader) {
        this.listTrader = listTrader;
        fireTableDataChanged();
    }

    public void clear() {
        listTrader.clear();
        fireTableDataChanged();
    }

}
