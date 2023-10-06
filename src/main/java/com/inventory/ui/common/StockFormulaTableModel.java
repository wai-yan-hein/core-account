/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.inventory.model.StockFormula;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class StockFormulaTableModel extends AbstractTableModel {

    private final String[] columnNames = {"Code", "Name"};
    private List<StockFormula> listDetail = new ArrayList<>();

    @Override
    public int getRowCount() {
        return listDetail == null ? 0 : listDetail.size();
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
        StockFormula s = listDetail.get(rowIndex);
        return switch (columnIndex) {
            case 0 ->
                s.getUserCode();
            case 1 ->
                s.getFormulaName();
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

    public List<StockFormula> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<StockFormula> listDetail) {
        this.listDetail = listDetail;
        fireTableDataChanged();
    }

    public StockFormula getObject(int row) {
        return listDetail.get(row);
    }

    public void setObject(StockFormula itemUnit, int row) {
        if (!listDetail.isEmpty()) {
            listDetail.set(row, itemUnit);
            fireTableRowsUpdated(row, row);
        }
    }

    public void addObject(StockFormula item) {
        if (!listDetail.isEmpty()) {
            listDetail.add(item);
            fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
        }
    }

    public void refresh() {
        fireTableDataChanged();
    }
}
