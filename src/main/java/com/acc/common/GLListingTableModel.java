/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.common;

import com.acc.model.VTriBalance;
import com.common.Util1;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author winswe
 */
public class GLListingTableModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(GLListingTableModel.class);
    private List<VTriBalance> listTBal = new ArrayList();
    private String[] columnNames = {"Code", "Chart Of Account", "Currency", "Dr-Amt", "Cr-Amt"};

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
            case 4 ->
                Double.class;
            case 5 ->
                Double.class;
            default ->
                String.class;
        };
    }
    @Override
    public Object getValueAt(int row, int column) {

        try {
            VTriBalance apar = listTBal.get(row);
            switch (column) {
                case 0 -> {
                    return apar.getUsrCoaCode();
                }
                case 1 -> {
                    return apar.getCoaName();
                }
                case 2 -> {
                    return apar.getCurCode();
                }
                case 3 -> {
                    return Util1.getDouble(apar.getDrAmt()) == 0 ? null : apar.getDrAmt();
                }
                case 4 -> {
                    return Util1.getDouble(apar.getCrAmt()) == 0 ? null : apar.getCrAmt();

                }
            }

            //Name
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
        if (listTBal == null) {
            return 0;
        }
        return listTBal.size();
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

    public List<VTriBalance> getListTBAL() {
        return listTBal;
    }

    public void setListTBAL(List<VTriBalance> listTBal) {
        //listTBal.removeIf(gl -> gl.getDrAmt() + gl.getCrAmt() == 0);
        this.listTBal = listTBal;
        fireTableDataChanged();
    }

    public VTriBalance getTBAL(int row) {
        return listTBal.get(row);
    }

    public void deleteTBAL(int row) {
        if (!listTBal.isEmpty()) {
            listTBal.remove(row);
            fireTableRowsDeleted(0, listTBal.size());
        }

    }

    public void addTBAL(VTriBalance apar) {
        listTBal.add(apar);
        fireTableRowsInserted(listTBal.size() - 1, listTBal.size() - 1);
    }

    public void setTBAL(int row, VTriBalance apar) {
        if (!listTBal.isEmpty()) {
            listTBal.set(row, apar);
            fireTableRowsUpdated(row, row);
        }
    }

    public void clear() {
        listTBal.clear();
        fireTableDataChanged();
    }

}
