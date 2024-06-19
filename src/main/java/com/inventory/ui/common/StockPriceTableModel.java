/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.common.Util1;
import com.inventory.entity.Stock;
import com.inventory.entity.StockUnitPrice;
import com.repo.InventoryRepo;
import java.awt.HeadlessException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class StockPriceTableModel extends AbstractTableModel {

    private final String[] columnNames = {"Unit", "Price N", "Price A", "Price B", "Price C", "Price D", "Prcie E"};
    private List<StockUnitPrice> listDetail = new ArrayList<>();
    @Setter
    private JRadioButton chkCalculate;
    @Setter
    private InventoryRepo inventoryRepo;
    @Setter
    private JTable table;
    @Setter
    private Stock stock;

    @Override
    public int getRowCount() {
        return listDetail.size();
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
        StockUnitPrice p = listDetail.get(rowIndex);
        switch (columnIndex) {
            case 0 -> {
                return p.getKey().getUnit();
            }
            case 1 -> {
                return p.getSalePriceN();
            }
            case 2 -> {
                return p.getSalePriceA();
            }
            case 3 -> {
                return p.getSalePriceB();
            }
            case 4 -> {
                return p.getSalePriceC();
            }
            case 5 -> {
                return p.getSalePriceD();
            }
            case 6 -> {
                return p.getSalePriceE();
            }
        }
        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        try {
            if (!Objects.isNull(value)) {
                StockUnitPrice p = listDetail.get(row);
                switch (column) {
                    case 1 -> {
                        double price = Util1.getDouble(value);
                        if (price > 0) {
                            p.setSalePriceN(price);
                        }
                    }
                    case 2 -> {
                        double price = Util1.getDouble(value);
                        if (price > 0) {
                            p.setSalePriceA(price);
                        }
                    }
                    case 3 -> {
                        double price = Util1.getDouble(value);
                        if (price > 0) {
                            p.setSalePriceB(price);
                        }
                    }
                    case 4 -> {
                        double price = Util1.getDouble(value);
                        if (price > 0) {
                            p.setSalePriceC(price);
                        }
                    }
                    case 5 -> {
                        double price = Util1.getDouble(value);
                        if (price > 0) {
                            p.setSalePriceD(price);
                        }
                    }
                    case 7 -> {
                        double price = Util1.getDouble(value);
                        if (price > 0) {
                            p.setSalePriceE(price);
                        }
                    }
                }
                calculatePrice(Util1.getDouble(value), row, column, p);
                fireTableRowsUpdated(row, row);
                table.requestFocus();
            }
        } catch (HeadlessException e) {
            log.error(String.format("setValueAt : %s", e.getMessage()));
        }
    }

    private void calculatePrice(double price, int row, int colum, StockUnitPrice sup) {
        if (stock != null) {
            if (chkCalculate.isSelected()) {
                if (price > 0) {
                    String relCode = stock.getRelCode();
                    String unit = sup.getKey().getUnit();
                    double smallQty = inventoryRepo.getSmallestQty(relCode, unit).block();
                    if (smallQty > 0) {
                        double smallestPrice = price / smallQty;
                        listDetail.forEach((t) -> {
                            double qtySmall = inventoryRepo.getSmallestQty(relCode, t.getKey().getUnit()).block();
                            double smallPrice = Math.round(smallestPrice * qtySmall);
                            switch (colum) {
                                case 1 -> //Sale Price
                                    t.setSalePriceN(smallPrice);
                                case 2 -> //Sale Price A
                                    t.setSalePriceA(smallPrice);
                                case 3 -> //Sale Price B
                                    t.setSalePriceB(smallPrice);
                                case 4 -> //Sale Price C
                                    t.setSalePriceC(smallPrice);
                                case 5 -> //Sale Price D
                                    t.setSalePriceD(smallPrice);
                                case 6 -> //Std Cost
                                    t.setSalePriceE(smallPrice);
                            }
                        });
                        setListStockUnitPrice(listDetail);
                    }

                }
            }
        }
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return switch (column) {
            case 0 ->
                String.class;
            default ->
                Double.class;
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex != 0;
    }

    public List<StockUnitPrice> getListStockUnitPrice() {
        return listDetail;
    }

    public void setListStockUnitPrice(List<StockUnitPrice> listStockUnitPrice) {
        this.listDetail = listStockUnitPrice;
        fireTableDataChanged();
    }

    public StockUnitPrice getStockUnitPrice(int row) {
        return listDetail.get(row);
    }

    public void setStockUnitPrice(StockUnitPrice report, int row) {
        if (!listDetail.isEmpty()) {
            listDetail.set(row, report);
            fireTableRowsUpdated(row, row);
        }
    }

    public void addStockUnitPrice(StockUnitPrice item) {
        if (!listDetail.isEmpty()) {
            listDetail.add(item);
            fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
        }
    }

    public void refresh() {
        fireTableDataChanged();
    }

    public void addRow() {
        StockUnitPrice p = new StockUnitPrice();
        listDetail.add(p);
        fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
    }

    public void remove(int row) {
        listDetail.remove(row);
        fireTableRowsDeleted(row, row);
    }

    public void clear() {
        listDetail.clear();
        fireTableDataChanged();
    }
}
