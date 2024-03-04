/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup.common;

import com.common.Global;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.entity.OPHisDetail;
import com.inventory.entity.OPHisDetailKey;
import com.inventory.entity.Stock;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author wai yan
 */
@Slf4j
public class OpeningConsignTableModel extends AbstractTableModel {

    private String[] columnNames = {"Stock Code", "Stock Name", "Moisture", "Head Rice",
        "Weight", "Bag", "Total Weight"};
    private JTable parent;
    private List<OPHisDetail> listDetail = new ArrayList();
    private SelectionObserver observer;
    private final List<OPHisDetailKey> deleteList = new ArrayList();

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
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
            case 0,1 ->
                String.class;
            default ->
                Double.class;
        };
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column != 6;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            OPHisDetail record = listDetail.get(row);
            switch (column) {
                case 0 -> {
                    //code
                    return record.getUserCode() == null ? record.getStockCode() : record.getUserCode();
                }
                case 1 -> {
                    //Name
                    return record.getStockName();
                }
                case 2 -> {
                    return Util1.toNull(record.getWet());
                }
                case 3 -> {
                    return Util1.toNull(record.getRice());
                }
                case 4 -> {
                    return Util1.toNull(record.getWeight());
                }
                case 5 -> {
                    //bag
                    return Util1.toNull(record.getBag());
                }
                case 6 -> {
                    return Util1.toNull(record.getTotalWeight());
                }
                default -> {
                    return new Object();
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
            OPHisDetail record = listDetail.get(row);
            switch (column) {
                case 0, 1 -> {
                    //Code
                    if (value != null) {
                        if (value instanceof Stock s) {
                            record.setStockCode(s.getKey().getStockCode());
                            record.setStockName(s.getStockName());
                            record.setUserCode(s.getUserCode());
                            record.setBag(1.0);
                            record.setUnitCode(Util1.isNull(s.getPurUnitCode(), "-"));
                            record.setWeight(s.getWeight());
                            addNewRow();
                        }
                    }
                    setSelection(row, 2);
                }
                case 2 -> {
                    double wet = Util1.getDouble(value);
                    if (wet > 0) {
                        record.setWet(wet);
                        setSelection(row, column + 1);
                    }
                }
                case 3 -> {
                    double rice = Util1.getDouble(value);
                    if (rice > 0) {
                        record.setRice(rice);
                        setSelection(row, column + 1);
                    }
                }
                case 4 -> { // weight
                    double weight = Util1.getDouble(value);
                    if (weight >= 0) {
                        record.setWeight(weight);
                    }
                }
                case 5 -> {
                    double bag = Util1.getDouble(value);
                    if (bag > 0) {
                        record.setBag(bag);
                        setSelection(row, column + 1);
                    }
                }
            }
            calculatTotal(record);
            fireTableRowsUpdated(row, row);
            observer.selected("CAL-TOTAL", "CAL-TOTAL");
            parent.requestFocusInWindow();
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }

    private void setSelection(int row, int column) {
        parent.setRowSelectionInterval(row, row);
        parent.setColumnSelectionInterval(column, column);
    }

    public boolean isValidEntry() {
        for (OPHisDetail sdh : listDetail) {
            if (sdh.getStockCode() != null) {
                if (Util1.getDouble(sdh.getBag()) <= 0) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Bag.",
                            "Invalid.", JOptionPane.ERROR_MESSAGE);
                    parent.requestFocus();
                    return false;
                }
            }
        }
        return true;
    }

    public void addNewRow() {
        if (listDetail != null) {
            if (hasEmptyRow()) {
                OPHisDetail pd = new OPHisDetail();
                listDetail.add(pd);
                fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
            }
        }
    }

    private boolean hasEmptyRow() {
        boolean status = true;
        if (listDetail.size() > 1) {
            OPHisDetail get = listDetail.get(listDetail.size() - 1);
            if (get.getStockCode() == null) {
                status = false;
            }
        }
        return status;
    }

    public List<OPHisDetail> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<OPHisDetail> listDetail) {
        this.listDetail = listDetail;
        fireTableDataChanged();
    }


    private void calculatTotal(OPHisDetail pur) {
        pur.setTotalWeight(pur.getBag() * pur.getWeight());
    }


    public List<OPHisDetailKey> getDelList() {
        return deleteList;
    }

    public void clearDelList() {
        if (deleteList != null) {
            deleteList.clear();
        }
    }

    public void delete(int row) {
        OPHisDetail sdh = listDetail.get(row);
        if (sdh != null) {
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

    public void addSale(OPHisDetail sd) {
        if (listDetail != null) {
            listDetail.add(sd);
            fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
        }
    }

    public void clear() {
        if (listDetail != null) {
            listDetail.clear();
            fireTableDataChanged();
        }
    }
}
