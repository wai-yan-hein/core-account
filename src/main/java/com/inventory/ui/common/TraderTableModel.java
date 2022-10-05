/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.common.Util1;
import com.inventory.model.Trader;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lenovo
 */
public class TraderTableModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(TraderTableModel.class);
    private List<Trader> listTrader = new ArrayList<>();
    private final String[] columnNames = {"Code", "Name", "Region"};
    private JTable table;

    public JTable getTable() {
        return table;
    }

    public void setTable(JTable table) {
        this.table = table;
    }

    public TraderTableModel() {
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

    public List<Trader> getListTrader() {
        return listTrader;
    }

    public void setListTrader(List<Trader> listTrader) {
        this.listTrader = listTrader;
        fireTableDataChanged();
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listTrader == null) {
            return null;
        }

        if (listTrader.isEmpty()) {
            return null;
        }

        try {
            Trader trader = listTrader.get(row);
            return switch (column) {
                case 0 ->
                    Util1.isNull(trader.getUserCode(), trader.getKey().getCode());
                case 1 ->
                    trader.getTraderName();
                case 2 ->
                    null;
                default ->
                    null;
            }; //Code
            //Description
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {

    }

    public void setTrader(Trader t, int row) {
        listTrader.set(row, t);
        fireTableRowsUpdated(row, row);
    }

    public void addTrader(Trader t) {
        listTrader.add(t);
        fireTableRowsInserted(listTrader.size() - 1, listTrader.size() - 1);
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

    public Trader getTrader(int row) {
        if (listTrader == null) {
            return null;
        } else if (listTrader.isEmpty()) {
            return null;
        } else {
            return listTrader.get(row);
        }
    }

    public int getSize() {
        if (listTrader == null) {
            return 0;
        } else {
            return listTrader.size();
        }
    }
}
