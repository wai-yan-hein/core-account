/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.common.Global;
import com.common.Util1;
import com.inventory.model.StockFormula;
import com.inventory.model.StockFormulaKey;
import com.repo.InventoryRepo;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class StockFormulaTableModel extends AbstractTableModel {

    private final String[] columnNames = {"Code", "Name", "Active"};
    private List<StockFormula> listDetail = new ArrayList<>();
    private JTable table;
    private InventoryRepo inventoryRepo;

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public void setTable(JTable table) {
        this.table = table;
    }

    @Override
    public int getRowCount() {
        return listDetail == null ? 0 : listDetail.size();
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        try {
            StockFormula s = listDetail.get(rowIndex);
            return switch (columnIndex) {
                case 0 ->
                    s.getUserCode();
                case 1 ->
                    s.getFormulaName();
                case 2 ->
                    s.isActive();
                default ->
                    null;
            };
        } catch (Exception e) {
            log.error("getValueAt : " + e.getMessage());
        }
        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        StockFormula s = listDetail.get(row);
        if (value != null) {
            try {
                switch (column) {
                    case 0 ->
                        s.setUserCode(String.valueOf(value));
                    case 1 -> {
                        s.setFormulaName(String.valueOf(value));
                        addNewRow();
                        setSelection(row + 1, 0);
                    }
                    case 2 ->
                        s.setActive(Util1.getBoolean(value));
                }
                save(s, row);
                fireTableRowsUpdated(row, row);
                table.requestFocus();
            } catch (Exception e) {
                log.info("setValueAt : " + e.getMessage());
            }
        }
    }

    private void save(StockFormula f, int row) {
        if (isValidEntry(f)) {
            inventoryRepo.saveStockFormula(f).doOnSuccess((t) -> {
                if (t != null) {
                    listDetail.set(row, t);
                    addNewRow();
                }
            }).subscribe();
        }
    }

    private boolean isValidEntry(StockFormula f) {
        if (Util1.isNullOrEmpty(f.getFormulaName())) {
            JOptionPane.showMessageDialog(table, "Name can't empty.");
            return false;
        }
        if (f.getKey().getFormulaCode() != null) {
            f.setUpdatedBy(Global.loginUser.getUserCode());
        }
        f.setActive(true);
        return f.getFormulaName() != null;
    }

    private void setSelection(int row, int column) {
        table.setRowSelectionInterval(row, row);
        table.setColumnSelectionInterval(column, column);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnIndex == 2 ? Boolean.class : String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    public List<StockFormula> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<StockFormula> listDetail) {
        this.listDetail = listDetail;
        fireTableDataChanged();
    }

    public StockFormula getObject(int row) {
        return listDetail.get(row);
    }

    public void setObject(StockFormula itemUnit, int row) {
        if (!listDetail.isEmpty()) {
            listDetail.set(row, itemUnit);
            fireTableRowsUpdated(row, row);
        }
    }

    public void addObject(StockFormula item) {
        if (!listDetail.isEmpty()) {
            listDetail.add(item);
            fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
        }
    }

    public void refresh() {
        fireTableDataChanged();
    }

    public void addNewRow() {
        if (listDetail != null) {
            if (!hasEmptyRow()) {
                StockFormula pd = new StockFormula();
                StockFormulaKey key = new StockFormulaKey();
                key.setCompCode(Global.compCode);
                pd.setKey(key);
                pd.setCreatedBy(Global.loginUser.getUserCode());
                listDetail.add(pd);
                fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
            }
        }
    }

    private boolean hasEmptyRow() {
        boolean status = false;
        if (listDetail.size() >= 1) {
            StockFormula get = listDetail.get(listDetail.size() - 1);
            if (get.getFormulaName() == null) {
                status = true;
            }
        }
        return status;
    }

    public void delete(int row) {
        listDetail.remove(row);
        fireTableRowsDeleted(row, row);
    }
}
