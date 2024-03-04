/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry.dialog.common;

import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.dto.SaleNote;
import com.inventory.entity.StockUnit;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class SaleNoteTableModel extends AbstractTableModel {

    private List<SaleNote> listDetail = new ArrayList<>();
    private final String[] columnNames = {"Description", "Sale Qty", "Note-Qty", "Unit"};
    private JTable table;
    @Setter
    private SelectionObserver observer;

    public JTable getTable() {
        return table;
    }

    public void setTable(JTable table) {
        this.table = table;
    }

    public SaleNoteTableModel() {
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return true;
    }

    @Override
    public Class getColumnClass(int column) {
        return switch (column) {
            case 1, 2 ->
                Double.class;
            default ->
                String.class;
        };
    }

    public List<SaleNote> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<SaleNote> listDetail) {
        this.listDetail = listDetail;
        fireTableDataChanged();
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listDetail.isEmpty()) {
            return null;
        }
        try {
            SaleNote b = listDetail.get(row);
            return switch (column) {
                case 0 ->
                    b.getDescription();
                case 1 ->
                    Util1.toNull(b.getSaleQty());
                case 2 ->
                    Util1.toNull(b.getQty());
                case 3 ->
                    b.getUnitName();
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
        try {
            if (value != null) {
                SaleNote obj = listDetail.get(row);
                switch (column) {
                    case 0 ->
                        obj.setDescription(String.valueOf(value));
                    case 1 -> {
                        double qty = Util1.getDouble(value);
                        if (qty > 0) {
                            obj.setSaleQty(qty);
                        }
                    }
                    case 2 -> {
                        double qty = Util1.getDouble(value);
                        if (qty > 0) {
                            obj.setQty(qty);
                        }
                    }
                    case 3 -> {
                        if (value instanceof StockUnit s) {
                            obj.setUnitName(s.getUnitName());
                            setSelection(row + 1, 2);
                        }
                    }
                }
                fireTableRowsUpdated(row, row);
                observer.selected("CAL_TOTAL", "CAL_TOTAL");
                table.requestFocus();
            }
        } catch (Exception e) {
            log.error("setValueAt : " + e.getMessage());
        }
    }

    private void setSelection(int row, int column) {
        table.setRowSelectionInterval(row, row);
        table.setColumnSelectionInterval(column, column);
    }

    public void setBatch(SaleNote t, int row) {
        listDetail.set(row, t);
        fireTableRowsUpdated(row, row);
    }

    public void addObject(SaleNote t) {
        listDetail.add(t);
        fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
    }

    @Override
    public int getRowCount() {
        if (listDetail == null) {
            return 0;
        } else {
            return listDetail.size();
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public SaleNote getObject(int row) {
        if (listDetail == null) {
            return null;
        } else if (listDetail.isEmpty()) {
            return null;
        } else {
            return listDetail.get(row);
        }
    }

    public int getSize() {
        if (listDetail == null) {
            return 0;
        } else {
            return listDetail.size();
        }
    }

    public void clear() {
        listDetail.clear();
        fireTableDataChanged();
    }

    public void delete(int row) {
        listDetail.remove(row);
        fireTableRowsDeleted(row, row);
    }

    public void addNewRow() {
        if (listDetail != null) {
            if (!hasEmptyRow()) {
                SaleNote pd = new SaleNote();
                listDetail.add(pd);
                fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
            }
        }
    }

    private boolean hasEmptyRow() {
        if (listDetail.size() >= 1) {
            SaleNote get = listDetail.get(listDetail.size() - 1);
            if (get.getQty() == 0) {
                return true;
            }
        }
        return false;
    }

}
