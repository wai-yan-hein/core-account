/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.ui.common;

import com.common.Global;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.entity.Location;
import com.inventory.entity.OrderDetailKey;
import com.inventory.entity.OrderHisDetail;
import com.inventory.entity.Stock;
import com.inventory.entity.StockUnit;
import com.inventory.entity.StockUnitPrice;
import com.inventory.entity.StockUnitPriceKey;
import com.inventory.ui.entry.OrderDynamic;
import com.inventory.ui.entry.dialog.StockBalanceFrame;
import com.inventory.ui.entry.dialog.UnitChooser;
import com.repo.InventoryRepo;
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
 * @author DELL
 */
@Slf4j
public class OrderTableModel extends AbstractTableModel {

    private String[] columnNames = {"Code", "Description", "Relation",
        "Order Qty", "Actual Qty", "Unit", "Price", "Amount", "Weight", "Weight Unit", "Location"};
    @Setter
    private JTable parent;
    private List<OrderHisDetail> listDetail = new ArrayList();
    @Setter
    private SelectionObserver observer;
    private final List<OrderDetailKey> deleteList = new ArrayList();
    @Setter
    private OrderDynamic orderDynamic;
    @Setter
    private JLabel lblRecord;
    @Setter
    private StockBalanceFrame dialog;
    @Setter
    private InventoryRepo inventoryRepo;

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
    public int getRowCount() {
        return listDetail.size();
    }

    @Override
    public Class getColumnClass(int column) {
        return switch (column) {
            case 3, 4, 6, 7, 8 ->
                Double.class;
            default ->
                String.class;
        };
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        switch (column) {
            case 2, 7 -> {
                return false;
            }
        }
        return true;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            OrderHisDetail sd = listDetail.get(row);
            switch (column) {
                case 0 -> {
                    //code
                    return sd.getUserCode();
                }
                case 1 -> {
                    String stockName = null;
                    if (sd.getStockCode() != null) {
                        stockName = sd.getStockName();
                        if (ProUtil.isStockNameWithCategory()) {
                            if (sd.getCatName() != null) {
                                stockName = String.format("%s (%s)", stockName, sd.getCatName());
                            }
                        }
                    }
                    return stockName;
                }
                case 2 -> {
                    return sd.getRelName();
                }
                case 3 -> {
                    //qty
                    return Util1.toNull(sd.getOrderQty());
                }
                case 4 -> {
                    //qty
                    return Util1.toNull(sd.getQty());
                }
                case 5 -> {
                    return sd.getUnitCode();
                }
                case 6 -> {
                    //price
                    return Util1.toNull(sd.getPrice());
                }
                case 7 -> {
                    //amount
                    return Util1.toNull(sd.getAmount());
                }

                case 8 -> {
                    return Util1.toNull(sd.getWeight());
                }
                case 9 -> {
                    return sd.getWeightUnit();
                }
                case 10 -> {
                    //loc
                    return sd.getLocName();
                }
                default -> {
                    return null;
                }
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        try {
            OrderHisDetail sd = listDetail.get(row);
            if (value != null) {
                switch (column) {
                    case 0, 1 -> {
                        //Code
                        if (value instanceof Stock s) {
                            dialog.calStock(s, Global.parentForm);
                            sd.setStockCode(s.getKey().getStockCode());
                            sd.setStockName(s.getStockName());
                            sd.setUserCode(s.getUserCode());
                            sd.setRelCode(s.getRelCode());
                            sd.setRelName(s.getRelName());
                            sd.setWeightUnit(s.getWeightUnit());
                            sd.setUnitCode(s.getSaleUnitCode());
                            sd.setStock(s);
                            addNewRow();
                            setSelection(row, 3);
                        }
                    }
                    case 3 -> {
                        //Order-Qty
                        double qty = Util1.getDouble(value);
                        if (qty > 0) {
                            sd.setOrderQty(qty);
                            sd.setQty(qty);
                            showPriceDialog(sd, row);
                            if (sd.getUnitCode() == null) {
                                setSelection(row, 5);
                            } else {
                                double price = sd.getPrice();
                                if (price == 0) {
                                    setSelection(row, 6);
                                } else {
                                    setSelection(row + 1, 0);
                                }
                            }

                        }
                    }
                    case 4 -> {
                        //Qty
                        double qty = Util1.getDouble(value);
                        if (qty > 0) {
                            sd.setQty(qty);
                        }
                    }
                    case 5 -> {
                        //Unit
                        if (value instanceof StockUnit stockUnit) {
                            sd.setUnitCode(stockUnit.getKey().getUnitCode());
                        }

                    }
                    case 6 -> {
                        //price
                        double price = Util1.getDouble(value);
                        if (price > 0) {
                            sd.setPrice(Util1.getDouble(value));
                            setSelection(row + 1, 0);
                        }
                    }
                    case 7 -> {
                        //amt
                        sd.setAmount(Util1.getDouble(value));
                    }

                    case 8 -> {
                        sd.setWeight(Util1.getDouble(value));
                        setSelection(row, 6);
                    }
                    case 9 -> {
                        if (value instanceof StockUnit u) {
                            sd.setWeightUnit(u.getKey().getUnitCode());
                            setSelection(row, 6);
                        }
                    }
                    case 10 -> {
                        //Loc
                        if (value instanceof Location l) {
                            sd.setLocCode(l.getKey().getLocCode());
                            sd.setLocName(l.getLocName());
                            setSelection(row + 1, column);
                        }
                    }

                }
                assignLocation(sd);
                calculateAmount(sd);
                fireTableRowsUpdated(row, row);
                setRecord(listDetail.size() - 1);
                observer.selected("ORDER-TOTAL", "ORDER-TOTAL");
                parent.requestFocusInWindow();
            }
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }

    private void showPriceDialog(OrderHisDetail sd, int row) {
        String stockCode = sd.getStockCode();
        String relCode = sd.getRelCode();
        if (!Util1.isNullOrEmpty(relCode)) {
            UnitChooser chooser = new UnitChooser(inventoryRepo, relCode);
            sd.setUnitCode(chooser.getSelectUnit());
            double price = sd.getPrice();
            if (price == 0) {
                StockUnitPrice sup = getPriceByUnit(stockCode, Global.compCode, sd.getUnitCode());
                if (sup != null) {
                    sd.setPrice(sup.getSalePriceN());
                } else {
                    sd.setPrice(sd.getStock() == null ? 0 : sd.getStock().getSalePriceN());
                }
                setSelection(row + 1, 0);
            }
        } else {
            Stock s = sd.getStock();
            sd.setUnitCode(s.getSaleUnitCode());
            sd.setPrice(sd.getStock() == null ? 0 : sd.getStock().getSalePriceN());
        }
    }

    private StockUnitPrice getPriceByUnit(String stockCode, String compCode, String unit) {
        StockUnitPriceKey key = new StockUnitPriceKey();
        key.setCompCode(compCode);
        key.setStockCode(stockCode);
        key.setUnit(unit);
        return inventoryRepo.findStockUnitPrice(key).block();
    }

    private void setSelection(int row, int column) {
        parent.setRowSelectionInterval(row, row);
        parent.setColumnSelectionInterval(column, column);
        parent.requestFocus();
    }

    private void assignLocation(OrderHisDetail sd) {
        if (sd.getLocCode() == null) {
            LocationAutoCompleter completer = orderDynamic.getLocationAutoCompleter();
            if (completer != null) {
                Location l = completer.getLocation();
                if (l != null) {
                    sd.setLocCode(l.getKey().getLocCode());
                    sd.setLocName(l.getLocName());
                }
            }
        }
    }

    public void addNewRow() {
        if (listDetail != null) {
            if (!hasEmptyRow()) {
                OrderHisDetail pd = new OrderHisDetail();
                listDetail.add(pd);
                fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
            }
        }
    }

    private boolean hasEmptyRow() {
        boolean status = false;
        if (listDetail.size() >= 1) {
            OrderHisDetail get = listDetail.get(listDetail.size() - 1);
            if (get.getStockCode() == null) {
                status = true;
            }
        }
        return status;
    }

    public List<OrderHisDetail> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<OrderHisDetail> listDetail) {
        this.listDetail = listDetail;
        setRecord(listDetail.size());
        fireTableDataChanged();
    }

    private void setRecord(int size) {
        lblRecord.setText("Records : " + size);
    }

    public void removeListDetail() {
        this.listDetail.clear();
        addNewRow();
    }

    private void calculateAmount(OrderHisDetail oh) {
        if (oh.getStockCode() != null) {
            double amount = Util1.getDouble(oh.getQty()) * Util1.getDouble(oh.getPrice());
            oh.setAmount(Util1.getDouble(Math.round(amount)));
        }
    }

    private void showMessageBox(String text) {
        JOptionPane.showMessageDialog(Global.parentForm, text);
    }

    public boolean isValidEntry() {
        boolean status = true;
        for (OrderHisDetail sdh : listDetail) {
            if (sdh.getStockCode() != null) {
                if (sdh.getLocCode() == null) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Location.");
                    status = false;
                    parent.requestFocus();
                    break;
                } else if (sdh.getUnitCode() == null) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Order Unit.");
                    status = false;
                    parent.requestFocus();
                    break;
                }
            }
        }
        return status;
    }

    public List<OrderDetailKey> getDelList() {
        return deleteList;
    }

    public void clearDelList() {
        if (deleteList != null) {
            deleteList.clear();
        }
    }

    public void delete(int row) {
        OrderHisDetail sdh = listDetail.get(row);
        if (sdh.getKey() != null) {
            deleteList.add(sdh.getKey());
        }
        listDetail.remove(row);
        addNewRow();
        fireTableRowsDeleted(row, row);
        if (row - 1 >= 0) {
            parent.setRowSelectionInterval(row - 1, row - 1);
        } else {
            parent.setRowSelectionInterval(0, 0);
        }
        parent.requestFocus();
    }

    public void addOrder(OrderHisDetail sd) {
        if (listDetail != null) {
            listDetail.add(sd);
            fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
        }
    }

    public OrderHisDetail getOrderEntry(int row) {
        return listDetail.get(row);
    }

    public void clear() {
        if (listDetail != null) {
            listDetail.clear();
        }
    }
}
