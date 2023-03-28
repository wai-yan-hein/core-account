/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup.dialog.common;

import com.user.model.Menu;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Component
public class MenuTableModel extends AbstractTableModel {

    private final String[] columnNames = {"Code", "Menu"};
    private List<Menu> listMenu = new ArrayList<>();

    public MenuTableModel() {
    }

    public MenuTableModel(List<Menu> listMenu) {
        this.listMenu = listMenu;
    }

    @Override
    public int getRowCount() {
        return listMenu == null ? 0 : listMenu.size();
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
        Menu category = listMenu.get(rowIndex);
        return switch (columnIndex) {
            case 0 ->
                category.getUserCode();
            case 1 ->
                category.getMenuName();
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

    public List<Menu> getListCategory() {
        return listMenu;
    }

    public void setListCategory(List<Menu> listMenu) {
        this.listMenu = listMenu;
        fireTableDataChanged();
    }

    public Menu getMenu(int row) {
        return listMenu.get(row);
    }

    public void setMenu(Menu category, int row) {
        if (!listMenu.isEmpty()) {
            listMenu.set(row, category);
            fireTableRowsUpdated(row, row);
        }
    }

    public void addCategory(Menu item) {
        if (!listMenu.isEmpty()) {
            listMenu.add(item);
            fireTableRowsInserted(listMenu.size() - 1, listMenu.size() - 1);
        }
    }

    public void remove(int row) {
        if (listMenu.isEmpty()) {
            listMenu.remove(row);
            fireTableRowsDeleted(row, row);
        }
    }

    public void refresh() {
        fireTableDataChanged();
    }
}
