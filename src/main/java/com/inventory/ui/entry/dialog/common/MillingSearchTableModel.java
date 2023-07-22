/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry.dialog.common;

import com.common.Global;
import com.inventory.model.VPurchase;
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
public class MillingSearchTableModel extends AbstractTableModel {

    private List<VPurchase> listDetail = new ArrayList();
    private final String[] columnNames = {"Date", "Vou No", "Supplier", "Remark", "Load Qty", "Load Weight", "Load Amount",
        "Output Qty", "Output Weight", "Output Amount", "Weight Diff"};
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
        switch (column) {
            case 4, 5, 6, 7, 8, 9, 10 -> {
                return Float.class;
            }
        }
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            if (!listDetail.isEmpty()) {
                VPurchase his = listDetail.get(row);
                switch (column) {
                    case 0 -> {
                        //date
                        return his.getVouDate();
                    }
                    case 1 -> {
                        //vou-no
                        if (his.isDeleted()) {
                            return his.getVouNo() + "***";
                        } else {
                            return his.getVouNo();
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

    public List<VPurchase> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<VPurchase> listDetail) {
        this.listDetail = listDetail;
        fireTableDataChanged();
    }

    public VPurchase getSelectVou(int row) {
        return listDetail.get(row);
    }

    public void addObject(VPurchase t) {
        listDetail.add(t);
    }

    public void clear() {
        listDetail.clear();
        fireTableDataChanged();
    }
}
