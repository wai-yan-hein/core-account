/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.common;

import com.user.model.Currency;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class CurrencyATableModel extends AbstractTableModel {

    private List<Currency> listCurrency = new ArrayList<>();
    private final String[] columnNames = {"Code", "Name"};

    public CurrencyATableModel() {
    }

    public List<Currency> getListCurrency() {
        return listCurrency;
    }

    public void setListCurrency(List<Currency> listCurrency) {
        this.listCurrency = listCurrency;
        fireTableDataChanged();

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
            Currency cur = listCurrency.get(row);
            return switch (column) {
                case 0 ->
                    cur.getCurCode();
                case 1 ->
                    cur.getCurrencyName();
                default ->
                    null;
            }; //Code
            //Description
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
        } else {
            return listCurrency.size();
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public Currency getCurrency(int row) {
        return listCurrency.get(row);
    }
}
