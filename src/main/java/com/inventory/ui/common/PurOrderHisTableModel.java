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
import com.inventory.entity.PurOrderHisDetail;
import com.inventory.entity.PurOrderHisDetailKey;
import com.toedter.calendar.JDateChooser;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author pann
 */
@Slf4j
public class PurOrderHisTableModel extends AbstractTableModel {

    private String[] columnNames = {"Code", "Stock Name", "Moisture", "Hard Rice", "Weight",
        "Qty", "Bag", "Price", "Amount"};
    private JTable parent;
    private List<PurOrderHisDetail> listDetail = new ArrayList();
    private SelectionObserver observer;
    private final List<PurOrderHisDetailKey> deleteList = new ArrayList();
    private InventoryRepo inventoryRepo;
    private JDateChooser vouDate;
    private JDateChooser dueDate;
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
    public JDateChooser getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(JDateChooser dueDate) {
        this.dueDate = dueDate;
    }

    public InventoryRepo getInventoryRepo() {
        return inventoryRepo;
    }

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public JTable getParent() {
        return parent;
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
        if (listDetail == null) {
            return 0;
        }
        return listDetail.size();
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
        return switch (column) {
            case  8 ->
                false;
            default ->
                true;
        };
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            if (!listDetail.isEmpty()) {
                PurOrderHisDetail record = listDetail.get(row);
                switch (column) {
                    case 0 -> {
                        //code
                        return record.getUserCode() == null ? record.getStockCode() : record.getUserCode();                        
                    }
                    case 1 -> {
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
                        return Util1.toNull(record.getQty());
                    }
                    case 6 -> {
                        return Util1.toNull(record.getBag());
                    }
                    case 7 -> {
                        return Util1.toNull(record.getPrice());
                    }
                    case 8 -> {
                        return Util1.toNull(record.getAmount());
                    }
                    default -> {
                        return new Object();
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
            if (value != null) {  
                PurOrderHisDetail pd = listDetail.get(row);
                switch (column) {
                    case 0, 1 -> {
                        //Code
                            if (value instanceof Stock s) {
                                pd.setStockCode(s.getKey().getStockCode());
                                pd.setStockName(s.getStockName());
                                pd.setUserCode(s.getUserCode());
                                pd.setWeight(Util1.getDouble(s.getWeight()));
                                pd.setQty(1.0);                               
                            } 
                             addNewRow();
                        parent.setColumnSelectionInterval(2, 2);
                    }
                    case 2 -> {
                        double wet = Util1.getDouble(value);
                        if (wet > 0) {
                            pd.setWet(wet);
                        }
                    }
                    case 3 -> {
                        double rice = Util1.getDouble(value);
                        if (rice > 0) {
                            pd.setRice(rice);
                        }
                    }
                    case 4 -> {
                        double wt = Util1.getDouble(value);
                        if (wt > 0) {
                            pd.setWeight(wt);
                        }
                    }
                    case 5 -> {
                        double qty = Util1.getDouble(value);
                        if (qty > 0) {
                            pd.setQty(qty);
                        }
                    }
                    case 6 -> {
                        double bag = Util1.getDouble(value);
                        if (bag > 0) {
                            pd.setBag(bag);
                        }
                    }
                    case 7 -> {
                        double price = Util1.getDouble(value);
                        if (price > 0) {
                            pd.setPrice(price);
                        }
                    }
                }
                calculateAmount(pd);               
                fireTableRowsUpdated(row, row);
                setRecord(listDetail.size() - 1);
                observer.selected("CAL-TOTAL", "CAL-TOTAL");
                parent.requestFocus();
            }
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }

//    private void assignLocation(PurOrderHisDetail sd) {
//        if (sd.getLocCode() == null) {
//            LocationAutoCompleter completer = stockIssRec.getLocationAutoCompleter();
//            if (completer != null) {
//                Location l = completer.getLocation();
//                if (l != null) {
//                    sd.setLocCode(l.getKey().getLocCode());
//                    sd.setLocName(l.getLocName());
//                }
//            }
//        }
//    }

    private void setRecord(int size) {
        if (lblRec != null) {
            lblRec.setText("Records : " + size);
        }
    }

    public void addNewRow() {
        if (listDetail != null) {
            if (!hasEmptyRow()) {
                PurOrderHisDetail pd = new PurOrderHisDetail();
                listDetail.add(pd);
                fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
            }
        }
    }

    private boolean hasEmptyRow() {
        boolean status = false;
        if (listDetail.size() >= 1) {
            PurOrderHisDetail get = listDetail.get(listDetail.size() - 1);
            if (get.getStockCode() == null) {
                status = true;
            }
        }
        return status;
    }

    public List<PurOrderHisDetail> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<PurOrderHisDetail> listDetail) {
        this.listDetail = listDetail;
        setRecord(listDetail.size());
//        addNewRow();
        fireTableDataChanged();
    }

    private void calculateAmount(PurOrderHisDetail pur) {
        double price = Util1.getDouble(pur.getPrice());
        double qty = Util1.getDouble(pur.getQty());
        if (pur.getStockCode() != null) {
            double amount = qty * price;
            int roundAmt = (int) amount;
            pur.setPrice(price);
            if (ProUtil.isPurRDDis()) {
                int netAmt = Util1.roundDownToNearest100(roundAmt);
                double discount = roundAmt - netAmt;
                observer.selected("DISCOUNT", discount);
            }
            pur.setAmount(roundAmt);
        }
    }

    private void showMessageBox(String text) {
        JOptionPane.showMessageDialog(Global.parentForm, text);
    }

    public boolean isValidEntry() {
        for (PurOrderHisDetail sdh : listDetail) {
            if (sdh.getStockCode() != null) {
//                sdh.setUnitCode("-");
                if (Util1.getDouble(sdh.getAmount()) <= 0) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Amount.",
                            "Invalid.", JOptionPane.ERROR_MESSAGE);
                    parent.requestFocus();
                    return false;
                }
            }
        }
        return true;
    }

    public List<PurOrderHisDetailKey> getDelList() {
        return deleteList;
    }

    public void clearDelList() {
        if (deleteList != null) {
            deleteList.clear();
        }
    }

    public void delete(int row) {
        PurOrderHisDetail sdh = listDetail.get(row);
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

    public void addStockIssRec(PurOrderHisDetail sd) {
        if (listDetail != null) {
            listDetail.add(sd);
            fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
        }
    }

    public PurOrderHisDetail getObject(int row) {
        return listDetail.get(row);
    }

    public void clear() {
        if (listDetail != null) {
            listDetail.clear();
            fireTableDataChanged();
        }
    }

    public PurOrderHisDetail getSelectVou(int row) {
        return listDetail.get(row);
    }
    public void updateRow(int row) {
        fireTableRowsUpdated(row, row);
    }
}
