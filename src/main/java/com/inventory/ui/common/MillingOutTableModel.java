/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.ui.common;

import com.common.Global;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.entity.MillingOutDetail;
import com.inventory.entity.MillingOutDetailKey;
import com.inventory.entity.Stock;
import com.repo.InventoryRepo;
import com.toedter.calendar.JDateChooser;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Athu Sint
 */
@Slf4j
public class MillingOutTableModel extends AbstractTableModel {

    private String[] columnNames = {"Code", "Stock Name", "Weight", "Qty", "Price", "Amount", "Total Wt", "Eff Wt", "Eff Qty", "Calculate"};
    @Setter
    private JTable parent;
    private List<MillingOutDetail> listDetail = new ArrayList();
    @Setter
    private SelectionObserver observer;
    private final List<MillingOutDetailKey> deleteList = new ArrayList();

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
            case 0, 1 ->
                String.class;
            case 9 ->
                Boolean.class;
            default ->
                Double.class;
        };
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        //"Code", "Stock Name", "Weight", "Qty", "Price", "Amount", "Total Wt", "Eff Wt", "Eff Qty", "Calculate"
        switch (column) {
            case 6, 7, 8 -> {
                return false;
            }
        }
        return true;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {

            MillingOutDetail sd = listDetail.get(row);
            switch (column) {
                case 0 -> {
                    //code
                    return sd.getUserCode();
                }
                case 1 -> {
                    return sd.getStockName();
                }
                case 2 -> {
                    return Util1.toNull(sd.getWeight());
                }
                case 3 -> {
                    //qty
                    return Util1.toNull(sd.getQty());
                }
                case 4 -> {
                    //price
                    return Util1.toNull(sd.getPrice());
                }
                case 5 -> {
                    //amt
                    return Util1.toNull(sd.getAmount());
                }
                case 6 -> {
                    return Util1.toNull(sd.getTotalWeight());
                }
                case 7 -> {
                    //amount %
                    return Util1.toNull(sd.getPercent());
                }
                case 8 -> {
                    //qty %
                    return Util1.toNull(sd.getPercentQty());
                }
                case 9 -> {
                    return sd.isCalculate();
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
            MillingOutDetail sd = listDetail.get(row);
            if (value != null) {
                switch (column) {
                    case 0, 1 -> {
                        //Code
                        if (value instanceof Stock s) {
                            sd.setStockCode(s.getKey().getStockCode());
                            sd.setStockName(s.getStockName());
                            sd.setUserCode(s.getUserCode());
                            sd.setRelName(s.getRelName());
                            sd.setQty(1);
                            sd.setWeight(s.getWeight());
                            sd.setWeightUnit(Util1.isNull(s.getWeightUnit(), "-"));
                            sd.setUnitCode("-");
                            sd.setStock(s);
                            setSelection(row, 2);
                            calWeight(sd);
                            checkRow(sd, row);
                            addNewRow();
                        }
                    }
                    case 2 -> {
                        double weight = Util1.getDouble(value);
                        if (weight > 0) {
                            sd.setWeight(weight);
                            calWeight(sd);
                            calculateAmount(sd);
                        }
                    }
                    case 3 -> {
                        //Qty
                        double qty = Util1.getDouble(value);
                        if (qty > 0) {
                            sd.setQty(Util1.getDouble(value));
                            sd.setQtyStr(String.valueOf(value));
                            calWeight(sd);
                            calculateAmount(sd);
                            setSelection(row, column + 1);
                        }
                    }
                    case 4 -> {
                        //price
                        double price = Util1.getDouble(value);
                        if (price > 0) {
                            sd.setPrice(price);
                            sd.setRecentPrice(price);
                            calculateAmount(sd);
                            setSelection(row + 1, 0);
                        }
                    }
                    case 5 -> {
                        //amt
                        double amt = Util1.getDouble(value);
                        if (amt > 0) {
                            sd.setAmount(amt);
                            calPrice(sd);
                        }
                    }
                    case 9 -> {
                        if (value instanceof Boolean t) {
                            sd.setCalculate(t);
                            if (!t) {
                                sd.setPrice(sd.getRecentPrice());
                                sd.setAmount(sd.getPrice() * sd.getQty());
                            }
                        }
                    }
                }
                fireTableRowsUpdated(row, row);
                observer.selected("SALE-TOTAL-OUT", "SALE-TOTAL-OUT");
                parent.requestFocus();
            }
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }

    private void checkRow(MillingOutDetail m, int row) {
        if (row == 0) {
            if (!m.isCalculate()) {
                m.setCalculate(true);
            }
        }
    }

    private void calWeight(MillingOutDetail sd) {
        if (ProUtil.isUseWeightPoint()) {
            String str = sd.getQtyStr();
            double wt = Util1.getDouble(sd.getWeight());
            sd.setQty(Util1.getDouble(str));
            sd.setTotalWeight(Util1.getTotalWeight(wt, str));
        } else {
            sd.setTotalWeight(sd.getQty() * sd.getWeight());
        }
    }

    private void setSelection(int row, int column) {
        parent.setRowSelectionInterval(row, row);
        parent.setColumnSelectionInterval(column, column);
    }

    public void addNewRow() {
        if (listDetail != null) {
            if (!hasEmptyRow()) {
                MillingOutDetail pd = new MillingOutDetail();
                listDetail.add(pd);
                fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
            }
        }
    }

    private boolean hasEmptyRow() {
        boolean status = false;
        if (listDetail.size() >= 1) {
            MillingOutDetail get = listDetail.get(listDetail.size() - 1);
            if (get.getStockCode() == null) {
                status = true;
            }
        }
        return status;
    }

    public List<MillingOutDetail> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<MillingOutDetail> listDetail) {
        this.listDetail = listDetail;
        fireTableDataChanged();
    }

    public void removeListDetail() {
        this.listDetail.clear();
    }

    private void calculateAmount(MillingOutDetail s) {
        double price = s.getPrice();
        double wt = s.getWeight();
        double ttlWt = s.getTotalWeight();
        if (s.getStockCode() != null) {
            double amount = (ttlWt * price) / wt;
            s.setAmount(amount);
        }
    }

    private void calPrice(MillingOutDetail s) {
        double wt = s.getWeight();
        double ttlWt = s.getTotalWeight();
        double amount = s.getAmount();
        if (s.getStockCode() != null) {
            double price = amount * wt / ttlWt;
            s.setPrice(price);
        }
    }

    public boolean isValidEntry() {
        boolean status = true;
        for (MillingOutDetail sdh : listDetail) {
            if (sdh.getStockCode() != null) {
                if (sdh.getUnitCode() == null) {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid  Unit.");
                    status = false;
                    parent.requestFocus();
                    break;
                }
            }
        }
        return status;
    }

    public List<MillingOutDetailKey> getDelList() {
        return deleteList;
    }

    public void clearDelList() {
        if (deleteList != null) {
            deleteList.clear();
        }
    }

    public void remove(int row) {
        listDetail.remove(row);
        fireTableRowsDeleted(row, row);
    }

    public void delete(int row) {
        MillingOutDetail sdh = listDetail.get(row);
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

    public MillingOutDetail getObject(int row) {
        return listDetail.get(row);
    }

    public void addObject(MillingOutDetail sd) {
        if (listDetail != null) {
            listDetail.add(sd);
            fireTableRowsInserted(listDetail.size() - 1, listDetail.size() - 1);
        }
    }

    public void setObject(int row, MillingOutDetail mod) {
        try {
            if (!listDetail.isEmpty()) {
                listDetail.set(row, mod);
                fireTableRowsUpdated(row, row);
            }
        } catch (Exception e) {
            log.error("setObject : " + e.getMessage());
        }
    }

    public void rowUpdate(int row) {
        fireTableRowsUpdated(row, row);
    }

    public void clear() {
        if (listDetail != null) {
            listDetail.clear();
            fireTableDataChanged();
        }
    }

}
