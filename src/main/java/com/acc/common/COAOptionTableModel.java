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
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class COAOptionTableModel extends AbstractTableModel {

    private List<ChartOfAccount> listCOA = new ArrayList();
    private final String[] columnNames = {"Code", "Name", "Group", "Select"};

    @Override
    public int getRowCount() {
        if (listCOA == null) {
            return 0;
        }
        return listCOA.size();
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
        return column == 3;
    }

    @Override
    public Class getColumnClass(int column) {
        return switch (column) {
            case 0, 1, 2 ->
                String.class;
            case 3 ->
                Boolean.class;
            default ->
                Object.class;
        }; //case 2:
        //return Boolean.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            ChartOfAccount coa = listCOA.get(row);

            return switch (column) {
                case 0 ->
                    coa.getKey().getCoaCode();
                case 1 ->
                    coa.getCoaNameEng();
                case 2 ->
                    coa.getGroupName();
                case 3 ->
                    coa.isActive();
                default ->
                    null;
            }; //Code
            //Name
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        try {
            if (!listCOA.isEmpty()) {
                ChartOfAccount coa = listCOA.get(row);
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

    public ChartOfAccount getChartOfAccount(int row) {
        return listCOA.get(row);
    }

    public void clear() {
        listCOA.clear();
        fireTableDataChanged();
    }

    public List<ChartOfAccount> getListCOA() {
        return listCOA;
    }

    public void setListCOA(List<ChartOfAccount> listCOA) {
        this.listCOA = listCOA;
        fireTableDataChanged();
    }
    

}
