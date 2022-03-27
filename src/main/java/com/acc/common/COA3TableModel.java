/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.common;

import com.acc.model.VCOALv3;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lenovo
 */
public class COA3TableModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(COA3TableModel.class);
    private List<VCOALv3> listCOA = new ArrayList<>();
    private final String[] columnNames = {"User Code", "COA Name", "COA Group", "COA Head"};

    public COA3TableModel(List<VCOALv3> listCOA) {
        this.listCOA = listCOA;
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
            VCOALv3 coa = listCOA.get(row);

            return switch (column) {
                case 0 ->
                    coa.getCoaCode();
                case 1 ->
                    coa.getCoaNameEng();
                case 2 ->
                    coa.getCoaNameEngParent2();
                case 3 ->
                    coa.getCoaNameEngParent3();
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

    public VCOALv3 getCOA(int row) {
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
}
