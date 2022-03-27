/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry.dialog.common;

import com.common.Global;
import com.common.Util1;
import com.inventory.model.VStockIO;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author wai yan
 */
@Slf4j
public class StockIOVouSearchTableModel extends AbstractTableModel {

    private List<VStockIO> listDetail = new ArrayList();
    private final String[] columnNames = {"Date", "Vou No", "Description", "Remark", "Voucher Type", "Created By"};
    private JTable parent;

    public JTable getParent() {
        return parent;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
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
            VStockIO his = listDetail.get(row);

            switch (column) {
                case 0 -> {
                    //date
                    return his.getVouDate();
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
                    return his.getDescription();
                }
                case 3 -> {
                    //user
                    return his.getRemark();
                }
                case 4 -> {
                    return his.getVouTypeName();
                }
                case 5 -> {
                    //v-total
                    return Global.hmUser.get(his.getCreatedBy());
                }
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        return null;
    }

    public List<VStockIO> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<VStockIO> listDetail) {
        this.listDetail = listDetail;
        fireTableDataChanged();
    }

    public VStockIO getSelectVou(int row) {
        return listDetail.get(row);
    }
}
