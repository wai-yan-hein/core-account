/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.common;

import com.common.Global;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.model.LandingHisDetail;
import com.inventory.model.LandingHisDetailKey;
import com.inventory.model.Stock;
import com.inventory.model.StockUnit;
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
public class LandingStockTableModel extends AbstractTableModel {

    private String[] columnNames = {"Code", "Description", "Weight Total", "Weight", "Weight Unit", "Qty", "Unit", "Price", "Amount"};
    private JTable parent;
    private List<LandingHisDetail> listDetail = new ArrayList();
    private SelectionObserver observer;
    private List<LandingHisDetailKey> listDel = new ArrayList();
    private JLabel lblRec;
    private boolean editable = true;

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public void setLblRec(JLabel lblRec) {
        this.lblRec = lblRec;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public List<LandingHisDetailKey> getListDel() {
        return listDel;
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
    public int getRowCount() {
        if (listDetail == null) {
            return 0;
        }
        return listDetail.size();
    }

    @Override
    public Class getColumnClass(int column) {
        return switch (column) {
            case 2, 3, 5, 7, 8 ->
                Double.class;
            default ->
                String.class;
        };
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column != 8;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            if (!listDetail.isEmpty()) {
                LandingHisDetail record = listDetail.get(row);
                switch (column) {
                    case 0 -> {
                        //code
                        return record.getUserCode() == null ? record.getStockCode() : record.getUserCode();
                    }
                    case 1 -> {
                        //Name
                        String stockName = null;
                        if (record.getStockCode() != null) {
                            stockName = record.getStockName();
                        }
                        return stockName;
                    }
                    case 2 -> {
                        return Util1.toNull(record.getTotalWeight());
                    }
                    case 3 -> {
                        return Util1.toNull(record.getWeight());
                    }
                    case 4 -> {
                        return record.getWeightUnit();
                    }
                    case 5 -> {
                        //qty
                        return Util1.toNull(record.getQty());
                    }
                    case 6 -> {
                        //unit
                        return record.getUnit();
                    }
                    case 7 -> {
                        return Util1.toNull(record.getPrice());
                    }
                    case 8 -> {
                        return Util1.toNull(record.getAmount());
                    }
                }
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        try {
            LandingHisDetail record = listDetail.get(row);
            switch (column) {
                case 0, 1 -> {
                    //Code
                    if (value != null) {
                        if (value instanceof Stock s) {
                            record.setStockCode(s.getKey().getStockCode());
                            record.setStockName(s.getStockName());
                            record.setUserCode(s.getUserCode());
                            record.setRelName(s.getRelName());
                            record.setQty(1);
                            record.setUnit(s.getPurUnitCode());
                            record.setWeight(s.getWeight());
                            record.setWeightUnit(s.getWeightUnit());
                            record.setFormulaCode(s.getFormulaCode());
                            record.setStock(s);
                            addNewRow();
                            setSelection(row, 2);
                            observer.selected("CRITERIA", "CRITERIA");
                        }
                    }
                }
                case 2 -> {
                    //total weight
                    if (Util1.getDouble(value) > 0) {
                        record.setTotalWeight(Util1.getDouble(value));
                        setSelection(row, 7);
                    }
                }
                case 3 -> {
                    record.setWeight(Util1.getDouble(value));
                    setSelection(row, 4);
                }
                case 4 -> {
                    if (value instanceof StockUnit u) {
                        record.setWeightUnit(u.getKey().getUnitCode());
                    }
                }
                case 5 -> {
                    //Qty
                    if (Util1.getDouble(value) > 0) {
                        record.setQty(Util1.getDouble(value));
                        setSelection(row + 1, 0);
                    }

                }
                case 6 -> {
                    //Unit
                    if (value instanceof StockUnit u) {
                        record.setUnit(u.getKey().getUnitCode());
                        setSelection(row + 1, 0);
                    }
                }
                case 7 -> {
                    if (Util1.getDouble(value) > 0) {
                        record.setPrice(Util1.getDouble(value));
                    }
                }

            }
            setRecord(listDetail.size() - 1);
            calAmount(record);
            fireTableRowsUpdated(row, row);
            parent.requestFocusInWindow();
        } catch (HeadlessException ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }

    private void calAmount(LandingHisDetail g) {
        double totalWt = g.getTotalWeight();
        double weight = g.getWeight();
        double price = g.getPrice();
        if (totalWt > 0) {
            double qty = totalWt / weight;
            g.setQty(qty);
            g.setAmount(qty * price);
        }
        observer.selected("CAL_TOTAL", "CAL_TOTAL");
    }

    private void setSelection(int row, int column) {
        parent.setRowSelectionInterval(row, row);
        parent.setColumnSelectionInterval(column, column);
    }

    private void setRecord(int size) {
        if (lblRec != null) {
            lblRec.setText("Records : " + size);
        }
    }

    public void addNewRow() {
        if (listDetail != null) {
            if (!hasEmptyRow()) {
                LandingHisDetail pd = new LandingHisDetail();
                listDetail.add(pd);
                fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
            }
        }
    }

    private boolean hasEmptyRow() {
        boolean status = false;
        if (listDetail.size() >= 1) {
            LandingHisDetail get = listDetail.get(listDetail.size() - 1);
            if (get.getStockCode() == null) {
                status = true;
            }
        }
        return status;
    }

    public List<LandingHisDetail> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<LandingHisDetail> listDetail) {
        this.listDetail = listDetail;
        setRecord(listDetail.size());
        fireTableDataChanged();
    }

    private void showMessageBox(String text) {
        JOptionPane.showMessageDialog(parent, text);
    }

    public boolean isValidEntry() {
        boolean status = true;
        for (LandingHisDetail sdh : listDetail) {
            if (sdh.getStockCode() != null) {
                if (sdh.getUnit() == null || sdh.getWeightUnit() == null) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Unit.");
                    status = false;
                    parent.requestFocus();
                }
            }
        }
        return status;
    }

    public void clearDelList() {
        if (listDel != null) {
            listDel.clear();
        }
    }

    public void delete(int row) {
        LandingHisDetail sdh = listDetail.get(row);
        if (sdh.getKey() != null) {
            listDel.add(sdh.getKey());
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

    public void addObject(LandingHisDetail sd) {
        if (listDetail != null) {
            listDetail.add(sd);
            fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
        }
    }

    public LandingHisDetail getObject(int row) {
        return listDetail.get(row);
    }

    public void clear() {
        if (listDetail != null) {
            listDetail.clear();
            clearDelList();
            fireTableDataChanged();
        }
    }
}
