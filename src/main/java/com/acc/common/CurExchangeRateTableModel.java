/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.common;

import com.common.Util1;
import com.user.model.CurExchange;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author winswe
 */
public class CurExchangeRateTableModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(CurExchangeRateTableModel.class);
    private List<CurExchange> listEx = new ArrayList();
    private String[] columnNames = {"Date", "H-Currency", "F-Currency", "Exchange Rate"};

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
        return switch (column) {
            case 3 ->
                Double.class;
            default ->
                String.class;
        };
    }

    @Override
    public Object getValueAt(int row, int column) {

        try {
            CurExchange apar = listEx.get(row);
            return switch (column) {
                case 0 -> {
                    yield Util1.toDateStr(apar.getExDate(), "dd/MM/yyyy");
                }
                case 1 -> {
                    yield apar.getHomeCur();
                }
                case 2 -> {
                    yield apar.getExCur();
                }
                case 3 ->
                    apar.getExRate();
                default -> {
                    yield null;
                }
            }; //date
            //des
            //ref
            //dr-amt
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {

    }

    @Override
    public int getRowCount() {
        if (listEx == null) {
            return 0;
        }
        return listEx.size();
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

    public List<CurExchange> getListEx() {
        return listEx;
    }

    public void setListEx(List<CurExchange> listEx) {
        this.listEx = listEx;
        fireTableDataChanged();
    }

    public CurExchange getEX(int row) {
        return listEx.get(row);
    }

    public void deleteEX(int row) {
        if (!listEx.isEmpty()) {
            listEx.remove(row);
            fireTableRowsDeleted(0, listEx.size());
        }

    }

    public void addEX(CurExchange apar) {
        listEx.add(apar);
        fireTableRowsInserted(listEx.size() - 1, listEx.size() - 1);
    }

    public void setEX(int row, CurExchange apar) {
        if (!listEx.isEmpty()) {
            listEx.set(row, apar);
            fireTableRowsUpdated(row, row);
        }
    }

    public void clear() {
        if (listEx != null) {
            listEx.clear();
        }
    }

}
