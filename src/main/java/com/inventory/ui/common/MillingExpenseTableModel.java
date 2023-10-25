/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.common.Global;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.model.Expense;
import com.inventory.model.MillingExpense;
import com.inventory.model.MillingExpenseKey;
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
public class MillingExpenseTableModel extends AbstractTableModel {

    private List<MillingExpense> listDetail = new ArrayList<>();
    private final List<MillingExpenseKey> deleteList = new ArrayList();
    private final String[] columnNames = {"Code", "Name", "Qty", "Price", "Amount"};
    private JTable parent;
    private SelectionObserver observer;
    private boolean change = false;

    public void setTable(JTable parent) {
        this.parent = parent;
    }

    public MillingExpenseTableModel() {
    }

    public boolean isChange() {
        return change;
    }

    public void setChange(boolean change) {
        this.change = change;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }


    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        switch (column) {
            case 4 -> {
                return false;
            }
        }
        return true;
    }

    @Override
    public Class getColumnClass(int column) {

        return switch (column) {
            case 2, 3, 4 ->
                Float.class;
            default ->
                String.class;
        };
    }

    public List<MillingExpense> getListDetail() {
        return listDetail;
    }

    public boolean isValidEntry() {
        boolean status = true;
        for (MillingExpense sdh : listDetail) {
            if (sdh.getExpenseName() != null) {
                if (Util1.getFloat(sdh.getAmount()) <= 0) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Expense.",
                            "Invalid.", JOptionPane.ERROR_MESSAGE);
                    status = false;
                    parent.requestFocus();
                    break;
                } else if (Util1.getFloat(sdh.getPrice()) <= 0 || Util1.getFloat(sdh.getAmount()) <= 0) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Amount.");
                    status = false;
                    parent.requestFocus();
                    break;
                }
            }
        }
        return status;
    }

    public void setListDetail(List<MillingExpense> listDetail) {
        this.listDetail = listDetail;
        addNewRow();
        fireTableDataChanged();
    }

    public void removeListDetail() {
        this.listDetail.clear();
        addNewRow();
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listDetail.isEmpty()) {
            return null;
        }
        try {
            MillingExpense b = listDetail.get(row);
            return switch (column) {
                case 0 ->
                    b.getKey() == null ? null : b.getKey().getExpenseCode();
                case 1 ->
                    b.getExpenseName();
                case 2 ->
                    Util1.toNull(b.getQty());
                case 3 ->
                    Util1.toNull(b.getPrice());
                case 4 ->
                    Util1.toNull(b.getAmount());
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
            MillingExpense b = listDetail.get(row);
            if (value != null) {
                switch (column) {
                    case 0, 1 -> {
                        if (value instanceof Expense ex) {
                            MillingExpenseKey key = new MillingExpenseKey();
                            key.setCompCode(ex.getKey().getCompCode());
                            key.setExpenseCode(ex.getKey().getExpenseCode());
                            b.setKey(key);
                            b.setExpenseName(ex.getExpenseName());
                            b.setQty(1.0);
                            addNewRow();
                            setSelection(row, 2);
                        }
                    }
                    case 2 -> {
                        if (Util1.isNumber(value)) {
                            b.setQty(Util1.getFloat(value));
                            setSelection(row, column);
                        }
                    }
                    case 3 -> {
                        if (Util1.isNumber(value)) {
                            if (Util1.isPositive(Util1.getFloat(value))) {
                                b.setPrice(Util1.getFloat(value));
                                setSelection(row+1, column);
                            } else {
                                showMessageBox("Input value must be positive");
                                setSelection(row, column);
                            }
                        } else {
                            showMessageBox("Input value must be positive");
                            setSelection(row, column);
                        }
                    }
                    case 4 ->
                        b.setAmount(Util1.getFloat(value));
                }
            }
            change = true;
            calculateAmount(b);
            fireTableRowsUpdated(row, row);
            observer.selected("EXPENSE", "EXPENSE");
            parent.requestFocusInWindow();
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }

    private void setSelection(int row, int column) {
        parent.setRowSelectionInterval(row, row);
        parent.setColumnSelectionInterval(column, column);
    }

    private void showMessageBox(String text) {
        JOptionPane.showMessageDialog(Global.parentForm, text);
    }

    public void setObject(MillingExpense t, int row) {
        listDetail.set(row, t);
        fireTableRowsUpdated(row, row);
    }

    public void addObject(MillingExpense t) {
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

    public MillingExpense getExpense(int row) {
        return listDetail.get(row);
    }

    public int getSize() {
        if (listDetail == null) {
            return 0;
        } else {
            return listDetail.size();
        }
    }

    private void calculateAmount(MillingExpense s) {
        float price = Util1.getFloat(s.getPrice());
        float qty = Util1.getFloat(s.getQty());
        if (s.getKey().getExpenseCode() != null) {
            float amount = qty * price;
            s.setAmount(amount);
        }
    }

    public void addNewRow() {
        if (listDetail != null) {
            if (!hasEmptyRow()) {
                MillingExpense pd = new MillingExpense();
                listDetail.add(pd);
                fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
            }
        }
    }

    private boolean hasEmptyRow() {
        boolean status = false;
        if (listDetail.size() >= 1) {
            MillingExpense get = listDetail.get(listDetail.size() - 1);
            if (get.getKey() == null || get.getKey().getExpenseCode() == null) {
                status = true;
            }
        }
        return status;
    }

    public List<MillingExpenseKey> getDelList() {
        return deleteList;
    }

    public void clearDelList() {
        if (deleteList != null) {
            deleteList.clear();
        }
    }

    public void delete(int row) {
        MillingExpense sdh = listDetail.get(row);
        if (sdh.getKey() != null) {
            deleteList.add(sdh.getKey());
        }
        listDetail.remove(row);
        addNewRow();
        fireTableRowsDeleted(row, row);
        if (row - 1 >= 0) {
            parent.setRowSelectionInterval(row - 1, row - 1);
        } else {
            parent.setRowSelectionInterval(0, 0);
        }
        parent.requestFocus();
    }

}
