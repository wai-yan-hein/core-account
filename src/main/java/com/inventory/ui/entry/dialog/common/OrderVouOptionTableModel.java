/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry.dialog.common;

import com.common.Util1;
import com.inventory.entity.VOrder;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author wai yan
 */
@Slf4j
public class OrderVouOptionTableModel extends AbstractTableModel {

    private List<VOrder> listOrderHis = new ArrayList();
    private final String[] columnNames = {"Date", "Vou No", "Customer", "Remark", "Ref:", "Status", "Post", "Select"};

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
        return column == 7;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 6, 7 -> {
                return Boolean.class;
            }
        }
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            if (!listOrderHis.isEmpty()) {
                VOrder his = listOrderHis.get(row);
                switch (column) {
                    case 0 -> {                        //date
                        return Util1.convertToLocalStorage(his.getVouDateTime());
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
                        return his.getTraderName();
                    }
                    case 3 -> {
                        return his.getRemark();
                    }
                    case 4 -> {
                        return his.getReference();
                    }
                    case 5 -> {
                        //paid
                        return his.getOrderStatusName();
                    }
                    case 6 -> {
                        return his.isPost();
                    }
                    case 7 -> {
                        return his.isSelect();
                    }
                }
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        try {
            VOrder his = listOrderHis.get(rowIndex);
            switch (columnIndex) {
                case 7 -> {
                    if (aValue instanceof Boolean select) {
                        his.setSelect(select);
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    public List<VOrder> getListOrderHis() {
        return listOrderHis;
    }

    public void setListOrderHis(List<VOrder> listOrderHis) {
        this.listOrderHis = listOrderHis;
        fireTableDataChanged();
    }

    public VOrder getSelectVou(int row) {
        if (listOrderHis != null) {
            if (!listOrderHis.isEmpty()) {
                return listOrderHis.get(row);
            }
        }
        return null;
    }


    public void clear() {
        listOrderHis.clear();
        fireTableDataChanged();
    }
}
