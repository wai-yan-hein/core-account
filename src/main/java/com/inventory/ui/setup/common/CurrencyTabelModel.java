/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup.common;

import com.user.model.Currency;
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
public class CurrencyTabelModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(CurrencyTabelModel.class);
    private List<Currency> listCurrency = new ArrayList();
    private String[] columnNames = {"Code", "Name", "Symbol", "Active"};

    public CurrencyTabelModel(List<Currency> listCurrency) {
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
        switch (column) {
            case 0:
                return String.class;
            case 1:
                return String.class;
            case 2:
                return String.class;
            case 3:
                return Boolean.class;
            default:
                return Object.class;
        }

    }

    @Override
    public Object getValueAt(int row, int column) {

        try {
            Currency currency = listCurrency.get(row);

            return switch (column) {
                case 0 -> currency.getCurCode();
                case 1 -> currency.getCurrencyName();
                case 2 -> currency.getCurrencySymbol();
                case 3 -> currency.isActive();
                default -> null;
            }; //Id
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

    public List<Currency> getListCurrency() {
        return listCurrency;
    }

    public void setListCurrency(List<Currency> listCurrency) {
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
