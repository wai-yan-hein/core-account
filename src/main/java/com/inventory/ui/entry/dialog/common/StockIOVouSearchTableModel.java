/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry.dialog.common;

import com.common.Util1;
import com.inventory.model.VStockIO;
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
public class StockIOVouSearchTableModel extends AbstractTableModel {

    private List<VStockIO> listDetail = new ArrayList();
    private final String[] columnNames = {"Date", "Vou No", "Description", "Remark", "Voucher Type", "In Qty", "Out Qty"};
    @Getter
    private int size;
    @Getter
    private double inBag;
    @Getter
    private double inQty;
    @Getter
    private double outBag;
    @Getter
    private double outQty;

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
        return switch (column) {
            case 5, 6 ->
                Double.class;
            default ->
                String.class;
        };
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            VStockIO his = listDetail.get(row);

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
                    //customer
                    return his.getDescription();
                }
                case 3 -> {
                    //user
                    return his.getRemark();
                }
                case 4 -> {
                    return his.getVouTypeName();
                }
                case 5 -> {
                    //v-total
                    return his.getInQty();
                }
                case 6 -> {
                    //v-total
                    return his.getOutQty();
                }
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        return null;
    }

    public List<VStockIO> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<VStockIO> listDetail) {
        this.listDetail = listDetail;
        fireTableDataChanged();
    }

    public VStockIO getSelectVou(int row) {
        return listDetail.get(row);
    }

    public void addObject(VStockIO t) {
        listDetail.add(t);
        size += 1;
        inBag += t.getInBag();
        inQty += t.getInQty();
        outBag += t.getOutBag();
        outQty += t.getOutQty();
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
        inBag = 0;
        inQty = 0;
        outBag = 0;
        outQty = 0;
        fireTableDataChanged();
    }
}
