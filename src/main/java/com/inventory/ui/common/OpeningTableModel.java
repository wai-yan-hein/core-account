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
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.model.OPHisDetail;
import com.inventory.model.Stock;
import com.inventory.model.StockUnit;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 *
 * @author wai yan
 */
@Component
@Slf4j
public class OpeningTableModel extends AbstractTableModel {

    private String[] columnNames = {"Stock Code", "Stock Name",
        "Qty", "Std-Wt", "Unit", "Price", "Amount"};
    private JTable parent;
    private List<OPHisDetail> listDetail = new ArrayList();
    private SelectionObserver observer;
    private final List<String> deleteList = new ArrayList();
    private LocationAutoCompleter locationAutoCompleter;

    public JTable getParent() {
        return parent;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    public LocationAutoCompleter getLocationAutoCompleter() {
        return locationAutoCompleter;
    }

    public void setLocationAutoCompleter(LocationAutoCompleter locationAutoCompleter) {
        this.locationAutoCompleter = locationAutoCompleter;
    }

    public SelectionObserver getObserver() {
        return observer;
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
            case 0,1,4 ->
                String.class;
            default ->
                Float.class;
        };
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column != 8;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            OPHisDetail record = listDetail.get(row);
            switch (column) {
                case 0 -> {
                    //code
                    return record.getStock() == null ? null : record.getStock().getUserCode();
                }
                case 1 -> {
                    //Name
                    String stockName = null;
                    if (record.getStock() != null) {
                        stockName = record.getStock().getStockName();
                        if (ProUtil.isStockNameWithCategory()) {
                            if (record.getStock().getCategory() != null) {
                                stockName = String.format("%s (%s)", stockName, record.getStock().getCategory().getCatName());
                            }
                        }
                    }
                    return stockName;
                }
                case 2 -> {
                    //qty
                    return record.getQty();
                }
                case 3 -> {
                    //Std-Wt
                    return record.getStdWt();
                }
                case 4 -> {
                    //unit
                    return record.getStockUnit();
                }
                case 5 -> {
                    //price
                    return record.getPrice();
                }
                case 6 -> {
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
            OPHisDetail record = listDetail.get(row);
            switch (column) {
                case 0,1 -> {
                    //Code
                    if (value != null) {
                        if (value instanceof Stock stock) {
                            record.setStock(stock);
                            record.setQty(1.0f);
                            record.setStdWt(Util1.gerFloatOne(stock.getPurWeight()));
                            record.setStockUnit(stock.getPurUnit());
                            addNewRow();
                        }
                    }
                    parent.setColumnSelectionInterval(2, 2);
                }
                case 2 -> {
                    //
                    if (Util1.isNumber(value)) {
                        if (Util1.isPositive(Util1.getFloat(value))) {
                            record.setQty(Util1.getFloat(value));
                        } else {
                            showMessageBox("Input value must be positive");
                            parent.setColumnSelectionInterval(column, column);
                        }
                    } else {
                        showMessageBox("Input value must be number.");
                        parent.setColumnSelectionInterval(column, column);
                    }
                    parent.setRowSelectionInterval(row, row);
                    parent.setColumnSelectionInterval(4, 4);
                }
                case 3 -> {
                    //Std-Wt
                    if (Util1.isNumber(value)) {
                        if (Util1.isPositive(Util1.getFloat(value))) {
                            record.setStdWt(Util1.getFloat(value));
                            parent.setColumnSelectionInterval(4, 4);
                        } else {
                            showMessageBox("Input value must be positive");
                            parent.setColumnSelectionInterval(column, column);
                        }
                    } else {
                        showMessageBox("Input value must be positive");
                        parent.setColumnSelectionInterval(column, column);
                    }
                }

                case 4 -> {
                    //Unit
                    if (value != null) {
                        if (value instanceof StockUnit stockUnit) {
                            record.setStockUnit(stockUnit);
                        }
                    }
                    parent.setColumnSelectionInterval(5, 5);
                }
                case 5 -> {
                    // Price
                    if (Util1.isNumber(value)) {
                        if (Util1.isPositive(Util1.getFloat(value))) {
                            record.setPrice(Util1.getFloat(value));
                            parent.setColumnSelectionInterval(0, 0);
                            parent.setRowSelectionInterval(row + 1, row + 1);
                            calculateAmount(record);
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
                    //Amount
                    if (value != null) {
                        if (Util1.isPositive(Util1.getFloat(value))) {
                            record.setAmount(Util1.getFloat(value));
                            parent.setColumnSelectionInterval(0, 0);
                            parent.setRowSelectionInterval(row + 1, row + 1);
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
            //   fireTableCellUpdated(row, 8);
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }

    public boolean isValidEntry() {
        boolean status = true;
        for (OPHisDetail op : listDetail) {
            op.setAmount(Util1.getFloat(op.getAmount()));
            op.setPrice(Util1.getFloat(op.getPrice()));
            op.setQty(Util1.getFloat(op.getQty()));
            if (op.getStock() != null) {
                if (op.getStockUnit() == null) {
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
            if (get.getStock() == null) {
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
        if (pd.getStock() != null) {
            float qty = Util1.getFloat(pd.getQty());
            float purAmt = Util1.getFloat(pd.getAmount());
            float price = purAmt / qty;
            pd.setPrice(price);
        }
    }

    private void calculateAmount(OPHisDetail pur) {
        float price = Util1.getFloat(pur.getPrice());
        float qty = Util1.getFloat(pur.getQty());
        if (pur.getStock() != null) {
            float amount = qty * price;
            pur.setPrice(price);
            pur.setAmount(amount);
        }
    }

    private void showMessageBox(String text) {
        JOptionPane.showMessageDialog(Global.parentForm, text);
    }

    public List<String> getDelList() {
        return deleteList;
    }

    public void clearDelList() {
        if (deleteList != null) {
            deleteList.clear();
        }
    }

    public void delete(int row) {
        OPHisDetail sdh = listDetail.get(row);
        if (sdh.getOpCode() != null) {
            deleteList.add(sdh.getOpCode());
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
