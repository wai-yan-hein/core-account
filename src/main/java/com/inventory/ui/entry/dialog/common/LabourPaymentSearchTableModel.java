/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry.dialog.common;

import com.common.Util1;
import com.inventory.entity.LabourPaymentDto;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author wai yan
 */
@Slf4j
public class LabourPaymentSearchTableModel extends AbstractTableModel {

    private List<LabourPaymentDto> listDetail = new ArrayList();
    private final String[] columnNames = {"Pay Date", "Vou No", "Remark", "Labour Group", "Member", "Payment", "Post"};
    private double paidTotal;
    private int size;

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
        switch (column) {
            case 4, 5 -> {
                return Double.class;
            }
            default -> {
                return String.class;
            }

        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            if (!listDetail.isEmpty()) {
                LabourPaymentDto his = listDetail.get(row);
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
                        return his.getRemark();
                    }
                    case 3 -> {
                        return his.getLabourName();
                    }
                    case 4 -> {
                        return his.getMemberCount();
                    }
                    case 5 -> {
                        //user
                        return his.getPayTotal();

                    }
                    case 6 -> {
                        return his.isPost();
                    }
                }
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        return null;
    }

    public List<LabourPaymentDto> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<LabourPaymentDto> listDetail) {
        this.listDetail = listDetail;
        fireTableDataChanged();
    }

    public LabourPaymentDto getSelectVou(int row) {
        if (listDetail != null) {
            if (!listDetail.isEmpty()) {
                return listDetail.get(row);
            }
        }
        return null;
    }

    public void addObject(LabourPaymentDto t) {
        listDetail.add(t);
        paidTotal += t.getPayTotal();
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
        paidTotal = 0;
        size = 0;
        fireTableDataChanged();
    }
}
