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
import com.inventory.model.MilingExpense;
import com.inventory.model.MilingExpenseKey;
import com.repo.InventoryRepo;
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
public class MilingExpenseTableModel extends AbstractTableModel {

    private List<MilingExpense> listDetail = new ArrayList<>();
    private final List<MilingExpenseKey> deleteList = new ArrayList();
    private final String[] columnNames = {"Code", "Name", "Qty", "Price", "Amount"};
    private JTable parent;
    private SelectionObserver selectionObserver;
    private boolean change = false;
    private InventoryRepo inventoryRepo;

    public JTable getTable() {
        return parent;
    }

    public void setTable(JTable parent) {
        this.parent = parent;
    }

    public MilingExpenseTableModel() {
    }

    public boolean isChange() {
        return change;
    }

    public void setChange(boolean change) {
        this.change = change;
    }

    public SelectionObserver getSelectionObserver() {
        return selectionObserver;
    }

    public void setSelectionObserver(SelectionObserver selectionObserver) {
        this.selectionObserver = selectionObserver;
    }

    public InventoryRepo getInventoryRepo() {
        return inventoryRepo;
    }

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        switch (column) {
            case 0, 1, 2, 3 -> {
                return true;
            }
        }
        return false;
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

    public List<MilingExpense> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<MilingExpense> listDetail) {
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
            MilingExpense b = listDetail.get(row);
            return switch (column) {
                case 0 ->
                    b.getExpenseCode();
                case 1 ->
                    b.getExpenseName();
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
            MilingExpense b = listDetail.get(row);
            if (value != null) {
                switch (column) {
                    case 0, 1 -> {
                        if (value instanceof Expense ex) {
                            MilingExpenseKey key = new MilingExpenseKey();
                            key.setCompCode(ex.getKey().getCompCode());
                            key.setExpenseCode(ex.getKey().getExpenseCode());
                            b.setKey(key);
                            b.setExpenseCode(ex.getKey().getExpenseCode());
                            b.setExpenseName(ex.getExpenseName());
                            b.setQty(1.0f);
                            addNewRow();
                            parent.setColumnSelectionInterval(1, 1);
                        }
                    }

                    case 2 -> {
                        if (Util1.isNumber(value)) {
                            if (Util1.isPositive(Util1.getFloat(value))) {
                                b.setQty(Util1.getFloat(value));
                            } else {
                                showMessageBox("Input value must be positive");
                                parent.setColumnSelectionInterval(column, column);
                            }
                        }
                    }

                    case 3 -> {
                        if (Util1.isNumber(value)) {
                            if (Util1.isPositive(Util1.getFloat(value))) {
                                b.setPrice(Util1.getFloat(value));
                            } else {
                                showMessageBox("Input value must be positive");
                                parent.setColumnSelectionInterval(column, column);
                            }
                        }
                    }
                    case 4 ->
                        b.setAmount(Util1.getFloat(value));
                }
            }
            change = true;
//            assignLocation(b);
            calculateAmount(b);
            fireTableRowsUpdated(row, row);
            selectionObserver.selected("EXPENSE", "EXPENSE");
            parent.requestFocusInWindow();
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }

    private void showMessageBox(String text) {
        JOptionPane.showMessageDialog(Global.parentForm, text);
    }

    public void setObject(MilingExpense t, int row) {
        listDetail.set(row, t);
        fireTableRowsUpdated(row, row);
    }

    public void addObject(MilingExpense t) {
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

    public MilingExpense getExpense(int row) {
        return listDetail.get(row);
    }

    public int getSize() {
        if (listDetail == null) {
            return 0;
        } else {
            return listDetail.size();
        }
    }

    private void calculateAmount(MilingExpense s) {
        float price = Util1.getFloat(s.getPrice());
        float qty = Util1.getFloat(s.getQty());
        if (s.getExpenseCode() != null) {
            float amount = qty * price;
            s.setAmount(amount);
        }
    }

    public void addNewRow() {
        if (listDetail != null) {
            if (!hasEmptyRow()) {
                MilingExpense pd = new MilingExpense();
                listDetail.add(pd);
                fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
            }
        }
    }

    private boolean hasEmptyRow() {
        boolean status = false;
        if (listDetail.size() >= 1) {
            MilingExpense get = listDetail.get(listDetail.size() - 1);
            if (get.getExpenseCode() == null) {
                status = true;
            }
        }
        return status;
    }

    public void delete(int row) {
        MilingExpense sdh = listDetail.get(row);
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
