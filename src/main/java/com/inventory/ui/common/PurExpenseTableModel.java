/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.entity.Expense;
import com.inventory.entity.PurExpense;
import com.inventory.entity.PurExpenseKey;
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
public class PurExpenseTableModel extends AbstractTableModel {

    private List<PurExpense> listDetail = new ArrayList<>();
    private final String[] columnNames = {"Name", "Amt", "%"};
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

    public PurExpenseTableModel() {
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
            case 1, 2 -> {
                return Float.class;
            }
        }
        return null;
    }

    public List<PurExpense> getExpenseList() {
        return listDetail.stream().filter((t) -> t.getAmount() != 0).toList();
    }

    public List<PurExpense> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<PurExpense> listDetail) {
        this.listDetail = listDetail;
        fireTableDataChanged();
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listDetail.isEmpty()) {
            return null;
        }
        try {
            PurExpense b = listDetail.get(row);
            return switch (column) {
                case 0 ->
                    b.getExpenseName();
                case 1 ->
                    Util1.toNull(b.getAmount());
                case 2 ->
                    Util1.toNull(b.getPercent());
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
                    PurExpense e = listDetail.get(row);
                    switch (column) {
                        case 0 -> {
                            if (value instanceof Expense ex) {
                                PurExpenseKey key = new PurExpenseKey();
                                key.setCompCode(ex.getKey().getCompCode());
                                key.setExpenseCode(ex.getKey().getExpenseCode());
                                e.setKey(key);
                                e.setExpenseName(ex.getExpenseName());
                                addNewRow();
                                table.setColumnSelectionInterval(1, 1);
                            }
                        }
                        case 1 -> {
                            double amt = Util1.getDouble(value);
                            if (amt > 0) {
                                e.setAmount(amt);
                                e.setPercent(0);
                                checkAndFocus(row + 1);
                            }
                        }
                        case 2 -> {
                            double percent = Util1.getDouble(value);
                            if (percent > 0) {
                                if (percent <= 100) {
                                    e.setPercent(percent);
                                    if (percent > 0) {
                                        double vouTotal = Util1.getDouble(txtVouTotal.getValue());
                                        e.setAmount(vouTotal * (percent / 100));
                                    } else {
                                        e.setAmount(0.0);
                                    }
                                }
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

    public void setObject(PurExpense t, int row) {
        listDetail.set(row, t);
        fireTableRowsUpdated(row, row);
    }

    public void addObject(PurExpense t) {
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

    public PurExpense getBatch(int row) {
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
            PurExpense get = listDetail.get(listDetail.size() - 1);
            if (get.getKey().getExpenseCode() == null) {
                status = true;
            }
        }
        return status;
    }

    public void addNewRow() {
        if (listDetail != null) {
            if (!hasEmptyRow()) {
                PurExpense pd = new PurExpense();
                PurExpenseKey key = new PurExpenseKey();
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
