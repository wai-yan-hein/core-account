/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.inventory.common.Global;
import com.inventory.common.Util1;
import com.inventory.model.General;
import com.inventory.model.Location;
import com.inventory.model.Stock;
import com.inventory.model.StockInOutDetail;
import com.inventory.model.StockUnit;
import java.awt.HeadlessException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.swing.JFormattedTextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Component
public class StockInOutTableModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(StockInOutTableModel.class);
    private String[] columnNames = {"Stock Code", "Stock Name", "Location",
        "In-Qty", "In-Weight", "In-Unit", "Out-Qty", "Out-Weight", "Out-Unit", "Cost Price"};
    private JTable parent;
    private List<StockInOutDetail> listStock = new ArrayList();
    private List<String> deleteList = new ArrayList();

    private JFormattedTextField inTotal;
    private JFormattedTextField outTotal;
    @Autowired
    private WebClient webClient;

    public JFormattedTextField getInTotal() {
        return inTotal;
    }

    public void setInTotal(JFormattedTextField inTotal) {
        this.inTotal = inTotal;
    }

    public JFormattedTextField getOutTotal() {
        return outTotal;
    }

    public void setOutTotal(JFormattedTextField outTotal) {
        this.outTotal = outTotal;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    @Override
    public int getRowCount() {
        return listStock.size();
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
    public Object getValueAt(int row, int column) {
        StockInOutDetail io = listStock.get(row);
        return switch (column) {
            case 0 ->
                io.getStock().getUserCode();
            case 1 ->
                io.getStock().getStockName();
            case 2 ->
                io.getLocation();
            case 3 ->
                io.getInQty();
            case 4 ->
                Util1.getFloat(io.getInWt()) == 1 ? null : io.getInWt();
            case 5 ->
                io.getInUnit();
            case 6 ->
                io.getOutQty();
            case 7 ->
                Util1.getFloat(io.getOutWt()) == 1 ? null : io.getOutWt();
            case 8 ->
                io.getOutUnit();
            case 9 ->
                io.getCostPrice();
            default ->
                null;
        };
    }

    @Override
    public Class getColumnClass(int column) {
        return switch (column) {
            case 3,4,6,7,9 ->
                Float.class;
            default ->
                String.class;
        };
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return true;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        StockInOutDetail io = listStock.get(row);
        try {
            if (value != null) {
                switch (column) {
                    case 0,1 -> {
                        if (value instanceof Stock stock) {
                            io.setStock(stock);
                            io.setInWt(stock.getPurWeight());
                            io.setOutWt(stock.getPurWeight());
                            io.setCostPrice(0.0f);
                            //io.setInUnit(stock.getPurUnit());
                            //io.setOutUnit(stock.getPurUnit());
                            io.setCostPrice(getPurAvgPrice(stock.getStockCode()));
                            setColumnSelection(3);
                        }
                        addNewRow();
                    }
                    case 2 -> {
                        if (value instanceof Location location) {
                            io.setLocation(location);
                        }
                        setColumnSelection(5);
                    }
                    case 3 -> {

                        io.setInQty(Util1.getFloat(value));
                        io.setInWt(io.getStock().getPurWeight());
                        io.setOutQty(null);
                        io.setOutWt(null);
                        io.setOutUnit(null);
                        io.setInUnit(io.getStock().getPurUnit());
                        parent.setRowSelectionInterval(row + 1, row + 1);
                        parent.setColumnSelectionInterval(0, 0);

                    }
                    case 4 ->
                        io.setInWt(Util1.getFloat(value));
                    case 5 -> {
                        if (value instanceof StockUnit stockUnit) {
                            io.setInUnit(stockUnit);
                        }
                        setColumnSelection(8);
                    }
                    case 6 -> {
                        io.setOutQty(Util1.getFloat(value));
                        io.setOutWt(io.getStock().getPurWeight());
                        io.setInQty(null);
                        io.setInWt(null);
                        io.setInUnit(null);
                        io.setOutUnit(io.getStock().getPurUnit());
                        parent.setRowSelectionInterval(row + 1, row + 1);
                        parent.setColumnSelectionInterval(0, 0);
                    }
                    case 7 ->
                        io.setOutWt(Util1.getFloat(value));
                    case 8 -> {
                        if (value instanceof StockUnit stockUnit) {
                            io.setOutUnit(stockUnit);
                        }
                    }
                    case 9 -> {
                        if (Util1.isPositive(Util1.getFloat(value))) {
                            io.setCostPrice(Util1.getFloat(value));
                        } else {
                            JOptionPane.showMessageDialog(parent, "Invalid Amount.");
                        }
                    }
                }
            }
            calStock(io);
            fireTableRowsUpdated(row, row);
            parent.requestFocus();
        } catch (HeadlessException e) {
            log.error("setValueAt :" + e.getMessage());
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

    public List<String> getDeleteList() {
        return deleteList;
    }

    public void setDeleteList(List<String> deleteList) {
        this.deleteList = deleteList;
    }

    private void setColumnSelection(int column) {
        parent.setColumnSelectionInterval(column, column);
        parent.requestFocus();
    }

    public boolean isValidEntry() {
        boolean status = true;
        for (StockInOutDetail od : listStock) {
            if (od.getStock().getStockCode() != null) {
                if (od.getLocation() == null) {
                    status = false;
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Location.");
                }
            }
        }
        return status;

    }

    private void calStock(StockInOutDetail od) {
        if (od.getStock().getPurUnit() != null) {
            calTotal();
        }
    }

    private void calTotal() {
        float inTtl = 0.0f;
        float outTtl = 0.0f;
        for (StockInOutDetail od : listStock) {
            inTtl += Util1.getFloat(od.getInQty());
            outTtl += Util1.getFloat(od.getOutQty());
        }
        this.inTotal.setValue(inTtl);
        this.outTotal.setValue(outTtl);
    }

    public List<StockInOutDetail> getCurrentRow() {
        return this.listStock;
    }

    public List<StockInOutDetail> getRetInDetailHis() {
        return this.listStock;
    }

    public List<StockInOutDetail> getListStock() {
        return listStock;
    }

    public void setListStock(List<StockInOutDetail> listStock) {
        this.listStock = listStock;
        calTotal();
        fireTableDataChanged();
    }

    public StockInOutDetail getStockInout(int row) {
        if (listStock != null) {
            return listStock.get(row);
        } else {
            return null;
        }
    }

    public void addNewRow() {
        if (listStock != null) {
            if (hasEmptyRow()) {
                StockInOutDetail pd = new StockInOutDetail();
                pd.setStock(new Stock());
                pd.setLocation(Global.defaultLocation);
                listStock.add(pd);
                fireTableRowsInserted(listStock.size() - 1, listStock.size() - 1);
            }
        }
    }

    private boolean hasEmptyRow() {
        boolean status = true;
        if (listStock.size() > 1) {
            StockInOutDetail get = listStock.get(listStock.size() - 1);
            if (get.getStock().getStockCode() == null) {
                status = false;
            }
        }
        return status;
    }

    public void clear() {
        if (listStock != null) {
            listStock.clear();
            fireTableDataChanged();
        }
    }

    public void delete(int row) {
        StockInOutDetail sdh = listStock.get(row);
        if (sdh.getIoKey() != null) {
            deleteList.add(sdh.getIoKey().getSdCode());
        }
        listStock.remove(row);
        addNewRow();
        fireTableRowsDeleted(row, row);
        if (row - 1 >= 0) {
            parent.setRowSelectionInterval(row - 1, row - 1);
        } else {
            parent.setRowSelectionInterval(0, 0);
        }
    }
}
