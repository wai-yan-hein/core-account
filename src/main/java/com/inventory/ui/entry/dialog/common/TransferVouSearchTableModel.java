/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry.dialog.common;

import com.common.Global;
import com.common.Util1;
import com.inventory.entity.VTransfer;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author wai yan
 */
@Slf4j
public class TransferVouSearchTableModel extends AbstractTableModel {

    private List<VTransfer> listDetail = new ArrayList();
    private final String[] columnNames = {"Date", "Vou No", "Location From", "Location To", "Remark", "Ref No", "Trader", "Created By"};
    private int size;

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
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            VTransfer his = listDetail.get(row);

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
                    return his.getFromLocationName();
                }
                case 3 -> {
                    //user
                    return his.getToLocationName();
                }
                case 4 -> {
                    return his.getRemark();
                }
                case 5 -> {
                    return his.getRefNo();
                }
                case 6 -> {
                    return his.getTraderName();
                }
                case 7 -> {
                    return Global.hmUser.get(his.getCreatedBy());
                }
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        return null;
    }

    public List<VTransfer> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<VTransfer> listDetail) {
        this.listDetail = listDetail;
        fireTableDataChanged();
    }

    public VTransfer getSelectVou(int row) {
        return listDetail.get(row);
    }

    public void addObject(VTransfer t) {
        listDetail.add(t);
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
        size = 0;
        fireTableDataChanged();
    }
}
