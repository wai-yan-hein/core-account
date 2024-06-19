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
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.entity.Location;
import com.inventory.entity.Pattern;
import com.inventory.entity.Stock;
import com.inventory.entity.StockInOutDetail;
import com.inventory.entity.StockUnit;
import com.inventory.ui.entry.dialog.UnitChooser;
import com.toedter.calendar.JDateChooser;
import java.awt.HeadlessException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author wai yan
 */
@Slf4j
public class StockInOutTableModel extends AbstractTableModel {

    private String[] columnNames = {"Stock Code", "Stock Name",
        "In-Qty", "In-Unit", "Out-Qty", "Out-Unit", "Cost Price", "Amount", "Location"};
    @Setter
    private JTable parent;
    private List<StockInOutDetail> listStock = new ArrayList();
    @Setter
    private SelectionObserver observer;
    @Setter
    private InventoryRepo inventoryRepo;
    @Setter
    private JDateChooser vouDate;
    @Setter
    private JLabel lblRec;
    @Setter
    private LocationAutoCompleter locationCompleter;
    @Setter
    private boolean negative;

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
                    return io.getUserCode();
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
                    return Util1.toNull(io.getInQty());
                }
                case 3 -> {
                    return io.getInUnitCode();
                }
                case 4 -> {
                    return Util1.toNull(io.getOutQty());
                }
                case 5 -> {
                    return io.getOutUnitCode();
                }
                case 6 -> {
                    return Util1.toNull(io.getCostPrice());
                }
                case 7 -> {
                    double amt = Util1.getDouble(io.getCostPrice()) * (Util1.getDouble(io.getInQty()) + Util1.getDouble(io.getOutQty()));
                    return Util1.toNull(amt);
                }
                case 8 -> {
                    return io.getLocName();
                }

            }
        } catch (Exception e) {
            log.error("getValueAt: " + e.getMessage()
            );
        }
        return null;
    }

    @Override
    public Class getColumnClass(int column) {
        return switch (column) {
            case 2, 4, 6, 7 ->
                Double.class;
            default ->
                String.class;
        };
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column != 7;
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
                            io.setRelCode(s.getRelCode());
                            io.setUserCode(s.getUserCode());
                            io.setCostPrice(0.0);
                            io.setInUnitCode(s.getPurUnitCode());
                            io.setOutUnitCode(s.getPurUnitCode());
                            assignDefaultLocation(io);
                            genPattern(s, io, row);
                            setSelection(row, 2);
                        }
                    }
                    case 2 -> {
                        double qty = Util1.getDouble(value);
                        if (qty > 0) {
                            io.setInQty(qty);
                            io.setOutQty(0);
                            io.setOutUnitCode(null);
                            String relCode = io.getRelCode();
                            if (!Util1.isNullOrEmpty(relCode)) {
                                UnitChooser chooser = new UnitChooser(inventoryRepo, relCode);
                                io.setInUnitCode(chooser.getSelectUnit());
                            }
                            if (io.getInUnitCode() != null) {
                                addNewRow();
                                setSelection(row + 1, 0);
                            } else {
                                setSelection(row, column + 1);
                            }
                        }

                    }

                    case 3 -> {
                        if (value instanceof StockUnit unit) {
                            io.setInUnitCode(unit.getKey().getUnitCode());
                            io.setOutUnitCode(null);
                            addNewRow();
                            setSelection(row + 1, 0);
                        }
                    }
                    case 4 -> {
                        double qty = Util1.getDouble(value);
                        if (qty > 0) {
                            io.setOutQty(qty);
                            io.setInQty(0);
                            io.setInUnitCode(null);
                            String relCode = io.getRelCode();
                            if (!Util1.isNullOrEmpty(relCode)) {
                                UnitChooser chooser = new UnitChooser(inventoryRepo, relCode);
                                io.setOutUnitCode(chooser.getSelectUnit());
                            }
                            if (io.getOutUnitCode() != null) {
                                addNewRow();
                                setSelection(row + 1, 0);
                            } else {
                                setSelection(row, column + 1);
                            }
                        }
                    }
                    case 5 -> {
                        if (value instanceof StockUnit unit) {
                            io.setOutUnitCode(unit.getKey().getUnitCode());
                            io.setInUnitCode(null);
                            addNewRow();
                            setSelection(row, column + 1);
                        }
                    }
                    case 6 -> {
                        double price = Util1.getDouble(value);
                        if (price > 0) {
                            io.setCostPrice(price);
                            setSelection(row, column + 1);
                        }
                    }
                    case 8 -> {
                        if (value instanceof Location l) {
                            io.setLocCode(l.getKey().getLocCode());
                            io.setLocName(l.getLocName());
                            setSelection(row + 1, column);
                        }
                    }
                }
            }
            if (column != 6) {
                if (Util1.getDouble(io.getCostPrice()) == 0) {
                    String stockCode = io.getStockCode();
                    if (stockCode != null) {
                        if (io.getInUnitCode() != null || io.getOutUnitCode() != null) {
                            String unit = Util1.isNull(io.getInUnitCode(), io.getOutUnitCode());
                            String vouDateStr = Util1.toDateStr(vouDate.getDate(), "yyyy-MM-dd");
                            inventoryRepo.getPrice(stockCode, vouDateStr, unit).doOnSuccess((t) -> {
                                io.setCostPrice(t == null ? 0 : t.getAmount());
                                fireTableRowsUpdated(row, row);
                            }).subscribe();
                        }
                    }
                }
            }
            observer.selected("CAL-TOTAL", "CAL-TOTAL");
            setRecord(listStock.size() - 1);
            fireTableRowsUpdated(row, row);
            parent.requestFocus();
        } catch (HeadlessException e) {
            log.error("setValueAt :" + e.getMessage());
        }
    }

    private void assignDefaultLocation(StockInOutDetail io) {
        Location loc = locationCompleter.getLocation();
        if (loc != null) {
            io.setLocCode(loc.getKey().getLocCode());
            io.setLocName(loc.getLocName());
        }
    }

    private void setRecord(int size) {
        lblRec.setText("Records : " + size);
    }

    private void genPattern(Stock s, StockInOutDetail iod, int row) {
        boolean disable = Util1.getBoolean(ProUtil.getProperty("disable.pattern.stockio"));
        if (!disable) {
            String stockCode = s.getKey().getStockCode();
            boolean explode = s.isExplode();
            String date = Util1.toDateStr(vouDate.getDate(), "yyyy-MM-dd");
            inventoryRepo.getPattern(stockCode, date).doOnSuccess((t) -> {
                if (t != null) {
                    if (!t.isEmpty()) {
                        String input = JOptionPane.showInputDialog("Enter Qty.");
                        if (Util1.getDouble(input) > 0) {
                            double totalPrice = 0.0f;
                            double qty = Util1.getDouble(input);
                            for (Pattern p : t) {
                                StockInOutDetail io = new StockInOutDetail();
                                io.setUserCode(p.getUserCode());
                                if (explode) {
                                    io.setInQty(qty * p.getQty());
                                    io.setInUnitCode(p.getUnitCode());
                                } else {
                                    io.setOutQty(qty * p.getQty());
                                    io.setOutUnitCode(p.getUnitCode());
                                }
                                double pPrice = Util1.getDouble(p.getPrice());
                                io.setCostPrice(pPrice);
                                io.setStockCode(p.getKey().getStockCode());
                                io.setLocCode(p.getLocCode());
                                io.setLocName(p.getLocName());
                                io.setStockName(p.getStockName());
                                addStockIO(io);
                                totalPrice += Util1.getDouble(p.getAmount());
                            }
                            if (explode) {
                                iod.setOutQty(qty);
                                iod.setOutUnitCode(s.getPurUnitCode());
                                iod.setInUnitCode(null);
                            } else {
                                iod.setInQty(qty);
                                iod.setInUnitCode(s.getPurUnitCode());
                                iod.setOutUnitCode(null);
                            }
                            setRecord(listStock.size());
                            iod.setCostPrice(totalPrice);
                            addNewRow();
                            focusOnTable();
                        } else {
                            setSelection(row, 3);
                            addNewRow();
                        }
                    }
                }
                observer.selected("CAL-TOTAL", "CAL-TOTAL");
            }).subscribe();
        } else {
            setSelection(row, 3);
            addNewRow();
        }
    }

    private void focusOnTable() {
        int rc = parent.getRowCount();
        if (rc > 1) {
            parent.setRowSelectionInterval(rc - 1, rc - 1);
            parent.setColumnSelectionInterval(0, 0);
            parent.requestFocus();
        }
    }

    private void setSelection(int row, int column) {
        parent.setRowSelectionInterval(row, row);
        parent.setColumnSelectionInterval(column, column);
    }

    public boolean isValidEntry() {
        for (StockInOutDetail od : listStock) {
            od.setCostPrice(Util1.getDouble(od.getCostPrice()));
            if (od.getStockCode() != null) {
                if (od.getLocCode() == null) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Location.");
                    parent.requestFocus();
                    return false;
                } else if (Util1.isNullOrEmpty(od.getInUnitCode()) && Util1.isNullOrEmpty(od.getOutUnitCode())) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Unit.");
                    parent.requestFocus();
                    return false;
                } else if (Util1.getDouble(od.getInQty()) <= 0 && Util1.getDouble(od.getOutQty()) <= 0) {
                    if (negative) {
                        return true;
                    }
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Qty.");
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

    private void addStockIO(StockInOutDetail s) {
        listStock.add(s);
        fireTableRowsInserted(listStock.size() - 1, listStock.size() - 1);

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
        boolean status = false;
        if (listStock.size() >= 1) {
            StockInOutDetail get = listStock.get(listStock.size() - 1);
            if (get.getStockCode() == null) {
                status = true;
            }
        }
        return status;
    }

    public void clear() {
        if (listStock != null) {
            listStock.clear();
            fireTableDataChanged();
        }
    }

    public void delete(int row) {
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
