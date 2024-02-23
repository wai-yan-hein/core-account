/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ui.management.common;

import com.ui.management.model.ClosingBalance;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author wai yan
 */
@Slf4j
public class SBSummaryTableModel extends AbstractTableModel {

    private List<ClosingBalance> listDetail = new ArrayList();
    private final String[] columnNames = {"Code", "Description", "Op-Qty",
        "Pur-Qty", "In-Qty", "Sale-Qty", "Out-Qty", "Closing-Qty"};

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public int getRowCount() {
        if (listDetail == null) {
            return 0;
        }
        return listDetail.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Class getColumnClass(int column) {
        return switch (column) {
            case 0, 1 ->
                String.class;
            default ->
                Double.class;
        };
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            if (!listDetail.isEmpty()) {
                ClosingBalance his = listDetail.get(row);
                switch (column) {
                    case 0 -> {
                        //date
                        return his.getStockUsrCode();
                    }
                    case 1 -> {
                        //vou-no
                        return his.getStockName();
                    }
                    case 2 -> {
                        return his.getOpenQty();
                    }
                    case 3 -> {
                        //user
                        return his.getPurQty();
                    }
                    case 4 -> {
                        //user
                        return his.getInQty();
                    }
                    case 5 -> {
                        //user
                        return his.getSaleQty();
                    }

                    case 6 -> {
                        //user
                        return his.getOutQty();
                    }

                    case 7 -> {
                        //user
                        return his.getBalQty();
                    }
                }
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        return null;
    }

    public List<ClosingBalance> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<ClosingBalance> listDetail) {
        this.listDetail = listDetail;
        fireTableDataChanged();
    }

    public ClosingBalance getSelectVou(int row) {
        if (listDetail != null) {
            if (!listDetail.isEmpty()) {
                return listDetail.get(row);
            }
        }
        return null;
    }

    public void addObject(ClosingBalance t) {
        listDetail.add(t);
    }

    public int getSize() {
        return listDetail.size();
    }

    public void clear() {
        listDetail.clear();
        fireTableDataChanged();
    }
}
