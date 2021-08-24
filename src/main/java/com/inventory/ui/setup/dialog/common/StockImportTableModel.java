/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup.dialog.common;

import com.inventory.model.Stock;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author winswe
 */
@Component
public class StockImportTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockImportTableModel.class);
    private List<Stock> listStock = new ArrayList();
    private String[] columnNames = {"Code", "Stock-Code", "Stock Name", "Weight", "Stock Type", "Pattern"};

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
        switch (column) {
            default:
                return String.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {

        try {
            Stock stock = listStock.get(row);

            switch (column) {
                case 0: //Id
                    return stock.getMigCode();
                case 1: //Name
                    return stock.getBarcode();
                case 2:
                    return stock.getStockName();
                case 3:
                    return stock.getSaleWeight();
                case 4:
                    return stock.getStockType().getStockTypeCode();
                case 5:
                    return null;
                default:
                    return null;
            }
        } catch (Exception ex) {
            LOGGER.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
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

    public String[] getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

    public List<Stock> getListStock() {
        return listStock;
    }

    public void setListStock(List<Stock> listStock) {
        this.listStock = listStock;
        fireTableDataChanged();
    }

    public void clear() {
        if (listStock != null) {
            listStock.clear();
            fireTableDataChanged();
        }
    }

    public void refresh() {
        fireTableDataChanged();
    }

}
