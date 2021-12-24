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
import com.inventory.model.Location;
import com.inventory.model.PatternDetail;
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
public class PatternDetailTableModel extends AbstractTableModel {

    public static final Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
    private final String[] columnNames = {"Stock Code", "Stock Name", "Location", "In Qty", "In Unit", "Out Qty", "Out Unit"};
    private List<PatternDetail> listPattern = new ArrayList<>();
    private WebClient webClient;
    private String patternCode;
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
        PatternDetail p = listPattern.get(rowIndex);
        return switch (columnIndex) {
            case 0 ->
                p.getStock() == null ? null : p.getStock().getUserCode();
            case 1 ->
                p.getStock() == null ? null : p.getStock().getStockName();
            case 2 ->
                p.getLocation() == null ? null : p.getLocation().getLocationName();
            case 3 ->
                p.getInQty();
            case 4 ->
                p.getInUnit() == null ? null : p.getInUnit().getUnitCode();
            case 5 ->
                p.getOutQty();
            case 6 ->
                p.getOutUnit() == null ? null : p.getOutUnit().getUnitCode();
            default ->
                null;
        };
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        try {
            if (!Objects.isNull(value)) {
                PatternDetail p = listPattern.get(row);
                switch (column) {
                    case 0,1 -> {
                        if (value instanceof Stock s) {
                            p.setStock(s);
                            table.setColumnSelectionInterval(2, 2);
                        }
                    }
                    case 2 -> {
                        if (value instanceof Location loc) {
                            p.setLocation(loc);
                            table.setColumnSelectionInterval(3, 3);
                        }
                    }
                    case 3 -> {
                        if (Util1.isPositive(Util1.getFloat(value))) {
                            p.setInQty(Util1.getFloat(value));
                            p.setOutQty(null);
                            p.setOutUnit(null);
                            table.setColumnSelectionInterval(4, 4);
                        } else {
                            JOptionPane.showMessageDialog(panel, String.format("Invalid %s", value));
                        }
                    }
                    case 4 -> {
                        if (value instanceof StockUnit unit) {
                            p.setInUnit(unit);
                            table.setColumnSelectionInterval(0, 0);
                            table.setRowSelectionInterval(row + 1, row + 1);
                        }
                    }
                    case 5 -> {
                        if (Util1.isPositive(Util1.getFloat(value))) {
                            p.setOutQty(Util1.getFloat(value));
                            p.setInQty(null);
                            p.setInUnit(null);
                            table.setColumnSelectionInterval(6, 6);
                        } else {
                            JOptionPane.showMessageDialog(panel, String.format("Invalid %s", value));
                        }
                    }
                    case 6 -> {
                        if (value instanceof StockUnit unit) {
                            p.setOutUnit(unit);
                            table.setColumnSelectionInterval(0, 0);
                            table.setRowSelectionInterval(row + 1, row + 1);
                        }
                    }
                }
                p.setPatternCode(patternCode);
                p.setUniqueId(row + 1);
                addNewRow();
                save(p);
                fireTableRowsUpdated(row, row);
                table.requestFocus();
            }
        } catch (HeadlessException e) {
            log.error(String.format("setValueAt : %s", e.getMessage()));
        }
    }

    private boolean isValidEntry(PatternDetail pd) {
        boolean status = true;
        if (Util1.isNull(pd.getPatternCode())) {
            status = false;
            JOptionPane.showMessageDialog(panel, "Select Pattern");
        } else if (Objects.isNull(pd.getStock())) {
            status = false;
        } else if (Objects.isNull(pd.getLocation())) {
            status = false;
        } else if (Util1.getFloat(pd.getInQty()) <= 0 && Util1.getFloat(pd.getOutQty()) <= 0) {
            status = false;
        } else if (Objects.isNull(pd.getInUnit()) && Objects.isNull(pd.getOutUnit())) {
            status = false;
        }
        return status;
    }

    private void save(PatternDetail pd) {
        if (isValidEntry(pd)) {
            Mono<ReturnObject> result = webClient.post()
                    .uri("/setup/save-pattern-detail")
                    .body(Mono.just(pd), PatternDetail.class)
                    .retrieve()
                    .bodyToMono(ReturnObject.class);
            ReturnObject ro = result.block();
            if (!Util1.isNull(ro.getErrorMessage())) {
                JOptionPane.showMessageDialog(panel, ro.getErrorMessage());
            }
            PatternDetail pattern = gson.fromJson(gson.toJson(ro.getObj()), PatternDetail.class);
            pd.setPtCode(pattern.getPtCode());

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

    public List<PatternDetail> getListPattern() {
        return listPattern;
    }

    public void setListPattern(List<PatternDetail> listPattern) {
        this.listPattern = listPattern;
        fireTableDataChanged();
    }

    public PatternDetail getPattern(int row) {
        return listPattern.get(row);
    }

    public void setPattern(PatternDetail report, int row) {
        if (!listPattern.isEmpty()) {
            listPattern.set(row, report);
            fireTableRowsUpdated(row, row);
        }
    }

    public void addPattern(PatternDetail item) {
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
            PatternDetail p = new PatternDetail();
            listPattern.add(p);
            fireTableRowsInserted(listPattern.size() - 1, listPattern.size() - 1);
        }
    }

    private boolean hasEmptyRow() {
        PatternDetail p = listPattern.get(listPattern.size() - 1);
        return p.getStock() == null;
    }

    public void addRow() {
        PatternDetail p = new PatternDetail();
        listPattern.add(p);
        fireTableRowsInserted(listPattern.size() - 1, listPattern.size() - 1);
    }
}
