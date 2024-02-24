/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry.dialog.common;

import com.common.Util1;
import com.inventory.model.OrderNote;
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
public class OrderNoteSearchTableModel extends AbstractTableModel {

    private List<OrderNote> list = new ArrayList();
    private final String[] columnNames = {"Date", "Vou No", "Customer", "Stock Name", "Order No", "Order Name"};
    @Getter
    private double vouTotal;
    @Getter
    private double paidTotal;
    @Getter
    private double qty;
    @Getter
    private double bag;
    @Getter
    private int size;

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public int getRowCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
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
            if (!list.isEmpty()) {
                OrderNote his = list.get(row);
                switch (column) {
                    case 0 -> {
                        //date
                        return Util1.convertToLocalStorage(his.getVouDateTime());
                    }
                    case 1 -> {
                        //vou-no
                        if (his.getDeleted()) {
                            return his.getVouNo() + "***";
                        } else {
                            return his.getVouNo();
                        }
                    }
                    case 2 -> {
                        return his.getTraderName();
                    }
                    case 3 -> {
                        return his.getStockName();
                    }
                    case 4 -> {
                        return his.getOrderCode();
                    }
                    case 5 -> {
                        return his.getOrderName();
                    }
                }
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        return null;
    }

    public List<OrderNote> getList() {
        return list;
    }

    public void setList(List<OrderNote> list) {
        this.list = list;
        fireTableDataChanged();
    }

    public OrderNote getSelectVou(int row) {
        if (list != null) {
            if (!list.isEmpty()) {
                return list.get(row);
            }
        }
        return null;
    }

    public void addObject(OrderNote t) {
        list.add(t);
        size += 1;
        int lastIndex = list.size() - 1;
        if (lastIndex >= 0) {
            fireTableRowsInserted(lastIndex, lastIndex);
        } else {
            fireTableRowsInserted(0, 0);
        }
    }

    public void clear() {
        list.clear();
        vouTotal = 0;
        paidTotal = 0;
        qty = 0;
        bag = 0;
        size = 0;
        fireTableDataChanged();
    }
}
