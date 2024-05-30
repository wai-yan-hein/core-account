/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.ui.common;

import com.acc.model.VDescription;
import com.common.Global;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.entity.Location;
import com.inventory.entity.OrderDetailKey;
import com.inventory.entity.OrderHisDetail;
import com.inventory.entity.Stock;
import com.inventory.ui.entry.OrderDynamic;
import com.inventory.ui.entry.dialog.StockBalanceFrame;
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
 * @author DELL
 */
@Slf4j
public class OrderDesginTableModel extends AbstractTableModel {

    private String[] columnNames = {"Design", "Size", "Order Qty", "Heat Press Qty"};
    @Setter
    private JTable parent;
    private List<OrderHisDetail> listDetail = new ArrayList();
    @Setter
    private SelectionObserver observer;
    private final List<OrderDetailKey> deleteList = new ArrayList();
    @Setter
    private OrderDynamic orderDynamic;
    @Setter
    private JLabel lblRecord;


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
        return listDetail.size();
    }

    @Override
    public Class getColumnClass(int column) {
        return switch (column) {
            case 2, 3 ->
                Double.class;
            default ->
                String.class;
        };
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return true;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            OrderHisDetail sd = listDetail.get(row);
            return switch (column) {
                case 0 ->
                    sd.getDesign();
                case 1 ->
                    sd.getSize();
                case 2 ->
                    Util1.toNull(sd.getQty());
                case 3 ->
                    Util1.toNull(sd.getHeatPressQty());
                default ->
                    null;
            };
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        try {
            OrderHisDetail sd = listDetail.get(row);
            if (value != null) {
                switch (column) {
                    case 0 -> {
                        //Code
                        if (value instanceof VDescription d) {
                            sd.setDesign(d.getDescription());
                        } else {
                            sd.setDesign(value.toString());
                        }
                        addNewRow();
                        setSelection(row, column + 1);
                    }
                    case 1 -> {
                        //Code
                        if (value instanceof VDescription d) {
                            sd.setSize(d.getDescription());
                        } else {
                            sd.setSize(value.toString());
                        }
                        setSelection(row, column + 1);
                    }
                    case 2 -> {
                        double qty = Util1.getDouble(value);
                        if (qty > 0) {
                            sd.setQty(qty);
                            setSelection(row, column + 1);
                        }
                    }
                    case 3 -> {
                        double qty = Util1.getDouble(value);
                        if (qty > 0) {
                            sd.setHeatPressQty(qty);
                            setSelection(row + 1, 0);
                        }
                    }
                }
                sd.setStockCode("-");
                sd.setUnitCode("-");
                assignLocation(sd);
                calculateAmount(sd);
                fireTableRowsUpdated(row, row);
                setRecord(listDetail.size() - 1);
                parent.requestFocusInWindow();
            }
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }

    private void setSelection(int row, int column) {
        parent.setRowSelectionInterval(row, row);
        parent.setColumnSelectionInterval(column, column);
        parent.requestFocus();
    }

    private void assignLocation(OrderHisDetail sd) {
        if (sd.getLocCode() == null) {
            LocationAutoCompleter completer = orderDynamic.getLocationAutoCompleter();
            if (completer != null) {
                Location l = completer.getLocation();
                if (l != null) {
                    sd.setLocCode(l.getKey().getLocCode());
                    sd.setLocName(l.getLocName());
                }
            }
        }
    }

    public void addNewRow() {
        if (listDetail != null) {
            if (!hasEmptyRow()) {
                OrderHisDetail pd = new OrderHisDetail();
                listDetail.add(pd);
                fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
            }
        }
    }

    private boolean hasEmptyRow() {
        if (listDetail.size() >= 1) {
            OrderHisDetail get = listDetail.get(listDetail.size() - 1);
            if (Util1.isNullOrEmpty(get.getDesign())) {
                return true;
            }
        }
        return false;
    }

    public List<OrderHisDetail> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<OrderHisDetail> listDetail) {
        this.listDetail = listDetail;
        setRecord(listDetail.size());
        fireTableDataChanged();
    }

    private void setRecord(int size) {
        lblRecord.setText("Records : " + size);
    }

    private void calculateAmount(OrderHisDetail oh) {
        if (oh.getStockCode() != null) {
            double amount = Util1.getDouble(oh.getQty()) * Util1.getDouble(oh.getPrice());
            oh.setAmount(Util1.getDouble(Math.round(amount)));
            observer.selected("ORDER-TOTAL", "ORDER-TOTAL");
        }
    }

    public boolean isValidEntry() {
        boolean status = true;
        for (OrderHisDetail sdh : listDetail) {
            if (!Util1.isNullOrEmpty(sdh.getDesign())) {
                if (sdh.getLocCode() == null) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Location.");
                    status = false;
                    parent.requestFocus();
                    break;
                } else if (sdh.getUnitCode() == null) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Order Unit.");
                    status = false;
                    parent.requestFocus();
                    break;
                }
            }
        }
        return status;
    }

    public List<OrderDetailKey> getDelList() {
        return deleteList;
    }

    public void delete(int row) {
        OrderHisDetail sdh = listDetail.get(row);
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

    public void addOrder(OrderHisDetail sd) {
        if (listDetail != null) {
            listDetail.add(sd);
            fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
        }
    }

    public OrderHisDetail getOrderEntry(int row) {
        return listDetail.get(row);
    }

    public void clear() {
        if (listDetail != null) {
            listDetail.clear();
            fireTableDataChanged();
        }
    }
}
