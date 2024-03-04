/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup.dialog.common;

import com.inventory.entity.Language;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Lenovo
 */
public class LanguageTableModel extends AbstractTableModel {

    private final String[] columnNames = {"Type", "Code", "Description"};
    private List<Language> listVou = new ArrayList<>();

    public LanguageTableModel() {
    }

    public LanguageTableModel(List<Language> listVou) {
        this.listVou = listVou;
    }

    public List<Language> getListVou() {
        return listVou;
    }

    public void setList(List<Language> listVou) {
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
        Language language = listVou.get(rowIndex);
        if (language != null) {
            return switch (columnIndex) {
                case 0 ->
                    language.getKey().getLanType();
                case 1 ->
                    language.getKey().getLanKey();
                case 2 ->
                    language.getLanValue();
                default ->
                    null;
            };
        }
        return null;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public Language getLanguage(int row) {
        return listVou.get(row);
    }

    public void setLanguage(Language lan, int row) {
        if (!listVou.isEmpty()) {
            listVou.set(row, lan);
            fireTableRowsUpdated(row, row);
        }
    }

    public void addLanguage(Language item) {
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
