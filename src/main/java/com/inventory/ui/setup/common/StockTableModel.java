/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup.common;

import com.inventory.entity.Stock;
import com.repo.InventoryRepo;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class StockTableModel extends AbstractTableModel {

    private List<Stock> listStock = new ArrayList();
    private String[] columnNames = {"Code", "Description", "Group Name", "Cat Name", "Weight", "Active"};
    private InventoryRepo inventoryRepo;

    public StockTableModel() {
    }

    public InventoryRepo getInventoryRepo() {
        return inventoryRepo;
    }

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public StockTableModel(List<Stock> listStock) {
        this.listStock = listStock;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        switch (column) {
            case 5 -> {
                return true;
            }

        }
        return false;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 5 -> {
                return Boolean.class;
            }
            case 4 -> {
                return Double.class;
            }
            default -> {
                return String.class;
            }
        }
    }

    @Override
    public Object getValueAt(int row, int column
    ) {
        if (listStock == null) {
            return null;
        }

        if (listStock.isEmpty()) {
            return null;
        }

        try {
            Stock med = listStock.get(row);

            return switch (column) {
                case 0 ->
                    med.getUserCode();
                case 1 ->
                    med.getStockName();
                case 2 ->
                    med.getGroupName();
                case 3 ->
                    med.getCatName();
                case 4 ->
                    med.getWeight();
                case 5 ->
                    med.isActive();
                default ->
                    null;
            }; //Code
            //Name
            //Active
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        Stock s = listStock.get(row);
        switch (column) {
            case 5 -> {
                if (value instanceof Boolean active) {
                    s.setActive(active);
                    inventoryRepo.saveStock(s);
                }
            }
            default -> {
            }
        }
    }

    @Override
    public int getRowCount() {
        if (listStock == null) {
            return 0;
        }
        return listStock.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public void modifyColumn() {
        String[] newColumn = {"Code", "Description"};
        this.columnNames = newColumn;
        fireTableStructureChanged();

    }

    public List<Stock> getListStock() {
        return listStock;
    }

    public void setListStock(List<Stock> listStock) {
        this.listStock = listStock;
        fireTableDataChanged();
    }

    public Stock getStock(int row) {
        if (listStock != null) {
            if (!listStock.isEmpty()) {
                return listStock.get(row);
            }
        }
        return null;
    }

    public void addStock(Stock stock) {
        if (listStock != null) {
            listStock.add(stock);
            fireTableRowsInserted(listStock.size() - 1, listStock.size() - 1);

        }
    }

    public void setStock(int row, Stock stock) {
        if (listStock != null) {
            listStock.set(row, stock);
            fireTableRowsUpdated(row, row);
        }
    }

    public void deleteStock(int row) {
        if (listStock != null) {
            if (!listStock.isEmpty()) {
                listStock.remove(row);
                fireTableDataChanged();
            }
        }
    }

    public void refresh() {
        fireTableDataChanged();
    }
}
