/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.common;

import com.acc.model.ChartOfAccount;
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
public class COATableModel extends AbstractTableModel {

    private List<ChartOfAccount> listCOA = new ArrayList<>();
    private final String[] columnNames = {"Code", "Name", "Group"};

    public COATableModel() {
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

    @Override
    public Object getValueAt(int row, int column) {
        if (listCOA == null) {
            return null;
        }

        if (listCOA.isEmpty()) {
            return null;
        }

        try {
            ChartOfAccount coa = listCOA.get(row);

            return switch (column) {
                case 0 ->
                    coa.getCoaCodeUsr();
                case 1 ->
                    coa.getCoaNameEng();
                case 2 ->
                    Util1.isNull(coa.getGroupName(), "Head");
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

    @Override
    public int getRowCount() {
        if (listCOA == null) {
            return 0;
        } else {
            return listCOA.size();
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public ChartOfAccount getCOA(int row) {
        if (listCOA == null) {
            return null;
        } else if (listCOA.isEmpty()) {
            return null;
        } else {
            return listCOA.get(row);
        }
    }

    public int getSize() {
        if (listCOA == null) {
            return 0;
        } else {
            return listCOA.size();
        }
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
