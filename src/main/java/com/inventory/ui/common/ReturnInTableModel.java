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
import com.inventory.model.RetInHisDetail;
import com.inventory.model.RetInKey;
import com.inventory.model.Stock;
import com.inventory.model.StockUnit;
import com.inventory.ui.entry.ReturnIn;
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
public class ReturnInTableModel extends AbstractTableModel {

    private String[] columnNames = {"Code", "Description", "Relation", "Location",
        "Qty", "Unit", "Weight", "Weight Unit", "Price", "Amount"};
    private JTable parent;
    private List<RetInHisDetail> listDetail = new ArrayList();
    private SelectionObserver selectionObserver;
    private final List<RetInKey> deleteList = new ArrayList();
    private InventoryRepo inventoryRepo;
    private JDateChooser vouDate;
    private JLabel lblRec;
    private ReturnIn returnIn;

    public ReturnIn getReturnIn() {
        return returnIn;
    }

    public void setReturnIn(ReturnIn returnIn) {
        this.returnIn = returnIn;
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

    public void setSelectionObserver(SelectionObserver selectionObserver) {
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
        return listDetail.size();
    }

    @Override
    public Class getColumnClass(int column) {
        return switch (column) {
            case 4, 6, 8, 9 ->
                Float.class;
            default ->
                String.class;
        };
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return switch (column) {
            case 5 ->
                ProUtil.isWeightOption();
            default ->
                column != 2;
        };
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            RetInHisDetail record = listDetail.get(row);
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
                    return record.getQty();
                }
                case 5 -> {
                    //unit
                    return record.getUnitCode();
                }
                case 6 -> {
                    //weight
                    return record.getWeight();
                }
                case 7 -> {
                    //weight unit
                    return record.getWeightUnit();
                }
                case 8 -> {
                    //price
                    return record.getPrice();
                }
                case 9 -> {
                    //amount
                    return record.getAmount();
                }
                default -> {
                    return new Object();
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
            if (value != null) {
                RetInHisDetail record = listDetail.get(row);
                switch (column) {
                    case 0, 1 -> {
                        //Code
                        if (value instanceof Stock s) {
                            record.setStockCode(s.getKey().getStockCode());
                            record.setStockName(s.getStockName());
                            record.setUserCode(s.getUserCode());
                            record.setRelName(s.getRelName());
                            record.setQty(1.0f);
                            record.setUnitCode(s.getPurUnitCode());
                            addNewRow();
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
                            if (Util1.isPositive(Util1.getFloat(value))) {
                                record.setQty(Util1.getFloat(value));
                                parent.setColumnSelectionInterval(6, 6);
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
                        if (value instanceof StockUnit s) {
                            record.setUnitCode(s.getKey().getUnitCode());
                        }
                        parent.setColumnSelectionInterval(8, 8);
                    }
                    case 6 -> {
                        if (Util1.isNumber(value)) {
                            if (Util1.isPositive(Util1.getFloat(value))) {
                                record.setWeight(Util1.getFloat(value));
                                parent.setColumnSelectionInterval(7, 7);
                            } else {
                                showMessageBox("Input value must be positive");
                                parent.setColumnSelectionInterval(column, column);
                            }
                        } else {
                            showMessageBox("Input value must be number");
                            parent.setColumnSelectionInterval(column, column);
                        }
                        parent.setRowSelectionInterval(row, row);
                        parent.setColumnSelectionInterval(7, 7);
                    }
                    case 7 -> {
                        if (value instanceof StockUnit unit) {
                            record.setWeightUnit(unit.getKey().getUnitCode());
                        }
                        parent.setColumnSelectionInterval(8, 8);
                    }
                    case 8 -> {
                        // Price
                        if (Util1.isNumber(value)) {
                            if (Util1.isPositive(Util1.getFloat(value))) {
                                record.setPrice(Util1.getFloat(value));
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
                    case 9 -> {
                        //Amount
                        record.setAmount(Util1.getFloat(value));

                    }
                }
                if (column != 8) {
                    if (Util1.getFloat(record.getPrice()) == 0) {
                        if (record.getStockCode() != null && record.getUnitCode() != null) {
                            inventoryRepo.getSaleRecentPrice(record.getStockCode(),
                                    Util1.toDateStr(vouDate.getDate(), "yyyy-MM-dd"), record.getUnitCode()).subscribe((t) -> {
                                record.setPrice(t.getAmount());
                            });
                        }
                    }
                }
                assignLocation(record);
                calculateAmount(record);
                setRecord(listDetail.size() - 1);
                fireTableRowsUpdated(row, row);
                selectionObserver.selected("SALE-TOTAL", "SALE-TOTAL");
                parent.requestFocusInWindow();
            }
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }

    private void assignLocation(RetInHisDetail sd) {
        if (sd.getLocCode() == null) {
            LocationAutoCompleter completer = returnIn.getLocationAutoCompleter();
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
                RetInHisDetail pd = new RetInHisDetail();
                listDetail.add(pd);
                fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
            }
        }
    }

    private boolean hasEmptyRow() {
        boolean status = false;
        if (listDetail.size() >= 1) {
            RetInHisDetail get = listDetail.get(listDetail.size() - 1);
            if (get.getStockCode() == null) {
                status = true;
            }
        }
        return status;
    }

    public List<RetInHisDetail> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<RetInHisDetail> listDetail) {
        this.listDetail = listDetail;
        setRecord(listDetail.size());
        fireTableDataChanged();
    }

    private void calculateAmount(RetInHisDetail ri) {
        float price = Util1.getFloat(ri.getPrice());
        float avgQty = Util1.getFloat(ri.getQty());
        if (ri.getStockCode() != null) {
            float amount = avgQty * price;
            ri.setPrice(price);
            ri.setAmount(Util1.getFloat(Math.round(amount)));
        }
    }

    private void showMessageBox(String text) {
        JOptionPane.showMessageDialog(Global.parentForm, text);
    }

    public boolean isValidEntry() {
        boolean status = true;
        for (RetInHisDetail sdh : listDetail) {
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
                } else if (sdh.getUnitCode() == null) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Stock Unit.");
                    status = false;
                    parent.requestFocus();
                }
            }
        }
        return status;
    }

    public List<RetInKey> getDelList() {
        return deleteList;
    }

    public void clearDelList() {
        if (deleteList != null) {
            deleteList.clear();
        }
    }

    public void delete(int row) {
        RetInHisDetail sdh = listDetail.get(row);
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

    public void addSale(RetInHisDetail sd) {
        if (listDetail != null) {
            listDetail.add(sd);
            fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
        }
    }

    public void clear() {
        if (listDetail != null) {
            listDetail.clear();
            fireTableDataChanged();
        }
    }

}
