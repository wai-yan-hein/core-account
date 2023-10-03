/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.inventory.model.ReorderLevel;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class ReorderAlertTableModel extends AbstractTableModel {

    private final String[] columnNames = {"Stock Code", "Stock Name", "Min Qty", "Min Unit", "Bal Qty", "Bal Unit", "Reorder Qty", "Reorder Unit"};
    private List<ReorderLevel> listReorder = new ArrayList<>();
    private WebClient inventoryApi;
    private String patternCode;
    private JTable table;

    public JTable getTable() {
        return table;
    }

    public void setTable(JTable table) {
        this.table = table;
    }

    public String getPatternCode() {
        return patternCode;
    }

    public void setPatternCode(String patternCode) {
        this.patternCode = patternCode;
    }

    public WebClient getWebClient() {
        return inventoryApi;
    }

    public void setWebClient(WebClient inventoryApi) {
        this.inventoryApi = inventoryApi;
    }

    @Override
    public int getRowCount() {
        return listReorder.size();
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
        ReorderLevel p = listReorder.get(rowIndex);
        return switch (columnIndex) {
            case 0 ->
                p.getUserCode() == null ? p.getKey().getStockCode() : p.getUserCode();
            case 1 ->
                p.getStockName();
            case 2 ->
                p.getMinQty();
            case 3 ->
                p.getMinUnitCode();
            case 4 ->
                p.getBalQty();
            case 5 ->
                p.getBalUnit();
            case 6 ->
                p.getOrderQty();
            case 7 ->
                p.getOrderUnitCode();
            default ->
                null;
        };
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case 2, 4, 6 ->
                Float.class;
            default ->
                String.class;
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public List<ReorderLevel> getListPattern() {
        return listReorder;
    }

    public void setListPattern(List<ReorderLevel> listReorder) {
        this.listReorder = listReorder;
        fireTableDataChanged();
    }

    public ReorderLevel getPattern(int row) {
        return listReorder.get(row);
    }

    public void setPattern(ReorderLevel report, int row) {
        if (!listReorder.isEmpty()) {
            listReorder.set(row, report);
            fireTableRowsUpdated(row, row);
        }
    }

    public void addPattern(ReorderLevel item) {
        if (!listReorder.isEmpty()) {
            listReorder.add(item);
            fireTableRowsInserted(listReorder.size() - 1, listReorder.size() - 1);
        }
    }

    public void refresh() {
        fireTableDataChanged();
    }

    public void addNewRow() {
        if (!hasEmptyRow()) {
            ReorderLevel p = new ReorderLevel();
            listReorder.add(p);
            fireTableRowsInserted(listReorder.size() - 1, listReorder.size() - 1);
        }
    }

    private boolean hasEmptyRow() {
        ReorderLevel p = listReorder.get(listReorder.size() - 1);
        return p.getKey().getStockCode() == null;
    }

    public void addRow() {
        ReorderLevel p = new ReorderLevel();
        listReorder.add(p);
        fireTableRowsInserted(listReorder.size() - 1, listReorder.size() - 1);
    }
}
