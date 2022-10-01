/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.common.Global;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.model.Stock;
import com.inventory.model.TransferHisDetail;
import com.inventory.model.StockUnit;
import com.toedter.calendar.JDateChooser;
import java.awt.HeadlessException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author wai yan
 */
public class TransferTableModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(TransferTableModel.class);
    private String[] columnNames = {"Stock Code", "Stock Name", "Qty", "Weight", "Amount"};
    private JTable parent;
    private List<TransferHisDetail> listTransfer = new ArrayList();
    private List<String> deleteList = new ArrayList();
    private SelectionObserver observer;
    private InventoryRepo inventoryRepo;
    private JDateChooser vouDate;

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
            return switch (column) {
                case 0 ->
                    io.getStock() == null ? null : io.getStock().getUserCode();
                case 1 ->
                    io.getStock() == null ? null : io.getStock().getStockName();
                case 2 ->
                    io.getQty();
                case 3 ->
                    Util1.getFloat(io.getWt()) == 1 ? null : Util1.getFloat(io.getWt());
                case 4 ->
                    io.getUnit() == null ? null : io.getUnit().getUnitCode();
                default ->
                    null;
            };
        } catch (Exception e) {
            log.error("getValueAt: " + e.getMessage()
            );
        }
        return null;
    }

    @Override
    public Class getColumnClass(int column) {
        return switch (column) {
            case 2,3 ->
                Float.class;
            default ->
                String.class;
        };
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return switch (column) {
            case 3 ->
                ProUtil.isWeightOption();
            default ->
                true;
        };
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        TransferHisDetail io = listTransfer.get(row);
        try {
            if (value != null) {
                switch (column) {
                    case 0,1 -> {
                        if (value instanceof Stock stock) {
                            io.setStock(stock);
                            io.setWt(1.0f);
                            io.setUnit(stock.getPurUnit());
                            setColumnSelection(2);
                        }
                        addNewRow();
                    }
                    case 2 -> {
                        if (Util1.isNumber(value)) {
                            io.setQty(Util1.getFloat(value));
                            parent.setRowSelectionInterval(row + 1, row + 1);
                            parent.setColumnSelectionInterval(0, 0);
                        }
                    }
                    case 3 -> {
                        if (Util1.isNumber(value)) {
                            io.setWt(Util1.getFloat(value));
                        }
                    }
                    case 4 -> {
                        if (value instanceof StockUnit stockUnit) {
                            io.setUnit(stockUnit);
                        }
                    }

                }
            }
            fireTableRowsUpdated(row, row);
            parent.requestFocus();
        } catch (HeadlessException e) {
            log.error("setValueAt :" + e.getMessage());
        }
    }

    public List<String> getDeleteList() {
        return deleteList;
    }

    public void setDeleteList(List<String> deleteList) {
        this.deleteList = deleteList;
    }

    private void setColumnSelection(int column) {
        parent.setColumnSelectionInterval(column, column);
        parent.requestFocus();
    }

    public boolean isValidEntry() {
        boolean status = true;
        for (TransferHisDetail od : listTransfer) {
            if (od.getStock() != null) {
                if (od.getUnit() == null) {
                    status = false;
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Unit.");
                    parent.requestFocus();
                } else if (Util1.getFloat(od.getQty()) <= 0) {
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
                pd.setWt(1.0f);
                listTransfer.add(pd);
                fireTableRowsInserted(listTransfer.size() - 1, listTransfer.size() - 1);
            }
        }
    }

    private boolean hasEmptyRow() {
        boolean status = false;
        if (listTransfer.size() >= 1) {
            TransferHisDetail get = listTransfer.get(listTransfer.size() - 1);
            if (get.getStock() == null) {
                status = true;
            }
        }
        return status;
    }

    public void clear() {
        if (listTransfer != null) {
            listTransfer.clear();
            fireTableDataChanged();
        }
    }

    public void delete(int row) {
        TransferHisDetail sdh = listTransfer.get(row);
        if (sdh.getTdCode() != null) {
            deleteList.add(sdh.getTdCode());
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
