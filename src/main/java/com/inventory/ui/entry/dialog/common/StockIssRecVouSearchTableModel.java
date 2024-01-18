/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry.dialog.common;

import com.common.Util1;
import com.inventory.model.VConsign;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author wai yan
 */
@Slf4j
public class StockIssRecVouSearchTableModel extends AbstractTableModel {

    private List<VConsign> listDetail = new ArrayList();
    private final String[] columnNames = {"Date", "Vou No", "Name", "Remark", "Location", "Total Bag"};
    @Getter
    private int size;
    @Getter
    private double bag;

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public int getRowCount() {
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
        if (column == 5) {
            return Double.class;
        }
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            VConsign his = listDetail.get(row);
            switch (column) {
                case 0 -> {
                    //date
                    return Util1.convertToLocalStorage(his.getVouDateTime());
                }
                case 1 -> {
                    //vou-no
                    if (Util1.getBoolean(his.isDeleted())) {
                        return his.getVouNo() + "***";
                    } else {
                        return his.getVouNo();
                    }
                }
                case 2 -> {
                    return his.getTraderName();
                }
                case 3 -> {
                    return his.getRemark();
                }
                case 4 -> {
                    return his.getLocation();
                }
                case 5 -> {
                    return his.getBag();
                }
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        return null;
    }

    public List<VConsign> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<VConsign> listDetail) {
        this.listDetail = listDetail;
        fireTableDataChanged();
    }

    public VConsign getSelectVou(int row) {
        return listDetail.get(row);
    }

    public void addObject(VConsign t) {
        listDetail.add(t);
        size += 1;
        bag += t.getBag();
        int lastIndex = listDetail.size() - 1;
        if (lastIndex >= 0) {
            fireTableRowsInserted(lastIndex, lastIndex);
        } else {
            fireTableRowsInserted(0, 0);
        }
    }

    public void clear() {
        listDetail.clear();
        size = 0;
        bag = 0;
        fireTableDataChanged();
    }
}
