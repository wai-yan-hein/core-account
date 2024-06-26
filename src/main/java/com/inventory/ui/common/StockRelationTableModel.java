/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.inventory.entity.Stock;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class StockRelationTableModel extends AbstractTableModel {

    private List<Stock> listStock = new ArrayList();
    private String[] columnNames = {"Code", "Name", "Relation", "Group", "Category"};

    public StockRelationTableModel() {
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Class getColumnClass(int column) {
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            Stock med = listStock.get(row);

            return switch (column) {
                case 0 ->
                    med.getUserCode();
                case 1 ->
                    med.getStockName();
                case 2 ->
                    med.getRelName();
                case 3 ->
                    med.getGroupName();
                case 4 ->
                    med.getCatName();
                default ->
                    null;
            }; //Code
            //Name
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {

    }

    @Override
    public int getRowCount() {
        if (listStock == null) {
            return 0;
        }
        return listStock.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<Stock> getListStock() {
        return listStock;
    }

    public void setListStock(List<Stock> listStock) {
        this.listStock = listStock;
        fireTableDataChanged();
    }

    public Stock getStock(int row) {
        if (listStock != null) {
            if (!listStock.isEmpty()) {
                return listStock.get(row);
            }
        }
        return null;
    }

    public void addStock(Stock stock) {
        if (listStock != null) {
            listStock.add(stock);
            int lastIndex = listStock.size() - 1;
            if (lastIndex >= 0) {
                fireTableRowsInserted(lastIndex, lastIndex);
            } else {
                fireTableRowsInserted(0, 0);
            }
        }
    }

    public void setStock(int row, Stock stock) {
        if (listStock != null) {
            listStock.set(row, stock);
            fireTableRowsUpdated(row, row);
        }
    }

    public void deleteStock(int row) {
        if (listStock != null) {
            if (!listStock.isEmpty()) {
                listStock.remove(row);
                fireTableDataChanged();
            }
        }
    }

    public void clear() {
        listStock.clear();
        fireTableDataChanged();
    }

}
