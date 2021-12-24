/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.inventory.model.Pattern;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class PatternOptionTableModel extends AbstractTableModel {

    private final Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
    private final String[] columnNames = {"Patter Code", "Pattern Name"};
    private List<Pattern> listPattern = new ArrayList<>();
    private WebClient webClient;

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
        try {
            Pattern p = listPattern.get(rowIndex);
            return switch (columnIndex) {
                case 0 ->
                    p.getUserCode();
                case 1 ->
                    p.getPatternName();
                default ->
                    null;
            };
        } catch (Exception e) {
            log.error(String.format("getValueAt %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        try {
            if (!Objects.isNull(value)) {
            }
            Pattern p = listPattern.get(row);
            switch (column) {
                case 1 -> {
                    p.setUserCode(String.valueOf(value));
                }
                case 2 -> {
                    p.setPatternName(String.valueOf(value));
                }
                case 3 -> {
                    p.setActive((Boolean) value);
                }
            }
            fireTableRowsDeleted(row, row);
        } catch (Exception e) {
            log.error(String.format("setValueAt : %s", e.getMessage()));
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnIndex == 3 ? Boolean.class : String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
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

    /*public void addNewRow() {
        if (hasEmptyRow()) {
            listPattern.add(new Pattern(">> New Pattern <<", true));
            fireTableRowsInserted(listPattern.size() - 1, listPattern.size() - 1);
        }
    }*/
    private boolean hasEmptyRow() {
        Pattern p = listPattern.get(listPattern.size() - 1);
        return p.getPatternCode() != null;
    }
}
