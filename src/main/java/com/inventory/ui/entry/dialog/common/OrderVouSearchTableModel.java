/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry.dialog.common;

import com.common.Global;
import com.common.Util1;
import com.inventory.model.VSale;
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
public class OrderVouSearchTableModel extends AbstractTableModel {

    private List<VSale> listOrderHis = new ArrayList();
    private final String[] columnNames = {"Date", "Vou No", "Customer", "Remark", "Ref:", "Created By", "Paid Amt", "V-Total"};

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
        if (listOrderHis == null) {
            return 0;
        }
        return listOrderHis.size();
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
            case 6, 7 -> {
                return Float.class;
            }
        }
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            if (!listOrderHis.isEmpty()) {
                VSale his = listOrderHis.get(row);
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
                        if (!Util1.isNullOrEmpty(his.getTraderCode())) {
                            return String.format("%s - %s", his.getTraderCode(), his.getTraderName());
                        }
                        //customer
                        return his.getTraderName();
                    }
                    case 3 -> {
                        //user
                        return his.getRemark();
                    }
                    case 4 -> {
                        return his.getReference();
                    }
                    case 5 -> {
                        //user
                        return Global.hmUser.get(his.getCreatedBy());
                    }
                    case 6 -> {
                        //paid
                        return his.getPaid();
                    }
                    case 7 -> {
                        return his.getVouTotal();
                    }
                }
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        return null;
    }

    public List<VSale> getListOrderHis() {
        return listOrderHis;
    }

    public void setListOrderHis(List<VSale> listOrderHis) {
        this.listOrderHis = listOrderHis;
        fireTableDataChanged();
    }

    public VSale getSelectVou(int row) {
        if (listOrderHis != null) {
            if (!listOrderHis.isEmpty()) {
                return listOrderHis.get(row);
            }
        }
        return null;
    }

    public void addObject(VSale t) {
        listOrderHis.add(t);

    }

    public void clear() {
        listOrderHis.clear();
        fireTableDataChanged();
    }
}