/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry.dialog.common;

import com.common.Global;
import com.common.Util1;
import com.inventory.entity.GRN;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author wai yan
 */
@Slf4j
public class GRNHistoryTableModel extends AbstractTableModel {

    private List<GRN> listDetail = new ArrayList();
    private final String[] columnNames = {"Date", "Vou No", "Trader", "Batch No", "Remark", "Created By"};

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
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            if (!listDetail.isEmpty()) {
                GRN his = listDetail.get(row);
                switch (column) {
                    case 0 -> {
                        //date
                        return Util1.convertToLocalStorage(his.getVouDateTime());
                    }
                    case 1 -> {
                        //vou-no
                        if (his.isDeleted()) {
                            return his.getKey().getVouNo() + "***";
                        } else {
                            return his.getKey().getVouNo();
                        }
                    }
                    case 2 -> {
                        if (!Util1.isNullOrEmpty(his.getTraderUserCode())) {
                            return String.format("%s - %s", his.getTraderUserCode(), his.getTraderName());
                        }
                        //customer
                        return his.getTraderName();
                    }
                    case 3 -> {
                        return his.getBatchNo();
                    }
                    case 4 -> {
                        //user
                        return his.getRemark();
                    }
                    case 5 -> {
                        //user
                        return Global.hmUser.get(his.getCreatedBy());
                    }
                }
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        return null;
    }

    public List<GRN> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<GRN> listDetail) {
        this.listDetail = listDetail;
        fireTableDataChanged();
    }

    public GRN getSelectVou(int row) {
        if (listDetail != null) {
            if (!listDetail.isEmpty()) {
                return listDetail.get(row);
            }
        }
        return null;
    }

    public void addObject(GRN t) {
        listDetail.add(t);
    }

    public int getSize() {
        return listDetail.size();
    }

    public void clear() {
        listDetail.clear();
        fireTableDataChanged();
    }
}
