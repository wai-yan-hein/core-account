/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry.dialog.common;

import com.common.Global;
import com.common.Util1;
import com.inventory.entity.RetOutHis;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author wai yan
 */
@Slf4j
public class RetOutVouSearchTableModel extends AbstractTableModel {

    private List<RetOutHis> listDetail = new ArrayList();
    private final String[] columnNames = {"Date", "Vou No", "Customer", "Remark", "Created By", "Paid Amt", "V-Total"};

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
            case 6,5 -> {
                return Double.class;
            }
        }
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            RetOutHis his = listDetail.get(row);

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
                    //v-total
                    return his.getVouTotal();
                }
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        return null;
    }

    public List<RetOutHis> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<RetOutHis> listDetail) {
        this.listDetail = listDetail;
        fireTableDataChanged();
    }

    public RetOutHis getSelectVou(int row) {
        return listDetail.get(row);
    }
}
