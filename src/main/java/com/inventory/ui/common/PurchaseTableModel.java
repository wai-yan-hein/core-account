/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.inventory.common.Global;
import com.inventory.common.SelectionObserver;
import com.inventory.common.Util1;
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.model.Location;
import com.inventory.model.PurHisDetail;
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
 * @author Mg Kyaw Thura Aung
 */
@Component
@Slf4j
public class PurchaseTableModel extends AbstractTableModel {

    private String[] columnNames = {"Code", "Description", "Location",
        "Qty", "Std-Wt", "Avg-Wt", "Unit", "Price", "Amount"};
    private JTable parent;
    private List<PurHisDetail> listDetail = new ArrayList();
    private SelectionObserver selectionObserver;
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
        if (listDetail == null) {
            return 0;
        }
        return listDetail.size();
    }

    @Override
    public Class getColumnClass(int column) {
        return switch (column) {
            case 0,1,2,6 ->
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
            PurHisDetail record = listDetail.get(row);
            switch (column) {
                case 0 -> {
                    //code
                    return record.getStock() == null ? null : record.getStock().getUserCode();
                }
                case 1 -> {
                    //Name
                    return record.getStock() == null ? null : record.getStock().getStockName();
                }
                case 2 -> {
                    //loc
                    return record.getLocation();
                }
                case 3 -> {
                    //qty
                    return record.getQty();
                }
                case 4 -> {
                    //Std-Wt
                    return record.getStdWeight();
                }
                case 5 -> {
                    //avg-Wt
                    return record.getAvgWeight();
                }
                case 6 -> {
                    //unit
                    return record.getPurUnit();
                }
                case 7 -> {
                    //price
                    return record.getPrice();
                }
                case 8 -> {
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
            PurHisDetail record = listDetail.get(row);
            switch (column) {
                case 0 -> {
                    //Code
                    if (value != null) {
                        if (value instanceof Stock stock) {
                            record.setStock(stock);
                            record.setQty(1.0f);
                            record.setStdWeight(Util1.gerFloatOne(stock.getPurWeight()));
                            record.setAvgWeight(Util1.gerFloatOne(stock.getPurWeight()));
                            record.setPurUnit(stock.getSaleUnit());
                            record.setPrice(stock.getPurPrice());
                            record.setLocation(locationAutoCompleter.getLocation());
                            addNewRow();
                        }
                    }
                    parent.setColumnSelectionInterval(3, 3);
                }
                case 2 -> {
                    //Loc
                    if (value instanceof Location location) {
                        record.setLocation(location);
                    } else {
                        record.setLocation(null);
                    }
                }
                case 3 -> {
                    //Qty
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
                    parent.setColumnSelectionInterval(5, 5);
                }
                case 4 -> {
                    //Std-Wt
                    if (Util1.isNumber(value)) {
                        if (Util1.isPositive(Util1.getFloat(value))) {
                            record.setStdWeight(Util1.getFloat(value));
                        } else {
                            showMessageBox("Input value must be positive");
                            parent.setColumnSelectionInterval(column, column);
                        }
                    } else {
                        showMessageBox("Input value must be positive");
                        parent.setColumnSelectionInterval(column, column);
                    }
                }
                case 5 -> {
                    //avg-Wt
                    if (Util1.isNumber(value)) {
                        if (Util1.isPositive(Util1.getFloat(value))) {
                            record.setAvgWeight(Util1.getFloat(value));
                        } else {
                            showMessageBox("Input value must be positive");
                            parent.setColumnSelectionInterval(column, column);
                        }
                    } else {
                        showMessageBox("Input value must be positive");
                        parent.setColumnSelectionInterval(column, column);
                    }
                    parent.setRowSelectionInterval(row, row);
                    parent.setColumnSelectionInterval(7, 7);
                }

                case 6 -> {
                    //Unit
                    if (value != null) {
                        if (value instanceof StockUnit stockUnit) {
                            record.setPurUnit(stockUnit);
                        }
                    }
                    parent.setColumnSelectionInterval(6, 6);
                }
                case 7 -> {
                    //Pur Price
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
                case 8 -> {
                    //Amount
                    if (value != null) {
                        record.setAmount(Util1.getFloat(value));
                    }
                }
            }
            calculateAmount(record);
            fireTableRowsUpdated(row, row);
            selectionObserver.selected("SALE-TOTAL", "SALE-TOTAL");
            parent.requestFocusInWindow();
            //   fireTableCellUpdated(row, 8);
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }

    public void addNewRow() {
        if (listDetail != null) {
            if (hasEmptyRow()) {
                PurHisDetail pd = new PurHisDetail();
                pd.setLocation(locationAutoCompleter.getLocation());
                listDetail.add(pd);
                fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
            }
        }
    }

    private boolean hasEmptyRow() {
        boolean status = true;
        if (listDetail.size() > 1) {
            PurHisDetail get = listDetail.get(listDetail.size() - 1);
            if (get.getStock() == null) {
                status = false;
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
        float price = Util1.getFloat(pur.getPrice());
        float avgWt = Util1.getFloat(pur.getAvgWeight());
        float stdWt = Util1.getFloat(pur.getStdWeight());
        float qty = Util1.getFloat(pur.getQty());
        price = (avgWt / stdWt) * price;
        if (pur.getStock() != null) {
            float amount = qty * price;
            pur.setPrice(price);
            pur.setAmount(amount);
        }
    }

    private void showMessageBox(String text) {
        JOptionPane.showMessageDialog(Global.parentForm, text);
    }

    public boolean isValidEntry() {
        boolean status = true;
        for (PurHisDetail sdh : listDetail) {
            if (sdh.getStock() != null) {
                if (Util1.getFloat(sdh.getAmount()) <= 0) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Amount.",
                            "Invalid.", JOptionPane.ERROR_MESSAGE);
                    status = false;
                    parent.requestFocus();
                    break;
                } else if (sdh.getLocation() == null) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Location.");
                    status = false;
                    parent.requestFocus();
                }
            }
        }
        return status;
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
        PurHisDetail sdh = listDetail.get(row);
        if (sdh.getPdKey() != null) {
            deleteList.add(sdh.getPdKey().getPdCode());
        }
        listDetail.remove(row);
        addNewRow();
        fireTableRowsDeleted(row, row);
        if (row - 1 >= 0) {
            parent.setRowSelectionInterval(row - 1, row - 1);
        } else {
            parent.setRowSelectionInterval(0, 0);
        }
    }

    public void addSale(PurHisDetail sd) {
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
