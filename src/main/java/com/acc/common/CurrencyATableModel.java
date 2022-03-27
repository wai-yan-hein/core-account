/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.common;

import com.user.model.Currency;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lenovo
 */
public class CurrencyATableModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(CurrencyATableModel.class);
    private List<Currency> listCurrency = new ArrayList<>();
    private final String[] columnNames = {"Code", "Name"};
    private JTable table;

    public JTable getTable() {
        return table;
    }

    public void setTable(JTable table) {
        this.table = table;
    }

    public CurrencyATableModel(List<Currency> listCurrency) {
        this.listCurrency = listCurrency;
    }

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
