/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.repo.InventoryRepo;
import com.common.Global;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.entity.Stock;
import com.inventory.entity.TransferHisDetail;
import com.inventory.entity.THDetailKey;
import com.toedter.calendar.JDateChooser;
import java.awt.HeadlessException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author wai yan
 */
@Slf4j
public class TransferPaddingTableModel extends AbstractTableModel {

    private String[] columnNames = {"Stock Code", "Stock Name", "Moisture", "Head Rice", "Weight", "Qty", "Bag", "Price", "Amount"};
    private JTable parent;
    private List<TransferHisDetail> listTransfer = new ArrayList();
    private List<THDetailKey> deleteList = new ArrayList();
    private SelectionObserver observer;
    private InventoryRepo inventoryRepo;
    private JDateChooser vouDate;
    private JLabel lblRec;

    public JLabel getLblRec() {
        return lblRec;
    }

    public void setLblRec(JLabel lblRec) {
        this.lblRec = lblRec;
    }

    public JDateChooser getVouDate() {
        return vouDate;
    }

    public void setVouDate(JDateChooser vouDate) {
        this.vouDate = vouDate;
    }

    public InventoryRepo getInventoryRepo() {
        return inventoryRepo;
    }

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public SelectionObserver getObserver() {
        return observer;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    @Override
    public int getRowCount() {
        return listTransfer.size();
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            TransferHisDetail io = listTransfer.get(row);
            switch (column) {
                case 0 -> {
                    return io.getUserCode() == null ? io.getStockCode() : io.getUserCode();
                }
                case 1 -> {
                    String stockName = null;
                    if (io.getStockCode() != null) {
                        stockName = io.getStockName();
                        if (ProUtil.isStockNameWithCategory()) {
                            if (io.getCatName() != null) {
                                stockName = String.format("%s (%s)", stockName, io.getCatName());
                            }
                        }
                    }
                    return stockName;
                }
                case 2 -> {
                    return Util1.toNull(io.getWet());
                }
                case 3 -> {
                    return Util1.toNull(io.getRice());
                }
                case 4 -> {
                    return Util1.toNull(io.getWeight());
                }
                case 5 -> {
                    return Util1.toNull(io.getQty());
                }
                case 6 -> {
                    return Util1.toNull(io.getBag());
                }
                case 7 -> {
                    return Util1.toNull(io.getPrice());
                }
                case 8 -> {
                    return Util1.toNull(io.getAmount());
                }
            }
        } catch (Exception e) {
            log.error("getValueAt: " + e.getMessage()
            );
        }
        return null;
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

    @Override
    public boolean isCellEditable(int row, int column) {
        return column != 8; // column != 2;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        TransferHisDetail io = listTransfer.get(row);
        try {
            if (value != null) {
                switch (column) {
                    case 0, 1 -> {
                        if (value instanceof Stock s) {
                            io.setStockName(s.getStockName());
                            io.setStockCode(s.getKey().getStockCode());
                            io.setUserCode(s.getUserCode());
                            io.setRelName(s.getRelName());
                            io.setUnitCode("-");
                            io.setWeight(s.getWeight());
                            io.setWeightUnit(s.getWeightUnit());
                            setSelection(row, 2);
                        }
                        addNewRow();
                    }
                    case 2 -> {
                        double wet = Util1.getDouble(value);
                        if (wet > 0) {
                            io.setWet(wet);
                            setSelection(row, column + 1);
                        }
                    }
                    case 3 -> {
                        double rice = Util1.getDouble(value);
                        if (rice > 0) {
                            io.setRice(rice);
                            setSelection(row, column + 1);
                        }
                    }
                    case 4 -> { // weight
                        if (Util1.isNumber(value)) {
                            if (Util1.isPositive(Util1.getDouble(value))) {
                                io.setWeight(Util1.getDouble(value));
                                setSelection(row, column + 1);
                            } else {
                                parent.setColumnSelectionInterval(column, column);
                                JOptionPane.showMessageDialog(Global.parentForm, "Input value must be positive.");
                            }
                        } else {
                            parent.setColumnSelectionInterval(column, column);
                            JOptionPane.showMessageDialog(Global.parentForm, "Input value must be number.");
                        }
                    }
                    case 5 -> { // qty
                        if (Util1.isNumber(value)) {
                            io.setQty(Util1.getDouble(value));
                            setSelection(row, column + 1);
                        }
                    }

                    case 6 -> {
                        double bag = Util1.getDouble(value);
                        if (bag > 0) {
                            io.setBag(bag);
                            setSelection(row, column + 1);
                        }
                    }
                    case 7 -> {
                        double price = Util1.getDouble(value);
                        if (price > 0) {
                            io.setPrice(price);
                            setSelection(row + 1, 0);
                        }
                    }
                }
            }
            setRecord(listTransfer.size() - 1);
            calAmount(io);
            fireTableRowsUpdated(row, row);
            parent.requestFocus();
        } catch (HeadlessException e) {
            log.error("setValueAt :" + e.getMessage());
        }
    }

    private void calAmount(TransferHisDetail thd) {
        double amt = thd.getQty() * thd.getPrice();
        thd.setAmount(amt);
    }

    private void setRecord(int size) {
        lblRec.setText("Records : " + size);
    }

    public List<THDetailKey> getDeleteList() {
        return deleteList;
    }

    public void setDeleteList(List<THDetailKey> deleteList) {
        this.deleteList = deleteList;
    }

    private void setSelection(int row, int column) {
        parent.setRowSelectionInterval(row, row);
        parent.setColumnSelectionInterval(column, column);
    }

    public boolean isValidEntry() {
        boolean status = true;
        for (TransferHisDetail od : listTransfer) {
            if (od.getStockCode() != null) {
                if (Util1.getDouble(od.getQty()) <= 0) {
                    status = false;
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Qty.");
                    parent.requestFocus();
                }
            }
        }
        return status;

    }

    public List<TransferHisDetail> getCurrentRow() {
        return this.listTransfer;
    }

    public List<TransferHisDetail> getRetInDetailHis() {
        return this.listTransfer;
    }

    public List<TransferHisDetail> getListTransfer() {
        return listTransfer;
    }

    public void setListTransfer(List<TransferHisDetail> listTransfer) {
        this.listTransfer = listTransfer;
        setRecord(listTransfer.size());
        fireTableDataChanged();
    }

    public TransferHisDetail getStockInout(int row) {
        if (listTransfer != null) {
            return listTransfer.get(row);
        } else {
            return null;
        }
    }

    public void addNewRow() {
        if (listTransfer != null) {
            if (!hasEmptyRow()) {
                TransferHisDetail pd = new TransferHisDetail();
                listTransfer.add(pd);
                fireTableRowsInserted(listTransfer.size() - 1, listTransfer.size() - 1);
            }
        }
    }

    private boolean hasEmptyRow() {
        if (listTransfer.size() >= 1) {
            TransferHisDetail get = listTransfer.get(listTransfer.size() - 1);
            if (get.getStockCode()== null) {
                return true;
            }
        }
        return false;
    }

    public void clear() {
        if (listTransfer != null) {
            listTransfer.clear();
            fireTableDataChanged();
        }
    }

    public void delete(int row) {
        TransferHisDetail sdh = listTransfer.get(row);
        if (sdh.getKey() != null) {
            deleteList.add(sdh.getKey());
        }
        listTransfer.remove(row);
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
