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
import com.inventory.editor.LocationAutoCompleter;
import com.inventory.entity.Location;
import com.inventory.entity.PurDetailKey;
import com.inventory.entity.PurHisDetail;
import com.inventory.entity.Stock;
import com.inventory.ui.entry.PurchaseDynamic;
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
public class PurchaseRiceBagTableModel extends AbstractTableModel {

    private String[] columnNames = {"Code", "Stock Name", "Location", "Moisture", "Hard Rice", "Std-Weight", "Avg-Weight", "Bag", "Price", "Amount"};
    private List<PurHisDetail> listDetail = new ArrayList();
    private final List<PurDetailKey> deleteList = new ArrayList();
    @Setter
    private JTable parent;
    @Setter
    private SelectionObserver observer;
    @Setter
    private JLabel lblRec;
    @Setter
    private PurchaseDynamic purchase;

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
            case 0, 1, 2 ->
                String.class;
            default ->
                Double.class;
        };
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return switch (column) {
            case 9 ->
                false;
            default ->
                true;
        };
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            if (!listDetail.isEmpty()) {
                PurHisDetail record = listDetail.get(row);
                switch (column) {
                    case 0 -> {
                        //code
                        return record.getUserCode();
                    }
                    case 1 -> {
                        return record.getStockName();
                    }
                    case 2 -> {
                        return record.getLocName();
                    }
                    case 3 -> {
                        return Util1.toNull(record.getWet());
                    }
                    case 4 -> {
                        return Util1.toNull(record.getRice());
                    }
                    case 5 -> {
                        return Util1.toNull(record.getWeight());
                    }
                    case 6 -> {
                        return Util1.toNull(record.getStdWeight());
                    }
                    case 7 -> {
                        return Util1.toNull(record.getBag());
                    }
                    case 8 -> {
                        return Util1.toNull(record.getPrice());
                    }
                    case 9 -> {
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
                PurHisDetail pd = listDetail.get(row);
                switch (column) {
                    case 0, 1 -> {
                        if (value instanceof Stock s) {
                            pd.setStockCode(s.getKey().getStockCode());
                            pd.setStockName(s.getStockName());
                            pd.setUserCode(s.getUserCode());
                            pd.setWeight(s.getWeight());
                            pd.setCalculate(s.isCalculate());
                            pd.setBag(1);
                            pd.setUnitCode("-");
                            double price = pd.getPrice();
                            if (price == 0) {
                                pd.setPrice(s.getPurPrice());
                                pd.setOrgPrice(s.getPurPrice());
                            }
                            addNewRow();
                            parent.setColumnSelectionInterval(2, 2);
                        }
                    }
                    case 2 -> {
                        //Loc
                        if (value instanceof Location l) {
                            pd.setLocCode(l.getKey().getLocCode());
                            pd.setLocName(l.getLocName());
                        }
                    }
                    case 3 -> {
                        double wet = Util1.getDouble(value);
                        if (wet > 0) {
                            pd.setWet(wet);
                        }
                    }
                    case 4 -> {
                        double rice = Util1.getDouble(value);
                        if (rice > 0) {
                            pd.setRice(rice);
                        }
                    }
                    case 5 -> {
                        double wt = Util1.getDouble(value);
                        if (wt > 0) {
                            pd.setWeight(wt);
                        }
                    }
                    case 6 -> {
                        double wt = Util1.getDouble(value);
                        if (wt > 0) {
                            pd.setStdWeight(wt);
                        }
                    }
                    case 7 -> {
                        double bag = Util1.getDouble(value);
                        if (bag > 0) {
                            pd.setBag(bag);
                        }
                    }
                    case 8 -> {
                        double price = Util1.getDouble(value);
                        if (price > 0) {
                            pd.setPrice(price);
                            pd.setOrgPrice(price);
                        }
                    }
                    case 9 -> {
                        //Amount
                        double amount = Util1.getDouble(value);
                        if (amount > 0) {
                            pd.setAmount(Util1.getDouble(value));
                        }
                    }

                }
                assignLocation(pd);
                calculateAmount(pd);
                setRecord(listDetail.size() - 1);
                fireTableRowsUpdated(row, row);
                observer.selected("CAL-TOTAL", "CAL-TOTAL");
                parent.requestFocus();
            }
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }

    private void assignLocation(PurHisDetail sd) {
        if (sd.getLocCode() == null) {
            LocationAutoCompleter completer = purchase.getLocationAutoCompleter();
            if (completer != null) {
                Location l = completer.getLocation();
                if (l != null) {
                    sd.setLocCode(l.getKey().getLocCode());
                    sd.setLocName(l.getLocName());
                }
            }
        }
    }

    private void setRecord(int size) {
        if (lblRec != null) {
            lblRec.setText("Records : " + size);
        }
    }

    public void addNewRow() {
        if (listDetail != null) {
            if (!hasEmptyRow()) {
                PurHisDetail pd = new PurHisDetail();
                listDetail.add(pd);
                fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
            }
        }
    }

    private boolean hasEmptyRow() {
        boolean status = false;
        if (listDetail.size() >= 1) {
            PurHisDetail get = listDetail.get(listDetail.size() - 1);
            if (get.getStockCode() == null) {
                status = true;
            }
        }
        return status;
    }

    public List<PurHisDetail> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<PurHisDetail> listDetail) {
        this.listDetail = listDetail;
        fireTableDataChanged();
    }

    private void calculateAmount(PurHisDetail s) {
        double orgPrice = Util1.getDouble(s.getOrgPrice());
        double bag = Util1.getDouble(s.getBag());
        double weight = s.getWeight();
        double stdWt = s.getStdWeight();
        if (s.isCalculate()) {
            if (s.getStockCode() != null && weight > 0 && orgPrice > 0) {
                stdWt = stdWt == 0 ? weight : stdWt;
                double price = stdWt / weight * orgPrice;
                double amount = bag * price;
                int roundAmt = (int) amount;
                s.setPrice(price);
                if (ProUtil.isPurRDDis()) {
                    int netAmt = Util1.roundDownToNearest100(roundAmt);
                    double discount = roundAmt - netAmt;
                    observer.selected("DISCOUNT", discount);
                }
                s.setAmount(amount);
                s.setTotalWeight(bag * weight);
            }
        } else {
            double amount = bag * s.getPrice();
            s.setAmount(amount);
        }

    }

    public boolean isValidEntry() {
        for (PurHisDetail sdh : listDetail) {
            if (sdh.getStockCode() != null) {
                sdh.setUnitCode("-");
                if (Util1.getDouble(sdh.getAmount()) <= 0) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Amount.",
                            "Invalid.", JOptionPane.ERROR_MESSAGE);
                    parent.requestFocus();
                    return false;
                } else if (sdh.getLocCode() == null) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Location.");
                    parent.requestFocus();
                    return false;
                }
            }
        }
        return true;
    }

    public List<PurDetailKey> getDelList() {
        return deleteList;
    }

    public void clearDelList() {
        if (deleteList != null) {
            deleteList.clear();
        }
    }

    public void delete(int row) {
        PurHisDetail sdh = listDetail.get(row);
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

    public void addPurchase(PurHisDetail sd) {
        if (listDetail != null) {
            listDetail.add(sd);
            fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
        }
    }

    public PurHisDetail getObject(int row) {
        return listDetail.get(row);
    }

    public void clear() {
        if (listDetail != null) {
            listDetail.clear();
            fireTableDataChanged();
        }
    }

    public void updateRow(int row) {
        fireTableRowsUpdated(row, row);
    }
}
