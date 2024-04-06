/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup.dialog.common;

import com.inventory.entity.Location;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class LocationTableModel extends AbstractTableModel {

    private final String[] columnNames = {"Code", "Location", "Ware House"};
    private List<Location> listLocation = new ArrayList<>();

    public LocationTableModel() {
    }

    @Override
    public int getRowCount() {
        return listLocation == null ? 0 : listLocation.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        try {
            Location location = listLocation.get(rowIndex);
            return switch (columnIndex) {
                case 0 ->
                    location.getUserCode();
                case 1 ->
                    location.getLocName();
                case 2 ->
                    location.getWareHouseName();
                default ->
                    null;
            };
        } catch (Exception e) {
            log.error("getValueAt : " + e.getMessage());
        }
        return null;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public List<Location> getListLocation() {
        return listLocation;
    }

    public void setListLocation(List<Location> listLocation) {
        this.listLocation = listLocation;
        fireTableDataChanged();
    }

    public Location getLocation(int row) {
        return listLocation.get(row);
    }

    public void setLocation(Location location, int row) {
        if (!listLocation.isEmpty()) {
            listLocation.set(row, location);
            fireTableRowsUpdated(row, row);
        }
    }

    public void addLocation(Location item) {
        if (!listLocation.isEmpty()) {
            listLocation.add(item);
            fireTableRowsInserted(listLocation.size() - 1, listLocation.size() - 1);
        }
    }

    public void remove(int row) {
        if (listLocation.isEmpty()) {
            listLocation.remove(row);
            fireTableRowsDeleted(row, row);
        }
    }

    public void refresh() {
        fireTableDataChanged();
    }
}
