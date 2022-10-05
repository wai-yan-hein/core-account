/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.common.Global;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.model.Location;
import com.inventory.model.Pattern;
import com.inventory.model.Stock;
import com.inventory.model.StockInOutDetail;
import com.inventory.model.StockUnit;
import com.toedter.calendar.JDateChooser;
import java.awt.HeadlessException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author wai yan
 */
public class StockInOutTableModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(StockInOutTableModel.class);
    private String[] columnNames = {"Stock Code", "Stock Name", "Location",
        "In-Qty", "In-Unit", "Out-Qty", "Out-Unit", "Cost Price", "Amount"};
    private JTable parent;
    private List<StockInOutDetail> listStock = new ArrayList();
    private List<String> deleteList = new ArrayList();
    private SelectionObserver observer;
    private InventoryRepo inventoryRepo;
    private JDateChooser vouDate;

    public JDateChooser getVouDate() {
        return vouDate;
    }

    public void setVouDate(JDateChooser vouDate) {
        this.vouDate = vouDate;
    }

    public InventoryRepo getInventoryRepo() {
        return inventoryRepo;
    }

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public SelectionObserver getObserver() {
        return observer;
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
                    return io.getStock() == null ? null : io.getStock().getUserCode();
                }
                case 1 -> {
                    String stockName = null;
                    if (io.getStock() != null) {
                        stockName = io.getStock().getStockName();
                        if (ProUtil.isStockNameWithCategory()) {
                            if (io.getStock().getCategory() != null) {
                                stockName = String.format("%s (%s)", stockName, io.getStock().getCategory().getCatName());
                            }
                        }
                    }
                    return stockName;
                }
                case 2 -> {
                    return io.getLocation();
                }
                case 3 -> {
                    return io.getInQty();
                }
                case 4 -> {
                    return io.getInUnit();
                }
                case 5 -> {
                    return io.getOutQty();
                }
                case 6 -> {
                    return io.getOutUnit();
                }
                case 7 -> {
                    return io.getCostPrice();
                }
                case 8 -> {
                    return Util1.getFloat(io.getCostPrice()) * (Util1.getFloat(io.getInQty()) + Util1.getFloat(io.getOutQty()));
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
            case 3,5,7,8 ->
                Float.class;
            default ->
                String.class;
        };
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column != 8;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        StockInOutDetail io = listStock.get(row);
        try {
            if (value != null) {
                switch (column) {
                    case 0,1 -> {
                        if (value instanceof Stock stock) {
                            io.setStock(stock);
                            io.setCostPrice(0.0f);
                            io.setLocation(inventoryRepo.getDefaultLocation());
                            genPattern(stock, io);
                            setColumnSelection(3);
                        }
                        addNewRow();
                    }
                    case 2 -> {
                        if (value instanceof Location location) {
                            io.setLocation(location);
                        }
                        setColumnSelection(5);
                    }
                    case 3 -> {
                        if (Util1.isNumber(value)) {
                            io.setInQty(Util1.getFloat(value));
                            io.setOutQty(null);
                            io.setOutUnit(null);
                            io.setInUnit(io.getStock().getPurUnit());
                            parent.setRowSelectionInterval(row + 1, row + 1);
                            parent.setColumnSelectionInterval(0, 0);
                        }

                    }

                    case 4 -> {
                        if (value instanceof StockUnit stockUnit) {
                            io.setInUnit(stockUnit);
                        }
                        setColumnSelection(8);
                    }
                    case 5 -> {
                        if (Util1.isNumber(value)) {
                            io.setOutQty(Util1.getFloat(value));
                            io.setInQty(null);
                            io.setInUnit(null);
                            io.setOutUnit(io.getStock().getPurUnit());
                            parent.setRowSelectionInterval(row + 1, row + 1);
                            parent.setColumnSelectionInterval(0, 0);
                        }
                    }
                    case 6 -> {
                        if (value instanceof StockUnit stockUnit) {
                            io.setOutUnit(stockUnit);
                        }
                    }
                    case 7 -> {
                        if (Util1.isNumber(value)) {
                            io.setCostPrice(Util1.getFloat(value));
                        }
                    }
                }
            }
            if (column != 7) {
                if (io.getStock() != null) {
                    if (io.getInUnit() != null || io.getOutUnit() != null) {
                        String unit = io.getInUnit() == null ? io.getOutUnit().getKey().getUnitCode() : io.getInUnit().getKey().getUnitCode();
                        io.setCostPrice(inventoryRepo.getPurRecentPrice(io.getStock().getKey().getStockCode(),
                                Util1.toDateStr(vouDate.getDate(), "yyyy-MM-dd"), unit));
                        if (io.getCostPrice() == 0) {
                            io.setCostPrice(inventoryRepo.getStockIORecentPrice(io.getStock().getKey().getStockCode(),
                                    Util1.toDateStr(vouDate.getDate(), "yyyy-MM-dd"), unit));
                        }
                    }
                }
            }

            observer.selected("CAL-TOTAL", "CAL-TOTAL");
            fireTableRowsUpdated(row, row);
            parent.requestFocus();
        } catch (HeadlessException e) {
            log.error("setValueAt :" + e.getMessage());
        }
    }

    private void genPattern(Stock s, StockInOutDetail iod) {
        String stockCode = s.getKey().getStockCode();
        List<Pattern> patterns = inventoryRepo.getPattern(stockCode);
        if (!patterns.isEmpty()) {
            String input = JOptionPane.showInputDialog("Enter Qty.");
            if (Util1.isPositive(input)) {
                float qty = Util1.getFloat(input);
                patterns.forEach((p) -> {
                    StockInOutDetail io = new StockInOutDetail();
                    io.setOutQty(qty * p.getQty());
                    io.setCostPrice(Util1.getFloat(p.getPrice()));
                    io.setOutUnit(p.getUnit());
                    io.setStock(p.getStock());
                    io.setLocation(p.getLocation());
                    addStockIO(io);
                });
                iod.setInQty(qty);
                iod.setInUnit(s.getPurUnit());
            }
        }
    }

    public List<String> getDeleteList() {
        return deleteList;
    }

    public void setDeleteList(List<String> deleteList) {
        this.deleteList = deleteList;
    }

    private void setColumnSelection(int column) {
        parent.setColumnSelectionInterval(column, column);
        parent.requestFocus();
    }

    public boolean isValidEntry() {
        boolean status = true;
        for (StockInOutDetail od : listStock) {
            if (od.getStock() != null) {
                if (od.getLocation() == null) {
                    status = false;
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Location.");
                    parent.requestFocus();
                } else if (od.getInUnit() == null && od.getOutUnit() == null) {
                    status = false;
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Unit.");
                    parent.requestFocus();
                } else if (Util1.getFloat(od.getInQty()) <= 0 && Util1.getFloat(od.getOutQty()) <= 0) {
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
            if (get.getStock() == null) {
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
        StockInOutDetail sdh = listStock.get(row);
        if (sdh.getIoKey() != null) {
            deleteList.add(sdh.getIoKey().getSdCode());
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
}
