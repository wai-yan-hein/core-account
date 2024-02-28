/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.ui.management.model.ClosingBalance;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class StockBalanceWeightTableModel extends AbstractTableModel {

    @Getter
    private double total;
    private List<ClosingBalance> listStockBalance = new ArrayList();
    private final String[] columnNames = {"Locaiton", "Total Weight", "Total Qty"};

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
        return column == 0 ? String.class : Double.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listStockBalance != null) {
            try {
                ClosingBalance stock = listStockBalance.get(row);
                switch (column) {
                    case 0 -> {
                        //Location
                        return stock.getLocName();
                    }
                    case 1 -> {
                        //Unit
                        return stock.getWeight();
                    }
                    case 2 -> {
                        //Unit
                        return stock.getTotalQty();
                    }
                }
            } catch (Exception ex) {
                log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
            }
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {

    }

    public List<ClosingBalance> getListStockBalance() {
        return listStockBalance;
    }

    public void setListStockBalance(List<ClosingBalance> listStockBalance) {
        this.listStockBalance = listStockBalance;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return listStockBalance.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public void clearList() {
        total = 0;
        if (listStockBalance != null) {
            listStockBalance.clear();
            fireTableDataChanged();
        }
    }

    public void addObject(ClosingBalance sd) {
        if (listStockBalance != null) {
            total += sd.getBalQty();
            listStockBalance.add(sd);
            fireTableRowsInserted(listStockBalance.size() - 1, listStockBalance.size() - 1);
        }
    }

}
