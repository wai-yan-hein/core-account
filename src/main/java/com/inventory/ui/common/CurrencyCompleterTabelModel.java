/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.user.model.Currency;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author wai yan
 */
@Component
public class CurrencyCompleterTabelModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(CurrencyCompleterTabelModel.class);
    private List<Currency> listCurrency = new ArrayList();
    private String[] columnNames = {"Name"};

    public CurrencyCompleterTabelModel(List<Currency> listCurrency) {
        this.listCurrency = listCurrency;
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
        return switch (column) {
            case 0 ->
                String.class;
            default ->
                Object.class;
        };

    }

    @Override
    public Object getValueAt(int row, int column) {

        try {
            Currency currency = listCurrency.get(row);

            return switch (column) {
                case 0 ->
                    currency.getCurrencyName();
                default ->
                    null;
            }; //Id
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
        if (listCurrency == null) {
            return 0;
        }
        return listCurrency.size();
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

    public List<Currency> getlistCurrency() {
        return listCurrency;
    }

    public void setlistCurrency(List<Currency> listCurrency) {
        this.listCurrency = listCurrency;
        fireTableDataChanged();
    }

    public Currency getCurrency(int row) {
        return listCurrency.get(row);
    }

    public void deleteCurrency(int row) {
        if (!listCurrency.isEmpty()) {
            listCurrency.remove(row);
            fireTableRowsDeleted(0, listCurrency.size());
        }

    }

    public void addCurrency(Currency currency) {
        listCurrency.add(currency);
        fireTableRowsInserted(listCurrency.size() - 1, listCurrency.size() - 1);
    }

    public void setCurrency(int row, Currency currency) {
        if (!listCurrency.isEmpty()) {
            listCurrency.set(row, currency);
            fireTableRowsUpdated(row, row);
        }
    }

}
