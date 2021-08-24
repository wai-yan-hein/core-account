/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.inventory.model.Stock;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Component
public class CodeTableModel extends AbstractTableModel {

    static Logger log = LoggerFactory.getLogger(CodeTableModel.class.getName());
    private List<Stock> listStock = new ArrayList();
    private final String[] columnNames = {"Code", "Item Name"};
    private JTable parent;

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 0;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //code
                return String.class;
            case 1: //description
                return String.class;
            default:
                return Object.class;
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
            Stock stock = listStock.get(row);

            switch (column) {
                case 0: //Code
                    return stock.getStockCode();
                case 1: //Desp
                    return stock.getStockName();
                default:
                    return new Object();
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {

        if (listStock == null) {
            return;
        }

        if (listStock.isEmpty()) {
            return;
        }
        try {
            Stock record = listStock.get(row);

            switch (column) {
                case 0: //Code
                    if (value != null) {
                        Stock stock = (Stock) value;
                        record.setStockCode(stock.getStockCode());
                        record.setStockName(stock.getStockName());
                        addEmptyRow();
                    }
                    break;
            }
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }

    @Override
    public int getRowCount() {
        return listStock.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public boolean hasEmptyRow() {
        boolean status = true;
        if (listStock.isEmpty() || listStock == null) {
            status = true;
        } else {
            Stock stock = listStock.get(listStock.size() - 1);
            if (stock.getStockCode() == null) {
                status = false;
            }
        }

        return status;
    }

    public void addEmptyRow() {
        if (listStock != null) {
            Stock record = new Stock();
            listStock.add(record);
            fireTableRowsInserted(listStock.size() - 1, listStock.size() - 1);
        }
    }

    public List<Stock> getListStock() {
        return listStock;
    }

    public void setListStock(List<Stock> listStock) {
        this.listStock = listStock;
        fireTableDataChanged();
    }

    public String getFilterCodeStr() {
        String strTmp = null;

        if (listStock != null) {
            for (Stock stock : listStock) {
                if (stock.getStockCode() != null) {

                    if (strTmp == null) {
                        strTmp = "'" + stock.getStockCode() + "'";
                    } else {
                        strTmp = strTmp + ",'" + stock.getStockCode() + "'";
                    }

                }
            }
        }

        return strTmp;
    }

    public void addNewRow() {
        if (hasEmptyRow()) {
            Stock stock = new Stock();
            listStock.add(stock);

            fireTableRowsInserted(listStock.size() - 1, listStock.size() - 1);
        }

    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    public void clearList() {
        this.listStock.clear();
    }

    public void delete(int row) {
        if (listStock == null) {
            return;
        }
        if (listStock.isEmpty()) {
            return;

        }
        listStock.remove(row);

        if (hasEmptyRow()) {
            addEmptyRow();
        }

        fireTableRowsDeleted(row, row);

    }
}
