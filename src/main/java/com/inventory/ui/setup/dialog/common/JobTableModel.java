/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup.dialog.common;

import com.inventory.entity.Job;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Lenovo
 */
public class JobTableModel extends AbstractTableModel {

    private final String[] columnNames = {"Code", "Name"};
    private List<Job> listData = new ArrayList<>();

    public JobTableModel() {
    }

    public List<Job> getListData() {
        return listData;
    }

    public void setListData(List<Job> listData) {
        this.listData = listData;
        fireTableDataChanged();
    }

 

    public void setListVou(List<Job> listData) {
        this.listData = listData;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return listData == null ? 0 : listData.size();
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
        Job job = listData.get(rowIndex);
        return switch (columnIndex) {
            case 0 ->
                job.getKey() == null ? null : job.getKey().getJobNo();
            case 1 ->
                job.getJobName();
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

    public Job getJob(int row) {
        return listData.get(row);
    }

    public void setJob(Job category, int row) {
        if (!listData.isEmpty()) {
            listData.set(row, category);
            fireTableRowsUpdated(row, row);
        }
    }

    public void addJob(Job item) {
        if (!listData.isEmpty()) {
            listData.add(item);
            fireTableRowsInserted(listData.size() - 1, listData.size() - 1);
        }
    }

    public void remove(int row) {
        if (listData.isEmpty()) {
            listData.remove(row);
            fireTableRowsDeleted(row, row);
        }
    }

    public void refresh() {
        fireTableDataChanged();
    }
}
