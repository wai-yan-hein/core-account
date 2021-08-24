/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup.dialog.common;

import com.inventory.model.StockBrand;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Component
public class StockBrandTableModel extends AbstractTableModel {

    private final String[] columnNames = {"Code", "Brand"};
    private List<StockBrand> listItemBrand = new ArrayList<>();

    public StockBrandTableModel() {
    }

    public StockBrandTableModel(List<StockBrand> listStockBrand) {
        this.listItemBrand = listStockBrand;
    }

    @Override
    public int getRowCount() {
        return listItemBrand == null ? 0 : listItemBrand.size();
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
        StockBrand brand = listItemBrand.get(rowIndex);
        return switch (columnIndex) {
            case 0 ->
                brand.getUserCode();
            case 1 ->
                brand.getBrandName();
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

    public List<StockBrand> getListItemBrand() {
        return listItemBrand;
    }

    public void setListItemBrand(List<StockBrand> listItemBrand) {
        this.listItemBrand = listItemBrand;
        fireTableDataChanged();
    }

    public StockBrand getItemBrand(int row) {
        return listItemBrand.get(row);
    }

    public void setItemBrand(StockBrand brand, int row) {
        if (!listItemBrand.isEmpty()) {
            listItemBrand.set(row, brand);
            fireTableRowsUpdated(row, row);
        }
    }

    public void addItemBrand(StockBrand item) {
        if (!listItemBrand.isEmpty()) {
            listItemBrand.add(item);
            fireTableRowsInserted(listItemBrand.size() - 1, listItemBrand.size() - 1);
        }
    }

    public void refresh() {
        fireTableDataChanged();
    }

}
