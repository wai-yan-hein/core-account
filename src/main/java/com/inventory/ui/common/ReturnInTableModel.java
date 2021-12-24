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
import com.inventory.model.General;
import com.inventory.model.Location;
import com.inventory.model.RetInHisDetail;
import com.inventory.model.Stock;
import com.inventory.model.StockUnit;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Slf4j
public class ReturnInTableModel extends AbstractTableModel {

    private String[] columnNames = {"Code", "Description", "Location",
        "Qty", "Std-Wt", "Unit", "Cost Price", "Price", "Amount"};
    private JTable parent;
    private List<RetInHisDetail> listDetail = new ArrayList();
    private SelectionObserver selectionObserver;
    private final List<String> deleteList = new ArrayList();
    private LocationAutoCompleter locationAutoCompleter;
    private WebClient webClient;

    public WebClient getWebClient() {
        return webClient;
    }

    public void setWebClient(WebClient webClient) {
        this.webClient = webClient;
    }

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
        return listDetail.size();
    }

    @Override
    public Class getColumnClass(int column) {
        return switch (column) {
            case 0,1,2,5 ->
                String.class;
            default ->
                Float.class;
        };
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        switch (column) {
            case 4,8 -> {
                return false;
            }
            default -> {
                return true;
            }
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            RetInHisDetail record = listDetail.get(row);
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
                    return record.getWt();
                }
                case 5 -> {
                    //unit
                    return record.getUnit();
                }
                case 6 -> {
                    return record.getCostPrice();
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
            RetInHisDetail record = listDetail.get(row);
            switch (column) {
                case 0 -> {
                    //Code
                    if (value != null) {
                        if (value instanceof Stock stock) {
                            record.setStock(stock);
                            record.setQty(1.0f);
                            record.setWt(Util1.gerFloatOne(stock.getSaleWeight()));
                            record.setUnit(stock.getSaleUnit());
                            record.setPrice(stock.getPurPrice());
                            record.setLocation(locationAutoCompleter.getLocation());
                            record.setCostPrice(getPurAvgPrice(record.getStock().getStockCode()));
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
                            record.setWt(Util1.getFloat(value));
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
                    //Unit
                    if (value != null) {
                        if (value instanceof StockUnit stockUnit) {
                            record.setUnit(stockUnit);
                        }
                    }
                    parent.setColumnSelectionInterval(6, 6);
                }
                case 6 -> {
                    if (Util1.isPositive(Util1.getFloat(value))) {
                        record.setCostPrice(Util1.getFloat(value));
                    }
                    parent.setColumnSelectionInterval(7, 7);
                }
                case 7 -> {
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
                RetInHisDetail pd = new RetInHisDetail();
                pd.setStock(new Stock());
                pd.setLocation(locationAutoCompleter.getLocation());
                listDetail.add(pd);
                fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
            }
        }
    }

    private boolean hasEmptyRow() {
        boolean status = true;
        if (listDetail.size() > 1) {
            RetInHisDetail get = listDetail.get(listDetail.size() - 1);
            if (get.getStock().getStockCode() == null) {
                status = false;
            }
        }
        return status;
    }

    public List<RetInHisDetail> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<RetInHisDetail> listDetail) {
        this.listDetail = listDetail;
        fireTableDataChanged();
    }

    private void calculateAmount(RetInHisDetail ri) {
        float price = Util1.getFloat(ri.getPrice());
        float qty = Util1.getFloat(ri.getQty());
        if (ri.getStock() != null) {
            float amount = qty * price;
            ri.setAmount(amount);
        }
    }

    private void showMessageBox(String text) {
        JOptionPane.showMessageDialog(Global.parentForm, text);
    }

    public boolean isValidEntry() {
        boolean status = true;
        for (RetInHisDetail sdh : listDetail) {
            if (sdh.getStock().getStockCode() != null) {
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
        RetInHisDetail sdh = listDetail.get(row);
        if (sdh.getRiKey() != null) {
            deleteList.add(sdh.getRiKey().getRdCode());
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

    public Float getPurAvgPrice(String stockCode) {
        Mono<General> result = webClient.get()
                .uri(builder -> builder.path("/report/get-purchase-price")
                .queryParam("stockCode", stockCode)
                .build())
                .retrieve().bodyToMono(General.class);
        return Util1.getFloat(result.block().getAmount());
    }
}
