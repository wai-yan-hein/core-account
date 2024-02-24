/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup.dialog.common;

import com.inventory.model.OrderStatus;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Lenovo
 */
public class OrderStatusTableModel extends AbstractTableModel {

    private final String[] columnNames = {"Code", "Description"};
    private List<OrderStatus> listVou = new ArrayList<>();

    public OrderStatusTableModel() {
    }

    public OrderStatusTableModel(List<OrderStatus> listVou) {
        this.listVou = listVou;
    }

    public List<OrderStatus> getListVou() {
        return listVou;
    }

    public void setListVou(List<OrderStatus> listVou) {
        this.listVou = listVou;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return listVou == null ? 0 : listVou.size();
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
        OrderStatus category = listVou.get(rowIndex);
        return switch (columnIndex) {
            case 0 ->
                category.getUserCode();
            case 1 ->
                category.getDescription();
            default ->
                null;
        };
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public OrderStatus getOrderStatus(int row) {
        return listVou.get(row);
    }

    public void setOrderStatus(OrderStatus category, int row) {
        if (!listVou.isEmpty()) {
            listVou.set(row, category);
            fireTableRowsUpdated(row, row);
        }
    }

    public void addOrderStatus(OrderStatus item) {
        if (!listVou.isEmpty()) {
            listVou.add(item);
            fireTableRowsInserted(listVou.size() - 1, listVou.size() - 1);
        }
    }

    public void remove(int row) {
        if (listVou.isEmpty()) {
            listVou.remove(row);
            fireTableRowsDeleted(row, row);
        }
    }

    public void refresh() {
        fireTableDataChanged();
    }
}
