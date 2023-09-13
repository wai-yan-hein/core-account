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
public class TraderAReportTableModel extends AbstractTableModel {

    private List<TraderA> listTrader = new ArrayList<>();
    private final String[] columnNames = {"Code", "Name", "Active"};

    public TraderAReportTableModel(List<TraderA> listTrader) {
        this.listTrader = listTrader;
    }

    public TraderAReportTableModel() {
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 2;
    }

    @Override
    public Class getColumnClass(int column) {
        return column == 2 ? Boolean.class : String.class;
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
                        Util1.isNull(trader.getUserCode(), trader.getKey().getCode());
                    case 1 ->
                        trader.getTraderName();
                    case 2 ->
                        trader.isActive();
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
        try {
            if (!listTrader.isEmpty()) {
                TraderA coa = listTrader.get(row);
                if (value != null) {
                    switch (column) {
                        case 3 ->
                            coa.setActive((Boolean) value);
                    }
                }
                fireTableRowsUpdated(row, row);
            }
        } catch (Exception e) {
            log.error("setValueAt : " + e.getMessage());
        }
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
