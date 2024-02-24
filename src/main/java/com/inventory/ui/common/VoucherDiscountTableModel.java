/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.model.StockUnit;
import com.inventory.model.VouDiscount;
import com.inventory.model.VouDiscountKey;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class VoucherDiscountTableModel extends AbstractTableModel {

    private List<VouDiscount> listDetail = new ArrayList<>();
    private List<VouDiscountKey> listDel = new ArrayList<>();
    private final String[] columnNames = {"Description", "Unit", "Qty", "Price", "Amount"};
    private JTable table;
    private SelectionObserver observer;

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public List<VouDiscountKey> getListDel() {
        return listDel;
    }

    public JTable getTable() {
        return table;
    }

    public void setTable(JTable table) {
        this.table = table;
    }

    public VoucherDiscountTableModel() {
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return true;
    }

    @Override
    public Class getColumnClass(int column) {
        return switch (column) {
            case 0, 1 ->
                String.class;
            default ->
                Double.class;
        };
    }

    public List<VouDiscount> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<VouDiscount> listDetail) {
        this.listDetail = listDetail;
        fireTableDataChanged();
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listDetail.isEmpty()) {
            return null;
        }
        try {
            VouDiscount b = listDetail.get(row);
            return switch (column) {
                case 0 ->
                    b.getDescription();
                case 1 ->
                    b.getUnit();
                case 2 ->
                    b.getQty();
                case 3 ->
                    b.getPrice();
                case 4 ->
                    b.getAmount();
                default ->
                    null;
            }; //Code
            //Description
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        try {
            if (value != null) {
                VouDiscount b = listDetail.get(row);
                switch (column) {
                    case 0 ->
                        b.setDescription(String.valueOf(value));
                    case 1 -> {
                        if (value instanceof StockUnit unit) {
                            b.setUnit(unit.getKey().getUnitCode());
                        }
                    }
                    case 2 ->
                        b.setQty(Util1.getDouble(value));
                    case 3 ->
                        b.setPrice(Util1.getDouble(value));
                    case 4 ->
                        b.setAmount(Util1.getDouble(value));

                }
                calAmount(b);
                addNewRow();
                fireTableRowsUpdated(row, row);
                table.requestFocus();
            }
            //Description
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }

    private void calAmount(VouDiscount d) {
        double qty = d.getQty();
        double price = d.getPrice();
        d.setAmount(qty * price);
        observer.selected("CAL_TOTAL", "CAL_TOTAL");
    }

    public void setObject(VouDiscount t, int row) {
        listDetail.set(row, t);
        fireTableRowsUpdated(row, row);
    }

    public void addObject(VouDiscount t) {
        listDetail.add(t);
        fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
    }

    @Override
    public int getRowCount() {
        if (listDetail == null) {
            return 0;
        } else {
            return listDetail.size();
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public VouDiscount getExpense(int row) {
        return listDetail.get(row);
    }

    public void addNewRow() {
        if (listDetail != null) {
            if (!hasEmptyRow()) {
                VouDiscount pd = new VouDiscount();
                VouDiscountKey key = new VouDiscountKey();
                pd.setKey(key);
                listDetail.add(pd);
                fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
            }
        }
    }

    private boolean hasEmptyRow() {
        if (listDetail.size() >= 1) {
            VouDiscount get = listDetail.get(listDetail.size() - 1);
            if (get.getAmount() == 0) {
                return true;
            }
        }
        return false;
    }

    public void delete(int row) {
        VouDiscount s = listDetail.get(row);
        if (s.getKey() != null) {
            listDel.add(s.getKey());
        }
        listDetail.remove(row);
        fireTableRowsDeleted(row, row);
    }

    public void clear() {
        listDel.clear();
        listDetail.clear();
        fireTableDataChanged();
    }

    public int getSize() {
        if (listDetail == null) {
            return 0;
        } else {
            return listDetail.size();
        }
    }
}
