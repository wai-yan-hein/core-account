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
import com.inventory.ui.entry.dialog.UnitChooser;
import com.toedter.calendar.JDateChooser;
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
public class PurchaseTableModel extends AbstractTableModel {

    private String[] columnNames = {"Code", "Description", "Relation",
        "Qty", "Unit", "Price", "Amount", "Location"};
    @Setter
    private JTable parent;
    private List<PurHisDetail> listDetail = new ArrayList();
    @Setter
    private SelectionObserver observer;
    private final List<PurDetailKey> deleteList = new ArrayList();
    @Setter
    private InventoryRepo inventoryRepo;
    @Setter
    private JDateChooser vouDate;
    @Setter
    private JLabel lblRec;
    @Setter
    private PurchaseDynamic purchase;

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
            case 3, 5, 6 ->
                Double.class;
            default ->
                String.class;
        };
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return switch (column) {
            case 2, 6 ->
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
                        return record.getRelName();
                    }
                    case 3 -> {
                        //qty
                        return Util1.toNull(record.getQty());
                    }
                    case 4 -> {
                        //unit
                        return record.getUnitCode();
                    }
                    case 5 -> {
                        //price
                        return Util1.toNull(record.getPrice());
                    }
                    case 6 -> {
                        //amount
                        return Util1.toNull(record.getAmount());
                    }
                    case 7 -> {
                        //loc
                        return record.getLocName();
                    }
                    default -> {
                        return new Object();
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
                            record.setRelCode(s.getRelCode());
                            record.setRelName(s.getRelName());
                            record.setUnitCode(s.getPurUnitCode());
                            addNewRow();
                        }
                    }
                    setSelection(row, 3);
                }
                case 3 -> {
                    //Qty
                    double qty = Util1.getDouble(value);
                    if (qty > 0) {
                        record.setQty(qty);
                        String relCode = record.getRelCode();
                        if (!Util1.isNullOrEmpty(relCode)) {
                            UnitChooser chooser = new UnitChooser(inventoryRepo, relCode);
                            record.setUnitCode(chooser.getSelectUnit());
                        }
                        if (record.getUnitCode() == null) {
                            setSelection(row, 4);
                        } else {
                            double price = record.getPrice();
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
                    if (value != null) {
                        if (value instanceof StockUnit u) {
                            record.setUnitCode(u.getKey().getUnitCode());
                        }
                    }
                    parent.setColumnSelectionInterval(6, 6);
                }
                case 5 -> {
                    //Pur Price
                    double price = Util1.getDouble(value);
                    if (price > 0) {
                        record.setPrice(Util1.getDouble(value));
                        record.setOrgPrice(record.getPrice());
                        setSelection(row + 1, 0);
                    }
                }
                case 6 -> {
                    //Amount
                    if (value != null) {
                        record.setAmount(Util1.getDouble(value));
                    }
                }
                case 7 -> {
                    //Loc
                    if (value instanceof Location l) {
                        record.setLocCode(l.getKey().getLocCode());
                        record.setLocName(l.getLocName());
                        setSelection(row + 1, column);
                    }
                }
            }
            if (column != 5) {
                if (Util1.getDouble(record.getPrice()) == 0) {
                    String stockCode = record.getStockCode();
                    if (stockCode != null && record.getUnitCode() != null) {
                        inventoryRepo.getPurRecentPrice(stockCode,
                                Util1.toDateStr(vouDate.getDate(), "yyyy-MM-dd"), record.getUnitCode()).doOnSuccess((t) -> {
                            if (t != null) {
                                record.setPrice(Util1.getDouble(t.getAmount()));
                                calculateAmount(record);
                                fireTableRowsUpdated(row, row);
                            }
                        }).subscribe();
                    }
                }
            }
            assignLocation(record);
            calculateAmount(record);
            setRecord(listDetail.size() - 1);
            fireTableRowsUpdated(row, row);
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
        if (lblRec != null) {
            lblRec.setText("Records : " + size);
        }
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
        fireTableDataChanged();
    }

    private void calculateAmount(PurHisDetail pur) {
        double price = Util1.getDouble(pur.getPrice());
        double qty = Util1.getDouble(pur.getQty());
        if (pur.getStockCode() != null) {
            double amount = qty * price;
            pur.setPrice(price);
            pur.setAmount(Util1.getDouble(Math.round(amount)));
        }
        observer.selected("CAL-TOTAL", "CAL-TOTAL");
    }

    private void showMessageBox(String text) {
        JOptionPane.showMessageDialog(Global.parentForm, text);
    }

    public boolean isValidEntry() {
        boolean status = true;
        for (PurHisDetail sdh : listDetail) {
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
                } else if (sdh.getUnitCode() == null) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Purchase Unit.");
                    status = false;
                    parent.requestFocus();
                }
            }
        }
        return status;
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
            fireTableDataChanged();
        }
    }

    public void updateRow(int row) {
        fireTableRowsUpdated(row, row);
    }
}
