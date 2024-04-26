/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry.dialog.common;

import com.common.Global;
import com.common.Util1;
import com.inventory.entity.OrderHis;
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
public class OrderVouSearchTableModel extends AbstractTableModel {

    private List<OrderHis> listOrderHis = new ArrayList();
    private final String[] columnNames = {"Date", "Vou No", "Customer", "Remark", "Ref:", "Created By", "Status", "V-Total", "Post"};
    @Getter
    private int size;

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
            case 7 -> {
                return Double.class;
            }
            case 8 -> {
                return Boolean.class;
            }
        }
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            if (!listOrderHis.isEmpty()) {
                OrderHis his = listOrderHis.get(row);
                switch (column) {
                    case 0 -> {                        //date
                        return Util1.convertToLocalStorage(his.getVouDateTime());
                    }
                    case 1 -> {
                        String vouNo = his.getKey().getVouNo();
                        //vou-no
                        if (his.isDeleted()) {
                            return Util1.getStar(vouNo);
                        } else {
                            return vouNo;
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
                        return his.getOrderStatusName();
                    }
                    case 7 -> {
                        return his.getVouTotal();
                    }
                    case 8 -> {
                        return his.isPost();
                    }
                }
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        return null;
    }

    public List<OrderHis> getListOrderHis() {
        return listOrderHis;
    }

    public void setListOrderHis(List<OrderHis> listOrderHis) {
        this.listOrderHis = listOrderHis;
        fireTableDataChanged();
    }

    public OrderHis getSelectVou(int row) {
        if (listOrderHis != null) {
            if (!listOrderHis.isEmpty()) {
                return listOrderHis.get(row);
            }
        }
        return null;
    }

    public void addObject(OrderHis t) {
        listOrderHis.add(t);
        size += 1;
        int lastIndex = listOrderHis.size() - 1;
        if (lastIndex >= 0) {
            fireTableRowsInserted(lastIndex, lastIndex);
        } else {
            fireTableRowsInserted(0, 0);
        }
    }

    public void clear() {
        listOrderHis.clear();
        fireTableDataChanged();
    }
}
