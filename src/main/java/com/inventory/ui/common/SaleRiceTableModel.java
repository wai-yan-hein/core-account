/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.common.Global;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.model.Location;
import com.inventory.model.SaleDetailKey;
import com.inventory.model.SaleHisDetail;
import com.inventory.model.Stock;
import com.inventory.ui.entry.SaleDynamic;
import com.inventory.ui.entry.dialog.StockBalanceFrame;
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
public class SaleRiceTableModel extends AbstractTableModel {

    private String[] columnNames = {"Code", "Description", "Std-Weight", "Avg-Weight", "Qty", "Total Weight", "Price", "Amount"};
    private JTable parent;
    private List<SaleHisDetail> listDetail = new ArrayList();
    private SelectionObserver observer;
    private final List<SaleDetailKey> deleteList = new ArrayList();
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

    public void setLblRecord(JLabel lblRecord) {
        this.lblRecord = lblRecord;
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
            case 0, 1 ->
                String.class;
            default ->
                Double.class;
        };
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return switch (column) {
            case 7 ->
                false;
            default ->
                true;
        };
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
                    return sd.getStockName();
                }
                case 2 -> {
                    return Util1.toNull(sd.getWeight());
                }
                case 3 -> {
                    return Util1.toNull(sd.getStdWeight());
                }
                case 4 -> {
                    //qty
                    return Util1.toNull(sd.getQty());
                }
                case 5 -> {
                    return Util1.toNull(sd.getTotalWeight());
                }
                case 6 -> {
                    //price
                    return Util1.toNull(sd.getPrice());
                }
                case 7 -> {
                    //amount
                    return Util1.toNull(sd.getAmount());
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
                            dialog.calStock(s.getKey().getStockCode(), Global.parentForm);
                            sd.setStockCode(s.getKey().getStockCode());
                            sd.setStockName(s.getStockName());
                            sd.setUserCode(s.getUserCode());
                            sd.setRelName(s.getRelName());
                            sd.setQty(1.0);
                            sd.setWeight(Util1.getDouble(s.getWeight()));
                            sd.setWeightUnit(s.getWeightUnit());
                            sd.setUnitCode(Util1.isNull(s.getSaleUnitCode(), "-"));
                            sd.setStock(s);
                            sd.setPrice(Util1.getDouble(sd.getPrice()) == 0 ? s.getSalePriceN() : sd.getPrice());
                            setSelection(row, 2);
                            addNewRow();
                            observer.selected("STOCK-INFO", "STOCK-INFO");
                        }
                    }
                    case 2 -> {
                        sd.setWeight(Util1.getDouble(value));
                    }
                    case 3 -> {
                        sd.setStdWeight(Util1.getDouble(value));
                    }
                    case 4 -> {
                        //Qty
                        if (Util1.isNumber(value)) {
                            if (Util1.isPositive(Util1.getDouble(value))) {
                                sd.setQty(Util1.getDouble(value));
                                if (sd.getUnitCode() == null) {
                                    setSelection(row, 6);
                                } else {
                                    setSelection(row, 7);
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
                    case 6 -> {
                        //price
                        if (Util1.isNumber(value)) {
                            if (Util1.isPositive(Util1.getDouble(value))) {
                                sd.setPrice(Util1.getDouble(value));
                                sd.setOrgPrice(Util1.getDouble(value));
                                setSelection(row + 1, 0);
                            } else {
                                showMessageBox("Input value must be positive");
                                setSelection(row, column);
                            }
                        } else {
                            showMessageBox("Input value must be number.");
                            setSelection(row, column);
                        }
                    }
                    case 7 -> {
                        //amt
                        sd.setAmount(Util1.getDouble(value));
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

    private void setSelection(int row, int column) {
        parent.setRowSelectionInterval(row, row);
        parent.setColumnSelectionInterval(column, column);
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
    }

    private void calculateAmount(SaleHisDetail s) {
        double orgPrice = Util1.getDouble(s.getOrgPrice());
        double qty = Util1.getDouble(s.getQty());
        double weight = s.getWeight();
        double stdWt = s.getStdWeight();
        if (s.getStockCode() != null && weight > 0 && orgPrice > 0) {
            stdWt = stdWt == 0 ? weight : stdWt;
            double price = stdWt / weight * orgPrice;
            double amount = qty * price;
            s.setPrice(price);
            s.setAmount(amount);
        }
        if (s.getQty() > 0 && s.getWeight() > 0) {
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
                if (Util1.getDouble(sdh.getAmount()) <= 0) {
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
