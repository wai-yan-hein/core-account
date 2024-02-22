/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.ui.common;

import com.common.Global;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.model.Location;
import com.inventory.model.OrderDetailKey;
import com.inventory.model.OrderHisDetail;
import com.inventory.ui.entry.SaleOrderEntry;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DELL
 */
public class SaleOrderTableModel extends AbstractTableModel {
    
    private static final Logger log = LoggerFactory.getLogger(SaleOrderTableModel.class);
    private String[] columnNames = {"Design", "Size", "Qty"};
    private JTable parent;
    private List<OrderHisDetail> listDetail = new ArrayList();
    private SelectionObserver observer;
    private final List<OrderDetailKey> deleteList = new ArrayList();
    private StockBalanceTableModel sbTableModel;
    private SaleOrderEntry saleOrderEntry;
    private boolean change = false;
    private JLabel lblRecord;

//    public void setOrderDynamic(OrderDynamic orderDynamic) {
//        this.orderDynamic = orderDynamic;
//    }
    public void setSbTableModel(StockBalanceTableModel sbTableModel) {
        this.sbTableModel = sbTableModel;
    }
    
    public void setLblRecord(JLabel lblRecord) {
        this.lblRecord = lblRecord;
    }
    
    public boolean isChange() {
        return change;
    }
    
    public void setChange(boolean change) {
        this.change = change;
    }
    
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
        return listDetail.size();
    }
    
    @Override
    public Class getColumnClass(int column) {
        return switch (column) {
            case 2 ->
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
            switch (column) {
                case 0 -> {
                    return sd.getDesign();
                }
                case 1 -> {
                    
                    return sd.getSize();
                }
                case 2 -> {
                    return sd.getQty();
                }
                default -> {
                    return null;
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
            OrderHisDetail sd = listDetail.get(row);
            if (value != null) {
                switch (column) {
                    case 0 -> {
                        sd.setDesign(String.valueOf(value));
                        setSelection(row, 1);
                        addNewRow();
                    }
                    case 1 -> {
                        sd.setSize(String.valueOf(value));
                        setSelection(row, 2);
                    }
                    case 2 -> {
                        //Qty
                        if (Util1.isNumber(value)) {
                            if (Util1.isPositive(Util1.getDouble(value))) {
                                sd.setQty(Util1.getDouble(value));
                            } else {
                                showMessageBox("Input value must be positive");
                                setSelection(row, column);
                            }
                        } else {
                            showMessageBox("Input value must be number.");
                            setSelection(row, column);
                        }
                    }
                    
                }
                change = true;
                calculateAmount(sd);
                fireTableRowsUpdated(row, row);
                setRecord(listDetail.size() - 1);
                observer.selected("ORDER-TOTAL", "ORDER-TOTAL");
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
        boolean status = false;
        if (listDetail.size() >= 1) {
            OrderHisDetail get = listDetail.get(listDetail.size() - 1);
            if (get.getStockCode() == null || get.getDesign() == null) {
                status = true;
            }
        }
        return status;
    }
    
    public List<OrderHisDetail> getListDetail() {
        return listDetail;
    }
    
    public void setListDetail(List<OrderHisDetail> listDetail) {
        this.listDetail = listDetail;
        setRecord(listDetail.size());
        addNewRow();
        fireTableDataChanged();
    }
    
    private void setRecord(int size) {
        lblRecord.setText("Records : " + size);
    }
    
    public void removeListDetail() {
        this.listDetail.clear();
        addNewRow();
    }
    
    private void calculateAmount(OrderHisDetail oh) {
        if (oh.getStockCode() != null) {
            double amount = Util1.getDouble(oh.getQty()) * Util1.getDouble(oh.getPrice());
            oh.setAmount(Util1.getDouble(Math.round(amount)));
        }
    }
    
    private void showMessageBox(String text) {
        JOptionPane.showMessageDialog(Global.parentForm, text);
    }
    
    public boolean isValidEntry() {
        boolean status = true;
        for (OrderHisDetail sdh : listDetail) {
            sdh.setLocCode("");
            sdh.setUnitCode("");
            if (sdh.getStockCode() != null || sdh.getDesign() != null) {
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
    
    public void clearDelList() {
        if (deleteList != null) {
            deleteList.clear();
        }
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
        }
    }
}
