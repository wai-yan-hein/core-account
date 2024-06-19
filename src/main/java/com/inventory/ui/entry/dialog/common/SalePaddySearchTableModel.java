/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry.dialog.common;

import com.common.Util1;
import com.inventory.entity.VSale;
import com.repo.InventoryRepo;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author wai yan
 */
@Slf4j
public class SalePaddySearchTableModel extends AbstractTableModel {

    private List<VSale> listSaleHis = new ArrayList();
    private final String[] columnNames = {"Date", "Vou No", "Customer", "Remark", "Ref:", "Qty", "Bag", "Paid Amt", "V-Total", "S-Pay"};
    @Getter
    private double vouTotal;
    @Getter
    private double paidTotal;
    @Getter
    private int size;
    @Getter
    private double qty;
    @Getter
    private double bag;
    @Getter
    private double discount;

    @Setter
    private InventoryRepo inventoryRepo;

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public int getRowCount() {
        if (listSaleHis == null) {
            return 0;
        }
        return listSaleHis.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 9;
    }

    @Override
    public Class getColumnClass(int column) {
        return switch (column) {
            case 5, 6, 7, 8 ->
                Double.class;
            case 9 ->
                Boolean.class;
            default ->
                String.class;
        };
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            if (!listSaleHis.isEmpty()) {
                VSale his = listSaleHis.get(row);
                switch (column) {
                    case 0 -> {
                        //date
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
                        return Util1.toNull(his.getQty());
                    }
                    case 6 -> {
                        //user
                        return Util1.toNull(his.getBag());
                    }
                    case 7 -> {
                        //paid
                        return his.getPaid();
                    }
                    case 8 -> {
                        return his.getVouTotal();
                    }
                    case 9 -> {
                        return his.isSPay();
                    }
                }
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        try {
            VSale obj = listSaleHis.get(row);
            switch (column) {
                case 9 -> {
                    if (value instanceof Boolean v) {
                        obj.setSPay(v);
                        inventoryRepo.updateSaleSPay(obj.getVouNo(), v).doOnSuccess((t) -> {
                            fireTableRowsUpdated(row, row);
                        }).subscribe();
                    }
                }
            }
        } catch (Exception e) {
            log.error("setValueAt : " + e.getMessage());
        }
    }

    public List<VSale> getListSaleHis() {
        return listSaleHis;
    }

    public void setListSaleHis(List<VSale> listSaleHis) {
        this.listSaleHis = listSaleHis;
        fireTableDataChanged();
    }

    public VSale getSelectVou(int row) {
        if (listSaleHis != null) {
            if (!listSaleHis.isEmpty()) {
                return listSaleHis.get(row);
            }
        }
        return null;
    }

    public void addObject(VSale t) {
        listSaleHis.add(t);
        vouTotal += t.getVouTotal();
        paidTotal += t.getPaid();
        qty += t.getQty();
        bag += t.getBag();
        discount +=t.getDiscount();
        size += 1;
        int lastIndex = listSaleHis.size() - 1;
        if (lastIndex >= 0) {
            fireTableRowsInserted(lastIndex, lastIndex);
        } else {
            fireTableRowsInserted(0, 0);
        }
    }

    public void clear() {
        listSaleHis.clear();
        vouTotal = 0;
        paidTotal = 0;
        qty = 0;
        bag = 0;
        size = 0;
        discount=0;
        fireTableDataChanged();
    }
}
