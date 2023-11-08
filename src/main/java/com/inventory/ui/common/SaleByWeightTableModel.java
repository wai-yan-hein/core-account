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
import com.inventory.model.Location;
import com.inventory.model.SaleDetailKey;
import com.inventory.model.SaleHisDetail;
import com.inventory.model.Stock;
import com.inventory.model.StockUnit;
import com.inventory.ui.entry.SaleDynamic;
import com.inventory.ui.entry.dialog.StockBalanceFrame;
import com.toedter.calendar.JDateChooser;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
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
public class SaleByWeightTableModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(SaleByWeightTableModel.class);
    private String[] columnNames = {"Code", "Description", "Relation", "Location", "Weight", "Weight Unit", "Qty", "Unit", "Std-Weight", "Total Qty", "Price", "Amount"};
    private JTable parent;
    private List<SaleHisDetail> listDetail = new ArrayList();
    private SelectionObserver observer;
    private final List<SaleDetailKey> deleteList = new ArrayList();
    private InventoryRepo inventoryRepo;
    private JDateChooser vouDate;
    private JLabel lblRecord;
    private SaleDynamic sale;
    private StockBalanceFrame dialog;

    public void setDialog(StockBalanceFrame dialog) {
        this.dialog = dialog;
    }

    public SaleDynamic getSale() {
        return sale;
    }

    public void setSale(SaleDynamic sale) {
        this.sale = sale;
    }

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public void setLblRecord(JLabel lblRecord) {
        this.lblRecord = lblRecord;
    }

    public void setVouDate(JDateChooser vouDate) {
        this.vouDate = vouDate;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
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
    public int getRowCount() {
        return listDetail.size();
    }

    @Override
    public Class getColumnClass(int column) {
        return switch (column) {
            case 0, 1, 2, 3, 5 ->
                String.class;
            default ->
                Double.class;
        };
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        switch (column) {
            case 2, 9, 11 -> {
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
                    //loc
                    return sd.getLocName();
                }
                case 4 -> {
                    return sd.getWeight();
                }
                case 5 -> {
                    return sd.getWeightUnit();
                }
                case 6 -> {
                    //qty
                    return sd.getQty();
                }
                case 7 -> {
                    return sd.getUnitCode();
                }
                case 8 -> {
                    return sd.getStdWeight();
                }
                case 9 -> {
                    return Util1.getDouble(sd.getTotalWeight()) == 0 ? null : sd.getTotalWeight();
                }
                case 10 -> {
                    //price
                    return sd.getPrice();
                }
                case 11 -> {
                    //amount
                    return sd.getAmount();
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
                            dialog.calStock(s.getKey().getStockCode(),Global.parentForm);
                            sd.setStockCode(s.getKey().getStockCode());
                            sd.setStockName(s.getStockName());
                            sd.setUserCode(s.getUserCode());
                            sd.setRelName(s.getRelName());
                            sd.setQty(1.0);
                            sd.setStdWeight(Util1.getDouble(s.getWeight()));
                            sd.setWeight(Util1.getDouble(s.getWeight()));
                            sd.setWeightUnit(s.getWeightUnit());
                            sd.setUnitCode(s.getSaleUnitCode());
                            sd.setStock(s);
                            sd.setPrice(Util1.getFloat(sd.getPrice()) == 0 ? s.getSalePriceN() : sd.getPrice());
                            parent.setColumnSelectionInterval(4, 4);
                            addNewRow();
                            observer.selected("STOCK-INFO", "STOCK-INFO");
                        }
                    }
                    case 3 -> {
                        //Loc
                        if (value instanceof Location l) {
                            sd.setLocCode(l.getKey().getLocCode());
                            sd.setLocName(l.getLocName());

                        }
                    }
                    case 4 -> {
                        sd.setWeight(Util1.getDouble(value));
                    }
                    case 5 -> {
                        if (value instanceof StockUnit u) {
                            sd.setWeightUnit(u.getKey().getUnitCode());
                        }
                    }
                    case 6 -> {
                        //Qty
                        if (Util1.isNumber(value)) {
                            if (Util1.isPositive(Util1.getFloat(value))) {
                                sd.setQty(Util1.getDouble(value));
                                if (sd.getUnitCode() == null) {
                                    parent.setColumnSelectionInterval(7, 7);
                                } else {
                                    parent.setColumnSelectionInterval(9, 9);
                                }
                            } else {
                                showMessageBox("Input value must be positive");
                                parent.setColumnSelectionInterval(column, column);
                            }
                        } else {
                            showMessageBox("Input value must be number.");
                            parent.setColumnSelectionInterval(column, column);
                        }
                    }
                    case 7 -> {
                        //Unit
                        if (value instanceof StockUnit stockUnit) {
                            sd.setUnitCode(stockUnit.getKey().getUnitCode());
                        }
                    }
                    case 8 -> {
                        sd.setStdWeight(Util1.getDouble(value));
                    }

                    case 10 -> {
                        //price
                        if (Util1.isNumber(value)) {
                            if (Util1.isPositive(Util1.getFloat(value))) {
                                sd.setPrice(Util1.getDouble(value));
                                parent.setColumnSelectionInterval(0, 0);
                                parent.setRowSelectionInterval(row + 1, row + 1);
                            } else {
                                showMessageBox("Input value must be positive");
                                parent.setColumnSelectionInterval(column, column);
                            }
                        } else {
                            showMessageBox("Input value must be number.");
                            parent.setColumnSelectionInterval(column, column);
                        }
                    }
                    case 11 -> {
                        //amt
                        sd.setAmount(Util1.getDouble(value));

                    }

                }
                if (column != 10) {
                    if (Util1.getFloat(sd.getPrice()) == 0) {
                        if (ProUtil.isSaleLastPrice()) {
                            if (sd.getStockCode() != null && sd.getUnitCode() != null) {
                                inventoryRepo.getSaleRecentPrice(sd.getStockCode(),
                                        Util1.toDateStr(vouDate.getDate(), "yyyy-MM-dd"), sd.getUnitCode()).subscribe((t) -> {
                                    sd.setPrice(Util1.getDouble(t.getAmount()));
                                });
                            }
                        }
                    }
                }
                assignLocation(sd);
                calculateAmount(sd);
                fireTableRowsUpdated(row, row);
                setRecord(listDetail.size() - 1);
                observer.selected("SALE-TOTAL", "SALE-TOTAL");
                parent.requestFocusInWindow();
            }
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
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

    public void setListDetail(List<SaleHisDetail> listDetail) {
        this.listDetail = listDetail;
        setRecord(listDetail.size());
        addNewRow();
        fireTableDataChanged();
    }

    private void setRecord(int size) {
        lblRecord.setText("Records : " + size);
    }

    public void removeListDetail() {
        this.listDetail.clear();
        addNewRow();
    }

    private void calculateAmount(SaleHisDetail s) {
        double price = Util1.getDouble(s.getPrice());
        double stdWt = Util1.getDouble(s.getStdWeight());
        double wt = Util1.getDouble(s.getWeight());
        double qty = Util1.getDouble(s.getQty());
        if (s.getStockCode() != null) {
            double amount = (qty * wt * price) / stdWt;
            s.setAmount(amount);
        }
        if (s.getQty() != null && s.getWeight() != null) {
            s.setTotalWeight(Util1.getDouble(s.getQty()) * Util1.getDouble(s.getWeight()));
        }
    }

    private void showMessageBox(String text) {
        JOptionPane.showMessageDialog(Global.parentForm, text);
    }

    public boolean isValidEntry() {
        boolean status = true;
        for (SaleHisDetail sdh : listDetail) {
            if (sdh.getStockCode() != null) {
                if (Util1.getFloat(sdh.getAmount()) <= 0) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Amount.",
                            "Invalid.", JOptionPane.ERROR_MESSAGE);
                    status = false;
                    parent.requestFocus();
                    break;
                } else if (sdh.getLocCode() == null) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Location.");
                    status = false;
                    parent.requestFocus();
                    break;
                } else if (sdh.getUnitCode() == null) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Sale Unit.");
                    status = false;
                    parent.requestFocus();
                    break;
                }
            }
        }
        return status;
    }

    public List<SaleDetailKey> getDelList() {
        return deleteList;
    }

    public void clearDelList() {
        if (deleteList != null) {
            deleteList.clear();
        }
    }

    public void delete(int row) {
        SaleHisDetail sdh = listDetail.get(row);
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
        }
    }
}
