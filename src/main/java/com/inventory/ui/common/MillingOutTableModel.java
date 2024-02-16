/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.ui.common;

import com.common.Global;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.common.Util1;
import com.inventory.model.MillingOutDetail;
import com.inventory.model.MillingOutDetailKey;
import com.inventory.model.Stock;
import com.inventory.model.StockUnit;
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

    private String[] columnNames = {"Code", "Stock Name", "Weight", "WT-Unit", "Qty", "Unit",
        "Price", "Amount", "Total Wt", "Eff Wt", "Eff Qty", "Calculate"};
    @Setter
    private JTable parent;
    private List<MillingOutDetail> listDetail = new ArrayList();
    @Setter
    private SelectionObserver observer;
    private final List<MillingOutDetailKey> deleteList = new ArrayList();
    private InventoryRepo inventoryRepo;
    private JLabel lblStockName;
    private JButton btnProgress;
    private JDateChooser vouDate;

    public InventoryRepo getInventoryRepo() {
        return inventoryRepo;
    }

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public JDateChooser getVouDate() {
        return vouDate;
    }

    public void setVouDate(JDateChooser vouDate) {
        this.vouDate = vouDate;
    }

    public JLabel getLblStockName() {
        return lblStockName;
    }

    public void setLblStockName(JLabel lblStockName) {
        this.lblStockName = lblStockName;
    }

    public JButton getBtnProgress() {
        return btnProgress;
    }

    public void setBtnProgress(JButton btnProgress) {
        this.btnProgress = btnProgress;
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
            case 0, 1, 3, 5 ->
                String.class;
            case 11 ->
                Boolean.class;
            default ->
                Double.class;
        };
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        //"Code", "Stock Name", "Weight", "Weight Unit", "Qty", "Unit",
        //"Price", "Amount", "Total Weight", "Amt %", "Qty %", "Weight %"
        switch (column) {
            case 7, 8, 9, 10 -> {
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
                    return sd.getUserCode() == null ? sd.getStockCode() : sd.getUserCode();
                }
                case 1 -> {
                    return sd.getStockName();
                }
                case 2 -> {
                    return Util1.toNull(sd.getWeight());
                }
                case 3 -> {
                    return sd.getWeightUnit();
                }
                case 4 -> {
                    //qty
                    return Util1.toNull(sd.getQty());
                }
                case 5 -> {
                    return sd.getUnitCode();
                }
                case 6 -> {
                    //price
                    return Util1.toNull(sd.getPrice());
                }
                case 7 -> {
                    //amt
                    return Util1.toNull(sd.getAmount());
                }
                case 8 -> {
                    return Util1.toNull(sd.getTotalWeight());
                }
                case 9 -> {
                    //amount %
                    return Util1.toNull(sd.getPercent());
                }
                case 10 -> {
                    //qty %
                    return Util1.toNull(sd.getPercentQty());
                }
                case 11 -> {
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
                            sd.setWeightUnit(s.getWeightUnit());
                            sd.setUnitCode(s.getSaleUnitCode());
                            sd.setStock(s);
                            setSelection(row, 3);
                            addNewRow();
                        }
                    }
                    case 2 -> {
                        sd.setWeight(Util1.getDouble(value));
                    }
                    case 3 -> {
                        if (value instanceof StockUnit u) {
                            sd.setWeightUnit(u.getKey().getUnitCode());
                        }
                    }
                    case 4 -> {
                        //Qty
                        if (Util1.isNumber(value)) {
                            if (Util1.isPositive(Util1.getDouble(value))) {
                                sd.setQty(Util1.getDouble(value));
                                sd.setQtyStr(String.valueOf(value));
                                if (sd.getUnitCode() == null) {
                                    setSelection(row, 5);
                                } else {
                                    setSelection(row, 6);
                                }
                            } else {
                                showMessageBox("Input value must be positive");
                                parent.setColumnSelectionInterval(column, column);
                            }
                        } else {
                            showMessageBox("Input value must be number.");
                            parent.setColumnSelectionInterval(column, column);
                        }
                    }
                    case 5 -> {
                        //Unit
                        if (value instanceof StockUnit stockUnit) {
                            sd.setUnitCode(stockUnit.getKey().getUnitCode());
                        }
                    }
                    case 6 -> {
                        //price
                        if (Util1.isNumber(value)) {
                            if (Util1.isPositive(Util1.getDouble(value))) {
                                sd.setPrice(Util1.getDouble(value));
                                setSelection(row + 1, 0);
                            } else {
                                showMessageBox("Input value must be positive");
                                setSelection(row, column);
                            }
                        } else {
                            showMessageBox("Input value must be number.");
                            setSelection(row, column);
                        }
                    }
                    case 7 -> {
                        //amt
                        sd.setAmount(Util1.getDouble(value));
                    }
                    case 11 -> {
                        if (value instanceof Boolean t) {
                            sd.setCalculate(t);
                        }
                    }
                }
                calculateAmount(sd);
                calWeight(sd);
                fireTableRowsUpdated(row, row);
                observer.selected("SALE-TOTAL-OUT", "SALE-TOTAL-OUT");
                parent.requestFocus();
            }
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }

    private void swapRow(int row1, int row2) {
        if (row2 > row1) {
            MillingOutDetail m1 = listDetail.get(row1);
            MillingOutDetail m2 = listDetail.get(row2);
            if (!Util1.isNullOrEmpty(m2.getStockCode())) {
                listDetail.set(row2, m1);
                listDetail.set(row1, m2);
                fireTableDataChanged();
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
            sd.setTotalWeight(Util1.getDouble(sd.getQty()) * Util1.getDouble(sd.getWeight()));
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
        addNewRow();
        fireTableDataChanged();
    }

    public void removeListDetail() {
        this.listDetail.clear();
        addNewRow();
    }

    private void calculateAmount(MillingOutDetail s) {
        double price = Util1.getDouble(s.getPrice());
        double wt = Util1.getDouble(s.getWeight());
        double qty = Util1.getDouble(s.getQty());
        double ttlWt = Util1.getDouble(s.getTotalWeight());
        if (s.getStockCode() != null) {
            double amount = (ttlWt * price) / wt;
            s.setAmount(amount);
        }
    }

    private void showMessageBox(String text) {
        JOptionPane.showMessageDialog(Global.parentForm, text);
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
        if (!listDetail.isEmpty()) {
            listDetail.set(row, mod);
            fireTableRowsUpdated(row, row);
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
