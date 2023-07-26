/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.model.Expense;
import com.inventory.model.SaleExpense;
import com.inventory.model.SaleExpenseKey;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class SaleExpenseTableModel extends AbstractTableModel {

    private List<SaleExpense> listDetail = new ArrayList<>();
    private final String[] columnNames = {"Name", "Amount"};
    private JTable table;
    private SelectionObserver observer;
    private JFormattedTextField txtVouTotal;

    public JFormattedTextField getTxtVouTotal() {
        return txtVouTotal;
    }

    public void setTxtVouTotal(JFormattedTextField txtVouTotal) {
        this.txtVouTotal = txtVouTotal;
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

    public SaleExpenseTableModel() {
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
        switch (column) {
            case 0 -> {
                return String.class;
            }
            case 1 -> {
                return Float.class;
            }
        }
        return null;
    }

    public List<SaleExpense> getExpenseList() {
        listDetail.removeIf((e) -> Util1.getFloat(e.getAmount()) == 0);
        return listDetail;
    }

    public List<SaleExpense> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<SaleExpense> listDetail) {
        this.listDetail = listDetail;
        fireTableDataChanged();
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listDetail.isEmpty()) {
            return null;
        }
        try {
            SaleExpense b = listDetail.get(row);
            return switch (column) {
                case 0 ->
                    b.getExpenseName();
                case 1 ->
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
        if (!listDetail.isEmpty()) {
            if (value != null) {
                try {
                    SaleExpense e = listDetail.get(row);
                    switch (column) {
                        case 0 -> {
                            if (value instanceof Expense ex) {
                                SaleExpenseKey key = new SaleExpenseKey();
                                key.setCompCode(ex.getKey().getCompCode());
                                key.setExpenseCode(ex.getKey().getExpenseCode());
                                e.setKey(key);
                                e.setExpenseName(ex.getExpenseName());
                                addNewRow();
                                table.setColumnSelectionInterval(1, 1);
                            }
                        }
                        case 1 -> {
                            if (Util1.isNumber(value)) {
                                e.setAmount(Util1.getFloat(value));
                                checkAndFocus(row + 1);
                            }
                        }
                    }
                    fireTableRowsUpdated(row, row);
                    observer.selected("CAL-TOTAL", "CAL-TOTAL");
                    table.requestFocus();
                } catch (Exception e) {
                    log.error("setValueAt : " + e.getMessage());
                }
            }
        }
    }

    private void checkAndFocus(int row) {
        int count = table.getSelectedRowCount();
        if (row <= count) {
            table.setRowSelectionInterval(row, row);
        } else {
            table.setRowSelectionInterval(row - 1, row - 1);
        }
        table.setColumnSelectionInterval(1, 1);

    }

    public void setObject(SaleExpense t, int row) {
        listDetail.set(row, t);
        fireTableRowsUpdated(row, row);
    }

    public void addObject(SaleExpense t) {
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

    public SaleExpense getBatch(int row) {
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

    private boolean hasEmptyRow() {
        boolean status = false;
        if (listDetail.size() >= 1) {
            SaleExpense get = listDetail.get(listDetail.size() - 1);
            if (get.getKey().getExpenseCode() == null) {
                status = true;
            }
        }
        return status;
    }

    public void addNewRow() {
        if (listDetail != null) {
            if (!hasEmptyRow()) {
                SaleExpense pd = new SaleExpense();
                SaleExpenseKey key = new SaleExpenseKey();
                pd.setKey(key);
                listDetail.add(pd);
                fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
            }
        }
    }

    public void clear() {
        listDetail.clear();
        fireTableDataChanged();
    }
}
