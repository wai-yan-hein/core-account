/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.common.Global;
import com.common.ProUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.common.Util1;
import com.inventory.model.Location;
import com.inventory.model.Pattern;
import com.inventory.model.Stock;
import com.inventory.model.StockUnit;
import java.awt.HeadlessException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
public class PatternTableModel extends AbstractTableModel {

    public static final Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
    private final String[] columnNames = {"Stock Code", "Stock Name", "Location", "Qty", "Unit", "Price"};
    private List<Pattern> listPattern = new ArrayList<>();
    private WebClient inventoryApi;
    private String stockCode;
    private JPanel panel;
    private JTable table;

    public JTable getTable() {
        return table;
    }

    public void setTable(JTable table) {
        this.table = table;
    }

    public JPanel getPanel() {
        return panel;
    }

    public void setPanel(JPanel panel) {
        this.panel = panel;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
        addNewRow();
    }

    public WebClient getWebClient() {
        return inventoryApi;
    }

    public void setWebClient(WebClient inventoryApi) {
        this.inventoryApi = inventoryApi;
    }

    @Override
    public int getRowCount() {
        return listPattern.size();
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
        Pattern p = listPattern.get(rowIndex);
        switch (columnIndex) {
            case 0 -> {
                return p.getUserCode() == null ? p.getStockCode() : p.getUserCode();
            }
            case 1 -> {
                String stockName = null;
                if (p.getStockCode() != null) {
                    stockName = p.getStockName();
                    if (ProUtil.isStockNameWithCategory()) {
                        if (p.getCatName() != null) {
                            stockName = String.format("%s (%s)", stockName, p.getCatName());
                        }
                    }
                }
                return stockName;
            }
            case 2 -> {
                return p.getLocName();
            }
            case 3 -> {
                return p.getQty();
            }
            case 4 -> {
                return p.getUnitCode();
            }
            case 5 -> {
                return p.getPrice();
            }
        }
        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        try {
            if (!Objects.isNull(value)) {
                Pattern p = listPattern.get(row);
                switch (column) {
                    case 0,1 -> {
                        if (value instanceof Stock s) {
                            p.setStockCode(s.getKey().getStockCode());
                            p.setUserCode(s.getUserCode());
                            p.setRelation(s.getRelName());
                            p.setUnitCode(s.getPurUnitCode());
                            table.setColumnSelectionInterval(2, 2);
                        }
                    }
                    case 2 -> {
                        if (value instanceof Location loc) {
                            p.setLocCode(loc.getKey().getLocCode());
                            p.setLocName(loc.getLocName());
                            table.setColumnSelectionInterval(3, 3);
                        }
                    }
                    case 3 -> {
                        if (Util1.isPositive(Util1.getFloat(value))) {
                            p.setQty(Util1.getFloat(value));
                            if (p.getUnitCode() == null) {
                                table.setColumnSelectionInterval(4, 4);
                            } else {
                                table.setColumnSelectionInterval(0, 0);
                                table.setRowSelectionInterval(row + 1, row + 1);
                            }
                        } else {
                            JOptionPane.showMessageDialog(panel, String.format("Invalid %s", value));
                        }
                    }
                    case 4 -> {
                        if (value instanceof StockUnit unit) {
                            p.setUnitCode(unit.getKey().getUnitCode());
                            table.setColumnSelectionInterval(0, 0);
                            table.setRowSelectionInterval(row + 1, row + 1);
                        }
                    }
                    case 5 -> {
                        if (Util1.isPositive(Util1.getFloat(value))) {
                            p.setPrice(Util1.getFloat(value));
                            table.setColumnSelectionInterval(0, 0);
                            table.setRowSelectionInterval(row + 1, row + 1);
                        } else {
                            JOptionPane.showMessageDialog(panel, String.format("Invalid %s", value));
                        }
                    }
                }
                p.setStockCode(stockCode);
                p.setUniqueId(row + 1);
                save(p);
                addNewRow();
                fireTableRowsUpdated(row, row);
                table.requestFocus();
            }
        } catch (HeadlessException e) {
            log.error(String.format("setValueAt : %s", e.getMessage()));
        }
    }

    private boolean isValidEntry(Pattern pd) {
        boolean status = true;
        if (Util1.isNull(pd.getStockCode())) {
            status = false;
            JOptionPane.showMessageDialog(panel, "Select Pattern");
        } else if (Objects.isNull(pd.getStockCode())) {
            status = false;
        } else if (Objects.isNull(pd.getLocCode())) {
            status = false;
        } else if (Util1.getFloat(pd.getQty()) <= 0) {
            status = false;
        } else if (Objects.isNull(pd.getUnitCode())) {
            status = false;
        }
        return status;
    }

    private void save(Pattern pd) {
        if (isValidEntry(pd)) {
            pd.setCompCode(Global.compCode);
            pd.setDeptId(Global.deptId);
            Mono<Pattern> result = inventoryApi.post()
                    .uri("/setup/save-pattern")
                    .body(Mono.just(pd), Pattern.class)
                    .retrieve()
                    .bodyToMono(Pattern.class);
            result.block();
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex
    ) {
        return true;
    }

    public List<Pattern> getListPattern() {
        return listPattern;
    }

    public void setListPattern(List<Pattern> listPattern) {
        this.listPattern = listPattern;
        fireTableDataChanged();
    }

    public Pattern getPattern(int row) {
        return listPattern.get(row);
    }

    public void setPattern(Pattern report, int row) {
        if (!listPattern.isEmpty()) {
            listPattern.set(row, report);
            fireTableRowsUpdated(row, row);
        }
    }

    public void addPattern(Pattern item) {
        if (!listPattern.isEmpty()) {
            listPattern.add(item);
            fireTableRowsInserted(listPattern.size() - 1, listPattern.size() - 1);
        }
    }

    public void refresh() {
        fireTableDataChanged();
    }

    public void addNewRow() {
        if (!hasEmptyRow()) {
            Pattern p = new Pattern();
            listPattern.add(p);
            fireTableRowsInserted(listPattern.size() - 1, listPattern.size() - 1);
        }
    }

    private boolean hasEmptyRow() {
        if (listPattern.isEmpty()) {
            return false;
        }
        Pattern p = listPattern.get(listPattern.size() - 1);
        return p.getStockCode() == null;
    }

    public void addRow() {
        Pattern p = new Pattern();
        listPattern.add(p);
        fireTableRowsInserted(listPattern.size() - 1, listPattern.size() - 1);
    }

    public void remove(int row) {
        listPattern.remove(row);
        fireTableRowsDeleted(row, row);
    }
}
