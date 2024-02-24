/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry.dialog.common;

import com.common.Global;
import com.common.Util1;
import com.inventory.model.MillingHis;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author wai yan
 */
@Slf4j
public class MillingSearchTableModel extends AbstractTableModel {

    private List<MillingHis> listDetail = new ArrayList();
    private final String[] columnNames = {"Date", "Vou No", "Trader", "Remark", "Created By", "Reference", "Process Type"};

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
            if (!listDetail.isEmpty()) {
                MillingHis his = listDetail.get(row);
                switch (column) {
                    case 0 -> {
                        //date
                        return Util1.convertToLocalStorage(his.getVouDateTime());
                    }
                    case 1 -> {
                        //vou-no
                        if (his.isDeleted()) {
                            return his.getKey().getVouNo()+ "***";
                        } else {
                            return his.getKey().getVouNo();
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
                        //user
                    }
                    case 5 -> {
                        //v-total
                        return his.getReference();
                    }
                    case 6 -> {
                        return his.getProcessType();
                    }
                }
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        return null;
    }

    public List<MillingHis> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<MillingHis> listDetail) {
        this.listDetail = listDetail;
        fireTableDataChanged();
    }

    public MillingHis getSelectVou(int row) {
        return listDetail.get(row);
    }

    public void addObject(MillingHis t) {
        listDetail.add(t);
    }

    public void clear() {
        listDetail.clear();
        fireTableDataChanged();
    }
}
