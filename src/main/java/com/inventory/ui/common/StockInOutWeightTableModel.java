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
import com.inventory.entity.Location;
import com.inventory.entity.Stock;
import com.inventory.entity.StockInOutDetail;
import com.inventory.entity.StockInOutKey;
import com.inventory.entity.StockUnit;
import com.toedter.calendar.JDateChooser;
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
public class StockInOutWeightTableModel extends AbstractTableModel {

    private String[] columnNames = {"Stock Code", "Stock Name", "Location", "Weight",
        "Weight Unit", "In-Qty",
        "In-Unit", "Out-Qty", "Out-Unit", "Cost Price", "Amount", "Total Weight"};
    private JTable parent;
    private List<StockInOutDetail> listStock = new ArrayList();
    private List<StockInOutKey> deleteList = new ArrayList();
    private SelectionObserver observer;
    private InventoryRepo inventoryRepo;
    private JDateChooser vouDate;
    private JLabel lblRec;
    private boolean negative = false;

    public void setNegative(boolean negative) {
        this.negative = negative;
    }

    public void setLblRec(JLabel lblRec) {
        this.lblRec = lblRec;
    }

    public void setVouDate(JDateChooser vouDate) {
        this.vouDate = vouDate;
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
                    return Util1.toNull(io.getWeight());
                }
                case 4 -> {
                    return io.getWeightUnit();
                }
                case 5 -> {
                    return Util1.toNull(io.getInQty());
                }
                case 6 -> {
                    return io.getInUnitCode();
                }
                case 7 -> {
                    return Util1.toNull(io.getOutQty());
                }
                case 8 -> {
                    return io.getOutUnitCode();
                }
                case 9 -> {
                    return Util1.toNull(io.getCostPrice());
                }
                case 10 -> {
                    return Util1.toNull(getAmount(io));
                }
                case 11 -> {
                    return Util1.toNull(io.getTotalWeight());
                }

            }
        } catch (Exception e) {
            log.error("getValueAt: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Class getColumnClass(int column) {
        //{"Stock Code", "Stock Name", "Location", "Weight", "Weight Unit", "In-Qty", "In-Unit", 
        //"Out-Qty", "Out-Unit", "Cost Price", "Amount", "Total Weight"};
        return switch (column) {
            case 3, 5, 7, 9, 10, 11 ->
                Double.class;
            default ->
                String.class;
        };
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return switch (column) {
            case 10, 11 ->
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
                            io.setInUnitCode(s.getPurUnitCode());
                            io.setOutUnitCode(s.getPurUnitCode());
                            io.setWeight(s.getWeight());
                            io.setWeightUnit(s.getWeightUnit());
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
                        if (Util1.isNumber(value)) {
                            io.setWeight(Util1.getDouble(value));
                            if (io.getWeightUnit() != null) {
                                setSelection(row, 4);
                            } else {
                                setSelection(row, 5);
                            }
                        }

                    }
                    case 4 -> {
                        if (value instanceof StockUnit unit) {
                            io.setWeightUnit(unit.getKey().getUnitCode());
                            setSelection(row, 5);
                        }
                    }
                    case 5 -> {
                        if (Util1.isNumber(value)) {
                            io.setInQty(Util1.getDouble(value));
                            io.setOutQty(0);
                            io.setOutUnitCode(null);
                            if (io.getInUnitCode() != null) {
                                setSelection(row, 6);
                            } else {
                                setSelection(row, 7);
                            }
                        }

                    }
                    case 6 -> {
                        if (value instanceof StockUnit unit) {
                            io.setInUnitCode(unit.getKey().getUnitCode());
                            io.setOutQty(0);
                            io.setOutUnitCode(null);
                            setSelection(row, 7);
                        }
                    }
                    case 7 -> {
                        if (Util1.isNumber(value)) {
                            io.setOutQty(Util1.getDouble(value));
                            io.setInQty(0);
                            io.setInUnitCode(null);
                            if (io.getOutUnitCode() != null) {
                                setSelection(row, 8);
                            } else {
                                setSelection(row, 9);
                            }
                        }
                    }
                    case 8 -> {
                        if (value instanceof StockUnit unit) {
                            io.setOutUnitCode(unit.getKey().getUnitCode());
                            io.setInQty(0);
                            io.setInUnitCode(null);
                            setSelection(row, 9);
                        }
                    }
                    case 9 -> {
                        if (Util1.isNumber(value)) {
                            io.setCostPrice(Util1.getDouble(value));
                            io.setAmount(calAmount(io));
                            addNewRow();
                            setSelection(row + 1, 0);
                        }
                    }
                }
            }
            if (column != 9) {
                if (Util1.getDouble(io.getCostPrice()) == 0) {
                    String stockCode = io.getStockCode();
                    String locCode = io.getLocCode();
                    if (stockCode != null && locCode != null) {
                        inventoryRepo.getWeightAvgPrice(stockCode, locCode).doOnSuccess((t) -> {
                            io.setCostPrice(t == null ? 0 : t.getAmount());
                            fireTableRowsUpdated(row, row);
                        }).subscribe();
                    }
                }
            }
            observer.selected("CAL-TOTAL", "CAL-TOTAL");
            setRecord(listStock.size() - 1);
            calWeight(io);
            fireTableRowsUpdated(row, row);
            parent.requestFocus();
        } catch (HeadlessException e) {
            log.error("setValueAt :" + e.getMessage());
        }
    }

    private double getAmount(StockInOutDetail io) {
        return io.getAmount() == 0 ? calAmount(io) : io.getAmount();
    }

    private double calAmount(StockInOutDetail io) {
        double qty = io.getInQty() > 0 ? io.getInQty() : io.getOutQty();
        double bag = io.getInBag() > 0 ? io.getInBag() : io.getOutBag();
        double price = io.getCostPrice();
        return qty > 0 ? qty * price : bag * price;
    }

    private void calWeight(StockInOutDetail io) {
        double qty = io.getInQty() > 0 ? io.getInQty() : io.getOutQty();
        io.setTotalWeight(qty * io.getWeight());
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
        boolean status = true;
        for (StockInOutDetail od : listStock) {
            od.setCostPrice(Util1.getDouble(od.getCostPrice()));
            if (od.getStockCode() != null) {
                if (od.getLocCode() == null) {
                    status = false;
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Location.");
                    parent.requestFocus();
                } else if (od.getInUnitCode() == null && od.getOutUnitCode() == null) {
                    status = false;
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Unit.");
                    parent.requestFocus();
                } else if (Util1.getDouble(od.getInQty()) <= 0 && Util1.getDouble(od.getOutQty()) <= 0) {
                    if (negative) {
                        return true;
                    }
                    status = false;
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Qty.");
                    parent.requestFocus();
                }
            }
        }
        return status;

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

    private void addStockIO(StockInOutDetail s) {
        listStock.add(s);
        fireTableRowsInserted(listStock.size() - 1, listStock.size() - 1);

    }

    public void addNewRow() {
        if (listStock != null) {
            if (!hasEmptyRow()) {
                StockInOutDetail pd = new StockInOutDetail();
                int row = listStock.size();
                if (row > 1) {
                    StockInOutDetail up = listStock.get(row - 1);
                    pd.setLocCode(up.getLocCode());
                    pd.setLocName(up.getLocName());
                    listStock.add(pd);
                    fireTableRowsInserted(listStock.size() - 1, listStock.size() - 1);
                } else {
                    inventoryRepo.getDefaultLocation().doOnSuccess((l) -> {
                        pd.setLocCode(l.getKey().getLocCode());
                        pd.setLocName(l.getLocName());
                        listStock.add(pd);
                        fireTableRowsInserted(listStock.size() - 1, listStock.size() - 1);
                    }).subscribe();
                }
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
