/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup.dialog.common;

import com.inventory.model.Job;
import com.inventory.model.LabourGroup;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Lenovo
 */
public class JobTableModel extends AbstractTableModel {

    private final String[] columnNames = {"Code", "Name"};
    private List<Job> listVou = new ArrayList<>();

    public JobTableModel() {
    }

    public JobTableModel(List<Job> listVou) {
        this.listVou = listVou;
    }

    public List<Job> getListVou() {
        return listVou;
    }

    public void setListVou(List<Job> listVou) {
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
        Job job = listVou.get(rowIndex);
        return switch (columnIndex) {
            case 0 ->
                job.getKey().getJobNo();
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
        return listVou.get(row);
    }

    public void setJob(Job category, int row) {
        if (!listVou.isEmpty()) {
            listVou.set(row, category);
            fireTableRowsUpdated(row, row);
        }
    }

    public void addJob(Job item) {
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
