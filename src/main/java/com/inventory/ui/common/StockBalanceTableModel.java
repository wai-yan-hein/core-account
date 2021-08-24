/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.inventory.model.StockBalanceTmp;
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
public class StockBalanceTableModel extends AbstractTableModel {

    static Logger log = LoggerFactory.getLogger(StockBalanceTableModel.class.getName());
    private List<StockBalanceTmp> listStockBalance = new ArrayList();
    private final String[] columnNames = {"Locaiton", " Balance"};

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
        if (listStockBalance != null) {
            try {
                StockBalanceTmp stock = listStockBalance.get(row);

                switch (column) {
                    case 0: //Code
                        if (stock.getLocation() == null) {
                            return "No Stock";
                        } else {
                            return stock.getLocation();
                        }
                    case 1: //Desp
                        if (stock.getTotalWt() == null && stock.getUnit() == null) {
                            return "No Stock";
                        } else {
                            return stock.getTotalWt() + "-" + stock.getUnit();
                        }
                    default:
                        return new Object();
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

    public List<StockBalanceTmp> getListStockBalance() {
        return listStockBalance;
    }

    public void setListStockBalance(List<StockBalanceTmp> listStockBalance) {
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
        if (listStockBalance != null) {
            this.listStockBalance.clear();
            fireTableDataChanged();
        }
    }

}
 