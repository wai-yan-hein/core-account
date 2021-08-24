/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup.common;

import com.inventory.model.Stock;
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
public class StockTableModel extends AbstractTableModel {

    static Logger log = LoggerFactory.getLogger(StockTableModel.class.getName());
    private List<Stock> listStock = new ArrayList();
    private String[] columnNames = {"Code", "Description", "Active", "Barcode"};

    public StockTableModel(List<Stock> listStock) {
        this.listStock = listStock;
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
        if (column == 2) {
            return Boolean.class;
        } else {
            return String.class;
        }
    }
    @Override
    public Object getValueAt(int row, int column) {
        if (listStock == null) {
            return null;
        }

        if (listStock.isEmpty()) {
            return null;
        }

        try {
            Stock med = listStock.get(row);

            switch (column) {
                case 0: //Code
                    return med.getUserCode();
                case 1: //Name
                    return med.getStockName();
                case 2: //Active
                    return med.getIsActive();
                case 3:
                    return med.getBarcode();
                default:
                    return null;
            }
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

    public void modifyColumn() {
        String[] newColumn = {"Code", "Description"};
        this.columnNames = newColumn;
        fireTableStructureChanged();

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
            fireTableRowsInserted(listStock.size() - 1, listStock.size() - 1);

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

    public void refresh() {
        fireTableDataChanged();
    }
}
