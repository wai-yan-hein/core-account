/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry.dialog.common;

import com.common.Global;
import com.common.Util1;
import com.inventory.model.VPurOrder;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author pann
 */
@Slf4j
public class PurOrderHisVouSearchTableModel extends AbstractTableModel {

    private List<VPurOrder> listDetail = new ArrayList();
    private final String[] columnNames = {"Date", "Vou No", "Location","Remark", "Trader", "Created By"};

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
            VPurOrder his = listDetail.get(row);
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
                    return his.getLocation();
                }
                case 3 -> {
                    return his.getRemark();
                }
                case 4 -> {
                    return his.getTraderName();
                }
                case 5 -> {
                    return Global.hmUser.get(his.getCreatedBy());
                }
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        return null;
    }

    public List<VPurOrder> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<VPurOrder> listDetail) {
        this.listDetail = listDetail;
        fireTableDataChanged();
    }

    public VPurOrder getSelectVou(int row) {
        return listDetail.get(row);
    }

    public void clear() {
        listDetail.clear();
        fireTableDataChanged();
    }
}
