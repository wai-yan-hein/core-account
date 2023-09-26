/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup.dialog.common;

import com.inventory.model.Country;
import com.inventory.model.Country;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class CountryTableModel extends AbstractTableModel {

    private List<Country> listCountry = new ArrayList<>();
    private final String[] columnNames = {"Code", "Name"};

    public CountryTableModel() {
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
        if (listCountry == null) {
            return null;
        }

        if (listCountry.isEmpty()) {
            return null;
        }

        try {
            Country record = listCountry.get(row);

            return switch (column) {
                case 0 ->
                    record.getCode();
                case 1 ->
                    record.getCountryName();
                default ->
                    null;
            }; //Code
            //Description
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
    }

    public List<Country> getListCountry() {
        return listCountry;
    }

    public void setListCountry(List<Country> listCountry) {
        this.listCountry = listCountry;
        fireTableDataChanged();
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        return listCountry == null ? 0 : listCountry.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public Country getCountry(int row) {
        if (listCountry == null) {
            return null;
        } else if (listCountry.isEmpty()) {
            return null;
        } else {
            return listCountry.get(row);
        }
    }

    public int getSize() {
        if (listCountry == null) {
            return 0;
        } else {
            return listCountry.size();
        }
    }

    public void refresh() {
        fireTableDataChanged();
    }
}
