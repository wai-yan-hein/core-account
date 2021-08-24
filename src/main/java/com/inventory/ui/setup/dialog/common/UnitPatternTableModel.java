/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup.dialog.common;

import com.inventory.common.Global;
import com.inventory.common.Util1;
import com.inventory.model.UnitPattern;
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
public class UnitPatternTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnitPatternTableModel.class);
    private final String[] columnNames = {"Pattern Name"};
    private List<UnitPattern> listPattern = new ArrayList<>();

    private String status = "NEW";

    @Override
    public int getRowCount() {
        return listPattern == null ? 0 : listPattern.size();
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
        UnitPattern pattern = listPattern.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return pattern.getPatternName();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        UnitPattern up = listPattern.get(rowIndex);
        switch (columnIndex) {
            case 0:
                if (aValue != null) {
                    if (up.getPatternCode() != null) {
                        status = "EDIT";
                    }
                    up.setPatternName(aValue.toString());
                    up.setMacId(Global.machineId);
                    up.setCompCode(Global.compCode);
                    up.setCreatedDate(Util1.getTodayDate());
                    up.setCreatedBy(Global.loginUser);
                    //UnitPattern save = patternService.save(up);

                    if (status.equals("EDIT")) {
                        up.setUpdatedBy(Global.loginUser);
                        //listPattern.set(rowIndex, save);
                        fireTableCellUpdated(rowIndex, columnIndex);
                    }
                    addNewRow();
                }
                break;

        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    public List<UnitPattern> getListCategory() {
        return listPattern;
    }

    public void setListCategory(List<UnitPattern> listPattern) {
        this.listPattern = listPattern;
        this.listPattern.add(new UnitPattern());
        fireTableDataChanged();
    }

    public UnitPattern getPattern(int row) {
        return listPattern.get(row);
    }

    public void setPattern(UnitPattern pattern, int row) {
        if (!listPattern.isEmpty()) {
            listPattern.set(row, pattern);
            fireTableRowsUpdated(row, row);
        }
    }

    public void addCategory(UnitPattern item) {
        if (!listPattern.isEmpty()) {
            listPattern.add(item);
            fireTableRowsInserted(listPattern.size() - 1, listPattern.size() - 1);
        }
    }

    public void refresh() {
        fireTableDataChanged();
    }

    public void addNewRow() {
        if (listPattern != null) {
            if (hasEmptyRow()) {
                listPattern.add(new UnitPattern());
            }
        }
    }

    private boolean hasEmptyRow() {
        boolean has = false;
        if (listPattern != null) {
            UnitPattern get = listPattern.get(listPattern.size() - 1);
            if (get.getPatternCode() != null) {
                has = true;
            }
        }
        return has;
    }
}
