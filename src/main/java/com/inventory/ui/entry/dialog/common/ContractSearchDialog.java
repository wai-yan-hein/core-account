/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry.dialog.common;

import com.common.Util1;
import com.inventory.model.StockPayment;
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
public class StockPaymentSearchTableModel extends AbstractTableModel {

    private List<StockPayment> listDetail = new ArrayList();
    private final String[] columnNames = {"Vou Date", "Vou No", "Contract No", "Name", "Remark", "Qty", "Bag"};
    @Getter
    private double qty;
    @Getter
    private double bag;
    @Getter
    private int size;

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
            case 5, 6 ->
                Double.class;
            default ->
                String.class;
        };
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            if (!listDetail.isEmpty()) {
                StockPayment his = listDetail.get(row);
                switch (column) {
                    case 0 -> {
                        //vou no
                        return Util1.convertToLocalStorage(his.getVouDateTime());
                    }
                    case 1 -> {
                        if (his.isDeleted()) {
                            return Util1.getStar(his.getVouNo());
                        }
                        //vou date
                        return his.getVouNo();
                    }
                    case 2 -> {
                        //vou date
                        return his.getProjectNo();
                    }
                    case 3 -> {
                        //remark
                        return his.getTraderName();
                    }
                    case 4 -> {
                        return his.getRemark();
                    }
                    case 5 -> {
                        return his.getPayQty();
                    }
                    case 6 -> {
                        return his.getPayBag();
                    }
                }
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        return null;
    }

    public List<StockPayment> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<StockPayment> listDetail) {
        this.listDetail = listDetail;
        fireTableDataChanged();
    }

    public StockPayment getSelectVou(int row) {
        if (listDetail != null) {
            if (!listDetail.isEmpty()) {
                return listDetail.get(row);
            }
        }
        return null;
    }

    public void addObject(StockPayment t) {
        listDetail.add(t);
        qty += t.getPayQty();
        bag += t.getPayBag();
        size += 1;
        int lastIndex = listDetail.size() - 1;
        if (lastIndex >= 0) {
            fireTableRowsInserted(lastIndex, lastIndex);
        } else {
            fireTableRowsInserted(0, 0);
        }
    }

    public void clear() {
        qty = 0;
        size = 0;
        bag = 0;
        listDetail.clear();
        fireTableDataChanged();
    }
}
