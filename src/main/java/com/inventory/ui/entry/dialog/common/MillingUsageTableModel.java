/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry.dialog.common;

import com.inventory.model.MillingUsage;
import com.repo.InventoryRepo;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class MillingUsageTableModel extends AbstractTableModel {

    private List<MillingUsage> listDetail = new ArrayList<>();
    private final String[] columnNames = {"Code", "Stock Name", "Location", "Qty", "Unit"};
    private JTable table;
    private InventoryRepo inventoryRepo;

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public JTable getTable() {
        return table;
    }

    public void setTable(JTable table) {
        this.table = table;
    }

    public MillingUsageTableModel() {
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
        return switch (column) {
            case 3 ->
                Double.class;
            default ->
                String.class;
        };
    }

    public List<MillingUsage> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<MillingUsage> listDetail) {
        this.listDetail = listDetail;
        fireTableDataChanged();
    }

    

    @Override
    public Object getValueAt(int row, int column) {
        if (listDetail.isEmpty()) {
            return null;
        }
        try {
            MillingUsage b = listDetail.get(row);
            return switch (column) {
                case 0 ->
                    b.getUserCode();
                case 1 ->
                    b.getStockName();
                case 2 ->
                    b.getLocName();
                case 3 ->
                    b.getQty();
                case 4 ->
                    b.getUnit();
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

    public void setObject(MillingUsage t, int row) {
        listDetail.set(row, t);
        fireTableRowsUpdated(row, row);
    }

    public void addObject(MillingUsage t) {
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

    public MillingUsage getBatch(int row) {
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
}
