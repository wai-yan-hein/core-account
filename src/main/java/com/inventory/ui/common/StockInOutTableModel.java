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
import javax.swing.JLabel;
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
    private JLabel lblRec;
    private boolean negative = false;

    public boolean isNegative() {
        return negative;
    }

    public void setNegative(boolean negative) {
        this.negative = negative;
    }

    public JLabel getLblRec() {
        return lblRec;
    }

    public void setLblRec(JLabel lblRec) {
        this.lblRec = lblRec;
    }

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
                    return Util1.getFloat(io.getInQty()) == 0 ? null : Util1.getFloat(io.getInQty());
                }
                case 4 -> {
                    return io.getInUnitCode();
                }
                case 5 -> {
                    return Util1.getFloat(io.getOutQty()) == 0 ? null : Util1.getFloat(io.getOutQty());
                }
                case 6 -> {
                    return io.getOutUnitCode();
                }
                case 7 -> {
                    return Util1.getFloat(io.getCostPrice()) == 0 ? null : Util1.getFloat(io.getCostPrice());
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
            case 3, 5, 7, 8 ->
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
                    case 0, 1 -> {
                        if (value instanceof Stock s) {
                            io.setStockCode(s.getKey().getStockCode());
                            io.setStockName(s.getStockName());
                            io.setUserCode(s.getUserCode());
                            io.setRelation(s.getRelName());
                            io.setCostPrice(0.0f);
                            io.setInUnitCode(s.getPurUnitCode());
                            io.setOutUnitCode(s.getPurUnitCode());
                            Location l = inventoryRepo.getDefaultLocation();
                            if (l != null) {
                                io.setLocCode(l.getKey().getLocCode());
                                io.setLocName(l.getLocName());
                            }
                            boolean disable = Util1.getBoolean(ProUtil.getProperty("disable.pattern.stockio"));
                            if (!disable) {
                                float costPrice = genPattern(s, io);
                                io.setCostPrice(costPrice);
                            }
                            setColumnSelection(3);
                        }
                        addNewRow();
                    }
                    case 2 -> {
                        if (value instanceof Location l) {
                            io.setLocCode(l.getKey().getLocCode());
                            io.setLocName(l.getLocName());
                        }
                        setColumnSelection(5);
                    }
                    case 3 -> {
                        if (Util1.isNumber(value)) {
                            io.setInQty(Util1.getFloat(value));
                            io.setOutQty(null);
                            io.setOutUnitCode(null);
                            if (io.getInUnitCode() != null) {
                                parent.setRowSelectionInterval(row + 1, row + 1);
                                parent.setColumnSelectionInterval(0, 0);
                            } else {
                                parent.setColumnSelectionInterval(4, 4);
                            }
                        }

                    }

                    case 4 -> {
                        if (value instanceof StockUnit unit) {
                            io.setInUnitCode(unit.getKey().getUnitCode());
                            io.setOutUnitCode(null);
                            parent.setRowSelectionInterval(row + 1, row + 1);
                            parent.setColumnSelectionInterval(0, 0);
                        }
                    }
                    case 5 -> {
                        if (Util1.isNumber(value)) {
                            io.setOutQty(Util1.getFloat(value));
                            io.setInQty(null);
                            io.setInUnitCode(null);
                            if (io.getOutUnitCode() != null) {
                                parent.setRowSelectionInterval(row + 1, row + 1);
                                parent.setColumnSelectionInterval(0, 0);
                            } else {
                                parent.setColumnSelectionInterval(6, 6);
                            }
                        }
                    }
                    case 6 -> {
                        if (value instanceof StockUnit unit) {
                            io.setOutUnitCode(unit.getKey().getUnitCode());
                            io.setInUnitCode(null);
                            parent.setRowSelectionInterval(row + 1, row + 1);
                            parent.setColumnSelectionInterval(0, 0);
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
                if (Util1.getFloat(io.getCostPrice()) == 0) {
                    if (io.getStockCode() != null) {
                        if (io.getInUnitCode() != null || io.getOutUnitCode() != null) {
                            String unit = Util1.isNull(io.getInUnitCode(), io.getOutUnitCode());
                            io.setCostPrice(inventoryRepo.getPurRecentPrice(io.getStockCode(),
                                    Util1.toDateStr(vouDate.getDate(), "yyyy-MM-dd"), unit));
                            if (io.getCostPrice() == 0) {
                                io.setCostPrice(inventoryRepo.getStockIORecentPrice(io.getStockCode(),
                                        Util1.toDateStr(vouDate.getDate(), "yyyy-MM-dd"), unit));
                            }
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

    private void setRecord(int size) {
        lblRec.setText("Records : " + size);
    }

    private float genPattern(Stock s, StockInOutDetail iod) {
        String stockCode = s.getKey().getStockCode();
        boolean explode = s.isExplode();
        List<Pattern> patterns = inventoryRepo.getPattern(stockCode, s.getKey().getDeptId());
        if (!patterns.isEmpty()) {
            String input = JOptionPane.showInputDialog("Enter Qty.");
            if (Util1.isPositive(input)) {
                float totalPrice = 0.0f;
                float qty = Util1.getFloat(input);
                for (Pattern p : patterns) {
                    StockInOutDetail io = new StockInOutDetail();
                    io.setUserCode(p.getUserCode());
                    if (explode) {
                        io.setInQty(qty * p.getQty());
                        io.setInUnitCode(p.getUnitCode());
                    } else {
                        io.setOutQty(qty * p.getQty());
                        io.setOutUnitCode(p.getUnitCode());
                    }
                    float costPrice = Util1.getFloat(p.getPrice());
                    io.setCostPrice(costPrice);
                    io.setStockCode(p.getKey().getStockCode());
                    io.setLocCode(p.getLocCode());
                    io.setLocName(p.getLocName());
                    io.setStockName(p.getStockName());
                    addStockIO(io);
                    totalPrice += costPrice;
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
                return totalPrice;
            }
        }

        return 0.0f;
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
            od.setCostPrice(Util1.getFloat(od.getCostPrice()));
            if (od.getStockCode() != null) {
                if (od.getLocCode() == null) {
                    status = false;
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Location.");
                    parent.requestFocus();
                } else if (od.getInUnitCode() == null && od.getOutUnitCode() == null) {
                    status = false;
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Unit.");
                    parent.requestFocus();
                } else if (Util1.getFloat(od.getInQty()) <= 0 && Util1.getFloat(od.getOutQty()) <= 0) {
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

    public void addObject(StockInOutDetail io) {
        this.listStock.add(io);
        fireTableRowsInserted(listStock.size() - 1, listStock.size() - 1);
    }
}
