/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.inventory.model.SaleMan;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author wai yan
 */
@Slf4j
public class SaleManCompleterTableModel extends AbstractTableModel {

    private List<SaleMan> listSaleMan = new ArrayList<>();
    private final String[] columnNames = {"Code", "Name"};

    @Override
    public int getRowCount() {
        if (listSaleMan == null) {
            return 0;
        } else {
            return listSaleMan.size();
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listSaleMan == null) {
            return null;
        }
        if (listSaleMan.isEmpty()) {
            return null;
        }
        try {
            SaleMan saleMan = listSaleMan.get(row);
            return switch (column) {
                case 0 ->
                    saleMan.getUserCode();
                case 1 ->
                    saleMan.getSaleManName();
                default ->
                    null;
            };
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Class getColumnClass(int column) {
        return String.class;
    }

    public SaleMan getSaleMan(int row) {
        return listSaleMan.get(row);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public List<SaleMan> getListSaleMan() {
        return listSaleMan;
    }

    public void setListSaleMan(List<SaleMan> listSaleMan) {
        this.listSaleMan = listSaleMan;
        fireTableDataChanged();
    }
    
}
