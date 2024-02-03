/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.repo.InventoryRepo;
import com.common.Global;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.model.Location;
import com.inventory.model.Stock;
import com.inventory.model.StockInOutDetail;
import com.inventory.model.StockInOutKey;
import java.awt.HeadlessException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author wai yan
 */
@Slf4j
public class StockInOutPaddyTableModel extends AbstractTableModel {

    private String[] columnNames = {"Stock Code", "Stock Name", "Location", "Moisture", "Hard Rice", "Weight",
        "In-Qty", "Out-Qty", "In-Bag", "Out-Bag", "Price", "Amount"};
    private JTable parent;
    private List<StockInOutDetail> listStock = new ArrayList();
    private List<StockInOutKey> deleteList = new ArrayList();
    private SelectionObserver observer;
    private InventoryRepo inventoryRepo;
    private JLabel lblRec;

    public void setLblRec(JLabel lblRec) {
        this.lblRec = lblRec;
    }

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    @Override
    public int getRowCount() {
        return listStock.size();
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            StockInOutDetail io = listStock.get(row);
            switch (column) {
                case 0 -> {
                    return io.getUserCode() == null ? io.getStockCode() : io.getUserCode();
                }
                case 1 -> {
                    String stockName = null;
                    if (io.getStockCode() != null) {
                        stockName = io.getStockName();
                        if (ProUtil.isStockNameWithCategory()) {
                            if (io.getCatName() != null) {
                                stockName = String.format("%s (%s)", stockName, io.getCatName());
                            }
                        }
                    }
                    return stockName;
                }
                case 2 -> {
                    return io.getLocName();
                }
                case 3 -> {
                    return Util1.toNull(io.getWet());
                }
                case 4 -> {
                    return Util1.toNull(io.getRice());
                }
                case 5 -> {
                    return Util1.toNull(io.getWeight());
                }
                case 6 -> {
                    return Util1.toNull(io.getInQty());
                }
                case 7 -> {
                    return Util1.toNull(io.getOutQty());
                }
                case 8 -> {
                    return Util1.toNull(io.getInBag());
                }
                case 9 -> {
                    return Util1.toNull(io.getOutBag());
                }
                case 10 -> {
                    return Util1.toNull(io.getCostPrice());
                }
                case 11 -> {
                    return Util1.toNull(getAmount(io));
                }
            }
        } catch (Exception e) {
            log.error("getValueAt: " + e.getMessage());
        }
        return null;
    }

    private double getAmount(StockInOutDetail io) {
        return io.getAmount() == 0 ? calAmount(io) : io.getAmount();
    }

    @Override
    public Class getColumnClass(int column) {
//        "Stock Code", "Stock Name", "Location", "Moisture", "Hard Rice", "Weight",
//        "In-Qty", "Out-Qty", "Bag", "Cost Price", "Amount", "Total Weight"
        return switch (column) {
            case 3, 4, 5, 6, 7, 8, 9, 10, 11 ->
                Double.class;
            default ->
                String.class;
        };
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return switch (column) {
            case 11 ->
                false;
            default ->
                true;
        };
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        StockInOutDetail io = listStock.get(row);
        try {
            if (value != null) {
                switch (column) {
                    case 0, 1 -> {
                        if (value instanceof Stock s) {
                            io.setStockCode(s.getKey().getStockCode());
                            io.setStockName(s.getStockName());
                            io.setUserCode(s.getUserCode());
                            io.setCostPrice(0.0f);
                            io.setWeight(s.getWeight());
                            assignDefaultLocation(io, row);
                            addNewRow();
                        }
                    }
                    case 2 -> {
                        if (value instanceof Location l) {
                            io.setLocCode(l.getKey().getLocCode());
                            io.setLocName(l.getLocName());
                        }
                        setSelection(row, 3);
                    }
                    case 3 -> {
                        double wet = Util1.getDouble(value);
                        if (wet > 0) {
                            io.setWet(wet);
                            setSelection(row, 4);
                        }
                    }
                    case 4 -> {
                        double rice = Util1.getDouble(value);
                        if (rice > 0) {
                            io.setRice(rice);
                            setSelection(row, 5);
                        }
                    }
                    case 5 -> {
                        double wt = Util1.getDouble(value);
                        if (wt >= 0) {
                            io.setWeight(wt);
                            if (io.getInQty() > 0) {
                                io.setTotalWeight(wt * io.getInQty());
                            } else if (io.getOutQty() > 0) {
                                io.setTotalWeight(wt * io.getOutQty());
                            }
                            setSelection(row, 6);
                        }

                    }
                    case 6 -> {
                        double qty = Util1.getDouble(value);
                        if (qty >= 0) {
                            io.setInQty(qty);
                            io.setOutQty(0);
                            io.setOutUnitCode(null);
                            if (io.getWeight() > 0) {
                                io.setTotalWeight(io.getInQty() * io.getWeight());
                            }
                            setSelection(row, 7);
                        }

                    }
                    case 7 -> {
                        double qty = Util1.getDouble(value);
                        if (qty >= 0) {
                            io.setOutQty(qty);
                            io.setInQty(0);
                            io.setInUnitCode(null);
                            if (io.getWeight() > 0) {
                                io.setTotalWeight(io.getOutQty() * io.getWeight());
                            }
                            setSelection(row, 8);
                        }
                    }
                    case 8 -> {
                        double bag = Util1.getDouble(value);
                        if (bag >= 0) {
                            io.setInBag(bag);
                            io.setOutBag(0);
                            setSelection(row, 10);
                        }
                    }
                    case 9 -> {
                        double bag = Util1.getDouble(value);
                        if (bag >= 0) {
                            io.setOutBag(bag);
                            io.setInBag(0);
                            setSelection(row, 10);
                        }
                    }
                    case 10 -> {
                        if (Util1.isNumber(value)) {
                            io.setCostPrice(Util1.getDouble(value));
                            setSelection(row + 1, 0);
                        }
                    }
                }
            }
            observer.selected("CAL-TOTAL", "CAL-TOTAL");
            setRecord(listStock.size() - 1);
            calWeight(io);
            io.setAmount(calAmount(io));
            fireTableRowsUpdated(row, row);
            parent.requestFocus();
        } catch (HeadlessException e) {
            log.error("setValueAt :" + e.getMessage());
        }
    }

    private void assignDefaultLocation(StockInOutDetail io, int row) {
        if (row > 1) {
            StockInOutDetail up = listStock.get(row - 1);
            io.setLocCode(up.getLocCode());
            io.setLocName(up.getLocName());
        } else {
            inventoryRepo.getDefaultLocation().doOnSuccess((l) -> {
                if (l != null) {
                    io.setLocCode(l.getKey().getLocCode());
                    io.setLocName(l.getLocName());
                    fireTableRowsUpdated(row, row);
                }
            }).subscribe();
        }

    }

    private void calWeight(StockInOutDetail io) {
        double qty = io.getInQty() > 0 ? io.getInQty() : io.getOutQty();
        io.setTotalWeight(qty * io.getWeight());
    }

    private double calAmount(StockInOutDetail io) {
        double qty = io.getInQty() > 0 ? io.getInQty() : io.getOutQty();
        double bag = io.getInBag() > 0 ? io.getInBag() : io.getOutBag();
        double price = io.getCostPrice();
        return qty > 0 ? qty * price : bag * price;
    }

    private void setSelection(int row, int column) {
        parent.setRowSelectionInterval(row, row);
        parent.setColumnSelectionInterval(column, column);
    }

    private void setRecord(int size) {
        lblRec.setText("Records : " + size);
    }

    public List<StockInOutKey> getDeleteList() {
        return deleteList;
    }

    public boolean isValidEntry() {
        for (StockInOutDetail od : listStock) {
            od.setCostPrice(Util1.getDouble(od.getCostPrice()));
            if (od.getStockCode() != null) {
                if (od.getLocCode() == null) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Location.");
                    parent.requestFocus();
                    return false;
                } else if (od.getInQty() <= 0
                        && od.getOutQty() <= 0
                        && od.getInBag() <= 0
                        && od.getOutBag() <= 0) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Qty or Bag.");
                    parent.requestFocus();
                    return false;
                }
            }
        }
        return true;

    }

    public List<StockInOutDetail> getCurrentRow() {
        return this.listStock;
    }

    public List<StockInOutDetail> getRetInDetailHis() {
        return this.listStock;
    }

    public List<StockInOutDetail> getListStock() {
        return listStock;
    }

    public void setListStock(List<StockInOutDetail> listStock) {
        this.listStock = listStock;
        setRecord(listStock.size());
        fireTableDataChanged();
    }

    public StockInOutDetail getStockInout(int row) {
        if (listStock != null) {
            return listStock.get(row);
        } else {
            return null;
        }
    }

    public void addNewRow() {
        if (listStock != null) {
            if (!hasEmptyRow()) {
                StockInOutDetail pd = new StockInOutDetail();
                listStock.add(pd);
                fireTableRowsInserted(listStock.size() - 1, listStock.size() - 1);
            }
        }
    }

    private boolean hasEmptyRow() {
        if (listStock.size() >= 1) {
            StockInOutDetail get = listStock.get(listStock.size() - 1);
            if (get.getStockCode() == null) {
                return true;
            }
        }
        return false;
    }

    public void clear() {
        if (listStock != null) {
            listStock.clear();
            fireTableDataChanged();
        }
    }

    public void delete(int row) {
        StockInOutDetail sdh = listStock.get(row);
        if (sdh.getKey() != null) {
            deleteList.add(sdh.getKey());
        }
        listStock.remove(row);
        addNewRow();
        fireTableRowsDeleted(row, row);
        if (row - 1 >= 0) {
            parent.setRowSelectionInterval(row - 1, row - 1);
        } else {
            parent.setRowSelectionInterval(0, 0);
        }
        parent.requestFocus();
    }

    public void addObject(StockInOutDetail io) {
        this.listStock.add(io);
        fireTableRowsInserted(listStock.size() - 1, listStock.size() - 1);
    }
}
