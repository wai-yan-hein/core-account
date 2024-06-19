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
import com.inventory.entity.PurDetailKey;
import com.inventory.entity.PurHisDetail;
import com.inventory.entity.Stock;
import com.inventory.entity.StockUnit;
import com.inventory.ui.entry.PurchaseDynamic;
import com.toedter.calendar.JDateChooser;
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
public class PurchaseExportTableModel extends AbstractTableModel {

    private String[] columnNames = {"Code", "Description", "Location", "Length", "Width", "Moisturizer",
        "Weight", "Weight Unit", "Qty", "Unit", "Total Weight", "Price", "Amount"};
    private JTable parent;
    private List<PurHisDetail> listDetail = new ArrayList();
    private SelectionObserver observer;
    private final List<PurDetailKey> deleteList = new ArrayList();
    private InventoryRepo inventoryRepo;
    private JDateChooser vouDate;
    private JLabel lblRec;
    private PurchaseDynamic purchase;

    public void setPurchase(PurchaseDynamic purchase) {
        this.purchase = purchase;
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
        if (listDetail == null) {
            return 0;
        }
        return listDetail.size();
    }

    @Override
    public Class getColumnClass(int column) {
        return switch (column) {
            case 3, 4, 6, 8, 10, 11, 12 ->
                Double.class;
            default ->
                String.class;
        };
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return switch (column) {
            case 13 ->
                false;
            default ->
                true;
        };
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            if (!listDetail.isEmpty()) {
                PurHisDetail record = listDetail.get(row);
                switch (column) {
                    case 0 -> {
                        //code
                        return record.getUserCode() == null ? record.getStockCode() : record.getUserCode();
                    }
                    case 1 -> {
                        //Name
                        String stockName = null;
                        if (record.getStockCode() != null) {
                            stockName = record.getStockName();
                            if (ProUtil.isStockNameWithCategory()) {
                                if (record.getCatName() != null) {
                                    stockName = String.format("%s (%s)", stockName, record.getCatName());
                                }
                            }
                        }
                        return stockName;
                    }
                    case 2 -> {
                        return record.getLocName();
                    }
                    case 3 -> {
                        return Util1.toNull(record.getLength());
                    }
                    case 4 -> {
                        return Util1.toNull(record.getWidth());
                    }
                    case 5 -> {
                        return record.getMPercent();
                    }
                    case 6 -> {
                        return Util1.getDouble(record.getWeight()) == 0 ? null : record.getWeight();
                    }
                    case 7 -> {
                        //unit
                        return record.getWeightUnit();
                    }
                    case 8 -> {
                        return Util1.toNull(record.getQty());
                    }
                    case 9 -> {
                        return record.getUnitCode();
                    }
                    //total
                    case 10 -> {
                        return Util1.getFloat(record.getTotalWeight()) == 0 ? null : record.getTotalWeight();
                    }
                    case 11 -> {
                        return Util1.getFloat(record.getPrice()) == 0 ? null : record.getPrice();
                    }
                    case 12 -> {
                        return Util1.getFloat(record.getAmount()) == 0 ? null : record.getAmount();
                    }
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
            PurHisDetail record = listDetail.get(row);
            switch (column) {
                case 0, 1 -> {
                    //Code
                    if (value != null) {
                        if (value instanceof Stock s) {
                            record.setStockCode(s.getKey().getStockCode());
                            record.setStockName(s.getStockName());
                            record.setUserCode(s.getUserCode());
                            record.setRelName(s.getRelName());
                            record.setQty(1.0);
                            record.setUnitCode(s.getPurUnitCode());
                            record.setWeightUnit(s.getWeightUnit());
                            record.setWeight(Util1.getDouble(s.getWeight()));
                            record.setStdWeight(Util1.getDouble(s.getWeight()));
                            addNewRow();
                            setSelection(row, 3);
                            observer.selected("STOCK-INFO", "STOCK-INFO");
                        }
                    }
                }
                case 2 -> {
                    //Loc
                    if (value instanceof Location l) {
                        record.setLocCode(l.getKey().getLocCode());
                        record.setLocName(l.getLocName());
                    }
                }
                case 3 -> {
                    //length
                    double len = Util1.getDouble(value);
                    if (len > 0) {
                        record.setLength(Util1.getDouble(value));
                    }
                }
                case 4 -> {
                    //width
                    double wid = Util1.getDouble(value);
                    if (wid > 0) {
                        record.setWidth(wid);
                    }
                }
                case 5 -> {
                    //mo
                    record.setMPercent(value.toString());
                }

                case 6 -> {
                    //weight
                    double wid = Util1.getDouble(value);
                    if (wid > 0) {
                        record.setWeight(wid);
                    }

                }
                case 7 -> {
                    //weight unit
                    if (value instanceof StockUnit u) {
                        record.setWeightUnit(u.getKey().getUnitCode());
                    }
                }
                case 8 -> {
                    //Qty
                    double qty = Util1.getDouble(value);
                    if (qty > 0) {
                        record.setQty(Util1.getDouble(value));
                        parent.setRowSelectionInterval(row, row);
                    }
                }
                case 9 -> {
                    //Unit
                    if (value != null) {
                        if (value instanceof StockUnit u) {
                            record.setUnitCode(u.getKey().getUnitCode());
                            setSelection(row, 8);
                        }
                    }
                }
                case 11 -> {
                    //Pur Price
                    double price = Util1.getDouble(value);
                    if (price > 0) {
                        record.setPrice(Util1.getDouble(value));
                        record.setOrgPrice(record.getPrice());
                        setSelection(row + 1, 0);
                    }
                }
                case 12 -> {
                    //Amount
                    if (value != null) {
                        record.setAmount(Util1.getDouble(value));
                    }
                }
            }
            if (column != 11) {
                if (Util1.getDouble(record.getPrice()) == 0) {
                    String stockCode = record.getStockCode();
                    if (stockCode != null && record.getUnitCode() != null) {
                        inventoryRepo.getPurRecentPrice(stockCode,
                                Util1.toDateStr(vouDate.getDate(), "yyyy-MM-dd"), record.getUnitCode()).doOnSuccess((t) -> {
                            record.setPrice(Util1.getDouble(t.getAmount()));
                            calculateAmount(record);
                            fireTableRowsUpdated(row, row);
                        }).subscribe();
                    }
                }
            }
            assignLocation(record);
            calculateAmount(record);
            setRecord(listDetail.size() - 1);
            fireTableRowsUpdated(row, row);
            observer.selected("CAL-TOTAL", "CAL-TOTAL");
            parent.requestFocusInWindow();
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }

    private void setSelection(int row, int column) {
        parent.setRowSelectionInterval(row, row);
        parent.setColumnSelectionInterval(column, column);
    }

    private void assignLocation(PurHisDetail sd) {
        if (sd.getLocCode() == null) {
            LocationAutoCompleter completer = purchase.getLocationAutoCompleter();
            if (completer != null) {
                Location l = completer.getLocation();
                if (l != null) {
                    sd.setLocCode(l.getKey().getLocCode());
                    sd.setLocName(l.getLocName());
                }
            }
        }
    }

    private void setRecord(int size) {
        lblRec.setText("Records : " + size);
    }

    public void addNewRow() {
        if (listDetail != null) {
            if (!hasEmptyRow()) {
                PurHisDetail pd = new PurHisDetail();
                listDetail.add(pd);
                fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
            }
        }
    }

    private boolean hasEmptyRow() {
        boolean status = false;
        if (listDetail.size() >= 1) {
            PurHisDetail get = listDetail.get(listDetail.size() - 1);
            if (get.getStockCode() == null) {
                status = true;
            }
        }
        return status;
    }

    public List<PurHisDetail> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<PurHisDetail> listDetail) {
        this.listDetail = listDetail;
        setRecord(listDetail.size());
        fireTableDataChanged();
    }

    private void calculateAmount(PurHisDetail pur) {
        double price = Util1.getDouble(pur.getPrice());
        double wt = Util1.getDouble(pur.getWeight());
        double qty = Util1.getDouble(pur.getQty());
        double stdWt = Util1.getDouble(pur.getStdWeight());
        if (pur.getStockCode() != null) {
            double amount = Math.round((qty * wt * price) / stdWt);
//            double amount = Math.round(qty * price);
            pur.setAmount(amount);
        }
        if (pur.getQty() > 0 && pur.getWeight() > 0) {
            pur.setTotalWeight(Util1.getDouble(pur.getQty()) * Util1.getDouble(pur.getWeight()));
        }
    }

    private void showMessageBox(String text) {
        JOptionPane.showMessageDialog(Global.parentForm, text);
    }

    public boolean isValidEntry() {
        boolean status = true;
        for (int i = 0; i < listDetail.size(); i++) {
            PurHisDetail sdh = listDetail.get(i);
            if (sdh.getStockCode() != null) {
                if (Util1.getDouble(sdh.getAmount()) <= 0) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Amount.",
                            "Invalid.", JOptionPane.ERROR_MESSAGE);
                    focusTable(i);
                    return false;
                } else if (sdh.getLocCode() == null) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Location.");
                    focusTable(i);
                    return false;
                } else if (sdh.getUnitCode() == null) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Purchase Unit.");
                    focusTable(i);
                    return false;
                } else if (sdh.getStdWeight() == 0) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Std Weight");
                    focusTable(i);
                    return false;
                } else if (sdh.getWeight() == 0) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Weight");
                    focusTable(i);
                    return false;
                } else if (sdh.getWeightUnit() == null) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Weight Unit");
                    focusTable(i);
                    return false;
                }
            }
        }
        return status;
    }

    private void focusTable(int row) {
        parent.setRowSelectionInterval(row, row);
        parent.setColumnSelectionInterval(0, 0);
        parent.requestFocus();
    }

    public List<PurDetailKey> getDelList() {
        return deleteList;
    }

    public void clearDelList() {
        if (deleteList != null) {
            deleteList.clear();
        }
    }

    public void delete(int row) {
        PurHisDetail sdh = listDetail.get(row);
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

    public void addPurchase(PurHisDetail sd) {
        if (listDetail != null) {
            listDetail.add(sd);
            fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
        }
    }

    public PurHisDetail getObject(int row) {
        return listDetail.get(row);
    }

    public void clear() {
        if (listDetail != null) {
            listDetail.clear();
            addNewRow();
            fireTableDataChanged();
        }
    }

    public void updateRow(int row) {
        fireTableRowsUpdated(row, row);
    }
}
