/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.repo.InventoryRepo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.common.Util1;
import com.inventory.model.ReorderLevel;
import com.inventory.model.Stock;
import com.inventory.model.StockUnit;
import java.awt.HeadlessException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class ReorderTableModel extends AbstractTableModel {

    public static final Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
    private final String[] columnNames = {"Stock Code", "Stock Name", "Relation", "Min Qty", "Min Unit", "Max Qty", "Max Unit", "Stock Balance", "Location", "Status"};
    private List<ReorderLevel> listReorder = new ArrayList<>();
    private InventoryRepo inventoryRepo;
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

    public InventoryRepo getInventoryRepo() {
        return inventoryRepo;
    }

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
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
                p.getRelName();
            case 3 ->
                p.getMinQty();
            case 4 ->
                p.getMinUnitCode();
            case 5 ->
                p.getMaxQty();
            case 6 ->
                p.getMaxUnitCode();
            case 7 ->
                p.getBalUnit();
            case 8 ->
                p.getLocName();
            case 9 ->
                p.getStatus();
            default ->
                null;
        };
    }

    private String getStatus(ReorderLevel r) {
        return switch (r.getPosition()) {
            case 1 ->
                "Below-Min";
            case 2 ->
                "Over-Min";
            case 3 ->
                "Below-Max";
            case 4 ->
                "Over-Max";
            default ->
                "Normal";
        };
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        try {
            if (!Objects.isNull(value)) {
                ReorderLevel p = listReorder.get(row);
                switch (column) {
                    case 0, 1 -> {
                        if (value instanceof Stock s) {
                            p.getKey().setStockCode(s.getKey().getStockCode());
                            p.setStockName(s.getStockName());
                            p.setUserCode(s.getUserCode());
                            p.setRelName(s.getRelName());
                            table.setColumnSelectionInterval(2, 2);
                        }
                    }
                    case 3 -> {
                        if (Util1.isPositive(Util1.getDouble(value))) {
                            p.setMinQty(Util1.getDouble(value));
                            p.setMinUnitCode(getPurUnit(p.getKey().getStockCode()));
                            table.setColumnSelectionInterval(3, 3);
                        } else {
                            JOptionPane.showMessageDialog(table, "Invalid Amount.");
                        }
                    }
                    case 4 -> {
                        if (value instanceof StockUnit unit) {
                            p.setMinUnitCode(unit.getKey().getUnitCode());
                            table.setColumnSelectionInterval(4, 4);
                        }
                    }
                    case 5 -> {
                        if (Util1.isPositive(Util1.getDouble(value))) {
                            p.setMaxQty(Util1.getDouble(value));
                            p.setMaxUnitCode(getPurUnit(p.getKey().getStockCode()));
                            table.setColumnSelectionInterval(5, 5);
                        } else {
                            JOptionPane.showMessageDialog(table, "Invalid Amount.");
                        }
                    }
                    case 6 -> {
                        if (value instanceof StockUnit unit) {
                            p.setMaxUnitCode(unit.getKey().getUnitCode());
                            table.setColumnSelectionInterval(4, 4);
                        }
                    }
                }
                switch (column) {
                    case 3, 4 ->
                        p.setMinSmallQty(p.getMinQty() * getSmallQty(p.getKey().getStockCode(), p.getMinUnitCode()));
                    case 5, 6 ->
                        p.setMaxSmallQty(p.getMaxQty() * getSmallQty(p.getKey().getStockCode(), p.getMinUnitCode()));

                }
                p.setPosition(getPosition(p));
                p.setStatus(getStatus(p));
                inventoryRepo.saveReorder(p);
                fireTableRowsUpdated(row, row);
                table.requestFocus();
            }
        } catch (HeadlessException e) {
            log.error(String.format("setValueAt : %s", e.getMessage()));
        }
    }

    private String getPurUnit(String stockCode) {
        return inventoryRepo.findStock(stockCode).block().getPurUnitCode();
    }

    private double getSmallQty(String stockCode, String unit) {
        double qty = 0.0f;
        if (!Objects.isNull(stockCode) && !Objects.isNull(unit)) {
            qty = inventoryRepo.getSmallQty(stockCode, unit).block().getSmallQty();
        }
        return qty;
    }

    private int getPosition(ReorderLevel rl) {
        double balQty = rl.getBalSmallQty();
        double minQty = rl.getMinSmallQty();
        double maxQty = rl.getMaxSmallQty();
        if (balQty < minQty) {
            return 1;
        } else if (balQty > minQty) {
            return 2;
        } else if (balQty < maxQty) {
            return 3;
        } else if (balQty > maxQty) {
            return 4;
        }
        return 5;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case 3, 5 ->
                Float.class;
            default ->
                String.class;
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return switch (columnIndex) {
            case 3, 4, 5, 6 ->
                true;
            default ->
                false;
        };
    }

    public List<ReorderLevel> getListPattern() {
        return listReorder;
    }

    public void setListPattern(List<ReorderLevel> listReorder) {
        this.listReorder = listReorder;
        if (!this.listReorder.isEmpty()) {
            for (ReorderLevel od : this.listReorder) {
                od.setStatus(getStatus(od));
            }
        }
        fireTableDataChanged();
    }

    public ReorderLevel getReorder(int row) {
        return listReorder.get(row);
    }

    public void setReorder(ReorderLevel report, int row) {
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
}
