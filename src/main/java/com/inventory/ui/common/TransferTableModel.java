/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.repo.InventoryRepo;
import com.common.Global;
import com.common.ProUtil;
import com.common.Util1;
import com.inventory.entity.Stock;
import com.inventory.entity.TransferHisDetail;
import com.inventory.entity.StockUnit;
import com.inventory.ui.entry.dialog.UnitChooser;
import java.awt.HeadlessException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author wai yan
 */
@Slf4j
public class TransferTableModel extends AbstractTableModel {

    private String[] columnNames = {"Stock Code", "Stock Name", "Relation", "Qty", "Unit", "Weight", "Weight Unit"};
    @Setter
    private JTable parent;
    private List<TransferHisDetail> listTransfer = new ArrayList();
    @Setter
    private JLabel lblRec;
    @Setter
    private InventoryRepo inventoryRepo;

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

                    return io.getRelName();
                }

                case 3 -> {
                    return Util1.toNull(io.getQty());
                }
                case 4 -> {
                    return io.getUnitCode();
                }
                case 5 -> {
                    return Util1.toNull(io.getWeight());
                }
                case 6 -> {
                    return io.getWeightUnit();
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
            case 3, 5 ->
                Double.class;
            default ->
                String.class;
        };
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column != 2;
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
                            io.setRelCode(s.getRelCode());
                            io.setRelName(s.getRelName());
                            io.setUnitCode(s.getPurUnitCode());
                            io.setWeight(s.getWeight());
                            io.setWeightUnit(s.getWeightUnit());
                            setSelection(row, 3);
                        }
                        addNewRow();
                    }
                    case 3 -> { // qty
                        double qty = Util1.getDouble(value);
                        if (qty > 0) {
                            io.setQty(qty);
                            String relCode = io.getRelCode();
                            if (!Util1.isNullOrEmpty(relCode)) {
                                UnitChooser chooser = new UnitChooser(inventoryRepo, relCode);
                                io.setUnitCode(chooser.getSelectUnit());
                            }
                            if (io.getUnitCode() == null) {
                                setSelection(row, 4);
                            } else {
                                setSelection(row + 1, 0);
                            }
                        }
                    }
                    case 4 -> {
                        if (value instanceof StockUnit stockUnit) {
                            io.setUnitCode(stockUnit.getKey().getUnitCode());
                        }
                    }
                    case 5 -> { // weight
                        double weight = Util1.getDouble(value);
                        if (weight > 0) {
                            io.setWeight(weight);
                            setSelection(row, column + 1);
                        }
                    }
                    case 6 -> {
                        if (value instanceof StockUnit unit) {
                            io.setWeightUnit(unit.getKey().getUnitCode());
                            setSelection(row + 1, 0);
                        }
                    }
                }
            }
            setRecord(listTransfer.size() - 1);
            fireTableRowsUpdated(row, row);
            parent.requestFocus();
        } catch (HeadlessException e) {
            log.error("setValueAt :" + e.getMessage());
        }
    }

    private void setRecord(int size) {
        lblRec.setText("Records : " + size);
    }

    private void setSelection(int row, int column) {
        parent.setRowSelectionInterval(row, row);
        parent.setColumnSelectionInterval(column, column);
    }

    public boolean isValidEntry() {
        boolean status = true;
        for (TransferHisDetail od : listTransfer) {
            if (od.getStockCode() != null) {
                if (od.getUnitCode() == null) {
                    status = false;
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Unit.");
                    parent.requestFocus();
                } else if (Util1.getDouble(od.getQty()) <= 0) {
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
            if (get.getStockCode() == null) {
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
