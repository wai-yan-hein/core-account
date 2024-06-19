/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup.dialog.common;

import com.common.Util1;
import com.inventory.entity.UnitRelationDetail;
import com.inventory.entity.StockUnit;
import com.inventory.entity.UnitRelation;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class RelationDetailTableModel extends AbstractTableModel {

    private final String[] columnNames = {"Qty", "Unit"};
    private List<UnitRelationDetail> listRelation = new ArrayList<>();
    private UnitRelation relation;
    private JTable table;

    public JTable getTable() {
        return table;
    }

    public void setTable(JTable table) {
        this.table = table;
    }

    public UnitRelation getRelation() {
        return relation;
    }

    public void setRelation(UnitRelation relation) {
        this.relation = relation;
    }

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
        UnitRelationDetail rel = listRelation.get(rowIndex);
        return switch (columnIndex) {
            case 0 ->
                Util1.toNull(rel.getQty());
            case 1 ->
                rel.getUnit();
            default ->
                null;
        };
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (!Objects.isNull(aValue)) {
            if (!listRelation.isEmpty()) {
                UnitRelationDetail rd = listRelation.get(rowIndex);
                switch (columnIndex) {
                    case 0 -> {
                        double qty = Util1.getDouble(aValue);
                        if (qty > 0) {
                            rd.setQty(qty);
                        }
                    }
                    case 1 -> {
                        if (aValue instanceof StockUnit unit) {
                            rd.setUnit(unit.getKey().getUnitCode());
                        }
                    }
                }
                if (isValidEntry(rd)) {
                    addEmptyRow();
                    table.setRowSelectionInterval(rowIndex + 1, rowIndex + 1);
                    table.setColumnSelectionInterval(0, 0);
                }
            }
        }
    }

    private boolean isValidEntry(UnitRelationDetail rd) {
        boolean stauts = true;
        if (Objects.isNull(rd.getQty())) {
            stauts = false;
        } else if (Objects.isNull(rd.getUnit())) {
            stauts = false;
        }
        return stauts;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnIndex == 0 ? Float.class : String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    public List<UnitRelationDetail> getListRelation() {
        return listRelation;
    }

    public void setListRelation(List<UnitRelationDetail> listRelation) {
        this.listRelation = listRelation;
        fireTableDataChanged();
    }

    public void setRelation(UnitRelationDetail rel, int row) {
        if (!listRelation.isEmpty()) {
            listRelation.set(row, rel);
            fireTableRowsUpdated(row, row);
        }
    }

    public void addRelation(UnitRelation item) {
        if (!listRelation.isEmpty()) {
            addEmptyRow();
            fireTableRowsInserted(listRelation.size() - 1, listRelation.size() - 1);
        }
    }

    public void addEmptyRow() {
        if (!hasEmptyRow()) {
            UnitRelationDetail rel = new UnitRelationDetail();
            listRelation.add(rel);
            fireTableRowsInserted(listRelation.size() - 1, listRelation.size() - 1);
        }
    }

    private boolean hasEmptyRow() {
        boolean status = false;
        if (listRelation.size() >= 1) {
            UnitRelationDetail get = listRelation.get(listRelation.size() - 1);
            if (get.getUnit() == null) {
                status = true;
            }
        }
        return status;
    }

    public void clear() {
        if (listRelation != null) {
            relation = null;
            listRelation.clear();
            fireTableDataChanged();
        }
    }

}
