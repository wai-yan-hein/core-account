/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.common.Util1;
import com.inventory.model.Location;
import com.inventory.model.ProcessDetail;
import com.inventory.model.Stock;
import com.inventory.model.StockUnit;
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
public class ProcessDetailTableModel extends AbstractTableModel {

    private final String[] columnNames = {"Stock Code", "Stock Name", "Location", "Qty", "Unit", "Price", "Amount"};
    private List<ProcessDetail> listPD = new ArrayList<>();
    private JTable table;

    public JTable getTable() {
        return table;
    }

    public void setTable(JTable table) {
        this.table = table;
    }

    @Override
    public int getRowCount() {
        return listPD.size();
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
            ProcessDetail p = listPD.get(rowIndex);
            switch (columnIndex) {
                case 0 -> {
                    if (p.getStock() == null) {
                        return null;
                    }
                    String userCode = p.getStock().getUserCode();
                    return userCode == null ? p.getStock().getKey().getStockCode() : userCode;
                }
                case 1 -> {
                    if (p.getStock() == null) {
                        return null;
                    }
                    return p.getStock().getStockName();
                }
                case 2 -> {
                    if (p.getLocation() == null) {
                        return null;
                    }
                    return p.getLocation().getLocationName();
                }
                case 3 -> {
                    return p.getQty();
                }
                case 4 -> {
                    if (p.getUnit() == null) {
                        return null;
                    }
                    return p.getUnit().getKey().getUnitCode();
                }
                case 5 -> {
                    return p.getPrice();
                }
                case 6 -> {
                    return p.getAmount();
                }

            }
        } catch (Exception e) {
            log.error(String.format("getValueAt %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        try {
            if (!Objects.isNull(value)) {
                ProcessDetail p = listPD.get(row);
                switch (column) {
                    case 0,1 -> {
                        if (value instanceof Stock stock) {
                            p.setStock(stock);
                            table.setColumnSelectionInterval(2, 2);
                            addNewRow();
                        }
                    }
                    case 2 -> {
                        if (value instanceof Location location) {
                            p.setLocation(location);
                            table.setColumnSelectionInterval(3, 3);
                        }
                    }
                    case 3 -> {
                        if (Util1.getFloat(value) > 1) {
                            p.setQty(Util1.getFloat(value));
                            table.setColumnSelectionInterval(4, 4);
                        }
                    }
                    case 4 -> {
                        if (value instanceof StockUnit unit) {
                            p.setUnit(unit);
                            table.setColumnSelectionInterval(5, 5);
                        }
                    }
                    case 5 -> {
                        if (Util1.getFloat(value) > 1) {
                            p.setPrice(Util1.getFloat(value));
                            table.setColumnSelectionInterval(0, 0);
                            table.setRowSelectionInterval(row + 1, row + 1);
                        }
                    }
                }
            }
            fireTableRowsUpdated(row, row);
            table.requestFocus();
        } catch (Exception e) {
            log.error(String.format("setValueAt : %s", e.getMessage()));
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 3,5,6 -> {
                return Float.class;
            }
        }
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex != 6;
    }

    public List<ProcessDetail> getListPattern() {
        return listPD;
    }

    public List<ProcessDetail> getListPD() {
        return listPD;
    }

    public void setListPD(List<ProcessDetail> listPD) {
        this.listPD = listPD;
        fireTableDataChanged();
    }

    public void setProces(ProcessDetail report, int row) {
        if (!listPD.isEmpty()) {
            listPD.set(row, report);
            fireTableRowsUpdated(row, row);
        }
    }

    public void addProcess(ProcessDetail item) {
        if (!listPD.isEmpty()) {
            listPD.add(item);
            fireTableRowsInserted(listPD.size() - 1, listPD.size() - 1);
        }
    }

    public void addNewRow() {
        if (listPD != null) {
            if (!hasEmptyRow()) {
                ProcessDetail pd = new ProcessDetail();
                listPD.add(pd);
                fireTableRowsInserted(listPD.size() - 1, listPD.size() - 1);
            }
        }
    }

    private boolean hasEmptyRow() {
        boolean status = false;
        if (listPD.size() >= 1) {
            ProcessDetail get = listPD.get(listPD.size() - 1);
            if (get.getStock() == null) {
                status = true;
            }
        }
        return status;
    }

}
