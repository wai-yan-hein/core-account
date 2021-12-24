/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.inventory.common.ReturnObject;
import com.inventory.common.Util1;
import com.inventory.model.ReorderLevel;
import com.inventory.model.Stock;
import com.inventory.model.StockUnit;
import java.awt.HeadlessException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class ReorderTableModel extends AbstractTableModel {

    public static final Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
    private final String[] columnNames = {"Stock Code", "Stock Name", "Min Qty", "Min Unit", "Max Qty", "Max Unit", "Balance Qty", "Balance Unit"};
    private List<ReorderLevel> listReorder = new ArrayList<>();
    private WebClient webClient;
    private String patternCode;
    private JTable table;

    public JTable getTable() {
        return table;
    }

    public void setTable(JTable table) {
        this.table = table;
    }


    public String getPatternCode() {
        return patternCode;
    }

    public void setPatternCode(String patternCode) {
        this.patternCode = patternCode;
    }

    public WebClient getWebClient() {
        return webClient;
    }

    public void setWebClient(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public int getRowCount() {
        return listReorder.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ReorderLevel p = listReorder.get(rowIndex);
        return switch (columnIndex) {
            case 0 ->
                p.getStock() == null ? null : p.getStock().getUserCode();
            case 1 ->
                p.getStock() == null ? null : p.getStock().getStockName();
            case 2 ->
                p.getMinQty();
            case 3 ->
                p.getMinUnit();
            case 4 ->
                p.getMaxQty();
            case 5 ->
                p.getMaxUnit();
            case 6 ->
                p.getBalQty();
            case 7 ->
                p.getBalUnit();
            default ->
                null;
        };
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        try {
            if (!Objects.isNull(value)) {
                ReorderLevel p = listReorder.get(row);
                switch (column) {
                    case 0,1 -> {
                        if (value instanceof Stock s) {
                            p.setStock(s);
                            table.setColumnSelectionInterval(2, 2);
                        }
                    }
                    case 2 -> {
                        if (Util1.isPositive(Util1.getFloat(value))) {
                            p.setMinQty(Util1.getFloat(value));
                            table.setColumnSelectionInterval(3, 3);
                        } else {
                            JOptionPane.showMessageDialog(table, "Invalid Amount.");
                        }
                    }
                    case 3 -> {
                        if (value instanceof StockUnit unit) {
                            p.setMinUnit(unit);
                            table.setColumnSelectionInterval(4, 4);
                        }
                    }
                    case 4 -> {
                        if (Util1.isPositive(Util1.getFloat(value))) {
                            p.setMaxQty(Util1.getFloat(value));
                            table.setColumnSelectionInterval(5, 5);
                        } else {
                            JOptionPane.showMessageDialog(table, "Invalid Amount.");
                        }
                    }
                    case 5 -> {
                        if (value instanceof StockUnit unit) {
                            p.setMaxUnit(unit);
                            table.setColumnSelectionInterval(4, 4);
                        }
                    }
                }
                addNewRow();
                save(p);
                fireTableRowsUpdated(row, row);
                table.requestFocus();
            }
        } catch (HeadlessException e) {
            log.error(String.format("setValueAt : %s", e.getMessage()));
        }
    }

    private void save(ReorderLevel rl) {
        Mono<ReturnObject> result = webClient.post()
                .uri("/setup/save-reorder")
                .body(Mono.just(rl), ReorderLevel.class)
                .retrieve()
                .bodyToMono(ReturnObject.class);
        ReturnObject ro = result.block();
        if (!Objects.isNull(ro)) {
            if (!Util1.isNull(ro.getErrorMessage())) {
                JOptionPane.showMessageDialog(table, ro.getErrorMessage());
            }
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case 2, 4, 6 ->
                Float.class;
            default ->
                String.class;
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    public List<ReorderLevel> getListPattern() {
        return listReorder;
    }

    public void setListPattern(List<ReorderLevel> listReorder) {
        this.listReorder = listReorder;
        fireTableDataChanged();
    }

    public ReorderLevel getPattern(int row) {
        return listReorder.get(row);
    }

    public void setPattern(ReorderLevel report, int row) {
        if (!listReorder.isEmpty()) {
            listReorder.set(row, report);
            fireTableRowsUpdated(row, row);
        }
    }

    public void addPattern(ReorderLevel item) {
        if (!listReorder.isEmpty()) {
            listReorder.add(item);
            fireTableRowsInserted(listReorder.size() - 1, listReorder.size() - 1);
        }
    }

    public void refresh() {
        fireTableDataChanged();
    }

    public void addNewRow() {
        if (!hasEmptyRow()) {
            ReorderLevel p = new ReorderLevel();
            listReorder.add(p);
            fireTableRowsInserted(listReorder.size() - 1, listReorder.size() - 1);
        }
    }

    private boolean hasEmptyRow() {
        ReorderLevel p = listReorder.get(listReorder.size() - 1);
        return p.getStock() == null;
    }

    public void addRow() {
        ReorderLevel p = new ReorderLevel();
        listReorder.add(p);
        fireTableRowsInserted(listReorder.size() - 1, listReorder.size() - 1);
    }
}
