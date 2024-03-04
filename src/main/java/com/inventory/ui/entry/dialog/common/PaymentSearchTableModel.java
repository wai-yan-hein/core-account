/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry.dialog.common;

import com.common.Util1;
import com.inventory.entity.PaymentHis;
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
public class PaymentSearchTableModel extends AbstractTableModel {

    private List<PaymentHis> listDetail = new ArrayList();
    private final String[] columnNames = {"Vou Date", "Vou No", "Name", "Remark", "Payment"};
    @Getter
    private int size;
    @Getter
    private double payment;

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
        if (column == 4) {
            return Double.class;
        }
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            if (!listDetail.isEmpty()) {
                PaymentHis his = listDetail.get(row);
                switch (column) {
                    case 0 -> {
                        //vou no
                        return Util1.convertToLocalStorage(his.getVouDateTime());
                    }
                    case 1 -> {
                        //vou date
                        return his.getVouNo();
                    }
                    case 2 -> {
                        //remark
                        return his.getTraderName();
                    }
                    case 3 -> {
                        return his.getRemark();
                    }
                    case 4 -> {
                        return his.getAmount();
                    }
                }
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        return null;
    }

    public List<PaymentHis> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<PaymentHis> listDetail) {
        this.listDetail = listDetail;
        fireTableDataChanged();
    }

    public PaymentHis getSelectVou(int row) {
        if (listDetail != null) {
            if (!listDetail.isEmpty()) {
                return listDetail.get(row);
            }
        }
        return null;
    }

    public void addObject(PaymentHis t) {
        listDetail.add(t);
        payment += t.getAmount();
        size += 1;
        int lastIndex = listDetail.size() - 1;
        if (lastIndex >= 0) {
            fireTableRowsInserted(lastIndex, lastIndex);
        } else {
            fireTableRowsInserted(0, 0);
        }
    }

    public void clear() {
        payment = 0;
        size = 0;
        listDetail.clear();
        fireTableDataChanged();
    }
}
