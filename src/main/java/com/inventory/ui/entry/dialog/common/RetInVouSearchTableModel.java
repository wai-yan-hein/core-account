/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry.dialog.common;

import com.common.Global;
import com.common.Util1;
import com.inventory.entity.RetInHis;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author wai yan
 */
@Slf4j
public class RetInVouSearchTableModel extends AbstractTableModel {

    private List<RetInHis> listDetail = new ArrayList();
    private final String[] columnNames = {"Date", "Vou No", "Customer", "Remark", "Created By", "Paid Amt", "V-Total"};
    private double vouTotal;
    private double paidTotal;
    private int size;

    public double getVouTotal() {
        return vouTotal;
    }

    public double getPaidTotal() {
        return paidTotal;
    }

    public int getSize() {
        return size;
    }

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
        switch (column) {
            case 6, 5 -> {
                return Float.class;
            }
        }
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            if (!listDetail.isEmpty()) {
                RetInHis his = listDetail.get(row);
                switch (column) {
                    case 0 -> {
                        //date
                        return Util1.convertToLocalStorage(his.getVouDateTime());
                    }
                    case 1 -> {
                        //vou-no
                        String vouNo = his.getKey().getVouNo();
                        if (his.isDeleted()) {
                            return Util1.getStar(vouNo);
                        } else {
                            return vouNo;
                        }
                    }
                    case 2 -> {
                        //customer
                        return his.getTraderName();
                    }
                    case 3 -> {
                        //remark
                        return his.getRemark();
                    }
                    case 4 -> {
                        return Global.hmUser.get(his.getCreatedBy());
                    }
                    case 5 -> {
                        //v-total
                        return his.getPaid();
                    }
                    case 6 -> {
                        return his.getVouTotal();
                    }
                }
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        return null;
    }

    public List<RetInHis> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<RetInHis> listDetail) {
        this.listDetail = listDetail;
        fireTableDataChanged();
    }

    public RetInHis getSelectVou(int row) {
        return listDetail.get(row);
    }

    public void addObject(RetInHis t) {
        listDetail.add(t);
        vouTotal += t.getVouTotal();
        paidTotal += t.getPaid();
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
        vouTotal = 0;
        paidTotal = 0;
        size = 0;
        fireTableDataChanged();
    }
}
