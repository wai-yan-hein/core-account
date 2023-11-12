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
public class SPConsignorDetailTableModel extends AbstractTableModel {

    private List<ClosingBalance> listDetail = new ArrayList();
    private final String[] columnNames = {"Date", "Vou No", "Remark", "Op-Qty", "Op-Weight", "In-Qty", "In-Weight",
        "Out-Qty", "Out-Weight", "Closing-Qty", "Closing-Weight"};

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
            case 0, 1, 2 ->
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
                        return his.getVouDate();
                    }
                    case 1 -> {
                        //vou-no
                        return his.getVouNo();
                    }
                    case 2 -> {
                        //remark
                        return his.getRemark();
                    }
                    case 3 -> {
                        return his.getOpenQty();
                    }
                    case 4 -> {
                        return his.getOpenWeight();
                    }
                    case 5 -> {
                        //user
                        return his.getInQty();
                    }
                    case 6 -> {
                        //user
                        return his.getInWeight();
                    }
                    case 7 -> {
                        //user
                        return his.getOutQty();
                    }
                    case 8 -> {
                        //user
                        return his.getOutWeight();
                    }
                    case 9 -> {
                        //user
                        return his.getBalQty();
                    }
                    case 10 -> {
                        //user
                        return his.getBalWeight();
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
