/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup.dialog.common;

import com.inventory.model.Region;
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
public class RegionTableModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(RegionTableModel.class);
    private List<Region> listRegion = new ArrayList<>();
    private final String[] columnNames = {"Code", "Name"};

    public RegionTableModel(List<Region> listRegion) {
        this.listRegion = listRegion;
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
        if (listRegion == null) {
            return null;
        }

        if (listRegion.isEmpty()) {
            return null;
        }

        try {
            Region record = listRegion.get(row);

            switch (column) {
                case 0: //Code
                    return record.getUserCode();
                case 1: //Description
                    return record.getRegionName();
                default:
                    return null;
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
    }

    public List<Region> getListRegion() {
        return listRegion;
    }

    public void setListRegion(List<Region> listRegion) {
        this.listRegion = listRegion;
        fireTableDataChanged();
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        return listRegion == null ? 0 : listRegion.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public Region getRegion(int row) {
        if (listRegion == null) {
            return null;
        } else if (listRegion.isEmpty()) {
            return null;
        } else {
            return listRegion.get(row);
        }
    }

    public int getSize() {
        if (listRegion == null) {
            return 0;
        } else {
            return listRegion.size();
        }
    }

    public void refresh() {
        fireTableDataChanged();
    }
}
