/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.common;

import com.acc.model.ChartOfAccount;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lenovo
 */
public class COAHeadTableModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(COAHeadTableModel.class);
    private List<ChartOfAccount> listCoaHead = new ArrayList();
    private final String[] columnNames = {"Code", "Name"};

    @Override
    public int getRowCount() {
        if (listCoaHead == null) {
            return 0;
        }
        return listCoaHead.size();
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
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Class getColumnClass(int column) {
        return switch (column) {
            case 0 ->
                String.class;
            case 1 ->
                String.class;
            default ->
                Object.class;
        }; //case 2:
        //return Boolean.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            ChartOfAccount coa = listCoaHead.get(row);

            return switch (column) {
                case 0 -> coa.getCoaCodeUsr();
                case 1 -> coa.getCoaNameEng();
                default -> null;
            }; //Code
            //Name
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {

    }

    public List<ChartOfAccount> getListCoaHead() {
        return listCoaHead;
    }

    public void setListCoaHead(List<ChartOfAccount> listCoaHead) {
        this.listCoaHead = listCoaHead;
        fireTableDataChanged();
    }

    public ChartOfAccount getChartOfAccount(int row) {
        return listCoaHead.get(row);
    }

}
