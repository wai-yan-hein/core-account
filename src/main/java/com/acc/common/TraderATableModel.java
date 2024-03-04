/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.common;

import com.acc.model.TraderA;
import com.common.Util1;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class TraderATableModel extends AbstractTableModel {

    private List<TraderA> listTrader = new ArrayList<>();
    private final String[] columnNames = {"Code", "Name", "Address"};

    public TraderATableModel(List<TraderA> listTrader) {
        this.listTrader = listTrader;
    }

    public TraderATableModel() {
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Class getColumnClass(int column) {
        return String.class;
    }

    public List<TraderA> getListTrader() {
        return listTrader;
    }

    public void setListTrader(List<TraderA> listTrader) {
        this.listTrader = listTrader;
        fireTableDataChanged();
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listTrader == null) {
            return null;
        }

        if (!listTrader.isEmpty()) {
            try {
                TraderA trader = listTrader.get(row);
                return switch (column) {
                    case 0 ->
                        trader.getUserCode();
                    case 1 ->
                        trader.getTraderName();
                    case 2 ->
                        trader.getAddress();
                    default ->
                        null;
                }; //Code
                //Description
            } catch (Exception ex) {
                log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
            }
        }
        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        if (listTrader == null) {
            return 0;
        } else {
            return listTrader.size();
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public TraderA getTrader(int row) {
        if (listTrader == null) {
            return null;
        } else if (listTrader.isEmpty()) {
            return null;
        } else {
            return listTrader.get(row);
        }
    }

    public void setTrader(int row, TraderA t) {
        listTrader.set(row, t);
        fireTableRowsUpdated(row, row);
    }

    public void addTrader(TraderA t) {
        listTrader.add(t);
    }

    public void deleteTrader(int row) {
        listTrader.remove(row);
        fireTableRowsDeleted(row, row);
    }

    public int getSize() {
        if (listTrader == null) {
            return 0;
        } else {
            return listTrader.size();
        }
    }

    public void clear() {
        listTrader.clear();
        fireTableDataChanged();
    }
}
