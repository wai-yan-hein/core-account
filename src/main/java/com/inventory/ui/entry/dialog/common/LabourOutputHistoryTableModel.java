/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry.dialog.common;

import com.common.Util1;
import com.inventory.entity.LabourOutput;
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
public class LabourOutputHistoryTableModel extends AbstractTableModel {

    private List<LabourOutput> listDetail = new ArrayList();
    private final String[] columnNames = {"Date", "Vou No", "Remark", "Output Qty", "Reject Qty", "Amount"};
    @Getter
    private int size;
    @Getter
    private double outputQty;
    @Getter
    private double rejectQty;
    @Getter
    private double amount;

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
            case 3, 4, 5 ->
                Double.class;
            default ->
                String.class;
        };
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            LabourOutput his = listDetail.get(row);

            switch (column) {
                case 0 -> {
                    //date
                    return Util1.convertToLocalStorage(his.getVouDateTime());
                }
                case 1 -> {
                    //vou-no
                    String vouNo = his.getVouNo();
                    if (his.isDeleted()) {
                        return Util1.getStar(vouNo);
                    } else {
                        return vouNo;
                    }
                }
                case 2 -> {
                    //remark
                    return his.getRemark();
                }
                case 3 -> {
                    //user
                    return his.getOutputQty();
                }
                case 4 -> {
                    return his.getRejectQty();
                }
                case 5 -> {
                    //v-total
                    return his.getAmount();
                }
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        return null;
    }

    public List<LabourOutput> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<LabourOutput> listDetail) {
        this.listDetail = listDetail;
        fireTableDataChanged();
    }

    public LabourOutput getSelectVou(int row) {
        return listDetail.get(row);
    }

    public void addObject(LabourOutput t) {
        listDetail.add(t);
        size += 1;
        outputQty += t.getOutputQty();
        rejectQty += t.getRejectQty();
        amount += t.getAmount();
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
        outputQty = 0;
        rejectQty = 0;
        amount = 0;
        fireTableDataChanged();
    }
}
