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
import com.inventory.ui.entry.Purchase;
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
public class PurchaseTableModel extends AbstractTableModel {

    private String[] columnNames = {"Code", "Description", "Relation", "Location",
        "Qty", "Unit", "Price", "Amount"};
    private JTable parent;
    private List<PurHisDetail> listDetail = new ArrayList();
    private SelectionObserver selectionObserver;
    private final List<PurDetailKey> deleteList = new ArrayList();
    private InventoryRepo inventoryRepo;
    private JDateChooser vouDate;
    private JLabel lblRec;
    private Purchase purchase;

    public Purchase getPurchase() {
        return purchase;
    }

    public void setPurchase(Purchase purchase) {
        this.purchase = purchase;
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

    public JTable getParent() {
        return parent;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    public SelectionObserver getSelectionObserver() {
        return selectionObserver;
    }

    public void setObserver(SelectionObserver selectionObserver) {
        this.selectionObserver = selectionObserver;
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
            case 4, 6, 7 ->
                Double.class;
            default ->
                String.class;
        };
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return switch (column) {
            case 2, 7 ->
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
                        //loc
                        return record.getLocName();
                    }
                    case 4 -> {
                        //qty
                        return Util1.toNull(record.getQty());
                    }
                    case 5 -> {
                        //unit
                        return record.getUnitCode();
                    }
                    case 6 -> {
                        //price
                        return Util1.toNull(record.getPrice());
                    }
                    case 7 -> {
                        //amount
                        return Util1.toNull(record.getAmount());
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
                            record.setRelName(s.getRelName());
                            record.setQty(1.0);
                            record.setUnitCode(s.getPurUnitCode());
                            addNewRow();
                            selectionObserver.selected("STOCK-INFO", "STOCK-INFO");
                        }
                    }
                    parent.setColumnSelectionInterval(4, 4);
                }
                case 3 -> {
                    //Loc
                    if (value instanceof Location l) {
                        record.setLocCode(l.getKey().getLocCode());
                        record.setLocName(l.getLocName());
                    }
                }
                case 4 -> {
                    //Qty
                    if (Util1.isNumber(value)) {
                        if (Util1.isPositive(Util1.getDouble(value))) {
                            record.setQty(Util1.getDouble(value));
                        } else {
                            showMessageBox("Input value must be positive");
                            parent.setColumnSelectionInterval(column, column);
                        }
                    } else {
                        showMessageBox("Input value must be number.");
                        parent.setColumnSelectionInterval(column, column);
                    }
                    parent.setRowSelectionInterval(row, row);
                }
                case 5 -> {
                    //Unit
                    if (value != null) {
                        if (value instanceof StockUnit u) {
                            record.setUnitCode(u.getKey().getUnitCode());
                        }
                    }
                    parent.setColumnSelectionInterval(6, 6);
                }
                case 6 -> {
                    //Pur Price
                    if (Util1.isNumber(value)) {
                        if (Util1.isPositive(Util1.getDouble(value))) {
                            record.setPrice(Util1.getDouble(value));
                            record.setOrgPrice(record.getPrice());
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
                case 7 -> {
                    //Amount
                    if (value != null) {
                        record.setAmount(Util1.getDouble(value));
                    }
                }
            }
            if (column != 6) {
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
            selectionObserver.selected("CAL-TOTAL", "CAL-TOTAL");
            parent.requestFocusInWindow();
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
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
        //setRecord(listDetail.size());
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
