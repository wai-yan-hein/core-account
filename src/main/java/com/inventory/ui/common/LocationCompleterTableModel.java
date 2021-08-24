/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.inventory.model.Location;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Component
public class LocationCompleterTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocationCompleterTableModel.class);
    private List<Location> listLocation = new ArrayList();
    private String[] columnNames = {"Name"};

    public LocationCompleterTableModel(List<Location> listLocation) {
        this.listLocation = listLocation;
    }

    @Override
    public int getRowCount() {
        if (listLocation == null) {
            return 0;
        }
        return listLocation.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            Location location = listLocation.get(row);

            switch (column) {
                case 0: //Name
                    return location.getLocationName();
                default:
                    return null;
            }
        } catch (Exception ex) {
            LOGGER.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
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
            default:
                return Object.class;
        }

    }

    @Override
    public void setValueAt(Object value, int row, int column) {

    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

    public List<Location> getlistLocation() {
        return listLocation;
    }

    public void setlistLocation(List<Location> listLocation) {
        this.listLocation = listLocation;
        fireTableDataChanged();
    }

    public Location getLocation(int row) {
        return listLocation.get(row);
    }

}
