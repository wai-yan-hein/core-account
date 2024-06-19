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
import com.inventory.editor.TraderAutoCompleter;
import com.inventory.entity.Location;
import com.inventory.entity.OrderHisDetail;
import com.inventory.entity.SaleHisDetail;
import com.inventory.entity.Stock;
import com.inventory.entity.StockUnit;
import com.inventory.entity.StockUnitPrice;
import com.inventory.entity.StockUnitPriceKey;
import com.inventory.entity.Trader;
import com.inventory.ui.entry.SaleDynamic;
import com.inventory.ui.entry.dialog.StockBalanceFrame;
import com.inventory.ui.entry.dialog.UnitChooser;
import com.inventory.ui.setup.dialog.StockUnitChooserDialog;
import com.toedter.calendar.JDateChooser;
import java.util.ArrayList;
import java.util.HashMap;
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
public class SaleTableModel extends AbstractTableModel {

    private String[] columnNames = {"Code", "Description", "Relation", "Qty", "Unit", "Price", "Amount", "Location"};
    @Setter
    private JTable parent;
    private List<SaleHisDetail> listDetail = new ArrayList();
    @Setter
    private SelectionObserver observer;
    @Setter
    private SaleDynamic sale;
    @Setter
    private InventoryRepo inventoryRepo;
    @Setter
    private JDateChooser vouDate;
    @Setter
    private JLabel lblRecord;
    @Setter
    private StockBalanceFrame dialog;
    private HashMap<String, Integer> hmStock = new HashMap<>();

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
            case 3, 5, 6 ->
                Double.class;
            default ->
                String.class;
        };
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        switch (column) {
            case 5 -> {
                return ProUtil.isSalePriceChange();
            }
            case 1, 2, 6, 4 -> {
                return false;
            }
        }
        return true;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            SaleHisDetail sd = listDetail.get(row);
            switch (column) {
                case 0 -> {
                    //code
                    return sd.getUserCode() == null ? sd.getStockCode() : sd.getUserCode();
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
                    return Util1.toNull(sd.getQty());
                }
                case 4 -> {
                    return sd.getUnitCode();
                }
                case 5 -> {
                    //price
                    return Util1.toNull(sd.getPrice());
                }
                case 6 -> {
                    //amount
                    return Util1.toNull(sd.getAmount());
                }
                case 7 -> {
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
            SaleHisDetail sd = listDetail.get(row);
            if (value != null) {
                switch (column) {
                    case 0, 1 -> {
                        //Code
                        if (value instanceof Stock s) {
                            dialog.calStock(s, Global.parentForm);
                            assignDefault(sd, s);
                            addNewRow();
                            if (!Util1.isNullOrEmpty(s.getBarcode())) {
                                mergeStock(sd, row);
                            } else {
                                setSelection(row, 3);
                            }
                        }
                    }
                    case 3 -> {
                        //Qty
                        double qty = Util1.getDouble(value);
                        if (qty > 0) {
                            sd.setQty(Util1.getDouble(value));
                            showPriceDialog(sd, row);
                            if (sd.getUnitCode() == null) {
                                setSelection(row, 4);
                            } else {
                                double price = sd.getPrice();
                                if (price == 0) {
                                    setSelection(row, 5);
                                } else {
                                    setSelection(row + 1, 0);
                                }
                            }

                        }
                    }
                    case 4 -> {
                        //Unit
                        if (value instanceof StockUnit stockUnit) {
                            sd.setUnitCode(stockUnit.getKey().getUnitCode());
                        }

                    }
                    case 5 -> {
                        //price
                        double price = Util1.getDouble(value);
                        if (price > 0) {
                            sd.setPrice(Util1.getDouble(value));
                            sd.setOrgPrice(sd.getPrice());
                            setSelection(row + 1, 0);
                        }
                    }
                    case 6 -> {
                        //amt
                        sd.setAmount(Util1.getDouble(value));

                    }
                    case 7 -> {
                        //Loc
                        if (value instanceof Location l) {
                            sd.setLocCode(l.getKey().getLocCode());
                            sd.setLocName(l.getLocName());
                            setSelection(row + 1, column);
                        }
                    }
                }
                if (column != 5) {
                    if (sd.getPrice() == 0) {
                        if (ProUtil.isSaleLastPrice()) {
                            String stockCode = sd.getStockCode();
                            if (stockCode != null && sd.getUnitCode() != null) {
                                inventoryRepo.getSaleRecentPrice(stockCode,
                                        Util1.toDateStr(vouDate.getDate(), "yyyy-MM-dd"), sd.getUnitCode()).doOnSuccess((t) -> {
                                    sd.setPrice(t == null ? 0 : Util1.getDouble(t.getAmount()));
                                    sd.setOrgPrice(sd.getPrice());
                                    calculateAmount(sd);
                                    fireTableRowsUpdated(row, row);
                                }).subscribe();
                            }
                        }
                    }
                }
                assignLocation(sd);
                calculateAmount(sd);
                fireTableRowsUpdated(row, row);
                setRecord(listDetail.size() - 1);
                parent.requestFocusInWindow();
            }
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }

    private void assignDefault(SaleHisDetail sd, Stock s) {
        sd.setStockCode(s.getKey().getStockCode());
        sd.setStockName(s.getStockName());
        sd.setUserCode(s.getUserCode());
        sd.setRelCode(s.getRelCode());
        sd.setRelName(s.getRelName());
        sd.setQty(0);
        sd.setStock(s);
    }

    private void showPriceDialog(SaleHisDetail sd, int row) {
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
                    sd.setPrice(Util1.getDouble(getTraderPrice(sd.getStock())));
                }
                sd.setOrgPrice(sd.getPrice());
                setSelection(row + 1, 0);
            }
        } else {
            Stock s = sd.getStock();
            sd.setUnitCode(s.getSaleUnitCode());
            sd.setPrice(Util1.getDouble(getTraderPrice(s)));
            sd.setOrgPrice(sd.getPrice());
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
    }

    private void mergeStock(SaleHisDetail sh, int row) {
        String stockCode = sh.getStockCode();
        if (hmStock.containsKey(stockCode)) {
            Integer existRow = hmStock.get(stockCode);
            SaleHisDetail obj = listDetail.get(existRow);
            double qty = obj.getQty() + sh.getQty();
            obj.setQty(qty);
            delete(row);
            fireTableRowsUpdated(existRow, existRow);
            setSelection(row, 0);
        } else {
            hmStock.put(stockCode, row);
            setSelection(row + 1, 0);
        }
    }

    private void assignLocation(SaleHisDetail sd) {
        if (sd.getLocCode() == null) {
            LocationAutoCompleter completer = sale.getLocationAutoCompleter();
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
                SaleHisDetail pd = new SaleHisDetail();
                listDetail.add(pd);
                fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
            }
        }
    }

    private boolean hasEmptyRow() {
        boolean status = false;
        if (listDetail.size() >= 1) {
            SaleHisDetail get = listDetail.get(listDetail.size() - 1);
            if (get.getStockCode() == null) {
                status = true;
            }
        }
        return status;
    }

    public List<SaleHisDetail> getListDetail() {
        return listDetail;
    }

    public void setListOrderDetail(List<OrderHisDetail> listDetail) {
        for (OrderHisDetail ld : listDetail) {
            SaleHisDetail sdl = new SaleHisDetail();
            sdl.setAmount(Util1.getDouble(ld.getAmount()));
            sdl.setBrandName(ld.getBrandName());
            sdl.setCatName(ld.getCatName());
            sdl.setGroupName(ld.getGroupName());
            sdl.setLocCode(ld.getLocCode());
            sdl.setLocName(ld.getLocName());
            sdl.setPrice(Util1.getDouble(ld.getPrice()));
            sdl.setQty(Util1.getDouble(ld.getQty()));
            sdl.setRelName(ld.getRelName());
            sdl.setStockCode(ld.getStockCode());
            sdl.setStockName(ld.getStockName());
            sdl.setTraderName(ld.getTraderName());
            sdl.setUnitCode(ld.getUnitCode());
            this.listDetail.add(sdl);
        }
        setRecord(listDetail.size());
        addNewRow();
    }

    public void setListDetail(List<SaleHisDetail> listDetail) {
        this.listDetail = listDetail;
        setRecord(listDetail.size());
        fireTableDataChanged();
    }

    private void setRecord(int size) {
        lblRecord.setText("Records : " + size);
    }

    private void calculateAmount(SaleHisDetail sale) {
        if (sale.getStockCode() != null) {
            double qty = Util1.getDouble(sale.getQty());
            double price = Util1.getDouble(sale.getPrice());
            double disAmt = sale.getDisAmt();
            double amount = (qty * price) - disAmt;
            boolean foc = sale.isFoc();
            if (foc) {
                sale.setAmount(0);
            } else {
                sale.setAmount(Util1.getDouble(Math.round(amount)));
            }
            observer.selected("SALE-TOTAL", "SALE-TOTAL");
        }
    }

    public boolean isValidEntry() {
        if (listDetail.isEmpty()) {
            JOptionPane.showMessageDialog(Global.parentForm, "No Transaction.",
                    "Invalid.", JOptionPane.ERROR_MESSAGE);
            parent.requestFocus();
            return false;
        }
        for (SaleHisDetail sdh : listDetail) {
            if (sdh.getStockCode() != null) {
                if (Util1.getDouble(sdh.getAmount()) <= 0) {
                    if (!sdh.isFoc()) {
                        JOptionPane.showMessageDialog(Global.parentForm, "Invalid Amount.",
                                "Invalid.", JOptionPane.ERROR_MESSAGE);
                        parent.requestFocus();
                        return false;
                    }
                } else if (sdh.getLocCode() == null) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Location.");
                    parent.requestFocus();
                    return false;
                } else if (sdh.getUnitCode() == null) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Sale Unit.");
                    parent.requestFocus();
                    return false;
                }
            }
        }
        return true;
    }

    private double getTraderPrice(Stock s) {
        if (s != null) {
            String priceType = getTraderType();
            switch (priceType) {
                case "A" -> {
                    return s.getSalePriceA();
                }
                case "B" -> {
                    return s.getSalePriceB();
                }
                case "C" -> {
                    return s.getSalePriceC();
                }
                case "D" -> {
                    return s.getSalePriceD();
                }
                case "E" -> {
                    return s.getSalePriceE();
                }
                //default normal
                default -> {
                    return s.getSalePriceN();
                }
            }
        }
        return 0;
    }

    public void delete(int row) {
        listDetail.remove(row);
        fireTableRowsDeleted(row, row);
        if (row - 1 >= 0) {
            parent.setRowSelectionInterval(row - 1, row - 1);
        } else {
            parent.setRowSelectionInterval(0, 0);
        }
        parent.requestFocus();
    }

    public void addSale(SaleHisDetail sd) {
        if (listDetail != null) {
            listDetail.add(sd);
            fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
        }
    }

    public SaleHisDetail getSale(int row) {
        return listDetail.get(row);
    }

    public void clear() {
        if (listDetail != null) {
            listDetail.clear();
            hmStock.clear();
            fireTableDataChanged();
        }
    }

    private String getTraderType() {
        TraderAutoCompleter completer = sale.getTraderAutoCompleter();
        if (completer != null) {
            Trader t = completer.getTrader();
            return t == null ? "N" : Util1.isNull(t.getPriceType(), "N");
        }
        return "N";
    }
}
