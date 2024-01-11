/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.model.StockPaymentDetail;
import com.inventory.model.StockPaymentDetailKey;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class StockPaymentQtyTableModel extends AbstractTableModel {

    private List<StockPaymentDetail> listDetail = new ArrayList<>();
    private List<StockPaymentDetailKey> listDelete = new ArrayList<>();
    private final String[] columnNames = {"Date", "Vou No", "Contract No", "Remark",
        "Reference", "Stock Code", "Stock Name", "Qty", "Balance Qty", "Issue Qty",
        "Single Issue"};
    private JTable table;
    private SelectionObserver observer;

    public List<StockPaymentDetailKey> getListDelete() {
        return listDelete;
    }

    public void setListDelete(List<StockPaymentDetailKey> listDelete) {
        this.listDelete = listDelete;
    }

    public SelectionObserver getObserver() {
        return observer;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public JTable getTable() {
        return table;
    }

    public void setTable(JTable table) {
        this.table = table;
    }

    public StockPaymentQtyTableModel() {
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        switch (column) {
            case 9, 10 -> {
                return true;
            }
        }
        return false;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 7, 8, 9 -> {
                return Double.class;
            }
            case 10 -> {
                return Boolean.class;
            }
        }
        return String.class;
    }

    public List<StockPaymentDetail> getPaymentList() {
        listDetail.removeIf(p -> Util1.getDouble(p.getPayQty()) == 0);
        return listDetail;
    }

    public List<StockPaymentDetail> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<StockPaymentDetail> listDetail) {
        this.listDetail = listDetail;
        fireTableDataChanged();
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listDetail.isEmpty()) {
            return null;
        }
        try {
            StockPaymentDetail b = listDetail.get(row);
            return switch (column) {
                case 0 ->
                    Util1.toDateStr(b.getRefDate(), "dd/MM/yyyy");
                case 1 ->
                    b.getRefNo();
                case 2 ->
                    b.getProjectNo();
                case 3 ->
                    b.getRemark();
                case 4 ->
                    b.getReference();
                case 5 ->
                    b.getStockUserCode();
                case 6 ->
                    b.getStockName();
                case 7 ->
                    Util1.toNull(b.getQty());
                case 8 ->
                    Util1.toNull(b.getBalQty());
                case 9 ->
                    Util1.toNull(b.getPayQty());
                case 10 ->
                    b.isFullPaid();
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
                StockPaymentDetail obj = listDetail.get(row);
                switch (column) {
                    case 9 -> {
                        double qty = Util1.getDouble(value);
                        double out = Util1.getDouble(obj.getBalQty());
                        obj.setPayQty(qty);
                        obj.setFullPaid(qty == out);
                    }
                    case 10 -> {
                        if (value instanceof Boolean paid) {
                            obj.setFullPaid(paid);
                            obj.setPayQty(paid ? obj.getBalQty() : 0.0);
                        }
                    }
                }
                fireTableRowsUpdated(row, row);
                observer.selected("CAL_PAYMENT", "CAL_PAYMENT");
                table.requestFocus();
            }
        } catch (Exception e) {
            log.error("setValueAt : " + e.getMessage());
        }

    }

    public void delete(int row) {
        StockPaymentDetail pd = listDetail.get(row);
        StockPaymentDetailKey key = new StockPaymentDetailKey();
        key.setCompCode(pd.getCompCode());
        key.setVouNo(pd.getVouNo());
        listDelete.add(key);
        listDetail.remove(row);
        fireTableRowsDeleted(row, row);
    }

    public void setPayment(StockPaymentDetail t, int row) {
        listDetail.set(row, t);
        fireTableRowsUpdated(row, row);
    }

    public void addPayment(StockPaymentDetail t) {
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

    public StockPaymentDetail getPayment(int row) {
        if (listDetail == null) {
            return null;
        } else if (listDetail.isEmpty()) {
            return null;
        } else {
            return listDetail.get(row);
        }
    }

    public int getSize() {
        if (listDetail == null) {
            return 0;
        } else {
            return listDetail.size();
        }
    }

    public void clear() {
        listDetail.clear();
        listDelete.clear();
        fireTableDataChanged();
    }

    public boolean isValidEntry() {
        return listDetail.stream()
                .filter(pd -> Util1.getDouble(pd.getBalQty()) < 0)
                .peek(pd -> {
                    JOptionPane.showMessageDialog(table, "Invalid Pay Amount.");
                    int index = listDetail.indexOf(pd);
                    table.setRowSelectionInterval(index, index);
                    table.setColumnSelectionInterval(7, 7);
                    table.requestFocus();
                })
                .findFirst()
                .isEmpty();
    }

}
