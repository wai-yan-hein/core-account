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
import com.inventory.model.OPHisDetail;
import com.inventory.model.OPHisDetailKey;
import com.inventory.model.Stock;
import com.inventory.model.StockUnit;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author wai yan
 */
@Slf4j
public class OpeningTableModel extends AbstractTableModel {

    private String[] columnNames = {"Stock Code", "Stock Name", "Relation",
        "Weight", "Weight Unit", "Qty", "Unit", "Price", "Amount"};
    private JTable parent;
    private List<OPHisDetail> listDetail = new ArrayList();
    private SelectionObserver observer;
    private final List<OPHisDetailKey> deleteList = new ArrayList();

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
            case 3, 5, 7, 8 ->
                Double.class;
            default ->
                String.class;
        };
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        switch (column) {
            case 2, 8 -> {
                return false;
            }
        }
        return true;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            OPHisDetail record = listDetail.get(row);
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
                    return Util1.getDouble(record.getWeight()) == 0 ? null : record.getWeight();
                }
                case 4 -> {
                    return record.getWeightUnit();
                }

                case 5 -> {
                    //qty
                    return record.getQty();
                }

                case 6 -> {
                    //unit
                    return record.getUnitCode();
                }

                case 7 -> {
                    //price
                    return Util1.getDouble(record.getPrice()) == 0 ? null : record.getPrice();
                }
                case 8 -> {
                    //amount
                    return Util1.getDouble(record.getAmount()) == 0 ? null : record.getAmount();
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
            OPHisDetail record = listDetail.get(row);
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
                            record.setWeight(s.getWeight());
                            record.setWeightUnit(s.getWeightUnit());
                            addNewRow();
                        }
                    }
                    if (ProUtil.isUseWeight()) {
                        setSelection(row, 3);
                    } else {
                        setSelection(row, 5);
                    }
                }
                case 3 -> { // weight
                    if (value != null) {
                        if (Util1.isNumber(value)) {
                            if (Util1.isPositive(Util1.getDouble(value))) {
                                record.setWeight(Util1.getDouble(value));
                            } else {
                                showMessageBox("Input value must be positive");
                                setSelection(row, column);
                            }
                        } else {
                            showMessageBox("Input value must be number");
                            setSelection(row, column);
                        }
                    }
                }
                case 4 -> {
                    if (value != null) {
                        if (value instanceof StockUnit unit) {
                            record.setWeightUnit(unit.getKey().getUnitCode());
                            setSelection(row, 7);
                        }
                    }
                }
                case 5 -> {
                    // Qty
                    if (Util1.isNumber(value)) {
                        if (Util1.isPositive(Util1.getDouble(value))) {
                            if (ProUtil.isUseWeightPoint()) {
                                String str = String.valueOf(value);
                                double wt = Util1.getDouble(record.getWeight());
                                record.setQty(Util1.getDouble(value));
                                record.setTotalWeight(Util1.getTotalWeight(wt, str));
                                setSelection(row, 5);
                            } else {
                                record.setQty(Util1.getDouble(value));
                                record.setTotalWeight(Util1.getDouble(record.getQty()) * Util1.getDouble(record.getWeight()));
                            }

                        } else {
                            showMessageBox("Input value must be positive");
                            setSelection(row, column);
                        }
                    } else {
                        showMessageBox("Input value must be number.");
                        setSelection(row, column);
                    }
                    setSelection(row, 5);
                }
                case 6 -> {
                    //Unit
                    if (value != null) {
                        if (value instanceof StockUnit unit) {
                            record.setUnitCode(unit.getKey().getUnitCode());
                            setSelection(row, 6);
                        }
                    }
                }

                case 7 -> {
                    // Price
                    if (Util1.isNumber(value)) {
                        if (Util1.isPositive(Util1.getDouble(value))) {
                            record.setPrice(Util1.getDouble(value));
                            setSelection(row + 1, 0);
                            calculateAmount(record);
                        } else {
                            showMessageBox("Input value must be positive");
                            setSelection(row, column);
                        }
                    } else {
                        showMessageBox("Input value must be number.");
                        setSelection(row, column);
                    }
                }
                case 8 -> {
                    //Amount
                    if (value != null) {
                        if (Util1.isPositive(Util1.getDouble(value))) {
                            record.setAmount(Util1.getDouble(value));
                            setSelection(row + 1, 0);
                            calculatePrice(record);
                        } else {
                            showMessageBox("Input value must be number.");
                        }
                    }
                }
            }
            calculateAmount(record);
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

    public boolean isValidEntry() {
        boolean status = true;
        for (OPHisDetail op : listDetail) {
            op.setAmount(Util1.getDouble(op.getAmount()));
            op.setPrice(Util1.getDouble(op.getPrice()));
            op.setQty(Util1.getDouble(op.getQty()));
            if (op.getStockCode() != null) {
                if (op.getUnitCode() == null) {
                    status = false;
                    JOptionPane.showMessageDialog(parent, "Invalid Unit.");
                }
            }
        }
        return status;
    }

    public void addNewRow() {
        if (listDetail != null) {
            if (hasEmptyRow()) {
                OPHisDetail pd = new OPHisDetail();
                listDetail.add(pd);
                fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
            }
        }
    }

    private boolean hasEmptyRow() {
        boolean status = true;
        if (listDetail.size() > 1) {
            OPHisDetail get = listDetail.get(listDetail.size() - 1);
            if (get.getStockCode() == null) {
                status = false;
            }
        }
        return status;
    }

    public List<OPHisDetail> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<OPHisDetail> listDetail) {
        this.listDetail = listDetail;
        fireTableDataChanged();
    }

    private void calculatePrice(OPHisDetail pd) {
        if (pd.getStockCode() != null) {
            double qty = Util1.getDouble(pd.getQty());
            double purAmt = Util1.getDouble(pd.getAmount());
            double price = purAmt / qty;
            pd.setPrice(price);
        }
    }

    private void calculateAmount(OPHisDetail pur) {
        double price = Util1.getDouble(pur.getPrice());
        double qty = Util1.getDouble(pur.getQty());
        if (pur.getStockCode() != null) {
            double amount = qty * price;
            pur.setPrice(price);
            pur.setAmount(amount);
        }
    }

    private void showMessageBox(String text) {
        JOptionPane.showMessageDialog(Global.parentForm, text);
    }

    public List<OPHisDetailKey> getDelList() {
        return deleteList;
    }

    public void clearDelList() {
        if (deleteList != null) {
            deleteList.clear();
        }
    }

    public void delete(int row) {
        OPHisDetail sdh = listDetail.get(row);
        if (sdh != null) {
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

    public void addSale(OPHisDetail sd) {
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
