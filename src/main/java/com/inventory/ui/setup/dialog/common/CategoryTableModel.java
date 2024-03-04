/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup.dialog.common;

import com.inventory.entity.Category;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Lenovo
 */
public class CategoryTableModel extends AbstractTableModel {

    private final String[] columnNames = {"Code", "Category"};
    private List<Category> listCategory = new ArrayList<>();

    public CategoryTableModel() {
    }

    public CategoryTableModel(List<Category> listCategory) {
        this.listCategory = listCategory;
    }

    @Override
    public int getRowCount() {
        return listCategory == null ? 0 : listCategory.size();
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
        Category category = listCategory.get(rowIndex);
        return switch (columnIndex) {
            case 0 ->
                category.getUserCode();
            case 1 ->
                category.getCatName();
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

    public List<Category> getListCategory() {
        return listCategory;
    }

    public void setListCategory(List<Category> listCategory) {
        this.listCategory = listCategory;
        fireTableDataChanged();
    }

    public Category getCategory(int row) {
        return listCategory.get(row);
    }

    public void setCategory(Category category, int row) {
        if (!listCategory.isEmpty()) {
            listCategory.set(row, category);
            fireTableRowsUpdated(row, row);
        }
    }

    public void addCategory(Category item) {
        if (!listCategory.isEmpty()) {
            listCategory.add(item);
            fireTableRowsInserted(listCategory.size() - 1, listCategory.size() - 1);
        }
    }

    public void remove(int row) {
        if (listCategory.isEmpty()) {
            listCategory.remove(row);
            fireTableRowsDeleted(row, row);
        }
    }

    public void refresh() {
        fireTableDataChanged();
    }
}
