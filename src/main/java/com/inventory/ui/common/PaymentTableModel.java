/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.model.PaymentHisDetail;
import com.inventory.model.PaymentHisDetailKey;
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
public class PaymentTableModel extends AbstractTableModel {

    private List<PaymentHisDetail> listDetail = new ArrayList<>();
    private List<PaymentHisDetailKey> listDelete = new ArrayList<>();
    private final String[] columnNames = {"Date", "Vou No", "Remark", "Reference", "Currency", "Vou Total", "Outstanding",
        "Partial Payment", "Single Payment"};
    private JTable table;
    private SelectionObserver observer;

    public List<PaymentHisDetailKey> getListDelete() {
        return listDelete;
    }

    public void setListDelete(List<PaymentHisDetailKey> listDelete) {
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

    public PaymentTableModel() {
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        switch (column) {
            case 7, 8 -> {
                return true;
            }
        }
        return false;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 5, 6, 7 -> {
                return Double.class;
            }
            case 8 -> {
                return Boolean.class;
            }
        }
        return String.class;
    }

    public List<PaymentHisDetail> getPaymentList() {
        List<PaymentHisDetail> listFilter = listDetail.stream().filter((t) -> Util1.getDouble(t.getPayAmt()) > 0).toList();
        return listFilter;

    }

    public List<PaymentHisDetail> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<PaymentHisDetail> listDetail) {
        this.listDetail = listDetail;
        fireTableDataChanged();
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listDetail.isEmpty()) {
            return null;
        }
        try {
            PaymentHisDetail b = listDetail.get(row);
            return switch (column) {
                case 0 ->
                    Util1.toDateStr(b.getSaleDate(), "dd/MM/yyyy");
                case 1 ->
                    b.getSaleVouNo();
                case 2 ->
                    b.getRemark();
                case 3 ->
                    b.getReference();
                case 4 ->
                    b.getCurCode();
                case 5 ->
                    b.getVouTotal();
                case 6 ->
                    b.getVouBalance();
                case 7 ->
                    b.getPayAmt();
                case 8 ->
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
                PaymentHisDetail obj = listDetail.get(row);
                switch (column) {
                    case 7 -> {
                        double amt = Util1.getDouble(value);
                        double out = Util1.getDouble(obj.getVouBalance());
                        obj.setPayAmt(amt);
                        obj.setFullPaid(amt == out);
                    }
                    case 8 -> {
                        if (value instanceof Boolean paid) {
                            obj.setFullPaid(paid);
                            obj.setPayAmt(paid ? obj.getVouBalance() : 0.0f);
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
        PaymentHisDetail pd = listDetail.get(row);
        listDelete.add(pd.getKey());
        listDetail.remove(row);
        fireTableRowsDeleted(row, row);
    }

    public void setPayment(PaymentHisDetail t, int row) {
        listDetail.set(row, t);
        fireTableRowsUpdated(row, row);
    }

    public void addPayment(PaymentHisDetail t) {
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

    public PaymentHisDetail getPayment(int row) {
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
                .filter(pd -> Util1.getDouble(pd.getVouBalance()) < 0)
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
