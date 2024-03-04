/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry.dialog.common;

import com.common.Util1;
import com.inventory.entity.WeightHis;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author wai yan
 */
@Slf4j
public class WeightHistoryTableModel extends AbstractTableModel {

    private List<WeightHis> listDetail = new ArrayList();
    private final String[] columnNames = {"Date", "Vou No", "Supplier", "Description", "Remark", "Type", "Stock Name", "Weight", "Qty", "Bag", "Post"};
    private double totalQty;
    private double totalBag;
    private int size;

    public int getSize() {
        return size;
    }

    public double getTotalQty() {
        return totalQty;
    }

    public double getTotalBag() {
        return totalBag;
    }

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
            case 7, 8, 9 ->
                Double.class;
            default ->
                String.class;
        };
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            if (!listDetail.isEmpty()) {
                WeightHis his = listDetail.get(row);
                switch (column) {
                    case 0 -> {
                        //date
                        return Util1.convertToLocalStorage(his.getVouDateTime());
                    }
                    case 1 -> {
                        //vou-no
                        if (his.isDeleted()) {
                            return his.getKey().getVouNo() + "***";
                        } else {
                            return his.getKey().getVouNo();
                        }
                    }
                    case 2 -> {
                        //customer
                        return his.getTraderName();
                    }
                    case 3 -> {
                        return his.getDescription();
                    }
                    case 4 -> {
                        //user
                        return his.getRemark();
                    }
                    case 5 -> {
                        return his.getTranSource();
                    }
                    case 6 -> {
                        return his.getStockName();
                    }
                    case 7 -> {
                        return his.getTotalWeight();
                    }
                    case 8 -> {
                        return his.getTotalQty();
                    }
                    case 9 -> {
                        return his.getTotalBag();
                    }
                    case 10 -> {
                        return his.isPost();
                    }
                }
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        return null;
    }

    public List<WeightHis> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<WeightHis> listDetail) {
        this.listDetail = listDetail;
        fireTableDataChanged();
    }

    public WeightHis getSelectVou(int row) {
        if (listDetail != null) {
            if (!listDetail.isEmpty()) {
                return listDetail.get(row);
            }
        }
        return null;
    }

    public void addObject(WeightHis t) {
        listDetail.add(t);
        totalBag += t.getTotalBag();
        totalQty += t.getTotalQty();
        size += 1;
        int lastIndex = listDetail.size() - 1;
        if (lastIndex >= 0) {
            fireTableRowsInserted(lastIndex, lastIndex);
        } else {
            fireTableRowsInserted(0, 0);
        }
    }

    public void clear() {
        listDetail.clear();
        totalBag = 0;
        totalQty = 0;
        size = 0;
        fireTableDataChanged();
    }
}
