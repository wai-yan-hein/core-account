/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup.dialog.common;

import com.inventory.model.UnitRelation;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class RelationTableModel extends AbstractTableModel {

    private final String[] columnNames = {"Sys Code", "Relation Name"};
    private List<UnitRelation> listRelation = new ArrayList<>();

    @Override
    public int getRowCount() {
        return listRelation.size();
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
        UnitRelation rel = listRelation.get(rowIndex);
        return switch (columnIndex) {
            case 0 ->
                rel.getRelCode();
            case 1 ->
                rel.getRelName();
            default ->
                null;
        };
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    public List<UnitRelation> getListRelation() {
        return listRelation;
    }

    public void setListRelation(List<UnitRelation> listRelation) {
        this.listRelation = listRelation;
        fireTableDataChanged();
    }

    public UnitRelation getRelation(int row) {
        return listRelation.get(row);
    }

    public void setRelation(UnitRelation rel, int row) {
        if (!listRelation.isEmpty()) {
            listRelation.set(row, rel);
            fireTableRowsUpdated(row, row);
        }
    }

    public void addRelation(UnitRelation item) {
        if (!listRelation.isEmpty()) {
            listRelation.add(item);
            fireTableRowsInserted(listRelation.size() - 1, listRelation.size() - 1);
        }
    }


    public void clear() {
        if (listRelation != null) {
            listRelation.clear();
            fireTableDataChanged();
        }
    }

}
